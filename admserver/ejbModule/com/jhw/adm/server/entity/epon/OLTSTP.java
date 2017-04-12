package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * OLT STP配置
 * 
 * @author Snow
 * 
 */
@Entity
@Table
public class OLTSTP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private int specifacation;
	private int stpMaxAge;
	private int priority;// 优先级
	private int helloTime;// STP 发送的时间间隔
	private String topologyChange;
	private int holdTime;
	private int topChanges;
	private int forwardDelay;// 4-30
	private String designatedRoot;
	private int bridgeMaxAge;// 6-40桥最大生存期
	private int rootExpenses; // 根路径开销
	private int bridgeHelloTime;// 1-10
	private int stpRootPort;
	private int bridgewardDelay;
	private OLTEntity oltEntity;
	private boolean syschorized = true;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getSpecifacation() {
		return specifacation;
	}

	public void setSpecifacation(int specifacation) {
		this.specifacation = specifacation;
	}

	public int getStpMaxAge() {
		return stpMaxAge;
	}

	public void setStpMaxAge(int stpMaxAge) {
		this.stpMaxAge = stpMaxAge;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getHelloTime() {
		return helloTime;
	}

	public void setHelloTime(int helloTime) {
		this.helloTime = helloTime;
	}

	public String getTopologyChange() {
		return topologyChange;
	}

	public void setTopologyChange(String topologyChange) {
		this.topologyChange = topologyChange;
	}

	public int getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(int holdTime) {
		this.holdTime = holdTime;
	}

	public int getTopChanges() {
		return topChanges;
	}

	public void setTopChanges(int topChanges) {
		this.topChanges = topChanges;
	}

	public int getForwardDelay() {
		return forwardDelay;
	}

	public void setForwardDelay(int forwardDelay) {
		this.forwardDelay = forwardDelay;
	}

	public String getDesignatedRoot() {
		return designatedRoot;
	}

	public void setDesignatedRoot(String designatedRoot) {
		this.designatedRoot = designatedRoot;
	}

	public int getBridgeMaxAge() {
		return bridgeMaxAge;
	}

	public void setBridgeMaxAge(int bridgeMaxAge) {
		this.bridgeMaxAge = bridgeMaxAge;
	}

	public int getRootExpenses() {
		return rootExpenses;
	}

	public void setRootExpenses(int rootExpenses) {
		this.rootExpenses = rootExpenses;
	}

	public int getBridgeHelloTime() {
		return bridgeHelloTime;
	}

	public void setBridgeHelloTime(int bridgeHelloTime) {
		this.bridgeHelloTime = bridgeHelloTime;
	}

	public int getStpRootPort() {
		return stpRootPort;
	}

	public void setStpRootPort(int stpRootPort) {
		this.stpRootPort = stpRootPort;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getBridgewardDelay() {
		return bridgewardDelay;
	}

	public void setBridgewardDelay(int bridgewardDelay) {
		this.bridgewardDelay = bridgewardDelay;
	}

	@ManyToOne
	public OLTEntity getOltEntity() {
		return oltEntity;
	}

	public void setOltEntity(OLTEntity oltEntity) {
		this.oltEntity = oltEntity;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

}
