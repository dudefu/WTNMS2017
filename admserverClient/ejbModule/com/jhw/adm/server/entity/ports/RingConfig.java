/**
 * RingConfig.java
 * Administrator
 * 2010-3-4
 * TODO
 * 
 */
package com.jhw.adm.server.entity.ports;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

/**
 * @author Administrator
 * @todo ring≈‰÷√
 */
@Entity
@Table(name = "RingConfig")
public class RingConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int ringID;
	private int port1;
	private int port2;
	private String systemType;
	private String port1Type;
	private String port2Type;
	private boolean ringEnable;
	private int ringType;
	private SwitchNodeEntity switchNode;
	private boolean syschorized=true;
	private String descs;
	private int issuedTag=0;//1£∫…Ë±∏Ç»  0£∫Õ¯π‹≤‡
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

	public int getRingID() {
		return ringID;
	}

	public void setRingID(int ringID) {
		this.ringID = ringID;
	}

	public int getPort1() {
		return port1;
	}

	public void setPort1(int port1) {
		this.port1 = port1;
	}

	public int getPort2() {
		return port2;
	}

	public void setPort2(int port2) {
		this.port2 = port2;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public String getPort1Type() {
		return port1Type;
	}

	public void setPort1Type(String port1Type) {
		this.port1Type = port1Type;
	}

	public String getPort2Type() {
		return port2Type;
	}

	public void setPort2Type(String port2Type) {
		this.port2Type = port2Type;
	}

	public boolean isRingEnable() {
		return ringEnable;
	}

	public void setRingEnable(boolean ringEnable) {
		this.ringEnable = ringEnable;
	}

	@ManyToOne
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

	public int getRingType() {
		return ringType;
	}

	public void setRingType(int ringType) {
		this.ringType = ringType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
