package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.nets.IPSegment;

@Component(FEPIpTableModel.ID)
public class FEPIpTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -372664544830324720L;
	public static final String ID = "fepIpTableModel";
	private List<IPSegment> ipSegmentList = null;
	private String[] columnName = null;

	@PostConstruct
	protected void initialize() {

	}

	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
	}

	public void setDataList(List<IPSegment> ipSegmentList) {
		if (null == ipSegmentList) {
			this.ipSegmentList = new ArrayList<IPSegment>();
		} else {
			this.ipSegmentList = new ArrayList<IPSegment>(ipSegmentList);
		}
	}

	public int getRowCount() {
		if (null == ipSegmentList) {
			return 0;
		}
		return ipSegmentList.size();
	}

	public int getColumnCount() {
		return columnName.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnName[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (null == ipSegmentList) {
			return null;
		}

		int count = ipSegmentList.size();
		if (count <= rowIndex) {
			return null;
		}

		Object value = null;
		IPSegment ipSegment = ipSegmentList.get(rowIndex);
		switch (columnIndex) {
		case 0:
			value = ipSegment.getBeginIp();
			break;
		case 1:
			value = ipSegment.getEndIp();
			break;
		}

		return value;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (null == ipSegmentList) {
			return;
		}

		int count = ipSegmentList.size();
		if (count <= rowIndex) {
			return;
		}

		IPSegment ipSegment = ipSegmentList.get(rowIndex);
		switch (columnIndex) {
		case 0:
			ipSegment.setBeginIp((String) aValue);
			break;
		case 1:
			ipSegment.setEndIp((String) aValue);
			break;
		}
	}

	public IPSegment getIPSegment(int index) {
		if (null == ipSegmentList) {
			return null;
		} else if (ipSegmentList.size() < 1) {
			return null;
		}

		return ipSegmentList.get(index);
	}

	public List getIPSegmentList() {
		if (null == ipSegmentList) {
			return null;
		} else if (ipSegmentList.size() < 1) {
			return null;
		}

		return ipSegmentList;
	}

	public void setIPSegment(IPSegment ipSegment) {
		if (null == ipSegmentList) {
			ipSegmentList = new ArrayList<IPSegment>();
		}
		ipSegmentList.add(ipSegment);
		this.fireTableDataChanged();
	}

	public void setIPSegmentList(List<IPSegment> list) {
		if (list != null){
			ipSegmentList = new ArrayList<IPSegment>(list);
		}
		
		this.fireTableDataChanged();
	}

	public void removeAll() {
		if (null == ipSegmentList) {
			return;
		}
		if (ipSegmentList.size() < 1) {
			return;
		}

		ipSegmentList.clear();

		this.fireTableDataChanged();
	}

	public void removeIPSegment(IPSegment ipSegment) {
		if (ipSegmentList.contains(ipSegment)){
			ipSegmentList.remove(ipSegment);	
		}
		this.fireTableDataChanged();
	}
	public void removeIPSegment(int index) {
		ipSegmentList.remove(index);
		this.fireTableDataChanged();
	}
}
