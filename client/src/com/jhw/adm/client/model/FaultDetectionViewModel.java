package com.jhw.adm.client.model;

import java.util.*;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.warning.FaultDetection;

@Component(FaultDetectionViewModel.ID)
public class FaultDetectionViewModel extends ViewModel{
	public static final String ID = "faultDetectionViewModel";
	
	private FaultDetection faultDetection = null;
	
	@PostConstruct
	protected void initialize() {
		
	}

	public FaultDetection getFaultDetection() {
		return faultDetection;
	}

	public void setFaultDetection(FaultDetection faultDetection) {
		this.faultDetection = faultDetection;
	}
}
