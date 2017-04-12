package com.jhw.adm.server.entity.tuopos;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.epon.EponSplitter;

/**
 * ·Ö¹âÆ÷Topo
 * @author Snow
 *
 */
@Entity
@Table(name = "topoepon_s_node")
@DiscriminatorValue(value = "E_S")
public class Epon_S_TopNodeEntity extends NodeEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String guid;
	private EponSplitter eponSplitter;
	
	private String oltGuid;  
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@OneToOne(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
	public EponSplitter getEponSplitter() {
		return eponSplitter;
	}
	public void setEponSplitter(EponSplitter eponSplitter) {
		this.eponSplitter = eponSplitter;
	}
	public String getOltGuid() {
		return oltGuid;
	}
	public void setOltGuid(String oltGuid) {
		this.oltGuid = oltGuid;
	}
	
}
