package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;


@Component(ARPTablesView.ID)
public class ARPTablesView extends ViewPart {
	public static final String ID = "arpTablesView";
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane jScrollPane1 = new JScrollPane();
	ArpDataModel model = new ArpDataModel();
	JTable routeTable = new JTable(model);
	JPanel jPanel2 = new JPanel();
	JButton refreshButton = new JButton();
	JButton closeButton = new JButton();
	private HashMap environment = new HashMap();
	private boolean isstop = false;
	JButton stopButton = new JButton();
	private JButton exportButton = new JButton(); // add by liuxw

	public ARPTablesView() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setViewSize(600, 450);
		setTitle("ARP表");
		jPanel1.setLayout(borderLayout1);
		jPanel2.setPreferredSize(new Dimension(120, 10));
		jPanel2.setLayout(null);
		refreshButton.setBounds(new Rectangle(18, 28, 83, 28));
		refreshButton.setMnemonic('R');
		refreshButton.setText("刷新(R)");
		closeButton.setBounds(new Rectangle(17, 115, 83, 28));
		closeButton.setMnemonic('C');
		closeButton.setText("关闭(C)");
		stopButton.setText("停止(S)");
		stopButton.setMnemonic('S');
		stopButton.setBounds(new Rectangle(17, 72, 83, 28));
		// add by liuxw begin
		exportButton.setMnemonic('E');
		exportButton.setText("导出...(E)");
		exportButton.setBounds(17, 158, 83, 28);
		this.add(jPanel1, BorderLayout.CENTER);
		jPanel1.add(jScrollPane1, BorderLayout.CENTER);
		jPanel1.add(jPanel2, BorderLayout.EAST);
		jPanel2.add(refreshButton, null);
		jPanel2.add(closeButton, null);
		jPanel2.add(stopButton, null);
		jPanel2.add(exportButton, null); // add by liuxw
		jScrollPane1.getViewport().add(routeTable, null);
	}
}

class ArpDataModel extends AbstractTableModel {
	final String[] names = { "接口索引", "MAC地址        ", "IP地址         ", "类型" };
	ArrayList tableList = new ArrayList();

	public int getRowCount() {
		return tableList.size();
	}

	public int getColumnCount() {
		return names.length;
	}

	@Override
	public String getColumnName(int column) {
		return names[column];
	}

	public Object getValueAt(int row, int column) {
		ArrayList val = (ArrayList) tableList.get(row);
		if (val.size() != 4)
			return "";
		String[] value = (String[]) val.get(column);
		return value[1];
	}

	@Override
	public void setValueAt(Object val, int row, int col) {

	}

	// 取得所有列名 add by liuxw
	public String[] getAllColumnName() {
		return names;
	}
}