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
 * 802.1x系统配置
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "SC8021x")
public class SC8021x  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean applied;
	private String radiusIP;
	private String certifyPort;
	private String certifyPW;
	private boolean reCertified;
	private int reCertifiedScyleTime;
	private int eapOutTime;
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

	public String getRadiusIP() {
		return radiusIP;
	}

	public void setRadiusIP(String radiusIP) {
		this.radiusIP = radiusIP;
	}

	public String getCertifyPort() {
		return certifyPort;
	}

	public void setCertifyPort(String certifyPort) {
		this.certifyPort = certifyPort;
	}

	public String getCertifyPW() {
		return certifyPW;
	}

	public void setCertifyPW(String certifyPW) {
		this.certifyPW = certifyPW;
	}

	public boolean isReCertified() {
		return reCertified;
	}

	public void setReCertified(boolean reCertified) {
		this.reCertified = reCertified;
	}

	public int getReCertifiedScyleTime() {
		return reCertifiedScyleTime;
	}

	public void setReCertifiedScyleTime(int reCertifiedScyleTime) {
		this.reCertifiedScyleTime = reCertifiedScyleTime;
	}

	public int getEapOutTime() {
		return eapOutTime;
	}

	public void setEapOutTime(int eapOutTime) {
		this.eapOutTime = eapOutTime;
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

	public void setDescs(String descs) {
		this.descs = descs;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

}
