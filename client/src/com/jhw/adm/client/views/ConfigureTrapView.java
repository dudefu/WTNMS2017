package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;

@Component(ConfigureTrapView.ID)
public class ConfigureTrapView extends ViewPart {
	public static final String ID = "configureTrapView";

	@PostConstruct
	protected void initialize() {
		this.setTitle("Trap事件配置");
		this.setViewSize(350, 400);
		this.setLayout(borderLayout1);

		//工具按钮
		JButton configBtn = new JButton("保存", imageRegistry.getImageIcon(ButtonConstants.SAVE));
		JButton cancelBtn = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
		setCloseButton(cancelBtn);
		
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		toolPanel.add(configBtn);
		toolPanel.add(cancelBtn);
		
		for (int i = 0; i <= 6; i++) {
			eventGradeCombox[i] = new JComboBox();
			eventGradeCombox[i].addItem("普通");
			eventGradeCombox[i].addItem("通知");
			eventGradeCombox[i].addItem("严重");
			eventGradeCombox[i].addItem("致命");
		}

		for (int i = 0 ; i < eventGradeCombox.length; i++){
			eventGradeCombox[i].setPreferredSize(new Dimension(180, eventGradeCombox[i].getPreferredSize().height));
		}

		centerPanel.setLayout(new SpringLayout());
		centerPanel.add(jLabel1);
		centerPanel.add(jLabel2);
		centerPanel.add(jLabel3);
		centerPanel.add(eventGradeCombox[0]);
		centerPanel.add(jLabel4);
		centerPanel.add(eventGradeCombox[1]);
		centerPanel.add(jLabel5);
		centerPanel.add(eventGradeCombox[2]);
		centerPanel.add(jLabel6);
		centerPanel.add(eventGradeCombox[3]);
		centerPanel.add(jLabel7);
		centerPanel.add(eventGradeCombox[4]);
		centerPanel.add(jLabel8);
		centerPanel.add(eventGradeCombox[5]);
		centerPanel.add(jLabel9);
		centerPanel.add(eventGradeCombox[6]);
		
		SpringUtilities.makeCompactGrid(centerPanel, 8, 2, 6, 6, 30, 6);

		bottomPanel.add(confirmButton);
		bottomPanel.add(cancelButton);
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel.add(centerPanel, BorderLayout.CENTER);
		
		this.add(toolPanel, BorderLayout.PAGE_END);
		this.add(panel, BorderLayout.CENTER);
	}

	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	private JPanel centerPanel = new JPanel();

	private JPanel bottomPanel = new JPanel();

	private JComboBox[] eventGradeCombox = new JComboBox[7];
	private JTextField snmpcommText = new JTextField();

	private JLabel jLabel1 = new JLabel("事件类型");
	private JLabel jLabel2 = new JLabel("告警等级");
	private JLabel jLabel3 = new JLabel("冷启动");
	private JLabel jLabel4 = new JLabel("热启动");
	private JLabel jLabel5 = new JLabel("端口断开");
	private JLabel jLabel6 = new JLabel("端口连通");
	private JLabel jLabel7 = new JLabel("认证失败");
	private JLabel jLabel8 = new JLabel("EGP邻居故障");
	private JLabel jLabel9 = new JLabel("厂商特定事件");

	private JButton confirmButton = new JButton("保存");
	private JButton cancelButton = new JButton("关闭");
	
	private BorderLayout borderLayout1 = new BorderLayout();
	private GridLayout gridLayout1 = new GridLayout(8, 2);

	private Hashtable trapEventGrade = null;
	private Hashtable trapconf = null;
	private String[] grade = new String[7];
	private String[] events = new String[] { "coldStart", "warmStart",
			"linkDown", "linkUp", "authenticationFailure", "egpNeighborLoss",
			"enterpriseSpecific" };

	private static final long serialVersionUID = 1L;
}