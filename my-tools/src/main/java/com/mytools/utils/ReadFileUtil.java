package com.mytools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 读文件工具类
 * @author huangping<br />
 * 2013-1-18
 */
public class ReadFileUtil {

	static Logger log = Logger.getLogger(ReadFileUtil.class);
	
	/**
	 * 读取文件的行数
	 * @param fileName 文件名
	 * @return
	 */
	public static long getFileTotalLine(String fileName) {
		return getFileTotalLine(new File(fileName));
	}
	
	/**
	 * 读取文件的行数
	 * @param file 文件
	 * @return
	 */
	public static long getFileTotalLine(File file) {
		long lines = 0;
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		try {
			if (file.isFile() && file.exists()) {
				read = new InputStreamReader(new FileInputStream(file));
				bufferedReader = new BufferedReader(read);
				while (bufferedReader.readLine() != null) {
					lines++;
				}
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (IOException e1) {
					log.error("", e1);
				}
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e1) {
					log.error("", e1);
				}
			}
		}
		return lines;
	}
	
	/**
	 * 读取文件
	 * @param file 文件
	 * @return 读取的内容
	 */
	public static List<String> readFileByLines(File file) {
		return readFileByLines(file, true, "UTF-8", 0, 0);
	}
	
	/**
	 * 读取文件
	 * @param file 文件
	 * @param start 开始行数
	 * @param end 结束行数
	 * @return 读取的内容
	 */
	public static List<String> readFileByLines(File file, long start, long end) {
		return readFileByLines(file, true, "UTF-8", start, end);
	}
	
	/**
	 * 读取文件
	 * @param fileName 文件名称
	 * @return 读取的内容
	 */
	public static List<String> readFileByLines(String fileName) {
		return readFileByLines(new File(fileName), true, "UTF-8", 0, 0);
	}
	
	/**
	 * 读取文件
	 * @param fileName 文件名称
	 * @param encoding 字符集
	 * @return 读取的内容
	 */
	public static List<String> readFileByLines(String fileName, String encoding) {
		return readFileByLines(new File(fileName), true, encoding, 0, 0);
	}
	
	/**
	 * 读取文件
	 * @param fileName 文件名称
	 * @param start 开始行数
	 * @param end 结束行数
	 * @return 读取的内容
	 */
	public static List<String> readFileByLines(String fileName, long start, long end) {
		return readFileByLines(new File(fileName), true, "UTF-8", start, end);
	}
	
	/**
	 * 读取文件
	 * @param file 文件
	 * @param allowSame 是否允许相同的行数
	 * @param encoding 字符集
	 * @param start 开始行数
	 * @param end 结束行数
	 * @return
	 */
	public static List<String> readFileByLines(File file, boolean allowSame, String encoding, long start, long end) {
		try {
			return readFileByLines(new FileInputStream(file), allowSame, encoding, start, end);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * 读文件
	 * @param in 文件流
	 * @param encoding 编码
	 * @return
	 */
	public static List<String> readFileByLines(InputStream in, String encoding) {
		return readFileByLines(in, true, encoding, 0, 0);
	}
	
	/**
	 * 读取文件
	 * @param in 文件流
	 * @param allowSame 是否允许相同的行数
	 * @param encoding 字符集
	 * @param start 开始行数
	 * @param end 结束行数
	 * @return
	 */
	public static List<String> readFileByLines(InputStream in, boolean allowSame, String encoding, long start, long end) {
		List<String> list = new ArrayList<String>();
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		try {
			if (in != null) {
				read = new InputStreamReader(in, encoding);
				bufferedReader = new BufferedReader(read);
				String lineTXT = null;
				long i = 0;
				while ((lineTXT = bufferedReader.readLine()) != null) {
					if (start > i) {
						i++;
						continue;
					}
					if (end > 0 && end < i) {
						i++;
						break;
					}
					i++;
					if (!allowSame && list.contains(lineTXT)) {
						continue;
					} else {
						list.add(lineTXT);
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (IOException e1) {
					log.error("", e1);
				}
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e1) {
					log.error("", e1);
				}
			}
		}
		return list;
	}
	
	/**
	 * 读取文件
	 * @param fileName
	 * @param encoding
	 * @return
	 */
	public static String readFile(String fileName, String encoding) {
		return readFile(new File(fileName), encoding);
	}
	
	/**
	 * 读取文件
	 * @param file
	 * @param encoding
	 * @return
	 */
	public static String readFile(File file, String encoding) {
		String str = null;
		try {
			str = readFile(new FileInputStream(file), encoding);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return str;
	}
	
	/**
	 * 读取文件
	 * @param in
	 * @param encoding
	 * @return
	 */
	public static String readFile(InputStream in, String encoding) {
		StringBuilder sb = new StringBuilder();
		try {
			int BUFFER_LENGTH = 1024 * 20;
			byte[] b = new byte[BUFFER_LENGTH];
			int i = 0;
			while ((i = in.read(b)) != -1) {
				if (i != BUFFER_LENGTH) {
					sb.append(new String(b, 0, i, encoding));
				} else {
					sb.append(new String(b, encoding));
				}
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			
		}
		return sb.toString();
	}
}
