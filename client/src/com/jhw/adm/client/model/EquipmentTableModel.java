package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.table.TableColumnExt;

import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;

public class EquipmentTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private List<SwitchTopoNodeEntity> nodeEntityList = null;
	private final TableColumnModel columnModel;

	public EquipmentTableModel(){
		nodeEntityList = new ArrayList<SwitchTopoNodeEntity>();
		
		int modelIndex = 0;
		columnModel = new DefaultTableColumnModel();

		//主机IP
		TableColumnExt equipmentIPColumn = new TableColumnExt(modelIndex++,120);
		equipmentIPColumn.setIdentifier("设备IP");
		equipmentIPColumn.setHeaderValue("设备IP");
		columnModel.addColumn(equipmentIPColumn);
		
		//节点名称
		TableColumnExt nodeNameColumn = new TableColumnExt(modelIndex++,120);
		nodeNameColumn.setIdentifier("节点名称");
		nodeNameColumn.setHeaderValue("节点名称");
		columnModel.addColumn(nodeNameColumn);
	}
	
	@Override
	public int getColumnCount() {
		return columnModel.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return nodeEntityList.size();
	}
	
	public TableColumnModel getColumnModel(){
		return columnModel;
	}

	@Override
	public String getColumnName(int col) {
		return columnModel.getColumn(col).getHeaderValue().toString();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object value = null;
		if(row < nodeEntityList.size()){
			SwitchTopoNodeEntity topoNodeEntity = nodeEntityList.get(row);
			switch (col) {
			case 0://设备IP
				value = topoNodeEntity.getIpValue();
				break;
			case 1://设备型号
				value = topoNodeEntity.getName();
				break;
			default:
				break;
			}
		}
		return value;
	}
	
	public void setNodeEntity(List<SwitchTopoNodeEntity> nodeEntityList) {
		if (nodeEntityList == null) {
			return;
		}
		this.nodeEntityList = nodeEntityList;
	}
	
	public List<SwitchTopoNodeEntity> getSelectedNodeEntity(int[] selectedRows){
		List<SwitchTopoNodeEntity> selectedNodeEntity = new ArrayList<SwitchTopoNodeEntity>();
		for(int i : selectedRows){
			selectedNodeEntity.add(this.nodeEntityList.get(i));
		}
		return selectedNodeEntity;
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
}
