package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;

@Component(SNMPGroupViewModel.ID)
public class SNMPGroupViewModel extends ViewModel{
	public static final String ID = "snmpGroupViewModel";

	@PostConstruct
	protected void initialize() {
		
	}
}
