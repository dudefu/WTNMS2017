package com.jhw.adm.client.views.epon;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.table.TableColumnExt;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ActionConstants;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.AlarmEvents;
import com.jhw.adm.client.model.AlarmMessage;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.JCloseButton;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.WarningType;

@Component(OLTPortDetailView.ID)
@Scope(Scopes.DESKTOP)
public class OLTPortDetailView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "oltPortDetailView";
	
	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name = AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name=AlarmSeverity.ID)
	private AlarmSeverity alarmSeverity;
	
	@Resource(name=AlarmEvents.ID)
	private AlarmEvents alarmEvents;
	
	@Resource(name=DateFormatter.ID)
	private DateFormatter dataFormatter;
	
	@Resource(name = LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	private ButtonFactory buttonFactory;
	private JXTable table;
	private AlarmTableModel tableModel;
	private AlarmMessage selectedAlarm;
	private JTextArea contentArea;
	private JCloseButton closeButton;
	private OLTPort selectedPort;
	
	private JTextField portNameFld;
	private JTextField portNOFld;
	private JTextField slotNumFld;
	private JTextField portTypeFld;
	private JTextField portStatusFld;
	
	@PostConstruct
	public void initialize(){
		setTitle("OLT端口详细信息");
		setViewSize(640, 480);
		setLayout(new BorderLayout());
		buttonFactory = actionManager.getButtonFactory(this);
		
		JPanel infoPanel = new JPanel();
		createInfoPanel(infoPanel);
		JPanel alarmPanel = new JPanel();
		createAlarmPanel(alarmPanel);
		JPanel detailPanel = new JPanel();
		createDetailPanel(detailPanel);
		
		add(infoPanel, BorderLayout.NORTH);
		add(alarmPanel, BorderLayout.CENTER);
		add(detailPanel, BorderLayout.SOUTH);
		
		selectedPort = (OLTPort)adapterManager.getAdapter(equipmentModel.getLastSelected(), OLTPort.class);
		if (alarmModel.getLastAlarms() != null && alarmModel.getLastAlarms().size() > 0) {
			alarmArrival(alarmModel.getLastAlarms());
		}
		
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, SelectionChangedLinstener);
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_ARRIVAL, AlarmArrivalListener);
	}
	
	private void alarmArrival(List<AlarmMessage> alarms) {
		if (selectedPort == null) return;
		List<AlarmMessage> thisPortAlarms = new ArrayList<AlarmMessage>();
		
		for (AlarmMessage trapWarning : alarms) {
			String ipValue = selectedPort.getOltEntity().getIpValue();
			if (ipValue.equals(trapWarning.getIpValue()) && 
				selectedPort.getProtNo() == trapWarning.getPortNo()) {
				thisPortAlarms.add(trapWarning);
			}
		}
		
		tableModel.setAlarms(thisPortAlarms);
		tableModel.fireTableDataChanged();
		if (thisPortAlarms.size() > 0) {
			table.setRowSelectionInterval(0, 0);
		}
	}
	
	private final PropertyChangeListener AlarmArrivalListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() instanceof List<?>) {
				List<AlarmMessage> alarms = (List<AlarmMessage>)evt.getNewValue();
				alarmArrival(alarms);
			}
		}
	};
	
	private void createDetailPanel(JPanel parent) {
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
		
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		parent.add(toolPanel, BorderLayout.PAGE_END);
		toolPanel.add(buttonFactory.createButton("deleteAlarm"));
		toolPanel.add(buttonFactory.createButton("cleanAlarm"));
		closeButton = buttonFactory.createCloseButton();
		toolPanel.add(closeButton);
		setCloseButton(closeButton);
	}
	
	private void createAlarmPanel(JPanel parent) {
		parent.setLayout(new GridLayout(1, 1));
		parent.setBorder(BorderFactory.createTitledBorder("告警信息"));
		table = new JXTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(false);
		table.getTableHeader().setReorderingAllowed(false);
		tableModel = new AlarmTableModel();		
		table.setModel(tableModel);		
		table.setColumnModel(tableModel.getColumnModel());
		JPanel tabelPanel = new JPanel(new BorderLayout(1, 2));
		tabelPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tabelPanel.add(table, BorderLayout.CENTER);
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);
		parent.add(scrollTablePanel);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						selectOneRow();
					}
				});
	}
	
	private void selectOneRow() {
		if (table.getSelectedRow() > - 1) {
			selectedAlarm = tableModel.getValue(table.getSelectedRow());
			contentArea.setText(selectedAlarm.getDescs());
		}
	}
	
	@ViewAction(text=ActionConstants.DELETE, icon=ButtonConstants.DELETE, desc="删除端口告警",role=Constants.MANAGERCODE)
	public void deleteAlarm() {
		if (selectedAlarm != null) {
			remoteServer.getService().deleteEntity(selectedAlarm.getWrapped());
		}
	}
	
	@ViewAction(text=ActionConstants.CLEAN, icon=ButtonConstants.CLEAN, desc="清空端口告警",role=Constants.MANAGERCODE)
	public void cleanAlarm() {		
		int count = tableModel.getRowCount();
		
		for (int index = 0; index < count; index++) {
			AlarmMessage alarm = tableModel.getValue(index);
			remoteServer.getService().deleteEntity(alarm.getWrapped());
		}
	}
	
	private void createInfoPanel(JPanel parent){
		parent.setBorder(BorderFactory.createTitledBorder("端口信息"));
		parent.setLayout(new MigLayout());
		
		parent.add(new JLabel("端口"), "gapright 30");
		portNOFld = new JTextField();
		portNOFld.setEditable(false);
		parent.add(portNOFld, "width 200px, left");
		
		parent.add(new JLabel("端口名称"), "gapleft 30, gapright 30");
		portNameFld = new JTextField();
		portNameFld.setEditable(false);
		parent.add(portNameFld, "span, width 200px, left");
		
		parent.add(new JLabel("端口类型"), "gapright 30");
		portTypeFld = new JTextField();
		portTypeFld.setEditable(false);
		parent.add(portTypeFld, "width 200px, left");

		parent.add(new JLabel("端口状态"), "gapleft 30, gapright 30");
		portStatusFld = new JTextField(); 
		portStatusFld.setEditable(false);
		parent.add(portStatusFld, "span, width 200px, left");
		
		parent.add(new JLabel("插槽号"), "gapright 30");
		slotNumFld = new JTextField(); 
		slotNumFld.setEditable(false);
		parent.add(slotNumFld, "width 200px, left");
	}
	
	private final PropertyChangeListener SelectionChangedLinstener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			selectedPort = 
				(OLTPort)adapterManager.getAdapter(equipmentModel.getLastSelected(), OLTPort.class);
			fillContents(selectedPort);
			if (alarmModel.getLastAlarms() != null && alarmModel.getLastAlarms().size() > 0) {
				alarmArrival(alarmModel.getLastAlarms());
			}
		}		
	};
	
	private void fillContents(OLTPort oltPort) {
		if (oltPort == null) return;
		int portNO = oltPort.getProtNo();
		String portName = oltPort.getPortName();
		String portType = oltPort.getPortType();
		boolean portStatus = oltPort.isPortStatus();
		int slotNum = oltPort.getSlotNum();
		
		portNOFld.setText(Integer.toString(portNO));
		portNameFld.setText(portName);
		portTypeFld.setText(portType);
		portStatusFld.setText("" + portStatus);
		slotNumFld.setText(Integer.toString(slotNum));
	}
	
	public class AlarmSeverityHighlighter extends AbstractHighlighter {
		@Override
		protected java.awt.Component doHighlight(
				java.awt.Component component, ComponentAdapter adapter) {
			AlarmMessage alarmMessage = tableModel.getValue(adapter.row);
			WarningType warningType = alarmModel.getWarningType(alarmMessage.getWarningType());
			component.setBackground(alarmSeverity.getColor(warningType.getWarningLevel()));
			return component;
		}
		private static final long serialVersionUID = -8795784237395864733L;
	}

	public class AlarmTableModel extends AbstractTableModel {
		
		public AlarmTableModel() {
			alarms = Collections.emptyList();

			int modelIndex = 0;
			columnModel = new DefaultTableColumnModel();
			TableColumnExt raisedTimeColumn = new TableColumnExt(modelIndex++, 160);
			raisedTimeColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_RAISED_TIME);
			raisedTimeColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_RAISED_TIME));
			columnModel.addColumn(raisedTimeColumn);

			TableColumnExt severityColumn = new TableColumnExt(modelIndex++, 60);
			severityColumn.setHighlighters(new AlarmSeverityHighlighter());
			severityColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_SEVERITY);
			severityColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_SEVERITY));		
			columnModel.addColumn(severityColumn);
			
			TableColumnExt contentColumn = new TableColumnExt(modelIndex++, 300);
			contentColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_CONTENT);
			contentColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_CONTENT));
			columnModel.addColumn(contentColumn);
			
			TableColumnExt eventColumn = new TableColumnExt(modelIndex++, 100);
			eventColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_EVENT);
			eventColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_EVENT));
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
			return columnModel.getColumn(col).getHeaderValue().toString();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if (row < alarms.size()) {
				AlarmMessage alarmMessage = alarms.get(row);
				WarningType warningType = alarmModel.getWarningType(alarmMessage.getWarningType());
				switch (col) {
					case 0: value = dataFormatter.format(alarmMessage.getCreateDate()); break;
					case 1: value = alarmSeverity.get(warningType.getWarningLevel()).getKey(); break;
					case 2: value = alarmMessage.getDescs(); break;
					case 3: value = alarmEvents.get(warningType.getWarningType()).getKey(); break;
					default: break;
				}
			}		
			
			return value;
		}
		
		public TableColumnModel getColumnModel() {
			return columnModel;
		}
		
		public AlarmMessage getValue(int row) {
			AlarmMessage value = null;
			if (row < alarms.size()) {
				value = alarms.get(row);
			}
			
			return value;
		}
		
		public void setAlarms(List<AlarmMessage> alarms) {
			if (alarms == null) return;
			this.alarms = alarms;
		}
		
		private List<AlarmMessage> alarms;
		private final TableColumnModel columnModel;
		private static final long serialVersionUID = -3065768803494840568L;
	}
}
