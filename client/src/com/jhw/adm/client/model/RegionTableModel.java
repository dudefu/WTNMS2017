package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

@Component(RegionTableModel.ID)
public class RegionTableModel extends AbstractTableModel{
	public static final String ID = "regionTableModel";
	
	private String[] columnName = null;
	private List<List> dataList = null;
	
	public String[] getColumnName() {
		return columnName;
	}

	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
	}

	public List<List> getDataList() {
		return dataList;
	}

	public void setDataList(List<List> dataList) {
		if (null == dataList){
			this.dataList = new ArrayList<List>();
		}
		else{
			this.dataList = dataList;
		}
	}

	public int getRowCount(){
		if (null == dataList){
			return 0;
		}
		return dataList.size();
	}

    public int getColumnCount(){
    	if (null == columnName){
    		return 0;
    	}
    	return columnName.length;
    }

    public String getColumnName(int columnIndex){
    	return columnName[columnIndex];
    }
  
    public boolean isCellEditable(int rowIndex, int columnIndex){
    	return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex){
    	Object object = null;
    	if (rowIndex < 0){
    		return object;
    	}
    	object = dataList.get(rowIndex).get(columnIndex);
    	return object;
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
    	dataList.get(rowIndex).set(columnIndex, aValue);
    }
}
