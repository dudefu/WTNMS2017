package com.jhw.adm.server.entity.tuopos;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * ��ʵ��
 * @author zuojunyong
 *
 */
@Entity
@Table(name = "toporing")
@DiscriminatorValue(value = "RI")
public class RingEntity extends NodeEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ring_guid;
	private int ringNo;//��ס���������
	private int portNo;//��ס�����������˿�
	private String ip_Value;//��ס����������IP
	public int getPortNo() {
		return portNo;
	}
	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getRing_guid() {
		return ring_guid;
	}
	public void setRing_guid(String ringGuid) {
		ring_guid = ringGuid;
	}
	
	public int getRingNo() {
		return ringNo;
	}
	public void setRingNo(int ringNo) {
		this.ringNo = ringNo;
	}
	public String getIp_Value() {
		return ip_Value;
	}
	public void setIp_Value(String ipValue) {
		ip_Value = ipValue;
	}
	
    
	
	
}
