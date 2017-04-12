package com.jhw.adm.client.model.epon;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.epon.ONUPort;

@Component(ONUPortConfigModel.ID)
public class ONUPortConfigModel implements Serializable{
	public static final String ID = "onuPortConfigModel";
	
	private Set<ONUPort> onuPortSet = new LinkedHashSet<ONUPort>();

	@PostConstruct
	protected void initialize() {
		
	}

	public Set<ONUPort> getOnuPortList() {
		return onuPortSet;
	}
	public void setOnuPortList(Set<ONUPort> onuPortSet) {
		this.onuPortSet = onuPortSet;
	}

	public ONUPort getValueAt(int port){
		if (null == onuPortSet){
			return null;
		}
		ONUPort onuPort = null;
		Iterator iterator = onuPortSet.iterator();
		while(iterator.hasNext()){
			onuPort = (ONUPort)iterator.next();
			int portNo = onuPort.getProtNo();
			if (portNo == port){
				break;
			}
		}
		
		return onuPort;	
	}
}
