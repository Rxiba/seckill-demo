package com.yoke.seckill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * MVC配置类
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private UserArgumentResolver userArgumentResolver;
	@Autowired
	private AccessLimitInterceptor accessLimitInterceptor;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(userArgumentResolver);
	}

	/*配置类优先于配置文件加载，所以要解决静态资源加载问题*/
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}

	/*添加拦截器*/
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessLimitInterceptor);
	}
}