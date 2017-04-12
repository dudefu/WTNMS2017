package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;

/**
 * 设备模型，包含顶层图形、交换机、载波机、子网。<p>
 * 实现了TreeModel，可以与JTree绑定。
 */
@Component(EquipmentModel.ID)
public class EquipmentModel extends ViewModel implements TreeModel {

	public EquipmentModel() {
		fepNo = new AtomicInteger();
		switcherTreeModel = new SwitcherTreeModel();
		carrierTreeModel = new CarrierTreeModel();
		gprsTreeModel = new GprsTreeModel();
		eponTreeModel = new EponTreeModel();
		selectedRows = new ArrayList<Object>();
		listOfTreeModelListener = new CopyOnWriteArrayList<TreeModelListener>();
	}
	
	/**
	 * 添加节点
	 */
	public void addNode(NodeEntity nodeEntity) {
//		diagramModel.addNode(nodeEntity);
		topDiagram.getNodes().add(nodeEntity);
		if (getSubnet() != null) {
			nodeEntity.setParentNode(getSubnet().getGuid());
			getSubnet().getNodes().add(nodeEntity);
			this.getEquipmentNoInfo(new HashSet<NodeEntity>(getSubnet().getNodes()));
		}else{
			this.getEquipmentNoInfo(topDiagram.getNodes());
		}
		nodeEntity.setTopDiagramEntity(topDiagram);
		touch();
	}
	
	/**
	 * 添加连线
	 */
	public void addLink(LinkEntity linkEntity) {
//		diagramModel.addLink(linkEntity);
		topDiagram.getLines().add(linkEntity);
		linkEntity.setTopDiagramEntity(topDiagram);
		lastSelected = linkEntity;
		touch();
	}
	
	/**
	 * 删除节点
	 */
	public void removeNode(NodeEntity nodeEntity) {
		topDiagram.getNodes().remove(nodeEntity);
		if(StringUtils.isBlank(nodeEntity.getParentNode())){
			this.getEquipmentNoInfo(topDiagram.getNodes());
		}else{
			getSubnet().getNodes().remove(nodeEntity);
			this.getEquipmentNoInfo(new HashSet<NodeEntity>(getSubnet().getNodes()));			
		}
		touch();
	}
	
	/**
	 * 删除连线
	 */
	public void removeLink(LinkEntity linkEntity) {
		topDiagram.getLines().remove(linkEntity);
		touch();
	}
	
	/**
	 * 修改设备模型
	 */
	public void touch() {
		if (isModified) {
			//
		} else {
			isModified = true;
			firePropertyChange(EQUIPMENT_MODIFIED, false, isModified);
		}
	}

	@Override
	public Object getChild(Object parent, int index) {
		Object child = null;
		if (parent instanceof DiagramDecorator) {
			DiagramDecorator diagram = (DiagramDecorator) parent;
			child = diagram.getNode(index);
		} else if (parent instanceof DiagramDecorator.Node) {
			DiagramDecorator.Node diagramNode = (DiagramDecorator.Node) parent;
			child = diagramNode.getNode(index);
		}

		return child;
	}

	@Override
	public int getChildCount(Object parent) {
		int count = 0;
		if (parent instanceof DiagramDecorator) {
			DiagramDecorator diagram = (DiagramDecorator) parent;
			count = diagram.getNodeCount();
		} else if (parent instanceof DiagramDecorator.Node) {
			DiagramDecorator.Node diagramNode = (DiagramDecorator.Node)parent;
			count = diagramNode.getNodeCount();
		}
		return count;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null) {
			return -1;
		}

		return -1;
	}

	@Override
	public Object getRoot() {
		return diagramModel;
	}

	@Override
	public boolean isLeaf(Object node) {
		if (node instanceof DiagramDecorator) {
			return false;
		}
		if (node instanceof DiagramDecorator.Node) {			
			return ((DiagramDecorator.Node)node).getNodeCount() == 0;
		}
		return true;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listOfTreeModelListener.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listOfTreeModelListener.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public TopDiagramEntity getDiagram() {
		return topDiagram;
	}
	
	/**
	 * 更新图形实体
	 */
	public void updateDiagram(TopDiagramEntity topDiagram) {
		this.topDiagram = topDiagram;
		isModified = false;
		diagramModel = new DiagramDecorator(topDiagram);
		subnetNode = null;
		this.getEquipmentNoInfo(topDiagram.getNodes());
		setDiagramName(topDiagram.getName());
		TreePath treePath = new TreePath(this);
		TreeModelEvent event = new TreeModelEvent(this, treePath);
		notifyStructureChanged(event);
		notifyObservers();
	}
	
	private int layer2No = 0;
	private int layer3No = 0;
	private int oltNo = 0;
	private int onuNo = 0;
	private int virtualNo = 0;
	private int carrierNo = 0;
	private int gprsNo = 0;
	
	private String layer2Message = "";
	public String getLayer2Message() {
		return layer2Message;
	}

	private String layer3Message = "";
	public String getLayer3Message() {
		return layer3Message;
	}

	private String oltMessage = "";
	public String getOltMessage() {
		return oltMessage;
	}

	private String onuMessage = "";
	public String getOnuMessage() {
		return onuMessage;
	}

	private String virtualMessage = "";
	public String getVirtualMessage() {
		return virtualMessage;
	}

	private String carrierMessage = "";
	public String getCarrierMessage() {
		return carrierMessage;
	}

	private String gprsMessage = "";
	public String getGprsMessage() {
		return gprsMessage;
	}
	
	private void getEquipmentNoInfo(Set<NodeEntity> nodeEntitySet){
		this.initializeEquipmentNo();
		this.countEquipmentNo(nodeEntitySet);
		this.packageMessage();
	}
	
	private void initializeEquipmentNo(){
		layer2No = 0;
		layer3No = 0;
		oltNo = 0;
		onuNo = 0;
		virtualNo = 0;
		carrierNo = 0;
		gprsNo = 0;
		layer2Message = "";
		layer3Message = "";
		oltMessage = "";
		onuMessage = "";
		virtualMessage = "";
		carrierMessage = "";
		gprsMessage = "";
	}
	
	private void countEquipmentNo(Set<NodeEntity> nodeEntitySet){
		for(NodeEntity nodeEntity : nodeEntitySet){
			if(nodeEntity instanceof SwitchTopoNodeEntity){
				layer2No += 1;
				continue;
			}
			if(nodeEntity instanceof SwitchTopoNodeLevel3){
				layer3No += 1;
				continue;
			}
			if(nodeEntity instanceof EponTopoEntity){
				oltNo += 1;
				continue;
			}
			if(nodeEntity instanceof ONUTopoNodeEntity){
				onuNo += 1;
				continue;
			}
			if(nodeEntity instanceof VirtualNodeEntity){
				virtualNo += 1;
				continue;
			}
			if(nodeEntity instanceof CarrierTopNodeEntity){
				carrierNo += 1;
				continue;
			}
			if(nodeEntity instanceof GPRSTopoNodeEntity){
				gprsNo += 1;
				continue;
			}
		}
	}
	
	private void packageMessage(){
		if(layer2No > 0){
			layer2Message = String.format("二层交换机:%s", layer2No);
		}
		if(layer3No > 0){
			layer3Message = String.format("三层交换机:%s", layer3No);
		}
		if(oltNo > 0){
			oltMessage = String.format("OLT:%s", oltNo);
		}
		if(onuNo > 0){
			onuMessage = String.format("ONU:%s", onuNo);
		}
		if(virtualNo > 0){
			virtualMessage = String.format("虚拟网元:%s", virtualNo);
		}
		if(carrierNo > 0){
			carrierMessage = String.format("载波机:%s", carrierNo);
		}
		if(gprsNo > 0){
			gprsMessage = String.format("GPRS:%s", gprsNo);
		}
	}
	
//	public SwitchTopoNodeEntity findSwitcherByAddress(String address) {
//		SwitchTopoNodeEntity theNode = null;		
//		DiagramDecorator.Node switcherCategory = diagramModel.getSwitcherCategory();		
//		int count = switcherCategory.getNodeCount();
//		
//		for (int index = 0; index < count; index++) {
//			SwitchTopoNodeEntity switcherNode = (SwitchTopoNodeEntity)switcherCategory.getNode(index).getEntity();
//			if (switcherNode.getIpValue().equals(address)) {
//				theNode = switcherNode;
//				break;
//			}
//		}
//		
//		return theNode;
//	}
	
	public List<CarrierTopNodeEntity> findAllCarrier() {
		List<CarrierTopNodeEntity> listOfCarrier = new ArrayList<CarrierTopNodeEntity>();
		DiagramDecorator.Node carrierCategory = diagramModel.getCarrierCategory();
		int count = carrierCategory.getNodeCount();
		
		for (int index = 0; index < count; index++) {
			CarrierTopNodeEntity carrierNode = (CarrierTopNodeEntity)carrierCategory.getNode(index).getEntity();
			listOfCarrier.add(carrierNode);
		}
		
		return listOfCarrier;
	}
	
	public List<GPRSTopoNodeEntity> findAllGprs() {
		List<GPRSTopoNodeEntity> listOfGprs = new ArrayList<GPRSTopoNodeEntity>();
		DiagramDecorator.Node gprsCategory = diagramModel.getGprsCategory();
		int count = gprsCategory.getNodeCount();
		
		for (int index = 0; index < count; index++) {
			GPRSTopoNodeEntity gprsNode = (GPRSTopoNodeEntity)gprsCategory.getNode(index).getEntity();
			listOfGprs.add(gprsNode);
		}
		
		return listOfGprs;
	}
	
	public LinkEntity findLink(Long id) {
		LinkEntity linkNode = null;
		
		for (LinkEntity link : topDiagram.getLines()) {
			if (link.getId() != null && link.getId().equals(id)) {
				linkNode = link;
				break;
			}
		}
		
		return linkNode;
	}

	protected void notifyStructureChanged(TreeModelEvent event) {
		for (TreeModelListener listener : listOfTreeModelListener) {
			listener.treeStructureChanged(event);
		}
	}

	/**
	 * 获取最后选择的设备。
	 */
	public Object getLastSelected() {
		return lastSelected;
	}
	
	/*
	 * 获取最后定位的设备。
	 */
	private Object lastFixedPosition;

	public Object getLastFixedPosition() {
		return lastFixedPosition;
	}
	public void setLastFixedPosition(Object lastFixedPosition) {
		this.lastFixedPosition = lastFixedPosition;
	}

	/**
	 * 改变选择的设备。
	 * @param selected 被选中的设备
	 */
	public void changeSelected(Object selected) {
		Object oldValue = lastSelected;
		this.lastSelected = selected;
		firePropertyChange(PROP_LAST_SELECTED, oldValue, selected);
	}
	
	public void clearSelection() {
		Object oldValue = lastSelected;
		this.lastSelected = null;
		firePropertyChange(PROP_LAST_SELECTED, oldValue, lastSelected);
	}
	
	/**
	 * 触发设备已更新事件
	 * @param equipment 已更新的设备
	 */
	public void fireEquipmentUpdated(Object equipment) {
		if (equipment == null) {
			return;
		}
		firePropertyChange(EQUIPMENT_UPDATED, null, equipment);
	}
	
	public List<?> getSelectedRows() {
		return Collections.unmodifiableList(selectedRows);
	}

	public TreeModel getSwitcherTreeModel() {
		return switcherTreeModel;
	}

	public TreeModel getCarrierTreeModel() {
		return carrierTreeModel;
	}

	public TreeModel getGprsTreeModel() {
		return gprsTreeModel;
	}

	public TreeModel getEponTreeModel() {
		return eponTreeModel;
	}
	
	/**
	 * 拓扑图形是否被修改
	 * @return true 修改未保存；false 已经保存
	 */
	public boolean isModified() {
		return isModified;
	}

	public String getDiagramName() {
		return diagramName;
	}

	public void setDiagramName(String diagramName) {
		this.diagramName = diagramName;
	}
	
	/**
	 * 被选中自动发现的前置机列表
	 */
	public List<FEPEntity> getSelectedFeps() {
		return selectedFeps;
	}
	
	public SubNetTopoNodeEntity getSubnet() {
		return subnetNode;
	}
	
	public void enterSubnet(final SubNetTopoNodeEntity subnetNode) {
		this.subnetNode = subnetNode;
		this.getEquipmentNoInfo(new HashSet<NodeEntity>(subnetNode.getNodes()));
		firePropertyChange(SUBNET_ENTER, null, subnetNode);
	}
	
	public void requireSubnet(final String subnetGuid) {
		firePropertyChange(REQUIRE_SUBNET, null, subnetGuid);
	}
	
	public void requireRefresh() {
		firePropertyChange(REQUIRE_REFRESH, null, REQUIRE_REFRESH);
	}
	
	public void beginDiscover(List<FEPEntity> list) {
		selectedFeps = list;
		
		if (selectedFeps != null) {
			fepNo.set(selectedFeps.size());
		}
	}
	
	public int discover() {
		return fepNo.decrementAndGet();
	}
	
	private final AtomicInteger fepNo;
	private List<FEPEntity> selectedFeps;
	private String diagramName;
	private boolean isModified;
	private final List<TreeModelListener> listOfTreeModelListener;
	private TopDiagramEntity topDiagram;
	private SubNetTopoNodeEntity subnetNode;
	private DiagramDecorator diagramModel;
	private final TreeModel switcherTreeModel;
	private final TreeModel carrierTreeModel;
	private final TreeModel gprsTreeModel;
	private final TreeModel eponTreeModel;
	private Object lastSelected;
	private final List<?> selectedRows;
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory
			.getLogger(EquipmentModel.class);
	public static final String PROP_LAST_SELECTED = "lastSelected";
	public static final String EQUIPMENT_UPDATED = "EQUIPMENT_UPDATED";
	public static final String EQUIPMENT_MODIFIED = "EQUIPMENT_MODIFIED";
	public static final String SUBNET_ENTER = "SUBNET_ENTER";
	public static final String REQUIRE_SUBNET = "REQUIRE_SUBNET";
	public static final String REQUIRE_REFRESH = "REQUIRE_REFRESH";
	public static final String ID = "equipmentModel";

	public class SwitcherTreeModel implements TreeModel {

		@Override
		public Object getChild(Object parent, int index) {
			Object child = null;
			if (parent instanceof DiagramDecorator) {
				DiagramDecorator diagramModel = (DiagramDecorator) parent;
				child = diagramModel.getSwitcherCategory().getNode(index);
			}

			return child;
		}

		@Override
		public int getChildCount(Object parent) {
			int count = 0;
			if (parent instanceof DiagramDecorator) {
				DiagramDecorator diagramModel = (DiagramDecorator) parent;
				count = diagramModel.getSwitcherCategory().getNodeCount();
			}
			return count;
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			if (parent == null || child == null) {
				return -1;
			}

			return -1;
		}

		@Override
		public Object getRoot() {
			return diagramModel;
		}

		@Override
		public boolean isLeaf(Object node) {
			if (node instanceof DiagramDecorator) {
				return false;
			}
			if (node instanceof DiagramDecorator.Node) {			
				return ((DiagramDecorator.Node)node).getNodeCount() == 0;
			}
			return true;
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) {
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
		}
	}
	
	public class EponTreeModel implements TreeModel {

		@Override
		public Object getChild(Object parent, int index) {
			Object child = null;
			if (parent instanceof DiagramDecorator) {
				child = diagramModel.getEponCategory().getNode(index);
			} else if (parent instanceof DiagramDecorator.Node) {
				DiagramDecorator.Node diagramNode = (DiagramDecorator.Node) parent;
				child = diagramNode.getNode(index);
			}

			return child;
		}

		@Override
		public int getChildCount(Object parent) {
			int count = 0;
			if (parent instanceof DiagramDecorator) {
				count = diagramModel.getEponCategory().getNodeCount();
			} else if (parent instanceof DiagramDecorator.Node) {
				DiagramDecorator.Node diagramNode = (DiagramDecorator.Node)parent;
				count = diagramNode.getNodeCount();
			}
			return count;
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			if (parent == null || child == null) {
				return -1;
			}

			return -1;
		}

		@Override
		public Object getRoot() {
			return diagramModel;
		}

		@Override
		public boolean isLeaf(Object node) {
			if (node instanceof DiagramDecorator) {
				return false;
			}
			if (node instanceof DiagramDecorator.Node) {			
				return ((DiagramDecorator.Node)node).getNodeCount() == 0;
			}
			return true;
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) {
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
		}
	}
	
	public class CarrierTreeModel implements TreeModel {

		@Override
		public Object getChild(Object parent, int index) {
			Object child = null;
			if (parent instanceof DiagramDecorator) {
				DiagramDecorator diagramModel = (DiagramDecorator) parent;
				child = diagramModel.getCarrierCategory().getNode(index);
			}

			return child;
		}

		@Override
		public int getChildCount(Object parent) {
			int count = 0;
			if (parent instanceof DiagramDecorator) {
				DiagramDecorator diagramModel = (DiagramDecorator) parent;
				count = diagramModel.getCarrierCategory().getNodeCount();
			}
			return count;
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			if (parent == null || child == null) {
				return -1;
			}

			return -1;
		}

		@Override
		public Object getRoot() {
			return diagramModel;
		}

		@Override
		public boolean isLeaf(Object node) {
			if (node instanceof DiagramDecorator) {
				return false;
			}
			if (node instanceof DiagramDecorator.Node) {			
				return ((DiagramDecorator.Node)node).getNodeCount() == 0;
			}
			return true;
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) {
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
		}
	}
	
	public class GprsTreeModel implements TreeModel {

		@Override
		public Object getChild(Object parent, int index) {
			Object child = null;
			if (parent instanceof DiagramDecorator) {
				DiagramDecorator diagramModel = (DiagramDecorator) parent;
				child = diagramModel.getGprsCategory().getNode(index);
			}

			return child;
		}

		@Override
		public int getChildCount(Object parent) {
			int count = 0;
			if (parent instanceof DiagramDecorator) {
				DiagramDecorator diagramModel = (DiagramDecorator) parent;
				count = diagramModel.getGprsCategory().getNodeCount();
			}
			return count;
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			if (parent == null || child == null) {
				return -1;
			}

			return -1;
		}

		@Override
		public Object getRoot() {
			return diagramModel;
		}

		@Override
		public boolean isLeaf(Object node) {
			if (node instanceof DiagramDecorator) {
				return false;
			}
			if (node instanceof DiagramDecorator.Node) {			
				return ((DiagramDecorator.Node)node).getNodeCount() == 0;
			}
			return true;
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) {
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
		}
	}
}