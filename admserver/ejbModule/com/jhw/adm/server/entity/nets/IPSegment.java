package com.jhw.adm.server.entity.nets;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "IPSegment")
public class IPSegment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String beginIp;
	private String endIp;
	private FEPEntity fepEntity;
	private String descs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBeginIp() {
		return beginIp;
	}

	public void setBeginIp(String beginIp) {
		this.beginIp = beginIp;
	}

	public String getEndIp() {
		return endIp;
	}

	public void setEndIp(String endIp) {
		this.endIp = endIp;
	}

	@ManyToOne
	@JoinColumn(name = "FEPID")
	public FEPEntity getFepEntity() {
		return fepEntity;
	}

	public void setFepEntity(FEPEntity fepEntity) {
		this.fepEntity = fepEntity;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public String getDescs() {
		return descs;
	}

}
