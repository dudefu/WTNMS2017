package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * sntp配置
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "SNTPConfig")
public class SNTPConfigEntity  implements Serializable {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String firstServerIP;
	private String secondServerIP;
	private int btSeconds;// 间隔时间：秒
	private String timeArea;// 时区
	private String currentTime;
	private boolean applied;
	private SwitchNodeEntity switchNode;
	private boolean syschorized=true;
	private String descs;
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

	public String getFirstServerIP() {
		return firstServerIP;
	}

	public void setFirstServerIP(String firstServerIP) {
		this.firstServerIP = firstServerIP;
	}

	public String getSecondServerIP() {
		return secondServerIP;
	}

	public void setSecondServerIP(String secondServerIP) {
		this.secondServerIP = secondServerIP;
	}

	public int getBtSeconds() {
		return btSeconds;
	}

	public void setBtSeconds(int btSeconds) {
		this.btSeconds = btSeconds;
	}

	public String getTimeArea() {
		return timeArea;
	}

	public void setTimeArea(String timeArea) {
		this.timeArea = timeArea;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
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
