/*
 * 作者：黄平
 * 
 */
package com.mytools.beans;

import org.apache.commons.lang3.StringUtils;

/**
 * 生成一个按钮
 * 
 * @author hp
 * 
 */
public class ButtonBean extends BaseTagBean {

	private String type = "button";

	public String toString() {
		StringBuffer sb = new StringBuffer("<input type=\"").append(this.type).append("\"");
		if (this.type.equals("image")) {
			if (!StringUtils.isEmpty(this.src)) {
				sb.append(" src=\"").append(this.src).append("\"");
			}
			if (!StringUtils.isEmpty(this.height)) {
				sb.append(" height=\"").append(this.height).append("\"");
			}
			if (!StringUtils.isEmpty(this.width)) {
				sb.append(" width=\"").append(this.width).append("\"");
			}
			if (!StringUtils.isEmpty(this.align)) {
				sb.append(" align=\"").append(this.align).append("\"");
			}
		}
		if (!StringUtils.isEmpty(this.id)) {
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

	public void setType(String type) {
		this.type = type;
	}

}
