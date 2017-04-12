package com.jhw.adm.comclient.protocol.tftp;

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

	private int timeout = 3;
	private int retries = 3;

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
			if (tftp.createServer()) {
				InputStream in = null;
				OutputStream out = null;
				if (tftp.getOperation() == READ) {
					if (!new File(tftp.getFileName()).exists()) {
						log.info("指定文件不存在,请检查网络连接是否正常。");
						return;
					}
					try {
						in = new BufferedInputStream(new FileInputStream(tftp
								.getFileName()));
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
								tftp.getFileName()));
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
				in.close();
				out.close();
			}
		} catch (Exception e) {
			log.info("启动TFTP服务出现异常:" + e.getMessage());
		}
	}

}
