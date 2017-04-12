package com.jhw.adm.server.entity.util;

import java.io.Serializable;

public class SNMPGroupBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String groupName;
	private String securityModel;// snmpv1/snmpv2c/snmpv3
	private int securityLevel;
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getSecurityModel() {
		return securityModel;
	}
	public void setSecurityModel(String securityModel) {
		this.securityModel = securityModel;
	}
	public int getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	

}
