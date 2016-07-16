package com.mytools.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

/**
 * 写文件的工具
 * @author huangping<br />
 * 2013-1-18
 */
public class WriteFileUtil {

	static Logger log = Logger.getLogger(WriteFileUtil.class);
	
	public static void writeTxt(String logFileName, String content) throws Exception {
		writeTxt(logFileName, content, true, true, "UTF-8");
	}
	
	/**
	 * 写文件
	 * @param logFileName
	 * @param content
	 * @param append 是否追加
	 * @param newLine 写完后是否新建一行
	 * @param encoding
	 * @throws Exception
	 */
	public static void writeTxt(String logFileName, String content, boolean append, boolean newLine, String encoding) throws Exception {
		OutputStreamWriter out = null;
		BufferedWriter writer = null;
		try {
			logFileName = logFileName.replace("\\", "/");
			if (logFileName.indexOf("/") >= 0) {
				FileUtil.createFolder(logFileName.substring(0, logFileName.lastIndexOf("/")));
			}
			out = new OutputStreamWriter(new FileOutputStream(new File(logFileName), append), encoding);
			writer = new BufferedWriter(out);
			writer.write(content);
			if (newLine) {
				writer.newLine();
			}
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			if (out != null) {
				out.flush();
			}
			if (writer != null) {
				writer.flush();
			}
			if (out != null) {
				out.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

}
