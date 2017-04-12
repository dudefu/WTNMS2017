package com.jhw.adm.client.model.switcher;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.switchs.IGMPEntity;
import com.jhw.adm.server.entity.switchs.Igmp_vsi;

@Component(IGMPTableModel.ID)
public class IGMPTableModel extends AbstractTableModel{
	public static final String ID = "igmpTableModel";
	
	private List<Igmp_vsi> igmpVsiData = null;
	
	private String[] columnNames = {"VLAN ID","Snooping Enabled","IGMP Querier"};;
	
	private static final Logger LOG = LoggerFactory.getLogger(IGMPTableModel.class);
	

	@PostConstruct
	protected void initialize() {
		
	}
	
	public List<Igmp_vsi> getIgmpVsiData() {
		return igmpVsiData;
	}

	public void setIgmpVsiData(List<Igmp_vsi> igmpVsiData) {
		if (null == igmpVsiData){
			this.igmpVsiData = new ArrayList<Igmp_vsi>();
		}
		else{
			this.igmpVsiData = igmpVsiData;
		}
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
    	if (null == igmpVsiData){
    		return 0;
    	}
    	else{
    		return igmpVsiData.size();
    	}
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
    	Object object = null;
    	Igmp_vsi igmp_Vsi = igmpVsiData.get(row);
    	switch(col){
    		case 0:
    			object = igmp_Vsi.getVlanId();
    			break;
    		case 1:
    			if (igmp_Vsi.isSnooping()){
    				object = "Enable";
    			}
    			else{
    				object = "Disable";
    			}
    			break;
    		case 2:
    			if (igmp_Vsi.isQuerier()){
    				object = "Enable";
    			}
    			else{
    				object = "Disable";
    			}
    			break;
    		case 3:
    			object = "";
    			break;
    	}
    	return object;
    }


//    public Class getColumnClass(int c) {
//        return getValueAt(0, c).getClass();
//    }


    public boolean isCellEditable(int row, int col) {
        if (col < 1 || col > 2) {
            return false;
        } else {
            return true;
        }
    }


    public void setValueAt(Object value, int row, int col) {
    	switch(col){
    		case 0:
    			igmpVsiData.get(row).setVlanId(String.valueOf(value));
    			break;
    		case 1:
    			if ("Enable".equals(value)){
    				igmpVsiData.get(row).setSnooping(true);
    			}
    			else{
    				igmpVsiData.get(row).setSnooping(false);
    			}
    			break;
    		case 2:
    			if ("Enable".equals(value)){
    				igmpVsiData.get(row).setQuerier(true);
    			}
    			else{
    				igmpVsiData.get(row).setQuerier(false);
    			}
    			break;
    		
    	}
    	
        fireTableCellUpdated(row, col);
    }
}
