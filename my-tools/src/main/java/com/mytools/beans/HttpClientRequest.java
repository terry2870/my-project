/* author hp
 * 创建日期 Sep 27, 2011
 */
package com.mytools.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.mytools.enums.CharSetEnum;

public class HttpClientRequest {

	private String url;
	private Map<String, Object> map;
	private int socketTimeout = 5000;
	private int connectTimeout = 5000;
	private List<FileParam> fileList;
	private String requestCharSet = CharSetEnum.UTF_8.getValue();
	private String responseCharSet = CharSetEnum.UTF_8.getValue();
	private String body;
	private List<Header> headerList = new ArrayList<Header>();

	public HttpClientRequest() {

	}

	public HttpClientRequest(String url) {
		setUrl(url);
	}

	public HttpClientRequest(String url, Map<String, Object> map) {
		setUrl(url);
		setMap(map);
	}

	public void addHeader(Header header) {
		headerList.add(header);
	}
	
	public void addHeader(String key, String value) {
		headerList.add(new BasicHeader(key, value));
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	public List<FileParam> getFileList() {
		return this.fileList;
	}

	public void setFileList(List<FileParam> fileList) {
		this.fileList = fileList;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getMap() {
		return this.map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public static class FileParam {
		private File file;
		private String jspName;
		private String fileName;
		private String contentType = "text/plain";
		private String charset;

		public FileParam() {

		}

		public FileParam(String jspName, String fileName) {
			setJspName(jspName);
			setFileName(fileName);
		}

		public FileParam(String jspName, String fileName, String charset) {
			setJspName(jspName);
			setFileName(fileName);
			setCharset(charset);
		}

		public FileParam(String jspName, File file) {
			setJspName(jspName);
			setFile(file);
		}

		public FileParam(String jspName, File file, String charset) {
			setJspName(jspName);
			setFile(file);
			setCharset(charset);
		}

		public String getContentType() {
			return this.contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		public String getCharset() {
			return this.charset;
		}

		public void setCharset(String charset) {
			this.charset = charset;
		}

		public String getJspName() {
			return this.jspName;
		}

		public void setJspName(String jspName) {
			this.jspName = jspName;
		}

		public String getFileName() {
			return this.fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public File getFile() {
			return this.file;
		}

		public void setFile(File file) {
			this.file = file;
		}
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public String getRequestCharSet() {
		return requestCharSet;
	}

	public void setRequestCharSet(String requestCharSet) {
		this.requestCharSet = requestCharSet;
	}

	public String getResponseCharSet() {
		return responseCharSet;
	}

	public void setResponseCharSet(String responseCharSet) {
		this.responseCharSet = responseCharSet;
	}

	/**
	 * @return the headerList
	 */
	public List<Header> getHeaderList() {
		return headerList;
	}

	/**
	 * @param headerList the headerList to set
	 */
	public void setHeaderList(List<Header> headerList) {
		this.headerList = headerList;
	}


}
