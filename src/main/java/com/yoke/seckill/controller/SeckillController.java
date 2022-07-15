package com.yoke.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.rabbitmq.tools.json.JSONUtil;
import com.sun.deploy.net.HttpResponse;
import com.wf.captcha.ArithmeticCaptcha;
import com.yoke.seckill.config.AccessLimit;
import com.yoke.seckill.exception.GlobalException;
import com.yoke.seckill.pojo.Order;
import com.yoke.seckill.pojo.SeckillMessage;
import com.yoke.seckill.pojo.SeckillOrder;
import com.yoke.seckill.pojo.User;
import com.yoke.seckill.rabbitmq.MQSender;
import com.yoke.seckill.service.IGoodsService;
import com.yoke.seckill.service.IOrderService;
import com.yoke.seckill.service.ISeckillOrderService;
import com.yoke.seckill.utils.JsonUtil;
import com.yoke.seckill.vo.GoodsVo;
import com.yoke.seckill.vo.RespBean;
import com.yoke.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author A_xiba
 * @Date 2022/7/5 14:01
 * @Version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    @RequestMapping("/doSeckill2")
    private String doSeckill2(User user, Model model, Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存是否充足
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        //判断是否重复抢购
        SeckillOrder orderServiceOne = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
                .eq("goods_id", goodsId));
        if (orderServiceOne != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }


    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    private RespBean doSeckill(User user, Long goodsId, @PathVariable String path) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //检查秒杀地址是否正确
        boolean cheakPath = orderService.checkPath(user,goodsId,path);
        if(!cheakPath){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //使用内存标记商品库存是否为空，减少对Redis的无用访问
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存操作
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            EmptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //RabbitMQ异步下单秒杀
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
        /*
        //判断库存是否充足
        if(goods.getStockCount() < 1){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
//        SeckillOrder orderServiceOne = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
//                .eq("goods_id", goodsId));
        SeckillOrder seckillOrder =
                (SeckillOrder)redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order =  orderService.seckill(user, goods);
        return RespBean.success(order);*/
    }

    /**
     * 系统初始化，把商品库存加载到Redis中
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/getResult", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha){
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //检查验证码是否正确
        boolean cheakCaptcha = orderService.checkCaptcha(user,goodsId,captcha);
        if(!cheakCaptcha){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    /**
     * 生成验证码
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    private void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if (user == null) {
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }
        //设置为输出图片类型
        response.setContentType("image/gif");
        //设置不缓存
        response.setHeader("Pargam","No-cache");
        response.setHeader("Cache-Control","No-cache");
        //设置失效时间
        response.setDateHeader("Expires",0);
        //生成验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        //存入redis
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300,
                TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败！"+e.getMessage());
        }
    }
}
