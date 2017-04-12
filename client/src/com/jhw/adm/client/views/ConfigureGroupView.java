package com.jhw.adm.client.views;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component(ConfigureGroupView.ID)
public class ConfigureGroupView extends ModalContainer {
	
	@PostConstruct
	protected void initialize() {
	}
	
	@Override
	public int getPlacement() {
		return TOP_CENTER;
	}
	@Override
	public boolean isModal() {
		return true;
	}
	private static final long serialVersionUID = -1L;
	private static final Logger LOG = LoggerFactory.getLogger(ConfigureGroupView.class);
	public static final String ID = "configureGroupView";
}