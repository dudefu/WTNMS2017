package com.jhw.adm.server.entity.util;
import java.io.Serializable;
import java.util.Date;

/**
 * 综合网管授权对象
 */
public class License implements Serializable {
	
	public License() {
	}
	/**
	 * 合同号
	 */
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String number) {
		this.contractNumber = number;
	}
	/**
	 * MAC地址
	 */
	public byte[] getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(byte[] macAddress) {
		this.macAddress = macAddress;
	}
	/**
	 * 授权的描述
	 */
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 授权的版本类型
	 */
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	/**
	 * 客户端的数量
	 */
	public PropertyValue<Integer> getClientCount() {
		return clientCount;
	}

	public void setClientCount(PropertyValue<Integer> clientCount) {
		this.clientCount = clientCount;
	}
	/**
	 * 二层交换机的数量
	 */
	public PropertyValue<Integer> getSwitcherCount() {
		return switcherCount;
	}

	public void setSwitcherCount(PropertyValue<Integer> switcherCount) {
		this.switcherCount = switcherCount;
	}
	/**
	 * 三层交换机的数量
	 */
	public PropertyValue<Integer> getLayer3SwitcherCount() {
		return layer3SwitcherCount;
	}

	public void setLayer3SwitcherCount(PropertyValue<Integer> layer3SwitcherCount) {
		this.layer3SwitcherCount = layer3SwitcherCount;
	}
	/**
	 * OLT的数量
	 */
	public PropertyValue<Integer> getOltCount() {
		return oltCount;
	}

	public void setOltCount(PropertyValue<Integer> oltCount) {
		this.oltCount = oltCount;
	}
	/**
	 * ONU的数量
	 */
	public PropertyValue<Integer> getOnuCount() {
		return onuCount;
	}

	public void setOnuCount(PropertyValue<Integer> onuCount) {
		this.onuCount = onuCount;
	}
	/**
	 * 载波机的数量
	 */
	public PropertyValue<Integer> getPlcCount() {
		return plcCount;
	}

	public void setPlcCount(PropertyValue<Integer> plcCount) {
		this.plcCount = plcCount;
	}
	/**
	 * GPRS的数量
	 */
	public PropertyValue<Integer> getGprsCount() {
		return gprsCount;
	}

	public void setGprsCount(PropertyValue<Integer> gprsCount) {
		this.gprsCount = gprsCount;
	}
	/**
	 * 有效日期
	 */
	public PropertyValue<Date> getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(PropertyValue<Date> expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	private String description;
	private String contractNumber;
	private byte[] macAddress;
	private int version;
	
	private PropertyValue<Integer> clientCount;
	private PropertyValue<Integer> switcherCount;
	private PropertyValue<Integer> layer3SwitcherCount;
	private PropertyValue<Integer> oltCount;
	private PropertyValue<Integer> onuCount;
	private PropertyValue<Integer> plcCount;
	private PropertyValue<Integer> gprsCount;
	private PropertyValue<Date> expirationDate;

	private static final long serialVersionUID = 4152299999029640432L;
	
	/**
	 * 评估版本
	 */
	public static final int EVALUATION_VERSION = 0;
	/**
	 * 注册版本
	 */
	public static final int REGISTRATION_VERSION = 1;
	
	
	/**
	 * 开发版本
	 */
	public static final int DEVELOPMENT_VERSION = 2;
}