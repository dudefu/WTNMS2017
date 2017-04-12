package com.jhw.adm.server.entity.level3;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="Switch3STPPort")
@Entity
public class Switch3STPPortEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portID;
	private String portName;
	private int priorityLevel;// ���ȼ�
	private int status;// Э��״̬  2:ʹ��  1:��ʹ��
	private int pathCost;// ·���ɱ�
	private int edgePort;// ��Ե�˿����� 2���Զ���� 1��ǿ����Ч  0��ǿ����Ч
	private boolean syschorized=true;
	private String descs;
	private int issuedTag=0;//1���豸��  0�����ܲ�
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
	public int getPortID() {
		return portID;
	}
	public void setPortID(int portID) {
		this.portID = portID;
	}
	public int getPriorityLevel() {
		return priorityLevel;
	}
	public void setPriorityLevel(int priorityLevel) {
		this.priorityLevel = priorityLevel;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getPathCost() {
		return pathCost;
	}
	public void setPathCost(int pathCost) {
		this.pathCost = pathCost;
	}
	public int getEdgePort() {
		return edgePort;
	}
	public void setEdgePort(int edgePort) {
		this.edgePort = edgePort;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
    
	
	
}
