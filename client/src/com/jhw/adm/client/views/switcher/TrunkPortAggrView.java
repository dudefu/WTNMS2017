package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.TrunkTableModel;
import com.jhw.adm.client.swing.CommonTable;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.UploadMessageProcessorStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.TrunkConfig;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(TrunkPortAggrView.ID)
@Scope(Scopes.DESKTOP)
public class TrunkPortAggrView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;

	public static final String ID = "trunkPortAggrView";
	
	//trunk�������
	private final JPanel trunkPnl = new JPanel(); 
	private final JPanel groupPnl = new JPanel();
	private final JLabel groupLbl = new JLabel();
	private final NumberField groupFld = new NumberField();
	
	private final JPanel portPnl = new JPanel();
	
	private final JLabel portLbl = new JLabel("�˿�");
	private final List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
	
	private JButton saveBtn ;
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton delBtn;
	
	
	//�ۺ϶˿��б�
	private final JScrollPane scrollPnl = new JScrollPane();
	private final CommonTable table = new CommonTable();
	
	private static final String[] COLUMN_NAME = {"��ID","�ۺ϶˿�","״̬"};
	
	private int portCount = 0; //�˿�����
	
	//����ӷ���˵õ�������
	private final List<List> dataList = new ArrayList<List>();
	
	//�������е��Ѿ��ۺϵĶ˿�
	private final List aggrPortList = new ArrayList();
	
	//�¶˵İ�ť���
	private final JPanel bottomPnl = new JPanel();
	private JButton closeBtn = null;
	
	private TrunkConfig trunkConfigs;

	private SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	
	private MessageSender messageSender;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Autowired
	@Qualifier(TrunkTableModel.ID)
	private TrunkTableModel model;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private final UploadMessageProcessorStrategy uploadMessageProcessorStrategy = new UploadMessageProcessorStrategy();
	
	private static final Logger LOG = LoggerFactory.getLogger(TrunkPortAggrView.class);
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageFepOffLineProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message){
			queryData();
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
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
		
		initTrunkPnl();
		initTablePnl();
		initBottomPnl();

		this.setLayout(new BorderLayout());

		this.add(trunkPnl,BorderLayout.NORTH);
		this.add(scrollPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		this.setViewSize(640,480);
		
		setResource();
	}
	
	private void initTrunkPnl(){
		JScrollPane portScrollPnl = new JScrollPane();
		portPnl.setBackground(Color.WHITE);
		
		portScrollPnl.setPreferredSize(new Dimension(320,120));
		portScrollPnl.setMinimumSize(new Dimension(320,120));
		portScrollPnl.getViewport().add(portPnl);
		
		JPanel groupMiddlePnl = new JPanel(new GridBagLayout());
		groupMiddlePnl.add(groupLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,0,0),0,0));
		groupMiddlePnl.add(groupFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,0,0),0,0));
		
		groupPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		groupPnl.add(groupMiddlePnl);
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(portLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		panel.add(portScrollPnl,new GridBagConstraints(0,1,5,5,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(6,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		JPanel middlePnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		middlePnl.add(panel);
		
		JPanel addBtnPnl = new JPanel(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		saveBtn = buttonFactory.createButton(SAVE);
		delBtn = buttonFactory.createButton(DELETE);
//		newPanel.add(synBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(saveBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(delBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		addBtnPnl.add(newPanel, BorderLayout.EAST);
		
		trunkPnl.setLayout(new BorderLayout());
		trunkPnl.add(groupPnl,BorderLayout.NORTH);
		trunkPnl.add(middlePnl,BorderLayout.CENTER);
		trunkPnl.add(addBtnPnl,BorderLayout.SOUTH);
	}

	private void initTablePnl(){
		model.setColumnName(COLUMN_NAME);
		table.setModel(model);
		
		scrollPnl.getViewport().add(table);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		closeBtn = buttonFactory.createCloseButton();
		newPanel.add(uploadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		bottomPnl.add(newPanel, BorderLayout.EAST);
	}
	
	private void setResource(){
		this.setTitle("Trunk�˿ھۺ�");
		trunkPnl.setBorder(BorderFactory.createTitledBorder("�˿���Ϣ"));

		groupLbl.setText("��ID");
		portLbl.setText("�˿�");
		groupFld.setColumns(20);
		
		//���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				int row = table.getSelectedRow();
				if (row < 0){
					return;
				}
				changePortSelected(row);
			}
		});
		
		groupFld.addFocusListener(new FocusAdapter(){
			@Override
			public void focusGained(FocusEvent e){
				changePortSelected(-1);
				table.clearSelection();
			}
		});
	}
	
	/**
	 * �ӷ�������ѯ���� 
	 */
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchNodeEntity =
			(SwitchNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		//�õ��˿�����
		portCount = switchNodeEntity.getPorts().size();
		//��ʼ���˿ں;ۺ��б�
		initComponents();

		//�����˲�ѯ����
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<TrunkConfig> trunkConfigList = (List<TrunkConfig>)remoteServer.getService().findAll(TrunkConfig.class, where, parms);
		if (null == trunkConfigList || trunkConfigList.size() <1){
			model.setDataList(null);
			model.fireTableDataChanged();
			aggrPortList.clear();
			return;
		}
		
		setValues(trunkConfigList);
	}
	
	/**
	 * ��ʼ���˿ں;ۺ��б�
	 */
	private void initComponents(){
		checkBoxList.clear();
		portPnl.removeAll();
		//��ʼ���˿��б�
		int portRow = 0;
		if (0 == portCount%3){
			portRow = portCount/3;
		}
		else{
			portRow = (portCount/3) +1;
		}
		portPnl.setLayout(new GridLayout(portRow,3));
		
		for (int i = 0 ; i < portCount; i++){
			JCheckBox checkBox = new JCheckBox("�˿�"+(i+1));
			checkBox.setBackground(Color.WHITE);
			portPnl.add(checkBox);
			checkBoxList.add(i,checkBox);
		}
		portPnl.revalidate();
	}
	
	@SuppressWarnings("unchecked")
	private void setValues(List<TrunkConfig> trunkConfigList){
		this.dataList.clear();
		aggrPortList.clear();
		int size = trunkConfigList.size();
		for (int i = 0 ; i < size; i++){
			TrunkConfig trunkConfig = trunkConfigList.get(i);
			int groupId = trunkConfig.getGroupId();
			String ports = trunkConfig.getPorts();
			String status = dataStatus.get(trunkConfig.getIssuedTag()).getKey();
			List rowList = new ArrayList();
			rowList.add(groupId);
			rowList.add(ports);
			rowList.add(status);
			rowList.add(trunkConfig);
			this.dataList.add(rowList);
			
			//�����еı��ۺϵĶ˿ڷŵ��б���
			setAggrList(ports);
		}
		changePortSelected(-1);
		
		model.setDataList(this.dataList);
		model.fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	private void setAggrList(String portStr){
		if (null != portStr && !"".equals(portStr)){
			String[] ports =  portStr.split(",");
			for (int i = 0 ; i < ports.length; i++){
				aggrPortList.add(ports[i]);
			}
		}
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="����Trunk�˿�������Ϣ",role=Constants.MANAGERCODE)
	public void save(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		if (groupFld.getText() == null || "".equals(groupFld.getText())){
			JOptionPane.showMessageDialog(this, "��������ID","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		int groupIdCount = 0;
		if (portCount%2 == 0){
			groupIdCount = portCount/2;
		}else{
			groupIdCount = (portCount/2)+1;
		}
		int group = NumberUtils.toInt(groupFld.getText());
		if (group > groupIdCount || group < 1){
			JOptionPane.showMessageDialog(this, "������Χ,��ID�ķ�ΧΪ: 1 - 4","��ʾ",JOptionPane.NO_OPTION);
			return;
		}

		//�ж������������޸� isNew :true ����  �� isNew :false �޸�
		boolean isNew = isNewBool();

		//�õ�groupId
		int groupId = Integer.parseInt(groupFld.getText().trim());
		
		//�õ�����ľۺ϶˿�
		String ports = "";
		for (int i = 0 ; i < checkBoxList.size(); i++){
			if (checkBoxList.get(i).isSelected() && checkBoxList.get(i).isEnabled()){
				ports = ports + (i+1) + ",";
			}
		}

		if (ports.split(",").length < 2){
			String message = "Trunk " + groupId + "��Ա������С��2";
			JOptionPane.showMessageDialog(this, message ,"��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		ports = ports.substring(0,ports.length()-1);
		
		int result = PromptDialog.showPromptDialog(this, "��ѡ�񱣴�ķ�ʽ",imageRegistry);
		if (result == 0){
			return;
		}

		String macValue = switchNodeEntity.getBaseInfo().getMacValue();

		//����
		if(isNew){
			trunkConfigs = new TrunkConfig();
		}
		trunkConfigs.setGroupId(groupId);
		trunkConfigs.setPorts(ports);
		trunkConfigs.setSwitchNode(switchNodeEntity);
		trunkConfigs.setIssuedTag(Constants.ISSUEDADM);
		List<Serializable> list = new ArrayList<Serializable>();
		list.add(trunkConfigs);
		
		showMessageDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.AGGREGATEPORT_ADD, list, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("��������쳣");
			LOG.error("TrunkPortAggrView.save() error:{}", e);
		}
		
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
		groupFld.setText("");
	}
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="����Trunk�˿�������Ϣ",role=Constants.MANAGERCODE)
	public void download(){
		int result = Constants.SYN_ALL;
		if (table.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "��ѡ����Ҫ���ص�����", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		
		List<Serializable> dataList = new ArrayList<Serializable>();
		List<List> list = model.getDataList();
		int selectRowCount = table.getSelectedRowCount();
		for(int i = 0 ; i < selectRowCount; i++){
			int row = table.getSelectedRows()[i];
			int modelRow = table.convertRowIndexToModel(row);
			TrunkConfig trunkConfig = (TrunkConfig)list.get(modelRow).get(3);
			int issuedTag = trunkConfig.getIssuedTag();
			if (issuedTag == Constants.ISSUEDDEVICE){
				JOptionPane.showMessageDialog(this, "ѡ���Trunk�˿��Ѿ����ڣ�������ѡ��", "��ʾ", JOptionPane.NO_OPTION);
				return;
			}
			
			trunkConfig.setIssuedTag(Constants.ISSUEDADM);
			dataList.add(trunkConfig);
		}
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		showMessageDownloadDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.AGGREGATEPORT_ADD, dataList, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("���س����쳣");
			LOG.error("TrunkPortAggrView.save() error:{}", e);
			queryData();
		}
		
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			queryData();
		}
//		queryData();
//		groupFld.setText("");
	}
	
	private boolean isNewBool(){
		boolean isNew = true;
		if (table.getRowCount()<1){
			return isNew;
		}
		String str = groupFld.getText();
		
		List<List> list = model.getDataList();
		for(List rowList : list){
			String strID = rowList.get(0).toString();
			if (str.equalsIgnoreCase(strID)){
				isNew = false;
				trunkConfigs = (TrunkConfig)rowList.get(3);
				break;
			}
		}
		
		return isNew;
	}
	
	/**
	 * �������·����豸������ܲ�ʱ
	 * ���ڽ��յ����첽��Ϣ���д���
	 */
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("����", this);
	}
	
	private void showMessageDownloadDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("����", this);
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="����Trunk�˿�������Ϣ",role=Constants.MANAGERCODE)
	public void upload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		uploadMessageProcessorStrategy.showInitializeDialog("����", this);
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.SINGLESYNCHDEVICE);
				message.setObject(synDeviceList);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHTRUNKPORT);
				return message;
			}
		});
	}
	
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="ɾ��Trunk�˿�������Ϣ",role=Constants.MANAGERCODE)
	public void delete(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return ;
		}
		
		if (table.getSelectedRowCount()< 1){
			JOptionPane.showMessageDialog(this, "��ѡ����ID","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		//�õ�groupId
		int groupId = Integer.parseInt(model.getValueAt(table.getSelectedRow(), 0).toString());
		
		Object object =model.getValueAt(table.getSelectedRow(), model.getColumnCount());
		if (object == null){
			String message = "Trunk " + groupId + "��Ա�в�û�ж˿�";
			JOptionPane.showMessageDialog(this, message ,"��ʾ",JOptionPane.NO_OPTION);
			return;
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
		
		List<List> list = model.getDataList();
		int selectRowCount = table.getSelectedRowCount();
		for(int i = 0 ; i < selectRowCount; i++){
			int row = table.getSelectedRows()[i];
			int modelRow = table.convertRowIndexToModel(row);
			TrunkConfig trunkConfig = (TrunkConfig)list.get(modelRow).get(3);
			
			if(trunkConfig.getIssuedTag() == Constants.ISSUEDADM){//���ܲ�
				isServer = true;
				dataServerList.add(trunkConfig);
			}
			else{//�豸������ܲ�
				isDevice = true;
				dataDeviceList.add(trunkConfig);
			}
		}
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		showDeleteMessageDialog();
		
		if (isDevice && isServer){ //�����豸������ܲ඼��Ҫɾ���ģ��ȷ�ɾ�����ܵģ����·�ɾ���豸����Ϣ
			//ɾ�����ܲ�
			try {
				remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.AGGREGATEPORT_DEL, dataServerList,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,Constants.SYN_SERVER);
			} catch (JMSException e) {
				messageOfSaveProcessorStrategy.showErrorMessage("ɾ�������쳣");
				LOG.error("TrunkPortAggrView.delete() error:{}", e);
			}
			
			//ɾ���豸��
			try {
				remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.AGGREGATEPORT_DEL, dataDeviceList,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,Constants.SYN_ALL);
			} catch (JMSException e) {
				messageOfSaveProcessorStrategy.showErrorMessage("ɾ�������쳣");
				LOG.error("TrunkPortAggrView.delete() error:{}", e);
			}
		}
		else if(isDevice == true && isServer == false){//ֻɾ���豸��
			//ɾ���豸��
			try {
				remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.AGGREGATEPORT_DEL, dataDeviceList,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,Constants.SYN_ALL);
			} catch (JMSException e) {
				messageOfSaveProcessorStrategy.showErrorMessage("ɾ�������쳣");
				LOG.error("TrunkPortAggrView.delete() error:{}", e);
			}
		}
		else if(isDevice == false && isServer == true){//ֻɾ�����ܲ�
			//ɾ�����ܲ�
			try {
				remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.AGGREGATEPORT_DEL, dataServerList,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,Constants.SYN_SERVER);
			} catch (JMSException e) {
				messageOfSaveProcessorStrategy.showErrorMessage("ɾ�������쳣");
				LOG.error("TrunkPortAggrView.delete() error:{}", e);
			}
		}
		
		queryData();
		groupFld.setText("");
	}
	
	public void showDeleteMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("ɾ��",this);
	}
	
	private void changePortSelected(int selectRow){
		int checkBoxCount = checkBoxList.size();
		
		//��������checkBox��Ϊ��ѡ����δ��ѡ��
		for (int count = 0 ; count < checkBoxCount; count++){
			checkBoxList.get(count).setEnabled(true);
			checkBoxList.get(count).setSelected(false);
		}
		
		//�������е��Ѿ��ۺϵĶ˿ڣ�������Ϊ����ѡ
		int aggrSum = aggrPortList.size();
		for (int i = 0 ; i < aggrSum; i++){
			int port = Integer.parseInt(aggrPortList.get(i).toString()) - 1;
			if(port < checkBoxCount){
				checkBoxList.get(port).setEnabled(false);
				checkBoxList.get(port).setSelected(true);
			}
		}
		
		if (selectRow < 0){
			return;
		}
		//����ѡ���еľۺ϶˿ڵõ���������Ϊ��ѡ��
		Object object = table.getValueAt(selectRow, 1);
		if (null != object && !("".equals(object.toString()))){
			String[] ports = object.toString().split(",");
			for (int k = 0 ; k < ports.length; k++){
				int port = Integer.parseInt(ports[k]);
				checkBoxList.get(port-1).setEnabled(true);
				checkBoxList.get(port-1).setSelected(true);
			}
		}
		
		String str = String.valueOf(table.getValueAt(selectRow, 0));
		groupFld.setText(str.trim());
	}
	
	private void clear(){
		portPnl.removeAll();
		checkBoxList.clear();
		dataList.clear();
	}

	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				clear();
				queryData();
			}
		}		
	};
	
	/**
	 * ���ڽ��յ����첽��Ϣ���д���
	 */
	public void receive(Object object){
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
	}
}
