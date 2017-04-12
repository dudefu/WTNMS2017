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
import com.jhw.adm.server.entity.util.ManagerEntity;

@Entity
public class OLTEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String epon_num;
	private String epon_name;
	private String softwareVersion;
	private String ipValue;
	private boolean syschorized = true;
	private AddressEntity address;
	private ManagerEntity managerEntity;
	private Set<OLTPort> ports;
	private String descs;
	private OLTBaseInfo oltBaseInfo;
	private int slotNum;// 插槽数量
	private int deviceModel;//设备型号

	private Set<LLDPInofEntity> lldpinfos;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name="addressID")
	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}

	@OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	public ManagerEntity getManagerEntity() {
		return managerEntity;
	}

	public void setManagerEntity(ManagerEntity managerEntity) {
		this.managerEntity = managerEntity;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public String getEpon_num() {
		return epon_num;
	}

	public void setEpon_num(String eponNum) {
		epon_num = eponNum;
	}

	public String getEpon_name() {
		return epon_name;
	}

	public void setEpon_name(String eponName) {
		epon_name = eponName;
	}

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public int getSlotNum() {
		return slotNum;
	}

	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<LLDPInofEntity> getLldpinfos() {
		return lldpinfos;
	}

	public void setLldpinfos(Set<LLDPInofEntity> lldpinfos) {
		this.lldpinfos = lldpinfos;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "oltEntity")
	public Set<OLTPort> getPorts() {
		return ports;
	}

	public void setPorts(Set<OLTPort> ports) {
		this.ports = ports;
	}

	@OneToOne(cascade={ CascadeType.ALL })
	public OLTBaseInfo getOltBaseInfo() {
		return oltBaseInfo;
	}

	public void setOltBaseInfo(OLTBaseInfo oltBaseInfo) {
		this.oltBaseInfo = oltBaseInfo;
	}
	
	
	public int getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(int deviceModel) {
		this.deviceModel = deviceModel;
	}

	public void copy(OLTEntity olt){
		setEpon_name(olt.getEpon_name());
		setEpon_num(olt.getEpon_num());
		setSoftwareVersion(olt.getSoftwareVersion());
		setDescs(olt.getDescs());
		setSlotNum(olt.getSlotNum());
		setIpValue(olt.getIpValue());
		setSyschorized(olt.isSyschorized());
		setDeviceModel(olt.getDeviceModel());
	}
}
