package com.jhw.adm.client.views;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;

@Component(ConfigureSystemView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureSystemView extends ViewPart {
	public static final String ID = "configureSystemView";
	private static final long serialVersionUID = 1L;
	
	private final JTabbedPane tabPnl = new  JTabbedPane();

	@Autowired
	@Qualifier(ConfigureSysMonitorView.ID)
	private ConfigureSysMonitorView monitorView;
	
	@Autowired
	@Qualifier(ConfigureSysServerAddrView.ID)
	private ConfigureSysServerAddrView serverAddrView;
	
	@Autowired
	@Qualifier(ConfigureSysGisView.ID)
	private ConfigureSysGisView gisView;
	
	@Autowired
	@Qualifier(WarningAudioConfigureView.ID)
	private WarningAudioConfigureView audioConfigureView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("配置系统参数");
		
		setCloseButton(monitorView.getCloseButton());
		setCloseButton(serverAddrView.getCloseButton());
		setCloseButton(gisView.getCloseButton());
		//Amend 2010.06.10
		setCloseButton(audioConfigureView.getCloseButton());
		
		tabPnl.addTab("监测参数", monitorView);
		tabPnl.addTab("告警服务器配置", serverAddrView);
//		tabPnl.addTab("GIS参数", gisView);
		tabPnl.addTab("告警声音配置", audioConfigureView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		setViewSize(500, 400);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		monitorView.close();
		serverAddrView.close();
		gisView.close();
		audioConfigureView.close();
	}
	
//	JTabbedPane tabbedPane = null;
//	
//	@Autowired
//	@Qualifier(ImageRegistry.ID)
//	private ImageRegistry imageRegistry;
//	
//	@PostConstruct
//	protected void initialize() {
//		setTitle("配置系统参数");
//		setViewSize(500, 420);
//		setLayout(new BorderLayout());	
//
//		tabbedPane = new JTabbedPane();
//		tabbedPane.setTabPlacement(SwingConstants.TOP);		
//		add(tabbedPane, BorderLayout.CENTER);
//		
//		JPanel monitorPanel = new JPanel();
//		createMonitorPanel(monitorPanel);
//		tabbedPane.addTab("监测参数", monitorPanel);
//		
//		JPanel serverPanel = new JPanel();
//		createServerPanel(serverPanel);
//		tabbedPane.addTab("服务器地址", serverPanel);
//		
//		JPanel gisPanel = new JPanel();
//		createGISPanel(gisPanel);
//		tabbedPane.addTab("GIS参数", gisPanel);
//	}
//	
//	private void createMonitorPanel(JPanel parent) {
//		parent.setLayout(new BorderLayout());
//		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
//
//		JButton saveButton = new JButton("保存", imageRegistry.getImageIcon(ButtonConstants.SAVE));
//		toolPanel.add(saveButton);
//		JButton closeButton = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		setCloseButton(closeButton);
//		toolPanel.add(closeButton);
//		
//		JPanel monitorContents = new JPanel();
//		createMonitorContents(monitorContents);
//		parent.add(monitorContents, BorderLayout.CENTER);
//		parent.add(toolPanel, BorderLayout.PAGE_END);
//	}
//	
//	private void createMonitorContents(JPanel parent) {
//		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
//		JPanel container = new JPanel(new SpringLayout());
//		parent.add(container);
//
//		NumberFormat integerFormat = NumberFormat.getNumberInstance();
//        integerFormat.setMinimumFractionDigits(0);
//        integerFormat.setParseIntegerOnly(true);
//        integerFormat.setGroupingUsed(false);
//        
//        JTextField intervalField = new JTextField();
//        JTextField delayField = new JTextField();
//        intervalField.setText("1");
//        delayField.setText("5");
//        intervalField.setColumns(20);
//        delayField.setColumns(20);
//		
//		container.add(new JLabel("心跳频率(分钟)"));
//		container.add(intervalField);
//		
//		container.add(new JLabel("网络延时(秒)"));
//		container.add(delayField);
//
//		SpringUtilities.makeCompactGrid(container, 2, 2, 6, 6, 30, 6);
//	}
//	
//	private void createServerPanel(JPanel parent) {
//		parent.setLayout(new BorderLayout());
//		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
//
//		JButton saveButton = new JButton("保存", imageRegistry.getImageIcon(ButtonConstants.SAVE));
//		toolPanel.add(saveButton);
//		JButton closeButton = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		setCloseButton(closeButton);
//		toolPanel.add(closeButton);
//		
//		JPanel serverContents = new JPanel();
//		createServerContents(serverContents);
//		
//		JPanel serverList = new JPanel();
//		createServerList(serverList);
//
//		parent.add(serverList, BorderLayout.PAGE_START);
//		parent.add(serverContents, BorderLayout.CENTER);
//		parent.add(toolPanel, BorderLayout.PAGE_END);
//	}
//	
//	private void createServerList(JPanel parent) {
//		parent.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
//		JPanel container = new JPanel(new SpringLayout());
//		parent.add(container);
//		JComboBox serverBox = new JComboBox(new String[] { 
//				"默认", "本机", "测试室", "会议室"});
//		serverBox.setEditable(true);
//		serverBox.setPreferredSize(new Dimension(150, serverBox.getPreferredSize().height));
//		container.add(serverBox);
//		container.add(new JButton("新增", imageRegistry.getImageIcon(ButtonConstants.NEW)));
//		SpringUtilities.makeCompactGrid(container, 1, 2, 6, 6, 6, 6);
//	}
//	
//	private void createServerContents(JPanel parent) {
//		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
//		JPanel container = new JPanel(new SpringLayout());
//		parent.add(container);
//
//		NumberFormat integerFormat = NumberFormat.getNumberInstance();
//        integerFormat.setMinimumFractionDigits(0);
//        integerFormat.setParseIntegerOnly(true);
//        integerFormat.setGroupingUsed(false);
//
//        JTextField serverNameField = new JTextField();
//        JTextField serverAddressField = new JTextField();
//        JTextField serverPortField = new JTextField();
//        JTextField mailAddressField = new JTextField();
//        JTextField smsAddressField = new JTextField();
//        serverNameField.setText("默认");
//        serverAddressField.setText("192.168.13.111");
//        mailAddressField.setText("192.168.13.23");
//        smsAddressField.setText("192.168.13.54");
//        serverPortField.setText("10001");
//		serverAddressField.setColumns(25);
//		serverPortField.setColumns(25);
//
//		container.add(new JLabel("名称"));
//		container.add(serverNameField);
//		
//		container.add(new JLabel("服务器地址"));
//		container.add(serverAddressField);
//		
//		container.add(new JLabel("服务器端口"));
//		container.add(serverPortField);
//		
//		container.add(new JLabel("邮件服务地址"));
//		container.add(mailAddressField);
//		
//		container.add(new JLabel("短信服务地址"));
//		container.add(smsAddressField);
//
//		SpringUtilities.makeCompactGrid(container, 5, 2, 6, 6, 30, 6);
//	}
//	
//	private void createGISPanel(JPanel parent) {
//		parent.setLayout(new BorderLayout());
//		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
//
//		JButton saveButton = new JButton("保存", imageRegistry.getImageIcon(ButtonConstants.SAVE));
//		toolPanel.add(saveButton);
//		JButton closeButton = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		setCloseButton(closeButton);
//		toolPanel.add(closeButton);
//				
//		JPanel gisContents = new JPanel();
//		createGISContents(gisContents);
//		parent.add(gisContents, BorderLayout.CENTER);
//		parent.add(toolPanel, BorderLayout.PAGE_END);
//	}
//	
//	private void createGISContents(JPanel parent) {
//		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
//		JPanel container = new JPanel(new SpringLayout());
//		parent.add(container);
//
//		NumberFormat integerFormat = NumberFormat.getNumberInstance();
//        integerFormat.setMinimumFractionDigits(0);
//        integerFormat.setParseIntegerOnly(true);
//        integerFormat.setGroupingUsed(false);
//        
//        JTextField serverAddressField = new JTextField();
//        JTextField serverPortField = new JTextField();
//        JTextField spaceAddressField = new JTextField();
//        JTextField spacePortField = new JTextField();
//        serverAddressField.setText("192.168.13.110");
//        serverPortField.setText("9001");
//        spaceAddressField.setText("192.168.13.1");
//        spacePortField.setText("9002");
//		serverAddressField.setColumns(25);
//		serverPortField.setColumns(25);
//				
//		container.add(new JLabel("服务器地址"));
//		container.add(serverAddressField);
//		
//		container.add(new JLabel("服务器端口"));
//		container.add(serverPortField);
//				
//		container.add(new JLabel("空间数据库地址"));
//		container.add(spaceAddressField);
//		
//		container.add(new JLabel("空间数据库端口"));
//		container.add(spacePortField);
//
//		SpringUtilities.makeCompactGrid(container, 4, 2, 6, 6, 30, 6);
//	}
}