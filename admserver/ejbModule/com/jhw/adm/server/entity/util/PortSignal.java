package com.jhw.adm.server.entity.util;

import java.io.Serializable;

public class PortSignal implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte portType;
	private byte slotNum;
	private byte portNo;
	private byte dataSingal;
	private byte workingSignal;
	private byte portName;
	public byte getPortType() {
		return portType;
	}
	public void setPortType(byte portType) {
		this.portType = portType;
	}
	public byte getPortNo() {
		return portNo;
	}
	public void setPortNo(byte portNo) {
		this.portNo = portNo;
	}
	public byte getDataSingal() {
		return dataSingal;
	}
	public void setDataSingal(byte dataSingal) {
		this.dataSingal = dataSingal;
	}
	public byte getWorkingSignal() {
		return workingSignal;
	}
	public void setWorkingSignal(byte workingSignal) {
		this.workingSignal = workingSignal;
	}
	public byte getSlotNum() {
		return slotNum;
	}
	public void setSlotNum(byte slotNum) {
		this.slotNum = slotNum;
	}
	public byte getPortName() {
		return portName;
	}
	public void setPortName(byte portName) {
		this.portName = portName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
