package com.jhw.adm.server.entity.nets;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

@Entity
@Table(name = "VlanConfig")
public class VlanConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Set<VlanEntity> vlanEntitys;
	private Set<VlanPort> vlanPorts;
	private SwitchNodeEntity switchNode;
	private boolean syschorized=true;
	private String descs;
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "SWITCHID")
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
	}

	@OneToMany(mappedBy="vlanConfig",fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<VlanEntity> getVlanEntitys() {
		return vlanEntitys;
	}

	public void setVlanEntitys(Set<VlanEntity> vlanEntitys) {
		this.vlanEntitys = vlanEntitys;
	}

	@OneToMany(mappedBy="vlanConfig",fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<VlanPort> getVlanPorts() {
		return vlanPorts;
	}

	public void setVlanPorts(Set<VlanPort> vlanPorts) {
		this.vlanPorts = vlanPorts;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

}
