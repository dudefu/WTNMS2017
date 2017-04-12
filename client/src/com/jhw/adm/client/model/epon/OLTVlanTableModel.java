package com.jhw.adm.client.model.epon;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.server.entity.nets.FEPEntity;


@Component(OLTVlanTableModel.ID)
public class OLTVlanTableModel extends AbstractTableModel{
	public static final String ID = "OLTVlanTableModel";
	
	@Autowired
	@Qualifier(LocalizationManager.ID)
	private LocalizationManager localizationManager;

	private List<List> dataList = null;
	private String[] columnName = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(OLTVlanTableModel.class);
	
	@PostConstruct
	protected void initialize() {
		columnName = new String[]{localizationManager.getString(ResourceConstants.VLANVIEW_VLANID)
									,localizationManager.getString(ResourceConstants.VLANVIEW_VLANNAME)
									,"Tag¶Ë¿Ú","Untag¶Ë¿Ú"};
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

    public String getColumnName(int columnIndex){
    	return columnName[columnIndex];
    }

  
//    public Class<?> getColumnClass(int columnIndex){
//    	
//    }

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
  
    	Object object = rowList.get(columnIndex);
    	
    	return object;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
    	if (null == dataList || dataList.size() <1){
    		return ;
    	}
    	List rowList = null;

    	rowList = dataList.get(rowIndex);

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
}
