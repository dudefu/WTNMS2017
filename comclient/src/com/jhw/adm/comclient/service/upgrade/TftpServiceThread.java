package com.jhw.adm.comclient.service.upgrade;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class TftpServiceThread extends Thread {
	private static Logger log = Logger.getLogger(TftpServiceThread.class);

	public static final int READ = 1;
	public static final int WRITE = 2;

	private int timeout = 10;
	private int retries = 10;
	public static boolean flag = false;

	// public static void main(String[] args) {
	// try {
	// TftpServiceThread tftpServer = new TftpServiceThread();
	// tftpServer.start();
	// } catch (Exception e) {
	// // ("启动TFTP服务失败" + e.getMessage());
	// e.printStackTrace();
	// }
	// }

	public TftpServiceThread() {
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getRetries() {
		return retries;
	}

	public void run() {
		try {
			TftpRequest tftp = new TftpRequest(timeout, retries);
			tftp.createClient();
			if (tftp.createServer()) {
				InputStream in = null;
				OutputStream out = null;
				String path = System.getProperty("user.dir");
				String fileName = path + "\\tftp\\";
				String filePath = fileName + tftp.getFileName();
				if (tftp.getOperation() == READ) {
					if (!new File(filePath).exists()) {
						log.info("指定文件不存在,请检查网络连接是否正常。");
						return;
					}
					try {
						System.out.println(filePath);
						in = new BufferedInputStream(new FileInputStream(
								filePath));
					} catch (FileNotFoundException e) {
						in.close();
						log.info("无效文件:" + e.getMessage());
						return;
					}
					out = tftp.getOutputStream();
				} else {
					in = tftp.getInputStream();
					try {
						out = new BufferedOutputStream(new FileOutputStream(
								filePath));
					} catch (FileNotFoundException e) {
						out.close();
						log.info("无效文件:" + e.getMessage());
						return;
					}
				}
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				flag = true;
				in.close();
				out.close();
			}
		} catch (Exception e) {
			log.info("启动TFTP服务出现异常:" + e.getMessage());
		}
	}

}
