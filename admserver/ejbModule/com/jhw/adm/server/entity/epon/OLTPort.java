package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Epon端口
 * 
 * @author Snow
 * 
 */
@Entity
public class OLTPort implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private int protNo;
	private String portName;     //端口名称
	private String enableStr; //使能
	private int connect;    //连接状态   1-连接  2-断开
	private String macValue;  //mac地址
	private String rate;        //速率  100/1000 单位:Mb/s
	private int duplex;      //双工模式   1-全双工 2-半双工
	private int flowControl; //流控   1-打开 2-关闭
	
	private String portIP;    //端口IP
	
	private int slotNum;// 插槽
	private String portType;
	private int diID;// 索引
	private boolean portStatus;
	private boolean syschorized = true;
	private OLTEntity oltEntity;
	private String descs;
	private int issuedTag=0;//1：设备側  0：网管侧

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getEnableStr() {
		return enableStr;
	}

	public void setEnableStr(String enableStr) {
		this.enableStr = enableStr;
	}

	public int getConnect() {
		return connect;
	}

	public void setConnect(int connect) {
		this.connect = connect;
	}

	public String getMacValue() {
		return macValue;
	}

	public void setMacValue(String macValue) {
		this.macValue = macValue;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getDuplex() {
		return duplex;
	}

	public void setDuplex(int duplex) {
		this.duplex = duplex;
	}

	public int getFlowControl() {
		return flowControl;
	}

	public void setFlowControl(int flowControl) {
		this.flowControl = flowControl;
	}

	public int getProtNo() {
		return protNo;
	}

	public void setProtNo(int protNo) {
		this.protNo = protNo;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	@ManyToOne
	public OLTEntity getOltEntity() {
		return oltEntity;
	}

	public void setOltEntity(OLTEntity oltEntity) {
		this.oltEntity = oltEntity;
	}

	public boolean isPortStatus() {
		return portStatus;
	}

	public void setPortStatus(boolean portStatus) {
		this.portStatus = portStatus;
	}

	public int getDiID() {
		return diID;
	}

	public void setDiID(int diID) {
		this.diID = diID;
	}

	public int getSlotNum() {
		return slotNum;
	}

	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}

	/**
	 * 端口IP
	 * @return
	 */
	public String getPortIP() {
		return portIP;
	}

	public void setPortIP(String portIP) {
		this.portIP = portIP;
	}
}
