package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class ONUVlanPortConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private int pvid;
	private int priority;
	private boolean syschorized = true;
	private ONUEntity onuEntity;
	private String descs;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public int getPvid() {
		return pvid;
	}

	public void setPvid(int pvid) {
		this.pvid = pvid;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
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

	
    @ManyToOne
	public ONUEntity getOnuEntity() {
		return onuEntity;
	}

	public void setOnuEntity(ONUEntity onuEntity) {
		this.onuEntity = onuEntity;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
