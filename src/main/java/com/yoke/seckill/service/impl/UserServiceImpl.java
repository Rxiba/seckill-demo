package com.yoke.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yoke.seckill.exception.GlobalException;
import com.yoke.seckill.mapper.UserMapper;
import com.yoke.seckill.pojo.User;
import com.yoke.seckill.service.IUserService;
import com.yoke.seckill.utils.CookieUtil;
import com.yoke.seckill.utils.MD5Util;
import com.yoke.seckill.utils.UUIDUtil;
import com.yoke.seckill.vo.LoginVo;
import com.yoke.seckill.vo.RespBean;
import com.yoke.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Yoke
 * @since 2022-07-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //参数校验
        /*if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if (!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }*/
        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (null == user) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
        if (!MD5Util.fromPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成cookie
        String ticket = UUIDUtil.uuid();
        // request.getSession().setAttribute(ticket,user);
        //将用户信息存入redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        User user = ((User) redisTemplate.opsForValue().get("user:" + userTicket));
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }
}
