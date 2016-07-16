package com.mytools.beans;

import com.alibaba.fastjson.JSON;

/**
 * @author huangping <br />
 * 2014-1-26
 */
public class PageBean {
	
	private String sql;
	private Object[] params;
	private int pageSize;
	private int currentPage;
	private int totalCount;
	private int maxPage;
	private String orderBy;
	private String sort;
	
	public PageBean() {}
	
	/**
	 * 判断是否为空
	 * @return
	 */
	public boolean isEmpty() {
		return this.pageSize == 0 && this.currentPage == 0;
	}
	
	/**
	 * @param sql
	 * @param params
	 */
	public PageBean(String sql, Object[] params) {
		super();
		this.sql = sql;
		this.params = params;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getMaxPage() {
		return maxPage;
	}
	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}

}
