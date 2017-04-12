package com.jhw.adm.server.entity.tuopos;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jhw.adm.server.entity.epon.OLTEntity;

@Entity
@Table(name = "topoeponnode")
@DiscriminatorValue(value = "EP")
public class EponTopoEntity extends NodeEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String guid;
	private OLTEntity oltEntity;
	private String ipValue;
	private String subnetMask;
	private String deviceType;
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public String getSubnetMask() {
		return subnetMask;
	}

	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}

	@Transient
	public OLTEntity getOltEntity() {
		return oltEntity;
	}

	public void setOltEntity(OLTEntity oltEntity) {
		this.oltEntity = oltEntity;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
}
