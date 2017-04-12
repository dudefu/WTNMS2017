package com.jhw.adm.client.core;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jndi.JndiObjectLocator;

public class LazyJndiObjectLocator extends JndiObjectLocator {

	public Object start() throws JndiLookupException {
		Object object = null;
		try {
			object = lookup();			
			LOG.info("start lookup {}", getJndiName());
			if (this.cache) {
				this.cachedObject = object;
			}
			else {
				this.targetClass = object.getClass();
			}
		} catch (NamingException ex) {
			String message = String.format("查找服务[%s]超时或者地址错误", getJndiName());
			throw new JndiLookupException(message, ex);
		}
		
		return object;
	}

	public Class<?> getTargetClass() {
		if (this.cachedObject != null) {
			return this.cachedObject.getClass();
		}
		else if (this.targetClass != null) {
			return this.targetClass;
		}
		else {
			return getExpectedType();
		}
	}

	public boolean isStatic() {
		return (this.cachedObject != null);
	}

	public Object getTarget() {
		return cachedObject;
	}

	public void releaseTarget(Object target) {
		LOG.debug("releaseTarget(Object target)");
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}
	
	private boolean cache = true;
	private Object cachedObject;
	private Class targetClass;
	private static final Logger LOG = LoggerFactory.getLogger(LazyJndiObjectLocator.class);
}