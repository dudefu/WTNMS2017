package com.jhw.adm.comclient.gprs.hongdian;

/**
 * 
 * @author xiongbo
 * 
 */
public class DSCUser {
	private String userId;
	private String address;
	private int port;
	private String localAddress;
	private int localPort;
	private String logonDate;
	private String updateDate;
	// private long updateTime;
	private int status;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public String getLogonDate() {
		return logonDate;
	}

	public void setLogonDate(String logonDate) {
		this.logonDate = logonDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	// public long getUpdateTime() {
	// return updateTime;
	// }
	//
	// public void setUpdateTime(long updateTime) {
	// this.updateTime = updateTime;
	// }
}
