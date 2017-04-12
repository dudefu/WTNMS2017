package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;

@Component(OnuNodeAdapterFactory.ID)
public class OnuNodeAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(ONUTopoNodeEntity.class)) {
			if (adaptableObject instanceof ONUTopoNodeEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}


	public static final String ID = "onuNodeAdapterFactory";
	public static Class<?> ADAPTER_CLASS = ONUTopoNodeEntity.class;
}