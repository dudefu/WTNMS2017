package com.jhw.adm.client.model;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component(UserManageViewModel.ID)
public class UserManageViewModel extends ViewModel{
	public static final String ID = "userManageViewModel";
	
	@PostConstruct
	protected void initialize() {
		
	}
}
