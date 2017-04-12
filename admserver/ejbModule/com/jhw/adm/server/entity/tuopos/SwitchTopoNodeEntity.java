package com.jhw.adm.server.entity.tuopos;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

@Entity
@Table(name="topswitchnode")
@DiscriminatorValue(value = "SW")
public class SwitchTopoNodeEntity extends NodeEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ipValue;
	private String subnetMask;
	private String deviceType;
	private String guid;
	private SwitchNodeEntity nodeEntity;

	
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
	public SwitchNodeEntity getNodeEntity() {
		return nodeEntity;
	}

	public void setNodeEntity(SwitchNodeEntity nodeEntity) {
		this.nodeEntity = nodeEntity;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
}
