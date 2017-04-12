package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import com.jhw.adm.server.entity.system.UserEntity;

public class CheckUserBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int errorCode; //1:用户不存在 2：密码不对 3：在线用户检查 4：是否已经登录  5:没有授权不能登录
	private String errorMessage;
	private UserEntity userEntity;
	private License license;
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public UserEntity getUserEntity() {
		return userEntity;
	}
	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public License getLicense() {
		return license;
	}
	public void setLicense(License license) {
		this.license = license;
	}
	
	
}
