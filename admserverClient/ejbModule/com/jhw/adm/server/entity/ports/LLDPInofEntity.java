package com.jhw.adm.server.entity.ports;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


/**
 * lldp信息
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "LLDPInfo")
public class LLDPInofEntity  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int localPortNo;
	private String localIP;
	private int localSlot;
	private int localDeviceType;
	private int localPortType;
	private int remotePortType;
	private int remotePortNo;
	private String remoteIP;
	private int remoteSlot;
	private int remoteDeviceType;

	private boolean connected=true;
	private boolean syschorized=true;
	private String descs;

	private int issuedTag=0;//1：设备側  0：网管侧
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getLocalPortNo() {
		return localPortNo;
	}

	public void setLocalPortNo(int localPortNo) {
		this.localPortNo = localPortNo;
	}

	public int getRemotePortNo() {
		return remotePortNo;
	}

	public void setRemotePortNo(int remotePortNo) {
		this.remotePortNo = remotePortNo;
	}

	public String getRemoteIP() {
		return remoteIP;
	}

	public void setRemoteIP(String remoteIP) {
		this.remoteIP = remoteIP;
	}

	public String getLocalIP() {
		return localIP;
	}

	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
	
	
	public int getLocalSlot() {
		return localSlot;
	}

	public void setLocalSlot(int localSlot) {
		this.localSlot = localSlot;
	}

	public int getLocalDeviceType() {
		return localDeviceType;
	}

	public void setLocalDeviceType(int localDeviceType) {
		this.localDeviceType = localDeviceType;
	}

	public int getRemoteSlot() {
		return remoteSlot;
	}

	public void setRemoteSlot(int remoteSlot) {
		this.remoteSlot = remoteSlot;
	}

	public int getRemoteDeviceType() {
		return remoteDeviceType;
	}

	public void setRemoteDeviceType(int remoteDeviceType) {
		this.remoteDeviceType = remoteDeviceType;
	}

	public void copy(LLDPInofEntity entity){
		setRemoteIP(entity.getRemoteIP());
		setRemotePortNo(entity.getRemotePortNo());
		setLocalIP(entity.getLocalIP());
		setDescs(entity.getDescs());
	}

	public int getLocalPortType() {
		return localPortType;
	}

	public void setLocalPortType(int localPortType) {
		this.localPortType = localPortType;
	}

	public int getRemotePortType() {
		return remotePortType;
	}

	public void setRemotePortType(int remotePortType) {
		this.remotePortType = remotePortType;
	}

}
