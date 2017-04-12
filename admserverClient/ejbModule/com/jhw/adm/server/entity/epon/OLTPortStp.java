package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class OLTPortStp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private int shiNeng;// 使能
	private int portStatus;
	private int priority;// 优先级
	private String n_bridgeRoot;// 网桥路径
	private int appointPort;// 指派端口
	private int forward;
	private String designatedRoot;
	private String designatedBridge;
	private int rootExpenses; // 根路径开销
	private boolean syschorized = true;
	private String descs;
	private OLTEntity oltEntity;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public int getShiNeng() {
		return shiNeng;
	}

	public void setShiNeng(int shiNeng) {
		this.shiNeng = shiNeng;
	}

	public int getPortStatus() {
		return portStatus;
	}

	public void setPortStatus(int portStatus) {
		this.portStatus = portStatus;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getN_bridgeRoot() {
		return n_bridgeRoot;
	}

	public void setN_bridgeRoot(String nBridgeRoot) {
		n_bridgeRoot = nBridgeRoot;
	}

	public int getAppointPort() {
		return appointPort;
	}

	public void setAppointPort(int appointPort) {
		this.appointPort = appointPort;
	}

	public int getForward() {
		return forward;
	}

	public void setForward(int forward) {
		this.forward = forward;
	}

	public String getDesignatedRoot() {
		return designatedRoot;
	}

	public void setDesignatedRoot(String designatedRoot) {
		this.designatedRoot = designatedRoot;
	}

	public String getDesignatedBridge() {
		return designatedBridge;
	}

	public void setDesignatedBridge(String designatedBridge) {
		this.designatedBridge = designatedBridge;
	}

	public int getRootExpenses() {
		return rootExpenses;
	}

	public void setRootExpenses(int rootExpenses) {
		this.rootExpenses = rootExpenses;
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
