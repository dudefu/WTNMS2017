package com.jhw.adm.server.entity.switchs;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.ManagerEntity;
import com.jhw.adm.server.entity.util.SwitchSerialPort;

/**
 * 交换机
 * 
 * @author yangxiao
 * 
 */
@Entity
@Table(name = "SwitchNode")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SwitchNodeEntity  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String type;
	private SwitchBaseInfo baseInfo;
	private SwitchBaseConfig baseConfig;
	private AddressEntity address;
	private ManagerEntity managerEntity;
	private Set<SwitchPortEntity> ports;
	private boolean syschorized = true;
	private Set<LLDPInofEntity> lldpinfos;
	private Set<SwitchRingInfo> rings;
	private Set<SwitchSerialPort> serialPorts;
	private String guid;
	private String descs;
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

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL },mappedBy="switchNode")
	public Set<SwitchPortEntity> getPorts() {
		return ports;
	}

	public void setPorts(Set<SwitchPortEntity> ports) {
		this.ports = ports;
	}

	@Lob
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	public SwitchBaseInfo getBaseInfo() {
		return baseInfo;
	}

	public void setBaseInfo(SwitchBaseInfo baseInfo) {
		this.baseInfo = baseInfo;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name="address_ID")
	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}
	@OneToOne(cascade=CascadeType.ALL)
	public ManagerEntity getManagerEntity() {
		return managerEntity;
	}

	public void setManagerEntity(ManagerEntity managerEntity) {
		this.managerEntity = managerEntity;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name="baseConfig_ID")
	public SwitchBaseConfig getBaseConfig() {
		return baseConfig;
	}

	public void setBaseConfig(SwitchBaseConfig baseConfig) {
		this.baseConfig = baseConfig;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<LLDPInofEntity> getLldpinfos() {
		return lldpinfos;
	}

	public void setLldpinfos(Set<LLDPInofEntity> lldpinfos) {
		this.lldpinfos = lldpinfos;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSyschorized() {
		return syschorized;
	}

	public void setSyschorized(boolean syschorized) {
		this.syschorized = syschorized;
	}

	@OneToMany(mappedBy = "switchNode", fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<SwitchSerialPort> getSerialPorts() {
		return serialPorts;
	}

	public void setSerialPorts(Set<SwitchSerialPort> serialPorts) {
		this.serialPorts = serialPorts;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<SwitchRingInfo> getRings() {
		return rings;
	}

	public void setRings(Set<SwitchRingInfo> rings) {
		this.rings = rings;
	}

	public int getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(int deviceModel) {
		this.deviceModel = deviceModel;
	}
    
}
