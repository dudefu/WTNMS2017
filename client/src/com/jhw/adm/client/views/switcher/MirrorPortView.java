package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
import com.jhw.adm.client.model.switcher.MirrorPortViewModel;
import com.jhw.adm.client.swing.MacAddressField;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.swing.UploadMessageProcessorStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.MirrorEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(MirrorPortView.ID)
@Scope(Scopes.DESKTOP)
public class MirrorPortView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;

	public static final String ID = "mirrorPortView";
	
	//端口镜像面板
	private final JPanel portMirrorPnl = new JPanel();
	private final JLabel isStartLbl = new JLabel();
	private final JCheckBox isStartChkBox = new JCheckBox();
	
	private final JLabel mirrorPortLbl = new JLabel();
	private final JComboBox mirrorPortCombox = new JComboBox();
	private final JLabel tipLbl = new JLabel();
	
	private final JLabel enterRateLbl = new JLabel();
	private final NumberField enterRateFld = new NumberField(4,0,1,1023,true);
	private final JLabel enterRateRangeLbl = new JLabel();
	
	private final JLabel exitRateLbl = new JLabel();
	private final NumberField exitRateFld = new NumberField(4,0,1,1023,true);
	private final JLabel exitRateRangeLbl = new JLabel();
	
	private final JPanel mirroredPnl = new JPanel();
	private final JLabel enterLbl = new JLabel();
	private final JPanel enterPnl = new JPanel();
	
	private final JLabel exitLbl = new JLabel();
	private final JPanel exitPnl = new JPanel();
	
	
	//监控模式面板
	private final JPanel mainPanel = new JPanel();
	private final JPanel monitorModePnl = new JPanel();
	private final JLabel exitDirectionLbl = new JLabel();
	private final JLabel exitMonitorMacLbl = new JLabel();
	private final MacAddressField exitMonitorMacFld = new MacAddressField(MacAddressField.EMRULE);
	private final JLabel exitMonitorModeLbl = new JLabel();
	private final JComboBox exitMonitorModeCombox = new JComboBox();
	
	private final JLabel enterDirectionLbl = new JLabel();
	private final JLabel enterMonitorMacLbl = new JLabel();
	private final MacAddressField enterMonitorMacFld = new MacAddressField(MacAddressField.EMRULE);
	private final JLabel enterMonitorModeLbl = new JLabel();
	private final JComboBox enterMonitorModeCombox = new JComboBox();
	
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	private static final String[] MONITORMODELIST = {"ALL","目的MAC","源MAC"};
	
	//下端的按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn ;
	
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
	@Qualifier(MirrorPortViewModel.ID)
	private MirrorPortViewModel model;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private final UploadMessageProcessorStrategy uploadMessageProcessorStrategy = new UploadMessageProcessorStrategy();
	
	private static final Logger LOG = LoggerFactory.getLogger(MirrorPortView.class);
	
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
		
		initToolBar();
		initPortMirrorPnl();
		initMonitorModePnl();
	
		this.setLayout(new BorderLayout());
		this.add(portMirrorPnl,BorderLayout.NORTH);
		this.add(mainPanel,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		this.setViewSize(640, 490);
		
		setResource();
	}
	
	private void initToolBar(){
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		statusPnl.add(statusFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		leftPnl.add(statusPnl);
		
		JPanel newPanel = new JPanel(new GridBagLayout());
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		uploadBtn = buttonFactory.createButton(UPLOAD);
		closeBtn = buttonFactory.createCloseButton();
		newPanel.add(uploadBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		rightPnl.add(newPanel);
		
		bottomPnl.setLayout(new BorderLayout());
		bottomPnl.add(leftPnl, BorderLayout.WEST);
		bottomPnl.add(rightPnl, BorderLayout.EAST);
	}
	
	private void initPortMirrorPnl(){
		JPanel mirrorConfigPnl = new JPanel(new GridBagLayout());
		
		mirrorConfigPnl.add(isStartLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		mirrorConfigPnl.add(isStartChkBox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,55,5,0),0,0));
		
		mirrorConfigPnl.add(mirrorPortLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		mirrorConfigPnl.add(mirrorPortCombox,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,55,5,0),0,0));
		mirrorConfigPnl.add(tipLbl,new GridBagConstraints(2,1,6,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));

		mirrorConfigPnl.add(enterRateLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		mirrorConfigPnl.add(enterRateFld,new GridBagConstraints(1,2,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,55,5,0),0,0));
		mirrorConfigPnl.add(enterRateRangeLbl,new GridBagConstraints(5,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		mirrorConfigPnl.add(new StarLabel(),new GridBagConstraints(6,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		mirrorConfigPnl.add(exitRateLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		mirrorConfigPnl.add(exitRateFld,new GridBagConstraints(1,3,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,55,0,0),0,0));
		mirrorConfigPnl.add(exitRateRangeLbl,new GridBagConstraints(5,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		mirrorConfigPnl.add(new StarLabel(),new GridBagConstraints(6,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(mirrorConfigPnl);
		
		JScrollPane enterScrollPnl = new JScrollPane();
		enterScrollPnl.setPreferredSize(new Dimension(230,120));
		enterScrollPnl.setMinimumSize(new Dimension(230,120));
		enterScrollPnl.getViewport().add(enterPnl);	
		
		JScrollPane exitScrollPnl = new JScrollPane();
		exitScrollPnl.setPreferredSize(new Dimension(230,120));
		exitScrollPnl.setMinimumSize(new Dimension(230,120));
		exitScrollPnl.getViewport().add(exitPnl);
		
		mirroredPnl.setLayout(new GridBagLayout());
		mirroredPnl.add(enterLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		mirroredPnl.add(enterScrollPnl,new GridBagConstraints(1,0,1,5,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		mirroredPnl.add(exitLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		mirroredPnl.add(exitScrollPnl,new GridBagConstraints(3,0,1,5,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		
		portMirrorPnl.setLayout(new BorderLayout());
		portMirrorPnl.add(topPnl,BorderLayout.NORTH);
		portMirrorPnl.add(mirroredPnl,BorderLayout.CENTER);
	}
	
	private void initMonitorModePnl(){
		JPanel optionPnl = new JPanel(new GridBagLayout());
		optionPnl.add(exitDirectionLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		optionPnl.add(exitMonitorMacLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,5),0,0));
		optionPnl.add(exitMonitorMacFld,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,20),0,0));
		optionPnl.add(exitMonitorModeLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,5),0,0));
		optionPnl.add(exitMonitorModeCombox,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		
		optionPnl.add(enterDirectionLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		optionPnl.add(enterMonitorMacLbl,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,5),0,0));
		optionPnl.add(enterMonitorMacFld,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,20),0,0));
		optionPnl.add(enterMonitorModeLbl,new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,5),0,0));
		optionPnl.add(enterMonitorModeCombox,new GridBagConstraints(4,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		monitorModePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		monitorModePnl.add(optionPnl);
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(monitorModePnl,BorderLayout.CENTER);
//		mainPanel.add(statusPnl,BorderLayout.SOUTH);
	}
	
	private void setResource(){
		this.setTitle("Mirror");
		portMirrorPnl.setBorder(BorderFactory.createTitledBorder("端口镜像"));
		monitorModePnl.setBorder(BorderFactory.createTitledBorder("监控模式"));
		mirroredPnl.setBorder(BorderFactory.createTitledBorder("被镜像端口"));
		
		enterRateFld.setColumns(20);
		exitRateFld.setColumns(20);
		exitMonitorMacFld.setColumns(20);
		enterMonitorMacFld.setColumns(20);
		exitMonitorMacFld.setDocument(new TextFieldPlainDocument(exitMonitorMacFld));
		enterMonitorMacFld.setDocument(new TextFieldPlainDocument(enterMonitorMacFld));
		
		mirrorPortCombox.setPreferredSize(new Dimension(80,mirrorPortCombox.getPreferredSize().height));
		
		enterPnl.setBackground(Color.WHITE);
		exitPnl.setBackground(Color.WHITE);

		exitMonitorModeCombox.setPreferredSize(new Dimension(80,exitMonitorModeCombox.getPreferredSize().height));
		
		isStartLbl.setText("是否启用");
		mirrorPortLbl.setText("镜像端口");
		tipLbl.setText("(如果启用镜像，则镜像端口不能被其他端口使用)");
		enterRateLbl.setText("入口比率");
		enterRateRangeLbl.setText("(1-1023)");
		exitRateLbl.setText("出口比率");
		exitRateRangeLbl.setText("(1-1023)");
		enterLbl.setText("入口方向");
		exitLbl.setText("出口方向");
		exitDirectionLbl.setText("出方向");
		exitMonitorMacLbl.setText("监控MAC");
		exitMonitorModeLbl.setText("监控模式");
		enterDirectionLbl.setText("入方向");
		enterMonitorMacLbl.setText("监控MAC");
		enterMonitorModeLbl.setText("监控模式");
		
		statusLbl.setText("状态");
		statusFld.setColumns(15);
		statusFld.setBackground(Color.WHITE);
		statusFld.setEditable(false);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	/**
	 * 从服务端查询数据
	 */
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		//初始化端口panel
		int portCount = switchNodeEntity.getPorts().size();
		model.setPortCount(portCount); //得到端口总数
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<MirrorEntity> mirrorEntityList = (List<MirrorEntity>)remoteServer.getService().findAll(MirrorEntity.class, where, parms);
		if (null == mirrorEntityList || mirrorEntityList.size() <1){
			return;
		}
		
		MirrorEntity mirrorEntity = mirrorEntityList.get(0);
		model.setMirrorEntity(mirrorEntity);
		//设置视图上的值
		setValue();
	}
	
	/**
	 * 把从服务端读取的数据显示到页面上
	 */
	private void setValue(){
		//初始化镜像端口列表中的端口数
		mirrorPortCombox.removeAllItems();
		for (int i = 0 ; i < model.getPortStrList().size() ; i++){
			mirrorPortCombox.addItem(model.getPortStrList().get(i));
		}
		
		exitMonitorModeCombox.removeAllItems();
		enterMonitorModeCombox.removeAllItems();
		for(int i = 0 ; i < MONITORMODELIST.length; i++){
			exitMonitorModeCombox.addItem(MONITORMODELIST[i]);
			enterMonitorModeCombox.addItem(MONITORMODELIST[i]);
		}
		
		model.clearCheckBoxList();
		
		model.getInChkBoxList();
		model.getOutChkBoxList();
		initEnterPortPnlPnl();  //初始化入口方向面板
		initExitPortPnlPnl();   //初始化出口方向面板
	
		//************设置控件的值**************
		isStartChkBox.setSelected(model.isApplied());
		mirrorPortCombox.setSelectedItem(model.getPortMirror());
		enterRateFld.setText(model.getInBit());
		exitRateFld.setText(model.getOutBit());
		
		//设置出口方向的端口
		if (null != model.getOutPorts()){
			String[] outPortStr = model.getOutPorts().split(",");
			if (null != outPortStr && !("".equals(outPortStr))){
				for (int i = 0 ; i < outPortStr.length; i++){
					int outPort = Integer.parseInt(outPortStr[i]);
					model.getOutChkBoxList().get(outPort-1).setSelected(true);
				}
			}
		}

		//设置入口方向的端口
		if (null != model.getInPorts()){
			String[] inPortStr = model.getInPorts().split(",");
			if (null != inPortStr && !("".equals(inPortStr))){
				for (int i = 0 ; i < inPortStr.length; i++){
					int inPort = Integer.parseInt(inPortStr[i]);
					model.getInChkBoxList().get(inPort-1).setSelected(true);
				}
			}
		}
		
		String outScanMac = model.getOutScanMac();	
		exitMonitorMacFld.setText(outScanMac);
		enterMonitorMacFld.setText(model.getInScanMac());
		
		exitMonitorModeCombox.setSelectedItem(reserveMacMode(model.getOutScanMode()));
		enterMonitorModeCombox.setSelectedItem(reserveMacMode(model.getInScanMode()));
		
		statusFld.setText(dataStatus.get(model.getStatus()).getKey());
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				enterPnl.revalidate();
				exitPnl.revalidate();
			}
		});
	}
	
	/**
	 * 初始化入口方向面板
	 */
	private void initEnterPortPnlPnl(){
		int portCount = model.getPortCount();
		
		int row = 0;
		int overage = portCount%3;
		if (overage > 0){
			row = portCount/3 +1;
		}
		else{
			row = portCount/3;
		}
		enterPnl.setLayout(new GridLayout(row,3));
		enterPnl.removeAll();
		for (int i = 0 ; i < portCount; i++){
			JCheckBox checkBox = new JCheckBox("端口"+(i+1));
			enterPnl.add(checkBox);
			checkBox.setBackground(Color.WHITE);
			
			//把所有的JCheckBox保存到列表checkBoxList中
			model.setInChkBoxList(i,checkBox);
		}
	}
	
	/**
	 * 初始化出口方向面板
	 */
	private void initExitPortPnlPnl(){
		int portCount = model.getPortCount();
		
		int row = 0;
		int overage = portCount%3;
		if (overage > 0){
			row = portCount/3 +1;
		}
		else{
			row = portCount/3;
		}
		exitPnl.setLayout(new GridLayout(row,3));
		exitPnl.removeAll();
		for (int i = 0 ; i < portCount; i++){
			JCheckBox checkBox = new JCheckBox("端口"+(i+1));
			exitPnl.add(checkBox);
			checkBox.setBackground(Color.WHITE);
			
			//把所有的JCheckBox保存到列表checkBoxList中
			model.setOutChkBoxList(i, checkBox);
		}
	}
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载Mirror端口信息",role=Constants.MANAGERCODE)
	public void download(){
		if (!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return ;
		}
		
		MirrorEntity mirrorEntity = getMirrorEntity();
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		List<Serializable> list = new ArrayList<Serializable>();
		list.add(mirrorEntity);
		showMessageDownloadDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.MIRRORUPDATE, list, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("下载出现异常");
			LOG.error("MirrorPortView.save() error:{}",e);
			queryData();
		}
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			queryData();
		}
	}
	
	private void showMessageDownloadDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("下载",this);
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载Mirror端口信息",role=Constants.MANAGERCODE)
	public void upload(){
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
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHMIRROR);
				return message;
			}
		});
	}
	
	/**
	 * 判断输入的值是否合法
	 */
	public boolean isValids(){
		//默认输入的值时合法的
		boolean isValid = true;
		
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if (mirrorPortCombox.getSelectedItem() == null){
			JOptionPane.showMessageDialog(this, "请输入镜像端口","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String enterRate = enterRateFld.getText().trim();
		if (null == enterRate || "".equals(enterRate)){
			JOptionPane.showMessageDialog(this, "入口比率错误，范围：1-1023","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		String exitRate = exitRateFld.getText().trim();
		if (null == exitRate || "".equals(exitRate)){
			JOptionPane.showMessageDialog(this, "出口比率错误，范围：1-1023","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		String message = "";
		int mirrorPort = model.reverseString(mirrorPortCombox.getSelectedItem().toString());
		List<JCheckBox> inChkBoxList = model.getInChkBoxList();
		for(int i = 0 ; i < inChkBoxList.size(); i++){
			if (inChkBoxList.get(i).isSelected()){
				if (mirrorPort == (i+1)){
					isValid  = false;
					message = "端口"+ mirrorPort +"为镜像端口,不能设置为被镜像端口。";
					break;
				}
			}
		}
		
		List<JCheckBox> outChkBoxList = model.getOutChkBoxList();
		for(int k = 0 ; k < outChkBoxList.size(); k++){
			if (outChkBoxList.get(k).isSelected()){
				if (mirrorPort == (k+1)){
					isValid  = false;
					message = "端口"+ mirrorPort +"为镜像端口,不能设置为被镜像端口。";
					break;
				}
			}
		}
		
		String enterMacAddr = enterMonitorMacFld.getText().trim();
		if(null == enterMacAddr || "".equals(enterMacAddr))
		{
			JOptionPane.showMessageDialog(this, "MAC地址不能为空，请输入MAC地址","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String enterRegex = "^([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})$";
		Pattern enterPattern=Pattern.compile(enterRegex);
		Matcher enterMatcher =enterPattern.matcher(enterMacAddr);
		if (!enterMatcher.matches()){
			JOptionPane.showMessageDialog(this, "MAC地址错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String exitMacAddr = exitMonitorMacFld.getText().trim();
		if(null == exitMacAddr || "".equals(exitMacAddr))
		{
			JOptionPane.showMessageDialog(this, "MAC地址不能为空，请输入MAC地址","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
//		String regex = "^([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})$";
//		Pattern p=Pattern.compile(regex);
		Matcher m=enterPattern.matcher(exitMacAddr);
		if (!m.matches()){
			JOptionPane.showMessageDialog(this, "MAC地址错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if (!isValid){
			JOptionPane.showMessageDialog(this, message,"提示",JOptionPane.NO_OPTION);
			return isValid;
		}
		
		return isValid;
	}
	
	public MirrorEntity getMirrorEntity(){
		model.setApplied(isStartChkBox.isSelected());
		model.setPortMirror(mirrorPortCombox.getSelectedItem().toString());
		model.setInBit(enterRateFld.getText().toString());
		model.setOutBit(exitRateFld.getText().toString());
		
		String inPorts = "";
		List<JCheckBox> inChkBoxList = model.getInChkBoxList();
		for(int i = 0 ; i < inChkBoxList.size(); i++){
			if (inChkBoxList.get(i).isSelected()){
				inPorts = inPorts + (i+1) + ",";
			}
		}
		if (!("".equals(inPorts))){
			inPorts = inPorts.substring(0,inPorts.length()-1);
		}
		model.setInPorts(inPorts);
		
		
		String outPorts = "";
		List<JCheckBox> outChkBoxList = model.getOutChkBoxList();
		for(int k = 0 ; k < outChkBoxList.size(); k++){
			if (outChkBoxList.get(k).isSelected()){
				outPorts = outPorts + (k+1) + ",";
			}
		}
		if (!("".equals(outPorts))){
			outPorts = outPorts.substring(0,outPorts.length()-1);
		}
		model.setOutPorts(outPorts);


		model.setOutScanMac(exitMonitorMacFld.getText());
		model.setInScanMac(enterMonitorMacFld.getText());
		model.setOutScanMode(exitMonitorModeCombox.getSelectedItem().toString());
		model.setInScanMode(enterMonitorModeCombox.getSelectedItem().toString());
		
		model.setStatus(Constants.ISSUEDADM);
		
		return model.getMirrorEntity();
	}
	
	private String reserveMacMode(String str){
		String object = "";
		if (str.equals("m_sa")){
			object =  "源MAC";
		}
		else if(str.equals("m_da")){
			object = "目的MAC";
		}
		else if(str.equals("all")){
			object = "ALL";
		}
		
		return object;
	}
	
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
		}		
	};
	
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
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
	}
	
	private void clear(){
		mirrorPortCombox.removeAllItems();
		enterRateFld.setText("");
		exitRateFld.setText("");
		enterPnl.removeAll();
		exitPnl.removeAll();
		exitMonitorMacFld.setText("");
		exitMonitorModeCombox.removeAllItems();
		enterMonitorMacFld.setText("");
		enterMonitorModeCombox.removeAllItems();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				enterPnl.updateUI();
				exitPnl.updateUI();
			}
		});
	}
}
