package com.mytools.database.impl;

import com.mytools.beans.PageBean;
import com.mytools.database.DatabaseAbst;

/**
 * 
 * @author huangping<br />
 * 2013-3-19
 */
public class SqlServerDatabaseImpl extends DatabaseAbst {

	@Override
	public String getPageSQL(PageBean pb) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDriverClassName() {
		return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	}

	@Override
	public String getConnectUrl() {
		return getConnectUrl("ip", 1433, "dbName");
	}

	@Override
	public String getConnectUrl(String ip, int port, String databaseName) {
		return "jdbc:sqlserver://"+ ip +":"+ port +";DatabaseName=" + databaseName;
	}

	@Override
	public String getCheckSql() {
		return "select getdate()";
	}

}
