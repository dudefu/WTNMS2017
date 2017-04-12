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
@Table(name = "SNMPUser")
public class SNMPUser  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String userName;
	private String snmpGroup;
	private boolean snmpEncrypted;
	private String orsProtocal;
	private String orsPassword;
	private String encryptProcoal;
	private String encryptPassword;
	private String version;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSnmpGroup() {
		return snmpGroup;
	}

	public void setSnmpGroup(String snmpGroup) {
		this.snmpGroup = snmpGroup;
	}

	public boolean isSnmpEncrypted() {
		return snmpEncrypted;
	}

	public void setSnmpEncrypted(boolean snmpEncrypted) {
		this.snmpEncrypted = snmpEncrypted;
	}

	public String getOrsProtocal() {
		return orsProtocal;
	}

	public void setOrsProtocal(String orsProtocal) {
		this.orsProtocal = orsProtocal;
	}

	public String getOrsPassword() {
		return orsPassword;
	}

	public void setOrsPassword(String orsPassword) {
		this.orsPassword = orsPassword;
	}

	public String getEncryptProcoal() {
		return encryptProcoal;
	}

	public void setEncryptProcoal(String encryptProcoal) {
		this.encryptProcoal = encryptProcoal;
	}

	public String getEncryptPassword() {
		return encryptPassword;
	}

	public void setEncryptPassword(String encryptPassword) {
		this.encryptPassword = encryptPassword;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
