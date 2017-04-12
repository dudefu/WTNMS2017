package com.jhw.adm.client.model.switcher;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

@Component(TrunkTableModel.ID)
public class TrunkTableModel extends AbstractTableModel{
	public static final String ID = "trunkTableModel";
	
	private List<List> dataList = null;
	private String[] columnName = null;
	
	@PostConstruct
	protected void initialize() {
		
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
    	return columnName.length;
    }

    public String getColumnName(int columnIndex){
    	return columnName[columnIndex];
    }

  
//    public Class<?> getColumnClass(int columnIndex){
//    	return getValueAt(0,columnIndex).getClass();
//    }

    public boolean isCellEditable(int rowIndex, int columnIndex){
    	return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex){
    	Object object = ((ArrayList)dataList.get(rowIndex)).get(columnIndex);

    	return object;

    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
    	((ArrayList)dataList.get(rowIndex)).set(columnIndex, aValue);
    }
}
