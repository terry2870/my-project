package com.mytools.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
/**
 * 注释一个hessian接口
 * @author ping.huang
 * @data 2014-09-15
 */
public @interface HessianService {

	/**
	 * 接口调用的url
	 * @return
	 */
	public String serviceUrl();
	
	/**
	 * 接口的class
	 * @return
	 */
	public Class<?> serviceInterface() default Object.class;
}
