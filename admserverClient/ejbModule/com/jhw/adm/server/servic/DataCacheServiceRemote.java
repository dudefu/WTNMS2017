package com.jhw.adm.server.servic;

import javax.ejb.Remote;

@Remote
public interface DataCacheServiceRemote {
	/**
	 * ͨ����Ҫ���õ��豸��ip��ַ����ȡ������豸��ǰ�û����
	 * 
	 * @param ipvalue
	 * @return
	 */
	public String getFepCodeByIP(String ipvalue);

	public void resettingTopo();

	public void resettingSyn();
}
