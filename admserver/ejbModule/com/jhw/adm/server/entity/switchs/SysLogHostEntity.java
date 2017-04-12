package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SysLogHost")
public class SysLogHostEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String hostIp;     //����IP
	private int hostPort;   //�����˿�
	
	private String descs;   
	private boolean syschorized=true;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * ����IP
	 * @return
	 */
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	
	/**
	 * �����˿�
	 * @return
	 */
	public int getHostPort() {
		return hostPort;
	}
	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}
	
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
	}
	
	public boolean isSyschorized() {
		return syschorized;
	}
	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}
	
	
}
