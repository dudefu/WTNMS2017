package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.views.ViewPart;


@Component(RINGManageView.ID)
public class RINGManageView extends ViewPart {
	public static final String ID = "ringManageView";
	private TitledBorder titledBorder2_1;
	private JScrollPane jScrollPane2_1;
	private TitledBorder titledBorder2;
	private JScrollPane jScrollPane2;
	private TitledBorder titledBorder1;
	private JList jList1;
	private JScrollPane jScrollPane1;
	private JPanel jPanel3;

	public RINGManageView() {
		setLayout(new BorderLayout());
		setViewSize(500, 360);
		setTitle("RING设置");

		jPanel3 = new JPanel();
		jPanel3.setPreferredSize(new Dimension(500, 15));
		add(jPanel3, BorderLayout.PAGE_START);

		jScrollPane1 = new JScrollPane();
		titledBorder1 = new TitledBorder(null, "端口列表",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null);
		jScrollPane1.setBorder(titledBorder1);
		add(jScrollPane1, BorderLayout.LINE_START);

		jList1 = new JList();
		jScrollPane1.setViewportView(jList1);

		jScrollPane2 = new JScrollPane();
		titledBorder2 = new TitledBorder(null, "RING列表",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null);
		jScrollPane2.setBorder(titledBorder2);
		jScrollPane2.setPreferredSize(new Dimension(0, 150));
		add(jScrollPane2, BorderLayout.PAGE_END);

		final JPanel panel = new JPanel();
		panel.setLayout(null);
		jScrollPane2.setViewportView(panel);

		jScrollPane2_1 = new JScrollPane();
		titledBorder2_1 = new TitledBorder(null, "RING端口配置",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null);
		jScrollPane2_1.setBorder(titledBorder2_1);
		jScrollPane2_1.setPreferredSize(new Dimension(0, 280));
		add(jScrollPane2_1, BorderLayout.CENTER);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		jScrollPane2_1.setViewportView(panel_1);
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