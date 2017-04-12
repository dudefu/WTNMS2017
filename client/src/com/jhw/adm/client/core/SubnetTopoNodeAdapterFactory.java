package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;

@Component(SubnetTopoNodeAdapterFactory.ID)
public class SubnetTopoNodeAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(ADAPTER_CLASS)) {
			if (adaptableObject instanceof SubNetTopoNodeEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}

	public static final String ID = "subnetTopoNodeAdapterFactory";
	public static Class<?> ADAPTER_CLASS = SubNetTopoNodeEntity.class;
}
