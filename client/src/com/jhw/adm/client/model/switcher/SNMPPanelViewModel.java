package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;

@Component(SNMPPanelViewModel.ID)
public class SNMPPanelViewModel extends ViewModel{
	public static final String ID = "snmpPanelViewModel";

	@PostConstruct
	protected void initialize() {
		
	}
}
