package com.jhw.adm.client.views.epon;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.OLTChipInfo;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;

@Component(OLTChipInformationView.ID)
@Scope(Scopes.DESKTOP)
public class OLTChipInformationView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "OLTChipInformationView";

	private final JScrollPane scrollPnl = new JScrollPane();
	private final JTable table = new JTable();
	private ChipInfoTableModel model = null;

	private final JPanel bottomPnl = new JPanel();
	private JButton closeBtn;

	private OLTEntity selectedOlt = null;
	private ButtonFactory buttonFactory;

	private final static String[] COLUMN_NAME = { "芯片索引", "槽位 ID", "模块 ID",
			"设备 ID", "MAC 地址", "芯片状态" };

	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name = DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;

	private final PropertyChangeListener eponNodeChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() instanceof EponTopoEntity) {
				queryData();
			} else {
				model.setDataList(null);
				model.fireTableDataChanged();
			}
		}
	};

	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}

	private void init() {
		buttonFactory = actionManager.getButtonFactory(this);
		initCenterPnl();
		initBottomPnl();

		setResource();

		this.setLayout(new BorderLayout());
		this.add(scrollPnl, BorderLayout.CENTER);
		this.add(bottomPnl, BorderLayout.SOUTH);
		this.setViewSize(640, 560);
	}

	private void initCenterPnl() {
		model = new ChipInfoTableModel();
		table.setModel(model);
		for (int i = 0; i < COLUMN_NAME.length; i++) {
			if (COLUMN_NAME[i].equals("MAC地址")) {
				TableColumn colum = table.getColumn(COLUMN_NAME[i]);
				colum.setPreferredWidth(100);
			}
		}
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		scrollPnl.getViewport().add(table, BorderLayout.CENTER);
		scrollPnl.setBorder(BorderFactory.createTitledBorder("芯片列表"));
	}

	private void initBottomPnl() {
		bottomPnl.setLayout(new FlowLayout(FlowLayout.TRAILING));
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		bottomPnl.add(closeBtn);
	}

	private void setResource() {
		this.setTitle("OLT 芯片信息");
		equipmentModel.addPropertyChangeListener(
				EquipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}

	@SuppressWarnings("unchecked")
	private void queryData() {
		selectedOlt = (OLTEntity) adapterManager.getAdapter(equipmentModel
				.getLastSelected(), OLTEntity.class);
//		if (null == selectedOlt) {
//			return;
//		}

		String where = " where entity.oltEntity=?";
		Object[] parms = { selectedOlt };

		List<OLTChipInfo> chipInfoList = (List<OLTChipInfo>) remoteServer.getService()
				.findAll(OLTChipInfo.class, where, parms);
		if (null == chipInfoList || chipInfoList.size() < 1) {
			return;
		}
		
		model.setDataList(chipInfoList);
		model.fireTableDataChanged();
	}

	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(
				EquipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}

	class ChipInfoTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1179687296669002585L;
		private List<OLTChipInfo> dataList = null;

		public void setDataList(List<OLTChipInfo> dataList) {
			if (null == dataList) {
				this.dataList = new ArrayList<OLTChipInfo>();
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
			OLTChipInfo chipInfo = dataList.get(rowIndex);
			Object value = null;
			//{ "芯片索引", "槽位ID", "模块ID", "设备ID", "MAC地址", "芯片状态" };
			switch (columnIndex) {
			case 0:
				value = chipInfo.getChipIndex();
				break;
			case 1:
				value = chipInfo.getSlotID();
				break;
			case 2:
				value = chipInfo.getModuleID();
				break;
			case 3:
				value = chipInfo.getDeviceID();
				break;
			case 4:
				value = chipInfo.getMac();
				break;
			case 5:
				value = getStatusText(chipInfo.getChipStatus());
				break;
			default:
				break;
			}

			return value;
		}
		
		public String getStatusText(int status) {
			String text = "Unknow";
			switch (status) {
				case 1: text = "wait_config(1)"; break;
				case 2: text = "operational(2)"; break;
				case 3: text = "shut_down(3)"; break;
				case 4: text = "timed_out(4)"; break;
				case 5: text = "downloading_image(5)"; break;
				default: break;
			}
			
			return text;
		}
	}

}
