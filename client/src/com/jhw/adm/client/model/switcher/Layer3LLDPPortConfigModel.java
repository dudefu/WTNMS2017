package com.jhw.adm.client.model.switcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.level3.Switch3LLDPPortEntity;

@Component(Layer3LLDPPortConfigModel.ID)
public class Layer3LLDPPortConfigModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "layer3LLDPPortConfigModel";
	
	private Set<Switch3LLDPPortEntity> portList = new HashSet<Switch3LLDPPortEntity>();

	@PostConstruct
	protected void initialize() {
	}
	
	public Set<Switch3LLDPPortEntity> getPortList() {
		return portList;
	}

	public void setPortList(Set<Switch3LLDPPortEntity> portList) {
		this.portList = portList;
	}

	public Switch3LLDPPortEntity getValueAt(int portID){
		if (null == portList){
			return null;
		}
		Switch3LLDPPortEntity portEntity = null;
		for(Switch3LLDPPortEntity entity : portList){
			int portNo = entity.getPortIndex();
			if (portNo == portID){
				portEntity = entity;
				break;
			}
		}
		
		return portEntity;	
	}
}
