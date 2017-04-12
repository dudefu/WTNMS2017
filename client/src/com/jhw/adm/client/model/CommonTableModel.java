package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.table.AbstractTableModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;

@Component(CommonTableModel.ID)
@Scope(Scopes.PROTOTYPE)
public class CommonTableModel extends AbstractTableModel{
	public static final String ID = "commonTableModel";
	
	private String[] columnName = null;
	
	private List<List> dataList = null;
	
	@PostConstruct
	protected void initialize() {
		dataList = new ArrayList<List>();
	}
	
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


//    public Class<?> getColumnClass(int columnIndex){}

  
    public boolean isCellEditable(int rowIndex, int columnIndex){
    	return false;
    }


    public Object getValueAt(int rowIndex, int columnIndex){
    	return dataList.get(rowIndex).get(columnIndex);
    }
    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
    	dataList.get(rowIndex).set(columnIndex, aValue);
    }
}