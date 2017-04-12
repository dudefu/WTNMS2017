package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

@Entity
@Table(name = "SSerialPort")
public class SwitchSerialPort implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private String portName;
	private float baudRate;// 波特率
	private boolean flowControl;
	private int databit;// 数据位
	private int stopbit;// 停止位
	private int checkbit;// 校验位：none/odd/even
	private String descs;

	private int serialMode;// 串口模式：tcpserver/tcpclient/udpserver/udpclient
	private String tcpclientRemoteIP;
	private String tcpclientRemotePort;
	private String tcpserverLocalPort;
	private String udpclientRemotePort;
	private String udpclientRemoteIp;
	private String udpserverLocalPort;
	private boolean syschorized = true;

	private SwitchNodeEntity switchNode;
	private int issuedTag=0;//1：设备側  0：网管侧
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
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

	public int getSerialMode() {
		return serialMode;
	}

	public void setSerialMode(int serialMode) {
		this.serialMode = serialMode;
	}

	public String getTcpclientRemoteIP() {
		return tcpclientRemoteIP;
	}

	public void setTcpclientRemoteIP(String tcpclientRemoteIP) {
		this.tcpclientRemoteIP = tcpclientRemoteIP;
	}

	public String getTcpclientRemotePort() {
		return tcpclientRemotePort;
	}

	public void setTcpclientRemotePort(String tcpclientRemotePort) {
		this.tcpclientRemotePort = tcpclientRemotePort;
	}

	public String getTcpserverLocalPort() {
		return tcpserverLocalPort;
	}

	public void setTcpserverLocalPort(String tcpserverLocalPort) {
		this.tcpserverLocalPort = tcpserverLocalPort;
	}

	public String getUdpclientRemotePort() {
		return udpclientRemotePort;
	}

	public void setUdpclientRemotePort(String udpclientRemotePort) {
		this.udpclientRemotePort = udpclientRemotePort;
	}

	public String getUdpclientRemoteIp() {
		return udpclientRemoteIp;
	}

	public void setUdpclientRemoteIp(String udpclientRemoteIp) {
		this.udpclientRemoteIp = udpclientRemoteIp;
	}

	public String getUdpserverLocalPort() {
		return udpserverLocalPort;
	}

	public void setUdpserverLocalPort(String udpserverLocalPort) {
		this.udpserverLocalPort = udpserverLocalPort;
	}

	@ManyToOne
	@JoinColumn(name = "SWITCHID")
	public SwitchNodeEntity getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(SwitchNodeEntity switchNode) {
		this.switchNode = switchNode;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

}
