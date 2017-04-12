package com.jhw.adm.server.entity.epon;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.util.AddressEntity;

/**
 * OLT ONU配置
 * 
 * @author Snow
 */
@Entity
public class ONUEntity implements Serializable {
	/**
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Set<ONUPort> onuPorts;
	private Set<ONUVlanPortConfig> portConfigs;
	private String software_Version;
	private String hardware_Version;
	private boolean syschorized = true;
	private int nowStatus; // onu状态 1-registered, 0-deregistered,
	private String macValue;// OLT 分配的Mac地址
	private String descs;// ONU描述
	private int distance;// onu与olt之间的距离
	private int bingType;// 绑定类型
	private AddressEntity addressEntity;
	private int SequenceNo;
	private Set<LLDPInofEntity> lldpinfos;
	
	private int deviceModel;//设备型号

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "oltonuEntity")
	public Set<ONUPort> getOnuPorts() {
		return onuPorts;
	}

	public void setOnuPorts(Set<ONUPort> onuPorts) {
		this.onuPorts = onuPorts;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	public String getSoftware_Version() {
		return software_Version;
	}

	public void setSoftware_Version(String softwareVersion) {
		software_Version = softwareVersion;
	}

	public String getHardware_Version() {
		return hardware_Version;
	}

	public void setHardware_Version(String hardwareVersion) {
		hardware_Version = hardwareVersion;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="addressID")
	public AddressEntity getAddressEntity() {
		return addressEntity;
	}

	public void setAddressEntity(AddressEntity addressEntity) {
		this.addressEntity = addressEntity;
	}

	public String getMacValue() {
		return macValue;
	}

	public void setMacValue(String macValue) {
		this.macValue = macValue;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "onuEntity")
	public Set<ONUVlanPortConfig> getPortConfigs() {
		return portConfigs;
	}

	public void setPortConfigs(Set<ONUVlanPortConfig> portConfigs) {
		this.portConfigs = portConfigs;
	}

	public int getNowStatus() {
		return nowStatus;
	}

	public void setNowStatus(int nowStatus) {
		this.nowStatus = nowStatus;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getBingType() {
		return bingType;
	}

	public void setBingType(int bingType) {
		this.bingType = bingType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getSequenceNo() {
		return SequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		SequenceNo = sequenceNo;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<LLDPInofEntity> getLldpinfos() {
		return lldpinfos;
	}

	public void setLldpinfos(Set<LLDPInofEntity> lldpinfos) {
		this.lldpinfos = lldpinfos;
	}

	public int getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(int deviceModel) {
		this.deviceModel = deviceModel;
	}

	public void copy(ONUEntity onuEntity) {

		this.setBingType(onuEntity.getBingType());
		this.setDescs(onuEntity.getDescs());
		this.setDistance(onuEntity.getDistance());
		this.setMacValue(onuEntity.getMacValue());
		this.setHardware_Version(onuEntity.getHardware_Version());
		this.setNowStatus(onuEntity.getNowStatus());
		this.setDeviceModel(onuEntity.getDeviceModel());
	}

}
