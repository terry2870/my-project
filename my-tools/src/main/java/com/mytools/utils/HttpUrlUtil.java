/* author hp
 * 创建日期 Sep 16, 2011
 */
package com.mytools.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlUtil {

	/**
	 * 给远程URL传送文件
	 * 
	 * @param fileName
	 * @param connectUrl
	 * @throws Exception
	 */
	public static String uploadFiles(String fileName, String connectUrl) throws Exception {
		return uploadFiles(fileName, connectUrl, "UTF-8");
	}

	/**
	 * 
	 * @param fileName
	 * @param connectUrl
	 * @param charset
	 * @throws Exception
	 */
	public static String uploadFiles(String fileName, String connectUrl, String charset) throws Exception {
		URL url = new URL(connectUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 发送POST请求必须设置如下两行
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Charsert", charset);
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------");
		OutputStream out = conn.getOutputStream();
		File file = new File(fileName);
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		out.flush();
		in.close();
		out.close();
		// 定义BufferedReader输入流来读取URL的响应
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
}
