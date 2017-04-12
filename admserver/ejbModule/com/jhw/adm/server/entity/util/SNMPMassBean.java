package com.jhw.adm.server.entity.util;

import java.io.Serializable;

public class SNMPMassBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String massName;
	private String massView;
	private String massRight;
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	public String getMassName() {
		return massName;
	}
	public void setMassName(String massName) {
		this.massName = massName;
	}
	public String getMassView() {
		return massView;
	}
	public void setMassView(String massView) {
		this.massView = massView;
	}
	public String getMassRight() {
		return massRight;
	}
	public void setMassRight(String massRight) {
		this.massRight = massRight;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
