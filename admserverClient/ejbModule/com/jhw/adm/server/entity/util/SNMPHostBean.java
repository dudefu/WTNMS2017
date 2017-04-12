package com.jhw.adm.server.entity.util;

import java.io.Serializable;

public class SNMPHostBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String hostIp;
	String snmpVersion;
	String massName;
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	public String getSnmpVersion() {
		return snmpVersion;
	}
	public void setSnmpVersion(String snmpVersion) {
		this.snmpVersion = snmpVersion;
	}
	public String getMassName() {
		return massName;
	}
	public void setMassName(String massName) {
		this.massName = massName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
