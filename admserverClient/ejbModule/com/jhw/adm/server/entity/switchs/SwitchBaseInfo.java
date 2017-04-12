package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


/**
 * 交换机基本信息
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "SwitchBaseInfo")
public class SwitchBaseInfo  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String company;
	private String deviceName;
	private String address;
	private String contacts;
    private String deviceAddress;
	private String macValue;
	private String bootromVersion;

	private String cosVersion;
	private String cosVersionTime;

	private String currentTime;
	private String startupTime;
	private SwitchNodeEntity switchNode; 
	private boolean syschorized=true;
	private String descs;
	private int issuedTag=0;//1：设备側  0：网管侧
	
	private String Temperature;
	private String MemoryUsageRate;
	private String CPUUsageRate;
	
	public int getIssuedTag() {
		return issuedTag;
	}

	public String getTemperature() {
		return Temperature;
	}

	public void setTemperature(String temperature) {
		Temperature = temperature;
	}

	public String getMemoryUsageRate() {
		return MemoryUsageRate;
	}

	public void setMemoryUsageRate(String memoryUsageRate) {
		MemoryUsageRate = memoryUsageRate;
	}

	public String getCPUUsageRate() {
		return CPUUsageRate;
	}

	public void setCPUUsageRate(String cPUUsageRate) {
		CPUUsageRate = cPUUsageRate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getMacValue() {
		return macValue;
	}

	public void setMacValue(String macValue) {
		this.macValue = macValue;
	}

	public String getBootromVersion() {
		return bootromVersion;
	}

	public void setBootromVersion(String bootromVersion) {
		this.bootromVersion = bootromVersion;
	}

	public String getCosVersion() {
		return cosVersion;
	}

	public void setCosVersion(String cosVersion) {
		this.cosVersion = cosVersion;
	}

	public String getCosVersionTime() {
		return cosVersionTime;
	}

	public void setCosVersionTime(String cosVersionTime) {
		this.cosVersionTime = cosVersionTime;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public String getStartupTime() {
		return startupTime;
	}

	public void setStartupTime(String startupTime) {
		this.startupTime = startupTime;
	}

	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
	}
	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
    

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	public void copy(SwitchBaseInfo baseInfo){
    	
    	this.setAddress(baseInfo.getAddress());
    	this.setBootromVersion(baseInfo.getBootromVersion());
    	this.setCompany(baseInfo.getCompany());
    	this.setContacts(baseInfo.getContacts());
    	this.setCosVersionTime(baseInfo.getCosVersionTime());
    	this.setCurrentTime(baseInfo.getCurrentTime());
    	this.setDescs(baseInfo.getDescs());
    	this.setDeviceName(baseInfo.getDeviceName());
    	this.setMacValue(baseInfo.getMacValue());
    	this.setStartupTime(baseInfo.getStartupTime());
    	this.setCPUUsageRate(baseInfo.getCPUUsageRate());
    	this.setMemoryUsageRate(baseInfo.getMemoryUsageRate());
    	this.setTemperature(baseInfo.getTemperature());
    	this.setSyschorized(baseInfo.isSyschorized());
    	setIssuedTag(baseInfo.getIssuedTag());
    }
}
