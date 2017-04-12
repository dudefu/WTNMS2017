package com.jhw.adm.client.model.switcher;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

@Component(MACMulticastModel.ID)
public class MACMulticastModel extends AbstractTableModel{
	public static final String ID = "macMulticastModel";
	
	private List<List> dataList = new ArrayList<List>();
	
	private String[] columnName = null;

	@PostConstruct
	protected void initialize() {
		
	}
	
	public void setDataList(List<List> dataList) {
		if (null == dataList){
			this.dataList = new ArrayList<List>();
		}
		else{
			this.dataList = dataList;
		}
		
	}
	
	public String[] getColumnName() {
		return columnName;
	}

	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
	}

	public int getRowCount(){
		if (null == dataList){
			return 0;
		}
		else{
			return dataList.size();
		}
	}


    public int getColumnCount(){
    	return columnName.length;
    }

    
    public String getColumnName(int columnIndex){
    	return columnName[columnIndex];
    }


    public boolean isCellEditable(int rowIndex, int columnIndex){
    	if (1== columnIndex){
    		return true;
    	}
    	return false;
    }


    public Object getValueAt(int rowIndex, int columnIndex){
    	if (dataList.size() < 1){
    		return null;
    	}
    	Object object  = dataList.get(rowIndex);
    	if (null == object){
    		return null;
    	}
    	return ((List)dataList.get(rowIndex)).get(columnIndex);
    }

  
    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
    	if (dataList.size() < 1){
    		return ;
    	}
    	Object object  = dataList.get(rowIndex);
    	if (null == object){
    		return;
    	}
    	((List)dataList.get(rowIndex)).set(columnIndex, aValue);
    }
    
    public List<List> getDataList() {
		return dataList;
	}
}
