package com.jhw.adm.client.model.switcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.switchs.STPPortConfig;

@Component(STPPortConfigModel.ID)
public class STPPortConfigModel implements Serializable{
	public static final String ID = "stpPortConfigModel";
	
	private List<STPPortConfig> stpPortConfigList = new ArrayList<STPPortConfig>();

	@PostConstruct
	protected void initialize() {
		
	}
	
	public List<STPPortConfig> getStpPortConfigList() {
		return stpPortConfigList;
	}

	public void setStpPortConfigList(List<STPPortConfig> stpPortConfigList) {
		this.stpPortConfigList = stpPortConfigList;
	}
	
	
	public STPPortConfig getValueAt(int port){
		if (null == stpPortConfigList){
			return null;
		}
		STPPortConfig portEntity = null;
		Iterator iterator = stpPortConfigList.iterator();
		while(iterator.hasNext()){
			portEntity = (STPPortConfig)iterator.next();
			int portNo = portEntity.getPortNo();
			if (portNo == port){
				break;
			}
		}
		
		return portEntity;	
	}
}
