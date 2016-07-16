package com.mytools.extend;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class Send {

	private static final String host = "127.0.0.1";
	private static InetAddress addr = null;
	private static final int port = 8802;
	private static Socket socket = null;
	private static OutputStream out = null;
	static SocketAddress sockaddr = null;
	static Logger log = Logger.getLogger(Send.class);
	
	static {
		if (socket == null || socket.isClosed()) {
			log.info("连接已断开，重新连接！");
			try {
				addr = InetAddress.getByName(host);
				sockaddr = new InetSocketAddress(addr, port);
				socket = new Socket();
				int timeoutMs = 1000; // 2 seconds
				socket.connect(sockaddr, timeoutMs);
			} catch (UnknownHostException e) {
				log.error("", e);
			} catch (IOException e) {
				log.error("", e);
			}
		}
	}

	// host-发送到服务器的地址,port-发送到服务器的端口
	public synchronized static void sendTcpInfo(String content) {
		byte[] b1 = null;
		try {
			if (socket == null || socket.isClosed()) {
				log.info("连接已断开，重新连接！");
				addr = InetAddress.getByName(host);
				SocketAddress sockaddr = new InetSocketAddress(addr, port);
				socket = new Socket();
				int timeoutMs = 1000; // 2 seconds
				socket.connect(sockaddr, timeoutMs);
			}
			b1 = new byte[1024];// 发送到服务器的
			b1 = content.getBytes();
			out = socket.getOutputStream();
			out.write(b1);
			System.out.println("发送的数据为:" + content);

		} catch (Exception ex) {
			System.out.println("连接到交通厅失败:" + ex.getMessage());
			try {
				if (socket != null) {
					socket.close();
					socket = null;
				}
				if (out != null) {
					out.close();
					out = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
			try {
				b1 = null;
				out = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

}