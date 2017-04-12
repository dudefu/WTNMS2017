package com.jhw.adm.comclient.service.topology;

/**
 * 
 * @author xiongbo
 * 
 */
public class RouteEntry {
	private String ipRouteDest;
	private String ipRouteNextHop;
	private int ipRouteType;

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

}
