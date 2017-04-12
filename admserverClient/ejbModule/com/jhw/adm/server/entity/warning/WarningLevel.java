package com.jhw.adm.server.entity.warning;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WarningLevel implements Serializable{
	/**
	 * 告警级别(紧急,严重,普通,通知)
	 */
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private int warningLevel;
	private String desccs;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getWarningLevel() {
		return warningLevel;
	}
	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}
	public String getDesccs() {
		return desccs;
	}
	public void setDesccs(String desccs) {
		this.desccs = desccs;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
