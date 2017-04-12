package com.jhw.adm.server.entity.warning;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class TrapWarningEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String warningCode;
	private String deviceName;
	private int warningEvent;
	private int currentStatus;
	private String ipValue;
    private String users;
	private int portNo;
	private int portType;
	private int deviceType;
	private String paramName;
	private long value;
	private Date sampleTime;
	private String descs;
	private int warningCategory;   //告警类别,分为设备告警,端口告警,性能告警,网管告警等
	private int slotNum;// 插槽（针对olt）
	private int onuSequenceNo;// 告警onu的编号
	private String warnOnuMac;// 告警onu的mac地址

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWarningCode() {
		return warningCode;
	}

	public void setWarningCode(String warningCode) {
		this.warningCode = warningCode;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public int getWarningEvent() {
		return warningEvent;
	}

	public void setWarningEvent(int warningEvent) {
		this.warningEvent = warningEvent;
	}

	public int getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSampleTime() {
		return sampleTime;
	}

	public void setSampleTime(Date sampleTime) {
		this.sampleTime = sampleTime;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public int getPortType() {
		return portType;
	}

	public void setPortType(int portType) {
		this.portType = portType;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public int getSlotNum() {
		return slotNum;
	}

	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}

	public String getWarnOnuMac() {
		return warnOnuMac;
	}

	public void setWarnOnuMac(String warnOnuMac) {
		this.warnOnuMac = warnOnuMac;
	}

	public int getOnuSequenceNo() {
		return onuSequenceNo;
	}

	public void setOnuSequenceNo(int onuSequenceNo) {
		this.onuSequenceNo = onuSequenceNo;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public int getWarningCategory() {
		return warningCategory;
	}

	public void setWarningCategory(int warningCategory) {
		this.warningCategory = warningCategory;
	}

	
	
}
