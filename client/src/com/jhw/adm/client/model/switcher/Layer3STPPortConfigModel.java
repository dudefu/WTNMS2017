package com.jhw.adm.client.model.switcher;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.level3.Switch3STPPortEntity;

@Component(Layer3STPPortConfigModel.ID)
public class Layer3STPPortConfigModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "layer3STPPortConfigModel";
	
	private Set<Switch3STPPortEntity> portList = new HashSet<Switch3STPPortEntity>();

	@PostConstruct
	protected void initialize() {
	}
	
	public Set<Switch3STPPortEntity> getPortList() {
		return portList;
	}

	public void setPortList(Set<Switch3STPPortEntity> portList) {
		this.portList = portList;
	}

	public Switch3STPPortEntity getValueAt(int portID){
		if (null == portList){
			return null;
		}
		Switch3STPPortEntity portEntity = null;
		for(Switch3STPPortEntity entity : portList){
			int portNo = entity.getPortID();
			if (portNo == portID){
				portEntity = entity;
				break;
			}
		}
		return portEntity;	
	}
}
