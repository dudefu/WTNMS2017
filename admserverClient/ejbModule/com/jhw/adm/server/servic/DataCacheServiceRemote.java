package com.jhw.adm.server.servic;

import javax.ejb.Remote;

@Remote
public interface DataCacheServiceRemote {
	/**
	 * 通过需要配置的设备的ip地址，获取管理该设备的前置机编号
	 * 
	 * @param ipvalue
	 * @return
	 */
	public String getFepCodeByIP(String ipvalue);

	public void resettingTopo();

	public void resettingSyn();
}
