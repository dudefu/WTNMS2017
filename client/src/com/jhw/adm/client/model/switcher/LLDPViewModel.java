package com.jhw.adm.client.model.switcher;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.ports.LLDPConfig;
import com.jhw.adm.server.entity.ports.LLDPPortConfig;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;

@Component(LLDPViewModel.ID)
public class LLDPViewModel extends ViewModel{
	public static final String ID = "lldpViewModel";
	
	private LLDPConfig lldpConfig = null;
	
	private Set<LLDPPortConfig> portConfigSet = null;
	
	@PostConstruct
	protected void initialize() {
		
	}

	public LLDPConfig getLldpConfig() {
		return lldpConfig;
	}

	public void setLldpConfig(LLDPConfig lldpConfig) {
		this.lldpConfig = lldpConfig;
		
		Set<LLDPPortConfig> portSet = lldpConfig.getLldpPortConfigs();
		if (null == portSet){
			portConfigSet = new LinkedHashSet<LLDPPortConfig>();
		}
		else{
			this.portConfigSet = portSet;
		}
	}
	
	public void setPortEntity(Set<LLDPPortConfig> portConfigSet){
		this.portConfigSet = portConfigSet;
	}
	  
	public LLDPPortConfig getLLDPPortEntity(int port){
		LLDPPortConfig portEntity = null;
		if(null == portConfigSet){
			portConfigSet = new LinkedHashSet<LLDPPortConfig>();
		}
		Iterator iterator = portConfigSet.iterator();
		while(iterator.hasNext()){
			portEntity = (LLDPPortConfig)iterator.next();
			int portNo = portEntity.getPortNum();
			if (port == portNo){
				break;
			}
			
		}
		return portEntity;
	}
}
