package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.DISABLED;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.START;
import static com.jhw.adm.client.core.ActionConstants.STOP;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.MessageConstant;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.RingConfigTableModel;
import com.jhw.adm.client.swing.CommonTable;
import com.jhw.adm.client.swing.MessagePromptDialog;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.MessageReceiveProcess;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.ports.RingConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(GHRINGPortConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class GHRINGPortConfigurationView extends ViewPart implements MessageReceiveInter{
	public static final String ID = "ghRINGPortConfigurationView";
	
	private JPanel ringTypePnl = new JPanel();
	private JLabel ringTypeLbl = new JLabel();
	private JComboBox ringTypeCombox = new JComboBox();
	
	private JPanel configPnl = new JPanel();
	private JLabel ringIdLbl = new JLabel();
//	private JTextField ringIdValueFld = new JTextField();
	//Amend 2010.06.03
	private NumberField ringIdValueFld = new NumberField(4,0,1,4096,true);
//	private JLabel ringNameLbl = new JLabel("Ring����");
//	private JLabel ringNameValueLbl = new JLabel("�з���");
	
	private JLabel firstPortMemberLbl = new JLabel();
	private JComboBox firstportMemberCombox = new JComboBox();
	
	private JLabel secondPortMemberLbl = new JLabel();
	private JComboBox secondPortMemberCombox = new JComboBox();
	
	private JLabel sysTypeLbl = new JLabel();
	private JComboBox sysTypeCombox = new JComboBox();
	
	private JLabel firstPortMemberTypeLbl = new JLabel();
	private JComboBox firstportMemberTypeCombox = new JComboBox();
	
	private JLabel secondPortMemberTypeLbl = new JLabel();
	private JComboBox secondportMemberTypeCombox = new JComboBox();
	
	private JLabel ringEnableLbl = new JLabel();
	private JCheckBox ringEnableChkBox = new JCheckBox();
	private JButton startBtn;
	private JButton stopBtn;
	private JButton deleteBtn;
	
	
	private JPanel tablePnl = new JPanel();
	private CommonTable table = new CommonTable();
	
	//�¶˵İ�ť���
	private JPanel bottomPnl = new JPanel();	
	
	private JButton synBtn;
	private JButton saveBtn;
	private JButton closeBtn = null;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private String[] ringType = {"��̬����","��̬����"};
	private String[] systemType = {"Trans","Master","Assistant","Edge"};
	
	private String[] portMemberType = {"None","Master","Subsidiary","Edgeport"};
	
	
	private static final String[] COLUMN_NAME = {"Ring ID","ģʽ","�˿�","ϵͳ����"}; 
		
	private ActionMap actionMap  = null;
	
	private ButtonFactory buttonFactory;
	
	private ListDataAdapter listDataAdapter;
	
	@Autowired
	@Qualifier(LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Autowired
	@Qualifier(RingConfigTableModel.ID)
	private RingConfigTableModel ringConfigTableModel;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(MessageReceiveProcess.ID)
	private MessageReceiveProcess messageReceiveProcess;
	
	@PostConstruct
	protected void initialize(){
		init();
		
		setResource();
		
		queryData();
	}
	
	private void init(){
		this.setTitle("WT-RING����");
		buttonFactory = actionManager.getButtonFactory(this); 
		
		initConfigPnl();
		initTablePnl();
		initBottomPnl();
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(configPnl,BorderLayout.NORTH);
		panel.add(tablePnl,BorderLayout.CENTER);
		panel.add(bottomPnl,BorderLayout.SOUTH);
		
		this.setLayout(new BorderLayout());
		this.add(panel,BorderLayout.CENTER);
	}
	
	private void initTablePnl(){
		table.adjustColumnPreferredWidths();
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(table);

		tablePnl.setLayout(new BorderLayout());
		tablePnl.add(scrollPnl,BorderLayout.CENTER);
		
		ringConfigTableModel.setColumnName(COLUMN_NAME);
		table.setModel(ringConfigTableModel);
		
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(ringConfigTableModel);
		table.setRowSorter(sorter);
		sorter.toggleSortOrder(0);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		bottomPnl.add(closeBtn);
	}
	
	private void initConfigPnl(){
		ringTypePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel middlePnl = new JPanel(new GridBagLayout());
		middlePnl.add(ringTypeLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		middlePnl.add(ringTypeCombox,new GridBagConstraints(1,1,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,40,5,0),0,0));
		ringTypePnl.add(middlePnl);
		ringTypePnl.setBorder(BorderFactory.createTitledBorder("������������"));
		
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(ringIdLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(ringIdValueFld,new GridBagConstraints(1,1,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		
		panel.add(firstPortMemberLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(firstportMemberCombox,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		panel.add(secondPortMemberLbl,new GridBagConstraints(3,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,5,0),0,0));
		panel.add(secondPortMemberCombox,new GridBagConstraints(4,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		
		panel.add(sysTypeLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(sysTypeCombox,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
		panel.add(firstPortMemberTypeLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(firstportMemberTypeCombox,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		panel.add(secondPortMemberTypeLbl,new GridBagConstraints(3,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
		panel.add(secondportMemberTypeCombox,new GridBagConstraints(4,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
//		panel.add(ringEnableLbl,new GridBagConstraints(0,5,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
//		panel.add(ringEnableChkBox,new GridBagConstraints(1,5,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
		JPanel ringConfigPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		ringConfigPnl.add(panel);
		ringConfigPnl.setBorder(BorderFactory.createTitledBorder("Ring�˿�����"));
		
		
		JPanel btnPnl = new JPanel(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		synBtn = buttonFactory.createButton(UPLOAD);
		saveBtn = buttonFactory.createButton(SAVE);
		startBtn = buttonFactory.createButton(START);
		stopBtn = buttonFactory.createButton(DISABLED);
		deleteBtn = buttonFactory.createButton(DELETE);
		newPanel.add(saveBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 5), 0, 0));
//		newPanel.add(startBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 5), 0, 0));
//		newPanel.add(stopBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 5), 0, 0));
		newPanel.add(deleteBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 5), 0, 0));
		newPanel.add(synBtn,new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, Constant.BUTTONINTERVAL, 10, 3), 0, 0));
		
		btnPnl.add(newPanel, BorderLayout.EAST);
		
		configPnl.setLayout(new BorderLayout());
		configPnl.add(ringTypePnl,BorderLayout.NORTH);
		configPnl.add(ringConfigPnl,BorderLayout.CENTER);
		configPnl.add(btnPnl,BorderLayout.SOUTH);
	}

	private void setResource(){
		//Ĭ��Ϊtrue;����
		ringEnableChkBox.setSelected(true);
		
		ringIdValueFld.setColumns(25);
		
		for(int j = 0 ; j < ringType.length; j++){
			ringTypeCombox.addItem(ringType[j]);
		}
		
		int systemTypeLen = systemType.length;
		int portMemberTypeLen = portMemberType.length;
		
		for(int j = 0 ; j < systemTypeLen; j++){
			sysTypeCombox.addItem(systemType[j]);
		}
		
		for(int k =0 ; k < portMemberTypeLen; k++){
			firstportMemberTypeCombox.addItem(portMemberType[k]);
			secondportMemberTypeCombox.addItem(portMemberType[k]);
		}
		firstportMemberTypeCombox.setEnabled(false);
		secondportMemberTypeCombox.setEnabled(false);
		
		ringTypeLbl.setText("��������");
		ringIdLbl.setText("Ring ID");
		firstPortMemberLbl.setText("�˿ڳ�Ա1");
		secondPortMemberLbl.setText("�˿ڳ�Ա2");
		sysTypeLbl.setText("ϵͳ����");
		firstPortMemberTypeLbl.setText("�˿ڳ�Ա����1");
		secondPortMemberTypeLbl.setText("�˿ڳ�Ա����2");
		ringEnableLbl.setText("Ring Enable");
		ringEnableChkBox.setText("Enable");
		
		addListener();
		
		//ע�᷵�ص��첽��Ϣ
		messageReceiveProcess.registerReceiveMessage(this);
	}
	
	private void addListener(){
		sysTypeCombox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				int index = sysTypeCombox.getSelectedIndex();
				if (index == 0){
					firstportMemberTypeCombox.setEnabled(false);
					secondportMemberTypeCombox.setEnabled(false);
				}
				else{
					firstportMemberTypeCombox.setEnabled(true);
					secondportMemberTypeCombox.setEnabled(true);
				}
			}
		});
		
		listDataAdapter = new ListDataAdapter();
		ringTypeCombox.getModel().addListDataListener(listDataAdapter);
		ringTypeCombox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
//				comboxItemAction();
			}
		});
		
		//���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	class ListDataAdapter implements ListDataListener{
		@Override
		public void contentsChanged(ListDataEvent e) {
			comboxItemAction(e);
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private void comboxItemAction(ListDataEvent e){
		if (null == switchNodeEntity){
			return ;
		}
		ringTypeCombox.getSelectedItem();
		
		DefaultComboBoxModel comboxModel = (DefaultComboBoxModel)e.getSource();
		comboxModel.getSelectedItem();
		
		int result = JOptionPane.showConfirmDialog(this, "ѡ��ͬ�Ļ������ͻ����Ring ID����ȷ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			comboxModel.removeListDataListener(listDataAdapter);
			if (comboxModel.getSelectedItem().equals("��̬����")){
				comboxModel.setSelectedItem("��̬����");
			}
			else if(comboxModel.getSelectedItem().equals("��̬����")){
				comboxModel.setSelectedItem("��̬����");
			}
			comboxModel.addListDataListener(listDataAdapter);
			return;
		}
		
		int index = ringTypeCombox.getSelectedIndex();
		if (index == 0){
			sysTypeCombox.setEnabled(true);
		}
		else{
			sysTypeCombox.setEnabled(false);
		}
		
		//���ring
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		List<RingConfig> selectList = new ArrayList<RingConfig>();
		int rowCount = table.getRowCount();
		if (rowCount < 1){
			return;
		}
		
		for (int i = 0 ; i < rowCount; i++){
			int modelRow = table.convertRowIndexToModel(i);
			RingConfig ringConfig = (RingConfig)ringConfigTableModel.getValueAt(modelRow, table.getColumnCount());
			selectList.add(ringConfig);
		}
		
		try {
			remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.RINGDELETE, selectList, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
		} catch (JMSException eee) {
			// TODO Auto-generated catch block
			eee.printStackTrace();
		}
		
		ringConfigTableModel.fireTableDataChanged();
	}
	
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		//ͨ��switchNodeEntity��ѯ�˿ڵ�����
		int portCount = switchNodeEntity.getPorts().size();
		firstportMemberCombox.removeAllItems();
		secondPortMemberCombox.removeAllItems();
		for (int i = 0 ; i < portCount; i++){
			firstportMemberCombox.addItem("Port"+(i+1));
			secondPortMemberCombox.addItem("Port"+(i+1));
		}
		
		//���������ѯ
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<RingConfig> ringConfigList = (List<RingConfig>)remoteServer.getService().findAll(RingConfig.class, where, parms);
		if (null == ringConfigList){
			return;
		}
		
		setDataList(ringConfigList);
	}
	
	private void setDataList(List<RingConfig> ringConfigList){
		List<List> dataList = new ArrayList<List>();
		for (int i = 0 ; i < ringConfigList.size(); i++){
			List rowData = new ArrayList();
			RingConfig ringConfig = ringConfigList.get(i);
			int ringId = ringConfig.getRingID();
			boolean enable = ringConfig.isRingEnable();
			String portStr = ringConfig.getPort1() + "," + ringConfig.getPort2();
			String sysType = ringConfig.getSystemType();
			
			rowData.add(0,ringId);
			rowData.add(1,reserveBooleanToString(enable));
			rowData.add(2,portStr);
			rowData.add(3,sysType);
			rowData.add(4,ringConfig);
			dataList.add(rowData);
		}
		
		ringConfigTableModel.setDataList(dataList);	
		ringConfigTableModel.fireTableDataChanged();
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="����GH_RING�˿�������Ϣ",role=Constants.MANAGERCODE)
	public void upSynchronize(){
		messageReceiveProcess.openMessageDialog(this,"ͬ��");
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="����GH_RING�˿�������Ϣ",role=Constants.MANAGERCODE)
	public void save(){
		//Amend 2010.06.03
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "��ѡ�񱣴�ķ�ʽ",imageRegistry);
		if (result == 0){
			return ;
		}
		
		String ringId = ringIdValueFld.getText();
		
		boolean mode = false;
		if (ringEnableChkBox.isSelected()){
			mode = true;
		}
		else{
			mode = false;
		}
		
		String port1 = firstportMemberCombox.getSelectedItem().toString().replaceAll("Port", "");
		String port2 = secondPortMemberCombox.getSelectedItem().toString().replaceAll("Port", "");; 
		
		String systemTypeStr = sysTypeCombox.getSelectedItem().toString();
		
		String port1Type = firstportMemberTypeCombox.getSelectedItem().toString();
		String port2Type = secondportMemberTypeCombox.getSelectedItem().toString();
		
		//**********�·����ݵ������***********
		RingConfig ringConfig = new RingConfig();
		ringConfig.setRingID(Integer.parseInt(ringId));//Ring ID
		ringConfig.setRingEnable(mode);//RingEnable
		ringConfig.setSystemType(systemTypeStr.toLowerCase());//SystemType
		ringConfig.setPort1(Integer.parseInt(port1));//port1
		ringConfig.setPort2(Integer.parseInt(port2));//port2
		ringConfig.setPort1Type(port1Type.toLowerCase());//port1type
		ringConfig.setPort2Type(port2Type.toLowerCase());//port2type
		ringConfig.setSwitchNode(switchNodeEntity);//������
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		try {
			remoteServer.getAdmService().saveAndSetting(macValue, MessageNoConstants.RINGNEW, ringConfig,
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
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
	
	@ViewAction(name=START, icon=ButtonConstants.START, desc="����GH_RING��·����",role=Constants.MANAGERCODE)
	public void start(){
		int result = PromptDialog.showPromptDialog(this, "��ѡ�񱣴�ķ�ʽ",imageRegistry);
		if (result == 0){
			return ;
		}
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		List<Serializable> objs = new ArrayList<Serializable>();
			
		List<List> list = ringConfigTableModel.getDataList();
		for(int i =0 ; i < list.size(); i++){
			RingConfig ringConfig = (RingConfig)list.get(4);
			ringConfig.setRingEnable(false);
			objs.add(ringConfig);
		}
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.RINGOFF, objs,
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@ViewAction(name=DISABLED, icon=ButtonConstants.DISABLED, desc="����GH_RING��·����",role=Constants.MANAGERCODE)
	public void disabled(){
		int result = PromptDialog.showPromptDialog(this, "��ѡ�񱣴�ķ�ʽ",imageRegistry);
		if (result == 0){
			return ;
		}
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		List<Serializable> objs = new ArrayList<Serializable>();
		
		List<List> list = ringConfigTableModel.getDataList();
		for(int i =0 ; i < list.size(); i++){
			RingConfig ringConfig = (RingConfig)list.get(4);
			ringConfig.setRingEnable(true);
			objs.add(ringConfig);
		}
		
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.RINGON, objs, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="ɾ��GH_RING��·������Ϣ",role=Constants.MANAGERCODE)
	public void delete(){
		int result = JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		//***********�·������******
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		List<List> list = ringConfigTableModel.getDataList();
		List<Serializable> dataList = new ArrayList<Serializable>();
		
		List<RingConfig> selectList = new ArrayList<RingConfig>();
		int[] rows = table.getSelectedRows(); 
		if (rows.length < 1){
			return;
		}
		
		for (int i = 0 ; i < rows.length; i++){
			int modelRow = table.convertRowIndexToModel(rows[i]);
			RingConfig ringConfig = (RingConfig)ringConfigTableModel.getValueAt(modelRow, table.getColumnCount());
			selectList.add(ringConfig);
		}
		
		try {
			remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.RINGDELETE, selectList, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		int row = table.getSelectedRow();
//		if (row < 0){
//			return;
//		}
//		int modelRow = table.convertRowIndexToModel(row);
//		
//		RingConfig ringConfig = (RingConfig)ringConfigTableModel.getValueAt(modelRow, table.getColumnCount());
//		
////		RingConfig ringConfig = (RingConfig)list.get(modelRow).get(4);
//		try {
//			remoteServer.getAdmService().deleteAndSetting(macValue, MessageNoConstants.RINGDELETE, ringConfig, 
//					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress());
//		} catch (JMSException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		messageReceiveProcess.openMessageDialog(this,"ɾ��");
	}
	
	@ViewAction(name=STOP, icon=ButtonConstants.STOP,desc="ֹͣ",role=Constants.MANAGERCODE)
	public void stop(){

	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	private PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
			else{
				ringConfigTableModel.setDataList(null);	
				ringConfigTableModel.fireTableDataChanged();
			}
		}
	};
	
	/**
	 * ���ڽ��յ����첽��Ϣ���д���
	 */
	public void receive(Object object){
		String message = String.valueOf(object);
		
		messageReceiveProcess.setMessage(message);
		
		//ˢ���б�
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
		messageReceiveProcess.dispose();
	}
	
	
	private boolean reserveStringToBoolean(String str){
		boolean bool = false;
		if (str.equals("enable")){
			bool = true;
		}
		else{
			bool = false;
		}
		
		return bool;
	}
	
	private String reserveBooleanToString(boolean bool){
		String str = "";
		if (bool){
			str = "enable";
		}
		else{
			str = "disable";
		}
		
		return str;
	}
	
	
	
	
	
	//Amend 2010.06.03
	public boolean isValids()
	{
		boolean isValid = true;
		
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == ringIdValueFld.getText() || "".equals(ringIdValueFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "Ring ID���󣬷�Χ�ǣ�1-4096","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if((NumberUtils.toInt(ringIdValueFld.getText()) < 1) || (NumberUtils.toInt(ringIdValueFld.getText()) > 4096))
		{
			JOptionPane.showMessageDialog(this, "Ring ID���󣬷�Χ�ǣ�1-4096","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		int ringID = NumberUtils.toInt(ringIdValueFld.getText().trim());
		for (int i = 0 ; i < table.getRowCount(); i++){
			RingConfig ringConfig = (RingConfig)ringConfigTableModel.getValueAt(0, ringConfigTableModel.getColumnCount());
			if (ringID == ringConfig.getRingID()){
				JOptionPane.showMessageDialog(this, "��������ͬ��Ring ID������������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		if(null == firstportMemberCombox.getSelectedItem() || "".equals(firstportMemberCombox.getSelectedItem().toString())){
			JOptionPane.showMessageDialog(this, "�˿ڳ�Ա1����Ϊ�գ���ѡ��˿ڳ�Ա","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == secondPortMemberCombox.getSelectedItem() || "".equals(secondPortMemberCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "�˿ڳ�Ա2����Ϊ�գ���ѡ��˿ڳ�Ա","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == sysTypeCombox.getSelectedItem() || "".equals(sysTypeCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "ϵͳ���Ͳ���Ϊ�գ���ѡ��ϵͳ����","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == firstportMemberTypeCombox.getSelectedItem() || "".equals(firstportMemberTypeCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "�˿ڳ�Ա����1����Ϊ�գ���ѡ��˿ڳ�Ա����","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == secondportMemberTypeCombox.getSelectedItem() || "".equals(secondportMemberTypeCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "�˿ڳ�Ա����2����Ϊ�գ���ѡ��˿ڳ�Ա����","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String firstPort = firstportMemberCombox.getSelectedItem().toString();
		String secondPort = secondPortMemberCombox.getSelectedItem().toString();
		if (firstPort.equalsIgnoreCase(secondPort)){
			JOptionPane.showMessageDialog(this, "��ѡ����ȷ�Ķ˿ڳ�Ա������ѡ��������ͬ�Ķ˿�","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String port = firstPort.replaceAll("Port", "") + "," + secondPort.replaceAll("Port", "");
		for (int i = 0 ; i < table.getRowCount(); i++){
			RingConfig ringConfig = (RingConfig)ringConfigTableModel.getValueAt(i, ringConfigTableModel.getColumnCount());
			if (port.equals(ringConfig.getPort1() + "," + ringConfig.getPort2())
					|| port.equals(ringConfig.getPort2() + "," + ringConfig.getPort1())){
				JOptionPane.showMessageDialog(this, "�˿ڱ�Rstp������Ringռ�ã���ѡ�������˿�","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}

		String sysType = sysTypeCombox.getSelectedItem().toString();
		String firstPortType = firstportMemberTypeCombox.getSelectedItem().toString();
		String secondPortType = secondportMemberTypeCombox.getSelectedItem().toString();
		if (sysType.equals("Master")){
			if (firstPortType.equals(secondPortType)){
				JOptionPane.showMessageDialog(this, "�˿���������ΪMasterʱ����Ա�˿ڱ���һ��ΪMaster��һ��ΪSubsidiary"
													,"��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			if (!((firstPortType.equals("Master") || firstPortType.equals("Subsidiary")) 
					&& (secondPortType.equals("Master") || secondPortType.equals("Subsidiary")))){
				JOptionPane.showMessageDialog(this, "�˿���������ΪMasterʱ����Ա�˿ڱ���һ��ΪMaster��һ��ΪSubsidiary"
													,"��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		if (sysType.equals("Assistant") || sysType.equals("Edge")){
			if (firstPortType.equals(secondPortType)){
				JOptionPane.showMessageDialog(this, "�˿���������ΪAssistant��Edgeʱ����Ա�˿ڱ���һ��ΪEdgeport��һ��ΪNone"
													,"��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			if (!((firstPortType.equals("Edgeport") || firstPortType.equals("None")) 
					&& (secondPortType.equals("Edgeport") || secondPortType.equals("None")))){
				JOptionPane.showMessageDialog(this, "�˿���������ΪAssistant��Edgeʱ����Ա�˿ڱ���һ��ΪEdgeport��һ��ΪNone"
													,"��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		for (int i = 0 ; i < table.getRowCount(); i++){
			RingConfig ringConfig = (RingConfig)ringConfigTableModel.getValueAt(0, ringConfigTableModel.getColumnCount());
			if (!sysType.equalsIgnoreCase(ringConfig.getSystemType())){
				JOptionPane.showMessageDialog(this, "ϵͳ����ֻ������һ�֣����Ƚ�ԭ����Ringɾ���ټ�������","��ʾ",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}

		return isValid;
	}
}
