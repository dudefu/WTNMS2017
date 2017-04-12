package com.jhw.adm.server.entity.level3;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="Switch3LLDPPort")
@Entity
public class Switch3LLDPPortEntity  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String descs;
	private boolean syschorized = true;
	private int lLDPPacket;//1：仅发生 2：仅接受 3：启用(发送和接受) 4：禁用
	private String  portName;
	private int portIndex;
	
	private int issuedTag=0;//1：设备側  0：网管侧
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
	public boolean isSyschorized() {
		return syschorized;
	}
	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}
	
	
	public int getlLDPPacket() {
		return lLDPPacket;
	}
	public void setlLDPPacket(int lLDPPacket) {
		this.lLDPPacket = lLDPPacket;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public int getPortIndex() {
		return portIndex;
	}
	public void setPortIndex(int portIndex) {
		this.portIndex = portIndex;
	}
	

}
