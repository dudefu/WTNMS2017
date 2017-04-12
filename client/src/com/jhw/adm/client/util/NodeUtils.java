package com.jhw.adm.client.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.EquipmentRepository;
import com.jhw.adm.client.model.carrier.CarrierCategory;
import com.jhw.adm.client.ui.ClientUI;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.CommentTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.RingEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.GPRSEntity;
import com.jhw.adm.server.entity.util.LogEntity;
import com.jhw.adm.server.entity.warning.WarningEntity;
import com.jhw.adm.server.servic.CommonServiceBeanRemote;

/**
 * ���˽ڵ㹤�ߣ����Ի�ȡ�ڵ��Guid���ı���Ϣ��
 */
public final class NodeUtils {

	/**
	 * ��ȡ�ڵ��GUID
	 */
	public static String getNodeGuid(NodeEntity nodeEntity) {
		String guid = StringUtils.EMPTY;
		
		if (nodeEntity instanceof FEPTopoNodeEntity) {
			guid = ((FEPTopoNodeEntity)nodeEntity).getGuid();
		} else if (nodeEntity instanceof CarrierTopNodeEntity) {
			guid = ((CarrierTopNodeEntity)nodeEntity).getGuid();
		} else if (nodeEntity instanceof SwitchTopoNodeEntity) {
			guid = ((SwitchTopoNodeEntity)nodeEntity).getGuid();
		} else if (nodeEntity instanceof GPRSTopoNodeEntity) {
			guid = ((GPRSTopoNodeEntity)nodeEntity).getGuid();
		} else if (nodeEntity instanceof RingEntity) {
			guid = ((RingEntity)nodeEntity).getRing_guid();
		} else if (nodeEntity instanceof SwitchTopoNodeLevel3) {
			guid = ((SwitchTopoNodeLevel3)nodeEntity).getGuid();
		} else if (nodeEntity instanceof EponTopoEntity) {
			guid = ((EponTopoEntity)nodeEntity).getGuid();
		} else if (nodeEntity instanceof ONUTopoNodeEntity) {
			guid = ((ONUTopoNodeEntity)nodeEntity).getGuid();
		} else if (nodeEntity instanceof Epon_S_TopNodeEntity) {
			guid = ((Epon_S_TopNodeEntity)nodeEntity).getGuid();
		} else if (nodeEntity instanceof SubNetTopoNodeEntity) {
			guid = ((SubNetTopoNodeEntity)nodeEntity).getGuid();
		} else if (nodeEntity instanceof VirtualNodeEntity) {
			guid = ((VirtualNodeEntity)nodeEntity).getGuid();
		} else if (nodeEntity instanceof CommentTopoNodeEntity){
			guid = ((CommentTopoNodeEntity)nodeEntity).getGuid();
		}
		
		return guid;
	}
	
	public static String getNodeText(NodeEntity nodeEntity) {
		Assert.notNull(nodeEntity);
		String text = StringUtils.EMPTY;
		
		if (nodeEntity instanceof FEPTopoNodeEntity) {
			text = getNodeText((FEPTopoNodeEntity)nodeEntity);
		} else if (nodeEntity instanceof CarrierTopNodeEntity) {
			text = getNodeText((CarrierTopNodeEntity)nodeEntity);
		} else if (nodeEntity instanceof SwitchTopoNodeEntity) {
			text = getNodeText((SwitchTopoNodeEntity)nodeEntity);
		} else if (nodeEntity instanceof GPRSTopoNodeEntity) {
			text = getNodeText((GPRSTopoNodeEntity)nodeEntity);
		} else if (nodeEntity instanceof SwitchTopoNodeLevel3) {
			text = getNodeText((SwitchTopoNodeLevel3)nodeEntity);
		} else if (nodeEntity instanceof EponTopoEntity) {
			text = getNodeText((EponTopoEntity)nodeEntity);
		} else if (nodeEntity instanceof ONUTopoNodeEntity) {
			text = getNodeText((ONUTopoNodeEntity)nodeEntity);
		} else if (nodeEntity instanceof Epon_S_TopNodeEntity) {
			text = getNodeText((Epon_S_TopNodeEntity)nodeEntity);
		} else if (nodeEntity instanceof SubNetTopoNodeEntity) {
			text = getNodeText((SubNetTopoNodeEntity)nodeEntity);
		} else if(nodeEntity instanceof VirtualNodeEntity){
			text = getNodeText((VirtualNodeEntity)nodeEntity);
		} else if(nodeEntity instanceof CommentTopoNodeEntity){
			text = getNodeText((CommentTopoNodeEntity)nodeEntity);
		}
		
		return text;
	}
	
	/**
	 * ��ʽ��LLDP��Ϣ
	 */
	public static String lldp2String(LLDPInofEntity lldp) {
		String text = "LLDP is null";
		
		if (lldp != null) {
			text = String.format("[%s][%s:%s.%s.%s] -> [%s:%s.%s.%s]",
					lldp.getId(),
					lldp.getLocalIP(), lldp.getLocalPortNo(), 
					lldp.getLocalPortType(), lldp.getLocalSlot(),
					lldp.getRemoteIP(), lldp.getRemotePortNo(),
					lldp.getRemotePortType(), lldp.getRemoteSlot());
		}
		return text;
	}
	
	/**
	 * ��ȡ�ز����ڵ���ı���Ϣ
	 */
	public static String getNodeText(CarrierTopNodeEntity carrierNode) {
		Assert.notNull(carrierNode);
		if(!StringUtils.isBlank(carrierNode.getName())){
			return carrierNode.getName();
		}
		String text = StringUtils.EMPTY;
		CarrierCategory carrierCategory = ClientUtils.getSpringBean(CarrierCategory.ID);
		
		String number = String.valueOf(carrierNode.getCarrierCode());
		String category = carrierCategory.get(carrierNode.getType()).getKey();
		text = number + " - " + category;
		return text;
	}
	
	/**
	 * ��ȡ�������ڵ���ı���Ϣ
	 */
	public static String getNodeText(SwitchTopoNodeEntity switcherNode) {
		Assert.notNull(switcherNode);
		if(!StringUtils.isBlank(switcherNode.getName())){
			return switcherNode.getName();
		}
		return switcherNode.getIpValue();
	}
	
	/**
	 * ��ȡ���㽻�����ڵ���ı���Ϣ
	 */
	public static String getNodeText(SwitchTopoNodeLevel3 layer3SwitcherNode) {
 		Assert.notNull(layer3SwitcherNode);
    	
		if(!StringUtils.isBlank(layer3SwitcherNode.getName())){
			return layer3SwitcherNode.getName();
		}
		
		return layer3SwitcherNode.getIpValue();
	}
	
	/**
	 * ��ȡGPRS�ڵ���ı���Ϣ
	 */
	public static String getNodeText(GPRSTopoNodeEntity gprsNode) {
		Assert.notNull(gprsNode);
		
		if(!StringUtils.isBlank(gprsNode.getName())){
			return gprsNode.getName();
		}
		
		String text = StringUtils.EMPTY;
		
		if(!StringUtils.isBlank(gprsNode.getGuid())){
			text = gprsNode.getUserId();
		}
    	
		return text;
	}
	
	/**
	 * ��ȡǰ�û��ڵ���ı���Ϣ
	 */
	public static String getNodeText(FEPTopoNodeEntity frontEndNode) {
		Assert.notNull(frontEndNode);
		
		if(!StringUtils.isBlank(frontEndNode.getName())){
			return frontEndNode.getName();
		}
		
		String text = StringUtils.EMPTY;
		
		if(StringUtils.isNotBlank(frontEndNode.getIpValue())){
			text = frontEndNode.getIpValue();
		}
		
		return text;
	}
	
	/**
	 * ��ȡOLT�ڵ���ı���Ϣ
	 */
	public static String getNodeText(EponTopoEntity oltNode) {
		Assert.notNull(oltNode, "oltNode must not be null");
		
		if(!StringUtils.isBlank(oltNode.getName())){
			return oltNode.getName();
		}
		
		return oltNode.getName();
	}
	
	/**
	 * ��ȡONU�ڵ���ı���Ϣ
	 */
	public static String getNodeText(ONUTopoNodeEntity onuNode) {
		Assert.notNull(onuNode, "onuNode must not be null");
    	
		if(!StringUtils.isBlank(onuNode.getName())){
			return onuNode.getName();
		}
		
		return onuNode.getName();
	}
	
	/**
	 * ��ȡ�ֹ����ڵ���ı���Ϣ
	 */
	public static String getNodeText(Epon_S_TopNodeEntity splitterNode) {
		Assert.notNull(splitterNode, "splitterNode must not be null");
		
		if(!StringUtils.isBlank(splitterNode.getName())){
			return splitterNode.getName();
		}
		
		return splitterNode.getName();
	}
	
	/**
	 * ��ȡ�����ڵ���ı���Ϣ
	 */
	public static String getNodeText(SubNetTopoNodeEntity subnetNode) {
		Assert.notNull(subnetNode, "subnetNode must not be null");
				
		return subnetNode.getName();
	}
	
	/**
	 * ��ȡ������Ԫ���ı���Ϣ
	 */
	public static String getNodeText(VirtualNodeEntity virtualNode) {
		Assert.notNull(virtualNode, "subnetNode must not be null");
		
		if(!StringUtils.isBlank(virtualNode.getName())){
			return virtualNode.getName();
		}
		
		return virtualNode.getIpValue();
	}
	
	/**
	 * ��ȡע��ͼ�ε��ı���Ϣ
	 */
	public static String getNodeText(CommentTopoNodeEntity commentNode) {
		Assert.notNull(commentNode, "subnetNode must not be null");
		
		return commentNode.getComment();
	}
	
	public static CarrierTopNodeEntity getNodeEntity(CarrierTopNodeEntity carrierNode) {
		Assert.notNull(carrierNode, "CarrierTopNodeEntity must not be null");
		
		if (carrierNode.getNodeEntity() == null || carrierNode.getNodeEntity().getId() != null) {
			CarrierEntity carrierEntity = getCommonService().getCarrierByCode(carrierNode.getCarrierCode());
			if (null == carrierEntity) {
				LOG.error(String.format("�޷��ҵ��ز���[%s]", carrierNode.getCarrierCode()));
				carrierEntity = new CarrierEntity();
			}
			carrierNode.setNodeEntity(carrierEntity);
		}
		
		return carrierNode;
	}
	
	public static SwitchTopoNodeEntity getNodeEntity(SwitchTopoNodeEntity switcherNode) {
		Assert.notNull(switcherNode, "SwitchTopoNodeEntity must not be null");
		
		if (switcherNode.getNodeEntity() == null || switcherNode.getNodeEntity().getId() != null) {
			SwitchNodeEntity switchNodeEntity = getCommonService().getSwitchByIp(switcherNode.getIpValue());
			
			if (null == switchNodeEntity) {
				LOG.error(String.format("�޷��ҵ�������[%s]", switcherNode.getIpValue()));
				switchNodeEntity = new SwitchNodeEntity();
			}
			switcherNode.setNodeEntity(switchNodeEntity);
		}
		
		return switcherNode;
	}
	
	public static GPRSTopoNodeEntity getNodeEntity(GPRSTopoNodeEntity gprsNode){
		Assert.notNull(gprsNode, "GPRSTopoNodeEntity must not be null");
		
		if (gprsNode.getEntity() == null || gprsNode.getEntity().getId() != null) {
			GPRSEntity gprsEntity = getCommonService().getGPRSEntityByUserId(gprsNode.getUserId());
			if (null == gprsEntity) {
				LOG.error(String.format("�޷��ҵ�GPRS[%s]", gprsNode.getUserId()));
				gprsEntity = new GPRSEntity();
			}
			gprsNode.setEntity(gprsEntity);
		}
		
		return gprsNode;
	}
	
	public static EponTopoEntity getNodeEntity(EponTopoEntity oltNode){
		Assert.notNull(oltNode, "EponTopoEntity must not be null");
		
		if (oltNode.getOltEntity() == null || oltNode.getOltEntity().getId() != null) {
			OLTEntity oltEntity = getCommonService().getOLTByIpValue(oltNode.getIpValue());
			if (null == oltEntity) {
				LOG.error(String.format("�޷��ҵ�OLT[%s]", oltNode.getIpValue()));
				oltEntity = new OLTEntity();
			}
			oltNode.setOltEntity(oltEntity);
		}
		
		return oltNode;
	}
	
	public static ONUTopoNodeEntity getNodeEntity(ONUTopoNodeEntity onuNode){
		Assert.notNull(onuNode, "ONUTopoNodeEntity must not be null");
		
		if (onuNode.getOnuEntity() == null || onuNode.getOnuEntity().getId() != null) {
			ONUEntity onuEntity = getCommonService().getOnuByMacValue(onuNode.getMacValue());
			if (null == onuEntity) {
				LOG.error(String.format("�޷��ҵ�ONU[%s]", onuNode.getMacValue()));
				onuEntity = new ONUEntity();
			}
			onuNode.setOnuEntity(onuEntity);
		}
		
		return onuNode;
	}
	
	public static FEPTopoNodeEntity getNodeEntity(FEPTopoNodeEntity fepNode){
		Assert.notNull(fepNode, "FEPTopoNodeEntity must not be null");
		
		if (fepNode.getFepEntity() == null || fepNode.getFepEntity().getId() != null) {
			FEPEntity fepEntity = getCommonService().getFEPEntityByIP(fepNode.getIpValue());
			if (null == fepEntity) {
				LOG.error(String.format("�޷��ҵ�ǰ�û�[%s]", fepNode.getIpValue()));
				fepEntity = new FEPEntity();
			}
			fepNode.setFepEntity(fepEntity);
		}
		
		return fepNode;
	}
	
	public static SwitchTopoNodeLevel3 getNodeEntity(SwitchTopoNodeLevel3 switchLayer3Node){
		Assert.notNull(switchLayer3Node, "SwitchTopoNodeLevel3 must not be null");
		
		if(switchLayer3Node.getSwitchLayer3() == null || switchLayer3Node.getSwitchLayer3().getId() != null){
			SwitchLayer3 switchLayer3 = getCommonService().getSwitcher3ByIP(switchLayer3Node.getIpValue());
			if(null == switchLayer3){
				LOG.error(String.format("�޷��ҵ����㽻����[%s]", switchLayer3Node.getIpValue()));
				switchLayer3 = new SwitchLayer3();
			}
			switchLayer3Node.setSwitchLayer3(switchLayer3);
		}
		
		return switchLayer3Node;
	}
	
	public static List<NodeEntity> sortNodeEntity(List<NodeEntity> nodeEntityList) {
		
		if(null == nodeEntityList || nodeEntityList.size() == 0){
			return new ArrayList<NodeEntity>();
		}
		
		Collections.sort(nodeEntityList, new IPComparator<NodeEntity>());
		
		return nodeEntityList;
	}
	
	public static List<NodeEntity> filterNodeEntityByUser(List<NodeEntity> nodeList){
		
		int currentUserCode = getClientModel().getCurrentUser().getRole().getRoleCode();
		UserEntity currentUserEntity = getClientModel().getCurrentUser();
		
		List<NodeEntity> nodeEntityList = new ArrayList<NodeEntity>();
		List<NodeEntity> switchEntityList = new ArrayList<NodeEntity>();
		
		if(Constants.ADMINCODE == currentUserCode){
			//��ǰ�û�Ϊadminʱ����Ҫ����
			for(NodeEntity nodeEntity : nodeList){
				if((nodeEntity instanceof SwitchTopoNodeEntity) || (nodeEntity instanceof SwitchTopoNodeLevel3)){
					switchEntityList.add(nodeEntity);
				}else{
					nodeEntityList.add(nodeEntity);
				}
			}
		}else{
			for(NodeEntity nodeEntity : nodeList){

				if (nodeEntity instanceof SwitchTopoNodeEntity) {
					SwitchTopoNodeEntity switchTopoNodeEntity = (SwitchTopoNodeEntity) nodeEntity;
					Set<UserEntity> userEntities = switchTopoNodeEntity.getUsers();
					if (null == userEntities || userEntities.size() == 0) {
					} else {
						for (UserEntity userEntity : userEntities) {
							if (ObjectUtils.equals(currentUserEntity.getId(),userEntity.getId())) {
								switchEntityList.add(nodeEntity);
								break;
							}
						}
					}
				} else if (nodeEntity instanceof SwitchTopoNodeLevel3) {
					SwitchTopoNodeLevel3 switchTopoNodeLevel3 = (SwitchTopoNodeLevel3) nodeEntity;
					Set<UserEntity> userEntities = switchTopoNodeLevel3.getUsers();
					if (null == userEntities || userEntities.size() == 0) {
					} else {
						for (UserEntity userEntity : userEntities) {
							if (ObjectUtils.equals(currentUserEntity.getId(),userEntity.getId())) {
								switchEntityList.add(nodeEntity);
								break;
							}
						}
					}
				} else if(nodeEntity instanceof VirtualNodeEntity){
					VirtualNodeEntity virtualNodeEntity = (VirtualNodeEntity)nodeEntity;
					Set<UserEntity> userEntities = virtualNodeEntity.getUsers();
					if (null == userEntities || userEntities.size() == 0) {
					} else {
						for (UserEntity userEntity : userEntities) {
							if (ObjectUtils.equals(currentUserEntity.getId(),userEntity.getId())) {
								switchEntityList.add(nodeEntity);
								break;
							}
						}
					}
				} else {
					nodeEntityList.add(nodeEntity);
				}
			}
		}
		switchEntityList = sortNodeEntity(switchEntityList);
		nodeEntityList.addAll(switchEntityList);
		
		return nodeEntityList;
	}
	
	public static boolean filterWarningInfoByUser(String warningFrom,int deviceType,int warningLevel){
		
		boolean isShowWarningInfo = false;
		
		int loginUserCode = getClientModel().getCurrentUser().getRole().getRoleCode();
		UserEntity loginUserEntity = getClientModel().getCurrentUser();
		String level = getClientModel().getCurrentUser().getCareLevel();
		if(Constants.ADMINCODE == loginUserCode) {
			isShowWarningInfo = true;
		}else {
			Set<UserEntity> management = getManagement(warningFrom, deviceType);
			if(null == management){
				isShowWarningInfo = false;
			}else {
				if(StringUtils.isBlank(level)){
					isShowWarningInfo = false;
				}else{
					for(UserEntity userEntity : management){
						if(ObjectUtils.equals(loginUserEntity.getId(), userEntity.getId())){
							String currentLevel = Integer.toString(warningLevel);
							if(level.contains(currentLevel)){
								isShowWarningInfo = true;
								break;
							}
						}
					}
				}
			}
		}
		return isShowWarningInfo;
	}
	
	private static Set<UserEntity> getManagement(String warningFrom,int deviceType){
		Set<UserEntity> userEntity = null;
		
		if(Constants.DEV_SWITCHER2 == deviceType){
			SwitchTopoNodeEntity switchTopoNodeEntity = (SwitchTopoNodeEntity) getCommonService()
					.findSwitchTopoNodeByIp(warningFrom);
			if(null == switchTopoNodeEntity){
				VirtualNodeEntity virtualNodeEntity = (VirtualNodeEntity) getCommonService()
						.findVirtualNodeByIp(warningFrom);
				if (null != virtualNodeEntity) {
					userEntity = virtualNodeEntity.getUsers();
				}
			}else{
				userEntity = switchTopoNodeEntity.getUsers();
			}
		}else if(Constants.DEV_SWITCHER3 == deviceType){
			SwitchTopoNodeLevel3 switchTopoNodeLevel3 = (SwitchTopoNodeLevel3) getCommonService()
					.findSwitchTopoNodeLevel3ByIp(warningFrom);
			if(null == switchTopoNodeLevel3){
				VirtualNodeEntity virtualNodeEntity = (VirtualNodeEntity) getCommonService()
						.findVirtualNodeByIp(warningFrom);
				if (null != virtualNodeEntity) {
					userEntity = virtualNodeEntity.getUsers();
				}
			}else{
				userEntity = switchTopoNodeLevel3.getUsers();
			}
		}else if(Constants.DEV_OLT == deviceType){
			EponTopoEntity eponTopoEntity = (EponTopoEntity) getCommonService()
					.findOLTTopoNodeByIp(warningFrom);
			if(null == eponTopoEntity){
				VirtualNodeEntity virtualNodeEntity = (VirtualNodeEntity) getCommonService()
						.findVirtualNodeByIp(warningFrom);
				if (null != virtualNodeEntity) {
					userEntity = virtualNodeEntity.getUsers();
				}
			}else{
				userEntity = eponTopoEntity.getUsers();
			}
		}else if(Constants.DEV_ONU == deviceType){
			ONUTopoNodeEntity onuTopoNodeEntity = (ONUTopoNodeEntity) getCommonService()
					.findOnuTopoNodeByMac(warningFrom);
			if(null == onuTopoNodeEntity){
				VirtualNodeEntity virtualNodeEntity = (VirtualNodeEntity) getCommonService()
						.findVirtualNodeByIp(warningFrom);
				if (null != virtualNodeEntity) {
					userEntity = virtualNodeEntity.getUsers();
				}
			}else{
				userEntity = onuTopoNodeEntity.getUsers();
			}
		}else {//For virtual network element (ping)
			VirtualNodeEntity virtualNodeEntity = (VirtualNodeEntity) getCommonService()
					.findVirtualNodeByIp(warningFrom);
			if(null != virtualNodeEntity) {
				userEntity = virtualNodeEntity.getUsers();
			}
		}
		
		return userEntity;
	}
	
	public static List<WarningEntity> filterWarningEntity(List<WarningEntity> warningEntityList){
		List<WarningEntity> resultList = new ArrayList<WarningEntity>();
		if(null != warningEntityList){
			for (WarningEntity warningEntity : warningEntityList) {
				int event = warningEntity.getWarningEvent();
				if((Constants.FEP_CONNECT == event) || (Constants.FEP_DISCONNECT == event)){
					resultList.add(warningEntity);
				}else{
					String warningFrom = warningEntity.getIpValue();
					if (warningEntity.getDeviceType() == Constants.DEV_ONU){
						warningFrom = warningEntity.getWarnOnuMac();
					}
					if (filterWarningInfoByUser(warningFrom,
							warningEntity.getDeviceType(), warningEntity.getWarningLevel())) {
						resultList.add(warningEntity);
					}
				}
			}
		}
		return resultList;
	}
	
	/**
	 * ��λ�豸
	 * @param selectedNode
	 */
	public static void fixedPositionEquipment(NodeEntity selectedNode){
		String parentNode = selectedNode.getParentNode();
		
		//ѡ�нڵ�
		getEquipmentModel().setLastFixedPosition(selectedNode);
		
		//��������
		if(StringUtils.isBlank(parentNode)){//����λ�豸��������Ϊ���������
			getEquipmentModel().requireRefresh();
		}else{//����λ�豸��������Ϊĳ����������
			getEquipmentModel().requireSubnet(parentNode);
		}

		//��С�����д���
		ClientUI.getDesktopWindow().minimizeAllWindow();
	}
	
	/**
	 * �ֶ��رո澯
	 * @param nodeEntityList
	 */
	public static void closeEquipmentAlarmManual(List<Object> nodeEntityList) {
		if (null == nodeEntityList) {
			return;
		}
		for (Object nodeEntity : nodeEntityList) {
			if(nodeEntity instanceof NodeEntity){
				updateNodeStatus((NodeEntity)nodeEntity);
				updateLinkStatus((NodeEntity)nodeEntity);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void updateLinkStatus(NodeEntity nodeEntity) {
		String nodeGuid = NodeUtils.getNodeGuid(nodeEntity);
		Object[] parmas = {nodeGuid,nodeGuid};
		String where = " where entity.node1Guid=? or entity.node2Guid=?";
		List<LinkEntity> linkEntityList = (List<LinkEntity>) getCommonService()
				.findAll(LinkEntity.class, where, parmas);
		if(null == linkEntityList){
			return;
		}
		for(LinkEntity linkEntity : linkEntityList){
			linkEntity.setStatus(Constants.NORMAL);
			getEquipmentRepository().updateNode(linkEntity);
			getEquipmentModel().fireEquipmentUpdated(linkEntity);
		}
	}

	private static void updateNodeStatus(NodeEntity nodeEntity) {
		nodeEntity.setStatus(Constants.NORMAL);
		getEquipmentRepository().updateNode(nodeEntity);
		getEquipmentModel().fireEquipmentUpdated(nodeEntity);
	}
	
	public static void saveDeleteLog(String description){
		if(StringUtils.isBlank(description)){
			description = "ɾ���ڵ�";
		}
		
		LogEntity logEntity = new LogEntity();
		logEntity.setUserName(getClientModel().getCurrentUser().getUserName());
		logEntity.setClientIp(getClientModel().getLocalAddress());
		logEntity.setDoTime(new Date());
		logEntity.setContents(description);
		try {
			getCommonService().saveEntity(logEntity);
		} catch (Exception ex) {
			LOG.error("����ɾ���ڵ㡢������־����", ex);
		}
	}
	
	private static EquipmentModel getEquipmentModel(){
		EquipmentModel equipmentModel = ClientUtils.getSpringBean(EquipmentModel.ID);
		return equipmentModel;
	}
	
	private static EquipmentRepository getEquipmentRepository(){
		EquipmentRepository equipmentRepository = ClientUtils.getSpringBean(EquipmentRepository.ID);
		return equipmentRepository;
	}

	private static CommonServiceBeanRemote getCommonService(){
		RemoteServer remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
		return remoteServer.getService();
	}
	
	private static ClientModel getClientModel(){
		ClientModel clientModel = ClientUtils.getSpringBean(ClientModel.ID);
		return clientModel;
	}

	private static final Logger LOG = LoggerFactory.getLogger(NodeUtils.class);
}