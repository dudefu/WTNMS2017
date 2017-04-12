package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;

@Component(CarrierEntityAdapterFactory.ID)
public class CarrierEntityAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(CarrierEntity.class)) {
			if (adaptableObject instanceof CarrierEntity) {
				adapterObject = adaptableObject;
			}
			if (adaptableObject instanceof CarrierTopNodeEntity) {
				CarrierTopNodeEntity carrierNode = (CarrierTopNodeEntity)adaptableObject;
				adapterObject = NodeUtils.getNodeEntity(carrierNode).getNodeEntity();
			}
		}
		return adapterObject;
	}


	public static final String ID = "carrierEntityAdapterFactory";
	public static Class<?> ADAPTER_CLASS = CarrierEntity.class;
}