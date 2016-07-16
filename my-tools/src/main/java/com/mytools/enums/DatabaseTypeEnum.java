package com.mytools.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mytools.database.DatabaseAbst;
import com.mytools.database.impl.Db2DatabaseImpl;
import com.mytools.database.impl.MysqlDatabaseImpl;
import com.mytools.database.impl.OracleDatabaseImpl;
import com.mytools.database.impl.SqlServerDatabaseImpl;

/**
 * 
 * @author huangping<br />
 * 2013-3-19
 */
public enum DatabaseTypeEnum {
	
	ORACLE,
	MYSQL,
	SQLSERVER,
	DB2;
	/**
	 * 根据数据库类型，获取不同的实现类
	 * @param databaseType
	 * @return DatabaseAbst
	 */
	public static DatabaseAbst getDatabaseAbst(String databaseType) {
		if (ORACLE.toString().equalsIgnoreCase(databaseType)) {
			return new OracleDatabaseImpl();
		} else if (MYSQL.toString().equalsIgnoreCase(databaseType)) {
			return new MysqlDatabaseImpl();
		} else if (SQLSERVER.toString().equalsIgnoreCase(databaseType)) {
			return new SqlServerDatabaseImpl();
		} else if (DB2.toString().equalsIgnoreCase(databaseType)) {
			return new Db2DatabaseImpl();
		} else {
			return null;
		}
	}
	
	/**
	 * 返回json格式的数据
	 * @return 返回json格式的数据
	 */
	public static JSONArray toJson() {
		JSONArray arr = new JSONArray();
		JSONObject json = null;
		for (int i = 0; i < values().length; i++) {
			json = new JSONObject();
			json.put("value", values()[i].toString());
			json.put("text", values()[i].toString());
			arr.add(json);
		}
		return arr;
	}

}
