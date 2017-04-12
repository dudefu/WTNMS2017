package com.jhw.adm.client.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.test.SpringJUnit47ClassRunner;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;

@RunWith(SpringJUnit47ClassRunner.class)   
@ContextConfiguration(locations={"file:conf/applicationContext.xml"})
public class TestRemoteServer extends AbstractJUnit4SpringContextTests {

	@Before
	public void before() {
		remoteServer.connect();
	}
	
	@Test
	public void findAllTopDiagram() {
		remoteServer.getService().findAll(TopDiagramEntity.class);
	}
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
}