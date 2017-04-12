package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;

@Component(SNMPComityViewModel.ID)
public class SNMPComityViewModel extends ViewModel{
	public static final String ID = "snmpComityViewModel";
	
	@PostConstruct
	protected void initialize() {
		
	}
}
