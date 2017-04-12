package com.jhw.adm.server.entity.level3;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 三层交换机LLDP协议配置
 * @author Snow
 *
 */
@Table(name="Switch3LLDP_P")
@Entity
public class Switch3LLDP_PEntity   implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String descs;
	private boolean syschorized = true;
	private int protocolState;//1：开启协议 0：关闭协议
    private int holdTime;
    private int reinit;
    private int sendCycle;
    private SwitchLayer3 switchLayer3;
    private Set<Switch3LLDPPortEntity> portEntities=new HashSet<Switch3LLDPPortEntity>();
    private int issuedTag=0;//1：设备側  0：网管侧
	public int getIssuedTag() {
		return issuedTag;
	}

	public void setIssuedTag(int issuedTag) {
		this.issuedTag = issuedTag;
	}
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	
	public int getProtocolState() {
		return protocolState;
	}
	public void setProtocolState(int protocolState) {
		this.protocolState = protocolState;
	}
	public int getHoldTime() {
		return holdTime;
	}
	public void setHoldTime(int holdTime) {
		this.holdTime = holdTime;
	}
	public int getReinit() {
		return reinit;
	}
	public void setReinit(int reinit) {
		this.reinit = reinit;
	}
	public int getSendCycle() {
		return sendCycle;
	}
	public void setSendCycle(int sendCycle) {
		this.sendCycle = sendCycle;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@ManyToOne
	public SwitchLayer3 getSwitchLayer3() {
		return switchLayer3;
	}
	public void setSwitchLayer3(SwitchLayer3 switchLayer3) {
		this.switchLayer3 = switchLayer3;
	}
	@OneToMany(fetch =FetchType.EAGER,cascade=CascadeType.ALL)
	public Set<Switch3LLDPPortEntity> getPortEntities() {
		return portEntities;
	}
	public void setPortEntities(Set<Switch3LLDPPortEntity> portEntities) {
		this.portEntities = portEntities;
	}
    
}
