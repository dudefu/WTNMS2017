package com.jhw.adm.server.interfaces;

import javax.ejb.Local;

import com.jhw.adm.server.entity.nets.FEPEntity;

@Local
public interface FepWarningLocal {
	public void sendFepMessageToClient(FEPEntity fepEntity,int fepOperate);
}
