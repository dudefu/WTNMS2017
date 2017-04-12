package com.jhw.adm.server.entity.level3;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class Switch3PortFlowEntity  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String descs;
	private boolean syschorized = true;
	private int receiveSpeed;
	private int sendSpeed;
	private String portName;
	private int portIndex;
	private SwitchLayer3 switchLayer3;
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
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
	public int getReceiveSpeed() {
		return receiveSpeed;
	}
	public void setReceiveSpeed(int receiveSpeed) {
		this.receiveSpeed = receiveSpeed;
	}
	public int getSendSpeed() {
		return sendSpeed;
	}
	public void setSendSpeed(int sendSpeed) {
		this.sendSpeed = sendSpeed;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@ManyToOne
	public SwitchLayer3 getSwitchLayer3() {
		return switchLayer3;
	}
	public void setSwitchLayer3(SwitchLayer3 switchLayer3) {
		this.switchLayer3 = switchLayer3;
	}
}
