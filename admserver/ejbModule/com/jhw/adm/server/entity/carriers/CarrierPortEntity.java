package com.jhw.adm.server.entity.carriers;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "CarrierPort")
public class CarrierPortEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portCode;
	private int portType;
	private int baudRate;// 波特率
	private int dataBit;//数据位值  
	private int stopBit;//停止位
	private int verify;//校验位
	private int subnetCode;//子网编号
	private String descs;
	private CarrierEntity carrier;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public int getPortCode() {
		return portCode;
	}

	public void setPortCode(int portCode) {
		this.portCode = portCode;
	}

	public int getPortType() {
		return portType;
	}

	public void setPortType(int portType) {
		this.portType = portType;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	@ManyToOne
	@JoinColumn(name="carrierId")
	public CarrierEntity getCarrier() {
		return carrier;
	}

	public void setCarrier(CarrierEntity carrier) {
		this.carrier = carrier;
	}

	public int getDataBit() {
		return dataBit;
	}

	public void setDataBit(int dataBit) {
		this.dataBit = dataBit;
	}

	public int getStopBit() {
		return stopBit;
	}

	public void setStopBit(int stopBit) {
		this.stopBit = stopBit;
	}

	public int getVerify() {
		return verify;
	}

	public void setVerify(int verify) {
		this.verify = verify;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getSubnetCode() {
		return subnetCode;
	}

	public void setSubnetCode(int subnetCode) {
		this.subnetCode = subnetCode;
	}
	
}
