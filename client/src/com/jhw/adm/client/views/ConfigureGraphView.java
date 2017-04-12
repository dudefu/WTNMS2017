package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.Scopes;

@Component(ConfigureGraphView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureGraphView extends ViewPart {

	@PostConstruct
	protected void initialize() {
		setTitle("图形配置");
		setViewSize(400, 300);
		createContents(this);
	}
	
	private void createContents(JPanel parent) {
		parent.setLayout(new BorderLayout());
		JPanel optionsPanel = new JPanel();
		createOptions(optionsPanel);
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		JButton saveButton = new JButton("保存", imageRegistry.getImageIcon(ButtonConstants.SAVE));
		toolPanel.add(saveButton);
		JButton closeButton = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
		setCloseButton(closeButton);
		toolPanel.add(closeButton);
		
		parent.add(optionsPanel, BorderLayout.CENTER);
		parent.add(toolPanel, BorderLayout.PAGE_END);
	}
	
	private void createOptions(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel container = new JPanel(new SpringLayout());
		parent.add(container);
		
		String[] colors = new String[]{"黑色", "蓝色", "绿色", "黄色", "紫色", "橙色", "青色"};
		JComboBox netLineBox = new JComboBox(colors);
		netLineBox.setPreferredSize(new Dimension(140, netLineBox.getPreferredSize().height));
		container.add(new JLabel("网线颜色"));
		container.add(netLineBox);

		JComboBox powerLineBox = new JComboBox(colors);	
		container.add(new JLabel("电力线颜色"));
		container.add(powerLineBox);

		JComboBox serialLineBox = new JComboBox(colors);	
		container.add(new JLabel("串口线颜色"));
		container.add(serialLineBox);
		
		JComboBox layoutBox = new JComboBox(new String[]{"手动", "星型", "树型"});	
		container.add(new JLabel("布局方式"));
		container.add(layoutBox);
		
		JComboBox switcherImageBox = new JComboBox(new String[]{"默认", "选择..."});	
		container.add(new JLabel("交换机图片"));
		container.add(switcherImageBox);
		
		JComboBox carrierImageBox = new JComboBox(new String[]{"默认", "选择..."});	
		container.add(new JLabel("载波机图片"));
		container.add(carrierImageBox);

		SpringUtilities.makeCompactGrid(container, 6, 2, 6, 6, 50, 6);
	}

	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	private static final long serialVersionUID = 1L;

	public static final String ID = "configureGraphView";
}
