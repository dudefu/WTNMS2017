package com.jhw.adm.server.entity.ports;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


/**
 * 端口安全配置
 * 
 * @author 杨霄
 * 
 */
@Entity
@Table(name = "PortSecurity")
public class PortSecurityConfigEntity  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long portId;
	private int aimStudy;
	private String macAddress;
	private Date shutTime;
	private boolean status;
	private String descs;
	private boolean ayylyed;
    
	private int issuedTag=0;//1：设备側  0：网管侧
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
	public boolean isAyylyed() {
		return ayylyed;
	}

	public void setAyylyed(boolean ayylyed) {
		this.ayylyed = ayylyed;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPortId() {
		return portId;
	}

	public void setPortId(Long portId) {
		this.portId = portId;
	}

	public int getAimStudy() {
		return aimStudy;
	}

	public void setAimStudy(int aimStudy) {
		this.aimStudy = aimStudy;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public Date getShutTime() {
		return shutTime;
	}

	public void setShutTime(Date shutTime) {
		this.shutTime = shutTime;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}
}
