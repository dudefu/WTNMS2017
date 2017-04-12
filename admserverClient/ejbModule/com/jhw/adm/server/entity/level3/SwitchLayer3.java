package com.jhw.adm.server.entity.level3;

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
public class SwitchLayer3  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private AddressEntity address;
	private Set<SwitchPortLevel3> ports;
	private Set<LLDPInofEntity> lldps;
	private String ipValue;
	private String macValue;
	private boolean syschorized = true;
	private String descs;
	private ManagerEntity managerEntity;
    private String subnetMask;//子网掩码
    private String defGeteway;//默认网关
    private int deviceModel;//设备型号
    
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
    
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="addressID")
	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public String getIpValue() {
		return ipValue;
	}

	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}

	public String getMacValue() {
		return macValue;
	}

	public void setMacValue(String macValue) {
		this.macValue = macValue;
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

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	public Set<LLDPInofEntity> getLldps() {
		return lldps;
	}

	public void setLldps(Set<LLDPInofEntity> lldps) {
		this.lldps = lldps;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	public Set<SwitchPortLevel3> getPorts() {
		return ports;
	}

	public void setPorts(Set<SwitchPortLevel3> ports) {
		this.ports = ports;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public ManagerEntity getManagerEntity() {
		return managerEntity;
	}

	public void setManagerEntity(ManagerEntity managerEntity) {
		this.managerEntity = managerEntity;
	}

	public String getSubnetMask() {
		return subnetMask;
	}

	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}

	public String getDefGeteway() {
		return defGeteway;
	}

	public void setDefGeteway(String defGeteway) {
		this.defGeteway = defGeteway;
	}
	
	public void copy(SwitchLayer3 switchLayer3){
		if(switchLayer3.getAddress()!=null){
		this.setAddress(switchLayer3.getAddress());
		}
		if(switchLayer3.getManagerEntity()!=null){
			this.setManagerEntity(switchLayer3.getManagerEntity());
		}
		this.setDefGeteway(switchLayer3.getDefGeteway());
		this.setDescs(switchLayer3.getDescs());
		this.setIpValue(switchLayer3.getIpValue());
		this.setMacValue(switchLayer3.getMacValue());
		this.setName(switchLayer3.getName());
		this.setSubnetMask(switchLayer3.getSubnetMask());
		this.setSyschorized(switchLayer3.isSyschorized());
		this.setDeviceModel(switchLayer3.getDeviceModel());
		setIssuedTag(switchLayer3.getIssuedTag());
	}

	public int getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(int deviceModel) {
		this.deviceModel = deviceModel;
	}
    
}
