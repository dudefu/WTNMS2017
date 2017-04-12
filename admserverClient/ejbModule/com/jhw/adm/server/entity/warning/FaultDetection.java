package com.jhw.adm.server.entity.warning;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FaultDetection implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Date beginTime;
	private Date endTime;
	private boolean warning;
	private boolean started;
	private int pinglv;
	private int pingtimes;
	private int jiange;
	private int lastpinglv;
	private String ipvlaues;
	private String descs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isWarning() {
		return warning;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public int getPinglv() {
		return pinglv;
	}

	public void setPinglv(int pinglv) {
		this.pinglv = pinglv;
	}

	public int getPingtimes() {
		return pingtimes;
	}

	public void setPingtimes(int pingtimes) {
		this.pingtimes = pingtimes;
	}

	public int getJiange() {
		return jiange;
	}

	public void setJiange(int jiange) {
		this.jiange = jiange;
	}

	public String getIpvlaues() {
		return ipvlaues;
	}

	public void setIpvlaues(String ipvlaues) {
		this.ipvlaues = ipvlaues;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getLastpinglv() {
		return lastpinglv;
	}

	public void setLastpinglv(int lastpinglv) {
		this.lastpinglv = lastpinglv;
	}
   
	
	
}
