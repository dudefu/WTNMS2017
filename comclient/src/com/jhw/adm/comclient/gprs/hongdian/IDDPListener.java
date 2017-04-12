package com.jhw.adm.comclient.gprs.hongdian;

public interface IDDPListener {
	void received(DDPData data);

	void heartbeat(DSCUser user);
}
                                                  