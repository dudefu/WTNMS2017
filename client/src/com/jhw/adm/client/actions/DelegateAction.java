package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.aop.LoggingRequired;

@Component(DelegateAction.ID)
@Scope("prototype")
public class DelegateAction extends AbstractAction {

	@Override
	@LoggingRequired
	public void actionPerformed(ActionEvent event) {		
		try {
			actionMethod.invoke(target, new Object[]{});
		} catch (IllegalArgumentException e) {
			LOG.error("DelegateAction.actionPerformed error", e);
		} catch (IllegalAccessException e) {
			LOG.error("DelegateAction.actionPerformed error", e);
		} catch (InvocationTargetException e) {
			LOG.error("DelegateAction.actionPerformed error", e);
		}
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Method getActionMethod() {
		return actionMethod;
	}

	public void setActionMethod(Method actionMethod) {
		this.actionMethod = actionMethod;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private String description;
	private Object target;
	private Method actionMethod;
	private static final Logger LOG = LoggerFactory.getLogger(DelegateAction.class);
	private static final long serialVersionUID = 1L;
	public static final String ID = "delegateAction";
}