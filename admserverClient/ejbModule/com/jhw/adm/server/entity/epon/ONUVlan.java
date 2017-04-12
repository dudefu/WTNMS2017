package com.jhw.adm.server.entity.epon;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * ONUVLan
 * 
 * @author Snow
 * 
 */
@Entity
@Table
public class ONUVlan implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int vlanMobile;// 0 transparent 1 tag 2 none
	private boolean syschorized = true;
	private String vlanName;
	private String vlanID;
	private Set<ONUVlanPort> onuPorts;
	private String descs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVlanMobile() {
		return vlanMobile;
	}

	public void setVlanMobile(int vlanMobile) {
		this.vlanMobile = vlanMobile;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@OneToMany(fetch =FetchType.EAGER,cascade=CascadeType.ALL)
	public Set<ONUVlanPort> getOnuPorts() {
		return onuPorts;
	}

	public void setOnuPorts(Set<ONUVlanPort> onuPorts) {
		this.onuPorts = onuPorts;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

}
