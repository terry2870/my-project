package com.mytools.database;

import javax.sql.DataSource;

import com.mytools.beans.PageBean;

/**
 * 每个数据库类型的抽象类
 * @author huangping <br />
 * 2014-1-26
 */
public abstract class DatabaseAbst {
	
	DataSource datasource;

//	/**
//	 * 计算总条数和总页数
//	 * 
//	 * @param pb
//	 * @param db
//	 * @throws Exception
//	 */
//	public void dealPage(PageBean pb) {
//		pb.setTotalCount(this.getTotalCount(pb));
//		pb.setMaxPage(this.getMaxPage(pb.getTotalCount(), pb.getPageSize()));
//	}

	
	/**
	 * 计算总条数
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public String getTotalCountSql(String sql) {
		return "SELECT COUNT(*) num FROM (" + sql + ") pageTotalCount";
	}

	/**
	 * 根据总行数算出总页数
	 * 
	 * @param totalCount
	 * @param rowPerPage
	 * @return
	 */
	public int getMaxPage(int totalCount, int rowPerPage) {
		if (rowPerPage == 0) {
			return 0;
		}
		int tmp = totalCount / rowPerPage;
		if (totalCount % rowPerPage == 0) {
			return tmp;
		} else {
			return tmp + 1;
		}
	}

	/**
	 * 取得分页的sql
	 * 
	 * @param pb
	 * @return
	 */
	public abstract String getPageSQL(PageBean pb);
	
	/**
	 * 获取驱动程序
	 * @return
	 */
	public abstract String getDriverClassName();
	
	/**
	 * 获取数据库连接url
	 * @return
	 */
	public abstract String getConnectUrl();
	
	/**
	 * 获取数据库连接url
	 * @param ip
	 * @param port
	 * @param databaseName
	 * @return
	 */
	public abstract String getConnectUrl(String ip, int port, String databaseName);
	
	public abstract String getCheckSql();

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}
}
