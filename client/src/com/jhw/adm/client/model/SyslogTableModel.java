package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.table.TableColumnExt;

import com.jhw.adm.server.entity.switchs.SysLogHostEntity;

public class SyslogTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private List<SysLogHostEntity> hostEntityList = null;
	private final TableColumnModel hostColumnModel;

	public SyslogTableModel(){
		hostEntityList = new ArrayList<SysLogHostEntity>();
		
		int modelIndex = 0;
		hostColumnModel = new DefaultTableColumnModel();

		//主机IP
		TableColumnExt hostIPColumn = new TableColumnExt(modelIndex++,120);
		hostIPColumn.setIdentifier("主机IP");
		hostIPColumn.setHeaderValue("主机IP");
		hostColumnModel.addColumn(hostIPColumn);
		//端口号
		TableColumnExt portNumberColumn = new TableColumnExt(modelIndex++,120);
		portNumberColumn.setIdentifier("端口号");
		portNumberColumn.setHeaderValue("端口号");
		hostColumnModel.addColumn(portNumberColumn);
	}
	
	@Override
	public int getColumnCount() {
		return hostColumnModel.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return hostEntityList.size();
	}
	
	public TableColumnModel getColumnModel(){
		return hostColumnModel;
	}

	@Override
	public String getColumnName(int col) {
		return hostColumnModel.getColumn(col).getHeaderValue().toString();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object value = null;
		if(row < hostEntityList.size()){
			SysLogHostEntity hostEntity = hostEntityList.get(row);
			switch (col) {
			case 0://主机IP
				value = hostEntity.getHostIp();
				break;
			case 1://端口号
				value = hostEntity.getHostPort();
				break;
			default:
				break;
			}
		}
		return value;
	}
	
	public SysLogHostEntity getValue(int row) {
		SysLogHostEntity value = null;
		if (row < hostEntityList.size()) {
			value = hostEntityList.get(row);
		}
		return value;
	}
	
	public void setSysLogHosts(List<SysLogHostEntity> hostEntityList) {
		if (hostEntityList == null) {
			return;
		}
		this.hostEntityList = hostEntityList;
	}
	
	public List<SysLogHostEntity> getSysLogHosts(){
		return this.hostEntityList;
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
