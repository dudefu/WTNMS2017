package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "Igmp_vsi")
public class Igmp_vsi  implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String vlanId;
	private boolean snooping;
	private boolean querier;
	private String descs;
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

	public String getVlanId() {
		return vlanId;
	}

	public void setVlanId(String vlanId) {
		this.vlanId = vlanId;
	}

	public boolean isSnooping() {
		return snooping;
	}

	public void setSnooping(boolean snooping) {
		this.snooping = snooping;
	}

	public boolean isQuerier() {
		return querier;
	}

	public void setQuerier(boolean querier) {
		this.querier = querier;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
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

}
