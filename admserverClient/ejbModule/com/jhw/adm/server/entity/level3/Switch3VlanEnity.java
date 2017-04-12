package com.jhw.adm.server.entity.level3;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Table(name="Switch3Vlan")
@Entity
public class Switch3VlanEnity  implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean syschorized = true;
	private String vlanName;
	private int vlanID;
	private Set<Switch3VlanPortEntity> vlanPortEntities =new HashSet<Switch3VlanPortEntity>();
	private String descs;
	private String ipValue;
	
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
	public int getVlanID() {
		return vlanID;
	}
	public void setVlanID(int vlanID) {
		this.vlanID = vlanID;
	}
	
	@OneToMany(fetch =FetchType.EAGER,cascade=CascadeType.ALL)
	public Set<Switch3VlanPortEntity> getVlanPortEntities() {
		return vlanPortEntities;
	}
	public void setVlanPortEntities(Set<Switch3VlanPortEntity> vlanPortEntities) {
		this.vlanPortEntities = vlanPortEntities;
	}
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getIpValue() {
		return ipValue;
	}
	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}
	
}
