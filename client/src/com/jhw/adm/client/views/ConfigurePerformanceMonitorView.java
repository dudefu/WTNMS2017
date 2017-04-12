package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.NumberFormat;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import org.springframework.stereotype.Component;


@Component(ConfigurePerformanceMonitorView.ID)
public class ConfigurePerformanceMonitorView extends ViewPart{
	
	@PostConstruct
	protected void initialize() {
		setTitle("性能监控配置");
		setViewSize(320, 240);
		setLayout(new BorderLayout());

		JPanel autoListenSetupWrapper = new JPanel(new FlowLayout(
				FlowLayout.LEADING));
		JPanel container = new JPanel(new SpringLayout());
		
		//工具按钮
		JButton saveBtn = new JButton("保存");
		JToolBar toolbar = new JToolBar();
		toolbar.add(saveBtn);
		toolbar.add(new JButton("取消"));
		toolbar.setRollover(true);

		NumberFormat integerFormat = NumberFormat.getNumberInstance();
		integerFormat.setMinimumFractionDigits(0);
		integerFormat.setParseIntegerOnly(true);
		integerFormat.setGroupingUsed(false);

		listenIntervalField = new JFormattedTextField(integerFormat);
		sendIntervalField = new JFormattedTextField(integerFormat);
		timeoutField = new JFormattedTextField(integerFormat);
		listenIntervalField.setColumns(20);
		sendIntervalField.setColumns(20);
		timeoutField.setColumns(20);
		listenIntervalField.setValue(new Integer(5));
		sendIntervalField.setValue(new Integer(5));
		timeoutField.setValue(new Integer(5));
		
		JComboBox equipmentBox = new JComboBox(new String[] { 
				"192.168.10.11", "192.168.10.12", "192.168.10.13",
				"192.168.10.11", "192.168.10.12", "192.168.10.13",
				"192.168.10.11", "192.168.10.12", "192.168.10.13",
				"192.168.10.11", "192.168.10.12", "192.168.10.13"});
		equipmentBox.setEditable(true);
		
		container.add(new JLabel("监控设备"));
		container.add(equipmentBox);

		container.add(new JLabel("监控端口"));
		JComboBox portBox = new JComboBox(new String[] { 
				"1", "2", "3", "4", "5", "6", "7", "8" });
		portBox.setEditable(true);
		container.add(portBox);

		container.add(new JLabel("流量上限阀值"));
		container.add(listenIntervalField);

		container.add(new JLabel("流量下限阀值"));
		container.add(sendIntervalField);

		SpringUtilities.makeCompactGrid(container, 4, 2, 6, 6,
				6, 6);

		autoListenSetupWrapper.add(container);
		add(toolbar, BorderLayout.NORTH);
		add(autoListenSetupWrapper, BorderLayout.CENTER);
	}

	public static final String ID = "configurePerformanceMonitorView";
	private JFormattedTextField listenIntervalField;
	private JFormattedTextField sendIntervalField;
	private JFormattedTextField timeoutField;
	private static final long serialVersionUID = 1L;
}