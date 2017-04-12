package com.jhw.adm.server.entity.ports;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "LLDPPortConfig")
public class LLDPPortConfig implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int lldpWork;// lldp���ã�����/����/������/������
	private Long id;
	private boolean syschorized = true;
	private int portNum;
	private String descs;
	private int issuedTag=0;//1���豸��  0�����ܲ�
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	
	public int getLldpWork() {
		return lldpWork;
	}

	public void setLldpWork(int lldpWork) {
		this.lldpWork = lldpWork;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public int getPortNum() {
		return portNum;
	}

	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
    
	
}
