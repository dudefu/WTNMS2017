package com.jhw.adm.server.entity.epon;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
@Entity
public class OLTVlanPortConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean syschorized=true;
	private String descs;//�˿�����
	private int portNo;
	private int slotNum;// ���
	private String portName;
	private int pvid;
	private int priority;
	private int portType;// 0 epon �˿� 1  gigaEthernet ǧ����̫���ӿ�
	private int switchMode;//����ģʽ
	private int untagged;
	private int allowVlan;//�����Vlan
	private OLTEntity oltEntity;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public int getPortNo() {
		return portNo;
	}
	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}
	public int getPvid() {
		return pvid;
	}
	public void setPvid(int pvid) {
		this.pvid = pvid;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getSwitchMode() {
		return switchMode;
	}
	public void setSwitchMode(int switchMode) {
		this.switchMode = switchMode;
	}
	public int getUntagged() {
		return untagged;
	}
	public void setUntagged(int untagged) {
		this.untagged = untagged;
	}
	public int getPortType() {
		return portType;
	}
	public void setPortType(int portType) {
		this.portType = portType;
	}
	public int getAllowVlan() {
		return allowVlan;
	}
	public void setAllowVlan(int allowVlan) {
		this.allowVlan = allowVlan;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public int getSlotNum() {
		return slotNum;
	}
	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}
	public OLTEntity getOltEntity() {
		return oltEntity;
	}
	public void setOltEntity(OLTEntity oltEntity) {
		this.oltEntity = oltEntity;
	}
	
}
