package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.epon.ONULLID;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(ONULLIDCofnigView.ID)
@Scope(Scopes.DESKTOP)
public class ONULLIDCofnigView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "ONULLIDCofnigView";

	private final JPanel topPnl = new JPanel();

	private final JLabel nameLbl = new JLabel();
	private final JTextField nameFld = new JTextField();

	private final JLabel peakBandWidthLbl = new JLabel();
	private final NumberField peakBandWidthFld = new NumberField();

	private final JLabel safeBandWidthLbl = new JLabel();
	private final NumberField safeBandWidthFld = new NumberField();

	private final JLabel fixedBandWidthLbl = new JLabel();
	private final NumberField fixedBandWidthFld = new NumberField();

	private final JScrollPane centerPnl = new JScrollPane();
	private final JTable table = new JTable();
	private LLIDTableModel model;

	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn;
	private JButton closeBtn;

	private ButtonFactory buttonFactory;
	private ONUEntity onuNodeEntity = null;
	private List<ONULLID> onuLLIDList;

	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private static final Logger LOG = LoggerFactory
			.getLogger(ONULLIDCofnigView.class);

	private final static String[] COLUMN_NAME = { "Name", "峰值带宽", "保证带宽",
			"固定带宽" };

	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;

	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;

	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}

	private void init() {
		buttonFactory = actionManager.getButtonFactory(this);
		initTopPnl();
		initCenterPnl();
		initBottomPnl();

		this.setTitle("ONU LLID 配置");
		this.setViewSize(600, 480);
		this.setLayout(new BorderLayout());
		this.add(topPnl, BorderLayout.NORTH);
		this.add(centerPnl, BorderLayout.CENTER);
		this.add(bottomPnl, BorderLayout.SOUTH);

		setResource();
	}

	private void initTopPnl() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(nameLbl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 0, 0), 0, 0));
		panel.add(nameFld, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 30, 0, 0), 0, 0));

		panel.add(peakBandWidthLbl, new GridBagConstraints(0, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 0, 0), 0, 0));
		panel.add(peakBandWidthFld, new GridBagConstraints(1, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 30, 0, 0), 0, 0));

		panel.add(safeBandWidthLbl, new GridBagConstraints(0, 2, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 0, 0), 0, 0));
		panel.add(safeBandWidthFld, new GridBagConstraints(1, 2, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 30, 0, 0), 0, 0));

		panel.add(fixedBandWidthLbl, new GridBagConstraints(0, 3, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 0, 0), 0, 0));
		panel.add(fixedBandWidthFld, new GridBagConstraints(1, 3, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 30, 0, 0), 0, 0));

		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(panel);
	}

	private void initCenterPnl() {

		model = new LLIDTableModel();
		model.setColumnName(COLUMN_NAME);
		table.setModel(model);
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		centerPnl.getViewport().add(table);
	}

	private void initBottomPnl() {
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);

		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
	}

	@SuppressWarnings("static-access")
	private void setResource() {
		nameLbl.setText("Name");
		peakBandWidthLbl.setText("峰值带宽");
		safeBandWidthLbl.setText("保证带宽");
		fixedBandWidthLbl.setText("固定带宽");

		nameFld.setEditable(false);
		fixedBandWidthFld.setEditable(false);
		nameFld.setBackground(Color.WHITE);
		fixedBandWidthFld.setBackground(Color.WHITE);

		nameFld.setColumns(25);

		// 增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(
				equipmentModel.PROP_LAST_SELECTED, onuNodeChangeListener);

		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						setSelectTableAction();
					}
				});
	}

	private void setSelectTableAction() {
		if (table.getSelectedRowCount() < 1) {
			clear();
			return;
		}
		int selectRow = table.getSelectedRow();
		ONULLID oltLLID = (ONULLID) model.getValueAt(selectRow, model
				.getColumnCount());
		String name = oltLLID.getPortName();
		int peakBW = oltLLID.getPeakBW();
		int assuredBW = oltLLID.getAssuredBW();
		int staticBW = oltLLID.getStaticBW();

		nameFld.setText(name);
		peakBandWidthFld.setText(Integer.toString(peakBW));
		safeBandWidthFld.setText(Integer.toString(assuredBW));
		fixedBandWidthFld.setText(Integer.toString(staticBW));
	}

	@SuppressWarnings("unchecked")
	private void queryData() {
		onuNodeEntity = (ONUEntity) adapterManager.getAdapter(equipmentModel
				.getLastSelected(), ONUEntity.class);

		if (null == onuNodeEntity) {
			return;
		}

		String where = " where entity.onuEntity=?";
		Object[] parms = { onuNodeEntity };
		onuLLIDList = (List<ONULLID>) remoteServer.getService().findAll(
				ONULLID.class, where, parms);

		if (null == onuLLIDList || onuLLIDList.size() < 0) {
			return;
		}
		setValue();
	}

	@SuppressWarnings("unchecked")
	private void setValue() {
		List<List> dataList = new ArrayList<List>();
		for (ONULLID oltLLID : onuLLIDList) {
			List rowList = new ArrayList();
			rowList.add(oltLLID.getPortName());
			rowList.add(oltLLID.getPeakBW());
			rowList.add(oltLLID.getAssuredBW());
			rowList.add(oltLLID.getStaticBW());
			rowList.add(oltLLID);

			dataList.add(rowList);
		}
		model.setDataList(dataList);
		model.fireTableDataChanged();
	}

	/**
	 * 保存操作
	 */
	@ViewAction(name = SAVE, icon = ButtonConstants.SAVE, desc = "保存LLID带宽配置", role = Constants.MANAGERCODE)
	public void save() {
		if (!isValids()) {
			return;
		}
		if (table.getSelectedRowCount() < 1) {
			return;
		}
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",
				imageRegistry);
		if (result == 0) {
			return;
		}

		List<Serializable> onuLLIDList = new ArrayList<Serializable>();
		int selectRow = table.getSelectedRow();
		ONULLID oltLLID = (ONULLID) model.getValueAt(selectRow, table
				.getColumnCount());

		oltLLID.setPortName(nameFld.getText());
		oltLLID.setPeakBW(Integer.parseInt(peakBandWidthFld.getText().trim()));
		oltLLID.setAssuredBW(Integer
				.parseInt(safeBandWidthFld.getText().trim()));
		oltLLID.setStaticBW(Integer
				.parseInt(fixedBandWidthFld.getText().trim()));
		onuLLIDList.add(oltLLID);

		String ipValue = oltLLID.getOltIp();
		showMessageDialog();

		try {
			remoteServer.getAdmService().updateAndSettingByIp(ipValue,
					MessageNoConstants.LLIDCONFIG, onuLLIDList,
					clientModel.getCurrentUser().getUserName(),
					clientModel.getLocalAddress(), Constants.DEV_OLT, result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("ONULLIDCofnigView.save() is failure:{}", e);
		}

		if (result == Constants.SYN_SERVER) {
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
	}

	private void showMessageDialog() {
		messageOfSaveProcessorStrategy.showInitializeDialog("保存", this);
	}

	private void clear() {
		nameFld.setText(StringUtils.EMPTY);
		peakBandWidthFld.setText(StringUtils.EMPTY);
		safeBandWidthFld.setText(StringUtils.EMPTY);
		fixedBandWidthFld.setText(StringUtils.EMPTY);
		model.setDataList(null);
		model.fireTableDataChanged();
	}

	public JButton getCloseButton() {
		return this.closeBtn;
	}

	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(
				EquipmentModel.PROP_LAST_SELECTED, onuNodeChangeListener);
	}

	/**
	 * 设备浏览器的时间监听 当选择不同的设备时，根据switchNodeEntity,重新查询数据库
	 */
	private final PropertyChangeListener onuNodeChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			clear();
			if (evt.getNewValue() instanceof ONUTopoNodeEntity) {
				queryData();
			}
		}
	};

	public boolean isValids() {
		boolean isValid = true;

		if (null == this.onuNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择EPON设备", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		if (null == peakBandWidthFld.getText().trim()
				|| "".equals(peakBandWidthFld.getText().trim())) {
			JOptionPane.showMessageDialog(this, "峰值带宽错误", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == safeBandWidthFld.getText().trim()
				|| "".equals(safeBandWidthFld.getText().trim())) {
			JOptionPane.showMessageDialog(this, "保证带宽错误", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		return isValid;
	}

	@SuppressWarnings("unchecked")
	private class LLIDTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private String[] columnName = null;
		private List<List> dataList = null;


		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
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
			return dataList.get(rowIndex).get(columnIndex);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			dataList.get(rowIndex).set(columnIndex, aValue);
		}
	}
}
