package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.TimerMonitoring;

@Component(TimingMonitoringConfigView.ID)
@Scope(Scopes.DESKTOP)
public class TimingMonitoringConfigView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "timingMonitoringConfigView";

	private JPanel topPnl = new JPanel();
	
	private JPanel modifyPnl = new JPanel();
	private JLabel deviceLbl = new JLabel();
	private IpAddressField deviceField = new IpAddressField();
	
	private JLabel portLbl = new JLabel();
	private JFormattedTextField portField;
	
	private JLabel paramLbl = new JLabel();
	private JComboBox paramCombox = new JComboBox();
	
	private JLabel beginTimeLbl = new JLabel();
	private JFormattedTextField beginTimeFld = null;
	
	private JLabel endTimeLbl = new JLabel();
	private JFormattedTextField endTimeFld = null;
	
	private ButtonFactory buttonFactory;
	private JButton addBtn = null;
	private JButton deleteBtn = null;
	private JButton closeBtn = null;
	
	private JPanel centerPnl = new JPanel();
	private JScrollPane scrollPnl = new JScrollPane();
	private JXTable table = new JXTable();
	private TimingTableModel tableModel = null;
	
	private final static String pattern = "yyyy-MM-dd HH:mm";
	
	//保存所查询到的所有的交换机
	private List<SwitchNodeEntityObject> switchList = new ArrayList<SwitchNodeEntityObject>();
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;

	@Resource(name=DateFormatter.ID)
	private DateFormatter dateFormatter;
	
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	private static final Logger LOG = LoggerFactory.getLogger(TimingMonitoringConfigView.class);
	
	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this);
		initTopPnl();
		initCenterPnl();
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(centerPnl,BorderLayout.CENTER);
		setViewSize(640, 480);
		setResource();
	}
	
	private void initTopPnl(){
		MaskFormatter mf = null;
		try {
			mf = new MaskFormatter("####-##-## ##:##");
			mf.setPlaceholderCharacter('_');
		} catch (ParseException e) {
			LOG.error("TimingMonitoringConfigView.initTopPnl() is failure:{}", e);
		}
		beginTimeFld = new JFormattedTextField(mf);
		endTimeFld = new JFormattedTextField(mf);
		
		topPnl.setLayout(new BorderLayout());
		
		NumberFormat integerFormat = NumberFormat.getNumberInstance();
		integerFormat.setMinimumFractionDigits(0);
		integerFormat.setParseIntegerOnly(true);
		integerFormat.setGroupingUsed(false);
		portField = new JFormattedTextField(integerFormat);
		portField.setColumns(5);
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(deviceLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(deviceField,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,10,0),0,0));
		panel.add(portLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,10,0),0,0));
		panel.add(portField,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,10,0),0,0));
		
		panel.add(paramLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(paramCombox,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,10,0),0,0));
		
		panel.add(beginTimeLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,10,0),0,0));
		panel.add(beginTimeFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,10,0),0,0));
		panel.add(endTimeLbl,new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,10,0),0,0));
		panel.add(endTimeFld,new GridBagConstraints(3,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,10,0),0,0));

		modifyPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		modifyPnl.add(panel);
		
		JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		addBtn = buttonFactory.createButton(APPEND);
		deleteBtn = buttonFactory.createButton(DELETE);
		closeBtn = buttonFactory.createCloseButton();
		buttonPnl.add(addBtn);
		buttonPnl.add(deleteBtn);
		buttonPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
		
		topPnl.add(modifyPnl,BorderLayout.CENTER);
		topPnl.add(buttonPnl,BorderLayout.SOUTH);
	}
	
	private void initCenterPnl(){
		
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(true);
		
		tableModel = new TimingTableModel();
		table.setModel(tableModel);
		table.setColumnModel(tableModel.getColumnMode());
		table.getTableHeader().setReorderingAllowed(false);
		
		scrollPnl.getViewport().add(table,null);
		scrollPnl.getHorizontalScrollBar().setFocusable(false);
		scrollPnl.getVerticalScrollBar().setFocusable(false);
		scrollPnl.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPnl.getVerticalScrollBar().setUnitIncrement(30);
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(scrollPnl,BorderLayout.CENTER);
	}
	
	private void setResource(){
		setTitle("定时监控配置");
		modifyPnl.setBorder(BorderFactory.createTitledBorder("定时监控配置"));
		deviceLbl.setText("监控设备");
		portLbl.setText("监控端口");
		paramLbl.setText("参数");
		beginTimeLbl.setText("开始时间");
		endTimeLbl.setText("结束时间");
		
		beginTimeFld.setColumns(20);
		endTimeFld.setColumns(20);
		
		paramCombox.removeAllItems();
		paramCombox.addItem(Constants.octets);
		paramCombox.addItem(Constants.packets);
		paramCombox.addItem(Constants.bcast_pkts);
		paramCombox.addItem(Constants.mcast_pkts);
		paramCombox.addItem(Constants.crc_align);
		paramCombox.addItem(Constants.undersize);
		paramCombox.addItem(Constants.oversize);
		paramCombox.addItem(Constants.fragments);
		paramCombox.addItem(Constants.jabbers);
		paramCombox.addItem(Constants.collisions);
		paramCombox.addItem(Constants.pkts_64);
		paramCombox.addItem(Constants.pkts_65_127);
		paramCombox.addItem(Constants.pkts_128_255);
		paramCombox.addItem(Constants.pkts_256_511);
		paramCombox.addItem(Constants.pkts_512_1023);
		paramCombox.addItem(Constants.pkts_1024_1518);
		
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		//查询所有的定时监控配置
		List<TimerMonitoring> timerMonitoringLists = (List<TimerMonitoring>) remoteServer
				.getService().findAll(TimerMonitoring.class);
		
		if(timerMonitoringLists.size() == 0){
			tableModel.setDataList(null);
			tableModel.fireTableDataChanged();
			return;
		}
		//根据所查询到的数据填充表格
		setTableValue(timerMonitoringLists);
	}

	private void setTableValue(List<TimerMonitoring> list){
		tableModel.setDataList(list);
		tableModel.fireTableDataChanged();
	}

	 /**
	 * 新增按钮事件
	 */
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND,desc="添加定时监控配置",role=Constants.MANAGERCODE)
	public void append(){
		if(!isValids()){
			return ;
		}
		List<TimerMonitoring> list = new ArrayList<TimerMonitoring>();
		TimerMonitoring timerMonitoring = new TimerMonitoring();
		
		SimpleDateFormat  format =new SimpleDateFormat(pattern);
		try {
			Date beginDate = format.parse(beginTimeFld.getText().trim());
			Date endDate = format.parse(endTimeFld.getText().trim());
			timerMonitoring.setStartDate(beginDate);
			timerMonitoring.setEndDate(endDate);
		} catch (ParseException e) {
			LOG.error("TimingMonitoringConfigView.append() is failure:{}",e);
		}
		
		timerMonitoring.setIpValue(deviceField.getIpAddress());
		timerMonitoring.setPortNo(Integer.parseInt(portField.getText()));
		timerMonitoring.setParam(paramCombox.getSelectedItem().toString());

		list.add(timerMonitoring);

		Task task = new SaveRequestTask(list);
		showMessageDialog(task, "添加");
	}
	
	private class SaveRequestTask implements Task{
		
		private List<TimerMonitoring> list = null;
		public SaveRequestTask(List<TimerMonitoring> list){
			this.list = list;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getPingTimerService().updateMonitoring(list);
			}catch(Exception e){
				strategy.showErrorMessage("添加网管侧异常");
				queryData();
				LOG.error("TimingMonitoringConfigView.append() error", e);
			}
			strategy.showNormalMessage("添加网管侧成功");
			queryData();
		}
	}
	
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE,desc="删除定时监控配置",role=Constants.MANAGERCODE)
	public void delete(){
		if(table.getSelectedColumnCount() == 0){
			JOptionPane.showMessageDialog(this, "请选择需要删除的记录", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int isDelete = JOptionPane.showConfirmDialog(this, "是否删除选中的记录", "提示", JOptionPane.OK_CANCEL_OPTION);
		if(isDelete == 0){
			List<TimerMonitoring> timerMonitoringList = tableModel
					.getSelectedRows(table.getSelectedRows());
			Task task = new DeleteRequestTask(timerMonitoringList);
			showMessageDialog(task, "删除");
		}
	}
	
	private class DeleteRequestTask implements Task{
		
		private List<TimerMonitoring> timerMonitoringList = null;
		public DeleteRequestTask(List<TimerMonitoring> timerMonitoringList){
			this.timerMonitoringList = timerMonitoringList;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getPingTimerService().deleteMonitoring(timerMonitoringList);
			}catch(Exception e){
				strategy.showErrorMessage("删除网管侧异常");
				queryData();
				LOG.error("TimingMonitoringConfigView.delete() error", e);
			}
			strategy.showNormalMessage("删除网管侧成功");
			queryData();
		}
	}

	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
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

	private boolean isValids(){
		boolean isValid = true;
		String device = deviceField.getIpAddress();
		int portNo= NumberUtils.toInt(portField.getText());
		Object param = paramCombox.getSelectedItem();
		
		if(null == device || "".equals(device.toString())){
			JOptionPane.showMessageDialog(this, "监控设备不允许为空，请选择监控设备", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		SwitchTopoNodeEntity topoNodeEntity = (SwitchTopoNodeEntity) remoteServer
				.getService().findSwitchTopoNodeByIp(device);
		if (null == topoNodeEntity) {
			JOptionPane.showMessageDialog(this, "该设备不存在，请重新输入设备地址", ClientUtils.getAppName(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if(StringUtils.isBlank(portField.toString())){
			JOptionPane.showMessageDialog(this, "监控端口不允许为空，请选择端口设备", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (portNo < 1) {
			JOptionPane.showMessageDialog(this, "监控设备端口必需大于 0", ClientUtils.getAppName(), JOptionPane.ERROR_MESSAGE);
			return false;
		}else{
			SwitchNodeEntity switchNodeEntity = NodeUtils.getNodeEntity(topoNodeEntity).getNodeEntity();
			Set<SwitchPortEntity> portSet = switchNodeEntity.getPorts();
			boolean isExists = false;
			if(null != portSet){
				for(SwitchPortEntity switchPortEntity : portSet){
					if(switchPortEntity.getPortNO() == portNo){
						isExists = true;
						break;
					}
				}
			}
			if(isExists == false){
				JOptionPane.showMessageDialog(this, "监控设备不存在该端口，请重新输入", ClientUtils.getAppName(), JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		
		
		if(null == param || "".equals(param.toString())){
			JOptionPane.showMessageDialog(this, "监控参数不允许为空，请选择监控参数", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		FormattedTextFieldVerifier verifier = new FormattedTextFieldVerifier(this);
		boolean boolBeginDate = verifier.shouldYieldFocus(beginTimeFld);
		if (!boolBeginDate){
			isValid = false;
			return isValid;
		}
		
		boolean boolEndDate = verifier.shouldYieldFocus(endTimeFld);
		if (!boolEndDate){
			isValid = false;
			return isValid;
		}
		
		if(beginTimeFld.getText() == null || "".equals(beginTimeFld.getText())){
			JOptionPane.showMessageDialog(this, "开始时间不允许为空，请输入开始时间", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(endTimeFld.getText() == null || "".equals(endTimeFld.getText())){
			JOptionPane.showMessageDialog(this, "结束时间不允许为空，请输入结束时间", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		SimpleDateFormat format =new SimpleDateFormat(pattern);
		Date beginDate = null;
		Date endDate = null;
		try {
			beginDate = format.parse(beginTimeFld.getText().trim());
			endDate = format.parse(endTimeFld.getText().trim());
			if(beginDate.getTime() >= endDate.getTime()){//开始时间大于或等于结束时间
				JOptionPane.showMessageDialog(this, "开始时间不能大于或等于结束时间，请重新输入", "提示", JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		} catch (ParseException e) {
			LOG.error("TimingMonitoringConfigView.isValids() is failure:{}",e);
		}
		
		if(beginDate.getTime() < new Date().getTime()){
			JOptionPane.showMessageDialog(this, "开始时间小于当前服务器时间，请重新输入", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		//相同监控设备，监控端口，参数下时间段不能有交集
		for (int row = 0; row < table.getRowCount(); row++) {
			TimerMonitoring timerMonitoring = tableModel.getSelectedRow(row);
			if (null != timerMonitoring) {
				String ip = timerMonitoring.getIpValue();
				String portStr = String.valueOf(timerMonitoring.getPortNo());
				String paramStr = timerMonitoring.getParam();

				if (ip.equalsIgnoreCase(device.toString())
						&& portStr.equalsIgnoreCase(String.valueOf(portNo))
						&& paramStr.equalsIgnoreCase(param.toString())
						&& ((timerMonitoring.getStartDate().getTime() <= beginDate.getTime() && beginDate.getTime() <= timerMonitoring.getEndDate().getTime())
								|| (endDate.getTime() >= timerMonitoring.getStartDate().getTime() && timerMonitoring.getEndDate().getTime() >= endDate.getTime())
								|| (beginDate.getTime() <= timerMonitoring.getStartDate().getTime() && endDate.getTime() >= timerMonitoring.getEndDate().getTime())
								)) {
					JOptionPane.showMessageDialog(this, "其他配置中的时间段与现时间段存在交集，请重新配置","提示", JOptionPane.NO_OPTION);
					isValid = false;
					return isValid;
				}
			}
		}

		return isValid;
	}
	
	/**
	 * 当开始时间大于结束时间时,返回false,否则返回true;
	 * @param beginText
	 * @param endText
	 * @return
	 */
	private boolean compareTime(String beginText,String endText){
		boolean bool = true;
		
		SimpleDateFormat  fm=new SimpleDateFormat(pattern); 
		Date beginTime = null;
		Date endTime = null;
		try {
			beginTime =fm.parse(beginText);
			endTime = fm.parse(endText);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		long begin = beginTime.getTime();
		long end = endTime.getTime();
		if (begin > end){
			return false;
		}
		
		return bool;
	}
	
	private class SwitchNodeEntityObject implements Serializable{
		private static final long serialVersionUID = 1L;
		SwitchNodeEntity switchNodeEntity = null;
		public SwitchNodeEntityObject(SwitchNodeEntity switchNodeEntity){
			this.switchNodeEntity = switchNodeEntity;
		}
		
		@Override
		public String toString(){
			String str = "";
			if (null != this.switchNodeEntity){
				str = this.switchNodeEntity.getBaseConfig().getIpValue();
			}
			return str;
		}

		public SwitchNodeEntity getSwitchNodeEntity() {
			return switchNodeEntity;
		}
	}
	
	class FormattedTextFieldVerifier extends InputVerifier { 
		ViewPart viewPart;
		public FormattedTextFieldVerifier(ViewPart viewPart){
			this.viewPart = viewPart;
		}
		
		@Override
		public boolean verify(JComponent input){ 
			if (input instanceof JFormattedTextField){ 
				JFormattedTextField ftf = (JFormattedTextField)input; 
				
				String text = ftf.getText(); 
//				System.out.println(text.trim()); 
				try { 
					SimpleDateFormat  fm=new SimpleDateFormat(pattern); 
					Date da=fm.parse(text); 
					if ((fm.format(da)).equals(text)) { 
						return true; 
					} 
					else { 
						Toolkit  kit=ftf.getToolkit(); 
						kit.beep(); 
						return false; 
					} 
				} 
				catch(Exception ee) {
					LOG.error("ConfigureFaultDetectionView.verify() error", ee);
					Toolkit  kit=ftf.getToolkit(); 
					kit.beep(); 
					return false; 
				} 
			} 
			return true; 
		} 
		@Override
		public boolean shouldYieldFocus(JComponent input){ 
			boolean bool = verify(input); 
			if (!bool){
//				JOptionPane.showMessageDialog(this.viewPart, "日期格式错误，正确的格式是'yyyy-MM-dd HH:mm'.","提示",JOptionPane.NO_OPTION);
				JOptionPane.showMessageDialog(this.viewPart, "日期错误,请重新输入.","提示",JOptionPane.NO_OPTION);
			}
			
			return verify(input); 
		} 
	}
	
	private class TimingTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private List<TimerMonitoring> TimerMonitoringEntityList;
		private TableColumnModelExt columnModel;
		
		public TimingTableModel(){
			TimerMonitoringEntityList = new ArrayList<TimerMonitoring>();

			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			
			TableColumnExt equipmentColumn = new TableColumnExt(modelIndex++,100);
			equipmentColumn.setIdentifier("MONITOR_EQUIPMENT");
			equipmentColumn.setTitle(localizationManager.getString("MONITOR_EQUIPMENT"));
			columnModel.addColumn(equipmentColumn);
			
			TableColumnExt portColumn = new TableColumnExt(modelIndex++,100);
			portColumn.setIdentifier("MONITOR_PORT");
			portColumn.setTitle(localizationManager.getString("MONITOR_PORT"));
			columnModel.addColumn(portColumn);
			
			TableColumnExt paramColumn = new TableColumnExt(modelIndex++,100);
			paramColumn.setIdentifier("PARAM");
			paramColumn.setTitle(localizationManager.getString("PARAM"));
			columnModel.addColumn(paramColumn);
			
			TableColumnExt startTimeColumn = new TableColumnExt(modelIndex++,100);
			startTimeColumn.setIdentifier("START_TIME");
			startTimeColumn.setTitle(localizationManager.getString("START_TIME"));
			columnModel.addColumn(startTimeColumn);
			
			TableColumnExt endTimeColumn = new TableColumnExt(modelIndex++,100);
			endTimeColumn.setIdentifier("END_TIME");
			endTimeColumn.setTitle(localizationManager.getString("END_TIME"));
			columnModel.addColumn(endTimeColumn);
		}
		
		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return TimerMonitoringEntityList.size();
		}
		
		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if(row < TimerMonitoringEntityList.size()){
				TimerMonitoring timerMonitoring = TimerMonitoringEntityList.get(row);
				switch (col) {
				case 0:
					value = timerMonitoring.getIpValue();
					break;
				case 1:
					value = timerMonitoring.getPortNo();
					break;
				case 2:
					value = timerMonitoring.getParam();
					break;
				case 3:
					value = dateFormatter.format(timerMonitoring.getStartDate(),pattern);
					break;
				case 4:
					value = dateFormatter.format(timerMonitoring.getEndDate(),pattern);
					break;
				default:
					break;
				}
			}
			return value;
		}
		
		@Override
		public String getColumnName(int col)
		{
			return columnModel.getColumnExt(col).getTitle();
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
		
		public TableColumnModel getColumnMode()
		{
			return columnModel;
		}
		
		public void setDataList(List<TimerMonitoring> dataList) {
			if (null == dataList){
				TimerMonitoringEntityList = new ArrayList<TimerMonitoring>();
			}
			else{
				this.TimerMonitoringEntityList = dataList;
			}
		}
		
		public List<TimerMonitoring> getSelectedRows(int[] selectedRows){
			List<TimerMonitoring> timerMonitoringEntityLists = new ArrayList<TimerMonitoring>();
			for(int i = 0;i < selectedRows.length;i++){
				timerMonitoringEntityLists.add(this.TimerMonitoringEntityList.get(selectedRows[i]));
			}
			return timerMonitoringEntityLists;
		}
		
		public TimerMonitoring getSelectedRow(int selectRow){
			TimerMonitoring timerMonitoring  = null;
			if (this.TimerMonitoringEntityList == null || this.TimerMonitoringEntityList.size() < 1){
				return null;
			}
			timerMonitoring = this.TimerMonitoringEntityList.get(selectRow);

			return timerMonitoring;
		}
	}
}