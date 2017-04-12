package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.DELETEIP;
import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.MODIFY;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentTableModel;
import com.jhw.adm.client.model.IPDetailTableModel;
import com.jhw.adm.client.model.SyslogTableModel;
import com.jhw.adm.client.swing.HyalineDialog;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.client.views.switcher.UploadRequestTask;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SysLogHostEntity;
import com.jhw.adm.server.entity.switchs.SysLogHostToDevEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(SyslogHostView.ID)
@Scope(Scopes.DESKTOP)
public class SyslogHostView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "syslogHostView";
	
	private final JXTable hostTable = new JXTable();
	private SyslogTableModel hostModel = null;
	
	private final JXTable ipTable = new JXTable();
	private final IPDetailTableModel ipModel = new IPDetailTableModel();
	private JButton deleteIPBtn = null;
	
	private final JPanel leftPanel = new JPanel();
	private final JPanel configurePanel = new JPanel();
	private JButton addBtn = null;
	private JButton editBtn = null;
	private JButton delBtn = null;
	private final JScrollPane tablePanel = new JScrollPane();
	private final JPanel configureButtonPanel = new JPanel();
	
	private final JPanel rightPanel = new JPanel();
	private final JPanel bottomButtonPanel = new JPanel();
	private ButtonFactory buttonFactory;
	private JButton uploadBtn = null;
	private JButton downloadBtn = null;
	
	private JButton closeBtn = null;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	private static final Logger LOG = LoggerFactory.getLogger(SyslogHostView.class);
	private final ExecutorService initializeExecutorService = Executors.newSingleThreadExecutor();
	private HyalineDialog hyalineDialog;
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("sleep error", e);
			}
			initializeHostTableData();
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(ObjectMessage message) {
			initializeHostTableData();
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
			initializeHostTableData();
		}
	};
	
	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this); 
		hyalineDialog = new HyalineDialog(this);
		messageDispatcher.addProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
		
		createLeftPanel(leftPanel);
		createRightPanel(rightPanel);
		createCloseButtonPanel(bottomButtonPanel);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(7);
		splitPane.setDividerLocation(500);
		splitPane.setLeftComponent(leftPanel);
		splitPane.setRightComponent(rightPanel);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		this.setLayout(new BorderLayout());
		this.add(splitPane,BorderLayout.CENTER);
		this.add(bottomButtonPanel,BorderLayout.SOUTH);
		this.setTitle("SYSLOG主机管理");
		this.setViewSize(800, 600);
		setResource();
		initializeHostTableData();
	}
	
	private void createCloseButtonPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		
		parent.add(closeBtn);
	}

	private void createRightPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
//		parent.setBorder(BorderFactory.createTitledBorder("设备信息"));
		
		JScrollPane ipScrollPnl = new JScrollPane();
		ipTable.setModel(ipModel);
		ipTable.setEditable(false);
		ipTable.setSortable(false);
		ipTable.setAutoCreateColumnsFromModel(false);
		ipTable.setColumnModel(ipModel.getColumnModel());
		ipTable.getTableHeader().setReorderingAllowed(false);
		ipScrollPnl.getViewport().add(ipTable);
		
		JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		deleteIPBtn = buttonFactory.createButton(DELETEIP);
		deletePanel.add(deleteIPBtn);
		
		parent.add(ipScrollPnl, BorderLayout.CENTER);
		parent.add(deletePanel, BorderLayout.SOUTH);
	}

	private void createLeftPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		createConfigurePanel(configurePanel);
		createTablePanel(tablePanel);
		createConfigureButtonPanel(configureButtonPanel);
		
		parent.add(configurePanel,BorderLayout.NORTH);
		parent.add(tablePanel,BorderLayout.CENTER);
		parent.add(configureButtonPanel, BorderLayout.SOUTH);
	}

	private void createConfigureButtonPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.TRAILING));

		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		
		parent.add(uploadBtn);
		parent.add(downloadBtn);
	}

	private void createTablePanel(JScrollPane parent) {
		hostModel = new SyslogTableModel();
		hostTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hostTable.setModel(hostModel);
		hostTable.setEditable(false);
		hostTable.setSortable(false);
		hostTable.setAutoCreateColumnsFromModel(false);
		hostTable.setColumnModel(hostModel.getColumnModel());
		hostTable.getTableHeader().setReorderingAllowed(false);

		parent.getViewport().add(hostTable);
	}

	private void createConfigurePanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.TRAILING));

		addBtn = buttonFactory.createButton(APPEND);
		editBtn= buttonFactory.createButton(MODIFY);
		delBtn = buttonFactory.createButton(DELETE);
		parent.add(addBtn);
		parent.add(editBtn);
		parent.add(delBtn);
	}

	private void setResource(){
		hostTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (hostTable.getSelectedRow() > -1 && e.getValueIsAdjusting()) {
					hostTableAction();
				}
			}
		});
	}
	
	private void hostTableAction(){
		final Runnable queryAndSetIPTableInfomationThread = new Runnable() {
			@Override
			public void run() {
				queryAndSetIPTableInfomation();
			}
		};
		hyalineDialog.run(queryAndSetIPTableInfomationThread);
	}
	
	private void queryAndSetIPTableInfomation(){
		int row  = hostTable.getSelectedRow();
//		if (row < 0){
////			setConfigBtnEnabled(false);
//			return;
//		}
//		setConfigBtnEnabled(true);
		
		SysLogHostEntity sysLogHostEntity = hostModel.getValue(row);
		List<SysLogHostToDevEntity> nodeEntityList = remoteServer.getNmsService().getSysLogHostToDevBySysLog(sysLogHostEntity);
		if(null == nodeEntityList){
			nodeEntityList = new ArrayList<SysLogHostToDevEntity>();
		}
		fireIPTableDataChange(nodeEntityList);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}
	
	private void fireIPTableDataChange(final List<SysLogHostToDevEntity> nodeEntityList){
		if(SwingUtilities.isEventDispatchThread()){
			ipModel.setNodeEntity(nodeEntityList);
			ipModel.fireTableDataChanged();
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireIPTableDataChange(nodeEntityList);
				}
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		fireIPTableDataChange(new ArrayList<SysLogHostToDevEntity>());
		List<SysLogHostEntity> hostEntityList = (List<SysLogHostEntity>) remoteServer.getService().findAll(SysLogHostEntity.class);
		if(null == hostEntityList){
			hostEntityList = new ArrayList<SysLogHostEntity>();
		}
		fireHostTableDataChange(hostEntityList);
	}
	
	private void fireHostTableDataChange(final List<SysLogHostEntity> hostEntityList){
		if(SwingUtilities.isEventDispatchThread()){
			hostModel.setSysLogHosts(hostEntityList);
			hostModel.fireTableDataChanged();
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireHostTableDataChange(hostEntityList);
				}
			});
		}
	}
	
	private void initializeHostTableData(){
		final Runnable initializeHostTableThread = new Runnable() {
			@Override
			public void run() {
				try{
					queryData();
				}catch(Exception e){
					LOG.error("SyslogHostView.initializeData() error", e);
				}
			}
		};
		initializeExecutorService.execute(initializeHostTableThread);
	}
	
	/**
	 * 添加操作
	 */
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND, desc="添加Syslog主机信息",role=Constants.MANAGERCODE)
	public void append(){
		ConfigureHostDialog dialog = new ConfigureHostDialog(this,null);
		dialog.openDialog();
		
	}
	
	private class RequestTask implements Task{
		
		private SysLogHostEntity sysLogHostEntity = null;
		private List<SysLogHostToDevEntity> list = null;
		public RequestTask(List<SysLogHostToDevEntity> list, SysLogHostEntity sysLogHostEntity){
			this.list = list;
			this.sysLogHostEntity = sysLogHostEntity;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getNmsService().saveSysLogHosts(list, sysLogHostEntity);
			}catch(Exception e){
				strategy.showErrorMessage("保存网管侧异常");
				LOG.error("SysLogHostEntity.append() error", e);
			}
			strategy.showNormalMessage("保存网管侧成功");
			initializeHostTableData();
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private ParamConfigStrategy paramConfigStrategy;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			hostTable.clearSelection();
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
	
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			hostTable.clearSelection();
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
			hostTable.clearSelection();
			progressBarModel = new JProgressBarModel();
			uploadStrategy = new ParamUploadStrategy(operation, progressBarModel,false);
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

	@ViewAction(name=MODIFY, icon=ButtonConstants.MODIFY, desc="编辑Syslog主机信息",role=Constants.MANAGERCODE)
	public void modify(){
		int row = hostTable.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(this, "请选择需要修改的SYSLOG主机", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		SysLogHostEntity sysLogHostEntity = hostModel.getValue(row);
		ConfigureHostDialog dialog = new ConfigureHostDialog(this,sysLogHostEntity);
		dialog.openDialog();
	}
	
//	/**
//	 * 配置操作
//	 */
//	@ViewAction(name=CONFIG, icon=ButtonConstants.MODIFY, desc="配置Syslog主机信息",role=Constants.MANAGERCODE)
//	public void config(){
//		List<String> ipList = ipModel.getIPList();
//		List<SwitchTopoNodeEntity> swticTopoNodeEntities = remoteServer
//				.getNmsService().getSwitchTopoNodeByIps(ipList);
//		showEventDialog(swticTopoNodeEntities);
//	}
//	
//	private void showEventDialog(List<SwitchTopoNodeEntity> list){
//		JDialog dialog = new JDialog(ClientUtils.getRootFrame(), "设备信息配置", true);
//		SwitchConfigView viewPart = new SwitchConfigView(this,dialog,imageRegistry,equipmentModel);
//		viewPart.setAllNodesData(list);
//		
//		dialog.getContentPane().add(viewPart);
//		dialog.setSize(new Dimension(500,400));
//		
//		dialog.setModal(true);
//		dialog.setVisible(true);
//		dialog.setResizable(false);
//		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//		dialog.setIconImage(ClientUtils.getRootFrame().getIconImage());
//		dialog.setLocationRelativeTo(this);
//	}
	
	/**
	 * 保存操作
	 */
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载Syslog主机信息",role=Constants.MANAGERCODE)
	public void download(){
		int row = hostTable.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(this, "请选择需要下载的SYSLOG主机", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
//		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
//		if (result == 0){
//			return ;
//		}
//		hostTable.clearSelection();
		
		List<SysLogHostToDevEntity> equipmentList = ipModel.getNodeEntityList();
		if(equipmentList.size() == 0){
			JOptionPane.showMessageDialog(this, "请给该主机分配设备后再下载", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
//		List<SysLogHostToDevEntity> entityList = ipModel.getNodeEntityList();
		Task task = new DownLoadRequestTask(equipmentList, Constants.SYN_ALL);
		showConfigureMessageDialog(task, "下载SYSLOG主机");
	}
	
	private class DownLoadRequestTask implements Task{

		private List<SysLogHostToDevEntity> entityList = null;
		private int result = 0;
		public DownLoadRequestTask(List<SysLogHostToDevEntity> entityList, int result){
			this.entityList = entityList;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getNmsService().configSyslogHost(entityList,clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),this.result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载SYSLOG主机异常");
				LOG.error("SyslogHostView.DownLoadRequestTask error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				initializeHostTableData();
			}
		}
	}
	
	/**
	 * 删除操作
	 */
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="删除Syslog主机信息",role=Constants.MANAGERCODE)
	public void delete(){
		int row = hostTable.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(this, "请选择需要删除的SYSLOG主机", "提示", JOptionPane.NO_OPTION);
			return;
		}

		List<SysLogHostToDevEntity> nodeEntityList = ipModel.getNodeEntityList();
		if(nodeEntityList.size() > 0){
			JOptionPane.showMessageDialog(this, "该SYSLOG主机正在使用，不允许删除", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int backSelected = JOptionPane.showConfirmDialog(this, "你确定删除所选择的SYSLOG主机", "提示", JOptionPane.OK_CANCEL_OPTION);
		if(backSelected != JOptionPane.OK_OPTION){
			return;
		}

		SysLogHostEntity sysLogHostEntity = hostModel.getValue(row);
		Task task = new DeleteRequestTask(sysLogHostEntity);
		showMessageDialog(task, "删除SYSLOG主机");
	}
	
	private class DeleteRequestTask implements Task{

		private SysLogHostEntity sysLogHostEntity = null;
		public DeleteRequestTask(SysLogHostEntity sysLogHostEntity){
			this.sysLogHostEntity = sysLogHostEntity;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().deleteEntity(sysLogHostEntity);
			}catch(Exception e){
				strategy.showErrorMessage("删除SYSLOG主机异常");
				LOG.error("", e);
			}
			strategy.showNormalMessage("删除SYSLOG主机成功");
			initializeHostTableData();
		}
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载Syslog主机信息",role=Constants.MANAGERCODE)
	public void upload(){
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		List<SwitchTopoNodeEntity> switchTopoNodeEntities = (List<SwitchTopoNodeEntity>) remoteServer
													.getService().findAll(SwitchTopoNodeEntity.class);
		for(SwitchTopoNodeEntity switchTopoNodeEntity : switchTopoNodeEntities){
			SwitchNodeEntity switchNodeEntity = remoteServer.getService()
				.getSwitchByIp(switchTopoNodeEntity.getIpValue());
			
			SynchDevice synchDevice = new SynchDevice();
			synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
			synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
			synDeviceList.add(synchDevice);
		}
		
//		hostTable.clearSelection();
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESYSLOGHOST);
		showUploadMessageDialog(task, "上载SYSLOG主机");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initializeHostTableData();
	}
	
	@ViewAction(name=DELETEIP, icon=ButtonConstants.DELETE, desc="删除Syslog主机对应的设备信息",role=Constants.MANAGERCODE)
	public void deleteIP(){
		int rows = ipTable.getSelectedRowCount();
		if(rows <= 0){
			JOptionPane.showMessageDialog(this, "请选择要删除的记录", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int backSelected = JOptionPane.showConfirmDialog(this, "你确定删除所选择的设备", "提示", JOptionPane.OK_CANCEL_OPTION);
		if(backSelected != JOptionPane.OK_OPTION){
			return;
		}
		
		List<SysLogHostToDevEntity> selectedEntityList = ipModel.getSelectedEntityList(ipTable.getSelectedRows());
		Task task = new DeleteIPRequestTask(selectedEntityList);
		showConfigureMessageDialog(task, "删除");
	}
	
	private class DeleteIPRequestTask implements Task{

		private List<SysLogHostToDevEntity> list = null;
		public DeleteIPRequestTask(List<SysLogHostToDevEntity> list){
			this.list = list;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getNmsService().deleteSysLogToDevEntity(
						list, clientModel.getCurrentUser().getUserName(),
						clientModel.getLocalAddress(), Constants.SYN_ALL);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("删除");
				LOG.error("SyslogHostView.DeleteIPRequestTask", e);
			}
		}
	}

	public JButton getCloseButton(){
		return this.closeBtn;
	}

	@Override
	public void dispose() {
		super.dispose();
		hyalineDialog.dispose();
		messageDispatcher.removeProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
	}
	
	private class ConfigureHostDialog extends JDialog{
		
		private static final long serialVersionUID = 1L;
		private final JLabel hostIpLbl = new JLabel();
		private final IpAddressField hostIpFld = new IpAddressField();
		
		private final JLabel portLbl = new JLabel();
		private final NumberField portFld = new NumberField();
		
		private final JLabel hostIDLbl = new JLabel();
		private final JComboBox hostIDComboBox = new JComboBox();

		private final JXTable table = new JXTable();
		private final EquipmentTableModel model = new EquipmentTableModel();
		
		private final String[] HOSTID = {"1","2","3","4"};
		private SysLogHostEntity hostEntity = null;
		private ExecutorService configureExecutorService = null;
		
		public ConfigureHostDialog(ViewPart viewPart,SysLogHostEntity hostEntity){
			super(ClientUtils.getRootFrame());
			this.hostEntity = hostEntity;
			this.configureExecutorService = Executors.newSingleThreadExecutor();
			this.createCenterPanel(this);
			this.setModal(true);
			this.setResizable(false);
			this.setSize(500,400);
			this.setTitle("配置SYSLOG主机");
//			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			this.setIconImage(ClientUtils.getRootFrame().getIconImage());
			this.setLocationRelativeTo(viewPart);
		}

		private void createCenterPanel(JDialog parent) {
			parent.setLayout(new BorderLayout());
			
			JPanel panel = new JPanel();
			createConfigurePanel(panel);

			JScrollPane scrollPane = new JScrollPane();
			createTablePanel(scrollPane);
			
			JPanel buttonPanel = new JPanel();
			createButtonPanel(buttonPanel);
			
			parent.add(panel, BorderLayout.NORTH);
			parent.add(scrollPane, BorderLayout.CENTER);
			parent.add(buttonPanel, BorderLayout.SOUTH);
			setDialogResource();
			initializeValue();
		}

		@SuppressWarnings("unchecked")
		private void initializeValue() {
			if(null == this.hostEntity){
				
			}else{
				hostIpFld.setText(this.hostEntity.getHostIp());
				portFld.setText(String.valueOf(this.hostEntity.getHostPort()));
				hostIpFld.setEditable(false);
//				portFld.setEditable(false);
			}
			
			configureExecutorService.execute(new Runnable() {
				@Override
				public void run() {
					List<SwitchTopoNodeEntity> list = (List<SwitchTopoNodeEntity>) remoteServer
							.getService().findAll(SwitchTopoNodeEntity.class);
					List<NodeEntity> nodeEntityList = new ArrayList<NodeEntity>();
					List<SwitchTopoNodeEntity> afterSortNodeEntity = new ArrayList<SwitchTopoNodeEntity>();
					for(SwitchTopoNodeEntity switchTopoNodeEntity : list){
						nodeEntityList.add(switchTopoNodeEntity);
					}
					nodeEntityList = NodeUtils.sortNodeEntity(nodeEntityList);
					for(NodeEntity nodeEntity : nodeEntityList){
						afterSortNodeEntity.add((SwitchTopoNodeEntity)nodeEntity);
					}
					fireTableDataChange(afterSortNodeEntity);
				}
			});
		}

		private void fireTableDataChange(final List<SwitchTopoNodeEntity> list) {
			if(SwingUtilities.isEventDispatchThread()){
				model.setNodeEntity(list);
				model.fireTableDataChanged();
			}else{
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fireTableDataChange(list);
					}
				});
			}
		}

		private void setDialogResource() {
			hostIpLbl.setText("主机IP");
			portLbl.setText("端口");
			hostIDLbl.setText("HOST ID");

			hostIpFld.setColumns(25);
			portFld.setColumns(25);
			portFld.setText("514");
			portFld.setEditable(false);
			
			for(String hostId : HOSTID){
				hostIDComboBox.addItem(hostId);
			}
			hostIDComboBox.setSelectedIndex(0);
		}

		private void createConfigurePanel(JPanel parent) {
			parent.setLayout(new FlowLayout(FlowLayout.LEADING));
			
			JPanel detailPanel = new JPanel(new GridBagLayout());
			detailPanel.add(hostIpLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
			detailPanel.add(hostIpFld,new GridBagConstraints(1,0,2,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,50,0,0),0,0));
			detailPanel.add(new StarLabel(),new GridBagConstraints(3,0,2,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
			
			detailPanel.add(portLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
			detailPanel.add(portFld,new GridBagConstraints(1,1,2,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,50,0,0),0,0));

			detailPanel.add(hostIDLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
			detailPanel.add(hostIDComboBox,new GridBagConstraints(1,2,2,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,50,0,0),0,0));
			parent.add(detailPanel);
		}

		private void createTablePanel(JScrollPane parent) {
			table.setModel(model);
			table.setEditable(false);
			table.setSortable(false);
			table.setAutoCreateColumnsFromModel(false);
			table.setColumnModel(model.getColumnModel());
			table.getTableHeader().setReorderingAllowed(false);
			parent.getViewport().add(table);
		}
		
		private void createButtonPanel(JPanel parent) {
			parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
			JButton okButton = new JButton(new OkAction("确定"));
			okButton.setIcon(imageRegistry.getImageIcon(ButtonConstants.SAVE));
			JButton closeButton = new JButton(new CloseAction("关闭"));
			closeButton.setIcon(imageRegistry.getImageIcon(ButtonConstants.CLOSE));
			
			parent.add(okButton);
			parent.add(closeButton);
		}
		
		private void openDialog(){
			this.setVisible(true);
		}
		
		private void closeDialog(){
			super.dispose();
			this.setVisible(false);
		}
		
		private class OkAction extends AbstractAction{

			private static final long serialVersionUID = 1L;
			public OkAction(String name){
				super(name);
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				int column = table.getSelectedColumn();
				if(row == -1 || column == -1){
					JOptionPane.showMessageDialog(null, "请选择需要添加的SYSLOG主机", "提示", JOptionPane.NO_OPTION);
					return;
				}
				List<SwitchTopoNodeEntity> topoNodeEntityList = model
						.getSelectedNodeEntity(table.getSelectedRows());
				List<SysLogHostToDevEntity> newEntityList = new ArrayList<SysLogHostToDevEntity>();
				int hostId = NumberUtils.toInt(hostIDComboBox.getSelectedItem().toString());
				if(null == hostEntity){//append
					if(!isValids()){
						return;
					}

					SysLogHostEntity sysLogHostEntity = new SysLogHostEntity();
					sysLogHostEntity.setHostIp(hostIpFld.getText().trim());
					sysLogHostEntity.setHostPort(NumberUtils.toInt(portFld.getText().trim()));
					
					for(SwitchTopoNodeEntity switchTopoNodeEntity : topoNodeEntityList){
						SysLogHostToDevEntity sysLogHostToDevEntity = new SysLogHostToDevEntity();
						sysLogHostToDevEntity.setIpValue(switchTopoNodeEntity.getIpValue());
						sysLogHostToDevEntity.setSysLogHostEntity(sysLogHostEntity);
						sysLogHostToDevEntity.setHostID(hostId);
						sysLogHostToDevEntity.setHostMode(1);
						sysLogHostToDevEntity.setIssuedTag(0);
						newEntityList.add(sysLogHostToDevEntity);
					}
					Task task = new RequestTask(newEntityList, sysLogHostEntity);
					showMessageDialog(task, "保存");
				}else{//modify
					
					List<SysLogHostToDevEntity> oldEneityList = ipModel.getNodeEntityList();
					for(SwitchTopoNodeEntity switchTopoNodeEntity : topoNodeEntityList){
						SysLogHostToDevEntity sysLogHostToDevEntity = new SysLogHostToDevEntity();
						sysLogHostToDevEntity.setIpValue(switchTopoNodeEntity.getIpValue());
						sysLogHostToDevEntity.setSysLogHostEntity(hostEntity);
						sysLogHostToDevEntity.setHostID(hostId);
						sysLogHostToDevEntity.setHostMode(1);
						sysLogHostToDevEntity.setIssuedTag(0);
						newEntityList.add(sysLogHostToDevEntity);
					}
					
					List<SysLogHostToDevEntity> tempList = new ArrayList<SysLogHostToDevEntity>();
					//过滤掉相同IP以及相同ID的记录
					for(SysLogHostToDevEntity newEntity : newEntityList){
						if(oldEneityList.size() > 0){
							boolean isInclude = false;
							for(SysLogHostToDevEntity oldEntity : oldEneityList){
								if ((newEntity.getHostID() == oldEntity.getHostID())
										&& (newEntity.getIpValue().equals(oldEntity.getIpValue()))) {
									isInclude = true;
									break;
								}
							}
							if(!isInclude){
								tempList.add(newEntity);
							}
						}else{
							tempList.add(newEntity);
						}
					}
					oldEneityList.addAll(tempList);
					Task task = new RequestTask(oldEneityList, hostEntity);
					showMessageDialog(task, "保存");
				}
				closeDialog();
			}
		}
		
		private boolean isValids() {
			boolean isValid = true;

			String ip = hostIpFld.getText().trim();
			int port = NumberUtils.toInt(portFld.getText().trim());
			if (StringUtils.isBlank(ip)) {
				JOptionPane.showMessageDialog(this, "主机IP不能为空，请输入主机IP", "提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			} else if (ClientUtils.isIllegal(ip)) {
				JOptionPane.showMessageDialog(this, "主机IP非法，请重新输入", "提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
			for (int i = 0; i < hostTable.getRowCount(); i++) {
				SysLogHostEntity sysLogHostEntity = hostModel.getValue(i);
				if ((ip.equals(sysLogHostEntity.getHostIp())) && (port == sysLogHostEntity.getHostPort())) {
					JOptionPane.showMessageDialog(this, "该主机以及端口已经存在，请重新输入", "提示",JOptionPane.NO_OPTION);
					isValid = false;
					return isValid;
				}
			}
			return isValid;
		}
		
		private class CloseAction extends AbstractAction{

			private static final long serialVersionUID = 1L;
			public CloseAction(String name){
				super(name);
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						closeDialog();
					}
				});
			}
		}
	}
}
