package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
@Component(GHRINGConfViewModel.ID)
public class GHRINGConfViewModel extends ViewModel{
	public static final String ID = "ghRINGConfViewModel";
	
	@PostConstruct
	protected void initialize() {
		
	}
}
