package com.mytools.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.mytools.beans.HttpClientRequest;
import com.mytools.beans.HttpClientResponse;

/**
 * @author hp
 * 该类是基于org.apache.http不是org.apache.commons.http
 * 2012-11-6
 */
public class HttpClientUtil {

	static Logger log = Logger.getLogger(HttpClientUtil.class);
	
	/**
	 * 发送http请求，传输body
	 * @param request
	 * @return
	 */
	public static HttpClientResponse postBody(HttpClientRequest request) throws Exception {
		HttpClientResponse response = new HttpClientResponse();
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse re = null;
		HttpPost post = null;
		try {
			post = new HttpPost(request.getUrl());
			post.setEntity(new StringEntity(request.getBody(), Charset.forName(request.getRequestCharSet())));
			setConfig(post, request);
			re = client.execute(post);
			response = convertResponse(re, request.getResponseCharSet());
			EntityUtils.consume(re.getEntity());
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			if (re != null) {
				re.close();
			}
			if (client != null) {
				client.close();
			}
		}
		return response;
	}
	
	/**
	 * 发送http请求，传递参数
	 * @param request
	 * @return HttpClientResponse
	 * @throws Exception
	 */
	public static HttpClientResponse post(HttpClientRequest request) throws Exception {
		HttpClientResponse response = null;
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse re = null;
		HttpPost post = null;
		try {
			post = new HttpPost(request.getUrl());
			if (request.getMap() != null) {
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				for (Entry<String, Object> entry : request.getMap().entrySet()) {
					log.debug("请求参数 --> " + entry.getKey() + "=" + entry.getValue());
					if (entry.getKey() == null || entry.getValue() == null) {
						throw new Exception("键值不能为null" + entry.getKey() + "=" + entry.getValue());
					}
					list.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
				}
				post.setEntity(new UrlEncodedFormEntity(list, request.getRequestCharSet()));
			}
			setConfig(post, request);
			re = client.execute(post);
			response = convertResponse(re, request.getResponseCharSet());
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			if (re != null) {
				re.close();
			}
			if (client != null) {
				client.close();
			}
		}
		return response;
	}
	
	/**
	 * 设置head和超时
	 * @param base
	 * @param request
	 */
	private static void setConfig(HttpRequestBase base, HttpClientRequest request) {
		if (request.getHeaderList() != null && request.getHeaderList().size() > 0) {
			for (Header head : request.getHeaderList()) {
				base.setHeader(head.getName(), head.getValue());
			}
		}
		RequestConfig config = RequestConfig.custom().setSocketTimeout(request.getSocketTimeout()).setConnectTimeout(request.getConnectTimeout()).setConnectionRequestTimeout(request.getConnectTimeout()).build();//设置请求和传输超时时间
		base.setConfig(config);
	}
	
	/**
	 * 转换response
	 * @param re
	 * @param charSet
	 * @return
	 */
	private static HttpClientResponse convertResponse(HttpResponse re, String charSet) {
		HttpClientResponse response = new HttpClientResponse();
		response.setHeader(re.getAllHeaders());
		response.setStatus(re.getStatusLine().getStatusCode());
		try {
			response.setResult(EntityUtils.toString(re.getEntity(), charSet));
		} catch (ParseException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			try {
				EntityUtils.consume(re.getEntity());
			} catch (IOException e) {
				log.error("", e);
			}
		}
		return response;
	}
	
	/**
	 * get请求
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResponse get(HttpClientRequest request) throws Exception {
		HttpClientResponse response = new HttpClientResponse();
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse re = null;
		HttpGet get = null;
		try {
			get = new HttpGet(request.getUrl());
			setConfig(get, request);
			re = client.execute(get);
			response = convertResponse(re, request.getResponseCharSet());
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			if (re != null) {
				re.close();
			}
			if (client != null) {
				client.close();
			}
		}
		return response;
	}
	
}
