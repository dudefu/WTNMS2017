package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "SNMPGroup")
public class SNMPGroup implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String groupName;
	private String readonlyView;
	private String readWriteView;
	private String warningView;
	private String securityModel;// snmpv1/snmpv2c/snmpv3
	private int securityLevel;
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSecurityModel() {
		return securityModel;
	}

	public void setSecurityModel(String securityModel) {
		this.securityModel = securityModel;
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public String getReadonlyView() {
		return readonlyView;
	}

	public void setReadonlyView(String readonlyView) {
		this.readonlyView = readonlyView;
	}

	public String getReadWriteView() {
		return readWriteView;
	}

	public void setReadWriteView(String readWriteView) {
		this.readWriteView = readWriteView;
	}

	public String getWarningView() {
		return warningView;
	}

	public void setWarningView(String warningView) {
		this.warningView = warningView;
	}
	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	@ManyToOne
	@JoinColumn(name = "SWITCHID")
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
	}

}
