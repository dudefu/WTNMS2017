package com.jhw.adm.comclient.gprs.hongdian;

import java.util.Date;

import com.jhw.adm.comclient.carrier.serial.StringUtil;

/**
 * 
 * @author xiongbo
 * 
 */
public class DSCData {
	public DSCData() {
		createdDate = new Date();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRecvDate() {
		return recvDate;
	}

	public void setRecvDate(String recvDate) {
		this.recvDate = recvDate;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
		return StringUtil.toHexString(buffer);
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	private String userId;
	private String recvDate;
	private byte[] buffer;
	private int length;
	private int type;

	private String address;
	private int port;
	private Date createdDate;
}
