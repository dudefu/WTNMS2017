package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * OLT LLID(逻辑链路标记)配置
 * 
 * @author Snow
 * 
 */

@Entity
public class ONULLID implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String oltIp;
	private int peakBW;// 峰值带宽
	private int assuredBW;// 保证带宽
	private int staticBW;// 固定带宽
	private String portName;
	private int diID;// 所属那个PON口
	private String macValue;//配置的onu的mac地址
	private ONUEntity onuEntity;
	private boolean syschorized = true;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPeakBW() {
		return peakBW;
	}

	public void setPeakBW(int peakBW) {
		this.peakBW = peakBW;
	}

	public int getAssuredBW() {
		return assuredBW;
	}

	public void setAssuredBW(int assuredBW) {
		this.assuredBW = assuredBW;
	}

	public int getStaticBW() {
		return staticBW;
	}

	public void setStaticBW(int staticBW) {
		this.staticBW = staticBW;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public int getDiID() {
		return diID;
	}

	public void setDiID(int diID) {
		this.diID = diID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getMacValue() {
		return macValue;
	}

	public void setMacValue(String macValue) {
		this.macValue = macValue;
	}

	public String getOltIp() {
		return oltIp;
	}

	public void setOltIp(String oltIp) {
		this.oltIp = oltIp;
	}

	@ManyToOne
	@JoinColumn(name = "onuID")
	public ONUEntity getOnuEntity() {
		return onuEntity;
	}

	public void setOnuEntity(ONUEntity onuEntity) {
		this.onuEntity = onuEntity;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

}
