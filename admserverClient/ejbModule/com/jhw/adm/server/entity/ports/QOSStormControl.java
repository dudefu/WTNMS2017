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
 * 风暴控制配置
 * @author Administrator
 *
 */
@Entity
@Table(name = "QOSStorm")
public class QOSStormControl  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private boolean unknowUnicast;
	private boolean mutilUnicast;
	private boolean broadcast;
	private int percentNum;
	private SwitchNodeEntity switchNode;
	private boolean syschorized=true;
	private String descs;
	/**
	 * //速率单位
	 */
	private String unit;
	/**
	 * //1：设备側  0：网管侧
	 */
	private int issuedTag=0;
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

	public boolean isUnknowUnicast() {
		return unknowUnicast;
	}

	public void setUnknowUnicast(boolean unknowUnicast) {
		this.unknowUnicast = unknowUnicast;
	}

	public boolean isMutilUnicast() {
		return mutilUnicast;
	}

	public void setMutilUnicast(boolean mutilUnicast) {
		this.mutilUnicast = mutilUnicast;
	}

	public boolean isBroadcast() {
		return broadcast;
	}

	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}

	public int getPercentNum() {
		return percentNum;
	}

	public void setPercentNum(int percentNum) {
		this.percentNum = percentNum;
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
