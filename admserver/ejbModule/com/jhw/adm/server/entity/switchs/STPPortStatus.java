package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "stpportstatus")
public class STPPortStatus  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private int portRole;
	private boolean portStatus;
	private long pathpay;
	private boolean edgePort;
	private boolean ptpPort;
	private String aimstpversion;
	private SwitchNodeEntity switchNode;
	private boolean syschorized=true;
	private String descs;
	
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
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

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public int getPortRole() {
		return portRole;
	}

	public void setPortRole(int portRole) {
		this.portRole = portRole;
	}

	public boolean isPortStatus() {
		return portStatus;
	}

	public void setPortStatus(boolean portStatus) {
		this.portStatus = portStatus;
	}

	public long getPathpay() {
		return pathpay;
	}

	public void setPathpay(long pathpay) {
		this.pathpay = pathpay;
	}

	public boolean isEdgePort() {
		return edgePort;
	}

	public void setEdgePort(boolean edgePort) {
		this.edgePort = edgePort;
	}

	public boolean isPtpPort() {
		return ptpPort;
	}

	public void setPtpPort(boolean ptpPort) {
		this.ptpPort = ptpPort;
	}

	public String getAimstpversion() {
		return aimstpversion;
	}

	public void setAimstpversion(String aimstpversion) {
		this.aimstpversion = aimstpversion;
	}

	@ManyToOne
	@JoinColumn(name = "SWITCHID")
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
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
