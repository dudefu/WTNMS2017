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
@Table(name = "STPCount")
public class STPCount  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private int rx_RSTP;
	private int tx_RSTP;
	private int rx_STP;
	private int tx_STP;
	private int rx_TCN;
	private int tx_TCN;
	private int rx_Ill;
	private int rx_Unk;
	private SwitchNodeEntity switchNode;
	private boolean syschorized=true;
	
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

	public int getRx_RSTP() {
		return rx_RSTP;
	}

	public void setRx_RSTP(int rxRSTP) {
		rx_RSTP = rxRSTP;
	}

	public int getTx_RSTP() {
		return tx_RSTP;
	}

	public void setTx_RSTP(int txRSTP) {
		tx_RSTP = txRSTP;
	}

	public int getRx_STP() {
		return rx_STP;
	}

	public void setRx_STP(int rxSTP) {
		rx_STP = rxSTP;
	}

	public int getTx_STP() {
		return tx_STP;
	}

	public void setTx_STP(int txSTP) {
		tx_STP = txSTP;
	}

	public int getRx_TCN() {
		return rx_TCN;
	}

	public void setRx_TCN(int rxTCN) {
		rx_TCN = rxTCN;
	}

	public int getTx_TCN() {
		return tx_TCN;
	}

	public void setTx_TCN(int txTCN) {
		tx_TCN = txTCN;
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

}
