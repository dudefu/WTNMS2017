package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.epon.EponSplitter;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;

@Component(EponSplitterAdapterFactory.ID)
public class EponSplitterAdapterFactory implements AdapterFactory {
	
	public static final String ID = "eponSplitterAdapterFactory";
	public static Class<?> ADAPTER_CLASS = EponSplitter.class;
	
	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(EponSplitter.class)) {
			if (adaptableObject instanceof Epon_S_TopNodeEntity) {
				adapterObject = ((Epon_S_TopNodeEntity)adaptableObject).getEponSplitter();
			}
			if (adaptableObject instanceof EponSplitter) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}

}
