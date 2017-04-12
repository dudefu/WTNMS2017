package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.LinkEntity;

@Component(LinkEntityAdapterFactory.ID)
public class LinkEntityAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(LinkEntity.class)) {
			if (adaptableObject instanceof LinkEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}

	public static final String ID = "linkEntityAdapterFactory";
	public static Class<?> ADAPTER_CLASS = LinkEntity.class;
}