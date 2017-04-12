package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.CLOSE;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.MODIFY;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SwitchUser;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(SwitcherUserManageView.ID)
@Scope(Scopes.DESKTOP)
public class SwitcherUserManageView extends ViewPart{
	private static final long serialVersionUID = 1L;

	public static final String ID = "switcherUserManageView";
	
	private final JPanel leftPnl = new JPanel();
	private final JPanel switchTablePnl = new JPanel();
	private final JTable switchTable = new JTable();
	private SwitchTableModel tableModel;
	private final JScrollPane switchScrollPnl = new JScrollPane();
	private static final String[] SWITCHER_COLUMN_NAME = { "������IP","����������"};
	
	private final JCheckBox selectChkbox = new JCheckBox();
	
	private final JPanel switchBtnPnl = new JPanel();
	private JButton uploadBtn ;//���ذ�ť
	private JButton addUserBtn ;///�����û���ť
	private JButton modifyPwdBtn ;//�޸����밴ť

	private final JPanel rightPnl = new JPanel();
	private final JPanel userTablePnl = new JPanel();
	private final JTable userTable = new JTable();
	private UserTableModel userModel;
	private final JScrollPane userScrollPnl = new JScrollPane();
	private static final String[] USER_COLUMN_NAME = { "�û���","Ȩ��","״̬"};
	
	private JButton delUserBtn ;//ɾ���û���ť
	private JButton downloadBtn ;//�����û���ť
	
	private final JPanel bottomPnl = new JPanel();
	private JButton closeBtn ;
	
	private static final Logger LOG = LoggerFactory.getLogger(SwitcherUserManageView.class);
	
	private JDialog dialog = null;
	private final static String ADD_OPERATE = "ADD";//�����û�
	private final static String MODIFY_OPERATE = "MODIFY";//�޸�����
	
	private ButtonFactory  buttonFactory;
	private MessageSender messageSender;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("sleep error", e);
			}
			queryUserTable();
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
			queryUserTable();
		}
	};
	
	private final MessageProcessorAdapter messageUpdateProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
			queryUserTable();
		}
	};
	
	private final MessageProcessorAdapter messageDeleteProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
			queryUserTable();
		}
	};
		
	@PostConstruct
	protected void initialize(){
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this);
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SWITCHUSERADD, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SWITCHUSERUPDATE, messageUpdateProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDeleteProcessor);
		initLeftPnl();
		initRightPnl();
		initBottomPnl();
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(400);
		splitPane.setDividerSize(7);
		splitPane.setLeftComponent(leftPnl);
		splitPane.setRightComponent(rightPnl);
		
		this.setLayout(new BorderLayout());
		this.add(splitPane,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		this.setViewSize(800, 600);
		
		setResource();
	}

	private void initLeftPnl(){
		tableModel = new SwitchTableModel();
		tableModel.setColumnName(SWITCHER_COLUMN_NAME);
		switchTable.setModel(tableModel);
		switchScrollPnl.getViewport().add(switchTable);
		
		switchTablePnl.setLayout(new BorderLayout());
		switchTablePnl.add(switchScrollPnl,BorderLayout.CENTER);
		switchTablePnl.add(selectChkbox,BorderLayout.SOUTH);
		
		switchBtnPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		uploadBtn = buttonFactory.createButton(UPLOAD);
		addUserBtn = buttonFactory.createButton(APPEND);
		modifyPwdBtn = buttonFactory.createButton(MODIFY);
		switchBtnPnl.add(uploadBtn);
		switchBtnPnl.add(addUserBtn);
		switchBtnPnl.add(modifyPwdBtn);
		
		leftPnl.setLayout(new BorderLayout());
		leftPnl.add(switchTablePnl,BorderLayout.CENTER);
		leftPnl.add(switchBtnPnl,BorderLayout.SOUTH);
		leftPnl.setBorder(BorderFactory.createTitledBorder("�豸��Ϣ"));
	}
	
	private void initRightPnl(){
		initUserTablePnl();
		
		rightPnl.setLayout(new BorderLayout());
		rightPnl.add(userTablePnl,BorderLayout.CENTER);
	}
	
	private void initUserTablePnl(){
		userModel = new UserTableModel();
		userModel.setColumnName(USER_COLUMN_NAME);
		userTable.setModel(userModel);
		userScrollPnl.getViewport().add(userTable);
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		delUserBtn = buttonFactory.createButton(DELETE);
		panel.add(downloadBtn);
		panel.add(delUserBtn);
		
		userTablePnl.setLayout(new BorderLayout());
		userTablePnl.add(userScrollPnl,BorderLayout.CENTER);
		userTablePnl.add(panel,BorderLayout.SOUTH);
		userTablePnl.setBorder(BorderFactory.createTitledBorder("�û���Ϣ"));
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}
	
	private void setResource(){
		selectChkbox.setText("ȫѡ(�����CTRL(��SHIFT)��֧�ֶ�ѡ)");
		uploadBtn.setText("����");
		addUserBtn.setText("����û�");
		modifyPwdBtn.setText("�޸�����");
		this.setTitle("�������û�����");
		switchScrollPnl.setPreferredSize(new Dimension(switchScrollPnl.getPreferredSize().width,149));
		userScrollPnl.setPreferredSize(new Dimension(userScrollPnl.getPreferredSize().width,120));
		
		selectChkbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					switchTable.setRowSelectionInterval(0, switchTable.getRowCount() - 1);
				} else if (switchTable.getSelectedRows().length == switchTable.getRowCount()) {
					switchTable.clearSelection();
				}
			}
		});
		
		switchTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				queryUserTable();
				
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						boolean selected = switchTable.getSelectedRows().length == switchTable.getRowCount();
						selectChkbox.getModel().setSelected(selected);
						SwitcherUserManageView.this.repaint();
					}
				});
			}
		});
		
	}
	
	@SuppressWarnings("unchecked")
	private void queryUserTable(){
		if (switchTable.getSelectedRows().length == 0){
			userModel.setDataList(null);
			userModel.fireTableDataChanged();
			return;
		}
		ArrayList<SwitchNodeEntity> list = tableModel.getSelectedRows(switchTable.getSelectedRows());
		SwitchNodeEntity switchNodeEntity = list.get(0);
		if(switchNodeEntity!=null){
			List<List> dataList = getSelectedUserTable(switchNodeEntity);
			userModel.setDataList(dataList);
			userModel.fireTableDataChanged();
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<List> getSelectedUserTable(SwitchNodeEntity switchNodeEntity){
		Object[] parms ={switchNodeEntity};
		List<SwitchUser> userList =(List<SwitchUser>) remoteServer.getService().findAll(SwitchUser.class
				, " where entity.switchNode=?", parms);
		List<List> dataList = new ArrayList<List>();
		for (int i = 0 ; i < userList.size(); i++){
			SwitchUser switchUser = userList.get(i);
			String name = switchUser.getUserName();
			String role = switchUser.getRole();
			String status = dataStatus.get(switchUser.getIssuedTag()).getKey();
			
			List list = new ArrayList();
			list.add(name);
			list.add(role);
			list.add(status);
			list.add(switchUser);
			list.add(switchNodeEntity);
			
			dataList.add(list);
		}
		
		return dataList;
	}

	@SuppressWarnings("unchecked")
	private void queryData(){
		List<SwitchNodeEntity> list = new ArrayList<SwitchNodeEntity>();

		List<SwitchTopoNodeEntity> switchTopoNodeEntities = (List<SwitchTopoNodeEntity>) remoteServer
				.getService().findAll(SwitchTopoNodeEntity.class);
		for(SwitchTopoNodeEntity switchTopoNodeEntity : switchTopoNodeEntities){
			LOG.info(switchTopoNodeEntity.getIpValue());
			SwitchNodeEntity switchNodeEntity = remoteServer.getService()
					.getSwitchByIp(switchTopoNodeEntity.getIpValue());
			list.add(switchNodeEntity);
		}
		
		tableModel.setDataList(list);
		tableModel.fireTableDataChanged();
	}
	
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="ɾ���������û�",role=Constants.MANAGERCODE)
	public void delete(){
		if (userTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "��ѡ����Ҫɾ�����û�", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		
		if (userTable.getSelectedRowCount() == 1){
			int selectRow = userTable.getSelectedRow();
			String userName = (String)userModel.getValueAt(selectRow, 0); 
			if (userName.equalsIgnoreCase("guest") || userName.equalsIgnoreCase("admin")){
				JOptionPane.showMessageDialog(this, userName + "�û����ܱ�ɾ��","��ʾ",JOptionPane.NO_OPTION);
				return;
			}
		}
		
		int result = JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		//���豸������ܲ�Ҫɾ������Ϣ�ֱ�ŵ��б�dataDeviceList��dataServerList��
		//���·�ɾ������ʱ���������ܲ໹���豸��ֱ����ɾ��
		List<Serializable> dataDeviceList = new ArrayList<Serializable>();//�豸������ܲ�
		List<Serializable> dataServerList = new ArrayList<Serializable>();//���ܲ�
		boolean isDevice = false;
		boolean isServer = false;
		
		for (int i = 0; i < userTable.getSelectedRows().length; i++){
			int row = userTable.getSelectedRows()[i];
			String userName = (String)userModel.getValueAt(row, 0); 
			if (userName.equalsIgnoreCase("guest") || userName.equalsIgnoreCase("admin")){
				continue;
			}
			
			SwitchUser switchUser = (SwitchUser)userModel.getValueAt(row, 3);
			if(switchUser.getIssuedTag() == Constants.ISSUEDADM){//���ܲ�
				isServer = true;
				dataServerList.add(switchUser);
			}else{//�豸������ܲ�
				isDevice = true;
				dataDeviceList.add(switchUser);
			}
		}
		ArrayList<SwitchNodeEntity> list = tableModel.getSelectedRows(switchTable.getSelectedRows());
		SwitchNodeEntity nodeEntity = list.get(0);
		String macValue = nodeEntity.getBaseInfo().getMacValue();
		
		Task task = new DeleteRequestTask(isDevice, isServer, macValue, dataDeviceList, dataServerList);
		showConfigureMessageDialog(task, "ɾ���������û�");
	}
	
	private class DeleteRequestTask implements Task{

		private boolean isDevice = false;
		private boolean isServer = false;
		private String macValue = "";
		private List<Serializable> dataDeviceList;//�豸������ܲ�
		private List<Serializable> dataServerList;//���ܲ�

		public DeleteRequestTask(boolean isDevice, boolean isServer,
				String macValue, List<Serializable> dataDeviceList,
				List<Serializable> dataServerList) {
			this.isDevice = isDevice;
			this.isServer = isServer;
			this.macValue = macValue;
			this.dataDeviceList = dataDeviceList;
			this.dataServerList = dataServerList;
		}
		
		@Override
		public void run() {
			if (isDevice && isServer){ //�����豸������ܲ඼��Ҫɾ���ģ��ȷ�ɾ�����ܵģ����·�ɾ���豸����Ϣ
				//ɾ�����ܲ�
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.SWITCHUSERDEL, dataServerList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_SERVER);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("ɾ���������û�");
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
					queryUserTable();
				}
				
				//ɾ���豸��
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.SWITCHUSERDEL, dataDeviceList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("ɾ���������û�");
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
					queryUserTable();
				}
			}else if(isDevice == true && isServer == false){//ֻɾ���豸��
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.SWITCHUSERDEL, dataDeviceList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("ɾ���������û�");
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
					queryUserTable();
				}
			}else if(isDevice == false && isServer == true){//ֻɾ�����ܲ�
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.SWITCHUSERDEL, dataServerList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_SERVER);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("ɾ���������û�");
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
					queryUserTable();
				}
				paramConfigStrategy.showNormalMessage(true,Constants.SYN_SERVER);
				queryUserTable();
			}
		}
	}
	
	@ViewAction(name=DOWNLOAD,icon=ButtonConstants.DOWNLOAD,desc="���ؽ������û�",role=Constants.MANAGERCODE)
	public void download(){
		if (userTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "��ѡ����Ҫ���صļ�¼", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		
		final ArrayList<SwitchUser> switchUserList = new ArrayList<SwitchUser>();
		for (int i = 0; i < userTable.getSelectedRows().length; i++){
			int row = userTable.getSelectedRows()[i];
			
			SwitchUser switchUser = (SwitchUser)userModel.getValueAt(row, 3);
			if(Constants.ISSUEDDEVICE == switchUser.getIssuedTag()){
				JOptionPane.showMessageDialog(this, "����������״̬Ϊ�豸�������", "��ʾ", JOptionPane.NO_OPTION);
				return;
			}
			switchUserList.add(switchUser);
		}
		
		Task task = new RequestTask(switchUserList, Constants.SWITCHUSERADD);
		showConfigureMessageDialog(task, "���ؽ������û�");
	}
	
	@ViewAction(name=UPLOAD,icon=ButtonConstants.SYNCHRONIZE,desc="���ؽ������û�",role=Constants.MANAGERCODE)
	public void upload(){
		ArrayList<SwitchNodeEntity> list = tableModel.getDataList();
		if(null == list || 0 == list.size()){
			return;
		}
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		for(SwitchNodeEntity nodeEntity : list){
			SynchDevice synchDevice = new SynchDevice();
			synchDevice.setIpvalue(nodeEntity.getBaseConfig().getIpValue());
			synchDevice.setModelNumber(nodeEntity.getDeviceModel());
			synDeviceList.add(synchDevice);
		}
		
		selectChkbox.setSelected(false);
		Task task = new UploadRequestTask(synDeviceList,MessageNoConstants.SINGLESWITCHUSERADM);
		showUploadMessageDialog(task, "���ؽ������û�");
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog noticeDialog;
	
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			paramConfigStrategy = new ParamConfigStrategy(operation, progressBarModel);
			noticeDialog = new JProgressBarDialog("��ʾ",0,1,this,operation,true);
			noticeDialog.setModel(progressBarModel);
			noticeDialog.setStrategy(paramConfigStrategy);
			noticeDialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showConfigureMessageDialog(task, operation);
				}
			});
		}
	}
	
	private void showUploadMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			uploadStrategy = new ParamUploadStrategy(operation, progressBarModel,false);
			noticeDialog = new JProgressBarDialog("��ʾ",0,1,this,operation,true);
			noticeDialog.setModel(progressBarModel);
			noticeDialog.setStrategy(uploadStrategy);
			noticeDialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showUploadMessageDialog(task, operation);
				}
			});
		}
	}
	
	private class RequestTask implements Task{

		private ArrayList<SwitchUser> switchUserList = null;
		private int operation = 0;
		public RequestTask(ArrayList<SwitchUser> switchUserList, int operation){
			this.switchUserList = switchUserList;
			this.operation = operation;
		}

		@Override
		public void run() {
			messageSender.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage message = session.createObjectMessage();
					message.setIntProperty(Constants.MESSAGETYPE,
							MessageNoConstants.SWITCHUSERMANAGE);
					message.setObject(switchUserList);
					message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
					message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());	
					message.setIntProperty(Constants.SWITCHUSERS, operation);
					return message;
				}
			});
		}
	}
	
	@SuppressWarnings("static-access")
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND,desc="��ӽ������û�",role=Constants.MANAGERCODE)
	public void append(){
		if (switchTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		
		dialog = new InputDialog(this,this.ADD_OPERATE);
		dialog.setTitle("����û�");
		dialog.setVisible(true);
	}
	
	@SuppressWarnings("static-access")
	@ViewAction(name=MODIFY, icon=ButtonConstants.MODIFY,desc="�޸Ľ������û�����",role=Constants.MANAGERCODE)
	public void modify(){
		if (switchTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		
		dialog = new InputDialog(this,this.MODIFY_OPERATE);
		dialog.setTitle("�޸��û�����");
		dialog.setVisible(true);
	}
	
	@Override
	public void dispose() {
		messageDispatcher.removeProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SWITCHUSERADD, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SWITCHUSERUPDATE, messageUpdateProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDeleteProcessor);
	}
	
	private class InputDialog extends JDialog{
		
		private static final long serialVersionUID = 1L;
		private final JPanel mainPnl = new JPanel();
		
		private final JPanel addUserPnl = new JPanel();
		private final JLabel userNameLbl = new JLabel();
		private final JTextField userNameFld = new JTextField();
		private final JLabel pwdLbl = new JLabel();
		private final JPasswordField pwdFld = new JPasswordField("", 20);
		private final JLabel modifyPwdLbl = new JLabel();
		private final JPasswordField modifyPwdFld = new JPasswordField("", 20);
		private final JLabel roleLbl = new JLabel();
		private final JComboBox roleCombox = new JComboBox();
		
		private final JPanel modifyPwdPnl = new JPanel();
		private final JLabel nameModifyLbl = new JLabel();
		private final JTextField nameModifyFld = new JTextField();
		private final JLabel oldPwdLbl = new JLabel();
		private final JPasswordField oldPwdFld = new JPasswordField("", 20);
		private final JLabel newPwdLbl = new JLabel();
		private final JPasswordField newPwdFld = new JPasswordField("", 20);
		private final JLabel newModifyPwdLbl = new JLabel();
		private final JPasswordField newModifyPwdFld = new JPasswordField("", 20);
		
		private final JPanel btnPnl = new JPanel();
		private JButton saveBtn;
		private JButton closeBtn;
		
		private final ViewPart viewPart ;
		
		//��־λ������û�Ϊ"ADD",�޸�����Ϊ"MODIFY"�����ݲ�����ͬ��ʾ��ͬ����ͼ
		private final String flag ;
		
		private final String[] ROLE_VALUE = {"users","administrators"};
		
		/**
		 * ������
		 */
		public InputDialog(ViewPart viewPart,String flag){
			super(ClientUtils.getRootFrame());
			this.flag = flag;
			init();
			this.viewPart = viewPart;
			this.setLocationRelativeTo(this.viewPart);
		}
		
		private void init(){
			initMainPnl();
			initBtnPnl();
			
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(mainPnl,BorderLayout.CENTER);
			panel.add(btnPnl,BorderLayout.SOUTH);
			this.getContentPane().add(panel);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setModal(true);
			this.setResizable(false);
			
			this.setSize(new Dimension(400,300));
			setResource();
		}
		
		private void initMainPnl(){
			initAddUserPnl();
			initModifyPwdPnl();
			
			mainPnl.setLayout(new GridBagLayout());
			mainPnl.add(addUserPnl,new GridBagConstraints(0,0,1,1,1.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
			mainPnl.add(modifyPwdPnl,new GridBagConstraints(0,1,1,1,1.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
			mainPnl.add(new JPanel(),new GridBagConstraints(0,2,1,1,0.0,1.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
		}
		
		private void initAddUserPnl(){
			JPanel panel = new JPanel(new GridBagLayout());
			panel.add(userNameLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
			panel.add(userNameFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,0,0),0,0));
			panel.add(new StarLabel("(1-16���ַ�)"),new GridBagConstraints(2,0,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
			
			panel.add(pwdLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
			panel.add(pwdFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
			panel.add(new StarLabel("(5-16���ַ�)"),new GridBagConstraints(2,1,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
			
			panel.add(modifyPwdLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
			panel.add(modifyPwdFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
			panel.add(new StarLabel("(5-16���ַ�)"),new GridBagConstraints(2,2,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
			
			panel.add(roleLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,80,0),0,0));
			panel.add(roleCombox,new GridBagConstraints(1,3,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,80,0),0,0));
			
			addUserPnl.setBorder(BorderFactory.createTitledBorder("����û�"));
			addUserPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
			addUserPnl.add(panel);
		}
		
		private void initModifyPwdPnl(){
			JPanel panel = new JPanel(new GridBagLayout());
			panel.add(nameModifyLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
			panel.add(nameModifyFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,0,0),0,0));
			panel.add(new StarLabel("(1-16���ַ�)"),new GridBagConstraints(2,0,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
			
			panel.add(oldPwdLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
			panel.add(oldPwdFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
			panel.add(new StarLabel("(5-16���ַ�)"),new GridBagConstraints(2,1,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(8,5,0,0),0,0));
			
			panel.add(newPwdLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
			panel.add(newPwdFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
			panel.add(new StarLabel("(5-16���ַ�)"),new GridBagConstraints(2,2,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(8,5,0,0),0,0));
			
			panel.add(newModifyPwdLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,110,0),0,0));
			panel.add(newModifyPwdFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,110,0),0,0));
			panel.add(new StarLabel("(5-16���ַ�)"),new GridBagConstraints(2,3,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(8,5,110,0),0,0));
			
			modifyPwdPnl.setBorder(BorderFactory.createTitledBorder("�޸�����"));
			modifyPwdPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
			modifyPwdPnl.add(panel);
		}
		
		private void initBtnPnl(){
			btnPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
			saveBtn = new JButton(localizationManager.getString(SAVE), 
					imageRegistry.getImageIcon(ButtonConstants.SAVE));
			closeBtn = new JButton(localizationManager.getString(CLOSE), 
					imageRegistry.getImageIcon(ButtonConstants.CLOSE));
			btnPnl.add(saveBtn);
			btnPnl.add(closeBtn);
		}
		
		private void setResource(){
			userNameLbl.setText("�û���");
			pwdLbl.setText("����");
			modifyPwdLbl.setText("ȷ������");
			roleLbl.setText("Ȩ��");
			
			nameModifyLbl.setText("�û���");
			oldPwdLbl.setText("ԭ����");
			newPwdLbl.setText("������");
			newModifyPwdLbl.setText("ȷ������");
			
			saveBtn.setText("����");
			closeBtn.setText("�ر�");
			
			userNameFld.setDocument(new TextFieldPlainDocument(userNameFld, 16));
			nameModifyFld.setDocument(new TextFieldPlainDocument(userNameFld, 16));
			
			for (int i = 0 ; i < ROLE_VALUE.length; i++){
				roleCombox.addItem(ROLE_VALUE[i]);
			}
			
			saveBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					save();
				}
			});
			
			closeBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					dispose();
				}
			});
			
			setShowPnl();
		}
		
		/**
		 * �����Ƿ��������û������޸���������ʾ��ͬ����ͼ
		 */
		private void setShowPnl(){
			if(flag.equalsIgnoreCase(ADD_OPERATE)){
				addUserPnl.setVisible(true);
				modifyPwdPnl.setVisible(false);
			}
			else if(flag.equalsIgnoreCase(MODIFY_OPERATE)){
				addUserPnl.setVisible(false);
				modifyPwdPnl.setVisible(true);
			}
			else{
				addUserPnl.setVisible(false);
				modifyPwdPnl.setVisible(false);
			}
		}
		
		@SuppressWarnings("unchecked")
		private void save(){
			if(flag.equalsIgnoreCase(ADD_OPERATE)){
				if(!isValid_Add()){
					return;
				}
				String userName = userNameFld.getText().trim();
				String pwd = String.valueOf(pwdFld.getPassword());
				String role = roleCombox.getSelectedItem().toString();
				
//				operateName = userName;
				
				final ArrayList<SwitchUser> switchUserList = new ArrayList<SwitchUser>();
				
				ArrayList<SwitchNodeEntity> list = tableModel.getSelectedRows(switchTable.getSelectedRows());
				if(list.size() == 1){
					SwitchNodeEntity switchNodeEntity = list.get(0);
					if(switchNodeEntity!=null){
						List<List> dataList = getSelectedUserTable(switchNodeEntity);
						if(dataList.size() >= 8){
							JOptionPane.showMessageDialog(this, "���������ֻ����8���û�","��ʾ",JOptionPane.NO_OPTION);
							return ;
						}
						else{
							for(List data : dataList){
								String user = String.valueOf(data.get(0));
								if(user.equals(userName)){
									JOptionPane.showMessageDialog(this, "�Ѿ�����ͬ���û���","��ʾ",JOptionPane.NO_OPTION);
									return;
								}
							}
						}
					}
				}
				
				for (SwitchNodeEntity switchNodeEntity : list){
					List<List> dataList = getSelectedUserTable(switchNodeEntity);
					if(dataList.size() >= 8){
						continue;
					}
					else{
						for(List data : dataList){
							String user = String.valueOf(data.get(0));
							if(user.equals(userName)){
								continue;
							}
						}
					}
					
					SwitchUser switchUser = new SwitchUser();
					switchUser.setUserName(userName);
					switchUser.setPassword(pwd);
					switchUser.setNewPwd(pwd);
					switchUser.setRole(role);
					switchUser.setIssuedTag(Constants.ISSUEDADM);
					switchUser.setSwitchNode(switchNodeEntity);
					
					switchUserList.add(switchUser);
				}

				Task task = new RequestTask(switchUserList, Constants.SWITCHUSERADD);
				showConfigureMessageDialog(task, "���潻�����û�");
			}
			else if(flag.equalsIgnoreCase(MODIFY_OPERATE)){
				if (!isValid_Modify()){
					return;
				}
				String name =  nameModifyFld.getText().trim();
				String oldPwd = String.valueOf(oldPwdFld.getPassword());
				String newPwd = String.valueOf(newPwdFld.getPassword());
				
//				operateName = name;
				
				final ArrayList<SwitchUser> switchUserList = new ArrayList<SwitchUser>();
				
				ArrayList<SwitchNodeEntity> list = tableModel.getSelectedRows(switchTable.getSelectedRows());
				for (SwitchNodeEntity switchNodeEntity : list){
					Object[] parms ={switchNodeEntity};
					List<SwitchUser> userList =(List<SwitchUser>) remoteServer.getService().findAll(SwitchUser.class, " where entity.switchNode=?", parms);
					
					for (SwitchUser switchUser : userList){
						String userName = switchUser.getUserName();
						if (userName.trim().equals(name)){
							switchUser.setPassword(oldPwd);
							switchUser.setNewPwd(newPwd);
							switchUser.setIssuedTag(Constants.ISSUEDADM);
							
							switchUserList.add(switchUser);
						}
					}
				}
				
				if (switchUserList != null && switchUserList.size() > 0){
					Task task = new RequestTask(switchUserList, Constants.SWITCHUSERUPDATE);
					showConfigureMessageDialog(task, "�޸�����");
				}else{
					JOptionPane.showMessageDialog(this, "��ѡ��Ľ�������û�д��û�","��ʾ",JOptionPane.NO_OPTION);
					return;
				}
			}
			
			this.dispose();
		}
		
		private boolean isValid_Add(){
			boolean isValid = true;
			if(null == userNameFld.getText() || "".equals(userNameFld.getText())){
				JOptionPane.showMessageDialog(this, "�������û���","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			String pwd = new String(pwdFld.getPassword());
			if(null == pwd || "".equals(pwd)){
				JOptionPane.showMessageDialog(this, "����������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if(pwd.length() > 16 || pwd.length() < 5){
				JOptionPane.showMessageDialog(this, "���볤�ȷ�ΧΪ5-16���ַ�,����������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			
			String modifyPwd = new String(modifyPwdFld.getPassword());
			if(null == modifyPwd || "".equals(modifyPwd)){
				JOptionPane.showMessageDialog(this, "������ȷ������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if(modifyPwd.length() >16 || modifyPwd.length() < 5){
				JOptionPane.showMessageDialog(this, "ȷ�����볤�ȷ�ΧΪ5-16���ַ�,����������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if(!modifyPwd.equals(pwd)){
				JOptionPane.showMessageDialog(this, "������������벻һ��������������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			
			if(null == roleCombox.getSelectedItem()
					|| "".equals(roleCombox.getSelectedItem().toString())){
				JOptionPane.showMessageDialog(this, "������Ȩ��","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			return isValid;
		}
		
		private boolean isValid_Modify(){
			boolean isValid = true;
			String name = nameModifyFld.getText().trim();
			if(null == name || "".equals(name)){
				JOptionPane.showMessageDialog(this, "�������û���","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if(name.length() >16 || name.length() < 1){
				JOptionPane.showMessageDialog(this, "�û�������ĸ�����ֺ��»�����ɣ�1-16λ,����������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			if (name.equals("guest")){
				JOptionPane.showMessageDialog(this, "guest�û������벻���޸�,����������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			
			String oldPwd = new String(oldPwdFld.getPassword());
			if(null == oldPwd || "".equals(oldPwd)){
				JOptionPane.showMessageDialog(this, "������ԭ����","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if(oldPwd.length() >16 || oldPwd.length() < 5){
				JOptionPane.showMessageDialog(this, "���볤�ȷ�ΧΪ5-16���ַ�,����������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			
			String newPwd = new String(newPwdFld.getPassword());
			if(null == newPwd || "".equals(newPwd)){
				JOptionPane.showMessageDialog(this, "������������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if(newPwd.length() >16 || newPwd.length() <5 ){
				JOptionPane.showMessageDialog(this, "���볤�ȷ�ΧΪ5-16���ַ�,����������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			
			String modifyPwd = new String(newModifyPwdFld.getPassword());
			if(null == modifyPwd || "".equals(modifyPwd)){
				JOptionPane.showMessageDialog(this, "������ȷ������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if(modifyPwd.length() >16 || modifyPwd.length() <5){
				JOptionPane.showMessageDialog(this, "���볤�ȷ�ΧΪ5-16���ַ�,����������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if(!modifyPwd.equals(newPwd)){
				JOptionPane.showMessageDialog(this, "������������벻һ��������������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			
			return isValid;
		}
	}

	@SuppressWarnings("unchecked")
	private class SwitchTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private String[] columnName = null;
		
		private List<SwitchNodeEntity> dataList = null;
		
		private final String result = "δ�޸�";
		
		private final HashMap resultMap = new HashMap();
		
		public SwitchTableModel(){
			super();
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public void setDataList(List<SwitchNodeEntity> dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<SwitchNodeEntity>();
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

	    @Override
		public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }
	  
	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	
	    public Object getValueAt(int row, int col){
	    	Object value = null;
			
			if(row < dataList.size()){
				SwitchNodeEntity switchNodeEntity = dataList.get(row);
				switch(col){
					case 0:
						if(null != switchNodeEntity.getBaseConfig()){
							if(null == switchNodeEntity.getBaseConfig().getIpValue()){
								value = "";
							}else{
								value = switchNodeEntity.getBaseConfig().getIpValue();
							}
						}else{
							value = "";
						}
						break;
					case 1:
						value = switchNodeEntity.getType();
						break;
					case 2:
						if (resultMap.get(row) == null || "".equals(resultMap.get(row).toString())){
							value = result;
						}
						else{
							value = resultMap.get(row).toString();
						}
						break;
					default:
						break;
				}
			}
			return value;
	    }
	    
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (aValue instanceof SwitchNodeEntity){
	    		dataList.set(rowIndex, (SwitchNodeEntity)aValue);
	    	}
	    	else if(aValue instanceof String){
	    		resultMap.put(rowIndex, String.valueOf(aValue));
	    	}
	    }
	    
	    public ArrayList<SwitchNodeEntity> getDataList(){
	    	return (ArrayList<SwitchNodeEntity>) dataList;
	    }
	    
	    public ArrayList<SwitchNodeEntity> getSelectedRows(int[] selectedRows){
	    	ArrayList<SwitchNodeEntity> switchNodeEntitys = new ArrayList<SwitchNodeEntity>();
	    	for(int i = 0;i < selectedRows.length ; i++){
	    		switchNodeEntitys.add(this.dataList.get(selectedRows[i]));
	    	}
	    	return switchNodeEntitys;
	    }
	}
	
	@SuppressWarnings("unchecked")
	class UserTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private String[] columnName = null;
		private List<List> dataList = null;

		public UserTableModel(){
		}
		
		public void setColumnName(String[] columnName){
			this.columnName = columnName;
		}
		
		public void setDataList(List<List> dataList){
			if (null == dataList){
				this.dataList = new ArrayList<List>();
			}
			else{
				this.dataList = dataList;
			}
		}
		
		public int getRowCount(){
			if(null == dataList){
				return 0;
			}
			return dataList.size();
		}


	    public int getColumnCount(){
	    	
	    	return columnName.length;
	    }

	    @Override
		public String getColumnName(int columnIndex){
	    	
	    	return columnName[columnIndex];
	    }

	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	    public Object getValueAt(int rowIndex, int columnIndex){
	    	if (null == dataList.get(rowIndex)){
	    		return null;
	    	}
	    	return dataList.get(rowIndex).get(columnIndex);
	    }

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (null == dataList.get(rowIndex)){
	    		return ;
	    	}
	    	dataList.get(rowIndex).set(columnIndex, aValue);
	    }
	    
	    public List<List> getDataList() {
			return dataList;
		}
	}
}
