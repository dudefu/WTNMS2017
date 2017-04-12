package com.jhw.adm.server.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.TestBean1;
import com.jhw.adm.server.entity.util.TestBean2;
import com.jhw.adm.server.entity.util.TestBean3;
import com.jhw.adm.server.servic.AdmServiceBeanRemote;
import com.jhw.adm.server.servic.CommonServiceBeanRemote;
import com.jhw.adm.server.servic.LoginServiceRemote;
import com.jhw.adm.server.servic.NMSServiceRemote;
import com.jhw.adm.server.servic.PingTimerRemote;

public class SessionBeanTest {
	InitialContext ctx;
	CommonServiceBeanRemote commservice = null;
	AdmServiceBeanRemote admserver = null;
	NMSServiceRemote nmsservice = null;
	PingTimerRemote timerRemote=null;
	LoginServiceRemote loginServiceRemote=null;

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		SessionBeanTest bt = new SessionBeanTest();
		bt.initContext();
//		bt.testFindAll();
//		 bt.testProcedure();
		bt.testProcedure();
	}
	
	
	public void testWarningInfo(){
		
	
	}
	@SuppressWarnings("unchecked")
	public void testProcedure(){
//		NodeEntity entity =commservice.findById((long)195,NodeEntity.class);
		nmsservice.findAllTopDiagramEntity();
		
	}

	public void savetest() {
//       NodeEntity entity =commservice.findById((long)49,NodeEntity.class);
//       commservice.deleteEntity(entity);
    
       
       /*TopDiagramEntity diagramEntity =nmsservice.findAllTopDiagramEntity();
       commservice.saveDiagram(diagramEntity);*/
		SwitchTopoNodeEntity switchTopoNodeEntity=commservice.findById((long)123, SwitchTopoNodeEntity.class);
//		
		TopDiagramEntity diagramEntity =nmsservice.findAllTopDiagramEntity();
		int x = 5;
		int y = 5;
		for(int i=1 ;i<255;i++){
			String address = "192.168.10."+i;//Integer.toString(i);
			SwitchTopoNodeEntity nodeEntity =new  SwitchTopoNodeEntity();
			SwitchNodeEntity switcher = new SwitchNodeEntity();
			SwitchBaseConfig config = new SwitchBaseConfig();
			config.setIpValue(address);
//			config.setSwitchNode(switcher);
			switcher.setBaseInfo(new SwitchBaseInfo());
			switcher.setBaseConfig(config);
			nodeEntity.setNodeEntity(switcher);
			nodeEntity.setIpValue(address);
			nodeEntity.setGuid(UUID.randomUUID().toString());
			nodeEntity.setTopDiagramEntity(diagramEntity);
			nodeEntity.setX(x);
			nodeEntity.setY(y);
			
			if(i >= 10 && i%10==0){
				x = 5;
				y += 50;
			} else {
				x += 150;
			}
			
			commservice.saveEntity(switcher);
			commservice.saveEntity(nodeEntity);
		}
		
		
		
//		nmsservice.deleteAllSwitchInfor(switchTopoNodeEntity);
		
//       List<SwitchTopoNodeEntity> list =new ArrayList<SwitchTopoNodeEntity>();
//       for(int i=0;i<100;i++){
//       SwitchTopoNodeEntity entity =new SwitchTopoNodeEntity();
//       SwitchNodeEntity nodeEntity =new SwitchNodeEntity();
//       SwitchBaseConfig baseConfig =new SwitchBaseConfig();
//       nodeEntity.setBaseConfig(baseConfig);
//       baseConfig.setSwitchNode(nodeEntity);
//       baseConfig.setIpValue(i+".12.13.14");
//       entity.setIpValue(i+".12.13.14");
//       entity.setGuid(i+"");
//       entity.setTopDiagramEntity(diagramEntity);
//       list.add(entity);
//       }
//       (list);
//       System.out.println("******************"+diagramEntity.getNodes().size());
//       System.out.println("******************"+diagramEntity.getLines().size());
		
//		TopDiagramEntity list=nmsservice.findAllTopDiagramEntity();
//		
//		System.out.println("start   "+list.getLines());
//		System.out.println("start  d "+list.getNodes());
//		System.out.println("start   "+list.getLines());
//		for(LinkEntity entity :list.getLines()){
//			System.out.println("end   "+entity.getCarrierRoute().getCarrierCode());
//			
//		}
//		System.out.println("aDiagramEntity  "+aDiagramEntity.getCarrierRoute().getCarrierCode());
//		
//		System.out.println("end   "+System.currentTimeMillis());
	}
	@SuppressWarnings("unchecked")
	public void deleteDiagram(){
		 List datas = commservice.findAll(TopDiagramEntity.class);
		 for(int i=0;i<datas.size();i++){
			 TopDiagramEntity tb1 = (TopDiagramEntity) datas.get(i);
//				 commservice.deleteTest(tb1);
			 
			
		 }
		 
	}
	public void updatetest(){
		List<TestBean1> datas = (List<TestBean1>) commservice.findAll(TestBean1.class);
		 TestBean1 tb = datas.get(0);
		Set b2 = tb.getBeans2();
		tb.getBeans2().removeAll(b2);
		TestBean2 b21 = new TestBean2();
		b21.setBean1(tb);
		b21.setDdds("xxxxxxx");
		TestBean2 b22 = new TestBean2();
		b22.setBean1(tb);
		b22.setDdds("xxxxxxx");
		tb.getBeans2().add(b21);
		tb.getBeans2().add(b22);
		commservice.updateEntity(tb);
		commservice.deleteEntities(b2);
	}

	
	
	public void testDeleteEponPort() throws InterruptedException{
		
//		Boolean flag =timerRemote.getCheckMap().get(2);
//		if(flag==null||!flag){
//			timerRemote.startTimer((long)3000);
//			timerRemote.getCheckMap().put(2, true);
//		}
//		if(timerRemote.getCheckMap().get(2)){
//			timerRemote.stopTimer();
//			timerRemote.getCheckMap().put(2, false);
//			Thread.sleep(10000);
//			timerRemote.startTimer((long)8000);
//		}
//		Boolean flag1 =timerRemote.getCheckMap().get(2);
//		if(flag1==null||!flag1){
//			timerRemote.startTimer((long)10000);
//		}
		
	}
	@SuppressWarnings("unchecked")
	public void delTest() {
		List datas = commservice.findAll(TestBean3.class);
		for (int i = 0; i < datas.size(); i++) {
			TestBean3 o = (TestBean3) datas.get(i);
			TestBean1 tb1 = o.getBean1();
			tb1.getBeans2().remove(o);
			commservice.updateEntity(tb1);
			commservice.deleteEntity(o);
		}
	}

	@SuppressWarnings("unchecked")
	public void testSetVlan() {
		List datas = commservice.findAll(SwitchNodeEntity.class);
		for (int i = 0; i < datas.size(); i++) {
			SwitchNodeEntity s = (SwitchNodeEntity) datas.get(i);
			if (s.getId() == 14) {
				String where = " where entity.switchNode=?";
				commservice.findAll(VlanConfig.class, where);
				VlanConfig vc = Tools.buildVlan();
				String mac = s.getBaseInfo().getMacValue();
				try {
					admserver.saveAndSetting(mac, MessageNoConstants.VLANSET,
							vc, "yangxiao","",1,1);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void testFoundTop() {
		List datas = commservice.findAll(TopDiagramEntity.class);
		for (int i = 0; i < datas.size(); i++) {
			TopDiagramEntity obj = (TopDiagramEntity) datas.get(i);
			// List ss = obj.getSwitchTopoNodes();
			// if(ss==null||ss.size()==0){
			// commservice.deleteEntity(obj);
			// }
		}
		System.out.println(datas.size());
	}

	public void testupdate() {
		List datas = commservice.findAll(SwitchNodeEntity.class);
		for (int i = 0; i < datas.size(); i++) {
			SwitchNodeEntity s = (SwitchNodeEntity) datas.get(0);
			// s.setBaseInfo(null);
			s.setLldpinfos(null);
			commservice.deleteEntity(s);
		}
	}

	public void testsaveswitch() {
		SwitchNodeEntity entity = Tools.buildSwitch1();

		commservice.saveEntity(entity);
	}

	public void testDeleFep() {
		List datas = commservice.findAll(SwitchNodeEntity.class);
		for (int i = 0; i < datas.size(); i++) {
			Object o = datas.get(i);
			commservice.deleteEntity(o);
		}
	}

	public void testSaveFEP() {
		List datas = new ArrayList();
		FEPEntity fep1 = new FEPEntity();
		fep1.setCode("GUANGDONG");
		fep1.setIpValue("192.168.1.1");

		fep1.setLoginName("guangdong");
		fep1.setLoginPassword("123456");
		IPSegment segment = new IPSegment();
		segment.setBeginIp("192.168.12.1");
		segment.setEndIp("129.168.12.255");
		datas.add(segment);
		fep1.setSegment(datas);
		commservice.saveEntity(fep1);
	}

	public void testfind() {
		commservice.updateLink(1, (long)1000);
		// String jpql = "select entity from "+
		// SwitchNodeEntity.class.getName()+" as entity where entity.baseConfig.ipValue=?";
//		SwitchBaseConfig entity = commservice.getSwitchByIp("192.168.13.145");
//		if (entity != null) {
//			System.out.println("===============");
//		}
		
		try {
//			TestEntity entity =new TestEntity();
//			Set<TestEntityChild> set =new HashSet<TestEntityChild>();
//			TestEntityChild testEntityChild =new TestEntityChild();
//			testEntityChild.setEntity(entity);
//			set.add(testEntityChild);
//			entity.setEntityChilds(set);
//			commservice.saveEntity(entity);
//			
//			String sql ="select c from TestEntity c left join fetch c.entityChilds ";
//			List<TestEntity> list = commservice.findAll(sql);
//			
//			TestEntity entity2 =list.get(0);
//			
//			TestEntityChild testEntityChild =new TestEntityChild();
//			testEntityChild.setEntity(entity2);
//			commservice.saveEntity(testEntityChild);
//			
//			System.out.println("***************"+entity2.getEntityChilds().size());
//			
//			for(TestEntityChild child :entity2.getEntityChilds()){
//				System.out.println("***************"+child.getId());
////				commservice.deleteEntity(TestEntityChild.class, child.getId());
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@SuppressWarnings("unchecked")
	public void testDeletes() {
//		List datas = commservice.findAll(VlanEntity.class);
		TopDiagramEntity diagramEntity =nmsservice.findAllTopDiagramEntity();
		commservice.saveDiagram(diagramEntity);
//		if (datas != null) {
//			VlanEntity entity = (VlanEntity) datas.get(0);
//			commservice.deleteEntity(entity);
//		}
	}

	@SuppressWarnings("unchecked")
	public void testUpdate() {
		List datas = commservice.findAll(SwitchNodeEntity.class);
		if (datas != null) {
			SwitchNodeEntity entity = (SwitchNodeEntity) datas.get(0);
			SwitchBaseInfo bi = entity.getBaseInfo();
			bi.setDeviceName("yyyyyyyyyyyyyyyœÍœ∏–≈œ¢");
			commservice.updateEntity(bi);
		}
	}

	@SuppressWarnings("unchecked")
	public void testFindAll() {
		List datas = commservice.findAll("select entity from "
				+ SwitchTopoNodeEntity.class.getName() + " as entity ");
		// List datas = commservice.findAll(TopDiagramEntity.class);
		@SuppressWarnings("unused")
		int size = datas.size();
		for (int i = 0; i < datas.size(); i++) {
			Object o = datas.get(i);
		}
	}
    
	public  void savaVlan(){
//		LinkEntity linkEntity =nmsservice.queryLinkEntity(1000);
//		NodeEntity entity =linkEntity.getNode1();
//		NodeEntity entity2 =linkEntity.getNode2();
//		if(entity!=null){
//		System.out.println("XXXXXXXXX"+entity.getX());
//		System.out.println("YYYYYYYYY"+entity.getY());
//		
//		System.out.println("XXXXXXXXX"+entity2.getX());
//		System.out.println("YYYYYYYYY"+entity2.getY());
//		}else{
//			
//			System.out.println("nullLLLLLLLLLLL");
//		}
	}
	
	public void insertSwitcherLayer3() {
		TopDiagramEntity diagramEntity =commservice.findById((long)5, TopDiagramEntity.class);
		SwitchTopoNodeLevel3 layer3Node = new SwitchTopoNodeLevel3();
		SwitchLayer3 layer3 = new SwitchLayer3();
		layer3.setIpValue("192.168.10.5");
		layer3.setMacValue("asvababasvb");
//		commservice.saveEntity(layer3);
		layer3Node.setSwitchLayer3(layer3);
		layer3Node.setX(100);
		layer3Node.setY(100);
		layer3Node.setName(layer3.getIpValue());
//		commservice.saveEntity(layer3Node);
		diagramEntity.getNodes().add(layer3Node);
		commservice.updateEntity(diagramEntity);
	}
	
	public void initContext() {
		try {
			ctx = new InitialContext();
			commservice = (CommonServiceBeanRemote) ctx
					.lookup("remote/CommonServiceBean");
			admserver = (AdmServiceBeanRemote) ctx
					.lookup("remote/AdmServiceBean");
			nmsservice = (NMSServiceRemote) ctx.lookup("remote/NMSServiceRemote");
			timerRemote =(PingTimerRemote) ctx.lookup("remote/PingTimerRemote");
loginServiceRemote=(LoginServiceRemote) ctx.lookup("remote/LoginService");
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
	}

}
