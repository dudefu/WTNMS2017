package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ActionConstants;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.EquipmentRepository;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(SubnetManagementView.ID)
@Scope(Scopes.DESKTOP)
public class SubnetManagementView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "subnetManagementView";
	private static final Logger LOG = LoggerFactory.getLogger(SubnetManagementView.class);
	
	private final JPanel northPnl = new JPanel();
	private final JPanel tablePnl = new JPanel();
	
	private JXTable table;
	private SubnetTableModel tableModel;
	
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
	
	private static final String TITLE_NAME = "当前子网";
	private String transformName = TITLE_NAME;

	private static final String SUPER_TITLE_NAME = "上级子网";
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
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
		//初始化tablList
		List<SubNetTopoNodeEntity> topoNodeEntityList = (List<SubNetTopoNodeEntity>) remoteServer.getService().findAll(SubNetTopoNodeEntity.class);
		initTableValue(topoNodeEntityList);
		
		initLeftTreeValue();
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
	
	private void initTableValue(List<SubNetTopoNodeEntity> topoNodeEntityList){
		tableModel.setData(topoNodeEntityList);
		tableModel.fireTableDataChanged();
	}

	private void init(){
		initCenterPnl();
		initTablePnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(tablePnl,BorderLayout.PAGE_START);
		this.add(northPnl, BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.PAGE_END);
		this.setViewSize(leftNorthScrollPnl.getPreferredSize().width
				+ rightNorthSrollPnl.getPreferredSize().width
				+ centerNorthPnl.getPreferredSize().width + 35, tablePnl
				.getPreferredSize().height
				+ northPnl.getPreferredSize().height
				+ bottomPnl.getPreferredSize().height + 30);
		this.setTitle("子网管理");
	}

	private void initTablePnl(){
		tablePnl.setLayout(new GridLayout(1,1));
		
		table = new JXTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(true);
		
		tableModel = new SubnetTableModel();
		table.setModel(tableModel);
		table.setColumnModel(tableModel.getColumnMode());
		table.getTableHeader().setReorderingAllowed(false);
		table.setSortable(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showSubnetInfo();
			}
		});
		
		JScrollPane scroollTablePane = new JScrollPane();
		scroollTablePane.setBorder(BorderFactory.createTitledBorder("子网信息"));
		scroollTablePane.getViewport().add(table);
		scroollTablePane.getHorizontalScrollBar().setFocusable(false);
		scroollTablePane.getVerticalScrollBar().setFocusable(false);
		scroollTablePane.getHorizontalScrollBar().setUnitIncrement(30);
		scroollTablePane.getVerticalScrollBar().setUnitIncrement(30);
		scroollTablePane.setPreferredSize(new Dimension(leftNorthScrollPnl
				.getPreferredSize().width
				+ rightNorthSrollPnl.getPreferredSize().width
				+ centerNorthPnl.getPreferredSize().width + 30, 160));
		
		tablePnl.add(scroollTablePane);
	}
	
	private void initCenterPnl(){
		northPnl.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		//左边
		leftNodeTreeFactory = new NodeTreeFactory("节点信息");
		JTree leftNodeTree = leftNodeTreeFactory.getTreeInstance();
		leftNorthScrollPnl.getViewport().add(leftNodeTree);
		leftNorthScrollPnl.setBackground(Color.WHITE);
		leftNorthScrollPnl.setPreferredSize(new Dimension(300,360));
		leftNorthPnl.add(leftNorthScrollPnl,BorderLayout.CENTER);
		leftNorthPnl.setBorder(BorderFactory.createTitledBorder(SUPER_TITLE_NAME + "(" + equipmentModel.getDiagramName()+ ")"));
		
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
		rightNorthPnl.setBorder(BorderFactory.createTitledBorder(TITLE_NAME));
		
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
	
	private List<NodeEntity> rightNodeEntitieLists = new ArrayList<NodeEntity>();
	private final List<NodeEntity> leftNodeEntitieLists = new ArrayList<NodeEntity>();
	private SubNetTopoNodeEntity subNetTopoNodeEntity = null;
	private SubNetTopoNodeEntity superSubnetTopoNodeEntity = null;
	private void showSubnetInfo(){
		if(table.getSelectedColumnCount() == 0){
			return;
		}
		
		//初始化右边树
		subNetTopoNodeEntity = tableModel.getValue(table.getSelectedRow());
		rightNorthPnl.setBorder(BorderFactory.createTitledBorder(TITLE_NAME + "(" + subNetTopoNodeEntity.getName() + ")"));
		transformName = TITLE_NAME + "(" + subNetTopoNodeEntity.getName() + ")";
		rightNodeTreeFactory.cleanTreeNode();
		if(null != subNetTopoNodeEntity.getTopDiagramEntity().getId()){
			rightNodeEntitieLists = remoteServer.getNmsService().queryNodeEntity(
					subNetTopoNodeEntity.getTopDiagramEntity().getId(),subNetTopoNodeEntity.getGuid(),clientModel.getCurrentUser());
		}
		if(null != rightNodeEntitieLists){
//			rightNodeEntitieLists = NodeUtils.filterNodeEntityByUser(rightNodeEntitieLists);
			rightNodeTreeFactory.addNode(rightNodeEntitieLists);
		}
		//初始化左边树
		leftNodeTreeFactory.cleanTreeNode();
		showSuperiorTreeValue(subNetTopoNodeEntity);
	}
	
	private void showSuperiorTreeValue(SubNetTopoNodeEntity subNetTopoNodeEntity){
		if(StringUtils.isBlank(subNetTopoNodeEntity.getParentNode())){//不存在上级
			leftNorthPnl.setBorder(BorderFactory.createTitledBorder(SUPER_TITLE_NAME + "(" + equipmentModel.getDiagramName()+ ")"));
			superSubnetTopoNodeEntity = null;
			initLeftTreeValue();
		} else {//存在上级
			superSubnetTopoNodeEntity = equipmentRepository.fillSubnet(
					equipmentModel.getDiagram().getId(), subNetTopoNodeEntity.getParentNode(),clientModel.getCurrentUser());
			leftNorthPnl.setBorder(BorderFactory.createTitledBorder(SUPER_TITLE_NAME + "(" + superSubnetTopoNodeEntity.getName() + ")"));
//			List<NodeEntity> superNodeList = NodeUtils.filterNodeEntityByUser(superSubnetTopoNodeEntity.getNodes());
			List<NodeEntity> superNodeList = superSubnetTopoNodeEntity.getNodes();
			leftNodeTreeFactory.addNode(superNodeList);
		}
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE,desc="保存子网设置",role=Constants.MANAGERCODE)
	public void save(){
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
		public RequestTask(List<NodeEntity> deleteEntityListst, List<NodeEntity> nodeEntityList){
			this.deleteEntityLists = deleteEntityListst;
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
			}catch(Exception e){
				strategy.showErrorMessage("保存网管侧异常");
				equipmentModel.requireRefresh();
				LOG.error("SubnetmanagementView.save() error", e);
			}
			strategy.showNormalMessage("保存网管侧成功");
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
		if(transformName.equals(TITLE_NAME)) {
			return;
		}
		
		if(!leftNodeTreeFactory.selectedNode()){
			JOptionPane.showMessageDialog(this, "请选择需要添加的节点", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		boolean canAdd = leftNodeTreeFactory.compare(tableModel.getValue(table.getSelectedRow()));
		if(canAdd == false){
			JOptionPane.showMessageDialog(this, "子网添加错误", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		nodeEntityLists = leftNodeTreeFactory.getNodeInstance();
		if (nodeEntityLists.size() == 0){
			return;
		}
		if(!isExists()){//与左边树比较
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
		if(transformName.equals(TITLE_NAME)) {
			return;
		}
		
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
	
	private class SubnetTableModel extends AbstractTableModel{
		private static final long serialVersionUID = -3065768803494840568L;
		private List<SubNetTopoNodeEntity> topoNodeEntityList;
		private final TableColumnModelExt columnModel;
		
		public SubnetTableModel(){
			topoNodeEntityList = new ArrayList<SubNetTopoNodeEntity>();
			
			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			TableColumnExt userNameColumn = new TableColumnExt(modelIndex++,100);
			userNameColumn.setIdentifier("SUBNET_NAME");
			userNameColumn.setTitle(localizationManager.getString("SUBNET_NAME"));
			columnModel.addColumn(userNameColumn);
		}
		
		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return topoNodeEntityList.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;

			if (row < topoNodeEntityList.size()) {
				SubNetTopoNodeEntity subNetTopoNodeEntity = topoNodeEntityList.get(row);
				switch (col) {
				case 0:
					value = subNetTopoNodeEntity.getName();
					break;
				default:
					break;
				}
			}
			return value;
		}
		
		@Override
		public String getColumnName(int col) {
			return columnModel.getColumnExt(col).getTitle();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public TableColumnModel getColumnMode() {
			return columnModel;
		}

		public SubNetTopoNodeEntity getValue(int row) {
			SubNetTopoNodeEntity value = null;
			if (row < topoNodeEntityList.size()) {
				value = topoNodeEntityList.get(row);
			}
			return value;
		}

		public void setData(List<SubNetTopoNodeEntity> data) {
			if (null == data) {
				return;
			}
			this.topoNodeEntityList = data;
		}
	}
}