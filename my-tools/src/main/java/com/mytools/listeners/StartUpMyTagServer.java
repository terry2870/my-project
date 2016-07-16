/*
 * 作者：黄平
 * 
 */
package com.mytools.listeners;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

import com.mytools.constants.MyToolsConstant;
import com.mytools.utils.JdomUtil;
import com.mytools.utils.MyToolsUtil;

@SuppressWarnings("unchecked")
public class StartUpMyTagServer implements ServletContextListener {

	private Logger log = Logger.getLogger(this.getClass());

	public void contextDestroyed(ServletContextEvent event) {
		
	}

	public void contextInitialized(ServletContextEvent event) {
		try {
			this.log.info("开始读取配置文件！");
			MyToolsConstant.setContext(event.getServletContext());
			String myTagsParameterPath = MyToolsConstant.context.getInitParameter("myTagsParameter");// 配置文件的路径
//			String jdbcXmlPath = MyTagsConstant.context.getInitParameter("jdbcXmlPath");// 数据库配置文件的路径
			String needInitXml = MyToolsConstant.context.getInitParameter("needInitXml");// 所有需要初始化的xml路径
			MyToolsConstant.realPath = MyToolsConstant.context.getRealPath("");// 工程路径

//			if (!StringUtil.isEmpty(jdbcXmlPath)) {
//				this.initJdbc(MyTagsConstant.realPath + jdbcXmlPath);
//			}
			// 读取所有初始化参数
			if (!StringUtils.isEmpty(myTagsParameterPath)) {
				this.initParameter(MyToolsConstant.realPath + myTagsParameterPath);
			}

			// 读取所有xml
			if (!StringUtils.isEmpty(needInitXml)) {
				this.initXML(needInitXml);
			}
			this.log.info("读取配置文件完成！");
		} catch (Exception e) {
			this.log.error("", e);
		}
	}

	/**
	 * 读取所有的配置参数
	 * 
	 * @param path
	 */
	private void initParameter(String path) throws Exception {
		Element root = JdomUtil.getRootElement(new File(path));
		if (root == null) {
			return;
		}
		List<Element> propertyList = root.getChildren("property");
		if (propertyList == null || propertyList.size() == 0) {
			return;
		}
		for (Element propertyElement : propertyList) {
			MyToolsConstant.paramsMap.put(propertyElement.getAttributeValue("key"), propertyElement.getAttributeValue("value"));
		}
		if (!StringUtils.isEmpty(MyToolsConstant.paramsMap.get("defaultPageSize"))) {
			MyToolsConstant.DEFAULT_PAGE_SIZE = MyToolsConstant.paramsMap.get("defaultPageSize");
		}
		if (!StringUtils.isEmpty(MyToolsConstant.paramsMap.get("defaultCurrentPage"))) {
			MyToolsConstant.DEFAULT_CURRENT_PAGE = MyToolsConstant.paramsMap.get("defaultCurrentPage");
		}
		if (!StringUtils.isEmpty(MyToolsConstant.paramsMap.get("pageEncoding"))) {
			MyToolsConstant.PAGE_ENCODING = MyToolsConstant.paramsMap.get("pageEncoding");
		}
		if (!StringUtils.isEmpty(MyToolsConstant.paramsMap.get("databaseEncoding"))) {
			MyToolsConstant.DATABASE_ENCODING = MyToolsConstant.paramsMap.get("databaseEncoding");
		}
		if (!StringUtils.isEmpty(MyToolsConstant.paramsMap.get("contentType"))) {
			MyToolsConstant.CONTENT_TYPE = MyToolsConstant.paramsMap.get("contentType");
		}
	}

	/**
	 * 读取jdbc的参数
	 * 
	 * @param path
	 */
//	private void initJdbc(String path) throws Exception {
//		Element root = JdomUtil.getRootElement(new File(path));
//		if (root != null) {
//			DbConfig.url = JdomUtil.getValueFromElement(root, "url", "");
//			DbConfig.userName = JdomUtil.getValueFromElement(root, "userName", "");
//			DbConfig.password = JdomUtil.getValueFromElement(root, "password", "");
//			DbConfig.driverClassName = JdomUtil.getValueFromElement(root, "driverClassName", "");
//			DbConfig.maxActive = Integer.parseInt(JdomUtil.getValueFromElement(root, "maxActive", "0"));
//			DbConfig.incrementNum = Integer.parseInt(JdomUtil.getValueFromElement(root, "incrementNum", "0"));
//			DbConfig.initConnNum = Integer.parseInt(JdomUtil.getValueFromElement(root, "initConnNum", "0"));
//			DbConfig.checkConnSQL = JdomUtil.getValueFromElement(root, "checkConnSQL", "");
//			DbConfig.maxWait = Integer.parseInt(JdomUtil.getValueFromElement(root, "maxWait", "0"));
//			DbConfig.databaseType = JdomUtil.getValueFromElement(root, "databaseType", "oracle");
//			DbConfig.debugModel = Boolean.parseBoolean(JdomUtil.getValueFromElement(root, "debugModel", "false"));
//			DbConfig.showSql = Boolean.parseBoolean(JdomUtil.getValueFromElement(root, "showSql", "false"));
//			DbConfig.transformColumn = Boolean.parseBoolean(JdomUtil.getValueFromElement(root, "transformColumn", "true"));
//			DbConfig.maxIdle = Integer.parseInt(JdomUtil.getValueFromElement(root, "maxIdle", "0"));
//			DbConfig.checkBeginTime = JdomUtil.getValueFromElement(root, "checkBeginTime", "");
//			DbConfig.checkPeriod = Long.parseLong(JdomUtil.getValueFromElement(root, "checkPeriod", "0"));
//			DbConfig.jndiName = JdomUtil.getValueFromElement(root, "jndiName", "");
//		}
//	}

	/**
	 * 初始化所有的sql和bean
	 * 
	 * @param path
	 */
	private void initXML(String path) throws Exception {
		String[] pathArr = path.split(",");
		if (pathArr == null || pathArr.length == 0) {
			return;
		}
		Element root = null;
		String rootName = "";
		String pathName = "", regName = "";
		File file = null;
		File[] files = null;
		String replaceFileName = "";
		for (String str : pathArr) {
			str = str.trim();
			pathName = str.substring(0, str.lastIndexOf("/") + 1);
			regName = str.substring(str.lastIndexOf("/") + 1);
			file = new File(MyToolsConstant.realPath + pathName);
			if (file.isDirectory()) {
				files = file.listFiles();
				for (File f : files) {
					replaceFileName = f.getPath().replace("\\", "/");
					if (MyToolsConstant.sqlPathList.contains(replaceFileName) || MyToolsConstant.beanPathList.contains(replaceFileName)) {
						continue;
					}
					if (f.getName().matches(regName)) {
						root = JdomUtil.getRootElement(f);
						if (root == null) {
							continue;
						}
						rootName = root.getName();
						if ("sqls".equals(rootName)) {
							MyToolsUtil.getSqlByPath(root);
							log.info("成功加载了文件：【" + replaceFileName + "】");
							MyToolsConstant.sqlPathList.add(replaceFileName);
						} else if ("beans".equals(rootName)) {
							MyToolsUtil.getBeanByPath(root);
							log.info("成功加载了文件：【" + replaceFileName + "】");
							MyToolsConstant.beanPathList.add(replaceFileName);
						}
					}
				}
			}
		}
	}

}
