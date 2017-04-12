package com.jhw.adm.server.entity.carriers;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * µçÁ¦¿Ú
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name="ElectricPort")
public class ElectricPortEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String portCode;
	private int status;
	private CarrierEntity carrier;
	private ElectricPortEntity nextPort;
	private String descs;

	@ManyToOne
	@JoinColumn(name = "CARRIER_ID", nullable = false)
	public CarrierEntity getCarrier() {
		return carrier;
	}

	public void setCarrier(CarrierEntity carrier) {
		this.carrier = carrier;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public ElectricPortEntity getNextPort() {
		return nextPort;
	}

	public void setNextPort(ElectricPortEntity nextPort) {
		this.nextPort = nextPort;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

}
