package com.jhw.adm.server.entity.tuopos;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jhw.adm.server.entity.level3.SwitchLayer3;

@Entity
@Table(name = "topswitchnodelevel3")
@DiscriminatorValue(value = "SWL3")
public class SwitchTopoNodeLevel3 extends NodeEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SwitchLayer3 switchLayer3;
	private String ipValue;
	private String deviceType;
    private String guid;
	
    @Transient
	public SwitchLayer3 getSwitchLayer3() {
		return switchLayer3;
	}
	public void setSwitchLayer3(SwitchLayer3 switchLayer3) {
		this.switchLayer3 = switchLayer3;
	}
	public String getIpValue() {
		return ipValue;
	}
	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
}
