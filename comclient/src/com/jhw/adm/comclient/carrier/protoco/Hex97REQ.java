package com.jhw.adm.comclient.carrier.protoco;

public class Hex97REQ  extends DataPacket{
	private int deviceType;  //1:单通道    2:双通道
	public static final int DEVICE_TYPE_LEN = 1;
	
	public Hex97REQ() {
	}
	
	public int getBodyLen() {
		return DEVICE_TYPE_LEN;
	}
	
	public int getDeviceType() {
		return deviceType;
	}
	
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
}
