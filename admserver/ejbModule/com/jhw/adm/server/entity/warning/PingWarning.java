/**
 * 
 */
package com.jhw.adm.server.entity.warning;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**定时Ping 告警
 * @author 左军勇
 * @时间 2010-8-2
 */
@Entity
public class PingWarning  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String deviceName;
	private int warningType;
	private int currentStatus;
	private String ipValue;

	private String descs;
	private Date createDate;
	private int warningLevel;
	private WarningFixStatus fixStatus;
	private WarningCloseStatus closeStatus;
	private int portNo;
	private int portType;
	private int deviceType;
	private Long linkId;

	private int slotNum;// 插槽（针对olt）
	private int onuSequenceNo;// 告警onu的编号
	private String warnOnuMac;// 告警onu的mac地址
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getWarningType() {
		return warningType;
	}

	public void setWarningType(int warningType) {
		this.warningType = warningType;
	}

	public int getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}

	public WarningFixStatus getFixStatus() {
		return fixStatus;
	}

	public void setFixStatus(WarningFixStatus fixStatus) {
		this.fixStatus = fixStatus;
	}

	public WarningCloseStatus getCloseStatus() {
		return closeStatus;
	}

	public void setCloseStatus(WarningCloseStatus closeStatus) {
		this.closeStatus = closeStatus;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	public int getPortType() {
		return portType;
	}

	public void setPortType(int portType) {
		this.portType = portType;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public int getSlotNum() {
		return slotNum;
	}

	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}

	public int getOnuSequenceNo() {
		return onuSequenceNo;
	}

	public void setOnuSequenceNo(int onuSequenceNo) {
		this.onuSequenceNo = onuSequenceNo;
	}

	public String getWarnOnuMac() {
		return warnOnuMac;
	}

	public void setWarnOnuMac(String warnOnuMac) {
		this.warnOnuMac = warnOnuMac;
	}


	
	

}
