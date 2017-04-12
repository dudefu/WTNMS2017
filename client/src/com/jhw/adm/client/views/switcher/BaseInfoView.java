package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.OK;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.SwitcherModelNumber;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(BaseInfoView.ID)
@Scope(Scopes.DESKTOP)
public class BaseInfoView extends ViewPart {

	private static final long serialVersionUID = 6166164329141833234L;

	public static final String ID = "baseInfoView";

	//
	private final JPanel systemPnl = new JPanel(); 
	
	//联系方式
	private final JLabel contactLbl = new JLabel();
	private final JTextField contactFld = new JTextField();
	
	//设备名称
	private final JLabel deviceNameLbl = new JLabel();
	private final JTextField deviceNameFld = new JTextField();
	
	//描述
	private final JLabel descriptionLbl = new JLabel();
	private final JTextField descriptionFld = new JTextField();
	
	//内存
	private final JLabel MemoryUsageRateLbl = new JLabel();
	private final JTextField MemoryUsageRateFld = new JTextField();
	
	//CPU
	private final JLabel CPUUsageRateLbl = new JLabel();
	private final JTextField CPUUsageRateFld = new JTextField();
		
	//ip地址
	private final JLabel ipAddrLbl = new JLabel();
	private final JTextField ipAddrFld = new JTextField();
	
	//温度
	private final JLabel TemperatureLbl = new JLabel();
	private final JTextField TemperatureFld = new JTextField();
	
	//类型
	private final JLabel TypeLbl = new JLabel();
	private final JTextField TypeFld = new JTextField();
	
	//设备地址
	private final JLabel companyAddrLbl = new JLabel();
	private final JTextField companyAddrFld = new JTextField();
	
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	//
	private final JPanel hardWarePnl = new JPanel();
	private final JLabel macAddrLbl = new JLabel();
	private final JLabel macAddrSetFld = new JLabel();
	
	private final JLabel btmVerLbl = new JLabel();
	private final JLabel btmVerSetLbl = new JLabel();
	
	//
	private final JPanel timePnl = new JPanel();
	private final JLabel systemTimeLbl = new JLabel();
	private final JLabel systemTimeSetLbl = new JLabel();
	
	private final JLabel startTimeLbl = new JLabel();
	private final JLabel startTimeSetFld = new JLabel();
	
	//
	private final JPanel softWarePnl = new JPanel();
	private final JLabel softWareVerLbl = new JLabel();
	private final JLabel softWareVerSetLbl = new JLabel();
	
	private final JLabel versionTimeLbl = new JLabel();
	private final JLabel versionTimeSetLbl = new JLabel();
	
	//下端的按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn = null;
	private JButton saveBtn = null;
	private JButton closeBtn = null;
	
	private ButtonFactory buttonFactory;
	
	private SwitchTopoNodeEntity switchTopoNode;
	private SwitchNodeEntity switchNodeEntity = null;
	private SwitchBaseInfo baseInfo = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseInfoView.class);
	
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name=DateFormatter.ID)
	private DateFormatter format;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@Resource(name=SwitcherModelNumber.ID)
	private SwitcherModelNumber switcherModelNumber;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;	
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("Upload error", e);
			}
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
		jInit();
		queryData();
	}
	
	private void jInit(){
		buttonFactory = actionManager.getButtonFactory(this);
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
		
		contactFld.setColumns(45);
		deviceNameFld.setColumns(45);
		companyAddrFld.setColumns(45);
		descriptionFld.setColumns(45);
		MemoryUsageRateFld.setColumns(45);
		CPUUsageRateFld.setColumns(45);
		
//		longitudeFld.addKeyListener(new KeyListener() {
//			@Override
//			public void keyTyped(KeyEvent arg0) {
//				keyTypeControl(arg0);
//			}
//			@Override
//			public void keyReleased(KeyEvent arg0) {}
//			@Override
//			public void keyPressed(KeyEvent arg0) {}
//		});
//		latitudeFld.addKeyListener(new KeyListener() {
//			@Override
//			public void keyTyped(KeyEvent e) {
//				keyTypeControl(e);
//			}
//			@Override
//			public void keyReleased(KeyEvent e) {}
//			@Override
//			public void keyPressed(KeyEvent e) {}
//		});
				
		systemPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(contactLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(contactFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,50,5,0),0,0));
		
		panel.add(deviceNameLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(deviceNameFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
	
		panel.add(companyAddrLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(companyAddrFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(TypeLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(TypeFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(ipAddrLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(ipAddrFld,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(descriptionLbl,new GridBagConstraints(0,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(descriptionFld,new GridBagConstraints(1,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
//		panel.add(descriptionLbl,new GridBagConstraints(2,2,1,1,0.0,0.0,
//		GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
//		panel.add(descriptionFld,new GridBagConstraints(3,2,1,1,0.0,0.0,
//		GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
		
		panel.add(MemoryUsageRateLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(MemoryUsageRateFld,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(CPUUsageRateLbl,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(CPUUsageRateFld,new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		panel.add(TemperatureLbl,new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(TemperatureFld,new GridBagConstraints(3,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
//		
		
		systemPnl.add(panel);

		hardWarePnl.setLayout(new FlowLayout(FlowLayout.LEADING));

		macAddrLbl.setPreferredSize(new Dimension(70,20));
		macAddrSetFld.setPreferredSize(new Dimension(180,20));
		btmVerLbl.setPreferredSize(new Dimension(70,20));
		btmVerSetLbl.setPreferredSize(new Dimension(150,20));
		hardWarePnl.add(macAddrLbl);
		hardWarePnl.add(macAddrSetFld);
		hardWarePnl.add(btmVerLbl);
		hardWarePnl.add(btmVerSetLbl);
		
		
		systemTimeLbl.setPreferredSize(new Dimension(70,20));
		systemTimeSetLbl.setPreferredSize(new Dimension(180,20));
		startTimeLbl.setPreferredSize(new Dimension(70,20));
		startTimeSetFld.setPreferredSize(new Dimension(240,20));
		timePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		timePnl.add(systemTimeLbl);
		timePnl.add(systemTimeSetLbl);
		timePnl.add(startTimeLbl);
		timePnl.add(startTimeSetFld);
		
		softWareVerLbl.setPreferredSize(new Dimension(70,20));
		softWareVerSetLbl.setPreferredSize(new Dimension(180,20));
		versionTimeLbl.setPreferredSize(new Dimension(70,20));
		versionTimeSetLbl.setPreferredSize(new Dimension(150,20));
		softWarePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		softWarePnl.add(softWareVerLbl);
		softWarePnl.add(softWareVerSetLbl);
		softWarePnl.add(versionTimeLbl);
		softWarePnl.add(versionTimeSetLbl);
		
		JPanel baseInfoPnl = new JPanel();
		baseInfoPnl.setLayout(new GridBagLayout());
		baseInfoPnl.add(systemPnl,new GridBagConstraints(0,0,1,1,0.0,1.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,5),0,0));//
		baseInfoPnl.add(hardWarePnl,new GridBagConstraints(0,1,1,1,0.0,1.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,5),0,0));//
		baseInfoPnl.add(timePnl,new GridBagConstraints(0,2,1,1,1.0,1.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,5),0,0));//
		baseInfoPnl.add(softWarePnl,new GridBagConstraints(0,3,1,1,0.0,1.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,5),0,0));//

		JPanel statusPnl = new JPanel();
		
		statusPnl.add(statusLbl);
		statusPnl.add(statusFld);
		
		uploadBtn = buttonFactory.createButton(UPLOAD);
		saveBtn = buttonFactory.createButton(OK);
		closeBtn = buttonFactory.createCloseButton();
		
		JPanel newPanel = new JPanel(new GridBagLayout());
		newPanel.add(uploadBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(saveBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 10), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));

		bottomPnl.setLayout(new BorderLayout());
		bottomPnl.add(statusPnl,BorderLayout.WEST);
		bottomPnl.add(newPanel, BorderLayout.EAST);
		this.setCloseButton(closeBtn);

		this.setLayout(new BorderLayout());
		this.add(baseInfoPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		this.setViewSize(610, 550);

		//得到资源文件
		setResource();
		setRegionValue();
	}

	private void setResource(){
		this.setTitle(localizationManager.getString(ResourceConstants.BASEINFOVIEW_BASEINFO));
		systemPnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()
				, localizationManager.getString(ResourceConstants.BASEINFOVIEW_SYSTEM)));
		hardWarePnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()
				, localizationManager.getString(ResourceConstants.BASEINFOVIEW_HARDWARE)));
		timePnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()
				, localizationManager.getString(ResourceConstants.BASEINFOVIEW_TIME)));
		softWarePnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()
				, localizationManager.getString(ResourceConstants.BASEINFOVIEW_SOFTWARE)));
		
		contactLbl.setText(localizationManager.getString(ResourceConstants.BASEINFOVIEW_CONTACT));
		deviceNameLbl.setText(localizationManager.getString(ResourceConstants.BASEINFOVIEW_DEVICENAME));
		
		macAddrLbl.setText(localizationManager.getString(ResourceConstants.BASEINFOVIEW_MACADDR));
		btmVerLbl.setText(localizationManager.getString(ResourceConstants.BASEINFOVIEW_BOOTROTVERSION));
		systemTimeLbl.setText(localizationManager.getString(ResourceConstants.BASEINFOVIEW_SYSTEMTIME));
		startTimeLbl.setText(localizationManager.getString(ResourceConstants.BASEINFOVIEW_STARTTIME));
		softWareVerLbl.setText(localizationManager.getString(ResourceConstants.BASEINFOVIEW_SOFTWAREVER));
		versionTimeLbl.setText(localizationManager.getString(ResourceConstants.BASEINFOVIEW_VERTIME));

		companyAddrLbl.setText("设备地址");
		descriptionLbl.setText("节点名称");
		statusLbl.setText("状态");
		
		MemoryUsageRateLbl.setText("内存使用率");
		CPUUsageRateLbl.setText("CPU使用率");
		ipAddrLbl.setText("IP地址");
		TemperatureLbl.setText("温度");
		TypeLbl.setText("设备型号");

		statusFld.setColumns(15);
		statusFld.setEditable(false);
		statusFld.setBackground(Color.WHITE);
		
		contactFld.setEditable(false);
		deviceNameFld.setEditable(false);
		companyAddrFld.setEditable(false);
		
		ipAddrFld.setEditable(false);
		MemoryUsageRateFld.setEditable(false);
		CPUUsageRateFld.setEditable(false);
		TemperatureFld.setEditable(false);	
		TypeFld.setEditable(false);
		
		Color color = new  Color(238,238,238);
		contactFld.setBackground(color);
		deviceNameFld.setBackground(color);
		descriptionFld.setBackground(Color.WHITE);
		companyAddrFld.setBackground(color);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}

	private void queryData(){
		switchTopoNode = (SwitchTopoNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchTopoNodeEntity.class);
		
		if (null == switchTopoNode){
			return ;
		}
		
		switchNodeEntity = NodeUtils.getNodeEntity(switchTopoNode).getNodeEntity();

		baseInfo = switchNodeEntity.getBaseInfo();
		if (null == baseInfo){
			return;
		}
		clear();
		setValue();
	}
	
	//初始化控件的值
	private void setRegionValue(){
		clear();	
	}
	
	private void setValue(){
		AddressEntity addressEntity = switchNodeEntity.getAddress();
		
		contactFld.setText(baseInfo.getContacts());
		deviceNameFld.setText(baseInfo.getDeviceName());
		
//		if (null != addressEntity){
//			longitudeFld.setText(addressEntity.getLongitude());
//			latitudeFld.setText(addressEntity.getLatitude());
//		}else{
//			longitudeFld.setText(StringUtils.EMPTY);
//			latitudeFld.setText(StringUtils.EMPTY);
//		}
		
		TypeFld.setText(switcherModelNumber.get(switchNodeEntity.getDeviceModel()).getKey());
		ipAddrFld.setText(switchNodeEntity.getBaseConfig().getIpValue());
//		MemoryUsageRateLbl.setText("内存使用率");
//		CPUUsageRateLbl.setText("CPU使用率");
//		TemperatureLbl.setText("温度");

		companyAddrFld.setText(baseInfo.getAddress());
		descriptionFld.setText(switchTopoNode.getName());
		String macAddr = "";
		if (baseInfo.getMacValue() != null){
			macAddr = baseInfo.getMacValue().replace(':', '-');
		}
		macAddrSetFld.setText(macAddr);
		btmVerSetLbl.setText(baseInfo.getBootromVersion());
		systemTimeSetLbl.setText(baseInfo.getCurrentTime());
		startTimeSetFld.setText(baseInfo.getStartupTime());
		softWareVerSetLbl.setText(baseInfo.getCosVersion());
		versionTimeSetLbl.setText(baseInfo.getCosVersionTime());
		statusFld.setText(dataStatus.get(baseInfo.getIssuedTag()).getKey());
		
		TemperatureFld.setText(baseInfo.getTemperature());
		MemoryUsageRateFld.setText(baseInfo.getMemoryUsageRate());
		CPUUsageRateFld.setText(baseInfo.getCPUUsageRate());
		
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task,operation);
				}
			});
		}
	}
	
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
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载交换机基本信息",role=Constants.MANAGERCODE)
	public void upload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		
		Task task = new UploadRequestTask(synDeviceList,MessageNoConstants.SINGLESWITCHINFO);
		showUploadMessageDialog(task, "上载交换机基本信息");
	}
	
	@ViewAction(name=OK, icon=ButtonConstants.SAVE, desc="保存交换机基本信息",role=Constants.MANAGERCODE)
	public void ok() {
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		if (!isValids()) {
			return;
		}
		
		//ip value must be different
		SwitchNodeEntity nodeEntity = remoteServer.getService().getSwitchByIp(ipAddrFld.getText().trim());
		
		if((nodeEntity != null) && (!ObjectUtils.equals(switchNodeEntity.getId(), nodeEntity.getId()))){
			JOptionPane.showMessageDialog(this, "IP地址不能相同，请重新输入IP地址", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		switchTopoNode.setIpValue(ipAddrFld.getText());
		switchTopoNode.setName(descriptionFld.getText().trim());
		
		SwitchBaseConfig switchBaseConfig = null;
		if(null == switchNodeEntity.getBaseConfig()){
			switchBaseConfig = new SwitchBaseConfig();
		}else{
			switchBaseConfig = switchNodeEntity.getBaseConfig();
		}
		switchBaseConfig.setIpValue(ipAddrFld.getText());
		
		//把视图中的数据填充到baseInfo中
		if(null == this.baseInfo){
			baseInfo = new SwitchBaseInfo();
		}
		
		this.baseInfo.setContacts(contactFld.getText().trim());
		this.baseInfo.setDeviceName(deviceNameFld.getText().trim());
		this.baseInfo.setAddress(companyAddrFld.getText());
		
		AddressEntity addrEntity = null;
		if (switchNodeEntity.getAddress() == null){
			addrEntity = new AddressEntity();
		}
		else{
			addrEntity = switchNodeEntity.getAddress();
		}
		addrEntity.setAddress(descriptionFld.getText());
		//addrEntity.setLongitude(getNewString(MemoryUsageRateFld.getText().trim()));
		//addrEntity.setLatitude(getNewString(CPUUsageRateFld.getText().trim()));
		
		switchNodeEntity.setAddress(addrEntity);
		switchNodeEntity.setBaseConfig(switchBaseConfig);
		switchNodeEntity.setBaseInfo(this.baseInfo);
		
		Task task = new RequestTask();
		showMessageDialog(task, "保存");
	}
	
	private class RequestTask implements Task{

		public RequestTask(){
			//
		}
		
		@Override
		public void run() {
			try{
				// 保存实体、地址、管理员
				if (switchNodeEntity.getId() == null) {			
					switcherModelNumber.createSerialPort(switchNodeEntity);
					switcherModelNumber.createPort(switchNodeEntity);
					switchNodeEntity = (SwitchNodeEntity)remoteServer.getService().saveEntity(switchNodeEntity);
				} else {
					switchNodeEntity = (SwitchNodeEntity)remoteServer.getService().updateEntity(switchNodeEntity);
				}
				
				if (switchTopoNode.getId() == null) {
					switchTopoNode = (SwitchTopoNodeEntity)remoteServer.getService().saveEntity(switchTopoNode);
				} else {
					switchTopoNode = (SwitchTopoNodeEntity)remoteServer.getService().updateEntity(switchTopoNode);
				}

			}catch(Exception e){
				strategy.showErrorMessage("保存交换机基本信息异常");
				LOG.error("", e);
			}
			switchTopoNode.setNodeEntity(switchNodeEntity);
			equipmentModel.fireEquipmentUpdated(switchTopoNode);
			strategy.showNormalMessage("保存交换机基本信息成功");
		}
	}

	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
			else {
				switchNodeEntity = null;
				clear();
			}
		}
	};
	
	private void clear() {
		contactFld.setText(StringUtils.EMPTY);
		deviceNameFld.setText(StringUtils.EMPTY);

		descriptionFld.setText(StringUtils.EMPTY);
		MemoryUsageRateFld.setText(StringUtils.EMPTY);
		CPUUsageRateFld.setText(StringUtils.EMPTY);
		TemperatureFld.setText(StringUtils.EMPTY);
		TypeFld.setText(StringUtils.EMPTY);
		
		ipAddrFld.setText(StringUtils.EMPTY);
		companyAddrFld.setText(StringUtils.EMPTY);
		macAddrSetFld.setText(StringUtils.EMPTY);
		btmVerSetLbl.setText(StringUtils.EMPTY);
		systemTimeSetLbl.setText(StringUtils.EMPTY);
		startTimeSetFld.setText(StringUtils.EMPTY);
		softWareVerSetLbl.setText(StringUtils.EMPTY);
		versionTimeSetLbl.setText(StringUtils.EMPTY);
	}
	
	class AreaEntityObject {
		AreaEntity areaEntity = null;
		public AreaEntityObject(AreaEntity areaEntity){
			this.areaEntity = areaEntity;
		}
		
		@Override
		public String toString(){
			if (null == this.areaEntity){
				return null;
			}
			return this.areaEntity.getName();
		}

		public AreaEntity getAreaEntity() {
			return areaEntity;
		}
	}
	
	class UserEntityObject
	{
		UserEntity userEntity = null;
		public UserEntityObject(UserEntity userEntity)
		{
			this.userEntity = userEntity;
		}
		
		@Override
		public String toString()
		{
			if(null == this.userEntity)
			{
				return null;
			}
			return this.userEntity.getUserName();
		}
		public UserEntity getUserEntity()
		{
			return userEntity;
		}
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
//		this.isSelected = false;
	}
	
	//Amend 2010.06.03
	public void keyTypeControl(KeyEvent e)
	{
		char c = e.getKeyChar();
		if (c != '.' &&(c < '0' || c > '9'))
		{
			e.consume();
		}
	}
	public boolean isValids(){
		boolean isValid = true;
		
		if(null == ipAddrFld.getText() || "".equals(ipAddrFld.getText())){
			JOptionPane.showMessageDialog(this, "IP地址不能为空，请输入IP地址","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else if(ClientUtils.isIllegal(ipAddrFld.getText())){
			JOptionPane.showMessageDialog(this, "IP地址非法，请重新输入","提示",JOptionPane.NO_OPTION);
			return false;
		}
		
//		String longitude = longitudeFld.getText().trim();
//		String latitude = latitudeFld.getText().trim();
		
//		if(longitude.indexOf(".") == 0){
//			JOptionPane.showMessageDialog(this, "经度必须以数字开头，请重新输入","提示",JOptionPane.NO_OPTION);
//			isValid = false;
//			return isValid;
//		}
//		if(latitude.indexOf(".") == 0){
//			JOptionPane.showMessageDialog(this, "纬度必须以数字开头，请重新输入","提示",JOptionPane.NO_OPTION);
//			isValid = false;
//			return isValid;
//		}
		
//		if(longitude.length() > 36 || latitude.length() > 36){
//			JOptionPane.showMessageDialog(this, "经纬度长度不能超过36个字符，请重新输入","提示",JOptionPane.NO_OPTION);
//			isValid = false;
//			return isValid;
//		}
//		
//		if(!StringUtils.isBlank(longitude)){
//			if(!NumberUtils.isNumber(longitude)){
//				JOptionPane.showMessageDialog(this, "经度值仅能包含数字和点号，请重新输入","提示",JOptionPane.NO_OPTION);
//				isValid = false;
//				return isValid;
//			}
//		}

//		if(!StringUtils.isBlank(latitude)){
//			if(!NumberUtils.isNumber(latitude)){
//				JOptionPane.showMessageDialog(this, "纬度值仅能包含数字和点号，请重新输入","提示",JOptionPane.NO_OPTION);
//				isValid = false;
//				return isValid;
//			}
//		}
//		
//		if(NumberUtils.toDouble(longitude) > 180){
//			JOptionPane.showMessageDialog(this, "经度值范围为：0-180，请重新输入","提示",JOptionPane.NO_OPTION);
//			isValid = false;
//			return isValid;
//		}
//		
//		if(NumberUtils.toDouble(latitude) > 90){
//			JOptionPane.showMessageDialog(this, "纬度值范围为：0-90，请重新输入","提示",JOptionPane.NO_OPTION);
//			isValid = false;
//			return isValid;
//		}
		
		if (descriptionFld.getText().trim().length() > 20) {
			JOptionPane.showMessageDialog(this, "节点名称长度不能超过20个字符，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		return isValid;
	}
	public String getNewString(String oldString)
	{
		String newString = "";
		
		if(oldString.indexOf(".") < 0){//no dot
		
			return oldString;
		}
		
		String headString = oldString.substring(0, oldString.indexOf(".") + 1);
		String tailString = oldString.substring(oldString.indexOf(".") + 1, oldString.length());
		
		if(tailString.length() == 0){//dot is the last char
		
			newString = headString.substring(0, oldString.indexOf("."));
			return newString;
		}
		
		if(tailString.length() <= 6){
			newString = oldString;
			return newString;
		}else{
			newString =  headString + tailString.substring(0, 6);
			return newString;
		}
	}
}
