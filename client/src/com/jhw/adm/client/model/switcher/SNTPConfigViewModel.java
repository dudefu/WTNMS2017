package com.jhw.adm.client.model.switcher;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.switchs.SNTPConfigEntity;

@Component(SNTPConfigViewModel.ID)
public class SNTPConfigViewModel extends ViewModel{
	public static final String ID = "sntpConfigViewModel";

	private SNTPConfigEntity sntpConfigEntity = null;
	
	@PostConstruct
	protected void initialize() {
		
	}

	public SNTPConfigEntity getSntpConfigEntity() {
		if (null == sntpConfigEntity){
			sntpConfigEntity = new SNTPConfigEntity();
		}
		return sntpConfigEntity;
	}

	public void setSntpConfigEntity(SNTPConfigEntity sntpConfigEntity) {
		this.sntpConfigEntity = sntpConfigEntity;
	}
	
	
}
