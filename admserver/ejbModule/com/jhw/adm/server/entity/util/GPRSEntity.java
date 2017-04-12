package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class GPRSEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String userId;// DTU���ʶ����
	private AddressEntity address;
	private String internateAddress;// DTU����Internet�Ĵ�������IP��ַ(���ǹ�˾��������ַ121.15.13.33,����䶯)
	private int port;// DTU����Internet�Ĵ�������IP�˿�(��䶯)
	private String localAddress;// DTU���ƶ�����IP��ַ(��䶯)
	private int localPort;// DTU���ƶ�����IP�˿�(��䶯)
	private String logonDate;// DTU��¼ʱ��
	private int status;// DTU״̬, 1 ���� ��0 ������
	private String guid;
	private String descs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@ManyToOne(cascade={CascadeType.ALL})
	@JoinColumn(name = "addressID")
	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}

	public String getInternateAddress() {
		return internateAddress;
	}

	public void setInternateAddress(String internateAddress) {
		this.internateAddress = internateAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public String getLogonDate() {
		return logonDate;
	}

	public void setLogonDate(String logonDate) {
		this.logonDate = logonDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

}
