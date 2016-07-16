package com.mytools.exception;

import org.springframework.beans.BeansException;

/**
 * bean的url为空异常
 * @author ping.huang
 * @date 2014-09-15
 */
public class BeanNameEmptyException extends BeansException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BeanNameEmptyException(String msg) {
		super(msg);
	}


}
