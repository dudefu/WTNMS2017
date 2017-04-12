package com.jhw.adm.client.core;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.warning.SimpleWarning;
import com.jhw.adm.server.entity.warning.TrapSimpleWarning;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;

@Component(TrapSimpleWarningAdapterFactory.ID)
public class TrapSimpleWarningAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(TrapSimpleWarning.class)) {
			if (adaptableObject instanceof TrapSimpleWarning) {
				adapterObject = adaptableObject;
			}
			if (adaptableObject instanceof SimpleWarning) {
				SimpleWarning simpleWarning = (SimpleWarning)adaptableObject;
				adapterObject = remoteServer.getNmsService().findTrapSimpleWarning(simpleWarning);
			}
		}
		return adapterObject;
	}

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;
	
	public static final String ID = "trapSimpleWarningAdapterFactory";
	public static Class<?> ADAPTER_CLASS = TrapSimpleWarning.class;
}