/**
 * 
 */
package com.jhw.adm.server.entity.util;

import java.io.Serializable;

/**
 * @author �����
 * @ʱ�� 2010-7-13
 */
public class VlanBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    String vlanID;
    String vlanName;
    int issuedTag=0;//1���豸��  0�����ܲ�
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
