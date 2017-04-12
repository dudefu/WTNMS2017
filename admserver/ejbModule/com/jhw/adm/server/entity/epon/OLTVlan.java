package com.jhw.adm.server.entity.epon;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class OLTVlan implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String vlanName;
	private String vlanID;
	private int vlanMode;
	private String egressPort;// 出口
	private String forbiddenEgressPort;// 禁止的出口
	private String untaggedPort;
	private boolean syschorized = true;
	private String descs;
	private Set<OLTVlanPort> ports;
	
	private OLTEntity oltEntity;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVlanName() {
		return vlanName;
	}

	public void setVlanName(String vlanName) {
		this.vlanName = vlanName;
	}

	public String getVlanID() {
		return vlanID;
	}

	public void setVlanID(String vlanID) {
		this.vlanID = vlanID;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public String getEgressPort() {
		return egressPort;
	}

	public void setEgressPort(String egressPort) {
		this.egressPort = egressPort;
	}

	public String getForbiddenEgressPort() {
		return forbiddenEgressPort;
	}

	public void setForbiddenEgressPort(String forbiddenEgressPort) {
		this.forbiddenEgressPort = forbiddenEgressPort;
	}

	public String getUntaggedPort() {
		return untaggedPort;
	}

	public void setUntaggedPort(String untaggedPort) {
		this.untaggedPort = untaggedPort;
	}

	public int getVlanMode() {
		return vlanMode;
	}

	public void setVlanMode(int vlanMode) {
		this.vlanMode = vlanMode;
	}

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	public Set<OLTVlanPort> getPorts() {
		return ports;
	}

	public void setPorts(Set<OLTVlanPort> ports) {
		this.ports = ports;
	}
    
	@ManyToOne
	public OLTEntity getOltEntity() {
		return oltEntity;
	}

	public void setOltEntity(OLTEntity oltEntity) {
		this.oltEntity = oltEntity;
	}

	

}
