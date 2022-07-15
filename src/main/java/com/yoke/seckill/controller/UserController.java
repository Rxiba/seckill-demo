package com.yoke.seckill.controller;


import com.yoke.seckill.pojo.User;
import com.yoke.seckill.rabbitmq.MQSender;
import com.yoke.seckill.vo.RespBean;
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
 * @since 2022-07-02
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    private RespBean info(User user){
        return RespBean.success(user);
    }


}
