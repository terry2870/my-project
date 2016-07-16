package com.mytools.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件操作工具
 * 
 * @author huangping<br />
 * 2013-1-18
 */
public final class FileUtil {

	/**
	 * 获得文件扩展名
	 * @param name
	 * @return
	 */
	public static String getFileExtName(String name) {
		if (StringUtils.isEmpty(name)) {
			return "";
		} else {
			name = name.trim();
			return name.substring(name.lastIndexOf('.') + 1);
		}
	}

	/**
	 * 获得文件名（不包括后缀）
	 * @param name
	 * @return
	 */
	public static String getFileName(String name) {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		if (name.indexOf(".") < 0) {
			return name;
		}
		return name.substring(0, name.indexOf("."));
	}

	/**
	 * 删除文件
	 * @param filePath
	 */
	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return;
		}
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				deleteFile(f.getPath());
			}
			file.delete();
		}
	}

	/**
	 * 下载文件
	 * @param response
	 * @param inputFileName
	 * @throws Exception
	 */
	public static void downloadFile(HttpServletResponse response, String inputFileName) throws Exception {
		downloadFile(response, inputFileName, inputFileName.substring(inputFileName.lastIndexOf("/") + 1));
	}
	
	/**
	 * 下载文件
	 * @param response
	 * @param inputFileName
	 * @param outputFileName
	 * @throws Exception
	 */
	public static void downloadFile(HttpServletResponse response, String inputFileName, String outputFileName) throws Exception {
		response.setContentType("application/x-download");
		outputFileName = URLEncoder.encode(outputFileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename=" + outputFileName);
		OutputStream outp = null;
		FileInputStream in = null;
		try {
			outp = response.getOutputStream();
			in = new FileInputStream(inputFileName);
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = in.read(b)) > 0) {
				outp.write(b, 0, i);
			}
			outp.flush();
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
			if (outp != null) {
				outp.flush();
				outp.close();
				outp = null;
			}
		}
	}

	/**
	 * 复制文件
	 * @param fromPathName
	 * @param toPath
	 * @param toFileName
	 * @return
	 * @throws Exception
	 */
	public static boolean copyFile(String fromPathName, String toPath, String toFileName) throws Exception {
		boolean flag = false;
		File fromFile = new File(fromPathName);
		InputStream in = null;
		OutputStream out = null;
		try {
			if (fromFile.exists()) {
				createFolder(toPath);
				File toFile = new File(toPath + toFileName);
				in = new FileInputStream(fromFile);
				out = new FileOutputStream(toFile);
				int len = 0;
				byte[] b = new byte[1024];
				while ((len = in.read(b)) != -1) {
					out.write(b, 0, len);
				}
			}
		} finally {
			if (out != null) {
				out.flush();
			}
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		return flag;
	}

	/**
	 * 判断目录是否存在，如果不存在就创建
	 * @param path
	 */
	public static void createFolder(String path) {
		File dirPath = new File(path);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
	}
	
	/**
	 * 字节数组转为文件
	 * @param bfile
	 * @param fileName
	 */
	public static void byte2File(byte[] bfile, String fileName) throws Exception {
		OutputStream out = null;
		try {
			int lastPathNum = fileName.lastIndexOf("\\");
			if (lastPathNum < 0) {
				lastPathNum = fileName.lastIndexOf("/");
			}
			String filePath = fileName.substring(lastPathNum + 1);
			createFolder(filePath);
			out = new FileOutputStream(fileName);
			out.write(bfile);
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	/**
	 * 文件转为字节数组
	 * @param filePath
	 * @return
	 */
	public static byte[] file2Byte(String filePath) throws Exception {
		byte[] b = null;
		InputStream in = null;
		try {
			File file = new File(filePath);
			in = new FileInputStream(file);
			b = new byte[in.available()];
			in.read(b);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return b;
	}
	
	/**
	 * 文件转为base64加密字符串
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static String file2Base64(String filePath) throws Exception {
		return InputStreamUtil.inputStream2Base64(new FileInputStream(filePath));
	}
	
	/**
	 * base64加密字符串转为文件
	 * @param str
	 * @param fileName
	 */
	public static void base642File(String str, String fileName) throws Exception {
		OutputStream out = null;
		try {
//			int lastPathNum = fileName.lastIndexOf("\\");
//			if (lastPathNum < 0) {
//				lastPathNum = fileName.lastIndexOf("/");
//			}
			//String filePath = fileName.substring(lastPathNum + 1);
			//createFolder(filePath);
			out = new FileOutputStream(fileName);
			out.write(Base64.decodeBase64(str));
			//out.write(new BASE64Decoder().decodeBuffer(str));
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	/**
	 * 获取WEB项目的根路径
	 * @return
	 */
	public static String getWebPath() throws Exception {
		String p = FileUtil.class.getResource("/").getPath();
		return new File(p).getParentFile().getParentFile().getCanonicalPath();
	}
}
