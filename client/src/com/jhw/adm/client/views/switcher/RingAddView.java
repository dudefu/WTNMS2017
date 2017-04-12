package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.CONFIG;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.TextMessage;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

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
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.swing.HyalineDialog;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.RingConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.RingBean;
import com.jhw.adm.server.entity.util.RingInfo;

@Component(RingAddView.ID)
@Scope(Scopes.DESKTOP)
public class RingAddView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "ringAddView";
	private static final Logger LOG = LoggerFactory.getLogger(RingAddView.class);

	private final JSplitPane splitPnl = new JSplitPane();
	private final JPanel centerPnl = new JPanel();
	private final JPanel centerTopPnl = new JPanel();
	private final JPanel optionPnl = new JPanel();
	
	private final JLabel ringIdLbl = new JLabel();
	private final NumberField ringIdFld = new NumberField(4,0,1,4096,true);
	private final JLabel ringTypeLbl = new JLabel();
	private final JComboBox ringTypeCombo = new JComboBox();
	private final JCheckBox modeChkBox = new JCheckBox();
	private final JPanel buttonPnl = new JPanel();
	private JButton saveBtn;
	private JButton deleteBtn ;
	
	private final JPanel ringPnl = new JPanel();
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JTable ringTable = new JTable();
	private RingTableModel ringModel = null;
	
	private final JTabbedPane tabPnl = new JTabbedPane();
	private final JScrollPane portDetailPnl = new JScrollPane();
	private final JTable portTable = new JTable();
	private RingTableModel portModel = null;
	
	private CardLayout cardLayout;
	private JPanel cardPnl;
	private RingManagementView ringMngView;
	
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn;
	private JButton configBtn;
	private JButton closeBtn ;	
	private HyalineDialog hyalineDialog;
	
	private final static String[] RING_COLUMNNAME = new String[]{"Ring ID","模式","环网类型","状态"};
	private final static String[] PORT_COLUMNNAME = new String[]{"设备","端口","系统类型"};
	
	private final static String[] RING_TYPE = new String[]{"静态环网","动态环网"};
	
	private ButtonFactory buttonFactory;
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
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

		splitPnl.setDividerLocation(350);
		splitPnl.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPnl.setLeftComponent(centerPnl);
		splitPnl.setRightComponent(tabPnl);
		splitPnl.setDividerSize(7);
	}
	
	private void initCenterPnl(){
		initCenterTopPnl();
		initRingPnl();
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(centerTopPnl,BorderLayout.NORTH);
		centerPnl.add(ringPnl,BorderLayout.CENTER);
	}
	
	private void initRingPnl(){
		initScrollPnl();
		ringPnl.setLayout(new BorderLayout());
		
		JPanel vlanBottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		uploadBtn = buttonFactory.createButton(UPLOAD);
		configBtn = buttonFactory.createButton(CONFIG);
		vlanBottomPnl.add(uploadBtn);
		vlanBottomPnl.add(configBtn);
		
		ringPnl.add(scrollPnl,BorderLayout.CENTER);
		ringPnl.add(vlanBottomPnl,BorderLayout.SOUTH);
	}
	
	private void initCenterTopPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(ringIdLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(ringIdFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		panel.add(new StarLabel("(1-4096)"),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
		panel.add(ringTypeLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(ringTypeCombo,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
		panel.add(modeChkBox,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,2,5,0),0,0));
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
	
	private void initScrollPnl(){
		ringModel = new RingTableModel();
		ringModel.setColumnName(RING_COLUMNNAME);
		ringTable.setModel(ringModel);
		ringTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPnl.getViewport().add(ringTable);
		
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(ringModel);
		ringTable.setRowSorter(sorter);
		sorter.toggleSortOrder(0);
	}
	
	private void initSouthPnl(){
		portModel = new RingTableModel();
		portModel.setColumnName(this.PORT_COLUMNNAME);
		portTable.setModel(portModel);
		portDetailPnl.getViewport().add(portTable);
		
		tabPnl.addTab("端口信息", portDetailPnl);
		tabPnl.setBorder(null);
		portDetailPnl.setBorder(null);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
//		configBtn = buttonFactory.createButton(CONFIG);
		closeBtn = buttonFactory.createCloseButton();
//		bottomPnl.add(configBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}
	
	private void setResource(){
		ringIdLbl.setText("Ring ID");
		ringTypeLbl.setText("环网类型");
		modeChkBox.setText("Enable");
		modeChkBox.setHorizontalTextPosition(AbstractButton.LEADING);
		
		modeChkBox.setSelected(true);
		modeChkBox.setEnabled(true);
//		modeChkBox.isEnabled();
		
		ringTypeCombo.removeAllItems();
		for(int i = 0 ; i < RING_TYPE.length; i++){
			ringTypeCombo.addItem(RING_TYPE[i]);
		}
		
		ringTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(ringTable.getSelectedRow() > -1 && e.getValueIsAdjusting()) {
					ringTableAction();
				}
			}
		});
//		
//		modeChkBox.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e){
//				modeChkBox.setSelected(true);
//			}
//		});
		modeChkBox.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				int a = e.getStateChange() ;
				if(a==ItemEvent.SELECTED){
					modeChkBox.setSelected(false);
				}else{
					modeChkBox.setSelected(true);
				}
			}
			
		});
	}
	
	/**
	 * ring表格的选择事件监听
	 */
	public void ringTableAction() {		
		final Runnable queryAndSetEquipmentInfomationThread = new Runnable() {
			
			@Override
			public void run() {
				queryAndSetEquipmentInformation();
			}
		};
		
		hyalineDialog.run(queryAndSetEquipmentInfomationThread);
	}
	
	@SuppressWarnings("unchecked")
	private void queryAndSetEquipmentInformation(){
		int row = ringTable.getSelectedRow();
		int modelRow = -1;
		if (row > -1){
			modelRow = ringTable.convertRowIndexToModel(row);
		}

		final RingBean selectRingBean = (RingBean)ringModel.getValueAt(modelRow, ringModel.getColumnCount());
		
		ringMngView.setSelectRingBean(selectRingBean);
		
		List<RingInfo> ringInfoList = remoteServer.getNmsService().queryRingInfo(selectRingBean);
		if (null == ringInfoList || ringInfoList.size() < 1){
			setCommonTableValue(null);
			ringMngView.setRinginfoList(null);
			return;
		}
		
		List<List> dataList = new ArrayList<List>();
		for (RingInfo ringInfo : ringInfoList){
			List rowList = new ArrayList();
			String ipValue = ringInfo.getIp();
			String ports = ringInfo.getPorts();
			String sysType = ringInfo.getSysType();
			rowList.add(ipValue);
			rowList.add(ports);
			rowList.add(sysType);
			
			dataList.add(rowList);
		}
		setCommonTableValue(dataList);
		ringMngView.setRinginfoList(ringInfoList);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}
	
	private void setCommonTableValue(final List<List> dataList){
		if(SwingUtilities.isEventDispatchThread()){
			portModel.setDataList(dataList);
			portModel.fireTableDataChanged();
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setCommonTableValue(dataList);
				}
			});
		}
	}
	
	public void queryData(){
		List<RingBean> ringBeanList = remoteServer.getNmsService().queryRingConfig();
		if (null == ringBeanList || ringBeanList.size() < 1){
			ringModel.setDataList(null);
			ringModel.fireTableDataChanged();
			return;
		}
		setValue(ringBeanList);
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(List<RingBean> ringBeanList){
		List<List> dataList = new ArrayList<List>();
		for (RingBean ringBean : ringBeanList){
			List rowList = new ArrayList();
			String ringId = String.valueOf(ringBean.getRingID());
			String mode = reverseMode(ringBean.isRingEnable());
			String ringType = reverseRingType(ringBean.getRingType());
			String status = dataStatus.get(ringBean.getIssuedTag()).getKey();
			rowList.add(ringId);
			rowList.add(mode);
			rowList.add(ringType);
			rowList.add(status);
			rowList.add(ringBean);
			
			dataList.add(rowList);
		}
		
		ringModel.setDataList(dataList);
		ringModel.fireTableDataChanged();	
	}
	
	public void setCardLayout(CardLayout cardLayout,JPanel cardPnl,RingManagementView ringMngView){
		this.cardLayout = cardLayout;
		this.cardPnl = cardPnl;
		this.ringMngView = ringMngView;
	}
	
	/**
	 * 保存操作
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存Ring ID",role=Constants.MANAGERCODE)
	public void save(){
		if(!isValids()){
			return;
		}
		
		int ringID = NumberUtils.toInt(ringIdFld.getText().trim());
		boolean mode = modeChkBox.isSelected();
		int ringType = reverseRingType(ringTypeCombo.getSelectedItem().toString());
		RingConfig ringConfig = new RingConfig();
		ringConfig.setRingID(ringID);
		ringConfig.setRingEnable(mode);
		ringConfig.setRingType(ringType);
		
		Task task = new SaveRequestTask(ringConfig);
		showMessageDialog(task, "保存Ring ID");
	}
	
	private class SaveRequestTask implements Task{

		private final RingConfig ringConfig;
		public SaveRequestTask(RingConfig ringConfig){
			this.ringConfig = ringConfig;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().saveEntity(ringConfig);
			}catch(Exception e){
				strategy.showErrorMessage("保存Ring ID异常");
				queryData();
				ringIdFld.setText("");
				LOG.error("", e);
			}
			strategy.showNormalMessage("保存Ring ID成功");
			queryData();
			ringIdFld.setText("");
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
	
	public boolean isValids(){
		boolean isValid = true;
		
		String ringID = ringIdFld.getText().trim();
		if(null == ringID || "".equals(ringID)){
			JOptionPane.showMessageDialog(this, "Ring ID错误,范围为：1-4096","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if((NumberUtils.toInt(ringID) < 1) || (NumberUtils.toInt(ringID) > 4097)){
			JOptionPane.showMessageDialog(this, "Ring ID错误,范围为：1-4096","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		for (int i = 0 ; i < ringTable.getRowCount(); i++){
			String id = String.valueOf(ringTable.getValueAt(i, 0));
			if (ringID.equals(id)){
				JOptionPane.showMessageDialog(this, "该Ring ID已经存在，请重新输入","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		return isValid;
	}
	
	/**
	 * 删除操作
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="删除Ring ID",role=Constants.MANAGERCODE)
	public void delete(){
		if (ringTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择要删除的Ring ID", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		List<List> portList = portModel.getDataList();
		if(null != portList && portList.size() > 0){
			JOptionPane.showMessageDialog(this, "请先删除ring下的端口","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		String ringID = (String)ringTable.getValueAt(ringTable.getSelectedRow(), 0);
		List<RingConfig> list = (List<RingConfig>)remoteServer.getService().findAll(RingConfig.class, " where entity.ringID="+ ringID);
		
		Task task = new DeleteRequestTask(list);
		showMessageDialog(task, "删除Ring ID");
	}
	
	private class DeleteRequestTask implements Task{

		private final List<RingConfig> list ;
		public DeleteRequestTask(List<RingConfig> list){
			this.list = list;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().deleteEntities(list);
			}catch(Exception e){
				strategy.showErrorMessage("删除Ring ID异常");
				ringIdFld.setText("");
				LOG.error("", e);
			}
			strategy.showNormalMessage("删除Ring ID成功");
			queryData();
			ringIdFld.setText("");
		}
	}
	
	/**
	 * ring配置操作
	 */
	@ViewAction(name=CONFIG, icon=ButtonConstants.MODIFY,desc = "进入RING端口信息配置视图",role=Constants.MANAGERCODE)
	public void config(){
		if (null == cardLayout){
			return;
		}
		
		if (ringTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要配置的Ring ID","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		ringMngView.refreshRingConfigView();
		cardLayout.show(cardPnl, RingManagementView.DETAIL_CARD);
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载WT-Ring信息",role=Constants.MANAGERCODE)
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
		
		Task task = new UploadRequestTask(synDeviceList,MessageNoConstants.SINGLESWITCHRING);
		showUploadMessageDialog(task, "上载WT-Ring信息");
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
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		hyalineDialog.dispose();
		messageDispatcher.removeProcessor(MessageNoConstants.SYNCHFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
	
	private String reverseMode(boolean bool){
		String str = "";
		if (bool){
			str = "Enable";
		}
		else{
			str = "Disable";
		}
		
		return str;
	}
	
	private boolean reverseMode(String str){
		boolean bool = false;;
		if (str.equals("Disable")){
			bool = false;
		}
		else{
			bool = true;
		}
		
		return bool;
	}
	
	private int reverseRingType(String str){
		int value = 0;
		if (str.equals("静态环网")){
			value = 0;
		}
		else {
			value = 1;
		}
		return value;
	}
	
	private String reverseRingType(int value){
		String str = "";
		if (value == 0){
			str = "静态环网";
		}
		else{
			str = "动态环网";
		}
		return str;
	}
	
	@SuppressWarnings("unchecked")
	class RingTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private List<List> dataList = null;
		private String[] columnName = null;
		
		
		protected RingTableModel() {
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
	    	if (null == dataList || dataList.size() <1){
	    		return null;
	    	}
	    	List rowList = null;
	    	try{
	    		rowList = dataList.get(rowIndex);
	    	}
	    	catch(Exception ee){
	    	}
	    	
	    	if (null == rowList){
	    		return null;
	    	}
	    	Object object = rowList.get(columnIndex);
	    	
	    	return object;
	    }

	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (null == dataList || dataList.size() <1){
	    		return ;
	    	}
	    	List rowList = null;

	    	rowList = dataList.get(rowIndex);

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
	}
}
