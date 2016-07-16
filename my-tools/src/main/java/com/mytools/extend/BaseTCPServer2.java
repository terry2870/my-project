/*
 * 作者：hp
 * Nov 10, 2009
 */

package com.mytools.extend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public abstract class BaseTCPServer2 implements Runnable {

	Logger log = Logger.getLogger(this.getClass());

	protected int port;
	protected String charset;

	private ServerSocket ss = null;
	
	private Executor executor;
	
	private int threadCount = 1;

	public BaseTCPServer2() {

	}

	public BaseTCPServer2(int port) {
		this.port = port;
	}

	public BaseTCPServer2(int port, String charset) {
		this.port = port;
		this.charset = charset;
	}
	
	public BaseTCPServer2(int port, Executor executor) {
		this.port = port;
		this.executor = executor;
	}
	
	public BaseTCPServer2(int port, Executor executor, String charset) {
		this.port = port;
		this.executor = executor;
		this.charset = charset;
	}

	public void init() {
		try {
			log.info("开始监听端口：" + this.port);
			ss = new ServerSocket(this.port);
//			if (executor == null) {
//				Thread t = new Thread(this);
//				t.start();
//			} else {
//				executor.execute(this);
//			}
			Thread thread = null;
			for (int i = 0; i < threadCount; i++) {
				thread = new Thread(this);
				thread.start();
			}
		} catch (Exception e) {
			log.error("监听端口 " + this.port + " 失败！", e);
		}
	}

	public void run() {
		while (true) {
			Socket s = null;
			OutputStream out = null;
			InputStream in = null;
			try {
				s = ss.accept();
				out = s.getOutputStream();
				in = s.getInputStream();
				int len = 0;
				byte[] b = new byte[4096];
				String receiveData = null;
				if ((len = in.read(b)) != -1) {
					if (StringUtils.isEmpty(charset)) {
						receiveData = new String(b, 0, len);
					} else {
						receiveData = new String(b, 0, len, charset);
					}
				}
				this.returnData(process(receiveData.toString()), out);
			} catch (Exception e) {
				log.error("监听端口 " + this.port + " 失败！", e);
			} finally {
				try {
					out.flush();
					out.close();
					in.close();
					// s.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public abstract String process(Object obj) throws Exception;

	/**
	 * 返回数据
	 * 
	 * @param str
	 */
	protected void returnData(String str, OutputStream out) {
		if (StringUtils.isEmpty(str)) {
			return;
		}
		if (out != null) {
			try {
				out.write(str.getBytes());
			} catch (IOException e) {
				log.error("返回数据失败！", e);
			}
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

}
