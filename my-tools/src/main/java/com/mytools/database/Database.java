/*
 * 作者：黄平
 * 
 */
package com.mytools.database;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mytools.beans.PageBean;
import com.mytools.beans.SqlParamsBean.ParamBean;
import com.mytools.enums.DatabaseTypeEnum;
import com.mytools.utils.DBUtil;
import com.mytools.utils.DateUtil;

/**
 * @author hp 数据库操作类
 */
public class Database {

	private Logger log = Logger.getLogger(Database.class);
	private DataSource dataSource;
	private String jndiName;
	
	private boolean debugModel = false;// 是否是调试模式
	private boolean showSql = false;// 是否打印sql
	private boolean transformColumn = true;//是否转换字段（如为true，则把类似user_id转换为userId）
	private String databaseType = DatabaseTypeEnum.ORACLE.toString();// 数据库类型
	private DatabaseAbst databaseAbst;
	
	public Database() {
	}

	public Database(DataSource dataSource) {
		setDataSource(dataSource);
	}

	/**
	 * 
	 */
	public void destroy() {
		if (this.dataSource instanceof MytoolsDataSource) {
			((MytoolsDataSource) this.dataSource).destroy();
		}
	}
	
	/**
	 * 获得一个数据库连接对象
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception {
		Connection conn = null;
		if (!StringUtils.isEmpty(this.jndiName)) {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + this.jndiName);
			conn = ds.getConnection();
		} else {
			conn = this.dataSource.getConnection();
		}
		return conn;
	}

	/**
	 * 得到ResultSet记录集
	 * 
	 * @param conn
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public ResultSet getRS(Connection conn, String sql, Object[] obj) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			showSql(sql, obj);
			ps = conn.prepareStatement(sql);
			setObject(ps, obj);
			rs = ps.executeQuery();
		} catch (Exception e) {
			log.error("执行查询语句失败！ with sql : " + sql);
			if (obj != null && obj.length > 0) {
				for (int i = 0; i < obj.length; i++) {
					if (obj[i] instanceof ParamBean) {
						log.info("param[" + i + "]= " + ((ParamBean) obj[i]).getValue());
					} else {
						log.info("param[" + i + "]= " + obj[i]);
					}
				}
			}
			throw e;
		}
		return rs;
	}

	/**
	 * 显示sql
	 * 
	 * @param sql
	 * @param obj
	 */
	public void showSql(String sql, Object[] obj) {
		if (this.showSql) {
			log.info("执行的sql为：" + sql);
			if (obj != null && obj.length > 0) {
				for (int i = 0; i < obj.length; i++) {
					if (obj[i] instanceof ParamBean) {
						log.info("param[" + i + "]= " + ((ParamBean) obj[i]).getValue());
					} else {
						log.info("param[" + i + "]= " + obj[i]);
					}
				}
			}
		}
	}

	/**
	 * 关闭ResultSet结果集
	 * 
	 * @param rs
	 */
	public void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.getStatement().close();
				rs.close();
				rs = null;
			}
		} catch (Exception e) {
			log.error("关闭ResultSet结果集失败", e);
		}
	}

	/**
	 * 清除任务,包括connection连接对象
	 * 
	 * @throws Exception
	 */
	public void cleanup(Connection conn) {
		try {
			if (conn == null) {
				return;
			}
			if (this.dataSource instanceof MytoolsDataSource) {
				MytoolsDataSource ds = (MytoolsDataSource) dataSource;
				ds.returnConn(conn);
			} else {
				if (!conn.isClosed()) {
					conn.close();
				}
				conn = null;
			}
		} catch (Exception e) {
			log.error("关闭connection对象失败", e);
		}
	}

	/**
	 * 查询，并返回List<Map<String, Object>>
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getToList(String sql) throws Exception {
		return getToList(sql, new Object[0]);
	}

	/**
	 * 查询，返回List<Map<String, Object>>
	 * 
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getToList(String sql, Object[] obj) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ResultSet rs = null;
		Connection conn = getConnection();
		try {
			rs = getRS(conn, sql, obj);
			ResultSetMetaData rsmd = rs.getMetaData();
			Map<String, Object> map = null;
			while (rs.next()) {
				map = new HashMap<String, Object>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (rs.getObject(i) != null) {
						map.put(DBUtil.formatColumn(rsmd.getColumnLabel(i), this.transformColumn), getValueFromRs(rs, rsmd.getColumnType(i), i));
					}
				}
				list.add(map);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				close(rs);
			} catch (Exception e) {
			} finally {
				cleanup(conn);
			}
		}
		return list.size() == 0 ? null : list;
	}
	
	/**
	 * 获取数据库值
	 * @param rs resultSet
	 * @param columnType 字段类型
	 * @param index 字段索引
	 * @return
	 * @throws Exception
	 */
	private Object getValueFromRs(ResultSet rs, int columnType, int index) throws Exception {
		switch (columnType) {
		case Types.TINYINT:
			return rs.getInt(index);
		case Types.SMALLINT:
			return rs.getInt(index);
		case Types.INTEGER:
			return rs.getInt(index);
		case Types.BIGINT:
			return rs.getLong(index);
		case Types.FLOAT:
			return rs.getFloat(index);
		case Types.REAL:
			return rs.getFloat(index);
		case Types.DOUBLE:
			return rs.getDouble(index);
		case Types.DATE:
			return DateUtil.dateToString(rs.getDate(index), DateUtil.DATE_TIME_FORMAT);
		case Types.TIME:
			return DateUtil.dateToString(rs.getDate(index), DateUtil.TIME_FORMAT);
		case Types.TIMESTAMP:
			return DateUtil.dateToString(rs.getDate(index), DateUtil.DATE_TIME_FORMAT);
		case Types.JAVA_OBJECT:
			return rs.getObject(index);
		case Types.ARRAY:
			return rs.getArray(index);
		case Types.BLOB:
			return rs.getBlob(index);
		case Types.REF:
			return rs.getRef(index);
		case Types.BOOLEAN:
			return rs.getBoolean(index);
		case Types.ROWID:
			return rs.getRowId(index);
		case Types.NCHAR:
			return rs.getNString(index);
		case Types.NVARCHAR:
			return rs.getNString(index);
		case Types.NCLOB:
			return rs.getNClob(index);
		case Types.SQLXML:
			return rs.getSQLXML(index);
		case Types.CLOB:
//			StringBuilder sb = new StringBuilder();
//			CLOB clob = (CLOB) rs.getClob(index);
//			BufferedReader br = new BufferedReader(clob.getCharacterStream());
//			String s = null;
//			while ((s = br.readLine()) != null) {
//				sb.append(s).append("\r\n");
//			}
//			br.close();
//			return sb.toString();
			
			StringBuilder sb = new StringBuilder();
			Clob clob = (Clob) rs.getClob(index);
			Reader br = clob.getCharacterStream();
			char[] c = new char[2048];
			int length = 0;
			while ((length = br.read(c)) != -1) {
				for (int i = 0; i < length; i++) {
					sb.append(c[i]);
				}
			}
			br.close();
			return sb.toString();
		default:
			return rs.getString(index);
		}
	}

	/**
	 * 根据类型获取数据库类型
	 * @param type
	 * @return
	 */
	private int transDbType(String type) {
		if (StringUtils.isEmpty(type)) {
			return Types.VARCHAR;
		} else if (type.equalsIgnoreCase("String") || type.equalsIgnoreCase("java.lang.String")) {
			return Types.VARCHAR;
		} else if (type.equalsIgnoreCase("Date") || type.equalsIgnoreCase("java.util.Date")) {
			return Types.DATE;
		} else if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("Integer") || type.equalsIgnoreCase("java.lang.Integer")) {
			return Types.INTEGER;
		} else if (type.equalsIgnoreCase("long") || type.equalsIgnoreCase("java.lang.Long")) {
			return Types.BIGINT;
		} else if (type.equalsIgnoreCase("short") || type.equalsIgnoreCase("java.lang.Short")) {
			return Types.SMALLINT;
		} else if (type.equalsIgnoreCase("double") || type.equalsIgnoreCase("java.lang.Double")) {
			return Types.DOUBLE;
		} else if (type.equalsIgnoreCase("float") || type.equalsIgnoreCase("java.lang.Float")) {
			return Types.FLOAT;
		} else if (type.equalsIgnoreCase("byte") || type.equalsIgnoreCase("java.lang.Byte")) {
			return Types.TINYINT;
		} else if (type.equalsIgnoreCase("java.math.BigDecimal") || type.equalsIgnoreCase("BigDecimal")) {
			return Types.DECIMAL;
		} else if (type.equalsIgnoreCase("java.sql.Blob") || type.equalsIgnoreCase("Blob")) {
			return Types.BLOB;
		} else if (type.equalsIgnoreCase("java.sql.Clob") || type.equalsIgnoreCase("Clob")) {
			return Types.CLOB;
		} else if (type.equalsIgnoreCase("boolean") || type.equalsIgnoreCase("java.lang.Boolean")) {
			return Types.BOOLEAN;
		} else if (type.equalsIgnoreCase("Time") || type.equalsIgnoreCase("java.sql.Time")) {
			return Types.TIME;
		} else if (type.equalsIgnoreCase("Timestamp") || type.equalsIgnoreCase("java.sql.Timestamp")) {
			return Types.TIMESTAMP;
		} else if (type.equalsIgnoreCase("array") || type.equalsIgnoreCase("java.sql.Array")) {
			return Types.ARRAY;
		} else if (type.equalsIgnoreCase("ref") || type.equalsIgnoreCase("java.sql.Ref")) {
			return Types.REF;
		} else if (type.equalsIgnoreCase("rowid") || type.equalsIgnoreCase("java.sql.RowId")) {
			return Types.ROWID;
		} else if (type.equalsIgnoreCase("nclob") || type.equalsIgnoreCase("java.sql.NClob")) {
			return Types.NCLOB;
		} else if (type.equalsIgnoreCase("sqlxml") || type.equalsIgnoreCase("java.lang.SQLXML")) {
			return Types.SQLXML;
		} else {
			return Types.VARCHAR;
		}
	}
	
	/**
	 * 查询，并返回List<T>
	 * 
	 * @param <T>
	 * @param sql
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> getToList(String sql, Class<T> className) throws Exception {
		return getToList(sql, null, className);
	}

	/**
	 * 查询，并返回List<T>
	 * 
	 * @param <T>
	 * @param sql
	 * @param obj
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> getToList(String sql, Object[] obj, Class<T> className) throws Exception {
		List<T> list = new ArrayList<T>();
		ResultSet rs = null;
		T targetObject = null;
		Connection conn = getConnection();
		try {
			rs = getRS(conn, sql, obj);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while (rs.next()) {
				targetObject = className.newInstance();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (rs.getObject(i) != null) {
						BeanUtils.setProperty(targetObject, DBUtil.formatColumn(rsmd.getColumnLabel(i), this.transformColumn), getValueFromRs(rs, rsmd.getColumnType(i), i));
					}
				}
				list.add(targetObject);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				close(rs);
			} catch (Exception e) {
			} finally {
				cleanup(conn);
			}
		}
		return list.size() == 0 ? null : list;
	}

	/**
	 * 查询，并返回map
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getToMap(String sql) throws Exception {
		return getToMap(sql, null);
	}

	/**
	 * 查询，并返回map
	 * 
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getToMap(String sql, Object[] obj) throws Exception {
		List<Map<String, Object>> list = getToList(sql, obj);
		Map<String, Object> map = null;
		if (list != null && list.size() > 0) {
			map = list.get(0);
		}
		return map;
	}

	/**
	 * 查询，返回Object数组的List
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> getToObjectList(String sql) throws Exception {
		return getToObjectList(sql, null);
	}

	/**
	 * 返回Object数组的List
	 * 
	 * @param sql
	 * @param paramObj
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> getToObjectList(String sql, Object[] paramObj) throws Exception {
		List<Object[]> list = new ArrayList<Object[]>();
		ResultSet rs = null;
		Object[] obj = null;
		Connection conn = getConnection();
		try {
			rs = getRS(conn, sql, paramObj);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				obj = new Object[rsmd.getColumnCount()];
				for (int i = 0; i < obj.length; i++) {
					obj[i] = getValueFromRs(rs, rsmd.getColumnType(i + 1), i + 1);
				}
				list.add(obj);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				close(rs);
			} catch (Exception e) {
			} finally {
				cleanup(conn);
			}
		}
		return list.size() == 0 ? null : list;
	}

	/**
	 * 查询，并返回stringList(通过 PreparedStatement )
	 * 
	 * @param sql
	 * @return
	 */
	public List<String> getToStringList(String sql) throws Exception {
		return getToStringList(sql, null);
	}

	/**
	 * 查询，并返回stringList
	 * 
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public List<String> getToStringList(String sql, Object[] obj) throws Exception {
		List<String> result = null;
		List<Object[]> list = getToObjectList(sql, obj);
		if (list != null && list.size() > 0) {
			result = new ArrayList<String>();
			for (Object[] tmpObj : list) {
				result.add(tmpObj[0] == null ? "" : tmpObj[0].toString());
			}
		}
		return result;
	}

	/**
	 * 查询，并返回一个Object
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public Object getObject(String sql) throws Exception {
		return getObject(sql, null);
	}

	/**
	 * 查询，并返回一个Object(通过 PreparedStatement )
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Object getObject(String sql, Object[] obj) throws Exception {
		ResultSet rs = null;
		Object obj1 = null;
		Connection conn = getConnection();
		try {
			rs = getRS(conn, sql, obj);
			if (rs.next()) {
				obj1 = rs.getObject(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				close(rs);
			} catch (Exception e) {
			} finally {
				cleanup(conn);
			}
		}
		return obj1;
	}

	/**
	 * 查询，并返回一个String
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public String getToString(String sql) throws Exception {
		return getToString(sql, null);
	}

	/**
	 * 查询，并返回一个String
	 * 
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public String getToString(String sql, Object[] obj) throws Exception {
		Object o = getObject(sql, obj);
		if (o == null) {
			return null;
		}
		return o.toString();
	}

	/**
	 * 查询，并返回一个int
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public int getToInt(String sql) throws Exception {
		return getToInt(sql, null);
	}

	/**
	 * 查询，并返回一个int
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public int getToInt(String sql, Object[] obj) throws Exception {
		String tmp = getToString(sql, obj);
		if (StringUtils.isEmpty(tmp)) {
			return 0;
		} else {
			return Integer.parseInt(tmp);
		}
	}

	/**
	 * 查询，并返回一个long
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public long getToLong(String sql) throws Exception {
		return getToLong(sql, null);
	}

	/**
	 * 查询，并返回一个long
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public long getToLong(String sql, Object[] obj) throws Exception {
		String tmp = getToString(sql, obj);
		if (StringUtils.isEmpty(tmp)) {
			return 0;
		} else {
			return Long.parseLong(tmp);
		}
	}

	/**
	 * 查询，并返回一个double
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public double getToDouble(String sql) throws Exception {
		return getToDouble(sql, null);
	}

	/**
	 * 查询，并返回一个double
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public double getToDouble(String sql, Object[] obj) throws Exception {
		String tmp = getToString(sql, obj);
		if (StringUtils.isEmpty(tmp)) {
			return 0;
		} else {
			return Double.parseDouble(tmp);
		}
	}

	/**
	 * 查询，并返回一个指定的对象
	 * @param sql
	 * @param obj
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public <T> T getToTargetObject(String sql, Object[] obj, Class<T> className) throws Exception {
		List<T> list = getToList(sql, obj, className);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查询，并返回一个指定的对象
	 * @param sql
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public <T> T getToTargetObject(String sql, Class<T> className) throws Exception {
		return getToTargetObject(sql, null, className);
	}

	/**
	 * 执行更新语句
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public long exeUpdate(String sql) throws Exception {
		return exeUpdate(sql, null);
	}

	/**
	 * 执行更新语句
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public long exeUpdate(String sql, Object[] obj) throws Exception {
		long result = 0;
		PreparedStatement ps = null;
		Connection conn = getConnection();
		ResultSet rs = null;
		try {
			showSql(sql, obj);
			ps = conn.prepareStatement(sql);
			setObject(ps, obj);
			result = ps.executeUpdate();
			if (DatabaseTypeEnum.MYSQL.toString().equalsIgnoreCase(this.databaseType) && sql.toLowerCase().indexOf("insert") >= 0) {
				sql = "SELECT LAST_INSERT_ID()";
				rs = getRS(conn, sql, null);
				if (rs.next()) {
					result = rs.getLong(1);
				}
			}
		} catch (Exception e) {
			log.error("执行更新出错！with sql is：" + sql);
			if (obj != null && obj.length > 0) {
				for (int i = 0; i < obj.length; i++) {
					if (obj[i] instanceof ParamBean) {
						log.info("param[" + i + "]= " + ((ParamBean) obj[i]).getValue());
					} else {
						log.info("param[" + i + "]= " + obj[i]);
					}
				}
			}
			throw e;
		} finally {
			try {
				if (rs != null) {
					close(rs);
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
			} finally {
				cleanup(conn);
			}
		}
		return result;
	}

	/**
	 * 设置值
	 * @param ps
	 * @param obj
	 * @throws Exception
	 */
	public void setObject(PreparedStatement ps, Object[] obj) throws Exception {
		ParamBean param = null;
		if (obj != null && obj.length > 0) {
			int type = 0;
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] instanceof ParamBean) {
					param = (ParamBean) obj[i];
					if (param == null || param.getValue() == null || StringUtils.isEmpty(String.valueOf(param.getValue()))) {
						ps.setNull(i + 1, Types.NULL);
						continue;
					}
					type = param.getColumnType();
					if (!StringUtils.isEmpty(param.getStringType())) {
						type = transDbType(param.getStringType());
					}
					switch (type) {
					case Types.TINYINT:
						if (param.getValue() instanceof Integer) {
							ps.setInt(i + 1, (Integer) param.getValue());
						} else {
							ps.setInt(i + 1, Integer.valueOf((String) param.getValue()));
						}
						break;
					case Types.SMALLINT:
						if (param.getValue() instanceof Integer) {
							ps.setInt(i + 1, (Integer) param.getValue());
						} else {
							ps.setInt(i + 1, Integer.valueOf((String) param.getValue()));
						}
						break;
					case Types.INTEGER:
						if (param.getValue() instanceof Integer) {
							ps.setInt(i + 1, (Integer) param.getValue());
						} else {
							ps.setInt(i + 1, Integer.valueOf((String) param.getValue()));
						}
						break;
					case Types.BIGINT:
						if (param.getValue() instanceof Long) {
							ps.setLong(i + 1, (Long) param.getValue());
						} else {
							ps.setLong(i + 1, Long.valueOf((String) param.getValue()));
						}
						break;
					case Types.FLOAT:
						if (param.getValue() instanceof Float) {
							ps.setFloat(i + 1, (Float) param.getValue());
						} else {
							ps.setFloat(i + 1, Float.valueOf((String) param.getValue()));
						}
						break;
					case Types.REAL:
						if (param.getValue() instanceof Float) {
							ps.setFloat(i + 1, (Float) param.getValue());
						} else {
							ps.setFloat(i + 1, Float.valueOf((String) param.getValue()));
						}
						break;
					case Types.DOUBLE:
						if (param.getValue() instanceof Double) {
							ps.setDouble(i + 1, (Double) param.getValue());
						} else {
							ps.setDouble(i + 1, Double.valueOf((String) param.getValue()));
						}
						break;
					case Types.DATE:
						if (param.getValue() instanceof Date) {
							ps.setDate(i + 1, (Date) param.getValue());
						} else {
							ps.setDate(i + 1, Date.valueOf((String) param.getValue()));
						}
						break;
					case Types.TIME:
						if (param.getValue() instanceof Time) {
							ps.setTime(i + 1, (Time) param.getValue());
						} else {
							ps.setTime(i + 1, Time.valueOf((String) param.getValue()));
						}
						break;
					case Types.TIMESTAMP:
						if (param.getValue() instanceof Timestamp) {
							ps.setTimestamp(i + 1, (Timestamp) param.getValue());
						} else {
							ps.setTimestamp(i + 1, Timestamp.valueOf((String) param.getValue()));
						}
						break;
					case Types.ARRAY:
						ps.setArray(i + 1, (Array) param.getValue());
						break;
					case Types.BLOB:
						ps.setBlob(i + 1, (Blob) param.getValue());
						break;
					case Types.REF:
						ps.setRef(i + 1, (Ref) param.getValue());
						break;
					case Types.BOOLEAN:
						if (param.getValue() instanceof Boolean) {
							ps.setBoolean(i + 1, (Boolean) param.getValue());
						} else {
							ps.setBoolean(i + 1, Boolean.valueOf((String) param.getValue()));
						}
						break;
					case Types.ROWID:
						ps.setRowId(i + 1, (RowId) param.getValue());
						break;
					case Types.NCLOB:
						ps.setNClob(i + 1, (NClob) param.getValue());
						break;
					case Types.SQLXML:
						ps.setSQLXML(i + 1, (SQLXML) param.getValue());
						break;
					case Types.CLOB:
						String str = (String) param.getValue();
						ps.setCharacterStream(i + 1, new StringReader(str), str.length());
						break;
					default:
						ps.setObject(i + 1, param.getValue());
					}
				} else {
					ps.setObject(i + 1, obj[i]);
				}
			}
		}
	}

	/**
	 * 批量更新
	 * @param sql sql语句
	 * @param obj 参数
	 * @param maxSize 最大提交个数
	 * @param showLog 是否显示提交日志
	 * @return true-提交成功；false-提交失败
	 * @throws Exception
	 */
	public boolean batchUpdateFromSQL(String sql, Object[][] obj, int maxSize, boolean showLog) throws Exception {
		if (obj == null || obj.length <= 0) {
			return false;
		}
		boolean result = false;
		PreparedStatement ps = null;
		Connection conn = getConnection();
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				setObject(ps, obj[i]);
				ps.addBatch();
				if (maxSize > 0 && ((i != 0 && (i + 1) % maxSize == 0) || i == obj.length - 1)) {
					ps.executeBatch();
					if (showLog) {
						log.info("批量更新，【" + (i + 1) + "/"+ obj.length +"】");
					}
					// this.conn.commit();
					ps.clearBatch();
				}
			}
			ps.executeBatch();
			conn.commit();
			ps.clearBatch();
			result = true;
		} catch (Exception e) {
			try {
				if (conn != null) {
					conn.rollback();
				}
				if (ps != null) {
					ps.clearBatch();
				}
			} catch (SQLException e1) {
			}
			throw e;
		} finally {
			try {
				if (conn != null) {
					conn.setAutoCommit(true);
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
			} finally {
				cleanup(conn);
			}
		}
		return result;
	}

	/**
	 * 带事务的更新
	 * @param sqls sql语句
	 * @return
	 * @throws Exception
	 */
	public boolean batchUpdateForAffairs(String[] sqls) throws Exception {
		if (sqls == null || sqls.length <= 0) {
			return false;
		}
		boolean result = false;
		Statement stat = null;
		Connection conn = getConnection();
		try {
			conn.setAutoCommit(false);
			stat = conn.createStatement();
			for (String sql : sqls) {
				stat.addBatch(sql);
			}
			stat.executeBatch();
			conn.commit();
			result = true;
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
			throw e;
		} finally {
			try {
				conn.setAutoCommit(true);
				if (stat != null) {
					stat.close();
				}
			} catch (SQLException e) {
			} finally {
				cleanup(conn);
			}
		}
		return result;
	}
	
	/**
	 * 带分页的查询
	 * @param page
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> getToList(PageBean page, Class<T> className) throws Exception {
		page.setTotalCount(getToInt(databaseAbst.getTotalCountSql(page.getSql()), page.getParams()));
		return getToList(databaseAbst.getPageSQL(page), page.getParams(), className);
	}
	
	/**
	 * 带分页的查询
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getToList(PageBean page) throws Exception {
		page.setTotalCount(getToInt(databaseAbst.getTotalCountSql(page.getSql()), page.getParams()));
		return getToList(databaseAbst.getPageSQL(page), page.getParams());
	}
	
	public void setDebugModel(boolean debugModel) {
		this.debugModel = debugModel;
	}
	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}
	public void setTransformColumn(boolean transformColumn) {
		this.transformColumn = transformColumn;
	}
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		if (this.dataSource instanceof MytoolsDataSource) {
			MytoolsDataSource ds = (MytoolsDataSource) this.dataSource;
			if (!StringUtils.isEmpty(ds.getDatabaseType())) {
				this.databaseType = ds.getDatabaseType();
			} else {
				ds.setDatabaseType(this.databaseType);
			}
		}
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public boolean isTransformColumn() {
		return transformColumn;
	}

	public boolean isDebugModel() {
		return debugModel;
	}

	public void setDatabaseAbst(DatabaseAbst databaseAbst) {
		this.databaseAbst = databaseAbst;
	}
}