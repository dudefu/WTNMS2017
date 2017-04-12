package com.jhw.adm.client.model.switcher;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(VlanMemberPortModel.ID)
public class VlanMemberPortModel extends AbstractTableModel{
	public static final String ID = "vlanMemberPortModel";

	private List dataList = null;
	private String[] columnName = null;
	
	private List<ButtonGroup> listBtnGroup = new ArrayList<ButtonGroup>();
	private ButtonGroup group = null;
	private int oldRow = -1;
	
	private HashMap map = new HashMap();
	
	private static final String TAG = "Tag";
	private static final String UNTAG = "Untag";
	
	private static final Logger LOG = LoggerFactory.getLogger(VlanMemberPortModel.class);

	@PostConstruct
	protected void initialize() {
		
	}
	
	public void setColumnName(String[] columnName) {
		this.columnName = columnName;
	}
	
	public void setDataList(List dataList) {
		if (null == dataList){
			this.dataList = new ArrayList();
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
    	return true;
    }

    public Object getValueAt(int rowIndex, int columnIndex){
    	Object object = ((ArrayList)dataList.get(rowIndex)).get(columnIndex);
    	if (oldRow != rowIndex){
    		group = new ButtonGroup();
    		oldRow = rowIndex;
    	}
    	
    	if (0 == columnIndex){
    		if (object instanceof JCheckBox){
    			map.put(((JCheckBox) object).getText(), group);
    		}
		}
    	else if (1 == columnIndex ){
    		if (object instanceof JRadioButton){
    			((JRadioButton)object).setActionCommand(TAG);
    			group.add((JRadioButton)object);   
    		}
		}
    	else if (2 == columnIndex){
    		if (object instanceof JRadioButton){
    			((JRadioButton)object).setActionCommand(UNTAG);
    			group.add((JRadioButton)object);   
    		}
		}
    	
    	listBtnGroup.add(group);

    	return object;

    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
    	((ArrayList)dataList.get(rowIndex)).set(columnIndex, aValue);
    }
    
    public void setSelectedValueAt(Object aValue, boolean isSelect,int rowIndex, int columnIndex){
    	if (aValue instanceof JCheckBox){
    		((JCheckBox) aValue).setSelected(isSelect);
    	}
    	else if (aValue instanceof JRadioButton){
    		((JRadioButton) aValue).setSelected(isSelect);
    	}
    	setValueAt(aValue,rowIndex,columnIndex);
    	this.fireTableDataChanged();
    }
    
    public void clearSelected(Object object){
    	String key = ((JCheckBox)object).getText();
    	if (null != (ButtonGroup)map.get(key)){
    		((ButtonGroup)map.get(key)).clearSelection();
    	}
    	
    	this.fireTableDataChanged();
    }
    
    public void setDefaultPortSelected(Object object){
    	String key = ((JCheckBox)object).getText();
    	if (null != (ButtonGroup)map.get(key)){
    		Enumeration<AbstractButton> enumeration = ((ButtonGroup)map.get(key)).getElements();
    		while (enumeration.hasMoreElements()){
    			JRadioButton radioBtn = (JRadioButton)enumeration.nextElement();
    			if (radioBtn.getActionCommand().equals(UNTAG)){
    				radioBtn.setSelected(true);
    			}
    		}
    	}
    	
    	this.fireTableDataChanged();
    }
    
//    public void clearSelectedStatuss(){
//    	
//    	for (int i = 0 ; i < getRowCount(); i++){
//    		for (int j = 0 ; j < getColumnCount() ; j++){
//    			Object object = this.getValueAt(i, j);
//    			if (object instanceof JCheckBox){
//    	    		((JCheckBox) object).setSelected(false);
//    	    	}
////    	    	else if (object instanceof JRadioButton){
////    	    		((JRadioButton) object).setSelected(false);
////    	    	}
//    		}
//    	}
//    	for (int k = 0 ; k < listBtnGroup.size(); k++){
//    		((ButtonGroup)listBtnGroup.get(k)).clearSelection();
//    	}
//    	
//    	this.fireTableDataChanged();	
//    }
    
    public void clearPortStatus(){
    	for (int i = 0 ; i < getRowCount(); i++){
    		for (int j = 0 ; j < getColumnCount() ; j++){
    			Object object = this.getValueAt(i, j);
    			if (object instanceof JCheckBox){
    	    		((JCheckBox) object).setSelected(false);
    	    		
//    	    		Object groupObject = map.get(((JCheckBox)object).getText());
//    	    		if (null != groupObject && groupObject instanceof ButtonGroup){
//    	    			((ButtonGroup)groupObject).clearSelection();
//    	    		}
    	    	}
    		}
    	}
    	
    	for (int k = 0 ; k < listBtnGroup.size(); k++){
    		((ButtonGroup)listBtnGroup.get(k)).clearSelection();
    	}
    	
    	this.fireTableDataChanged();	
    }
    
    public Vector getTagPort(){
    	Vector vector = new Vector();
    	String tagStr = "";
    	String untagStr = "";
    	
    	for (int row = 0 ; row < getRowCount(); row++){
    		for (int col = 0 ; col < getColumnCount() ; col++){
    			Object object = this.getValueAt(row, col);
    			if (0 == col){
    				boolean isSelect = ((JCheckBox)object).isSelected();
    				if (!isSelect){
    					break;
    				}
    			}
    			else if (1 == col){
    				boolean isSelect = ((JRadioButton)object).isSelected();
    				if (isSelect){
    					tagStr = tagStr + (row+1) + ",";
    				}
    			}
    			else if (2 == col){
    				boolean isSelect = ((JRadioButton)object).isSelected();
    				if (isSelect){
    					untagStr = untagStr + (row+1) + ",";
    				}
    			}
    		}
    	}
    	if (tagStr.length() < 1){
    		tagStr = "";
    	}
    	else{
    		tagStr = tagStr.substring(0, tagStr.length() -1);
    	}
    	if (untagStr.length() < 1){
    		untagStr = "";
    	}
    	else{
    		untagStr = untagStr.substring(0, untagStr.length() -1);
    	}
    	vector.add(0, tagStr);
    	vector.add(1, untagStr);
    	return vector;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
