package com.jhw.adm.server.util;

import java.io.Serializable;

public class IssuedTag implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
