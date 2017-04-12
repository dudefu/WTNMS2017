package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;

@Component(VirtualNodeEntityAdapter.ID)
public class VirtualNodeEntityAdapter implements AdapterFactory {

	public static final String ID = "virtualNodeEntityAdapter";
	public static Class<?> ADAPTER_CLASS = VirtualNodeEntity.class;
	
	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(VirtualNodeEntity.class)) {
			if (adaptableObject instanceof VirtualNodeEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}

}
