package com.mytools.extend.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * 缓存操作对象
 * @author ping.huang
 * 创建日期 2014-07-21
 */
public class MyCache implements Serializable {

	private static final long serialVersionUID = -6662928467248070121L;
	/**
	 * 主操作对象
	 */
	private Map<String, CacheObject> cache = null;
	/**
	 * 是否允许覆盖
	 */
	private boolean allowCover;
	/**
	 * 缓存容量（个数）
	 * 小等于0，则容量没有限制
	 */
	private int capacity = 500;
	/**
	 * 自动清理缓存的周期
	 * 小于等于0，表示不自动清理
	 */
	private long period;
	
	public MyCache() {
		
	}

	/**
	 * 初始化
	 */
	public void init() {
		if (cache == null) {
			cache = new HashMap<String, CacheObject>();
			if (this.period > 0) {
				Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(new Runnable() {
					@Override
					public void run() {
						clearInvalid();
					}
				}, 3 * 60 * 1000, period, TimeUnit.MILLISECONDS);
			}
		}
	}
	
	/**
	 * 缓存大小
	 * @return
	 * @throws InterruptedException 
	 */
	public int size() {
		return cache.size();
	}
	
	/**
	 * 清空缓存
	 * @throws InterruptedException 
	 */
	public synchronized void clear() {
		cache.clear();
	}
	
	/**
	 * 根据KEY来删除
	 * @param key
	 */
	public synchronized void removeByKey(String key) {
		cache.remove(key);
	}
	
	/**
	 * 根据值来删除
	 * @param value
	 */
	public synchronized void removeByValue(Object value) {
		List<String> removeList = new ArrayList<String>();
		if (value == null) {
			for (Entry<String, CacheObject> entry : cache.entrySet()) {
				if (entry.getValue() == null) {
					removeList.add(entry.getKey());
				}
			}
		} else {
			for (Entry<String, CacheObject> entry : cache.entrySet()) {
				if (value.equals(entry.getValue())) {
					removeList.add(entry.getKey());
				}
			}
		}
		for (String key : removeList) {
			cache.remove(key);
		}
	}
	
	/**
	 * 载入
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean put(String key, Object value) throws Exception {
		return put(key, value, 0);
	}
	
	/**
	 * 载入
	 * @param key
	 * @param value
	 * @param cacheTime
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean put(String key, Object value, long cacheTime) throws Exception {
		if (this.capacity > 0 && size() >= this.capacity) {
			clearInvalid();
			if (this.capacity > 0 && size() >= this.capacity) {
				throw new Exception("缓存容量已达上限！");
			}
		}
		CacheObject obj = null;
		if (containsKey(key)) {
			obj = cache.get(key);
			if (!allowCover) {
				throw new Exception("键" + "【"+ key +"】已经存在，并且不允许覆盖。");
			}
		} else {
			obj = new CacheObject();
		}
		setValue(obj, value, cacheTime);
		cache.put(key, obj);
		return true;
	}
	
	/**
	 * 获取值
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		CacheObject obj = cache.get(key);
		if (obj == null) {
			return null;
		}
		if (!obj.checkValid()) {
			if (obj.isValid()) {
				obj.setValid(false);
			}
			return null;
		}
		return obj.getValue() == null ? null : obj.getValue();
	}
	
	/**
	 * 获取值
	 * @param key
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) {
		Object obj = get(key);
		return (T) obj;
	}
	
	/**
	 * 更新缓存
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean update(String key, Object value) throws Exception {
		return update(key, value, 0);
	}
	
	/**
	 * 更新缓存
	 * @param key
	 * @param value
	 * @param cacheTime
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean update(String key, Object value, long cacheTime) throws Exception {
		CacheObject obj = cache.get(key);
		if (obj == null) {
			throw new Exception("键" + "【"+ key +"】不存在。");
		}
		setValue(obj, value, cacheTime);
		return true;
	}
	
	/**
	 * 更新或载入缓存
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean putOrUpdate(String key, Object value) throws Exception {
		return putOrUpdate(key, value, 0);
	}
	
	/**
	 * 更新或载入缓存
	 * @param key
	 * @param value
	 * @param cacheTime
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean putOrUpdate(String key, Object value, long cacheTime) throws Exception {
		CacheObject obj = cache.get(key);
		if (obj == null) {
			if (this.capacity > 0 && size() >= this.capacity) {
				clearInvalid();
				if (this.capacity > 0 && size() >= this.capacity) {
					throw new Exception("缓存容量已达上限！");
				}
			}
			obj = new CacheObject();
		}
		setValue(obj, value, cacheTime);
		cache.put(key, obj);
		return true;
	}
	
	/**
	 * 包含键值
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key) {
		return cache.containsKey(key);
	}
	
	/**
	 * 包含值
	 * @param value
	 * @return
	 */
	public boolean containValue(Object value) {
		for (Entry<String, CacheObject> entry : cache.entrySet()) {
			if (value.equals(entry.getValue())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 设置值
	 * @param obj
	 * @param value
	 * @param cacheTime
	 */
	private void setValue(CacheObject obj, Object value, long cacheTime) {
		obj.setValue(value);
		obj.setLastUpdateTime(System.currentTimeMillis());
		obj.setValid(true);
		obj.setCacheTime(cacheTime);
		obj.setInvalidTime(cacheTime <= 0 ? 0 : System.currentTimeMillis() + cacheTime);
	}
	
	/**
	 * 清除过期的缓存
	 */
	public synchronized void clearInvalid() {
		CacheObject obj = null;
		List<String> removeList = new ArrayList<String>();
		for (Entry<String, CacheObject> entry : cache.entrySet()) {
			obj = entry.getValue();
			if (!obj.checkValid()) {
				removeList.add(entry.getKey());
			}
		}
		for (String key : removeList) {
			cache.remove(key);
		}
	}
	
	public boolean isAllowCover() {
		return allowCover;
	}

	public void setAllowCover(boolean allowCover) {
		this.allowCover = allowCover;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public Map<String, CacheObject> getCache() {
		return cache;
	}
}
