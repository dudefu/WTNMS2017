package com.jhw.adm.client.views;

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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.client.views.switcher.UploadRequestTask;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.RmonThing;
import com.jhw.adm.server.entity.warning.RmonWarningConfig;
import com.jhw.adm.server.entity.warning.ThresholdValue;

@Component(ConfigPortWarningView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigPortWarningView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "configPortWarningView";

	private final JPanel topPnl = new JPanel();
	private final JLabel itemCodeLbl = new JLabel();//��Ŀ���
	private final NumberField itemCodeFld = new NumberField(5, 0, 1, 65535, true);
	
	private final JLabel portLbl = new JLabel();//�˿ں�
	private final JComboBox portCombox = new JComboBox();
	
	private final JLabel warningLbl = new JLabel();//�澯����
	private final JTextField warnVariableFld = new JTextField();
	
	private final JLabel sampleTimeLbl = new JLabel();//����ʱ��
	private final NumberField sampleTimeFld = new NumberField(4, 0, 0, 3600, true);
	
	private final JLabel sampleTypeLbl = new JLabel();//��������
	private final JComboBox sampleTypeCombox = new JComboBox();
	
	private final JLabel upLimitValueLbl = new JLabel();//���޷�ֵ
	private final JTextField upLimitValueFld = new JTextField();
	
	private final JLabel lowLimitValueLbl = new JLabel();//���޷�ֵ
	private final JTextField lowLimitValueFld = new JTextField();
	
	private final JLabel trapNoLbl = new JLabel();//trap�����¼����
	private final JTextField trapNoFld = new JTextField();
	private final JButton trapBtn = new JButton("...");
	
	private final JLabel trapLowNoLbl = new JLabel();//trap�����¼����
	private final JTextField trapLowNoFld = new JTextField();
	private final JButton trapLowBtn = new JButton("...");
	
	private final JPanel centerPnl = new JPanel();
	
	private final JPanel toolPnl = new JPanel();
	private JButton saveBtn ;
	private JButton deleteBtn;
	
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JTable table = new JTable();
	private PortWarningTableModel tableModel = null;
	
	private final JPanel bottomPnl = new JPanel();
	
	private static final String ITEM_RANGE = "1-65535";
	private static final String TIME_RANGE = "5-3600";
	private static final String UP_RANGE = "1-4294967295";
	private static final String DOWN_RANGE = "1-4294967295";
	
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn ;
	
	private final static String[] VALUES = {"�仯ֵ","����ֵ"};
	private final static String[] WARNINGVALUES = {Constants.octets,Constants.packets,Constants.bcast_pkts,Constants.mcast_pkts,
											Constants.crc_align,Constants.undersize,Constants.oversize,Constants.fragments,
											Constants.jabbers,Constants.collisions,Constants.pkts_64,Constants.pkts_65_127,
											Constants.pkts_128_255,Constants.pkts_256_511,Constants.pkts_512_1023,Constants.pkts_1024_1518};
	private final static String[] COLUMN_NAME = {"��Ŀ���","�˿ں�","�澯����","����ʱ��","��������"
											,"���޷�ֵ","���޷�ֵ","trap�����¼����","trap�����¼����","״̬"};
	
	private SwitchNodeEntity switchNodeEntity = null;

	private ButtonFactory buttonFactory;
	
	private MessageSender messageSender;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;	
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;

	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private static final Logger LOG = LoggerFactory.getLogger(ConfigPortWarningView.class);
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
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
		
		initTopPnl();
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(700, 520);
		
		setResource();
	}
	
	private void initTopPnl(){
		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(itemCodeLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(itemCodeFld,new GridBagConstraints(1,0,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		panel.add(new StarLabel("(" + ITEM_RANGE + ")"),new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		
		panel.add(portLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(portCombox,new GridBagConstraints(1,1,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		
		panel.add(warningLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(warnVariableFld,new GridBagConstraints(1,2,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		
		panel.add(sampleTimeLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(sampleTimeFld,new GridBagConstraints(1,3,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		panel.add(new StarLabel("(" + TIME_RANGE + ")"),new GridBagConstraints(3,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		
		panel.add(sampleTypeLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(sampleTypeCombox,new GridBagConstraints(1,4,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		
		panel.add(upLimitValueLbl,new GridBagConstraints(0,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(upLimitValueFld,new GridBagConstraints(1,5,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		panel.add(new StarLabel("(" + UP_RANGE + ")"),new GridBagConstraints(3,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		
		panel.add(lowLimitValueLbl,new GridBagConstraints(0,7,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(lowLimitValueFld,new GridBagConstraints(1,7,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		panel.add(new StarLabel("(" + DOWN_RANGE + ")"),new GridBagConstraints(3,7,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		
		panel.add(trapNoLbl,new GridBagConstraints(0,8,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(trapNoFld,new GridBagConstraints(1,8,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		panel.add(trapBtn,new GridBagConstraints(2,8,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(new StarLabel("(1-65535)"),new GridBagConstraints(3,8,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		
		panel.add(trapLowNoLbl,new GridBagConstraints(0,9,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(trapLowNoFld,new GridBagConstraints(1,9,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		panel.add(trapLowBtn,new GridBagConstraints(2,9,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(new StarLabel("(1-65535)"),new GridBagConstraints(3,9,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		
		topPnl.add(panel);
	}
	
	private void initCenterPnl(){
		toolPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);
		deleteBtn = buttonFactory.createButton(DELETE);
		toolPnl.add(saveBtn);
		toolPnl.add(deleteBtn);
		
		tableModel = new PortWarningTableModel();
		tableModel.setColumnName(COLUMN_NAME);
		table.setModel(tableModel);
		scrollPnl.getViewport().add(table);
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(toolPnl,BorderLayout.NORTH);
		centerPnl.add(scrollPnl,BorderLayout.CENTER);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		closeBtn = buttonFactory.createCloseButton();
		
		JPanel newPanel = new JPanel(new GridBagLayout());
		newPanel.add(uploadBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		bottomPnl.add(newPanel, BorderLayout.EAST);
		this.setCloseButton(closeBtn);
	}
	
	private void setResource(){
		this.setTitle("�˿ڸ澯����");
		topPnl.setBorder(BorderFactory.createTitledBorder("�˿ڸ澯"));
		scrollPnl.setBorder(BorderFactory.createTitledBorder(""));
		itemCodeLbl.setText("��Ŀ���");
		portLbl.setText("�˿ں�");
		warningLbl.setText("�澯����");
		sampleTimeLbl.setText("����ʱ��");
		sampleTypeLbl.setText("��������");
		upLimitValueLbl.setText("���޷�ֵ");
		lowLimitValueLbl.setText("���޷�ֵ");
		trapNoLbl.setText("trap�����¼����");
		trapNoFld.setEnabled(false);
		trapLowNoLbl.setText("trap�����¼����");
		trapLowNoFld.setEnabled(false);
		
		warnVariableFld.setEnabled(true);
		warnVariableFld.setBackground(Color.white);

		lowLimitValueFld.setColumns(22);
		trapNoFld.setColumns(21);
		trapLowNoFld.setColumns(21);
		trapBtn.setPreferredSize(new Dimension(19,19));
		trapLowBtn.setPreferredSize(new Dimension(19,19));
		itemCodeFld.setHorizontalAlignment(JTextField.LEADING);
		sampleTimeFld.setHorizontalAlignment(JTextField.LEADING);
		
		upLimitValueFld.setDocument(new TextFieldPlainDocument(upLimitValueFld));
		lowLimitValueFld.setDocument(new TextFieldPlainDocument(upLimitValueFld));
		
		upLimitValueFld.addKeyListener(new KeyAdapter(){
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() < '0' || e.getKeyChar() > '9'){
					e.consume();
				}
			}
		});
		
		lowLimitValueFld.addKeyListener(new KeyAdapter(){
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() < '0' || e.getKeyChar() > '9'){
					e.consume();
				}
			}
		});
		
		ButtonActionListener btnActionListener = new ButtonActionListener();
		trapBtn.addActionListener(btnActionListener);
		
		LowButtonActionListener lowActionListener = new LowButtonActionListener();
		trapLowBtn.addActionListener(lowActionListener);
		
		//���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		setCombox();
		
		//�����ݿ��в�ѯ����̽������
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<RmonWarningConfig> rmonWarningConfigList = (List<RmonWarningConfig>)remoteServer.getService()
																.findAll(RmonWarningConfig.class, where, parms);
		if(null == rmonWarningConfigList){
			return;
		}
		setValue(rmonWarningConfigList);	
	}
	
	/**
	 * ���ö˿�������
	 */
	private void setCombox(){
		sampleTypeCombox.removeAllItems();
		for(int i = 0 ; i < VALUES.length; i++){
			sampleTypeCombox.addItem(VALUES[i]);
		}
		
		int portCount = switchNodeEntity.getPorts().size();
		for (int i = 0 ; i < portCount; i++){
			portCombox.addItem(i+1+"");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(List<RmonWarningConfig> rmonWarningConfigList){
		//warnVariableFld.setText("1.3.6.1.4.1.44405.71.4.1.0.0");
		
		List<List> dataList = new ArrayList<List>();
		for(int i = 0 ; i < rmonWarningConfigList.size(); i++){
			RmonWarningConfig rmonWarningConfig = rmonWarningConfigList.get(i);
			int itemCode = rmonWarningConfig.getItemCode();  //��Ŀ���
			int portNo = rmonWarningConfig.getPortNo();      //�˿ں�
			String param = rmonWarningConfig.getThreshold().getWarnParm();  //�澯����
			int sampleTime = rmonWarningConfig.getSampleTime();  //����ʱ��
			int type = rmonWarningConfig.getThreshold().getSamplingType(); //��������
			int max = rmonWarningConfig.getThreshold().getK_Max();   //���޷�ֵ
			int low = rmonWarningConfig.getThreshold().getK_low();//���޷�ֵ
			String trapEventCode = rmonWarningConfig.getMaxLimitcode();//trap�����¼����
			String trapLowCode = rmonWarningConfig.getLowLimitcode();//trap�����¼����
			String status = dataStatus.get(rmonWarningConfig.getIssuedTag()).getKey();//״̬
			
			List rowList = new ArrayList();
			rowList.add(0,itemCode);
			rowList.add(1,portNo);
			rowList.add(2,param);
			rowList.add(3,sampleTime);
			rowList.add(4,reserveIntToString(type));
			rowList.add(5,max);
			rowList.add(6,low);
			rowList.add(7,trapEventCode);
			rowList.add(8,trapLowCode);
			rowList.add(9,status);
			rowList.add(10,rmonWarningConfig);
			
			dataList.add(rowList);
		}
		
		tableModel.setDataList(dataList);
		tableModel.fireTableDataChanged();
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="���ض˿ڸ澯������Ϣ",role=Constants.MANAGERCODE)
	public void upload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHPORTWARN);
		showUploadMessageDialog(task, "���ض˿ڸ澯������Ϣ");
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			paramConfigStrategy = new ParamConfigStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("��ʾ",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(paramConfigStrategy);
			dialog.run(task);
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
			uploadStrategy = new ParamUploadStrategy(operation, progressBarModel,true);
			dialog = new JProgressBarDialog("��ʾ",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(uploadStrategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showUploadMessageDialog(task, operation);
				}
			});
		}
	}
	
	/**
	 * ���水ť�¼�
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE,desc="����˿ڸ澯����",role=Constants.MANAGERCODE)
	public void save(){
		if (!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "��ѡ�񱣴�ķ�ʽ",imageRegistry);
		if (result == 0){
			return;
		}
		
		int itemCode = NumberUtils.toInt(itemCodeFld.getText()); 
		int portNo = NumberUtils.toInt(portCombox.getSelectedItem().toString());
		String param = warnVariableFld.getText();
		int sampleTime = NumberUtils.toInt(sampleTimeFld.getText());
		int type = reserveStringToInt(sampleTypeCombox.getSelectedItem().toString());
		int max = NumberUtils.toInt(upLimitValueFld.getText());
		int low = NumberUtils.toInt(lowLimitValueFld.getText());
		String trapCode = trapNoFld.getText().trim();
		String trapLowCode = trapLowNoFld.getText().trim();
		
		RmonWarningConfig rmonWarningConfig = new RmonWarningConfig();
		rmonWarningConfig.setItemCode(itemCode);
		rmonWarningConfig.setPortNo(portNo);
		rmonWarningConfig.setSampleTime(sampleTime);
		
		ThresholdValue thresholdValue = new ThresholdValue();
		thresholdValue.setWarnParm(param);
		thresholdValue.setSamplingType(type);
		thresholdValue.setK_Max(max);
		thresholdValue.setK_low(low);
		rmonWarningConfig.setThreshold(thresholdValue);
		
		rmonWarningConfig.setMaxLimitcode(trapCode);
		rmonWarningConfig.setLowLimitcode(trapLowCode);
		rmonWarningConfig.setSwitchNode(switchNodeEntity);
		rmonWarningConfig.setIssuedTag(Constants.ISSUEDADM);
		
		List<Serializable> dataList = new ArrayList<Serializable>();
		dataList.add(rmonWarningConfig);
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		Task task = new SaveRequestTask(dataList, macValue, result);
		showConfigureMessageDialog(task, "����");
	}
	
	private class SaveRequestTask implements Task{

		private List<Serializable> list = null;
		private String macValue = "";
		private int result = 0;
		public SaveRequestTask(List<Serializable> list, String macValue, int result){
			this.list = list;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getAdmService().saveAndSetting(macValue, MessageNoConstants.PWARNINGCONFIG, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2, result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("����˿ڸ澯����");
				queryData();
				itemCodeFld.setText("");
				sampleTimeFld.setText("");
				upLimitValueFld.setText("");
				lowLimitValueFld.setText("");
				trapNoFld.setText("");
				trapLowNoFld.setText("");
				LOG.error("ConfigPortWarningView.save() error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
				itemCodeFld.setText("");
				sampleTimeFld.setText("");
				upLimitValueFld.setText("");
				lowLimitValueFld.setText("");
				trapNoFld.setText("");
				trapLowNoFld.setText("");
			}
		}
	}
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="���ض˿ڸ澯����",role=Constants.MANAGERCODE)
	public void download(){
		int result = Constants.SYN_ALL;
		if (table.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "��ѡ����Ҫ���ص�����", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		
		List<Serializable> dataList = new ArrayList<Serializable>();
		List<List> list = tableModel.getDataList();
		int selectRowCount = table.getSelectedRowCount();
		for(int i = 0 ; i < selectRowCount; i++){
			int row = table.getSelectedRows()[i];
			int modelRow = table.convertRowIndexToModel(row);
			RmonWarningConfig rmonWarningConfig = (RmonWarningConfig)list.get(modelRow).get(10);
			int issuedTag = rmonWarningConfig.getIssuedTag();
			if (issuedTag == Constants.ISSUEDDEVICE){
				JOptionPane.showMessageDialog(this, "����Ķ˿ڸ澯��Ϣ�Ѿ����ڣ�����������", "��ʾ", JOptionPane.NO_OPTION);
				return;
			}
			
			rmonWarningConfig.setIssuedTag(Constants.ISSUEDADM);
			dataList.add(rmonWarningConfig);
		}

		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		Task task = new DownLoadRequestTask(dataList, macValue, result);
		showConfigureMessageDialog(task, "����");
	}
	
	private class DownLoadRequestTask implements Task{

		private List<Serializable> list = null;
		private String macValue = "";
		private int result = 0;
		public DownLoadRequestTask(List<Serializable> list, String macValue, int result){
			this.list = list;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.PWARNINGCONFIG, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2, result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("���ض˿ڸ澯�����쳣");
				queryData();
				LOG.error("ConfigPortWarningView.download() error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	/**
	 * ɾ����ť�¼�
	 */
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE,desc="ɾ���˿ڸ澯����",role=Constants.MANAGERCODE)
	public void delete(){
		if (table.getSelectedRowCount() < 1){
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		//���豸������ܲ�Ҫɾ������Ϣ�ֱ�ŵ��б�dataDeviceList��dataServerList��
		//���·�ɾ������ʱ���������ܲ໹���豸��ֱ����ɾ��
		List<Serializable> dataDeviceList = new ArrayList<Serializable>();//�豸������ܲ�
		List<Serializable> dataServerList = new ArrayList<Serializable>();//���ܲ�
		boolean isDevice = false;
		boolean isServer = false;
		
		List<List> list = tableModel.getDataList();
		int selectRowCount = table.getSelectedRowCount();
		for(int i = 0 ; i < selectRowCount; i++){
			int row = table.getSelectedRows()[i];
			int modelRow = table.convertRowIndexToModel(row);
			RmonWarningConfig rmonWarningConfig = (RmonWarningConfig)list.get(modelRow).get(10);
			
			if(rmonWarningConfig.getIssuedTag() == Constants.ISSUEDADM){//���ܲ�
				isServer = true;
				dataServerList.add(rmonWarningConfig);
			}
			else{//�豸������ܲ�
				isDevice = true;
				dataDeviceList.add(rmonWarningConfig);
			}
		}

		String macValue = switchNodeEntity.getBaseInfo().getMacValue();

		Task task = new DeleteRequestTask(isDevice, isServer, macValue, dataDeviceList, dataServerList);
		showConfigureMessageDialog(task, "ɾ��");
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
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.PWARNINGCONFIGDELETE, dataServerList,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, Constants.SYN_SERVER);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("ɾ���˿ڸ澯�����쳣");
					queryData();
					LOG.error("ConfigPortWarningView.delete() error:{}", e);
				}
				
				//ɾ���豸��
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.PWARNINGCONFIGDELETE, dataDeviceList,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, Constants.SYN_ALL);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("ɾ���˿ڸ澯�����쳣");
					queryData();
					LOG.error("ConfigPortWarningView.delete() error:{}", e);
				}
			}
			else if(isDevice == true && isServer == false){//ֻɾ���豸��
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.PWARNINGCONFIGDELETE, dataDeviceList,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, Constants.SYN_ALL);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("ɾ���˿ڸ澯�����쳣");
					queryData();
					LOG.error("ConfigPortWarningView.delete() error:{}", e);
				}
			}
			else if(isDevice == false && isServer == true){//ֻɾ�����ܲ�
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.PWARNINGCONFIGDELETE, dataServerList,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, Constants.SYN_SERVER);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("ɾ���˿ڸ澯�����쳣");
					queryData();
					LOG.error("ConfigPortWarningView.delete() error:{}", e);
				}
				paramConfigStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			}
			queryData();
		}
	}
	
	/**
	 * �ж�������Ƿ�Ϸ�
	 * @return
	 */
	private boolean isValids(){
		boolean valid = true;
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		
		if (null == itemCodeFld.getText() || "".equals(itemCodeFld.getText())){
			JOptionPane.showMessageDialog(this, "��Ŀ��Ŵ��󣬷�Χ�ǣ�1-65535","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		
		int itemCode = NumberUtils.toInt(itemCodeFld.getText().trim()); 
		for (int i = 0 ; i < table.getRowCount(); i++){
			RmonWarningConfig rmonWarningConfig = (RmonWarningConfig)tableModel.getValueAt(i, table.getColumnCount());
			if (itemCode == rmonWarningConfig.getItemCode()){
				JOptionPane.showMessageDialog(this, "��Ŀ����ظ�������������" ,"��ʾ",JOptionPane.NO_OPTION);
				valid = false;
				return valid;
			}
		}
		
		if (null == portCombox.getSelectedItem() || "".equals(portCombox.getSelectedItem().toString())){
			JOptionPane.showMessageDialog(this, "�˿ںŲ���Ϊ�գ���ѡ��˿ں�","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		if (null == warnVariableFld.getText() || "".equals(warnVariableFld.getText())){
			JOptionPane.showMessageDialog(this, "�澯��������Ϊ�գ���ѡ��澯����","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		if (!checkAlarmVar(warnVariableFld.getText())){
			JOptionPane.showMessageDialog(this, "�澯������ʽ����Ӧ���ǽڵ�OID�ĵ�ָ�ʽ","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		if (null == sampleTimeFld.getText() || "".equals(sampleTimeFld.getText())){
			JOptionPane.showMessageDialog(this, "����ʱ����󣬷�Χ�ǣ�5-3600","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}else{
			if(!StringUtils.isNumeric(sampleTimeFld.getText()) 
					|| ((NumberUtils.toInt(sampleTimeFld.getText()) < 5) || (NumberUtils.toInt(sampleTimeFld.getText()) > 3600))){
				JOptionPane.showMessageDialog(this, "����ʱ����󣬷�Χ�ǣ�5-3600","��ʾ",JOptionPane.NO_OPTION);
				valid = false;
				return valid;
			}
		}
		if (null == upLimitValueFld.getText() || "".equals(upLimitValueFld.getText())){
			JOptionPane.showMessageDialog(this, "���޷�ֵ���󣬷�Χ�ǣ�1-4294967295","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		else{
			if((NumberUtils.toLong(upLimitValueFld.getText()) < 1) || (NumberUtils.toLong(upLimitValueFld.getText()) > 4294967295l)){
				JOptionPane.showMessageDialog(this, "���޷�ֵ���󣬷�Χ�ǣ�1-4294967295","��ʾ",JOptionPane.NO_OPTION);
				valid = false;
				return valid;
			}
		}
		if (null == lowLimitValueFld.getText() || "".equals(lowLimitValueFld.getText())){
			JOptionPane.showMessageDialog(this, "���޷�ֵ���󣬷�Χ�ǣ�1-4294967295","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		else{
			if((NumberUtils.toLong(lowLimitValueFld.getText()) < 1) || (NumberUtils.toLong(lowLimitValueFld.getText()) > 4294967295l)){
				JOptionPane.showMessageDialog(this, "���޷�ֵ���󣬷�Χ�ǣ�1-4294967295","��ʾ",JOptionPane.NO_OPTION);
				valid = false;
				return valid;
			}
		}
		
		if(NumberUtils.toLong(upLimitValueFld.getText()) < NumberUtils.toLong(lowLimitValueFld.getText())){
			JOptionPane.showMessageDialog(this, "���޷�ֵ����С�����޷�ֵ������������","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		
		if (null == trapNoFld.getText() || "".equals(trapNoFld.getText())){
			JOptionPane.showMessageDialog(this, "trap�����¼���Ŵ��󣬷�Χ�ǣ�1-65535","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		
		if (null == trapLowNoFld.getText() || "".equals(trapLowNoFld.getText())){
			JOptionPane.showMessageDialog(this, "trap�����¼���Ŵ��󣬷�Χ�ǣ�1-65535","��ʾ",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		
		return valid;
	}
	
	private boolean checkAlarmVar(String v){
		for(int i = 0; i < v.length();i++){
		    if(v.charAt(i)!='.' && (v.charAt(i)>'9' || v.charAt(i)<'0')){
		    	return false; 
		    }
		}	
		return true;
	}
	
	class ButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			showEventDialog(true);
		}	
	}

	class LowButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			showEventDialog(false);
		}	
	}
	
	private void showEventDialog(boolean isMax){
		JDialog dialog = new JDialog(ClientUtils.getRootFrame(), "�¼���", true);
		EventGroupView viewPart = new EventGroupView(this,dialog,imageRegistry,remoteServer, actionManager,
				messageDispatcher,dataStatus,isMax);
		dialog.getContentPane().add(viewPart);
		dialog.setResizable(false);
		dialog.setVisible(true);
	}
	
	public ClientModel getClientModel(){
		return clientModel;
	}
	
	public void setTrapNoFld(RmonThing rmonThing){
		if (null == rmonThing){
			return;
		}
		String trapCode = rmonThing.getCode();
		trapNoFld.setText(trapCode);
	}
	
	public void setTrapLowNoFld(RmonThing rmonThing){
		if (null == rmonThing){
			return;
		}
		String trapLowCode = rmonThing.getCode();
		trapLowNoFld.setText(trapLowCode);
	}
	
	private void clear(){
		itemCodeFld.setText("");
		portCombox.removeAllItems();
		warnVariableFld.setText("");
		sampleTimeFld.setText("");
		sampleTypeCombox.removeAllItems();
		upLimitValueFld.setText("");
		lowLimitValueFld.setText("");
		trapNoFld.setText("");
		trapLowNoFld.setText("");
	}
	
	private String reserveIntToString(int value){
		String str = "";
		if (value == 0){
			str = "�仯ֵ";
		}
		else if(value == 1){
			str = "����ֵ";
		}
		return str;
	}
	
	private int reserveStringToInt(String str){
		int value = 0;
		if ("�仯ֵ".equals(str)){
			value = 0 ;
		}
		else if("����ֵ".equals(str)){
			value = 1;
		}
		
		return value;
	}
	
	public SwitchNodeEntity getSwitchNodeEntity(){
		return switchNodeEntity;
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchNodeEntity.class);
			queryData();
		}
	};
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
	}
	
	@SuppressWarnings("unchecked")
	public class PortWarningTableModel extends AbstractTableModel{
		
		private static final long serialVersionUID = 1L;
		private List<List> dataList = new ArrayList<List>();
		private String[] columnName = null;

		public List<List> getDataList() {
			return dataList;
		}

		public void setDataList(List<List> dataList) {
			if (null == dataList){
				dataList = new ArrayList<List>();
			}
			else{
				this.dataList = dataList;
			}
		}

		public String[] getColumnName() {
			return columnName;
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		@Override
		public int getColumnCount() {
			return columnName.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			// TODO Auto-generated method stub
			return columnName[columnIndex];
		}

		@Override
		public int getRowCount() {
			return dataList.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (null == dataList.get(rowIndex)){
				return null;
			}
			return dataList.get(rowIndex).get(columnIndex);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}


		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (null == dataList.get(rowIndex)){
				return;
			}
			dataList.get(rowIndex).set(columnIndex, aValue);
		}
	}
}
