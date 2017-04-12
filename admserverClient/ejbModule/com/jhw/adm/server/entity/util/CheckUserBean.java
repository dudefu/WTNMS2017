package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import com.jhw.adm.server.entity.system.UserEntity;

public class CheckUserBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int errorCode; //1:�û������� 2�����벻�� 3�������û���� 4���Ƿ��Ѿ���¼  5:û����Ȩ���ܵ�¼
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
