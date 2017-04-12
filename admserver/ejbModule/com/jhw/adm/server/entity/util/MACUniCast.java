package com.jhw.adm.server.entity.util;

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
 * 单播
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "MACUniCast")
public class MACUniCast  implements Serializable {
	public static int DYNAMIC = 0;
	public static int STATIC = 1;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int oldTime;
	private String macAddress;
	private int unitCastType;
	private String vlanID;
	private int portNO;// 端口号
	private SwitchNodeEntity switchNode;
	private boolean syschorized=true;
	private String descs;
	private int sortNum;
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

	public int getOldTime() {
		return oldTime;
	}

	public void setOldTime(int oldTime) {
		this.oldTime = oldTime;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public int getUnitCastType() {
		return unitCastType;
	}

	public void setUnitCastType(int unitCastType) {
		this.unitCastType = unitCastType;
	}

	public String getVlanID() {
		return vlanID;
	}

	public void setVlanID(String vlanID) {
		this.vlanID = vlanID;
	}

	public int getPortNO() {
		return portNO;
	}

	public void setPortNO(int portNO) {
		this.portNO = portNO;
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

	public void setDescs(String descs) {
		this.descs = descs;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}
	
}
