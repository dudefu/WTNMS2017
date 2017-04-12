package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.Assert;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.test.SpringJUnit47ClassRunner;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;

@RunWith(SpringJUnit47ClassRunner.class)   
@ContextConfiguration(locations={"file:conf/applicationContext.xml"})
public class TestFrontRepository extends AbstractJUnit4SpringContextTests {
	
	@Before
	public void before() {
		remoteServer.connect();
	}
		
	@Test
	public void saveTopDiagram() {
		FEPEntity fepEntity = new FEPEntity();
		fepEntity.setCode("shenzhen");
		fepEntity.setFepName("qianzhiji");
		fepEntity.setLoginName("admin");
		fepEntity.setLoginPassword("admin");
		fepEntity.setIpValue("192.168.12.185");
		fepEntity.setDirectSwitchIp("192.168.13.148");//
		
		List list = new ArrayList<IPSegment>();
		
		IPSegment ipsegment1 = new IPSegment();
		ipsegment1.setBeginIp("192.168.13.1");
		ipsegment1.setEndIp("192.168.13.254");
		ipsegment1.setFepEntity(fepEntity);
		list.add(ipsegment1);
		
		fepEntity.setSegment(list);
		remoteServer.getService().saveEntity(fepEntity);
	
//		Assert.notNull(savedTopDiagramEntity, "TopDiagramEntity should not be null");
//		Assert.isTrue(savedTopDiagramEntity.getSwitchTopoNodes().size() > 0, "SwitchTopoNodes().size() shoule be gt 0");
	}	
	
	@Autowired
	@Qualifier(EquipmentRepository.ID)
	private EquipmentRepository equipmentRepository;

	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
}