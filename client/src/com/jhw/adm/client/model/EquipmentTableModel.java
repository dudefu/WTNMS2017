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

		//����IP
		TableColumnExt equipmentIPColumn = new TableColumnExt(modelIndex++,120);
		equipmentIPColumn.setIdentifier("�豸IP");
		equipmentIPColumn.setHeaderValue("�豸IP");
		columnModel.addColumn(equipmentIPColumn);
		
		//�ڵ�����
		TableColumnExt nodeNameColumn = new TableColumnExt(modelIndex++,120);
		nodeNameColumn.setIdentifier("�ڵ�����");
		nodeNameColumn.setHeaderValue("�ڵ�����");
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
			case 0://�豸IP
				value = topoNodeEntity.getIpValue();
				break;
			case 1://�豸�ͺ�
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
