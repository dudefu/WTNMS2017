package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.warning.WarningType;

@Component(TrapConfigViewModel.ID)
public class TrapConfigViewModel extends ViewModel{
	public static final String ID = "trapConfigViewModel";

	private List<WarningType> warningTypeList = new ArrayList<WarningType>();
	@PostConstruct
	protected void initialize() {
		
	}
	public List<WarningType> getWarningTypeList() {
		return warningTypeList;
	}
	public void setWarningTypeList(List<WarningType> warningTypeList) {
		this.warningTypeList = warningTypeList;
	}
	
}
