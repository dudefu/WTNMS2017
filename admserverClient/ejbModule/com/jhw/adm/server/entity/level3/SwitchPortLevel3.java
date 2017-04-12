package com.jhw.adm.server.entity.level3;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class SwitchPortLevel3  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;      //�˿ں�
	private String portName; //�˿�����
	private String enableStr; //ʹ��
	private int connect;    //����״̬   1-����  2-�Ͽ�
	private String macValue;  //mac��ַ
	private String rate;        //����  100/1000 ��λ:Mb/s
	private int duplex;      //˫��ģʽ   1-ȫ˫�� 2-��˫��
	private int flowControl; //����   1-�� 2-�ر�
	
	
	private String ipValue;  //�˿�IP
	private int slot;       //�ۺ�
	private int portType;   //�˿�����
	
	private String switchLayer3Ip; //���㽻����IP
	
	private int issuedTag=0;//1���豸��  0�����ܲ�
	private String descs;
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getEnableStr() {
		return enableStr;
	}

	public void setEnableStr(String enableStr) {
		this.enableStr = enableStr;
	}

	public int getConnect() {
		return connect;
	}

	public void setConnect(int connect) {
		this.connect = connect;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getDuplex() {
		return duplex;
	}

	public void setDuplex(int duplex) {
		this.duplex = duplex;
	}

	public int getFlowControl() {
		return flowControl;
	}

	public void setFlowControl(int flowControl) {
		this.flowControl = flowControl;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public String getMacValue() {
		return macValue;
	}

	public void setMacValue(String macValue) {
		this.macValue = macValue;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public int getPortType() {
		return portType;
	}

	public void setPortType(int portType) {
		this.portType = portType;
	}

	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}

	/**
	 * ���㽻����IP
	 * @return
	 */
	public String getSwitchLayer3Ip() {
		return switchLayer3Ip;
	}

	public void setSwitchLayer3Ip(String switchLayer3Ip) {
		this.switchLayer3Ip = switchLayer3Ip;
	}

}
