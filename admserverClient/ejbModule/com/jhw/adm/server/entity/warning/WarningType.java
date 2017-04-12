package com.jhw.adm.server.entity.warning;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WarningType implements Serializable {
	/**
	 * 告警事件(冷启动,热启动,端口断开,端口连接,流量,Ping不通,Ping成功,前置机断开,前置机连接,syslog)
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int warningType;
	private int warningLevel;
	private String warningStyle;//声音、消息.多种方式的时候，用","隔开
	private String desccs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getWarningType() {
		return warningType;
	}

	public void setWarningType(int warningType) {
		this.warningType = warningType;
	}

	public int getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}

	public String getWarningStyle() {
		return warningStyle;
	}

	public void setWarningStyle(String warningStyle) {
		this.warningStyle = warningStyle;
	}

	public String getDesccs() {
		return desccs;
	}

	public void setDesccs(String desccs) {
		this.desccs = desccs;
	}

}
