package com.jhw.adm.client.diagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.Tuple;
import com.jhw.adm.client.model.EquipmentRepository;
import com.jhw.adm.client.model.LinkCategory;
import com.jhw.adm.client.model.SwitchPortCategory;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SwitchRingInfo;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.Constants;

/**
 * 拓扑连线生成器<br>
 * 根据发现回来的节点，生成节点连线
 */
public class LinkNodeGenerator {

	public LinkNodeGenerator(EquipmentRepository equipmentRepository, SwitcherNodeGenerator switcherNodeGenerator, EponNodeGenerator eponNodeGenerator) {
		this.switcherNodeGenerator = switcherNodeGenerator;
		this.eponNodeGenerator = eponNodeGenerator;
		this.equipmentRepository = ClientUtils.getSpringBean(EquipmentRepository.ID);
	}
	
	public void generate(final TopDiagramEntity topDiagramEntity) {

		LOG.info("开始生成拓扑节点链接====================>");
		Set<LinkEntity> setOfLink = new HashSet<LinkEntity>();
		generateSwitcherLink(setOfLink);
		generateEponLink(setOfLink);
		
		List<FEPTopoNodeEntity> listOfFrontEnd = equipmentRepository.findFrontEndNode();

		generateFrontEndLink(listOfFrontEnd, setOfLink);
		LOG.info(String.format("<====================完成拓扑节点链接[size: %s]的生成", setOfLink.size()));

		for (LinkEntity createdLink : setOfLink) {
			topDiagramEntity.getLines().add(createdLink);
			createdLink.setTopDiagramEntity(topDiagramEntity);
		}
	}
	
	private void generateSwitcherLink(final Set<LinkEntity> setOfLink) {

		List<LLDPInofEntity> createdLldpList = new ArrayList<LLDPInofEntity>();
		
		// 三层交换机
		for (SwitchTopoNodeLevel3 layer3SwitcherNode : switcherNodeGenerator.getAllLayer3Switcher()) {
			SwitchTopoNodeLevel3 startLayer3SwitcherNode = layer3SwitcherNode;
			SwitchTopoNodeLevel3 endLayer3SwitcherNode = null;
			SwitchLayer3 startLayer3Switcher =  startLayer3SwitcherNode.getSwitchLayer3();
			
			if (startLayer3Switcher.getLldps() == null || startLayer3Switcher.getLldps().size() == 0) {
				LOG.info("三层交换机[{}]LLDP为空，不会生成链接", startLayer3Switcher.getIpValue());
				continue;
			}
			
			for (LLDPInofEntity lldp : startLayer3Switcher.getLldps()) {
				
				if (!lldp.isSyschorized()) {
					LOG.info(String.format("LLDP %s 已经被标志为[删除]，不会生成LLDP链接", 
							NodeUtils.lldp2String(lldp)));
					continue;
				}
				
				if (existLink(createdLldpList, lldp)) {
					LOG.info(String.format("LLDP %s 已经存在，不会生成相同的LLDP链接", 
							NodeUtils.lldp2String(lldp)));
					continue;
				}
				
				LOG.debug(String.format("三层 LLDP %s", 
						NodeUtils.lldp2String(lldp)));
				
				endLayer3SwitcherNode = switcherNodeGenerator.findLayer3Switcher(lldp.getRemoteIP());
				if (endLayer3SwitcherNode != null) {
					LinkEntity link = new LinkEntity();
					link.setGuid(UUID.randomUUID().toString());
					link.setLineType(LinkCategory.ETHERNET);
					link.setLldp(lldp);
					link.setSynchorized(true);
					link.setNode1(startLayer3SwitcherNode);
					link.setNode2(endLayer3SwitcherNode);
					link.setNode1Guid(startLayer3SwitcherNode.getGuid());
					link.setNode2Guid(endLayer3SwitcherNode.getGuid());

					LOG.info(String.format("添加 LLDP %s", 
							NodeUtils.lldp2String(lldp)));
					
					setOfLink.add(link);
					createdLldpList.add(lldp);
				}

				SwitchTopoNodeEntity endSwitcherNode = switcherNodeGenerator.findSwitcher(lldp.getRemoteIP());
				
				if (endSwitcherNode != null) {
					LinkEntity link = new LinkEntity();
					link.setGuid(UUID.randomUUID().toString());
					link.setLldp(lldp);
					link.setLineType(LinkCategory.ETHERNET);
					link.setSynchorized(true);
					link.setNode1(startLayer3SwitcherNode);
					link.setNode2(endSwitcherNode);
					link.setNode1Guid(startLayer3SwitcherNode.getGuid());
					link.setNode2Guid(endSwitcherNode.getGuid());

					LOG.info(String.format("添加 LLDP %s", 
							NodeUtils.lldp2String(lldp)));
					setOfLink.add(link);
					createdLldpList.add(lldp);
				}
				
				EponTopoEntity oltNode = eponNodeGenerator.findOlt(lldp.getRemoteIP());
				
				if (oltNode != null) {
					LinkEntity link = new LinkEntity();
					link.setGuid(UUID.randomUUID().toString());
					link.setLldp(lldp);
					link.setSynchorized(true);
					link.setNode1(startLayer3SwitcherNode);
					link.setNode2(oltNode);
					link.setNode1Guid(startLayer3SwitcherNode.getGuid());
					link.setNode2Guid(oltNode.getGuid());
					LOG.info(String.format("添加三层交换机与OLT链接 LLDP %s", 
							NodeUtils.lldp2String(lldp)));
					setOfLink.add(link);
				}
			}
		}
		
		// 两层交换机
		for (SwitchTopoNodeEntity switcherNode : switcherNodeGenerator.getAllSwitcher()) {
			SwitchTopoNodeEntity startSwitcherNode = switcherNode;
			SwitchTopoNodeEntity endSwitcherNode = null;
			SwitchNodeEntity startSwitcher =  startSwitcherNode.getNodeEntity();
			
			if (startSwitcher.getLldpinfos() == null || startSwitcher.getLldpinfos().size() == 0) {
				LOG.info(String.format("交换机[%s][%s]LLDP为空，不会生成链接", 
						startSwitcher.getId(), startSwitcher.getBaseConfig().getIpValue()));
				continue;
			}

			for (LLDPInofEntity lldp : startSwitcher.getLldpinfos()) {
				
				if (!lldp.isSyschorized()) {
					LOG.info(String.format("LLDP %s 已经被标志为[删除]，不会生成LLDP链接", 
							NodeUtils.lldp2String(lldp)));
					continue;
				}
				
				if (existLink(createdLldpList, lldp)) {
					LOG.info(String.format("LLDP %s 已经存在，不会生成相同的LLDP链接", 
							NodeUtils.lldp2String(lldp)));
					continue;
				}
				
				endSwitcherNode = switcherNodeGenerator.findSwitcher(lldp.getRemoteIP());
				
				if (endSwitcherNode != null) {					
					int linkCategory = ensureLinkCategory(startSwitcherNode, lldp);
					LinkEntity link = new LinkEntity();
					link.setGuid(UUID.randomUUID().toString());
					link.setStatus(Constants.L_CONNECT);
					link.setLineType(linkCategory);
					link.setSynchorized(true);
					link.setNode1(startSwitcherNode);
					link.setNode2(endSwitcherNode);
					link.setNode1Guid(startSwitcherNode.getGuid());
					link.setNode2Guid(endSwitcherNode.getGuid());
					link.setLldp(lldp);

					LOG.info(String.format("添加LLDP链接%s", 
							NodeUtils.lldp2String(lldp)));

					if (findLocalBlockingLink(startSwitcher, lldp)) {
						link.setStatus(Constants.L_BLOCK);
						LOG.info(String.format("交换机[%s]存在环里面的链路[%s:%s] -> [%s:%s]不通", 
								startSwitcher.getBaseConfig().getIpValue(), 
								lldp.getLocalIP(), lldp.getLocalPortNo(),
								lldp.getRemoteIP(), lldp.getRemotePortNo()));
					} else if (findRemoteBlockingLink(endSwitcherNode.getNodeEntity(), lldp)) {
						link.setStatus(Constants.L_BLOCK);
						LOG.info(String.format("交换机[%s]存在环里面的链路[%s:%s] -> [%s:%s]不通", 
								startSwitcher.getBaseConfig().getIpValue(), 
								lldp.getLocalIP(), lldp.getLocalPortNo(),
								lldp.getRemoteIP(), lldp.getRemotePortNo()));
					}
					
					link.setRingID(findLinkInRing(startSwitcher, lldp));
					
					setOfLink.add(link);
					createdLldpList.add(lldp);
				} else {
					LOG.error(String.format("无法找到此LLDP%s指向的交换机",
							NodeUtils.lldp2String(lldp)));
				}
			}
		}
	}
	
	private void generateEponLink(final Set<LinkEntity> setOfLink) {
		for (EponTopoEntity oltNode : eponNodeGenerator.getAllOlt()) {
			OLTEntity olt = oltNode.getOltEntity();
			
			for (LLDPInofEntity lldp : olt.getLldpinfos()) {
				if (!lldp.isSyschorized()) {
					LOG.info(String.format("LLDP[%s:%s] -> [%s:%s] 已经被标志为[删除]，不会生成LLDP链接", 
							lldp.getLocalIP(), lldp.getLocalPortNo(),
							lldp.getRemoteIP(), lldp.getRemotePortNo()));
					continue;
				}
				String remoteAddress = lldp.getRemoteIP();
				
				SwitchTopoNodeEntity switcherNode = switcherNodeGenerator.findSwitcher(remoteAddress);
				
				if (switcherNode == null) {
					LOG.info(String.format("OLT[%s]指向的交换机[%s]不存在", 
							olt.getIpValue(), remoteAddress));
				} else {			
					LinkEntity link = new LinkEntity();
					link.setGuid(UUID.randomUUID().toString());
					link.setLldp(lldp);
					link.setLineType(LinkCategory.ETHERNET);
					link.setSynchorized(true);
					link.setNode1(oltNode);
					link.setNode2(switcherNode);
					link.setNode1Guid(oltNode.getGuid());
					link.setNode2Guid(switcherNode.getGuid());
					LOG.info(String.format("添加OLT[%s]与交换机[%s]的链接", 
							remoteAddress, switcherNode.getNodeEntity().getBaseConfig().getIpValue()));
					setOfLink.add(link);
				}
				
				SwitchTopoNodeLevel3 layer3SwitcherNode = switcherNodeGenerator.findLayer3Switcher(remoteAddress);
				if (layer3SwitcherNode != null) {					
					LinkEntity link = new LinkEntity();
					link.setGuid(UUID.randomUUID().toString());
					link.setLldp(lldp);
					link.setLineType(LinkCategory.ETHERNET);
					link.setSynchorized(true);
					link.setNode1(oltNode);
					link.setNode2(layer3SwitcherNode);
					link.setNode1Guid(oltNode.getGuid());
					link.setNode2Guid(layer3SwitcherNode.getGuid());
					LOG.info(String.format("添加OLT[%s]与三层交换机[%s]链接",
							olt.getIpValue(), layer3SwitcherNode.getSwitchLayer3().getIpValue()));
					setOfLink.add(link);
				}
				
				EponTopoEntity remoteOltNode = eponNodeGenerator.findOlt(remoteAddress);
				
				if (remoteOltNode == null) {
					LOG.info(String.format("OLT[%s]指向的OLT[%s]不存在", 
							olt.getIpValue(), remoteAddress));
				} else {
					LinkEntity link = new LinkEntity();
					link.setGuid(UUID.randomUUID().toString());
					link.setLldp(lldp);
					link.setLineType(LinkCategory.ETHERNET);
					link.setSynchorized(true);
					link.setNode1(oltNode);
					link.setNode2(remoteOltNode);
					link.setNode1Guid(oltNode.getGuid());
					link.setNode2Guid(remoteOltNode.getGuid());
					LOG.info(String.format("添加OLT[%s]与OLT[%s]链接",
							olt.getIpValue(), remoteOltNode.getIpValue()));
					setOfLink.add(link);
				}
			}
		}
		
		for (Tuple<String, Integer, Integer> targetOlt : eponNodeGenerator.getAllSplitterKey()) {
			String address = targetOlt.getT0();
			int slotNo = targetOlt.getT1();
			int portNo = targetOlt.getT2();
			EponTopoEntity oltNode = eponNodeGenerator.findOlt(address);
			Epon_S_TopNodeEntity splitterNode = eponNodeGenerator.findSplitter(targetOlt);
			
			if (oltNode != null && splitterNode != null) {
				LLDPInofEntity linkInfo = new LLDPInofEntity();
				linkInfo.setLocalIP(address);
				linkInfo.setLocalSlot(slotNo);
				linkInfo.setLocalPortNo(portNo);
				
				OLTEntity olt = oltNode.getOltEntity();
				
				if (olt.getLldpinfos() == null) {
					olt.setLldpinfos(new HashSet<LLDPInofEntity>());
				}
				
				olt.getLldpinfos().add(linkInfo);
				
				LinkEntity link = new LinkEntity();
				link.setGuid(UUID.randomUUID().toString());
				link.setLldp(linkInfo);
				link.setLineType(LinkCategory.FDDI);
				link.setSynchorized(true);
				link.setNode1(oltNode);
				link.setNode2(splitterNode);
				link.setNode1Guid(oltNode.getGuid());
				link.setNode2Guid(splitterNode.getGuid());
				LOG.info(String.format("添加OLT[%s]与分光器[%s:%s-%s]的链接", 
						address, address, slotNo, portNo));
				setOfLink.add(link);
			}
		}
		
		for (ONUTopoNodeEntity onuNode : eponNodeGenerator.getAllOnu()) {
			ONUEntity onu = onuNode.getOnuEntity();
			
			for (LLDPInofEntity lldp : onu.getLldpinfos()) {
				if (!lldp.isSyschorized()) {
					LOG.info(String.format("LLDP[%s:%s] -> [%s:%s] 已经被标志为[删除]，不会生成LLDP链接", 
							lldp.getLocalIP(), lldp.getLocalPortNo(),
							lldp.getRemoteIP(), lldp.getRemotePortNo()));
					continue;
				}
				String address = lldp.getRemoteIP();
				int slotNo = lldp.getRemoteSlot();
				int portNo = lldp.getRemotePortNo();
				Tuple<String, Integer, Integer> key = new Tuple<String, Integer, Integer>(address, slotNo, portNo);
				Epon_S_TopNodeEntity splitterNode = 
					eponNodeGenerator.findSplitter(key);
				
				if (splitterNode != null) {
					LinkEntity link = new LinkEntity();
					link.setGuid(UUID.randomUUID().toString());
					link.setLldp(lldp);
					link.setLineType(LinkCategory.FDDI);
					link.setSynchorized(true);
					link.setNode1(onuNode);
					link.setNode2(splitterNode);
					link.setNode1Guid(onuNode.getGuid());
					link.setNode2Guid(splitterNode.getGuid());
					
					/*
					 *  这是为了满足北京电科院OLT手拉手需求
					 *  1. 如果ONU的LLDP数量大于一则表示是在OLT拉手拉环境下
					 *  2. 如果LLDP的状态为断开，这是硬编码为阻塞状态
					 */
					if (hasManyLink(onu)) {
						link.setStatus(lldp.isConnected() ? Constants.L_CONNECT : Constants.L_BLOCK);
					} else {
						link.setStatus(lldp.isConnected() ? Constants.L_CONNECT : Constants.L_UNCONNECT);	
					}
					
					LOG.info(String.format("添加ONU[%s]与分光器[%s:%s-%s]的链接", 
							onu.getMacValue(), address, slotNo, portNo));
					setOfLink.add(link);
				}
			}
		}
	}
	
	private boolean hasManyLink(ONUEntity onu) {
		int count = 0;
		
		for (LLDPInofEntity lldp : onu.getLldpinfos()) {
			if (lldp.isSyschorized()) {
				count++;
			}
		}
		
		return count > 1;
	}
	
	private void generateFrontEndLink(List<FEPTopoNodeEntity> listOfFrontEnd, Set<LinkEntity> setOfLink) {		
		
		for (FEPTopoNodeEntity frontEndNode : listOfFrontEnd) {
			FEPEntity frontEnd = NodeUtils.getNodeEntity(frontEndNode).getFepEntity();
			String switcherAddress = frontEnd.getDirectSwitchIp();
			SwitchTopoNodeEntity switcherNode = switcherNodeGenerator.findSwitcher(switcherAddress);
			if (switcherNode != null) {
				LinkEntity link = new LinkEntity();
				link.setGuid(UUID.randomUUID().toString());
				link.setLineType(LinkCategory.WIRELESS);
				link.setSynchorized(true);
				link.setNode1(frontEndNode);
				link.setNode2(switcherNode);
				link.setNode1Guid(frontEndNode.getGuid());
				link.setNode2Guid(switcherNode.getGuid());
				LOG.info(String.format("添加前置机[%s]与交换机[%s]的链接", 
						frontEnd.getIpValue(), switcherNode.getNodeEntity().getBaseConfig().getIpValue()));
				setOfLink.add(link);
			}
			
			SwitchTopoNodeLevel3 switcher3Node = switcherNodeGenerator.findLayer3Switcher(switcherAddress);
			if (switcher3Node != null) {
				LinkEntity link = new LinkEntity();
				link.setGuid(UUID.randomUUID().toString());
				link.setLineType(LinkCategory.WIRELESS);
				link.setSynchorized(true);
				link.setNode1(frontEndNode);
				link.setNode2(switcher3Node);
				link.setNode1Guid(frontEndNode.getGuid());
				link.setNode2Guid(switcher3Node.getGuid());
				LOG.info(String.format("添加前置机[%s]与三层交换机[%s]的链接", 
						frontEnd.getIpValue(), switcher3Node.getSwitchLayer3().getIpValue()));
				setOfLink.add(link);
			}
		}
	}
	
	/**
	 * 判断交换机连线类型
	 */
	private int ensureLinkCategory(SwitchTopoNodeEntity switcher, LLDPInofEntity lldp) {
		int category = LinkCategory.ETHERNET;		
//		int portNo = lldp.getLocalPortNo();
		
		if (lldp.getLocalPortType() == SwitchPortCategory.FDDI_PORT) {
			category = LinkCategory.FDDI;
		}
		
//		SwitchPortEntity thisPort = null;
//		if (switcher.getNodeEntity().getPorts() != null) {
//			for (SwitchPortEntity port : switcher.getNodeEntity().getPorts()) {
//				if (port.getPortNO() == portNo) {
//					thisPort = port;
//					LOG.info(String.format("查找到LLDP[%s]对应交换机[%s]端口[%s:%s]",
//							NodeUtils.lldp2String(lldp),
//							switcher.getIpValue(),
//							thisPort.getPortNO(), 
//							thisPort.getType()));
//					break;
//				}
//			}
//		} else {
//			LOG.error(String.format("交换机[%s]端口为空，无法查找LLDP%s端口类型", 
//					switcher.getIpValue(), NodeUtils.lldp2String(lldp)));
//		}
//		
//		if (thisPort != null && 
//			thisPort.getType() == SwitchPortCategory.FDDI_PORT) {
//			category = LinkCategory.FDDI;
//		}
		
		return category;
	}
	
	private int findLinkInRing(SwitchNodeEntity switcher, LLDPInofEntity lldp) {
		int ringNo = 0;
		String address = switcher.getBaseConfig().getIpValue();
		for (SwitchRingInfo ringInfo : switcher.getRings()) {
			int ringPort = ringInfo.getPortNo();
			
			boolean inRing = 
				(lldp.getLocalIP().equals(address) && lldp.getLocalPortNo() == ringPort) || 
				(lldp.getRemoteIP().equals(address) && lldp.getRemotePortNo() == ringPort);
			if (inRing) {
				ringNo = ringInfo.getRingID();
			}
		}
		
		return ringNo;
	}
	
	/**
	 * 查找交换机的LLDP本地端口(LocalPort)是否阻塞
	 */
	private boolean findLocalBlockingLink(SwitchNodeEntity switcher, LLDPInofEntity lldp) {
		boolean result = false;
		for (SwitchRingInfo ringInfo : switcher.getRings()) {
			if (!ringInfo.isForwarding() &&
				ringInfo.getPortNo() == lldp.getLocalPortNo()) {
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * 查找交换机的LLDP远程端口(LocalPort)是否阻塞
	 */
	private boolean findRemoteBlockingLink(SwitchNodeEntity switcher, LLDPInofEntity lldp) {
		boolean result = false;
		for (SwitchRingInfo ringInfo : switcher.getRings()) {
			if (!ringInfo.isForwarding() &&
				ringInfo.getPortNo() == lldp.getRemotePortNo()) {
				result = true;
			}
		}
		
		return result;
	}
	
	// 连线是否存在
	private boolean existLink(List<LLDPInofEntity> createdLLDP, LLDPInofEntity lldp) {
		boolean result = false;
		
		if (lldp.getLocalIP().equals(lldp.getRemoteIP())) {
			LOG.error(String.format("不能自己连接自己 LLDP %s", 
					NodeUtils.lldp2String(lldp)));
			
			return true;
		}
		
		for (LLDPInofEntity start : createdLLDP) {
			if (sameLink(start, lldp)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	// 两个连线是否相同
	private boolean sameLink(LLDPInofEntity start, LLDPInofEntity end) {
		boolean result = false;
		
		if (start.getLocalIP().equals(end.getLocalIP()) &&
			start.getLocalPortNo() == end.getLocalPortNo() &&
			start.getLocalPortType() == end.getLocalPortType() &&
			start.getLocalSlot() == end.getLocalSlot()) {
			return true;
		}
		
		if (start.getLocalIP().equals(end.getRemoteIP()) &&
			start.getLocalPortNo() == end.getRemotePortNo() &&
			start.getLocalPortType() == end.getRemotePortType() &&
			start.getLocalSlot() == end.getRemoteSlot()) {
			return true;
		}
		
		return result;
	}

	private final EquipmentRepository equipmentRepository;
	private final SwitcherNodeGenerator switcherNodeGenerator;
	private final EponNodeGenerator eponNodeGenerator;
	private static final Logger LOG = LoggerFactory.getLogger(LinkNodeGenerator.class);
}