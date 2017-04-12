package com.jhw.adm.client.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.test.SpringJUnit47ClassRunner;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.PersonInfo;
import com.jhw.adm.server.entity.system.RoleEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.WarningType;

@RunWith(SpringJUnit47ClassRunner.class)   
@ContextConfiguration(locations={"file:conf/applicationContext.xml"})
public class TestAreaEntityRepository extends AbstractJUnit4SpringContextTests {
	
	@Before
	public void before() {
		remoteServer.connect();
	}
		
	@Test
	public void saveUserInfo() {
		initData();
		
	}
	
	private void initData(){
		addRole();
		
		addRegion();
		
		configRegionUser();
		
		addTrapWarning();
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

		List list = new ArrayList<RoleEntity>();
		list.add(roleEntity1);
		list.add(roleEntity2);
		list.add(roleEntity3);
		
		remoteServer.getService().saveEntities(list);
		
		
		RoleEntity roleEntity = null;
		List<RoleEntity> roleEntityList = (List<RoleEntity>)remoteServer.getService().findAll(RoleEntity.class);
		for (int i = 0 ; i < roleEntityList.size(); i++){
			roleEntity = roleEntityList.get(i);
			if (roleEntity.getRoleCode() == Constants.ADMINCODE){
				break;
			}
		}
		
		UserEntity userEntity = new UserEntity();
		userEntity.setUserName("admin");
		userEntity.setPassword("admin");
		userEntity.setRole(roleEntity);
		PersonInfo info = new PersonInfo();
		info.setName("杨霄");
		info.setMobile("13907553256");
		info.setEmail("aaa126@126.com");
		userEntity.setPersonInfo(info);
		
		InetAddress localHost = null;
		try {
			localHost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String address = localHost.getHostAddress();
		userEntity.setClientIp(address);
		
		
		
		remoteServer.getService().saveEntity(userEntity);
	}
	
	/**
	 * 增加区域
	 */
	private void addRegion(){
		AreaEntity areaEntity1 = new AreaEntity();
		areaEntity1.setName("深圳");
		
		remoteServer.getService().saveEntity(areaEntity1);
		
		AreaEntity areaEntity2 = new AreaEntity();
		areaEntity2.setName("广州");
		remoteServer.getService().saveEntity(areaEntity2);
		
		
		AreaEntity superEntity = null;
		List<AreaEntity> areaEntityList = (List<AreaEntity>)remoteServer.getService().findAll(AreaEntity.class);
		for(int i = 0 ; i <areaEntityList.size(); i++ ){
			superEntity = areaEntityList.get(i);
			if (superEntity.getName().equals("深圳")){
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
		remoteServer.getService().saveEntities(list);
		
		////////////////////////////////////////////////////////////
		AreaEntity superEntitys = null;
		List<AreaEntity> areaEntityLists = (List<AreaEntity>)remoteServer.getService().findAll(AreaEntity.class);
		for(int i = 0 ; i <areaEntityLists.size(); i++ ){
			superEntitys = areaEntityLists.get(i);
			if (superEntitys.getName().equals("广州")){
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
		remoteServer.getService().saveEntities(lists);
	}
	
	/**
	 * 把admin配置到福田中
	 */
	private void configRegionUser(){
		List<AreaEntity> listss = new ArrayList<AreaEntity>();
		AreaEntity superEntityss = null;
		List<AreaEntity> areaEntityListss = (List<AreaEntity>)remoteServer.getService().findAll(AreaEntity.class);
		for(int i = 0 ; i <areaEntityListss.size(); i++ ){
			superEntityss = areaEntityListss.get(i);
			if (superEntityss.getName().equals("福田")){
				break;
			}
		}
		listss.add(superEntityss);

		String level = "1,2,3,4";
		String style = "S,E";
		
		UserEntity userEntity = null;
		List<UserEntity> userEntityLists = (List<UserEntity>)remoteServer.getService().findAll(UserEntity.class);
		for (int i = 0 ; i < userEntityLists.size(); i++){
			userEntity = userEntityLists.get(i);
			if (userEntity.getUserName().equals("admin")){
				break;
			}
		}
		
		userEntity.setCareLevel(level);
		userEntity.setWarningStyle(style);
		userEntity.getAreas().addAll(listss);
		remoteServer.getService().updateEntity(userEntity);
	}
	
	/**
	 * 增加trap告警配置
	 */
	private void addTrapWarning(){
		List<WarningType> warningDataList = new ArrayList<WarningType>();
		
		WarningType warningType1 = new WarningType();
		warningType1.setWarningType(0);
		warningType1.setWarningLevel(4);
		warningType1.setWarningStyle("M,V");
		
		WarningType warningType2 = new WarningType();
		warningType2.setWarningType(1);
		warningType2.setWarningLevel(4);
		warningType2.setWarningStyle("M,V");
		
		WarningType warningType3 = new WarningType();
		warningType3.setWarningType(2);
		warningType3.setWarningLevel(4);
		warningType3.setWarningStyle("M,V");
		
		WarningType warningType4 = new WarningType();
		warningType4.setWarningType(3);
		warningType4.setWarningLevel(4);
		warningType4.setWarningStyle("M,V");
		
		WarningType warningType5 = new WarningType();
		warningType5.setWarningType(6);
		warningType5.setWarningLevel(2);
		warningType5.setWarningStyle("M,V");
		
		warningDataList.add(0,warningType1);
		warningDataList.add(1,warningType2);
		warningDataList.add(2,warningType3);
		warningDataList.add(3,warningType4);
		warningDataList.add(4,warningType5);
		remoteServer.getService().saveEntities(warningDataList);
	}
	
	@Autowired
	@Qualifier(EquipmentRepository.ID)
	private EquipmentRepository equipmentRepository;

	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
}