package com.jhw.adm.server.entity.util;

import java.io.Serializable;

public class SNMPUserBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String snmpGroup;
	private String snmpTag;
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSnmpGroup() {
		return snmpGroup;
	}
	public void setSnmpGroup(String snmpGroup) {
		this.snmpGroup = snmpGroup;
	}
	public String getSnmpTag() {
		return snmpTag;
	}
	public void setSnmpTag(String snmpTag) {
		this.snmpTag = snmpTag;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
