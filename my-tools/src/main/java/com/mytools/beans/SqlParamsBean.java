/* author hp
 * 创建日期 Jun 15, 2011
 */
package com.mytools.beans;

import java.util.List;

public class SqlParamsBean {

	private String sqlValue;
	private List<ParamBean> paramList;

	public static class ParamBean {
		private String name;
		private int columnType;
		private String defaultValue;
		private Object value;
		private String stringType;

		public ParamBean() {

		}
		public ParamBean(Object value) {
			this.value = value;
		}
		public ParamBean(Object value, int columnType) {
			this.value = value;
			this.columnType = columnType;
		}
		public ParamBean(Object value, int columnType, String defaultValue) {
			this.columnType = columnType;
			if (value == null && defaultValue != null) {
				value = defaultValue;
			}
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDefaultValue() {
			return defaultValue;
		}
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		public int getColumnType() {
			return columnType;
		}
		public void setColumnType(int columnType) {
			this.columnType = columnType;
		}
		public String getStringType() {
			return stringType;
		}
		public void setStringType(String stringType) {
			this.stringType = stringType;
		}

	}

	/**
	 * 获取参数值的数组
	 * 
	 * @return
	 */
	public Object[] getParamObjectValue() {
		if (paramList == null || paramList.size() == 0) {
			return null;
		}
		Object[] obj = new Object[paramList.size()];
		for (int i = 0; i < paramList.size(); i++) {
			obj[i] = paramList.get(i).getValue();
		}
		return obj;
	}

	public ParamBean[] getParamBeanArr() {
		if (paramList == null || paramList.size() == 0) {
			return null;
		}
		ParamBean[] param = new ParamBean[paramList.size()];
		for (int i = 0; i < paramList.size(); i++) {
			param[i] = paramList.get(i);
		}
		return param;
	}

	public String getSqlValue() {
		return this.sqlValue;
	}

	public void setSqlValue(String sqlValue) {
		this.sqlValue = sqlValue;
	}

	public List<ParamBean> getParamList() {
		return paramList;
	}

	public void setParamList(List<ParamBean> paramList) {
		this.paramList = paramList;
	}
}
