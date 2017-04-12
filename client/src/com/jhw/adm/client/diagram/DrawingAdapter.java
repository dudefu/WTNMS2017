package com.jhw.adm.client.diagram;

import static com.jhw.adm.client.util.NodeUtils.getNodeGuid;
import static com.jhw.adm.client.util.NodeUtils.getNodeText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.jhw.adm.client.core.ConcurrentLinkedHashMap;
import com.jhw.adm.client.draw.CircleLineFigure;
import com.jhw.adm.client.draw.CommentEdit;
import com.jhw.adm.client.draw.EquipmentEdit;
import com.jhw.adm.client.draw.LabeledLinkEdit;
import com.jhw.adm.client.draw.LabeledLinkFigure;
import com.jhw.adm.client.draw.NodeEdit;
import com.jhw.adm.client.draw.NodeFigure;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
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

/**
 * 图画适配器
 */
public class DrawingAdapter {
	
	public DrawingAdapter(final Drawing drawing) {
		this.drawing = drawing;
		figureGuidIndexes = new ConcurrentLinkedHashMap<String, NodeFigure>();
		figureAddressIndexes = new ConcurrentLinkedHashMap<String, NodeFigure>();
		figureNameIndexes = new ConcurrentLinkedHashMap<String, NodeFigure>();
		linkAddressIndexes = new ConcurrentLinkedHashMap<LinkIndex, LabeledLinkFigure>();
		linkFigureIdIndexes = new ConcurrentLinkedHashMap<Long, LabeledLinkFigure>();
		ringNoIndexes = new ConcurrentLinkedHashMap<Integer, RingEntity>();
	}
	
	private void cleanIndexes() {
		figureGuidIndexes.clear();
		figureAddressIndexes.clear();
		figureNameIndexes.clear();
		linkAddressIndexes.clear();
		linkFigureIdIndexes.clear();
		ringNoIndexes.clear();
		figureGuidIndexes = new ConcurrentLinkedHashMap<String, NodeFigure>();
		figureAddressIndexes = new ConcurrentLinkedHashMap<String, NodeFigure>();
		figureNameIndexes = new ConcurrentLinkedHashMap<String, NodeFigure>();
		linkAddressIndexes = new ConcurrentLinkedHashMap<LinkIndex, LabeledLinkFigure>();
		linkFigureIdIndexes = new ConcurrentLinkedHashMap<Long, LabeledLinkFigure>();
		ringNoIndexes = new ConcurrentLinkedHashMap<Integer, RingEntity>();
	}
	
	private boolean canAccess(NodeEntity nodeEntity) {
		boolean result = false;
		ClientModel clientModel = ClientUtils.getSpringBean(ClientModel.class, ClientModel.ID);
		String currentUsername = clientModel.getCurrentUser().getUserName();
		int roleCode = clientModel.getCurrentUser().getRole().getRoleCode();
		
		if (roleCode == Constants.ADMINCODE) {
			LOG.debug("当前用户为超级管理员");
			result = true;
		} else {
			if (nodeEntity instanceof SwitchTopoNodeEntity) {
				SwitchTopoNodeEntity switcherNode = (SwitchTopoNodeEntity)nodeEntity;
				SwitchNodeEntity switchNodeEntity = NodeUtils.getNodeEntity(switcherNode).getNodeEntity();
				String userNames = StringUtils.EMPTY;
				if(switchNodeEntity.getBaseConfig() != null){
					userNames = switchNodeEntity.getBaseConfig().getUserNames();
				}
				String[] splitNames = StringUtils.split(userNames, ";");
				
				if (splitNames == null) {
					LOG.warn(
							String.format("交换机[%s]没有配置管理员", 
								switcherNode.getNodeEntity().getBaseConfig().getIpValue()));
				} else {
					for (String username : splitNames) {
						if (currentUsername.equals(username)) {
							result = true;
							break;
						}
					}
				}
			} else if (nodeEntity instanceof SwitchTopoNodeLevel3) {
				SwitchTopoNodeLevel3 layer3SwitcherNode = (SwitchTopoNodeLevel3)nodeEntity;
				SwitchLayer3 switchLayer3 = NodeUtils.getNodeEntity(layer3SwitcherNode).getSwitchLayer3();
				
				String userNames = StringUtils.EMPTY;
				if(switchLayer3.getManagerEntity() != null){
					userNames = switchLayer3.getManagerEntity().getName();
				}
				String[] splitNames = StringUtils.split(userNames, ",");
				
				if (splitNames == null) {
					LOG.warn(
							String.format("交换机[%s]没有配置管理员", 
									switchLayer3.getIpValue()));
				} else {
					for (String username : splitNames) {
						if (currentUsername.equals(username)) {
							result = true;
							break;
						}
					}
				}
			} else {
				result = true;
			}
		}
		
		return true;
	}
	
	public void createFigureIndex(final Figure figure) {
		final FigureActionBuilder figureActionBuilder = ClientUtils.getSpringBean(FigureActionBuilder.ID);
		if (figure instanceof NodeFigure) {
			NodeFigure equipmentFigure = (NodeFigure)figure;
			
			Object figureModel = equipmentFigure.getEdit().getModel();
			if (figureModel instanceof NodeEntity) {
				/* 两层交换机 */
				if (figureModel instanceof SwitchTopoNodeEntity) {					
					SwitchTopoNodeEntity switcherNode = (SwitchTopoNodeEntity)figureModel;
					String ipValue = switcherNode.getIpValue();
					figureGuidIndexes.put(getNodeGuid(switcherNode), equipmentFigure);
					figureAddressIndexes.put(ipValue, equipmentFigure);
					figureNameIndexes.put(getNodeText(switcherNode), equipmentFigure);
					figureActionBuilder.configureSwitcherFigureDoubleClickActions(equipmentFigure);
					figureActionBuilder.configureSwitcherFigurePopupActions(equipmentFigure);
				}
				/* 三层交换机 */
				if (figureModel instanceof SwitchTopoNodeLevel3) {
					SwitchTopoNodeLevel3 level3SwitcherNode = (SwitchTopoNodeLevel3)figureModel;
					String ipValue = level3SwitcherNode.getIpValue();
					figureGuidIndexes.put(getNodeGuid(level3SwitcherNode), equipmentFigure);
					figureAddressIndexes.put(ipValue, equipmentFigure);
					figureNameIndexes.put(getNodeText(level3SwitcherNode), equipmentFigure);
					figureActionBuilder.configureLayer3FigurePopupActions(equipmentFigure);
					figureActionBuilder.configureLayer3FigureDoubleClickActions(equipmentFigure);
				}
				/* 载波机 */
				if (figureModel instanceof CarrierTopNodeEntity) {
					CarrierTopNodeEntity carrierNode = (CarrierTopNodeEntity)figureModel;
					int code = carrierNode.getCarrierCode();
					figureAddressIndexes.put(Integer.toString(code), equipmentFigure);
					figureGuidIndexes.put(getNodeGuid(carrierNode), equipmentFigure);
					figureNameIndexes.put(getNodeText(carrierNode), equipmentFigure);
					figureActionBuilder.configureCarrierFigureDoubleClickActions(equipmentFigure);
					figureActionBuilder.configureCarrierFigurePopupActions(equipmentFigure);
				}	
				/* 前置机 */
				if (figureModel instanceof FEPTopoNodeEntity) {
					FEPTopoNodeEntity frontEndNode = (FEPTopoNodeEntity)figureModel;					
					figureGuidIndexes.put(getNodeGuid(frontEndNode), equipmentFigure);
					figureAddressIndexes.put(frontEndNode.getIpValue(), equipmentFigure);
					figureNameIndexes.put(getNodeText(frontEndNode), equipmentFigure);
					figureActionBuilder.configureFrontEndDoubleClickActions(equipmentFigure);
					figureActionBuilder.configureFrontEndPopupActions(equipmentFigure);
				}
				/* GPRS */
				if (figureModel instanceof GPRSTopoNodeEntity) {
					GPRSTopoNodeEntity gprsNode = (GPRSTopoNodeEntity)figureModel;
					String userId = gprsNode.getUserId();
					figureAddressIndexes.put(userId, equipmentFigure);
					figureGuidIndexes.put(getNodeGuid(gprsNode), equipmentFigure);
					figureNameIndexes.put(getNodeText(gprsNode), equipmentFigure);
					figureActionBuilder.configureGPRSDoubleClickActions(equipmentFigure);
					figureActionBuilder.configureGPRSFigurePopupActions(equipmentFigure);
				}
				/* OLT */
				if (figureModel instanceof EponTopoEntity) {
					EponTopoEntity oltNode = (EponTopoEntity)figureModel;
					figureGuidIndexes.put(getNodeGuid(oltNode), equipmentFigure);
					figureAddressIndexes.put(oltNode.getIpValue(), equipmentFigure);
					figureActionBuilder.configureOltFigurePopupActions(equipmentFigure);
					figureActionBuilder.configureOltFigureDoubleClickActions(equipmentFigure);
				}
				/* ONU */
				if (figureModel instanceof ONUTopoNodeEntity) {
					ONUTopoNodeEntity onuNode = (ONUTopoNodeEntity)figureModel;
					figureGuidIndexes.put(getNodeGuid(onuNode), equipmentFigure);
					figureAddressIndexes.put(onuNode.getMacValue(), equipmentFigure);
					figureActionBuilder.configureOnuFigurePopupActions(equipmentFigure);
					figureActionBuilder.configureOnuFigureDoubleClickActions(equipmentFigure);
				}
				/* 分光器 */
				if (figureModel instanceof Epon_S_TopNodeEntity) {
					Epon_S_TopNodeEntity splitterNode = (Epon_S_TopNodeEntity)figureModel;
					figureGuidIndexes.put(getNodeGuid(splitterNode), equipmentFigure);
					figureActionBuilder.configureSplitterFigurePopupActions(equipmentFigure);
					figureActionBuilder.configureSplitterFigureDoubleClickActions(equipmentFigure);
				}
				/* 子网 */
				if (figureModel instanceof SubNetTopoNodeEntity) {
					SubNetTopoNodeEntity subnetNode = (SubNetTopoNodeEntity)figureModel;
					figureGuidIndexes.put(getNodeGuid(subnetNode), equipmentFigure);
					figureNameIndexes.put(getNodeText(subnetNode), equipmentFigure);
					figureActionBuilder.configureSubnetFigureDoubleClickActions(equipmentFigure);
					figureActionBuilder.configureSubnetFigurePopupActions(equipmentFigure);
				}
				/* 虚拟网元 */
				if (figureModel instanceof VirtualNodeEntity) {
					VirtualNodeEntity virtualNode = (VirtualNodeEntity)figureModel;
					figureGuidIndexes.put(getNodeGuid(virtualNode), equipmentFigure);
					figureNameIndexes.put(getNodeText(virtualNode), equipmentFigure);
					figureActionBuilder.configureVirtualElementFigurePopupActions(equipmentFigure);
					figureActionBuilder.configureVirtualElementFigureDoubleClickActions(equipmentFigure);
				}
				/* 注释图形*/
				if(figureModel instanceof CommentTopoNodeEntity){
					CommentTopoNodeEntity commentNode = (CommentTopoNodeEntity)figureModel;
					figureGuidIndexes.put(getNodeGuid(commentNode), equipmentFigure);
					figureNameIndexes.put(getNodeText(commentNode), equipmentFigure);
					figureActionBuilder.configureCommentFigurePopupActions(equipmentFigure);
					figureActionBuilder.configureCommentFigureDoubleClickActions(equipmentFigure);
				}
				/* 环 */
				if (figureModel instanceof RingEntity) {
					RingEntity ringNode = (RingEntity)figureModel;
					ringNoIndexes.put(ringNode.getRingNo(), ringNode);
				}
			}
		}

		if (figure instanceof LabeledLinkFigure) {
			LabeledLinkFigure linkFigure = (LabeledLinkFigure)figure;
			Object figureModel = linkFigure.getEdit().getModel();
			if (figureModel instanceof LinkEntity) {
				LinkEntity linkEntity = (LinkEntity)figureModel;
				
				if (linkEntity.getLldp() != null &&
					StringUtils.isNotBlank(linkEntity.getLldp().getLocalIP()) &&
					StringUtils.isNotBlank(linkEntity.getLldp().getRemoteIP())) {
					linkAddressIndexes.put(
							new LinkIndex(linkEntity.getLldp().getLocalIP(), linkEntity.getLldp().getRemoteIP()), 
							linkFigure);
				}				
				linkFigureIdIndexes.put(linkEntity.getId(), linkFigure);
				figureGuidIndexes.put(linkEntity.getGuid(), linkFigure);
				figureActionBuilder.configureLinkFigureDoubleClickAction(linkFigure);
			}
		}
	}
	
	/**
	 * 添加节点图形到图画里面
	 */
	public void addNode(final List<NodeEntity> listOfNode, final List<LinkEntity> listOfLink) {
		Assert.notNull(listOfNode, "listOfNode must not be null");
		Assert.notNull(listOfLink, "listOfLink must not be null");
		cleanIndexes();
		
		DiagramNodeEditFactory nodeEditFactory = new DiagramNodeEditFactory();
		int count = listOfNode.size();
		List<Figure> addingNode = new ArrayList<Figure>();
		for (int index = 0; index < count; index++) {
			NodeEntity nodeEntity = listOfNode.get(index);
			NodeEdit<? extends NodeFigure> nodeEdit = nodeEditFactory.createNodeEdit(nodeEntity);
			if (nodeEdit == null) {
				LOG.error(String.format("无法找到该节点{%s}类型", nodeEntity.getId()));
				continue;
			}
			NodeFigure nodeFigure = nodeEdit.restoreFigure(nodeEntity);
			
			if (nodeFigure instanceof CircleLineFigure) {
				//
			} else {
				if (canAccess(nodeEntity)) {
					addingNode.add(nodeFigure);
				}
			}
			createFigureIndex(nodeFigure);
        }
		
		/* 连线 */
		for (LinkEntity linkEntity : listOfLink) {
			
			if (StringUtils.isBlank(linkEntity.getGuid())) {
				LOG.error(String.format("linkEntity[%s].guid is null", linkEntity.getId()));
				continue;
			}
			NodeEntity startNode = linkEntity.getNode1();
			NodeEntity endNode = linkEntity.getNode2();
						
			String startGuid = getNodeGuid(startNode);
			String endGuid = getNodeGuid(endNode);
						
			if (StringUtils.isBlank(startGuid) || StringUtils.isBlank(endGuid)) {
				LOG.error(String.format("Blank GUID -> start[%s].guid: %s; end[%s].guid: %s", 
						startNode.getName(), startGuid, 
						endNode.getName(), endGuid));
				continue;
			}
						
			NodeFigure startFigure = figureGuidIndexes.get(startGuid);
			NodeFigure endFigure = figureGuidIndexes.get(endGuid);
			
			if (startFigure == null && startNode.getParentNode() != null) {
				startFigure = figureGuidIndexes.get(startNode.getParentNode());
			}
			if (endFigure == null && endNode.getParentNode() != null) {
				endFigure = figureGuidIndexes.get(endNode.getParentNode());
			}
			if (startFigure == null || endFigure == null) {
				LOG.warn(String.format("Null figure -> start[%s].guid: %s; end[%s].guid: %s", 
						startNode, startGuid, 
						endNode, endGuid));
				continue;
			}
			
			final LabeledLinkEdit linkEdit = new SmartLinkEdit();
			final LabeledLinkFigure linkFigure = linkEdit.restoreFigure(linkEntity);
			Connector startConnector = startFigure.findConnector(null, null);
			Connector endConnector = endFigure.findConnector(null, null);
			
			if (startConnector == null || endConnector == null) {
				LOG.error(String.format("Null connector -> start[%s].guid: %s; end[%s].guid: %s", 
						startNode, startGuid,
						endNode, endGuid));
				continue;
			}
			
			linkFigure.setStartConnector(startConnector);
			linkFigure.setEndConnector(endConnector);
			
			if (canAccess(startNode) && canAccess(endNode)) {
				addingNode.add(linkFigure);
			}
			createFigureIndex(linkFigure);
		}
		
		drawing.addAll(addingNode);
	}
	
	public void updateNode(final Object updatedNode) {		
		if (updatedNode instanceof NodeEntity) {
			final NodeEntity nodeEntity = (NodeEntity)updatedNode;
			final String guid = getNodeGuid(nodeEntity);
			final Figure figure = findNodeByGuid(guid);
			if (figure instanceof NodeFigure) {
				NodeFigure equipment = (NodeFigure)figure;
				if (equipment.getEdit() instanceof EquipmentEdit) {
					EquipmentEdit equipmentEdit = (EquipmentEdit)equipment.getEdit();
					equipmentEdit.setModel(updatedNode);
				}else if(equipment.getEdit() instanceof CommentEdit){
					CommentEdit equipmentEdit = (CommentEdit)equipment.getEdit();
					equipmentEdit.setModel(updatedNode);
				}
				
				equipment.getEdit().updateAttributes();
			}
		}
		// 前置机修改IP地址后，更新索引
		if (updatedNode instanceof FEPTopoNodeEntity) {
			FEPTopoNodeEntity fepNode = (FEPTopoNodeEntity)updatedNode;
			String address = fepNode.getIpValue();
			final String guid = getNodeGuid(fepNode);
			NodeFigure nodeFigure = findNodeByAddress(address);
			
			if (nodeFigure == null) {
				final Figure figure = findNodeByGuid(guid);
				figureAddressIndexes.put(address, (NodeFigure)figure);
			}
		}
		if (updatedNode instanceof LinkEntity) {
			LinkEntity linkNode = (LinkEntity)updatedNode;
			NodeFigure linkFigure = findNodeByGuid(linkNode.getGuid());
			if ((linkFigure != null) && (linkFigure.getEdit() instanceof SmartLinkEdit)) {
				SmartLinkEdit smartLinkEdit = (SmartLinkEdit)linkFigure.getEdit();
				smartLinkEdit.setModel(linkNode);
				smartLinkEdit.updateAttributes();
			}
		}
	}

	/**
	 * 查找交换机所有的连线
	 */
	public List<LinkEntity> findSwitcherLinks(final NodeEntity switcherNode) {
		
		List<LinkEntity> listOfSwitcherLink = new ArrayList<LinkEntity>();
		
		for (LabeledLinkFigure linkFigure : linkFigureIdIndexes.values()) {
			LinkEntity linkNode = (LinkEntity)linkFigure.getEdit().getModel();
			
			NodeEntity startNode = linkNode.getNode1();
			NodeEntity endNode = linkNode.getNode2();
						
			String startGuid = getNodeGuid(startNode);
			String endGuid = getNodeGuid(endNode);
			String switcherGuid = getNodeGuid(switcherNode);
						
			if (StringUtils.isBlank(startGuid) || 
				StringUtils.isBlank(endGuid) || 
				StringUtils.isBlank(switcherGuid)) {
				continue;
			}
			
			if (switcherGuid.equals(startGuid) ||
				switcherGuid.equals(endGuid)) {
				listOfSwitcherLink.add(linkNode);
			}
		}
		
		return listOfSwitcherLink;
	}


	/**
	 * 判断交换机的连线是否全部断开
	 */
	public boolean isSwticherShutdown(final NodeEntity switcherNode) {
		boolean result = true;
		List<LinkEntity> listOfSwitcherLink = findSwitcherLinks(switcherNode);
		for (LinkEntity linkNode : listOfSwitcherLink) {
			if (linkNode.getStatus() != Constants.L_UNCONNECT) {
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * 查找环里面阻塞的连线
	 */
	public LinkEntity findBlockingLink(final int ringNo) {
		LinkEntity blockLinkNode = null;
		if (ringNoIndexes.containsKey(ringNo)) {
			RingEntity ringNode = ringNoIndexes.get(ringNo);
			String address = ringNode.getIp_Value();
			int portNo = ringNode.getPortNo();
			
			for (LabeledLinkFigure linkFigure : linkFigureIdIndexes.values()) {
				LinkEntity linkNode = (LinkEntity)linkFigure.getEdit().getModel();
				if (linkNode.getRingID() == ringNo &&
					(linkNode.getLldp().getLocalIP().equals(address) &&
					 linkNode.getLldp().getLocalPortNo() == portNo)) {
					blockLinkNode = linkNode;
					break;
				}
				if (linkNode.getRingID() == ringNo &&
					(linkNode.getLldp().getRemoteIP().equals(address) &&
					 linkNode.getLldp().getRemotePortNo() == portNo)) {
					blockLinkNode = linkNode;
					break;
				}
			}
		}
		
		return blockLinkNode;
	}
	
	public LabeledLinkFigure findLinkById(final Long linkId) {
		LabeledLinkFigure linkFigure = null;
		if (linkFigureIdIndexes.containsKey(linkId)) {
			linkFigure = linkFigureIdIndexes.get(linkId);
		}
		
		return linkFigure;
	}
	
	public LabeledLinkFigure findLinkByLldp(final LLDPInofEntity lldp) {
		LinkIndex linkIndex = new LinkIndex(
				lldp.getLocalIP(), lldp.getRemoteIP());
		LabeledLinkFigure linkFigure = null;
		if (linkAddressIndexes.containsKey(linkIndex)) {
			linkFigure = linkAddressIndexes.get(linkIndex);
		}
		
		return linkFigure;
	}
	
	public NodeFigure findNodeByGuid(final String guid) {
		Assert.notNull(guid, "guid must not be null");
		NodeFigure nodeFigure = null;
		if (figureGuidIndexes.containsKey(guid)) {
			nodeFigure = figureGuidIndexes.get(guid);
		}
		
		return nodeFigure;
	}
	
	public NodeFigure findNodeByAddress(final String address) {
		NodeFigure nodeFigure = null;
		if (figureAddressIndexes.containsKey(address)) {
			nodeFigure = figureAddressIndexes.get(address);
		}
		
		return nodeFigure;
	}
	
	public NodeFigure findNodeByName(final String name) {
		NodeFigure nodeFigure = null;
		if (figureNameIndexes.containsKey(name)) {
			nodeFigure = figureNameIndexes.get(name);
		}
		
		return nodeFigure;
	}
	
	public Set<NodeEntity> getAllNodes() {
		Set<NodeEntity> setOfNode = new HashSet<NodeEntity>();
		for (NodeFigure nodeFigure : figureGuidIndexes.values()) {
			
			if (nodeFigure.getEdit().getModel() instanceof NodeEntity) {
				setOfNode.add((NodeEntity)nodeFigure.getEdit().getModel());
			}
		}
		return setOfNode;
	}
	
	public Set<LinkEntity> getAllLinks() {
		Set<LinkEntity> setOfLink = new HashSet<LinkEntity>();
		for (NodeFigure nodeFigure : figureGuidIndexes.values()) {
			
			if (nodeFigure.getEdit().getModel() instanceof LinkEntity) {
				setOfLink.add((LinkEntity)nodeFigure.getEdit().getModel());
			}
		}
		
		return setOfLink;
	}
	
	public void removeNode(String guid) {
		figureGuidIndexes.remove(guid);
	}

	private Map<String, NodeFigure> figureGuidIndexes;
	private Map<String, NodeFigure> figureAddressIndexes;
	private Map<String, NodeFigure> figureNameIndexes;
	private Map<LinkIndex, LabeledLinkFigure> linkAddressIndexes;
	private Map<Long, LabeledLinkFigure> linkFigureIdIndexes;
	private Map<Integer, RingEntity> ringNoIndexes;
	private final Drawing drawing;

	private static final Logger LOG = LoggerFactory.getLogger(DrawingAdapter.class);
}