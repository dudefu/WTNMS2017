package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;

@Component(FrontEndNodeAdapterFactory.ID)
public class FrontEndNodeAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(FEPTopoNodeEntity.class)) {
			if (adaptableObject instanceof FEPTopoNodeEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}

	public static final String ID = "frontEndNodeAdapterFactory";
	public static Class<?> ADAPTER_CLASS = FEPTopoNodeEntity.class;
}
