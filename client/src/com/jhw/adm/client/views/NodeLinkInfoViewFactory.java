package com.jhw.adm.client.views;

import com.jhw.adm.server.entity.tuopos.LinkEntity;

public interface NodeLinkInfoViewFactory {
	
	//isStart为true,为开始节点,isStart为false,为末端节点
	void buildPanel(boolean isStart);
	void fillNode(LinkEntity linkNode,  boolean isStart);
	
}
