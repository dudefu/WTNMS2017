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
 * lldp 统计
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "LLDPCount")
public class LLDPCountInfo  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private int rx_Frames;
	private int tx_Frames;
	private int rx_Errors;
	private int rx_Discards;
	private int rx_TLV_Errors;
	private int rx_TLV_Unknown;
	private int rx_TLV_Organz;
	private int rx_TLV_Aged;
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

	public int getRx_Errors() {
		return rx_Errors;
	}

	public void setRx_Errors(int rxErrors) {
		rx_Errors = rxErrors;
	}

	public int getRx_Discards() {
		return rx_Discards;
	}

	public void setRx_Discards(int rxDiscards) {
		rx_Discards = rxDiscards;
	}

	public int getRx_TLV_Errors() {
		return rx_TLV_Errors;
	}

	public void setRx_TLV_Errors(int rxTLVErrors) {
		rx_TLV_Errors = rxTLVErrors;
	}

	public int getRx_TLV_Unknown() {
		return rx_TLV_Unknown;
	}

	public void setRx_TLV_Unknown(int rxTLVUnknown) {
		rx_TLV_Unknown = rxTLVUnknown;
	}

	public int getRx_TLV_Organz() {
		return rx_TLV_Organz;
	}

	public void setRx_TLV_Organz(int rxTLVOrganz) {
		rx_TLV_Organz = rxTLVOrganz;
	}

	public int getRx_TLV_Aged() {
		return rx_TLV_Aged;
	}

	public void setRx_TLV_Aged(int rxTLVAged) {
		rx_TLV_Aged = rxTLVAged;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
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
