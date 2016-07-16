/* author hp
 * 创建日期 Mar 17, 2011
 */
package com.mytools.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mytools.beans.JxlBean;
import com.mytools.beans.JxlBean.JxlColumnWidthBean;
import com.mytools.beans.JxlBean.JxlDataBean;
import com.mytools.beans.JxlBean.JxlImageBean;
import com.mytools.beans.JxlBean.JxlMergeBean;
import com.mytools.beans.JxlBean.JxlSheetBean;

public class ExcelUtil {

	private static Logger log = Logger.getLogger(ExcelUtil.class);

	/**
	 * 读取excel
	 * @param file 文件
	 * @param sheetIndex sheet索引（从0开始）
	 * @return 返回excel内容
	 * @throws Exception
	 */
	public static List<String[]> getListFromExcel(File file, int sheetIndex) throws Exception {
		return getListFromExcel(file, sheetIndex, 0);
	}
	
	/**
	 * 读取excel
	 * @param file 文件
	 * @return 返回excel内容
	 * @throws Exception
	 */
	public static List<String[]> getListFromExcel(File file) throws Exception {
		return getListFromExcel(file, 0, 0);
	}
	
	/**
	 * 读取excel
	 * @param file 文件
	 * @param sheetIndex sheet索引（从0开始）
	 * @param startRow 开始行号（从0开始）
	 * @return 返回excel内容
	 */
	public static List<String[]> getListFromExcel(File file, int sheetIndex, int startRow) throws Exception {
		InputStream in = null;
		List<String[]> list = null;
		try {
			in = new FileInputStream(file);
			list = getListFromExcel(in, sheetIndex, startRow);
			return list;
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 读取excel
	 * @param in InputStream
	 * @param sheetIndex sheet索引（从0开始）
	 * @return 返回excel内容
	 * @throws Exception
	 */
	public static List<String[]> getListFromExcel(InputStream in, int sheetIndex) throws Exception {
		return getListFromExcel(in, sheetIndex, 0);
	}
	
	/**
	 * 读取excel
	 * @param in InputStream
	 * @return 返回excel内容
	 * @throws Exception
	 */
	public static List<String[]> getListFromExcel(InputStream in) throws Exception {
		return getListFromExcel(in, 0, 0);
	}
	
	/**
	 * 读取excel
	 * @param in InputStream
	 * @param sheetIndex sheet索引（从0开始）
	 * @param startRow 开始行号（从0开始）
	 * @return 返回excel内容
	 * @throws Exception
	 */
	public static List<String[]> getListFromExcel(InputStream in, int sheetIndex, int startRow) throws Exception {
		if (in == null) {
			return null;
		}
		Workbook rwb = null;
		List<String[]> list = new ArrayList<String[]>();
		try {
			rwb = Workbook.getWorkbook(in);
			Sheet sheet = rwb.getSheet(sheetIndex);
			Cell[] cellArr = null;
			String[] tmp = null;
			Cell cell = null;
			for (int i = startRow; i < sheet.getRows(); i++) {
				cellArr = sheet.getRow(i);
				tmp = new String[cellArr.length];
				for (int j = 0; j < tmp.length; j++) {
					cell = cellArr[j];
					tmp[j] = cell == null ? "" : cell.getContents();
				}
				list.add(tmp);
			}
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (rwb != null) {
				rwb.close();
			}
		}
	}

	/**
	 * 读取excel
	 * @param fileName 文件名
	 * @param sheetIndex sheet索引（从0开始）
	 * @param startRow 开始行号（从0开始）
	 * @return 返回excel内容
	 * @throws Exception
	 */
	public static List<String[]> getListFromExcel(String fileName, int sheetIndex, int startRow) throws Exception {
		return getListFromExcel(new File(fileName), sheetIndex, startRow);
	}
	
	/**
	 * 读取excel
	 * @param fileName 文件名
	 * @param sheetIndex sheet索引（从0开始）
	 * @return 返回excel内容
	 * @throws Exception
	 */
	public static List<String[]> getListFromExcel(String fileName, int sheetIndex) throws Exception {
		return getListFromExcel(new File(fileName), sheetIndex, 0);
	}
	
	/**
	 * 读取excel
	 * @param fileName 文件名
	 * @return 返回excel内容
	 * @throws Exception
	 */
	public static List<String[]> getListFromExcel(String fileName) throws Exception {
		return getListFromExcel(new File(fileName), 0, 0);
	}

	public static void main(String[] args) {
		String f = "D:/1/recovery Odin刷入包.part2.rar";
		File file = new File(f);
		System.out.println(file.getName());
		System.out.println(file.getPath());
		System.out.println(file.getAbsolutePath());
		System.out.println(file.getParent());
	}
	
	/**
	 * 根据excel模板，生成excel
	 * @param bean excel对象
	 * @throws Exception
	 */
	public static void createExcelFromTemplate(JxlBean bean) throws Exception {
		if (bean == null) {
			return;
		}
		Workbook templateBook = null;
		WritableWorkbook workBook = null;
		WritableSheet sheet = null;
		try {
			templateBook = Workbook.getWorkbook(bean.getTemplateFile());
			if (bean.getOutFile() != null) {
				FileUtil.createFolder(bean.getOutFile().getParent());
				workBook = Workbook.createWorkbook(new FileOutputStream(bean.getOutFile()), templateBook);
			} else if (bean.getOut() != null) {
				workBook = Workbook.createWorkbook(bean.getOut(), templateBook);
			}
			if (bean.getSheetList() == null || bean.getSheetList().length == 0) {
				return;
			}
			for (JxlSheetBean sheet1 : bean.getSheetList()) {
				sheet = workBook.getSheet(sheet1.getSheetIndex());
				create(sheet1, sheet);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (workBook != null) {
				workBook.write();
				workBook.close();
			}
			if (templateBook != null) {
				templateBook.close();
			}
		}
	}

	/**
	 * 不需要模板，直接生成excel
	 * @param bean excel对象
	 * @throws Exception
	 */
	public static void createExcel(JxlBean bean) throws Exception {
		if (bean == null) {
			return;
		}
		WritableWorkbook workBook = null;
		WritableSheet sheet = null;
		try {
			WorkbookSettings ws = new WorkbookSettings();
			ws.setWriteAccess("");
			if (bean.getOutFile() != null) {
				FileUtil.createFolder(bean.getOutFile().getParent());
				workBook = Workbook.createWorkbook(new FileOutputStream(bean.getOutFile()), ws);
			} else if (bean.getOut() != null) {
				workBook = Workbook.createWorkbook(bean.getOut(), ws);
			}
			if (bean.getSheetList() == null) {
				return;
			}
			for (JxlSheetBean sheet1 : bean.getSheetList()) {
				if (sheet1 == null || sheet1.getDataList() == null) {
					continue;
				}
				sheet = workBook.createSheet(sheet1.getSheetName(), sheet1.getSheetIndex());
				create(sheet1, sheet);
			}
			workBook.write();
		} catch (Exception e) {
			throw e;
		} finally {
			if (workBook != null) {
				workBook.close();
			}
		}
	}
	
	/**
	 * 生成Excel
	 * @param sheet1
	 * @param sheet
	 * @throws Exception
	 */
	private static void create(JxlSheetBean sheet1, WritableSheet sheet) throws Exception {
		WritableCell cell = null;
		CellFormat format = null;
		if (!StringUtils.isEmpty(sheet1.getSheetName())) {
			sheet.setName(sheet1.getSheetName());
		}
		if (sheet1.getInsertRowList() != null && sheet1.getInsertRowList().length > 0) {
			for (int rowIndex : sheet1.getInsertRowList()) {
				sheet.insertRow(rowIndex);
			}
		}
		if (sheet1.getMergeList() != null && sheet1.getMergeList().length > 0) {
			for (JxlMergeBean merge : sheet1.getMergeList()) {
				sheet.mergeCells(merge.getStartColumn(), merge.getStartRow(), merge.getEndColumn(), merge.getEndRow());
			}
		}
		if (sheet1.getDataList() != null && sheet1.getDataList().length > 0) {
			for (JxlDataBean data : sheet1.getDataList()) {
				if (!StringUtils.isEmpty(data.getPosition())) {
					cell = sheet.getWritableCell(data.getPosition());
				} else {
					cell = sheet.getWritableCell(data.getColumn(), data.getRow());
				}
				format = data.getFormat();
				if (format == null) {
					format = cell.getCellFormat();
				}
				if (format == null) {
					format = getDefaultFormat();
				}
				sheet.addCell(new Label(cell.getColumn(), cell.getRow(), data.getContent(), format));
			}
		}
		if (sheet1.getColumnWidthList() != null) {
			for (JxlColumnWidthBean columnWidthBean : sheet1.getColumnWidthList()) {
				sheet.setColumnView(columnWidthBean.getColumn(), columnWidthBean.getWidth());
			}
		}
		if (sheet1.getImageList() != null && sheet1.getImageList().length > 0) {
			WritableImage ri = null;
			for (JxlImageBean image : sheet1.getImageList()) {
				ri = new WritableImage(image.getStartColumn(), image.getStartRow(), image.getColumnWidth(), image.getRowHeight(), image.getImageFile());
				sheet.addImage(ri);
			}
		}
	}

	/**
	 * 获取默认的单元格式
	 * @return
	 * @throws Exception
	 */
	public static WritableCellFormat getDefaultFormat() throws Exception {
		WritableFont font = null;
		font = new WritableFont(WritableFont.TAHOMA);
		font.setPointSize(WritableFont.DEFAULT_POINT_SIZE);
		font.setColour(Colour.BLACK);
		font.setBoldStyle(WritableFont.NO_BOLD);
		font.setItalic(false);
		WritableCellFormat format = new WritableCellFormat(font);
		format.setAlignment(Alignment.CENTRE);
		format.setVerticalAlignment(VerticalAlignment.CENTRE);
		format.setBackground(Colour.WHITE);
		format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
		format.setWrap(false);
		return format;
	}

	/**
	 * 简单的生成Excel
	 * @param data 数据
	 * @param headLines excel的第一行（头）
	 * @param fileName 生成的文件名
	 * @param response 响应对象
	 * @throws IOException
	 */
	public static void simpleCreateExcel(List<Object[]> data, String[] headLines, String fileName, HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.ms-excel; charset=UTF-8");
		response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("GBK"), "ISO-8859-1") + ".xls");
		// response.setHeader("Content-disposition", "attachment; filename=" +
		// URLEncoder.encode(fileName, "UTF-8") + ".xls");
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		sb.append("<style>");
		sb.append("td {font-size: 12px; line-height: 160%; font-family: 宋体;}");
		sb.append(".headLine{font-size: 20px; font-weight: bold; font-family: 宋体;background-color: #c0d2ec;}");
		sb.append("</style>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<table border=\"0\" width=\"100%\">");
		sb.append("<tr align=\"center\">");
		sb.append("<td class=\"headLine\">").append(fileName).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><table border=\"1\" width=\"100%\">");
		if (data != null) {
			for (Object[] obj1 : data) {
				sb.append("<tr align=\"center\">");
				for (Object obj : obj1) {
					sb.append("<td style=\"vnd.ms-excel.numberformat:@\">").append(obj.toString()).append("</td>");
				}
				sb.append("</tr>");
			}
		}
		sb.append("</table></td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		response.getWriter().print(sb.toString());
	}

}
