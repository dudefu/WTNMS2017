package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.ports.QOSSysConfig;

@Component(QOSSysViewModel.ID)
public class QOSSysViewModel extends ViewModel{
	public static final String ID = "qosSysViewModel";
	
	private QOSSysConfig qosSysConfig = null;
	
	@PostConstruct
	protected void initialize() {
		
	}

	public QOSSysConfig getQosSysConfig() {
		return qosSysConfig;
	}

	public void setQosSysConfig(QOSSysConfig qosSysConfig) {
		this.qosSysConfig = qosSysConfig;
	}
	
	
}
