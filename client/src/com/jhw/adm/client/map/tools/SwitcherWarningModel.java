package com.jhw.adm.client.map.tools;

import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.warning.SimpleWarning;

public class SwitcherWarningModel {
	private NodeEntity node;
	private SimpleWarning simpleWarning;
	public NodeEntity getNode() {
		return node;
	}
	public void setNode(NodeEntity node) {
		this.node = node;
	}
	public SimpleWarning getSimpleWarning() {
		return simpleWarning;
	}
	public void setSimpleWarning(SimpleWarning simpleWarning) {
		this.simpleWarning = simpleWarning;
	}
	
}
