/*
 * 作者：黄平
 * 
 */
package com.mytools.beans;

import org.apache.commons.lang3.StringUtils;

/**
 * 生成一个文本输入框<input type="text">
 * 
 * @author hp
 * 
 */
public class TextBean extends BaseTagBean {

	private String maxLength;
	private String type = "text";

	public String toString() {
		StringBuffer sb = new StringBuffer("<input type=\"").append(this.type).append("\"");
		if (!StringUtils.isEmpty(id)) {
			sb.append(" id=\"").append(this.id).append("\"");
		}
		if (!StringUtils.isEmpty(this.name)) {
			sb.append(" name=\"").append(this.name).append("\"");
		}
		if (!StringUtils.isEmpty(this.value)) {
			sb.append(" value=\"").append(this.value).append("\"");
		}
		if (!StringUtils.isEmpty(this.className)) {
			sb.append(" class=\"").append(this.className).append("\"");
		}
		if (!StringUtils.isEmpty(this.style)) {
			sb.append(" style=\"").append(this.style).append("\"");
		}
		if (!StringUtils.isEmpty(this.size)) {
			sb.append(" size=\"").append(this.size).append("\"");
		}
		if (!StringUtils.isEmpty(this.maxLength)) {
			sb.append(" maxlength=\"").append(this.maxLength).append("\"");
		}
		if (!StringUtils.isEmpty(this.event)) {
			sb.append(" ").append(this.event);
		}
		if (this.readonly) {
			sb.append(" readonly");
		}
		if (this.disabled) {
			sb.append(" disabled");
		}
		sb.append(" />");
		return sb.toString();
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public void setType(String type) {
		this.type = type;
	}
}
