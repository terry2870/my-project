package com.mytools.database.impl;

import org.apache.commons.lang3.StringUtils;

import com.mytools.beans.PageBean;
import com.mytools.database.DatabaseAbst;

/**
 * 
 * @author huangping<br />
 * 2013-3-19
 */
public class OracleDatabaseImpl extends DatabaseAbst {

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
		result = "SELECT * FROM (SELECT oraclePageSQL1.*,rownum rn FROM (" + result + ") oraclePageSQL1 WHERE rownum<=" + endIndex + ") oraclePageSQL2 WHERE rn>" + startIndex;
		return result;
	}

	@Override
	public String getDriverClassName() {
		return "oracle.jdbc.OracleDriver";
	}

	@Override
	public String getConnectUrl() {
		return getConnectUrl("ip", 1521, "dbName");
	}

	@Override
	public String getConnectUrl(String ip, int port, String databaseName) {
		return "jdbc:oracle:thin:@"+ ip +":"+ port +":" + databaseName;
	}

	@Override
	public String getCheckSql() {
		return "SELECT SYSDATE FROM DUAL";
	}

}
