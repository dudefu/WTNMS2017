package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


/**
 * tos优先级配置:qos
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "PriorityTOS")
public class PriorityTOS  implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private int priorityLevel;// 0...7
	private int queueValue;
	private String descs;
	private boolean syschorized=true;

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}
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

	public int getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(int priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	public int getQueueValue() {
		return queueValue;
	}

	public void setQueueValue(int queueValue) {
		this.queueValue = queueValue;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
