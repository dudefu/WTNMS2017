package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.table.TableColumnExt;

import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.warning.WarningEntity;

public class WarnByTypeTableModel extends AbstractTableModel {
	
	public WarnByTypeTableModel() {
		alarmSeverity = ClientUtils.getSpringBean(AlarmSeverity.ID);
		localizationManager = ClientUtils.getSpringBean(LocalizationManager.ID);
		dateFormatter = ClientUtils.getSpringBean(DateFormatter.ID);
		alarmTypeCategory = ClientUtils.getSpringBean(AlarmTypeCategory.ID);
		
		warningAlarms = new ArrayList<WarningEntity>();

		int modelIndex = 0;
		warningColumnModel = new DefaultTableColumnModel();
		
		//时间
		TableColumnExt raisedTimeColumn = new TableColumnExt(modelIndex++,120);
		raisedTimeColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_RAISED_TIME);
		raisedTimeColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_RAISED_TIME));
		warningColumnModel.addColumn(raisedTimeColumn);
		//设备
		TableColumnExt equipmentColumn = new TableColumnExt(modelIndex++,120);
		equipmentColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_EQUIPMENT);
		equipmentColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_EQUIPMENT));
		warningColumnModel.addColumn(equipmentColumn);
		//所属子网
		TableColumnExt subnetColumn = new TableColumnExt(modelIndex++,120);
		subnetColumn.setIdentifier("所属子网");
		subnetColumn.setHeaderValue("所属子网");
		warningColumnModel.addColumn(subnetColumn);
		//内容
		TableColumnExt contentColumn = new TableColumnExt(modelIndex++, 100);
		contentColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_CONTENT);
		contentColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_CONTENT));
		warningColumnModel.addColumn(contentColumn);
		//级别
		TableColumnExt severityColumn = new TableColumnExt(modelIndex++, 60);
		severityColumn.setHighlighters(new WarningAlarmSeverityHighlighter());
		severityColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_SEVERITY);
		severityColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_SEVERITY));
		warningColumnModel.addColumn(severityColumn);
		//类型
		TableColumnExt equipmentTypeColumn = new TableColumnExt(modelIndex++, 60);
		equipmentTypeColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_EQUIPMENT_TYPE);
		equipmentTypeColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_EQUIPMENT_TYPE));
		warningColumnModel.addColumn(equipmentTypeColumn);
	}
	
	public void clean() {
		warningAlarms.clear();
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return warningColumnModel.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return warningAlarms.size();
	}
	
//	public TableColumnModel getColumnMode(){
//		return warningColumnModel;
//	}

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
			case 1://来源
				value = alarm.getNodeName();
				break;
			case 2://所属子网
				value = alarm.getSubnetName();
				break;
			case 3://内容
				value = alarm.getDescs();
				break;
			case 4://级别
				value = alarmSeverity.get(alarm.getWarningLevel()).getKey();
				break;
			case 5://类型
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
	
	private AlarmSeverity alarmSeverity;
	private LocalizationManager localizationManager;
	private DateFormatter dateFormatter;
	private AlarmTypeCategory alarmTypeCategory;
	
	public WarnByTypeTableModel getModel(){
		return this;
	}
	
	public static final String ID = "warningTableModel";
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