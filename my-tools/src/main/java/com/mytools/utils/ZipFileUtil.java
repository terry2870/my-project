/* author hp
 * 创建日期 Oct 12, 2011
 */
package com.mytools.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class ZipFileUtil {

	static Logger log = Logger.getLogger(ZipFileUtil.class);
	private static final String BASE_DIR = "";
	// 符号"/"用来作为目录标识判断符
	private static final String PATH = "/";
	private static final int BUFFER = 1024;

	/**
	 * 压缩文件或目录
	 * @param srcFileName
	 * @param destFileName
	 */
	public static void compressFile(String srcFileName, String destFileName) {
		File file = new File(srcFileName);
		if (!file.exists()) {
			return;
		}
		CheckedOutputStream cos = null;
		ZipOutputStream zos = null;
		try {
			cos = new CheckedOutputStream(new FileOutputStream(destFileName), new CRC32());
			zos = new ZipOutputStream(cos);
			compress(file, zos, BASE_DIR);

		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				if (zos != null) {
					zos.flush();
				}
				if (cos != null) {
					cos.flush();
				}
				if (zos != null) {
					zos.close();
				}
				if (cos != null) {
					cos.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	private static void compress(File srcFile, ZipOutputStream zos, String basePath) throws Exception {
		if (srcFile.isDirectory()) {
			compressDir(srcFile, zos, basePath);
		} else {
			compressFile(srcFile, zos, basePath);
		}
	}

	private static void compressDir(File dir, ZipOutputStream zos, String basePath) throws Exception {
		File[] files = dir.listFiles();
		// 构建空目录
		if (files.length < 1) {
			ZipEntry entry = new ZipEntry(basePath + dir.getName() + PATH);
			zos.putNextEntry(entry);
			zos.closeEntry();
		}
		for (File file : files) {
			// 递归压缩
			compress(file, zos, basePath + dir.getName() + PATH);
		}
	}

	private static void compressFile(File file, ZipOutputStream zos, String dir) throws Exception {
		ZipEntry entry = new ZipEntry(dir + file.getName());
		zos.putNextEntry(entry);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		int count;
		byte data[] = new byte[BUFFER];
		while ((count = bis.read(data, 0, BUFFER)) != -1) {
			zos.write(data, 0, count);
		}
		bis.close();
		zos.closeEntry();
	}
	
	public static void main(String[] args) {
		compressFile("C:/1.txt", "C:/大都督.zip");
	}

}
