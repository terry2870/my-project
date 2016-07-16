package com.mytools.database.impl;

import org.apache.commons.lang3.StringUtils;

import com.mytools.beans.PageBean;
import com.mytools.database.DatabaseAbst;


/**
 * 
 * @author huangping<br />
 * 2013-3-19
 */
public class Db2DatabaseImpl extends DatabaseAbst {


	@Override
	public String getPageSQL(PageBean pb) {
		String result = "";
		int startIndex = (pb.getCurrentPage() - 1) * pb.getPageSize();
		int endIndex = startIndex + pb.getPageSize();
		if (!StringUtils.isEmpty(pb.getOrderBy())) {
			result = "SELECT * FROM (" + pb.getSql() + ") oraclePageSQL ORDER BY " + pb.getOrderBy() + " " + pb.getSort();
		} else {
			result = pb.getSql();
		}
		result = "SELECT * FROM (SELECT oraclePageSQL2.*, ROWNUMBER() OVER() AS RN FROM(" + result + ") AS oraclePageSQL2)AS oraclePageSQL1 WHERE oraclePageSQL1.RN > " + startIndex
				+ " AND oraclePageSQL1.RN<=" + endIndex;
		return result;
	}

	@Override
	public String getDriverClassName() {
		return "com.ibm.db2.jdbc.app.DB2.Driver";
	}

	@Override
	public String getConnectUrl() {
		return getConnectUrl("ip", 5000, "dbName");
	}

	@Override
	public String getConnectUrl(String ip, int port, String databaseName) {
		return "jdbc:db2://"+ ip +":"+ port +"/" + databaseName;
	}

	@Override
	public String getCheckSql() {
		return "SELECT current date FROM sysibm.sysdummy1";
	}

}
