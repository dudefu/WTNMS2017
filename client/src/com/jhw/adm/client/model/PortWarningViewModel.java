package com.jhw.adm.client.model;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component(PortWarningViewModel.ID)
public class PortWarningViewModel extends ViewModel{
	public static final String ID = "portWarningViewModel";
	
	@PostConstruct
	protected void initialize() {
		
	}
}
