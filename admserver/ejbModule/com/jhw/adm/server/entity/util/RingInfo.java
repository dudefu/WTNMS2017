package com.jhw.adm.server.entity.util;

import java.io.Serializable;

public class RingInfo implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ip;
	private String ports;
	private int port1;
	private int port2;
	private String port1Type;
	private String port2Type;
	private String sysType;
	private int ringType;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPorts() {
		return ports;
	}
	public void setPorts(String ports) {
		this.ports = ports;
	}
	public String getSysType() {
		return sysType;
	}
	public void setSysType(String sysType) {
		this.sysType = sysType;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getPort1() {
		return port1;
	}
	public void setPort1(int port1) {
		this.port1 = port1;
	}
	public int getPort2() {
		return port2;
	}
	public void setPort2(int port2) {
		this.port2 = port2;
	}
	public String getPort1Type() {
		return port1Type;
	}
	public void setPort1Type(String port1Type) {
		this.port1Type = port1Type;
	}
	public String getPort2Type() {
		return port2Type;
	}
	public void setPort2Type(String port2Type) {
		this.port2Type = port2Type;
	}
	public int getRingType() {
		return ringType;
	}
	public void setRingType(int ringType) {
		this.ringType = ringType;
	}
    
}
