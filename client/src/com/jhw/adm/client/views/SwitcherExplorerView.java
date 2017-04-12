package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.ui.ClientUI;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.NodeTreeFactory.SwitcherGroupNodeEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(SwitcherExplorerView.ID)
@Scope(Scopes.PROTOTYPE)
public class SwitcherExplorerView extends ViewPart {

	@SuppressWarnings("unchecked")
	@PostConstruct
	protected void initialize() {
		setTitle("交换机浏览器");
		setViewSize(240, 640);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("交换机浏览"));

		nodeTreeFactory = new NodeTreeFactory(equipmentModel.getDiagramName(), 1, true);
		tree = nodeTreeFactory.getTreeInstance();
		tree.addTreeExpansionListener(treeExpansionListener);
		treeSelectListener =  new TreeSelectListener();
		tree.addTreeSelectionListener(treeSelectListener);
		tree.setRootVisible(false);
		JScrollPane scrollTreeView = new JScrollPane(tree);
		add(scrollTreeView, BorderLayout.CENTER);
		
		JPanel nodeNoInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel messageLabel = new JLabel();
		nodeNoInfoPanel.add(messageLabel);
		add(nodeNoInfoPanel, BorderLayout.PAGE_END);
		
		//initianlize tree value
		switchTopoNodeEntityLists = (List<SwitchTopoNodeEntity>) remoteServer
				.getService().findAll(SwitchTopoNodeEntity.class);
		int currentUserCode = clientModel.getCurrentUser().getRole().getRoleCode();
		UserEntity currentUserEntity = clientModel.getCurrentUser();
		List<NodeEntity> nodeEntityList = new ArrayList<NodeEntity>();
		
		//过滤出当前用户管理的交换机
		if(Constants.ADMINCODE == currentUserCode){
			//当前用户为admin时不需要过滤
			for(SwitchTopoNodeEntity switchTopoNodeEntity : switchTopoNodeEntityLists){
				nodeEntityList.add(switchTopoNodeEntity);
			}
		}else{
			for(SwitchTopoNodeEntity switchTopoNodeEntity : switchTopoNodeEntityLists){
				Set<UserEntity> userEntities = switchTopoNodeEntity.getUsers();
				
				if(null == userEntities || userEntities.size() == 0){
//					nodeEntityList.add(switchTopoNodeEntity);
				}else{
					for(UserEntity userEntity : userEntities){
						if(ObjectUtils.equals(currentUserEntity.getId(), userEntity.getId())){
							nodeEntityList.add(switchTopoNodeEntity);
							break;
						}
					}
				}	
			}
		}
		nodeEntityList = NodeUtils.sortNodeEntity(nodeEntityList);
		String message = "";
		if(nodeEntityList.size() > 0){
			message = String.format("二层交换机:%s", nodeEntityList.size());
			nodeTreeFactory.addNode(nodeEntityList);
		}else{
			message = ClientUI.getDesktopWindow().DEFAULT_MESSAGE;
		}
		messageLabel.setText(message);
		addTreeSelectionPath(equipmentModel.getLastSelected());
	}
	
	private void addTreeSelectionPath(Object selected) {
		if (selected instanceof SwitchTopoNodeEntity) {
			SwitchTopoNodeEntity switcherNode = (SwitchTopoNodeEntity)selected;
			if(StringUtils.isBlank(switcherNode.getIpValue())){
				return;
			}
			this.setSelectedNodeEntity(switcherNode);
			
			String groupIP = switcherNode.getIpValue().substring(0, switcherNode.getIpValue().lastIndexOf("."));
			DefaultMutableTreeNode topNode =  nodeTreeFactory.getSwitchRoot();
			
			if(topNode.getChildCount() > 0){
				for(int i = 0;i < topNode.getChildCount();i++){
					expandNode = (DefaultMutableTreeNode) topNode.getChildAt(i);
					String groupName = ((SwitcherGroupNodeEntity)expandNode.getUserObject()).getGroupName();
					if(groupName.contains(groupIP)){
						break;
					}
				}
				tree.expandPath(new TreePath(nodeTreeFactory.getTopRoot()).pathByAddingChild(topNode).pathByAddingChild(expandNode));
			}
		}
	}
	
	private TreeExpansionListener treeExpansionListener = new TreeExpansionListener(){
		@Override
		public void treeCollapsed(TreeExpansionEvent event) {
			//
		}
		@Override
		public void treeExpanded(TreeExpansionEvent event) {
			if(isRemove() == true){
				//移除不需要显示的节点
				needToRemoveSwitchTopoNode();
				equipmentModel.clearSelection();
			}

			if(null != getSelectedNodeEntity()){
				for (int row = 0; row < tree.getRowCount(); row++) {
					Object obj = tree.getPathForRow(row).getLastPathComponent();
					if (((DefaultMutableTreeNode)obj).getUserObject() instanceof SwitchTopoNodeEntity) {
						SwitchTopoNodeEntity diagramNode = (SwitchTopoNodeEntity)((DefaultMutableTreeNode)obj).getUserObject();
						
						if (getSelectedNodeEntity().getGuid().equals(diagramNode.getGuid())) {
							tree.addSelectionPath(tree.getPathForRow(row));
						}
					}
				}
			}
		}
	};
	
	/**
	 * 移除不需要显示的节点
	 */
	public void needToRemoveSwitchTopoNode(){
		// 通过ip地址删除不需要显示的节点
		for (int row = (tree.getRowCount()-1); row >= 0; row--){
			Object obj = tree.getPathForRow(row).getLastPathComponent();
			if (((DefaultMutableTreeNode)obj).getUserObject() instanceof SwitchTopoNodeEntity) {
				SwitchTopoNodeEntity diagramNode = (SwitchTopoNodeEntity)((DefaultMutableTreeNode)obj).getUserObject();
				if (null != removedNodeEntityList){
					for (SwitchTopoNodeEntity switchTopoNodeEntity : removedNodeEntityList){
						if (switchTopoNodeEntity != null && 
							switchTopoNodeEntity.getIpValue().equals(diagramNode.getIpValue())) {
							tree.addSelectionPath(tree.getPathForRow(row));
							nodeTreeFactory.deleteNode();
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
				if (((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject() instanceof SwitchTopoNodeEntity) {
					SwitchTopoNodeEntity diagramNode = (SwitchTopoNodeEntity)((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject();
					equipmentModel.changeSelected(diagramNode);
				}else {
					equipmentModel.clearSelection();
				}
			}
		}		
	}	
	
	public void addNode(List<NodeEntity> nodeEntitieList){
		for(NodeEntity nodeEntity : nodeTreeFactory.getAllNodesData()){
			if (((SwitchTopoNodeEntity) nodeEntitieList.get(0)).getIpValue()
					.equals(((SwitchTopoNodeEntity) nodeEntity).getIpValue())) {// 不存在的就添加
				nodeEntitieList.remove(0);
				break;
			}
		}
		nodeTreeFactory.addNode(nodeEntitieList);
	}
	
	public void deleteNode(){
		nodeTreeFactory.deleteNode();
	}

	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private NodeTreeFactory nodeTreeFactory;
	private List<SwitchTopoNodeEntity> switchTopoNodeEntityLists;
	private DefaultMutableTreeNode expandNode;
	
	private SwitchTopoNodeEntity selectedNodeEntity = null;
	private TreeSelectListener treeSelectListener = null;
	private boolean isRemove = false;
	
	public boolean isRemove() {
		return isRemove;
	}

	public void setRemove(boolean isRemove) {
		this.isRemove = isRemove;
	}

	//不需要显示的节点
	private List<SwitchTopoNodeEntity> removedNodeEntityList;
	
	public SwitchTopoNodeEntity getSelectedNodeEntity() {
		return selectedNodeEntity;
	}

	public void setSelectedNodeEntity(SwitchTopoNodeEntity selectedNodeEntity) {
		this.selectedNodeEntity = selectedNodeEntity;
	}
	
	public List<SwitchTopoNodeEntity> getRemovedNodeEntityList() {
		return removedNodeEntityList;
	}

	public void setRemovedNodeEntityList(
			List<SwitchTopoNodeEntity> removedNodeEntityList) {
		this.removedNodeEntityList = removedNodeEntityList;
	}
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;

	private JTree tree;
	public static final String ID = "switcherExplorerView";
	private static final long serialVersionUID = 1L;
}