package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;

/**
 * 设备仓库，负责顶层图形、交换机、载波机、子网的查找和储存。
 */
@Component(EquipmentRepository.ID)
public class EquipmentRepository {
	
	@PostConstruct
	protected void initialize() {
	}
	
	/**
	 * 保存或者更新拓扑图
	 * @return 如果返回NULL表示拓扑图已经被更改或者保存出错，否则返回保存后的拓扑图
	 */
	public TopDiagramEntity saveOrUpdateDiagram(TopDiagramEntity topDiagramEntity) {

		TopDiagramEntity savedDiagram = null;
		
		try {
			savedDiagram = remoteServer.getService().saveDiagram(topDiagramEntity);
		} catch (Exception ex) {
			LOG.error("save diagram error", ex);
		}
		
		return savedDiagram;
	}
	
	public void deleteNode(Object node) {
		remoteServer.getService().deleteEntity(node);
	}
	
	public Object savaNode(Object node) {
		return remoteServer.getService().saveEntity(node);
	}
	
	/**
	 * 更新拓扑节点
	 */
	public void updateNode(Object node) {
		remoteServer.getService().updateEntity(node);
	}
	
	/**
	 * 删除拓扑图
	 */
	public void deleteDiagram(TopDiagramEntity topDiagramEntity) {
		Assert.isNull(topDiagramEntity, "Diagram must not be null");
		remoteServer.getService().deleteEntity(topDiagramEntity);
	}
	
	/**
	 * 查找拓扑图
	 */
	@SuppressWarnings("unchecked")
	public TopDiagramEntity findDiagram(UserEntity userEntity) {
//		List<TopDiagramEntity> list = (List<TopDiagramEntity>)
//			remoteServer.getService().findAll(TopDiagramEntity.class);
		
//		if (list != null && list.size() > 0) {
//			TopDiagramEntity topDiagram = list.get(0); 
//			if (topDiagram.getNodes() == null) {
//				topDiagram.setNodes(new HashSet<NodeEntity>());
//			}			
//			if (topDiagram.getLines() == null) {
//				topDiagram.setLines(new HashSet<LinkEntity>());
//			}
//			
//			return topDiagram;
//		} else {
//			return null;
//		}
		
		TopDiagramEntity topDiagram = remoteServer.getNmsService().findAllTopDiagramEntity(userEntity);
		if (topDiagram.getNodes() == null) {
			topDiagram.setNodes(new HashSet<NodeEntity>());
		}			
		if (topDiagram.getLines() == null) {
			topDiagram.setLines(new HashSet<LinkEntity>());
		}
		return topDiagram;
	}
	
	public SubNetTopoNodeEntity fillSubnet(Long diagramId, String subnetGuid,UserEntity userEntity) {
		SubNetTopoNodeEntity filledSubnet = remoteServer.getNmsService().getSubNetTopoNodeEntity(
				diagramId, subnetGuid, userEntity);
		
		if (filledSubnet == null) {
			filledSubnet = new SubNetTopoNodeEntity();
			LOG.warn("subnet must not be null");
		}
		
		if (filledSubnet.getNodes() == null) {
			filledSubnet.setNodes(new ArrayList<NodeEntity>());
		}
		if (filledSubnet.getLines() == null) {
			filledSubnet.setLines(new ArrayList<LinkEntity>());
		}
		
		return filledSubnet;
	}
	
	/**
	 * 查找前置机
	 */
	@SuppressWarnings("unchecked")
	public List<FEPTopoNodeEntity> findFrontEndNode() {
		List<FEPTopoNodeEntity> listOfFrontEnd = (List<FEPTopoNodeEntity>)
			remoteServer.getService().findAll(FEPTopoNodeEntity.class);
		
		if (listOfFrontEnd == null) {
			listOfFrontEnd = new ArrayList<FEPTopoNodeEntity>();
		}
		
		return listOfFrontEnd;
	}
	
	/**
	 * 查找前置机
	 */
	@SuppressWarnings("unchecked")
	public List<FEPEntity> findAllFep() {
		List<FEPEntity> listOfFrontEnd = (List<FEPEntity>)
			remoteServer.getService().findAll(FEPEntity.class);
		
		if (listOfFrontEnd == null) {
			listOfFrontEnd = new ArrayList<FEPEntity>();
		}
		
		return listOfFrontEnd;
	}
	
	/**
	 * 查找OLT
	 */
	@SuppressWarnings("unchecked")
	public List<OLTEntity> findOlts() {
		List<OLTEntity> listOfOlt = (List<OLTEntity>)
			remoteServer.getService().findAll(OLTEntity.class);
		
		if (listOfOlt == null) {
			listOfOlt = new ArrayList<OLTEntity>();
		}
		
		return listOfOlt;
	}
	
	/**
	 * 查找ONU
	 */
	@SuppressWarnings("unchecked")
	public List<ONUEntity> findOnus() {
		List<ONUEntity> listOfOnu = (List<ONUEntity>)
			remoteServer.getService().findAll(ONUEntity.class);
		
		if (listOfOnu == null) {
			listOfOnu = new ArrayList<ONUEntity>();
		}
		
		return listOfOnu;
	}
		
	/**
	 * 查找二层交换机
	 */
	@SuppressWarnings("unchecked")
	public List<SwitchNodeEntity> findSwitchers() {
		List<SwitchNodeEntity> listOfSwitchNode = new ArrayList<SwitchNodeEntity>();
		List<?> result = remoteServer.getNmsService().findDevice(SwitchNodeEntity.class);
		if (result != null) {
			listOfSwitchNode = (List<SwitchNodeEntity>)result;
		}
		return listOfSwitchNode;
	}
	
	/**
	 * 查找三层交换机
	 */
	@SuppressWarnings("unchecked")
	public List<SwitchLayer3> findLayer3Switchers() {
		List<SwitchLayer3> listOfSwitchNode = new ArrayList<SwitchLayer3>();
		List<?> result = remoteServer.getNmsService().findDevice(SwitchLayer3.class);
		if (result != null) {
			listOfSwitchNode = (List<SwitchLayer3>)result;
		}
		return listOfSwitchNode;
	}
	
	/**
	 * 根据前置机编号查找OLT
	 */
	@SuppressWarnings("unchecked")
	public List<OLTEntity> findOlts(String fepCode) {
		List<OLTEntity> listOfOlt = remoteServer.getNmsService().findDeviceByFep(OLTEntity.class, fepCode);
		
		if (listOfOlt == null) {
			listOfOlt = new ArrayList<OLTEntity>();
		}
		
		return listOfOlt;
	}
	
	/**
	 * 根据前置机编号查找ONU
	 */
	@SuppressWarnings("unchecked")
	public List<ONUEntity> findOnus(String fepCode) {
		List<ONUEntity> listOfOnu = remoteServer.getNmsService().findDeviceByFep(ONUEntity.class, fepCode);
		
		if (listOfOnu == null) {
			listOfOnu = new ArrayList<ONUEntity>();
		}
		
		LOG.info(String.format("ONU list.size is %s", listOfOnu.size()));
		
		return listOfOnu;
	}
		
	/**
	 * 根据前置机编号查找二层交换机
	 */
	@SuppressWarnings("unchecked")
	public List<SwitchNodeEntity> findSwitchers(String fepCode) {
		List<SwitchNodeEntity> listOfSwitchNode = new ArrayList<SwitchNodeEntity>();
		List<?> result = remoteServer.getNmsService().findDeviceByFep(SwitchNodeEntity.class, fepCode);
		if (result != null) {
			listOfSwitchNode = (List<SwitchNodeEntity>)result;
		}
		return listOfSwitchNode;
	}
	
	/**
	 * 根据前置机编号查找三层交换机
	 */
	@SuppressWarnings("unchecked")
	public List<SwitchLayer3> findLayer3Switchers(String fepCode) {
		List<SwitchLayer3> listOfSwitchNode = new ArrayList<SwitchLayer3>();
		List<?> result = remoteServer.getNmsService().findDeviceByFep(SwitchLayer3.class, fepCode);
		if (result != null) {
			listOfSwitchNode = (List<SwitchLayer3>)result;
		}
		return listOfSwitchNode;
	}
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private static final Logger LOG = LoggerFactory.getLogger(EquipmentRepository.class);
	public static final String ID = "equipmentRepository";
}