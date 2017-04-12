package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.tuopos.CommentTopoNodeEntity;

@Component(CommentTopoNodeEntityAdapter.ID)
public class CommentTopoNodeEntityAdapter implements AdapterFactory {

	public static final String ID = "commentTopoNodeEntityAdapter";
	public static Class<?> ADAPTER_CLASS = CommentTopoNodeEntity.class;

	
	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(CommentTopoNodeEntity.class)) {
			if (adaptableObject instanceof CommentTopoNodeEntity) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}

}
