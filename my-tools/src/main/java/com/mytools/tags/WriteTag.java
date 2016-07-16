/*
 * 作者：黄平
 * 
 */
package com.mytools.tags;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mytools.constants.MyToolsConstant;
import com.mytools.utils.MyToolsUtil;
import com.mytools.utils.SpringContextUtil;
import com.mytools.utils.StringUtil;

/**
 * 页面输出
 * 
 * @author Administrator
 * 
 */
public class WriteTag extends BaseTagSupport {

	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(this.getClass());

	private String converter;
	private boolean transferred;
	private int add;

	public int doStartTag() throws JspException {
		super.doStartTag();
		Object tmp = null;
		String result = null;
		String objName = this.name;
		if (StringUtils.isEmpty(objName)) {
			objName = MyToolsConstant.DEFAULT_ATTRIBUTE_NAME;
		}
		try {
			if ("contextPath".equalsIgnoreCase(objName)) {
				result = this.request.getContextPath();
			} else {
				if (!StringUtils.isEmpty(this.property)) {
					objName = objName + "." + this.property;
				}
				tmp = MyToolsUtil.getValueFromObject(this.request, objName);
				if (tmp == null || StringUtils.isEmpty(String.valueOf(tmp))) {
					result = "";
				} else {
					result = tmp.toString();
				}
				if (!StringUtils.isEmpty(this.converter)) {
					String[] arr = this.converter.split("[.]");
					if (arr.length != 2) {
						log.error("converter转换的格式不正确！");
					} else {
						result = (String) MyToolsUtil.executeJavaMethod(SpringContextUtil.getBean(arr[0]), arr[1], new Class[] { String.class }, new String[] { result });
					}
				}
				if (add != 0 && ((String) result).matches("\\d+")) {
					int temp = Integer.parseInt(result);
					temp = temp + add;
					result = String.valueOf(temp);
				}
				if (StringUtils.isEmpty(result) && !StringUtils.isEmpty(this.defaultValue)) {
					result = this.defaultValue;
				}
				if (transferred) {
					result = StringUtil.transferredForJsp((String) result);
				}
			}
			this.out.print(result);
		} catch (Exception e) {
			this.log.error("页面输出出错！", e);
		}
		return EVAL_BODY_INCLUDE;
	}

	public void setConverter(String converter) {
		this.converter = converter;
	}

	public void setAdd(int add) {
		this.add = add;
	}

	public void setTransferred(boolean transferred) {
		this.transferred = transferred;
	}

}
