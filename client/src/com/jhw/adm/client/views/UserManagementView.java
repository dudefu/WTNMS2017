package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.MODIFY;
import static com.jhw.adm.client.core.ActionConstants.QUERY;
import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.swing.HyalineDialog;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.system.PersonInfo;
import com.jhw.adm.server.entity.system.RoleEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(UserManagementView.ID)
@Scope(Scopes.DESKTOP)
public class UserManagementView extends ViewPart {
	public static final String ID = "userManagementView";
	private static final long serialVersionUID = 1L;
	private static final String APPEND = "append";
	private static final Logger LOG = LoggerFactory.getLogger(UserManagementView.class);

	private HyalineDialog hyalineDialog;
	
	private final JSplitPane splitCenterPnl = new JSplitPane();
	
	//��ߵ����
	private final JPanel leftPnl = new JPanel();
	//����ϱߵ����
	private final JPanel leftTopPnl = new JPanel();
	private final JTextField queryFld = new JTextField();
	private JButton queryBtn;
	//����м�����
	private JScrollPane scrollTreePnl = null;
	private JTree tree = null;
	private final DefaultMutableTreeNode top = new DefaultMutableTreeNode();

	//�ұߵ����
	private final JTabbedPane rightTabPnl = new JTabbedPane();
	

	//�ұ��ϱߵ����
	private JButton addBtn;
	private JButton modifyBtn ;
	private JButton deleteBtn;
	
	//�ұ��м�����
	private final JLabel userLbl = new JLabel();//�û�
	private final JTextField userFld = new JTextField();
	
	private final JLabel pwdLbl = new JLabel();//����
	private final JPasswordField pwdFld = new JPasswordField();
	private static final String PASSWORD_RANGE = "6-16���ַ�";
	
	private final JLabel roleLbl = new JLabel(); //��ɫ
	private final JComboBox roleCombox = new JComboBox();
	
	private final JLabel nameLbl = new JLabel();//����
	private final JTextField nameFld = new JTextField();
	
	private final JLabel mobileLbl = new JLabel();//�ֻ�
	private final NumberField mobileFld = new NumberField(11,0,true);
	
	private final JLabel mailLbl = new JLabel();//����
	private final JTextField mailFld = new JTextField();
	
	private final JLabel warningLevelLbl = new JLabel("");
	private final JCheckBox generalChk = new JCheckBox();
	private final JCheckBox noticeChk = new JCheckBox();
	private final JCheckBox seriousChk = new JCheckBox();
	private final JCheckBox urgentChk = new JCheckBox();
	
	private final JLabel warningModeLbl = new JLabel();
	private final JCheckBox emailChk = new JCheckBox();
	private final JCheckBox smsChk = new JCheckBox();
	
	//�ұ���������
	private final JPanel rightBottomPnl = new JPanel();
	private JButton saveBtn ; //����
	private JButton closeBtn;
	
	private List<UserEntity> userEntityList = null;
	private List<RoleEntity> roleEntityList = null;
	
	private ButtonFactory buttonFactory;
	
	//��ʱ��Ӳ���ʱoperationΪ1����Ϊ�޸Ĳ���ʱoperationΪ2������operationΪ0
	private static final int OPERATION_ADD = 1;
	private static final int OPERATION_MODIFY = 2;
	private static final int ZERO = 0;
	private int operation;
	
	private UserEntity selectUserEntity = null;
	
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;	
	
	@Resource(name=UserDeviceManageView.ID)
	private UserDeviceManageView deviceManageView;
	
	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this);
		hyalineDialog = new HyalineDialog(this);
		initLeftPnl();
		initRightPnl();
		
		splitCenterPnl.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitCenterPnl.setResizeWeight(0.18);
		splitCenterPnl.setLeftComponent(leftPnl);
		splitCenterPnl.setRightComponent(rightTabPnl);
		
		this.setLayout(new BorderLayout());
		this.add(splitCenterPnl,BorderLayout.CENTER);
		this.setViewSize(660, 480);
		
		deviceManageView.setUserView(this);
		setResource();
	}
	
	private void initLeftPnl(){
		JToolBar searchBar = new JToolBar();
		queryBtn = buttonFactory.createButton(QUERY);
		queryFld.setDocument(new TextFieldPlainDocument(queryFld,true));
		searchBar.add(queryFld);
		searchBar.add(queryBtn);
		searchBar.setFloatable(false);
		leftTopPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		leftTopPnl.add(searchBar);
		
		top.setUserObject("�û����");
		scrollTreePnl = new JScrollPane();
		
		leftPnl.setLayout(new BorderLayout());
		leftPnl.add(searchBar,BorderLayout.NORTH);
		leftPnl.add(scrollTreePnl,BorderLayout.CENTER);
	}
	
	private void initRightPnl(){
		JToolBar toolBar = new JToolBar();
		addBtn = buttonFactory.createButton(APPEND);
		modifyBtn = buttonFactory.createButton(MODIFY);
		deleteBtn = buttonFactory.createButton(DELETE);
		toolBar.add(addBtn);
		toolBar.add(modifyBtn);
		toolBar.add(deleteBtn);
		toolBar.setFloatable(false);
		JPanel userTopPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		userTopPnl.add(toolBar);
		
		
		JPanel userCenterPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel container = new JPanel(new GridBagLayout());
		container.add(userLbl,new GridBagConstraints(0,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,0,0),0,0));
		container.add(userFld,new GridBagConstraints(1,0,3,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		container.add(new StarLabel(),new GridBagConstraints(4,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		
		container.add(pwdLbl,new GridBagConstraints(0,1,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,0,0),0,0));
		container.add(pwdFld,new GridBagConstraints(1,1,3,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
		container.add(new StarLabel("(" + PASSWORD_RANGE + ")"),new GridBagConstraints(4,1,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		
		container.add(roleLbl,new GridBagConstraints(0,2,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,0,0),0,0));
		container.add(roleCombox,new GridBagConstraints(1,2,2,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
		
		container.add(nameLbl,new GridBagConstraints(0,3,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,0,0),0,0));
		container.add(nameFld,new GridBagConstraints(1,3,3,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
		
		container.add(mobileLbl,new GridBagConstraints(0,4,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,0,0),0,0));
		container.add(mobileFld,new GridBagConstraints(1,4,3,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
		
		container.add(mailLbl,new GridBagConstraints(0,5,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,0,0),0,0));
		container.add(mailFld,new GridBagConstraints(1,5,3,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
		
		userCenterPnl.add(container);
		
		
		JPanel warningLevelPnl = new JPanel();
		warningLevelPnl.setLayout(new SpringLayout());
		warningLevelPnl.add(generalChk);
		warningLevelPnl.add(noticeChk);
		warningLevelPnl.add(seriousChk);
		warningLevelPnl.add(urgentChk);
		SpringUtilities.makeCompactGrid(warningLevelPnl, 1,4, 16, 16, 25, 10);
		
		JPanel middlePnl = new JPanel(new SpringLayout());
		middlePnl.add(emailChk);
		middlePnl.add(smsChk);
		SpringUtilities.makeCompactGrid(middlePnl, 1,2, 16, 6, 25, 16);
		JPanel warningModePnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		warningModePnl.add(middlePnl);
		
		
		JPanel warningPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel warningMiddlePnl = new JPanel(new GridBagLayout());
		warningMiddlePnl.setBorder(BorderFactory.createTitledBorder("�澯����"));
		warningMiddlePnl.add(warningLevelLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));//
		warningMiddlePnl.add(warningLevelPnl,new GridBagConstraints(1,1,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,25,0,0),0,0));//
		warningMiddlePnl.add(warningModeLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));//
		warningMiddlePnl.add(warningModePnl,new GridBagConstraints(1,2,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,20,0,0),0,0));//
		warningPnl.add(warningMiddlePnl);
		
		
		JPanel centerPnl = new JPanel(new BorderLayout());
		centerPnl.add(userCenterPnl,BorderLayout.NORTH);
		centerPnl.add(warningPnl,BorderLayout.CENTER);
		
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		rightBottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		rightBottomPnl.add(saveBtn);
		rightBottomPnl.add(closeBtn);
		
		JPanel addUserPnl = new JPanel();
		addUserPnl.setLayout(new BorderLayout());
		addUserPnl.add(toolBar,BorderLayout.NORTH);
		addUserPnl.add(centerPnl,BorderLayout.CENTER);
		addUserPnl.add(rightBottomPnl,BorderLayout.SOUTH);
		
		rightTabPnl.add("�û���Ϣ",addUserPnl);
		rightTabPnl.add("�����豸",deviceManageView);
	}
	

	private void setResource(){
		this.setTitle(localizationManager.getString("UserManagement"));
		queryFld.setColumns(20);
		userFld.setColumns(25);
		pwdFld.setColumns(25);
		nameFld.setColumns(25);
		mobileFld.setColumns(25);
		mailFld.setColumns(25);
		roleCombox.setPreferredSize(new Dimension(100,roleCombox.getPreferredSize().height));
		
		userFld.setDocument(new TextFieldPlainDocument(userFld));
		nameFld.setDocument(new TextFieldPlainDocument(nameFld,true));
		mailFld.setDocument(new TextFieldPlainDocument(mailFld));
		
		userLbl.setText("�û�");
		pwdLbl.setText("����");
		roleLbl.setText("��ɫ");
		nameLbl.setText("����");
		mobileLbl.setText("�ֻ�");
		mailLbl.setText("����");
//		detailLbl.setForeground(new Color(35,175,98));
		
		warningLevelLbl.setText("�ȼ�");
		warningModeLbl.setText("��ʽ");
		generalChk.setText("��ͨ");
		noticeChk.setText("֪ͨ");
		seriousChk.setText("����");
		urgentChk.setText("����");
		emailChk.setText("�����ʼ�");
		smsChk.setText("����");
		
		setCloseButton(deviceManageView.getCloseButton());
		
		setEnable(false);
		
		queryBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				locationTreeNode(queryFld.getText());
			}
		});
	}
	
	/**
	 * ��λ�����ڵ�
	 * @param nodeName
	 */
	private void locationTreeNode(String nodeName){
		DefaultMutableTreeNode node = searchNode(nodeName);
        if (node != null) {
          TreeNode[] nodes = ((DefaultTreeModel)tree.getModel()).getPathToRoot(node);
          TreePath path = new TreePath(nodes);
          tree.scrollPathToVisible(path);
          tree.setSelectionPath(path);
        } else if("".equals(nodeName)){
        	JOptionPane.showMessageDialog(this, "����Ϊ�գ��������û���","��ʾ",JOptionPane.NO_OPTION);
			return;
        }else{
        	JOptionPane.showMessageDialog(this, "û�и��û��������²�ѯ","��ʾ",JOptionPane.NO_OPTION);
			return;
        }
	}
	
	public JTree getTree(){
		return this.tree;
	}
	
	/**
	 * ����nodeStr��ѯ���ڵ�
	 * @param nodeStr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DefaultMutableTreeNode searchNode(String nodeStr) {
		DefaultMutableTreeNode node = null;
		Enumeration e = top.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if (nodeStr.equalsIgnoreCase(node.getUserObject().toString())) {
				return node;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		//�Ȳ�ѯ��ɫ
		roleEntityList = (List<RoleEntity>)remoteServer.getService().findAll(RoleEntity.class);
		if (null == roleEntityList){
			return;
		}
		//���ݲ�ѯ���Ľ�ɫ��Ϣ��ʼ����ߵ�tree�������б��roleCombox
		initTreeRootNode();
		
		userEntityList = (List<UserEntity>)remoteServer.getService().findAll(UserEntity.class);
		if (null == userEntityList){
			return;
		}
		
		//����tree�еĸ�����ɫ�µ��û���
		setTreeChildValue();
		
		deviceManageView.refreshSwitcherTable(null);
	}
	
	private void initTreeRootNode(){
		top.removeAllChildren();
		roleCombox.removeAllItems();
		for(int i = 0 ; i < roleEntityList.size(); i++){
			RoleEntityObject entity = new RoleEntityObject(roleEntityList.get(i));
			DefaultMutableTreeNode node = new DefaultMutableTreeNode();
			node.setUserObject(entity);
			top.add(node);
//			if(roleEntityList.get(i).getRoleCode() != 1000){
				roleCombox.addItem(entity);
//			}
		}
		tree = new JTree(top);
		
		ImageIcon leafIcon = imageRegistry
			.getImageIcon(ButtonConstants.SINGLE_USER);
		ImageIcon openIcon = imageRegistry
			.getImageIcon(ButtonConstants.GROUP_USER);
		ImageIcon closedIcon = imageRegistry
			.getImageIcon(ButtonConstants.GROUP_USER);
		if (leafIcon != null) {
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setOpenIcon(openIcon);
			renderer.setClosedIcon(closedIcon);
			renderer.setLeafIcon(leafIcon);
			tree.setCellRenderer(renderer);
		}
		
		
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		scrollTreePnl.getViewport().add(tree);
		
		// ��������ѡ���¼�
		tree.addTreeSelectionListener(new UserTreeSelectionListener());
	}
	
	/**
	 * �����ݿ��еõ����û����õ���Ӧ�Ľ�ɫ����
	 */
	private void setTreeChildValue(){
		removeAllChildren();
		
		int size = userEntityList.size();
		for (int i = 0 ;i < size; i++){
			UserEntity userEntity = userEntityList.get(i);
			createNode(userEntity);
		}
		
		expandTree(this.tree);
	}
	
	private void createNode(UserEntity userEntity) {
		long roleID = userEntity.getRole().getId();
		
		int count = top.getChildCount();
		for (int i = 0 ; i < count; i++){
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode)top.getChildAt(i));
			Object object = node.getUserObject();
			if (object instanceof RoleEntityObject){
				long id = ((RoleEntityObject) object).getRoleEntity().getId();
				if (id == roleID){
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
					childNode.setUserObject(new UserEntityObject(userEntity));
					node.add(childNode);
					break;
				}
			}
		}
	}
	
	/**
	 * �Ƴ���ɫ����������û����ӽڵ�
	 */
	private void removeAllChildren(){
		int count = top.getChildCount();
		for (int i = 0 ; i < count; i++){
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode)top.getChildAt(i));
			Object object = node.getUserObject(); 
			if (object instanceof RoleEntityObject){
				node.removeAllChildren();
			}
		}
	}
	
	private void setValue(Object object){
		clear();
		if (object instanceof RoleEntityObject){
			roleCombox.setSelectedItem(object);
		}else if(object instanceof UserEntityObject){
			UserEntity userEntity = ((UserEntityObject)object).getUserEntity();
			String user = userEntity.getUserName();
			String pwd = userEntity.getPassword();
			String name = userEntity.getPersonInfo().getName();
			String mobile = userEntity.getPersonInfo().getMobile();
			String email = userEntity.getPersonInfo().getEmail();
			
			RoleEntityObject roleObject = getRoleEntityObject(userEntity);
			
//			this.user = user;
			userFld.setText(user);
			pwdFld.setText(pwd);
			roleCombox.setSelectedItem(roleObject);
			nameFld.setText(name);
			mobileFld.setText(mobile);
			mailFld.setText(email);
			
			selectUserEntity = userEntity;
			
			setWarningInfo();
		}
	}
	
	/**
	 * ������������ѡ�е�UserEntity��ʾ�澯������Ϣ
	 * @param e
	 */
	private void setWarningInfo(){
		setCheckBoxDisable();
		
		if (null == selectUserEntity){
			return;
		}
		
		//ͨ��entity��ѯ�õ��澯�ȼ�
		String levelStr = selectUserEntity.getCareLevel();
		if (null != levelStr){
			String[] levels = levelStr.split(",");
			for (int i = 0 ; i < levels.length; i++){
				if (StringUtils.isNumeric(levels[i])){
					switch(NumberUtils.toInt(levels[i])){
						case Constants.URGENCY:
							urgentChk.setSelected(true);
							break;
						case Constants.SERIOUS:
							seriousChk.setSelected(true);
							break;
						case Constants.INFORM:
							noticeChk.setSelected(true);
							break;
						case Constants.GENERAL:
							generalChk.setSelected(true);
							break;
					}
				}
			}
		}
		
		//ͨ��entity��ѯ�õ��澯��ʽ
		String styleStr = selectUserEntity.getWarningStyle();
		if (null != styleStr){
			String[] styles = styleStr.split(",");
			for (int j = 0 ; j < styles.length; j++){
				if (Constants.SMS.equals(styles[j])){
					smsChk.setSelected(true);
				}
				else if (Constants.EMAIL.equals(styles[j])){
					emailChk.setSelected(true);
				}
			}
		}
	}
	
	private void setCheckBoxDisable(){
		urgentChk.setSelected(false);
		seriousChk.setSelected(false);
		noticeChk.setSelected(false);
		generalChk.setSelected(false);
		
		emailChk.setSelected(false);
		smsChk.setSelected(false);
	}
	
	
	/**
	 * ͨ��UserEntity�õ����õ�RoleEntityObject;
	 * @param userEntity
	 * @return
	 */
	private RoleEntityObject getRoleEntityObject(UserEntity userEntity){
		RoleEntityObject roleObject = null;
		long id = userEntity.getRole().getId();
		for(int i = 0 ; i < roleCombox.getItemCount(); i++){
			roleObject = (RoleEntityObject)roleCombox.getItemAt(i);
			
			long roleID = roleObject.getRoleEntity().getId();
			if (id == roleID){
				break;
			}
		}
		return roleObject;
	}
	
	/**
	 * ��ѯ����
	 */
	@ViewAction(name="", icon=ButtonConstants.QUERY,desc="��ѯ�û�",role=Constants.MANAGERCODE)
	public void query(){
		
	}
	
	/**
	 * ���Ӳ���
	 */
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND,desc="����û�",role=Constants.ADMINCODE)
	public void append(){
		operation = OPERATION_ADD;
//		user = "";
		setEnable(true);
		
		userFld.setText("");
		pwdFld.setText("");
		nameFld.setText("");
		mobileFld.setText("");
		mailFld.setText("");
		
		generalChk.setSelected(false);
		noticeChk.setSelected(false);
		seriousChk.setSelected(false);
		urgentChk.setSelected(false);
		emailChk.setSelected(false);
		smsChk.setSelected(false);
	}
	
	public UserEntity getSelectedUserEntity(){
		UserEntity selectUserEntity = null;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if(null == node){
			return selectUserEntity;
		}
		
		if (node.getUserObject() instanceof UserEntityObject){
			selectUserEntity = ((UserEntityObject)node.getUserObject()).getUserEntity();
		}

		return selectUserEntity;
	}
	
	
	/**
	 * �޸Ĳ���
	 */
	@ViewAction(name=MODIFY, icon=ButtonConstants.MODIFY,desc="�޸��û�",role=Constants.ADMINCODE)
	public void modify(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if(null == node){
			JOptionPane.showMessageDialog(this, "��ѡ����Ҫ�޸ĵ��û�","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		Object object = node.getUserObject();
		if (!(object instanceof UserEntityObject)){
			JOptionPane.showMessageDialog(this, "��ѡ����Ҫ�޸ĵ��û�","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		operation = OPERATION_MODIFY;
		setEnable(true);
		if (((UserEntityObject) object).getUserEntity().getRole().getRoleCode() == Constants.ADMINCODE) {
			roleCombox.setEnabled(false);
		}
		setValue(node.getUserObject());
	}
	
	/**
	 * ɾ������
	 */
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="ɾ���û�",role=Constants.ADMINCODE)
	public void delete(){
		UserEntity userEntity = null;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if(null == node){
			JOptionPane.showMessageDialog(this, "��ѡ��Ҫɾ�����û�","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		Object object = node.getUserObject();
		if (!(object instanceof UserEntityObject)){
			JOptionPane.showMessageDialog(this, "��ѡ��Ҫɾ�����û�","��ʾ",JOptionPane.NO_OPTION);
			return;
		}else{
			userEntity = ((UserEntityObject)object).getUserEntity();
		}
		
		if(userEntity.getRole().getRoleCode() == Constants.ADMINCODE){
			JOptionPane.showMessageDialog(this, "������ɾ����������Ա","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		if(clientModel.getCurrentUser().getUserName().equals(userEntity.getUserName())){
			JOptionPane.showMessageDialog(this, "������ɾ���Լ�","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}

		Task deleteTask = new DeleteRequestTask(userEntity);
		showMessageDialog(deleteTask, "ɾ��");
	}
	
	private class DeleteRequestTask implements Task{

		private final UserEntity userEntity;
		public DeleteRequestTask(UserEntity userEntity){
			this.userEntity = userEntity;
		}
		
		@Override
		public void run() {
			try{
				deleteNodeEntity(userEntity);
				
				remoteServer.getService().deleteEntity(userEntity);
			}catch(Exception e){
				strategy.showErrorMessage("ɾ���û��쳣");
				doAfterSave();
				operation = ZERO;
				LOG.error("UserManagementView.deleteEntity() error", e);
			}
			strategy.showNormalMessage("ɾ���û��ɹ�");
			doAfterSave();
			operation = ZERO;
		}
	}
	
	/**
	 * �Ȱ�nodeEntity�а���userEntity��ȥ��
	 */
	private void deleteNodeEntity(UserEntity userEntity){
		try{
			if (deviceManageView.getDeviceModel() != null){
				List deviceList = deviceManageView.getDeviceModel().getDataList();
				if (deviceList != null && deviceList.size() > 0){
					List<NodeEntity> nodeEntityList = new ArrayList<NodeEntity>();
					for (int i = 0 ; i < deviceList.size(); i++){
						NodeEntity nodeEntity = (NodeEntity)((List)deviceList.get(i)).get(2);
						Set<UserEntity> userEntities = nodeEntity.getUsers();
						if (userEntities != null && userEntities.size() > 0){
							Iterator iterator = userEntities.iterator();
							while (iterator.hasNext()){
								UserEntity entity = (UserEntity)iterator.next();
								if ((long)entity.getId() == (long)userEntity.getId()){
									userEntities.remove(entity);
									nodeEntity.setUsers(userEntities);
									
									nodeEntityList.add(nodeEntity);
									break;
								}
							}
						}
					}
					
					remoteServer.getService().updateEntities(nodeEntityList);
				}
			}
		}
		catch(Exception e){
			this.LOG.error("deleteNodeEntity() is Exception",e);
		}
	}
	
	/**
	 * �������
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="�����û�",role=Constants.ADMINCODE)
	public void save(){
		if(!this.isValids()){
			return;
		}
		
		if(operation == OPERATION_ADD){
			if(isExists(userFld.getText())){
				JOptionPane.showMessageDialog(this, "�û��������ظ�������������", "��ʾ", JOptionPane.NO_OPTION);
				return;
			}
			if (((RoleEntityObject) roleCombox.getSelectedItem())
					.getRoleEntity().getRoleCode() == Constants.ADMINCODE) {
				JOptionPane.showMessageDialog(this, "��������Ա����Ψһ��������������������Ա", "��ʾ", JOptionPane.NO_OPTION);
				return;
			}
		}else if(operation == OPERATION_MODIFY){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			if(!(node.getUserObject() instanceof UserEntityObject)){
				return;
			}
			UserEntity userEntity = ((UserEntityObject)node.getUserObject()).getUserEntity();
			if(Constants.ADMINCODE != userEntity.getRole().getRoleCode()){
				if (((RoleEntityObject) roleCombox.getSelectedItem())
						.getRoleEntity().getRoleCode() == Constants.ADMINCODE) {
					JOptionPane.showMessageDialog(this, "��������Ա����Ψһ��������������������Ա", "��ʾ", JOptionPane.NO_OPTION);
					return;
				}
			}
		}
		
		String userName = "";
		if (operation == OPERATION_ADD){
			userName = userFld.getText();
			String pwd = String.valueOf(pwdFld.getPassword());
			RoleEntity roleEntity = ((RoleEntityObject)roleCombox.getSelectedItem()).getRoleEntity();
			String name = nameFld.getText();
			String mobile = mobileFld.getText();
			String email = mailFld.getText();
			
			UserEntity userEntity = new UserEntity();
	
			PersonInfo personInfo = new PersonInfo();
			personInfo.setName(name);
			personInfo.setMobile(mobile);
			personInfo.setEmail(email);
			
			userEntity.setUserName(userName);
			userEntity.setPassword(pwd);
			
			InetAddress localHost = null;
			try {
				localHost = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			String address = localHost.getHostAddress();
			userEntity.setClientIp(address);
			
			userEntity.setRole(roleEntity);
			userEntity.setPersonInfo(personInfo);
			
			//�õ��澯�ȼ�
			String levels = "";
			if (urgentChk.isSelected()){
				levels =levels + Constants.URGENCY + ",";
			}
			if (seriousChk.isSelected()){
				levels =levels + Constants.SERIOUS + ",";
			}
			if (noticeChk.isSelected()){
				levels =levels + Constants.INFORM + ",";
			}
			if (generalChk.isSelected()){
				levels =levels + Constants.GENERAL + ",";
			}
			
			if (levels.length() > 0){
				levels = levels.substring(0,levels.length()-1);
			}
			
			//�õ��澯��ʽ
			String styles = "";
			if (emailChk.isSelected()){
				styles = styles + Constants.EMAIL + ",";
			}
			if (smsChk.isSelected()){
				styles = styles + Constants.SMS + ",";
			}
			if (styles.length() > 0){
				styles = styles.substring(0,styles.length() -1);
			}
			
			userEntity.setCareLevel(levels);
			userEntity.setWarningStyle(styles);
			
			Task saveTask = new SaveRequestTask(userEntity, userName);
			showMessageDialog(saveTask, "���");
		}else if (operation == OPERATION_MODIFY){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			if(!(node.getUserObject() instanceof UserEntityObject)){
				return;
			}
			UserEntity userEntity = ((UserEntityObject)node.getUserObject()).getUserEntity();
			
			RoleEntity roleEntity = ((RoleEntityObject)roleCombox.getSelectedItem()).getRoleEntity();
			
			PersonInfo personInfo = userEntity.getPersonInfo();
			String name = nameFld.getText();
			String mobile = mobileFld.getText();
			String email = mailFld.getText();
			personInfo.setName(name);
			personInfo.setMobile(mobile);
			personInfo.setEmail(email);
			
			userEntity.setRole(roleEntity);
			userEntity.setPersonInfo(personInfo);
			
			userName = userEntity.getUserName();
			
			
			//�õ��澯�ȼ�
			String levels = "";
			if (urgentChk.isSelected()){
				levels =levels + Constants.URGENCY + ",";
			}
			if (seriousChk.isSelected()){
				levels =levels + Constants.SERIOUS + ",";
			}
			if (noticeChk.isSelected()){
				levels =levels + Constants.INFORM + ",";
			}
			if (generalChk.isSelected()){
				levels =levels + Constants.GENERAL + ",";
			}
			
			if (levels.length() > 0){
				levels = levels.substring(0,levels.length()-1);
			}
			
			//�õ��澯��ʽ
			String styles = "";
			if (emailChk.isSelected()){
				styles = styles + Constants.EMAIL + ",";
			}
			if (smsChk.isSelected()){
				styles = styles + Constants.SMS + ",";
			}
			if (styles.length() > 0){
				styles = styles.substring(0,styles.length() -1);
			}
			
			userEntity.setCareLevel(levels);
			userEntity.setWarningStyle(styles);

			Task modifyTask = new ModifyRequestTask(userEntity, userName);
			showMessageDialog(modifyTask, "�޸�");
		}
	}
	
	private void doAfterSave(){
		queryData();
		clear();
		setEnable(false);
	}
	
	private class SaveRequestTask implements Task{

		private final UserEntity userEntity;
		private final String userName;
		public SaveRequestTask(UserEntity userEntity,String userName){
			this.userEntity = userEntity;
			this.userName = userName;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().saveEntity(userEntity);
			}catch(Exception e){
				strategy.showErrorMessage("����û��쳣");
				doAfterSave();
				locationTreeNode(this.userName);
				LOG.error("UserManagementView.saveEntity() error", e);
			}
			strategy.showNormalMessage("����û��ɹ�");
			doAfterSave();
			locationTreeNode(this.userName);

		}
	}
	
	private class ModifyRequestTask implements Task{

		private final UserEntity userEntity;
		private final String userName;
		public ModifyRequestTask(UserEntity userEntity,String userName){
			this.userEntity = userEntity;
			this.userName = userName;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().updateEntity(userEntity);
			}catch(Exception e){
				strategy.showErrorMessage("�޸��û��쳣");
				LOG.error("UserManagementView.saveEntity() error", e);
				doAfterSave();
				locationTreeNode(this.userName);
			}
			strategy.showNormalMessage("�޸��û��ɹ�");
			doAfterSave();
			locationTreeNode(this.userName);
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("��ʾ",0,1,this,operation,true);
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

	
	@SuppressWarnings("unchecked")
	private boolean isExists(String userName){
		boolean isExists = false;
		DefaultMutableTreeNode node = null;
		Enumeration e = top.breadthFirstEnumeration();
		while(e.hasMoreElements()){
			node = (DefaultMutableTreeNode) e.nextElement();
			if(userName.equals(node.getUserObject().toString())){
				isExists = true;
				return isExists;
			}
		}
		return isExists;
	}
	
	private boolean isValids(){
		boolean bool = true;
		String usr = userFld.getText().trim();
		if (null == usr || "".equals(usr)){
			JOptionPane.showMessageDialog(this, "�������û���","��ʾ",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}
		
		String password = String.valueOf(pwdFld.getPassword());
		if (null == password || "".equals(password)){
			JOptionPane.showMessageDialog(this, "����������","��ʾ",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}else if(password.length() < 6 || password.length() > 16){
			JOptionPane.showMessageDialog(this, "���볤�ȷ�ΧΪ" + PASSWORD_RANGE + "������������ȷ������","��ʾ",JOptionPane.NO_OPTION);
			return false;
		}
		
		String regex = "[a-zA-Z0-9]{1,}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher =pattern.matcher(password);
		if(!matcher.matches()){
			JOptionPane.showMessageDialog(this, "����ֻ���������ֺ���ĸ������������","��ʾ",JOptionPane.NO_OPTION);
			return false;
		}
		
		String mailAddress = mailFld.getText().trim();
		if(!StringUtils.isBlank(mailAddress)){
		    String mailRegex = "\\b(^['_A-Za-z0-9-]+(\\.['_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
			Pattern p=Pattern.compile(mailRegex);
			Matcher m=p.matcher(mailAddress);
			if (!m.matches()){
				JOptionPane.showMessageDialog(this, "���������������������","��ʾ",JOptionPane.NO_OPTION);
				bool = false;
				return bool;
			}
		}
		return bool;
	}
	
	/**
	 * ��ʼ����ͼ�е������
	 */
	private void clear(){
		userFld.setText("");
		pwdFld.setText("");
		roleCombox.setSelectedIndex(0);
		nameFld.setText("");
		mobileFld.setText("");
		mailFld.setText("");
		
		generalChk.setSelected(false);
		noticeChk.setSelected(false);
		seriousChk.setSelected(false);
		urgentChk.setSelected(false);
		emailChk.setSelected(false);
		smsChk.setSelected(false);
	}
	
	private void setEnable(boolean enable){
		if (operation == OPERATION_MODIFY){
			userFld.setEnabled(false);
			pwdFld.setEnabled(false);
		}
		else{
			userFld.setEnabled(enable);
			pwdFld.setEnabled(enable);
		}
		roleCombox.setEnabled(enable);
		nameFld.setEnabled(enable);
		mobileFld.setEnabled(enable);
		mailFld.setEnabled(enable);
		
		generalChk.setEnabled(enable);
		noticeChk.setEnabled(enable);
		seriousChk.setEnabled(enable);
		urgentChk.setEnabled(enable);
		emailChk.setEnabled(enable);
		smsChk.setEnabled(enable);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		hyalineDialog.dispose();
	}
	
	public class UserTreeSelectionListener implements TreeSelectionListener{
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			selectUserEntity = null;
			clear();
			JTree userTree = (JTree) e.getSource();
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode)userTree.getLastSelectedPathComponent();
			if (node.getUserObject() instanceof RoleEntityObject 
					||node.getUserObject() instanceof UserEntityObject){
				operation = ZERO;
				setEnable(false);
				setValue(node.getUserObject());	
				
				if (node.getUserObject() instanceof RoleEntityObject){
					deviceManageView.refreshSwitcherTable(null);
					return;
				}
				
				final Runnable queryAndSetPortInformationThread = new Runnable() {
					@Override
					public void run() {
						if(node.getUserObject() instanceof UserEntityObject){
							UserEntity userEntity = ((UserEntityObject)node.getUserObject()).getUserEntity();
							deviceManageView.refreshSwitcherTable(userEntity);
						}
					}
				};

				hyalineDialog.run(queryAndSetPortInformationThread);
			}
		}
	}
	
	public class UserCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value,
				  boolean sel,
				  boolean expanded,
				  boolean leaf, int row,
				  boolean hasFocus) {
			return this;
		}
	}
	
	class UserEntityObject implements Serializable{
		private static final long serialVersionUID = 1L;
		private UserEntity userEntity = null;
		
		public UserEntityObject(Object entity){
			this.userEntity = (UserEntity)entity;
		}
		
		@Override
		public String toString() {
			return this.userEntity.getUserName();
		}

		public UserEntity getUserEntity() {
			return userEntity;
		}
	}
	
	class RoleEntityObject implements Serializable{
		private static final long serialVersionUID = 1L;
		private RoleEntity roleEntity = null;
		
		public RoleEntityObject(Object entity){
			this.roleEntity = (RoleEntity)entity;
		}
		
		@Override
		public String toString() {
			return this.roleEntity.getRoleName();
		}
		
		public RoleEntity getRoleEntity() {
			return roleEntity;
		}
	}
	
	private void expandTree(JTree tree) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root));
	}
	@SuppressWarnings("unchecked")
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
}