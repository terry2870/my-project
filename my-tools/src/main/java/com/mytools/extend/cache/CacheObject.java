package com.mytools.extend.cache;

/**
 * 缓存对象
 * @author ping.huang
 * 创建日期 2014-07-21
 */
public class CacheObject {

	public CacheObject() {
	}
	/**
	 * 缓存值
	 */
	private Object value;
	/**
	 * 最后更新时间
	 */
	private long lastUpdateTime;
	/**
	 * 缓存时间
	 * 小于等于0，则永不过期，除非手动设置valid为false。
	 */
	private long cacheTime;
	/**
	 * 失效时间
	 */
	private long invalidTime;
	/**
	 * 是否有效
	 */
	private boolean valid;
	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public long getCacheTime() {
		return cacheTime;
	}
	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}
	public long getInvalidTime() {
		return invalidTime;
	}
	public void setInvalidTime(long invalidTime) {
		this.invalidTime = invalidTime;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	/**
	 * 验证是否有效
	 * @return
	 */
	public boolean checkValid() {
		if (!this.valid) {
			return false;
		}
		if (this.cacheTime <= 0) {
			return true;
		}
		return this.invalidTime >= System.currentTimeMillis();
	}
}
