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
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.AlarmEvents;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.util.FileImportAndExport;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.TrapWarningBean;
import com.jhw.adm.server.entity.warning.SimpleWarning;

/**
 * �澯��Ϣ������ͼ�����Բ�ѯ���������и澯��Ϣ
 */
@Component(CurrentNodeWarningView.ID)
@Scope(Scopes.DESKTOP)
public class CurrentNodeWarningView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "currentNodeWarningView";
	
	private JTextArea contentArea;
	private JXTable table;
	private AlarmReportTableModel tableModel;
	
	private JXDatePicker fromPicker;
	
	private JXDatePicker thruPicker;
	
	private JComboBox levelCombox;
	
	private JComboBox equipmentBox;
	
	private JComboBox typeCombox;
	
	private SwitchNodeEntity switchNodeEntity;
	private String ipValue = "";
	private OLTEntity eponEntity;
	
	private final JComboBox pageCountBox = new JComboBox();
	
	private ButtonFactory buttonFactory;
	
	private JLabel statusLabel;
	private static final String STATUS_PATTERN = "��%sҳ��ÿҳ�����ʾ16���澯��Ϣ";
	private static final int PAGE_SIZE = 16;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
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
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@PostConstruct
	protected void initialize() {
		setTitle("��ǰ�ڵ�澯��Ϣ");
		setLayout(new BorderLayout());
		
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		if(null != switchNodeEntity){
			if (null != switchNodeEntity.getBaseConfig()) {
				ipValue = switchNodeEntity.getBaseConfig().getIpValue();
			}
		}
		
		eponEntity = (OLTEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), OLTEntity.class);
		if(null != eponEntity){
			if(eponEntity.getIpValue() != null){
				ipValue = eponEntity.getIpValue();
			}	
		}
		
		createContents(this);
		queryData();
	}
	
	private void createContents(JPanel parent) {
		buttonFactory = actionManager.getButtonFactory(this); 
		
		JPanel toolPanel = new JPanel(new GridLayout(2, 1));

		// **************************
		// ��ѯ���
		JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 250, 0));
		toolPanel.setBorder(BorderFactory.createTitledBorder("��ѯ����"));
		JLabel levelLbl = new JLabel("����");
		
		levelCombox = new JComboBox(new ListComboBoxModel(
				alarmSeverity.toListIncludeAll()));

		levelCombox.setSelectedIndex(0);
		JLabel ipLbl = new JLabel();
		ipLbl.setText("�豸");
		equipmentBox = new JComboBox();
		setComboxItem();

		JButton queryBtn = buttonFactory.createButton(QUERY);
		JButton exportBtn = buttonFactory.createButton(EXPORT);

		queryPanel.add(new JLabel("��ʼʱ��"));
		fromPicker = new JXDatePicker();
		fromPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(fromPicker);

		queryPanel.add(new JLabel("��"));
		thruPicker = new JXDatePicker();
		thruPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(thruPicker);

		queryPanel.add(levelLbl);
		queryPanel.add(levelCombox);

		queryPanel.add(ipLbl);
		queryPanel.add(equipmentBox);

		queryPanel.add(new JLabel("�¼�"));
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
		scrollTablePanel.setBorder(BorderFactory.createTitledBorder("�澯��Ϣ"));
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);

		JPanel detailPanel = new JPanel();
		createMessageDetail(detailPanel);
		detailPanel.setBorder(BorderFactory.createTitledBorder("��ϸ����"));

		resultPanel.add(scrollTablePanel, BorderLayout.CENTER);
		resultPanel.add(detailPanel, BorderLayout.PAGE_END);
		// ***************

		// ��ҳ����
		JPanel paginationPanel = new JPanel(new BorderLayout());
		JPanel pageButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

		statusLabel = new JLabel();
		statusLabel.setText(String.format(STATUS_PATTERN, 0));
		
		JButton firstBtn = buttonFactory.createButton(FIRST);
		JButton previousBtn = buttonFactory.createButton(PREVIOUS);
		JButton nextBtn = buttonFactory.createButton(NEXT);
		JButton lastBtn = buttonFactory.createButton(LAST); 

		pageButtonPanel.add(firstBtn);
		pageButtonPanel.add(previousBtn);
		pageButtonPanel.add(nextBtn);
		pageButtonPanel.add(lastBtn);
		pageButtonPanel.add(pageCountBox);
		
		paginationPanel.add(statusLabel, BorderLayout.CENTER);
		paginationPanel.add(pageButtonPanel, BorderLayout.LINE_END);

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
	
	private void setComboxItem(){
		equipmentBox.removeAllItems();
		equipmentBox.addItem(ipValue);
		equipmentBox.setSelectedItem(ipValue);
		equipmentBox.setEditable(false);
	}
	
	private void selectOneRow() {
		if (table.getSelectedRow() > - 1) {
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
	
	private void queryData(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				TrapWarningBean bean = new TrapWarningBean();
				bean.setStartDate(null);
				bean.setEndDate(null);
				bean.setIpValue(ipValue);
				bean.setLevel(-1);
				bean.setStatus(-1);
				bean.setWarningType(-1);
				bean.setUserName(clientModel.getCurrentUser().getUserName());
				
				//��ѯ���еļ�¼��
				long count = remoteServer.getNmsService().queryAllRowCount(bean);
				setPageBoxItem(count);
			}
		});
	}
	
	private void setTableValue(List<SimpleWarning> list){
		tableModel.setAlarms(list);
		tableModel.fireTableDataChanged();
	}
	
	@ViewAction(name=QUERY, icon=ButtonConstants.QUERY, desc="��ѯ��ǰ�ڵ�澯��Ϣ",role=Constants.MANAGERCODE)
	public void query() {
		Date startDate = fromPicker.getDate();
		Date endDate = thruPicker.getDate();
		int level = ((StringInteger)levelCombox.getSelectedItem()).getValue();
		int warningType = ((StringInteger)typeCombox.getSelectedItem()).getValue();
		
		TrapWarningBean bean = new TrapWarningBean();
		bean.setStartDate(startDate);
		bean.setEndDate(endDate);
		bean.setIpValue(ipValue);
		bean.setLevel(level);
		bean.setWarningType(warningType);
		bean.setUserName(clientModel.getCurrentUser().getUserName());
		
		long count = remoteServer.getNmsService().queryAllRowCount(bean);
		setPageBoxItem(count);
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=EXPORT, icon=ButtonConstants.EXPORT, desc="������ǰ�ڵ�澯��Ϣ",role=Constants.MANAGERCODE)
	public void export(){
		Date startDate = fromPicker.getDate();
		Date endDate = thruPicker.getDate();
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
		propertiesName[0] = "ʱ��";
		propertiesName[1] = "����";
		propertiesName[2] = "�豸";
		propertiesName[3] = "����";
		propertiesName[4] = "�¼�";
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
		FileImportAndExport.export(this, list, propertiesName);
	}
	
	@ViewAction(name=FIRST, icon=ButtonConstants.FIRST,desc="��ʾ�澯��Ϣ��һҳ",role=Constants.MANAGERCODE)
	public void first(){
		pageCountBox.setSelectedIndex(0);
	}
	
	@ViewAction(name=PREVIOUS, icon=ButtonConstants.PREVIOUS,desc="��ʾ�澯��Ϣ��һҳ",role=Constants.MANAGERCODE)
	public void previous(){
		if (pageCountBox.getSelectedIndex() <=0){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() -1);
	}
	
	@ViewAction(name=NEXT, icon=ButtonConstants.NEXT,desc="��ʾ�澯��Ϣ��һҳ",role=Constants.MANAGERCODE)
	public void next(){
		if (pageCountBox.getSelectedIndex() >= pageCountBox.getItemCount()-1){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() +1);
	}
	
	@ViewAction(name=LAST, icon=ButtonConstants.LAST,desc="��ʾ�澯��Ϣ���ҳ",role=Constants.MANAGERCODE)
	public void last(){
		pageCountBox.setSelectedIndex(pageCountBox.getItemCount()-1);
	}
	
	/**
	 * pageCountBox�ļ����¼�
	 */
	private void warnInfoAction(){
		int page = pageCountBox.getSelectedIndex()+1;
		
		int maxPageSize = PAGE_SIZE;
		
		Date startDate = fromPicker.getDate();
		Date endDate = thruPicker.getDate();
		int level = ((StringInteger)levelCombox.getSelectedItem()).getValue();
		int warningType = ((StringInteger)typeCombox.getSelectedItem()).getValue();
		
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
		setTableValue(list);
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