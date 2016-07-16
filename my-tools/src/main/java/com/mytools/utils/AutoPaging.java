/*
 * 作者：黄平
 * 
 */
package com.mytools.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.mytools.beans.ButtonBean;
import com.mytools.beans.PageBean;
import com.mytools.beans.TextBean;

public class AutoPaging {

	public static final String PAGE_SIZE = "myToolsPageSize";
	public static final String CURRENT_PAGE = "myToolsCurrentPage";
	public static final String ORDER_BY = "myToolsOrder";
	public static final String SORT = "myToolsSort";


	/**
	 * 取的分页的按钮
	 * @param page
	 * @param request
	 * @return
	 */
	public static String getPageButton(PageBean page, HttpServletRequest request, String formName) {
		if (StringUtils.isEmpty(formName)) {
			formName = "f1";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<input type='hidden' name='").append(CURRENT_PAGE).append("' id='").append(CURRENT_PAGE).append("' value='").append(page.getCurrentPage()).append("'>\r\n");
		sb.append("<input type='hidden' name='" + PAGE_SIZE + "' id='" + PAGE_SIZE + "' value='").append(page.getPageSize()).append("'>\r\n");
		sb.append("<input type='hidden' name='" + ORDER_BY + "' id='" + ORDER_BY + "' value='").append(page.getOrderBy()).append("'>\r\n");
		sb.append("<input type='hidden' name='" + SORT + "' id='" + SORT + "' value='").append(page.getSort()).append("'>\r\n");
		sb.append("<table border='0' cellpadding='0' cellspacing='0'><tr><td>\r\n");
//		sb.append("<select onchange='gotoPage(this.value, " + page.getPageSize() + ")'>\r\n");
//		for (int i = 1; i <= page.getMaxPage(); i++) {
//			if (i == page.getCurrentPage()) {
//				sb.append("<option value='" + i + "' selected>" + i + "</option>\r\n");
//			} else {
//				sb.append("<option value='" + i + "'>" + i + "</option>\r\n");
//			}
//		}
//		sb.append("</select>&nbsp;\r\n");
		
		if (page.getCurrentPage() <= 1) {
			sb.append("<input type='button' class='Page_Navigate_class_button' value='首页'>&nbsp;\r\n");
		} else {
			sb.append("<input type='button' class='Page_Navigate_class_button' value='首页' onClick='gotoPage(1, " + page.getPageSize() + ");'>&nbsp;\r\n");
		}
		if (page.getCurrentPage() <= 1) {
			sb.append("<input type='button' class='Page_Navigate_class_button' value='上一页' >&nbsp;\r\n");
		} else {
			sb.append("<input type='button' class='Page_Navigate_class_button' value='上一页' onClick='gotoPage(" + (page.getCurrentPage() - 1) + ", " + page.getPageSize() + ");'>&nbsp;\r\n");
		}
		if (page.getCurrentPage() >= page.getMaxPage()) {
			sb.append("<input type='button' class='Page_Navigate_class_button' value='下一页' >&nbsp;\r\n");
		} else {
			sb.append("<input type='button' class='Page_Navigate_class_button' value='下一页' onClick='gotoPage(" + (page.getCurrentPage() + 1) + ", " + page.getPageSize() + ");'>&nbsp;\r\n");
		}
		if (page.getCurrentPage() >= page.getMaxPage()) {
			sb.append("<input type='button' class='Page_Navigate_class_button' value='末页' >&nbsp;\r\n");
		} else {
			sb.append("<input type='button' class='Page_Navigate_class_button' value='末页' onClick='gotoPage(" + page.getMaxPage() + ", " + page.getPageSize() + ");'>&nbsp;\r\n");
		}
		sb.append(page.getCurrentPage()).append("/").append(page.getMaxPage()).append("(" + page.getTotalCount() + ")");
		sb.append("<input type='text' size='1' name='changePageSizeText' id='changePageSizeText' value='").append(page.getCurrentPage()).append("' />&nbsp;");
		sb.append("<div class='gotopage' onclick='changePage();' title='跳转到该页'></div>");
		//sb.append("<input type='button' style='cursor:pointer' onclick='showChangePageSizeDiv(event);' value='每页" + page.getPageSize() + "条' />\r\n");
		sb.append("</td></tr></table>\r\n");
		sb.append("<script>\r\n");
		sb.append("function changePage(){\r\n");
		sb.append("	var changePageSizeText = document.getElementById('changePageSizeText');\r\n");
		sb.append("	var reg = /^[0-9]+$/;\r\n");
		sb.append("	if(!reg.test(changePageSizeText.value)){\r\n");
		sb.append("		alert('请输入整数！');\r\n");
		sb.append("		return;\r\n");
		sb.append("	}\r\n");
		sb.append("	if(changePageSizeText.value < '1' || changePageSizeText.value == '").append(page.getCurrentPage()).append("' || changePageSizeText.value > '").append(page.getMaxPage()).append("'){\r\n");
		sb.append("		return;\r\n");
		sb.append("	}\r\n");
		sb.append("	gotoPage(changePageSizeText.value, ").append(page.getPageSize()).append(");\r\n");
		sb.append("}\r\n");
		sb.append("function gotoPage(pageIndex, rowPerPages){\r\n");
		sb.append("	document.getElementById('" + CURRENT_PAGE + "').value = pageIndex;\r\n");
		sb.append("	document.getElementById('" + PAGE_SIZE + "').value = rowPerPages;\r\n");
		sb.append("	document.getElementById('" + ORDER_BY + "').value = '" + page.getOrderBy() + "';\r\n");
		sb.append("	document.getElementById('" + SORT + "').value = '" + formName + "';\r\n");
		if (StringUtils.isEmpty(formName)) {
			sb.append("	document.forms[0].submit();\r\n");
		} else {
			sb.append("	document.getElementById('" + formName + "').submit();\r\n");
		}
		sb.append("}\r\n");
		sb.append("function showChangePageSizeDiv(ev){\r\n");
		StringBuffer divContent = new StringBuffer();
		divContent.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" height=\"50\">");
		divContent.append("<tr>");
		divContent.append("<td>");
		TextBean text = new TextBean();
		text.setName("newPageSize");
		text.setId("newPageSize");
		text.setSize("3");
		text.setMaxLength("3");
		text.setValue(String.valueOf(page.getPageSize()));
		ButtonBean button = new ButtonBean();
		button.setValue("确定");
		button.setClassName("Page_Navigate_class_button");
		button.setEvent("onclick=\"changePageSize();\"");
		button.setStyle("float:right;margin-right:10px;");
		divContent.append("<span class=\"Page_Navigate_class_text\">每页显示的条数：</span>").append(text.toString()).append(button.toString());
		divContent.append("</td>");
		divContent.append("</tr>");
		divContent.append("</table>");
		sb.append("	openCoverDiv('修改每页显示条数', '").append(divContent.toString()).append("', ev.clientX - 280, ev.clientY - 80, 260, 100, true);\r\n");
		sb.append("}\r\n");
		sb.append("function changePageSize(){\r\n");
		sb.append("	var num = document.getElementById('newPageSize').value;\r\n");
		sb.append("	var reg = /^[0-9]+$/;\r\n");
		sb.append("	if(num != null && !reg.test(num)){\r\n");
		sb.append("		alert('每页显示条数必须是正整数！');\r\n");
		sb.append("		return;\r\n");
		sb.append("	}\r\n");
		sb.append("	if(num != " + page.getPageSize() + "){\r\n");
		sb.append("		gotoPage(1, num);\r\n");
		sb.append("	}\r\n");
		sb.append("}\r\n");
		sb.append("function orderBySort(orderByColumn){\r\n");
		sb.append("	document.getElementById('" + ORDER_BY + "').value = orderByColumn;\r\n");
		sb.append("	if('" + page.getSort() + "' == 'ASC'){\r\n");
		sb.append("		document.getElementById('" + SORT + "').value = 'DESC';\r\n");
		sb.append("	}else{\r\n");
		sb.append("		document.getElementById('" + SORT + "').value = 'ASC';\r\n");
		sb.append("	}\r\n");
		sb.append("	document.getElementById('" + CURRENT_PAGE + "').value = 1;\r\n");
		sb.append("	document.getElementById('" + PAGE_SIZE + "').value = " + page.getPageSize() + ";\r\n");
		if (StringUtils.isEmpty(formName)) {
			sb.append("	document.forms[0].submit();\r\n");
		} else {
			sb.append("	document.getElementById('" + formName + "').submit();\r\n");
		}
		sb.append("}\r\n");
		sb.append("</script>\r\n");
		return sb.toString();
	}

}
