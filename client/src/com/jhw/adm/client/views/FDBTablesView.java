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


@Component(FDBTablesView.ID)
public class FDBTablesView extends ViewPart {
	public static final String ID = "fdbTablesView";
	JPanel jPanel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane jScrollPane1 = new JScrollPane();
	FdbDataModel model = new FdbDataModel();
	JTable routeTable = new JTable(model);
	JPanel jPanel2 = new JPanel();
	JButton refreshButton = new JButton();
	JButton closeButton = new JButton();
	private HashMap environment = new HashMap();
	private boolean isstop = false;
	JButton stopButton = new JButton();
	private JButton exportButton = new JButton(); // add by liuxw

	public FDBTablesView() {
		try {
			jbInit();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			// e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setViewSize(600, 450);
		setTitle("转发表");
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
		// end
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

class FdbDataModel extends AbstractTableModel {
	final String[] names = { "MAC地址", "目的端口", "状态" };
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
		if (column == 2) {
			String[] value = (String[]) val.get(2);
			if (value[1].equals("1"))
				return "其它";
			else if (value[1].equals("2"))
				return "无效";
			else if (value[1].equals("3"))
				return "学习";
			else if (value[1].equals("4"))
				return "自有";
			else if (value[1].equals("5"))
				return "管理";
		} else if (column == 1) {
			String[] value = (String[]) val.get(1);
			return value[1];
		} else if (column == 0) {
			String[] value = (String[]) val.get(0);
			return value[1];
		}

		return "";
	}

	@Override
	public void setValueAt(Object val, int row, int col) {

	}

	// 取得所有列名 add by liuxw
	public String[] getAllColumnName() {
		return names;
	}
}