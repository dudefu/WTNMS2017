package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.REFRESH;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.ComponentTitledBorder;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TopologyRefreshStrategy;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.message.TopoFoundFEPs;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(RefreshTopologyView.ID)
@Scope(Scopes.DESKTOP)
public class RefreshTopologyView extends ViewPart {

	private JButton refreshBtn;

	private JTextField diagramNameField;
	private final JPanel centerPnl = new JPanel();
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JXTable table = new JXTable();
	private final FepTableModel tableModel = new FepTableModel();
	private final JCheckBox chkBox = new JCheckBox("全选(鼠标结合CTRL(或SHIFT)键支持多选)");

	private final JPanel bottomPnl = new JPanel();
	private JButton closeBtn = null;

	private static final long serialVersionUID = 1L;
	public static final String ID = "refreshTopologyView";
	private ButtonFactory buttonFactory;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;

	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;

	private long longTime;

	private JTextField timeField;

	private JButton button;

	public static MessageSender messageSender;

	public static TopoFoundFEPs topoFoundFEPs;

	@PostConstruct
	protected void initialize() {
		this.setTitle("拓扑发现");
		messageSender = remoteServer.getMessageSender();
		buttonFactory = actionManager.getButtonFactory(this);
		init();
		// 查询前置机列表
		queryData();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	private void init() {
		setLayout(new BorderLayout(0, 0));

		initCenterPnl();
		initBottomPnl();

		add(centerPnl, BorderLayout.CENTER);
		add(bottomPnl, BorderLayout.SOUTH);

		this.setViewSize(450, 350);
	}

	private void initCenterPnl() {
		JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 5));
		JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 5));
		diagramNameField = new JTextField(30);
		timeField = new JTextField("1", 17);
		String diagramName = equipmentModel.getDiagramName();
		diagramNameField.setText(diagramName);
		textPanel.add(new JLabel("拓扑图名称"));
		textPanel.add(diagramNameField);
		textPanel.add(new StarLabel("(1-16个字符)"));
		timePanel.add(new JLabel("自动拓扑时间设定"));
		timePanel.add(timeField);
		timePanel.add(new StarLabel("(分钟)"));

		scrollPnl.getViewport().add(table);
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(scrollPnl, BorderLayout.CENTER);
		centerPnl.add(textPanel, BorderLayout.PAGE_END);
		//centerPnl.add(timePanel, BorderLayout.PAGE_END);
		ComponentTitledBorder titledBorder = new ComponentTitledBorder(chkBox, centerPnl,
				BorderFactory.createLineBorder(table.getGridColor()));
		centerPnl.setBorder(titledBorder);

		chkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					table.setRowSelectionAllowed(true);
					table.setRowSelectionInterval(0, table.getRowCount() - 1);
				} else if (table.getSelectedRows().length == table.getRowCount()) {
					table.clearSelection();
				}
			}
		});

		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.setSortable(false);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				boolean selected = table.getSelectedRows().length == table.getRowCount();
				chkBox.getModel().setSelected(selected);
				RefreshTopologyView.this.repaint();
			}
		});
	}

	private void initBottomPnl() {
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		refreshBtn = buttonFactory.createButton(REFRESH);
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(refreshBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}

	@SuppressWarnings("unchecked")
	private void queryData() {
		List<FEPEntity> listOfFep = new ArrayList<FEPEntity>();
		List<FEPTopoNodeEntity> listOfFepNode = (List<FEPTopoNodeEntity>) remoteServer.getService()
				.findAll(FEPTopoNodeEntity.class);

		if (listOfFepNode != null) {
			for (FEPTopoNodeEntity fepNode : listOfFepNode) {
				listOfFep.add(NodeUtils.getNodeEntity(fepNode).getFepEntity());
			}
		}
		tableModel.setData(listOfFep);
		table.setModel(tableModel);
	}

	@ViewAction(name = REFRESH, icon = ButtonConstants.START, desc = "拓扑发现操作", role = Constants.MANAGERCODE)
	public void refresh() {
		if (StringUtils.isBlank(diagramNameField.getText())) {
			JOptionPane.showMessageDialog(this, "拓扑图名称不能为空，请输入拓扑图名称", "提示", JOptionPane.ERROR_MESSAGE);
			return;
		}
		int nameLen = diagramNameField.getText().length();
		if (nameLen < 1 || nameLen > 16) {
			JOptionPane.showMessageDialog(this, "拓扑图名称的长度(" + Integer.toString(nameLen) + ")超出范围(1-16个字符)", "提示",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// final TopoFoundFEPs topoFoundFEPs = createTopoFoundFEPs();
		if (table.getSelectedRows().length == 0) {
			JOptionPane.showMessageDialog(this, "请选择前置机", "提示", JOptionPane.NO_OPTION);
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "该操作会清除当前所有的拓扑信息，你确定吗？", "提示", JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result) {
			return;
		}

		topoFoundFEPs = new TopoFoundFEPs();
		List<String> listOfFepCode = new ArrayList<String>();
		List<FEPEntity> listOfFep = new ArrayList<FEPEntity>();
		for (int row : table.getSelectedRows()) {
			FEPEntity fep = tableModel.getValue(row);
			listOfFepCode.add(fep.getCode());
			listOfFep.add(fep);
		}
		topoFoundFEPs.setFepCodes(listOfFepCode);
		equipmentModel.beginDiscover(listOfFep);
		equipmentModel.setDiagramName(diagramNameField.getText());

		Task task = new RefreshRequestTask(topoFoundFEPs);
		showMessageDialog(task);
		
		//开启自动拓扑
//		try {
//			longTime = Long.parseLong(timeField.getText());
//			AutoTopo(longTime);
//		} catch (Exception e1) {
//			JOptionPane.showMessageDialog(null, "输入的字符必须为整数", "提示", JOptionPane.ERROR_MESSAGE);
//			return;
//		}

	}

	// 定时自动拓扑
	public void AutoTopo(long longTime) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// 每次创建一个新对象
				topoFoundFEPs = new TopoFoundFEPs();
				List<String> listOfFepCode = new ArrayList<String>();
				List<FEPEntity> listOfFep = new ArrayList<FEPEntity>();
				for (int row : table.getSelectedRows()) {
					FEPEntity fep = tableModel.getValue(row);
					listOfFepCode.add(fep.getCode());
					listOfFep.add(fep);
				}
				topoFoundFEPs.setFepCodes(listOfFepCode);
				equipmentModel.beginDiscover(listOfFep);
				equipmentModel.setDiagramName(diagramNameField.getText());

				Task task = new RefreshRequestTask(topoFoundFEPs);
				task.run();
				topoFoundFEPs = null;
			}

		}, longTime * 60 * 1000, longTime * 60 * 1000);// 延迟10分钟后开始执行，间隔时间10分钟
	}

	private class RefreshRequestTask implements Task {

		private final TopoFoundFEPs topoFoundFEPs;

		public RefreshRequestTask(TopoFoundFEPs topoFoundFEPs) {
			this.topoFoundFEPs = topoFoundFEPs;
		}

		@Override
		public void run() {
			messageSender.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage message = session.createObjectMessage();
					message.setIntProperty(Constants.MESSAGETYPE, MessageNoConstants.TOPOSEARCH);
					Long diagramId = equipmentModel.getDiagram().getId();
					String username = clientModel.getCurrentUser().getUserName();
					topoFoundFEPs.setRefreshDiagramId(diagramId);
					message.setObject(topoFoundFEPs);
					message.setStringProperty(Constants.MESSAGEFROM, username);
					message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
					return message;
				}
			});
		}

	}

	private JProgressBarModel progressBarModel;
	private TopologyRefreshStrategy strategy;
	private JProgressBarDialog dialog;

	private void showMessageDialog(final Task task) {
		if (SwingUtilities.isEventDispatchThread()) {
			progressBarModel = new JProgressBarModel();
			strategy = new TopologyRefreshStrategy("拓扑发现", progressBarModel, table.getSelectedRowCount());
			dialog = new JProgressBarDialog("提示", 0, 1, this, "拓扑发现", true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task);
				}
			});
		}
	}

	public class FepTableModel extends AbstractTableModel {

		public FepTableModel() {
			listOfFep = new ArrayList<FEPEntity>();

			int modelIndex = 0;
			fepColumnModel = new DefaultTableColumnModel();
			TableColumnExt codeColumn = new TableColumnExt(modelIndex++, 120);
			codeColumn.setIdentifier("code");
			codeColumn.setHeaderValue("编号");
			fepColumnModel.addColumn(codeColumn);

			TableColumnExt addressColumn = new TableColumnExt(modelIndex++, 120);
			addressColumn.setIdentifier("address");
			addressColumn.setHeaderValue("地址");
			fepColumnModel.addColumn(addressColumn);
		}

		@Override
		public int getColumnCount() {
			return fepColumnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return listOfFep.size();
		}

		@Override
		public String getColumnName(int col) {
			return fepColumnModel.getColumn(col).getHeaderValue().toString();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if (row < listOfFep.size()) {
				FEPEntity fep = listOfFep.get(row);
				switch (col) {
				case 0:
					value = fep.getCode();
					break;
				case 1:
					value = fep.getIpValue();
					break;
				default:
					break;
				}
			}
			return value;
		}

		public TableColumnModel getColumnModel() {
			return fepColumnModel;
		}

		public FEPEntity getValue(int row) {
			FEPEntity value = null;
			if (row < listOfFep.size()) {
				value = listOfFep.get(row);
			}
			return value;
		}

		public void setData(List<FEPEntity> feps) {
			if (feps == null) {
				return;
			}
			this.listOfFep = feps;
		}

		private List<FEPEntity> listOfFep;
		private final TableColumnModel fepColumnModel;
		private static final long serialVersionUID = -3065768803494840568L;
	}
}