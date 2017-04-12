package com.jhw.adm.server.entity.warning;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WarningCategory implements Serializable {
	/**
	 * 告警类别(设备告警,端口告警,协议告警,性能告警,网管告警)
	 */

	private static final long serialVersionUID = 1L;
	private Long id;
	private int warningCategory;
//	private String warningStyle;//声音、消息.多种方式的时候，用","隔开
	private String desccs;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public int getWarningCategory() {
		return warningCategory;
	}
	public void setWarningCategory(int warningCategory) {
		this.warningCategory = warningCategory;
	}

//	public String getWarningStyle() {
//		return warningStyle;
//	}
//	public void setWarningStyle(String warningStyle) {
//		this.warningStyle = warningStyle;
//	}
	
	public String getDesccs() {
		return desccs;
	}
	public void setDesccs(String desccs) {
		this.desccs = desccs;
	}
	
	
}
