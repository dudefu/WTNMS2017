package com.jhw.adm.comclient.gprs.hongdian;

import java.util.Date;

import com.jhw.adm.comclient.carrier.serial.StringUtil;

/**
 * 
 * @author xiongbo
 * 
 */
public class DDPData {
	public DDPData(byte[] data) {
		this.data = data;
		createdDate = new Date();
	}

	public byte[] getData() {
		return data;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String toHexString() {
		return StringUtil.toHexString(data);
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public int getLength() {
		return data.length;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	private byte[] data;
	private String userId;
	private String address;
	private int port;
	private Date createdDate;
}
