package com.jhw.adm.server.entity.message;

import java.io.Serializable;

public class SynchFEP implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fepCode;
	private String message;

	public String getFepCode() {
		return fepCode;
	}

	public void setFepCode(String fepCode) {
		this.fepCode = fepCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
