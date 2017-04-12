package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;

@Component(Layer3SwitcherTopoAdapterFactory.ID)
public class Layer3SwitcherTopoAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(ADAPTER_CLASS)) {
			if (adaptableObject instanceof SwitchTopoNodeLevel3) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}

	public static final String ID = "layer3SwitcherTopoAdapterFactory";
	public static Class<?> ADAPTER_CLASS = SwitchTopoNodeLevel3.class;
}
