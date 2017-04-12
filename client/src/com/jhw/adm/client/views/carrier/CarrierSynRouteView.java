package com.jhw.adm.client.views.carrier;

import static com.jhw.adm.client.core.ActionConstants.SYNCHRONIZE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
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
import com.jhw.adm.client.swing.CommonTable;
import com.jhw.adm.client.swing.MessageOfCarrierConfigProcessorStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.TopoFoundFEPs;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(CarrierSynRouteView.ID)
@Scope(Scopes.DESKTOP)
public class CarrierSynRouteView extends ViewPart implements PropertyChangeListener{
	private JButton synBtn ;
	private final JProgressBar progressBar = new JProgressBar();

	private final JPanel centerPnl = new JPanel();
	private final JScrollPane scrollPnl = new JScrollPane();
	private final CommonTable table = new CommonTable();
	private FepTableModel fepTableModel = new FepTableModel();
	private final JCheckBox chkBox = new JCheckBox("全选");
	private static final String[] COLUMN_NAME = { "前置机编号", "名称" };

	private final JPanel bottomPnl = new JPanel();
	private JButton closeBtn = null;

	private static final long serialVersionUID = 1L;
	public static final String ID = "carrierSynRouteView";

	private List<FEPTopoNodeEntity> fepEntityList = null;
	
	private ButtonFactory buttonFactory;

	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;

//	@Resource(name=FEPTableModel.ID)
//	private FEPTableModel fepTableModel;

	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	private MessageSender messageSender;
	private MessageOfCarrierConfigProcessorStrategy messageOfCarrierProcessorStrategy = new MessageOfCarrierConfigProcessorStrategy();	

	@PostConstruct
	protected void initialize() {

		this.setTitle("载波机同步");
		messageSender = remoteServer.getMessageSender();
		buttonFactory = actionManager.getButtonFactory(this); 
		init();
		queryData();
	}
	
	private void init() {
		setLayout(new BorderLayout(0, 0));
		
		initCenterPnl();
		initBottomPnl();
		
		add(centerPnl, BorderLayout.CENTER);
		add(bottomPnl, BorderLayout.SOUTH);

		this.setViewSize(450, 320);
	}

	private void initCenterPnl() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel.add(chkBox);

		scrollPnl.getViewport().add(table);

		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(panel, BorderLayout.SOUTH);
		centerPnl.add(scrollPnl, BorderLayout.CENTER);
		centerPnl.setBorder(BorderFactory.createTitledBorder(""));

		chkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					table.setRowSelectionAllowed(true);
					table.setRowSelectionInterval(0, table.getRowCount() - 1);
				}else if(e.getStateChange() == ItemEvent.DESELECTED){
					table.clearSelection();
				}
				table.revalidate();
			}
		});
	}

	private void initBottomPnl() {
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		synBtn = buttonFactory.createButton(SYNCHRONIZE);
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(synBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
		setButtonEanble(true);
	}

	private void setButtonEanble(boolean enable) {
		synBtn.setEnabled(enable);
	}

	@SuppressWarnings("unchecked")
	private void queryData() {
		List<FEPEntity> listOfFep = (List<FEPEntity>) remoteServer.getService().findAll(
				FEPEntity.class);
		fepTableModel.setData(listOfFep);
		table.setModel(fepTableModel);
	}

	@ViewAction(name = SYNCHRONIZE, icon = ButtonConstants.START,desc="同步载波机",role=Constants.MANAGERCODE)
	public void synchronize() {
		if (table.getSelectedRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "请选择前置机","提示",JOptionPane.NO_OPTION);
			return;
		}

		String message = "如果设备侧和网管侧数据不一致，网管侧数据会删除。你确定吗？";
		int result = JOptionPane.showConfirmDialog(this.getRootFrame(), message, "警告", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (0 != result){
			return;
		}
		
		final TopoFoundFEPs topoFoundFEPs = new TopoFoundFEPs();
		List<String> listOfFepCode = new ArrayList<String>();
		for (int row : table.getSelectedRows()) {
			FEPEntity fep = fepTableModel.getValue(row);
			listOfFepCode.add(fep.getCode());
		}
		topoFoundFEPs.setFepCodes(listOfFepCode);

		showMessageDialog();
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.CARRIERROUTECONFIGS);
				Long diagramId = equipmentModel.getDiagram().getId();
				topoFoundFEPs.setRefreshDiagramId(diagramId);
				message.setObject(topoFoundFEPs);
				
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				return message;
			}
		});
	}
	
	private void showMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("同步", this);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (StringUtils.isNumeric(String.valueOf(evt.getNewValue()))){
			int progress = (Integer) evt.getNewValue();
	        progressBar.setIndeterminate(false);
	        progressBar.setValue(progress);
		}
	}

	private class FepTableModel extends AbstractTableModel {

		public FepTableModel() {
			listOfFep = new ArrayList<FEPEntity>();

			int modelIndex = 0;
			fepColumnModel = new DefaultTableColumnModel();
			TableColumnExt codeColumn = new TableColumnExt(modelIndex++,
					120);
			codeColumn.setIdentifier("code");
			codeColumn.setHeaderValue("编号");
			fepColumnModel.addColumn(codeColumn);
			
			TableColumnExt nameColumn = new TableColumnExt(modelIndex++,
					120);
			nameColumn.setIdentifier("name");
			nameColumn.setHeaderValue("名称");
			fepColumnModel.addColumn(nameColumn);
			
			TableColumnExt addressColumn = new TableColumnExt(modelIndex++,
					120);
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
			return fepColumnModel.getColumn(col).getHeaderValue()
					.toString();
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
					value = fep.getFepName();
					break;
				case 2:
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
			if (feps == null) return;
			this.listOfFep = feps;
		}

		private List<FEPEntity> listOfFep;
		private final TableColumnModel fepColumnModel;
		private static final long serialVersionUID = -3065768803494840568L;
	}
}
