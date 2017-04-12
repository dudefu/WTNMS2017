package com.jhw.adm.client.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import com.jhw.adm.client.core.ApplicationException;
import com.jhw.adm.client.views.ViewPart;

/**
 * 桌面对象的生命周期
 */
public class DesktopScope implements Scope {

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Object object = null;
		DesktopModel desktopModel = ClientUI.getDesktopWindow().getModel();
		if (desktopModel.containsViewPart(name)) {
			object = desktopModel.getViewPart(name);
			LOG.debug("ViewPart({}) opened", name);
		} else {			
			object = objectFactory.getObject();
			LOG.debug("objectFactory.getObject()" + object.toString());
			if (object instanceof ViewPart) {
				desktopModel.addViewPart((ViewPart)object);
			} else {
				String message = String.format("object[{}] must be ViewPart subclass", object.getClass().getCanonicalName());
				LOG.error(message);
				throw new ApplicationException(message);
			}
		}
		return object;
	}

	@Override
	public Object remove(String name) {
		return null;
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
	}

	@Override
	public Object resolveContextualObject(String key) {
		return null;
	}

	@Override
	public String getConversationId() {
		return Thread.currentThread().getName();
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(DesktopScope.class);
}