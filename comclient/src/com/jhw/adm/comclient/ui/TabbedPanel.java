package com.jhw.adm.comclient.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TabbedPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private LogPanel logPanel;
	private DiagnoseView diagnoseView;

	public JComponent init() {
		JTabbedPane tabPnl = new JTabbedPane();
		tabPnl.addTab("日志", logPanel.init());
		tabPnl.addTab("参数诊断", diagnoseView.init());
		this.setLayout(new BorderLayout());
		this.add(tabPnl, BorderLayout.CENTER);

		this.setSize(500, 400);

		return this;
	}

	public LogPanel getLogPanel() {
		return logPanel;
	}

	public void setLogPanel(LogPanel logPanel) {
		this.logPanel = logPanel;
	}

	public DiagnoseView getDiagnoseView() {
		return diagnoseView;
	}

	public void setDiagnoseView(DiagnoseView diagnoseView) {
		this.diagnoseView = diagnoseView;
	}

}
