package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;


@Component(ConfigureNotificationView.ID)
public class ConfigureNotificationView extends ViewPart {
	public static final String ID = "configureNotificationView";

	@PostConstruct
	protected void initialize() {
		jbInit();
	}

	private JPanel[] centerPanel = new JPanel[] { new JPanel(), new JPanel(),
			new JPanel(), new JPanel() };
	private JPanel mainPanel = new JPanel();

	private JCheckBox[][] checkBox = new JCheckBox[4][5];
	private String[] modes = new String[] { "日志", "消息框", "声音", "电子邮件", "短信" };

	
	private JPanel toolbar = new JPanel();

	private BorderLayout borderLayout1 = new BorderLayout();

	private TitledBorder titledBorder1 = new TitledBorder("通知方式");
	private TitledBorder titledBorder[] = new TitledBorder[] {
			new TitledBorder("普通"), new TitledBorder("通知"),
			new TitledBorder("严重"), new TitledBorder("致命") };

	private void jbInit() {
		this.setTitle("告警方式配置");
		this.setViewSize(350, 350);
		this.setLayout(borderLayout1);
		JButton confirmButton = new JButton("保存", imageRegistry.getImageIcon(ButtonConstants.SAVE));
		JButton cancelButton = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
		setCloseButton(cancelButton);
			
		//工具Bar
		toolbar.setLayout(new FlowLayout(FlowLayout.TRAILING));
		toolbar.add(confirmButton);
		toolbar.add(cancelButton);

		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 4; j++) {
				checkBox[i][j] = new JCheckBox(modes[j], false);
			}
		}

		titledBorder1.setBorder(BorderFactory.createLineBorder(Color.gray));
		for (int i = 0; i <= 3; i++) {
			titledBorder[i].setBorder(BorderFactory
					.createLineBorder(Color.gray));
		}

		for (int i = 0; i <= 3; i++) {
			centerPanel[i].setBorder(titledBorder[i]);
//			centerPanel[i].setLayout(gridLayout1);
			centerPanel[i].setLayout(new SpringLayout());
			for (int j = 0; j <= 4; j++) {
				centerPanel[i].add(checkBox[i][j]);
			}
			SpringUtilities.makeCompactGrid(centerPanel[i], 5, 1, 10, 10,30, 30);
		}

		mainPanel.setLayout(new GridLayout(1, 4));
		mainPanel.setLayout(new SpringLayout());
		mainPanel.add(centerPanel[0]);
		mainPanel.add(centerPanel[1]);
		mainPanel.add(centerPanel[2]);
		mainPanel.add(centerPanel[3]);
		SpringUtilities.makeCompactGrid(mainPanel, 1, 4, 10, 6, 20, 20);
		

		this.add(toolbar, BorderLayout.PAGE_END);
		this.add(mainPanel, BorderLayout.CENTER);
		
		this.setViewSize(580, 400);
	}

	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	private static final long serialVersionUID = 1L;
}