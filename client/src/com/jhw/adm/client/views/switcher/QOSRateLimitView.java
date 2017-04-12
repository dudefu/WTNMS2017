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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import org.apache.commons.lang.math.NumberUtils;
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
import com.jhw.adm.client.model.switcher.QOSRateLimitViewModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.UploadMessageProcessorStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.QOSSpeedConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

@Component(QOSRateLimitView.ID)
@Scope(Scopes.DESKTOP)
public class QOSRateLimitView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;
	public static final String ID = "qosRateLimitView";
	//端口限速面板
	private final JPanel ratePnl = new JPanel();
	private final JPanel centerPnl = new JPanel();
	private final JLabel portLbl = new JLabel();
	private final JLabel enterLimitLbl = new JLabel();
	private final JLabel enterRateLbl = new JLabel();
	private final JLabel exitLimitLbl = new JLabel();
	private final JLabel exitRateLbl = new JLabel();
	private final JTextField statusField = new JTextField(); 
	
	private final List<List> componentList = new ArrayList<List>();
	
	//下端的按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn;
	
	private static final String[] UNIT = {"Kbps","Mbps"};

	private SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	private MessageSender messageSender;
	
	//端口总数
	private int portCount = 0 ;
	
	private final static int IN = 0; //进限速
	private final static int OUT = 1; //出限速
	private final static String MBPS = "mbps";//
	private final static String KBPS = "kbps";
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=QOSRateLimitViewModel.ID)
	private QOSRateLimitViewModel viewModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;

	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private final UploadMessageProcessorStrategy uploadMessageProcessorStrategy = new UploadMessageProcessorStrategy();
	
	private static final Logger LOG = LoggerFactory.getLogger(QOSRateLimitView.class);
	
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
			
			if (messageType == MessageNoConstants.QOS_LIMITPORT){
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
						message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHQOSPORT);
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
		
		initRatePnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(ratePnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
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
	
	private void initRatePnl(){
		JPanel lblPnl = new JPanel(new GridBagLayout());
		lblPnl.add(portLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,3,0,0),0,0));
		lblPnl.add(enterLimitLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,26,0,0),0,0));
		lblPnl.add(enterRateLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,70,0,0),0,0));
		lblPnl.add(exitLimitLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,98,0,0),0,0));
		lblPnl.add(exitRateLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,70,0,0),0,0));
		
		
		
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(lblPnl);
		
		centerPnl.setLayout(new GridBagLayout());
		JPanel middlePnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		middlePnl.add(centerPnl);
		
		
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(middlePnl);
		scrollPnl.setPreferredSize(new Dimension(600,150) );
		

		ratePnl.setLayout(new BorderLayout());
		ratePnl.add(topPnl,BorderLayout.NORTH);
		ratePnl.add(scrollPnl,BorderLayout.CENTER);
	}
	
	private void setResource(){
		portLbl.setText("端口");
		enterLimitLbl.setText("进限速");
		enterRateLbl.setText("速率");
		exitLimitLbl.setText("出限速");
		exitRateLbl.setText("速率");
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		//端口总数
		portCount = switchNodeEntity.getPorts().size();
		setCenterLayout();
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<QOSSpeedConfig> qosSpeedConfigList = (List<QOSSpeedConfig>)remoteServer.getService().findAll(QOSSpeedConfig.class, where, parms);
		if (null == qosSpeedConfigList || qosSpeedConfigList.size() < 1){
			return;
		}
		
		//设置控件的值
		setValue(qosSpeedConfigList);
	}
	
	/**
	 * 根据端口数对面板进行布局
	 */
	private void setCenterLayout(){
		componentList.clear();
		centerPnl.removeAll();
		
		for(int i = 0 ; i < portCount ; i++){
			JLabel portLbl = new JLabel(i+1+"");
			final JCheckBox enterLimitChk = new JCheckBox();
			final NumberField enterRateFld = new NumberField(6,0,1,102400,true);
			enterRateFld.setColumns(10);
			final JComboBox enterUnit = new JComboBox();
			
			final JCheckBox exitLimitChk = new JCheckBox();
			final NumberField exitRateFld = new NumberField(6,0,1,102400,true);
			exitRateFld.setColumns(10);
			final JComboBox exitUnit = new JComboBox();
						
			for(int j = 0 ; j <UNIT.length ; j++ ){
				enterUnit.addItem(UNIT[j]);
				exitUnit.addItem(UNIT[j]);
			}
			
			centerPnl.add(portLbl,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,10,0),0,0));
			centerPnl.add(enterLimitChk,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,45,10,0),0,0));
			centerPnl.add(enterRateFld,new GridBagConstraints(2,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,45,10,0),0,0));
			centerPnl.add(enterUnit,new GridBagConstraints(3,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
			centerPnl.add(exitLimitChk,new GridBagConstraints(4,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,45,10,0),0,0));
			centerPnl.add(exitRateFld,new GridBagConstraints(5,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,45,10,0),0,0));
			centerPnl.add(exitUnit,new GridBagConstraints(6,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
			
			
			List<JComponent> rowList = new ArrayList<JComponent>();
			rowList.add(0,portLbl);          //端口
			rowList.add(1,enterLimitChk);    //进限速
			rowList.add(2,enterRateFld);     //速率
			rowList.add(3,enterUnit);        //
			rowList.add(4,exitLimitChk);     //出限速
			rowList.add(5,exitRateFld);      //速率
			rowList.add(6,exitUnit);         //
			
			componentList.add(rowList);
			
			enterRateFld.setEnabled(false);
			enterUnit.setEnabled(false);
			exitRateFld.setEnabled(false);
			exitUnit.setEnabled(false);

			enterLimitChk.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(enterLimitChk.isSelected()){
						enterRateFld.setEnabled(true);
						enterUnit.setEnabled(true);
					}
					else{
						enterRateFld.setEnabled(false);
						enterUnit.setEnabled(false);
					}
				}
			});

			exitLimitChk.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(exitLimitChk.isSelected()){
						exitRateFld.setEnabled(true);
						exitUnit.setEnabled(true);
					}
					else{
						exitRateFld.setEnabled(false);
						exitUnit.setEnabled(false);
					}
				}
			});
		}
		
		centerPnl.revalidate();
	}
	
	private void setValue(List<QOSSpeedConfig> qosSpeedConfigList){
		viewModel.setDataList(qosSpeedConfigList);
		
		if (qosSpeedConfigList.size() > 0) {
			QOSSpeedConfig qosSpeedConfig = qosSpeedConfigList.get(0);
			statusField.setText(dataStatus.get(qosSpeedConfig.getIssuedTag()).getKey());
        }
		
		List<List> dataList = new ArrayList<List>();
		for (int row = 0 ; row < portCount; row++){
			int port = row+1;
			QOSSpeedConfig qosSpeedConfig = viewModel.getValueAt(port);
			
			boolean inApplied = qosSpeedConfig.getInSpeed().isApplied();
			int inSpeed = (int)qosSpeedConfig.getInSpeed().getSpeed();
			String inUnit = qosSpeedConfig.getInSpeed().getUnit();
			boolean outApplied = qosSpeedConfig.getOutSpeed().isApplied();
			int outSpeed = (int)qosSpeedConfig.getOutSpeed().getSpeed();
			String outUnit = qosSpeedConfig.getOutSpeed().getUnit();
			
			
			List rowList = componentList.get(row);
			
			((JLabel)rowList.get(0)).setText(port+""); //端口
			((JCheckBox)rowList.get(1)).setSelected(inApplied);
			((NumberField)rowList.get(2)).setText(String.valueOf(inSpeed));
			((JComboBox)rowList.get(3)).setSelectedItem(reserveUnitLowerToUpper(inUnit));
			((JCheckBox)rowList.get(4)).setSelected(outApplied);
			((NumberField)rowList.get(5)).setText(String.valueOf(outSpeed));
			((JComboBox)rowList.get(6)).setSelectedItem(reserveUnitLowerToUpper(outUnit));
			
			if (inApplied){
				((NumberField)rowList.get(2)).setEnabled(true);
				((JComboBox)rowList.get(3)).setEnabled(true);
			}
			else{
				((NumberField)rowList.get(2)).setEnabled(false);
				((JComboBox)rowList.get(3)).setEnabled(false);
			}
			
			if (outApplied){
				((NumberField)rowList.get(5)).setEnabled(true);
				((JComboBox)rowList.get(6)).setEnabled(true);
			}
			else{
				((NumberField)rowList.get(5)).setEnabled(false);
				((JComboBox)rowList.get(6)).setEnabled(false);
			}
		}
	}
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载QOS端口限速信息",role=Constants.MANAGERCODE)
	public void download(){
		if (!isValids()){
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return ;
		}
		
		List<Serializable> list = getChangedDataLists();

		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		showMessageDownloadDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.QOS_LIMITPORT, list, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),switchNodeEntity.getDeviceModel(),result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("下载出现异常");
			LOG.error("QOSRateLimitView.save() error: {}", e);
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
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载QoS端口限速信息",role=Constants.MANAGERCODE)
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
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHQOSPORT);
				return message;
			}
		});
	}
	
	/**
	 * 当数据下发到设备侧和网管侧时
	 * 对于接收到的异步消息进行处理
	 */
	private void showMessageDownloadDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("下载",this);
	}
	
	private List<Serializable> getChangedDataLists(){
		List<Serializable> list = new ArrayList<Serializable>();
		for (int k = 0 ; k < portCount; k++){
			JLabel portLbl = (JLabel)componentList.get(k).get(0);
			int port = Integer.parseInt(portLbl.getText());
				
			QOSSpeedConfig qosSpeedConfig =  viewModel.getValueAt(port);
				
			JCheckBox inAppliedChkBox = (JCheckBox)componentList.get(k).get(1);
			boolean newInApplied = inAppliedChkBox.isSelected();
				
			JComboBox inUnitCombox = (JComboBox)componentList.get(k).get(3);
			String newInUnit = inUnitCombox.getSelectedItem().toString();
				
			NumberField inSpeedFld = (NumberField)componentList.get(k).get(2);
			float newInSpeed = 0f;
			if (!(null == inSpeedFld.getText().trim() || "".equals(inSpeedFld.getText().trim()))){
				newInSpeed = Float.valueOf(inSpeedFld.getText().trim());
//					if (newInUnit.equalsIgnoreCase("Kbps")){
//						newInSpeed = setRateKbps(Float.valueOf(inSpeedFld.getText().trim()));
//					}else{
//						newInSpeed = Float.valueOf(inSpeedFld.getText().trim());
//					}
			}

			JCheckBox outAppliedChkBox = (JCheckBox)componentList.get(k).get(4);
			boolean newOutApplied = outAppliedChkBox.isSelected();
				
			JComboBox outUnitCombox = (JComboBox)componentList.get(k).get(6);
			String newOutUnit = outUnitCombox.getSelectedItem().toString();
				
			NumberField outSpeedFld = (NumberField)componentList.get(k).get(5);
			float newOutSpeed = 0f;
			if (!(null == outSpeedFld.getText().trim() || "".equals(outSpeedFld.getText().trim()))){
				newOutSpeed = Float.valueOf(outSpeedFld.getText().trim());
//					if (newOutUnit.equalsIgnoreCase("Kbps")){
//						newOutSpeed = setRateKbps(Float.valueOf(outSpeedFld.getText().trim()));
//					}else{
//						newOutSpeed = Float.valueOf(outSpeedFld.getText().trim());
//					}
			}
				
			qosSpeedConfig.getInSpeed().setApplied(newInApplied);
			qosSpeedConfig.getInSpeed().setSpeed(newInSpeed);
			qosSpeedConfig.getInSpeed().setUnit(newInUnit);
						
			qosSpeedConfig.getOutSpeed().setApplied(newOutApplied);
			qosSpeedConfig.getOutSpeed().setSpeed(newOutSpeed);
			qosSpeedConfig.getOutSpeed().setUnit(newOutUnit);
			qosSpeedConfig.setSwitchNode(switchNodeEntity);
			qosSpeedConfig.setIssuedTag(Constants.ISSUEDADM);
			list.add(qosSpeedConfig);	
		}
		return list;
	}
	
	private List<Serializable> getChangedDataList(){
		List<Serializable> list = new ArrayList<Serializable>();
		
		for (int i = 0 ; i < portCount; i++){
			int portNo = i+1;
			QOSSpeedConfig qosSpeedConfig =  viewModel.getValueAt(portNo);
			
			boolean oldInApplied = qosSpeedConfig.getInSpeed().isApplied();
			float oldInSpeed = qosSpeedConfig.getInSpeed().getSpeed();
			String oldInUnit = qosSpeedConfig.getInSpeed().getUnit();
			
			boolean oldOutApplied = qosSpeedConfig.getOutSpeed().isApplied();
			float oldOutSpeed = qosSpeedConfig.getOutSpeed().getSpeed();
			String oldOutUnit = qosSpeedConfig.getOutSpeed().getUnit();
			
			for (int k = 0 ; k < portCount; k++){
				JLabel portLbl = (JLabel)componentList.get(k).get(0);
				int port = Integer.parseInt(portLbl.getText());
				
				JCheckBox inAppliedChkBox = (JCheckBox)componentList.get(k).get(1);
				boolean newInApplied = inAppliedChkBox.isSelected();
				
				JComboBox inUnitCombox = (JComboBox)componentList.get(k).get(3);
				String newInUnit = inUnitCombox.getSelectedItem().toString();
				
				NumberField inSpeedFld = (NumberField)componentList.get(k).get(2);
				float newInSpeed = 0f;
				if (!(null == inSpeedFld.getText().trim() || "".equals(inSpeedFld.getText().trim()))){
					newInSpeed = Float.valueOf(inSpeedFld.getText().trim());
//					if (newInUnit.equalsIgnoreCase("Kbps")){
//						newInSpeed = setRateKbps(Float.valueOf(inSpeedFld.getText().trim()));
//					}else{
//						newInSpeed = Float.valueOf(inSpeedFld.getText().trim());
//					}
				}

				
				JCheckBox outAppliedChkBox = (JCheckBox)componentList.get(k).get(4);
				boolean newOutApplied = outAppliedChkBox.isSelected();
				
				JComboBox outUnitCombox = (JComboBox)componentList.get(k).get(6);
				String newOutUnit = outUnitCombox.getSelectedItem().toString();
				
				NumberField outSpeedFld = (NumberField)componentList.get(k).get(5);
				float newOutSpeed = 0f;
				if (!(null == outSpeedFld.getText().trim() || "".equals(outSpeedFld.getText().trim()))){
					newOutSpeed = Float.valueOf(outSpeedFld.getText().trim());
//					if (newOutUnit.equalsIgnoreCase("Kbps")){
//						newOutSpeed = setRateKbps(Float.valueOf(outSpeedFld.getText().trim()));
//					}else{
//						newOutSpeed = Float.valueOf(outSpeedFld.getText().trim());
//					}
				}
				
				if (portNo == port){
					if ((newInApplied != oldInApplied) 
							||(newInSpeed != oldInSpeed) 
							||(!oldInUnit.equalsIgnoreCase(newInUnit))
							||(newOutApplied != oldOutApplied) 
							||(newOutSpeed != oldOutSpeed) 
							||(!oldOutUnit.equalsIgnoreCase(newOutUnit))){
						qosSpeedConfig.getInSpeed().setApplied(newInApplied);
						qosSpeedConfig.getInSpeed().setSpeed(newInSpeed);
						qosSpeedConfig.getInSpeed().setUnit(newInUnit);
						
						qosSpeedConfig.getOutSpeed().setApplied(newOutApplied);
						qosSpeedConfig.getOutSpeed().setSpeed(newOutSpeed);
						qosSpeedConfig.getOutSpeed().setUnit(newOutUnit);
						qosSpeedConfig.setSwitchNode(switchNodeEntity);
						qosSpeedConfig.setIssuedTag(Constants.ISSUEDADM);
						list.add(qosSpeedConfig);
						break;
					}
				}
			}
		}
		
		return list;
	}

	
	private String reserveUnitLowerToUpper(String lower){
		String upper = "";
		if (MBPS.equalsIgnoreCase(lower)){
			upper = "Mbps";
		}
		else if (KBPS.equalsIgnoreCase(lower)){
			upper = "Kbps";
		}
		
		return upper;
	}
	
	private String reserveUnitUpperToLower(String upper){
		String lower = "";
		if ("Mbps".equals(upper)){
			lower = MBPS;
		}
		else if ("Kbps".equals(upper)){
			lower = KBPS;
		}
		
		return lower;
	}
	
	public float setRateKbps(float f){
		float value = 0f;
		int m = (int)f%64; //取模
		int n = (int)f/64; //取整除值
		if(m>0){
			value = 64 * (n+1);
		}
		else{
			value = 64 * n;
		}
		return value;
	}

	private void clear(){
		switchNodeEntity = null;
		componentList.clear();
		centerPnl.removeAll();
		centerPnl.revalidate();
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
		}		
	};
	
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
			
			NumberField inSpeedFld = (NumberField)componentList.get(k).get(2);
			int inSpeed = NumberUtils.toInt(inSpeedFld.getText().trim());
			JComboBox inCombox = (JComboBox)componentList.get(k).get(3);
			String inUnit = inCombox.getSelectedItem().toString();
			if (inUnit.equalsIgnoreCase("Kbps")){
				if(inSpeed < 1 || inSpeed > 102400){
					JOptionPane.showMessageDialog(this, "端口" + portStr + "进方向速率设置错误，正确值是 1-102400Kbps","提示",JOptionPane.NO_OPTION);
					isValid = false;
					return isValid;
				}
			}
			else if (inUnit.equalsIgnoreCase("Mbps")){
				if(inSpeed < 1 || inSpeed > 100){
					JOptionPane.showMessageDialog(this, "端口" + portStr + "进方向速率设置错误，正确值是 1-100Mbps","提示",JOptionPane.NO_OPTION);
					isValid = false;
					return isValid;
				}
			}
			
			NumberField outSpeedFld = (NumberField)componentList.get(k).get(5);
			int outSpeed = NumberUtils.toInt(outSpeedFld.getText().trim());
			JComboBox outCombox = (JComboBox)componentList.get(k).get(6);
			String outUnit = outCombox.getSelectedItem().toString();
			if (outUnit.equalsIgnoreCase("Kbps")){
				if (outSpeed < 1 || outSpeed > 102400){
					JOptionPane.showMessageDialog(this, "端口" + portStr + "出方向速率设置错误，正确值是 1-102400Kbps","提示",JOptionPane.NO_OPTION);
					isValid = false;
					return isValid;
				}
			}
			else if (outUnit.equalsIgnoreCase("Mbps")){
				if(outSpeed < 1 || outSpeed > 100){
					JOptionPane.showMessageDialog(this, "端口" + portStr + "出方向速率设置错误，正确值是 1-100Mbps","提示",JOptionPane.NO_OPTION);
					isValid = false;
					return isValid;
				}
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
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
}
