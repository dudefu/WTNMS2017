package com.jhw.adm.server.entity.ports;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

/**
 * lldp≈‰÷√
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "LLDPConfig")
public class LLDPConfig  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private SwitchNodeEntity switchNode;
	private int tx_Interval;
	private int tx_Hold;
	private int tx_Delay;
	private int tx_Reinit;
	private boolean syschorized=true;
	private Set<LLDPPortConfig> lldpPortConfigs;
	private String descs;
	
	private int issuedTag=0;//1£∫…Ë±∏Ç»  0£∫Õ¯π‹≤‡
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

	public int getTx_Interval() {
		return tx_Interval;
	}

	public void setTx_Interval(int txInterval) {
		tx_Interval = txInterval;
	}

	public int getTx_Hold() {
		return tx_Hold;
	}

	public void setTx_Hold(int txHold) {
		tx_Hold = txHold;
	}

	public int getTx_Delay() {
		return tx_Delay;
	}

	public void setTx_Delay(int txDelay) {
		tx_Delay = txDelay;
	}

	public int getTx_Reinit() {
		return tx_Reinit;
	}

	public void setTx_Reinit(int txReinit) {
		tx_Reinit = txReinit;
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

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<LLDPPortConfig> getLldpPortConfigs() {
		return lldpPortConfigs;
	}

	public void setLldpPortConfigs(Set<LLDPPortConfig> lldpPortConfigs) {
		this.lldpPortConfigs = lldpPortConfigs;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
