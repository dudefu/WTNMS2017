package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import org.springframework.stereotype.Component;


@Component(ConfigureTopogolyView.ID)
public class ConfigureTopogolyView extends ViewPart {

	public static final String ID = "configureTopogolyView";

	private BorderLayout borderLayout1 = new BorderLayout();
	private JButton exitButton = new JButton();
	private boolean doSubmapTopo = false;
	private boolean stopProgressBar = false;
	private BorderLayout borderLayout3 = new BorderLayout();
	private JPanel autoPanel = new JPanel();
	private JButton startDisButton = new JButton();
	private JLabel jLabel4 = new JLabel();
	private JTextField disStartIP = new JTextField();
	private JLabel jLabel5 = new JLabel();
	private JTextField disSnmpComm = new JTextField();
	private JLabel jLabel6 = new JLabel();
	private JTextField asynIcmpStep = new JTextField();
	private Map disFilter = null;
	private JTextArea readmeText = new JTextArea();

	private JCheckBox icmpBox = new JCheckBox();
	private JPanel checkPanel;
	private JScrollPane readmeTextScrollPane;
	private JLabel threadLabel = new JLabel("");
	private JTextField countField = new JTextField();
	private JToggleButton advancedBt = new JToggleButton("高级选项(A)");
	private JPanel advancedPanel = new JPanel();
	private JLabel timeoutLabel = new JLabel("超时 ");
	private JLabel retryLabel = new JLabel("重试 ");
	private JTextField timeoutText = new JTextField("");
	private JTextField retryText = new JTextField("");
	private JButton dbt = new JButton("默认值");
	private JButton dbt1 = new JButton("默认值");

	private JLabel scantimeoutLabel = new JLabel("超时 ");
	private JLabel scanretryLabel = new JLabel("重试 ");

	private JTextField scantimeoutText = new JTextField("");
	private JTextField scanretryText = new JTextField("");

	private JPanel mainPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private JPanel northCenterPanel = new JPanel();
	private static final long serialVersionUID = -1L;

	public ConfigureTopogolyView() {
		jbInit();
		centerDialog();
	}

	private void jbInit() {
		this.setTitle("拓扑配置");
		this.setViewSize(350, 522);// 500
		
		//工具按钮
		JButton startBtn = new JButton("开始");
		JButton closeBtn = new JButton("关闭");
		JToolBar toolbar = new JToolBar();
		toolbar.add(startBtn);
		toolbar.add(closeBtn);
		toolbar.setRollover(true);
		toolbar.setPreferredSize(new Dimension(280,30));

		// main panel
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEtchedBorder());

		// main panel - north panel
		readmeTextScrollPane = new JScrollPane(readmeText);
		readmeTextScrollPane.setPreferredSize(new Dimension(1, 130));
		readmeTextScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15,
				15, 15));

		readmeText.setEditable(false);
		readmeText.setText("");
		readmeText.setLineWrap(true);
		readmeText.setBorder(BorderFactory.createLoweredBevelBorder());
		// readmeText.setRows(0);
		readmeText.append("注意：\r\n");
		readmeText
				.append("1、您需要设定一个起始设备作为自动拓扑发现的起点，此设备应该是具有路由功能的设备，最好采用管理 工作站的默认网关。\r\n");
		readmeText.append("2、请确定起始设备的SNMP服务可以访问，并且需要输入授权访问的SNMP团体字符串。\r\n");
		readmeText
				.append("3、如果选择了先用ICMP协议扫描网络中的活动节点，那么一些禁止Ping的设备将不能被发现。默认情况下 不开启扫描服务，直接使用SNMP协议发现，可能需要花费比较长的时间。"); // add
																													// by
																													// liuxw
		readmeText.setCaretPosition(0);

		// main panel - center panel

		northCenterPanel.setLayout(new GridBagLayout());
		
		jLabel4.setText("起始设备IP地址：");
		jLabel4.setPreferredSize(new Dimension(180, 30));

		disStartIP.setPreferredSize(new Dimension(180, 30));

		jLabel5.setText("SNMP团体字符串 (如有多个团体字符串用逗号“,”区分)");

		disSnmpComm.setText("public");
		
		northCenterPanel.add(jLabel4,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));//
		northCenterPanel.add(disStartIP,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		
		northCenterPanel.add(jLabel5,new GridBagConstraints(0,1,2,1,1.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));//
		northCenterPanel.add(disSnmpComm,new GridBagConstraints(0,2,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		northCenterPanel.setPreferredSize(new Dimension(366, 110)); // 
		
		
		checkPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.GRAY);
				g.drawLine(17, 55, 17, 110);
				g.drawLine(17, 75, 30, 75);
				g.drawLine(17, 110, 30, 110);
			}
		};
		checkPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		checkPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), " 扫描配置"));
		checkPanel.setPreferredSize(new Dimension(366, 140)); // 85

		icmpBox.setText("利用ICMP协议扫描活动节点");
		icmpBox.setPreferredSize(new Dimension(278, 30));

		dbt1.setPreferredSize(new Dimension(65, 30));
		dbt1.setBorder(null);
		dbt1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		dbt1.setActionCommand("scan");

		jLabel6.setText("异步Ping每次发包数");
		jLabel6.setPreferredSize(new Dimension(115, 30)); // 125

		asynIcmpStep.setEditable(false);
		JLabel label4 = new JLabel("(支持范围:1-256)");
		label4.setPreferredSize(new Dimension(120, 30));
		asynIcmpStep.setPreferredSize(new Dimension(75, 30)); // 95

		scantimeoutLabel.setPreferredSize(new Dimension(40, 30));
		scanretryLabel.setPreferredSize(new Dimension(40, 30));
		JLabel label3 = new JLabel("秒");
		label3.setPreferredSize(new Dimension(40, 30));

		scantimeoutText.setPreferredSize(new Dimension(80, 30));
		scanretryText.setPreferredSize(new Dimension(80, 30));
		scantimeoutText.setEditable(false);
		scanretryText.setEditable(false);

		checkPanel.add(icmpBox);
		checkPanel.add(dbt1);
		checkPanel.add(jLabel6);
		checkPanel.add(asynIcmpStep);
		checkPanel.add(label4);
		checkPanel.add(scantimeoutLabel);
		checkPanel.add(scantimeoutText);
		checkPanel.add(label3);
		checkPanel.add(scanretryLabel);
		checkPanel.add(scanretryText);
		checkPanel.add(new JLabel("次"));
		

		advancedPanel.setLayout(new FlowLayout());
		advancedPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), " 全局配置"));
		advancedPanel.setPreferredSize(new Dimension(366, 140));

		threadLabel.setText("允许启动线程数 ");

		threadLabel.setPreferredSize(new Dimension(110, 30));
		// countField.setText("5");
		countField.setPreferredSize(new Dimension(75, 30));
		// countField.setToolTipText("支持范围:1 - 100线程");
		JLabel label2 = new JLabel("(支持范围:1-100线程)");
		label2.setPreferredSize(new Dimension(130, 30));

		timeoutLabel.setPreferredSize(new Dimension(40, 30));
		retryLabel.setPreferredSize(new Dimension(40, 30));
		JLabel label1 = new JLabel("秒");
		label1.setPreferredSize(new Dimension(40, 30));

		timeoutText.setPreferredSize(new Dimension(80, 30));
		retryText.setPreferredSize(new Dimension(80, 30));

		JPanel dbtPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		dbtPanel.add(dbt);
		dbt.setPreferredSize(new Dimension(65, 30));
		dbt.setBorder(null);
		dbt.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		dbtPanel.setPreferredSize(new Dimension(353, 35));
		dbt.setActionCommand("dis");
		advancedPanel.add(threadLabel);
		advancedPanel.add(countField);
		advancedPanel.add(label2);
		advancedPanel.add(timeoutLabel);
		advancedPanel.add(timeoutText);
		advancedPanel.add(label1);
		advancedPanel.add(retryLabel);
		advancedPanel.add(retryText);
		advancedPanel.add(new JLabel("次"));
		advancedPanel.add(dbtPanel);

		centerPanel.setLayout(new GridBagLayout());
		centerPanel.add(northCenterPanel,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));//
		centerPanel.add(checkPanel,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		centerPanel.add(advancedPanel,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(toolbar,BorderLayout.NORTH);
		topPanel.add(readmeTextScrollPane, BorderLayout.CENTER);
		
		mainPanel.setLayout(new SpringLayout());
		mainPanel.add(topPanel);
		mainPanel.add(centerPanel);
		SpringUtilities.makeCompactGrid(mainPanel, 2, 1, 5, 5, 5, 5);
		
//		this.setLayout(new FlowLayout(FlowLayout.LEADING));
//		this.add(mainPanel);
		
//		mainPanel.add(topPanel, BorderLayout.NORTH);
//		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		this.setViewSize(640,480);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setLayout(new BorderLayout());
		panel.add(mainPanel, BorderLayout.CENTER);
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.add(panel);
	}

	/**
	 * Show this dialog on center screen.
	 */
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