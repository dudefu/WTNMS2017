
package com.jhw.adm.client.ui;

import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.joran.JoranConfigurator;
//import ch.qos.logback.core.joran.spi.JoranException;
//import ch.qos.logback.core.util.StatusPrinter;

import com.jhw.adm.client.core.JndiLookupException;
import com.jhw.adm.client.core.LazyJndiObjectLocator;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.SystemData;
import com.jhw.adm.client.resources.ResourceManager;
import com.jhw.adm.server.entity.carriers.MonitorConfigEntity;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.PersonInfo;
import com.jhw.adm.server.entity.system.RoleEntity;
import com.jhw.adm.server.entity.system.TimeConfig;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.VirtualType;
import com.jhw.adm.server.entity.warning.WarningCategory;
import com.jhw.adm.server.entity.warning.WarningLevel;
import com.jhw.adm.server.entity.warning.WarningType;
import com.jhw.adm.server.servic.CommonServiceBeanRemote;

public class DebugUI {
	private DebugUI() {
	}

	public static void main(String[] args) {
		DebugUI.getInstance().launch(args);
	}

	@SuppressWarnings("unchecked")
	private void launch(String[] args) {
//		saveFEP();		
		configureLogging();
		configureSpringContext();
		LazyJndiObjectLocator lazyCommonObjectLocator = (LazyJndiObjectLocator) springContext.getBean(RemoteServer.COMMON_OBJECT);
		Properties jndiEnvironment = lazyCommonObjectLocator.getJndiEnvironment();
		String url = jndiEnvironment.getProperty("java.naming.provider.url");
		try {
			commService = (CommonServiceBeanRemote) lazyCommonObjectLocator.start();
			
			List<TopDiagramEntity> topDiagramEntityList = (List<TopDiagramEntity>) commService.findAll(TopDiagramEntity.class);
			if(null != topDiagramEntityList && 0 != topDiagramEntityList.size()){
				JOptionPane.showMessageDialog(null, "系统数据不为空，请清空数据后再初始化！", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int result = JOptionPane.showConfirmDialog(null, String.format("请确认在[%s]生成数据？！", url), "提示", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				initData();
				JOptionPane.showMessageDialog(null, String.format("服务器[%s]数据生成完毕", url), "成功", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (JndiLookupException ex) {
			LOG.error("数据生成错误", ex);
			JOptionPane.showMessageDialog(null, String.format("连接服务器[%s]失败!", url), "错误", JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			LOG.error("数据生成错误", ex);
			JOptionPane.showMessageDialog(null, String.format("服务器[%s]数据生成失败!", url), "错误", JOptionPane.ERROR_MESSAGE);
		} finally {
			System.exit(0);
		}
	}

	private void initData() {
		saveFEP();
		addRole();
		addRegion();
		configRegionUser();
		addTrapWarning();
		insertTimeConfig();
		insertDefaultVirtualType();
	}
	
	/**
	 * 插入虚拟网元默认类型
	 */
	private void insertDefaultVirtualType() {
		//
		ResourceManager resourceManager = new ResourceManager();
		ImageIcon imageIcon = new ImageIcon(resourceManager.getURL(NetworkConstants.SNMP_DEVICE));
		BufferedImage bufferedImage = Images.toBufferedImage(imageIcon.getImage());
		
		ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
		
		try {
			ImageIO.write(bufferedImage, "png", imageOutputStream);
		} catch (IOException e) {
			LOG.error("error occur when writing image", e);
		} finally {
			if (null != imageOutputStream) {
				try {
					imageOutputStream.close();
				} catch (IOException e) {
					LOG.error("error occur when closing ByteArrayOutputStream", e);
				}
			}
		}
		
		byte[] imageByteArray = imageOutputStream.toByteArray();
		//
		VirtualType virtualType = new VirtualType();
		virtualType.setType("SNMP设备");
		virtualType.setBytes(imageByteArray);
		commService.saveEntity(virtualType);
	}
	
	/**
	 * 插入监测参数配置
	 */
	private void insertTimeConfig() {
		TimeConfig timeConfig = new TimeConfig();
		timeConfig.setHeartbeatMaxTime(10);
		timeConfig.setHearbeatdelayMaxTime(5);
		timeConfig.setTuopoMaxTime(120);
		timeConfig.setSynchoizeMaxTime(120);
		timeConfig.setParmConfigMaxTime(120);
		
		commService.saveEntity(timeConfig);
		
		MonitorConfigEntity monitorEntity = new MonitorConfigEntity();
		monitorEntity.setFrequence(1);
		monitorEntity.setDistance(200);
		monitorEntity.setOuttime(5);
		commService.saveEntity(monitorEntity);
	}

	/**
	 * 增加前置机
	 */
	public void saveFEP() {
//		FEPEntity fepEntity = new FEPEntity();
//
//		fepEntity.setCode("shenzhen");
//		fepEntity.setFepName("shenzhen");
//		fepEntity.setLoginName("shenzhen");
//		fepEntity.setLoginPassword("shenzhen");
//		fepEntity.setIpValue("192.168.5.185");
//		fepEntity.setDirectSwitchIp("10.45.99.2");
//
//		List<IPSegment> list = new ArrayList<IPSegment>();
//
//		IPSegment ipsegment = new IPSegment();
//		ipsegment.setBeginIp("10.45.99.1");
//		ipsegment.setEndIp("10.45.99.254");
//		ipsegment.setFepEntity(fepEntity);
//		list.add(ipsegment);
//		
//		IPSegment ipsegment2 = new IPSegment();
//		ipsegment2.setBeginIp("192.168.10.1");
//		ipsegment2.setEndIp("192.168.10.254");
//		ipsegment2.setFepEntity(fepEntity);
//		list.add(ipsegment2);
//		
//		fepEntity.setSegment(list);
//
//		StatusEntity statusEntity = new StatusEntity();
//		fepEntity.setStatus(statusEntity);
//		
//		commService.saveEntity(topDiagram);
		SystemData systemData = loadSystemData();
		
		TopDiagramEntity topDiagram = systemData.getTopDiagram();
		Set<NodeEntity> setOfNode = new HashSet<NodeEntity>();		
		int index = 0;
		for (FEPEntity fep : systemData.getFeps()) {
			FEPEntity savedFep = (FEPEntity)commService.saveEntity(fep);
			FEPTopoNodeEntity fepNode = new FEPTopoNodeEntity();
			fepNode.setCode(savedFep.getCode());
			fepNode.setIpValue(savedFep.getIpValue());
			fepNode.setX(10 + index * 80);
			fepNode.setY(10);
			fepNode.setGuid(UUID.randomUUID().toString());
			fepNode.setFepEntity(savedFep);
			fepNode.setTopDiagramEntity(topDiagram);
			index++;
			setOfNode.add(fepNode);

		}

		topDiagram.setNodes(setOfNode);
		commService.saveEntity(topDiagram);
		
//		SystemData systemData = new SystemData();
//		Set<FEPEntity> setOfFep = new HashSet<FEPEntity>();
//		setOfFep.add(fepEntity);
//		TopDiagramEntity topDiagram = new TopDiagramEntity();
//		topDiagram.setName("网络拓扑图");
//		topDiagram.setNodes(new HashSet<NodeEntity>());
//		systemData.setTopDiagram(topDiagram);
//		systemData.setFeps(setOfFep);
//		saveObject2File(systemData, "conf/system.data.xml");
	}
	
	private void saveObject2File(Object obj, String fileName) {
		File fo = new File(fileName);
		FileOutputStream fos = null;
		XMLEncoder encoder = null;
		try {
			fos = new FileOutputStream(fo);
			encoder = new XMLEncoder(fos);
			encoder.writeObject(obj);
			encoder.flush();
		} catch (IOException ex) {
			LOG.error("save config error", ex);
		} finally {
			if (encoder != null) {
				encoder.close();
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {
					LOG.error("FileOutputStream.close error", ex);
				}
			}
		}
	}
	
	private SystemData loadSystemData() {
		File fin = new File("conf/system.data.xml");
		FileInputStream fis = null;
		XMLDecoder decoder = null;
		SystemData systemData = null;
		try {
			fis = new FileInputStream(fin);
			decoder = new XMLDecoder(fis);
			systemData = (SystemData) decoder.readObject();
		} catch (Exception ex) {
			LOG.error("load config error", ex);
		} finally {
			if (decoder != null) {
				decoder.close();
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) {
					LOG.error("FileInputStream.close error", ex);
				}
			}
		}
		
		return systemData;
	}

	/**
	 * 增加角色
	 */
	public void addRole() {
		RoleEntity roleEntity1 = new RoleEntity();
		roleEntity1.setRoleName("超级管理员");
		roleEntity1.setRoleCode(Constants.ADMINCODE);
		RoleEntity roleEntity2 = new RoleEntity();
		roleEntity2.setRoleName("区域管理员");
		roleEntity2.setRoleCode(Constants.MANAGERCODE);
		RoleEntity roleEntity3 = new RoleEntity();
		roleEntity3.setRoleName("普通用户");
		roleEntity3.setRoleCode(Constants.USERCODE);

		List<RoleEntity> list = new ArrayList<RoleEntity>();
		list.add(roleEntity1);
		list.add(roleEntity2);
		list.add(roleEntity3);

		commService.saveEntities(list);

		RoleEntity adminRole = null;
		RoleEntity managerRole = null;
		RoleEntity userRole = null;
		List<RoleEntity> roleEntityList = (List<RoleEntity>) commService.findAll(RoleEntity.class);
		for (int i = 0; i < roleEntityList.size(); i++) {
			RoleEntity role = roleEntityList.get(i);
			if (role.getRoleCode() == Constants.ADMINCODE) {
				adminRole = role;
			}
			if (role.getRoleCode() == Constants.MANAGERCODE) {
				managerRole = role;
			}
			if (role.getRoleCode() == Constants.USERCODE) {
				userRole = role;
			}
		}

		UserEntity adminUser = new UserEntity();
		adminUser.setUserName("admin");
		adminUser.setPassword("WTFEP");
		adminUser.setRole(adminRole);
		PersonInfo info = new PersonInfo();
		info.setName("超级用户");
//		info.setMobile("13907553256");
//		info.setEmail("admin@126.com");
		adminUser.setPersonInfo(info);

		UserEntity managerUser = new UserEntity();
		managerUser.setUserName("manager");
		managerUser.setPassword("WTFEP");
		managerUser.setRole(managerRole);
		PersonInfo managerinfo = new PersonInfo();
		managerinfo.setName("区域管理员");
//		managerinfo.setMobile("13907553256");
//		managerinfo.setEmail("admin@126.com");
		managerUser.setPersonInfo(managerinfo);

		UserEntity guestUser = new UserEntity();
		guestUser.setUserName("guest");
		guestUser.setPassword("WTFEP");
		guestUser.setRole(userRole);
		PersonInfo userInfo = new PersonInfo();
		userInfo.setName("普通用户");
//		userInfo.setMobile("13907553256");
//		userInfo.setEmail("admin@126.com");
		guestUser.setPersonInfo(userInfo);

		commService.saveEntity(adminUser);
		commService.saveEntity(managerUser);
		commService.saveEntity(guestUser);
	}

	/**
	 * 增加区域
	 */
	private void addRegion() {
		AreaEntity areaEntity1 = new AreaEntity();
		areaEntity1.setName("深圳");

		commService.saveEntity(areaEntity1);

		AreaEntity areaEntity2 = new AreaEntity();
		areaEntity2.setName("广州");
		commService.saveEntity(areaEntity2);

		AreaEntity superEntity = null;
		List<AreaEntity> areaEntityList = (List<AreaEntity>) commService.findAll(AreaEntity.class);
		for (int i = 0; i < areaEntityList.size(); i++) {
			superEntity = areaEntityList.get(i);
			if (superEntity.getName().equals("深圳")) {
				break;
			}
		}
		AreaEntity areaEntity3 = new AreaEntity();
		areaEntity3.setName("福田");
		areaEntity3.setSuperArea(superEntity);
		AreaEntity areaEntity4 = new AreaEntity();
		areaEntity4.setName("南山");
		areaEntity4.setSuperArea(superEntity);
		AreaEntity areaEntity5 = new AreaEntity();
		areaEntity5.setName("罗湖");
		areaEntity5.setSuperArea(superEntity);

		List<AreaEntity> list = new ArrayList<AreaEntity>();
		list.add(areaEntity3);
		list.add(areaEntity4);
		list.add(areaEntity5);
		commService.saveEntities(list);

		// //////////////////////////////////////////////////////////
		AreaEntity superEntitys = null;
		List<AreaEntity> areaEntityLists = (List<AreaEntity>) commService.findAll(AreaEntity.class);
		for (int i = 0; i < areaEntityLists.size(); i++) {
			superEntitys = areaEntityLists.get(i);
			if (superEntitys.getName().equals("广州")) {
				break;
			}
		}
		AreaEntity areaEntity3s = new AreaEntity();
		areaEntity3s.setName("越秀");
		areaEntity3s.setSuperArea(superEntitys);
		AreaEntity areaEntity4s = new AreaEntity();
		areaEntity4s.setName("番禺");
		areaEntity4s.setSuperArea(superEntitys);
		AreaEntity areaEntity5s = new AreaEntity();
		areaEntity5s.setName("花城");
		areaEntity5s.setSuperArea(superEntitys);

		List<AreaEntity> lists = new ArrayList<AreaEntity>();
		lists.add(areaEntity3s);
		lists.add(areaEntity4s);
		lists.add(areaEntity5s);
		commService.saveEntities(lists);
	}

	/**
	 * 把admin配置到福田中
	 */
	private void configRegionUser() {
		List<AreaEntity> listss = new ArrayList<AreaEntity>();
		AreaEntity superEntityss = null;
		List<AreaEntity> areaEntityListss = (List<AreaEntity>) commService.findAll(AreaEntity.class);
		for (int i = 0; i < areaEntityListss.size(); i++) {
			superEntityss = areaEntityListss.get(i);
			if (superEntityss.getName().equals("福田")) {
				break;
			}
		}
		listss.add(superEntityss);

		String level = "1,2,3,4";
		String style = "S,E";

		UserEntity userEntity = null;
		List<UserEntity> userEntityLists = (List<UserEntity>) commService.findAll(UserEntity.class);
		for (int i = 0; i < userEntityLists.size(); i++) {
			userEntity = userEntityLists.get(i);
			if (userEntity.getUserName().equals("admin")) {
				break;
			}
		}

		userEntity.setCareLevel(level);
		userEntity.setWarningStyle(style);
		userEntity.getAreas().addAll(listss);
		commService.updateEntity(userEntity);
	}

	/**
	 * 增加trap告警配置
	 */
	private void addTrapWarning() {
		List<WarningType> warningDataList = new ArrayList<WarningType>();

		WarningType warningType1 = new WarningType();
		warningType1.setWarningType(Constants.COLDSTART);
		warningType1.setWarningLevel(Constants.GENERAL);
		warningType1.setWarningStyle("M,V");

		WarningType warningType2 = new WarningType();
		warningType2.setWarningType(Constants.WARMSTART);
		warningType2.setWarningLevel(Constants.INFORM);
		warningType2.setWarningStyle("M,V");

		WarningType warningType3 = new WarningType();
		warningType3.setWarningType(Constants.LINKDOWN);
		warningType3.setWarningLevel(Constants.URGENCY);
		warningType3.setWarningStyle("M,V");

		WarningType warningType4 = new WarningType();
		warningType4.setWarningType(Constants.LINKUP);
		warningType4.setWarningLevel(Constants.INFORM);
		warningType4.setWarningStyle("M,V");

		WarningType warningType5 = new WarningType();
		warningType5.setWarningType(Constants.REMONTHING);
		warningType5.setWarningLevel(Constants.SERIOUS);
		warningType5.setWarningStyle("M,V");
		
		
		WarningType warningType6 = new WarningType();
		warningType6.setWarningType(Constants.PINGOUT);
		warningType6.setWarningLevel(Constants.SERIOUS);
		warningType6.setWarningStyle("M,V");
		
		WarningType warningType7 = new WarningType();
		warningType7.setWarningType(Constants.PINGIN);
		warningType7.setWarningLevel(Constants.INFORM);
		warningType7.setWarningStyle("M,V");
		
		WarningType warningType8 = new WarningType();
		warningType8.setWarningType(Constants.FEP_DISCONNECT);
		warningType8.setWarningLevel(Constants.SERIOUS);
		warningType8.setWarningStyle("M,V");
		
		WarningType warningType9 = new WarningType();
		warningType9.setWarningType(Constants.FEP_CONNECT);
		warningType9.setWarningLevel(Constants.GENERAL);
		warningType9.setWarningStyle("M,V");
		
		WarningType warningType10 = new WarningType();
		warningType10.setWarningType(Constants.SYSLOG);
		warningType10.setWarningLevel(Constants.INFORM);
		warningType10.setWarningStyle("M,V");
		

		warningDataList.add(0, warningType1);
		warningDataList.add(1, warningType2);
		warningDataList.add(2, warningType3);
		warningDataList.add(3, warningType4);
		warningDataList.add(4, warningType5);
		warningDataList.add(5, warningType6);
		warningDataList.add(6, warningType7);
		warningDataList.add(7, warningType8);
		warningDataList.add(8, warningType9);
		warningDataList.add(9, warningType10);
		
		commService.saveEntities(warningDataList);
		
		WarningLevel urgency = new WarningLevel();
		urgency.setWarningLevel(Constants.URGENCY);
		urgency.setDesccs("紧急");
		
		WarningLevel serious = new WarningLevel();
		serious.setWarningLevel(Constants.SERIOUS);
		serious.setDesccs("严重");
		
		WarningLevel inform = new WarningLevel();
		inform.setWarningLevel(Constants.INFORM);
		inform.setDesccs("通知");
		
		WarningLevel general = new WarningLevel();
		general.setWarningLevel(Constants.GENERAL);
		general.setDesccs("普通");
		
		commService.saveEntity(urgency);
		commService.saveEntity(serious);
		commService.saveEntity(inform);
		commService.saveEntity(general);
		
		
		WarningCategory category1 = new WarningCategory();
		category1.setWarningCategory(Constants.equipment_Warning);
		category1.setDesccs("设备告警");
		
		WarningCategory category2 = new WarningCategory();
		category2.setWarningCategory(Constants.port_Warning);
		category2.setDesccs("端口告警");
		
		WarningCategory category3 = new WarningCategory();
		category3.setWarningCategory(Constants.protocol_Warning);
		category3.setDesccs("协议告警");
		
		WarningCategory category4 = new WarningCategory();
		category4.setWarningCategory(Constants.performance_Warning);
		category4.setDesccs("性能告警");
		
		WarningCategory category5 = new WarningCategory();
		category5.setWarningCategory(Constants.NMS_Warning);
		category5.setDesccs("网管告警");
		
		commService.saveEntity(category1);
		commService.saveEntity(category2);
		commService.saveEntity(category3);
		commService.saveEntity(category4);
		commService.saveEntity(category5);
	}

	private static DebugUI getInstance() {
		return LazyHolder.instance;
	}

	private void configureLogging() {
//		LoggerContext loggerContext = (LoggerContext) LoggerFactory
//				.getILoggerFactory();
//		JoranConfigurator joranConfigurator = new JoranConfigurator();
//		joranConfigurator.setContext(loggerContext);
//		loggerContext.reset();
//		try {
//			joranConfigurator.doConfigure(LOGBACK_CONFIG_FILE);
//		} catch (JoranException e) {
//			StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
//		}
//		LOG.info("Configure logging ok...");
	}

	private void configureSpringContext() {
		springContext = new FileSystemXmlApplicationContext(
				SPRING_CONFIG_LOCATIONS);
		springContext.registerShutdownHook();
		LOG.info("Configure spring context ok...");
	}

	private static class LazyHolder {
		private static DebugUI instance = new DebugUI();
	}

	private CommonServiceBeanRemote commService;
	private FileSystemXmlApplicationContext springContext;
	private static final Logger LOG = LoggerFactory
			.getLogger(DebugUI.class);

	private static final String PASSWORD = "400-888-0018";
	private static final String LOGBACK_CONFIG_FILE = "conf/logback.xml";
	private static final String[] SPRING_CONFIG_LOCATIONS = new String[] { "conf/applicationContext.xml" };
}