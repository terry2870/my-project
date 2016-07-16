package com.mytools.configs;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author huangping <br />
 * 2014-2-13
 */
public class MyToolsConfig {

	private boolean debugModel;
	private String databaseType = "oracle";
	private JdbcTemplate jdbcTemplate;
	
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setDebugModel(boolean debugModel) {
		this.debugModel = debugModel;
	}

	public boolean isDebugModel() {
		return debugModel;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

}
