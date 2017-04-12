package com.jhw.adm.client.model.epon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.epon.OLTPortStp;

@Component(OLTSTPPortConfigModel.ID)
public class OLTSTPPortConfigModel implements Serializable{
	public static final String ID = "oltStpPortConfigModel";
	
	private List<OLTPortStp> oltStpPortList = new ArrayList<OLTPortStp>();

	@PostConstruct
	protected void initialize() {
		
	}

	public List<OLTPortStp> getOltStpPortList() {
		return oltStpPortList;
	}
	
	public void setOltStpPortList(List<OLTPortStp> oltStpPortList) {
		this.oltStpPortList = oltStpPortList;
	}

	public OLTPortStp getValueAt(int port){
		if (null == oltStpPortList){
			return null;
		}
		OLTPortStp portEntity = null;
		Iterator iterator = oltStpPortList.iterator();
		while(iterator.hasNext()){
			portEntity = (OLTPortStp)iterator.next();
			int portNo = portEntity.getPortNo();
			if (portNo == port){
				break;
			}
		}
		
		return portEntity;	
	}
}
