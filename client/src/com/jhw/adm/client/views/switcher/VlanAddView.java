package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.CONFIG;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

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
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.switcher.VlanAddTableModel;
import com.jhw.adm.client.swing.HyalineDialog;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.SwitchVlanPortInfo;
import com.jhw.adm.server.entity.util.VlanBean;

/*
 * 批量配置
 */

@Component(VlanAddView.ID)
@Scope(Scopes.DESKTOP)
public class VlanAddView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "vlanAddView";

	private final JSplitPane splitPnl = new JSplitPane();
	
	private final JPanel centerPnl = new JPanel();
	private final JPanel centerTopPnl = new JPanel();
	
	private final JPanel optionPnl = new JPanel();
	private final JLabel vlanIdLbl = new JLabel();
	private final NumberField vlanIdFld = new NumberField(4,0,1,4094,true);
	private final JLabel vlanNameLbl = new JLabel();
	private final JTextField vlanNameFld = new JTextField();
	private final JLabel vlanNameRangeLbl = new JLabel();
	private final JPanel buttonPnl = new JPanel();
	private JButton saveBtn;
	private JButton deleteBtn ;
	
	private final JPanel vlanPnl = new JPanel();
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JTable vlanTable = new JTable();
	
//	private JTabbedPane tabPnl = new JTabbedPane();
	private final JPanel tabPnl = new JPanel();
	private final JScrollPane portDetailPnl = new JScrollPane();
	private final JTable portTable = new JTable();
	private VlanTableModel portModel = null;
	
	private VlanManagementView vlanMngView;
	
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn;
	private JButton configBtn;
	private JButton closeBtn ;
	private HyalineDialog hyalineDialog;
	
	private final static String[] VLAN_COLUMNNAME = new String[]{"VLAN ID","VLAN名称","状态"};
	private final static String[] PORT_COLUMNNAME = new String[]{"设备","Tag","Untag"};

	private ButtonFactory buttonFactory;
	
	private CardLayout cardLayout;
	private JPanel cardPnl;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Autowired
	@Qualifier(VlanAddTableModel.ID)
	private VlanAddTableModel vlanModel;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	private static final Logger LOG = LoggerFactory.getLogger(VlanAddView.class);
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
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
		hyalineDialog = new HyalineDialog(this);
		messageDispatcher.addProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		initSplitPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(splitPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initSplitPnl(){
		initCenterPnl();
		initSouthPnl();

		splitPnl.setDividerLocation(320);
		splitPnl.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPnl.setLeftComponent(centerPnl);
		splitPnl.setRightComponent(tabPnl);
		splitPnl.setDividerSize(7);
	}
	
	private void initCenterPnl(){
		initCenterTopPnl();
		initVlanPnl();
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(centerTopPnl,BorderLayout.NORTH);
		centerPnl.add(vlanPnl,BorderLayout.CENTER);
	}
	
	private void initCenterTopPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(vlanIdLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(vlanIdFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
		panel.add(new StarLabel("(1-4094)"),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,3,5,0),0,0));

//		panel.add(vlanNameLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
//		panel.add(vlanNameFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
//		panel.add(vlanNameRangeLbl,new GridBagConstraints(2,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,3,5,0),0,0));
		optionPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		optionPnl.add(panel);
		
		saveBtn = buttonFactory.createButton(SAVE);
		deleteBtn = buttonFactory.createButton(DELETE);
		buttonPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPnl.add(saveBtn);
		buttonPnl.add(deleteBtn);
		
		centerTopPnl.setLayout(new BorderLayout());
		centerTopPnl.add(optionPnl,BorderLayout.CENTER);
		centerTopPnl.add(buttonPnl,BorderLayout.SOUTH);	
	}
	
	private void initVlanPnl(){
		initScrollPnl();
		vlanPnl.setLayout(new BorderLayout());
		
		JPanel vlanBottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		uploadBtn = buttonFactory.createButton(UPLOAD);
		configBtn = buttonFactory.createButton(CONFIG);
		vlanBottomPnl.add(uploadBtn);
		vlanBottomPnl.add(configBtn);
		
		vlanPnl.add(scrollPnl,BorderLayout.CENTER);
		vlanPnl.add(vlanBottomPnl,BorderLayout.SOUTH);
	}
	
	private void initScrollPnl(){
		vlanModel.setColumnName(VLAN_COLUMNNAME);
		vlanTable.setModel(vlanModel);
		scrollPnl.getViewport().add(vlanTable);
		vlanTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//设置vlan名称列的长度
		TableColumn column = vlanTable.getColumnModel().getColumn(1);
		column.setPreferredWidth(150);
	}
	
	private void initSouthPnl(){
		portModel = new VlanTableModel();
		portModel.setColumnName(this.PORT_COLUMNNAME);
		portTable.setModel(portModel);
		portDetailPnl.getViewport().add(portTable);
		tabPnl.setLayout(new BorderLayout());
		tabPnl.add(portDetailPnl,BorderLayout.CENTER);
		tabPnl.setBorder(BorderFactory.createTitledBorder("端口信息"));
		
		//设置vlan名称列的长度
		TableColumn column = portTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(20);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new BorderLayout());
		JPanel panel = new JPanel(new GridBagLayout());
		closeBtn = buttonFactory.createCloseButton();
		panel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		bottomPnl.add(panel, BorderLayout.EAST);
	}
	
	private void setResource(){
		vlanIdLbl.setText("VLAN ID");
		vlanNameLbl.setText("VLAN名称");
		
		vlanNameFld.setColumns(20);
		
		vlanNameFld.setDocument(new TextFieldPlainDocument(vlanNameFld, 24));
		
		vlanNameRangeLbl.setText("(1-24个字符)");
		vlanNameRangeLbl.setForeground(Color.RED);
		
		vlanTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (vlanTable.getSelectedRow() > -1 && e.getValueIsAdjusting()) {
					vlanTableAction();
				}	
			}
		});
	}

	/**
	 * vlan表格的选择事件监听
	 */
	public void vlanTableAction() {
		
		final Runnable queryAndSetPortInformationThread = new Runnable() {
			@Override
			public void run() {
				queryAndSetPortInformation();
			}
		};

		hyalineDialog.run(queryAndSetPortInformationThread);
	}
	
	@SuppressWarnings("unchecked")
	private void queryAndSetPortInformation(){
		int row = vlanTable.getSelectedRow();
		
		final VlanBean selectVlanBean = (VlanBean)vlanModel.getValueAt(row, vlanModel.getColumnCount());
		vlanMngView.setSelectVlanBean(selectVlanBean);
		
		if (null != selectVlanBean){
			if("1".equals(selectVlanBean.getVlanID())){
				setConfigBtnEnabled(false);
			}else{
				setConfigBtnEnabled(true);
			}
		}
		
		List<SwitchVlanPortInfo> switchVlanPortInfoList = remoteServer
				.getNmsService().querySwitchVlanPortInfo(selectVlanBean);
		if (null == switchVlanPortInfoList || switchVlanPortInfoList.size() < 1) {
			setPortInformation(null);
			vlanMngView.setSwitchVlanPortInfoList(null);
			return;
		}

		List<List> dataList = new ArrayList<List>();
		for (SwitchVlanPortInfo switchVlanPortInfo : switchVlanPortInfoList) {
			List rowList = new ArrayList();
			String ipValue = switchVlanPortInfo.getIpVlue();
			String tagPort = switchVlanPortInfo.getTagPort();
			String untagPort = switchVlanPortInfo.getUntagPort();
			tagPort = sortByPort(tagPort);
			untagPort = sortByPort(untagPort);
			
			rowList.add(ipValue);
			rowList.add(tagPort);
			rowList.add(untagPort);

			dataList.add(rowList);
		}
		setPortInformation(dataList);
		vlanMngView.setSwitchVlanPortInfoList(switchVlanPortInfoList);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}
	
	private void setConfigBtnEnabled(final boolean eanbled){
		if(SwingUtilities.isEventDispatchThread()){
			configBtn.setEnabled(eanbled);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setConfigBtnEnabled(eanbled);
				}
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setPortInformation(List<List> dataList){
		portModel.setDataList(dataList);
		portModel.fireTableDataChanged();
	}
	
	public void setCardLayout(CardLayout cardLayout,JPanel cardPnl,VlanManagementView vlanMngView){
		this.cardLayout = cardLayout;
		this.cardPnl = cardPnl;
		this.vlanMngView = vlanMngView;
	}
	
	public void queryData(){
		List<VlanBean> vlanBeanList = remoteServer.getNmsService().querySwitchVlanInfo();
		if (null == vlanBeanList || vlanBeanList.size() < 1){
			vlanModel.setDataList(null);
			vlanModel.fireTableDataChanged();
			return;
		}
		setValue(vlanBeanList);
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(List<VlanBean> vlanBeanList){
		List<List> dataList = new ArrayList<List>();
		for (VlanBean vlanBean : vlanBeanList){
			List rowList = new ArrayList();
			String vlanId = vlanBean.getVlanID();
			String vlanName = vlanBean.getVlanName();
			rowList.add(vlanId);
			rowList.add(vlanName);
			rowList.add(dataStatus.get(vlanBean.getIssuedTag()).getKey());
			rowList.add(vlanBean);
			
			dataList.add(rowList);
		}
		
		vlanModel.setDataList(dataList);
		vlanModel.fireTableDataChanged();
//		vlanTable.updateUI();
	}
	
	/**
	 * 保存操作
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存VLAN ID",role=Constants.MANAGERCODE)
	public void save(){
		if(null == vlanIdFld.getText().trim() || "".equals(vlanIdFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "VLAN ID错误，范围为：1-4094","提示",JOptionPane.NO_OPTION);
			return ;
		}
		else if ((NumberUtils.toInt(vlanIdFld.getText().trim()) < 1) || (NumberUtils
						.toInt(vlanIdFld.getText().trim()) > 4094)){
			JOptionPane.showMessageDialog(this, "VLAN ID错误，范围为：1-4094","提示",JOptionPane.NO_OPTION);
			return ;
		}
		else if ((NumberUtils.toInt(vlanIdFld.getText().trim()) == 1)){
			JOptionPane.showMessageDialog(this, "VLAN ID 1为默认ID，请重新输入","提示",JOptionPane.NO_OPTION);
			return ;
		}
		
//		if(vlanNameFld.getText().trim().length() < 1 || vlanNameFld.getText().trim().length() > 36){
//			JOptionPane.showMessageDialog(this, "VLAN名称错误，范围为：1-36个字符","提示",JOptionPane.NO_OPTION);
//			return ;
//		}
		
		List<List> dataList = vlanModel.getDataList();
		if (null != dataList){
			for (int i = 0; i < dataList.size(); i++){
				List rowList = dataList.get(i);
				String vlanStr = String.valueOf(rowList.get(0));
				if (NumberUtils.toInt(vlanStr) == NumberUtils.toInt(vlanIdFld.getText().trim())){
					JOptionPane.showMessageDialog(this, "已经有相同的VLAN ID，请重新输入","提示",JOptionPane.NO_OPTION);
					return ;
				}
			}
		}
		
		int vlanID = NumberUtils.toInt(vlanIdFld.getText().trim());
		//String vlanName = vlanNameFld.getText().trim();
		String vlanName = String.valueOf(vlanID);
		VlanEntity vlanEntity = new VlanEntity();
		vlanEntity.setVlanID(vlanID);
		vlanEntity.setVlanName(vlanName);
		
		Task task = new SaveRequestTask(vlanEntity);
		showMessageDialog(task, "保存VLAN ID");
	}
	
	private class SaveRequestTask implements Task{

		private final VlanEntity vlanEntity;
		public SaveRequestTask(VlanEntity vlanEntity){
			this.vlanEntity = vlanEntity;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().saveEntity(vlanEntity);
			}catch(Exception e){
				strategy.showErrorMessage("保存VLAN ID异常");
				LOG.error("", e);
			}
			strategy.showNormalMessage("保存VLAN ID成功");
			queryData();
			vlanIdFld.setText("");
			vlanNameFld.setText("");
		}
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
	 * 删除操作
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="删除VLAN ID",role=Constants.MANAGERCODE)
	public void delete(){
		if (vlanTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要删除的VLAN ID","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		String vlanID = (String)vlanTable.getValueAt(vlanTable.getSelectedRow(), 0);
		if ("1".equals(vlanID)){
			JOptionPane.showMessageDialog(this, "VLAN 1 不能被删除，请重新选择","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		List<List> portList = portModel.getDataList();
		if(null != portList && portList.size() > 0){
			JOptionPane.showMessageDialog(this, "请先删除VLAN下绑定的端口","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		List<VlanEntity> list = (List<VlanEntity>)remoteServer.getService().findAll(VlanEntity.class, " where entity.vlanID="+ vlanID);
		
		Task task = new DeleteRequestTask(list);
		showMessageDialog(task, "删除VLAN ID");
	}
	
	private class DeleteRequestTask implements Task{

		private final List<VlanEntity> list ;
		public DeleteRequestTask(List<VlanEntity> list){
			this.list = list;
		}
		
		//String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		//String macValue = "dc:e1:ad:01:04:fa";
		@Override
		public void run() {
			try{
				remoteServer.getService().deleteEntities(list);
				for(int i=0 ;i<list.size();i++){
					VlanEntity vlanEntity = (VlanEntity) list.get(i);
					String macValue = vlanEntity.getVlanConfig().getSwitchNode().getBaseInfo().getMacValue();
					remoteServer.getAdmService().deleteAndSetting(macValue, MessageNoConstants.VLANDELETE, vlanEntity, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
				}
			}catch(Exception e){
				strategy.showErrorMessage("删除VLAN ID异常");
				LOG.error("", e);
			}
			strategy.showNormalMessage("删除VLAN ID成功");
			queryData();
		}
	}
	
	/**
	 * vlan配置操作
	 */
	@ViewAction(name=CONFIG, icon=ButtonConstants.MODIFY,desc="进入Vlan端口配置视图",role=Constants.MANAGERCODE)
	public void config(){
		if (null == cardLayout){
			return;
		}
		
		if (vlanTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要配置的VLAN ID","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		vlanMngView.refreshVlanConfigView();
		cardLayout.show(cardPnl, VlanManagementView.DETAIL_CARD);
	}
	
	
	/**
	 * vlan上载操作
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载VLAN信息",role=Constants.MANAGERCODE)
	public void upload(){
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		List<SwitchTopoNodeEntity> switchTopoNodeEntities = (List<SwitchTopoNodeEntity>) remoteServer
													.getService().findAll(SwitchTopoNodeEntity.class);
		for(SwitchTopoNodeEntity switchTopoNodeEntity : switchTopoNodeEntities){
			 switchNodeEntity = remoteServer.getService()
				.getSwitchByIp(switchTopoNodeEntity.getIpValue());
			
			SynchDevice synchDevice = new SynchDevice();
			synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
			synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
			synDeviceList.add(synchDevice);
		}
		
		Task task = new UploadRequestTask(synDeviceList,MessageNoConstants.SINGLESWITCHVLAN);
		showUploadMessageDialog(task, "上载VLAN信息");
	}
	
	/**
	 * 对端口进行排序
	 * @param strInfo
	 * @return 
	 */
	private String sortByPort(String strInfo){
		StringBuffer buffer = new StringBuffer();
		if (strInfo == null || "".equals(strInfo)){
			return buffer.toString();
		}
		
		String[] infoArray = strInfo.split(",");
		int[] iPort = new int[infoArray.length];
		for (int i = 0; i < infoArray.length; i++){
            try{
                int temp = Integer.parseInt(infoArray[i]);   
                iPort[i] = temp; 
            }catch(Exception e){
            }   
		}

		Arrays.sort(iPort);
		for(int j = 0 ; j < iPort.length; j++){
			buffer.append(iPort[j]).append(",");  
		}
		
		String portStrs = buffer.toString();
		if (!"".equals(portStrs)){
			portStrs = portStrs.substring(0,portStrs.length()-1);
		}
		
		return portStrs;
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	@Override
	public void dispose() {
		hyalineDialog.dispose();
		messageDispatcher.removeProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
	
	 @SuppressWarnings("unchecked")
	class VlanTableModel extends AbstractTableModel{

		private static final long serialVersionUID = 1L;
		private List<List> dataList = new ArrayList<List>();
		private String[] columnName = null;
		
		
		protected VlanTableModel() {
		}
		
		public void setDataList(List dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<List>();
			}
			else{
				this.dataList= dataList;
			}
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public int getRowCount(){
			if (null == dataList){
				return 0;
			}
			return dataList.size();
		}
	    
	    public int getColumnCount(){
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
			if (null == dataList || dataList.size() < 1) {
				return null;
			}
			List rowList = null;
			try {
				rowList = dataList.get(rowIndex);
			} catch (Exception ee) {
			}

			if (null == rowList) {
				return null;
			}
			Object object = rowList.get(columnIndex);

			return object;
	    }

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
			if (null == dataList || dataList.size() < 1) {
				return;
			}
			List rowList = null;

			rowList = dataList.get(rowIndex);

			if (null == rowList) {
				return;
			}

			rowList.set(columnIndex, aValue);
	    }
	    
	    public List<List> getDataList(){
	    	return dataList;
	    }
	    
	    public void insertRow(List rowData){
	    	dataList.add(rowData);
	    	this.fireTableDataChanged();
	    }
	    
	    public void updateRow(int index,List rowData){
	    	dataList.set(index, rowData);
	    	this.fireTableDataChanged();
	    }
	    
	    public void deleteRow(List rowData){
	    	dataList.remove(rowData);
	    	this.fireTableDataChanged();
	    }
	    
	    @Override
		public void fireTableDataChanged() {
	    	if(SwingUtilities.isEventDispatchThread()) {
	    		super.fireTableDataChanged();
	    	}else {
	    		SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fireTableDataChanged();
					}
				});
	    	}
	    }
	}
}
