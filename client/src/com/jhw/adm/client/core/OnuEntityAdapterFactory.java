package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;

@Component(OnuEntityAdapterFactory.ID)
public class OnuEntityAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(ONUEntity.class)) {
			if (adaptableObject instanceof ONUTopoNodeEntity) {
				adapterObject = NodeUtils.getNodeEntity((ONUTopoNodeEntity)adaptableObject).getOnuEntity();
			}
			if (adaptableObject instanceof ONUEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}


	public static final String ID = "onuEntityAdapterFactory";
	public static Class<?> ADAPTER_CLASS = ONUEntity.class;
}