package com.mytools.database;

import java.io.Serializable;

import com.mytools.enums.DatabaseTypeEnum;

public class MytoolsDataSourceBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String databaseType = DatabaseTypeEnum.ORACLE.toString();
	private int maxActive = 20;// 连接池中允许存放的最大数据库连接数（0表示没有限制）
	private int incrementNum = 5;// 当连接池中的空闲数据库连接数不足时，每次允许连接池增加的数据库连接数
	private int initConnNum = 5;// 连接池在被创建时，允许生成的初始数据库连接数
	private boolean checkConnOnGet = true;// 是否在获取连接时检查连接的有效性
	private int maxWait = 10000;// 取连接时最大的等待时间，超过的话就抛出异常（单位毫秒）
	private int maxIdle = 5;// 最大空闲数（0表示没有限制）
	private String checkBeginTime = "";// 检查连接开始时间（格式：HH:mm:ss  maxIdle>0才生效，为空表示从服务启动开始进行）
	private long checkPeriod = 3600000;// 检查连接池周期（单位：毫秒 maxIdle>0才生效）

	private String driverClassName = "";// 数据库连接驱动程序
	private String url = ""; // 数据库连接地址
	private String userName = ""; // 登录数据库的用户名
	private String password = ""; // 登录数据库用户的用户口令
	
	public int getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
	public int getIncrementNum() {
		return incrementNum;
	}
	public void setIncrementNum(int incrementNum) {
		this.incrementNum = incrementNum;
	}
	public int getMaxWait() {
		return maxWait;
	}
	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public String getCheckBeginTime() {
		return checkBeginTime;
	}
	public void setCheckBeginTime(String checkBeginTime) {
		this.checkBeginTime = checkBeginTime;
	}
	public long getCheckPeriod() {
		return checkPeriod;
	}
	public void setCheckPeriod(long checkPeriod) {
		this.checkPeriod = checkPeriod;
	}
	public String getDriverClassName() {
		return driverClassName;
	}
	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getInitConnNum() {
		return initConnNum;
	}
	public void setInitConnNum(int initConnNum) {
		this.initConnNum = initConnNum;
	}
	public boolean getCheckConnOnGet() {
		return checkConnOnGet;
	}
	public void setCheckConnOnGet(boolean checkConnOnGet) {
		this.checkConnOnGet = checkConnOnGet;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

}
