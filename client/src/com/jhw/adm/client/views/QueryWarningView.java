package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.EXPORT;
import static com.jhw.adm.client.core.ActionConstants.FIRST;
import static com.jhw.adm.client.core.ActionConstants.LAST;
import static com.jhw.adm.client.core.ActionConstants.NEXT;
import static com.jhw.adm.client.core.ActionConstants.PREVIOUS;
import static com.jhw.adm.client.core.ActionConstants.QUERY;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
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
import com.jhw.adm.client.model.AlarmEvents;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.util.FileImportAndExport;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.TrapWarningBean;
import com.jhw.adm.server.entity.warning.SimpleWarning;

/**
 * 告警信息管理视图，可以查询、导出所有告警信息
 */
@Component(QueryWarningView.ID)
@Scope(Scopes.DESKTOP)
public class QueryWarningView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "queryWarningView";
	
	private static final String RAISED_TIME_PATTERN = "yyyy:MM:dd HH:mm:ss";
	
	private JTextArea contentArea;
	private JXTable table;
	private AlarmReportTableModel tableModel;
	
	private JXDatePicker fromPicker;
	
	private JXDatePicker thruPicker;
	
	private JComboBox levelCombox;
	
	private JTextField equipmentField;
	
	private JComboBox typeCombox;
	
	private final JComboBox pageCountBox = new JComboBox();
	
	private ButtonFactory buttonFactory;
	
	private JLabel statusLabel;
	private static final String STATUS_PATTERN = "共%s页，每页最多显示16条告警信息";
	private static final int PAGE_SIZE = 16;
	
	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name = LocalizationManager.ID)
	private LocalizationManager localizationManager;

	@Resource(name = DateFormatter.ID)
	private DateFormatter dataFormatter;

	@Resource(name = AlarmSeverity.ID)
	private AlarmSeverity alarmSeverity;

	@Resource(name = AlarmEvents.ID)
	private AlarmEvents alarmEvents;
	
	@PostConstruct
	protected void initialize() {
		setTitle("告警信息管理");
		setLayout(new BorderLayout());
		createContents(this);
	}
	
	private void createContents(JPanel parent) {
		buttonFactory = actionManager.getButtonFactory(this); 
		
		JPanel toolPanel = new JPanel(new GridLayout(2, 1));

		// **************************
		// 查询面板
		JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 250, 0));
		toolPanel.setBorder(BorderFactory.createTitledBorder("查询条件"));
		JLabel levelLbl = new JLabel("级别");
		
		levelCombox = new JComboBox(new ListComboBoxModel(
				alarmSeverity.toListIncludeAll()));

		levelCombox.setSelectedIndex(0);
		JLabel ipLbl = new JLabel();
		ipLbl.setText("设备");
		equipmentField = new JTextField(20);

		JButton queryBtn = buttonFactory.createButton(QUERY);
		JButton exportBtn = buttonFactory.createButton(EXPORT);

		queryPanel.add(new JLabel("开始时间"));
		fromPicker = new JXDatePicker();
		fromPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(fromPicker);

		queryPanel.add(new JLabel("到"));
		thruPicker = new JXDatePicker();
		thruPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(thruPicker);

		queryPanel.add(levelLbl);
		queryPanel.add(levelCombox);

		queryPanel.add(ipLbl);
		queryPanel.add(equipmentField);

		queryPanel.add(new JLabel("事件"));
		ListComboBoxModel boxModel = new ListComboBoxModel(alarmEvents.toListIncludeAll());
		typeCombox = new JComboBox(boxModel);
		typeCombox.setSelectedIndex(0);
		queryPanel.add(typeCombox);

		buttonPnl.add(exportBtn);
		buttonPnl.add(queryBtn);

		toolPanel.add(queryPanel);
		toolPanel.add(buttonPnl);

		table = new JXTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(true);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// System.out.println(e.getLastIndex());
						selectOneRow();
					}
				});
		tableModel = new AlarmReportTableModel();
		table.setModel(tableModel);
		table.setColumnModel(tableModel.getColumnModel());
		table.getTableHeader().setReorderingAllowed(false);

		JPanel tabelPanel = new JPanel(new BorderLayout(1, 2));
		tabelPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tabelPanel.add(table, BorderLayout.CENTER);

		JPanel panel = new JPanel(new BorderLayout(1, 2));
		panel.add(tabelPanel, BorderLayout.CENTER);

		JPanel resultPanel = new JPanel(new BorderLayout());

		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.setBorder(BorderFactory.createTitledBorder("查询结果"));
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);

		JPanel detailPanel = new JPanel();
		createMessageDetail(detailPanel);
		detailPanel.setBorder(BorderFactory.createTitledBorder("详细内容"));

		resultPanel.add(scrollTablePanel, BorderLayout.CENTER);
		resultPanel.add(detailPanel, BorderLayout.PAGE_END);
		// ***************

		// 分页画面
		JPanel paginationPanel = new JPanel(new BorderLayout());
		JPanel pageButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

		statusLabel = new JLabel();
		statusLabel.setText(String.format(STATUS_PATTERN, 0));
		
		JButton firstBtn = buttonFactory.createButton(FIRST);
		JButton previousBtn = buttonFactory.createButton(PREVIOUS);
		JButton nextBtn = buttonFactory.createButton(NEXT);
		JButton lastBtn = buttonFactory.createButton(LAST); 

		paginationPanel.add(statusLabel, BorderLayout.CENTER);
		paginationPanel.add(pageButtonPanel, BorderLayout.LINE_END);
		
		pageButtonPanel.add(firstBtn);
		pageButtonPanel.add(previousBtn);
		pageButtonPanel.add(nextBtn);
		pageButtonPanel.add(lastBtn);
		pageButtonPanel.add(pageCountBox);
		
		pageCountBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						warnInfoAction();
					}
				});
			}
		});
		
		parent.add(toolPanel, BorderLayout.NORTH);
		parent.add(resultPanel, BorderLayout.CENTER);
		parent.add(paginationPanel, BorderLayout.SOUTH);
	}
	
	private void selectOneRow() {
		if (table.getSelectedRow() > - 1) {
//			alarmModel.changeSelected(tableModel.getValue(table.getSelectedRow()));
			SimpleWarning warning = tableModel.getValue(table.getSelectedRow());
			contentArea.setText(warning.getContent());
		}
	}
	
	private void setPageBoxItem(long count){
		long pageSize = PAGE_SIZE;
		long page = 0; 
		if(count%pageSize ==0){
			page = count/pageSize;
		}
		else{
			page = count/pageSize + 1;
		}
		
		pageCountBox.removeAllItems();
		for(int i = 1 ; i <= page; i++){
			pageCountBox.addItem(i);
		}
		statusLabel.setText(String.format(STATUS_PATTERN, page));
	}

	private void createMessageDetail(JPanel parent) {
		parent.setLayout(new BorderLayout());
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
	
	/**
	 * 
	 */
	private void queryData(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				TrapWarningBean bean = new TrapWarningBean();
				bean.setStartDate(null);
				bean.setEndDate(null);
				bean.setIpValue("");
				bean.setLevel(-1);
				bean.setStatus(-1);
				bean.setWarningType(-1);
				bean.setUserName(clientModel.getCurrentUser().getUserName());
				
				//查询所有的记录数
				long count = remoteServer.getNmsService().queryAllRowCount(bean);
				setPageBoxItem(count);
			}
		});
	}
	
	private void setTableValue(List<SimpleWarning> list){
		tableModel.setAlarms(list);
		tableModel.fireTableDataChanged();
	}
	
	@ViewAction(name=QUERY, icon=ButtonConstants.QUERY, desc="查询告警信息",role=Constants.MANAGERCODE)
	public void query() {
		
//		//查询之前先清空table中的数据
//		setTableValue(null);
		
		Date startDate = fromPicker.getDate();
		Date endDate = thruPicker.getDate();
		if ((startDate != null) && (endDate != null)){
			long startTime = startDate.getTime();
			long endTime = endDate.getTime();
			if (startTime > endTime){
				JOptionPane.showMessageDialog(this, "开始时间大于结束时间，请重新输入","提示",JOptionPane.NO_OPTION);
				return;
			}
		}
		int level = ((StringInteger)levelCombox.getSelectedItem()).getValue();
		String ipValue = equipmentField.getText().trim();
		int warningType = ((StringInteger)typeCombox.getSelectedItem()).getValue();
		
		TrapWarningBean bean = new TrapWarningBean();
		bean.setStartDate(startDate);
		bean.setEndDate(endDate);
		bean.setIpValue(ipValue);
		bean.setLevel(level);
		bean.setWarningType(warningType);
		bean.setUserName(clientModel.getCurrentUser().getUserName());
		
		long count = remoteServer.getNmsService().queryAllRowCount(bean);
//		if(count > 0){
			setPageBoxItem(count);
//		}else{
//			setTableValue(new ArrayList<SimpleWarning>());
//		}
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=EXPORT, icon=ButtonConstants.EXPORT, desc="导出告警信息",role=Constants.MANAGERCODE)
	public void export(){
		Date startDate = fromPicker.getDate();
		Date endDate = thruPicker.getDate();
		String ipValue = equipmentField.getText().trim();
		int level = ((StringInteger)levelCombox.getSelectedItem()).getValue();
		int warningType = ((StringInteger)typeCombox.getSelectedItem()).getValue();
		
		TrapWarningBean bean = new TrapWarningBean();
		bean.setStartDate(startDate);
		bean.setEndDate(endDate);
		bean.setIpValue(ipValue);
		bean.setLevel(level);
		bean.setWarningType(warningType);
		bean.setUserName(clientModel.getCurrentUser().getUserName());
		
		bean.setMaxPageSize(0);
		bean.setStartPage(0);
		
		List<SimpleWarning> simpleWarnings = remoteServer.getNmsService().findWarningInfo(bean);
		
		String[] propertiesName = new String[5];
		propertiesName[0] = "时间";
		propertiesName[1] = "级别";
		propertiesName[2] = "设备";
		propertiesName[3] = "内容";
		propertiesName[4] = "事件";
		List<HashMap> list = new ArrayList<HashMap>();
	
		for(int i = 0;i < simpleWarnings.size();i++){
			HashMap hashMap = new HashMap();
			hashMap.put(propertiesName[0], dataFormatter.format(simpleWarnings.get(i).getTime()));
			hashMap.put(propertiesName[1], alarmSeverity.get(simpleWarnings.get(i).getWarningLevel()).getKey());
			hashMap.put(propertiesName[2], simpleWarnings.get(i).getIpValue());
			hashMap.put(propertiesName[3], simpleWarnings.get(i).getContent());
			hashMap.put(propertiesName[4], alarmEvents.get(simpleWarnings.get(i).getWarningType()).getKey());
			list.add(hashMap);
		}
		int result = FileImportAndExport.export(this, list, propertiesName);
		if(FileImportAndExport.SUCCESS_RESULT == result){
			JOptionPane.showMessageDialog(this, "日志导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
		}else if(FileImportAndExport.CANCLE_RESULT == result){
			//if cancle, no message dialog
		}else if(FileImportAndExport.FAILURE_RESULT == result){
			JOptionPane.showMessageDialog(this, "日志导出失败", "提示", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@ViewAction(name=FIRST, icon=ButtonConstants.FIRST,desc="显示告警信息第一页",role=Constants.MANAGERCODE)
	public void first(){
		pageCountBox.setSelectedIndex(0);
	}
	
	@ViewAction(name=PREVIOUS, icon=ButtonConstants.PREVIOUS,desc="显示告警信息上一页",role=Constants.MANAGERCODE)
	public void previous(){
		if (pageCountBox.getSelectedIndex() <=0){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() -1);
	}
	
	@ViewAction(name=NEXT, icon=ButtonConstants.NEXT,desc="显示告警信息下一页",role=Constants.MANAGERCODE)
	public void next(){
		if (pageCountBox.getSelectedIndex() >= pageCountBox.getItemCount()-1){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() +1);
	}
	
	@ViewAction(name=LAST, icon=ButtonConstants.LAST,desc="显示告警信息最后页",role=Constants.MANAGERCODE)
	public void last(){
		pageCountBox.setSelectedIndex(pageCountBox.getItemCount()-1);
	}
	
	/**
	 * pageCountBox的监听事件
	 */
	private void warnInfoAction(){
		int page = pageCountBox.getSelectedIndex()+1;
		
		Date startDate = fromPicker.getDate();
		Date endDate = thruPicker.getDate();
		int level = ((StringInteger)levelCombox.getSelectedItem()).getValue();
		String ipValue = equipmentField.getText().trim();
		int warningType = ((StringInteger)typeCombox.getSelectedItem()).getValue();
		
		int maxPageSize = PAGE_SIZE;
		
		
		TrapWarningBean bean = new TrapWarningBean();
		bean.setStartDate(startDate);
		bean.setEndDate(endDate);
		bean.setIpValue(ipValue);
		bean.setLevel(level);
		bean.setWarningType(warningType);
		bean.setUserName(clientModel.getCurrentUser().getUserName());
		bean.setMaxPageSize(maxPageSize);
		bean.setStartPage(page);
		
		List<SimpleWarning> list = remoteServer.getNmsService().findWarningInfo(bean);
		if(null != list){
			setTableValue(list);
		}else{
			setTableValue(new ArrayList<SimpleWarning>());
		}
		contentArea.setText("");
	}

	public class AlarmSeverityHighlighter extends AbstractHighlighter {
		@Override
		protected java.awt.Component doHighlight(java.awt.Component component,
				ComponentAdapter adapter) {
			SimpleWarning sw = tableModel.getValue(adapter.row);
			component.setBackground(alarmSeverity
					.getColor(sw.getWarningLevel()));
			return component;
		}

		private static final long serialVersionUID = -8795784237395864733L;
	}

	public class AlarmReportTableModel extends AbstractTableModel {
		private List<SimpleWarning> alarms;
		private final TableColumnModelExt columnModel;
		private static final long serialVersionUID = -3065768803494840568L;
		
		public AlarmReportTableModel() {
			alarms = Collections.emptyList();

			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			TableColumnExt raisedTimeColumn = new TableColumnExt(modelIndex++, 160);
			raisedTimeColumn
					.setIdentifier(ResourceConstants.ALARM_COLUMN_RAISED_TIME);
			raisedTimeColumn.setTitle(localizationManager
					.getString(ResourceConstants.ALARM_COLUMN_RAISED_TIME));
			columnModel.addColumn(raisedTimeColumn);

			TableColumnExt severityColumn = new TableColumnExt(modelIndex++, 80);
			severityColumn.setHighlighters(new AlarmSeverityHighlighter());
			severityColumn
					.setIdentifier(ResourceConstants.ALARM_COLUMN_SEVERITY);
			severityColumn.setTitle(localizationManager
					.getString(ResourceConstants.ALARM_COLUMN_SEVERITY));
			columnModel.addColumn(severityColumn);

			TableColumnExt equipmentColumn = new TableColumnExt(modelIndex++, 180);
			equipmentColumn
					.setIdentifier(ResourceConstants.ALARM_COLUMN_EQUIPMENT);
			equipmentColumn.setTitle(localizationManager
					.getString(ResourceConstants.ALARM_COLUMN_EQUIPMENT));
			columnModel.addColumn(equipmentColumn);

			TableColumnExt contentColumn = new TableColumnExt(modelIndex++, 250);
			contentColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_CONTENT);
			contentColumn.setTitle(localizationManager
					.getString(ResourceConstants.ALARM_COLUMN_CONTENT));
			columnModel.addColumn(contentColumn);

			TableColumnExt eventColumn = new TableColumnExt(modelIndex++, 100);
			eventColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_EVENT);
			eventColumn.setTitle(localizationManager
					.getString(ResourceConstants.ALARM_COLUMN_EVENT));
			columnModel.addColumn(eventColumn);

		}

		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return alarms.size();
		}

		@Override
		public String getColumnName(int col) {
			return columnModel.getColumnExt(col).getTitle();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if(alarms != null){
				if (row < alarms.size()) {
					SimpleWarning alarm = alarms.get(row);
					switch (col) {
					case 0:
						value = dataFormatter.format(alarm.getTime());
						break;
					case 1:
						value = alarmSeverity.get(alarm.getWarningLevel()).getKey();
						break;
					case 2:
						value = alarm.getIpValue();
						break;
					case 3:
						value = alarm.getContent();
						break;
					case 4:
						value = alarmEvents.get(alarm.getWarningType()).getKey();
						break;
					default:
						break;
					}
				}
			}
			return value;
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public TableColumnModel getColumnModel() {
			return columnModel;
		}

		public SimpleWarning getValue(int row) {
			SimpleWarning value = null;
			if (row < alarms.size()) {
				value = alarms.get(row);
			}

			return value;
		}

		public void setAlarms(List<SimpleWarning> data) {
			if (data == null)
				return;
			this.alarms = data;
		}
	}
}