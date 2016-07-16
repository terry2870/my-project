package com.mytools.annotation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

import com.mytools.exception.BeanNameEmptyException;

/**
 * 对自定义的注解进行封装，解析
 * @author ping.huang
 * @date 2014-09-15
 */
public class MyAnnotation extends BeanNameUrlHandlerMapping implements BeanPostProcessor {
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = AopUtils.getTargetClass(bean);
		if (clazz.isAnnotationPresent(HessianService.class)) {
			HessianService hessian = (HessianService) bean.getClass().getAnnotation(HessianService.class);
			if (StringUtils.isEmpty(hessian.serviceUrl())) {
				throw new BeanNameEmptyException("serviceUrl is empty in annotation ["+ bean.getClass().getName() +"]");
			}
			HessianServiceExporter exporter = new HessianServiceExporter();
			exporter.setService(bean);
			Class<?> c = hessian.serviceInterface();
			if (Object.class.getName().equals(c.getName())) {
				Class<?>[] arr = bean.getClass().getInterfaces();
				c = arr[0];
			}
			exporter.setServiceInterface(c);
			exporter.afterPropertiesSet();
			registerHandler(hessian.serviceUrl(), exporter);
		}
		return bean;
	}


}
