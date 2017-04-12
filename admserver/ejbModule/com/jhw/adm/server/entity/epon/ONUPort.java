package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * ONU�˿�
 * @author Snow
 *
 */
@Entity
@Table
public class ONUPort implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean portStatus;//�˿�״̬����
	private int operStatus;
	private int portDuplexStatus;//�˿�˫��״̬  1-full��2-half��3-auto
	private int portRate;//�˿�����  1-10M��2-100M��3-1000M��ONU��δ֧�֣�4-auto
	private boolean portSCStatus;//�˿�����״̬
	private boolean portRingCheck;//�˿ڻػ����
	private int portRateLimit;//�˿���������  64~100000kbps
	private int stormCType;//�籩��������   1-�㲥��2-�㲥���鲥��3-�㲥���鲥��δ֪����
	private int stormCThresholds;//�籩���Ʒ�ֵ  256~100000kbps
	private int cRowStatus;//������״̬ 
	private int maxNumLimit;//mac��������  1-63
	private String portName;//�˿�����
	private int protNo;//�˿ں�
	private int portType;
	private boolean syschorized=true;
	private ONUEntity oltonuEntity;
	private String descs;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isPortStatus() {
		return portStatus;
	}
	public void setPortStatus(boolean portStatus) {
		this.portStatus = portStatus;
	}
	public int getOperStatus() {
		return operStatus;
	}
	public void setOperStatus(int operStatus) {
		this.operStatus = operStatus;
	}
	public int getPortDuplexStatus() {
		return portDuplexStatus;
	}
	public void setPortDuplexStatus(int portDuplexStatus) {
		this.portDuplexStatus = portDuplexStatus;
	}
	public int getPortRate() {
		return portRate;
	}
	public void setPortRate(int portRate) {
		this.portRate = portRate;
	}
	public boolean isPortSCStatus() {
		return portSCStatus;
	}
	public void setPortSCStatus(boolean portSCStatus) {
		this.portSCStatus = portSCStatus;
	}
	public boolean isPortRingCheck() {
		return portRingCheck;
	}
	public void setPortRingCheck(boolean portRingCheck) {
		this.portRingCheck = portRingCheck;
	}
	public int getPortRateLimit() {
		return portRateLimit;
	}
	public void setPortRateLimit(int portRateLimit) {
		this.portRateLimit = portRateLimit;
	}
	public int getStormCType() {
		return stormCType;
	}
	public void setStormCType(int stormCType) {
		this.stormCType = stormCType;
	}
	public int getStormCThresholds() {
		return stormCThresholds;
	}
	public void setStormCThresholds(int stormCThresholds) {
		this.stormCThresholds = stormCThresholds;
	}
	public int getcRowStatus() {
		return cRowStatus;
	}
	public void setcRowStatus(int cRowStatus) {
		this.cRowStatus = cRowStatus;
	}
	public int getMaxNumLimit() {
		return maxNumLimit;
	}
	public void setMaxNumLimit(int maxNumLimit) {
		this.maxNumLimit = maxNumLimit;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
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
	public int getPortType() {
		return portType;
	}
	public void setPortType(int portType) {
		this.portType = portType;
	}
	@ManyToOne
	public ONUEntity getOltonuEntity() {
		return oltonuEntity;
	}
	public void setOltonuEntity(ONUEntity oltonuEntity) {
		this.oltonuEntity = oltonuEntity;
	}
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
//	private String phyAddress;//�����ַ
//	public String getPhyAddress() {
//		return phyAddress;
//	}
//	public void setPhyAddress(String phyAddress) {
//		this.phyAddress = phyAddress;
//	}
	
	
	
	
	

}
