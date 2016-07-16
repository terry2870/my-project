package com.mytools.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.mytools.enums.DatabaseTypeEnum;
import com.mytools.extend.BaseTimer;

/**
 * 连接池对象
 * @author huangping
 *
 */
public class ConnectionPool {

	private Logger log = Logger.getLogger(getClass());
	private Map<Connection, Boolean> pool = new Hashtable<Connection, Boolean>();
	private MytoolsDataSource datasource;
	
	public ConnectionPool(MytoolsDataSource datasource) {
		this.datasource = datasource;
	}
	
	public synchronized Connection getConnection() throws Exception {
		Connection conn = null; // 需要返回的连接，初始值为空
		synchronized (pool) {
			Date d1 = new Date();
			while (true) {
				Set<Entry<Connection, Boolean>> set = pool.entrySet();
				// 遍历连接池中的所有连接，取出一个空闲的
				for (Entry<Connection, Boolean> entry : set) {
					if (entry.getValue()) {
						continue;
					}
					conn = entry.getKey();
					if (this.datasource.getCheckConnOnGet() && !checkConn(conn)) {
						log.info("【" + conn + "】已经被关闭！重新生成一个连接。");
						pool.remove(conn);
						conn = createConnection();
						pool.put(conn, Boolean.FALSE);
					}
					pool.put(conn, Boolean.TRUE);
					return conn;
				}
				// 如果没有空闲的连接，并且数据库中的连接数量小于最大连接数，那么就去生成几个
				if (this.datasource.getMaxActive() == 0 || pool.size() < this.datasource.getMaxActive()) {
					conn = addConnection(this.datasource.getIncrementNum());
					pool.put(conn, Boolean.TRUE);
					return conn;
				}
				// 如果还是没有可用的连接，先判断等待时间是否超时。如果超时，直接抛出异常，如果没有超时，则进去线程等待
				if (this.datasource.getMaxWait() > 0) {
					Date d2 = new Date();
					if ((d2.getTime() - d1.getTime()) > this.datasource.getMaxWait()) {
						throw new Exception("获取连接超时");
					}
				}
				pool.wait(500);
			}
		}
	}
	
	/**
	 * 创建一个连接
	 * 
	 * @return
	 * @throws Exception
	 */
	private synchronized Connection createConnection() throws Exception {
		Connection conn = null;
		Class.forName(this.datasource.getDriverClassName());
		conn = DriverManager.getConnection(this.datasource.getUrl(), this.datasource.getUserName(), this.datasource.getPassword());
		log.info("创建一个连接");
		return conn;
	}
	
	/**
	 * 增加连接，返回最后一个创建的连接
	 * @param num
	 * @return
	 * @throws Exception
	 */
	protected synchronized Connection addConnection(int num) throws Exception {
		Connection conn = null;
		for (int i = 0; i < num && (this.datasource.getMaxActive() == 0 || pool.size() < this.datasource.getMaxActive()); i++) {
			conn = createConnection();
			pool.put(conn, Boolean.FALSE);
		}
		return conn;
	}

	/**
	 * 释放单个连接
	 * 
	 * @param conn
	 */
	protected synchronized void returnConnection(Connection conn) {
		if (conn == null) {
			return;
		}
		pool.put(conn, Boolean.FALSE);
	}

	/**
	 * 清空连接池中所有的连接 不考虑该连接是否正在被使用，一律强制关闭。 该方法一般是tomcat关闭的时候去执行。
	 */
	protected synchronized void clearPool() {
		if (pool == null || pool.isEmpty()) {
			return;
		}
		log.info("销毁连接池中所有连接！");
		Connection conn = null;
		Set<Entry<Connection, Boolean>> set = pool.entrySet();
		for (Entry<Connection, Boolean> entry : set) {
			conn = entry.getKey();
			try {
				conn.close();
			} catch (Exception ex) {
				log.error("", ex);
			}
		}
		pool.clear();
	}

	/**
	 * 获取连接池使用情况
	 * 
	 * @return
	 */
	protected Map<String, Integer> getConnUse() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		int usingConn = 0, freeConn = 0;
		Set<Entry<Connection, Boolean>> set = pool.entrySet();
		for (Entry<Connection, Boolean> entry : set) {
			if (!entry.getValue()) {
				freeConn++;
			} else {
				usingConn++;
			}
		}
		map.put("usingConn", usingConn);
		map.put("freeConn", freeConn);
		return map;
	}

	/**
	 * 获取各个连接的连接状态
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected String toStrings() throws Exception {
		StringBuffer sb = new StringBuffer();
		Map<String, Integer> map = getConnUse();
		sb.append("数据库最大连接数：" + this.datasource.getMaxActive() + "<br>");
		sb.append("当前正在使用的连接数：" + map.get("usingConn") + "<br>");
		sb.append("当前空闲的连接数：" + map.get("freeConn") + "<br>");
		Connection conn = null;
		Set<Entry<Connection, Boolean>> set = pool.entrySet();
		int i = 0;
		for (Entry<Connection, Boolean> entry : set) {
			conn = entry.getKey();
			sb.append("第【" + i + "】个连接【" + conn + "】；使用状态：【" + (pool.get(conn) ? "正在被使用" : "没有被使用") + "】；连接状态：【" + (checkConn(conn) ? "连接正常" : "连接异常") + "】<br>");
			i++;
		}
		return sb.toString();
	}
	
	/**
	 * 检查连接是正常
	 * 
	 * @param conn
	 * @return
	 */
	private boolean checkConn(Connection conn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (conn == null || conn.isClosed()) {
				return false;
			}
			String sql = "";
			if (DatabaseTypeEnum.ORACLE.toString().equalsIgnoreCase(this.datasource.getDatabaseType())) {
				sql = "SELECT SYSDATE FROM DUAL";
			} else if (DatabaseTypeEnum.MYSQL.toString().equalsIgnoreCase(this.datasource.getDatabaseType())) {
				sql = "SELECT 1";
			} else if (DatabaseTypeEnum.SQLSERVER.toString().equalsIgnoreCase(this.datasource.getDatabaseType())) {
				sql = "SELECT GETDATE()";
			} else if (DatabaseTypeEnum.DB2.toString().equalsIgnoreCase(this.datasource.getDatabaseType())) {
				sql = "SELECT CURRENT TIMESTAMP FROM sysibm.sysdummy1";
			}
			ps = conn.prepareStatement(sql);
			if (ps == null) {
				return false;
			}
			rs = ps.executeQuery();
			if (rs == null) {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return true;
	}
	
	/**
	 * 自动检查空闲连接的个数是否超过最大空闲数
	 * 如果超过，则释放掉那些空闲的连接，直到空闲数小于等于最大空闲数
	 * @author huangping
	 *
	 */
	protected class ConnectionCheck extends BaseTimer {

		public ConnectionCheck(long delay, long period) {
			super(delay, period);
			init();
		}
		@Override
		public void process() {
			synchronized (pool) {
				try {
					Map<String, Integer> map = getConnUse();
					int free = map.get("freeConn");
					if (free > datasource.getMaxIdle()) {
						Set<Entry<Connection, Boolean>> set = pool.entrySet();
						int i = 0;
						List<Connection> list = new ArrayList<Connection>();
						for (Entry<Connection, Boolean> entry : set) {
							if (!entry.getValue()) {
								list.add(entry.getKey());
								i++;
								if (i == free - datasource.getMaxIdle()) {
									break;
								}
							}
						}
						for (Connection conn : list) {
							conn.close();
							pool.remove(conn);
							log.info("删除连接【"+ conn +"】");
							conn = null;
						}
					}
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
		
	}
}
