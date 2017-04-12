package com.jhw.adm.comclient.service.topology.epon;

/**
 * 
 * @author xiongbo
 * 
 */
public class DeviceType {
	private String ip;
	// 1-Router or Three layer switch
	// 2-two layer switch
	private int type;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
