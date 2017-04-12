package com.jhw.adm.server.entity.level3;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class Switch3SNMPEntity   implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private long id;
	private String descs;
	private boolean syschorized = true;
	private int readOnly;//1：是 0：否
	private int readWrite;//1：是 0：否
	private String snmpAddress;//多个IP以“;”分开
	private SwitchLayer3 switchLayer3;
	
	private int issuedTag=0;//1：设备側  0：网管侧
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
	public boolean isSyschorized() {
		return syschorized;
	}
	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}
	
	public int getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(int readOnly) {
		this.readOnly = readOnly;
	}
	public int getReadWrite() {
		return readWrite;
	}
	public void setReadWrite(int readWrite) {
		this.readWrite = readWrite;
	}
	public String getSnmpAddress() {
		return snmpAddress;
	}
	public void setSnmpAddress(String snmpAddress) {
		this.snmpAddress = snmpAddress;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@ManyToOne
	public SwitchLayer3 getSwitchLayer3() {
		return switchLayer3;
	}
	public void setSwitchLayer3(SwitchLayer3 switchLayer3) {
		this.switchLayer3 = switchLayer3;
	}

}
