/**
 * 
 */
package com.jhw.adm.server.entity.util;

import java.io.Serializable;

/**
 * @author 左军勇
 * @时间 2010-7-13
 */
public class VlanBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    String vlanID;
    String vlanName;
    int issuedTag=0;//1：设备側  0：网管侧
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	public String getVlanID() {
		return vlanID;
	}
	public void setVlanID(String vlanID) {
		this.vlanID = vlanID;
	}
	public String getVlanName() {
		return vlanName;
	}
	public void setVlanName(String vlanName) {
		this.vlanName = vlanName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
}
