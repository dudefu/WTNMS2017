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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
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
import com.jhw.adm.server.entity.util.MACUniCast;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * @author Administrator
 *
 */
@Component(MACUnicastConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class MACUnicastConfigurationView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;

	public static final String ID = "macUnicastConfigurationView";
	
	//上端工具条
	private final JPanel toolBtnPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton saveBtn ;
	private JButton deleteBtn ;
	private JButton closeBtn = null;
	
	//配置面板
	private final JPanel mainPnl = new JPanel();
	
	//单播MAC配置面板
	private final JPanel configPnl = new JPanel();
	
	private final JLabel agingLbl = new JLabel();
	//Amend 2010.06.03
	private final NumberField agingFld = new NumberField(7,0,1,1000000,true);
//	private JLabel secondsLbl = new JLabel();
	
	private final JLabel macAddrLbl = new JLabel();
	private final MacAddressField macAddrFld = new MacAddressField(MacAddressField.EMRULE);
	
	private final JLabel vlanIdLbl = new JLabel();
	//Amend 2010.06.03
	private final NumberField vlanIdFld = new NumberField(4,0,1,4094,true);
	
	private final JLabel portLbl = new JLabel();
	//Amend 2010.06.03
	private final NumberField portFld = new NumberField(4,0);
	
	//单播MAC地址表
	private JScrollPane scrollPnl = null;
	
	private final JXTable table = new JXTable();
	private MacTableModel model = null;
	
	private static final String[] COLUMNNAME = {"编号","端口","VLAN ID","MAC地址","类型","状态"}; 
	private static final String VLANID_RANG = "1-4094";
	private static final String AGING_RANGE = "15~3825s,15的倍数"; 
	
	
	//下端的分页面板
	private final JPanel bottomPnl = new JPanel();
	private JButton firstBtn;
	private JButton previousBtn ;
	private JButton nextBtn;
	private JButton lastBtn;
	private final JComboBox pageValueCombox = new JComboBox();
	
	private ButtonFactory buttonFactory;
	
	private SwitchNodeEntity switchNodeEntity = null;
	private MessageSender messageSender;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
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
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private static final Logger LOG = LoggerFactory.getLogger(MACUnicastConfigurationView.class);
	
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
	protected void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		initConfigPanel();
		initTablePnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(configPnl,BorderLayout.NORTH);
		this.add(scrollPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initToolPnl(){
		toolBtnPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);
		deleteBtn = buttonFactory.createButton(DELETE);
		toolBtnPnl.add(saveBtn);
		toolBtnPnl.add(deleteBtn);
	}
	
	private void initConfigPanel(){
		initToolPnl();
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(agingLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(8,5,6,10),0,0));
		panel.add(agingFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(8,25,6,0),0,0));
		panel.add(new StarLabel("秒(" + AGING_RANGE + ")"),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(8,0,6,0),0,0));
		
		panel.add(macAddrLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(8,5,6,10),0,0));
		panel.add(macAddrFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(6,25,6,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(6,5,6,0),0,0));
		
		panel.add(vlanIdLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(6,5,6,10),0,0));
		panel.add(vlanIdFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(6,25,6,0),0,0));
		panel.add(new StarLabel("(" +  VLANID_RANG + ")"),new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(6,0,6,0),0,0));
		
		panel.add(portLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(6,5,6,10),0,0));
		panel.add(portFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(6,25,6,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(6,5,6,0),0,0));
		
		JPanel inputPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		inputPnl.add(panel);
		
		configPnl.setLayout(new BorderLayout());
		configPnl.add(inputPnl,BorderLayout.CENTER);
		configPnl.add(toolBtnPnl,BorderLayout.SOUTH);
	}
	
	private void initTablePnl(){
		model = new MacTableModel();
		model.setColumn(COLUMNNAME);
		table.setModel(model);
		
		TableColumn column= table.getColumnModel().getColumn(3);
		column.setPreferredWidth(100);
		
		table.setSortable(false);
		scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(table);
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
	
	/**
	 * 初始化资源文件
	 */
	private void setResource(){
		agingFld.setColumns(25);
		macAddrFld.setColumns(25);
		vlanIdFld.setColumns(25);
		portFld.setColumns(25);
		agingLbl.setText("老化时间");
		macAddrLbl.setText("MAC地址");
		vlanIdLbl.setText("VLAN ID");
		portLbl.setText("端口");
		macAddrFld.setDocument(new TextFieldPlainDocument(macAddrFld));
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	/**
	 * 清空控件中的值
	 */
	private void clear(){
		agingFld.setText("");
		macAddrFld.setText("");
		vlanIdFld.setText("");
		portFld.setText("");
	}
	
	/**
	 * 查询数据库中的值
	 */
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		String where = " where entity.switchNode=? order by issuedTag asc";
		Object[] parms = {switchNodeEntity};
		List<MACUniCast> macUnicastList = (List<MACUniCast>)remoteServer.getService().findAll(MACUniCast.class, where, parms);
		
		//设置列表的值
		setValue(macUnicastList);
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(List<MACUniCast> macUnicastList){
		List<List> data = new ArrayList<List>();
		if (null != macUnicastList && macUnicastList.size() > 0){
			for (int i = 0 ; i < macUnicastList.size(); i++){
				MACUniCast macUnicast = macUnicastList.get(i);
				int port = macUnicast.getPortNO();//端口
				if (port > switchNodeEntity.getPorts().size()){ //当得到的端口号大于 设备的端口总数时，不显示此端口信息
					continue;
				}
				
				int sortNum = macUnicast.getSortNum();
				int row = i+1;
				String vlanID = macUnicast.getVlanID();//vlan ID
				String macAddress = macUnicast.getMacAddress();//mac地址
				int type = macUnicast.getUnitCastType();//类型
				int oldTime = macUnicast.getOldTime();//老化时间
				
				List rowData = new ArrayList();
				rowData.add(0, row);
				rowData.add(1,port);
				rowData.add(2,vlanID);
				rowData.add(3,macAddress);
				rowData.add(4,reserveIntToString(type));
				rowData.add(5,dataStatus.get(macUnicast.getIssuedTag()).getKey());
				rowData.add(6,oldTime);
				rowData.add(7,macUnicast);
				data.add(rowData);
			}
		}
		model.setDataList(data);
		model.fireTableDataChanged();
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存单播MAC地址",role=Constants.MANAGERCODE)
	public void save(){
		//Amend 2010.06.03
		/*
		  * 判断输入的是否合法
		  * @return
		 */
		if(!isValids()){
			return;
		}
		
		//老化时间
		long aringValue = Long.parseLong(agingFld.getText().trim());
		String macAddrValue = macAddrFld.getText().trim().toUpperCase();
		String vlanIDValue = vlanIdFld.getText().trim();
		int portIDValue = Integer.parseInt(portFld.getText().trim());
		int typeValue= 1; //"Static" 默认值
		
		//判断列表中是否已经有相同的单播mac地址
		List<List> list = model.getDataList();
		if (list != null && list.size() >0){
			int rowCount = table.getRowCount();
			for (int row = 0 ; row < rowCount; row++){
				int modelRow = table.convertRowIndexToModel(row);
				MACUniCast macUnicastEntity = (MACUniCast)list.get(modelRow).get(7);
				String macAddr = macUnicastEntity.getMacAddress();
				String vlanID = macUnicastEntity.getVlanID();
				if (macAddrValue.equalsIgnoreCase(macAddr) 
						&& vlanID.equalsIgnoreCase(vlanIDValue)){
					JOptionPane.showMessageDialog(this, "输入的MAC地址已经存在，请重新输入", "提示", JOptionPane.NO_OPTION);
					return;
				}
			}
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return ;
		}
		
		MACUniCast macUnicast = new MACUniCast();
		macUnicast.setOldTime((int)aringValue);
		macUnicast.setMacAddress(macAddrValue);
		macUnicast.setVlanID(vlanIDValue);
		macUnicast.setPortNO(portIDValue);
		macUnicast.setUnitCastType(typeValue);
		macUnicast.setSwitchNode(switchNodeEntity);
		macUnicast.setIssuedTag(Constants.ISSUEDADM);
		List<Serializable> dataList = new ArrayList<Serializable>();
		dataList.add(macUnicast);
		
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
				remoteServer.getAdmService().saveAndSetting(macValue, MessageNoConstants.MACUNINEW, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("保存单播MAC地址");
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
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载单播MAC地址",role=Constants.MANAGERCODE)
	public void download(){
		int result = Constants.SYN_ALL;
		if (table.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要下载的数据", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		//mac单播类型为false;
		boolean isDynamic = false;
		List<Serializable> dataList = new ArrayList<Serializable>();
		List<List> list = model.getDataList();
		int selectRowCount = table.getSelectedRowCount();
		for(int i = 0 ; i < selectRowCount; i++){
			int row = table.getSelectedRows()[i];
			int modelRow = table.convertRowIndexToModel(row);
			MACUniCast macUnicast = (MACUniCast)list.get(modelRow).get(7);
			
			int issuedTag = macUnicast.getIssuedTag();
			if (issuedTag == Constants.ISSUEDDEVICE){
				JOptionPane.showMessageDialog(this, "选择的MAC地址已经存在，请重新选择", "提示", JOptionPane.NO_OPTION);
				return;
			}
			
			int type = macUnicast.getUnitCastType();
			if (type == 0){
				isDynamic = true;
				break;
			}
			
			macUnicast.setIssuedTag(Constants.ISSUEDADM);
			dataList.add(macUnicast);
		}
		if (isDynamic){
			JOptionPane.showMessageDialog(this, "动态类型的单播信息不能下载，请重新选择", "提示", JOptionPane.NO_OPTION);
			return;
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
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.MACUNINEW, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载单播MAC地址");
				queryData();
				clear();
				LOG.error("MACUnicastConfigurationView.save() error", e);
			}
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载单播MAC地址",role=Constants.MANAGERCODE)
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
		
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHUNICAST);
		showUploadMessageDialog(task, "上载单播MAC地址");
	}
	
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="删除单播MAC地址",role=Constants.MANAGERCODE)
	public void delete(){
		if (table.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要删除的数据", "提示", JOptionPane.NO_OPTION);
			return;
		}
		//mac单播类型为false;
		boolean isDynamic = false;
		
		//把设备侧和网管侧要删除的信息分别放到列表dataDeviceList和dataServerList中
		//当下发删除命令时，根据网管侧还是设备侧分别进行删除
		List<Serializable> dataDeviceList = new ArrayList<Serializable>();//设备侧和网管侧
		List<Serializable> dataServerList = new ArrayList<Serializable>();//网管侧
		boolean isDevice = false;
		boolean isServer = false;
		
		List<List> list = model.getDataList();
		int selectRowCount = table.getSelectedRowCount();
		for(int i = 0 ; i < selectRowCount; i++){
			int row = table.getSelectedRows()[i];
			int modelRow = table.convertRowIndexToModel(row);
			MACUniCast macUnicast = (MACUniCast)list.get(modelRow).get(7);
			int type = macUnicast.getUnitCastType();
			if (type == 0){
				isDynamic = true;
				break;
			}
			
			if(macUnicast.getIssuedTag() == Constants.ISSUEDADM){//网管侧
				isServer = true;
				dataServerList.add(macUnicast);
			}
			else{//设备侧和网管侧
				isDevice = true;
				dataDeviceList.add(macUnicast);
			}
		}
		if (isDynamic){
			JOptionPane.showMessageDialog(this, "动态类型的单播信息不能删除，请重新选择", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
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
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.MACUNIDEL, dataServerList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_SERVER);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除单播MAC地址异常");
					queryData();
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
				}
				
				//删除设备侧
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.MACUNIDEL, dataDeviceList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除单播MAC地址异常");
					queryData();
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
				}
			}
			else if(isDevice == true && isServer == false){//只删除设备侧
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.MACUNIDEL, dataDeviceList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除单播MAC地址异常");
					queryData();
					LOG.error("MACUnicastConfigurationView delete() error:{}", e);
				}
			}
			else if(isDevice == false && isServer == true){//只删除网管侧
				try {
					remoteServer.getAdmService().deleteAndSettings(macValue, MessageNoConstants.MACUNIDEL, dataServerList, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_SERVER);
				} catch (JMSException e) {
					paramConfigStrategy.showErrorMessage("删除单播MAC地址异常");
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
	
	private String reserveIntToString(int type){
		String value = "";
		switch(type){
			case 0:
				value = "Dynamic";
				break;
			case 1:
				value = "Static";
				break;
		}
		
		return value;
	}
	
	private int reserveStringToInt(String str){
		int value = 0 ;
		if ("Dynamic".equals(str)){
			value = 0;
		}
		else if ("Static".equals(str)){
			value = 1;
		}
		
		return value;
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
				model.setDataList(null);
				model.fireTableDataChanged();
				switchNodeEntity = null;
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
	
	/**
	 * 判断输入的是否合法
	 * @return
	 */
	public boolean isValids()
	{
		boolean isValid = true;
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == agingFld.getText() || "".equals(agingFld.getText())){
			JOptionPane.showMessageDialog(this, "老化时间错误，范围是：10-1000000","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if ((org.apache.commons.lang.math.NumberUtils.toInt(agingFld
						.getText()) < 10) || (org.apache.commons.lang.math.NumberUtils
						.toLong(agingFld.getText()) > 1000000)){
			JOptionPane.showMessageDialog(this, "老化时间错误，范围是：10-1000000","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String macAddr = macAddrFld.getText().trim();
		if(null == macAddr || "".equals(macAddr)){
			JOptionPane.showMessageDialog(this, "MAC地址不能为空，请输入MAC地址","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else if(!"00".equals(macAddr.substring(0, 2))){
			JOptionPane.showMessageDialog(this, "MAC地址必须以00开头，请输入MAC地址","提示",JOptionPane.NO_OPTION);
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
			JOptionPane.showMessageDialog(this, "VLAN ID错误，范围是：1-4094","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if((org.apache.commons.lang.math.NumberUtils.toInt(vlanIdFld
						.getText()) < 1) || (org.apache.commons.lang.math.NumberUtils
						.toInt(vlanIdFld.getText()) > 4094))
		{
			JOptionPane.showMessageDialog(this, "VLAN ID错误，范围是：1-4094","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		int portSize = switchNodeEntity.getPorts().size();
		if(null == portFld.getText() || "".equals(portFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "端口错误，范围是：1-" + portSize,"提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else 
		{
			if(null != switchNodeEntity)
			{
				if(portSize > 0)//necessity
				{
					if ((org.apache.commons.lang.math.NumberUtils
									.toInt(portFld.getText()) < 1) || (org.apache.commons.lang.math.NumberUtils
									.toInt(portFld.getText()) > portSize))
					{
						JOptionPane.showMessageDialog(this, "端口错误，范围是：1-" + portSize,"提示",JOptionPane.NO_OPTION);
						isValid = false;
						return isValid;
					}
				}
			}
		}
		
		return isValid;
	}
	
	
	//****************************************************
	class MacTableModel extends AbstractTableModel{
		private String[] column = null;
		private List<List> dataList = null;

		public MacTableModel(){
			
		}
		
		public void setColumn(String[] columnName){
			column = columnName;
		}
		
		public void setDataList(List<List> dataList){
			if (null == dataList){
				this.dataList = new ArrayList<List>();
			}
			else{
				this.dataList = dataList;
			}
		}
		
		public int getRowCount(){
			if(null == dataList){
				return 0;
			}
			return dataList.size();
		}


	    public int getColumnCount(){
	    	
	    	return column.length;
	    }


	    @Override
		public String getColumnName(int columnIndex){
	    	
	    	return column[columnIndex];
	    }


//	    public Class<?> getColumnClass(int columnIndex);


	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }


	    public Object getValueAt(int rowIndex, int columnIndex){
	    	if (null == dataList.get(rowIndex)){
	    		return null;
	    	}
	    	return dataList.get(rowIndex).get(columnIndex);
	    }


	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (null == dataList.get(rowIndex)){
	    		return ;
	    	}
	    	dataList.get(rowIndex).set(columnIndex, aValue);
	    }
	    
	    public List<List> getDataList() {
			return dataList;
		}
	}
}