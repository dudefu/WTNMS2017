/**
 * PortRingInfo.java
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
 * 环端口信息
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "PortRing")
public class RingPortInfo  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int ringID;
	private int portNo;
	private boolean status;// true:enable;false:disable
	private String ringMode;
	private String portRole;
	private String transferStatus;
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

	public int getRingID() {
		return ringID;
	}

	public void setRingID(int ringID) {
		this.ringID = ringID;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getRingMode() {
		return ringMode;
	}

	public void setRingMode(String ringMode) {
		this.ringMode = ringMode;
	}

	public String getPortRole() {
		return portRole;
	}

	public void setPortRole(String portRole) {
		this.portRole = portRole;
	}

	public String getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
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

}
