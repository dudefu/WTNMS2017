package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 * 三层交换机IP地址转发表
 * @author Snow
 *
 */
public class Switch3IPF_Table implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ipNetToMediaPhysAddress;
	private String ipNetToMediaNetAddress;
	private int ipNetToMediaType;
    private Long id;
	public String getIpNetToMediaPhysAddress() {
		return ipNetToMediaPhysAddress;
	}
	public void setIpNetToMediaPhysAddress(String ipNetToMediaPhysAddress) {
		this.ipNetToMediaPhysAddress = ipNetToMediaPhysAddress;
	}
	public String getIpNetToMediaNetAddress() {
		return ipNetToMediaNetAddress;
	}
	public void setIpNetToMediaNetAddress(String ipNetToMediaNetAddress) {
		this.ipNetToMediaNetAddress = ipNetToMediaNetAddress;
	}
	public int getIpNetToMediaType() {
		return ipNetToMediaType;
	}
	public void setIpNetToMediaType(int ipNetToMediaType) {
		this.ipNetToMediaType = ipNetToMediaType;
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
