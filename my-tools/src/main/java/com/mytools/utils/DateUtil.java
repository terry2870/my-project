package com.mytools.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 时间日期工具类
 * 
 * @author Administrator
 * 
 */
public class DateUtil {

	static Logger log = Logger.getLogger(DateUtil.class);
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TIME_FORMAT = "HH:mm:ss";

	/**
	 * 按照格式，取得当前时间
	 * @param str
	 * @return
	 */
	public static String getToday(String str) {
		return dateToString(new Date(), str);
	}
	
	/**
	 * 获取今天的日期时间（yyyy-MM-dd HH:mm:ss）
	 * @return
	 */
	public static String getcurrentDateTime() {
		return getToday(DATE_TIME_FORMAT);
	}
	
	/**
	 * 获取今天的日期（yyyy-MM-dd）
	 * @return
	 */
	public static String getcurrentDate() {
		return getToday(DATE_FORMAT);
	}
	
	/**
	 * 获取今天的日期（HH:mm:ss）
	 * @return
	 */
	public static String getcurrentTime() {
		return getToday(TIME_FORMAT);
	}

	/**
	 * 对日期进行加减
	 * @param date 被转换的日期
	 * @param type 转换类型(y-年,M-月,d-日, H-小时, m-分钟, s-秒)
	 * @param offset 转换的单位
	 * @param simpleDateFormat 日期格式
	 * @return
	 */
	public static String dateAdd(String date, String type, int offset, String simpleDateFormat) {
		if (StringUtils.isEmpty(date)) {
			return null;
		}
		if (StringUtils.isEmpty(type)) {
			return date;
		}
		if (offset == 0) {
			return date;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(stringToDate(date, simpleDateFormat));
		if (type.equals("y")) {
			cal.add(Calendar.YEAR, offset);
		} else if (type.equals("M")) {
			cal.add(Calendar.MONTH, offset);
		} else if (type.equals("d")) {
			cal.add(Calendar.DATE, offset);
		} else if (type.equals("H")) {
			cal.add(Calendar.HOUR, offset);
		} else if (type.equals("m")) {
			cal.add(Calendar.MINUTE, offset);
		} else if (type.equals("s")) {
			cal.add(Calendar.SECOND, offset);
		}
		return dateToString(cal.getTime(), simpleDateFormat);
	}

	/**
	 * ͳDate型日期转换为String型
	 * @param date
	 * @param simpleDateFormat
	 * @return
	 */
	public static String dateToString(Date date, String simpleDateFormat) {
		SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
		if (date == null) {
			return null;
		} else {
			return format.format(date);
		}
	}

	/**
	 * String型日期转换为Date型
	 * @param date
	 * @param simpleDateFormat
	 * @return
	 */
	public static Date stringToDate(String date, String simpleDateFormat) {
		SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
		Date d = null;
		if (StringUtils.isEmpty(date)) {
			return null;
		} else {
			try {
				d = format.parse(date);
			} catch (ParseException e) {
				log.error("", e);
			}
			return d;
		}
	}

	/**
	 * 日期格式转换
	 * @param date
	 * @param from
	 * @param to
	 * @return
	 */
	public static String formatDate(String date, String from, String to) {
		return dateToString(stringToDate(date, from), to);
	}
	
	/**
	 * 获取当前时间距离输入时间的毫秒数。如果输入时间小于当前时间，则取第二天的同一时间
	 * 时间格式（HH:mm:ss）
	 * @param time
	 * @return
	 */
	public static long getTimeDiff(String time) {
		if (StringUtils.isEmpty(time)) {
			return 0;
		}
		Date d1 = new Date();
		Date d2 = null;
		String nowDate = dateToString(d1, "yyyy-MM-dd");
		String nowTime = dateToString(d1, "HH:mm:ss");
		if (time.compareTo(nowTime) >= 0) {
			d2 = stringToDate(nowDate + " " + time, "yyyy-MM-dd HH:mm:ss");
		} else {
			d2 = stringToDate(dateAdd(nowDate, "d", 1, "yyyy-MM-dd") + " " + time, "yyyy-MM-dd HH:mm:ss");
		}
		return d2.getTime() - d1.getTime();
	}
	
	/**
	 * 从start到end的毫秒数
	 * @param start
	 * @param end
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public static long getTimeDiff(String start, String end, String format) throws Exception {
		if (StringUtils.isEmpty(start) || StringUtils.isEmpty(end) || StringUtils.isEmpty(format)) {
			return 0;
		}
		return stringToDate(end, format).getTime() - stringToDate(start, format).getTime();
	}
	
}
