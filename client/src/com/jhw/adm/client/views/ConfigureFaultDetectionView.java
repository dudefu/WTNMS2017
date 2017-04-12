package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.MaskFormatter;

import org.apache.commons.lang.StringUtils;
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
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.SwitchRefreshInfoInter;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.FaultDetection;

@Component(ConfigureFaultDetectionView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureFaultDetectionView extends ViewPart implements SwitchRefreshInfoInter{
	private static final long serialVersionUID = 1L;
	public static final String ID = "configureFaultDetectionView";
	
	private JPanel topPnl = new JPanel();
	private JLabel autoDectionLbl = new JLabel();//主动探测
	private JCheckBox autoDectionChk = new JCheckBox();
	private JLabel waringNotifyLbl = new JLabel();//告警通知
	private JCheckBox waringNotifyChk = new JCheckBox();
	private JLabel beginDateLbl = new JLabel();//启动时间
	private JFormattedTextField beginDateFld = null;
	private JLabel endDateLbl = new JLabel();//结束时间
	private JFormattedTextField endDateFld = null;
	private JLabel detectionIntervalLbl = new JLabel();//探测频率（分钟）
	private NumberField detectionIntervalFld = null;
	private JLabel pingTimesLbl = new JLabel();//单个PING重复次数
	private NumberField pingTimesFld = null;
	private JLabel timeoutLbl = new JLabel();//延时时间（秒）
	private NumberField timeoutFld = null;
	
	private JLabel deviceLbl = new JLabel();
	private IpAddressField deviceField = new IpAddressField();
	
	private JPanel centerPnl = new JPanel();
	private JScrollPane scrollPnl = new JScrollPane();
	private JTable table = new JTable();
	private FaultDetectionTableModel tableModel = null;
	//
	private JPanel bottomPnl = new JPanel();
	private JButton deleteBtn;
	private JButton saveBtn ;
	private JButton closeBtn ;
	
	//保存所查询到的所有的交换机
	private List<SwitchTopoNodeEntityObject> switchList = new ArrayList<SwitchTopoNodeEntityObject>();
	
	private static final Logger LOG = LoggerFactory.getLogger(ConfigureFaultDetectionView.class);
	private ButtonFactory buttonFactory;
	

	private final static String[] COLUMN_NAME = {"告警通知","启动时间","结束时间","单个ping次数","延时时间","IP地址"};
	
	private final static String pattern = "yyyy-MM-dd HH:mm";
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;	
	
	@Autowired
	@Qualifier(DateFormatter.ID)
	private DateFormatter format;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initTopPnl();
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setViewSize(640, 480);
		
		setResource();
	}
	
	private void initTopPnl(){
		MaskFormatter beginMf = null;
		MaskFormatter endMf = null;
		try {
			beginMf = new MaskFormatter("####-##-## ##:##");
			beginMf.setPlaceholderCharacter('_');

			endMf = new MaskFormatter("####-##-## ##:##");
			endMf.setPlaceholderCharacter('_');
		} catch (ParseException e) {
			LOG.error("ConfigureFaultDetectionView.initTopPnl() error", e);
		}
		
		beginDateFld = new JFormattedTextField(beginMf);
		endDateFld = new JFormattedTextField(endMf);
		
		detectionIntervalFld = new NumberField();
		pingTimesFld = new NumberField();
		timeoutFld = new NumberField();
		detectionIntervalFld.setHorizontalAlignment(JTextField.LEFT);
		pingTimesFld.setHorizontalAlignment(JTextField.LEFT);
		timeoutFld.setHorizontalAlignment(JTextField.LEFT);

		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel middlePnl = new JPanel(new GridBagLayout());
		
		middlePnl.add(autoDectionLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		middlePnl.add(autoDectionChk,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		
		middlePnl.add(waringNotifyLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		middlePnl.add(waringNotifyChk,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		
		middlePnl.add(beginDateLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		middlePnl.add(beginDateFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		middlePnl.add(new StarLabel(),new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		
		middlePnl.add(endDateLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		middlePnl.add(endDateFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		middlePnl.add(new StarLabel(),new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		
		middlePnl.add(detectionIntervalLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		middlePnl.add(detectionIntervalFld,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		
		middlePnl.add(pingTimesLbl,new GridBagConstraints(0,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		middlePnl.add(pingTimesFld,new GridBagConstraints(1,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		middlePnl.add(new StarLabel(),new GridBagConstraints(2,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		
		middlePnl.add(timeoutLbl,new GridBagConstraints(0,6,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		middlePnl.add(timeoutFld,new GridBagConstraints(1,6,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		middlePnl.add(new StarLabel(),new GridBagConstraints(2,6,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		
		middlePnl.add(deviceLbl,new GridBagConstraints(0,7,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		middlePnl.add(deviceField,new GridBagConstraints(1,7,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		middlePnl.add(new StarLabel(),new GridBagConstraints(2,7,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		
		topPnl.add(middlePnl);
	}
	
	private void initCenterPnl(){
		initTablePnl();
		JPanel topCenterPnl = new JPanel();

		topCenterPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(SAVE);
		deleteBtn = buttonFactory.createButton(DELETE);
		topCenterPnl.add(saveBtn);
		topCenterPnl.add(deleteBtn);
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(topCenterPnl,BorderLayout.NORTH);
		centerPnl.add(scrollPnl,BorderLayout.CENTER);
	}
	private void initTablePnl(){
		tableModel = new FaultDetectionTableModel();
		tableModel.setColumn(COLUMN_NAME);
		table.setModel(tableModel);
		setTableWidth();
//		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
//		table.setRowSorter(sorter);
//		sorter.toggleSortOrder(0);
		scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(table);
		scrollPnl.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	private void setTableWidth(){
		table.getColumn(COLUMN_NAME[0]).setPreferredWidth(80);
		table.getColumn(COLUMN_NAME[1]).setPreferredWidth(130);
		table.getColumn(COLUMN_NAME[2]).setPreferredWidth(130);
		table.getColumn(COLUMN_NAME[3]).setPreferredWidth(100);
		table.getColumn(COLUMN_NAME[4]).setPreferredWidth(80);
		table.getColumn(COLUMN_NAME[5]).setPreferredWidth(100);

	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}

	private void setResource(){
		setTitle("故障探测设置");
		waringNotifyLbl.setText("告警通知");
		beginDateLbl.setText("启动时间");
		endDateLbl.setText("结束时间");
		detectionIntervalLbl.setText("探测频率（分钟）");
		pingTimesLbl.setText("单个PING重复次数");
		timeoutLbl.setText("延时时间（秒）");
		deviceLbl.setText("IP地址");
		
		beginDateFld.setColumns(20);
		
		detectionIntervalFld.setText("1");
		detectionIntervalLbl.setVisible(false);
		detectionIntervalFld.setVisible(false);
		
		autoDectionChk.setSelected(true);
		autoDectionLbl.setVisible(true);
		autoDectionChk.setVisible(false);
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		//从数据库中查询故障探测设置
		List<FaultDetection> faultDetectionList = (List<FaultDetection>)remoteServer.getService().findAll(FaultDetection.class);
		if (null == faultDetectionList){
			return;
		}
		//通过得到的值设置视图
		setValue(faultDetectionList);
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(List<FaultDetection> faultDetectionList){
		if (faultDetectionList.size() < 1){
			tableModel.setDataList(null);
			tableModel.fireTableDataChanged();
			return;
		}
		
		List<List> dataList = new ArrayList<List>();
		for (int i = 0 ; i < faultDetectionList.size(); i++){
			FaultDetection faultDetection = faultDetectionList.get(i);
			
			boolean isDetection = faultDetection.isStarted();
			boolean isNotify = faultDetection.isWarning();
			Date beginDate = faultDetection.getBeginTime();
			Date endDate = faultDetection.getEndTime();
			int interval = faultDetection.getPinglv();//探测频率
			int pingTimes = faultDetection.getPingtimes();//单个PING重复次数
			int timeOut = faultDetection.getJiange();//延时时间（秒）
			String ipAddrStr = faultDetection.getIpvlaues(); //探测IP
			
			List rowList = new ArrayList();
			rowList.add(String.valueOf(isNotify));
			rowList.add(format.format(beginDate));
			rowList.add(format.format(endDate));
			rowList.add(pingTimes);
			rowList.add(timeOut);
			rowList.add(ipAddrStr);
			rowList.add(faultDetection);
			
			dataList.add(rowList);
		}
		
		tableModel.setDataList(dataList);
		tableModel.fireTableDataChanged();
	}
	
	private boolean isValids(){
		boolean isValid = true;
		
		String beginText = beginDateFld.getText();
		String endText = endDateFld.getText();
		FormattedTextFieldVerifier verifier = new FormattedTextFieldVerifier(this);
		boolean boolBeginDate = verifier.shouldYieldFocus(beginDateFld);
		if (!boolBeginDate){
			isValid = false;
			return isValid;
		}
	
		boolean boolEndDate = verifier.shouldYieldFocus(endDateFld);
		if (!boolEndDate){
			isValid = false;
			return isValid;
		}
		
		if(StringUtils.isBlank(beginText)){
			JOptionPane.showMessageDialog(this, "启动时间不能为空，请输入启动时间", "提示", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}
		if (StringUtils.isBlank(endText)) {
			JOptionPane.showMessageDialog(this, "结束时间不能为空，请输入结束时间", "提示", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}
		
		SimpleDateFormat format =new SimpleDateFormat(pattern);
		Date beginDate = null;
		Date endDate = null;
		try {
			beginDate = format.parse(beginText);
			endDate = format.parse(endText);
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
		
		//相同IP地址下时间段不能有交集
		for (int row = 0; row < table.getRowCount(); row++) {
			FaultDetection faultDetection = (FaultDetection) tableModel.getValueAt(row, tableModel.getColumnCount());
			if (null != faultDetection) {
				String ip = faultDetection.getIpvlaues();

				if (ip.equalsIgnoreCase(deviceField.getIpAddress())
						&& ((faultDetection.getBeginTime().getTime() <= beginDate.getTime() && beginDate.getTime() <= faultDetection.getEndTime().getTime())
								|| (endDate.getTime() >= faultDetection.getBeginTime().getTime() && faultDetection.getEndTime().getTime() >= endDate.getTime())
								|| (beginDate.getTime() <= faultDetection.getBeginTime().getTime() && endDate.getTime() >= faultDetection.getEndTime().getTime())
								)) {
					JOptionPane.showMessageDialog(this, "其他配置中的时间段与现时间段存在交集，请重新配置","提示", JOptionPane.NO_OPTION);
					isValid = false;
					return isValid;
				}
			}
		}
		
		if(null == detectionIntervalFld.getText() || "".equals(detectionIntervalFld.getText())){
			JOptionPane.showMessageDialog(this, "探测频率不能为空，请输入探测频率", "提示", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}else if(NumberUtils.toInt(detectionIntervalFld.getText()) == 0){
			JOptionPane.showMessageDialog(this, "探测频率不能为零，请输入探测频率", "提示", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}
		if(null == pingTimesFld.getText() || "".equals(pingTimesFld.getText())){
			JOptionPane.showMessageDialog(this, "单个PING重复次数不能为空，请输入单个PING重复次数", "提示", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}else if(NumberUtils.toInt(pingTimesFld.getText()) == 0){
			JOptionPane.showMessageDialog(this, "单个PING重复次数不能为零，请输入单个PING重复次数", "提示", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;
		}
		if(null == timeoutFld.getText() || "".equals(timeoutFld.getText())){
			JOptionPane.showMessageDialog(this, "延时时间不能为空，请输入延时时间", "提示", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;	
		}else if(NumberUtils.toInt(timeoutFld.getText()) == 0){
			JOptionPane.showMessageDialog(this, "延时时间不能为零，请输入延时时间", "提示", JOptionPane.NO_OPTION);
			isValid =false;
			return isValid;	
		}
		if (StringUtils.isBlank(deviceField.getText().trim())) {
			JOptionPane.showMessageDialog(this, "请输入IP地址", ClientUtils.getAppName(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		NodeEntity topoNodeEntity = getNodeEntity();
		if(null == topoNodeEntity){
			JOptionPane.showMessageDialog(this, "该设备不存在，请重新输入IP地址", ClientUtils.getAppName(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return isValid;
	}
	
	private NodeEntity getNodeEntity(){
		NodeEntity topoNodeEntity = null;
		String deviceIP = deviceField.getText().trim();
		
		topoNodeEntity = remoteServer.getService().findSwitchTopoNodeByIp(deviceIP);   //二层
		if (topoNodeEntity == null){
			topoNodeEntity = remoteServer.getService().findVirtualNodeByIp(deviceIP);  //虚拟网元
		}
		if (topoNodeEntity == null){
			topoNodeEntity = remoteServer.getService().findOLTTopoNodeByIp(deviceIP);  //OLT
		}
		if (topoNodeEntity == null){
			topoNodeEntity = remoteServer.getService().findSwitchTopoNodeLevel3ByIp(deviceIP); //三层
		}
		
		return topoNodeEntity;
	}
	
	/**
	 * 保存按钮事件
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE,desc="保存故障探测设置",role=Constants.MANAGERCODE)
	public void save(){
		if (!isValids()){
			return;
		}
		
		autoDectionChk.setSelected(true);
		boolean isDection = autoDectionChk.isSelected();
		boolean isNotify = waringNotifyChk.isSelected();
		
		SimpleDateFormat  format =new SimpleDateFormat(pattern); 
		Date beginDate = null;
		Date endDate = null;
		try {
			beginDate = format.parse(beginDateFld.getText());
			endDate = format.parse(endDateFld.getText());
		} catch (ParseException e) {
			LOG.error("ConfigureFaultDetectionView.save() error", e);
		} 

		detectionIntervalFld.setText("1");
		int interval = NumberUtils.toInt(detectionIntervalFld.getText());
		int pingTimes = NumberUtils.toInt(pingTimesFld.getText());
		int timeOut = NumberUtils.toInt(timeoutFld.getText());
		
		String ipAddr = deviceField.getIpAddress();
		
		FaultDetection faultDetection = new FaultDetection();
		faultDetection.setStarted(isDection);
		faultDetection.setWarning(isNotify);
		faultDetection.setBeginTime(beginDate);
		faultDetection.setEndTime(endDate);
		faultDetection.setPinglv(interval);
		faultDetection.setPingtimes(pingTimes);
		faultDetection.setJiange(timeOut);
		faultDetection.setIpvlaues(ipAddr);

		Task task = new RequestTask(faultDetection);
		showMessageDialog(task, "保存");
	}
	
	private class RequestTask implements Task{
		
		private FaultDetection faultDetection = null;
		public RequestTask(FaultDetection faultDetection){
			this.faultDetection = faultDetection;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getPingTimerService().updateFaultDetection(faultDetection);
			}catch(Exception e){
				strategy.showErrorMessage("保存网管侧异常");
				LOG.error("SysLogHostEntity.append() error", e);
			}
			strategy.showNormalMessage("保存网管侧成功");
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
	
	/**
	 * 删除按钮事件
	 */
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE,desc="删除故障探测设置",role=Constants.MANAGERCODE)
	public void delete(){
		int[] rows = table.getSelectedRows();
		if (rows.length < 1){
			return;
		}
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		List<FaultDetection> list = new ArrayList<FaultDetection>();
		for (int i = 0; i < rows.length; i++){
			FaultDetection faultDetection = (FaultDetection)tableModel.getValueAt(rows[i], tableModel.getColumnCount());
			list.add(faultDetection);
		}
		
		Task task = new DeleteRequestTask(list);
		showMessageDialog(task, "删除");
	}
	
	private class DeleteRequestTask implements Task{
		
		private List<FaultDetection> list = null;
		public DeleteRequestTask(List<FaultDetection> list){
			this.list = list;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().deleteEntities(list);
			}catch(Exception e){
				strategy.showErrorMessage("删除网管侧异常");
				LOG.error("SysLogHostEntity.append() error", e);
			}
			strategy.showNormalMessage("删除网管侧成功");
			queryData();
		}
	}
	
	private class SwitchTopoNodeEntityObject implements Serializable{
		private static final long serialVersionUID = 1L;
		SwitchTopoNodeEntity switchTopoNodeEntity = null;
		public SwitchTopoNodeEntityObject(SwitchTopoNodeEntity switchTopoNodeEntity){
			this.switchTopoNodeEntity = switchTopoNodeEntity;
		}
		
		@Override
		public String toString(){
			if(null != switchTopoNodeEntity){
				return this.switchTopoNodeEntity.getIpValue();
			}else{
				return null;
			}
		}

		public SwitchTopoNodeEntity getSwitchNodeEntity() {
			return switchTopoNodeEntity;
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
				try { 
					SimpleDateFormat  fm=new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
					
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

	 @SuppressWarnings("unchecked")
	class FaultDetectionTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private String[] column = null;
		private List<List> dataList = null;

		public FaultDetectionTableModel(){
			
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
	    
		public FaultDetection getSelectedRow(int selectRow){
			List timerMonitoring  = null;
			if (this.dataList == null || this.dataList.size() < 1){
				return null;
			}
			timerMonitoring = this.dataList.get(selectRow);

			return (FaultDetection) timerMonitoring;
		}

	}

	@Override
	public void refresh(List list) {
		// TODO Auto-generated method stub
		
	}
}