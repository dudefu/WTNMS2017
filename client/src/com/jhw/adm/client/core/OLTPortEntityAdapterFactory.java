package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.epon.OLTPort;

@Component(OLTPortEntityAdapterFactory.ID)
public class OLTPortEntityAdapterFactory implements AdapterFactory {

	public static final String ID = "oltPortEntityAdapterFactory";
	public static Class<?> ADAPTER_CLASS = OLTPort.class;
	
	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(OLTPort.class)) {
			if (adaptableObject instanceof OLTPort) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}
	
}
