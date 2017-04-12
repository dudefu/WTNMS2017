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
	private String ipValue;     //设备ip地址
	private int warningEvent;   //告警事件
	private String syslogType;  //syslog类型   有lldp、port等
	private String manageUser;  //交换机管理用户
	private Date currentTime;   //告警时间
	private String contents;    //内容
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
	 * 设备ip地址
	 * @return
	 */
	public String getIpValue() {
		return ipValue;
	}
	/**
	 * 设备ip地址
	 * @param ipValue
	 */
	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}
	
	/**
	 * 告警事件
	 * @return
	 */
	public int getWarningEvent() {
		return warningEvent;
	}
	/**
	 * 告警事件
	 * @param warningEvent
	 */
	public void setWarningEvent(int warningEvent) {
		this.warningEvent = warningEvent;
	}
	
	/**
	 * 交换机管理用户
	 * @return
	 */
	public String getManageUser() {
		return manageUser;
	}
	/**
	 * 交换机管理用户
	 * @param manageUser
	 */
	public void setManageUser(String manageUser) {
		this.manageUser = manageUser;
	}
	
	/**
	 * 告警时间
	 * @return
	 */
	public Date getCurrentTime() {
		return currentTime;
	}
	/**
	 * 告警时间
	 * @param currentTime
	 */
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	
	/**
	 * 内容
	 * @return
	 */
	public String getContents() {
		return contents;
	}
	/**
	 * 内容
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
	 * syslog类型   有lldp、port等
	 * @return
	 */
	public String getSyslogType() {
		return syslogType;
	}
	public void setSyslogType(String syslogType) {
		this.syslogType = syslogType;
	}
	
	
}
