package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Epon�˿�
 * 
 * @author Snow
 * 
 */
@Entity
public class OLTPort implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private int protNo;
	private String portName;     //�˿�����
	private String enableStr; //ʹ��
	private int connect;    //����״̬   1-����  2-�Ͽ�
	private String macValue;  //mac��ַ
	private String rate;        //����  100/1000 ��λ:Mb/s
	private int duplex;      //˫��ģʽ   1-ȫ˫�� 2-��˫��
	private int flowControl; //����   1-�� 2-�ر�
	
	private String portIP;    //�˿�IP
	
	private int slotNum;// ���
	private String portType;
	private int diID;// ����
	private boolean portStatus;
	private boolean syschorized = true;
	private OLTEntity oltEntity;
	private String descs;
	private int issuedTag=0;//1���豸��  0�����ܲ�

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

	public String getMacValue() {
		return macValue;
	}

	public void setMacValue(String macValue) {
		this.macValue = macValue;
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

	public int getProtNo() {
		return protNo;
	}

	public void setProtNo(int protNo) {
		this.protNo = protNo;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	@ManyToOne
	public OLTEntity getOltEntity() {
		return oltEntity;
	}

	public void setOltEntity(OLTEntity oltEntity) {
		this.oltEntity = oltEntity;
	}

	public boolean isPortStatus() {
		return portStatus;
	}

	public void setPortStatus(boolean portStatus) {
		this.portStatus = portStatus;
	}

	public int getDiID() {
		return diID;
	}

	public void setDiID(int diID) {
		this.diID = diID;
	}

	public int getSlotNum() {
		return slotNum;
	}

	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}

	/**
	 * �˿�IP
	 * @return
	 */
	public String getPortIP() {
		return portIP;
	}

	public void setPortIP(String portIP) {
		this.portIP = portIP;
	}
}
