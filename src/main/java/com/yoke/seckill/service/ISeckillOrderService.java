package com.yoke.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yoke.seckill.pojo.SeckillOrder;
import com.yoke.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Yoke
 * @since 2022-07-04
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
