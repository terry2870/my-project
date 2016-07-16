package com.mytools.utils;

import org.apache.log4j.Logger;

import java.security.MessageDigest;

/**
 * MD5算法
 * @author huangping<br />
 * 2013-1-28
 */
public class MD5Util {
	private static Logger log = Logger.getLogger(MD5Util.class);

	/**
	 * MD5加密。32位
	 * 
	 * @param s
	 * @return
	 */
	public static String getMD5(String str) {
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString());
			throw new RuntimeException(e);
		}

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}
