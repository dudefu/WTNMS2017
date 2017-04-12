package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;

@Component(SNMPHostViewModel.ID)
public class SNMPHostViewModel extends ViewModel{
	public static final String ID = "snmpHostViewModel";

	@PostConstruct
	protected void initialize() {
		
	}
}
