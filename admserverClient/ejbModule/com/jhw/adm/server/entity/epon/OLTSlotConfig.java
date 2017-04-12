/**
 * 
 */
package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author 左军勇
 * @时间 2010-8-2
 */
@Entity
public class OLTSlotConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private int slotID;
	private int registeredNum;//已注册芯片数
	private int helloMsgTimeOut;//报文间隔
	private int connectTimerOut;//保持连接超时间隔
	private boolean syschorized = true;
	private String descs;
	private OLTEntity oltEntity;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getSlotID() {
		return slotID;
	}
	public void setSlotID(int slotID) {
		this.slotID = slotID;
	}
	public int getRegisteredNum() {
		return registeredNum;
	}
	public void setRegisteredNum(int registeredNum) {
		this.registeredNum = registeredNum;
	}
	public int getHelloMsgTimeOut() {
		return helloMsgTimeOut;
	}
	public void setHelloMsgTimeOut(int helloMsgTimeOut) {
		this.helloMsgTimeOut = helloMsgTimeOut;
	}
	public int getConnectTimerOut() {
		return connectTimerOut;
	}
	public void setConnectTimerOut(int connectTimerOut) {
		this.connectTimerOut = connectTimerOut;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public boolean isSyschorized() {
		return syschorized;
	}
	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
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
	

}
