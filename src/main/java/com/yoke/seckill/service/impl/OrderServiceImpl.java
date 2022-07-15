package com.yoke.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yoke.seckill.exception.GlobalException;
import com.yoke.seckill.mapper.OrderMapper;
import com.yoke.seckill.pojo.Order;
import com.yoke.seckill.pojo.SeckillGoods;
import com.yoke.seckill.pojo.SeckillOrder;
import com.yoke.seckill.pojo.User;
import com.yoke.seckill.service.IOrderService;
import com.yoke.seckill.service.ISeckillGoodsService;
import com.yoke.seckill.service.ISeckillOrderService;
import com.yoke.seckill.utils.MD5Util;
import com.yoke.seckill.utils.UUIDUtil;
import com.yoke.seckill.vo.GoodsVo;
import com.yoke.seckill.vo.OrderDetailVo;
import com.yoke.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Yoke
 * @since 2022-07-04
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private GoodsServiceImpl goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().
                eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //更新库存前需要判断库存是否大于零
        if(seckillGoods.getStockCount() < 1){
            redisTemplate.opsForValue().set("isStockEmpty:"+goods.getId(),"0");
            return null;
        }
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = stock_count - 1")
                .eq("goods_id", goods.getId())
                .gt("stock_count",0));
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrderService.save(seckillOrder);
        //将秒杀订单信息存入redis中，判断用户重复抢购就从redis中获取判断，减少对数据库的访问
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getId(), seckillOrder);
        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setGoodsVo(goodsVo);
        detail.setOrder(order);
        return detail;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String path = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,path,60, TimeUnit.SECONDS);
        return path;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if(goodsId < 0 || StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath =(String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(goodsId < 0 || StringUtils.isEmpty(captcha)){
            return false;
        }
        String redisCaptcha =(String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
