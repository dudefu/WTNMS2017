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
	
	//��ߵ����
	private JPanel leftPnl = new JPanel();
	private JToolBar toolRegionBar = new JToolBar();
	private JTextField textField = new JTextField();
	private JButton addRegionBtn = new JButton();
	private JButton delRegionBtn = new JButton();
	
	private JScrollPane scrollTreePnl = new JScrollPane();
	private JTree tree = null;
	
	//�ұߵ����
	private JPanel rightPanel = new JPanel();

	
	private JTable managerTable = new JTable();
	private ManagerTableModel managerModel = null;
	private DefaultMutableTreeNode top = null;
	
	private List<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();
	private MessageOfSwitchConfigProcessorStrategy messageOfSwitchConfigProcessorStrategy  = new MessageOfSwitchConfigProcessorStrategy();
	
	private JButton closeBtn = null;
	
	private List<UserEntity> userEntityList = null;
	
	private static final String[] MANAGER_COLUMN_NAME = {"�û���","��ɫ"};
	
	private static final String[] DEVICE_COLUMN_NAME = {"�豸��ʶ","�豸����"};
	
	private static final String[] REGION_COLUMN_NAME = {"������"};

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
		JPanel managerPnl = getManagerPnl();//����Ա�������
		JPanel devicePnl = getDevicePnl();//�豸��ʶ�������
		tabPnl.addTab("����Ա", managerPnl);
		tabPnl.addTab("�豸��ʶ", devicePnl);
		
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
	 * �õ�����Ա�������
	 * @return
	 */
	private JPanel getManagerPnl(){
		//����Ա����
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton appendBtn = new JButton("���",imageRegistry.getImageIcon(ButtonConstants.APPEND));
		appendBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				showManagerDialog();
			}
		});
		
		JButton delBtn = buttonFactory.createButton(DELMANAGER);
		
		topPnl.add(appendBtn);
		topPnl.add(delBtn);

		
		//���
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
	 * �õ������������
	 * @return
	 */
	private JPanel getDevicePnl(){
		//�豸����
//		JPanel topPnl = new JPanel(new BorderLayout());
//		JPanel infoPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
//		JPanel panel = new JPanel(new GridBagLayout());
//		begintIPField = new IpAddressField();
//		endIPField = new IpAddressField();
//		begintIPField.setColumns(25);
//		endIPField.setColumns(25);
//		panel.add(new JLabel("��ʼIP:"),new GridBagConstraints(0,0,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
//		panel.add(begintIPField,new GridBagConstraints(1,0,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
//		panel.add(new StarLabel(),new GridBagConstraints(2,0,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
//		
//		panel.add(new JLabel("��ֹIP:"),new GridBagConstraints(0,1,1,1,0.0,0.0,
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
		JButton appendDeviceBtn = new JButton("���",imageRegistry.getImageIcon(ButtonConstants.APPEND));
		appendDeviceBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				showDeviceDialog();
			}
		});
		
		JButton delDeviceBtn = buttonFactory.createButton(DELSWITCHER);
		
		topPnl.add(appendDeviceBtn);
		topPnl.add(delDeviceBtn);
		
		//���
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
		//�����ݿ��ѯ���е��û�
		List<UserEntity> userList = new ArrayList<UserEntity>();
		
		List<List> dataList = managerModel.getDataList();
		if (dataList != null && dataList.size() > 0){
			for (List rowList : dataList){
				UserEntity userEntity = (UserEntity)rowList.get(2);//�õ����е��û���Ϣ
				userList.add(userEntity);
			}
		}

		SelectedManagerDialog dialog = new SelectedManagerDialog(userList,this,"�û��б�");
		dialog.setVisible(true);
	}
	
	private void showDeviceDialog(){
		List<NodeEntity> deviceList = new ArrayList<NodeEntity>();
		
		List<List> dataList = switcherModel.getDataList();
		if (dataList != null && dataList.size() > 0){
			for (List rowList : dataList){
				NodeEntity userEntity = (NodeEntity)rowList.get(2);//�õ����е��û���Ϣ
				deviceList.add(userEntity);
			}
		}
		
		SelectedDeviceDialog dialog = new SelectedDeviceDialog(deviceList,this,"�豸�б�");
		dialog.setVisible(true);
	}
	
	/**
	 * ��ѯ���е��豸
	 */
	private List<List> queryAllDevice(){
		List<List> objects = new ArrayList<List>();
		
		//���㽻����
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
		
		//���㽻����
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
		this.setTitle("�������");
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
	
		top = new DefaultMutableTreeNode("����",true);
		DefaultTreeModel model = new DefaultTreeModel(top);
		tree = new JTree(model);
	
		if (leafIcon != null) {
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setOpenIcon(openIcon);
			renderer.setClosedIcon(closedIcon);
			renderer.setLeafIcon(leafIcon);
			tree.setCellRenderer(renderer);
		}
	
		// ��������ѡ��ģʽ
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// ��������ѡ���¼�
		tree.addTreeSelectionListener(new AreaTreeSelectionListener());
		
		scrollTreePnl.getViewport().setLayout(new FlowLayout(FlowLayout.LEADING));
		scrollTreePnl.getViewport().add(tree);
		scrollTreePnl.getViewport().setBackground(Color.WHITE);
		scrollTreePnl.revalidate();
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		//�����ݿ��ѯ���еĵ���
		List<AreaEntity> areaEntityList = (List<AreaEntity>)remoteServer.getService().findAll(AreaEntity.class);
		//�����ݿ��ѯ���е��û�
		userEntityList = (List<UserEntity>)remoteServer.getService().findAll(UserEntity.class);
		
		//���ݲ�ѯ���ĵ����������ṹ
		initTreePnl(areaEntityList);
	}
	
	
	/**
	 * ͨ�������ݿ��ȡ���ݣ�����tree�Ľṹ
	 */
	private void initTreePnl(List<AreaEntity> areaEntityList){
		if(null == areaEntityList){
			return;
		}

		//������ṹ
		clearTree();
		
		//ͨ�������ݿ��ѯ��ֵ���¼���tree�ڵ�
		AreaEntity entity = null;
		for (int i = 0 ; i < areaEntityList.size(); i++){
			entity =  areaEntityList.get(i);
			setNode(entity);
		}
		
		//���ø��ڵ㲻�ɼ�
//		tree.setRootVisible(false);
		//չ�����ڵ�
		expandTree(tree);
	}
	
	/**
	 * ͨ���ݹ���ð����еĸ��ڵ��ҵ�
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
	 * �жϴ��ӽڵ��Ƿ��Ѿ������ڸ��ڵ��У�����Ѿ����ڣ��Ͳ���
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
	 * ͨ��list�ж��Ƿ��Ѿ����ڴ˽ڵ�
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
			JOptionPane.showMessageDialog(this, "����������","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		if(isExists(areaName)){
			JOptionPane.showMessageDialog(this, "���������ظ�������������", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		
		AreaEntity areaEntity = new AreaEntity();
		areaEntity.setName(areaName);
		
		AreaEntity selectedAreaEntity = getSelectedAreaEntity(tree);
		if (null != selectedAreaEntity){
			areaEntity.setSuperArea(selectedAreaEntity);
		}
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("����", this);
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
		
		int result = JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		if(!remoteServer.getService().deleteArea(selectedAreaEntity)){
			JOptionPane.showMessageDialog(this, "�����������ã�����ɾ��","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("ɾ��", this);
		remoteServer.getService().deleteEntities(selectedAreaEntity);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		queryData();
		refreshManagerTable(null);
	}
	
	public void saveManager(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
		if(null == node){
			JOptionPane.showMessageDialog(this, "��ѡ��Ҫ���ù���Ա������","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		//�õ���ѡ���AreaEntity
		List<AreaEntity> list = new ArrayList<AreaEntity>();
		final AreaEntity selectedAreaEntity = getSelectedAreaEntity(tree);
		if (null == selectedAreaEntity){
			JOptionPane.showMessageDialog(this, "��ѡ������","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		list.add(selectedAreaEntity);
		
		
		List<UserEntity> userList = new ArrayList<UserEntity>();
		
		List<List> dataList = managerModel.getDataList();
		for (List rowList : dataList){
			UserEntity userEntity = (UserEntity)rowList.get(2);//�õ����е��û���Ϣ
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

		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("����", this);
		remoteServer.getService().updateEntities(userList);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		refreshManagerTable(selectedAreaEntity);
	}
	
	@ViewAction(name=DELMANAGER, icon=ButtonConstants.DELETE, text=DELETE, desc="ɾ���������Ա",role=Constants.MANAGERCODE)
	public void delManager(){
		if(managerTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "��ѡ��Ҫɾ���Ĺ���Ա","��ʾ",JOptionPane.NO_OPTION);
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		int[] rows = managerTable.getSelectedRows();

		//����ѡ���userEntity���뵽userEntities��
		List<UserEntity> userEntities = new ArrayList<UserEntity>();
		for (int i = 0 ; i < rows.length; i++){
			List rowList = managerModel.getSelectedRow(rows[i]);
			UserEntity userEntity = (UserEntity) rowList.get(2);
			userEntities.add(userEntity);
		}
		
		//����userEntities�������ڵ�õ���areaEntity��userEntity��ɾ����
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
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("ɾ��", this);
		remoteServer.getService().updateEntities(userEntities);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);

		refreshManagerTable(selectedAreaEntity);
	}
	
	
	
	public void saveDevice(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
		if(null == node){
			JOptionPane.showMessageDialog(this, "��ѡ��Ҫ���ù���Ա������","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		//�õ���ѡ���AreaEntity
		List<AreaEntity> list = new ArrayList<AreaEntity>();
		final AreaEntity selectedAreaEntity = getSelectedAreaEntity(tree);
		if (null == selectedAreaEntity){
			JOptionPane.showMessageDialog(this, "��ѡ������","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		list.add(selectedAreaEntity);
		
		
		List<NodeEntity> nodeList = new ArrayList<NodeEntity>();
		
		List<List> dataList = switcherModel.getDataList();
		for (List rowList : dataList){
			NodeEntity nodeEntity = (NodeEntity)rowList.get(2);//�õ����е��豸��Ϣ
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

		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("����", this);
		remoteServer.getService().updateEntities(nodeList);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		refreshSwitcherTable(selectedAreaEntity);
	}
	

	
	@ViewAction(name=APPENDSWITCHER, icon=ButtonConstants.APPEND, text=APPEND, desc="���ӽ���������",role=Constants.MANAGERCODE)
	public void appendSwitcher(){
		//�õ���ѡ���AreaEntity
		List<AreaEntity> list = new ArrayList<AreaEntity>();
		final AreaEntity selectedAreaEntity = getSelectedAreaEntity(tree);
		if (null == selectedAreaEntity){
			JOptionPane.showMessageDialog(this, "��ѡ������","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		if (null == begintIPField.getIpAddress()
				|| "".equals(begintIPField.getIpAddress())
				|| null == endIPField.getIpAddress()
				|| "".equals(endIPField.getIpAddress())) {
			JOptionPane.showMessageDialog(this, "IP�����в������ֵ������������IP��ַ","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		if(!verifyIP(begintIPField.getIpAddress(),endIPField.getIpAddress())){
			JOptionPane.showMessageDialog(this, "����IP��ַ��������������IP��ַ","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		if(isIpSameValue()){
			return;
		}
		
		if(ClientUtils.isIllegal(begintIPField.getIpAddress())){
			JOptionPane.showMessageDialog(this, "��������ӷǷ�����,����������IP��ַ","��ʾ",JOptionPane.NO_OPTION);
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
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("����", this);
		remoteServer.getService().updateEntity(selectedAreaEntity);
		messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		refreshSwitcherTable(selectedAreaEntity);
		
		begintIPField.setText("");
		endIPField.setText("");
	}
	
	@ViewAction(name=DELSWITCHER, icon=ButtonConstants.DELETE, text=DELETE, desc="ɾ�������豸",role=Constants.MANAGERCODE)
	public void delSwitcher(){
		if(switchTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "��ѡ��Ҫɾ�����豸","��ʾ",JOptionPane.NO_OPTION);
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		int[] rows = switchTable.getSelectedRows();

		//����ѡ���nodeEntity���뵽nodeEntities��
		List<NodeEntity> nodeEntities = new ArrayList<NodeEntity>();
		for (int i = 0 ; i < rows.length; i++){
			List rowList = switcherModel.getSelectedRow(rows[i]);
			NodeEntity nodeEntity = (NodeEntity) rowList.get(2);
			nodeEntities.add(nodeEntity);
		}
		
		//����nodeEntities�������ڵ�õ���areaEntity��nodeEntity��ɾ����
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
		
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("ɾ��", this);
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
		
		String newBeginIP = begintIPField.getText().trim();//����ʼIP
		String newEndIP = endIPField.getText().trim();//����ֹIP
		
		for (int i = 0 ; i < ipSegmentList.size() ;i++){
			String oldBeginIp = ipSegmentList.get(i).getBeginIp();
			String oldEndIp = ipSegmentList.get(i).getEndIp();
			
			if(isSubNetworkSegment(oldBeginIp,newBeginIP,oldEndIp,newEndIP)){
				JOptionPane.showMessageDialog(this, "�����������ظ����������������","��ʾ",JOptionPane.NO_OPTION);
				isSame = true;
				break;
			}
		}
		
		return isSame;
	}
	
	//�ظ������ж�
	private boolean isSubNetworkSegment(String oldBeginIP,String newBeginIP,String oldEndIP,String newEndIP){
		boolean isSubNetworkSegment = false;
		
		if (!oldBeginIP.substring(0, oldBeginIP.lastIndexOf(".")).equals(
				newBeginIP.substring(0, newBeginIP.lastIndexOf(".")))) {//�ж�ip��ַ��ǰ��λ�Ƿ���ͬ
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
			JOptionPane.showMessageDialog(this, "��������ʼIP��ַ","��ʾ",JOptionPane.NO_OPTION);
			return null;
		}
		else if (null == endIp || endIp.trim().equals("")){
			JOptionPane.showMessageDialog(this, "��������ֹIP��ַ","��ʾ",JOptionPane.NO_OPTION);
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
	 * ͨ����ѡ���tree�ڵ�ˢ��table
	 * @param selectedAreaEntity
	 */
	@SuppressWarnings("unchecked")
	private void refreshManagerTable(AreaEntity selectedAreaEntity){
		List<List> dataList = new ArrayList<List>();
		List<UserEntity> userEntityLists = areaEntityToUserEntity(selectedAreaEntity);
		for (UserEntity userEntity : userEntityLists){
			List rowList = new ArrayList();
			rowList.add(0, userEntity.getUserName());//��һ��Ϊ����
			rowList.add(1, userEntity.getRole().getRoleName());//�ڶ���Ϊ��ɫ
			rowList.add(2,userEntity);//������ΪUserEntity
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
	 * ͨ������õ������µ��û�
	 * @param selectedAreaEntity
	 * @return
	 */
	private List<UserEntity> areaEntityToUserEntity(AreaEntity selectedAreaEntity){
		userEntityList = (List<UserEntity>)remoteServer.getService().findAll(UserEntity.class);
		List<UserEntity> userList = new ArrayList<UserEntity>();
		
		if (null != selectedAreaEntity){
			long selectID = selectedAreaEntity.getId();
			//���±��
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
		 * ��дtoString����
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
		 * @param list  ���������е��û���Ϣ�б�
		 * @param type  �ǲ�������Ա���ǽ�����
		 * @param viewPart  ����ͼ
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
			JButton saveBtn = new JButton("����", imageRegistry.getImageIcon(ButtonConstants.SAVE));
			JButton closeSelectBtn = new JButton("�ر�", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
			bottomPnl.add(saveBtn);
			bottomPnl.add(closeSelectBtn);
			
			panel.add(scrollPnl,BorderLayout.CENTER);
			panel.add(bottomPnl,BorderLayout.SOUTH);
			
			//����ֵ
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
				
			//��ѯ���е��û���Ϣ
			List<UserEntity> userLists = (List<UserEntity>)remoteServer.getService().findAll(UserEntity.class);
				
			for(Object object : userLists){
				UserEntity userEntity = (UserEntity)object;
				List rowList = new ArrayList();
				rowList.add(0,userEntity.getUserName());//����
				rowList.add(1,userEntity.getRole().getRoleName());//��ɫ
				rowList.add(2,userEntity);
					
				dataList.add(rowList);
			}
			((ManagerTableModel)tableModel).setDataList(dataList);
			((ManagerTableModel)tableModel).fireTableDataChanged();
		}
		
		/**
		 * ����
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
			rowList.add(0, userEntity.getUserName());//��һ��Ϊ����
			rowList.add(1, userEntity.getRole().getRoleName());//�ڶ���Ϊ��ɫ
			rowList.add(2,userEntity);//������ΪUserEntity
			dataList.add(rowList);
		}
		
		managerModel.setDataList(dataList);
		
		//���浽���ݿ�
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
		 * @param list  ���������е��û���Ϣ�б�
		 * @param type  �ǲ�������Ա���ǽ�����
		 * @param viewPart  ����ͼ
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
			JButton saveBtn = new JButton("����", imageRegistry.getImageIcon(ButtonConstants.SAVE));
			JButton closeSelectBtn = new JButton("�ر�", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
			bottomPnl.add(saveBtn);
			bottomPnl.add(closeSelectBtn);
			
			panel.add(scrollPnl,BorderLayout.CENTER);
			panel.add(bottomPnl,BorderLayout.SOUTH);
			
			//����ֵ
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
			//�����ݿ��ѯ���е��豸
			List<List> dataList = queryAllDevice();
			((SwitcherTableModel)tableModel).setDataList(dataList);
			((SwitcherTableModel)tableModel).fireTableDataChanged();
		}
		
		/**
		 * ����
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
			String deviceTag = "";   //�豸��ʶ
			String name = "";  //�豸����
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
			
			rowList.add(0, deviceTag);//��һ��Ϊ����
			rowList.add(1, name);//�ڶ���Ϊ��ɫ
			rowList.add(2,nodeEntity);//������ΪUserEntity
			dataList.add(rowList);
		}
		
		switcherModel.setDataList(dataList);
		
		//���浽���ݿ�
		saveDevice();
	}
	
	public void setSelectSwitcherList(List<SynchDevice> selectSwitcherList) {
		
	}
	
	
	
	
	
	
	
	

	
	
}