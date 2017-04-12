package com.jhw.adm.server.entity.nets;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "vlanPortConfig")
public class VlanPortConfig  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private int portNo;
	private char portTag;// T/U
	private String descs;
	private String ipVlue;
	private String vlanID;
	private int issuedTag=0;//1£ºÉè±¸‚È  0£ºÍø¹Ü²à
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

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public char getPortTag() {
		return portTag;
	}

	public void setPortTag(char portTag) {
		this.portTag = portTag;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public String getIpVlue() {
		return ipVlue;
	}

	public void setIpVlue(String ipVlue) {
		this.ipVlue = ipVlue;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getVlanID() {
		return vlanID;
	}

	public void setVlanID(String vlanID) {
		this.vlanID = vlanID;
	}

}
