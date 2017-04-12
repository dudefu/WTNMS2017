package com.jhw.adm.server.entity.warning;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SysLogWarningEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String ipValue;     //�豸ip��ַ
	private int warningEvent;   //�澯�¼�
	private String syslogType;  //syslog����   ��lldp��port��
	private String manageUser;  //�����������û�
	private Date currentTime;   //�澯ʱ��
	private String contents;    //����
	private String descs;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * �豸ip��ַ
	 * @return
	 */
	public String getIpValue() {
		return ipValue;
	}
	/**
	 * �豸ip��ַ
	 * @param ipValue
	 */
	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}
	
	/**
	 * �澯�¼�
	 * @return
	 */
	public int getWarningEvent() {
		return warningEvent;
	}
	/**
	 * �澯�¼�
	 * @param warningEvent
	 */
	public void setWarningEvent(int warningEvent) {
		this.warningEvent = warningEvent;
	}
	
	/**
	 * �����������û�
	 * @return
	 */
	public String getManageUser() {
		return manageUser;
	}
	/**
	 * �����������û�
	 * @param manageUser
	 */
	public void setManageUser(String manageUser) {
		this.manageUser = manageUser;
	}
	
	/**
	 * �澯ʱ��
	 * @return
	 */
	public Date getCurrentTime() {
		return currentTime;
	}
	/**
	 * �澯ʱ��
	 * @param currentTime
	 */
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	
	/**
	 * ����
	 * @return
	 */
	public String getContents() {
		return contents;
	}
	/**
	 * ����
	 * @param contents
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}
	
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
	
	/**
	 * syslog����   ��lldp��port��
	 * @return
	 */
	public String getSyslogType() {
		return syslogType;
	}
	public void setSyslogType(String syslogType) {
		this.syslogType = syslogType;
	}
	
	
}
