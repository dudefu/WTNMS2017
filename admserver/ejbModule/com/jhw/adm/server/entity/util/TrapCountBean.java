package com.jhw.adm.server.entity.util;

import java.io.Serializable;

public class TrapCountBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long count;
	int warningLevel;
	int month;
	int year;
	String ipValue;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public int getWarningLevel() {
		return warningLevel;
	}
	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getIpValue() {
		return ipValue;
	}
	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}
	
	
}
