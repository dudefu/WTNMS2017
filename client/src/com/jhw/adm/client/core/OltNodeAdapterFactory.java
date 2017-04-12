package com.jhw.adm.client.core;


import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.EponTopoEntity;

@Component(OltNodeAdapterFactory.ID)
public class OltNodeAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(EponTopoEntity.class)) {
			if (adaptableObject instanceof EponTopoEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}
	
	public static final String ID = "oltNodeAdapterFactory";
	public static Class<?> ADAPTER_CLASS = EponTopoEntity.class;
}