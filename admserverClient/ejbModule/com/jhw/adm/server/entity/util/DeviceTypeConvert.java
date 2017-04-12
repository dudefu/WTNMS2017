package com.jhw.adm.server.entity.util;

import java.io.Serializable;

/**
 * 通过设备型号转化为设备类型
 * 转化规则:设备型号 1-100为二层交换机,值为800
 * 设备型号101-200为三层交换机,值为3000
 * 设备型号201-300为OLT,值为10000
 * @author Administrator
 *
 */
public class DeviceTypeConvert implements Serializable{
	private static DeviceTypeConvert instance ;
	
	public static DeviceTypeConvert getInstance(){
		if (null == instance){
			instance = new DeviceTypeConvert();
		}
		return instance;
	}
	
	public int convertDeviceType(int devType){
		if (1 <= devType && devType <= 100) {
			devType = Constants.DEV_SWITCHER2;

		} else if (101 <= devType && devType <= 200) {
			devType = Constants.DEV_SWITCHER3;
		} else if (201 <= devType && devType <= 300) {
			devType = Constants.DEV_OLT;
		}
		
		return devType;
	}
}
