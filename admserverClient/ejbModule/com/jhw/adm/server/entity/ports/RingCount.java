/**
 * PortRingCountInfo.java
 * Administrator
 * 2010-3-5
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
 * 环端口数据统计
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "RingCount")
public class RingCount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private int rx_ERSTP;
	private int tx_ERSTP;
	private int rx_ETCN;
	private int rx_Ill;
	private int rx_Unk;
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

	public int getRx_ERSTP() {
		return rx_ERSTP;
	}

	public void setRx_ERSTP(int rxERSTP) {
		rx_ERSTP = rxERSTP;
	}

	public int getTx_ERSTP() {
		return tx_ERSTP;
	}

	public void setTx_ERSTP(int txERSTP) {
		tx_ERSTP = txERSTP;
	}

	public int getRx_ETCN() {
		return rx_ETCN;
	}

	public void setRx_ETCN(int rxETCN) {
		rx_ETCN = rxETCN;
	}

	public int getRx_Ill() {
		return rx_Ill;
	}

	public void setRx_Ill(int rxIll) {
		rx_Ill = rxIll;
	}

	public int getRx_Unk() {
		return rx_Unk;
	}

	public void setRx_Unk(int rxUnk) {
		rx_Unk = rxUnk;
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
