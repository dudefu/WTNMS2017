package com.jhw.adm.server.entity.util;

import java.io.Serializable;
import java.util.List;

public class ParmRep implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Long> parmIds;
	private List objects;
	private Class parmClass;
	private boolean success;
	private String descs;//"I.U,D"
	private int messageType;
	
	
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public List<Long> getParmIds() {
		return parmIds;
	}
	public void setParmIds(List<Long> parmIds) {
		this.parmIds = parmIds;
	}
	public Class getParmClass() {
		return parmClass;
	}
	public void setParmClass(Class parmClass) {
		this.parmClass = parmClass;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
	public List getObjects() {
		return objects;
	}
	public void setObjects(List objects) {
		this.objects = objects;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
