package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.springframework.stereotype.Component;


@Component(EquipmentView.ID)
public class EquipmentView extends ViewPart {

	@PostConstruct
	protected void initialize() {
		setTitle("设备信息");
		setLayout(new BorderLayout());

		JPanel autoListenSetupWrapper = new JPanel(new FlowLayout(
				FlowLayout.LEADING));
		JPanel autoListenSetupContainer = new JPanel(new SpringLayout());

		autoListenSetupContainer.add(new JLabel("名称", 10));
		autoListenSetupContainer.add(new JTextField("10", 20));

		autoListenSetupContainer.add(new JLabel("设备类型", 10));
		autoListenSetupContainer.add(new JTextField("载波机", 20));

		autoListenSetupContainer.add(new JLabel("版本", 10));
		autoListenSetupContainer.add(new JTextField("5", 20));

		autoListenSetupContainer.add(new JLabel("生产厂家", 10));
		autoListenSetupContainer.add(new JTextField("金宏威", 20));

		autoListenSetupContainer.add(new JLabel("ROS版本", 10));
		autoListenSetupContainer.add(new JTextField("65", 20));

		SpringUtilities.makeCompactGrid(autoListenSetupContainer, 5, 2, 6, 6,
				30, 6);

		autoListenSetupWrapper.add(autoListenSetupContainer);
		add(autoListenSetupWrapper, BorderLayout.CENTER);
	}

	public static final String ID = "equipmentView";
	private static final long serialVersionUID = 1L;
}