package com.jhw.adm.server.entity.warning;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * 查询用的告警信息
 * @author Snow
 *
 */
@Entity
public class TrapSimpleWarning implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String deviceName;
	private int warningType;
	private int currentStatus;
	private String ipValue;
	private String descs;
	private Date createDate;
	private int warningLevel;
	private WarningFixStatus fixStatus;
	private WarningCloseStatus closeStatus;
	private int portNo;
	private String paramName;
	private long value;
	public int getPortNo() {
		return portNo;
	}
	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public int getWarningType() {
		return warningType;
	}
	public void setWarningType(int warningType) {
		this.warningType = warningType;
	}
	public int getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}
	public String getIpValue() {
		return ipValue;
	}
	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
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
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public int getWarningLevel() {
		return warningLevel;
	}
	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "FIXID")
	public WarningFixStatus getFixStatus() {
		return fixStatus;
	}

	public void setFixStatus(WarningFixStatus fixStatus) {
		this.fixStatus = fixStatus;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "CLOSEID")
	public WarningCloseStatus getCloseStatus() {
		return closeStatus;
	}

	public void setCloseStatus(WarningCloseStatus closeStatus) {
		this.closeStatus = closeStatus;
	}

}
