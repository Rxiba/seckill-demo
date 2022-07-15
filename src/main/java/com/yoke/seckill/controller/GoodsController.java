package com.yoke.seckill.controller;

import com.yoke.seckill.pojo.User;
import com.yoke.seckill.service.IGoodsService;
import com.yoke.seckill.service.IUserService;
import com.yoke.seckill.vo.DetailVo;
import com.yoke.seckill.vo.GoodsVo;
import com.yoke.seckill.vo.RespBean;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 商品
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private ThymeleafViewResolver thymeleafViewResolver;

	/**
	 * 功能描述: 跳转商品列表页
	 * windows优化前QPS：1332
	 * 缓存QPS：2342
	 */
	@RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String toList(Model model,User user,
	                     HttpServletRequest request, HttpServletResponse response) {
		//Redis中获取页面，如果不为空，直接返回页面
		ValueOperations valueOperations = redisTemplate.opsForValue();
		String html = (String) valueOperations.get("goodsList");
		if (!StringUtils.isEmpty(html)) {
			return html;
		}
		model.addAttribute("user", user);
		model.addAttribute("goodsList", goodsService.findGoodsVo());
		// return "goodsList";
		//如果为空，手动渲染，存入Redis并返回
		WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(),
				model.asMap());
		html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
		if (!StringUtils.isEmpty(html)) {
			valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
		}
		return html;
	}

	/**
	 * 功能描述: 跳转商品详情页
	 */
	@RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String toDetail(Model model,User user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response) {
		ValueOperations valueOperations = redisTemplate.opsForValue();
		//Redis中获取页面，如果不为空，直接返回页面
		String html = (String) valueOperations.get("goodsDetail:" + goodsId);
		if (!StringUtils.isEmpty(html)) {
			return html;
		}
		model.addAttribute("user", user);
		GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
		Date startDate = goodsVo.getStartDate();
		Date endDate = goodsVo.getEndDate();
		Date nowDate = new Date();
		//秒杀状态
		int secKillStatus = 0;
		//秒杀倒计时
		int remainSeconds = 0;
		//秒杀还未开始
		if (nowDate.before(startDate)) {
			remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime()) / 1000));
		} else if (nowDate.after(endDate)) {
			//	秒杀已结束
			secKillStatus = 2;
			remainSeconds = -1;
		} else {
			//秒杀中
			secKillStatus = 1;
			remainSeconds = 0;
		}
		model.addAttribute("remainSeconds", remainSeconds);
		model.addAttribute("secKillStatus", secKillStatus);
		model.addAttribute("goods", goodsVo);
		//return "goodsDetail";
		WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(),
				model.asMap());
		html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);
		if (!StringUtils.isEmpty(html)) {
			valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
		}
		return html;
	}

	/**
	 * 功能描述: 跳转商品详情页（静态化传参）
	 */
	@RequestMapping("/detail/{goodsId}")
	@ResponseBody
	public RespBean detail(User user, @PathVariable Long goodsId) {
		GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
		Date startDate = goodsVo.getStartDate();
		Date endDate = goodsVo.getEndDate();
		Date nowDate = new Date();
		//秒杀状态
		int secKillStatus = 0;
		//秒杀倒计时
		int remainSeconds = 0;
		//秒杀还未开始
		if (nowDate.before(startDate)) {
			remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime()) / 1000));
		} else if (nowDate.after(endDate)) {
			//	秒杀已结束
			secKillStatus = 2;
			remainSeconds = -1;
		} else {
			//秒杀中
			secKillStatus = 1;
			remainSeconds = 0;
		}
		DetailVo detailVo = new DetailVo();
		detailVo.setUser(user);
		detailVo.setGoodsVo(goodsVo);
		detailVo.setSecKillStatus(secKillStatus);
		detailVo.setRemainSeconds(remainSeconds);
		return RespBean.success(detailVo);
	}
}