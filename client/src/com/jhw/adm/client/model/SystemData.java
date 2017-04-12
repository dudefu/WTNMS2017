package com.jhw.adm.client.model;

import java.util.Set;

import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;

public class SystemData {

	public TopDiagramEntity getTopDiagram() {
		return topDiagram;
	}
	public void setTopDiagram(TopDiagramEntity topDiagram) {
		this.topDiagram = topDiagram;
	}
	public Set<FEPEntity> getFeps() {
		return setOfFep;
	}
	public void setFeps(Set<FEPEntity> setOfFep) {
		this.setOfFep = setOfFep;
	}
	
	private TopDiagramEntity topDiagram;
	private Set<FEPEntity> setOfFep;
}