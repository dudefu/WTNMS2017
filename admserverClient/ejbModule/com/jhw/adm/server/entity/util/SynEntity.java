package com.jhw.adm.server.entity.util;

import java.io.Serializable;

public class SynEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int synType;
	private String from;
	private String clientIp;
	public int getSynType() {
		return synType;
	}
	public void setSynType(int synType) {
		this.synType = synType;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
}
