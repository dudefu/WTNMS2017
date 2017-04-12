package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;

@Component(BaseInfoViewModel.ID)
public class BaseInfoViewModel extends ViewModel{
	public static final String ID = "baseInfoViewModel";
	
	public static final String STATUS_MESSAGE = "STATUS_MESSAGE";
	
	@PostConstruct
	protected void initialize() {
		
	}
	
	public void changeStatusMessage(String message) {
		firePropertyChange(STATUS_MESSAGE, null, message);
	}
}
