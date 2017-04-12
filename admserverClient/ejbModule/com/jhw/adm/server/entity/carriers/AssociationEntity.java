package com.jhw.adm.server.entity.carriers;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="CarrierAssociation")
public class AssociationEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private int preCarrierCode;
	private int preCarrierPortCode;
	private int localPortCode;
	private String descs;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public int getPreCarrierCode() {
		return preCarrierCode;
	}

	public void setPreCarrierCode(int preCarrierCode) {
		this.preCarrierCode = preCarrierCode;
	}

	public int getPreCarrierPortCode() {
		return preCarrierPortCode;
	}

	public void setPreCarrierPortCode(int preCarrierPortCode) {
		this.preCarrierPortCode = preCarrierPortCode;
	}

	public int getLocalPortCode() {
		return localPortCode;
	}

	public void setLocalPortCode(int localPortCode) {
		this.localPortCode = localPortCode;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

}
