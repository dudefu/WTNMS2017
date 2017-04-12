package com.jhw.adm.server.entity.warning;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class ThresholdValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String warnParm;
	private int samplingType;// 采样类型：变化值/绝对值
	private int k_Max;// 上限值
	private int k_low;
	private String descs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWarnParm() {
		return warnParm;
	}

	public void setWarnParm(String warnParm) {
		this.warnParm = warnParm;
	}

	public int getSamplingType() {
		return samplingType;
	}

	public void setSamplingType(int samplingType) {
		this.samplingType = samplingType;
	}

	public int getK_Max() {
		return k_Max;
	}

	public void setK_Max(int kMax) {
		k_Max = kMax;
	}

	public int getK_low() {
		return k_low;
	}

	public void setK_low(int kLow) {
		k_low = kLow;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

}
