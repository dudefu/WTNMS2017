/**
 * 
 */
package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author 左军勇
 * @时间 2010-7-29
 */
@Entity
public class SerialPortConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private Long id;
    private int  baudRate;//波特率值 4800 7200 9600 19200 38400 57600 115200 
    private int dataBit;//数据位值  5 6 7 8
    private int stopBit;//停止位1 2
    private int verify;//校验位 none odd even
    private String serialPortName;//串口名称
    private int issuedTag=0;//1：设备側  0：网管侧
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getBaudRate() {
		return baudRate;
	}
	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}
	public int getDataBit() {
		return dataBit;
	}
	public void setDataBit(int dataBit) {
		this.dataBit = dataBit;
	}
	public int getStopBit() {
		return stopBit;
	}
	public void setStopBit(int stopBit) {
		this.stopBit = stopBit;
	}
	public int getVerify() {
		return verify;
	}
	public void setVerify(int verify) {
		this.verify = verify;
	}
	public String getSerialPortName() {
		return serialPortName;
	}
	public void setSerialPortName(String serialPortName) {
		this.serialPortName = serialPortName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
}
