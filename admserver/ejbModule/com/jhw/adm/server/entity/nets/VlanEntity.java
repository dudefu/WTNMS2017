package com.jhw.adm.server.entity.nets;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * vlan ≈‰÷√
 * 
 * @author —Óœˆ
 * 
 */
@Entity
@Table(name = "VlanEntity")
public class VlanEntity  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int vlanID;
	private String vlanName;
	private Set<VlanPortConfig> portConfig;
	private VlanConfig vlanConfig;
	private boolean syschorized=true;
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

	public int getVlanID() {
		return vlanID;
	}

	public void setVlanID(int vlanID) {
		this.vlanID = vlanID;
	}

	public String getVlanName() {
		return vlanName;
	}

	public void setVlanName(String vlanName) {
		this.vlanName = vlanName;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Set<VlanPortConfig> getPortConfig() {
		return portConfig;
	}

	public void setPortConfig(Set<VlanPortConfig> portConfig) {
		this.portConfig = portConfig;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	public VlanConfig getVlanConfig() {
		return vlanConfig;
	}
	
	public void setVlanConfig(VlanConfig vlanConfig) {
		this.vlanConfig = vlanConfig;
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
