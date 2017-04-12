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
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;

@Component(SwitchLayer3ExplorerView.ID)
@Scope(Scopes.PROTOTYPE)
public class SwitchLayer3ExplorerView extends ViewPart {

	@SuppressWarnings("unchecked")
	@PostConstruct
	protected void initialize() {
		setTitle("三层交换机浏览器");
		setViewSize(240, 640);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("三层交换机浏览"));

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
		switchTopoNodeLevel3Lists = (List<SwitchTopoNodeLevel3>) remoteServer
				.getService().findAll(SwitchTopoNodeLevel3.class);
		int currentUserCode = clientModel.getCurrentUser().getRole().getRoleCode();
		UserEntity currentUserEntity = clientModel.getCurrentUser();
		List<NodeEntity> nodeEntityList = new ArrayList<NodeEntity>();
		//过滤出当前用户管理的三层交换机
		if(Constants.ADMINCODE == currentUserCode){
			//当前用户为admin时不需要过滤
			for(SwitchTopoNodeLevel3 switchTopoNodeLevel3 : switchTopoNodeLevel3Lists){
				nodeEntityList.add(switchTopoNodeLevel3);
			}
		}else{
			for(SwitchTopoNodeLevel3 switchTopoNodeLevel3 : switchTopoNodeLevel3Lists){
				Set<UserEntity> userEntities = switchTopoNodeLevel3.getUsers();
				
				if(null == userEntities || userEntities.size() == 0){
//					nodeEntityList.add(switchTopoNodeLevel3);
				}else{
					for(UserEntity userEntity : userEntities){
						if(ObjectUtils.equals(currentUserEntity.getId(), userEntity.getId())){
							nodeEntityList.add(switchTopoNodeLevel3);
							break;
						}
					}
				}	
			}
		}
		nodeEntityList = NodeUtils.sortNodeEntity(nodeEntityList);
		String message = "";
		if(nodeEntityList.size() > 0){
			message = String.format("三层交换机:%s", nodeEntityList.size());
			nodeTreeFactory.addNode(nodeEntityList);
		}else{
			message = ClientUI.getDesktopWindow().DEFAULT_MESSAGE;
		}
		messageLabel.setText(message);
		addTreeSelectionPath(equipmentModel.getLastSelected());
	}
	
	private void addTreeSelectionPath(Object selected) {
		if (selected instanceof SwitchTopoNodeLevel3) {
			SwitchTopoNodeLevel3 switcherNode = (SwitchTopoNodeLevel3)selected;
			if(StringUtils.isBlank(switcherNode.getIpValue())){
				return;
			}
			this.setSwitchTopoNodeLevel3(switcherNode);
			
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
			if(null != getSwitchTopoNodeLevel3()){
				for (int row = 0; row < tree.getRowCount(); row++) {
					Object obj = tree.getPathForRow(row).getLastPathComponent();
					if (((DefaultMutableTreeNode)obj).getUserObject() instanceof SwitchTopoNodeLevel3) {
						SwitchTopoNodeLevel3 diagramNode = (SwitchTopoNodeLevel3)((DefaultMutableTreeNode)obj).getUserObject();
						
						if (getSwitchTopoNodeLevel3().getGuid().equals(diagramNode.getGuid())) {
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
				if (((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject() instanceof SwitchTopoNodeLevel3) {
					SwitchTopoNodeLevel3 diagramNode = (SwitchTopoNodeLevel3)((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject();
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
	private List<SwitchTopoNodeLevel3> switchTopoNodeLevel3Lists;
	private DefaultMutableTreeNode expandNode;
	
	private SwitchTopoNodeLevel3 switchTopoNodeLevel3 = null;
	private TreeSelectListener treeSelectListener = null;
	
	public SwitchTopoNodeLevel3 getSwitchTopoNodeLevel3() {
		return switchTopoNodeLevel3;
	}

	public void setSwitchTopoNodeLevel3(SwitchTopoNodeLevel3 switchTopoNodeLevel3) {
		this.switchTopoNodeLevel3 = switchTopoNodeLevel3;
	}

	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;

	private JTree tree;
	public static final String ID = "switchLayer3ExplorerView";
	private static final long serialVersionUID = 1L;
}