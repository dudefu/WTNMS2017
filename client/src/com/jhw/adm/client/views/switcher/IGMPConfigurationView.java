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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

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
import com.jhw.adm.client.model.switcher.IGMPViewModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.UploadMessageProcessorStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.IGMPEntity;
import com.jhw.adm.server.entity.switchs.Igmp_vsi;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(IGMPConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class IGMPConfigurationView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;

	public static final String ID = "igmpConfigurationView";
	
	//IGMP配置面板
	private final JPanel modifyPnl = new JPanel();
	private final JLabel modeLbl = new JLabel();
	private final JCheckBox modeChkBox = new JCheckBox();
	
	private final JLabel portLbl = new JLabel();
	private final JPanel portPnl = new JPanel();
	
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	private JButton uploadPortBtn;
	private JButton downloadPortBtn;
	
	//VLAN ID 面板
	private final JPanel vlanPnl = new JPanel();
	private final JTable table  = new JTable();
	
	private final JLabel statusVlanLbl = new JLabel();
	private final JTextField statusVlanFld = new JTextField();
	
	private JButton uploadVlanBtn;
	private JButton downloadVlanBtn;
	
//	private final static String[] COLUMN_NAME= {"VLAN ID","Snooping Enabled","IGMP Querier"};
	
	private final static String[] ENABLE = {"Enable","Disable"};
	private final List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
	
	
	//下端按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton closeBtn = null;
	
	private static final String PORTSAVE = "portSave";
	private static final String VLANSAVE = "vlanSave";
	
	private static final String PORTUPLOAD = "portUpload";
	private static final String VLANUPLOAD = "vlanUpload";
	
	private int portCount;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	
	private MessageSender messageSender;
	
	private static final Logger LOG = LoggerFactory.getLogger(IGMPConfigurationView.class);
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(IGMPViewModel.ID)
	private IGMPViewModel viewModel;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
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
		//从服务器上读取vlan数据，显示在列表中
		queryData();
		
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		initConfigPnl();
		initVlanIdPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(modifyPnl,BorderLayout.NORTH);
		this.add(vlanPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(640, 480);
		
		setResource();
	}
	
	private void initConfigPnl(){
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(portPnl);
		
		
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		statusPnl.add(statusFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,33,0,0),0,0));
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		leftPnl.add(statusPnl);
		
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		uploadPortBtn = buttonFactory.createButton(PORTUPLOAD);
		downloadPortBtn = buttonFactory.createButton(PORTSAVE);
		rightPnl.add(uploadPortBtn);
		rightPnl.add(downloadPortBtn);
		
		JPanel saveBtnPnl = new JPanel(new BorderLayout());
		saveBtnPnl.add(leftPnl,BorderLayout.WEST);
		saveBtnPnl.add(rightPnl,BorderLayout.EAST);
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(modeLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(modeChkBox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		
		panel.add(portLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,10),0,0));
		panel.add(scrollPnl,new GridBagConstraints(1,1,1,4,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,5),0,0)); 
		
		modifyPnl.setLayout(new BorderLayout());
		modifyPnl.add(panel,BorderLayout.CENTER);
		modifyPnl.add(saveBtnPnl,BorderLayout.SOUTH);
	}

	private void initVlanIdPnl(){
//		viewModel.setColumnNames(COLUMN_NAME);
		table.setModel(viewModel.getTableModel());
		setComboxColumn(table, table.getColumnModel().getColumn(1));
		setComboxColumn(table, table.getColumnModel().getColumn(2));
		
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(table);
		
		
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusVlanLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		statusPnl.add(statusVlanFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,33,0,0),0,0));
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		leftPnl.add(statusPnl);
		
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		uploadVlanBtn = buttonFactory.createButton(VLANUPLOAD);
		downloadVlanBtn = buttonFactory.createButton(VLANSAVE);
		rightPnl.add(uploadVlanBtn);
		rightPnl.add(downloadVlanBtn);
		
		JPanel btnPnl = new JPanel(new BorderLayout());
		btnPnl.add(leftPnl,BorderLayout.WEST);
		btnPnl.add(rightPnl,BorderLayout.EAST);
		
		vlanPnl.setLayout(new BorderLayout());
		vlanPnl.add(scrollPnl,BorderLayout.CENTER);
		vlanPnl.add(btnPnl,BorderLayout.SOUTH);
	}
	
	public void setComboxColumn(JTable table,TableColumn sportColumn) {
        JComboBox comboBox = new JComboBox();
        for(int i = 0 ; i < ENABLE.length; i++){
        	comboBox.addItem(ENABLE[i]);
        }
        sportColumn.setCellEditor(new DefaultCellEditor(comboBox));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        sportColumn.setCellRenderer(renderer);
    }
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());

		closeBtn = buttonFactory.createCloseButton();
//		newPanel.add(uploadBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		bottomPnl.add(newPanel, BorderLayout.EAST);
	}
	
	private void setResource(){
		this.setTitle("IGMP配置");
		modifyPnl.setBorder(BorderFactory.createTitledBorder("IGMP配置"));
		vlanPnl.setBorder(BorderFactory.createTitledBorder("VLAN ID"));
		
		portPnl.setPreferredSize(new Dimension(150,80));
		portPnl.setBackground(Color.WHITE);
		
		modeLbl.setText("模式");
		modeChkBox.setText("Enable");
		portLbl.setText("端口路由");
		
		statusLbl.setText("状态");
		statusFld.setColumns(15);
		statusFld.setBackground(Color.WHITE);
		statusFld.setEditable(false);
		
		statusVlanLbl.setText("状态");
		statusVlanFld.setColumns(15);
		statusVlanFld.setBackground(Color.WHITE);
		statusVlanFld.setEditable(false);
		
		
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
    		return;
    	}
    	//端口总数
    	portCount = switchNodeEntity.getPorts().size();
    	setPortPnlLayout();
    	
    	String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<IGMPEntity> igmpEntityList = (List<IGMPEntity>)remoteServer.getService().findAll(IGMPEntity.class, where, parms);
		
		if (null == igmpEntityList ){
			return ;
		}
		
		if (igmpEntityList.size() < 1){
			viewModel.setIgmpEntity(null);
		}
		else{
			viewModel.setIgmpEntity(igmpEntityList.get(0));
		}
		
		setValue();
	}
	
	/**
	 * 根据端口数布局portPnl
	 */
	private void setPortPnlLayout(){
		//清空
		portPnl.removeAll();
		checkBoxList.clear();
		
		int row = 0;
		int overage = portCount%3;
		if (overage > 0){
			row = portCount/3 +1;
		}
		else{
			row = portCount/3;
		}
		portPnl.setLayout(new GridLayout(row,3));
		for (int i = 0 ; i < portCount; i++){
			JCheckBox checkBox = new JCheckBox("端口"+(i+1));
			portPnl.add(checkBox);
			checkBox.setBackground(Color.WHITE);
			
			//把所有的JCheckBox保存到列表checkBoxList中
			checkBoxList.add(i, checkBox);
		}
		//刷新
		portPnl.revalidate();
	}
	
	/**
	 * 设置视图中控件的值
	 */
	private void setValue(){
		IGMPEntity igmpEntity = viewModel.getIgmpEntity();
		if (null == igmpEntity){
			viewModel.setIgmpVsiList(null);
			return;
		}
		
		//------------设置IGMP端口信息
		modeChkBox.setSelected(igmpEntity.isApplied());
		statusFld.setText(dataStatus.get(igmpEntity.getIssuedTag()).getKey());
		
		//设置所选择的端口
		if (null != igmpEntity.getPorts() && !("".equals(igmpEntity.getPorts()))){
			String[] ports = igmpEntity.getPorts().split(",");
			for (int i = 0 ; i < ports.length; i++){
				int port = NumberUtils.toInt(ports[i]);////转化整型
				checkBoxList.get(port-1).setSelected(true);
			}
		}
		portPnl.revalidate();
		
		//------------设置vlanId表格信息
		List<Igmp_vsi> igmpVsiList = igmpEntity.getVlanIds();
		if (igmpVsiList != null && igmpVsiList.size() > 0){
			statusVlanFld.setText(dataStatus.get(igmpVsiList.get(0).getIssuedTag()).getKey());
			viewModel.setIgmpVsiList(igmpVsiList);
		}
	}
	
	@ViewAction(name=PORTSAVE, icon=ButtonConstants.DOWNLOAD, text=DOWNLOAD, desc="下载IGMP路由端口信息",role=Constants.MANAGERCODE)
	public void portSave(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		if(checkBoxList == null || checkBoxList.size() <1){
			JOptionPane.showMessageDialog(this, "输入值非法,请重新输入","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		boolean isApplied = modeChkBox.isSelected();
		
		String ports = "";
		for (int i = 0 ; i <checkBoxList.size(); i++){
			if (checkBoxList.get(i).isSelected()){
				ports = ports + (i+1) + ",";
			}
		}
		if(ports.trim().length() != 0){
			ports = ports.substring(0,ports.length()-1);			
		}
		
		IGMPEntity igmpEntity = viewModel.getIgmpEntity();
		if(null == igmpEntity){
			igmpEntity = new IGMPEntity();
		}
		igmpEntity.setApplied(isApplied);
		igmpEntity.setPorts(ports);
		igmpEntity.setSwitchNode(switchNodeEntity);
		
		igmpEntity.setIssuedTag(Constants.ISSUEDADM);
		
		List<Serializable> data = new ArrayList<Serializable>();
		data.add(igmpEntity);
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		showMessageDialog();

		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.IGMP_PORTSET, igmpEntity, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("下载出现异常");
			LOG.error("IGMPConfigurationView.save() error:{}", e);
			queryData();
		}catch (Exception ex) {
			messageOfSaveProcessorStrategy.showErrorMessage("下载出现异常");
			LOG.error("IGMPConfigurationView.save() error:{}", ex);
			queryData();
		}
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			statusFld.setText(dataStatus.get(Constants.ISSUEDADM).getKey());
		}
	}
	
	@ViewAction(name=PORTUPLOAD, icon=ButtonConstants.SYNCHRONIZE, text=UPLOAD, desc="上载IGMP路由端口信息",role=Constants.MANAGERCODE)
	public void portUpload(){
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
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHIGMPPORT);
				return message;
			}
		});
	}
	
	@ViewAction(name=VLANSAVE, icon=ButtonConstants.DOWNLOAD, text=DOWNLOAD, desc="下载IGMP组播VLAN",role=Constants.MANAGERCODE)
	public void vlanSave(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		IGMPEntity igmpEntity = viewModel.getIgmpEntity();
		if(null == igmpEntity){
			igmpEntity = new IGMPEntity();
		}
		
		List<Igmp_vsi> list = viewModel.getIgmpVsiData();
		if (list == null || list.size() < 1){
			JOptionPane.showMessageDialog(this, "输入值非法,请重新输入","提示",JOptionPane.NO_OPTION);
			return;
		}
		for (int i = 0; i < list.size(); i++){
			list.get(i).setIssuedTag(Constants.ISSUEDADM);
		}
		igmpEntity.setVlanIds(list);
		igmpEntity.setSwitchNode(switchNodeEntity);
		
		List<Serializable> data = new ArrayList<Serializable>();
		data.add(igmpEntity);
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		showMessageDialog();
		
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.IGMP_VLANSET, igmpEntity, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("下载出现异常");
			LOG.error("IGMPConfigurationView.save() error:{}", e);
			queryData();
		}
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			statusVlanFld.setText(dataStatus.get(Constants.ISSUEDADM).getKey());
		}
	}
	
	@ViewAction(name=VLANUPLOAD, icon=ButtonConstants.SYNCHRONIZE, text=UPLOAD, desc="上载IGMP组播VLAN",role=Constants.MANAGERCODE)
	public void vlanUpload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		//清除列的编辑状态
		table.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();
		table.getColumnModel().getColumn(2).getCellEditor().stopCellEditing();
		
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
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHIGMPVLANID);
				return message;
			}
		});
	}
	
	/**
	 * 当数据下发到设备侧和网管侧时
	 * 对于接收到的异步消息进行处理
	 */
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("下载",this);
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
			else{
				switchNodeEntity = null;
				viewModel.setIgmpVsiList(null);
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						portPnl.removeAll();
						portPnl.revalidate();
						portPnl.updateUI();
					}
				});
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
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
}
