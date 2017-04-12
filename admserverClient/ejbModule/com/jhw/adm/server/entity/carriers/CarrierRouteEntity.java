package com.jhw.adm.server.entity.carriers;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 路由表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "CarrierRoute")
public class CarrierRouteEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int port;//自己侧
	private int carrierCode;//对方
	private CarrierEntity carrier;//自身
	private String descs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(int carrierCode) {
		this.carrierCode = carrierCode;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "carrierId")
	public CarrierEntity getCarrier() {
		return carrier;
	}

	public void setCarrier(CarrierEntity carrier) {
		this.carrier = carrier;
	}
}
