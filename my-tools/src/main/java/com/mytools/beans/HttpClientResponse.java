package com.mytools.beans;

import org.apache.http.Header;

/**
 * @author hp
 * 2012-11-13
 */
public class HttpClientResponse {

	private int status;
	private String result;
	private Header[] header;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Header[] getHeader() {
		return header;
	}
	public void setHeader(Header[] header) {
		this.header = header;
	}
	
}
