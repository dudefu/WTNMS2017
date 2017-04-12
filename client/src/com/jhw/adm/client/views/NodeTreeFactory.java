package com.jhw.adm.client.views;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MainMenuConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;

public class NodeTreeFactory {

	private JTree tree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode topRoot;
	
	private DefaultMutableTreeNode switchRoot;
	private DefaultMutableTreeNode fepRoot;
	private DefaultMutableTreeNode eponRoot;
	private DefaultMutableTreeNode subnetRoot;
	
	private ImageRegistry imageRegistry;
	private RemoteServer remoteServer;
	private ClientModel clientModel;
	
	public NodeTreeFactory(){}
	
	public NodeTreeFactory(String treeName){
		imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
		clientModel = ClientUtils.getSpringBean(ClientModel.ID);
		createTreeInstance(treeName);
	}
	
	private void createTreeInstance(String treeName){
		
		topRoot = new DefaultMutableTreeNode(treeName, true);
		
//		switchRoot = new DefaultMutableTreeNode(new SwitchTopoNodeEntity());
		switchRoot = new DefaultMutableTreeNode(new SwitcherGroupNodeEntity());
		fepRoot = new DefaultMutableTreeNode(new FEPTopoNodeEntity());
		eponRoot = new DefaultMutableTreeNode(new EponTopoEntity());
		subnetRoot = new DefaultMutableTreeNode(new SubNetTopoNodeEntity());
		
		topRoot.add(switchRoot);
		topRoot.add(fepRoot);
		topRoot.add(eponRoot);
		topRoot.add(subnetRoot);
		
		model = new DefaultTreeModel(topRoot);
		tree = new JTree(model);
//		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		initPopupMenu();
		tree.addMouseListener(new DetailPopupListener());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setCellRenderer(new TreeNodeRenderer());
		tree.setToggleClickCount(0);
//		tree.setShowsRootHandles(false);
		tree.addTreeWillExpandListener(treeWillExpandListener);
	}
	
	//For switch explorer
	public NodeTreeFactory(String treeName,int nodeNo,boolean isSwitch){
		imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
		createTreeInstance(treeName, nodeNo,isSwitch);
	}

	private  void createTreeInstance(String treeName,int nodeNo,boolean isSwitch){
		topRoot = new DefaultMutableTreeNode(treeName, true);
		if(isSwitch){
			switchRoot = new DefaultMutableTreeNode(new SwitcherGroupNodeEntity());
			topRoot.add(switchRoot);
		}else{
			eponRoot = new DefaultMutableTreeNode(new EponTopoEntity());
			topRoot.add(eponRoot);
		}
		
		model = new DefaultTreeModel(topRoot);
		tree = new JTree(model);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new TreeNodeRenderer());
		tree.setToggleClickCount(0);
		tree.addTreeWillExpandListener(treeWillExpandListener);
	}
	
	private final TreeWillExpandListener treeWillExpandListener = new TreeWillExpandListener() {
		
		@Override
		public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
			expandTree(event);
		}
		
		@Override
		public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
			if((((DefaultMutableTreeNode) event.getPath().getParentPath()
					.getLastPathComponent()).getUserObject() instanceof SwitcherGroupNodeEntity)
					&& ((DefaultMutableTreeNode) event.getPath().getLastPathComponent()).getUserObject() instanceof SwitcherGroupNodeEntity){
				((SwitcherGroupNodeEntity)((DefaultMutableTreeNode)event.getPath().getLastPathComponent()).getUserObject()).setExpand(false);
			}
		}
	};
	
	private void expandTree(TreeExpansionEvent event){
		if ((((DefaultMutableTreeNode) event.getPath().getParentPath()
				.getLastPathComponent()).getUserObject() instanceof SubNetTopoNodeEntity)
				&& ((DefaultMutableTreeNode) event.getPath()
						.getLastPathComponent()).getUserObject() instanceof SubNetTopoNodeEntity) {

			SubNetTopoNodeEntity subNetTopoNodeEntity = (SubNetTopoNodeEntity) ((DefaultMutableTreeNode)event.getPath().getLastPathComponent()).getUserObject();
			List<NodeEntity> nodeEntityList = remoteServer.getNmsService()
					.queryNodeEntity(subNetTopoNodeEntity.getTopDiagramEntity().getId(),
							subNetTopoNodeEntity.getGuid(), clientModel.getCurrentUser());
			groupNodeEntitiesForSubnet = new ArrayList<SwitcherGroupNodeEntity>();
			List<NodeEntity> switchEntity = new ArrayList<NodeEntity>();
			List<NodeEntity> list = new ArrayList<NodeEntity>();
			if(null != nodeEntityList){
				for(NodeEntity nodeEntity : nodeEntityList){
					if(nodeEntity instanceof SwitchTopoNodeEntity || nodeEntity instanceof SwitchTopoNodeLevel3){
						switchEntity.add(nodeEntity);
					}else{
						list.add(nodeEntity);
					}
				}
			}
			switchEntity = NodeUtils.sortNodeEntity(switchEntity);
			list.addAll(switchEntity);
			updateNodeForSubnet(list,(DefaultMutableTreeNode)event.getPath().getLastPathComponent());
		} else if ((((DefaultMutableTreeNode) event.getPath().getParentPath()
				.getLastPathComponent()).getUserObject() instanceof SwitcherGroupNodeEntity)
				&& ((DefaultMutableTreeNode) event.getPath()
						.getLastPathComponent()).getUserObject() instanceof SwitcherGroupNodeEntity) {

			String switchIP = "";
			SwitcherGroupNodeEntity switcherGroupNodeEntity = (SwitcherGroupNodeEntity) ((DefaultMutableTreeNode)event.getPath().getLastPathComponent()).getUserObject(); 
			String groupIP = switcherGroupNodeEntity.getGroupName().substring(0, switcherGroupNodeEntity.getGroupName().lastIndexOf("."));
			List<NodeEntity> nodeEntitieList = new ArrayList<NodeEntity>();
			
			for(NodeEntity nodeEntity : this.nodeEntity){
				if(nodeEntity instanceof SwitchTopoNodeEntity){
					switchIP = ((SwitchTopoNodeEntity)nodeEntity).getIpValue();
					switchIP = switchIP.substring(0, switchIP.lastIndexOf("."));
					if(switchIP.equals(groupIP)){
						nodeEntitieList.add(nodeEntity);
					}
				}else if(nodeEntity instanceof SwitchTopoNodeLevel3){
					switchIP = ((SwitchTopoNodeLevel3)nodeEntity).getIpValue();
					switchIP = switchIP.substring(0, switchIP.lastIndexOf("."));
					if(switchIP.equals(groupIP)){
						nodeEntitieList.add(nodeEntity);
					}
				}else if(nodeEntity instanceof VirtualNodeEntity){
					switchIP = ((VirtualNodeEntity)nodeEntity).getIpValue();
					switchIP = switchIP.substring(0, switchIP.lastIndexOf("."));
					if(switchIP.equals(groupIP)){
						nodeEntitieList.add(nodeEntity);
					}
				}
			}
			nodeEntitieList = NodeUtils.sortNodeEntity(nodeEntitieList);
			((SwitcherGroupNodeEntity)((DefaultMutableTreeNode)event.getPath().getLastPathComponent()).getUserObject()).setExpand(true);
			updateNode(nodeEntitieList, (DefaultMutableTreeNode)event.getPath().getLastPathComponent());
		} else if ((((DefaultMutableTreeNode) event.getPath().getParentPath()
				.getLastPathComponent()).getUserObject() instanceof SubNetTopoNodeEntity)
				&& ((DefaultMutableTreeNode) event.getPath()
						.getLastPathComponent()).getUserObject() instanceof SwitcherGroupNodeEntity) {

			SubNetTopoNodeEntity subNetTopoNodeEntity = (SubNetTopoNodeEntity) ((DefaultMutableTreeNode)event.getPath().getParentPath().getLastPathComponent()).getUserObject();
			
			String groupIPForSubnet = "";
			SwitcherGroupNodeEntity switcherGroupNodeEntity = (SwitcherGroupNodeEntity) ((DefaultMutableTreeNode) event
					.getPath().getLastPathComponent()).getUserObject();
			String groupIP = switcherGroupNodeEntity.getGroupName().substring(0, switcherGroupNodeEntity.getGroupName().lastIndexOf("."));
			List<NodeEntity> nodeEntitieList = new ArrayList<NodeEntity>();
			
			List<NodeEntity> nodeEntityList = remoteServer.getNmsService()
					.queryNodeEntity(subNetTopoNodeEntity.getTopDiagramEntity().getId(),
							subNetTopoNodeEntity.getGuid(), clientModel.getCurrentUser());
			
			for(NodeEntity nodeEntity : nodeEntityList){
				if(nodeEntity instanceof SwitchTopoNodeEntity){
					groupIPForSubnet = ((SwitchTopoNodeEntity)nodeEntity).getIpValue();
					groupIPForSubnet = groupIPForSubnet.substring(0, groupIPForSubnet.lastIndexOf("."));
					if(groupIPForSubnet.equals(groupIP)){
						nodeEntitieList.add(nodeEntity);
					}
				}else if(nodeEntity instanceof SwitchTopoNodeLevel3){
					groupIPForSubnet = ((SwitchTopoNodeLevel3)nodeEntity).getIpValue();
					groupIPForSubnet = groupIPForSubnet.substring(0, groupIPForSubnet.lastIndexOf("."));					
					if(groupIPForSubnet.equals(groupIP)){
						nodeEntitieList.add(nodeEntity);
					}
				}else if(nodeEntity instanceof VirtualNodeEntity){
					groupIPForSubnet = ((VirtualNodeEntity)nodeEntity).getIpValue();
					groupIPForSubnet = groupIPForSubnet.substring(0, groupIPForSubnet.lastIndexOf("."));					
					if(groupIPForSubnet.equals(groupIP)){
						nodeEntitieList.add(nodeEntity);
					}
				}
			}
			nodeEntitieList = NodeUtils.sortNodeEntity(nodeEntitieList);
			updateNode(nodeEntitieList, (DefaultMutableTreeNode)event.getPath().getLastPathComponent());
		}
	}
	
	private void updateNode(List<NodeEntity> nodeEntitys,DefaultMutableTreeNode treeNode){
		int loopNum = treeNode.getChildCount();
		for(int i = 0;i < loopNum;i++){
			model.removeNodeFromParent((MutableTreeNode) treeNode.getChildAt(0));
		}
		if(null != nodeEntitys){
			for(NodeEntity nodeEntity : nodeEntitys) {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(nodeEntity);
				model.insertNodeInto(childNode, treeNode, treeNode.getChildCount());
//				tree.fireTreeExpanded(new TreePath(treeNode));
			}
		}
	}
	
	private List<SwitcherGroupNodeEntity> groupNodeEntitiesForSubnet = new ArrayList<SwitcherGroupNodeEntity>();
	private SwitcherGroupNodeEntity groupNodeEntityForSubnet = null;
	private boolean existsForSubnet = false;
	private void updateNodeForSubnet(List<NodeEntity> nodeEntitys,DefaultMutableTreeNode treeNode){
		int loopNum = treeNode.getChildCount();
		for(int i = 0;i < loopNum;i++){
			model.removeNodeFromParent((MutableTreeNode) treeNode.getChildAt(0));
		}
		if(null != nodeEntitys){
			for(NodeEntity nodeEntity : nodeEntitys) {
				if(nodeEntity instanceof SwitchTopoNodeEntity){
					existsForSubnet = false;
					String groupIP = ((SwitchTopoNodeEntity) nodeEntity)
							.getIpValue().substring(0,((SwitchTopoNodeEntity) nodeEntity)
											.getIpValue().lastIndexOf(".") + 1)+ "*";
					for(SwitcherGroupNodeEntity switcherGroupNodeEntity : groupNodeEntitiesForSubnet){
						if(groupIP.equals(switcherGroupNodeEntity.getGroupName())){
							existsForSubnet = true;
							break;
						}
					}
					if(!existsForSubnet){
						groupNodeEntityForSubnet = new SwitcherGroupNodeEntity();
						groupNodeEntityForSubnet.setGroupName(groupIP);
						groupNodeEntitiesForSubnet.add(groupNodeEntityForSubnet);
						DefaultMutableTreeNode treeChileNode = new DefaultMutableTreeNode(groupNodeEntityForSubnet);
						model.insertNodeInto(treeChileNode, treeNode, treeNode.getChildCount());
						treeChileNode.add(new DefaultMutableTreeNode("*"));
					}
					continue;
				}
				if(nodeEntity instanceof SwitchTopoNodeLevel3){
					existsForSubnet = false;
					String groupIP = ((SwitchTopoNodeLevel3) nodeEntity)
							.getIpValue().substring(0,((SwitchTopoNodeLevel3) nodeEntity)
											.getIpValue().lastIndexOf(".") + 1)+ "*";
					for(SwitcherGroupNodeEntity switcherGroupNodeEntity : groupNodeEntitiesForSubnet){
						if(groupIP.equals(switcherGroupNodeEntity.getGroupName())){
							existsForSubnet = true;
							break;
						}
					}
					if(!existsForSubnet){
						groupNodeEntityForSubnet = new SwitcherGroupNodeEntity();
						groupNodeEntityForSubnet.setGroupName(groupIP);
						groupNodeEntitiesForSubnet.add(groupNodeEntityForSubnet);
						DefaultMutableTreeNode treeChileNode = new DefaultMutableTreeNode(groupNodeEntityForSubnet);
						model.insertNodeInto(treeChileNode, treeNode, treeNode.getChildCount());
						treeChileNode.add(new DefaultMutableTreeNode("*"));
					}
					continue;
				}
				if(nodeEntity instanceof VirtualNodeEntity){
					existsForSubnet = false;
					String groupIP = ((VirtualNodeEntity) nodeEntity)
							.getIpValue().substring(0,((VirtualNodeEntity) nodeEntity)
											.getIpValue().lastIndexOf(".") + 1)+ "*";
					for(SwitcherGroupNodeEntity switcherGroupNodeEntity : groupNodeEntitiesForSubnet){
						if(groupIP.equals(switcherGroupNodeEntity.getGroupName())){
							existsForSubnet = true;
							break;
						}
					}
					if(!existsForSubnet){
						groupNodeEntityForSubnet = new SwitcherGroupNodeEntity();
						groupNodeEntityForSubnet.setGroupName(groupIP);
						groupNodeEntitiesForSubnet.add(groupNodeEntityForSubnet);
						DefaultMutableTreeNode treeChileNode = new DefaultMutableTreeNode(groupNodeEntityForSubnet);
						model.insertNodeInto(treeChileNode, treeNode, treeNode.getChildCount());
						treeChileNode.add(new DefaultMutableTreeNode("*"));
					}
					continue;
				}
				
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(nodeEntity);
				model.insertNodeInto(childNode, treeNode, treeNode.getChildCount());
				if (nodeEntity instanceof SubNetTopoNodeEntity) {
					childNode.add(new DefaultMutableTreeNode("*"));
				}
			}
		}
	}
	
	private List<SwitcherGroupNodeEntity> groupNodeEntities = new ArrayList<SwitcherGroupNodeEntity>();
	private SwitcherGroupNodeEntity groupNodeEntity = null;
	private boolean exists = false;
	private List<NodeEntity> nodeEntity = new ArrayList<NodeEntity>();
	private DefaultMutableTreeNode node = null;
	private void updateNode(List<NodeEntity> nodeEntitys){
		for(NodeEntity nodeEntity : nodeEntitys){
			this.deleteNodeEntitieList.remove(nodeEntity);
			this.nodeEntity.add(nodeEntity);
			//添加交换机分组
			if(nodeEntity instanceof SwitchTopoNodeEntity){
				exists = false;
				if(StringUtils.isBlank(((SwitchTopoNodeEntity)nodeEntity).getIpValue())){
					break;
				}
				String groupIP = ((SwitchTopoNodeEntity)nodeEntity).getIpValue().substring(0, ((SwitchTopoNodeEntity)nodeEntity).getIpValue().lastIndexOf(".") + 1) + "*";
				for(SwitcherGroupNodeEntity switcherGroupNodeEntity : groupNodeEntities){
					if(groupIP.equals(switcherGroupNodeEntity.getGroupName())){
						exists = true;
						break;
					}
				}
				if(!exists){
					groupNodeEntity = new SwitcherGroupNodeEntity();
					groupNodeEntity.setGroupName(groupIP);
					groupNodeEntities.add(groupNodeEntity);
					DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(groupNodeEntity);
					model.insertNodeInto(treeNode, switchRoot, switchRoot.getChildCount());
					treeNode.add(new DefaultMutableTreeNode("*"));
				}else if(exists){
					Enumeration breadthFirst = switchRoot.children();
					while(breadthFirst.hasMoreElements()){
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)breadthFirst.nextElement();
						if(groupIP.equals(((SwitcherGroupNodeEntity)node.getUserObject()).getGroupName())){
							this.node = node;
							break;
						}
					}
					if(((SwitcherGroupNodeEntity)this.node.getUserObject()).isExpand()){
						DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(nodeEntity);
						model.insertNodeInto(treeNode, this.node, this.node.getChildCount());
					}
				}
			}
			
			if(nodeEntity instanceof SwitchTopoNodeLevel3){
				exists = false;
				String groupIP = ((SwitchTopoNodeLevel3)nodeEntity).getIpValue().substring(0, ((SwitchTopoNodeLevel3)nodeEntity).getIpValue().lastIndexOf(".") + 1) + "*";
				for(SwitcherGroupNodeEntity switcherGroupNodeEntity : groupNodeEntities){
					if(groupIP.equals(switcherGroupNodeEntity.getGroupName())){
						exists = true;
						break;
					}
				}
				if(!exists){
					groupNodeEntity = new SwitcherGroupNodeEntity();
					groupNodeEntity.setGroupName(groupIP);
					groupNodeEntities.add(groupNodeEntity);
					DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(groupNodeEntity);
					model.insertNodeInto(treeNode, switchRoot, switchRoot.getChildCount());
					treeNode.add(new DefaultMutableTreeNode("*"));
				}else if(exists){
					Enumeration breadthFirst = switchRoot.children();
					while(breadthFirst.hasMoreElements()){
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)breadthFirst.nextElement();
						if(groupIP.equals(((SwitcherGroupNodeEntity)node.getUserObject()).getGroupName())){
							this.node = node;
							break;
						}
					}
					if(((SwitcherGroupNodeEntity)this.node.getUserObject()).isExpand()){
						DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(nodeEntity);
						model.insertNodeInto(treeNode, this.node, this.node.getChildCount());
					}
				}
			}
			
			if(nodeEntity instanceof VirtualNodeEntity){
				exists = false;
				if(StringUtils.isBlank(((VirtualNodeEntity)nodeEntity).getIpValue())){
					break;
				}
				String groupIP = ((VirtualNodeEntity)nodeEntity).getIpValue().substring(0, ((VirtualNodeEntity)nodeEntity).getIpValue().lastIndexOf(".") + 1) + "*";
				for(SwitcherGroupNodeEntity switcherGroupNodeEntity : groupNodeEntities){
					if(groupIP.equals(switcherGroupNodeEntity.getGroupName())){
						exists = true;
						break;
					}
				}
				if(!exists){
					groupNodeEntity = new SwitcherGroupNodeEntity();
					groupNodeEntity.setGroupName(groupIP);
					groupNodeEntities.add(groupNodeEntity);
					DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(groupNodeEntity);
					model.insertNodeInto(treeNode, switchRoot, switchRoot.getChildCount());
					treeNode.add(new DefaultMutableTreeNode("*"));
				}else if(exists){
					Enumeration breadthFirst = switchRoot.children();
					while(breadthFirst.hasMoreElements()){
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)breadthFirst.nextElement();
						if(groupIP.equals(((SwitcherGroupNodeEntity)node.getUserObject()).getGroupName())){
							this.node = node;
							break;
						}
					}
					if(((SwitcherGroupNodeEntity)this.node.getUserObject()).isExpand()){
						DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(nodeEntity);
						model.insertNodeInto(treeNode, this.node, this.node.getChildCount());
					}
				}
			}
			
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(nodeEntity);
			
			if(nodeEntity instanceof FEPTopoNodeEntity){
				model.insertNodeInto(treeNode, fepRoot, fepRoot.getChildCount());
				tree.fireTreeExpanded(new TreePath(fepRoot));
			}
			if(nodeEntity instanceof EponTopoEntity){
				model.insertNodeInto(treeNode, eponRoot, eponRoot.getChildCount());
				tree.fireTreeExpanded(new TreePath(eponRoot));
			}
			if(nodeEntity instanceof SubNetTopoNodeEntity){
				model.insertNodeInto(treeNode, subnetRoot, subnetRoot.getChildCount());
				treeNode.add(new DefaultMutableTreeNode("*"));
				tree.fireTreeExpanded(new TreePath(subnetRoot));
			}
			if(nodeEntity instanceof ONUTopoNodeEntity){
				model.insertNodeInto(treeNode, eponRoot, eponRoot.getChildCount());
				tree.fireTreeExpanded(new TreePath(eponRoot));
			}
		}
		
		if((null != switchRoot)){
			tree.expandPath(new TreePath(topRoot).pathByAddingChild(switchRoot));
		}
		if(null != fepRoot){
			tree.expandPath(new TreePath(topRoot).pathByAddingChild(fepRoot));
		}
		if(null != eponRoot){
			tree.expandPath(new TreePath(topRoot).pathByAddingChild(eponRoot));
		}
		if(null != subnetRoot){
			tree.expandPath(new TreePath(topRoot).pathByAddingChild(subnetRoot));
		}
	}
	
	//展开(关闭)一个树
	@SuppressWarnings("unchecked")
	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		
//		if(node.getParent() != subnetRoot && node.)
		if (node.getChildCount() > 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode treeNode = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(treeNode);
				expandAll(tree, path, expand);
			}
		}
		if (expand) {
//			if(parent.getLastPathComponent() instanceof SubNetTopoNodeEntity){
//				//不操作
//			}else{
			tree.expandPath(parent);
//			}
		} else {
			tree.collapsePath(parent);
		}
	}
	
	public boolean selectedNode(){
		boolean selected = true;
		if(null == tree.getSelectionPaths()){
			selected = false;
		}
		return selected;
	}

	public List<NodeEntity> getNodeInstance(){
		List<NodeEntity> nodeEntityList = new ArrayList<NodeEntity>();
		if(null != tree.getSelectionPaths()){
			for(TreePath treePath : tree.getSelectionPaths()){
				if (((DefaultMutableTreeNode) treePath.getLastPathComponent()) != null
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != null
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != topRoot
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != switchRoot){
					nodeEntityList.add((NodeEntity) (((DefaultMutableTreeNode) treePath
							.getLastPathComponent()).getUserObject()));
				}
			}
		}
		return nodeEntityList;
	}
	
	public boolean compare(SubNetTopoNodeEntity currentSubnetNode){
		boolean canAdd = true;
		List<SubNetTopoNodeEntity> subnetEntityList = new ArrayList<SubNetTopoNodeEntity>();
		if(null != tree.getSelectionPaths()){
			for(TreePath treePath : tree.getSelectionPaths()){
				if(((DefaultMutableTreeNode) treePath.getLastPathComponent()) != null
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != null
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != topRoot
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject() instanceof SubNetTopoNodeEntity){
					subnetEntityList.add((SubNetTopoNodeEntity) (((DefaultMutableTreeNode) treePath
							.getLastPathComponent()).getUserObject()));
				}
			}
		}
		for(SubNetTopoNodeEntity subNetTopoNodeEntity : subnetEntityList){
			if(recursionCompare(currentSubnetNode,subNetTopoNodeEntity)){
				canAdd = false;
				break;
			}
		}
		return canAdd;
	}
	
	private boolean isInclude = false;
	@SuppressWarnings("unchecked")
	private boolean recursionCompare(SubNetTopoNodeEntity currentSubnetNode,SubNetTopoNodeEntity selectedSubnetNode){
		
		String currentGuid = currentSubnetNode.getGuid();
		String selectedGuid = selectedSubnetNode.getGuid();
		
		if(currentGuid.equals(selectedGuid)){
			isInclude = true;
		}else{
			Object[] guid = {currentSubnetNode.getParentNode()};
			if(StringUtils.isBlank((String) guid[0])){
				isInclude = false;
			}else{
				String where = " where entity.guid = ?";
				List<SubNetTopoNodeEntity> topoNodeEntity = (List<SubNetTopoNodeEntity>) remoteServer.getService().findAll(SubNetTopoNodeEntity.class, where, guid);
				recursionCompare(topoNodeEntity.get(0),selectedSubnetNode);
			}
		}
		return isInclude;
	}
	
	private final List<NodeEntity> deleteNodeEntitieList = new ArrayList<NodeEntity>();
	public void deleteNode(boolean isLeft){
		if(null != tree.getSelectionPaths()){
			for(TreePath treePath : tree.getSelectionPaths()){
				if (((DefaultMutableTreeNode) treePath.getLastPathComponent()) != null
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != null
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != topRoot
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != switchRoot){
					if(isLeft){
						deleteNodeEntitieList.add((NodeEntity)(((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject()));
					}
					this.nodeEntity.remove((((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject()));
					model.removeNodeFromParent(((DefaultMutableTreeNode) treePath.getLastPathComponent()));
				}
			}
		}
	}
	
	public void  deleteNode(){
		if(null != tree.getSelectionPaths()){
			for(TreePath treePath : tree.getSelectionPaths()){
				if (((DefaultMutableTreeNode) treePath.getLastPathComponent()) != null
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != null
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != topRoot
						&& ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getParent() != switchRoot){
					this.nodeEntity.remove((((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject()));
					model.removeNodeFromParent(((DefaultMutableTreeNode) treePath.getLastPathComponent()));
				}
			}
		}
	}
	
	public void addNode(List<NodeEntity> nodeEntitys){
		List<NodeEntity> nodeEntityList = new ArrayList<NodeEntity>();
		for(NodeEntity nodeEntity : nodeEntitys){
			if(nodeEntity instanceof SwitchTopoNodeEntity){
				if(!StringUtils.isBlank(((SwitchTopoNodeEntity)nodeEntity).getIpValue())){
					nodeEntityList.add(nodeEntity);
				}
			}else if(nodeEntity instanceof SubNetTopoNodeEntity){
				if(!StringUtils.isBlank(((SubNetTopoNodeEntity)nodeEntity).getName())){
					nodeEntityList.add(nodeEntity);
				}
			}else if(nodeEntity instanceof FEPTopoNodeEntity){
				if(!StringUtils.isBlank(((FEPTopoNodeEntity)nodeEntity).getIpValue())){
					nodeEntityList.add(nodeEntity);
				}
			}else if(nodeEntity instanceof SwitchTopoNodeLevel3){
				if(!StringUtils.isBlank(((SwitchTopoNodeLevel3)nodeEntity).getIpValue())){
					nodeEntityList.add(nodeEntity);
				}
			}else if(nodeEntity instanceof VirtualNodeEntity){
				if(!StringUtils.isBlank(((VirtualNodeEntity)nodeEntity).getIpValue())){
					nodeEntityList.add(nodeEntity);
				}
			}else{
				nodeEntityList.add(nodeEntity);
			}
		}
		updateNode(nodeEntityList);
	}
	
	public void cleanTreeNode(){
		cleanTreeNode(switchRoot.getPath());
		cleanTreeNode(fepRoot.getPath());
		cleanTreeNode(eponRoot.getPath());
		cleanTreeNode(subnetRoot.getPath());
		this.nodeEntity = new ArrayList<NodeEntity>();
		this.groupNodeEntities = new ArrayList<SwitcherGroupNodeEntity>();
	}
	
	private void cleanTreeNode(TreeNode[] node){
		for(int i = 0;i < node.length;i++){
			if(null != node[i].getParent()){
				int loopNum = node[i].getChildCount();
				for(int j = 0;j < loopNum;j++){
					model.removeNodeFromParent((MutableTreeNode) node[i].getChildAt(0));
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean allowAddNodes(List<NodeEntity> nodeEntitys) {
		boolean allow = true;
		
		for(NodeEntity nodeEntity : nodeEntitys){
			if(nodeEntity instanceof SwitchTopoNodeEntity){//交换机
				String ipAddr = "";
				SwitchTopoNodeEntity switchTopoNodeEntity = (SwitchTopoNodeEntity)nodeEntity;
				String newIP = switchTopoNodeEntity.getIpValue();
				for(NodeEntity oldNodeEntity : this.nodeEntity){
					if(oldNodeEntity instanceof SwitchTopoNodeEntity){
						ipAddr = ((SwitchTopoNodeEntity)oldNodeEntity).getIpValue();
					}else if(oldNodeEntity instanceof SwitchTopoNodeLevel3){
						ipAddr = ((SwitchTopoNodeLevel3)oldNodeEntity).getIpValue();
					}
					if (ipAddr.equals(newIP)){
						allow = false;
						return allow;
					}
				}
			}
			if(nodeEntity instanceof SwitchTopoNodeLevel3){//三层交换机
				String ipAddr = "";
				SwitchTopoNodeLevel3 switchTopoNodeLevel3 = (SwitchTopoNodeLevel3)nodeEntity;
				String newIP = switchTopoNodeLevel3.getIpValue();
				for(NodeEntity oldNodeEntity : this.nodeEntity){
					if(oldNodeEntity instanceof SwitchTopoNodeEntity){
						ipAddr = ((SwitchTopoNodeEntity)oldNodeEntity).getIpValue();
					}else if(oldNodeEntity instanceof SwitchTopoNodeLevel3){
						ipAddr = ((SwitchTopoNodeLevel3)oldNodeEntity).getIpValue();
					}
					if (ipAddr.equals(newIP)){
						allow = false;
						return allow;
					}
				}
			}
			if(nodeEntity instanceof FEPTopoNodeEntity){//前置机
				String code = "";
				FEPTopoNodeEntity fepTopoNodeEntity = (FEPTopoNodeEntity)nodeEntity;
				String newCode = fepTopoNodeEntity.getCode();
				Enumeration breadthFirst = fepRoot.children();
				while(breadthFirst.hasMoreElements()){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)breadthFirst.nextElement();
					FEPTopoNodeEntity object = (FEPTopoNodeEntity)node.getUserObject();
					code = object.getCode();
					if(code.equals(newCode)){
						allow = false;
						return allow;
					}
				}
			}
			if(nodeEntity instanceof EponTopoEntity){//OLT
				String ipValue = "";
				EponTopoEntity oltTopoEntity = (EponTopoEntity)nodeEntity;
				String newIpVlaue = oltTopoEntity.getIpValue();
				Enumeration breadthFirst = eponRoot.children();
				while(breadthFirst.hasMoreElements()){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)breadthFirst.nextElement();
					if(node.getUserObject() instanceof EponTopoEntity){
						EponTopoEntity object = (EponTopoEntity)node.getUserObject();
						ipValue = object.getIpValue();
						if(ipValue.equals(newIpVlaue)){
							allow = false;
							return allow;
						}
					}
				}
			}
			if(nodeEntity instanceof ONUTopoNodeEntity){//ONU
				String macValue = "";
				ONUTopoNodeEntity onuTopoNodeEntity = (ONUTopoNodeEntity)nodeEntity;
				String newMacVlaue = onuTopoNodeEntity.getMacValue();
				Enumeration breadthFirst = eponRoot.children();
				while(breadthFirst.hasMoreElements()){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)breadthFirst.nextElement();
					if(node.getUserObject() instanceof ONUTopoNodeEntity){
						ONUTopoNodeEntity object = (ONUTopoNodeEntity)node.getUserObject();
						macValue = object.getMacValue();
						if(macValue.equals(newMacVlaue)){
							allow = false;
							return allow;
						}
					}
				}
			}
		}
		return allow;
	}
	
	private JPopupMenu popup = null;
	private void initPopupMenu(){
		popup = new JPopupMenu();
		JMenuItem  locationMenuItem = new JMenuItem ("定位");
		popup.add(locationMenuItem);
		
		Action locationAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NodeEntity object = (NodeEntity) ((DefaultMutableTreeNode)(tree.getSelectionPath().getLastPathComponent())).getUserObject();
				if(object instanceof NodeEntity){
					if(object.getId() == null){
						//
					}else{
						NodeUtils.fixedPositionEquipment((object));
					}
				}
			}
		};
		locationMenuItem.addActionListener(locationAction);
	}

	class DetailPopupListener extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			processEvent(e);
		}
		
	    @Override
		public void mouseReleased(MouseEvent e) {
	    	processEvent(e);
	        if(SwingUtilities.isRightMouseButton(e)){
	            popup.show(e.getComponent(),e.getX(),e.getY());
	        }
	    }
	    
	    private void processEvent(MouseEvent e) {
	    	if (SwingUtilities.isRightMouseButton(e)) {
				MouseEvent ne = new MouseEvent(e.getComponent(), e.getID(), 
						e.getWhen(), MouseEvent.BUTTON1_MASK, e.getX(),
						e.getY(), e.getClickCount(), false);
				tree.dispatchEvent(ne);
			}
		}
	}
	
	public List<NodeEntity> getAllNodesData() {
		return this.nodeEntity;
	}
	
	public List<NodeEntity> getDeleteList(){
		return deleteNodeEntitieList;
	}
	
	public DefaultMutableTreeNode getTopRoot(){
		return topRoot;
	}
	
	public DefaultMutableTreeNode getSwitchRoot(){
		return switchRoot;
	}
	
	public DefaultMutableTreeNode getEponRoot(){
		return eponRoot;
	}
	
	public JTree getTreeInstance(){
		return tree;
	}
	
	//树渲染器
	private class TreeNodeRenderer extends DefaultTreeCellRenderer{
	    private static final long serialVersionUID = 8532405600839140757L;

	    private final Icon subnetTypeIcon = imageRegistry.getImageIcon(MainMenuConstants.SUBNET_TYPE);
	    private final Icon switchLayerTypeIcon = imageRegistry.getImageIcon(MainMenuConstants.SWITCHER_TYPE);
	    private final Icon fepTypeIcon = imageRegistry.getImageIcon(MainMenuConstants.FEP_TYPE);
	    private final Icon eponTypeIcon = imageRegistry.getImageIcon(MainMenuConstants.EPON_TYPE);
	    
	    private final Icon switchLayer2Icon = imageRegistry.getImageIcon(MainMenuConstants.SWITCHER_LAYER2);
	    private final Icon switchLayer3Icon = imageRegistry.getImageIcon(MainMenuConstants.SWITCHER_LAYER3);
	    private final Icon fepLayerIcon = imageRegistry.getImageIcon(MainMenuConstants.FRONT_END);
	    private final Icon oltLayerIcon = imageRegistry.getImageIcon(MainMenuConstants.OLT);
	    private final Icon onuLayerIcon = imageRegistry.getImageIcon(MainMenuConstants.ONU_MANAGEMENT);
	    private final Icon subnetIcon = imageRegistry.getImageIcon(MainMenuConstants.SUBNET);
	    private final Icon virtualElementIcon = imageRegistry.getImageIcon(MainMenuConstants.VIRTUAL_ELEMENT);
	    private final Icon groupIcon = imageRegistry.getImageIcon(MainMenuConstants.GROUP);
	    
	    @Override
		public java.awt.Component getTreeCellRendererComponent(JTree tree,
	                                                  Object value,
	                                                  boolean sel,
	                                                  boolean expanded,
	                                                  boolean leaf,
	                                                  int row,
	                                                  boolean hasFocus){
	        super.getTreeCellRendererComponent(tree, value,sel,expanded,leaf,row,hasFocus);   
	        // 取得节点
	        DefaultMutableTreeNode node=(DefaultMutableTreeNode)value;
	        
	        // 取得路径
	        TreeNode[] paths = node.getPath();
	        
	        // 按路径层次赋予不同的图标和文本信息
	        if(paths.length == 1){
	            setIcon(subnetTypeIcon);
	        }else if (paths.length == 2) {            
	            NodeEntity object = (NodeEntity)((DefaultMutableTreeNode)value).getUserObject();
				if(object instanceof FEPTopoNodeEntity){
					setIcon(fepTypeIcon);
					this.setText("前置机");
				}else if(object instanceof SwitchTopoNodeEntity){
					setIcon(switchLayerTypeIcon);
					this.setText("交换机");
				}else if(object instanceof SwitchTopoNodeLevel3){
					setIcon(switchLayerTypeIcon);
					this.setText("交换机");
				}else if(object instanceof EponTopoEntity){
					setIcon(eponTypeIcon);
					this.setText("EPON");
				}else if(object instanceof SubNetTopoNodeEntity){
					setIcon(subnetTypeIcon);
					this.setText("子网");
				}else if(object instanceof SwitcherGroupNodeEntity){
					setIcon(switchLayerTypeIcon);
					this.setText("交换机");
				}else if(object instanceof VirtualNodeEntity){
					setIcon(switchLayerTypeIcon);
					this.setText("虚拟网元");
				}
	        }else if (paths.length > 2) {            
	            NodeEntity object = (NodeEntity)((DefaultMutableTreeNode)value).getUserObject();
	            if(object instanceof FEPTopoNodeEntity){
					setIcon(fepLayerIcon);
					this.setText(((FEPTopoNodeEntity)object).getIpValue());
				}else if(object instanceof SwitchTopoNodeEntity){
					setIcon(switchLayer2Icon);
					this.setText(((SwitchTopoNodeEntity)object).getIpValue());
				}else if(object instanceof SwitchTopoNodeLevel3){
					setIcon(switchLayer3Icon);
					this.setText(((SwitchTopoNodeLevel3)object).getIpValue());
				}else if(object instanceof EponTopoEntity){
					setIcon(oltLayerIcon);
					this.setText(((EponTopoEntity)object).getIpValue());
				}else if(object instanceof ONUTopoNodeEntity){
					setIcon(onuLayerIcon);
					this.setText(((ONUTopoNodeEntity)object).getMacValue());
				}else if(object instanceof SubNetTopoNodeEntity){
					setIcon(subnetIcon);
					this.setText(((SubNetTopoNodeEntity)object).getName());
				} else if(object instanceof SwitcherGroupNodeEntity){
					setIcon(groupIcon);
					this.setText(((SwitcherGroupNodeEntity)object).getGroupName());
				}else if(object instanceof VirtualNodeEntity){
					setIcon(virtualElementIcon);
					this.setText(((VirtualNodeEntity)object).getIpValue());
				}
	        }
	        return this;
	    }
	}
	
	public class SwitcherGroupNodeEntity extends NodeEntity{
		
		private static final long serialVersionUID = 1L;
		private String groupName;
		private boolean isExpand = false;
		
		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public boolean isExpand() {
			return isExpand;
		}

		public void setExpand(boolean isExpand) {
			this.isExpand = isExpand;
		}
	}
}