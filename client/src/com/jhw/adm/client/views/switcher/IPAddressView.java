package com.jhw.adm.client.views.switcher;

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
import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.TextMessage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.resources.ResourceManager;
import com.jhw.adm.client.snmp.SnmpData;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(IPAddressView.ID)
@Scope(Scopes.DESKTOP)
public class IPAddressView extends ViewPart {
	private static final long serialVersionUID = 1L;

	public static final String ID = "ipAddressView";

	private final JLabel dhcpLbl = new JLabel();
	private final JCheckBox dhcpChk = new JCheckBox();
	
	private final JLabel ipAddrLbl = new JLabel();
	private final IpAddressField ipAddrFld = new IpAddressField();
	
	private final JLabel maskLbl = new JLabel();
	private final IpAddressField maskFld = new IpAddressField();
	
	private final JLabel gateWayLbl = new JLabel();
	private final IpAddressField gateWayFld = new IpAddressField();
	
	private final JLabel preferredLbl = new JLabel();
	private final IpAddressField preferredFld = new IpAddressField();
	
	private final JLabel standbyLbl = new JLabel();
	private final IpAddressField standbyFld = new IpAddressField();
	
	private final JLabel vlanLbl = new JLabel();
	private final JTextField vlanFld = new JTextField();
	private final JButton vlanBtn = new JButton("..");
	
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn ;
	
	private static final Logger LOG = LoggerFactory.getLogger(IPAddressView.class);
	private MessageSender messageSender;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	
	// IP Information of Node
	public final static String IP = "1.3.6.1.4.1.44405.71.2.4.1.0";
	public final static String NETMASK = "1.3.6.1.4.1.44405.71.2.4.2.0";
	public final static String GATEWAY = "1.3.6.1.4.1.44405.71.2.4.3.0";
	public final static String DHCPSTATE = "1.3.6.1.4.1.44405.71.2.4.4.0";
	public final static String PRIDNS = "1.3.6.1.4.1.44405.71.2.4.5.0";
	public final static String SECDNS = "1.3.6.1.4.1.44405.71.2.4.6.0";
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
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
		jInit();
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
		queryData();
	}
	
	private void jInit(){
		buttonFactory = actionManager.getButtonFactory(this); 
		
		this.setTitle("IP地址");

		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(dhcpLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(dhcpChk,new GridBagConstraints(1,0,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,80,5,0),0,0));
		
		panel.add(ipAddrLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(ipAddrFld,new GridBagConstraints(1,1,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,80,5,0),0,0));
		
		panel.add(maskLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(maskFld,new GridBagConstraints(1,2,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,80,5,0),0,0));
		
		panel.add(gateWayLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(gateWayFld,new GridBagConstraints(1,3,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,80,5,0),0,0));
		
		panel.add(preferredLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(preferredFld,new GridBagConstraints(1,4,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,80,5,0),0,0));
		
		panel.add(standbyLbl,new GridBagConstraints(0,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(standbyFld,new GridBagConstraints(1,5,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,80,5,0),0,0));
		
		panel.add(vlanLbl,new GridBagConstraints(0,6,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(vlanFld,new GridBagConstraints(1,6,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,80,5,0),0,0));
//		panel.add(vlanBtn,new GridBagConstraints(2,6,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,0,5,0),0,0));
		
		
		JPanel ipInfoPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		ipInfoPnl.add(panel);
//		ipInfoPnl.setBorder(BorderFactory.createTitledBorder("IP地址信息"));
		
		
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		statusPnl.add(statusFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		leftPnl.add(statusPnl);
		
		JPanel newPanel = new JPanel(new GridBagLayout());
		uploadBtn = buttonFactory.createButton(UPLOAD);
		ResourceManager resourceManager = new ResourceManager();
		ImageIcon icon = new ImageIcon(resourceManager.getURL(ButtonConstants.DOWNLOAD));
		downloadBtn = new JButton("下载",icon);
		downloadBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SwitchBaseConfig switchBaseConfig = switchNodeEntity.getBaseConfig();
				if (switchBaseConfig == null) {
					LOG.error("BaseConfig is null");
					return;
				}
				String ipAddr = switchBaseConfig.getIpValue();
				String ipaddr = ipAddrFld.getText();
				String community = "private";
				String mask = maskFld.getText();
				String gateWay = gateWayFld.getText();
				String preferred = preferredFld.getText();
				String standby = standbyFld.getText();
				SnmpData.snmpSet(ipAddr, community, IP, ipaddr);
				SnmpData.snmpSet(ipaddr, community, NETMASK, mask);
				SnmpData.snmpSet(ipaddr, community, GATEWAY, gateWay);
				SnmpData.snmpSet(ipaddr, community, PRIDNS, preferred);
				SnmpData.snmpSet(ipaddr, community, SECDNS, standby);
				JOptionPane.showMessageDialog(null, "      下载完成！", "下载完成",JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		closeBtn = buttonFactory.createCloseButton();
		newPanel.add(uploadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPnl.add(newPanel);
		
		bottomPnl.setLayout(new BorderLayout());
		bottomPnl.add(leftPnl,BorderLayout.WEST);
		bottomPnl.add(rightPnl,BorderLayout.EAST);

		this.setLayout(new BorderLayout());
		this.add(ipInfoPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		dhcpChk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setDhcp();
			}
		});
		setResource();
	}
	
	/**
	 * 设置资源文件
	 */
	private void setResource(){
		this.setViewSize(400, 400);
		ipAddrFld.setColumns(20);
		vlanFld.setColumns(20);
		vlanBtn.setPreferredSize(new Dimension(19,19));
		
		dhcpLbl.setText("DHCP");
		dhcpChk.setEnabled(true);
		ipAddrLbl.setText("IP地址");
		ipAddrLbl.setEnabled(true);
		maskLbl.setText("子网掩码");
		maskLbl.setEnabled(true);
		gateWayLbl.setText("网关");
		gateWayLbl.setEnabled(true);
		preferredLbl.setText("首选DNS");
		preferredLbl.setEnabled(true);
		standbyLbl.setText("备用DNS");
		standbyLbl.setEnabled(true);
		vlanLbl.setText("管理VLAN");
		
		vlanFld.setEditable(true);
		vlanFld.setBackground(Color.WHITE);
		
		statusLbl.setText("状态");
		statusFld.setColumns(10);
		statusFld.setBackground(Color.WHITE);
		statusFld.setEditable(false);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		//setEnable(true);
	}
	
	/**
	 * 从服务端查询数据
	 */
	private void queryData(){
		switchNodeEntity =(SwitchNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		setValue();
	}
	
	private void setValue(){
		SwitchBaseConfig baseConfig = switchNodeEntity.getBaseConfig();
		if (baseConfig == null) {
			LOG.error("BaseConfig is null");
			return;
		}
		boolean isDhcp = baseConfig.isDhcpAyylied();
		String ipValue = baseConfig.getIpValue();
		String maskValue = baseConfig.getMaskValue();;
		String netGate = baseConfig.getNetGate();
		String firstDns = baseConfig.getFirstDNS();
		String secondDns = baseConfig.getSecondDNS();
		//默认管理VLAN ID为1，并且不能修改
		//int vlanId = baseConfig.getManagerVlanID();
		String vlanId = "1";
		String status = dataStatus.get(baseConfig.getIssuedTag()).getKey();
		
		dhcpChk.setSelected(isDhcp);
		ipAddrFld.setIpAddress(ipValue);
		maskFld.setIpAddress(maskValue);
		gateWayFld.setIpAddress(netGate);
		preferredFld.setIpAddress(firstDns);
		standbyFld.setIpAddress(secondDns);
		vlanFld.setText(vlanId);
		statusFld.setText(status);
	}
	
	private void setDhcp(){
		if (dhcpChk.isSelected()){
			ipAddrFld.setEnabled(false);
			maskFld.setEnabled(false);
			gateWayFld.setEnabled(false);
		}
		else {
			ipAddrFld.setEnabled(true);
			maskFld.setEnabled(true);
			gateWayFld.setEnabled(true);
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载交换机IP地址",role=Constants.MANAGERCODE)
	public void upload(){
		
		if(null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHIP);
		showUploadMessageDialog(task, "上载交换机IP地址");
	}
	
	private JProgressBarModel progressBarModel;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	private void showUploadMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			uploadStrategy = new ParamUploadStrategy(operation, progressBarModel,true);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
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
	 * 设备浏览器监听事件
	 * @author Administrator
	 */
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
		}		
	};

	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESWITCHIP, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
	}
	
	private void clear(){
		dhcpChk.setSelected(false);
		ipAddrFld.setIpAddress("");
		maskFld.setIpAddress("");
		gateWayFld.setIpAddress("");
		preferredFld.setIpAddress("");
		standbyFld.setIpAddress("");
		vlanFld.setText("");
	}
	
	private void setEnable(boolean bool){
		dhcpChk.setEnabled(bool);
		ipAddrFld.setEditable(bool);
		maskFld.setEditable(bool);
		gateWayFld.setEditable(bool);
		preferredFld.setEditable(bool);
		standbyFld.setEditable(bool);
		vlanFld.setEditable(bool);
		vlanBtn.setEnabled(bool);
	}
}
