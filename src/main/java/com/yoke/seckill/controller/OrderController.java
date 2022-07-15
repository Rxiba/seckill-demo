package com.yoke.seckill.controller;


import com.yoke.seckill.pojo.User;
import com.yoke.seckill.service.IOrderService;
import com.yoke.seckill.vo.OrderDetailVo;
import com.yoke.seckill.vo.RespBean;
import com.yoke.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Yoke
 * @since 2022-07-04
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 功能描述: 订单详情(静态化传参)
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }


}
