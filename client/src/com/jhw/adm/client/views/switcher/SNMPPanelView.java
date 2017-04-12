package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.CONFIG;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

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
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.CommonTableModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.CommonTable;
import com.jhw.adm.client.swing.MessagePromptDialog;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.MessageReceiveProcess;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.swing.UploadMessageProcessorStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SNMPView;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.SNMPSwitchIPBean;
import com.jhw.adm.server.entity.util.SNMPViewBean;

@Component(SNMPPanelView.ID)
@Scope(Scopes.DESKTOP)
public class SNMPPanelView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;
	public static final String ID = "snmpPanelView";

	private final JPanel topPnl = new JPanel();
	private final JLabel viewNameLbl = new JLabel();
	private final JTextField viewNameFld = new JTextField();
	private final JLabel viewRangeLbl = new JLabel();
	
	private final JLabel oidLbl = new JLabel();
	private final JTextField oidFld = new JTextField();
	
	private final JLabel viewTypeLbl = new JLabel();
	private final JComboBox viewTypeCombox = new JComboBox();
	private static final String[] VIEWTYPELIST = {"include","exclude"};
	
	
	private final JScrollPane scrollPnl = new JScrollPane();
	private final CommonTable table = new CommonTable();
	private SNMPTableModel model = null;
	
	
	private final JPanel rightPnl = new JPanel();
	private final JTable switchTable = new JTable();
	private final String[] SWITCH_COLUMN_NAME = {"�豸IP"};
	
	//�¶˵İ�ť���
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn ;
	private JButton uploadBtn;
	private JButton delBtn;
	private JButton closeBtn;
	private JButton addBtn;
	private JButton configBtn;
	
	
	private static final String[] COLUMN_NAME = {"��ͼ��","����","��ͼ����","״̬"};
	
	private final SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	
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
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Autowired
	@Qualifier(MessageReceiveProcess.ID)
	private MessageReceiveProcess messageReceiveProcess;
	
	@Autowired
	@Qualifier(CommonTableModel.ID)
	private CommonTableModel commonTableModel;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	private static final Logger LOG = LoggerFactory.getLogger(SNMPPanelView.class);
	private final UploadMessageProcessorStrategy uploadMessageProcessorStrategy = new UploadMessageProcessorStrategy();
	private MessageSender messageSender;
	
	private MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("sleep error", e);
			}
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageFepOffLineProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("sleep error", e);
			}
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
		messageDispatcher.addProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
		initLeftComponents();
		initRightPnl();
		initBottomPnl();

		JPanel leftPnl = new JPanel(new BorderLayout());
		
		leftPnl.add(topPnl,BorderLayout.NORTH);
		leftPnl.add(scrollPnl,BorderLayout.CENTER);
		leftPnl.add(buttonPanel, BorderLayout.SOUTH);
		
		JSplitPane splitPnl = new JSplitPane();
		splitPnl.setLeftComponent(leftPnl);
		splitPnl.setRightComponent(rightPnl);
		splitPnl.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPnl.setDividerSize(7);
		splitPnl.setDividerLocation(500);

		this.setLayout(new BorderLayout());
		this.add(splitPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);

		setResource();
	}
	
	private JPanel buttonPanel = new JPanel();
	private void initButtonPnl(){
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);

		uploadBtn = buttonFactory.createButton(UPLOAD);
		configBtn = buttonFactory.createButton(CONFIG);
		
		buttonPanel.add(uploadBtn);
		buttonPanel.add(configBtn);
	}
	
	private void initRightPnl(){
		commonTableModel.setColumnName(SWITCH_COLUMN_NAME);
		switchTable.setModel(commonTableModel);
		JScrollPane switchScrollPnl = new JScrollPane();
		switchScrollPnl.getViewport().add(switchTable);
		commonTableModel.getDataList();
		
		rightPnl.setLayout(new BorderLayout());
		rightPnl.add(switchScrollPnl, BorderLayout.CENTER);
		rightPnl.setBorder(BorderFactory.createTitledBorder("�豸��Ϣ"));
	}
	
	private void initBottomPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		panel.add(closeBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.add(panel);
	}
	
	private void initLeftComponents(){
		for (int i = 0 ; i < VIEWTYPELIST.length ; i++){
			viewTypeCombox.addItem(VIEWTYPELIST[i]);
		}
		viewTypeCombox.setPreferredSize(new Dimension(100,viewTypeCombox.getPreferredSize().height));
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(viewNameLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		panel.add(viewNameFld,new GridBagConstraints(1,0,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		
		
		panel.add(oidLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		panel.add(oidFld,new GridBagConstraints(1,1,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		
		panel.add(viewTypeLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		panel.add(viewTypeCombox,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		
		
		addBtn = buttonFactory.createButton(APPEND);
		delBtn = buttonFactory.createButton(DELETE);
		JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JPanel configPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		configPnl.add(panel);
		
		topPnl.setLayout(new BorderLayout());
		topPnl.add(configPnl,BorderLayout.CENTER);
		topPnl.add(btnPnl,BorderLayout.SOUTH);
		model = new SNMPTableModel();
		model.setColumnName(COLUMN_NAME);
		table.setModel(model);

		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);

		scrollPnl.getViewport().add(table);
		
		initButtonPnl();
	}
	
	
	private void setResource(){
		viewNameFld.setColumns(25);
		oidFld.setColumns(25);
		
		viewNameLbl.setText("��ͼ��");
		viewRangeLbl.setText("(��Χ:1-32���ַ�)");
		oidLbl.setText("���� OID");
		viewTypeLbl.setText("��ͼ����");
		//Amend 2010.06.08
		viewNameFld.setDocument(new TextFieldPlainDocument(viewNameFld, 32));
		oidFld.setDocument(new TextFieldPlainDocument(oidFld));
		
		//���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
//		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		
		//ע�᷵�ص��첽��Ϣ
		messageReceiveProcess.registerReceiveMessage(this);
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				tableSelectAction();
			}
		});
		
		setEnable(false);
	}
	
	/**
	 * �����¼�����
	 */
	private void tableSelectAction(){
		int row  = table.getSelectedRow();
		if (row < 0){
			commonTableModel.setDataList(null);
			commonTableModel.fireTableDataChanged();
			return;
		}
		int modelRow = table.convertRowIndexToModel(row);
		
		final SNMPViewBean snmpViewBean = (SNMPViewBean)model.getValueAt(modelRow, table.getColumnCount());
		
		new Thread(new Runnable(){
			@SuppressWarnings("unchecked")
			public void run(){
				List<SNMPSwitchIPBean> snmpSwitchIPBeanList = remoteServer.getNmsService().querySNMPSwitchIPBean(snmpViewBean); 
				
				List<List> dataList = new ArrayList<List>();
				for(SNMPSwitchIPBean snmpSwitchIPBean : snmpSwitchIPBeanList){
					String ip = snmpSwitchIPBean.getIpValue();
					
					SwitchBaseConfig switchBaseConfig = new SwitchBaseConfig();
					switchBaseConfig.setIpValue(ip);
					SwitchNodeEntity switchNodeEntity = new SwitchNodeEntity();
					switchNodeEntity.setBaseConfig(switchBaseConfig);
					
					List rowList = new ArrayList();
					rowList.add(ip);
					rowList.add(switchNodeEntity);
					dataList.add(rowList);
				}
				commonTableModel.setDataList(dataList);
				commonTableModel.fireTableDataChanged();
			}
		}).start();
	}
	
	/**
	 * �������·���ѯ����
	 */
	@SuppressWarnings("unchecked")
	private void queryData(){
		commonTableModel.setDataList(null);
		commonTableModel.fireTableDataChanged();
		List<SNMPViewBean> snmpViewBeanList = remoteServer.getNmsService().querySNMPView();
		
		List<List> dataList = new ArrayList<List>();
		for(SNMPViewBean snmpViewBean : snmpViewBeanList){
			String viewName = snmpViewBean.getViewName();
			String oidName = snmpViewBean.getOIDName();
			String viewType = snmpViewBean.getViewType();
			String status = dataStatus.get(snmpViewBean.getIssuedTag()).getKey();
			
			List rowList = new ArrayList();
			rowList.add(viewName);
			rowList.add(oidName);
			rowList.add(viewType);
			rowList.add(3,status);
			rowList.add(snmpViewBean);
			dataList.add(rowList);
		}
		
		model.setDataList(dataList);
		model.fireTableDataChanged();
	}
	
	/**
	 * ���ݲ�ѯ�õ���ֵ���õ���ͼ�Ŀؼ���
	 * @param lldpConfig
	 */
	@SuppressWarnings("unchecked")
	private void setValue(List<SNMPView> snmpViewList){
		model.setDataList(null);
		List<List> dataList = new ArrayList<List>();
		int sum = snmpViewList.size();
		for (int i = 0 ; i < sum; i++){
			SNMPView snmpView = snmpViewList.get(i);
			
			String viewName = snmpView.getViewName();
			String oidName = snmpView.getOIDName();
			String viewType = snmpView.getViewType();
			String status = dataStatus.get(snmpView.getIssuedTag()).getKey();
			
			List rowList = new ArrayList();
			rowList.add(0,viewName);
			rowList.add(1,oidName);
			rowList.add(2,viewType);
			rowList.add(3,status);
			rowList.add(4,snmpView);
			
			dataList.add(rowList);
		}
		
		model.setDataList(dataList);
		model.fireTableDataChanged();
	}
	
	/**
	 * ��Ӳ���
	 */
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND, desc="���SNMP��ͼ��Ϣ",role=Constants.MANAGERCODE)
	public void append(){
		
	}
	
	/**
	 * ���ò���
	 */
	@ViewAction(name=CONFIG, icon=ButtonConstants.MODIFY,role=Constants.MANAGERCODE)
	public void config(){
//		switchConfigViewss.setAllNodesData(null);
//		switchConfigViewss.setDialogVisible(true,this);
	}
	
	/**
	 * �������
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="����SNMP��ͼ��Ϣ",role=Constants.MANAGERCODE)
	public void save(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}

		//Amend 2010.06.04
		if(!isValids())
		{
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "��ѡ�񱣴�ķ�ʽ",imageRegistry);
		if (result == 0){
			return;
		}
		
		List<Serializable> list = new ArrayList<Serializable>();
		
		SNMPView snmpView = new SNMPView();
		String  viewName = viewNameFld.getText().trim();
		String oidName = oidFld.getText().trim();
		String viewType = viewTypeCombox.getSelectedItem().toString().toLowerCase();
		snmpView.setViewName(viewName);
		snmpView.setOIDName(oidName);
		snmpView.setViewType(viewType);
		snmpView.setSwitchNode(switchNodeEntity);
		
		list.add(snmpView);
		
		//�·������
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.MIRRORUPDATE, list,
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		if (result == Constants.SYN_ALL){
			messageReceiveProcess.openMessageDialog(this,"����");
		}
		else{
			final String operate = "����";
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					//����Ϣ��ʾ�Ի���
					openMessageDialog(operate);
				}
			});
		}
	}
	
	/**
	 * ɾ������
	 */
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="ɾ��SNMP��ͼ��Ϣ",role=Constants.MANAGERCODE)
	public void delete(){
		int result = JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		int row  = table.getSelectedRow();
		if (row < 0){
			return;
		}
		int modelRow = table.convertRowIndexToModel(row);
		
		SNMPView snmpView = (SNMPView)model.getValueAt(modelRow, table.getColumnCount());
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		try {
			remoteServer.getAdmService().deleteAndSetting(macValue, MessageNoConstants.VLANDELETE, snmpView, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,Constants.SYN_ALL);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		messageReceiveProcess.openMessageDialog(this,"ɾ��");
		
		//�ٴδ����ݿ��ѯ
		queryData();
		clear();
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="����SNMP��ͼ��Ϣ",role=Constants.MANAGERCODE)
	public void upload(){
		
		uploadMessageProcessorStrategy.showInitializeDialog("����",this,false);
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		List<SwitchTopoNodeEntity> switchTopoNodeEntities = (List<SwitchTopoNodeEntity>) remoteServer
													.getService().findAll(SwitchTopoNodeEntity.class);
		for(SwitchTopoNodeEntity switchTopoNodeEntity : switchTopoNodeEntities){
			SwitchNodeEntity switchNodeEntity = remoteServer.getService()
				.getSwitchByIp(switchTopoNodeEntity.getIpValue());
			
			SynchDevice synchDevice = new SynchDevice();
			synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
			synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
			synDeviceList.add(synchDevice);
		}
		
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.SINGLESYNCHDEVICE);
				message.setObject(synDeviceList);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHSNMPVIEW);
				return message;
			}
		});	
		
	}
	
	private void clear(){
		viewNameFld.setText("");
		oidFld.setText("");
		viewTypeCombox.setSelectedIndex(0);
	}
	
	/**
	 * �豸����������¼�
	 * @author Administrator
	 */
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
			else{
				model.setDataList(null);
				model.fireTableDataChanged();
			}
		}		
	};
	
	/**
	 * �������·����豸������ܲ�ʱ
	 * ���ڽ��յ����첽��Ϣ���д���
	 */
	public void receive(Object object){
		String message = String.valueOf(object);
		
		messageReceiveProcess.setMessage(message);
		
		//���²�ѯ���ݿ�
		queryData();
	}
	
	/**
	 * ������ֻ�·������ܲ�ʱ
	 * ��ʾ����Ի���
	 */
	private void openMessageDialog(String operate){
		MessagePromptDialog messageDlg = new MessagePromptDialog(this,operate,imageRegistry,remoteServer);
		queryData();
		messageDlg.setMessage("�������");
		messageDlg.setVisible(true);
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
		messageReceiveProcess.dispose();
	}
	
	private void setEnable(boolean bool){
		viewNameFld.setEditable(bool);
		oidFld.setEditable(bool);
		
		viewTypeCombox.setEnabled(bool);

		saveBtn.setEnabled(bool);
		delBtn.setEnabled(bool);
		addBtn.setEnabled(bool);
		configBtn.setEnabled(bool);
		
		viewNameFld.setBackground(Color.WHITE);
		oidFld.setBackground(Color.WHITE);
	}
	
	//Amend 2010.06.04
	public boolean isValids()
	{
		boolean isValid = true;
		if(null == viewNameFld.getText() || "".equals(viewNameFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "��ͼ��������󣬷�Χ�ǣ�1-32", "��ʾ", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if((viewNameFld.getText().trim().length() < 1) || (viewNameFld.getText().trim().length() > 32))
		{
			JOptionPane.showMessageDialog(this, "��ͼ��������󣬷�Χ�ǣ�1-32", "��ʾ", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == oidFld.getText() || "".equals(oidFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "����OID����Ϊ�գ�����������OID", "��ʾ", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == viewTypeCombox.getSelectedItem() || "".equals(viewTypeCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "��ͼ���Ͳ���Ϊ�գ���ѡ����ͼ����", "��ʾ", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		return isValid;
	}
	
	class SNMPTableModel extends AbstractTableModel{
		private String[] columnName = null;
		
		private List<List> dataList = null;
		
		public SNMPTableModel(){
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
				this.dataList = new ArrayList();
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


//	    public Class<?> getColumnClass(int columnIndex){}

	  
	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	
	    public Object getValueAt(int rowIndex, int columnIndex){
	    	return dataList.get(rowIndex).get(columnIndex);
	    }
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	dataList.get(rowIndex).set(columnIndex, aValue);
	    }
	}
	
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	public JButton getSaveButton(){
		return this.saveBtn;
	}
	public JButton getDelButton(){
		return this.delBtn;
	}
}
