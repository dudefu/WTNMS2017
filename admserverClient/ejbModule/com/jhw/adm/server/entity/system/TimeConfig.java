package com.jhw.adm.server.entity.system;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TimeConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int heartbeatMaxTime;
	private int hearbeatdelayMaxTime;
	private int synchoizeMaxTime;
	private int tuopoMaxTime;
	private int parmConfigMaxTime;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getHeartbeatMaxTime() {
		return heartbeatMaxTime;
	}

	public void setHeartbeatMaxTime(int heartbeatMaxTime) {
		this.heartbeatMaxTime = heartbeatMaxTime;
	}

	public int getHearbeatdelayMaxTime() {
		return hearbeatdelayMaxTime;
	}

	public void setHearbeatdelayMaxTime(int hearbeatdelayMaxTime) {
		this.hearbeatdelayMaxTime = hearbeatdelayMaxTime;
	}

	public int getSynchoizeMaxTime() {
		return synchoizeMaxTime;
	}

	public void setSynchoizeMaxTime(int synchoizeMaxTime) {
		this.synchoizeMaxTime = synchoizeMaxTime;
	}

	public int getTuopoMaxTime() {
		return tuopoMaxTime;
	}

	public void setTuopoMaxTime(int tuopoMaxTime) {
		this.tuopoMaxTime = tuopoMaxTime;
	}

	public int getParmConfigMaxTime() {
		return parmConfigMaxTime;
	}

	public void setParmConfigMaxTime(int parmConfigMaxTime) {
		this.parmConfigMaxTime = parmConfigMaxTime;
	}

}
