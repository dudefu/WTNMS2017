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
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(VirtualElementExplorerView.ID)
@Scope(Scopes.PROTOTYPE)
public class VirtualElementExplorerView extends ViewPart {

	@SuppressWarnings("unchecked")
	@PostConstruct
	protected void initialize() {
		setTitle("虚拟网元浏览器");
		setViewSize(240, 640);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("虚拟网元"));

		nodeTreeFactory = new NodeTreeFactory(equipmentModel.getDiagramName(), 1,true);
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
		virtualNodeEntityList = (List<VirtualNodeEntity>) remoteServer
				.getService().findAll(VirtualNodeEntity.class);
		int currentUserCode = clientModel.getCurrentUser().getRole().getRoleCode();
		UserEntity currentUserEntity = clientModel.getCurrentUser();
		List<NodeEntity> nodeEntityList = new ArrayList<NodeEntity>();
		//过滤出当前用户管理的虚拟网元
		if(Constants.ADMINCODE == currentUserCode){
			//当前用户为admin时不需要过滤
			for(VirtualNodeEntity virtualNodeEntity : virtualNodeEntityList){
				nodeEntityList.add(virtualNodeEntity);
			}
		}else{
			for(VirtualNodeEntity virtualNodeEntity : virtualNodeEntityList){
				Set<UserEntity> userEntities = virtualNodeEntity.getUsers();
				
				if(null == userEntities || userEntities.size() == 0){
					//
				}else{
					for(UserEntity userEntity : userEntities){
						if(ObjectUtils.equals(currentUserEntity.getId(), userEntity.getId())){
							nodeEntityList.add(virtualNodeEntity);
							break;
						}
					}
				}	
			}
		}
		nodeEntityList = NodeUtils.sortNodeEntity(nodeEntityList);
		String message = "";
		if(nodeEntityList.size() > 0){
			message = String.format("虚拟网元:%s", nodeEntityList.size());
			nodeTreeFactory.addNode(nodeEntityList);
		}else{
			message = ClientUI.getDesktopWindow().DEFAULT_MESSAGE;
		}
		messageLabel.setText(message);
		addTreeSelectionPath(equipmentModel.getLastSelected());
	}
	
	private void addTreeSelectionPath(Object selected) {
		if (selected instanceof VirtualNodeEntity) {
			VirtualNodeEntity switcherNode = (VirtualNodeEntity)selected;
			if(StringUtils.isBlank(switcherNode.getIpValue())){
				return;
			}
			this.setVirtualNodeEntity(switcherNode);
			
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
			if(null != getVirtualNodeEntity()){
				for (int row = 0; row < tree.getRowCount(); row++) {
					Object obj = tree.getPathForRow(row).getLastPathComponent();
					if (((DefaultMutableTreeNode)obj).getUserObject() instanceof VirtualNodeEntity) {
						VirtualNodeEntity diagramNode = (VirtualNodeEntity)((DefaultMutableTreeNode)obj).getUserObject();
						
						if (getVirtualNodeEntity().getGuid().equals(diagramNode.getGuid())) {
							tree.addSelectionPath(tree.getPathForRow(row));
						}
					}
				}
			}
		}
	};
	
	private class TreeSelectListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if(null != tree.getLastSelectedPathComponent()){
				if (((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject() instanceof VirtualNodeEntity) {
					VirtualNodeEntity diagramNode = (VirtualNodeEntity)((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject();
					equipmentModel.changeSelected(diagramNode);
				}else {
					equipmentModel.clearSelection();
				}
			}
		}		
	}	
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private NodeTreeFactory nodeTreeFactory;
	private List<VirtualNodeEntity> virtualNodeEntityList;
	private DefaultMutableTreeNode expandNode;
	
	private TreeSelectListener treeSelectListener = null;
	private VirtualNodeEntity virtualNodeEntity = null;
	public VirtualNodeEntity getVirtualNodeEntity() {
		return virtualNodeEntity;
	}

	public void setVirtualNodeEntity(VirtualNodeEntity virtualNodeEntity) {
		this.virtualNodeEntity = virtualNodeEntity;
	}

	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;

	private JTree tree;
	public static final String ID = "virtualElementExplorerView";
	private static final long serialVersionUID = 1L;
}