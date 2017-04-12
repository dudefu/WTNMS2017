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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DiagramDecorator;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.ui.ClientUI;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(OLTExplorerView.ID)
@Scope(Scopes.PROTOTYPE)
public class OLTExplorerView extends ViewPart {

	@SuppressWarnings("unchecked")
	@PostConstruct
	protected void initialize() {
		setTitle("OLT浏览器");
		setViewSize(240, 640);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("OLT浏览"));

		nodeTreeFactory = new NodeTreeFactory(equipmentModel.getDiagramName(), 1,false);
		tree = nodeTreeFactory.getTreeInstance();
//		tree.addTreeExpansionListener(treeExpansionListener);
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
		eponTopoEntityList = (List<EponTopoEntity>) remoteServer
				.getService().findAll(EponTopoEntity.class);
		int currentUserCode = clientModel.getCurrentUser().getRole().getRoleCode();
		UserEntity currentUserEntity = clientModel.getCurrentUser();
		List<NodeEntity> nodeEntityList = new ArrayList<NodeEntity>();
		//过滤出当前用户管理的OLT
		if(Constants.ADMINCODE == currentUserCode){
			//当前用户为admin时不需要过滤
			for(EponTopoEntity eponTopoEntity : eponTopoEntityList){
				nodeEntityList.add(eponTopoEntity);
			}
		}else{
			for(EponTopoEntity eponTopoEntity : eponTopoEntityList){
				Set<UserEntity> userEntities = eponTopoEntity.getUsers();
				
				if(null == userEntities || userEntities.size() == 0){
					//
				}else{
					for(UserEntity userEntity : userEntities){
						if(ObjectUtils.equals(currentUserEntity.getId(), userEntity.getId())){
							nodeEntityList.add(eponTopoEntity);
							break;
						}
					}
				}	
			}
		}
		nodeEntityList = NodeUtils.sortNodeEntity(nodeEntityList);
		String message = "";
		if(nodeEntityList.size() > 0){
			message = String.format("OLT:%s", nodeEntityList.size());
			nodeTreeFactory.addNode(nodeEntityList);
		}else{
			message = ClientUI.getDesktopWindow().DEFAULT_MESSAGE;
		}
		messageLabel.setText(message);
		addTreeSelectionPath(equipmentModel.getLastSelected());
	}
	
	private void addTreeSelectionPath(Object selected) {
		if(selected instanceof EponTopoEntity){
			EponTopoEntity eponNode = (EponTopoEntity)selected;
			for (int row = 0; row < tree.getRowCount(); row++) {
				Object obj = tree.getPathForRow(row).getLastPathComponent();
				if (obj instanceof DiagramDecorator.Node) {
					DiagramDecorator.Node diagramNode = (DiagramDecorator.Node)obj;
					if (eponNode.equals(diagramNode.getEntity())) {
						tree.addSelectionPath(tree.getPathForRow(row));
					}
				}
			}
		}
	}
	
//	private TreeExpansionListener treeExpansionListener = new TreeExpansionListener(){
//		@Override
//		public void treeCollapsed(TreeExpansionEvent event) {
//			//
//		}
//		@Override
//		public void treeExpanded(TreeExpansionEvent event) {
//			if(null != getEponTopoEntity()){
//				for (int row = 0; row < tree.getRowCount(); row++) {
//					Object obj = tree.getPathForRow(row).getLastPathComponent();
//					if (((DefaultMutableTreeNode)obj).getUserObject() instanceof EponTopoEntity) {
//						EponTopoEntity diagramNode = (EponTopoEntity)((DefaultMutableTreeNode)obj).getUserObject();
//						
//						if (getEponTopoEntity().getGuid().equals(diagramNode.getGuid())) {
//							tree.addSelectionPath(tree.getPathForRow(row));
//						}
//					}
//				}
//			}
//		}
//	};
	
	private class TreeSelectListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if(null != tree.getLastSelectedPathComponent()){
				if (((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject() instanceof EponTopoEntity) {
					EponTopoEntity diagramNode = (EponTopoEntity)((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject();
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
	private List<EponTopoEntity> eponTopoEntityList;
	private TreeSelectListener treeSelectListener = null;

	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;

	private JTree tree;
	public static final String ID = "OLTExplorerView";
	private static final long serialVersionUID = 1L;
}