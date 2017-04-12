package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(RingConfigTableModel.ID)
public class RingConfigTableModel extends AbstractTableModel{
	public static final String ID = "ringConfigTableModel";
	
	private static final Logger LOG = LoggerFactory.getLogger(RingConfigTableModel.class);
	
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

 
//    public Class<?> getColumnClass(int columnIndex){
//    	if (null == getValueAt(0,columnIndex)){
//    		return null;
//    	}
//    	
//    	return getValueAt(0,columnIndex).getClass();
//    }


    public boolean isCellEditable(int rowIndex, int columnIndex){
//    	if (1== columnIndex){
//    		return true;
//    	}
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

    public void insertRow(List list){
    	dataList.add(list);
    	this.fireTableDataChanged();
//    	boolean mode = Boolean.valueOf(list.get(1).toString());
//    	setEnableMode(mode);
    }
    
    public List<List> getDataList(){
    	return dataList;
    }

    public void setEnableMode(boolean enable){
    	for (int i = 0 ; i < dataList.size() ; i++){
    		dataList.get(i).set(1, enable);
    	}
    	this.fireTableDataChanged();
    }
    
    public void delRow(int[] indexs){
    	for(int i = indexs.length-1; i >=0 ; i--){
    		dataList.remove(i);
    	}
    	this.fireTableDataChanged();
    }
    
    public void removeRow(int rows[]){
		int size = rows.length;
		List listStr = new ArrayList();
		for(int j = 0 ; j < size; j++){
			String ip = (String)this.getValueAt(rows[j], 0);
			listStr.add(ip);
		}
		
		for(int i = 0 ; i < listStr.size(); i++){
			String ip = (String)listStr.get(i);
			for (int k = this.dataList.size()-1 ; k >=0; k--){
				if (this.dataList.get(k).get(0).toString().equals(ip)){
					this.dataList.remove(k);
					break;
				}
			}
		}
		
		this.fireTableDataChanged();
	}
}
