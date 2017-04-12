package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;

@Component(SNMPEngineIDViewModel.ID)
public class SNMPEngineIDViewModel extends ViewModel{
	public static final String ID = "snmpEngineIDViewModel";
	
	@PostConstruct
	protected void initialize() {
		
	}

}
