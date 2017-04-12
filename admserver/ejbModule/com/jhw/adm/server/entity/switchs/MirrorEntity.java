package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * 端口镜像
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "MirrorConfig")
public class MirrorEntity  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private int inbit;
	private int outbit;
	private String inports;// 端口之間用分號","隔開
	private String outports;// 端口之間用分號","隔開
	private String outscanMac;
	private String inscanMac;
	private String scanOutMode;// ALL,目的mac,源mac
	private String scanInMode;// ALL,目的mac,源mac
	private boolean applied;//是否启用
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

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public int getInbit() {
		return inbit;
	}

	public void setInbit(int inbit) {
		this.inbit = inbit;
	}

	public int getOutbit() {
		return outbit;
	}

	public void setOutbit(int outbit) {
		this.outbit = outbit;
	}

	@OneToOne
	@JoinColumn(name = "SWITCHID")
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
	}

	public String getOutscanMac() {
		return outscanMac;
	}

	public void setOutscanMac(String outscanMac) {
		this.outscanMac = outscanMac;
	}

	public String getInscanMac() {
		return inscanMac;
	}

	public void setInscanMac(String inscanMac) {
		this.inscanMac = inscanMac;
	}

	public String getScanOutMode() {
		return scanOutMode;
	}

	public void setScanOutMode(String scanOutMode) {
		this.scanOutMode = scanOutMode;
	}

	public String getScanInMode() {
		return scanInMode;
	}

	public void setScanInMode(String scanInMode) {
		this.scanInMode = scanInMode;
	}

	public String getInports() {
		return inports;
	}

	public void setInports(String inports) {
		this.inports = inports;
	}

	public String getOutports() {
		return outports;
	}

	public void setOutports(String outports) {
		this.outports = outports;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
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
