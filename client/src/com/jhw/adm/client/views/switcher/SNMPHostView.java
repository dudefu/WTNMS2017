package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.CONFIG;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.jhw.adm.client.model.CommonTableModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.SwitchRefreshInfoInter;
import com.jhw.adm.client.snmp.SnmpData;
import com.jhw.adm.client.swing.CommonTable;
import com.jhw.adm.client.swing.HyalineDialog;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SNMPHost;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.SNMPHostBean;
import com.jhw.adm.server.entity.util.SNMPSwitchIPBean;

@Component(SNMPHostView.ID)
@Scope(Scopes.DESKTOP)
public class SNMPHostView extends ViewPart implements SwitchRefreshInfoInter{
	private static final long serialVersionUID = 1L;
	public static final String ID = "snmpHostView";
	
	private final JPanel topPnl = new JPanel();
	private final JLabel hostIpLbl = new JLabel();
	private final IpAddressField hostIpFld = new IpAddressField();
	
	private final JLabel versionLbl = new JLabel();
	private final JComboBox versionCombox = new JComboBox();
	private static final String[] VERSIONLIST = {"V1","V2c"};
	
	private final JLabel comityLbl = new JLabel();
	private final JComboBox comityCombox = new JComboBox();
	private static final String[] COMITYLIST = {"public","private"};
	private static final String COMITY_RANGE = "范围:1-32个字符";
	
	private final JScrollPane scrollPnl = new JScrollPane();
	private final CommonTable table = new CommonTable();
	private SNMPTableModel model = null;
	
	private final JPanel rightPnl = new JPanel();
	private final JTable switchTable = new JTable();
	private final String[] SWITCH_COLUMN_NAME = {"设备IP"};
	
	//下端的按钮面板
	private ButtonFactory buttonFactory;
	private final JPanel bottomPnl = new JPanel();
	private JButton delBtn;
	private JButton closeBtn;
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton addBtn;
	private JButton configBtn;	
	private HyalineDialog hyalineDialog;
	
	private static final String[] COLUMN_NAME = {"主机IP","SNMP版本","团体名","状态"};
	
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
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Autowired
	@Qualifier(CommonTableModel.ID)
	private CommonTableModel commonTableModel;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	private static final Logger LOG = LoggerFactory.getLogger(SNMPHostView.class);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LOG.error("sleep error", e);
			}
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(ObjectMessage message) {
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
		messageDispatcher.addProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
		initLeftComponents();
		initRightPnl();
		initBottomPnl();
		
		JPanel leftPnl = new JPanel(new BorderLayout());
		
		leftPnl.add(topPnl,BorderLayout.NORTH);
		leftPnl.add(scrollPnl,BorderLayout.CENTER);
		leftPnl.add(buttonPanel, BorderLayout.SOUTH);
		 
		JSplitPane splitPnl = new JSplitPane();
		splitPnl.setDividerSize(7);
		splitPnl.setDividerLocation(500);
		splitPnl.setLeftComponent(leftPnl);
		splitPnl.setRightComponent(rightPnl);
		splitPnl.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		this.setLayout(new BorderLayout());
		this.add(splitPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(800, 600);
		this.setTitle("SNMP主机管理");
		setResource();
		hyalineDialog = new HyalineDialog(this);
	}
	
	private final JPanel buttonPanel = new JPanel();
	private void initButtonPnl(){
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		configBtn = buttonFactory.createButton(CONFIG);
		
		buttonPanel.add(uploadBtn);
		buttonPanel.add(downloadBtn);
		buttonPanel.add(configBtn);
	}
	
	private void initRightPnl(){
		commonTableModel.setColumnName(SWITCH_COLUMN_NAME);
		switchTable.setModel(commonTableModel);
		JScrollPane switchScrollPnl = new JScrollPane();
		switchScrollPnl.getViewport().add(switchTable);
		
		rightPnl.setLayout(new BorderLayout());
		rightPnl.add(switchScrollPnl, BorderLayout.CENTER);
		rightPnl.setBorder(BorderFactory.createTitledBorder("设备信息"));
	}
	
	private void initBottomPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		panel.add(closeBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.add(panel);
	}
	
	private void initLeftComponents(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(hostIpLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		panel.add(hostIpFld,new GridBagConstraints(1,0,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,50,0,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(3,0,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		
		panel.add(versionLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		panel.add(versionCombox,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,50,0,0),0,0));
		
		panel.add(comityLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		panel.add(comityCombox,new GridBagConstraints(1,2,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,50,0,0),0,0));
		panel.add(new StarLabel(""),new GridBagConstraints(3,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		
		addBtn = buttonFactory.createButton(APPEND);
		delBtn = buttonFactory.createButton(DELETE);
		JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPnl.add(addBtn);
		btnPnl.add(delBtn);
		
		JPanel configPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		configPnl.add(panel);
		
		topPnl.setLayout(new BorderLayout());
		topPnl.add(configPnl,BorderLayout.CENTER);
		topPnl.add(btnPnl,BorderLayout.SOUTH);
		
		model = new SNMPTableModel();
		model.setColumnName(COLUMN_NAME);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(model);
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		scrollPnl.getViewport().add(table);
		
		initButtonPnl();
	}
	
	private void setResource(){
		hostIpFld.setColumns(25);
		comityCombox.setPreferredSize(new Dimension(100,comityCombox.getPreferredSize().height));
		for (int i = 0 ; i < COMITYLIST.length ; i++){
			comityCombox.addItem(COMITYLIST[i]);
		}
		versionCombox.setPreferredSize(new Dimension(100,versionCombox.getPreferredSize().height));
		
		for (int i = 0 ; i < VERSIONLIST.length ; i++){
			versionCombox.addItem(VERSIONLIST[i]);
		}
		
		hostIpLbl.setText("主机IP");
		versionLbl.setText("版本");
		comityLbl.setText("团体名");
//		comityRangeLbl.setText("(范围:1-32个字符)");
		//comityFld.setDocument(new TextFieldPlainDocument(comityFld, 32));
		
		configBtn.setEnabled(false);
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (table.getSelectedRow() > -1 && e.getValueIsAdjusting()) {
					tableSelectAction();
				}
			}
		});
	}
	
	private void tableSelectAction(){		
		final Runnable queryAndSetEquipmentInformationThread = new Runnable() {
			
			@Override
			public void run() {
				queryAndSetEquipmentInformation();
			}
		};
		
		hyalineDialog.run(queryAndSetEquipmentInformationThread);
	}
	
	@SuppressWarnings("unchecked")
	private void queryAndSetEquipmentInformation(){
		int row  = table.getSelectedRow();
		if (row < 0){
			configBtn.setEnabled(false);
			return;
		}
		configBtn.setEnabled(true);
		int modelRow = table.convertRowIndexToModel(row);
		
		final SNMPHostBean snmpHostBean = (SNMPHostBean)model.getValueAt(modelRow, table.getColumnCount());
		
		List<SNMPSwitchIPBean> snmpSwitchIPBeanList = remoteServer.getNmsService().querySwitchIPBeans(snmpHostBean); 
		if(null == snmpSwitchIPBeanList || snmpSwitchIPBeanList.size() == 0){
			setCommonTableValue(null);
			return;
		}
		
		List<List> dataList = new ArrayList<List>();
		for(SNMPSwitchIPBean snmpSwitchIPBean : snmpSwitchIPBeanList){
			String ip = snmpSwitchIPBean.getIpValue();
			
			SwitchBaseConfig switchBaseConfig = new SwitchBaseConfig();
			switchBaseConfig.setIpValue(ip);
			SwitchNodeEntity switchNodeEntity = new SwitchNodeEntity();
			switchNodeEntity.setBaseConfig(switchBaseConfig);
			SwitchTopoNodeEntity switchTopoNodeEntity = new SwitchTopoNodeEntity();
			switchTopoNodeEntity.setNodeEntity(switchNodeEntity);
			switchTopoNodeEntity.setIpValue(ip);
			
			List rowList = new ArrayList();
			rowList.add(ip);
			rowList.add(switchTopoNodeEntity);
			dataList.add(rowList);
		}
		setCommonTableValue(dataList);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}
	
	private void setCommonTableValue(final List<List> dataList){
		if(SwingUtilities.isEventDispatchThread()){
			commonTableModel.setDataList(dataList);
			commonTableModel.fireTableDataChanged();
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setCommonTableValue(dataList);
				}
			});
		}
	}
	
	/**
	 * 查询数据库中的值
	 */
	private void queryData(){
		setCommonTableValue(null);
		List<SNMPHostBean> snmpHostList = remoteServer.getNmsService().queryAllSNMPHostBean();
		setValue(snmpHostList);
	}
	
	/**
	 * 根据查询得到的值设置到视图的控件中
	 * @param snmpGroupList
	 */
	@SuppressWarnings("unchecked")
	private void setValue(List<SNMPHostBean> snmpHostList){
		List<List> dataList = new ArrayList<List>();
		if (null == snmpHostList){
			model.setDataList(dataList);
			model.fireTableDataChanged();
			return;
		}
		int sum = snmpHostList.size();
		for (int i = 0 ; i < sum; i++){
			SNMPHostBean snmpHostBean = snmpHostList.get(i);
			String hostIp = snmpHostBean.getHostIp();
			String version = snmpHostBean.getSnmpVersion();
			String comityName = snmpHostBean.getMassName();
			String status = dataStatus.get(snmpHostBean.getIssuedTag()).getKey();
			
			List rowList = new ArrayList();
			rowList.add(0,hostIp);
			rowList.add(1,version);
			rowList.add(2,comityName);
			rowList.add(3,status);
			rowList.add(4,snmpHostBean);
			
			dataList.add(rowList);
		}
		
		model.setDataList(dataList);
		model.fireTableDataChanged();
	}
	
	/**
	 * 添加操作
	 */
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND, desc="添加SNMP主机信息",role=Constants.MANAGERCODE)
	public void append(){
		if(!isValids()){
			return;
	 	}
		
		//组合SNMPHost
		String hostIp = hostIpFld.getText();
		String version = versionCombox.getSelectedItem().toString();
		String comityName = comityCombox.getSelectedItem().toString();
		
		SNMPHost snmpHost = new SNMPHost();
		snmpHost.setHostIp(hostIp);
		snmpHost.setSnmpVersion(version);
		snmpHost.setMassName(comityName.toLowerCase());
		
		Task task = new RequestTask(snmpHost);
		showMessageDialog(task, "添加");
	}
	
	private class RequestTask implements Task{
		
		private SNMPHost snmpHost = null;
		public RequestTask(SNMPHost snmpHost){
			this.snmpHost = snmpHost;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().saveEntity(snmpHost);
			}catch(Exception e){
				strategy.showErrorMessage("保存网管侧异常");
				queryData();
				clear();
				LOG.error("SNMPHostView.append() error", e);
			}
			strategy.showNormalMessage("保存网管侧成功");
			queryData();
			clear();
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private ParamConfigStrategy paramConfigStrategy;
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
	
	/**
	 * 配置操作
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=CONFIG, icon=ButtonConstants.MODIFY, desc="配置SNMP主机信息",role=Constants.MANAGERCODE)
	public void config(){
		List<SwitchTopoNodeEntity> list = new ArrayList<SwitchTopoNodeEntity>();
		
		List<List> dataList = commonTableModel.getDataList();
		for (List rowList : dataList){
			SwitchTopoNodeEntity switchTopoNodeEntity  = (SwitchTopoNodeEntity)rowList.get(1);
			
			list.add(switchTopoNodeEntity);
		}
		
		showEventDialog(list);
	}
	
	private void showEventDialog(List<SwitchTopoNodeEntity> list){
		JDialog dialog = new JDialog(ClientUtils.getRootFrame(), "设备信息配置", true);
		SwitchConfigView viewPart = new SwitchConfigView(this,dialog,imageRegistry,equipmentModel);
		viewPart.setAllNodesData(list);
		
		dialog.getContentPane().add(viewPart);
		dialog.setSize(new Dimension(505,400));
		
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(viewPart);
		dialog.setResizable(false);
		dialog.setVisible(true);
	}
	
	/**
	 * 保存操作
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载SNMP主机信息",role=Constants.MANAGERCODE)
	public void download(){
		if (table.getSelectedRow() < 0){
			JOptionPane.showMessageDialog(this, "请选择SNMP主机信息", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int row1 = table.getSelectedRow();
		int column = table.getSelectedColumn();
		String status = (String) table.getValueAt(row1, column);
		List<List> ipList = commonTableModel.getDataList();
		if(ipList.size() == 0 && "网管侧".equals(status)){
			JOptionPane.showMessageDialog(this, "请先配置设备IP", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		int row = table.convertRowIndexToModel(table.getSelectedRow());
		SNMPHostBean snmpHostBean = (SNMPHostBean)model.getValueAt(row, model.getColumnCount());
		
		List<SNMPHost> snmpHostList = new ArrayList<SNMPHost>();
		List<List> dataList = commonTableModel.getDataList();

		for(List rowList : dataList){
			SwitchTopoNodeEntity switchTopoNodeEntity = (SwitchTopoNodeEntity)rowList.get(1);
			SwitchNodeEntity switchNodeEntity = remoteServer.getService().getSwitchByIp(switchTopoNodeEntity.getIpValue());
			SNMPHost snmpHost = new SNMPHost();
			snmpHost.setHostIp(snmpHostBean.getHostIp());
			snmpHost.setSnmpVersion(snmpHostBean.getSnmpVersion());
			snmpHost.setMassName(snmpHostBean.getMassName());
			snmpHost.setSwitchNode(switchNodeEntity);
			snmpHost.setIssuedTag(Constants.ISSUEDADM);
			
			snmpHostList.add(snmpHost);
		}

		Task task = new DownLoadRequestTask(snmpHostList, snmpHostBean, result);
		showConfigureMessageDialog(task, "下载SNMP主机");
	}
	
	private class DownLoadRequestTask implements Task{

		private List<SNMPHost> snmpHostList = null;
		private SNMPHostBean snmpHostBean = null;
		private int result = 0;
		public DownLoadRequestTask(List<SNMPHost> snmpHostList, SNMPHostBean snmpHostBean, int result){
			this.snmpHostList = snmpHostList;
			this.snmpHostBean = snmpHostBean;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getNmsService().saveSNMPHost(snmpHostList, clientModel.getLocalAddress()
						,snmpHostBean,result,clientModel.getCurrentUser().getUserName());
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载SNMP主机异常");
				queryData();
				LOG.error("SNMPHostView.DownLoadRequestTask error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	/**
	 * 删除操作
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="删除SNMP主机信息",role=Constants.MANAGERCODE)
	public void delete(){
		int row  = table.getSelectedRow();
		if (row < 0){
			JOptionPane.showMessageDialog(this, "请选择需要删除的主机", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		List<List> ipList = commonTableModel.getDataList();
		if(null != ipList && ipList.size() > 0){
			JOptionPane.showMessageDialog(this, "请先删除所属的设备IP", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int column = table.getSelectedColumn();
		String status = (String) table.getValueAt(row, column);
		
		if("设备侧".equals(status)){
			JOptionPane.showMessageDialog(this, "请下载后再删除", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		
		int modelRow = table.convertRowIndexToModel(row);
		
		SNMPHostBean snmpHostBean = (SNMPHostBean)model.getValueAt(modelRow, table.getColumnCount());
		
		Object[] params = {snmpHostBean.getHostIp(),snmpHostBean.getSnmpVersion(),snmpHostBean.getMassName()};
		
		List<SNMPHost> list = (List<SNMPHost>) remoteServer.getService().findAll(SNMPHost.class,
						" where entity.hostIp=? and entity.snmpVersion=? and entity.massName=?",params);
		
		Task task = new DeleteRequestTask(list);
		showMessageDialog(task, "删除SNMP主机");
	}
	
	private class DeleteRequestTask implements Task{

		private List<SNMPHost> list = null;
		public DeleteRequestTask(List<SNMPHost> list){
			this.list = list;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().deleteEntities(list);
			}catch(Exception e){
				strategy.showErrorMessage("删除SNMP主机异常");
				clear();
				queryData();
				LOG.error("", e);
			}
			strategy.showNormalMessage("删除SNMP主机成功");
			clear();
			queryData();
		}
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载SNMP主机信息",role=Constants.MANAGERCODE)
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
		
		Task task = new UploadRequestTask(synDeviceList,MessageNoConstants.SINGLESWITCHSNMPHOST);
		showUploadMessageDialog(task, "上载SNMP主机");
	}
	
	private void clear(){
		hostIpFld.setIpAddress("");
		versionCombox.setSelectedIndex(0);
		comityCombox.setSelectedIndex(0);
	}

	/**
	 * 设备浏览器监听事件
	 * @author Administrator
	 */
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			model.setDataList(null);
			model.fireTableDataChanged();
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
		}		
	};
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	public JButton getDelButton(){
		return this.delBtn;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		hyalineDialog.dispose();
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
	}
	
	//Amend 2010.06.04
	public boolean isValids(){
		boolean isValid = true;
		
		if(null == hostIpFld.getText() || "".equals(hostIpFld.getText())){
			JOptionPane.showMessageDialog(this, "主机IP不能为空，请输入主机IP", "提示", JOptionPane.NO_OPTION);
			isValid  = false;
			return isValid;
		}else if(ClientUtils.isIllegal(hostIpFld.getText())){
			JOptionPane.showMessageDialog(this, "主机IP非法，请重新输入","提示",JOptionPane.NO_OPTION);
			return false;
		}
		if(null == versionCombox.getSelectedItem() || "".equals(versionCombox.getSelectedItem().toString())){
			JOptionPane.showMessageDialog(this, "版本不能为空，请选择版本", "提示", JOptionPane.NO_OPTION);
			isValid  = false;
			return isValid;
		}
		if(null == comityCombox.getSelectedItem() || "".equals(comityCombox.getSelectedItem().toString())){
			JOptionPane.showMessageDialog(this, "团体名不能为空，请选择团体名", "提示", JOptionPane.NO_OPTION);
			isValid  = false;
			return isValid;
		}
//		else if((comityFld.getText().trim().length() < 1) || (comityFld.getText().trim().length() > 32)){
//			JOptionPane.showMessageDialog(this, "团体名错误，范围是：1-32个字符", "提示", JOptionPane.NO_OPTION);
//			isValid  = false;
//			return isValid;
//		}
		
		String ipAddr = hostIpFld.getText();
		for (int i = 0 ; i < table.getRowCount(); i++){
			SNMPHostBean snmpHostBean = (SNMPHostBean)model.getValueAt(i, model.getColumnCount());
			if (ipAddr.equals(snmpHostBean.getHostIp())){
				JOptionPane.showMessageDialog(this, "主机已经存在，请重新输入", "提示", JOptionPane.NO_OPTION);
				isValid  = false;
				return isValid;
			}
		}
		return isValid;
	}

	@SuppressWarnings("unchecked")
	public void refresh(List list){
		List<List> dataList = new ArrayList<List>();
		for (int i = 0 ; i < list.size(); i++){
			SwitchTopoNodeEntity switchTopoNodeEntity = (SwitchTopoNodeEntity)list.get(i);
			String ip = switchTopoNodeEntity.getIpValue();
			
			List rowList = new ArrayList();
			rowList.add(ip);
			rowList.add(switchTopoNodeEntity);
			
			dataList.add(rowList);
		}
		commonTableModel.setDataList(dataList);
		commonTableModel.fireTableDataChanged();
	}
	
	/**
	 * 表格
	 * @author Administrator
	 */
	@SuppressWarnings("unchecked")
	class SNMPTableModel extends AbstractTableModel{
		private String[] columnName = null;
		
		private List<List> dataList = null;
		
		public SNMPTableModel(){
			super();
		}
		
		public String[] getColumnName() {
			return columnName;
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public List<List> getDataList() {
			return dataList;
		}

		public void setDataList(List<List> dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<List>();
			}
			else{
				this.dataList = dataList;
			}
		}

		public int getRowCount(){
			if (null == dataList){
				return 0;
			}
			return dataList.size();
		}

	    public int getColumnCount(){
	    	if (null == columnName){
	    		return 0;
	    	}
	    	return columnName.length;
	    }

	    @Override
		public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }

	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	    public Object getValueAt(int rowIndex, int columnIndex){
	    	return dataList.get(rowIndex).get(columnIndex);
	    }
	    
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	dataList.get(rowIndex).set(columnIndex, aValue);
	    }
	}
}
