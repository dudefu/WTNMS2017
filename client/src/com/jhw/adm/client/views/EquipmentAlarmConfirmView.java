package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.CONFIRM;
import static com.jhw.adm.client.core.ActionConstants.EXPORT;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.table.TableColumnExt;
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
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.AlarmTypeCategory;
import com.jhw.adm.client.model.EquipmentAlarmModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.WarningConfirmStrategy;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.FileImportAndExport;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.WarningEntity;

@Component(EquipmentAlarmConfirmView.ID)
@Scope(Scopes.DESKTOP)
public class EquipmentAlarmConfirmView extends ViewPart {

	private static final long serialVersionUID = -1288837887099756518L;
	public static final String ID = "equipmentAlarmConfirmView";
	private static final Logger LOG = LoggerFactory.getLogger(EquipmentAlarmConfirmView.class);
	private static final int ZERO = 0;
	
	private JScrollPane scrollPane = new JScrollPane();
	private JXTable table = new JXTable();
	private EquipmentAlarmTableModel model = new EquipmentAlarmTableModel();
	private JTextArea contentArea;
	private JPanel resultPanel = new JPanel();
	private JPanel detailPanel = new JPanel();
	
	private ButtonFactory buttonFactory;
	private JPanel buttonPanel = new JPanel();
	private JButton confirmButton = null;
	private JButton exportButton = null;
	private JLabel messageLabel = null;
	private static final String MESSAGE = "告警总数：%s,选中告警数：%s";
	private boolean isSelected = false;
	
	private List<WarningEntity> warningConfirmedList = new ArrayList<WarningEntity>();
	private JCheckBox realTimeUpdateCheckBox = new JCheckBox("实时更新");
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=EquipmentAlarmModel.ID)
	private EquipmentAlarmModel equipmentModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name=DateFormatter.ID)
	private DateFormatter dateFormatter;
	
	@Resource(name=AlarmSeverity.ID)
	private AlarmSeverity alarmSeverity;
	
	@Resource(name=AlarmTypeCategory.ID)
	private AlarmTypeCategory alarmTypeCategory;
	
	private PropertyChangeListener equipmentAlarmListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateEquipmentAlarmTableData();
		}
	};
	
	private PropertyChangeListener equipmentAlarmUpdateAfterConfirmListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setTableData();
		}
	};
	
	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this);
		equipmentModel.addPropertyChangeListener(EquipmentAlarmModel.UPDATE_EMULATION_VIEW, equipmentAlarmListener);
		equipmentModel.addPropertyChangeListener(EquipmentAlarmModel.NOTICE_VIEW_UPDATE_AFTER_CONFIRM_ALARM, equipmentAlarmUpdateAfterConfirmListener);
		createResultPanel(resultPanel);
		createButtonPanel(buttonPanel);
		
		this.setLayout(new BorderLayout());
		this.add(resultPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setTitle("设备告警列表");
		this.setViewSize(600, 500);
		initializeTableData();
	}

	protected void updateEquipmentAlarmTableData() {
		if(isSelected == true){
			setTableData();
		}else if(isSelected = false){
			//do something maybe
		}
	}

	private void setTableData() {
		if(SwingUtilities.isEventDispatchThread()){
			model.setWarningAlarms(equipmentModel.getEquipmentAlam());
			model.fireTableDataChanged();
			setMessageLabel(equipmentModel.getEquipmentAlam().size(), ZERO);
			this.contentArea.setText("");
			String originIP = equipmentModel.getOriginIP();
			if(!StringUtils.isBlank(originIP)){
				this.setTitle(String.format("设备(%s)告警列表", originIP));
			}
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setTableData();
				}
			});
		}
	}

	private void createResultPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		createTablePanel(scrollPane);
		createMessageDetail(detailPanel);
		
		parent.add(scrollPane, BorderLayout.CENTER);
		parent.add(detailPanel, BorderLayout.SOUTH);
	}

	private void initializeTableData() {
		setTableData();
	}

	private void createButtonPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		JPanel messagePanel = new JPanel(new BorderLayout());
		
		messageLabel = new JLabel();
		setMessageLabel(ZERO, ZERO);
		
		realTimeUpdateCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					isSelected = true;
				}else if(e.getStateChange() == ItemEvent.DESELECTED){
					isSelected = false;
				}
			}
		});
		realTimeUpdateCheckBox.setSelected(true);
		
		messagePanel.add(messageLabel, BorderLayout.CENTER);
		messagePanel.add(realTimeUpdateCheckBox, BorderLayout.LINE_END);
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		confirmButton = buttonFactory.createButton(CONFIRM);
		exportButton = buttonFactory.createButton(EXPORT);
		panel.add(exportButton);
		panel.add(confirmButton);
		
		parent.add(messagePanel, BorderLayout.CENTER);
		parent.add(panel, BorderLayout.LINE_END);
	}
	
	private void createMessageDetail(JPanel parent) {
		parent.setLayout(new BorderLayout());
		parent.setBorder(BorderFactory.createTitledBorder("详细内容"));
		
		contentArea = new JTextArea(3, 1);
		contentArea.setEditable(false);
		contentArea.setBorder(new JTextField().getBorder());
		contentArea.setLineWrap(true);		
		JScrollPane scrollPane = new JScrollPane(contentArea);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		contentArea.setCaretPosition(0);
		parent.add(scrollPane, BorderLayout.CENTER);
	}
	
	private void setMessageLabel(final int totalMessage, final int selectedMessage){
		if(SwingUtilities.isEventDispatchThread()){
			messageLabel.setText(String.format(MESSAGE, totalMessage, selectedMessage));
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setMessageLabel(totalMessage,selectedMessage);
				}
			});
		}
	}
	
	private void selectOneRow() {
		if (table.getSelectedRow() > - 1) {
			WarningEntity warningEntity = model.getValue(table.getSelectedRow());
			contentArea.setText(warningEntity.getDescs());
		}
	}
	
	private void createTablePanel(JScrollPane parent) {
		
		parent.getHorizontalScrollBar().setFocusable(false);
		parent.getVerticalScrollBar().setFocusable(false);
		parent.getHorizontalScrollBar().setUnitIncrement(30);
		parent.getVerticalScrollBar().setUnitIncrement(30);
		
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(true);
		
		table.setModel(model);
		table.setColumnModel(model.getColumnMode());
		table.getTableHeader().setReorderingAllowed(false);
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				setMessageLabel(equipmentModel.getEquipmentAlam().size(), table.getSelectedRowCount());
				selectOneRow();
			}
		});

		parent.getViewport().add(table);
	}
	
	@ViewAction(name=CONFIRM, icon=ButtonConstants.SAVE, desc="确认设备告警信息",role=Constants.MANAGERCODE)
	public void confirm(){
		int selected = table.getSelectedRowCount();
		if(selected <= 0){
			JOptionPane.showMessageDialog(this, "请选择需要确认的告警信息", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int backSelected = JOptionPane.showConfirmDialog(this, "你确定确认所选择的告警信息", "提示", JOptionPane.OK_CANCEL_OPTION);
		if(backSelected != JOptionPane.OK_OPTION){
			return;
		}
		
		warningConfirmedList = model.getSelectedList(table.getSelectedRows());
		
		Task task = new RequestTask(warningConfirmedList);
		showMessageDialog(task);
	}
	
	private class RequestTask implements Task{

		private List<WarningEntity> list = null;
		
		public RequestTask(List<WarningEntity> list){
			this.list = list;
		}
		
		@Override
		public void run() {
			confirmedWarning = new ArrayList<WarningEntity>();
			unConfirmedWarning = new ArrayList<WarningEntity>();
			alarmModel.updateSelectedWarningAttributes(list,unConfirmedWarning,confirmedWarning);
			try{
				remoteServer.getNmsService().confirmWarningInfo(unConfirmedWarning);
			}catch(Exception e){
				strategy.showErrorMessage("确认设备告警异常");
				LOG.error("Error occur when confirming equipment warning(s)", e);
			}
			strategy.showNormalMessage(confirmedWarning.size(), unConfirmedWarning.size());
			alarmModel.confirmAlarm(model.getSelectedList(table.getSelectedRows()));
		}
	}
	
	private List<WarningEntity> confirmedWarning = null;
	private List<WarningEntity> unConfirmedWarning = null;
	private JProgressBarModel progressBarModel;
	private WarningConfirmStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new WarningConfirmStrategy("确认设备告警", progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,"确认设备告警",true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task);
				}
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=EXPORT, icon=ButtonConstants.EXPORT, desc="导出设备告警信息",role=Constants.MANAGERCODE)
	public void export(){
		List<WarningEntity> warninglist = equipmentModel.getEquipmentAlam();
		
		String[] propertiesName = new String[5];
		propertiesName[0] = "时间";
		propertiesName[1] = "来源";
		propertiesName[2] = "内容";
		propertiesName[3] = "级别";
		propertiesName[4] = "类型";
		List<HashMap> list = new ArrayList<HashMap>();
		for(WarningEntity warningEntity : warninglist){
			HashMap hashMap = new HashMap(); 
			hashMap.put(propertiesName[0], dateFormatter.format(warningEntity.getCreateDate()));
			hashMap.put(propertiesName[1], warningEntity.getNodeName());
			hashMap.put(propertiesName[2], warningEntity.getDescs());
			hashMap.put(propertiesName[3], alarmSeverity.get(warningEntity.getWarningLevel()).getKey());
			hashMap.put(propertiesName[4], alarmTypeCategory.get(warningEntity.getWarningCategory()).getKey());
			list.add(hashMap);
		}
		int result = FileImportAndExport.export(this, list, propertiesName);
		if(FileImportAndExport.SUCCESS_RESULT == result){
			JOptionPane.showMessageDialog(this, "设备告警信息导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
		}else if(FileImportAndExport.CANCLE_RESULT == result){
			//if cancle, no message dialog
		}else if(FileImportAndExport.FAILURE_RESULT == result){
			JOptionPane.showMessageDialog(this, "设备告警信息导出失败", "提示", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentAlarmModel.UPDATE_EMULATION_VIEW, equipmentAlarmListener);
		equipmentModel.removePropertyChangeListener(EquipmentAlarmModel.NOTICE_VIEW_UPDATE_AFTER_CONFIRM_ALARM, equipmentAlarmUpdateAfterConfirmListener);
	}
	
	private class EquipmentAlarmTableModel extends AbstractTableModel {
		
		public EquipmentAlarmTableModel() {
			localizationManager = ClientUtils.getSpringBean(LocalizationManager.ID);
			warningAlarms = new ArrayList<WarningEntity>();

			int modelIndex = 0;
			warningColumnModel = new DefaultTableColumnModel();
			
			//时间
			TableColumnExt raisedTimeColumn = new TableColumnExt(modelIndex++,150);
			raisedTimeColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_RAISED_TIME);
			raisedTimeColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_RAISED_TIME));
			warningColumnModel.addColumn(raisedTimeColumn);
			//内容
			TableColumnExt contentColumn = new TableColumnExt(modelIndex++, 150);
			contentColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_CONTENT);
			contentColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_CONTENT));
			warningColumnModel.addColumn(contentColumn);
			//级别
			TableColumnExt severityColumn = new TableColumnExt(modelIndex++, 150);
			severityColumn.setHighlighters(new WarningAlarmSeverityHighlighter());
			severityColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_SEVERITY);
			severityColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_SEVERITY));
			warningColumnModel.addColumn(severityColumn);
			//类型
			TableColumnExt equipmentTypeColumn = new TableColumnExt(modelIndex++, 150);
			equipmentTypeColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_EQUIPMENT_TYPE);
			equipmentTypeColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_EQUIPMENT_TYPE));
			warningColumnModel.addColumn(equipmentTypeColumn);
		}
		
		@Override
		public int getColumnCount() {
			return warningColumnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return warningAlarms.size();
		}
		
		public TableColumnModel getColumnMode(){
			return warningColumnModel;
		}

		@Override
		public String getColumnName(int col) {
			return warningColumnModel.getColumn(col).getHeaderValue()
					.toString();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if (row < warningAlarms.size()) {
				WarningEntity alarm = warningAlarms.get(row);
				switch (col) {
				case 0://时间
					value = dateFormatter.format(alarm.getCreateDate());
					break;
				case 1://内容
					value = alarm.getDescs();
					break;
				case 2://级别
					value = alarmSeverity.get(alarm.getWarningLevel()).getKey();
					break;
				case 3://类型
					value = alarmTypeCategory.get(alarm.getWarningCategory()).getKey();
					break;
				default:
					break;
				}
			}
			return value;
		}

		public TableColumnModel getColumnModel() {
			return warningColumnModel;
		}

		public WarningEntity getValue(int row) {
			WarningEntity value = null;
			if (row < warningAlarms.size()) {
				value = warningAlarms.get(row);
			}
			return value;
		}
		
		public List<WarningEntity> getSelectedList(int[] selectedRows){
			List<WarningEntity> selectedList = new ArrayList<WarningEntity>();
			if(selectedRows.length > 0){
				for(int selectedIndex : selectedRows){
					selectedList.add(this.warningAlarms.get(selectedIndex));
				}
			}
			return selectedList;
		}

		public void setWarningAlarms(List<WarningEntity> alarms) {
			if (alarms == null) {
				return;
			}
			this.warningAlarms = alarms;
		}

		@Override
		public void fireTableDataChanged(){
			if(SwingUtilities.isEventDispatchThread()){
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
		
		
		public EquipmentAlarmTableModel getModel(){
			return this;
		}
		
		private LocalizationManager localizationManager;
		private List<WarningEntity> warningAlarms;
		private final TableColumnModel warningColumnModel;
		private static final long serialVersionUID = -3065768803494840568L;
		
		private class WarningAlarmSeverityHighlighter extends AbstractHighlighter {
			@Override
			protected java.awt.Component doHighlight(java.awt.Component component,
					ComponentAdapter adapter) {
				WarningEntity warningEntity = getModel().getValue(adapter.row);
				if(warningEntity != null){
					component.setBackground(alarmSeverity.getColor(warningEntity.getWarningLevel()));
				}
				return component;
			}
			private static final long serialVersionUID = -8795784237395864733L;
		}

	}
	
}
