package com.jhw.adm.server.entity.warning;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

@Entity
public class RmonWarningConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int itemCode;     //条目编号
	private int portNo;       //端口号
	private String maxLimitcode;      //trap上限事件编号
	private String lowLimitcode;      //trap下限事件编号
	private ThresholdValue threshold;
	private int sampleTime;
	private SwitchNodeEntity switchNode;
	private String descs;
	private boolean syschorized = true;
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

	public int getItemCode() {
		return itemCode;
	}

	public void setItemCode(int itemCode) {
		this.itemCode = itemCode;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public String getMaxLimitcode() {
		return maxLimitcode;
	}

	public void setMaxLimitcode(String maxLimitcode) {
		this.maxLimitcode = maxLimitcode;
	}

	public String getLowLimitcode() {
		return lowLimitcode;
	}

	public void setLowLimitcode(String lowLimitcode) {
		this.lowLimitcode = lowLimitcode;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "thresholdID")
	public ThresholdValue getThreshold() {
		return threshold;
	}

	public void setThreshold(ThresholdValue threshold) {
		this.threshold = threshold;
	}

	@ManyToOne
	@JoinColumn(name = "SWITCHID")
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
	}

	public int getSampleTime() {
		return sampleTime;
	}

	public void setSampleTime(int sampleTime) {
		this.sampleTime = sampleTime;
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
    
}
