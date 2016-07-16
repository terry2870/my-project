package com.mytools.database;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mytools.utils.DateUtil;

/**
 * 数据源
 * @author huangping
 *
 */
public class MytoolsDataSource extends MytoolsDataSourceBean implements
		javax.sql.DataSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Logger log = Logger.getLogger(getClass());
	ConnectionPool pool;
	
	/**
	 * 初始化连接池
	 */
	public void init() {
		try {
			if (this.pool == null) {
				this.pool = new ConnectionPool(this);
				this.pool.addConnection(this.getInitConnNum());
				if (this.getMaxIdle() > 0) {
					long delay = 0;
					if (!StringUtils.isEmpty(this.getCheckBeginTime())) {
						delay = DateUtil.getTimeDiff(this.getCheckBeginTime());
					}
					this.pool.new ConnectionCheck(delay, (long) (this.getCheckPeriod()));
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	public MytoolsDataSource() {
	}

	public MytoolsDataSource(MytoolsDataSourceBean bean) {
		Field[] fields = MytoolsDataSourceBean.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				field.set(this, field.get(bean));
			} catch (Exception e) {
			}
		}
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		init();
		try {
			conn = this.pool.getConnection();
		} catch (Exception e) {
			log.error("", e);
		}
		return conn;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toStrings() {
		try {
			return this.pool.toStrings();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}
	
	public void destroy() {
		if (this.pool != null) {
			this.pool.clearPool();
		}
	}
	
	/**
	 * 回收连接
	 * @param conn
	 */
	public void returnConn(Connection conn) {
		this.pool.returnConnection(conn);
	}

	/* (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getParentLogger()
	 */
	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
