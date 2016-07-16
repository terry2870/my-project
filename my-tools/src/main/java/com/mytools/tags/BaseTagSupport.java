/*
 * 作者：黄平
 * 
 */
package com.mytools.tags;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mytools.beans.PageBean;
import com.mytools.beans.SqlParamsBean;
import com.mytools.configs.ConfigFactory;
import com.mytools.configs.MyToolsConfig;
import com.mytools.constants.MyToolsConstant;
import com.mytools.database.DatabaseAbst;
import com.mytools.utils.AutoPaging;
import com.mytools.utils.MyToolsUtil;
import com.mytools.utils.SpringContextUtil;

public class BaseTagSupport extends TagSupport {

	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(this.getClass());

	protected String name;
	protected String label;
	protected String value;
	protected String size;
	protected String event;
	protected String className;
	protected String style;
	protected String beanName;
	protected String align;

	protected String checked;
	protected String selected;
	protected String disabled;
	protected String readonly;

	protected String labelClassName;
	protected String labelStyle;
	protected String labelEvent;
	protected String textClassName;
	protected String textStyle;
	protected String textEvent;
	protected String property;
	protected String src;
	protected String height;
	protected String width;

	protected String defaultValue;

	protected String xml;
	private String sql;
	private String dataListName;
	protected String pageSize;
	protected String formName;

	protected String checkBoxClassName;
	protected String checkBoxStyle;
	protected String checkBoxEvent;

	protected String radioClassName;
	protected String radioStyle;
	protected String radioEvent;

	protected String labelField;
	protected String valueField;
	protected String checkedField;
	protected String rowSize;
	protected String splitChar;

	protected String rootNodePid;

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected ServletConfig servletConfig;
	protected ServletContext servletContext;
	protected HttpSession session;
	protected JspWriter out;
	
	private String jdbcTemplateName;
	private String databaseType;
	private String paging;
	
	private String pageInfoObject;
	private String pageInfoHtml;

	public int doStartTag() throws JspException {
		this.request = (HttpServletRequest) this.pageContext.getRequest();
		this.response = (HttpServletResponse) this.pageContext.getResponse();
		this.servletConfig = this.pageContext.getServletConfig();
		this.servletContext = this.pageContext.getServletContext();
		this.session = this.pageContext.getSession();
		this.out = this.pageContext.getOut();
		return super.doStartTag();
	}

	/**
	 * 根据提供的条件，取数据(先判断xml，再判断sql，最后在判断dataListName)
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> getDataList() {
		List<Object> list = null;
		Object o = null;
		JdbcTemplate jdbcTemplate = null;
		MyToolsConfig config = ConfigFactory.getMyToolsConfig();
		try {
			if (!StringUtils.isEmpty(jdbcTemplateName)) {
				jdbcTemplate = SpringContextUtil.getBean(jdbcTemplateName, JdbcTemplate.class);
			} else {
				jdbcTemplate = config.getJdbcTemplate();
			}
			PageBean pb = new PageBean();
			if (StringUtils.isEmpty(databaseType)) {
				databaseType = config.getDatabaseType();
			}
			DatabaseAbst abst = MyToolsConstant.getDatabaseAbst(databaseType);
			if (!StringUtils.isEmpty(this.xml)) {
				SqlParamsBean sqlBean = MyToolsUtil.getSqlByXml(this.xml, this.request, config.isDebugModel());
				pb.setSql(sqlBean.getSqlValue());
				pb.setParams(sqlBean.getParamObjectValue());
				paging(pb, jdbcTemplate, abst);
				o = jdbcTemplate.queryForList(pb.getSql(), sqlBean.getParamObjectValue());
			} else if (!StringUtils.isEmpty(this.sql)) {
				pb.setSql(this.sql);
				paging(pb, jdbcTemplate, abst);
				o = jdbcTemplate.queryForList(pb.getSql());
			} else if (!StringUtils.isEmpty(this.dataListName)) {
				o = this.request.getAttribute(this.dataListName);
			}
			if (o != null) {
				if (o instanceof List) {
					list = (List<Object>) o;
				} else {
					list = new ArrayList<Object>();
					list.add(o);
				}
			}
		} catch (Exception e) {
			this.log.error("查询数据出错！", e);
		}
		return list;
	}
	
	private void paging(PageBean pb, JdbcTemplate jdbcTemplate, DatabaseAbst abst) {
		if ("true".equals(this.paging)) {
			String pageSize = (String) MyToolsUtil.getValueFromRequest(this.request, AutoPaging.PAGE_SIZE);
			String currentPage = (String) MyToolsUtil.getValueFromRequest(this.request, AutoPaging.CURRENT_PAGE);
			String orderBy = (String) MyToolsUtil.getValueFromRequest(this.request, AutoPaging.ORDER_BY, "");
			String sort = (String) MyToolsUtil.getValueFromRequest(this.request, AutoPaging.SORT, "");
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = StringUtils.isEmpty(this.pageSize) ? MyToolsConstant.DEFAULT_PAGE_SIZE : this.pageSize;
			}
			if (StringUtils.isEmpty(currentPage)) {
				currentPage = String.valueOf(MyToolsConstant.DEFAULT_CURRENT_PAGE);
			}
			pb.setPageSize(Integer.parseInt(pageSize));
			pb.setCurrentPage(Integer.parseInt(currentPage));
			pb.setOrderBy(orderBy);
			pb.setSort(sort);
			try {
				pb.setTotalCount(jdbcTemplate.queryForObject(abst.getTotalCountSql(pb.getSql()), pb.getParams(), Integer.class).intValue());
				pb.setMaxPage(abst.getMaxPage(pb.getTotalCount(), pb.getPageSize()));
				pb.setSql(abst.getPageSQL(pb));
			} catch (Exception e) {
				this.log.error("", e);
			}
			if (!StringUtils.isEmpty(this.pageInfoObject)) {
				this.request.setAttribute(this.pageInfoObject, pb);
			}
			if (!StringUtils.isEmpty(this.pageInfoHtml)) {
				this.request.setAttribute(this.pageInfoHtml, AutoPaging.getPageButton(pb, request, sort));
			}
		}
	}

	/**
	 * 根据提供的条件，查询被选中的控件(先判断checkedXml，再判断checkedSql，最后在判断checkedDataListName)
	 * 
	 * @return
	 */
	// protected List getCheckedDataList() {
	// List list = null;
	// Database db = new Database();
	// try {
	// if (!StringUtil.isEmpty(this.checkedXml)) {
	// Map map = MyTagsUtil.getSqlByXml(this.checkedXml, request);
	// String sql = map.get("sql").toString();
	// Object[] obj = (Object[]) map.get("params");
	// list = db.getToStringList(sql, obj);
	// } else if (!StringUtil.isEmpty(this.checkedSql)) {
	// list = db.getToStringList(this.checkedSql);
	// } else {
	// Object o = null;
	// if (this.checkedDataListName != null) {
	// o = this.request.getAttribute(this.checkedDataListName);
	// }
	// if (o instanceof List) {
	// list = (List) o;
	// }
	// }
	// } catch (Exception e) {
	// this.log.error("查询被选中的树出错！", e);
	// }
	// return list;
	// }
	protected String getValuesFromParam() throws Exception {
		String result = null;
		if (this.value != null) {
			if (this.value.indexOf("{") == 0 && this.value.indexOf("}") > 0) {
				Object tmp = MyToolsUtil.getValueFromObject(this.request, this.value.substring(this.value.indexOf("{") + 1, this.value.indexOf("}")));
				if (tmp != null && !StringUtils.isEmpty(String.valueOf(tmp))) {
					result = tmp.toString();
				}
			} else {
				result = this.value;
			}
		}
		if (result == null && this.defaultValue != null) {
			result = this.defaultValue;
		}
		return result;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public void setLabelClassName(String labelClassName) {
		this.labelClassName = labelClassName;
	}

	public void setLabelStyle(String labelStyle) {
		this.labelStyle = labelStyle;
	}

	public void setLabelEvent(String labelEvent) {
		this.labelEvent = labelEvent;
	}

	public void setTextStyle(String textStyle) {
		this.textStyle = textStyle;
	}

	public void setTextEvent(String textEvent) {
		this.textEvent = textEvent;
	}

	public void setTextClassName(String textClassName) {
		this.textClassName = textClassName;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setCheckBoxClassName(String checkBoxClassName) {
		this.checkBoxClassName = checkBoxClassName;
	}

	public void setCheckBoxStyle(String checkBoxStyle) {
		this.checkBoxStyle = checkBoxStyle;
	}

	public void setCheckBoxEvent(String checkBoxEvent) {
		this.checkBoxEvent = checkBoxEvent;
	}

	public void setLabelField(String labelField) {
		this.labelField = labelField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public void setRowSize(String rowSize) {
		this.rowSize = rowSize;
	}

	public void setRadioClassName(String radioClassName) {
		this.radioClassName = radioClassName;
	}

	public void setRadioStyle(String radioStyle) {
		this.radioStyle = radioStyle;
	}

	public void setRadioEvent(String radioEvent) {
		this.radioEvent = radioEvent;
	}

	public void setRootNodePid(String rootNodePid) {
		this.rootNodePid = rootNodePid;
	}

	public void setSplitChar(String splitChar) {
		this.splitChar = splitChar;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setCheckedField(String checkedField) {
		this.checkedField = checkedField;
	}

	public void setJdbcTemplateName(String jdbcTemplateName) {
		this.jdbcTemplateName = jdbcTemplateName;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public void setPaging(String paging) {
		this.paging = paging;
	}

	public void setPageInfoObject(String pageInfoObject) {
		this.pageInfoObject = pageInfoObject;
	}

	public void setPageInfoHtml(String pageInfoHtml) {
		this.pageInfoHtml = pageInfoHtml;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setDataListName(String dataListName) {
		this.dataListName = dataListName;
	}

}
