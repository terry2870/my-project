package com.mytools.configs;

import com.mytools.utils.SpringContextUtil;

/**
 * 配置文件对象的工厂
 * @author ping.huang
 * @date 2014-12-05
 *
 */
public class ConfigFactory {

	private static MyToolsConfig myToolsConfig = null;
	
	static {
		myToolsConfig = SpringContextUtil.getBean(MyToolsConfig.class);
	}

	public static MyToolsConfig getMyToolsConfig() {
		return myToolsConfig;
	}

}
