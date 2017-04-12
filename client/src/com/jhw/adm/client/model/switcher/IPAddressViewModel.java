package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;

@Component(IPAddressViewModel.ID)
public class IPAddressViewModel extends ViewModel {
	public static final String ID = "ipAddressViewModel";
	
	@PostConstruct
	protected void initialize() {
		
	}
}
