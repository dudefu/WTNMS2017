package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;

@Component(RegionConfigView.ID)
@Scope(Scopes.DESKTOP)
public class RegionConfigView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "regionConfigView";
	
	private JTabbedPane tabPnl = new JTabbedPane();
	
	private JSplitPane splitPane = new JSplitPane();
	
	//左边的面板
	private JPanel leftPnl = new JPanel();
	private JToolBar toolRegionBar = new JToolBar();
	private JTextField textField = new JTextField();
	private JButton addRegionBtn = new JButton();
	private JButton delRegionBtn = new JButton();
	
	private JScrollPane scrollTreePnl = new JScrollPane();
	private JTree tree = null;
	
	//右边的面板
	private JPanel rightPanel = new JPanel();

	
	private JTable managerTable = new JTable();
	private ManagerTableModel managerModel = null;
	private DefaultMutableTreeNode top = null;
	
	private List<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();
	private MessageOfSwitchConfigProcessorStrategy messageOfSwitchConfigProcessorStrategy  = new MessageOfSwitchConfigProcessorStrategy();
	
	private JButton closeBtn = null;
	
	private List<UserEntity> userEntityList = null;
	
	private static final String[] MANAGER_COLUMN_NAME = {"用户名","角色"};
	
	private static final String[] DEVICE_COLUMN_NAME = {"设备标识","设备名称"};
	
	private static final String[] REGION_COLUMN_NAME = {"区域名"};

	private ButtonFactory buttonFactory;
	
	private final static String SAVEMANAGER = "saveManager";
	private final static String DELMANAGER = "delManager";
	
	private final static String APPENDSWITCHER = "appendSwitcher";
	private final static String DELSWITCHER = "delSwitcher";
	
	private JTable switchTable = new JTable();
	private SwitcherTableModel switcherModel = null;
	
//	private JButton appendSwitcherBtn = null;
//	private JButton delSwitcherBtn = null;
	
	private IpAddressField begintIPField = null;
	private  IpAddressField endIPField = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(RegionConfigView.class);
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;	
	
	@PostConstruct
	public void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initLeftPnl();
		initRightPnl();
		
		splitPane.setLeftComponent(leftPnl);
		splitPane.setRightComponent(rightPanel);
		splitPane.setResizeWeight(0.4);

		this.setLayout(new BorderLayout());
		this.add(splitPane,BorderLayout.CENTER);
		
		this.setViewSize(640, 480);
		
		setResource();
	}
	
	private void initLeftPnl(){
		toolRegionBar.add(textField);
		
		addRegionBtn.setIcon(imageRegistry.getImageIcon(ButtonConstants.APPEND));
		delRegionBtn.setIcon(imageRegistry.getImageIcon(ButtonConstants.DELETE));
		addRegionBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				addRegion();
			}
		});
		
		delRegionBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				delRegion();
			}
		});
		
		toolRegionBar.add(addRegionBtn);
		toolRegionBar.add(delRegionBtn);
		toolRegionBar.setFloatable(false);

		leftPnl.setLayout(new BorderLayout());
		leftPnl.add(toolRegionBar,BorderLayout.NORTH);
		leftPnl.add(scrollTreePnl, BorderLayout.CENTER);
		leftPnl.setBorder(new EmptyBorder(0, 0, 0, 0));
	}
	
	private void initRightPnl(){
		JPanel managerPnl = getManagerPnl();//管理员配置面板
		JPanel devicePnl = getDevicePnl();//设备标识配置面板
		tabPnl.addTab("管理员", managerPnl);
		tabPnl.addTab("设备标识", devicePnl);
		
		JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//		saveManagerBtn = buttonFactory.createButton(SAVEMANAGER);
//		delManagerBtn = buttonFactory.createButton(DELMANAGER);
		closeBtn = buttonFactory.createCloseButton();
//		bottomPnl.add(saveManagerBtn);
//		bottomPnl.add(delManagerBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
		
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(tabPnl,BorderLayout.CENTER);
		rightPanel.add(bottomPnl,BorderLayout.SOUTH);
	}
	
	/**
	 * 得到管理员配置面板
	 * @return
	 */
	private JPanel getManagerPnl(){
		//管理员配置
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton appendBtn = new JButton("添加",imageRegistry.getImageIcon(ButtonConstants.APPEND));
		appendBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				showManagerDialog();
			}
		});
		
		JButton delBtn = buttonFactory.createButton(DELMANAGER);
		
		topPnl.add(appendBtn);
		topPnl.add(delBtn);

		
		//表格
		managerModel = new ManagerTableModel();
		managerModel.setColumnName(MANAGER_COLUMN_NAME);
		managerTable.setModel(managerModel);
		JScrollPane managerScrollPnl = new JScrollPane();
		managerScrollPnl.getViewport().add(managerTable);

		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.add(topPnl,BorderLayout.NORTH);
		
		JPanel userPnl = new JPanel(new BorderLayout());
		userPnl.add(panel1,BorderLayout.CENTER);
		
		JPanel middleCenterPnl = new JPanel(new BorderLayout());
		middleCenterPnl.add(userPnl,BorderLayout.NORTH);
		middleCenterPnl.add(managerScrollPnl,BorderLayout.CENTER);
		
		
		JPanel managerPnl = new JPanel(new BorderLayout());
		managerPnl.add(middleCenterPnl,BorderLayout.CENTER);
		
		return managerPnl;
	}
	
	/**
	 * 得到管理网段面板
	 * @return
	 */
	private JPanel getDevicePnl(){
		//设备配置
//		JPanel topPnl = new JPanel(new BorderLayout());
//		JPanel infoPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
//		JPanel panel = new JPanel(new GridBagLayout());
//		begintIPField = new IpAddressField();
//		endIPField = new IpAddressField();
//		begintIPField.setColumns(25);
//		endIPField.setColumns(25);
//		panel.add(new JLabel("起始IP:"),new GridBagConstraints(0,0,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
//		panel.add(begintIPField,new GridBagConstraints(1,0,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
//		panel.add(new StarLabel(),new GridBagConstraints(2,0,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
//		
//		panel.add(new JLabel("终止IP:"),new GridBagConstraints(0,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
//		panel.add(endIPField,new GridBagConstraints(1,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
//		panel.add(new StarLabel(),new GridBagConstraints(2,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
//		infoPnl.add(panel);
//		
//
//		appendSwitcherBtn = buttonFactory.createButton(APPENDSWITCHER);
//		delSwitcherBtn = buttonFactory.createButton(DELSWITCHER);
//		JPanel toolPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//		toolPnl.add(appendSwitcherBtn);
//		toolPnl.add(delSwitcherBtn);
//		
//		topPnl.add(infoPnl,BorderLayout.CENTER);
//		topPnl.add(toolPnl,BorderLayout.SOUTH);
		
		
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton appendDeviceBtn = new JButton("添加",imageRegistry.getImageIcon(ButtonConstants.APPEND));
		appendDeviceBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				showDeviceDialog();
			}
		});
		
		JButton delDeviceBtn = buttonFactory.createButton(DELSWITCHER);
		
		topPnl.add(appendDeviceBtn);
		topPnl.add(delDeviceBtn);
		
		//表格
		switcherModel = new SwitcherTableModel();
		switcherModel.setColumnName(DEVICE_COLUMN_NAME);
		switchTable.setModel(switcherModel);
		switcherModel.setDataList(null);
		JScrollPane switchScrollPnl = new JScrollPane();
		switchScrollPnl.getViewport().add(switchTable);

		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.add(topPnl,BorderLayout.NORTH);
		
		JPanel userPnl = new JPanel(new BorderLayout());
		userPnl.add(panel1,BorderLayout.CENTER);
		
		JPanel middleCenterPnl = new JPanel(new BorderLayout());
		middleCenterPnl.add(userPnl,BorderLayout.NORTH);
		middleCenterPnl.add(switchScrollPnl,BorderLayout.CENTER);
		
		JPanel switchPnl = new JPanel(new BorderLayout());
		switchPnl.add(middleCenterPnl,BorderLayout.CENTER);
		
		return switchPnl;
	}
	
	private void showManagerDialog(){
		//向数据库查询所有的用户
		List<UserEntity> userList = new ArrayList<UserEntity>();
		
		List<List> dataList = managerModel.getDataList();
		if (dataList != null && dataList.size() > 0){
			for (List rowList : dataList){
				UserEntity userEntity = (UserEntity)rowList.get(2);//得到已有的用户信息
				userList.add(userEntity);
			}
		}

		SelectedManagerDialog dialog = new SelectedManagerDialog(userList,this,"用户列表");
		dialog.setVisible(true);
	}
	
	private void showDeviceDialog(){
		List<NodeEntity> deviceList = new ArrayList<NodeEntity>();
		
		List<List> dataList = switcherModel.getDataList();
		if (dataList != null && dataList.size() > 0){
			for (List rowList : dataList){
				NodeEntity userEntity = (NodeEntity)rowList.get(2);//得到已有的用户信息
				deviceList.add(userEntity);
			}
		}
		
		SelectedDeviceDialog dialog = new SelectedDeviceDialog(deviceList,this,"设备列表");
		dialog.setVisible(true);
	}
	
	/**
	 * 查询所有的设备
	 */
	private List<List> queryAllDevice(){
		List<List> objects = new ArrayList<List>();
		
		//二层交换机
		List<SwitchTopoNodeEntity> switchTopoNodeEntities = (List<SwitchTopoNodeEntity>) remoteServer
			.getService().findAll(SwitchTopoNodeEntity.class);
		for(SwitchTopoNodeEntity switchTopoNodeEntity : switchTopoNodeEntities){
//			SwitchNodeEntity switchNodeEntity = remoteServer.getService()
//					.getSwitchByIp(switchTopoNodeEntity.getIpValue());
			
			List rowList = new ArrayList();
			rowList.add(switchTopoNodeEntity.getIpValue());
			rowList.add(switchTopoNodeEntity.getName());
			rowList.add(switchTopoNodeEntity);
			objects.add(rowList);
		}
		
		//三层交换机
		List<SwitchTopoNodeLevel3> layer3TopoEntitys = (List<SwitchTopoNodeLevel3>) remoteServer
				.getService().findAll(SwitchTopoNodeLevel3.class);
		for(SwitchTopoNodeLevel3 layer3TopoNode : layer3TopoEntitys){
//			SwitchLayer3 layer3 = remoteServer.getService().getSwitcher3ByIP(layer3TopoNode.getIpValue());
			
			List rowList = new ArrayList();
			rowList.add(layer3TopoNode.getIpValue());
			rowList.add(layer3TopoNode.getName());
			rowList.add(layer3TopoNode);
			objects.add(rowList);
		}
		
		//OLT
		List<EponTopoEntity> eponTopoEntities = (List<EponTopoEntity>) remoteServer
				.getService().findAll(EponTopoEntity.class);
		for(EponTopoEntity eponTopoEntity : eponTopoEntities){
//			OLTEntity oltEntity = remoteServer.getService()
//					.getOLTByIpValue(eponTopoEntity.getIpValue());
			
			List rowList = new ArrayList();
			rowList.add(eponTopoEntity.getIpValue());
			rowList.add(eponTopoEntity.getName());
			rowList.add(eponTopoEntity);
			objects.add(rowList);
		}
		
		List<ONUTopoNodeEntity> onuTopoNodeEntities = (List<ONUTopoNodeEntity>) remoteServer
				.getService().findAll(ONUTopoNodeEntity.class);
		for(ONUTopoNodeEntity onuTopoNodeEntity : onuTopoNodeEntities){
//			String macValue = onuTopoNodeEntity.getMacValue();
//			ONUEntity onuEntity = remoteServer.getService().getOLTONUEntity(macValue);	
			
			List rowList = new ArrayList();
			rowList.add(onuTopoNodeEntity.getMacValue());
			rowList.add(onuTopoNodeEntity.getName());
			rowList.add(onuTopoNodeEntity);
			objects.add(rowList);
		}
		
		
		List<CarrierTopNodeEntity> carrierTopNodeEntities = (List<CarrierTopNodeEntity>) remoteServer
			.getService().findAll(CarrierTopNodeEntity.class);
		for(CarrierTopNodeEntity carrierTopNodeEntity : carrierTopNodeEntities){
//			int carrierCode = carrierTopNodeEntity.getCarrierCode();
//			CarrierEntity carrierEntity = remoteServer.getService().getCarrierByCode(carrierCode);
			
			List rowList = new ArrayList();
			rowList.add(carrierTopNodeEntity.getCarrierCode() + "");
			rowList.add(carrierTopNodeEntity.getName());
			rowList.add(carrierTopNodeEntity);	
			objects.add(rowList);
		}
		
		List<GPRSTopoNodeEntity> gprsTopoNodeEntities = (List<GPRSTopoNodeEntity>) remoteServer
			.getService().findAll(GPRSTopoNodeEntity.class);
		for(GPRSTopoNodeEntity gprsTopoNodeEntity : gprsTopoNodeEntities){
//			String userID = gprsTopoNodeEntity.getUserId();
//			GPRSEntity gprsEntity = remoteServer.getService().getGPRSEntityByUserId(userID);
			
			List rowList = new ArrayList();
			rowList.add(gprsTopoNodeEntity.getUserId());
			rowList.add(gprsTopoNodeEntity.getName());
			rowList.add(gprsTopoNodeEntity);
			objects.add(rowList);
		}
		
		return objects;
	}
	
	
	private void setResource(){
		this.setTitle("区域管理");
		textField.setPreferredSize(new Dimension(100,textField.getPreferredSize().height));
		textField.setDocument(new TextFieldPlainDocument(textField,true));
		
		scrollTreePnl.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				tree.clearSelection();
			}
		});
	}
	
	
	private void initTree(){
		scrollTreePnl.getViewport().removeAll();
		ImageIcon leafIcon = imageRegistry
			.getImageIcon(NetworkConstants.REGION_CHILDREN);
		ImageIcon openIcon = imageRegistry
			.getImageIcon(NetworkConstants.REGION_PARENT);
		ImageIcon closedIcon = imageRegistry
			.getImageIcon(NetworkConstants.REGION_PARENT);
	
		top = new DefaultMutableTreeNode("区域",true);
		DefaultTreeModel model = new DefaultTreeModel(top);
		tree = new JTree(model);
	
		if (leafIcon != null) {
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setOpenIcon(openIcon);
			renderer.setClosedIcon(closedIcon);
			renderer.setLeafIcon(leafIcon);
			tree.setCellRenderer(renderer);
		}
	
		// 设置树的选择模式
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// 设置树的选择事件
		tree.addTreeSelectionListener(new AreaTreeSelectionListener());
		
		scrollTreePnl.getViewport().setLayout(new FlowLayout(FlowLayout.LEADING));
		scrollTreePnl.getViewport().add(tree);
		scrollTreePnl.getViewport().setBackground(Color.WHITE);
		scrollTreePnl.revalidate();
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		//向数据库查询所有的地区
		List<AreaEntity> areaEntityList = (List<AreaEntity>)remoteServer.getService().findAll(AreaEntity.class);
		//向数据库查询所有的用户
		userEntityList = (List<UserEntity>)remoteServer.getService().findAll(UserEntity.class);
		
		//根据查询到的地区构造树结构
		initTreePnl(areaEntityList);
	}
	
	
	/**
	 * 通过向数据库读取数据，设置tree的结构
	 */
	private void initTreePnl(List<AreaEntity> areaEntityList){
		if(null == areaEntityList){
			return;
		}

		//清空树结构
		clearTree();
		
		//通过从数据库查询的值重新加载tree节点
		AreaEntity entity = null;
		for (int i = 0 ; i < areaEntityList.size(); i++){
			entity =  areaEntityList.get(i);
			setNode(entity);
		}
		
		//设置根节点不可见
//		tree.setRootVisible(false);
		//展开根节点
		expandTree(tree);
	}
	
	/**
	 * 通过递归调用把所有的父节点找到
	 * @param entity
	 */
	private void setNode(AreaEntity childEntity){
		AreaEntity parentEntity = childEntity.getSuperArea();
		if (null == parentEntity){
			DefaultMutableTreeNode childNode = getNodeFromList(childEntity.getId());
			if (null == childNode){
				childNode = new DefaultMutableTreeNode();
				childNode.setUserObject(new AreaEntityObject(childEntity));
			}
			addNode(top,childNode);
			nodeList.add(childNode);
		}else{
			DefaultMutableTreeNode node = getNodeFromList(parentEntity.getId());
			if (null == node){
				DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();
				parentNode.setUserObject(new AreaEntityObject(parentEntity));
				
				DefaultMutableTreeNode childNode = getNodeFromList(childEntity.getId());
				if (null == childNode){
					childNode = new DefaultMutableTreeNode();
					childNode.setUserObject(new AreaEntityObject(childEntity));
					nodeList.add(childNode);
				}
				nodeList.add(parentNode);
				
				addNode(parentNode,childNode);
				setNode(parentEntity);
			}else{
				DefaultMutableTreeNode childNode = getNodeFromList(childEntity.getId());
				if(null == childNode){
					childNode = new DefaultMutableTreeNode();
					nodeList.add(childNode);
					childNode.setUserObject(new AreaEntityObject(childEntity));
				}
				node.add(childNode);
			}
		}
	}
	
	/**
	 * 判断此子节点是否已经存在于父节点中，如果已经存在，就不加
	 * @param parentNode
	 * @param childNode
	 */
	private void addNode(DefaultMutableTreeNode parentNode,DefaultMutableTreeNode childNode){
		long id = ((AreaEntityObject)childNode.getUserObject()).getEntity().getId();
		boolean isSame = false;
		
		int count = parentNode.getChildCount();
		for (int i = 0 ; i < count; i++){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)parentNode.getChildAt(i);
			AreaEntityObject object = (AreaEntityObject)node.getUserObject();
			long idValue = object.getEntity().getId(); 
			if (id == idValue){
				isSame = true;
				break;
			}
		}
		
		if (!isSame){
			parentNode.add(childNode);
		}
	}
	
	/**
	 * 通过list判断是否已经存在此节点
	 * @param id
	 * @return
	 */
	private DefaultMutableTreeNode getNodeFromList(long id){
		int size = nodeList.size();
		DefaultMutableTreeNode node = null;
		
		boolean isSame = false;
		for (int i =0 ; i< size; i++){
			node = (DefaultMutableTreeNode)nodeList.get(i);
			AreaEntityObject object = (AreaEntityObject)node.getUserObject();
			long idValue = object.getEntity().getId();
			if (id == idValue){
				isSame = true;
				break;
			}
		}
		if (isSame){
			return node;
		}
		else{
			return null;
		}
	}
	
	
	public void addRegion(){
		String areaName = textField.getText().trim();
		if (StringUtils.isBlank(areaName)){
			JOptionPane.showMessageDialog(this, "请输入区域","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		if(isExists(areaName)){
			JOptionPane.showMessageDialog(this, "区域不允许重复，请重新输入", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		AreaEntity areaEntity = new AreaEntity();
		areaEntity.setName(areaName);
		
		AreaEntity selectedAreaEntity = getSelectedAreaEntity(tree);
		if (null != selectedAreaEntity){
			areaEntity.setSuperArea(selectedAreaEntity);
		}
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("新增", this);
		remoteServer.getService().saveEntity(areaEntity);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		
		queryData();
		textField.setText("");
	}
	
	@SuppressWarnings("unchecked")
	private boolean isExists(String userName){
		boolean isExists = false;
		DefaultMutableTreeNode node = null;
		Enumeration e = top.breadthFirstEnumeration();
		while(e.hasMoreElements()){
			node = (DefaultMutableTreeNode) e.nextElement();
			if(null != node.getUserObject()){
				if(userName.equals(node.getUserObject().toString())){
					isExists = true;
					return isExists;
				}
			}
		}
		return isExists;
	}
	
	private boolean unDelete = true;
	public void delRegion(){
		List<AreaEntity> selectedAreaEntity = getSelectedAreaEntity();
		if (selectedAreaEntity.size() == 0){
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		if(!remoteServer.getService().deleteArea(selectedAreaEntity)){
			JOptionPane.showMessageDialog(this, "存在区域被配置，不能删除","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("删除", this);
		remoteServer.getService().deleteEntities(selectedAreaEntity);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		queryData();
		refreshManagerTable(null);
	}
	
	public void saveManager(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
		if(null == node){
			JOptionPane.showMessageDialog(this, "请选择要配置管理员的区域","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		//得到所选择的AreaEntity
		List<AreaEntity> list = new ArrayList<AreaEntity>();
		final AreaEntity selectedAreaEntity = getSelectedAreaEntity(tree);
		if (null == selectedAreaEntity){
			JOptionPane.showMessageDialog(this, "请选择区域","提示",JOptionPane.NO_OPTION);
			return;
		}
		list.add(selectedAreaEntity);
		
		
		List<UserEntity> userList = new ArrayList<UserEntity>();
		
		List<List> dataList = managerModel.getDataList();
		for (List rowList : dataList){
			UserEntity userEntity = (UserEntity)rowList.get(2);//得到已有的用户信息
			boolean isSame = false;
			int index = -1;
			for (int i = 0 ; i < userEntity.getAreas().size(); i++){
				long selectId = selectedAreaEntity.getId();
				long id = userEntity.getAreas().get(i).getId();
				if (selectId == id){
					index = i;
					isSame = true;
					break;
				}
			}
			if (isSame){
				userEntity.getAreas().set(index, selectedAreaEntity);
			}
			else{
				userEntity.getAreas().addAll(list);
			}
			
			userList.add(userEntity);
		}

		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("保存", this);
		remoteServer.getService().updateEntities(userList);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		refreshManagerTable(selectedAreaEntity);
	}
	
	@ViewAction(name=DELMANAGER, icon=ButtonConstants.DELETE, text=DELETE, desc="删除区域管理员",role=Constants.MANAGERCODE)
	public void delManager(){
		if(managerTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择要删除的管理员","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		int[] rows = managerTable.getSelectedRows();

		//把所选择的userEntity放入到userEntities中
		List<UserEntity> userEntities = new ArrayList<UserEntity>();
		for (int i = 0 ; i < rows.length; i++){
			List rowList = managerModel.getSelectedRow(rows[i]);
			UserEntity userEntity = (UserEntity) rowList.get(2);
			userEntities.add(userEntity);
		}
		
		//遍历userEntities，把树节点得到的areaEntity从userEntity中删除掉
		AreaEntity selectedAreaEntity = getSelectedAreaEntity(tree);
		for(UserEntity userEntity : userEntities){
			long selectID = selectedAreaEntity.getId();
			
			List<AreaEntity> list = userEntity.getAreas();
			for (int i = list.size() -1 ; i >= 0; i--){
				long id = list.get(i).getId();
				if (selectID == id){
					list.remove(i);
				}
			}
			
			userEntity.setAreas(list);
		}
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("删除", this);
		remoteServer.getService().updateEntities(userEntities);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);

		refreshManagerTable(selectedAreaEntity);
	}
	
	
	
	public void saveDevice(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
		if(null == node){
			JOptionPane.showMessageDialog(this, "请选择要配置管理员的区域","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		//得到所选择的AreaEntity
		List<AreaEntity> list = new ArrayList<AreaEntity>();
		final AreaEntity selectedAreaEntity = getSelectedAreaEntity(tree);
		if (null == selectedAreaEntity){
			JOptionPane.showMessageDialog(this, "请选择区域","提示",JOptionPane.NO_OPTION);
			return;
		}
		list.add(selectedAreaEntity);
		
		
		List<NodeEntity> nodeList = new ArrayList<NodeEntity>();
		
		List<List> dataList = switcherModel.getDataList();
		for (List rowList : dataList){
			NodeEntity nodeEntity = (NodeEntity)rowList.get(2);//得到已有的设备信息
			boolean isSame = false;
			int index = -1;
			for (int i = 0 ; i < nodeEntity.getAreas().size(); i++){
				long selectId = selectedAreaEntity.getId();
				Object[] objects = nodeEntity.getAreas().toArray();
				long id = ((AreaEntity)objects[i]).getId();
				if (selectId == id){
					index = i;
					isSame = true;
					break;
				}
			}
			if (!isSame){
				nodeEntity.getAreas().add(selectedAreaEntity);
			}
//			if (isSame){
//				nodeEntity.getAreas().add(selectedAreaEntity);
//			}
//			else{
//				nodeEntity.getAreas().addAll(list);
//			}
			
			nodeList.add(nodeEntity);
		}

		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("保存", this);
		remoteServer.getService().updateEntities(nodeList);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		refreshSwitcherTable(selectedAreaEntity);
	}
	

	
	@ViewAction(name=APPENDSWITCHER, icon=ButtonConstants.APPEND, text=APPEND, desc="增加交换机网段",role=Constants.MANAGERCODE)
	public void appendSwitcher(){
		//得到所选择的AreaEntity
		List<AreaEntity> list = new ArrayList<AreaEntity>();
		final AreaEntity selectedAreaEntity = getSelectedAreaEntity(tree);
		if (null == selectedAreaEntity){
			JOptionPane.showMessageDialog(this, "请选择区域","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		if (null == begintIPField.getIpAddress()
				|| "".equals(begintIPField.getIpAddress())
				|| null == endIPField.getIpAddress()
				|| "".equals(endIPField.getIpAddress())) {
			JOptionPane.showMessageDialog(this, "IP网段中不允许空值，请重新输入IP地址","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		if(!verifyIP(begintIPField.getIpAddress(),endIPField.getIpAddress())){
			JOptionPane.showMessageDialog(this, "网段IP地址错误，请重新输入IP地址","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		if(isIpSameValue()){
			return;
		}
		
		if(ClientUtils.isIllegal(begintIPField.getIpAddress())){
			JOptionPane.showMessageDialog(this, "不允许添加非法网段,请重新输入IP地址","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		IPSegment ipSegment = getInputIPSegment();
		if(null == ipSegment){
			return ;
		}
		
		Set<IPSegment> ipSegmentSet = null;
		if (selectedAreaEntity.getIps() == null  || selectedAreaEntity.getIps().size() < 1){
			ipSegmentSet = new HashSet<IPSegment>();
			ipSegmentSet.add(ipSegment);
		}
		else{
			ipSegmentSet = selectedAreaEntity.getIps();
			ipSegmentSet.add(ipSegment);
		}
		selectedAreaEntity.setIps(ipSegmentSet);
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("保存", this);
		remoteServer.getService().updateEntity(selectedAreaEntity);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		refreshSwitcherTable(selectedAreaEntity);
		
		begintIPField.setText("");
		endIPField.setText("");
	}
	
	@ViewAction(name=DELSWITCHER, icon=ButtonConstants.DELETE, text=DELETE, desc="删除区域设备",role=Constants.MANAGERCODE)
	public void delSwitcher(){
		if(switchTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择要删除的设备","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		int[] rows = switchTable.getSelectedRows();

		//把所选择的nodeEntity放入到nodeEntities中
		List<NodeEntity> nodeEntities = new ArrayList<NodeEntity>();
		for (int i = 0 ; i < rows.length; i++){
			List rowList = switcherModel.getSelectedRow(rows[i]);
			NodeEntity nodeEntity = (NodeEntity) rowList.get(2);
			nodeEntities.add(nodeEntity);
		}
		
		//遍历nodeEntities，把树节点得到的areaEntity从nodeEntity中删除掉
		AreaEntity selectedAreaEntity = getSelectedAreaEntity(tree);
		for(NodeEntity nodeEntity : nodeEntities){
			long selectID = selectedAreaEntity.getId();
			
			Set<AreaEntity> set = nodeEntity.getAreas();
			Object[] objects = set.toArray();
			for (int i = objects.length -1 ; i >= 0; i--){
				AreaEntity entity = (AreaEntity)objects[i];
				long id = entity.getId();
				if (selectID == id){
					set.remove(entity);
				}
			}
			
			nodeEntity.setAreas(set);
		}
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("删除", this);
		remoteServer.getService().updateEntities(nodeEntities);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		
		refreshSwitcherTable(selectedAreaEntity);
	}
	
	private void clearTree(){
		initTree();
		nodeList.clear();
	}
	
	private boolean verifyIP(String beginIP,String endIP) {
		boolean isValid = true;
		
		String[] beginIPs = beginIP.split("\\.");
		String[] endIPs = endIP.split("\\.");
		if(beginIPs.length != 4 || endIPs.length != 4){
			isValid = false;
			return isValid;
		}
		
		for(int i = 0;i < 4;i++){
			if(i < 3){
				if(!beginIPs[i].equals(endIPs[i])){
					isValid = false;
					break;
				}
			}else if(i == 3){
				if(Integer.parseInt(beginIPs[i]) > Integer.parseInt(endIPs[i])){
					isValid = false;
					break;
				}
			}
		}
		
		return isValid;
	}
	
	private boolean isIpSameValue(){
		
		boolean isSame = false;
		
//		List<IPSegment> ipSegmentList = switcherModel.getIPSegmentList();
		List<IPSegment> ipSegmentList = null;
		if (null == ipSegmentList || ipSegmentList.size() < 1){
			isSame = false;
			return isSame;
		}
		
		String newBeginIP = begintIPField.getText().trim();//新起始IP
		String newEndIP = endIPField.getText().trim();//新终止IP
		
		for (int i = 0 ; i < ipSegmentList.size() ;i++){
			String oldBeginIp = ipSegmentList.get(i).getBeginIp();
			String oldEndIp = ipSegmentList.get(i).getEndIp();
			
			if(isSubNetworkSegment(oldBeginIp,newBeginIP,oldEndIp,newEndIP)){
				JOptionPane.showMessageDialog(this, "管理网段有重复，请检查后重新输入","提示",JOptionPane.NO_OPTION);
				isSame = true;
				break;
			}
		}
		
		return isSame;
	}
	
	//重复网段判断
	private boolean isSubNetworkSegment(String oldBeginIP,String newBeginIP,String oldEndIP,String newEndIP){
		boolean isSubNetworkSegment = false;
		
		if (!oldBeginIP.substring(0, oldBeginIP.lastIndexOf(".")).equals(
				newBeginIP.substring(0, newBeginIP.lastIndexOf(".")))) {//判断ip地址的前三位是否相同
			isSubNetworkSegment = false;
			return isSubNetworkSegment;
		}
		
		int oldBeginForthIP = Integer.parseInt(oldBeginIP.substring(oldBeginIP
				.lastIndexOf(".") + 1, oldBeginIP.length()));
		int newBeginForthIP = Integer.parseInt(newBeginIP.substring(newBeginIP
				.lastIndexOf(".") + 1, newBeginIP.length()));
		
		int oldEndForthIP = Integer.parseInt(oldEndIP.substring(oldEndIP
				.lastIndexOf(".") + 1, oldEndIP.length()));
		int newEndForthIP = Integer.parseInt(newEndIP.substring(newEndIP
				.lastIndexOf(".") + 1, newEndIP.length()));
		
		if ((oldBeginForthIP <= newBeginForthIP && newBeginForthIP <= oldEndForthIP)
				|| (newEndForthIP >= oldBeginForthIP && oldEndForthIP >= newEndForthIP)
				|| (newBeginForthIP <= oldBeginForthIP && newEndForthIP >= oldEndForthIP)
				) {
			isSubNetworkSegment = true;
			return isSubNetworkSegment;
		}

		return isSubNetworkSegment;
	}
	
	public IPSegment getInputIPSegment(){
		String biginIp = begintIPField.getText();
		String endIp = endIPField.getText();
		
		if (null == biginIp || biginIp.trim().equals("")){
			JOptionPane.showMessageDialog(this, "请输入起始IP地址","提示",JOptionPane.NO_OPTION);
			return null;
		}
		else if (null == endIp || endIp.trim().equals("")){
			JOptionPane.showMessageDialog(this, "请输入终止IP地址","提示",JOptionPane.NO_OPTION);
			return null;
		}
		
		IPSegment ipSegment = new IPSegment();
		ipSegment.setBeginIp(biginIp.trim());
		ipSegment.setEndIp(endIp.trim());
		return ipSegment;
	}
	
	public class AreaTreeSelectionListener implements TreeSelectionListener{
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			JTree regionTree = (JTree) e.getSource();
			AreaEntity selectedAreaEntity = getSelectedAreaEntity(regionTree);
			if (null != selectedAreaEntity){
				refreshManagerTable(selectedAreaEntity);
				
				refreshSwitcherTable(selectedAreaEntity);
			}
			else{
				managerModel.setDataList(null);
				switcherModel.setDataList(null);
				managerModel.fireTableDataChanged();
				switcherModel.fireTableDataChanged();
			}
		}
	}
	
	/**
	 * 通过所选择的tree节点刷新table
	 * @param selectedAreaEntity
	 */
	@SuppressWarnings("unchecked")
	private void refreshManagerTable(AreaEntity selectedAreaEntity){
		List<List> dataList = new ArrayList<List>();
		List<UserEntity> userEntityLists = areaEntityToUserEntity(selectedAreaEntity);
		for (UserEntity userEntity : userEntityLists){
			List rowList = new ArrayList();
			rowList.add(0, userEntity.getUserName());//第一列为名称
			rowList.add(1, userEntity.getRole().getRoleName());//第二列为角色
			rowList.add(2,userEntity);//第三列为UserEntity
			dataList.add(rowList);
		}
		
		managerModel.setDataList(dataList);
		managerModel.fireTableDataChanged();
	}
	
	private void refreshSwitcherTable(AreaEntity selectedAreaEntity){
		List<List> dataList = new ArrayList<List>(); 
		
		List<List> lists = this.queryAllDevice();
		for(int i = 0; i < lists.size(); i++){
			List rowList = lists.get(i);
			NodeEntity nodeEntity = (NodeEntity)rowList.get(2);
			Set<AreaEntity> areaEntities = nodeEntity.getAreas();
			Iterator iterator = areaEntities.iterator();
			while(iterator.hasNext()){
				AreaEntity areaEntity = (AreaEntity)iterator.next();
				long id = areaEntity.getId();
				long selectId = selectedAreaEntity.getId();
				if (selectId == id){
					dataList.add(rowList);
					break;
				}
			}
		}
		
		switcherModel.setDataList(dataList);
		switcherModel.fireTableDataChanged();
	}
	
	/**
	 * 通过区域得到区域下的用户
	 * @param selectedAreaEntity
	 * @return
	 */
	private List<UserEntity> areaEntityToUserEntity(AreaEntity selectedAreaEntity){
		userEntityList = (List<UserEntity>)remoteServer.getService().findAll(UserEntity.class);
		List<UserEntity> userList = new ArrayList<UserEntity>();
		
		if (null != selectedAreaEntity){
			long selectID = selectedAreaEntity.getId();
			//更新表格
			int userCount = userEntityList.size();
			for (int i = 0 ; i < userCount; i++){
				UserEntity userEntity = userEntityList.get(i);
				List<AreaEntity> list = userEntity.getAreas();
				for (int j = 0 ; j < list.size(); j++){
					AreaEntity areaEntity = list.get(j);
					long id = areaEntity.getId();
					if(selectID == id){
						userList.add(userEntity);
					}
				}
			}
		}
		
		return userList;
	}
	
	private List<AreaEntity> areaEntityList = null;
//	private List<AreaEntity> getSelectedAreaEntity(){
//		areaEntityList = new ArrayList<AreaEntity>();
//		DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.tree.getLastSelectedPathComponent();
//		if(null == node){
//			return areaEntityList;
//		}
//		node.breadthFirstEnumeration();
//		recursionTree(node);
//		return areaEntityList;
//	}

	private List<AreaEntity> getSelectedAreaEntity(){
		areaEntityList = new ArrayList<AreaEntity>();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.tree.getLastSelectedPathComponent();
		if(null == node){
			return areaEntityList;
		}
		
		Enumeration  breadthFirst = node.breadthFirstEnumeration(); 
		while(breadthFirst.hasMoreElements()){
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)breadthFirst.nextElement();
			AreaEntity object = ((AreaEntityObject)treeNode.getUserObject()).getEntity();
			areaEntityList.add(object);
		}
		
//		node.breadthFirstEnumeration();
//		recursionTree(node);
		return areaEntityList;
	}
	
	private void recursionTree(DefaultMutableTreeNode node){
		AreaEntity areaEntity = ((AreaEntityObject)node.getUserObject()).getEntity();
		areaEntityList.add(areaEntity);
		if(node.getChildCount() != 0){
			for (int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
				recursionTree(childNode);
			}
		}
	}
	
	private AreaEntity getSelectedAreaEntity(JTree tree){
		AreaEntity selectedAreaEntity = null;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if(null == node){
			return selectedAreaEntity;
		}
		
		if (node.getUserObject() instanceof AreaEntityObject){
			selectedAreaEntity = ((AreaEntityObject)node.getUserObject()).getEntity();
		}
		return selectedAreaEntity;
	}
	
	
	@SuppressWarnings("unchecked")
	class ManagerTableModel extends AbstractTableModel{
		private String[] columnName = null;
		private List<List> dataList = null;
		
		public ManagerTableModel(){
			super();
		}
		
		public String[] getColumnName() {
			return columnName;
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public List<List> getDataList() {
			return dataList;
		}

		public void setDataList(List<List> dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<List>();
			}
			else{
				this.dataList = dataList;
			}
		}

		public int getRowCount(){
			if (null == dataList){
				return 0;
			}
			return dataList.size();
		}

	    public int getColumnCount(){
	    	if (null == columnName){
	    		return 0;
	    	}
	    	return columnName.length;
	    }

	    public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }
	  
	    public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	    public Object getValueAt(int rowIndex, int columnIndex){
	    	Object object = null;
	    	if (rowIndex < 0){
	    		return object;
	    	}
	    	if(columnIndex == 0){
	    		object = ((UserEntity) (dataList.get(rowIndex).get(columnIndex + 2))).getUserName();
	    	}
	    	else if (columnIndex == 1){
	    		object = ((UserEntity) (dataList.get(rowIndex).get(columnIndex + 1))).getRole().getRoleName();
	    	}
	    	return object;
	    }
	    
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	dataList.get(rowIndex).set(columnIndex, aValue);
	    }
	    
		public List getSelectedRow(int selectedRow){
			return this.dataList.get(selectedRow);
		}
		
		public void removeRow(int[] selectRows){
			for (int i = selectRows.length -1 ; i >= 0 ; i--){
				dataList.remove(selectRows[i]);
			}
			this.setDataList(dataList);
			this.fireTableDataChanged();
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	class SwitcherTableModel extends AbstractTableModel{
		private String[] columnName = null;
		private List<List> dataList = null;
		
		public SwitcherTableModel(){
			super();
		}
		
		public String[] getColumnName() {
			return columnName;
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public List<List> getDataList() {
			return dataList;
		}

		public void setDataList(List<List> dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<List>();
			}
			else{
				this.dataList = dataList;
			}
		}

		public int getRowCount(){
			if (null == dataList){
				return 0;
			}
			return dataList.size();
		}

	    public int getColumnCount(){
	    	if (null == columnName){
	    		return 0;
	    	}
	    	return columnName.length;
	    }

	    public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }
	  
	    public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	    public Object getValueAt(int rowIndex, int columnIndex){
	    	if (rowIndex < 0 || columnIndex < 0){
	    		return null;
	    	}
	    	
	    	return dataList.get(rowIndex).get(columnIndex);
	    }
	    
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	dataList.get(rowIndex).set(columnIndex, aValue);
	    }
	    
	    public List getSelectedRow(int selectedRow){
			return this.dataList.get(selectedRow);
		}
	}
	
	/**
	 * @author Administrator
	 */
	class AreaEntityObject implements Serializable{
		private AreaEntity entity = null;
		
		public AreaEntityObject(AreaEntity entity){
			this.entity = entity;
		}
		
		/**
		 * 重写toString方法
		 */
		public String toString(){
			return this.entity.getName();
		}

		public AreaEntity getEntity() {
			return entity;
		}
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	private void expandTree(JTree tree) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root));
	}
	private void expandAll(JTree tree, TreePath parent) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path);
			}
		} // Expansion or collapse must be done bottom-up

		tree.expandPath(parent);
	}
	

	
	/**
	 * @author Administrator
	 */
	class SelectedManagerDialog extends JDialog{
		private List list = null; 
		private JTable table = null;
		private TableModel tableModel = null;
		private ViewPart viewPart = null; 
		
		
		/**
		 * @param list  区域中已有的用户信息列表
		 * @param type  是操作管理员还是交换机
		 * @param viewPart  本视图
		 */
		public SelectedManagerDialog(List list,ViewPart viewPart,String title){
			super(ClientUtils.getRootFrame(),title);
			this.list = list;
			this.viewPart = viewPart;
			
			init();
		}
		
		private void init(){
			JPanel panel = new JPanel(new BorderLayout());
			
			
			JScrollPane scrollPnl = new JScrollPane();
			table = new JTable();

			tableModel = new ManagerTableModel();
			((ManagerTableModel)tableModel).setColumnName(MANAGER_COLUMN_NAME);

			table.setModel(tableModel);
			scrollPnl.getViewport().add(table);
			
			JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			JButton saveBtn = new JButton("保存", imageRegistry.getImageIcon(ButtonConstants.SAVE));
			JButton closeSelectBtn = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
			bottomPnl.add(saveBtn);
			bottomPnl.add(closeSelectBtn);
			
			panel.add(scrollPnl,BorderLayout.CENTER);
			panel.add(bottomPnl,BorderLayout.SOUTH);
			
			//设置值
			setValue();
			

			saveBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					saveAction();
				}
			});
			
			closeSelectBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
			
			this.getContentPane().add(panel);
			this.setSize(400, 300);
			this.setLocationRelativeTo(viewPart);
			this.setModal(true);
			this.setResizable(false);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
		
		private void setValue(){
			List<List> dataList = new ArrayList();
				
			//查询所有的用户信息
			List<UserEntity> userLists = (List<UserEntity>)remoteServer.getService().findAll(UserEntity.class);
				
			for(Object object : userLists){
				UserEntity userEntity = (UserEntity)object;
				List rowList = new ArrayList();
				rowList.add(0,userEntity.getUserName());//名称
				rowList.add(1,userEntity.getRole().getRoleName());//角色
				rowList.add(2,userEntity);
					
				dataList.add(rowList);
			}
			((ManagerTableModel)tableModel).setDataList(dataList);
			((ManagerTableModel)tableModel).fireTableDataChanged();
		}
		
		/**
		 * 保存
		 */
		private void saveAction(){
			int[] selectRows = table.getSelectedRows();
			int lenth = selectRows.length;
			if (lenth < 1){
				return;
			}
			
			List<UserEntity> userList = new ArrayList<UserEntity>(list); 

			for (int i = 0 ; i < lenth; i++){
				List rowList = ((ManagerTableModel)tableModel).getSelectedRow(selectRows[i]);
				UserEntity userEntity = (UserEntity)rowList.get(2);
					
				boolean isSame = false;
				for (UserEntity entity : userList){
					if ((long)entity.getId() == (long)userEntity.getId()){
						isSame = true;
						break;
					}
				}
				if (!isSame){
					userList.add(userEntity);
				}
			}
			saveUserEntity(userList);
			
			this.dispose();
		}
		
		private void close(){
			this.dispose();
		}
	}

	public void saveUserEntity(List<UserEntity> userList) {
		List<List> dataList = new ArrayList<List>();
		
		for (UserEntity userEntity : userList){
			List rowList = new ArrayList();
			rowList.add(0, userEntity.getUserName());//第一列为名称
			rowList.add(1, userEntity.getRole().getRoleName());//第二列为角色
			rowList.add(2,userEntity);//第三列为UserEntity
			dataList.add(rowList);
		}
		
		managerModel.setDataList(dataList);
		
		//保存到数据库
		saveManager();
	}
	
	/**
	 * @author Administrator
	 */
	class SelectedDeviceDialog extends JDialog{
		private List<NodeEntity> list = null; 
		private JTable table = null;
		private TableModel tableModel = null;
		private ViewPart viewPart = null; 
		
		/**
		 * @param list  区域中已有的用户信息列表
		 * @param type  是操作管理员还是交换机
		 * @param viewPart  本视图
		 */
		public SelectedDeviceDialog(List<NodeEntity> list,ViewPart viewPart,String title){
			super(ClientUtils.getRootFrame(),title);
			this.list = list;
			this.viewPart = viewPart;
			
			init();
		}
		
		private void init(){
			JPanel panel = new JPanel(new BorderLayout());
			
			JScrollPane scrollPnl = new JScrollPane();
			table = new JTable();

			tableModel = new SwitcherTableModel();
			((SwitcherTableModel)tableModel).setColumnName(DEVICE_COLUMN_NAME);

			table.setModel(tableModel);
			scrollPnl.getViewport().add(table);
			
			JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			JButton saveBtn = new JButton("保存", imageRegistry.getImageIcon(ButtonConstants.SAVE));
			JButton closeSelectBtn = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
			bottomPnl.add(saveBtn);
			bottomPnl.add(closeSelectBtn);
			
			panel.add(scrollPnl,BorderLayout.CENTER);
			panel.add(bottomPnl,BorderLayout.SOUTH);
			
			//设置值
			setValue();
			
			saveBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					saveAction();
				}
			});
			
			closeSelectBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
			
			this.getContentPane().add(panel);
			this.setSize(400, 300);
			this.setLocationRelativeTo(viewPart);
			this.setModal(true);
			this.setResizable(false);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
		
		private void setValue(){
			//向数据库查询所有的设备
			List<List> dataList = queryAllDevice();
			((SwitcherTableModel)tableModel).setDataList(dataList);
			((SwitcherTableModel)tableModel).fireTableDataChanged();
		}
		
		/**
		 * 保存
		 */
		private void saveAction(){
			int[] selectRows = table.getSelectedRows();
			int lenth = selectRows.length;
			if (lenth < 1){
				return;
			}
			
			List<NodeEntity> nodeList = new ArrayList<NodeEntity>(list); 
//
			for (int i = 0 ; i < lenth; i++){
				List rowList = ((SwitcherTableModel)tableModel).getSelectedRow(selectRows[i]);
				NodeEntity nodeEntity = (NodeEntity)rowList.get(2);
				
				boolean isSame = false;
				for (NodeEntity entity : nodeList){
					if ((long)entity.getId() == (long)nodeEntity.getId()){
						isSame = true;
						break;
					}
				}
				if (!isSame){
					nodeList.add(nodeEntity);
				}
			}
			saveNodeEntity(nodeList);
			
			this.dispose();
		}
		
		private void close(){
			this.dispose();
		}
	}
	
	public void saveNodeEntity(List<NodeEntity> nodeList) {
		List<List> dataList = new ArrayList<List>();
		
		for (NodeEntity nodeEntity : nodeList){
			List rowList = new ArrayList();
			String deviceTag = "";   //设备标识
			String name = "";  //设备名称
			if(nodeEntity instanceof SwitchTopoNodeEntity){
				deviceTag = ((SwitchTopoNodeEntity)nodeEntity).getIpValue();
				name = nodeEntity.getName();
			}
			else if(nodeEntity instanceof SwitchTopoNodeLevel3){
				deviceTag = ((SwitchTopoNodeLevel3)nodeEntity).getIpValue();
				name = nodeEntity.getName();
			}
			else if(nodeEntity instanceof EponTopoEntity){
				deviceTag = ((EponTopoEntity)nodeEntity).getIpValue();
				name = nodeEntity.getName();		
			}
			else if(nodeEntity instanceof ONUTopoNodeEntity){
				deviceTag = ((ONUTopoNodeEntity)nodeEntity).getMacValue();
				name = nodeEntity.getName();	
			}
			else if(nodeEntity instanceof CarrierTopNodeEntity){
				deviceTag = ((CarrierTopNodeEntity)nodeEntity).getCarrierCode() + "";
				name = nodeEntity.getName();	
			}
			else if(nodeEntity instanceof GPRSTopoNodeEntity){
				deviceTag = ((GPRSTopoNodeEntity)nodeEntity).getUserId();
				name = nodeEntity.getName();	
			}
			else{
				continue;
			}
			
			rowList.add(0, deviceTag);//第一列为名称
			rowList.add(1, name);//第二列为角色
			rowList.add(2,nodeEntity);//第三列为UserEntity
			dataList.add(rowList);
		}
		
		switcherModel.setDataList(dataList);
		
		//保存到数据库
		saveDevice();
	}
	
	public void setSelectSwitcherList(List<SynchDevice> selectSwitcherList) {
		
	}
	
	
	
	
	
	
	
	

	
	
}