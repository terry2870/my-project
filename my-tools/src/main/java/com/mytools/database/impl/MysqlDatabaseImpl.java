package com.mytools.database.impl;

import org.apache.commons.lang3.StringUtils;

import com.mytools.beans.PageBean;
import com.mytools.database.DatabaseAbst;

/**
 * 
 * @author huangping<br />
 * 2013-3-19
 */
public class MysqlDatabaseImpl extends DatabaseAbst {

	@Override
	public String getPageSQL(PageBean pb) {
		String result = "";
		int startIndex = (pb.getCurrentPage() - 1) * pb.getPageSize();
		result = "SELECT * FROM (" + pb.getSql() + ") mysqlPageSQL";
		if (!StringUtils.isEmpty(pb.getOrderBy())) {
			result += " ORDER BY " + pb.getOrderBy() + " " + pb.getSort();
		}
		result += " LIMIT " + startIndex + "," + pb.getPageSize();
		return result;
	}

	@Override
	public String getDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

	@Override
	public String getConnectUrl() {
		return getConnectUrl("ip", 3306, "dbName");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConnectUrl(String ip, int port, String databaseName) {
		return "jdbc:mysql://"+ ip +":"+ port +"/"+ databaseName +"?useUnicode=true&characterEncoding=UTF-8";
	}

	@Override
	public String getCheckSql() {
		return "SELECT NOW()";
	}

}
