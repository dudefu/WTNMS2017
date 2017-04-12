package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;

@Component(SNMPUserViewModel.ID)
public class SNMPUserViewModel extends ViewModel{
	public static final String ID = "snmpUserViewModel";

	@PostConstruct
	protected void initialize() {
		
	}

}
