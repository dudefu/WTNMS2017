package com.jhw.adm.comclient.service.upgrade;

/*
 * 创建日期 2006-5-12
 * @author liuxw
 * 仅从服务端读取配置文件到本地
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.jhw.adm.comclient.data.Constants;

/**
 * @author liuxw
 */
public class SimpleTftpClient extends Thread {
	private String srcFile = "";
	private String dstFile = "";
	private InetAddress inetaddress;
	private int operation = 1;
	private File dst = null;

	public SimpleTftpClient() {
	}

	public void setDestinationFileName(String dstFile) {
		this.dstFile = dstFile;
		dst = new File(dstFile);
	}

	public void setSourceFileName(String srcFile) {
		this.srcFile = srcFile;
	}

	public void setServiceIp(String ip) {
		try {
			inetaddress = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
		}
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public void run() {
		try {
			TftpRequest tftp = new TftpRequest();
			tftp.setHost(inetaddress);
			tftp.setOperation(operation);
			tftp.setFileName(operation == Constants.READ ? srcFile : dstFile);
			tftp.createClient();

			InputStream in;
			OutputStream out;
			if (operation == Constants.READ) {
				in = tftp.getInputStream();
				out = new BufferedOutputStream(new FileOutputStream(dstFile));
			} else {
				in = new BufferedInputStream(new FileInputStream(srcFile));
				out = tftp.getOutputStream();
			}

			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long getLength() {
		return dst.length();
	}

}
