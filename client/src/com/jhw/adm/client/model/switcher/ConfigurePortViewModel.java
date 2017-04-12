package com.jhw.adm.client.model.switcher;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;

@Component(ConfigurePortViewModel.ID)
public class ConfigurePortViewModel extends ViewModel{
	public static final String ID = "configurePortViewModel";
	
	private Set<SwitchPortEntity> dataSet = new LinkedHashSet<SwitchPortEntity>();
	
	@PostConstruct
	protected void initialize() {
		
	}

	public Set<SwitchPortEntity> getDataSet() {
		return dataSet;
	}

	public void setDataSet(Set<SwitchPortEntity> dataSet) {
		this.dataSet = dataSet;
	}

	public SwitchPortEntity getValueAt(int port){
		if (null == dataSet){
			return null;
		}
		SwitchPortEntity portEntitySet = null;
		Iterator iterator = dataSet.iterator();
		while(iterator.hasNext()){
			portEntitySet = (SwitchPortEntity)iterator.next();
			int portNo = portEntitySet.getPortNO();
			if (portNo == port){
				break;
			}
		}
		
		return portEntitySet;	
	}
}
