package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;

@Component(Layer3SwitcherAdapterFactory.ID)
public class Layer3SwitcherAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(ADAPTER_CLASS)) {
			if (adaptableObject instanceof SwitchLayer3) {
				adapterObject = adaptableObject;
			}
			if (adaptableObject instanceof SwitchTopoNodeLevel3) {
				SwitchTopoNodeLevel3 layer3Switcher = (SwitchTopoNodeLevel3)adaptableObject;
				adapterObject = NodeUtils.getNodeEntity(layer3Switcher).getSwitchLayer3();
			}
		}
		return adapterObject;
	}

	public static final String ID = "layer3SwitcherAdapterFactory";
	public static Class<?> ADAPTER_CLASS = SwitchLayer3.class;
}
