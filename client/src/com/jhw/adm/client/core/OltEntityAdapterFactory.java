package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;

@Component(OltEntityAdapterFactory.ID)
public class OltEntityAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(OLTEntity.class)) {
			if (adaptableObject instanceof EponTopoEntity) {
				adapterObject = NodeUtils.getNodeEntity((EponTopoEntity)adaptableObject).getOltEntity();
			}
			if (adaptableObject instanceof OLTEntity) {
				adapterObject = adaptableObject;
			}
			if (adaptableObject instanceof OLTPort) {
				OLTPort oltPort = (OLTPort)adaptableObject;
				adapterObject = oltPort.getOltEntity();
			}
		}
		return adapterObject;
	}


	public static final String ID = "oltEntityAdapterFactory";
	public static Class<?> ADAPTER_CLASS = OLTEntity.class;
}