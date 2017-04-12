package com.jhw.adm.comclient.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.service.LldpHandler;
import com.jhw.adm.comclient.service.topology.TopologyHandler;
import com.jhw.adm.comclient.service.topology.epon.EponHandler;
import com.jhw.adm.comclient.system.AutoIncreaseConstants;
import com.jhw.adm.comclient.system.IDiagnose;
import com.jhw.adm.comclient.util.IpAddressField;

public class DiagnoseView extends JPanel {
	private final Logger LOG = Logger.getLogger(this.getClass().getName());
	private final Map<Integer, IDiagnose> diagnoseMap;

	private static final long serialVersionUID = 1L;

	private IpAddressField ipField = new IpAddressField();
	private JButton scanBtn = new JButton();
	private JButton clearBtn = new JButton();
	private JComboBox filterComboBox = new JComboBox();
	private JTextArea leftArea = new JTextArea();
	private JTextArea rightArea = new JTextArea();

	// private static final String[] FILTERS = { "请选择类型", "二层lldp表", "设备系统信息",
	// "网络接口表", "三层端口地址表", "三层地址转换表", "OLT生成树协议表", "三层lldp表" };
	private static final String[] FILTERS = { "请选择类型", "二层lldp表", "设备系统信息" };
	// For Diagnose
	private EponHandler eponHandler;
	private TopologyHandler topologyHandler;
	private LldpHandler lldpHandler;

	public DiagnoseView() {
		diagnoseMap = new ConcurrentHashMap<Integer, IDiagnose>();
		diagnoseMap.put(AutoIncreaseConstants.LAYER2LLDP, layer2lldp);
		diagnoseMap.put(AutoIncreaseConstants.SYSTEM, system);
		diagnoseMap.put(AutoIncreaseConstants.IFTABLE, ifTable);
		diagnoseMap.put(AutoIncreaseConstants.IPADDRTABLE, ipAddrTable);
		diagnoseMap.put(AutoIncreaseConstants.IPNETTOMEDIATABLE,
				ipNetToMediaTable);
		diagnoseMap.put(AutoIncreaseConstants.DOT1DSTPPORTTABLE,
				dot1dStpPortTable);
		diagnoseMap.put(AutoIncreaseConstants.LAYER3LLDP, layer3lldp);
	}

	public JComponent init() {
		initialize();
		return this;
	}

	private void initialize() {
		setLayout(new BorderLayout());

		JPanel centerPanel = new JPanel();
		createCenterPanel(centerPanel);
		add(centerPanel, BorderLayout.CENTER);
	}

	private void createCenterPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());

		JPanel conditionPanel = new JPanel();
		createConditionPanel(conditionPanel);
		JPanel resultPanel = new JPanel();
		createResultPanel(resultPanel);

		parent.add(conditionPanel, BorderLayout.NORTH);
		parent.add(resultPanel, BorderLayout.CENTER);

		setFilterComponentAttribute();
	}

	private void createConditionPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));

		JPanel filterPanel = new JPanel();
		createFilterPanel(filterPanel);

		parent.add(filterPanel);
	}

	private void createFilterPanel(JPanel parent) {
		parent.setLayout(new GridBagLayout());

		parent.add(new JLabel("IP地址"), new GridBagConstraints(0, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 0, 5), 0, 0));
		parent.add(ipField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 0, 5), 0, 0));

		parent.add(new JLabel("诊断类型"), new GridBagConstraints(2, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 0, 5), 0, 0));
		parent.add(filterComboBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 0, 5), 0, 0));

		parent.add(scanBtn, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 0, 5), 0, 0));

		parent.add(clearBtn, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 0, 5), 0, 0));
	}

	private void setFilterComponentAttribute() {
		ipField.setColumns(15);
		for (String filter : FILTERS) {
			filterComboBox.addItem(filter);
		}
		filterComboBox.setSelectedIndex(0);
		filterComboBox.setEditable(false);
		scanBtn.setAction(new ScanAction("点击诊断"));
		clearBtn.setAction(new ClearAction("清除数据"));
		leftArea.setLineWrap(true);
		rightArea.setLineWrap(true);
	}

	private void createResultPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());

		JPanel leftPanel = new JPanel();
		createLeftResultPanel(leftPanel);
		JPanel rightPanel = new JPanel();
		createRightResultPanel(rightPanel);

		JSplitPane splitPanel = new JSplitPane();
		splitPanel.setBorder(BorderFactory.createEmptyBorder());
		splitPanel.setDividerSize(0);
		splitPanel.setResizeWeight(0.5);
		splitPanel.setLeftComponent(leftPanel);
		splitPanel.setRightComponent(rightPanel);
		splitPanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		parent.add(splitPanel, BorderLayout.CENTER);
	}

	private void createLeftResultPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		parent.setBorder(BorderFactory.createTitledBorder("原始数据"));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(leftArea);
		parent.add(scrollPane, BorderLayout.CENTER);
	}

	private void createRightResultPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		parent.setBorder(BorderFactory.createTitledBorder("使用数据"));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(rightArea);
		parent.add(scrollPane, BorderLayout.CENTER);
	}

	private class ScanAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ScanAction(String title) {
			super(title);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String ip = ipField.getText();
			if (null == ip || "".equals(ip)) {
				JOptionPane.showMessageDialog(null, "IP地址不能为空，请输入IP地址。", "提示",
						JOptionPane.NO_OPTION);
				return;
			}
			int index = filterComboBox.getSelectedIndex();
			if (index == 0) {
				JOptionPane.showMessageDialog(null, "请选择诊断类型。", "提示",
						JOptionPane.NO_OPTION);
				return;
			}
			if (index == 1) {
				lldpHandler.getLldpInfoTable(ip);
			} else if (index == 2) {
				topologyHandler.getSystemInfo(ip, "public");
			} else if (index == 3) {
				eponHandler.getOltPortStateTable(ip, "public");
			} else if (index == 4) {
				topologyHandler.getAddrTableWithTable(ip, "public");
			} else if (index == 5) {
				topologyHandler.getIpNetToMediaTableWithTableFilter(ip,
						"public");
			} else if (index == 6) {
				eponHandler.getDot1dStpPortTable(ip, "public");
			} else if (index == 7) {
				topologyHandler.getLayer3LLDPTable(ip, "public");
			}
		}

	}

	private class ClearAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ClearAction(String title) {
			super(title);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			leftArea.setText(null);
			rightArea.setText(null);
		}
	}

	public IDiagnose getDiagnoseReference(int type) {
		return diagnoseMap.get(type);
	}

	IDiagnose layer2lldp = new IDiagnose() {
		@Override
		public void receiveInfo(String source, String target) {
			if (filterComboBox.getSelectedIndex() == 1) {
				appendLog(source, target);
			}
		}
	};

	IDiagnose system = new IDiagnose() {
		@Override
		public void receiveInfo(String source, String target) {
			if (filterComboBox.getSelectedIndex() == 2) {
				appendLog(source, target);
			}
		}
	};
	IDiagnose ifTable = new IDiagnose() {
		@Override
		public void receiveInfo(String source, String target) {
			if (filterComboBox.getSelectedIndex() == 3) {
				appendLog(source, target);
			}
		}
	};
	IDiagnose ipAddrTable = new IDiagnose() {
		@Override
		public void receiveInfo(String source, String target) {
			if (filterComboBox.getSelectedIndex() == 4) {
				appendLog(source, target);
			}
		}
	};
	IDiagnose ipNetToMediaTable = new IDiagnose() {
		@Override
		public void receiveInfo(String source, String target) {
			if (filterComboBox.getSelectedIndex() == 5) {
				appendLog(source, target);
			}
		}
	};
	IDiagnose dot1dStpPortTable = new IDiagnose() {
		@Override
		public void receiveInfo(String source, String target) {
			if (filterComboBox.getSelectedIndex() == 6) {
				appendLog(source, target);
			}
		}
	};
	IDiagnose layer3lldp = new IDiagnose() {
		@Override
		public void receiveInfo(String source, String target) {
			if (filterComboBox.getSelectedIndex() == 7) {
				appendLog(source, target);
			}
		}
	};

	private void appendLog(String source, String target) {
		leftArea.append(source);
		rightArea.append(target);

		if (leftArea.getLineCount() > 98) {
			try {
				String tmp = leftArea.getText();
				int index = tmp.indexOf("\n");
				leftArea.getDocument().remove(0, index + 1);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		if (rightArea.getLineCount() > 98) {
			try {
				String tmp = rightArea.getText();
				int index = tmp.indexOf("\n");
				rightArea.getDocument().remove(0, index + 1);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	public void setEponHandler(EponHandler eponHandler) {
		this.eponHandler = eponHandler;
	}

	public void setTopologyHandler(TopologyHandler topologyHandler) {
		this.topologyHandler = topologyHandler;
	}

	public void setLldpHandler(LldpHandler lldpHandler) {
		this.lldpHandler = lldpHandler;
	}

}
