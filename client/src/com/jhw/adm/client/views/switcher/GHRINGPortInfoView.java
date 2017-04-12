package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.ports.RingPortInfo;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;

@Component(GHRINGPortInfoView.ID)
@Scope(Scopes.DESKTOP)
public class GHRINGPortInfoView extends ViewPart{
	public static final String ID = "ghRINGPortInfoView";
	
	private JScrollPane scrollPnl = new JScrollPane();
	private JTable table = new JTable();
	private PortInfoTableModel model = null;
	private final String[] COLUMN_NAME = {"端口","状态","环ID","环模式","角色","转发状态"};
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@PostConstruct
	protected void initialize(){
		init();
		
		queryData();
	}
	
	private void init(){
		scrollPnl.getViewport().add(table);
		model = new PortInfoTableModel();
		model.setColumnName(COLUMN_NAME);
		table.setModel(model);
		
		this.setLayout(new BorderLayout());
		this.add(scrollPnl);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		
		List<RingPortInfo> ringPortInfoList = (List<RingPortInfo>)remoteServer.getService().findAll(RingPortInfo.class, where, parms);
		if (null == ringPortInfoList ){
			return;
		}
		
		setDataList(ringPortInfoList);
	}
	
	private void setDataList(List<RingPortInfo> ringPortInfoList){
		List<List> dataList = new ArrayList<List>();
		for (int i = 0 ; i < ringPortInfoList.size(); i++){
			List rowData = new ArrayList();
			RingPortInfo ringPortInfo = ringPortInfoList.get(i);
			int port = ringPortInfo.getPortNo();
			String status = "";
			if(ringPortInfo.isStatus()){
				status = "up";
			}
			else{
				status = "down";
			}
			int ringID = ringPortInfo.getRingID();
			String mode = ringPortInfo.getRingMode();
			String role = ringPortInfo.getPortRole();
			String tranfStatus = ringPortInfo.getTransferStatus();
			
			rowData.add(0, port);
			rowData.add(1,status);
			rowData.add(2,ringID);
			rowData.add(3,mode);
			rowData.add(4,role);
			rowData.add(5,tranfStatus);
			
			dataList.add(rowData);
		}
		
		model.setDataList(dataList);	
		model.fireTableDataChanged();
	}
	
//	public JButton getCloseButton(){
//		return this.closeBtn;
//	}
	
	private PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
			else{
				model.setDataList(null);	
				model.fireTableDataChanged();
			}
		}
	};
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
//		messageReceiveProcess.dispose();
	}
	
	//**********************************************************
	public class PortInfoTableModel extends AbstractTableModel{
		private List<List> dataList = new ArrayList<List>();
		public String[] getColumnName() {
			return columnName;
		}
		
		private String[] columnName = null;

		
		public void setDataList(List<List> dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<List>();
			}
			else{
				this.dataList = dataList;
			}
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public int getRowCount(){
			return dataList.size();
		}


	    public int getColumnCount(){
	    	return columnName.length;
	    }

	    
	    public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }

	 
//	    public Class<?> getColumnClass(int columnIndex){
//	    	if (null == getValueAt(0,columnIndex)){
//	    		return null;
//	    	}
//	    	
//	    	return getValueAt(0,columnIndex).getClass();
//	    }


	    public boolean isCellEditable(int rowIndex, int columnIndex){
	    	if (1== columnIndex){
	    		return true;
	    	}
	    	return false;
	    }


	    public Object getValueAt(int rowIndex, int columnIndex){
	    	if (dataList.size() < 1){
	    		return null;
	    	}
	    	Object object  = dataList.get(rowIndex);
	    	if (null == object){
	    		return null;
	    	}
	    	return ((List)dataList.get(rowIndex)).get(columnIndex);
	    }

	  
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (dataList.size() < 1){
	    		return ;
	    	}
	    	Object object  = dataList.get(rowIndex);
	    	if (null == object){
	    		return;
	    	}
	    	((List)dataList.get(rowIndex)).set(columnIndex, aValue);
	    }
	}
}
