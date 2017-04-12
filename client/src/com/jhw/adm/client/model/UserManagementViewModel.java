package com.jhw.adm.client.model;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component(UserManagementViewModel.ID)
public class UserManagementViewModel extends ViewModel{
	public static final String ID = "userManagementViewModel";

	@PostConstruct
	protected void initialize() {
		
	}

}
