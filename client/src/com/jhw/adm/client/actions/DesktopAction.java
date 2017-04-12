package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.views.FrontEndManagementView;
import com.jhw.adm.server.entity.nets.FEPEntity;

public abstract class DesktopAction  extends AbstractAction implements BeanFactoryAware{
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	public RemoteServer getRemoteServer() {
		return remoteServer;
	}

	public ImageRegistry getImageRegistry() {
		return imageRegistry;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	private BeanFactory beanFactory;

	
	
}