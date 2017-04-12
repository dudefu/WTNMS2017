/**
 * 
 */
package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

/**
 * @author ×ó¾üÓÂ
 * @Ê±¼ä 2010-7-15
 */
@Entity
public class OLTBaseInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String company;
	private String deviceName;
	private String address;
	private boolean syschorized = true;
	private String descs;
	private OLTEntity oltEntity;
	private String userNames;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public String getUserNames() {
		return userNames;
	}

	@OneToOne
	public OLTEntity getOltEntity() {
		return oltEntity;
	}

	public void setOltEntity(OLTEntity oltEntity) {
		this.oltEntity = oltEntity;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}
	
	public void copy(OLTBaseInfo oltBaseInfo){
		this.setAddress(oltBaseInfo.getAddress());
		this.setCompany(oltBaseInfo.getCompany());
		this.setDescs(oltBaseInfo.getDescs());
		this.setDeviceName(oltBaseInfo.getDeviceName());
		this.setUserNames(oltBaseInfo.getUserNames());
		this.setSyschorized(oltBaseInfo.isSyschorized());
		
	}
}
