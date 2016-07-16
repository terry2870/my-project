/* author hp
 * 创建日期 May 30, 2011
 */
package com.mytools.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

import com.mytools.beans.SqlParamsBean;
import com.mytools.beans.SqlParamsBean.ParamBean;
import com.mytools.configs.ConfigFactory;
import com.mytools.configs.MyToolsConfig;
import com.mytools.constants.MyToolsConstant;

@SuppressWarnings("unchecked")
public class MyToolsUtil {

	static Logger log = Logger.getLogger(MyToolsUtil.class);
	
	/**
	 * 从对象中取属性
	 * 
	 * @param obj
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Object getValueFromObject(Object obj, String name) throws Exception {
		return getValueFromObject(obj, name, null);
	}

	/**
	 * 从对象中取属性
	 * 
	 * @param obj
	 * @param name
	 * @param defaultValue
	 * @return
	 * @throws Exception
	 */
	public static Object getValueFromObject(Object obj, String name, Object defaultValue) throws Exception {
		if (obj == null || StringUtils.isEmpty(name)) {
			return defaultValue;
		}
		Object result = null;
		String[] arr = name.split("[.]");
		result = getProperty(obj, arr[0], defaultValue);
		for (int i = 1; i < arr.length; i++) {
			if (result == null) {
				return defaultValue;
			}
			if (result instanceof String) {
				return result.toString();
			} else {
				result = getProperty(result, arr[i], defaultValue);
			}
		}
		return result;
	}

	/**
	 * 从request中取值，先getParameter再getAttribute最后取request.getSession().
	 * getAttribute
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	public static Object getValueFromRequest(ServletRequest request, String param) {
		return getValueFromRequest(request, param, null);
	}

	/**
	 * 从request中取值，先getParameter再getAttribute最后取request.getSession().
	 * getAttribute
	 * 
	 * @param request
	 * @param param
	 * @param defaultValue
	 * @return
	 */
	public static Object getValueFromRequest(ServletRequest request, String param, Object defaultValue) {
		if (request == null || param == null) {
			return defaultValue;
		}
		Object obj = request.getAttribute(param);
		if (obj == null) {
			obj = request.getParameter(param);
		}
		if (obj == null) {
			HttpServletRequest req = (HttpServletRequest) request;
			obj = req.getSession().getAttribute(param);
		}
		if (obj == null) {
			obj = defaultValue;
		}
		return obj;
	}

	/**
	 * 从对象中取值，相当于beanUtils.getProperty
	 * 
	 * @param obj
	 * @param name
	 * @param defaultValue
	 * @return
	 * @throws Exception
	 */
	public static Object getProperty(Object obj, String name, Object defaultValue) throws Exception {
		Object result = null;
		if (obj == null) {
			result = null;
		} else if (obj instanceof Map) {
			result = ((Map<String, Object>) obj).get(name);
		} else if (obj instanceof ServletRequest) {
			result = getValueFromRequest((ServletRequest) obj, name, defaultValue);
		} else {
			String method = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
			result = executeJavaMethod(obj, method, null, null);
		}
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	/**
	 * 从对象中取值，相当于beanUtils.getProperty
	 * 
	 * @param obj
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Object getProperty(Object obj, String name) throws Exception {
		return getProperty(obj, name, null);
	}

	/**
	 * 执行方法
	 * 
	 * @param classObj
	 * @param method
	 * @param classArr
	 * @param paramArr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object executeJavaMethod(Object classObj, String method, Class[] classArr, Object[] paramArr) throws Exception {
		if (classObj == null || StringUtils.isEmpty(method)) {
			return null;
		}
		Object result = null;
		if (classObj instanceof Class) {
			Class clazz = (Class) classObj;
			result = clazz.getMethod(method, classArr).invoke(classObj, paramArr);
		} else {
			result = classObj.getClass().getMethod(method, classArr).invoke(classObj, paramArr);
		}
		return result;
	}

	/**
	 * 重新加载sql
	 * @param debugModel
	 */
	private static void reloadSQL(boolean debugModel) {
		try {
			if (debugModel) {
				synchronized (MyToolsConstant.sqlMap) {
					MyToolsConstant.sqlMap.clear();
					if (MyToolsConstant.sqlPathList != null && MyToolsConstant.sqlPathList.size() > 0) {
						for (String str : MyToolsConstant.sqlPathList) {
							MyToolsUtil.getSqlByPath(JdomUtil.getRootElement(new File(str)));
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	/**
	 * 取sql语句
	 * @param xml
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static SqlParamsBean getSqlByXml(String xml, ServletRequest request) throws Exception {
		MyToolsConfig config = ConfigFactory.getMyToolsConfig();
		return getSqlByXml(xml, request, config.isDebugModel());
	}
	
	/**
	 * 取sql语句
	 * @param xml
	 * @param request
	 * @param debugModel
	 * @return
	 * @throws Exception
	 */
	public static SqlParamsBean getSqlByXml(String xml, ServletRequest request, boolean debugModel) throws Exception {
		reloadSQL(debugModel);
		SqlParamsBean sql = MyToolsConstant.sqlMap.get(xml);
		if (sql == null) {
			throw new Exception("找不到 【" + xml + "】 所指定的sql");
		}
		if (sql.getParamList() != null && sql.getParamList().size() > 0) {
			for (ParamBean param : sql.getParamList()) {
				param.setValue(getValueFromRequest(request, param.getName(), param.getDefaultValue()));
			}
		}
		return sql;
	}

	/**
	 * 初始化所有的sql
	 * 
	 * @param root
	 * @throws Exception
	 */
	public static void getSqlByPath(Element root) throws Exception {
		if (root == null) {
			return;
		}
		List<Element> sqlsList = root.getChildren("sql");
		if (sqlsList == null || sqlsList.size() == 0) {
			return;
		}
		SqlParamsBean sqlBean = null;
		List<ParamBean> paramsList = null;
		ParamBean param = null;
		List<Element> paramList = null;
		Element paramsElement = null;
		Element paramElement = null;
		String id = "";
		for (Element sqlElement : sqlsList) {
			id = sqlElement.getAttributeValue("id");
			if (MyToolsConstant.sqlMap.containsKey(id)) {
				throw new Exception("初始化所有的sql出错！[" + id + "]重复");
			}
			sqlBean = new SqlParamsBean();
			sqlBean.setSqlValue(sqlElement.getChildText("value"));
			paramsElement = sqlElement.getChild("params");
			if (paramsElement != null) {
				paramList = paramsElement.getChildren("param");
				if (paramList != null && paramList.size() > 0) {
					paramsList = new ArrayList<SqlParamsBean.ParamBean>();
					for (int i = 0; i < paramList.size(); i++) {
						paramElement = paramList.get(i);
						param = new ParamBean();
						param.setName(paramElement.getAttributeValue("name"));
						param.setStringType(paramElement.getAttributeValue("type"));
						param.setDefaultValue(paramElement.getAttributeValue("defaultValue"));
						if (StringUtils.isEmpty(param.getName())) {
							param.setName(paramElement.getText());
						}
						paramsList.add(param);
					}
					sqlBean.setParamList(paramsList);
				}
			}
			MyToolsConstant.sqlMap.put(id, sqlBean);
		}
	}

	/**
	 * 初始化所有的bean
	 * 
	 * @param root
	 * @throws Exception
	 */
	public static void getBeanByPath(Element root) throws Exception {
		if (root == null) {
			return;
		}
		List<Element> beanList = root.getChildren("bean");
		if (beanList == null || beanList.size() == 0) {
			return;
		}
		String id = "", clazz = "", init = "";
		for (Element beanElement : beanList) {
			id = beanElement.getAttributeValue("id");
			clazz = beanElement.getAttributeValue("class");
			init = beanElement.getAttributeValue("init");
			if (MyToolsConstant.beanMap.containsKey(id)) {
				throw new Exception("初始化所有的bean出错！[" + id + "]重复");
			}
			if ("true".equals(init)) {
				MyToolsConstant.beanMap.put(id, Class.forName(clazz).newInstance());
			} else {
				MyToolsConstant.beanMap.put(id, Class.forName(clazz));
			}
		}
	}

}
