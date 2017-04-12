/**
 * 
 */
package com.jhw.adm.server.entity.util;

import java.io.Serializable;

/**
 * @author 左军勇
 * @时间 2010-7-13
 */
public class SwitchVlanPortInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String ipVlue;
	String tagPort;
	String untagPort;
	int issuedTag=0;//1：设备側  0：网管侧
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	public String getIpVlue() {
		return ipVlue;
	}
	public void setIpVlue(String ipVlue) {
		this.ipVlue = ipVlue;
	}
	public String getTagPort() {
		return tagPort;
	}
	public void setTagPort(String tagPort) {
		this.tagPort = tagPort;
	}
	public String getUntagPort() {
		return untagPort;
	}
	public void setUntagPort(String untagPort) {
		this.untagPort = untagPort;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
