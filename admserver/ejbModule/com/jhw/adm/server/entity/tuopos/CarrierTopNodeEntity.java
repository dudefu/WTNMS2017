package com.jhw.adm.server.entity.tuopos;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jhw.adm.server.entity.carriers.CarrierEntity;

@Entity
@Table(name = "topcarriernode")
@DiscriminatorValue(value = "CR")
public class CarrierTopNodeEntity extends NodeEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CarrierEntity nodeEntity;
	private int carrierCode;
	private int type;
	private String guid;
	private int carrierType;
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(int carrierCode) {
		this.carrierCode = carrierCode;
	}

	@Transient
	public CarrierEntity getNodeEntity() {
		return nodeEntity;
	}

	public void setNodeEntity(CarrierEntity nodeEntity) {
		this.nodeEntity = nodeEntity;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCarrierType() {
		return carrierType;
	}

	public void setCarrierType(int carrierType) {
		this.carrierType = carrierType;
	}
    
}
