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
		setTitle("�豸��Ϣ");
		setLayout(new BorderLayout());

		JPanel autoListenSetupWrapper = new JPanel(new FlowLayout(
				FlowLayout.LEADING));
		JPanel autoListenSetupContainer = new JPanel(new SpringLayout());

		autoListenSetupContainer.add(new JLabel("����", 10));
		autoListenSetupContainer.add(new JTextField("10", 20));

		autoListenSetupContainer.add(new JLabel("�豸����", 10));
		autoListenSetupContainer.add(new JTextField("�ز���", 20));

		autoListenSetupContainer.add(new JLabel("�汾", 10));
		autoListenSetupContainer.add(new JTextField("5", 20));

		autoListenSetupContainer.add(new JLabel("��������", 10));
		autoListenSetupContainer.add(new JTextField("�����", 20));

		autoListenSetupContainer.add(new JLabel("ROS�汾", 10));
		autoListenSetupContainer.add(new JTextField("65", 20));

		SpringUtilities.makeCompactGrid(autoListenSetupContainer, 5, 2, 6, 6,
				30, 6);

		autoListenSetupWrapper.add(autoListenSetupContainer);
		add(autoListenSetupWrapper, BorderLayout.CENTER);
	}

	public static final String ID = "equipmentView";
	private static final long serialVersionUID = 1L;
}