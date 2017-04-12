package com.jhw.adm.server.entity.message;

import java.io.Serializable;

import com.jhw.adm.server.entity.util.DeviceTypeConvert;

public class SynchDevice implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ipvalue;
	private String message;
	private int deviceType;
	private int modelNumber;
	private DeviceTypeConvert deviceTypeConvert = DeviceTypeConvert.getInstance();

	/**
	 * 设备ip地址
	 * @return
	 */
	public String getIpvalue() {
		return ipvalue;
	}

	public void setIpvalue(String ipvalue) {
		this.ipvalue = ipvalue;
	}

	/**
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 设备类型,
	 * 分为二层  800,
	 * 三层    3000,
	 * OLT  10000
	 * ONU  10001
	 * 
	 * 前置机用,用大值
	 * @return
	 */
	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * 设备型号
	 * 例如:  
	 * 二层交换机:IETH802、IETH804、IETH8008等
	 * 三层交换机: IETH3424
	 * OLT交换机：  EPON8506
	 * 
	 * 客户端用,传入小值
	 * @return
	 */
	public int getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(int model) {
		setDeviceType(deviceTypeConvert.convertDeviceType(model));
		this.modelNumber = model;
	}
	
	

}
