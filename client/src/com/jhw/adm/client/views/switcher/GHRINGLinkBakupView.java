package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.math.NumberUtils;
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
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.SwitcherExplorerView;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.RingLinkBak;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(GHRINGLinkBakupView.ID)
@Scope(Scopes.DESKTOP)
public class GHRINGLinkBakupView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "ghRINGLinkBakupView";
	private static final Logger LOG = LoggerFactory.getLogger(GHRINGLinkBakupView.class);
	
	//上面的设置panel
	public JPanel topPanel = new JPanel(); 
	
	public JPanel configPnl = new JPanel();
	private final JLabel linkIdLbl = new JLabel();
	private final NumberField linkIdFld = new NumberField(4,0,1,1000,true);
	private static final String RANG = "1-1000";
	
	private final JLabel portLbl = new JLabel();
	private final JComboBox portCombox = new JComboBox();
	private final JLabel roleLbl = new JLabel();
	private final JComboBox roleCombox = new JComboBox();
	
	private final JPanel btnPnl = new JPanel();
	private JButton saveBtn;
	private JButton deleteBtn ;
	
	//********中间的列表***************
	private final JScrollPane centerScrlPnl = new JScrollPane();
	private final JTable table = new JTable();
	private LinkBakTableModel model = null;
	
	//****下面的的关闭按钮********
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn ;
	private JButton downloadBtn;
	private JButton closeBtn = null;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	
	private final String[] roleList = new String[]{"主链路","备链路"};
	private final static String[] COLUMN_NAME = {"链路ID","端口","角色","状态"};
	
	private MessageSender messageSender;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=SwitcherExplorerView.ID)
	private ViewPart explorerView;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
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
		
		initTopPnl();
		initCenterPnl();
		initBottomPnl();
		
		JPanel mainPnl = new JPanel(new BorderLayout());
		mainPnl.add(topPanel,BorderLayout.NORTH);
		mainPnl.add(centerScrlPnl,BorderLayout.CENTER);
		mainPnl.add(bottomPnl,BorderLayout.SOUTH);
		
		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				explorerView, mainPnl);
		splitPanel.setDividerLocation(210);
		this.setLayout(new BorderLayout());
		this.add(splitPanel,BorderLayout.CENTER);
		
		setResource();
	}
	
	private void initTopPnl(){
		JPanel middlePnl = new JPanel(new GridBagLayout());
		middlePnl.add(linkIdLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		middlePnl.add(linkIdFld,new GridBagConstraints(1,0,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,0,0),0,0));
//		middlePnl.add(rangeLbl,new GridBagConstraints(3,0,2,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		middlePnl.add(new StarLabel("(" + RANG + ")"),new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		
		middlePnl.add(portLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,0,0,0),0,0));
		middlePnl.add(portCombox,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
		
		middlePnl.add(roleLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,0,0,0),0,0));
		middlePnl.add(roleCombox,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,30,0,0),0,0));
		configPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		configPnl.add(middlePnl);
		
		btnPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		saveBtn = buttonFactory.createButton(SAVE);
		deleteBtn = buttonFactory.createButton(DELETE);
		
		newPanel.add(saveBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(deleteBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		
		btnPnl.add(newPanel,BorderLayout.EAST);
		
		JLabel promptLbl = new JLabel();
		promptLbl.setText("(提示：保存操作会把设备上已有的链路覆盖，请先确认。)");
		promptLbl.setForeground(new Color(35,175,98));
		btnPnl.add(promptLbl,BorderLayout.CENTER);
		
		topPanel.setLayout(new BorderLayout());
		topPanel.add(configPnl,BorderLayout.CENTER);
		topPanel.add(btnPnl,BorderLayout.SOUTH);
	}
	
	private void initCenterPnl(){
		centerScrlPnl.getViewport().add(table);
		model = new LinkBakTableModel();
		model.setColumnName(COLUMN_NAME);
		table.setModel(model);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	private void initBottomPnl(){
		bottomPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		closeBtn = buttonFactory.createCloseButton();
		newPanel.add(uploadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		bottomPnl.add(newPanel, BorderLayout.EAST);
	}
	
	private void setResource(){
		linkIdFld.setColumns(25);
		portCombox.setPreferredSize(new Dimension(100,portCombox.getPreferredSize().height));
		roleCombox.setPreferredSize(new Dimension(100,roleCombox.getPreferredSize().height));
		for (int i = 0 ; i < roleList.length; i++){
			roleCombox.addItem(roleList[i]);
		}
		
		linkIdLbl.setText("链路ID");
		portLbl.setText("端口");
		roleLbl.setText("角色");
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	/**
	 * 从服务端查询所有的链路信息 
	 */
	private void queryData(){
		clear();
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		//通过switchNodeEntity查询端口的数量
		int portCount = switchNodeEntity.getPorts().size();
		portCombox.removeAllItems();
		for(int i = 0 ; i < portCount; i++){
			portCombox.addItem(i+1);
		}
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		
		List<RingLinkBak> ringLinkBakList = (List<RingLinkBak>)remoteServer.getService().findAll(RingLinkBak.class, where, parms);
		if (null == ringLinkBakList || ringLinkBakList.size() <1){
			return;
		}
		
		setDataList(ringLinkBakList);
	}
	
	@SuppressWarnings("unchecked")
	private void setDataList(List<RingLinkBak> ringLinkBakList){
		List<List> dataList = new ArrayList<List>();
		for (int i = 0 ; i < ringLinkBakList.size(); i++){
			List rowData = new ArrayList();
			RingLinkBak ringLinkBak = ringLinkBakList.get(i);
			int linkedId = ringLinkBak.getLinkId();
			int portId = ringLinkBak.getPortNo();
			String role = ringLinkBak.getLinkRole();
			
			rowData.add(0,linkedId);
			rowData.add(1,portId);
			rowData.add(2,reverseToString(role));
			rowData.add(3,dataStatus.get(ringLinkBak.getIssuedTag()).getKey());
			rowData.add(4,ringLinkBak);
			dataList.add(rowData);
		}
		
		model.setDataList(dataList);	
		model.fireTableDataChanged();
	}
	
	/**
	 * 当交换机中已经配置有链路备份记录时，通过网管再增加链路备份时，
	 * 交换机会把原来的信息删除掉后再把增加的链路备份信息设置到交换机上。
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存GH_RING链路备份信息",role=Constants.MANAGERCODE)
	public void save(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		if(!isValids()){
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		int rowCount = table.getRowCount();
		List<List> list = model.getDataList();
		if (list != null && list.size() >0){
			for(int row = 0 ; row < rowCount ; row++){
				RingLinkBak ringLinkBak = (RingLinkBak)list.get(row).get(4);
				int issued = ringLinkBak.getIssuedTag();
				if ((issued == Constants.ISSUEDDEVICE) && (result == Constants.SYN_ALL)){
					JOptionPane.showMessageDialog(this, "链路只能配置一条，请先将原来配置的链路删除再继续配置","提示",JOptionPane.NO_OPTION);
					return;
				}
			}
		}
		
		int linkedId = Integer.parseInt(linkIdFld.getText().trim());
		int portId = Integer.parseInt(portCombox.getSelectedItem().toString().trim());
		String role = reverseToValue(roleCombox.getSelectedItem().toString().trim());
		
		RingLinkBak ringLinkBak = new RingLinkBak();
		
		ringLinkBak.setLinkId(linkedId);
		ringLinkBak.setPortNo(portId);
		ringLinkBak.setLinkRole(role);
		ringLinkBak.setSwitchNode(switchNodeEntity);
		ringLinkBak.setIssuedTag(Constants.ISSUEDADM);
		List<Serializable> dataList = new ArrayList<Serializable>();
		dataList.add(ringLinkBak);
		
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
				remoteServer.getAdmService().saveAndSetting(macValue, MessageNoConstants.LINKBAKNEW, list, 
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("保存GH_RING链路备份信息异常");
				queryData();
				LOG.error("GHRINGLinkBakupView.save() failure:{}",e);
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
	
	/**
	 * 当交换机中已经配置有链路备份记录时，通过网管再增加链路备份时，
	 * 交换机会把原来的信息删除掉后再把增加的链路备份信息设置到交换机上。
	 */
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载GH_RING链路备份信息",role=Constants.MANAGERCODE)
	public void download(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		if (table.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择要下载的链路信息","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		List<List> list = model.getDataList();
		if (list != null && list.size() >0){
			for(int row = 0 ; row < list.size() ; row++){
				RingLinkBak ringLinkBak = (RingLinkBak)list.get(row).get(4);
				int issued = ringLinkBak.getIssuedTag();
				if (issued == Constants.ISSUEDDEVICE){
					JOptionPane.showMessageDialog(this, "链路已在设备侧存在，不可再次下载！","提示",JOptionPane.NO_OPTION);
					return;
				}
			}
		}
		
		List<Serializable> dataList = new ArrayList<Serializable>();
		int row = table.getSelectedRow();
		int modelRow = table.convertRowIndexToModel(row);
		RingLinkBak ringLinkBak = (RingLinkBak)list.get(modelRow).get(4);
		ringLinkBak.setIssuedTag(Constants.ISSUEDADM);
		dataList.add(ringLinkBak);
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		Task task = new DownloadRequestTask(dataList, macValue, Constants.SYN_ALL);
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
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.LINKBAKNEW, list, 
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载GH_RING链路备份信息异常");
				queryData();
				LOG.error("GHRINGLinkBakupView.download() failure:{}",e);
			}
		}
	}
	
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="删除GH_RING链路备份信息",role=Constants.MANAGERCODE)
	public void delete(){
		int config = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != config){
			return;
		}
		
		//***********下发服务端******
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		List<List> list = model.getDataList();
		List<Serializable> dataList = new ArrayList<Serializable>();
		int row = table.getSelectedRow();
		RingLinkBak ringLinkBak = (RingLinkBak)list.get(row).get(4);
		dataList.add(ringLinkBak);
		
		int result = Constants.SYN_SERVER;
		if (ringLinkBak.getIssuedTag() == Constants.ISSUEDADM){
			result = Constants.SYN_SERVER;
		}
		else{
			result = Constants.SYN_ALL;
		}
		
		Task task = new DeleteRequestTask(dataList, macValue, result);
		showConfigureMessageDialog(task, "下载");
	}
	
	private class DeleteRequestTask implements Task{

		private List<Serializable> list = null;
		private String macValue = "";
		private int result = 0;
		public DeleteRequestTask(List<Serializable> list, String macValue, int result){
			this.list = list;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.LINKBAKDELETE, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("删除GH_RING链路备份信息异常");
				queryData();
				LOG.error("GHRINGLinkBakupView.download() failure:{}",e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载GH_RING链路备份信息",role=Constants.MANAGERCODE)
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
		
		Task task = new UploadRequestTask(synDeviceList,MessageNoConstants.SINGLESWITCHLINKBACKUPS);
		showUploadMessageDialog(task, "上载GH_RING链路备份信息");
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}else{
				clear();
			}
		}
		
	};
	
	private void clear(){
		switchNodeEntity = null;
		linkIdFld.setText("");
		model.setDataList(null);	
		model.fireTableDataChanged();
	}
	
	private String reverseToString(String value){
		String str = "";
		if ("sublink".equals(value)){
			str = "备链路";
		}
		else if ("mainlink".equals(value)){
			str = "主链路";
		}
		
		return str;
	}
	
	private String reverseToValue(String str){
		String value = "";
		if ("备链路".equals(str)){
			value = "sublink";
		}
		else if ("主链路".equals(str)){
			value = "mainlink";
		}
		
		return value;
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
	
	//Amend 2010.06.03
	public boolean isValids()
	{
		boolean isValid = true;
		
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == linkIdFld.getText() || "".equals(linkIdFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "链路ID错误，范围是：1-1000","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if ((NumberUtils.toInt(linkIdFld.getText()) < 1) || (NumberUtils
						.toInt(linkIdFld.getText()) > 1000))
		{
			JOptionPane.showMessageDialog(this, "链路ID错误，范围是：1-1000","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == portCombox.getSelectedItem() || "".equals(portCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "端口不能为空，请选择端口","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == roleCombox.getSelectedItem() || "".equals(roleCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "角色不能为空，请选择角色","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
//		int rowCount = table.getRowCount();
//		if (rowCount >0){
//			JOptionPane.showMessageDialog(this, "链路只能配置一条，请先将原来配置的链路删除再继续配置！","提示",JOptionPane.NO_OPTION);
//			isValid = false;
//			return isValid;
//		}
		
		return isValid;
	}
	
	//***********************************************************************************
	@SuppressWarnings("unchecked")
	public class LinkBakTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private List<List> dataList = new ArrayList<List>();
		public String[] getColumnName() {
			return columnName;
		}
		
		private String[] columnName = null;

		@PostConstruct
		public void LinkBakTableModel() {
			
		}
		
		public void setDataList(List<List> dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<List>();
			}
			else{
				this.dataList = dataList;
			}	
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public int getRowCount(){
			return dataList.size();
		}


	    public int getColumnCount(){
	    	return columnName.length;
	    }

	    
	    @Override
		public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }

	 
//	    public Class<?> getColumnClass(int columnIndex){
//	    	if (null == getValueAt(0,columnIndex)){
//	    		return null;
//	    	}
//	    	
//	    	return getValueAt(0,columnIndex).getClass();
//	    }


	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
	    	if (1== columnIndex){
	    		return true;
	    	}
	    	return false;
	    }


	    public Object getValueAt(int rowIndex, int columnIndex){
	    	if (dataList.size() < 1){
	    		return null;
	    	}
	    	Object object  = dataList.get(rowIndex);
	    	if (null == object){
	    		return null;
	    	}
	    	return (dataList.get(rowIndex)).get(columnIndex);
	    }

	  
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (dataList.size() < 1){
	    		return ;
	    	}
	    	Object object  = dataList.get(rowIndex);
	    	if (null == object){
	    		return;
	    	}
	    	(dataList.get(rowIndex)).set(columnIndex, aValue);
	    }
	    
	    public List<List> getDataList() {
			return dataList;
		}
	}
}
