package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.views.ViewPart;


@Component(RSTPManageView.ID)
public class RSTPManageView extends ViewPart {
	public static final String ID = "rstpManageDialog";
	private JCheckBox edgeEnbale;
	private JCheckBox stpPortEnable;
	private JLabel portNum;
	private JComboBox rstpType;
	private JComboBox p2pEnbale;
	private JComboBox portPriority;
	private JTextField pathcost;
	private JTextField transferDelay;
	private JTextField maxOldTime;
	private JComboBox comboBox_Priority;
	private TitledBorder titledBorder2;
	private TitledBorder titledBorder3;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane3;

	private TitledBorder titledBorder1;
	private JList jList1;
	private JScrollPane jScrollPane1;
	private JPanel jPanel3;
	private JTextField textField;
	private int selectPortNum = 1;

	private DefaultListModel defaultListModel1 = new DefaultListModel();

	private HashMap environment = new HashMap(); // 环境变量

	private JPanel jPanel1 = new JPanel();

	public RSTPManageView() {
		super();
		try {
			this.setViewSize(500, 360);
			this.setTitle("RSTP设置");

			// rstpInfo = new RstpInfo();

			titledBorder3 = new TitledBorder(null, "RSTP系统信息",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, null);
			jPanel3 = new JPanel();
			jPanel3.setLayout(null);
			jPanel3.setPreferredSize(new Dimension(500, 100));
			jPanel3.setBorder(titledBorder3);
			add(jPanel3, BorderLayout.PAGE_START);

			final JLabel label_2 = new JLabel();
			label_2.setBounds(10, 20, 96, 24);
			label_2.setText("系统优先级");
			jPanel3.add(label_2);

			comboBox_Priority = new JComboBox();
			comboBox_Priority.setBounds(100, 20, 96, 20);
			comboBox_Priority.addItem("0");
			comboBox_Priority.addItem("4096");
			comboBox_Priority.addItem("8192");
			comboBox_Priority.addItem("12288");
			comboBox_Priority.addItem("16384");
			comboBox_Priority.addItem("20480");
			comboBox_Priority.addItem("24576");
			comboBox_Priority.addItem("28672");
			comboBox_Priority.addItem("32768");
			comboBox_Priority.addItem("36864");
			comboBox_Priority.addItem("40960");
			comboBox_Priority.addItem("45056");
			comboBox_Priority.addItem("49152");
			comboBox_Priority.addItem("53248");
			comboBox_Priority.addItem("57344");
			comboBox_Priority.addItem("61440");
			jPanel3.add(comboBox_Priority);

			final JLabel label_3 = new JLabel();
			label_3.setBounds(10, 46, 96, 24);
			label_3.setText("最大老化时间");
			jPanel3.add(label_3);

			maxOldTime = new JTextField();
			maxOldTime.setBounds(101, 46, 96, 20);
			maxOldTime.setMargin(new Insets(5, 0, 0, 5));
			jPanel3.add(maxOldTime);

			final JLabel label_5 = new JLabel();
			label_5.setBounds(289, 46, 96, 24);
			label_5.setText("转发延时");
			jPanel3.add(label_5);

			transferDelay = new JTextField();
			transferDelay.setBounds(380, 46, 96, 20);
			transferDelay.setMargin(new Insets(5, 0, 0, 5));
			jPanel3.add(transferDelay);

			final JLabel label_6 = new JLabel();
			label_6.setBounds(289, 20, 96, 24);
			label_6.setText("协议版本");
			jPanel3.add(label_6);

			final JButton button = new JButton();
			button.setBounds(100, 70, 96, 20);

			rstpType = new JComboBox();
			rstpType.setBounds(380, 20, 96, 20);
			rstpType.addItem("RSTP");
			rstpType.addItem("stp");
			jPanel3.add(rstpType);

			/*
			 * final JLabel label_19 = new JLabel(); label_19.setText(" ");
			 * jPanel3.add(label_19);
			 * 
			 * final JLabel label_20 = new JLabel(); label_20.setText(" ");
			 * jPanel3.add(label_20);
			 * 
			 * final JLabel label_21 = new JLabel(); label_21.setText(" ");
			 * jPanel3.add(label_21);
			 */

			button.setText("提交修改");
			jPanel3.add(button);

			jScrollPane1 = new JScrollPane();
			titledBorder1 = new TitledBorder(null, "端口列表",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, null);
			jScrollPane1.setBorder(titledBorder1);
			add(jScrollPane1, BorderLayout.LINE_START);

			jList1 = new JList(defaultListModel1);
			jScrollPane1.setViewportView(jList1);
			jList1.setFixedCellWidth(75);
			jList1.setFixedCellHeight(15);

			jScrollPane2 = new JScrollPane();
			jScrollPane2.setPreferredSize(new Dimension(0, 280));
			titledBorder2 = new TitledBorder(null, "端口配置信息",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, null);
			jScrollPane2.setBorder(titledBorder2);
			add(jScrollPane2, BorderLayout.CENTER);

			final JPanel panel = new JPanel();
			panel.setLayout(null);
			jScrollPane2.setViewportView(panel);

			final JLabel label_7 = new JLabel();
			label_7.setBounds(5, 0, 131, 28);
			label_7.setText("端口");
			panel.add(label_7);

			portNum = new JLabel();
			portNum.setBounds(130, 5, 131, 20);
			portNum.setText("");
			panel.add(portNum);

			final JLabel label_14 = new JLabel();
			label_14.setBounds(263, 1, 131, 28);
			label_14.setText(" ");
			panel.add(label_14);

			final JLabel label_9 = new JLabel();
			label_9.setBounds(5, 25, 131, 28);
			label_9.setText("STP模式");
			panel.add(label_9);

			stpPortEnable = new JCheckBox();
			stpPortEnable.setBounds(130, 25, 131, 20);
			stpPortEnable.setText("Enable");
			panel.add(stpPortEnable);

			final JLabel label_10 = new JLabel();
			label_10.setBounds(263, 29, 131, 28);
			label_10.setText(" ");
			panel.add(label_10);

			final JLabel label_1a = new JLabel();
			label_1a.setBounds(5, 55, 131, 28);
			label_1a.setText("路径开销");
			panel.add(label_1a);

			pathcost = new JTextField();
			pathcost.setBounds(132, 58, 131, 20);
			pathcost.setMargin(new Insets(5, 0, 0, 5));
			pathcost.setPreferredSize(new Dimension(100, 20));
			panel.add(pathcost);

			final JLabel label_11_1 = new JLabel();
			label_11_1.setBounds(263, 57, 131, 28);
			label_11_1.setText("(0表示auto状态)");
			panel.add(label_11_1);

			final JLabel label_11_2 = new JLabel();
			label_11_2.setBounds(5, 85, 131, 28);
			label_11_2.setText("端口优先级");
			panel.add(label_11_2);

			portPriority = new JComboBox();
			portPriority.setBounds(132, 85, 131, 20);
			portPriority.addItem("0");
			portPriority.addItem("16");
			portPriority.addItem("32");
			portPriority.addItem("64");
			portPriority.addItem("80");
			portPriority.addItem("96");
			portPriority.addItem("112");
			portPriority.addItem("128");
			portPriority.addItem("144");
			portPriority.addItem("160");
			portPriority.addItem("176");
			portPriority.addItem("192");
			portPriority.addItem("208");
			portPriority.addItem("224");
			portPriority.addItem("240");
			portPriority.setPreferredSize(new Dimension(100, 20));
			panel.add(portPriority);

			final JLabel label_11 = new JLabel();
			label_11.setBounds(263, 85, 131, 28);
			label_11.setText("  ");
			panel.add(label_11);

			final JLabel label_12 = new JLabel();
			label_12.setBounds(5, 110, 131, 28);
			label_12.setText("边缘端口");
			panel.add(label_12);

			edgeEnbale = new JCheckBox();
			edgeEnbale.setBounds(130, 110, 131, 20);
			edgeEnbale.setText("Enable");
			panel.add(edgeEnbale);

			final JLabel label_13 = new JLabel();
			label_13.setBounds(263, 113, 131, 28);
			label_13.setText(" ");
			panel.add(label_13);

			final JLabel label_15 = new JLabel();
			label_15.setBounds(5, 140, 131, 28);
			label_15.setText("P2P端口");
			panel.add(label_15);

			p2pEnbale = new JComboBox();
			p2pEnbale.setBounds(132, 141, 131, 20);
			p2pEnbale.addItem("Enabled");
			p2pEnbale.addItem("Disabled");
			p2pEnbale.addItem("Auto");
			p2pEnbale.setPreferredSize(new Dimension(100, 20));
			panel.add(p2pEnbale);

			final JLabel label_16 = new JLabel();
			label_16.setBounds(263, 141, 131, 28);
			label_16.setText(" ");
			panel.add(label_16);

			final JLabel label_17 = new JLabel();
			label_17.setBounds(1, 169, 131, 28);
			label_17.setText(" ");
			panel.add(label_17);

			final JButton button_1 = new JButton();
			button_1.setBounds(132, 169, 131, 20);
			button_1.setText("修改");
			button_1.setPreferredSize(new Dimension(100, 20));
			panel.add(button_1);

			// final JLabel label_17_1_1 = new JLabel();
			// label_17_1_1.setText(" ");
			// panel.add(label_17_1_1);

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}