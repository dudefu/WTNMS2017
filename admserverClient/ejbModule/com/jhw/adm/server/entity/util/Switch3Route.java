package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 * 三层交换机路由地址信息
 * @author Snow
 *
 */
public class Switch3Route implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ipRouteDest;
	private String ipRouteNextHop;
	private int ipRouteType;
	private String ipRouteMask;
    private Long id;
	public String getIpRouteDest() {
		return ipRouteDest;
	}
	public void setIpRouteDest(String ipRouteDest) {
		this.ipRouteDest = ipRouteDest;
	}
	public String getIpRouteNextHop() {
		return ipRouteNextHop;
	}
	public void setIpRouteNextHop(String ipRouteNextHop) {
		this.ipRouteNextHop = ipRouteNextHop;
	}
	public int getIpRouteType() {
		return ipRouteType;
	}
	public void setIpRouteType(int ipRouteType) {
		this.ipRouteType = ipRouteType;
	}
	public String getIpRouteMask() {
		return ipRouteMask;
	}
	public void setIpRouteMask(String ipRouteMask) {
		this.ipRouteMask = ipRouteMask;
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
