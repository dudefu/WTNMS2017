package com.jhw.adm.server.entity.level3;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class Switch3RingEntity  implements Serializable{

	private static final long serialVersionUID = 1L;
	private long id;
	private String descs;
	private boolean syschorized = true;
	private int ringID;
	private int ringStatus; 
	private int nodeType;
	private String controlVlan;
	private int helloTime;
	private int failTime;
	private int preforwardTime;//主端口
	private int primaryPortIndex;
	private int primaryPort_F_Status;//主端口转发状态
	private int primaryPortLinkStatus; 
	private String primaryPortName;
	private int secondaryPortIndex;//次端口
	private String secondaryPortName;
	private int secondaryPort_F_Status;
	private int secondaryPortLinkStatus;
	private SwitchLayer3 switchLayer3;
	
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
	public int getRingID() {
		return ringID;
	}
	public void setRingID(int ringID) {
		this.ringID = ringID;
	}
	public int getRingStatus() {
		return ringStatus;
	}
	public void setRingStatus(int ringStatus) {
		this.ringStatus = ringStatus;
	}
	public int getNodeType() {
		return nodeType;
	}
	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}
	public String getControlVlan() {
		return controlVlan;
	}
	public void setControlVlan(String controlVlan) {
		this.controlVlan = controlVlan;
	}
	public int getHelloTime() {
		return helloTime;
	}
	public void setHelloTime(int helloTime) {
		this.helloTime = helloTime;
	}
	public int getFailTime() {
		return failTime;
	}
	public void setFailTime(int failTime) {
		this.failTime = failTime;
	}
	public int getPreforwardTime() {
		return preforwardTime;
	}
	public void setPreforwardTime(int preforwardTime) {
		this.preforwardTime = preforwardTime;
	}
	public int getPrimaryPortIndex() {
		return primaryPortIndex;
	}
	public void setPrimaryPortIndex(int primaryPortIndex) {
		this.primaryPortIndex = primaryPortIndex;
	}
	public int getPrimaryPort_F_Status() {
		return primaryPort_F_Status;
	}
	public void setPrimaryPort_F_Status(int primaryPortFStatus) {
		primaryPort_F_Status = primaryPortFStatus;
	}
	public int getPrimaryPortLinkStatus() {
		return primaryPortLinkStatus;
	}
	public void setPrimaryPortLinkStatus(int primaryPortLinkStatus) {
		this.primaryPortLinkStatus = primaryPortLinkStatus;
	}
	public String getPrimaryPortName() {
		return primaryPortName;
	}
	public void setPrimaryPortName(String primaryPortName) {
		this.primaryPortName = primaryPortName;
	}
	public int getSecondaryPortIndex() {
		return secondaryPortIndex;
	}
	public void setSecondaryPortIndex(int secondaryPortIndex) {
		this.secondaryPortIndex = secondaryPortIndex;
	}
	public String getSecondaryPortName() {
		return secondaryPortName;
	}
	public void setSecondaryPortName(String secondaryPortName) {
		this.secondaryPortName = secondaryPortName;
	}
	public int getSecondaryPort_F_Status() {
		return secondaryPort_F_Status;
	}
	public void setSecondaryPort_F_Status(int secondaryPortFStatus) {
		secondaryPort_F_Status = secondaryPortFStatus;
	}
	public int getSecondaryPortLinkStatus() {
		return secondaryPortLinkStatus;
	}
	public void setSecondaryPortLinkStatus(int secondaryPortLinkStatus) {
		this.secondaryPortLinkStatus = secondaryPortLinkStatus;
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
