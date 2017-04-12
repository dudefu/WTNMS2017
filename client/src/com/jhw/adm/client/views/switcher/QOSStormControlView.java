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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
import com.jhw.adm.client.model.switcher.QOSStormViewModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.UploadMessageProcessorStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.QOSStormControl;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

@Component(QOSStormControlView.ID)
@Scope(Scopes.DESKTOP)
public class QOSStormControlView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;

	public static final String ID = "qosStormControlView";
	
	private final JPanel topPnl = new JPanel();
	private final JLabel portRateTypeLbl = new JLabel();
	private final JCheckBox mcastCheckbox = new JCheckBox();
	private final JCheckBox bcastCheckbox = new JCheckBox();
	private final JCheckBox dlfcastCheckbox = new JCheckBox();
//	ucast mcast bcast dlf
	
	//风暴控制面板
	private final JPanel controlPnl = new JPanel();
	private final JPanel centerPnl = new JPanel();
	private final JLabel portLbl = new JLabel();
	private final JLabel modeLbl = new JLabel();
	private final JTextField statusField = new JTextField(); 
	
	private final List<List> componentList = new ArrayList<List>();
	
	//下端的按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn = null;
	
	private static final String[] CONTROLPROPORTION = {"3%","5%","10%","20%"};
	
	private static final String[] UNIT = {"Mbps","Kbps"};
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private int portCount = 0;
	
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
	
	@Resource(name=QOSStormViewModel.ID)
	private QOSStormViewModel viewModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;	
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private final UploadMessageProcessorStrategy uploadMessageProcessorStrategy = new UploadMessageProcessorStrategy();
	
	private static final Logger LOG = LoggerFactory.getLogger(QOSStormControlView.class);

	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(500);
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
			ParmRep object = null;
			try {
				object = (ParmRep) message.getObject();
			} catch (JMSException e) {
				e.printStackTrace();
			}
			if (object == null){
				return;
			}
			int messageType = object.getMessageType();
			//
			if (messageType == MessageNoConstants.QOS_STORMCONTROL){
				//需再次上载一次数据
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
						message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHQOSSTORM);
						return message;
					}
				});
			}
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
		
		initTopPnl();
		initControlPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(controlPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initTopPnl(){
		topPnl.setLayout(new BorderLayout());
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(portRateTypeLbl,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 80), 0, 0));
		panel.add(mcastCheckbox,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		panel.add(bcastCheckbox,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		panel.add(dlfcastCheckbox,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		topPnl.add(panel, BorderLayout.WEST);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		closeBtn = buttonFactory.createCloseButton();
		newPanel.add(uploadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 10), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		bottomPnl.add(newPanel, BorderLayout.EAST);
		
		JPanel statusPanel = new JPanel();
		statusPanel.add(new JLabel("状态"));
		statusField.setColumns(15);
		statusField.setBackground(Color.WHITE);
		statusField.setEditable(false);
		statusPanel.add(statusField);
		bottomPnl.add(statusPanel, BorderLayout.WEST);
	}
	
	private void initControlPnl(){
		JPanel lblPnl = new JPanel(new GridBagLayout());
		lblPnl.add(portLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,0,0),0,0));
		lblPnl.add(modeLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,180,0,0),0,0));
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(lblPnl);
		
		centerPnl.setLayout(new GridBagLayout());
		JPanel middlePnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		middlePnl.add(centerPnl);
		
		
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(middlePnl);
		scrollPnl.setPreferredSize(new Dimension(600,150) );
		

		controlPnl.setLayout(new BorderLayout());
		controlPnl.add(topPnl,BorderLayout.NORTH);
		controlPnl.add(scrollPnl,BorderLayout.CENTER);
	}
	
	private void setResource(){
		portLbl.setText("端口");
		modeLbl.setText("抑制速率");
		
		portRateTypeLbl.setText("端口限速类型");
		mcastCheckbox.setText("多播");
		bcastCheckbox.setText("广播");
		dlfcastCheckbox.setText("未知单播");
		
		mcastCheckbox.setHorizontalTextPosition(SwingConstants.LEFT);
		bcastCheckbox.setHorizontalTextPosition(SwingConstants.LEFT);
		dlfcastCheckbox.setHorizontalTextPosition(SwingConstants.LEFT);  
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		portCount = switchNodeEntity.getPorts().size();
		setCenterLayout();
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		final List<QOSStormControl> qosStormControlList = (List<QOSStormControl>)remoteServer.getService().findAll(QOSStormControl.class, where, parms);
		if (null == qosStormControlList || qosStormControlList.size() < 1){
			return;
		}
		
		setValue(qosStormControlList);
	}
	
	private void setCenterLayout() {
		componentList.clear();
		centerPnl.removeAll();
		for (int i = 0 ; i < portCount ; i++) {
			JLabel label = new JLabel(i + 1 + "");
			NumberField textFld = new NumberField(6, 0, 0, 102400, true);
			JComboBox unitCombox = new JComboBox();			
			textFld.setColumns(15);
			
			for(int j = 0 ; j <UNIT.length ; j++ ){
				unitCombox.addItem(UNIT[j]);
			}
			unitCombox.setPreferredSize(new Dimension(60,unitCombox.getPreferredSize().height));
			
			centerPnl.add(label,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,12,10,0),0,0));
			centerPnl.add(textFld,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,140,10,0),0,0));
			centerPnl.add(unitCombox,new GridBagConstraints(2,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,10,0),0,0));

			List<JComponent> rowList = new ArrayList<JComponent>();

			rowList.add(0,label);
			rowList.add(1,textFld);
			rowList.add(2,unitCombox);
			
			componentList.add(rowList);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				centerPnl.revalidate();
				controlPnl.revalidate();
			}
		});
	}
	
	private void setValue(List<QOSStormControl> qosStormControlList) {

		int count = componentList.size();
		viewModel.setQosStormControlList(qosStormControlList);
		
		
        if (qosStormControlList.size() > 0) {
        	QOSStormControl qosStormControl = qosStormControlList.get(0);
    		mcastCheckbox.setSelected(qosStormControl.isMutilUnicast());
    		bcastCheckbox.setSelected(qosStormControl.isBroadcast());
    		dlfcastCheckbox.setSelected(qosStormControl.isUnknowUnicast());
    		statusField.setText(dataStatus.get(qosStormControl.getIssuedTag()).getKey());
        }
		
		for (int i = 0 ; i < count; i++){
			int port = i+1;
			QOSStormControl qosStormControl = viewModel.getQOSStormControl(port);			
			List<JComponent> rowList = componentList.get(i);
			((JLabel)rowList.get(0)).setText(port+"");
			((NumberField)rowList.get(1)).setText(Integer.toString(qosStormControl.getPercentNum()));
			((JComboBox)rowList.get(2)).setSelectedItem(qosStormControl.getUnit());
		}
	}
	
	@ViewAction(icon=ButtonConstants.DOWNLOAD, desc="下载QOS风暴控制信息",role=Constants.MANAGERCODE)
	public void download() {
		if (!isValids()){
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0) {
			return;
		}
		
		//组合QOSStormControl
		List<Serializable> list = getChangedDataLists();

		//下发服务端
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		showMessageDownloadDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.QOS_STORMCONTROL, list, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),switchNodeEntity.getDeviceModel(),result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("下载出现异常");
			LOG.error("QOSStormControlView.save() error:{}", e);
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
	}
	
	/**
	 * 当数据下发到设备侧和网管侧时
	 * 对于接收到的异步消息进行处理
	 */
	private void showMessageDownloadDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("下载",this);
	}
	
	@ViewAction(icon=ButtonConstants.SYNCHRONIZE, desc="上载QoS风暴控制信息",role=Constants.MANAGERCODE)
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
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHQOSSTORM);
				return message;
			}
		});
	}
	
	/**
	 * 得到所有的端口
	 * @return
	 */
	private List<Serializable> getChangedDataLists(){
		List<Serializable> list = new ArrayList<Serializable>();
		int count = componentList.size();
		
		for(int k = 0 ; k < count; k++){
			List<JComponent> rowList = componentList.get(k);
			int port = Integer.parseInt(((JLabel)rowList.get(0)).getText());
			QOSStormControl qosStormControl = viewModel.getQOSStormControl(port);
			
			boolean newUnicast = dlfcastCheckbox.isSelected();
			boolean newBroadcast = bcastCheckbox.isSelected();
			boolean newMutilcast = mcastCheckbox.isSelected();
			int value = Integer.parseInt(((NumberField)rowList.get(1)).getText().trim());
			String unit = String.valueOf(((JComboBox)rowList.get(2)).getSelectedItem());
			if ("Mbps".equalsIgnoreCase(unit)){ //因为下发时单位默认下发的是Kbps，所有单位为Mbps时需要转换成Kbps的值
				value = value*1024;
			}
			
			qosStormControl.setUnknowUnicast(newUnicast);
			qosStormControl.setBroadcast(newBroadcast);
			qosStormControl.setMutilUnicast(newMutilcast);
			qosStormControl.setPercentNum(value);
			qosStormControl.setUnit(unit);
			qosStormControl.setSwitchNode(switchNodeEntity);
			qosStormControl.setIssuedTag(Constants.ISSUEDADM);
			list.add(qosStormControl);
		}
		return list;
	}
	
	/**
	 * 得到所改变的端口
	 * @return
	 */
	private List<Serializable> getChangedDataList(){
		List<Serializable> list = new ArrayList<Serializable>();
		int count = componentList.size();
		
		for(int i =0 ; i< count; i++){
			int portNo = i+1;
			QOSStormControl qosStormControl = viewModel.getQOSStormControl(portNo);
			boolean oldUnicast = qosStormControl.isUnknowUnicast();
			boolean oldBroadcast = qosStormControl.isBroadcast();
			boolean oldMutilcast = qosStormControl.isMutilUnicast();
			int oldPercent = qosStormControl.getPercentNum();
			
			for(int k = 0 ; k < count; k++){
				List<JComponent> rowList = componentList.get(k);
				int port = Integer.parseInt(((JLabel)rowList.get(0)).getText());
				boolean newUnicast = ((JCheckBox)rowList.get(1)).isSelected();
				boolean newBroadcast = ((JCheckBox)rowList.get(2)).isSelected();
				boolean newMutilcast = ((JCheckBox)rowList.get(3)).isSelected();
				int newPercent = reverseStringToInt(((JComboBox)rowList.get(4)).getSelectedItem().toString());
				
				if (portNo == port){
					if (oldUnicast != newUnicast 
						||oldBroadcast !=newBroadcast 
						||oldMutilcast != newMutilcast
						||oldPercent != newPercent){
						qosStormControl.setUnknowUnicast(newUnicast);
						qosStormControl.setBroadcast(newBroadcast);
						qosStormControl.setMutilUnicast(newMutilcast);
						qosStormControl.setPercentNum(newPercent);
						qosStormControl.setSwitchNode(switchNodeEntity);
						list.add(qosStormControl);
						break;
					}
				}
			}
		}
		return list;
	}

	private int reverseStringToInt(String str){
		int percent = 0;
		if ("3%".equals(str)){
			percent = 3;
		}
		else if ("5%".equals(str)){
			percent = 5;
		}
		else if ("10%".equals(str)){
			percent = 10;
		}
		else if ("20%".equals(str)){
			percent = 20;
		}
		return percent;
	}
	
	private String reverseIntToString(int percent){
		String value = "";
		switch(percent){
			case 3:
				value = "3%";
				break;
			case 5:
				value = "5%";
				break;
			case 10:
				value = "10%";
				break;
			case 20:
				value = "20%";
				break;
		}
		
		return value;
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
		}		
	};
	
	private void clear() {		
		if (SwingUtilities.isEventDispatchThread()) {
			switchNodeEntity = null;
			statusField.setText("");
			componentList.clear();
			centerPnl.removeAll();
			centerPnl.revalidate();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					clear();
				}
			});
		}
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
//	public JButton getSaveButton(){
//		return this.saveBtn;
//	}
	
	private boolean isValids(){
		boolean isValid = true;
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		for (int k = 0 ; k < portCount; k++){
			JLabel portLbl = (JLabel)componentList.get(k).get(0);
			String portStr = portLbl.getText();
			if (portStr == null || "".equals(portStr)){
				JOptionPane.showMessageDialog(this, "请输入端口号","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			
			NumberField speedFld = (NumberField)componentList.get(k).get(1);
			String speedStr = speedFld.getText();
			if (speedStr == null || "".equals(speedStr)){
				JOptionPane.showMessageDialog(this, "抑制速率设置错误，正确值是1-100Mbps","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			
			JComboBox unitCombox = (JComboBox)componentList.get(k).get(2);
			Object unitStr = unitCombox.getSelectedItem();
			if (unitStr == null || "".equals(unitStr.toString())){
				JOptionPane.showMessageDialog(this, "请输入单位","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			
			
			int value = Integer.parseInt(((NumberField)componentList.get(k).get(1)).getText().trim());
			String unit = String.valueOf(((JComboBox)componentList.get(k).get(2)).getSelectedItem());
			if ("Mbps".equalsIgnoreCase(unit)){
				if (value > 100 || value < 1){
					JOptionPane.showMessageDialog(this, "抑制速率设置错误，正确值是1-100Mbps","提示",JOptionPane.NO_OPTION);
					isValid = false;
					return isValid;
				}
			}
		}
		
		return isValid;
	}
	
	/**
	 * 对于接收到的异步消息进行处理
	 */
	public void receive(Object object){
	}
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
}
