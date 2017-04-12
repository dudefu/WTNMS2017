package com.jhw.adm.server.entity.level3;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="Switch3VlanPort")
@Entity
public class Switch3VlanPortEntity  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private int  portID;
	private String portName;
	private int vlanID;
	private String descs;
	private boolean syschorized = true;
	private int model; //1:Access 0:Trunk
	private int delTag; //1：是  0：否
	private int allowTag;//1:是 0：否
	private String ipValue;
	
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
	public int getPortID() {
		return portID;
	}
	public void setPortID(int portID) {
		this.portID = portID;
	}
	public int getVlanID() {
		return vlanID;
	}
	public void setVlanID(int vlanID) {
		this.vlanID = vlanID;
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
	public int getModel() {
		return model;
	}
	public void setModel(int model) {
		this.model = model;
	}
	public int getDelTag() {
		return delTag;
	}
	public void setDelTag(int delTag) {
		this.delTag = delTag;
	}
	public int getAllowTag() {
		return allowTag;
	}
	public void setAllowTag(int allowTag) {
		this.allowTag = allowTag;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getIpValue() {
		return ipValue;
	}
	public void setIpValue(String ipValue) {
		this.ipValue = ipValue;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public void copy(Switch3VlanPortEntity switch3VlanPortEntity){
		this.setAllowTag(switch3VlanPortEntity.getAllowTag());
		this.setDelTag(switch3VlanPortEntity.getDelTag());
		this.setIpValue(switch3VlanPortEntity.getIpValue());
		this.setModel(switch3VlanPortEntity.getModel());
		this.setPortID(switch3VlanPortEntity.getPortID());
		this.setPortName(switch3VlanPortEntity.getPortName());
		this.setSyschorized(switch3VlanPortEntity.isSyschorized());
		this.setVlanID(switch3VlanPortEntity.getVlanID());
		this.setDescs(switch3VlanPortEntity.getDescs());
		setIssuedTag(switch3VlanPortEntity.getIssuedTag());
		
	}
}
