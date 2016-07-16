package com.mytools.utils;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import com.mytools.constants.MyToolsConstant;

/**
 * spring上下文对象，用来获取bean
 * @author huangping <br />
 *         2012-12-1
 */
public class SpringContextUtil implements ApplicationContextAware {


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		MyToolsConstant.setApplicationContext(applicationContext);
	}

	/**
	 * 动态注册bean到spring容器中
	 * @param beanName
	 * @param className
	 * @param property
	 */
	public static void loadBean(String beanName, Class<?> className, Map<String, Object> property) throws Exception {
		if (containsBean(beanName)) {
			throw new Exception("【"+ beanName +"】，spring bean名称重复");
		}
		ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) MyToolsConstant.applicationContext;
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(className);
		if (property != null && !property.isEmpty()) {
			for (Entry<String, Object> entry : property.entrySet()) {
				builder.addPropertyValue(entry.getKey(), entry.getValue());
			}
		}
		registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
	}
	
	/**
	 * 根据bean的名称，获取bean对象
	 * @param beanName bean名称
	 * @return 返回获取到的对象
	 */
	public static Object getBean(String beanName) {
		return MyToolsConstant.applicationContext.getBean(beanName);
	}

	/**
	 * 根据bean的名称和class类型，获取bean对象
	 * @param beanName bean名称
	 * @param clazz 对象的class
	 * @return 返回获取到的对象
	 */
	public static <T> T getBean(String beanName, Class<T> clazz) {
		return MyToolsConstant.applicationContext.getBean(beanName, clazz);
	}

	/**
	 * 根据class类型，获取bean对象
	 * @param clazz 对象的class
	 * @return 返回获取到的对象
	 */
	public static <T> T getBean(Class<T> clazz) {
		return MyToolsConstant.applicationContext.getBean(clazz);
	}

	/**
	 * 查询spring工厂中是否包含该名称的bean对象
	 * @param beanName bean名称
	 * @return spring工厂中是否包含该名称的bean对象
	 */
	public static boolean containsBean(String beanName) {
		return MyToolsConstant.applicationContext.containsBean(beanName);
	}

	/**
	 * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。<br />
	 * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
	 * @param beanName bean名称
	 * @return 返回该对象是否是singleton
	 * @throws NoSuchBeanDefinitionException
	 */
	public static boolean isSingleton(String beanName) throws NoSuchBeanDefinitionException {
		return MyToolsConstant.applicationContext.isSingleton(beanName);
	}

	/**
	 * 如果给定的bean名字在bean定义中有别名，则返回这些别名
	 * @param beanName bean名称
	 * @return 返回该对象的别名
	 * @throws NoSuchBeanDefinitionException
	 */
	public static String[] getAliases(String beanName) throws NoSuchBeanDefinitionException {
		return MyToolsConstant.applicationContext.getAliases(beanName);
	}
	
}
