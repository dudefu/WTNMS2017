package com.jhw.adm.client.model.switcher;


import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.ports.LACPConfig;

@Component(LacpPortViewModel.ID)
public class LacpPortViewModel extends ViewModel {
	public static final String ID = "lacpPortViewModel";
	
	private List<LACPConfig> lacpConfigList = null;
	
	@PostConstruct
	protected void initialize() {
		
	}

	public List<LACPConfig> getLacpConfigList() {
		return lacpConfigList;
	}

	public void setLacpConfigList(List<LACPConfig> lacpConfigList) {
		this.lacpConfigList = lacpConfigList;
	}
	
	public LACPConfig getLACPConfig(int port){
		LACPConfig lacpConfig = new LACPConfig();
		for (int i = 0 ; i < lacpConfigList.size(); i++){
			lacpConfig = lacpConfigList.get(i);
			int portNo = lacpConfig.getPortNo();
			if (portNo == port){
				break;
			}
		}
		
		return lacpConfig;
	}
}
