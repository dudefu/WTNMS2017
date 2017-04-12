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
	 * �豸ip��ַ
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
	 * �豸����,
	 * ��Ϊ����  800,
	 * ����    3000,
	 * OLT  10000
	 * ONU  10001
	 * 
	 * ǰ�û���,�ô�ֵ
	 * @return
	 */
	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * �豸�ͺ�
	 * ����:  
	 * ���㽻����:IETH802��IETH804��IETH8008��
	 * ���㽻����: IETH3424
	 * OLT��������  EPON8506
	 * 
	 * �ͻ�����,����Сֵ
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
