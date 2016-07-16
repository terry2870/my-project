/* author hp
 * 创建日期 Dec 14, 2010
 */
package com.mytools.utils;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串操作类
 * 
 * @author Administrator
 * 
 */
public class StringUtil {
	
	/**
	 * 用途：检查输入的电话号码格式是否正确 输入： strPhone：字符串 返回： 1：手机号码 2：固话号码 0:非法号码
	 * 
	 * @param strPhone
	 * @return
	 */
	public static int checkPhone(String strPhone) {
		String phoneRegNoArea = "[1][0-9]{10}";
		String phoneRegWithArea2 = "[0][1-9]{2,3}[0-9]{7,8}";
		if (strPhone.matches(phoneRegNoArea)) {
			return 1;
		}
		if (strPhone.matches(phoneRegWithArea2)) {
			return 2;
		}
		return 0;
	}

	/**
	 * eWebEditor使用时，替换一些符号
	 * 
	 * @param str
	 * @return
	 */
	public static String TCode(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		} else {
			str = str.replaceAll(" ", "&nbsp;");
			str = str.replaceAll("\r\n", "<br>");
			str = str.replaceAll("\"", "");
			return str;
		}
	}

	/**
	 * 使传过来的值不为null
	 * 
	 * @param str
	 * @return
	 */
	public static String notNull(String str, String defaultValue) {
		return StringUtils.isEmpty(str) ? defaultValue : str;
	}

	
	/**
	 * 验证是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str){
		return str.matches("\\d+");
	}
	
	/**
	 * 保留指定位数的小数
	 * @param d
	 * @param num
	 * @return
	 */
	public static String getNumber(double d, int num){
		String str = "0.";
		for (int i = 0; i < num; i++) {
			str += "0";
		}
		DecimalFormat df = new DecimalFormat(str);
		return df.format(d);
	}
	
	/**
	 * 替换字符串里面的占位符
	 * @param str 待替换的字符串
	 * @param arr 参数数组
	 * @return 返回替换后的字符串
	 */
	public static String replaceData(String str, String[] arr) {
		if (StringUtils.isEmpty(str) || arr == null || arr.length == 0) {
			return str;
		}
		for (String s : arr) {
			str = str.replaceFirst("\\{\\?\\}", s);
		}
		return str;
	}
	
	/**
	 * 处理JSP中转义字符
	 * @param str
	 * @return
	 */
	public static String transferredForJsp(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (char c : str.toCharArray()) {
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '\'':
				sb.append("&apos;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

}
