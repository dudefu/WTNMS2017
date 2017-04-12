package com.jhw.adm.server.entity.util;

import java.io.Serializable;

/**
 * ͨ���豸�ͺ�ת��Ϊ�豸����
 * ת������:�豸�ͺ� 1-100Ϊ���㽻����,ֵΪ800
 * �豸�ͺ�101-200Ϊ���㽻����,ֵΪ3000
 * �豸�ͺ�201-300ΪOLT,ֵΪ10000
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
