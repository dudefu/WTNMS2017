package com.jhw.adm.server.entity.ports;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

/**
 * qos配置
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "QOSSysConfig")
public class QOSSysConfig  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean applied;
	private int queueValue;// 2...4
	private String dispatchAlgorithms;// 调度算法
	private int priorityMode;// 0:802.1p;1:dscp;2:tos
	private String dispatchConfig;//调度算法配置：从1...4的值，以";"隔开
	private SwitchNodeEntity switchNode;
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

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
	}

	public int getQueueValue() {
		return queueValue;
	}

	public void setQueueValue(int queueValue) {
		this.queueValue = queueValue;
	}

	public String getDispatchAlgorithms() {
		return dispatchAlgorithms;
	}

	public void setDispatchAlgorithms(String dispatchAlgorithms) {
		this.dispatchAlgorithms = dispatchAlgorithms;
	}

	public int getPriorityMode() {
		return priorityMode;
	}

	public void setPriorityMode(int priorityMode) {
		this.priorityMode = priorityMode;
	}

	public String getDispatchConfig() {
		return dispatchConfig;
	}

	public void setDispatchConfig(String dispatchConfig) {
		this.dispatchConfig = dispatchConfig;
	}

	@OneToOne
	@JoinColumn(name = "SWITCHID")
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
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
}
