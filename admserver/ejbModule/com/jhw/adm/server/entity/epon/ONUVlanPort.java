/**
 * 
 */
package com.jhw.adm.server.entity.epon;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author ×ó¾üÓÂ
 * @Ê±¼ä 2010-7-27
 */
@Entity
public class ONUVlanPort implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String onuMacValue;
	private int portNo;
	private String descs;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOnuMacValue() {
		return onuMacValue;
	}

	public void setOnuMacValue(String onuMacValue) {
		this.onuMacValue = onuMacValue;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

}
