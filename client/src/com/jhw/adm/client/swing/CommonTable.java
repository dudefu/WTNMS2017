package com.jhw.adm.client.swing;

import java.awt.Component;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;

public class CommonTable extends JXTable{
	private int headerHeight = 0 ;
	
	private int headerWidth = 0;
	
	private int[] headerWidths ;

	//根据列的宽度自动适配
	public void adjustColumnPreferredWidths(){
		headerWidths = new int[this.getColumnCount()];
		TableColumnModel columnModel = this.getColumnModel();
		for(int col =0; col < this.getColumnCount(); col++){
			int maxwidth = 0;
			for(int row =0; row<this.getRowCount();row++){
				TableCellRenderer rend = this.getCellRenderer(row, col);
				Object value = this.getValueAt(row, col);
				Component comp = rend.getTableCellRendererComponent
									(this, value, false, false, row, col);
				maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
			}
			TableColumn column = columnModel.getColumn(col);
			
//		column.setPreferredWidth(maxwidth);
			
			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if(headerRenderer == null){
				headerRenderer = this.getTableHeader().getDefaultRenderer();
			}
			Object headerValue = column.getHeaderValue();
			Component headerComp = headerRenderer.getTableCellRendererComponent(this, headerValue, false, false, 0, col);
			maxwidth = Math.max(maxwidth, headerComp.getPreferredSize().width);
			column.setPreferredWidth(maxwidth);
			
			//得到表头的高度
			int  dfads = column.getPreferredWidth();
			this.headerHeight = headerComp.getPreferredSize().height;
			headerWidths[col] = headerComp.getPreferredSize().width;
		}
	}
	
	public int getHeaderHight(){
		return this.headerHeight ;
	}
	
	public int getHeaderWidth(int col){
		return headerWidths[col];
	}
}
