package com.jhw.adm.client.model.switcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.nets.VlanPort;

@Component(VlanPortModel.ID)
public class VlanPortModel extends AbstractTableModel{
	public static final String ID = "vlanPortModel";

	private String[] columnNames = {"端口","PVID","优先级"};
//	private Object[][] data = {
//		{"1", "1","2"},
//		{"2", "1", "1"},
//		{"3", "1", "1"},
//		{"4", "1", "1"},
//		{"5", "1","1"},
//		{"6", "1", "1"},
//		{"7", "1", "1"},
//		{"8", "1","1"},
//	};
	
	private List<VlanPort> vlanPortData = new ArrayList<VlanPort>();
	
	private static final Logger LOG = LoggerFactory.getLogger(VlanPortModel.class);

	@PostConstruct
	protected void initialize() {
		
	}
	
	public void setVlanPortData(List<VlanPort> vlanPortData){
		if (null == vlanPortData){
			this.vlanPortData = new ArrayList<VlanPort>();
		}
		else{
			this.vlanPortData = vlanPortData;
		}
		this.fireTableDataChanged();
	}
	
	public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return vlanPortData.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
    	Object object = null;
    	VlanPort vlanPort = vlanPortData.get(row);
    	if (0 == col){
    		object = vlanPort.getPortNO();
    	}
    	else if (1 == col){
    		object = vlanPort.getPvid();
    	}
    	else if (2 == col){
    		object = vlanPort.getPriority();
    	}
    	else if (3 == col){
    		object = "";
    	}
    	
    	return object;
    }


//    public Class getColumnClass(int c) {
//        return getValueAt(0, c).getClass();
//    }


    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 1 || col > 2) {
            return false;
        } else {
            return true;
        }
    }


    public void setValueAt(Object value, int row, int col) {
    	if (null == value || "".equals(value)){
    		return;
    	}
    	int iValue = Integer.parseInt(String.valueOf(value));
    	if (0 == col){
    		((VlanPort)vlanPortData.get(row)).setPortNO(iValue);
    	}
    	else if (1 == col){
    		((VlanPort)vlanPortData.get(row)).setPvid(iValue);
    	}
    	else if (2 == col){
    		((VlanPort)vlanPortData.get(row)).setPriority(iValue);
    	}
    	else if (3 == col){
//    		((VlanPort)vlanPortData.get(row)).setPriority(iValue);
    	}
    	
        fireTableCellUpdated(row, col);
    }
    
    public List<VlanPort> getVlanPortData(){
    	return vlanPortData;
    }
}
