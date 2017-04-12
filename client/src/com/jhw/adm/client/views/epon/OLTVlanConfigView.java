package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTVlan;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(OLTVlanConfigView.ID)
@Scope(Scopes.DESKTOP)
public class OLTVlanConfigView extends ViewPart {
	private static final long serialVersionUID = 848009940348895044L;
	public static final String ID = "OLTVlanConfigView";
	private static final String DEFAULT_VLAN_ID = "1";

	private final JPanel configPnl = new JPanel();
	private final JToolBar toolBar = new JToolBar();
	private JButton addBtn;
	private JButton saveBtn;
	private JButton delBtn;

	private final JPanel inputPnl = new JPanel();
	private final JLabel vlanIdLbl = new JLabel();
	private final NumberField vlanIdFld = new NumberField();
	private final JLabel vlanNameLbl = new JLabel();
	private final JTextField vlanNameFld = new JTextField();
	private final JLabel egressPortLbl = new JLabel();
	private final JComboBox egressPortCombox = new JComboBox();
	private final JLabel forbiddenLbl = new JLabel();
	private final JComboBox forbiddenCombox = new JComboBox();

	private JScrollPane portScrollPnl;
	private final JPanel untagPortPnl = new JPanel();
	private final List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();

	private final JPanel vlanPnl = new JPanel();
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JTable table = new JTable();
	private VlanTableModel model = null;

	private final JPanel bottomPnl = new JPanel();
	private JButton closeBtn = new JButton();

	private OLTEntity selectedOlt = null;

	private ButtonFactory buttonFactory;

	private static final Logger LOG = LoggerFactory.getLogger(OLTVlanConfigView.class);

	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;

	@Resource(name = DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	private MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy;
	private boolean isExists = false;//标记新增还是修改
	private OLTVlan oltVlan = null;
	private String tempVlanID = "";
	private static final int DOUBLE_CLICK = 2;

	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;

	@PostConstruct
	protected void initialize() {
		messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
		init();
		queryData();
	}

	private void init() {
		buttonFactory = actionManager.getButtonFactory(this);
		this.setLayout(new BorderLayout());
		initConfigPnl();
		initVlanPnl();
		initBottomPnl();

		this.add(configPnl, BorderLayout.NORTH);
		this.add(vlanPnl, BorderLayout.CENTER);
		this.add(bottomPnl, BorderLayout.SOUTH);
		// 得到资源文件
		setResource();
	}

	private void initConfigPnl() {
		addBtn = buttonFactory.createButton(APPEND);
		saveBtn = buttonFactory.createButton(SAVE);
		delBtn = buttonFactory.createButton(DELETE);
		toolBar.add(addBtn);
		toolBar.add(saveBtn);
		toolBar.add(delBtn);
		toolBar.setFloatable(false);

		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(vlanIdLbl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		panel.add(vlanIdFld, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 0, 0), 0, 0));
		panel.add(vlanNameLbl, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 0, 0, 0), 0, 0));
		panel.add(vlanNameFld, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 0, 0), 0, 0));

		panel.add(egressPortLbl, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 0, 0, 0), 0, 0));
		panel.add(egressPortCombox, new GridBagConstraints(1, 2, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 0, 0), 0, 0));
		panel.add(forbiddenLbl, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 0, 0, 0), 0, 0));
		panel.add(forbiddenCombox, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 0, 0), 0, 0));
		inputPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		inputPnl.add(panel);

		untagPortPnl.setBackground(Color.WHITE);

		portScrollPnl = new JScrollPane();
		portScrollPnl.getViewport().add(untagPortPnl);
		portScrollPnl.setPreferredSize(new Dimension(320, 120));
		JPanel portPnl = new JPanel(new BorderLayout());
		portPnl.setBorder(BorderFactory.createTitledBorder("Untag端口"));
		portPnl.add(portScrollPnl);

		JPanel container = new JPanel(new GridBagLayout());
		container.add(inputPnl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		container.add(portPnl, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 20, 0, 0), 0, 0));
		JPanel middlePnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		middlePnl.add(container);
		middlePnl.setBorder(BorderFactory.createTitledBorder("Vlan选项"));

		configPnl.setLayout(new BorderLayout());
		configPnl.add(toolBar, BorderLayout.NORTH);
		configPnl.add(middlePnl, BorderLayout.CENTER);
	}

	private void initVlanPnl() {
		model = new VlanTableModel();
		table.setModel(model);
		scrollPnl.getViewport().add(table);
		vlanPnl.setLayout(new BorderLayout());
		vlanPnl.add(scrollPnl);
	}

	private void initBottomPnl() {
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.add(closeBtn);
	}

	private void setResource() {
		this.setTitle("OLT VLAN 配置");
		vlanIdLbl.setText("VLAN ID");
		vlanNameLbl.setText("VLAN NAME");
		egressPortLbl.setText("Egress Ports");
		forbiddenLbl.setText("ForbiddenEgress Ports");

		vlanIdFld.setColumns(20);
		vlanNameFld.setColumns(20);
		vlanNameFld.setDocument(new TextFieldPlainDocument(vlanNameFld));

		this.setEnable(false);

		setViewSize(640, 550);

//		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//			public void valueChanged(ListSelectionEvent e) {
//				selectVlan();
//			}
//		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() >= DOUBLE_CLICK){
					selectVlan();	
				}
			}
		});

		// 增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(
				EquipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}

	/**
	 *从服务器查询设备的vlan信息
	 */
	@SuppressWarnings("unchecked")
	private void queryData() {
		selectedOlt = (OLTEntity) adapterManager.getAdapter(equipmentModel
				.getLastSelected(), OLTEntity.class);

		if (null == selectedOlt) {
			return;
		}

		setPorts();

		String where = " where entity.oltEntity=?";
		Object[] parms = { selectedOlt };

		List<OLTVlan> oltVlanList = (List<OLTVlan>) remoteServer.getService()
				.findAll(OLTVlan.class, where, parms);
		if (null == oltVlanList || oltVlanList.size() < 1) {
			return;
		}

		model.setDataList(oltVlanList);
		model.fireTableDataChanged();
	}

	private void setPorts() {
		egressPortCombox.removeAllItems();
		forbiddenCombox.removeAllItems();
		if(selectedOlt.getPorts() == null){
			return;
		}
		
		int portCount = selectedOlt.getPorts().size();

		for (int i = 0; i < portCount; i++) {
			String gePort = "GigeEthernet0/" + (i + 1);
			String fePort = "FastEthernet0/" + (i + 1);
			egressPortCombox.addItem(gePort);
			forbiddenCombox.addItem(fePort);
		}

		setPortPnlLayout();
	}

	private void setPortPnlLayout() {

		int portCount = selectedOlt.getPorts().size();
		JCheckBox[] checkBox = new JCheckBox[portCount];

		int row = 0;
		int overage = portCount % 3;
		if (overage > 0) {
			row = portCount / 3 + 1;
		} else {
			row = portCount / 3;
		}
		untagPortPnl.setLayout(new GridLayout(row, 3));
		untagPortPnl.removeAll();
		checkBoxList.clear();
		for (int i = 0; i < portCount; i++) {
			checkBox[i] = new JCheckBox("端口" + (i + 1));
			checkBox[i].setEnabled(false);

			untagPortPnl.add(checkBox[i]);
			checkBox[i].setBackground(Color.WHITE);
			// 把所有的JCheckBox保存到列表checkBoxList中
			checkBoxList.add(i, checkBox[i]);
		}

		untagPortPnl.updateUI();
	}

	/**
	 * 新增按钮事件
	 */
	@ViewAction(name = APPEND, icon = ButtonConstants.APPEND, desc = "增加OLT VLAN", role = Constants.MANAGERCODE)
	public void append() {
		if (null == selectedOlt) {
			JOptionPane.showMessageDialog(this, "请选择EPON设备", "提示",
					JOptionPane.NO_OPTION);
			return;
		}

		isExists = false;
		tempVlanID = "";
		this.oltVlan = null;
		clear();
		setEnable(true);
	}

	/**
	 * 设置输入控件是否可选
	 * 
	 * @param enable
	 */
	private void setEnable(boolean enable) {
		vlanIdFld.setEditable(enable);
		vlanNameFld.setEditable(enable);
		egressPortCombox.setEnabled(enable);
		forbiddenCombox.setEnabled(enable);
		vlanIdFld.setBackground(Color.WHITE);
		vlanNameFld.setBackground(Color.WHITE);
		for (JCheckBox checkBox : checkBoxList) {
			checkBox.setEnabled(enable);
		}
	}

	/**
	 * 保存按钮事件
	 */
	
	@ViewAction(name = SAVE, icon = ButtonConstants.SAVE, desc = "保存VLAN", role = Constants.MANAGERCODE)
	public void save() {
		if (!isValids()) {
			return;
		}
		
		String vlanID = vlanIdFld.getText().trim();
		if(StringUtils.isBlank(tempVlanID)){//新增
			int rowCount = model.getRowCount();
			for (int row = 0; row < rowCount; row++) {
				String vlan = model.getValueAt(row, 0).toString();
				if (vlan.equals(vlanID)) {
					JOptionPane.showMessageDialog(this, "该VLAN ID已经存在，不允许重复添加", "提示", JOptionPane.NO_OPTION);
					return;
				}
			}
		}else{
			if(vlanID.equals(tempVlanID)){//不变Vlan ID
				//
			}else{//改变Vlan ID
				int rowCount = model.getRowCount();
				for (int row = 0; row < rowCount; row++) {
					String vlan = model.getValueAt(row, 0).toString();
					if (vlan.equals(vlanID)) {
						JOptionPane.showMessageDialog(this, "该VLAN ID已经存在，不允许重复添加", "提示", JOptionPane.NO_OPTION);
						return;
					}
				}
			}
		}

		int sync = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (sync == 0) {
			return;
		}

		if(null == this.oltVlan){//新增时
			this.oltVlan = new OLTVlan();
		}

		this.oltVlan.setVlanID(vlanID);
		this.oltVlan.setVlanName(vlanNameFld.getText().trim());
		this.oltVlan.setEgressPort(egressPortCombox.getSelectedItem().toString());
		this.oltVlan.setForbiddenEgressPort(forbiddenCombox.getSelectedItem().toString());
		String ports = StringUtils.EMPTY;
		for (int i = 0; i < checkBoxList.size(); i++) {
			JCheckBox checkBox = checkBoxList.get(i);
			if (checkBox.isSelected()) {
				if (StringUtils.isNotBlank(ports)) {
					ports += ",";
				}
				ports = ports + (i + 1);
			}
		}
		this.oltVlan.setUntaggedPort(ports);
		this.oltVlan.setOltEntity(selectedOlt);

		String address = selectedOlt.getIpValue();
		String from = clientModel.getCurrentUser().getUserName();
		String localAddress = clientModel.getLocalAddress();
		int messageNo = MessageNoConstants.OLTVLANADD;


		List<Serializable> list = new ArrayList<Serializable>();
		list.add(this.oltVlan);
		messageOfSaveProcessorStrategy.showInitializeDialog("保存VLAN", this);
		try {
			if (isExists == false) {
				remoteServer.getAdmService().saveAndSettingByIP(address,
						messageNo, list, from, localAddress, Constants.DEV_OLT,sync);
			} else {
				remoteServer.getAdmService().updateAndSettingByIp(address,
						messageNo, list, from, localAddress, Constants.DEV_OLT,sync);
			}
		} catch (JMSException e) {
			LOG.error("保存VLAN失败", e);
			messageOfSaveProcessorStrategy.showErrorMessage("保存VLAN失败");
		}
		if (sync == Constants.SYN_SERVER) {
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		clear();
		queryData();
		setEnable(false);
	}

	@ViewAction(name = DELETE, icon = ButtonConstants.DELETE, desc = "删除VLAN", role = Constants.MANAGERCODE)
	public void delete() {
		if (null == selectedOlt) {
			JOptionPane.showMessageDialog(this, "请选择 OLT 设备", "提示",
					JOptionPane.NO_OPTION);
			return;
		}

		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		
		OLTVlan oltVlan = model.getVlan(row);
		
		if (oltVlan.getVlanID().equals(DEFAULT_VLAN_ID)) {
			JOptionPane.showMessageDialog(this, "默认的 VLAN 不能删除", "提示",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？", "提示",
				JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result) {
			return;
		}

		int sync = PromptDialog.showPromptDialog(this, "请选择删除的方式",imageRegistry);
		if (sync == 0) {
			return;
		}

		String address = selectedOlt.getIpValue();
		String from = clientModel.getCurrentUser().getUserName();
		String localAddress = clientModel.getLocalAddress();
		int messageNo = MessageNoConstants.OLTVLANDEL;
		List<OLTVlan> list = new ArrayList<OLTVlan>();
		list.add(oltVlan);
		messageOfSaveProcessorStrategy.showInitializeDialog("删除 VLAN", this);
		try {
			remoteServer.getAdmService().deleteAndSettingByIp(address,
					messageNo, list, from, localAddress, Constants.DEV_OLT, sync);
		} catch (JMSException e) {
			LOG.error("删除 VLAN 失败", e);
			messageOfSaveProcessorStrategy.showErrorMessage("删除 VLAN 失败");
		}
		if(sync == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		clear();
		queryData();
		setEnable(false);
	}

	/**
	 * vlan表格中被选择行的事件响应 根据所选择的Vlan，在端口表格中相应显示Tag和untag端口
	 */
	private void selectVlan() {
		setEnable(true);
		
		for (int i = 0; i < checkBoxList.size(); i++) {
			checkBoxList.get(i).setSelected(false);
		}

		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		this.oltVlan = model.getVlan(row);
		String vlanId = oltVlan.getVlanID();
		String vlanName = oltVlan.getVlanName();
		String egressPort = oltVlan.getEgressPort();
		String forbiddenPort = oltVlan.getForbiddenEgressPort();
		String untagPorts = oltVlan.getUntaggedPort();

		vlanIdFld.setText(vlanId);
		vlanNameFld.setText(vlanName);
		egressPortCombox.setSelectedItem(egressPort);
		forbiddenCombox.setSelectedItem(forbiddenPort);

		String[] ports = StringUtils.split(untagPorts, ",");

		if (ports != null) {
			for (int k = 0; k < ports.length; k++) {
				String portName = StringUtils.right(ports[k], 1);
				if (StringUtils.isBlank(portName)) continue;
				
				for (int i = 0; i < checkBoxList.size(); i++) {
					if ((i + 1) == Integer.parseInt(portName)) {
						(checkBoxList.get(i)).setSelected(true);
					}
				}
			}
		}
		isExists = true;
		tempVlanID = vlanId;
	}

	/**
	 * 设备浏览器的时间监听 当选择不同的设备时，根据switchNodeEntity,重新查询数据库
	 * 
	 * @author Administrator
	 * 
	 */
	private final PropertyChangeListener eponNodeChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() instanceof EponTopoEntity) {
				queryData();
			} else {
				clearPanel();
			}
		}
	};

	private void clearPanel() {
		clear();
		selectedOlt = null;

		untagPortPnl.removeAll();
		checkBoxList.clear();
		model.setDataList(null);
		model.fireTableDataChanged();
		untagPortPnl.revalidate();
	}

	/**
	 * 清空控件中的值
	 */
	private void clear() {
		vlanIdFld.setText(StringUtils.EMPTY);
		vlanNameFld.setText(StringUtils.EMPTY);
		egressPortCombox.setSelectedItem(StringUtils.EMPTY);
		forbiddenCombox.setSelectedItem(StringUtils.EMPTY);

		for (int i = 0; i < checkBoxList.size(); i++) {
			JCheckBox checkBox = checkBoxList.get(i);
			checkBox.setSelected(false);
		}
	}

	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(
				EquipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}

	public boolean isValids() {
		boolean isValid = true;

		if (null == this.selectedOlt) {
			isValid = false;
			JOptionPane.showMessageDialog(this, "请选择 OLT 设备", "提示",
					JOptionPane.NO_OPTION);
			return isValid;
		}
		
		int vlanId = NumberUtils.toInt(vlanIdFld.getText());

		if (vlanId < 1 || vlanId > 4094) {
			isValid = false;
			JOptionPane.showMessageDialog(this, "VLAN ID 错误，范围为：1-4094", "提示",
					JOptionPane.NO_OPTION);
			return isValid;
		}

		return isValid;
	}

	private class VlanTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -2029693199786317795L;
		private List<OLTVlan> dataList = null;
		private final String[] COLUMN_NAME = { "VLAN ID", "VLAN 名称",
				"Egress Ports", "ForbiddenEgress Ports", "Untag Ports" };

		protected VlanTableModel() {
		}

		public void setDataList(List<OLTVlan> dataList) {
			if (null == dataList) {
				this.dataList = new ArrayList<OLTVlan>();
			} else {
				this.dataList = dataList;
			}
		}

		public int getRowCount() {
			if (null == dataList) {
				return 0;
			}
			return dataList.size();
		}

		public int getColumnCount() {
			return COLUMN_NAME.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return COLUMN_NAME[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (null == dataList || dataList.size() < 1) {
				return null;
			}
			OLTVlan vlan = dataList.get(rowIndex);
			Object value = null;

			switch (columnIndex) {
			case 0:
				value = vlan.getVlanID();
				break;
			case 1:
				value = vlan.getVlanName();
				break;
			case 2:
				value = vlan.getEgressPort();
				break;
			case 3:
				value = vlan.getForbiddenEgressPort();
				break;
			case 4:
				value = vlan.getUntaggedPort();
				break;
			default:
				break;
			}

			return value;
		}

		public OLTVlan getVlan(int row) {
			return dataList.get(row);
		}
	}
}