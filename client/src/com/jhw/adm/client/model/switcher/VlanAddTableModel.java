package com.jhw.adm.client.model.switcher;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

@Component(VlanAddTableModel.ID)
public class VlanAddTableModel extends AbstractTableModel{
	public static final String ID = "vlanAddTableModel";
	
	private List<List> dataList = new ArrayList<List>();
	private String[] columnName = null;
	
	@PostConstruct
	protected void initialize() {
		
	}
	
	public void setDataList(List dataList) {
		if (null == dataList){
			this.dataList = new ArrayList<List>();
		}
		else{
			this.dataList= dataList;
		}
	}

	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
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

    @Override
	public String getColumnName(int columnIndex){
    	return columnName[columnIndex];
    }

    @Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
    	return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex){
    	if (null == dataList || dataList.size() <1){
    		return null;
    	}
    	List rowList = null;
    	try{
    		rowList = dataList.get(rowIndex);
    	}
    	catch(Exception ee){
    	}
    	
    	if (null == rowList){
    		return null;
    	}
    	Object object = rowList.get(columnIndex);
    	
    	return object;
    }

	@SuppressWarnings("unchecked")
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (null == dataList || dataList.size() < 1) {
			return;
		}
		List rowList = null;

		rowList = dataList.get(rowIndex);

		if (null == rowList) {
			return;
		}

		rowList.set(columnIndex, aValue);
	}

    
    public List<List> getDataList(){
    	return dataList;
    }
    
    public void insertRow(List rowData){
    	dataList.add(rowData);
    	this.fireTableDataChanged();
    }
    
    public void updateRow(int index,List rowData){
    	dataList.set(index, rowData);
    	this.fireTableDataChanged();
    }
    
    public void deleteRow(List rowData){
    	dataList.remove(rowData);
    	this.fireTableDataChanged();
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
