package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;

@Component(GPRSNodeAdapterFactory.ID)
public class GPRSNodeAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(GPRSTopoNodeEntity.class)) {
			if (adaptableObject instanceof GPRSTopoNodeEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}


	public static final String ID = "gprsNodeAdapterFactory";
	public static Class<?> ADAPTER_CLASS = GPRSTopoNodeEntity.class;
}