package com.yoke.seckill.rabbitmq;

import com.yoke.seckill.pojo.SeckillMessage;
import com.yoke.seckill.pojo.SeckillOrder;
import com.yoke.seckill.pojo.User;
import com.yoke.seckill.service.impl.GoodsServiceImpl;
import com.yoke.seckill.service.impl.OrderServiceImpl;
import com.yoke.seckill.utils.JsonUtil;
import com.yoke.seckill.vo.GoodsVo;
import com.yoke.seckill.vo.RespBean;
import com.yoke.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息接收者
 */

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private GoodsServiceImpl goodsService;
    @Autowired
    private OrderServiceImpl orderService;


    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接受消息：" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        User user = seckillMessage.getUser();
        Long goodId = seckillMessage.getGoodId();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodId);
        //库存是否大于0
        if(goodsVo.getStockCount() < 1){
            return;
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder =
                (SeckillOrder)redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodId);
        if(seckillOrder != null){
            return;
        }
        //下单操作
        orderService.seckill(user,goodsVo);
    }
}
