package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;

@Component(OpticalSplitterAdapterFactory.ID)
public class OpticalSplitterAdapterFactory implements AdapterFactory {

	public static final String ID = "opticalSplitterAdapterFactory";
	public static Class<?> ADAPTER_CLASS = Epon_S_TopNodeEntity.class;
	
	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(Epon_S_TopNodeEntity.class)) {
			if (adaptableObject instanceof Epon_S_TopNodeEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}

}
