package com.jhw.adm.client.core;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.warning.SimpleWarning;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;

@Component(TrapWarningEntityAdapterFactory.ID)
public class TrapWarningEntityAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(TrapWarningEntity.class)) {
			if (adaptableObject instanceof TrapWarningEntity) {
				adapterObject = adaptableObject;
			}
			if (adaptableObject instanceof SimpleWarning) {
				SimpleWarning simpleWarning = (SimpleWarning)adaptableObject;
				adapterObject = remoteServer.getNmsService().findTrapEntity(simpleWarning);
			}
		}
		return adapterObject;
	}

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;
	
	public static final String ID = "trapWarningEntityAdapterFactory";
	public static Class<?> ADAPTER_CLASS = TrapWarningEntity.class;
}