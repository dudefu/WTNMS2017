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

@Table(name="Switch3STP")
@Entity
public class Switch3STPEntity  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String descs;
	private boolean syschorized = true;
	private int stpType; //1:不是使能  2:STP 3: RSTP
	private int helloTime;
	private int maxAgeTime;
	private int forwardDelTime;
	private int s_TreePriority;
	private Set<Switch3STPPortEntity> portEntities =new  HashSet<Switch3STPPortEntity>();
	private SwitchLayer3 switchLayer3;
	
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
	
	public int getStpType() {
		return stpType;
	}
	public void setStpType(int stpType) {
		this.stpType = stpType;
	}
	public int getHelloTime() {
		return helloTime;
	}
	public void setHelloTime(int helloTime) {
		this.helloTime = helloTime;
	}
	public int getMaxAgeTime() {
		return maxAgeTime;
	}
	public void setMaxAgeTime(int maxAgeTime) {
		this.maxAgeTime = maxAgeTime;
	}
	public int getForwardDelTime() {
		return forwardDelTime;
	}
	public void setForwardDelTime(int forwardDelTime) {
		this.forwardDelTime = forwardDelTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getS_TreePriority() {
		return s_TreePriority;
	}
	public void setS_TreePriority(int sTreePriority) {
		s_TreePriority = sTreePriority;
	}
	@ManyToOne
	public SwitchLayer3 getSwitchLayer3() {
		return switchLayer3;
	}
	public void setSwitchLayer3(SwitchLayer3 switchLayer3) {
		this.switchLayer3 = switchLayer3;
	}
	@OneToMany(fetch =FetchType.EAGER,cascade=CascadeType.ALL)
	public Set<Switch3STPPortEntity> getPortEntities() {
		return portEntities;
	}
	public void setPortEntities(Set<Switch3STPPortEntity> portEntities) {
		this.portEntities = portEntities;
	}
	
}
