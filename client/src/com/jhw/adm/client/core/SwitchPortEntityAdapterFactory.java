package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.ports.SwitchPortEntity;

@Component(SwitchPortEntityAdapterFactory.ID)
public class SwitchPortEntityAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(SwitchPortEntity.class)) {
			if (adaptableObject instanceof SwitchPortEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}

	public static final String ID = "switchPortEntityAdapterFactory";
	public static Class<?> ADAPTER_CLASS = SwitchPortEntity.class;
}
