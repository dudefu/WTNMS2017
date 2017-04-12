package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.jhw.adm.client.model.switcher.QOSSysViewModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.UploadMessageProcessorStrategy;
import com.jhw.adm.client.views.SpringUtilities;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.QOSSysConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(QOSSysConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class QOSSysConfigurationView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;

	public static final String ID = "qosSysConfigurationView";
	
	//系统配置面板
	private final JPanel sysPnl = new JPanel();
	
	private final JPanel configPnl = new JPanel();
	private final JLabel mode = new JLabel();
	private final JCheckBox modeChkBox = new JCheckBox();
	
	private final JLabel queueLbl = new JLabel();
	private final JComboBox queueCombox = new JComboBox();
	private static final String[] QUEUELIST = {"2","3","4"};
	
	private final JLabel algorithmLbl = new JLabel();
	private final JComboBox algorithmCombox = new JComboBox();
	private static final String[] ALGORITLIST = {"HQ_WRR","WRR"};
	
	private final JLabel priorityLbl = new JLabel();
	private final JComboBox priorityCombox = new JComboBox();
	private static final String[] PRIORITYLIST = {"dot1p","dscp","tos"};
	private static final String WEIGHT = "0-31";
	
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusField = new JTextField();
	
	//调度算法配置面板
	private final JPanel schedulePnl = new JPanel();
	private final JLabel queueZeroLbl = new JLabel();
	private final JLabel queueZeroValueLbl = new JLabel();
	private final JLabel weightZeroLbl = new JLabel();
	private final NumberField weightZeroFld = new NumberField(2,0,0,31,true);
//	private JLabel weightZeroRangeLbl = new JLabel();
	
	private final JLabel queueFirstLbl = new JLabel();
	private final JLabel queueFirstValueLbl = new JLabel();
	private final JLabel weightFirstLbl = new JLabel();
	private final NumberField weightFirstFld = new NumberField(2,0,0,31,true);
//	private JLabel weightFirstRangeLbl = new JLabel();
	
	private final JLabel queueSecondLbl = new JLabel();
	private final JLabel queueSecondValueLbl = new JLabel();
	private final JLabel weightSecondLbl = new JLabel();
	private final NumberField weightSecondFld = new NumberField(2,0,0,31,true);
//	private JLabel weighSecondRangeLbl = new JLabel();
	
	private final JLabel queueThirdLbl = new JLabel();
	private final JLabel queueThirdValueLbl = new JLabel();
	private final JLabel weightThirdLbl = new JLabel();
	private final NumberField weightThirdFld = new NumberField(2,0,0,31,true);
//	private JLabel weighThirdRangeLbl = new JLabel();
	
	//下端按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn = null;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	private MessageSender messageSender;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=QOSSysViewModel.ID)
	private QOSSysViewModel qosSysViewModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;

	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private final UploadMessageProcessorStrategy uploadMessageProcessorStrategy = new UploadMessageProcessorStrategy();
	private static final Logger LOG = LoggerFactory.getLogger(QOSSysConfigurationView.class);
	
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
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
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
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		initSysPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		
		this.add(sysPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}

	private void initSysPnl(){
		for (int i = 0 ; i < QUEUELIST.length ; i++){
			queueCombox.addItem(QUEUELIST[i]);
		}
		
		for (int j = 0 ; j < ALGORITLIST.length ; j++){
			algorithmCombox.addItem(ALGORITLIST[j]);
			if(j == 1){
				algorithmCombox.setSelectedItem(ALGORITLIST[j]);
			}
		}
		
		for (int k = 0 ; k < PRIORITYLIST.length ; k++){
			priorityCombox.addItem(PRIORITYLIST[k]);
		}
		
		modeChkBox.setPreferredSize(new Dimension(120,modeChkBox.getPreferredSize().height));
		
		modeChkBox.setSelected(false);
		queueCombox.setSelectedItem(null);
		priorityCombox.setSelectedItem(null);
		
		JPanel panel = new JPanel(new SpringLayout());
		panel.add(mode);
		panel.add(modeChkBox);
		panel.add(queueLbl);
		panel.add(queueCombox);
		panel.add(algorithmLbl);
		panel.add(algorithmCombox);
		panel.add(priorityLbl);
		panel.add(priorityCombox);
		SpringUtilities.makeCompactGrid(panel, 4, 2, 5, 5, 20, 5);
		
		configPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		configPnl.add(panel);
		configPnl.setBorder(BorderFactory.createTitledBorder("系统配置"));
		
		initSchedulePnl();
		
		sysPnl.setLayout(new BorderLayout());
		sysPnl.add(configPnl,BorderLayout.NORTH);
		sysPnl.add(schedulePnl,BorderLayout.CENTER);
	}
	
	private void initSchedulePnl(){
		weightZeroFld.setColumns(20);
		weightFirstFld.setColumns(20);
		weightSecondFld.setColumns(20);
		weightThirdFld.setColumns(20);
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(queueZeroLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,30),0,0));
		panel.add(weightZeroLbl,new GridBagConstraints(1,0,3,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,50),0,0));
		
		panel.add(queueFirstLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,30),0,0));
		panel.add(weightFirstLbl,new GridBagConstraints(5,0,3,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		
		panel.add(queueZeroValueLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,0,30),0,0));
		panel.add(weightZeroFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
//		panel.add(weightZeroRangeLbl,new GridBagConstraints(2,1,1,1,0.0,0.0,
//				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		panel.add(new StarLabel("(" + WEIGHT + ")"),new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,50),0,0));
		
		panel.add(queueFirstValueLbl,new GridBagConstraints(4,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,30),0,0));
		panel.add(weightFirstFld,new GridBagConstraints(5,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
//		panel.add(weightFirstRangeLbl,new GridBagConstraints(6,1,1,1,0.0,0.0,
//				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		panel.add(new StarLabel("(" + WEIGHT + ")"),new GridBagConstraints(6,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		
		panel.add(queueSecondLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(20,5,0,30),0,0));
		panel.add(weightSecondLbl,new GridBagConstraints(1,2,3,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(20,0,0,50),0,0));
		
		panel.add(queueThirdLbl,new GridBagConstraints(4,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(20,0,0,30),0,0));
		panel.add(weightThirdLbl,new GridBagConstraints(5,2,3,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(20,0,0,0),0,0));
		
		panel.add(queueSecondValueLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,0,30),0,0));
		panel.add(weightSecondFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
//		panel.add(weighSecondRangeLbl,new GridBagConstraints(2,3,1,1,0.0,0.0,
//				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		panel.add(new StarLabel("(" + WEIGHT + ")"),new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,30),0,0));
		
		panel.add(queueThirdValueLbl,new GridBagConstraints(4,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,30),0,0));
		panel.add(weightThirdFld,new GridBagConstraints(5,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
//		panel.add(weighThirdRangeLbl,new GridBagConstraints(6,3,1,1,0.0,0.0,
//				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		panel.add(new StarLabel("(" + WEIGHT + ")"),new GridBagConstraints(6,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		
		JLabel promptLbl = new JLabel("(提示：当队列的权重值为0时，会导致网管无法发现此设备。)");
		promptLbl.setForeground(new Color(35,175,98));
		panel.add(promptLbl,new GridBagConstraints(0,4,6,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(20,5,0,0),0,0));
		
		
		schedulePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		schedulePnl.add(panel);
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
		
		JPanel statusPanel = new JPanel();
		statusPanel.add(statusLbl);
		statusField.setColumns(15);
		statusField.setBackground(Color.WHITE);
		statusField.setEditable(false);
		statusPanel.add(statusField);
		bottomPnl.add(statusPanel, BorderLayout.WEST);
	}
	
	private void setResource() {
		schedulePnl.setBorder(BorderFactory.createTitledBorder("调度算法配置"));
		mode.setText("QoS模式");
		queueLbl.setText("队列数");
		algorithmLbl.setText("调度算法");
		priorityLbl.setText("优先级模式");
		statusLbl.setText("状态");
		statusField.setEditable(false);
		statusField.setBackground(Color.WHITE);
	
		queueZeroLbl.setText("队列");
		queueZeroValueLbl.setText("0");
		weightZeroLbl.setText("权重");
//		weightZeroRangeLbl.setText("(0-31)");
				
		queueFirstLbl.setText("队列");
		queueFirstValueLbl.setText("1");
		weightFirstLbl.setText("权重");
//		weightFirstRangeLbl.setText("(0-31)");
			
		queueSecondLbl.setText("队列");
		queueSecondValueLbl.setText("2");
		weightSecondLbl.setText("权重");
//		weighSecondRangeLbl.setText("(0-31)");
				
		queueThirdLbl.setText("队列");
		queueThirdValueLbl.setText("3");
		weightThirdLbl.setText("权重");
//		weighThirdRangeLbl.setText("(0-31)");
		
		algorithmCombox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if (algorithmCombox.getSelectedItem().equals(ALGORITLIST[0])){
					weightZeroFld.setEnabled(false);
//					weightFirstLbl.setEnabled(false);
					weightFirstFld.setEnabled(false);
					weightSecondFld.setEnabled(false);
					weightThirdFld.setEnabled(false);
				}
				else{
					weightZeroFld.setEnabled(true);
					weightFirstFld.setEnabled(true);
					weightSecondFld.setEnabled(true);
					weightThirdFld.setEnabled(true);
				}
			}
		});
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		
	}
	
	
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<QOSSysConfig> qosSysConfigList = (List<QOSSysConfig>)remoteServer.getService().findAll(QOSSysConfig.class, where, parms);
		if (null == qosSysConfigList || qosSysConfigList.size() < 1){
			return;
		}
		
		QOSSysConfig qosSysConfig = qosSysConfigList.get(0);
		if (null == qosSysConfig){
			return;
		}
		
		qosSysViewModel.setQosSysConfig(qosSysConfig);
		setValue(qosSysConfig);
	}
	
	private void setValue(final QOSSysConfig qosSysConfig) {
		
		if (SwingUtilities.isEventDispatchThread()) {
			modeChkBox.setSelected(qosSysConfig.isApplied());
			queueCombox.setSelectedItem(qosSysConfig.getQueueValue()+"");
			algorithmCombox.setSelectedItem(qosSysConfig.getDispatchAlgorithms().toUpperCase());
			priorityCombox.setSelectedItem(reverseIntToString(qosSysConfig.getPriorityMode()));
			statusField.setText(dataStatus.get(qosSysConfig.getIssuedTag()).getKey());
			if(qosSysConfig.getDispatchConfig() != null){
				String[] dispCfgStr = qosSysConfig.getDispatchConfig().split(",");
				weightZeroFld.setText(dispCfgStr[0]);
				weightFirstFld.setText(dispCfgStr[1]);
				weightSecondFld.setText(dispCfgStr[2]);
				weightThirdFld.setText(dispCfgStr[3]);			
			}
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setValue(qosSysConfig);
				}
			});
		}
	}
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载QOS系统配置信息",role=Constants.MANAGERCODE)
	public void download(){
		
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return ;
		}
		
		QOSSysConfig qosSysConfig = getQOSSysConfig();
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		List<Serializable> list = new ArrayList<Serializable>();
		list.add(qosSysConfig);
		showMessageDownloadDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.QOS_SYSCONFIG, list,
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("下载出现异常");
			LOG.error("QOSSysConfigurationView.save() error:{}", e);
			queryData();
		} catch (Exception ex) {
			messageOfSaveProcessorStrategy.showErrorMessage("下载出现异常");
			LOG.error("QOSSysConfigurationView.save() error:{}", ex);
			queryData();
		} 
		if(result == Constants.SYN_SERVER){
			//当只下发到网管侧而不下发到设备侧时，客户端先暂时更新返回的消息和状态
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			statusField.setText(dataStatus.get(Constants.ISSUEDADM).getKey());
		}
//		queryData();
	}
	
	/**
	 * 当数据下发到设备侧和网管侧时
	 * 对于接收到的异步消息进行处理
	 */
	private void showMessageDownloadDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("下载",this);
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载QoS系统配置信息",role=Constants.MANAGERCODE)
	public void upload() {

		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		uploadMessageProcessorStrategy.showInitializeDialog("上载", this);
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
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHQOSSYS);
				return message;
			}
		});
	}
	
	private QOSSysConfig getQOSSysConfig(){
		QOSSysConfig qosSysConfig = qosSysViewModel.getQosSysConfig();
		if(null == qosSysConfig){
			qosSysConfig =new QOSSysConfig();
		}
		qosSysConfig.setApplied(modeChkBox.isSelected());
		qosSysConfig.setQueueValue(Integer.parseInt(queueCombox.getSelectedItem().toString()));
		qosSysConfig.setDispatchAlgorithms(algorithmCombox.getSelectedItem().toString().toLowerCase());
		qosSysConfig.setPriorityMode(priorityCombox.getSelectedIndex());
		if (algorithmCombox.getSelectedItem().toString().equals(ALGORITLIST[1])){
//			String dispCfgStr = weightZeroFld.getText()+"," + weightFirstFld.getText()+"," 
//									+weightSecondLbl.getText() + "," + weightThirdFld.getText();
			String dispCfgStr = weightZeroFld.getText() + ","
					+ weightFirstFld.getText() + ","
					+ weightSecondFld.getText() + ","
					+ weightThirdFld.getText();
			qosSysConfig.setDispatchConfig(dispCfgStr);
		}
		
		qosSysConfig.setSwitchNode(switchNodeEntity);
		
		qosSysConfig.setIssuedTag(Constants.ISSUEDADM);
		return qosSysConfig;
	}
	
	/**
	 * 设置默认值
	 */
	private void clear(){
//		modeChkBox.setSelected(false);
//		queueCombox.setSelectedIndex(0);
//		algorithmCombox.setSelectedIndex(0);
//		priorityCombox.setSelectedIndex(0);
		switchNodeEntity = null;
		modeChkBox.setSelected(false);
		queueCombox.setSelectedItem(null);
		algorithmCombox.setSelectedItem(1);
		priorityCombox.setSelectedItem(null);
		
		weightZeroFld.setText("");
		weightFirstFld.setText("");
		weightSecondFld.setText("");
		weightThirdFld.setText("");
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
		}		
	};
	
	private String reverseIntToString(int value){
		String str = "";
		switch(value){
			case 0:
				str = "dot1p";
				break;
			case 1:
				str = "dscp";
				break;
			case 2:
				str = "tos";
				break;
		}
		
		return str;
	}
	
	private int reverseStringToInt(String str){
		int value = -1;
		if ("dot1p".equals(str)){
			value = 0;
		}
		else if ("dscp".equals(str)){
			value = 1;
		}
		else if ("tos".equals(str)){
			value = 2;
		}
		
		return value;
	}
	
	private boolean isValids(){
		boolean isValid = true;
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if (weightZeroFld.isEnabled()){
			if(null == weightZeroFld.getText() || "".equals(weightZeroFld.getText())){
				JOptionPane.showMessageDialog(this, "队列0权重设置错误，范围：0-31","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		if (weightFirstFld.isEnabled()){
			if(null == weightFirstFld.getText() || "".equals(weightFirstFld.getText())){
				JOptionPane.showMessageDialog(this, "队列1权重设置错误，范围：0-31","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		if (weightSecondFld.isEnabled()){
			if(null == weightSecondFld.getText() || "".equals(weightSecondFld.getText())){
				JOptionPane.showMessageDialog(this, "队列2权重设置错误，范围：0-31","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		if (weightThirdFld.isEnabled()){
			if(null == weightThirdFld.getText() || "".equals(weightThirdFld.getText())){
				JOptionPane.showMessageDialog(this, "队列3权重设置错误，范围：0-31","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		return isValid;
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	/**
	 * 对于接收到的异步消息进行处理
	 */
	public void receive(Object object){
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
}
