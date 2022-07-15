package com.yoke.seckill.controller;

import com.yoke.seckill.service.IUserService;
import com.yoke.seckill.vo.LoginVo;
import com.yoke.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Author A_xiba
 * @Date 2022/7/2 13:58
 * @Version 1.0
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    private IUserService userService;

    /**
     * 功能描述: 跳转登录页面
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 功能描述: 登录功能
     * @param:
     * @return:
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        //log.info("{}",loginVo);
        return userService.doLogin(loginVo, request, response);
    }
}
