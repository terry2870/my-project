package com.mytools.utils;

import org.apache.commons.lang3.StringUtils;



/* author hp
 * 创建日期 May 30, 2011
 */
public class DBUtil {

	/**
	 * 将数据库中的字段名转换成po类中的对应的样式 例如，将POINTS_TOTAL_NUM转成pointsTotalNum
	 * 
	 * @param columnName
	 * @return
	 */
	public static String formatColumn(String columnName) {
		return formatColumn(columnName, true);
	}

	/**
	 * 将数据库中的字段名转换成po类中的对应的样式
	 * 例如当isTransformColumn=true时，将POINTS_TOTAL_NUM转成pointsTotalNum
	 * 
	 * @param columnName
	 * @param formatColumn
	 * @return
	 */
	public static String formatColumn(String columnName, boolean formatColumn) {
		if (StringUtils.isEmpty(columnName)) {
			return columnName;
		}
		columnName = columnName.toLowerCase();// 将字段名全部转换成小写
		if (!formatColumn) {
			return columnName;
		}
		if (columnName.indexOf("_") < 0) {
			return columnName;
		}
		StringBuilder sb = new StringBuilder();
		String[] arr = columnName.split("[_]");
		for (int i = 0; i < arr.length; i++) {
			if (i == 0) {
				sb.append(arr[0]);
			} else {
				sb.append(arr[i].substring(0, 1).toUpperCase()).append(arr[i].substring(1));
			}
		}
		return sb.toString();
	}
	
	/**
	 * 将对象的字段转换为数据库的字段
	 * @param column
	 * @param formatColumn
	 * @return
	 */
	public static String unFormatColumn(String column, boolean formatColumn) {
		if (StringUtils.isEmpty(column)) {
			return column;
		}
		if (!formatColumn) {
			return column;
		}
		String[] s = column.split("");
		StringBuilder sb = new StringBuilder();
		String reg = "[A-Z]";
		for (int i = 0; i < s.length; i++) {
			if (s[i].matches(reg)) {
				sb.append("_");
			}
			sb.append(s[i].toLowerCase());
		}
		return sb.toString();
	}

}
