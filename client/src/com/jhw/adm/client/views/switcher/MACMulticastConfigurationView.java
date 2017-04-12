package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.FIRST;
import static com.jhw.adm.client.core.ActionConstants.LAST;
import static com.jhw.adm.client.core.ActionConstants.NEXT;
import static com.jhw.adm.client.core.ActionConstants.PREVIOUS;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
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
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.MACMulticastModel;
import com.jhw.adm.client.swing.CommonTable;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.MacAddressField;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MACMutiCast;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(MACMulticastConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class MACMulticastConfigurationView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;

	public static final String ID = "macMulticastConfigurationView";
	
	//工具按钮面板
	private final JPanel toolBtnPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton saveBtn;
	private JButton deleteBtn;
	private JButton closeBtn = null;

	private final JScrollPane scrollPnl = new JScrollPane();
	private final JPanel configPnl = new JPanel();
	
	private final JLabel macAddrLbl = new JLabel();
	private final MacAddressField macAddrFld = new MacAddressField(MacAddressField.EMRULE);
	
	private final JLabel vlanIdLbl = new JLabel();
	private final NumberField vlanIdFld = new NumberField(4,0,1,4094,true);
	
	private final JLabel portLbl = new JLabel();
	private final JPanel portPnl = new JPanel();
	private final List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
	
	private static final String MAC_RANG = "01开头";
	private static final String VLANID_RANG = "1-4094";
	private final JPanel tablePnl = new JPanel();
	private final CommonTable table = new CommonTable(); 
	private static final String[] COLUMN_NAME = {"编号","端口","VLAN ID","MAC地址","状态"}; 
	
	//下端的分页面板
	private final JPanel bottomPnl = new JPanel();
	private JButton firstBtn;
	private JButton previousBtn;
	private JButton nextBtn;
	private JButton lastBtn;
	private final JComboBox pageValueCombox = new JComboBox();
	
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
	@Qualifier(MACMulticastModel.ID)
	private MACMulticastModel model;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private static final Logger LOG = LoggerFactory.getLogger(MACMulticastConfigurationView.class);
	
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
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
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
		
		initToolPanel();
		initConfigPnl();
		initTablePnl();
		initBottomPnl();
	
		this.setLayout(new BorderLayout());
		this.add(configPnl,BorderLayout.NORTH);
		this.add(tablePnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initToolPanel(){
		toolBtnPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);
		deleteBtn = buttonFactory.createButton(DELETE);
		toolBtnPnl.add(saveBtn);
		toolBtnPnl.add(deleteBtn);
	}
	
	private void initConfigPnl(){
		JPanel leftPanel = new JPanel(new GridBagLayout());
		leftPanel.add(macAddrLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		leftPanel.add(macAddrFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		leftPanel.add(new StarLabel("(" +  MAC_RANG + ")"),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,10),0,0));
		
		leftPanel.add(vlanIdLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(20,5,0,5),0,0));
		leftPanel.add(vlanIdFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(20,10,0,0),0,0));
		leftPanel.add(new StarLabel("(" +  VLANID_RANG + ")"),new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(20,0,0,10),0,0));
		
		JPanel rightPanel = new JPanel(new GridBagLayout());
		portPnl.setBackground(Color.WHITE);
		scrollPnl.getViewport().add(portPnl);
		scrollPnl.setPreferredSize(new Dimension(scrollPnl.getPreferredSize().width,80));
		rightPanel.add(portLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,0,10),0,0));
		rightPanel.add(scrollPnl,new GridBagConstraints(3,0,1,4,1.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0)); 
		rightPanel.add(new StarLabel(),new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,5),0,0));
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(leftPanel,BorderLayout.WEST);
		panel.add(rightPanel,BorderLayout.CENTER);
		
		configPnl.setBorder(BorderFactory.createLineBorder(Color.gray));
		configPnl.setLayout(new BorderLayout());
		configPnl.add(panel,BorderLayout.CENTER);
		configPnl.add(toolBtnPnl,BorderLayout.SOUTH);
		
	}
	
	private void initTablePnl(){
		model.setColumnName(COLUMN_NAME);
		table.setModel(model);
		table.setSortable(false);
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(table);
		tablePnl.setLayout(new BorderLayout());
		tablePnl.add(scrollPnl,BorderLayout.CENTER);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		firstBtn = buttonFactory.createButton(FIRST);
		previousBtn = buttonFactory.createButton(PREVIOUS);
		nextBtn = buttonFactory.createButton(NEXT);
		lastBtn = buttonFactory.createButton(LAST);
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
	
	private void setPortPnlLayout(int portCount){
		checkBoxList.clear();
		JCheckBox[] checkBox = new JCheckBox[portCount];
		
		int row = 0;
		int overage = portCount%3;
		if (overage > 0){
			row = portCount/3 +1;
		}
		else{
			row = portCount/3;
		}
		portPnl.setLayout(new GridLayout(row,3));
		portPnl.removeAll();
		for (int i = 0 ; i < portCount; i++){
			checkBox[i] = new JCheckBox("端口"+(i+1));
			portPnl.add(checkBox[i]);
			checkBox[i].setBackground(Color.WHITE);
			
			//把所有的JCheckBox保存到列表checkBoxList中
			checkBoxList.add(i, checkBox[i]);
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				portPnl.updateUI();
			}
		});
	}
	
	private void setResource(){
		macAddrFld.setColumns(25);
		vlanIdFld.setColumns(25);

		macAddrLbl.setText("MAC地址");
		vlanIdLbl.setText("VLAN ID");
		portLbl.setText("端口");
		//2010.06.08
		macAddrFld.setDocument(new TextFieldPlainDocument(macAddrFld));
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		//初始化端口panel
		int portCount = switchNodeEntity.getPorts().size();
		setPortPnlLayout(portCount);

		String where = " where entity.switchNode=? order by issuedTag asc";
		Object[] parms = {switchNodeEntity};
		List<MACMutiCast> macMuticastList = (List<MACMutiCast>)remoteServer.getService().findAll(MACMutiCast.class, where, parms);
		
		setValue(macMuticastList);
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(List<MACMutiCast> macMuticastList){
		List<List> data = new ArrayList<List>();
		if (null != macMuticastList && macMuticastList.size() > 0){
			for (int i = 0 ; i < macMuticastList.size(); i++){
				MACMutiCast macMutiCast = macMuticastList.get(i);
				int index = macMutiCast.getSortNum();
				int row = i + 1;
				String macAddr = macMutiCast.getMacAddress(); //mac地址
				String vlanID = macMutiCast.getVlanID();  //VLAN ID
				String ports = macMutiCast.getPorts(); //端口组成的字符串
				
				List rowData = new ArrayList();
				rowData.add(0, row);
				rowData.add(1,ports);
				rowData.add(2,vlanID);
				rowData.add(3,macAddr);
				rowData.add(4,dataStatus.get(macMutiCast.getIssuedTag()).getKey());
				rowData.add(5,macMutiCast);
				data.add(rowData);
			}
		}
		model.setDataList(data);
		model.fireTableDataChanged();
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存多播MAC地址",role=Constants.MANAGERCODE)
	public void save(){
		if(!isValids()){
			return;
		}

		String macAddrValue = macAddrFld.getText().trim().toUpperCase();//mac地址
		String vlanIdValue = vlanIdFld.getText().trim();//VLAN ID
		String ports = getSelectedPorts();//端口集合
		if (ports.equals("")){
			JOptionPane.showMessageDialog(this, "请选择端口","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		//判断列表中是否已经有相同的多播mac地址
		List<List> list = model.getDataList();
		if (list != null && list.size() >0){
			int rowCount = table.getRowCount();
			for (int row = 0 ; row < rowCount; row++){
				int modelRow = table.convertRowIndexToModel(row);
				MACMutiCast macMutiCastEntity = (MACMutiCast)list.get(modelRow).get(5);
				String macAddr = macMutiCastEntity.getMacAddress();
				String vlanID = macMutiCastEntity.getVlanID();
				if (macAddr.equalsIgnoreCase(macAddrValue) 
						&& vlanID.equalsIgnoreCase(vlanIdValue)){
					JOptionPane.showMessageDialog(this, "输入的MAC地址已经存在，请重新输入", "提示", JOptionPane.NO_OPTION);
					return;
				}
			}
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return ;
		}
		
		MACMutiCast macMutiCast = new MACMutiCast();
		macMutiCast.setMacAddress(macAddrValue);
		macMutiCast.setVlanID(vlanIdValue);
		macMutiCast.setPorts(ports);
		macMutiCast.setSwitchNode(switchNodeEntity);
		macMutiCast.setIssuedTag(Constants.ISSUEDADM);
		List<Serializable> dataList = new ArrayList<Serializable>();
		dataList.add(macMutiCast);
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		Task task = new SaveRequestTask(dataList, macValue, result);
		showConfigureMessageDialog(task, "保存");
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
				remoteServer.getAdmService().saveAndSetting(macValue, MessageNoConstants.MACMUTINEW, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("保存多播MAC地址");
				queryData();
				clear();
				LOG.error("MACUnicastConfigurationView.save() error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
				clear();
			}
		}
	}
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载多播MAC地址",role=Constants.MANAGERCODE)
	public void download(){
		int result = Constants.SYN_ALL;
		if (table.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要下载的数据", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		//mac多播类型为false;
		boolean isDynamic = false;
		List<Serializable> dataList = new ArrayList<Serializable>();
		List<List> list = model.getDataList();
		int selectRowCount = table.getSelectedRowCount();
		for(int i = 0 ; i < selectRowCount; i++){
			int row = table.getSelectedRows()[i];
			int modelRow = table.convertRowIndexToModel(row);
			MACMutiCast macMutiCast = (MACMutiCast)list.get(modelRow).get(5);
			int issuedTag = macMutiCast.getIssuedTag();
			if (issuedTag == Constants.ISSUEDDEVICE){
				JOptionPane.showMessageDialog(this, "选择的MAC地址已经存在，请重新选择", "提示", JOptionPane.NO_OPTION);
				return;
			}
			
			macMutiCast.setIssuedTag(Constants.ISSUEDADM);
			dataList.add(macMutiCast);
		}
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		Task task = new DownloadRequestTask(dataList, macValue, result);
		showConfigureMessageDialog(task, "下载");
	}
	
	private class DownloadRequestTask implements Task{

		private List<Serializable> list = null;
		private String macValue = "";
		private int result = 0;
		public DownloadRequestTask(List<Serializable> list, String macValue, int result){
			this.list = list;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.MACMUTINEW, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载多播MAC地址");
				queryData();
				clear();
				LOG.error("MACUnicastConfigurationView.save() error", e);
			}
		}
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			paramConfigStrategy = new ParamConfigStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
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
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载多播MAC地址",role=Constants.MANAGERCODE)
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
		
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHMULTICAST);
		showUploadMessageDialog(task, "上载多播MAC地址");
	}
	
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="删除多播MAC地址",role=Constants.MANAGERCODE)
	public void delete(){
		if(table.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要删除的数据", "提示", JOptionPane.NO_OPTION);
			return;
		}
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}

		List<Serializable> dataDeviceList = new ArrayList<Serializable>();//设备侧和网管侧
		List<Serializable> dataServerList = new ArrayList<Serializable>();//网管侧
		boolean isDevice = false;
		boolean isServer = false;
		
		List<List> list = model.getDataList();
		int selectRowCount = table.getSelectedRowCount();
		for(int i = 0 ; i < selectRowCount; i++){
			int row = table.getSelectedRows()[i];
			int modelRow = table.convertRowIndexToModel(row);
			MACMutiCast macMutiCast = (MACMutiCast)list.get(modelRow).get(5);
			
			if(macMutiCast.getIssuedTag() == Constants.ISSUEDADM){//网管侧
				isServer = true;
				dataServerList.add(macMutiCast);
			}
			else{//设备侧和网管侧
				isDevice = true;
				dataDeviceList.add(macMutiCast);
			}
		}

		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		Task task = new DeleteRequestTask(isDevice, isServer, macValue, dataDeviceList, dataServerList);
		showConfigureMessageDialog(task, "删除");
	}
	
	private class DeleteRequestTask implements Task{

		private boolean isDevice = false;
		private boolean isServer = false;
		private String macValue = "";
		private List<Serializable> dataDeviceList;//设备侧和网管侧
		private List<Serializable> dataServerList;//网管侧

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
			if (isDevice && isServer){ //加入设备侧和网管侧都有要删除的，先发删除网管的，再下发删除设备的消息
				//删除网管侧
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.MACMUTIDEL, dataServerList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_SERVER);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除多播MAC地址异常");
					queryData();
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
				}
				
				//删除设备侧
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.MACMUTIDEL, dataDeviceList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除多播MAC地址异常");
					queryData();
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
				}
			}
			else if(isDevice == true && isServer == false){//只删除设备侧
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.MACMUTIDEL, dataDeviceList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除多播MAC地址异常");
					queryData();
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
				}
			}
			else if(isDevice == false && isServer == true){//只删除网管侧
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.MACMUTIDEL, dataServerList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_SERVER);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除多播MAC地址异常");
					queryData();
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
				}
				paramConfigStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			}
			queryData();
		}
	}
	
	@ViewAction(name=FIRST, icon=ButtonConstants.FIRST)
	public void first(){
		
	}
	
	@ViewAction(name=PREVIOUS, icon=ButtonConstants.PREVIOUS)
	public void previous(){
		
	}
	
	@ViewAction(name=NEXT, icon=ButtonConstants.NEXT)
	public void next(){
		
	}
	
	@ViewAction(name=LAST, icon=ButtonConstants.LAST)
	public void last(){
		
	}
	
	/**
	 * 得到所有被选择端口的集合，以","分隔
	 * @author Administrator
	 */
	private String getSelectedPorts(){
		int count = checkBoxList.size();
		String ports = "";
		for (int i = 0 ; i < count; i++){
			JCheckBox checkBox = checkBoxList.get(i);
			if (checkBox.isSelected()){
				if(ports.length() == 0){
					ports = ports + (i+1);
				}else{
					ports = ports + "," + (i+1);
				}
			}
		}
//		if(ports.length() != 0){
//			ports = ports.substring(0,ports.length()-1);
//		}
		return ports;
	}
	
	/**
	 * 清空控件中的值
	 */
	private void clear(){
		macAddrFld.setText("");
		vlanIdFld.setText("");
		for (int i = 0 ; i < checkBoxList.size(); i++){
			checkBoxList.get(i).setSelected(false);
		}
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
			else{
				switchNodeEntity = null;
				model.setDataList(null);
				model.fireTableDataChanged();
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
	public void receive(Object object){}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
	
	//Amend 2010.06.03
	public boolean isValids(){
		boolean isValid = true;
		
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String macAddr = macAddrFld.getText();
		if(null == macAddrFld.getText() || "".equals(macAddrFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "MAC地址不能为空，请输入MAC地址","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else if(!"01".equals(macAddr.substring(0, 2))){
			JOptionPane.showMessageDialog(this, "MAC地址必须以01开头，请输入MAC地址","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String regex = "^([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})$";
		Pattern p=Pattern.compile(regex);
		Matcher m=p.matcher(macAddr);
		if (!m.matches()){
			JOptionPane.showMessageDialog(this, "MAC地址错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == vlanIdFld.getText() || "".equals(vlanIdFld.getText()))
		{
//			TaskDialogs.inform("提示", "VLAN ID不能为空，请输入VLAN ID");
			JOptionPane.showMessageDialog(this, "VLAN ID不能为空，请输入VLAN ID","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if((org.apache.commons.lang.math.NumberUtils.toInt(vlanIdFld
						.getText()) < 1) || (org.apache.commons.lang.math.NumberUtils
						.toInt(vlanIdFld.getText()) > 4094))
		{
			JOptionPane.showMessageDialog(this, "VLAN ID错误，范围是1-4094","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		return isValid;
	}
}
