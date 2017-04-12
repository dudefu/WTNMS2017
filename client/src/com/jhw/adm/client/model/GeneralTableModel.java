package com.jhw.adm.client.model;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class GeneralTableModel extends ViewModel {
	private TableModel tableModel = null;
	
	public GeneralTableModel(String[] columnName,Object[][] data){
		super();
		tableModel = new JTableModel(columnName,data);
	}
	
	public TableModel getTableModel(){
		return tableModel;
	}
}

class JTableModel extends AbstractTableModel{
	private String[] columnName = null;
	private Object[][] data = null;
	
	public JTableModel(String[] columnName,Object[][] data){
		super();
		this.columnName = columnName;
		this.data = data;
	}
	
	public int getColumnCount() {
        return columnName.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnName[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
    	return false;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
	
}
