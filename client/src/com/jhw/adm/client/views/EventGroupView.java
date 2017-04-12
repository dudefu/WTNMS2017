package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
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

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.client.views.switcher.UploadRequestTask;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.RmonThing;

public class EventGroupView extends ViewPart {
	private static final long serialVersionUID = 2478348590364535198L;

	public static final String ID = "eventGroupView";
	
	private static final Logger LOG = LoggerFactory.getLogger(EventGroupView.class);
	private final JPanel topPnl = new JPanel();
	private final JLabel itemCodeLbl = new JLabel();//条目编号
	private final NumberField itemCodeFld = new NumberField(5, 0, 1, 65535, true);
	
	private final JLabel descripLbl = new JLabel();//事件描述
	private final JTextField descripFld = new JTextField();
	
	private final JLabel typeLbl = new JLabel();//事件类型
	private final JComboBox typeCombox = new JComboBox();
	
	private final JLabel comityLbl = new JLabel();//事件团体名
	private final JTextField comityFld = new JTextField();
	
	////
	private final JPanel centerPnl = new JPanel();
	
	private final JPanel toolPnl = new JPanel();
	private JButton saveBtn;
	private JButton deleteBtn;
	
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JTable table = new JTable();
	private EventTableModel tableModel = null;
	
	///
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton selectBtn ;
	private JButton closeBtn ;
	
	private MessageSender messageSender;
	
	private ActionManager actionManager;
	
	private final static String[] VALUES = {"none","log","trap","log_trap"};
	private final static String[] COLUMN_NAME = {"条目编号","事件描述","事件类型","事件团体名","状态"};
	
	private final ImageRegistry imageRegistry;
	private final RemoteServer remoteServer;	
	private final ConfigPortWarningView portWarningView;	
	private SwitchNodeEntity switchNodeEntity = null;
	private ButtonFactory buttonFactory;
	private DataStatus dataStatus;
	private boolean isMax;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel = null;
	
	private MessageDispatcher messageDispatcher;
	
	private final JDialog dialog ;
	
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
	
	public EventGroupView(ConfigPortWarningView portWarningView,JDialog dialog,ImageRegistry imageRegistry
			,RemoteServer remoteServer,ActionManager actionManager,
			MessageDispatcher messageDispatcher,
			 DataStatus dataStatus,boolean isMax){
		this.portWarningView = portWarningView;
		this.imageRegistry = imageRegistry;
		this.remoteServer = remoteServer;
		this.actionManager = actionManager;
		this.messageDispatcher = messageDispatcher;
		this.dataStatus = dataStatus;
		this.isMax = isMax;
		this.dialog = dialog;
		if (null != portWarningView){
			this.switchNodeEntity = portWarningView.getSwitchNodeEntity();
			this.clientModel = portWarningView.getClientModel();
		}
		
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
		
		setResource(); 
		
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		
		dialog.setSize(new Dimension(500,400));
		
		dialog.setLocationRelativeTo(portWarningView);
	}
	
	private void initTopPnl(){
		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(itemCodeLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(itemCodeFld,new GridBagConstraints(1,0,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		panel.add(new StarLabel("(1-65535)"),new GridBagConstraints(3,0,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		
		panel.add(descripLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(descripFld,new GridBagConstraints(1,1,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		panel.add(new StarLabel("(字符串长度范围1~32)"),new GridBagConstraints(3,1,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		
		panel.add(typeLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(typeCombox,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		
		panel.add(comityLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(comityFld,new GridBagConstraints(1,3,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		panel.add(new StarLabel("(字符串长度范围1~32)"),new GridBagConstraints(3,3,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		
		topPnl.add(panel);
	}
	
	private void initCenterPnl(){
		toolPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);
		deleteBtn = buttonFactory.createButton(DELETE);;
		toolPnl.add(saveBtn);
		toolPnl.add(deleteBtn);

		tableModel = new EventTableModel();
		tableModel.setColumnName(COLUMN_NAME);
		table.setModel(tableModel);
//		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPnl.getViewport().add(table);
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(toolPnl,BorderLayout.NORTH);
		centerPnl.add(scrollPnl,BorderLayout.CENTER);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		selectBtn = new JButton("选择", imageRegistry.getImageIcon(ButtonConstants.QUERY));
		closeBtn = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
		
		JPanel newPanel = new JPanel(new GridBagLayout());
		newPanel.add(uploadBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(selectBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		bottomPnl.add(newPanel, BorderLayout.EAST);
	}
	
	private void setResource(){
		itemCodeLbl.setText("条目编号");
		descripLbl.setText("事件描述");
		typeLbl.setText("事件类型");
		comityLbl.setText("事件团体名");
		
		itemCodeFld.setColumns(20);
		itemCodeFld.setHorizontalAlignment(JTextField.LEADING);
		
		for(int i = 0 ; i < VALUES.length; i++){
			typeCombox.addItem(VALUES[i]);
		}
		
		selectBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAction();
			}
		}); 
		closeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		}); 
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		if (null == switchNodeEntity){
			return;
		}
		
		//从数据库中查询
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		
		List<RmonThing> rmonThingList = (List<RmonThing>)remoteServer.getService().findAll(RmonThing.class, where, parms);
		if (null == rmonThingList){
			return;
		}
		setValue(rmonThingList);
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(List<RmonThing> rmonThingList){
		List<List> dataList = new ArrayList<List>();
		for(int i = 0 ; i < rmonThingList.size(); i++){
			RmonThing rmonThing = rmonThingList.get(i);
			String status = dataStatus.get(rmonThing.getIssuedTag()).getKey();//状态
			
			List rowList = new ArrayList();
			rowList.add(0,rmonThing.getCode());//条目编号
			rowList.add(1,rmonThing.getDescs());//事件描述
			rowList.add(2,rmonThing.getWarningStyle());//事件类型
			rowList.add(3,rmonThing.getCommunityName());//事件团体名
			rowList.add(4, status);//状态
			rowList.add(5,rmonThing);//对象rmonThing
			
			dataList.add(rowList);
		}
		
		tableModel.setDataList(dataList);
		tableModel.fireTableDataChanged();
	}
	
	/**
	 * 保存按钮事件
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE,desc="保存事件组",role=Constants.MANAGERCODE)
	public void save(){
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return ;
		}

		if (!isValids()){
			return;
		}
		
		String itemCode = itemCodeFld.getText();
		String descrip = descripFld.getText();
		String type = typeCombox.getSelectedItem().toString();
		String communityName = comityFld.getText();
		
		RmonThing rmonThing = new RmonThing();
		rmonThing.setCode(itemCode);
		rmonThing.setDescs(descrip);
		rmonThing.setWarningStyle(type);
		rmonThing.setCommunityName(communityName);
		rmonThing.setSwitchNode(this.switchNodeEntity);
		
		List<RmonThing> rmonThings = new ArrayList<RmonThing>();
		rmonThings.add(rmonThing);
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		Task task = new SaveRequestTask(rmonThings, macValue, result);
		showConfigureMessageDialog(task, "保存");
	}
	
	private class SaveRequestTask implements Task{

		private List<RmonThing> list = null;
		private String macValue = "";
		private int result = 0;
		public SaveRequestTask(List<RmonThing> list, String macValue, int result){
			this.list = list;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getAdmService().saveAndSetting(macValue, MessageNoConstants.REMONTHINGNEW, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("保存端口告警设置");
				queryData();
				LOG.error("EventGroupView.save() error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog messageDialog;
	
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			paramConfigStrategy = new ParamConfigStrategy(operation, progressBarModel);
			messageDialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			messageDialog.setModel(progressBarModel);
			messageDialog.setStrategy(paramConfigStrategy);
			messageDialog.run(task);
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
			messageDialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			messageDialog.setModel(progressBarModel);
			messageDialog.setStrategy(uploadStrategy);
			messageDialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showUploadMessageDialog(task, operation);
				}
			});
		}
	}
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载事件组",role=Constants.MANAGERCODE)
	public void download(){
		int result = Constants.SYN_ALL;
		if (table.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要下载的数据", "提示", JOptionPane.NO_OPTION);
			return;
		}

		List<Serializable> dataList = new ArrayList<Serializable>();
		List<List> list = tableModel.getDataList();
		int selectRowCount = table.getSelectedRowCount();
		for(int i = 0 ; i < selectRowCount; i++){
			int row = table.getSelectedRows()[i];
			int modelRow = table.convertRowIndexToModel(row);
			RmonThing rmonThing = (RmonThing)list.get(modelRow).get(5);
			int issuedTag = rmonThing.getIssuedTag();
			if (issuedTag == Constants.ISSUEDDEVICE){
				JOptionPane.showMessageDialog(this, "输入的事件信息已经存在，请重新输入", "提示", JOptionPane.NO_OPTION);
				return;
			}
			
			rmonThing.setIssuedTag(Constants.ISSUEDADM);
			dataList.add(rmonThing);
		}

		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		Task task = new DownLoadRequestTask(dataList, macValue, result);
		showConfigureMessageDialog(task, "下载");
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
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.REMONTHINGNEW, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载事件组异常");
				queryData();
				LOG.error("EventGroupView.download() error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载事件组",role=Constants.MANAGERCODE)
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
		
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHEVENTGROUP);
		showUploadMessageDialog(task, "上载事件组");
	}
	
	/**
	 * 删除按钮事件
	 */
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE,desc="删除事件组",role=Constants.MANAGERCODE)
	public void delete(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		int[] rows = table.getSelectedRows();
		if (rows.length < 1){
			JOptionPane.showMessageDialog(this, "请选择要删除的行","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		//把设备侧和网管侧要删除的信息分别放到列表dataDeviceList和dataServerList中
		//当下发删除命令时，根据网管侧还是设备侧分别进行删除
		List<Serializable> dataDeviceList = new ArrayList<Serializable>();//设备侧和网管侧
		List<Serializable> dataServerList = new ArrayList<Serializable>();//网管侧
		boolean isDevice = false;
		boolean isServer = false;
		
		List<List> list = tableModel.getDataList();
		int selectRowCount = table.getSelectedRowCount();
		for(int i = 0 ; i < selectRowCount; i++){
			int row = table.getSelectedRows()[i];
			int modelRow = table.convertRowIndexToModel(row);
			RmonThing rmonThing = (RmonThing)list.get(modelRow).get(5);
			
			if(rmonThing.getIssuedTag() == Constants.ISSUEDADM){//网管侧
				isServer = true;
				dataServerList.add(rmonThing);
			}
			else{//设备侧和网管侧
				isDevice = true;
				dataDeviceList.add(rmonThing);
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
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.REMONTHINGDEL, dataServerList,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, Constants.SYN_SERVER);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除事件组异常");
					queryData();
					LOG.error("EventGroupView.delete() error:{}", e);
				}
				
				//删除设备侧
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.REMONTHINGDEL, dataDeviceList,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, Constants.SYN_ALL);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除事件组异常");
					queryData();
					LOG.error("EventGroupView.delete() error:{}", e);
				}
			}
			else if(isDevice == true && isServer == false){//只删除设备侧
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.REMONTHINGDEL, dataDeviceList,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, Constants.SYN_ALL);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除事件组异常");
					queryData();
					LOG.error("EventGroupView.delete() error:{}", e);
				}
			}
			else if(isDevice == false && isServer == true){//只删除网管侧
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.REMONTHINGDEL, dataServerList,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, Constants.SYN_SERVER);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除事件组异常");
					queryData();
					LOG.error("EventGroupView.delete() error:{}", e);
				}
				paramConfigStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			}
			queryData();
		}
	}
	
	private void selectAction(){
		RmonThing rmonThing = null;
		if (table.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择事件组","提示",JOptionPane.NO_OPTION);
			return;
		}else{
			rmonThing = (RmonThing)tableModel.getValueAt(table.getSelectedRow(), 5);
		}
		if(this.isMax){//上限
			portWarningView.setTrapNoFld(rmonThing);
		}else{//下限
			portWarningView.setTrapLowNoFld(rmonThing);
		}
		dialog.dispose();
	}
	
	/**
	 * 判断输入的是否合法
	 * @return
	 */
	private boolean isValids(){
		boolean valid = true;
		
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		
		if (null == itemCodeFld.getText() || "".equals(itemCodeFld.getText())){
			JOptionPane.showMessageDialog(this, "trap事件条目错误，范围是：1-65535","提示",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		else{
			if((NumberUtils.toLong(itemCodeFld.getText()) < 1) || (NumberUtils.toLong(itemCodeFld.getText()) > 65535)){
				JOptionPane.showMessageDialog(this, "trap事件条目错误，范围是：1-65535","提示",JOptionPane.NO_OPTION);
				valid = false;
				return valid;
			}
		}
		
		String itemCode = itemCodeFld.getText().trim(); 
		for (int i = 0 ; i < table.getRowCount(); i++){
			RmonThing rmonThingConfig = (RmonThing)tableModel.getValueAt(i, table.getColumnCount());
			if (itemCode.equals(rmonThingConfig.getCode())){
				JOptionPane.showMessageDialog(this, "条目编号重复，请重新输入" ,"提示",JOptionPane.NO_OPTION);
				valid = false;
				return valid;
			}
		}
		
		if(null == typeCombox.getSelectedItem() || "".equals(typeCombox.getSelectedItem().toString())){
			JOptionPane.showMessageDialog(this, "事件类型不能为空，请选择事件类型","提示",JOptionPane.NO_OPTION);
			valid = false;
			return valid;
		}
		
		return valid;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
	}
	
	@SuppressWarnings("unchecked")
	class EventTableModel extends AbstractTableModel{
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
		
		public List<List> getSelectedRows(int[] selectedRows)
		{
			List<List> eventLists = new ArrayList<List>();
			for(int i = 0;i < selectedRows.length;i++)
			{
				eventLists.add(this.dataList.get(selectedRows[i]));
			}
			return eventLists;
		}
	}
}
