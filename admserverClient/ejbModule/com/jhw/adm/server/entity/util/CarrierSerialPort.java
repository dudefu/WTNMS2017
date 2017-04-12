package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.carriers.CarrierEntity;

@Entity
@Table(name = "CSerialPort")
public class CarrierSerialPort implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String portCode;
	private String portName;
	private float baudRate;// 波特率
	private boolean flowControl;
	private int databit;// 数据位
	private int stopbit;// 停止位
	private int checkbit;// 校验位：none/odd/even
	private String descs;

	private int resendtimes;// 重传次数
	private int send_inter_arrival_time;// 重传间隔时间
	private int status;

	private CarrierEntity carrier;

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

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public float getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(float baudRate) {
		this.baudRate = baudRate;
	}

	public boolean isFlowControl() {
		return flowControl;
	}

	public void setFlowControl(boolean flowControl) {
		this.flowControl = flowControl;
	}

	public int getDatabit() {
		return databit;
	}

	public void setDatabit(int databit) {
		this.databit = databit;
	}

	public int getStopbit() {
		return stopbit;
	}

	public void setStopbit(int stopbit) {
		this.stopbit = stopbit;
	}

	public int getCheckbit() {
		return checkbit;
	}

	public void setCheckbit(int checkbit) {
		this.checkbit = checkbit;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public int getResendtimes() {
		return resendtimes;
	}

	public void setResendtimes(int resendtimes) {
		this.resendtimes = resendtimes;
	}

	public int getSend_inter_arrival_time() {
		return send_inter_arrival_time;
	}

	public void setSend_inter_arrival_time(int sendInterArrivalTime) {
		send_inter_arrival_time = sendInterArrivalTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@ManyToOne
	@JoinColumn(name="carrierId")
	public CarrierEntity getCarrier() {
		return carrier;
	}

	public void setCarrier(CarrierEntity carrier) {
		this.carrier = carrier;
	}
}
