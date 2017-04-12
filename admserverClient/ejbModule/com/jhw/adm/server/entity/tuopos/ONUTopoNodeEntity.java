package com.jhw.adm.server.entity.tuopos;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jhw.adm.server.entity.epon.ONUEntity;

@Entity
@Table(name = "topoonunode")
@DiscriminatorValue(value = "ON")
public class ONUTopoNodeEntity extends NodeEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String guid;
	private ONUEntity onuEntity;
	private int SequenceNo;
	private String macValue;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public int getSequenceNo() {
		return SequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		SequenceNo = sequenceNo;
	}

	public String getMacValue() {
		return macValue;
	}

	public void setMacValue(String macValue) {
		this.macValue = macValue;
	}

	@Transient
	public ONUEntity getOnuEntity() {
		return onuEntity;
	}

	public void setOnuEntity(ONUEntity onuEntity) {
		this.onuEntity = onuEntity;
	}
}
