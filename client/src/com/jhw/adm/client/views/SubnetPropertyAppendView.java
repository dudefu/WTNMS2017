package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ActionConstants;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.EquipmentRepository;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(SubnetPropertyAppendView.ID)
@Scope(Scopes.DESKTOP)
public class SubnetPropertyAppendView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "subnetPropertyAppendView";
	private static final Logger LOG = LoggerFactory.getLogger(SubnetPropertyAppendView.class);
	
	private final JPanel centerPnl = new JPanel();
	private final JPanel northPnl = new JPanel();
	
	private final JLabel subnetNameLbl = new JLabel();
	private final JTextField subnetNameFld = new JTextField();
	
	private final JPanel leftNorthPnl = new JPanel(new BorderLayout());
	private final JScrollPane leftNorthScrollPnl = new JScrollPane();
	
	private final JPanel centerNorthPnl = new JPanel();
	private final JButton addBtn = new JButton(">>"); 
	private final JButton delBtn = new JButton("<<");
	
	private final JPanel rightNorthPnl = new JPanel(new BorderLayout());
	private final JScrollPane rightNorthSrollPnl = new JScrollPane();
	
	private NodeTreeFactory leftNodeTreeFactory; 
	private NodeTreeFactory rightNodeTreeFactory;
	
	private final JPanel bottomPnl = new JPanel();
	private JButton okBtn;
	private JButton closeBtn;
	
	private ButtonFactory buttonFactory;
	private List<NodeEntity> nodeEntityLists = null;
	private SubNetTopoNodeEntity subNetTopoNodeEntity = null;
	private SubNetTopoNodeEntity superSubnetTopoNodeEntity = null;
	private static final String SUPER_TITLE_NAME = "上级子网";
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=EquipmentRepository.ID)
	private EquipmentRepository equipmentRepository;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@PostConstruct
	protected void initialize() {
		buttonFactory = actionManager.getButtonFactory(this);
		init();
		initializeValue();
	}
	
	@SuppressWarnings("unchecked")
	private void initializeValue(){
		subNetTopoNodeEntity = (SubNetTopoNodeEntity) adapterManager
				.getAdapter(equipmentModel.getLastSelected(),SubNetTopoNodeEntity.class);
		
		if(subNetTopoNodeEntity == null){
			return;
		}
		subnetNameFld.setText(subNetTopoNodeEntity.getName());
		
		//初始化左边树
		leftNodeTreeFactory.cleanTreeNode();
		if(StringUtils.isBlank(subNetTopoNodeEntity.getParentNode())){
			leftNorthPnl.setBorder(BorderFactory.createTitledBorder(SUPER_TITLE_NAME + "(" + equipmentModel.getDiagramName()+ ")"));
			superSubnetTopoNodeEntity = null;
			initLeftTreeValue();
		} else {
			superSubnetTopoNodeEntity = equipmentRepository.fillSubnet(
					equipmentModel.getDiagram().getId(), subNetTopoNodeEntity.getParentNode(),clientModel.getCurrentUser());
			leftNorthPnl.setBorder(BorderFactory.createTitledBorder(SUPER_TITLE_NAME + "(" + superSubnetTopoNodeEntity.getName() + ")"));
//			List<NodeEntity> superNodeEntity = NodeUtils.filterNodeEntityByUser(superSubnetTopoNodeEntity.getNodes());
			List<NodeEntity> superNodeEntity = superSubnetTopoNodeEntity.getNodes();
			leftNodeTreeFactory.addNode(superNodeEntity);
		}
		
		//初始化右边树
		List<NodeEntity> rightNodeEntitieLists = new ArrayList<NodeEntity>();
		if(null != subNetTopoNodeEntity.getTopDiagramEntity().getId()){
			rightNodeEntitieLists = remoteServer.getNmsService().queryNodeEntity(
					subNetTopoNodeEntity.getTopDiagramEntity().getId(),subNetTopoNodeEntity.getGuid(),clientModel.getCurrentUser());
		}
		if(null != rightNodeEntitieLists){
//			rightNodeEntitieLists = NodeUtils.filterNodeEntityByUser(rightNodeEntitieLists);
			rightNodeTreeFactory.addNode(rightNodeEntitieLists);
		}
	}
	
	//初始化左边树
	@SuppressWarnings("unchecked")
	private void initLeftTreeValue(){
		List<NodeEntity> leftNodeEntities = new ArrayList<NodeEntity>();
		List<NodeEntity> nodeEntityList = (List<NodeEntity>) remoteServer.getService().findAll(NodeEntity.class);
		for(NodeEntity nodeEntity : nodeEntityList){
			if(StringUtils.isBlank(nodeEntity.getParentNode())){
				leftNodeEntities.add(nodeEntity);
			}
		}
		leftNodeEntities = NodeUtils.filterNodeEntityByUser(leftNodeEntities);
		if(leftNodeEntities.size() != 0){
			leftNodeTreeFactory.addNode(leftNodeEntities);
		}
	}

	private void init(){
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(leftNorthScrollPnl.getPreferredSize().width
				+ centerNorthPnl.getPreferredSize().width
				+ rightNorthSrollPnl.getPreferredSize().width + 35, centerPnl
				.getPreferredSize().height
				+ bottomPnl.getPreferredSize().height + 30);
		this.setTitle("编辑子网");
	}
	
	private void initCenterPnl(){
		initNorthPnl();

		JPanel namePanel = new JPanel();
		namePanel.setBorder(BorderFactory.createTitledBorder("子网名称"));
		namePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		subnetNameLbl.setText("子网名称");
		subnetNameFld.setColumns(25);
		
		namePanel.add(subnetNameLbl);
		namePanel.add(subnetNameFld);
		namePanel.add(new StarLabel("(1-16个字符)"));
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(namePanel,BorderLayout.NORTH);
		centerPnl.add(northPnl,BorderLayout.CENTER);
	}
	
	private void initNorthPnl(){
		northPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		//左边
		leftNodeTreeFactory = new NodeTreeFactory("节点信息");
		JTree leftNodeTree = leftNodeTreeFactory.getTreeInstance();
		leftNorthScrollPnl.getViewport().add(leftNodeTree);
		leftNorthScrollPnl.setBackground(Color.WHITE);
		leftNorthScrollPnl.setPreferredSize(new Dimension(300,360));
		leftNorthPnl.add(leftNorthScrollPnl,BorderLayout.CENTER);
		leftNorthPnl.setBorder(BorderFactory.createTitledBorder(SUPER_TITLE_NAME + "(" + equipmentModel.getDiagramName() + ")"));
		
		//中间
		centerNorthPnl.setLayout(new GridBagLayout());
		centerNorthPnl.add(addBtn,new GridBagConstraints(0,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		centerNorthPnl.add(delBtn,new GridBagConstraints(0,1,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(40,0,0,0),0,0));
		addBtn.setActionCommand("addBtn");
		delBtn.setActionCommand("delBtn");
		ButtonActionListener actionListener = new ButtonActionListener();
		addBtn.addActionListener(actionListener);
		delBtn.addActionListener(actionListener);
		
		//右边
		rightNodeTreeFactory = new NodeTreeFactory("子网信息");
		JTree rightNodeTree = rightNodeTreeFactory.getTreeInstance();
		rightNorthSrollPnl.getViewport().add(rightNodeTree);
		rightNorthSrollPnl.getViewport().setBackground(Color.WHITE);
		rightNorthSrollPnl.getViewport().setLayout(new FlowLayout(FlowLayout.LEADING));
		rightNorthSrollPnl.setBackground(Color.WHITE);
		rightNorthSrollPnl.setPreferredSize(new Dimension(300,360));
		rightNorthPnl.add(rightNorthSrollPnl,BorderLayout.CENTER);
		rightNorthPnl.setBorder(BorderFactory.createTitledBorder("当前子网"));
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(leftNorthPnl, BorderLayout.WEST);
		panel.add(centerNorthPnl, BorderLayout.CENTER);
		panel.add(rightNorthPnl, BorderLayout.EAST);
		northPnl.add(panel);
	}

	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		okBtn = buttonFactory.createButton(ActionConstants.SAVE);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		bottomPnl.add(okBtn);
		bottomPnl.add(closeBtn);
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE,desc="保存子网属性设置",role=Constants.MANAGERCODE)
	public void save(){

		String name = subnetNameFld.getText().trim();
		if(StringUtils.isBlank(name)){
			JOptionPane.showMessageDialog(this, "子网名称不能为空，请输入子网名称", "提示", JOptionPane.NO_OPTION);
			return;
		}else if (name.length() < 1 || name.length() > 16) {
			JOptionPane.showMessageDialog(this, "子网名称的长度(" + Integer.toString(name.length()) + ")超出范围(1-16个字符)","提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		List<SubNetTopoNodeEntity> existingSubnets = (List<SubNetTopoNodeEntity>) remoteServer.getService()
				.findAll(SubNetTopoNodeEntity.class, " where entity.name ='"+ name +"' ");
		
		if(existingSubnets.size() != 0){
			SubNetTopoNodeEntity nodeEntity = existingSubnets.get(0);
			if(!ObjectUtils.equals(nodeEntity.getId(), subNetTopoNodeEntity.getId())){
				JOptionPane.showMessageDialog(this, "子网名称不能重复，请重新输入子网名称", "提示", JOptionPane.NO_OPTION);
				return;
			}
		}
		
		subNetTopoNodeEntity.setName(name);
		List<NodeEntity> nodeEntityList = rightNodeTreeFactory.getAllNodesData();
		
		for(NodeEntity nodeEntity : nodeEntityList){
			nodeEntity.setParentNode(subNetTopoNodeEntity.getGuid());
		}
		List<NodeEntity> deleteEntityLists = rightNodeTreeFactory.getDeleteList();
		if(deleteEntityLists.size() != 0){//delete
			if(null == superSubnetTopoNodeEntity){
				for(NodeEntity nodeEntity : deleteEntityLists){
					nodeEntity.setParentNode(null);
				}	
			} else {
				for(NodeEntity nodeEntity : deleteEntityLists){
					nodeEntity.setParentNode(superSubnetTopoNodeEntity.getGuid());
				}
			}
		}

		Task task = new RequestTask(deleteEntityLists, nodeEntityList);
		showMessageDialog(task, "保存");
	}
	
	private class RequestTask implements Task{
		
		private List<NodeEntity> deleteEntityLists = null;
		private List<NodeEntity> nodeEntityList = null;
		public RequestTask(List<NodeEntity> deleteEntityLists, List<NodeEntity> nodeEntityList){
			this.deleteEntityLists = deleteEntityLists;
			this.nodeEntityList = nodeEntityList;
		}
		
		@Override
		public void run() {
			try{
				if(deleteEntityLists.size() != 0){
					remoteServer.getService().updateEntities(deleteEntityLists);
				}
				if(nodeEntityList.size() != 0){
					remoteServer.getService().updateEntities(nodeEntityList);
				}
				if(subNetTopoNodeEntity.getId() == null){
					subNetTopoNodeEntity = (SubNetTopoNodeEntity) remoteServer.getService().saveEntity(subNetTopoNodeEntity);
				}else{
					remoteServer.getService().updateEntity(subNetTopoNodeEntity);
				}
			}catch(Exception e){
				strategy.showErrorMessage("保存子网属性异常");
				equipmentModel.requireRefresh();
				LOG.error("SubnetPropertyAppendView.save() error", e);
			}
			strategy.showNormalMessage("保存子网属性成功");
			equipmentModel.requireRefresh();
		}
	}

	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task,operation);
				}
			});
		}
	}
	
	//从左往右
	private void addNode(){
		if(!leftNodeTreeFactory.selectedNode()){
			JOptionPane.showMessageDialog(this, "请选择需要添加的节点", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		boolean canAdd = leftNodeTreeFactory.compare(subNetTopoNodeEntity);
		if(canAdd == false){
			JOptionPane.showMessageDialog(this, "子网添加错误", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		nodeEntityLists = leftNodeTreeFactory.getNodeInstance();
		if (nodeEntityLists.size() == 0){
			return;
		}
		if(!isExists()){//与右边树比较
			JOptionPane.showMessageDialog(this, "设备已存在", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		rightNodeTreeFactory.addNode(nodeEntityLists);
		leftNodeTreeFactory.deleteNode(false);
	}
	
	private boolean isExists(){//false-存在,true-不存在
		return rightNodeTreeFactory.allowAddNodes(nodeEntityLists);
	}
	
	//从右往左
	public void deleteNode(){
		if(!rightNodeTreeFactory.selectedNode()){
			JOptionPane.showMessageDialog(this, "请选择需要添加的节点", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		nodeEntityLists = rightNodeTreeFactory.getNodeInstance();
		if (nodeEntityLists.size() == 0){
			return;
		}
		for(NodeEntity nodeEntity : leftNodeTreeFactory.getAllNodesData()){
			if(nodeEntityLists.size() == 0){
				break;
			}
			for(NodeEntity deleteNodeEntity : nodeEntityLists){
				if(nodeEntity.getId().equals(deleteNodeEntity.getId())){
					nodeEntityLists.remove(deleteNodeEntity);
					break;
				}
			}
		}
		if(nodeEntityLists.size() > 0){
			leftNodeTreeFactory.addNode(nodeEntityLists);
		}
		rightNodeTreeFactory.deleteNode(true);
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
	
	private class ButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if (e.getActionCommand().equals("addBtn")){
				addNode();
			}
			else if (e.getActionCommand().equals("delBtn")){
				deleteNode();
			}
		}
	}
}