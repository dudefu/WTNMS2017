package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.NodeTreeFactory.SwitcherGroupNodeEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;

@Component(EquipmentExplorerView.ID)
@Scope(Scopes.DESKTOP)
public class EquipmentExplorerView extends ViewPart {	

	private NodeTreeFactory nodeTreeFactory = null;
	
	@PostConstruct
	protected void initialize() {
		setTitle(localizationManager.getString(ResourceConstants.EQUIPVIEW_EQUIPEXPLORER));
		setLayout(new BorderLayout());
		
		nodeTreeFactory = new NodeTreeFactory(equipmentModel.getDiagramName());
		tree = nodeTreeFactory.getTreeInstance();
		
		tree.addTreeExpansionListener(treeExpansionListener);
		tree.addTreeSelectionListener(new TreeSelectListener());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane scrollTreeView = new JScrollPane(tree);
		add(scrollTreeView, BorderLayout.CENTER);
		initTreeValue();

		addTreeSelectionPath(equipmentModel.getLastSelected());
	}
	
	@SuppressWarnings("unchecked")
	private void initTreeValue(){
		List<NodeEntity> nodeEntitiyLists = new ArrayList<NodeEntity>();
		List<NodeEntity> nodeEntityList = (List<NodeEntity>) remoteServer.getService().findAll(NodeEntity.class);
		for(NodeEntity nodeEntity : nodeEntityList){
			if(StringUtils.isBlank(nodeEntity.getParentNode())){
				nodeEntitiyLists.add(nodeEntity);
			}
		}
		nodeEntitiyLists = NodeUtils.filterNodeEntityByUser(nodeEntitiyLists);
		if(nodeEntitiyLists.size() != 0){
			nodeTreeFactory.addNode(nodeEntitiyLists);
		}
	}
	
	private final TreeExpansionListener treeExpansionListener = new TreeExpansionListener(){
		@Override
		public void treeCollapsed(TreeExpansionEvent event) {
			//
		}
		@Override
		public void treeExpanded(TreeExpansionEvent event) {
			if(null != getSelectedNodeEntity()){
				for (int row = 0; row < tree.getRowCount(); row++) {
					Object obj = tree.getPathForRow(row).getLastPathComponent();
					if (((DefaultMutableTreeNode)obj).getUserObject() instanceof NodeEntity) {
						NodeEntity diagramNode = (NodeEntity)((DefaultMutableTreeNode)obj).getUserObject();
						if (getSelectedNodeEntity().getId().equals(diagramNode.getId())) {
							tree.addSelectionPath(tree.getPathForRow(row));
						}
					}
				}
			}
		}
	};
	
	private void addTreeSelectionPath(Object selected) {
		if(null == selected){
			return;
		}
		if(selected instanceof NodeEntity){
			if (selected instanceof SwitchTopoNodeEntity) {
				SwitchTopoNodeEntity switcherNode = (SwitchTopoNodeEntity)selected;
				if(StringUtils.isBlank(switcherNode.getIpValue())){
					return;
				}
				this.setSelectedNodeEntity(switcherNode);
				
				String groupIP = switcherNode.getIpValue().substring(0, switcherNode.getIpValue().lastIndexOf("."));
				DefaultMutableTreeNode topNode =  nodeTreeFactory.getSwitchRoot();
				
				for(int i = 0;i < topNode.getChildCount();i++){
					expandNode = (DefaultMutableTreeNode) topNode.getChildAt(i);
					String groupName = ((SwitcherGroupNodeEntity)expandNode.getUserObject()).getGroupName();
					if(groupName.contains(groupIP)){
						break;
					}
				}
				tree.expandPath(new TreePath(nodeTreeFactory.getTopRoot()).pathByAddingChild(topNode).pathByAddingChild(expandNode));
			}else if(selected instanceof SwitchTopoNodeLevel3) {
				SwitchTopoNodeLevel3 switcherLevel3Node = (SwitchTopoNodeLevel3)selected;
				if(StringUtils.isBlank(switcherLevel3Node.getIpValue())){
					return;
				}
				this.setSelectedNodeEntity(switcherLevel3Node);
				
				String groupIP = switcherLevel3Node.getIpValue().substring(0, switcherLevel3Node.getIpValue().lastIndexOf("."));
				DefaultMutableTreeNode topNode =  nodeTreeFactory.getSwitchRoot();
				
				for(int i = 0;i < topNode.getChildCount();i++){
					expandNode = (DefaultMutableTreeNode) topNode.getChildAt(i);
					String groupName = ((SwitcherGroupNodeEntity)expandNode.getUserObject()).getGroupName();
					if(groupName.contains(groupIP)){
						break;
					}
				}
				tree.expandPath(new TreePath(nodeTreeFactory.getTopRoot()).pathByAddingChild(topNode).pathByAddingChild(expandNode));
			}else if(selected instanceof VirtualNodeEntity) {
				VirtualNodeEntity virtualNode = (VirtualNodeEntity)selected;
				if(StringUtils.isBlank(virtualNode.getIpValue())){
					return;
				}
				this.setSelectedNodeEntity(virtualNode);
				
				String groupIP = virtualNode.getIpValue().substring(0, virtualNode.getIpValue().lastIndexOf("."));
				DefaultMutableTreeNode topNode =  nodeTreeFactory.getSwitchRoot();
				
				for(int i = 0;i < topNode.getChildCount();i++){
					expandNode = (DefaultMutableTreeNode) topNode.getChildAt(i);
					String groupName = ((SwitcherGroupNodeEntity)expandNode.getUserObject()).getGroupName();
					if(groupName.contains(groupIP)){
						break;
					}
				}
				tree.expandPath(new TreePath(nodeTreeFactory.getTopRoot()).pathByAddingChild(topNode).pathByAddingChild(expandNode));
			}else{
				NodeEntity selectedNode = (NodeEntity)selected;
				for (int row = 0; row < tree.getRowCount(); row++) {
					Object obj = tree.getPathForRow(row).getLastPathComponent();
					if (((DefaultMutableTreeNode)obj).getUserObject() instanceof NodeEntity) {
						NodeEntity diagramNode = (NodeEntity)((DefaultMutableTreeNode)obj).getUserObject();
						if(null != diagramNode.getId()){
							if (selectedNode.getId().equals(diagramNode.getId())) {
								tree.addSelectionPath(tree.getPathForRow(row));
								break;
							}
						}
					}
				}
			}
		}
	}
	
	private class TreeSelectListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if(null != tree.getLastSelectedPathComponent()){
				if (((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getParent() != null
						&& (((DefaultMutableTreeNode)((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getParent()).getUserObject() instanceof NodeEntity)
						&& (((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject() instanceof NodeEntity)) {
					NodeEntity diagramNode = (NodeEntity)((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject();
					equipmentModel.changeSelected(diagramNode);
				}else {
					equipmentModel.clearSelection();
				}
			}
		}
	}

	private JTree tree;
		
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private DefaultMutableTreeNode expandNode;
	
	private NodeEntity selectedNodeEntity = null;
	public NodeEntity getSelectedNodeEntity() {
		return selectedNodeEntity;
	}
	
	public void setSelectedNodeEntity(NodeEntity selectedNodeEntity) {
		this.selectedNodeEntity = selectedNodeEntity;
	}
	
//	private SwitchTopoNodeEntity selectedNodeEntity = null;
//	public SwitchTopoNodeEntity getSelectedNodeEntity() {
//		return selectedNodeEntity;
//	}
//
//	public void setSelectedNodeEntity(SwitchTopoNodeEntity selectedNodeEntity) {
//		this.selectedNodeEntity = selectedNodeEntity;
//	}
		

	private static final Logger LOG = LoggerFactory.getLogger(EquipmentExplorerView.class);
	private static final long serialVersionUID = 1L;
	
	public static final String ID = "equipmentExplorerView";
}