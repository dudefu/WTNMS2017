package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.table.TableColumnExt;

import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.switchs.SysLogHostToDevEntity;

public class IPDetailTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private List<SysLogHostToDevEntity> nodeEntityList = null;
	private final TableColumnModel ipColumnModel;
	private DataStatus dataStatus;

	public IPDetailTableModel(){
		dataStatus = ClientUtils.getSpringBean(DataStatus.ID);
		nodeEntityList = new ArrayList<SysLogHostToDevEntity>();
		
		int modelIndex = 0;
		ipColumnModel = new DefaultTableColumnModel();

		//主机IP
		TableColumnExt equipmentIPColumn = new TableColumnExt(modelIndex++,120);
		equipmentIPColumn.setIdentifier("设备IP");
		equipmentIPColumn.setHeaderValue("设备IP");
		ipColumnModel.addColumn(equipmentIPColumn);
		
		//HOSTIP
		TableColumnExt hostIDColumn = new TableColumnExt(modelIndex++,120);
		hostIDColumn.setIdentifier("HOST ID");
		hostIDColumn.setHeaderValue("HOST ID");
		ipColumnModel.addColumn(hostIDColumn);
		
		//状态
		TableColumnExt statusColumn = new TableColumnExt(modelIndex++,120);
		statusColumn.setIdentifier("状态");
		statusColumn.setHeaderValue("状态");
		ipColumnModel.addColumn(statusColumn);
	}
	
	@Override
	public int getColumnCount() {
		return ipColumnModel.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return nodeEntityList.size();
	}
	
	public TableColumnModel getColumnModel(){
		return ipColumnModel;
	}

	@Override
	public String getColumnName(int col) {
		return ipColumnModel.getColumn(col).getHeaderValue().toString();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object value = null;
		if(row < nodeEntityList.size()){
			SysLogHostToDevEntity sysLogHostToDevEntity = nodeEntityList.get(row);
			switch (col) {
			case 0://设备IP
				value = sysLogHostToDevEntity.getIpValue();
				break;
			case 1://hostID
				value = sysLogHostToDevEntity.getHostID();
				break;
			case 2://状态
				value = dataStatus.get(sysLogHostToDevEntity.getIssuedTag()).getKey();
				break;
			default:
				break;
			}
		}
		return value;
	}
	
	public void setNodeEntity(List<SysLogHostToDevEntity> nodeEntityList) {
		if (nodeEntityList == null) {
			return;
		}
		this.nodeEntityList = nodeEntityList;
	}
	
	public List<SysLogHostToDevEntity> getNodeEntityList(){
		return this.nodeEntityList;
	}
	
	public List<String> getIPList(){
		List<String> ipList = new ArrayList<String>();
		if(null != this.nodeEntityList){
			for(SysLogHostToDevEntity sysLogHostToDevEntity : this.nodeEntityList){
				ipList.add(sysLogHostToDevEntity.getIpValue());
			}
		}
		return ipList;
	}

	public List<SysLogHostToDevEntity> getSelectedEntityList(int[] selectedRows){
		List<SysLogHostToDevEntity> selectedList = new ArrayList<SysLogHostToDevEntity>();
		for(int i : selectedRows){
			selectedList.add(nodeEntityList.get(i));
		}
		return selectedList;
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
