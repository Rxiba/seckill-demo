package com.yoke.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yoke.seckill.pojo.User;
import com.yoke.seckill.vo.LoginVo;
import com.yoke.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Yoke
 * @since 2022-07-02
 */
public interface IUserService extends IService<User> {

    /**
     * 功能描述: 登录
     *
     * @param:
     * @return:
     */
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 功能描述: 根据cookie获取用户
     *
     * @param:
     * @return:
     */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
}
