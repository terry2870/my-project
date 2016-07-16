package com.mytools.utils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author huangping<br />
 *         2013-9-29
 */
public class OutputStreamUtil {

	/**
	 * base64加密字符串转为OutputStream
	 * @param base64
	 * @return
	 * @throws Exception
	 */
	public static OutputStream base642OutputStream(String base64) throws Exception {
		OutputStream out = null;
		out = new ByteArrayOutputStream();
		out.write(Base64.decodeBase64(base64));
		out.flush();
		return out;
	}

}
