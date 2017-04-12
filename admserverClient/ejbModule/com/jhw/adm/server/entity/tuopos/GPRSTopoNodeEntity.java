package com.jhw.adm.server.entity.tuopos;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jhw.adm.server.entity.util.GPRSEntity;

@Entity
@Table(name = "topogprsnode")
@DiscriminatorValue(value = "GP")
public class GPRSTopoNodeEntity extends NodeEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GPRSEntity entity;
	private String userId;
	private String guid;
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Transient
	public GPRSEntity getEntity() {
		return entity;
	}

	public void setEntity(GPRSEntity entity) {
		this.entity = entity;
	}

}
