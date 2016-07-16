/* author hp
 * 创建日期 May 31, 2011
 */
package com.mytools.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.mytools.beans.SqlParamsBean;
import com.mytools.database.DatabaseAbst;
import com.mytools.enums.DatabaseTypeEnum;

public class MyToolsConstant {
	
	public static ServletContext context = null;
	public static ApplicationContext applicationContext;
	public static String realPath = "";// 工程路径

	public static String PAGE_ENCODING = "UTF-8";// 页面字符集编码
	public static String DATABASE_ENCODING = "UTF-8";// 数据库字符集编码
	public static String CONTENT_TYPE = "text/html; charset=UTF-8";// 页面编码

	public static String DEFAULT_PAGE_SIZE = "20";// 默认每页多少行
	public static String DEFAULT_CURRENT_PAGE = "1";// 默认开始是第几页

	public static final String DEFAULT_ATTRIBUTE_NAME = "defaultAttributeName";
	public static List<String> sqlPathList = new ArrayList<String>();// 所有sql语句的文件路径
	public static Map<String, SqlParamsBean> sqlMap = new Hashtable<String, SqlParamsBean>();// 所有的sql路径

	// public static List biPathList = new ArrayList();// 所有需要初始化的BI的文件路径
	// public static Map biMap = new HashMap();// 所有的需要初始化的BI

	public static List<String> beanPathList = new ArrayList<String>();// 所有需要初始化的类的文件路径
	public static Map<String, Object> beanMap = new Hashtable<String, Object>();// 所有的需要初始化的类

	public static Map<String, String> paramsMap = new HashMap<String, String>();// 配置文件里的值
	
	private static Map<String, DatabaseAbst> databaseAbstMap = null;
	/**
	 * 获取分页的数据库操作对象
	 * @param databaseType
	 * @return
	 */
	public synchronized static DatabaseAbst getDatabaseAbst(String databaseType) {
		if (StringUtils.isEmpty(databaseType)) {
			return null;
		}
		DatabaseTypeEnum[] values = DatabaseTypeEnum.values();
		if (databaseAbstMap == null) {
			databaseAbstMap = new HashMap<String, DatabaseAbst>();
			for (DatabaseTypeEnum d : values) {
				databaseAbstMap.put(d.toString(), DatabaseTypeEnum.getDatabaseAbst(d.toString()));
			}
		}
		return databaseAbstMap.get(databaseType.toUpperCase());
	}
	public static void setContext(ServletContext context) {
		MyToolsConstant.context = context;
	}
	public static void setApplicationContext(ApplicationContext applicationContext) {
		MyToolsConstant.applicationContext = applicationContext;
	}

	/**
	 * 初始化所有的BI
	 * 
	 * @param file
	 */
	// private static void getBIByPath(File file) {
	// SAXBuilder sax = new SAXBuilder();
	// Document doc = null;
	// try {
	// doc = sax.build(file);
	// Element root = doc.getRootElement();
	// List biList = root.getChildren("bi");
	// if (biList != null && biList.size() > 0) {
	// Element bi, params = null;
	// String id = "";
	// List paramList = null;
	// Map biMaps = null;
	// for (int i = 0; i < biList.size(); i++) {
	// biMaps = new HashMap();
	// bi = (Element) biList.get(i);
	// params = bi.getChild("params");
	// id = JdomUtil.getValueFromElement(bi, "id");
	// biMaps.put("bean", JdomUtil.getValueFromElement(bi, "bean"));
	// biMaps.put("class", JdomUtil.getValueFromElement(bi, "class"));
	// biMaps.put("method", JdomUtil.getValueFromElement(bi, "method"));
	// // biMaps.put("params", bi.getChild("params"));
	//
	// if (params != null) {
	// paramList = params.getChildren("param");
	// if (paramList != null && paramList.size() > 0) {
	// List paramList1 = new ArrayList();
	// for (int j = 0; j < paramList.size(); j++) {
	// Map paramMap = new HashMap();
	// Element param = (Element) paramList.get(j);
	// paramMap.put("type", JdomUtil.getValueFromElement(param, "type"));
	// paramMap.put("value", JdomUtil.getValueFromElement(param, "value"));
	// paramMap.put("valueFromRequest", JdomUtil.getValueFromElement(param,
	// "valueFromRequest"));
	// BIUtil.getPropertyByElement(param, paramMap);
	// paramList1.add(paramMap);
	// }
	// biMaps.put("param", paramList1);
	// }
	// }
	//
	// if (biMap.containsKey(id)) {
	// throw new Exception("初始化所有的BI出错！[" + id + "]重复");
	// }
	// biMap.put(id, biMaps);
	// }
	// }
	// } catch (Exception e) {
	// log.error("初始化所有的BI出错！", e);
	// }
	// }
}
