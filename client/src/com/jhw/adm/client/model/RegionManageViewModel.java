package com.jhw.adm.client.model;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component(RegionManageViewModel.ID)
public class RegionManageViewModel extends ViewModel{
	public static final String ID = "regionManageViewModel";

	@PostConstruct
	protected void initialize() {
		
	}
}
