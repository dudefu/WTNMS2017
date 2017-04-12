package com.jhw.adm.client.model;

import static com.jhw.adm.client.util.NodeUtils.getNodeText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;

/**
 * 图形装饰器，把原始数据进行分类、提取显示文本信息。
 */
public class DiagramDecorator {
	
	public DiagramDecorator(TopDiagramEntity topDiagram) {
		this.topDiagram = topDiagram;
		this.text = topDiagram.getName();
		this.remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
		listOfNode = new ArrayList<Node>();
		if (topDiagram.getNodes() == null) {
			topDiagram.setNodes(new HashSet<NodeEntity>());
		}
		if (topDiagram.getLines() == null) {
			topDiagram.setLines(new HashSet<LinkEntity>());
		}
		classify(topDiagram);
	}
	
	private void classify(TopDiagramEntity diagram) {
		treeOltNodeMap = new HashMap<String, Node>();
		switcherCategory = new Node("交换机", SWITCHER_CATEGORY);
		carrierCategory = new Node("载波机", CARRIER_CATEGORY);
		gprsCategory = new Node("GPRS", GPRS_CATEGORY);
		frontEndCategory = new Node("前置机", FRONT_END_CATEGORY);
		eponCategory = new Node("EPON", EPON_CATEGORY);
		listOfNode.add(switcherCategory);
		listOfNode.add(carrierCategory);
		listOfNode.add(gprsCategory);
		listOfNode.add(frontEndCategory);
		listOfNode.add(eponCategory);
		
		for (NodeEntity nodeEntity : diagram.getNodes()) {
			classifyNode(nodeEntity);
		}
		
		for (NodeEntity nodeEntity : diagram.getNodes()) {
			if (nodeEntity instanceof ONUTopoNodeEntity) {
				classifyONUNode((ONUTopoNodeEntity)nodeEntity);
			}
		}
	}
	
	private void classifyNode(NodeEntity nodeEntity) {
		if (nodeEntity instanceof SwitchTopoNodeEntity) {
			SwitchTopoNodeEntity switcherNode = (SwitchTopoNodeEntity)nodeEntity;
			String text = getNodeText(switcherNode);
			switcherCategory.addChild(new Node(text, SWITCHER_CATEGORY, switcherNode));
		}
		if (nodeEntity instanceof CarrierTopNodeEntity) {
			CarrierTopNodeEntity carrierNode = (CarrierTopNodeEntity)nodeEntity;
			String text = getNodeText(carrierNode);
			carrierCategory.addChild(new Node(text, CARRIER_CATEGORY, carrierNode));
		}
		if (nodeEntity instanceof FEPTopoNodeEntity) {
			FEPTopoNodeEntity frontEndNode = (FEPTopoNodeEntity)nodeEntity;
			String text = getNodeText(frontEndNode);
			frontEndCategory.addChild(new Node(text, FRONT_END_CATEGORY, frontEndNode));
		}
		if (nodeEntity instanceof GPRSTopoNodeEntity) {
			GPRSTopoNodeEntity gprsNode = (GPRSTopoNodeEntity)nodeEntity;
			String text = getNodeText(gprsNode);
			gprsCategory.addChild(new Node(text, GPRS_CATEGORY, gprsNode));
		}
		if (nodeEntity instanceof EponTopoEntity) {
			EponTopoEntity oltNode = (EponTopoEntity)nodeEntity;
			String text = getNodeText(oltNode);
			Node treeNode = new Node(text, EPON_CATEGORY, oltNode);
			eponCategory.addChild(treeNode);
			treeOltNodeMap.put(oltNode.getIpValue(), treeNode);
		}
		if (nodeEntity instanceof SubNetTopoNodeEntity) {
			SubNetTopoNodeEntity subnetNode = (SubNetTopoNodeEntity)nodeEntity;
			Node treeNode = new Node(subnetNode.getName(), SUBNET_CATEGORY, subnetNode);
//			treeNode.addChild(new Node("*", 999));
			listOfNode.add(treeNode);
		}
	}
	
	private void classifyONUNode(ONUTopoNodeEntity onuNode) {
		ONUEntity onu = remoteServer.getService().getOnuByMacValue(onuNode.getMacValue());
		String text = getNodeText(onuNode);
		//暂时新增对ONU的空值判断
		if(onu != null){
			for (LLDPInofEntity linkInfo : onu.getLldpinfos()) {
				Node treeOltNode = treeOltNodeMap.get(linkInfo.getRemoteIP());
				
				if (treeOltNode == null) {
					LOG.error("OLT[{}] node is null", linkInfo.getRemoteIP());
				} else {
					treeOltNode.addChild(new Node(text, ONU_CATEGORY, onuNode));
				}
			}
		}
	}
	
	public Node getNode(int index) {
		return listOfNode.get(index);
	}
	
	public int getNodeCount() {
		return listOfNode.size();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Node getSwitcherCategory() {
		return switcherCategory;
	}

	public Node getCarrierCategory() {
		return carrierCategory;
	}

	public Node getGprsCategory() {
		return gprsCategory;
	}

	public Node getFrontEndCategory() {
		return frontEndCategory;
	}

	public TopDiagramEntity getEntity() {
		return topDiagram;
	}

	public Node getEponCategory() {
		return eponCategory;
	}
	
	private Node switcherCategory;
	private Node carrierCategory;
	private Node gprsCategory;
	private Node frontEndCategory;
	private Node eponCategory;

	private String text;
	private Map<String, Node> treeOltNodeMap;
	private final TopDiagramEntity topDiagram;
	private final RemoteServer remoteServer;
	
	private final List<Node> listOfNode;
	
	public static final int SWITCHER_CATEGORY = 1;
	public static final int CARRIER_CATEGORY = 2;
	public static final int GPRS_CATEGORY = 3;
	public static final int FRONT_END_CATEGORY = 4;
	public static final int EPON_CATEGORY = 5;
	public static final int ONU_CATEGORY = 6;
	public static final int SUBNET_CATEGORY = 7;
	private static final Logger LOG = LoggerFactory.getLogger(DiagramDecorator.class);
	
	public static class Node {
		
		public Node(String text, int category) {
			this(text, category, new NodeEntity());
		}
		
		public Node(String text, int category, NodeEntity entity) {
			this.text = text;
			this.entity = entity;
			this.category = category;
			children = new ArrayList<Node>();
		}
		
		public String getText() {
			return text;
		}
		
		public void setText(String text) {
			this.text = text;
		}
		
		public void addChild(Node child) {
			children.add(child);
		}
		
		public Node getNode(int index) {
			return children.get(index);
		}
		
		public int getNodeCount() {
			return children.size();
		}
		
		public NodeEntity getEntity() {
			return entity;
		}

		public void setEntity(NodeEntity entity) {
			this.entity = entity;
		}

		public int getCategory() {
			return category;
		}

		public void setCategory(int category) {
			this.category = category;
		}

		private NodeEntity entity;
		private String text;
		private int category;
		private final List<Node> children;
	}
}