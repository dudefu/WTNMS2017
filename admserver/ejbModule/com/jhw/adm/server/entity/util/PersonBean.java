package com.jhw.adm.server.entity.util;

import java.io.Serializable;

public class PersonBean implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String email;
	String mobile;
	String userName;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
	
}
