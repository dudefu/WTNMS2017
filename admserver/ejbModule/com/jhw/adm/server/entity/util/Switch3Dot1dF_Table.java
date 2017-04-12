package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Switch3Dot1dF_Table implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dot1dTpFdbAddress;
	private int dot1dTpFdbPort;
    private Long id;
	public String getDot1dTpFdbAddress() {
		return dot1dTpFdbAddress;
	}
	public void setDot1dTpFdbAddress(String dot1dTpFdbAddress) {
		this.dot1dTpFdbAddress = dot1dTpFdbAddress;
	}
	public int getDot1dTpFdbPort() {
		return dot1dTpFdbPort;
	}
	public void setDot1dTpFdbPort(int dot1dTpFdbPort) {
		this.dot1dTpFdbPort = dot1dTpFdbPort;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
}
