package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 * 三次交换机地址信息
 * @author Snow
 *
 */
public class Switch3Address implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ipAdEntAddr;
	private String ipAdEntNetMask;
	private Long id;
	public String getIpAdEntAddr() {
		return ipAdEntAddr;
	}
	public void setIpAdEntAddr(String ipAdEntAddr) {
		this.ipAdEntAddr = ipAdEntAddr;
	}
	public String getIpAdEntNetMask() {
		return ipAdEntNetMask;
	}
	public void setIpAdEntNetMask(String ipAdEntNetMask) {
		this.ipAdEntNetMask = ipAdEntNetMask;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
