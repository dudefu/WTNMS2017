package com.jhw.adm.client.views.carrier;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.CLEAN;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.model.carrier.CarrierPort;
import com.jhw.adm.client.swing.MessageOfCarrierConfigProcessorStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(CarrierConfigureRouteView.ID)
@Scope(Scopes.DESKTOP)
public class CarrierConfigureRouteView extends ViewPart {
	public static final String ID = "carrierConfigureRouteView";

	private static final long serialVersionUID = 1L;

	private final JTable table = new JTable();
	private RouteTableModel tableModel = null;

	private final JTextField versionField = new JTextField(10);
	private final JTextField routeCountField = new JTextField(10);

	private final JComboBox portBox = new JComboBox();
	private JFormattedTextField plcIdField = null;

	private final JButton addBtn = new JButton();
	private final JButton deleteBtn = new JButton();

	private JButton synBtn = null;
	private JButton cleanBtn = null;
	private JButton closeBtn = null;

	private JCheckBox netWorkSideChkBox; // 清空网管侧
	private JCheckBox deviceSideChkBox; // 清空设备侧

	private static final String[] COLUMN_NAME = { "端口号", "设备ID" };

	private ActionMap actionMap = null;

	private CarrierEntity carrierEntity = null;

	private MessageSender messageSender;
	private static final Logger LOG = LoggerFactory.getLogger(CarrierConfigureRouteView.class);
	private final MessageOfCarrierConfigProcessorStrategy messageOfCarrierProcessorStrategy = new MessageOfCarrierConfigProcessorStrategy();

	private ButtonFactory buttonFactory;

	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;

	@Resource(name = CarrierPort.ID)
	private CarrierPort carrierPort;

	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;

	@PostConstruct
	protected void initialize() {
		init();
	}

	private void init() {
		buttonFactory = actionManager.getButtonFactory(this);
		messageSender = remoteServer.getMessageSender();
		setTitle("载波机路由设置");
		setViewSize(540, 420);
		setLayout(new BorderLayout());

		JPanel toolPanel = new JPanel(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());

		closeBtn = buttonFactory.createCloseButton();
		synBtn = buttonFactory.createButton(UPLOAD);
		cleanBtn = buttonFactory.createButton(CLEAN);
		newPanel.add(synBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		// toolPanel.add(saveBtn);
		newPanel.add(cleanBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		toolPanel.add(newPanel, BorderLayout.EAST);

		add(toolPanel, BorderLayout.PAGE_END);

		JPanel content = new JPanel();
		createContents(content);
		add(content, BorderLayout.CENTER);

		setResource();
		queryData();
	}

	private void createContents(JPanel parent) {
		parent.setLayout(new BorderLayout());

		JPanel addRouteTool = new JPanel(new FlowLayout(FlowLayout.LEADING));
		addRouteTool.setBorder(BorderFactory.createTitledBorder("添加路由"));

		NumberFormat integerFormat = NumberFormat.getNumberInstance();
		integerFormat.setMinimumFractionDigits(0);
		integerFormat.setParseIntegerOnly(true);
		integerFormat.setGroupingUsed(false);

		addRouteTool.add(new JLabel("端口号"));
		ListComboBoxModel portBoxModel = new ListComboBoxModel(carrierPort
				.toList());
		portBox.setModel(portBoxModel);
		if (portBox.getItemCount() > 0) {
			portBox.setSelectedIndex(0);
		}
		addRouteTool.add(portBox);
		addRouteTool.add(new JLabel("设备ID"));
		plcIdField = new JFormattedTextField(integerFormat);
		plcIdField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if (c < '0' || c > '9') {
					arg0.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
		plcIdField.setPreferredSize(new Dimension(
				plcIdField.getPreferredSize().width,
				portBox.getPreferredSize().height));
		plcIdField.setValue(new Integer(0));
		plcIdField.setColumns(10);
		addRouteTool.add(plcIdField);

		addRouteTool.add(new StarLabel());
		addRouteTool.add(addBtn);
		addRouteTool.add(deleteBtn);

		JPanel tablePanel = new JPanel(new BorderLayout(1, 2));
		tablePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tablePanel.add(table, BorderLayout.CENTER);

		// JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel infoPanel = new JPanel(new BorderLayout());
		JPanel leftInfoPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel rightInofPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		infoPanel.add(leftInfoPnl, BorderLayout.WEST);
		// infoPanel.add(rightInofPnl,BorderLayout.CENTER);

		versionField.setEnabled(false);
		routeCountField.setEnabled(false);
		leftInfoPnl.add(new JLabel("版本号"));
		leftInfoPnl.add(versionField);
		leftInfoPnl.add(new JLabel("路由数量"));
		leftInfoPnl.add(routeCountField);

		netWorkSideChkBox = new JCheckBox("清空网管侧");
		deviceSideChkBox = new JCheckBox("清空设备侧");
		rightInofPnl.add(netWorkSideChkBox);
		rightInofPnl.add(deviceSideChkBox);

		tableModel = new RouteTableModel();
		tableModel.setColumnName(COLUMN_NAME);
		table.setModel(tableModel);
		tableModel.setDataList(null);

		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.setBorder(BorderFactory.createTitledBorder("路由列表"));
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);

		parent.add(addRouteTool, BorderLayout.PAGE_START);
		parent.add(scrollTablePanel, BorderLayout.CENTER);
		parent.add(infoPanel, BorderLayout.PAGE_END);
	}

	private void setResource() {
		actionMap = actionManager.getActionMap(this);
		addBtn.setAction(actionMap.get(APPEND));
		deleteBtn.setAction(actionMap.get(DELETE));

		// 增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(
				EquipmentModel.PROP_LAST_SELECTED, carrierChangeListener);
	}

	private void queryData() {
		carrierEntity = (CarrierEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), CarrierEntity.class);
		if (carrierEntity != null) {
			setValue(carrierEntity.getRoutes());
		}
	}

	@SuppressWarnings("unchecked")
	private void setValue(Set<CarrierRouteEntity> carrierRouteEntitySet) {
		if(null == carrierRouteEntitySet){
			return;
		}
		int routeCount = carrierRouteEntitySet.size();
		versionField.setText(String.valueOf(carrierEntity.getVersion()));
		routeCountField.setText(String.valueOf(routeCount));

		Iterator iterator = carrierRouteEntitySet.iterator();
		if (null == iterator) {
			return;
		}

		List<List> dataList = new ArrayList<List>();
		while (iterator.hasNext()) {
			CarrierRouteEntity entity = (CarrierRouteEntity) iterator.next();
			String port = (carrierPort.get(entity.getPort()))
					.getKey();
			String deviceId = String.valueOf(entity.getCarrierCode());

			List rowList = new ArrayList();
			rowList.add(0, port);
			rowList.add(1, deviceId);
			rowList.add(2, entity);
			dataList.add(rowList);
		}
		tableModel.setDataList(dataList);
		tableModel.fireTableDataChanged();
	}

	@ViewAction(name = APPEND, icon = ButtonConstants.APPEND, desc = "添加载波机路由", role = Constants.MANAGERCODE)
	public void append() {
		if (null == carrierEntity) {
			JOptionPane.showMessageDialog(this, "请选择载波机", "提示",JOptionPane.NO_OPTION);
			return;
		}

		if (null == carrierEntity.getId()) {
			JOptionPane.showMessageDialog(this, "请先保存载波机拓扑", "提示",JOptionPane.NO_OPTION);
			return;
		}

		if (!isValids()) {
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0) {
			return;
		}

		int port = ((StringInteger) portBox.getSelectedItem()).getValue();
		String deviceId = plcIdField.getText().trim();

		CarrierRouteEntity entity = new CarrierRouteEntity();
		entity.setPort(port);
		entity.setCarrierCode(NumberUtils.toInt(deviceId));

		showSaveMessageDialog();
		try {
			remoteServer.getAdmService().saveAndSettingCarrier(MessageNoConstants.CARRIERROUTECONFIG,
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),
					carrierEntity.getCarrierCode(), entity, result);
		} catch (JMSException e) {
			messageOfCarrierProcessorStrategy.showErrorMessage("添加出现异常");
			LOG.error("添加载波机路由错误", e);
		}
		if(result == Constants.SYN_SERVER){
			messageOfCarrierProcessorStrategy.showNormalMessage("S");
		}
		updateCarrierEntity(null);
	}
	
	private void showSaveMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("添加", this);
	}

	@ViewAction(name = DELETE, icon = ButtonConstants.DELETE, desc = "删除载波机路由", role = Constants.MANAGERCODE)
	public void delete() {
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？", "提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result) {
			return;
		}

		if (null == carrierEntity) {
			JOptionPane.showMessageDialog(this, "请选择载波机", "提示",JOptionPane.NO_OPTION);
			return;
		}

		if (null == carrierEntity.getId()) {
			JOptionPane.showMessageDialog(this, "请先保存载波机拓扑", "提示",JOptionPane.NO_OPTION);
			return;
		}

		
		ArrayList<CarrierRouteEntity> list = new ArrayList<CarrierRouteEntity>();
//		int rowCount = table.getRowCount();
//		for(int row = 0 ; row < rowCount; row++){
//			CarrierRouteEntity entity = (CarrierRouteEntity) tableModel.getValueAt(row, 2);
//			list.add(entity);
//		}
		
		int[] rows = table.getSelectedRows();
		if (rows.length < 1) {
			return;
		}


		for (int i = rows.length - 1; i >= 0; i--) {
			CarrierRouteEntity entity = (CarrierRouteEntity) tableModel
					.getValueAt(rows[i], 2);
			list.add(entity);
			
//			CarrierRouteEntity entity = (CarrierRouteEntity) tableModel
//				.getValueAt(rows[i], 2);
//			if (list.contains(entity)){
//				list.remove(entity);
//			}
		}

		
		showDeleteMessageDialog();
		try {
			remoteServer.getAdmService().deleteAndSettingCarrier(
					MessageNoConstants.CARRIERROUTECONFIG,
					clientModel.getCurrentUser().getUserName(),
					clientModel.getLocalAddress(),
					carrierEntity.getCarrierCode(), list, Constants.SYN_ALL);
		} catch (JMSException e) {
			messageOfCarrierProcessorStrategy.showErrorMessage("删除出现异常");
			LOG.error("删除载波机路由错误", e);
		}
		updateCarrierEntity(null);
	}
	
	private void showDeleteMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("删除", this);
	}

	@SuppressWarnings("unchecked")
	private ArrayList getRouteLists() {
		ArrayList list = new ArrayList();
		for (int i = 0; i < table.getRowCount(); i++) {
			CarrierRouteEntity entity = (CarrierRouteEntity) table.getValueAt(
					i, 2);
			list.add(entity);
		}
		return list;
	}

	@ViewAction(name = UPLOAD, icon = ButtonConstants.SYNCHRONIZE, desc = "向上同步载波机路由信息", role = Constants.MANAGERCODE)
	public void upSynchronize() {
		if (null == carrierEntity) {
			JOptionPane.showMessageDialog(this, "请选择载波机", "提示",JOptionPane.NO_OPTION);
			return;
		}
		if (null == carrierEntity.getId()) {
			JOptionPane.showMessageDialog(this, "请先保存载波机拓扑", "提示",JOptionPane.NO_OPTION);
			return;
		}

		List<CarrierEntity> listOfCarrier = remoteServer.getService().queryCarrierEntity();
		if (listOfCarrier == null) {
			JOptionPane.showMessageDialog(this, "请先设置一台中心载波机","提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (listOfCarrier.size() > 1) {
			JOptionPane.showMessageDialog(this, "中心载波机只能有一台","提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int result1 = JOptionPane.showConfirmDialog(this, "确定需要同步载波机路由吗？", "提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result1) {
			return;
		}
		
		showQueryMessageDialog();
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.CARRIERROUTEQUERY);
				message.setObject(carrierEntity);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel
						.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel
						.getLocalAddress());
				return message;
			}
		});
	}
	
	private void showQueryMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("向上同步", this);
	}

	@ViewAction(name = CLEAN, icon = ButtonConstants.CLEAN, desc = "清空载波机路由信息", role = Constants.MANAGERCODE)
	public void clean() {
		if (null == carrierEntity) {
			JOptionPane.showMessageDialog(this, "请选择载波机", "提示",JOptionPane.NO_OPTION);
			return;
		}

		if (null == carrierEntity.getId()) {
			JOptionPane.showMessageDialog(this, "请先保存载波机拓扑", "提示",JOptionPane.NO_OPTION);
			return;
		}
		
		List<CarrierEntity> listOfCarrier = remoteServer.getService().queryCarrierEntity();
		if (listOfCarrier == null) {
			JOptionPane.showMessageDialog(this, "请先设置一台中心载波机","提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (listOfCarrier.size() > 1) {
			JOptionPane.showMessageDialog(this, "中心载波机只能有一台","提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int result1 = JOptionPane.showConfirmDialog(this, "你确定要清空路由吗？", "提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result1) {
			return;
		}
		
		int result2 = PromptDialog.showPromptDialog(this, "请选择清空的方式",imageRegistry);
		if (result2 == 0) {
			return;
		}
		
		showCleanMessageDialog();
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.CARRIERROUTECLEAR);
				message.setObject(carrierEntity);
				message.setIntProperty(Constants.SYN_TYPE,getSelectCleanItem());
				message.setStringProperty(Constants.MESSAGEFROM, clientModel
						.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel
						.getLocalAddress());
				return message;
			}
		});
	}
	
	private void showCleanMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("清空", this);
	}

	private int getSelectCleanItem() {
		int value = 0;
		if (netWorkSideChkBox.isSelected()) {
			value = Constants.SYN_SERVER;
		}
		if (deviceSideChkBox.isSelected()) {
			value = Constants.SYN_DEV;
		}
		if (netWorkSideChkBox.isSelected() && deviceSideChkBox.isSelected()) {
			value = Constants.SYN_ALL;
		}
		return value;
	}


	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, carrierChangeListener);
	}

	public void updateCarrierEntity(CarrierEntity entity) {
		if (null != entity) {
			this.carrierEntity = entity;
		}

		int carrierCode = carrierEntity.getCarrierCode();
		setValue(carrierEntity.getRoutes());
//		// 通过载波机编号向数据库查询此载波机
//		CarrierEntity entities = remoteServer.getService().getCarrierByCode(carrierCode);
//		if (null != entities) {
//			// 把查到的载波机赋值给carrierEntity
//			carrierEntity = entities;
//			setValue(carrierEntity.getRoutes());
//		}
	}

	private final PropertyChangeListener carrierChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() instanceof CarrierTopNodeEntity) {
				CarrierEntity entity = (CarrierEntity) adapterManager
						.getAdapter(equipmentModel.getLastSelected(),CarrierEntity.class);
				updateCarrierEntity(entity);
			} else {
				carrierEntity = null;
				versionField.setText("");
				routeCountField.setText("");
				tableModel.setDataList(null);
				tableModel.fireTableDataChanged();
			}
		}
	};

	// Amend 2010.06.08
	public boolean isValids() {
		boolean isValid = true;

		if (null == portBox.getSelectedItem()
				|| "".equals(portBox.getSelectedItem().toString())) {
			JOptionPane.showMessageDialog(this, "端口号不能为空，请选择端口号", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == plcIdField.getText() || "".equals(plcIdField.getText())) {
			JOptionPane.showMessageDialog(this, "设备ID不能为空，请输入设备ID", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		} else if (NumberUtils.toInt(plcIdField.getText()) < 0) {
			JOptionPane.showMessageDialog(this, "设备ID必须大于1，请输入设备ID", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		return isValid;
	}

	class RouteTableModel extends AbstractTableModel {
		private String[] columnName = null;

		private List<List> dataList = null;

		public RouteTableModel() {
			super();
		}

		public String[] getColumnName() {
			return columnName;
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public List<List> getDataList() {
			return dataList;
		}

		public void setDataList(List<List> dataList) {
			if (null == dataList) {
				this.dataList = new ArrayList<List>();
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
			if (null == columnName) {
				return 0;
			}
			return columnName.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnName[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (null == dataList.get(rowIndex)) {
				return null;
			}
			return dataList.get(rowIndex).get(columnIndex);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			dataList.get(rowIndex).set(columnIndex, aValue);
		}

		public void addRow(List rowList) {
			dataList.add(rowList);
			this.fireTableDataChanged();
		}

		public void deleteRow(int[] rows) {
			for (int i = rows.length - 1; i >= 0; i--) {
				dataList.remove(rows[i]);
			}
			this.fireTableDataChanged();
		}
	}

}