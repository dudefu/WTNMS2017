package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;


@Component(RouteTablesView.ID)
public class RouteTablesView extends ViewPart {
	public static final String ID = "routeTablesView";
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane jScrollPane1 = new JScrollPane();
	RouteDataModel model = new RouteDataModel();
	JTable routeTable = new JTable(model);
	JPanel jPanel2 = new JPanel();
	JButton refreshButton = new JButton();
	JButton closeButton = new JButton();
	private boolean isstop = false;
	JButton stopButton = new JButton();
	private JButton exportButton = new JButton(); // add by liuxw

	public RouteTablesView() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setViewSize(800, 600);
		setTitle("路由表");
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
		centerDialog();
	}

	protected void centerDialog() {
		Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height / 2;
		screenSize.width = screenSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		this.setLocation(x, y);
	}
}

class RouteDataModel extends AbstractTableModel {
	private String disp4[] = { "invalid", "direct", "indirect", "other" };
	private String disp5[] = { "local", "netmgmt", "icmp", "egp", "ggp",
			"hello", "rip", "is-is", "es-is", "ciscoIgrp", "bbnSpfIgp", "ospf",
			"bgp", "other" };

	final String[] names = { "目的地址      ", "网关IP地址    ", "子网掩码      ", "接口索引",
			"类型    ", "协议    ", "年龄    ", "度量1", "度量2", "度量3", "度量4" };
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
		if (val.size() != 11)
			return "";
		if (column == 0) {
			String[] value = (String[]) val.get(0);
			return value[1];
		} else if (column == 1) {
			String[] value = (String[]) val.get(6);
			return value[1];
		} else if (column == 2) {
			String[] value = (String[]) val.get(10);
			return value[1];
		} else if (column == 3) {
			String[] value = (String[]) val.get(1);
			return value[1];
		} else if (column == 4) {
			String[] value = (String[]) val.get(7);
			int valueindex = Integer.parseInt(value[1]);
			if (valueindex >= 2 && valueindex <= 4)
				return disp4[valueindex - 2];
			else
				return disp4[3];
		} else if (column == 5) {
			String[] value = (String[]) val.get(8);
			int valueindex = Integer.parseInt(value[1]);
			if (valueindex >= 2 && valueindex <= 14)
				return disp5[valueindex - 2];
			else
				return disp5[13];
		} else if (column == 6) {
			String[] value = (String[]) val.get(9);
			return value[1];
		} else if (column == 7) {
			String[] value = (String[]) val.get(2);
			return value[1];
		} else if (column == 8) {
			String[] value = (String[]) val.get(3);
			return value[1];
		} else if (column == 9) {
			String[] value = (String[]) val.get(4);
			return value[1];
		} else if (column == 10) {
			String[] value = (String[]) val.get(5);
			return value[1];
		}
		return "";
	}

	@Override
	public void setValueAt(Object val, int row, int col) {

	}

	// 取得所有列名
	public String[] getAllColumnName() {
		return names;
	}

}