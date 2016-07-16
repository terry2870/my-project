/*
 * 作者：黄平
 * 
 */
package com.mytools.beans;

public abstract class BaseTagBean {

	protected String id;
	protected String name;
	protected String value;
	protected String className;
	protected String style;
	protected String event;
	protected String size;

	protected String src;
	protected String align;
	protected boolean readonly;

	protected String height;
	protected String width;
	protected String title;
	protected boolean disabled;
	protected boolean checked;
	protected boolean selected;

	protected String label;

	public abstract String toString();

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
