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
	private JToggleButton advancedBt = new JToggleButton("�߼�ѡ��(A)");
	private JPanel advancedPanel = new JPanel();
	private JLabel timeoutLabel = new JLabel("��ʱ ");
	private JLabel retryLabel = new JLabel("���� ");
	private JTextField timeoutText = new JTextField("");
	private JTextField retryText = new JTextField("");
	private JButton dbt = new JButton("Ĭ��ֵ");
	private JButton dbt1 = new JButton("Ĭ��ֵ");

	private JLabel scantimeoutLabel = new JLabel("��ʱ ");
	private JLabel scanretryLabel = new JLabel("���� ");

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
		this.setTitle("��������");
		this.setViewSize(350, 522);// 500
		
		//���߰�ť
		JButton startBtn = new JButton("��ʼ");
		JButton closeBtn = new JButton("�ر�");
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
		readmeText.append("ע�⣺\r\n");
		readmeText
				.append("1������Ҫ�趨һ����ʼ�豸��Ϊ�Զ����˷��ֵ���㣬���豸Ӧ���Ǿ���·�ɹ��ܵ��豸����ò��ù��� ����վ��Ĭ�����ء�\r\n");
		readmeText.append("2����ȷ����ʼ�豸��SNMP������Է��ʣ�������Ҫ������Ȩ���ʵ�SNMP�����ַ�����\r\n");
		readmeText
				.append("3�����ѡ��������ICMPЭ��ɨ�������еĻ�ڵ㣬��ôһЩ��ֹPing���豸�����ܱ����֡�Ĭ������� ������ɨ�����ֱ��ʹ��SNMPЭ�鷢�֣�������Ҫ���ѱȽϳ���ʱ�䡣"); // add
																													// by
																													// liuxw
		readmeText.setCaretPosition(0);

		// main panel - center panel

		northCenterPanel.setLayout(new GridBagLayout());
		
		jLabel4.setText("��ʼ�豸IP��ַ��");
		jLabel4.setPreferredSize(new Dimension(180, 30));

		disStartIP.setPreferredSize(new Dimension(180, 30));

		jLabel5.setText("SNMP�����ַ��� (���ж�������ַ����ö��š�,������)");

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
				.createEtchedBorder(), " ɨ������"));
		checkPanel.setPreferredSize(new Dimension(366, 140)); // 85

		icmpBox.setText("����ICMPЭ��ɨ���ڵ�");
		icmpBox.setPreferredSize(new Dimension(278, 30));

		dbt1.setPreferredSize(new Dimension(65, 30));
		dbt1.setBorder(null);
		dbt1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		dbt1.setActionCommand("scan");

		jLabel6.setText("�첽Pingÿ�η�����");
		jLabel6.setPreferredSize(new Dimension(115, 30)); // 125

		asynIcmpStep.setEditable(false);
		JLabel label4 = new JLabel("(֧�ַ�Χ:1-256)");
		label4.setPreferredSize(new Dimension(120, 30));
		asynIcmpStep.setPreferredSize(new Dimension(75, 30)); // 95

		scantimeoutLabel.setPreferredSize(new Dimension(40, 30));
		scanretryLabel.setPreferredSize(new Dimension(40, 30));
		JLabel label3 = new JLabel("��");
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
		checkPanel.add(new JLabel("��"));
		

		advancedPanel.setLayout(new FlowLayout());
		advancedPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), " ȫ������"));
		advancedPanel.setPreferredSize(new Dimension(366, 140));

		threadLabel.setText("���������߳��� ");

		threadLabel.setPreferredSize(new Dimension(110, 30));
		// countField.setText("5");
		countField.setPreferredSize(new Dimension(75, 30));
		// countField.setToolTipText("֧�ַ�Χ:1 - 100�߳�");
		JLabel label2 = new JLabel("(֧�ַ�Χ:1-100�߳�)");
		label2.setPreferredSize(new Dimension(130, 30));

		timeoutLabel.setPreferredSize(new Dimension(40, 30));
		retryLabel.setPreferredSize(new Dimension(40, 30));
		JLabel label1 = new JLabel("��");
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
		advancedPanel.add(new JLabel("��"));
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