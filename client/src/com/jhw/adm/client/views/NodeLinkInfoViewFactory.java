package com.jhw.adm.client.views;

import com.jhw.adm.server.entity.tuopos.LinkEntity;

public interface NodeLinkInfoViewFactory {
	
	//isStartΪtrue,Ϊ��ʼ�ڵ�,isStartΪfalse,Ϊĩ�˽ڵ�
	void buildPanel(boolean isStart);
	void fillNode(LinkEntity linkNode,  boolean isStart);
	
}
