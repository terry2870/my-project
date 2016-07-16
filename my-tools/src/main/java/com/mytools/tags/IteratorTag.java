/*
 * 作者：黄平
 * 
 */
package com.mytools.tags;

import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mytools.constants.MyToolsConstant;

/**
 * 循环显示list
 * 
 * @author Administrator
 * 
 */
public class IteratorTag extends BaseTagSupport {

	Logger log = Logger.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	private int count = 0;
	private List<Object> list = null;
	private String bean;
	private String rowIndex;
	private int startIndex;

	public int doStartTag() throws JspException {
		super.doStartTag();
		try {
			this.list = getDataList();
		} catch (Exception e) {
			this.log.error("查询数据出错：", e);
		}
		this.bean = this.beanName;
		if (StringUtils.isEmpty(this.bean)) {
			this.bean = MyToolsConstant.DEFAULT_ATTRIBUTE_NAME;
		}
		this.count = this.startIndex;
		if (check()) {
			this.request.setAttribute(this.bean, this.list.get(this.count));
			this.count++;
			if (!StringUtils.isEmpty(this.rowIndex)) {
				request.setAttribute(this.rowIndex, count);
			}
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}

	public int doAfterBody() throws JspException {
		if (check()) {
			this.request.setAttribute(this.bean, this.list.get(this.count));
			this.count++;
			if (!StringUtils.isEmpty(this.rowIndex)) {
				request.setAttribute(this.rowIndex, count);
			}
			return EVAL_BODY_AGAIN;
		} else {
			return SKIP_BODY;
		}
	}
	
	private boolean check(){
		return this.list != null && this.list.size() > 0 && this.count < this.list.size() && (StringUtils.isEmpty(this.pageSize) || this.count < Integer.parseInt(this.pageSize) + this.startIndex);
	}

	public void setRowIndex(String rowIndex) {
		this.rowIndex = rowIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

}
