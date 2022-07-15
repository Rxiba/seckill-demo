package com.yoke.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yoke.seckill.pojo.Order;
import com.yoke.seckill.pojo.User;
import com.yoke.seckill.vo.GoodsVo;
import com.yoke.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Yoke
 * @since 2022-07-04
 */
public interface IOrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
