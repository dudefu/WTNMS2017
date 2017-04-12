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
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTSlotConfig;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;

@Component(OLTSlotInformateionView.ID)
@Scope(Scopes.DESKTOP)
public class OLTSlotInformateionView extends ViewPart {
	private static final long serialVersionUID = 2322569469578750848L;
	public static final String ID = "OLTSlotInformateionView";

	private final JScrollPane scrollPnl = new JScrollPane();
	private final JTable table = new JTable();
	private SlotInfoTableModel model = null;

	private final JPanel bottomPnl = new JPanel();
	private JButton closeBtn;

	private ButtonFactory buttonFactory;

	private OLTEntity selectedOlt = null;

	private final static String[] COLUMN_NAME = { "��λ ID", "Hello ���ļ��",
			"�������ӳ�ʱ���", "��ע��оƬ��" };

	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;

	/**
	 * �豸�������ʱ����� ��ѡ��ͬ���豸ʱ������switchNodeEntity,���²�ѯ���ݿ�
	 */
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
		model = new SlotInfoTableModel();
		table.setModel(model);
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		scrollPnl.getViewport().add(table, BorderLayout.CENTER);
		scrollPnl.setBorder(BorderFactory.createTitledBorder("��λ�б�"));
	}

	private void initBottomPnl() {
		bottomPnl.setLayout(new FlowLayout(FlowLayout.TRAILING));
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		bottomPnl.add(closeBtn);
	}

	private void setResource() {
		this.setTitle("OLT ��λ��Ϣ");

		// ���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
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

		List<OLTSlotConfig> slotInfoList = (List<OLTSlotConfig>) remoteServer.getService()
				.findAll(OLTSlotConfig.class, where, parms);
		if (null == slotInfoList || slotInfoList.size() < 1) {
			return;
		}
		
		model.setDataList(slotInfoList);
		model.fireTableDataChanged();
	}

	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(
				EquipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}

	class SlotInfoTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = -7625414469572103119L;
		private List<OLTSlotConfig> dataList = null;

		public void setDataList(List<OLTSlotConfig> dataList) {
			if (null == dataList) {
				this.dataList = new ArrayList<OLTSlotConfig>();
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
			OLTSlotConfig slotConfig = dataList.get(rowIndex);
			Object value = null;
			//{ "��λID", "Hello���ļ��", "�������ӳ�ʱ���", "��ע��оƬ��" };
			switch (columnIndex) {
			case 0:
				value = slotConfig.getSlotID();
				break;
			case 1:
				value = slotConfig.getHelloMsgTimeOut();
				break;
			case 2:
				value = slotConfig.getConnectTimerOut();
				break;
			case 3:
				value = slotConfig.getRegisteredNum();
				break;
			default:
				break;
			}

			return value;
		}
	}
}