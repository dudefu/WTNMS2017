package com.jhw.adm.server.entity.util;

import java.io.Serializable;

public class RingBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ringID;
	private String ringName;
	private boolean ringEnable;
	private int ringType;
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	
	public String getRingName() {
		return ringName;
	}
	public void setRingName(String ringName) {
		this.ringName = ringName;
	}
	public int getRingID() {
		return ringID;
	}
	public void setRingID(int ringID) {
		this.ringID = ringID;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public boolean isRingEnable() {
		return ringEnable;
	}
	public void setRingEnable(boolean ringEnable) {
		this.ringEnable = ringEnable;
	}
	public int getRingType() {
		return ringType;
	}
	public void setRingType(int ringType) {
		this.ringType = ringType;
	}

	
}
