package com.jhw.adm.client.model;

import java.io.Serializable;

public class ServerInfo implements Serializable {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getLastUser() {
		return lastUser;
	}
	public void setLastUser(String lastUser) {
		this.lastUser = lastUser;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (name == null) return false;
		if (obj == null) return false;
		
		if (obj instanceof ServerInfo) {
			ServerInfo si = (ServerInfo)obj;
			return name.equals(si.getName());
		} else {
			return false;
		}
	}
	private String name;
	private String address;
	private int port;
	private int timeout;
	private String lastUser;
	private static final long serialVersionUID = 6208816522602135428L;
}