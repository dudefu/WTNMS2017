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
import com.jhw.adm.client.model.switcher.SerialPortViewModel;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.swing.UploadMessageProcessorStrategy;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.SwitchSerialPort;

@Component(SerialPortConfigView.ID)
@Scope(Scopes.DESKTOP)
public class SerialPortConfigView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "serialPortConfigView";
	
	public static final Logger LOG = LoggerFactory.getLogger(SerialPortConfigView.class);
	
	//串口设置面板
	private final JPanel serialConfigPnl = new JPanel();
	private final JLabel serialLbl = new JLabel();
	private final JComboBox serialCombox = new JComboBox();
	
	private final JLabel serialNameLbl = new JLabel();
	private final JTextField serialNameFld = new JTextField();
	private final JLabel serialRangeLbl = new JLabel();
	
	private final JLabel baudLbl = new JLabel();
	private final JComboBox baudCombox = new JComboBox();
	private final String[] baudList = {"50","75","110","150","300","600","1200","2000","2400","3600"
						,"4800","7200","9600","19200","38400","57600","115200"};
	
	private final JLabel fluidicsLbl = new JLabel();
	private final JCheckBox checkBox = new JCheckBox();
	
	private final JLabel dataBitLbl = new JLabel();
	private final JComboBox dataBitCombox = new JComboBox();
	private final String[] dataBitList = {"5","6","7","8"};
	
	private final JLabel parityLbl = new JLabel();
	private final JComboBox parityCombox = new JComboBox();
	private final String[] parityList = {"none","odd","even"};
	
	private final JLabel stopBitLbl = new JLabel();
	private final JComboBox stopBitCombox = new JComboBox();
	private final String[] stopBitList = {"1","2"};
	
	
	//串口模式面板
	private final JPanel serialModePnl = new JPanel();
	private final JLabel modeLbl = new JLabel();
	private final JComboBox modeCombox = new JComboBox();
	private final String[]  modeList = {"tcpserver","tcpclient","udpserver","udpclient"};
	
	//TCP Client设置面板
	private final JPanel tcpClientPnl = new JPanel();
	private final JLabel tcpClientRemotePortLbl = new JLabel();
	private final NumberField tcpClientRemotePortFld = new NumberField(5,0,0,65535,true);
	private final JLabel tcpClientRemoteRangeLbl = new JLabel();
	
	private final JLabel tcpClientRemoteIpLbl = new JLabel();
	private final IpAddressField tcpClientRemoteIpFld = new IpAddressField();
	
	//TCP Server设置面板
	private final JPanel   tcpServerPnl = new JPanel();
	private final JLabel tcpServerLocalPortLbl = new JLabel();
	private final NumberField tcpServerLocalPortFld = new NumberField(5,0,0,65535,true);
	private final JLabel tcpServerLocalRangeLbl = new JLabel();
	
	//UDP Client设置面板
	private final JPanel udpClientPnl = new JPanel();
	private final JLabel udpClientRemotePortLbl = new JLabel();
	private final NumberField udpClientRemotePortFld = new NumberField(5,0,0,65535,true);
	private final JLabel udpClientRemoteRangeLbl = new JLabel();
	
	private final JLabel udpClientRemoteIpLbl = new JLabel();
	private final IpAddressField udpClientRemoteIpFld = new IpAddressField();
	
	//UDP Server设置面板
	private final JPanel udpServerPnl = new JPanel();
	private final JLabel udpServerLocalPortLbl = new JLabel();
	private final NumberField udpServerLocalPortFld = new NumberField(5,0,0,65535,true);
	private final JLabel udpServerLocalRangeLbl = new JLabel();
	
	//status 面板
	private final JPanel statusPnl = new JPanel();
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	//下端的按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
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
	
	@Autowired
	@Qualifier(SerialPortViewModel.ID)
	private SerialPortViewModel viewModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private final UploadMessageProcessorStrategy uploadMessageProcessorStrategy = new UploadMessageProcessorStrategy();
	private MessageSender messageSender;
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
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
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message){
			
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
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
		initBottomPnl();
		initSerialConfigPnl();
		initSerialModePnl();
		initTcpClientPnl();
		initTcpServerPnl();
		initUdpClientPnl();
		initUdpServerPnl();
		initStatusPnl();
		
		JPanel panel = new JPanel();
		this.setLayout(new GridBagLayout());
		
		this.add(serialConfigPnl,new GridBagConstraints(0,1,1,1,1.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(-10,0,0,0),0,0));
		this.add(serialModePnl,new GridBagConstraints(0,2,1,1,1.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		this.add(tcpClientPnl,new GridBagConstraints(0,3,1,1,1.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		this.add(tcpServerPnl,new GridBagConstraints(0,4,1,1,1.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		this.add(udpClientPnl,new GridBagConstraints(0,5,1,1,1.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		this.add(udpServerPnl,new GridBagConstraints(0,6,1,1,1.0,1.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		this.add(statusPnl,new GridBagConstraints(0,7,1,1,1.0,1.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		this.add(bottomPnl,new GridBagConstraints(0,8,1,1,1.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		
		this.add(panel);
		this.setViewSize(640, 570);
		
		setResource();
	}
	
	//Amend 2010.06.17
	private void initBottomPnl(){
		bottomPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		closeBtn = buttonFactory.createCloseButton();
		newPanel.add(uploadBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		bottomPnl.add(newPanel,BorderLayout.EAST);
	}
	
	private void initSerialConfigPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.add(serialLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,5),0,0));
		panel.add(serialCombox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,5),0,0));
		panel.add(serialNameLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,150,5,5),0,0));
		panel.add(serialNameFld,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		panel.add(serialRangeLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,5),0,0));
		
		panel.add(baudLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(baudCombox,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(fluidicsLbl,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,150,5,5),0,0));
		panel.add(checkBox,new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		
		
		panel.add(dataBitLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(dataBitCombox,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(parityLbl,new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,150,5,5),0,0));
		panel.add(parityCombox,new GridBagConstraints(3,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,50),0,0));
		
		panel.add(stopBitLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(stopBitCombox,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		
		serialConfigPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		serialConfigPnl.add(panel);
	}
	
	private void initSerialModePnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(modeLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,25),0,0));
		panel.add(modeCombox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,5),0,0));
		
		serialModePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		serialModePnl.add(panel);
	}
	
	private void initTcpClientPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(tcpClientRemotePortLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,5),0,0));
		panel.add(tcpClientRemotePortFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		panel.add(tcpClientRemoteRangeLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,80),0,0));
		panel.add(tcpClientRemoteIpLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,5),0,0));
		panel.add(tcpClientRemoteIpFld,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,5),0,0));
		
		tcpClientPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		tcpClientPnl.add(panel);
	}
	
	private void initTcpServerPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.add(tcpServerLocalPortLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,5),0,0));
		panel.add(tcpServerLocalPortFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		panel.add(tcpServerLocalRangeLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,5),0,0));
		
		tcpServerPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		tcpServerPnl.add(panel);
	}
	
	private void initUdpClientPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(udpClientRemotePortLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,5),0,0));
		panel.add(udpClientRemotePortFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		panel.add(udpClientRemoteRangeLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,80),0,0));
		panel.add(udpClientRemoteIpLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,5),0,0));
		panel.add(udpClientRemoteIpFld,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,5),0,0));
		
		udpClientPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		udpClientPnl.add(panel);
	}
	
	private void initUdpServerPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.add(udpServerLocalPortLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,5),0,0));
		panel.add(udpServerLocalPortFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		panel.add(udpServerLocalRangeLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,5),0,0));
		
		udpServerPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		udpServerPnl.add(panel);
	}
	
	private void initStatusPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(statusLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
		panel.add(statusFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,34,0,0),0,0));
		
		statusPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		statusPnl.add(panel);
	}
	
	private void setComponentEnable(String mode){
		if (mode.equals("tcpserver")){
			tcpClientRemotePortFld.setEnabled(false);
			tcpClientRemoteIpFld.setEnabled(false);
			
			tcpServerLocalPortFld.setEnabled(true);
			
			udpClientRemotePortFld.setEnabled(false);
			udpClientRemoteIpFld.setEnabled(false);
			
			udpServerLocalPortFld.setEnabled(false);
		}
		else if (mode.equals("tcpclient")){
			tcpClientRemotePortFld.setEnabled(true);
			tcpClientRemoteIpFld.setEnabled(true);
			
			tcpServerLocalPortFld.setEnabled(false);
			
			udpClientRemotePortFld.setEnabled(false);
			udpClientRemoteIpFld.setEnabled(false);
			
			udpServerLocalPortFld.setEnabled(false);
		}
		else if (mode.equals("udpserver")){
			tcpClientRemotePortFld.setEnabled(false);
			tcpClientRemoteIpFld.setEnabled(false);
			
			tcpServerLocalPortFld.setEnabled(false);
			
			udpClientRemotePortFld.setEnabled(false);
			udpClientRemoteIpFld.setEnabled(false);
			
			udpServerLocalPortFld.setEnabled(true);
		}
		else if (mode.equals("udpclient")){
			tcpClientRemotePortFld.setEnabled(false);
			tcpClientRemoteIpFld.setEnabled(false);
			
			tcpServerLocalPortFld.setEnabled(false);
			
			udpClientRemotePortFld.setEnabled(true);
			udpClientRemoteIpFld.setEnabled(true);
			
			udpServerLocalPortFld.setEnabled(false);
		}
	}

	private void setResource(){
		this.setTitle("串口设置");
		serialConfigPnl.setBorder(BorderFactory.createTitledBorder("串口设置"));
		serialModePnl.setBorder(BorderFactory.createTitledBorder("串口模式"));
		tcpClientPnl.setBorder(BorderFactory.createTitledBorder("TCP Client设置"));
		tcpServerPnl.setBorder(BorderFactory.createTitledBorder("TCP Server"));
		udpServerPnl.setBorder(BorderFactory.createTitledBorder("UDP Server"));
		udpClientPnl.setBorder(BorderFactory.createTitledBorder("UDP Client设置"));	

		serialLbl.setText("串口");
		serialNameLbl.setText("串口名");
		serialRangeLbl.setText("(范围:1-32字符)");
		baudLbl.setText("波特率");
		fluidicsLbl.setText("流控");
		dataBitLbl.setText("数据位");
		parityLbl.setText("校验位");
		stopBitLbl.setText("停止位");
		modeLbl.setText("模式");
		tcpClientRemotePortLbl.setText("远端端口");
		tcpClientRemoteRangeLbl.setText("(范围:0 - 65535)");
		tcpClientRemoteIpLbl.setText("远端IP");
		tcpServerLocalPortLbl.setText("本地端口");
		tcpServerLocalRangeLbl.setText("(范围:0 - 65535)");
		udpClientRemotePortLbl.setText("远端端口");
		udpClientRemoteRangeLbl.setText("(范围:0 - 65535)");
		udpClientRemoteIpLbl.setText("远端IP");
		udpServerLocalPortLbl.setText("本地端口");
		udpServerLocalRangeLbl.setText("(范围:0 - 65535)");	
		//Amend 2010.06.08
		serialNameFld.setDocument(new TextFieldPlainDocument(serialNameFld, 32));
		
		statusLbl.setText("状态");
		statusFld.setEditable(false);
		statusFld.setBackground(Color.WHITE);
		statusFld.setColumns(15);
		
		serialNameFld.setColumns(20);
		tcpClientRemotePortFld.setColumns(20);
		tcpClientRemoteIpFld.setColumns(20);
		tcpServerLocalPortFld.setColumns(20);
		udpServerLocalPortFld.setColumns(20);
		udpClientRemotePortFld.setColumns(20);
		udpClientRemoteIpFld.setColumns(20);
		
		serialCombox.setPreferredSize(new Dimension(80,serialCombox.getPreferredSize().height));
		modeCombox.setPreferredSize(new Dimension(80,modeCombox.getPreferredSize().height));
		
		checkBox.setEnabled(false);
		
		//串口下拉框的事件监听
		serialCombox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if (serialCombox.getSelectedItem() != null 
						&& !"".equals(serialCombox.getSelectedItem().toString().trim())){
					String selectSerial = serialCombox.getSelectedItem().toString();
					selectSerial = selectSerial.substring(2,selectSerial.length());
					int selectPort = NumberUtils.toInt(selectSerial);
					setValue(selectPort);
				}
			}
		});
		
		//串口模式下拉框的事件监听
		modeCombox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if (null == serialCombox.getSelectedItem()){
					return;
				}
				String selectSerial = serialCombox.getSelectedItem().toString();
				selectSerial = selectSerial.substring(2,selectSerial.length());
				int selectPort = NumberUtils.toInt(selectSerial);
				
				int iSerialMode = modeCombox.getSelectedIndex();
				setSerialModeChange(selectPort,iSerialMode);
			}
		});
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	/**
	 * 从服务端查询数据
	 */
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager
				.getAdapter(equipmentModel.getLastSelected(),SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			LOG.info("queryData().switchNodeEntity is null!");
			return ;
		}
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<SwitchSerialPort> serialPortList = (List<SwitchSerialPort>)remoteServer.getService()
														.findAll(SwitchSerialPort.class, where, parms);
		if (null == serialPortList || serialPortList.size() < 1){
			LOG.info("queryData().serialPortList is null or size is zero!");
			return;
		}
		
		viewModel.setSerialPortList(serialPortList);
		initSerialCombox();
	}
	
	/**
	 * 初始化端口列表控件
	 * @param serialPortList
	 */
	private void initSerialCombox(){
		modeCombox.removeAllItems();
		for(int i = 0 ; i < modeList.length ; i++){
			modeCombox.addItem(modeList[i]);
		}
		
		baudCombox.removeAllItems();
		for(int i = 0 ; i < baudList.length; i++){
			baudCombox.addItem(baudList[i]);
		}
		baudCombox.setSelectedItem("9600");
		
		dataBitCombox.removeAllItems();
		for(int j = 0 ; j < dataBitList.length; j++){
			dataBitCombox.addItem(dataBitList[j]);
		}
		
		parityCombox.removeAllItems();
		for(int k = 0 ; k < parityList.length; k++){
			parityCombox.addItem(parityList[k]);
		}
			
		stopBitCombox.removeAllItems();
		for(int m = 0 ; m < stopBitList.length; m++){
			stopBitCombox.addItem(stopBitList[m]);
		}
		
		int serialPortCount = viewModel.getSerialPortList().size();
		
		serialCombox.removeAllItems();
		for(int i = 0 ; i < serialPortCount; i++){
			serialCombox.addItem("串口" + viewModel.getSerialPortList().get(i).getPortNo());
		}
		serialCombox.setSelectedIndex(0);
		
		setComponentEnable(modeCombox.getSelectedItem().toString());
	}
	
	private void setValue(int serialPort){
		int port = serialPort;              //串口             
		String portName = viewModel.getSwitchSerialPort(port).getPortName(); //串口名
		float baudRate =  viewModel.getSwitchSerialPort(port).getBaudRate();  //波特率
		boolean flowControl =  viewModel.getSwitchSerialPort(port).isFlowControl();//流控
		int dataBit =  viewModel.getSwitchSerialPort(port).getDatabit();//数据位
		int checkBit =  viewModel.getSwitchSerialPort(port).getCheckbit();//校验位
		int stopBit =  viewModel.getSwitchSerialPort(port).getStopbit();//停止位
		
		int iSerialMode =  viewModel.getSwitchSerialPort(port).getSerialMode();
		String serialMode = reverseIntToString(iSerialMode);//模式
		String status = dataStatus.get(viewModel.getSwitchSerialPort(port).getIssuedTag()).getKey();

		//设置控件的值
		serialNameFld.setText(portName);
		baudCombox.setSelectedItem(""+(int)baudRate);
		checkBox.setSelected(flowControl);
		dataBitCombox.setSelectedItem(""+dataBit);
		parityCombox.setSelectedItem(reverseCheckToString(checkBit));
		stopBitCombox.setSelectedItem(""+stopBit);
		modeCombox.setSelectedItem(serialMode);
		statusFld.setText(status);

		setSerialModeChange(port ,iSerialMode);
		
	}
	
	/**
	 * 根据选择的串口模式，设置相应的控件
	 * @param port
	 * @param iSerialMode
	 */
	private void setSerialModeChange(int port,int iSerialMode){
		String tcpRemotePort = viewModel.getSwitchSerialPort(port).getTcpclientRemotePort();
		String tcpRemoteIP = viewModel.getSwitchSerialPort(port).getTcpclientRemoteIP();
		String tcpLocalPort = viewModel.getSwitchSerialPort(port).getTcpserverLocalPort();
		String udpRemotePort = viewModel.getSwitchSerialPort(port).getUdpclientRemotePort();
		String udpRemoteIP = viewModel.getSwitchSerialPort(port).getUdpclientRemoteIp();
		String udpLocalPort = viewModel.getSwitchSerialPort(port).getUdpserverLocalPort();
		
		switch(iSerialMode){
			case 0:
				tcpClientRemotePortFld.setEnabled(false);
				tcpClientRemoteIpFld.setEnabled(false);
				tcpServerLocalPortFld.setEnabled(true);
				udpClientRemotePortFld.setEnabled(false);
				udpClientRemoteIpFld.setEnabled(false);
				udpServerLocalPortFld.setEnabled(false);
				break;
			case 1:
				tcpClientRemotePortFld.setEnabled(true);
				tcpClientRemoteIpFld.setEnabled(true);
				tcpServerLocalPortFld.setEnabled(false);
				udpClientRemotePortFld.setEnabled(false);
				udpClientRemoteIpFld.setEnabled(false);
				udpServerLocalPortFld.setEnabled(false);
				break;
			case 2:
				tcpClientRemotePortFld.setEnabled(false);
				tcpClientRemoteIpFld.setEnabled(false);
				tcpServerLocalPortFld.setEnabled(false);
				udpClientRemotePortFld.setEnabled(false);
				udpClientRemoteIpFld.setEnabled(false);
				udpServerLocalPortFld.setEnabled(true);
				break;
			case 3:
				tcpClientRemotePortFld.setEnabled(false);
				tcpClientRemoteIpFld.setEnabled(false);
				tcpServerLocalPortFld.setEnabled(false);
				udpClientRemotePortFld.setEnabled(true);
				udpClientRemoteIpFld.setEnabled(true);
				udpServerLocalPortFld.setEnabled(false);
				break;
		}
		
		tcpClientRemotePortFld.setText(tcpRemotePort);
		tcpClientRemoteIpFld.setIpAddress(tcpRemoteIP);
		tcpServerLocalPortFld.setText(tcpLocalPort);
		udpClientRemotePortFld.setText(udpRemotePort);
		udpClientRemoteIpFld.setIpAddress(udpRemoteIP);
		udpServerLocalPortFld.setText(udpLocalPort);
	}
	
	
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载串口配置信息",role=Constants.MANAGERCODE)
	public void download(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		//Amend 2010.06.04
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		List<Serializable> list = getSerialPortList();
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		showMessageDownloadDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.SERIALCOFIG, list, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("下载出现异常");
			LOG.error("SerialPortConfigView.save() error", e);
			//网管侧
			queryData();
		}
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
	}
	
	/**
	 * 当数据下发到设备侧和网管侧时
	 * 对于接收到的异步消息进行处理
	 */
	private void showMessageDownloadDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("下载",this);
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载串口配置信息",role=Constants.MANAGERCODE)
	public void upload(){
		
		if(null == switchNodeEntity){
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
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHSERIAL);
				return message;
			}
		});
	}
	
	private List<Serializable> getSerialPortList(){
		List<Serializable> list = new ArrayList<Serializable>();
		
		String selectSerial = serialCombox.getSelectedItem().toString();
		selectSerial = selectSerial.substring(2,selectSerial.length());
		int selectPort = NumberUtils.toInt(selectSerial);
		SwitchSerialPort switchSerialPort = viewModel.getSwitchSerialPort(selectPort);
		
		String portName = serialNameFld.getText().trim();
		float baudRate = NumberUtils.toFloat(baudCombox.getSelectedItem().toString());
		boolean flowControl = checkBox.isSelected();
		int dataBit = NumberUtils.toInt(dataBitCombox.getSelectedItem().toString());
		int checkBit = reverseCheckToInt(parityCombox.getSelectedItem().toString());
		int stopBit = NumberUtils.toInt(stopBitCombox.getSelectedItem().toString());
		int serialMode = reverseStringToInt(modeCombox.getSelectedItem().toString());
		
		switchSerialPort.setPortName(portName);
		switchSerialPort.setBaudRate(baudRate);
		switchSerialPort.setFlowControl(flowControl);
		switchSerialPort.setDatabit(dataBit);
		switchSerialPort.setCheckbit(checkBit);
		switchSerialPort.setStopbit(stopBit);
		switchSerialPort.setSerialMode(serialMode);
		
		if (tcpClientRemotePortFld.isEnabled()){
			String tcpClientRemotePort = tcpClientRemotePortFld.getText().trim();
			switchSerialPort.setTcpclientRemotePort(tcpClientRemotePort);
		}
		if (tcpClientRemoteIpFld.isEnabled()){
			String tcpClientRemoteIp = tcpClientRemoteIpFld.getText().trim();
			switchSerialPort.setTcpclientRemoteIP(tcpClientRemoteIp);
		}
		if (tcpServerLocalPortFld.isEnabled()){
			String tcpServerLocalPor = tcpServerLocalPortFld.getText().trim();
			switchSerialPort.setTcpserverLocalPort(tcpServerLocalPor);
		}
		if (udpClientRemotePortFld.isEnabled()){
			String udpClientRemotePort = udpClientRemotePortFld.getText().trim();
			switchSerialPort.setUdpclientRemotePort(udpClientRemotePort);
		}
		if (udpClientRemoteIpFld.isEnabled()){
			String udpClientRemoteIp = udpClientRemoteIpFld.getText().trim();
			switchSerialPort.setUdpclientRemoteIp(udpClientRemoteIp);
		}
		if (udpServerLocalPortFld.isEnabled()){
			String udpServerLocalPort = udpServerLocalPortFld.getText().trim();
			switchSerialPort.setUdpserverLocalPort(udpServerLocalPort);
		}
		
		switchSerialPort.setIssuedTag(Constants.ISSUEDADM);
		
		switchSerialPort.setSwitchNode(switchNodeEntity);

		list.add(switchSerialPort);
		
		return list;
	}

	private String reverseIntToString(int value){
		String str = "";
		switch(value){
			case 0:
				str = "tcpserver";
				break;
			case 1:
				str = "tcpclient";
				break;
			case 2:
				str = "udpserver";
				break;
			case 3:
				str = "udpclient";
				break;
		}
		
		return str;
	}
	
	private int reverseStringToInt(String str){
		int value = 0;
		if ("tcpserver".equals(str)){
			value = 0;
		}
		else if ("tcpclient".equals(str)){
			value = 1;
		}
		else if ("udpserver".equals(str)){
			value = 2;
		}
		else if ("udpclient".equals(str)){
			value = 3;
		}
		return value;
	}
	
	private String reverseCheckToString(int value){
		String str = "";
		switch(value){
			case 0:
				str = "none";
				break;
			case 1:
				str = "odd";
				break;
			case 2:
				str = "even";
				break;
		}

		return str;
	}
	
	private int reverseCheckToInt(String str){
		int value = -1;
		if ("none".equals(str)){
			value = 0;
		}
		else if ("odd".equals(str)){
			value = 1;
		}
		else if ("even".equals(str)){
			value = 2;
		}
		return value;
	}

	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			switchNodeEntity = (SwitchNodeEntity) adapterManager
					.getAdapter(equipmentModel.getLastSelected(),SwitchNodeEntity.class);
			queryData();
		}		
	};
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
	}
	
	private void clear(){
		serialNameFld.setText("");
		tcpClientRemotePortFld.setText("");
		tcpClientRemoteIpFld.setIpAddress("");
		tcpServerLocalPortFld.setText("");
		udpClientRemotePortFld.setText("");
		udpClientRemoteIpFld.setIpAddress("");
		udpServerLocalPortFld.setText("");

		serialCombox.removeAllItems();
		modeCombox.removeAllItems();
		baudCombox.removeAllItems();
		dataBitCombox.removeAllItems();
		parityCombox.removeAllItems();
		stopBitCombox.removeAllItems();
	}
	
	//Amend 2010.06.04
	public boolean isValids()
	{
		boolean isValid = true;
		//串口属性，串口号，波特率，数据位，校验位，停止位
		if(null == serialCombox.getSelectedItem() || "".equals(serialCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "串口号不能为空，请选择串口", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == baudCombox.getSelectedItem() || "".equals(baudCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "波特率不能为空，请选择波特率", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == dataBitCombox.getSelectedItem() || "".equals(dataBitCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "数据位不能为空，请选择数据位", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == stopBitCombox.getSelectedItem() || "".equals(stopBitCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "停止位不能为空，请选择停止位", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == parityCombox.getSelectedItem() || "".equals(parityCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "校验位不能为空，请选择校验位", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		//串口名
//		if(null == serialNameFld.getText() || "".equals(serialNameFld.getText()))
//		{
//			JOptionPane.showMessageDialog(this, "串口名错误，范围是：1-32个字符", "提示", JOptionPane.NO_OPTION);
//			isValid = false;
//			return isValid;
//		}
//		else if((serialNameFld.getText().trim().length() < 1) || (serialNameFld.getText().trim().length() > 32))
//		{
//			JOptionPane.showMessageDialog(this, "串口名错误，范围是：1-32个字符", "提示", JOptionPane.NO_OPTION);
//			isValid = false;
//			return isValid;
//		}
		//模式
		if(null == modeCombox.getSelectedItem() || "".equals(modeCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "模式不能为空，请选择模式", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		//others
		if (tcpClientRemotePortFld.isEnabled()){//tcp remote port
			if(null == tcpClientRemotePortFld.getText() || "".equals(tcpClientRemotePortFld.getText()))
			{
				JOptionPane.showMessageDialog(this, "TCP远端端口错误，范围是：0-65535", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if((NumberUtils.toInt(tcpClientRemotePortFld.getText()) < 0) || (NumberUtils.toLong(tcpClientRemotePortFld.getText()) > 65535))
			{
				JOptionPane.showMessageDialog(this, "TCP远端端口错误，范围是：0-65535", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		if (tcpClientRemoteIpFld.isEnabled()){//tcp remote ip
			if(null == tcpClientRemoteIpFld.getText() || "".equals(tcpClientRemoteIpFld.getText()))
			{
				JOptionPane.showMessageDialog(this, "TCP远端IP不能为空，请输入TCP远端IP", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}else if(ClientUtils.isIllegal(tcpClientRemoteIpFld.getText())){
				JOptionPane.showMessageDialog(this, "TCP远端IP非法，请重新输入","提示",JOptionPane.NO_OPTION);
				return false;
			}
		}
		if (tcpServerLocalPortFld.isEnabled()){//tcp local port
			if(null == tcpServerLocalPortFld.getText() || "".equals(tcpServerLocalPortFld.getText()))
			{
				JOptionPane.showMessageDialog(this, "TCP本地端口错误，范围是：0-65535", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if((NumberUtils.toInt(tcpServerLocalPortFld.getText()) < 0) || (NumberUtils.toLong(tcpServerLocalPortFld.getText()) > 65535))
			{
				JOptionPane.showMessageDialog(this, "TCP本地端口错误，范围是：0-65535", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		if (udpClientRemotePortFld.isEnabled()){//udp remote port
			if(null == udpClientRemotePortFld.getText() || "".equals(udpClientRemotePortFld.getText()))
			{
				JOptionPane.showMessageDialog(this, "UDP远端端口错误，范围是：0-65535", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if((NumberUtils.toInt(udpClientRemotePortFld.getText()) < 0) || (NumberUtils.toLong(udpClientRemotePortFld.getText()) > 65535))
			{
				JOptionPane.showMessageDialog(this, "UDP远端端口错误，范围是：0-65535", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		if (udpClientRemoteIpFld.isEnabled()){//udp remote ip
			if(null == udpClientRemoteIpFld.getText() || "".equals(udpClientRemoteIpFld.getText()))
			{
				JOptionPane.showMessageDialog(this, "UDP远端IP不能为空，请输入UDP远端IP", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}else if(ClientUtils.isIllegal(udpClientRemoteIpFld.getText())){
				JOptionPane.showMessageDialog(this, "UDP远端IP非法，请重新输入","提示",JOptionPane.NO_OPTION);
				return false;
			}
		}
		if (udpServerLocalPortFld.isEnabled()){//udp local port
			if(null == udpServerLocalPortFld.getText() || "".equals(udpServerLocalPortFld.getText()))
			{
				JOptionPane.showMessageDialog(this, "UDP本地端口错误，范围是：0-65535", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			else if((NumberUtils.toInt(udpServerLocalPortFld.getText()) < 0) || (NumberUtils.toLong(udpServerLocalPortFld.getText()) > 65535))
			{
				JOptionPane.showMessageDialog(this, "UDP本地端口错误，范围是：0-65535", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		return isValid;		
	}
	
}
