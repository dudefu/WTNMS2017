package com.jhw.adm.comclient.carrier.protoco;

public class PortInfo {

	public PortInfo() {
	}

	public PortInfo(int number, int category, int baudRate, int parity,
			int dataBits, int stopBits) {
		this.number = number;
		this.category = category;
		this.baudRate = baudRate;
		this.parity = parity;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.subnetCode = subnetCode;
	}

	private int number;
	private int category;
	private int baudRate;
	private int parity;
	private int dataBits;
	private int stopBits;
	private int subnetCode;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public int getParity() {
		return parity;
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	public int getDataBits() {
		return dataBits;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public int getStopBits() {
		return stopBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public int getSubnetCode() {
		return subnetCode;
	}

	public void setSubnetCode(int subnetCode) {
		this.subnetCode = subnetCode;
	}

}