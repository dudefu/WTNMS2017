package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


/**
 * 交换机基本配置数据
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "SwitchBaseConfig")
public class SwitchBaseConfig  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean dhcpAyylied;
	private String ipValue;
	private String preIp;
	private String maskValue;
	private String netGate;
	private String firstDNS;
	private String secondDNS;
	private int managerVlanID;
	private String userNames;
	private SwitchNodeEntity switchNode;
	private boolean syschorized = true;
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

	public boolean isDhcpAyylied() {
		return dhcpAyylied;
	}

	public void setDhcpAyylied(boolean dhcpAyylied) {
		this.dhcpAyylied = dhcpAyylied;
	}

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public String getMaskValue() {
		return maskValue;
	}

	public void setMaskValue(String maskValue) {
		this.maskValue = maskValue;
	}

	public String getNetGate() {
		return netGate;
	}

	public void setNetGate(String netGate) {
		this.netGate = netGate;
	}

	public String getFirstDNS() {
		return firstDNS;
	}

	public void setFirstDNS(String firstDNS) {
		this.firstDNS = firstDNS;
	}

	public String getSecondDNS() {
		return secondDNS;
	}

	public void setSecondDNS(String secondDNS) {
		this.secondDNS = secondDNS;
	}

	public int getManagerVlanID() {
		return managerVlanID;
	}

	public void setManagerVlanID(int managerVlanID) {
		this.managerVlanID = managerVlanID;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

	public String getPreIp() {
		return preIp;
	}

	public void setPreIp(String preIp) {
		this.preIp = preIp;
	}

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

	public void copy(SwitchBaseConfig config) {
		setDescs(config.getDescs());
		setDhcpAyylied(isDhcpAyylied());
		setFirstDNS(config.getFirstDNS());
		setIpValue(config.getIpValue());
		setManagerVlanID(config.getManagerVlanID());
		setMaskValue(config.getMaskValue());
		setNetGate(config.getNetGate());
		setPreIp(config.getPreIp());
		setSecondDNS(config.getSecondDNS());
		setIssuedTag(config.getIssuedTag());

	}

}
