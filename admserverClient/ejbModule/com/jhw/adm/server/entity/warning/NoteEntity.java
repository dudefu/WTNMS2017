package com.jhw.adm.server.entity.warning;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class NoteEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String deviceName;
	private int noteType;
	private String ipValue;
	private int portNo;
	private int portType;
	private int deviceType;
	private String descs;
	private Date createDate;
	private Long targetDiagramId;
	
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

	public int getNoteType() {
		return noteType;
	}

	public void setNoteType(int noteType) {
		this.noteType = noteType;
	}

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
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

	public Long getTargetDiagramId() {
		return targetDiagramId;
	}

	public void setTargetDiagramId(Long targetDiagramId) {
		this.targetDiagramId = targetDiagramId;
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
