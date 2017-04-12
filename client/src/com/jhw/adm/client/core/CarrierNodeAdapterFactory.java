package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;

@Component(CarrierNodeAdapterFactory.ID)
public class CarrierNodeAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(CarrierTopNodeEntity.class)) {
			if (adaptableObject instanceof CarrierTopNodeEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}


	public static final String ID = "carrierNodeAdapterFactory";
	public static Class<?> ADAPTER_CLASS = CarrierTopNodeEntity.class;
}