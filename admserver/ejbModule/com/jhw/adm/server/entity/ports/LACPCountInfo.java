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
 * lacp 统计
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "LACPCount")
public class LACPCountInfo  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private int rx_Frames;
	private int tx_Frames;
	private int rx_Unknown;
	private int rx_Illegal;
	private boolean syschorized=true;
	private SwitchNodeEntity switchNode;
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

	public int getRx_Frames() {
		return rx_Frames;
	}

	public void setRx_Frames(int rxFrames) {
		rx_Frames = rxFrames;
	}

	public int getTx_Frames() {
		return tx_Frames;
	}

	public void setTx_Frames(int txFrames) {
		tx_Frames = txFrames;
	}

	public int getRx_Unknown() {
		return rx_Unknown;
	}

	public void setRx_Unknown(int rxUnknown) {
		rx_Unknown = rxUnknown;
	}

	public int getRx_Illegal() {
		return rx_Illegal;
	}

	public void setRx_Illegal(int rxIllegal) {
		rx_Illegal = rxIllegal;
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
