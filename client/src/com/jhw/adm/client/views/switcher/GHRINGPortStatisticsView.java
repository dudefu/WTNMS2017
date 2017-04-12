package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.CLEAN;
import static com.jhw.adm.client.core.ActionConstants.REFRESH;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

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
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.ports.RingCount;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(GHRINGPortStatisticsView.ID)
@Scope(Scopes.DESKTOP)
public class GHRINGPortStatisticsView extends ViewPart{
	public static final String ID = "ghRINGPortStatisticsView";
	
	//上面的面板
	private JPanel topPnl = new JPanel();
	private JPanel topRightPnl = new JPanel();
	private JCheckBox autoRefreshChkbox = new JCheckBox();
	private JButton refreshBtn ;
	private JButton zeroBtn;
	
	//中间的面板
	private JScrollPane centerScrolPnl = new JScrollPane();
	private JTable table = new JTable();
	private PortStatisticsTableModel model = null;
	
	private ButtonFactory buttonFactory;
	
	private final String[] COLUMN_NAME = {"端口","Rx ERSTP","Tx ERSTP","Rx ETCN","Tx ETCN","Rx Ill","Rx Unk"};
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private ActionMap actionMap  = null;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@PostConstruct
	protected void initialize(){
		init();
		
		autoRefresh();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initTopPnl();
		initCenterPnl();
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(centerScrolPnl,BorderLayout.CENTER);
		setResource();
	}
	
	private void initTopPnl(){
		refreshBtn = buttonFactory.createButton(REFRESH);
		zeroBtn = buttonFactory.createButton(CLEAN);
		
		topRightPnl.setLayout(new GridBagLayout());
		
		topRightPnl.add(autoRefreshChkbox,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		topRightPnl.add(refreshBtn,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,0,0),0,0));
		topRightPnl.add(zeroBtn,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,0,0),0,0));
		
		topPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		topPnl.add(topRightPnl);
	}
	
	private void initCenterPnl(){
		centerScrolPnl.getViewport().add(table);
		model = new PortStatisticsTableModel();
		model.setColumnName(COLUMN_NAME);
		table.setModel(model);
	}
	
	private void setResource(){
		autoRefreshChkbox.setText("自动刷新");
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		autoRefreshChkbox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (autoRefreshChkbox.isSelected()){
					autoRefresh();
				}
			}
		});
	}
	
	/**
	 * 判断自动刷新是否启动
	 */
	private void autoRefresh(){
		Thread thread = new Thread(new Runnable(){
			public void run(){
				while(true){
					queryData();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!autoRefreshChkbox.isSelected()){
						return;
					}
				}
			}
		});
		
		thread.start();
	}
	
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		if (null == switchNodeEntity){
			return ;
		}
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		
		List<RingCount> ringCountList = (List<RingCount>)remoteServer.getService().findAll(RingCount.class, where, parms);
		if (null == ringCountList){
			return;
		}
		setDataList(ringCountList);
	}
	
	private void setDataList(List<RingCount> ringCountList){
		List<List> dataList = new ArrayList<List>();
		
		int sum = ringCountList.size();
		for (int i = 0 ; 0 < sum ; i++){
			RingCount ringCount = ringCountList.get(i);
			List rowData = new ArrayList();
			rowData.add(0, ringCount.getPortNo());
			rowData.add(1, ringCount.getRx_ERSTP());
			rowData.add(2, ringCount.getTx_ERSTP());
			rowData.add(3, ringCount.getRx_ETCN());
			rowData.add(4, ringCount.getRx_ETCN());//RingCount中少定义一个属性
//			rowData.add(4, ringCount.getTx_ETCN());
			rowData.add(5, ringCount.getRx_Ill());
			rowData.add(6, ringCount.getRx_Unk());
			rowData.add(7, ringCount);
			dataList.add(rowData);
		}
		model.setDataList(dataList);	
		model.fireTableDataChanged();
	}
	
	/**
	 * 立即刷新按钮
	 */
	@ViewAction(name=REFRESH, icon=NetworkConstants.REFRESH,desc="刷新端口信息",role=Constants.MANAGERCODE)
	public void refresh(){
		queryData();
	}
	
	/**
	 * 清零按钮
	 */
	@ViewAction(name=CLEAN, icon=ButtonConstants.CLEAN,desc="清空端口信息",role=Constants.MANAGERCODE)
	public void clean(){
		List<Serializable> ringCountList = model.getRingCountList();
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.VLANUPDATE, ringCountList,
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		queryData();
	}
	
	
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
	public class PortStatisticsTableModel extends AbstractTableModel{
		private List<List> dataList = new ArrayList<List>();
		
		public List<List> getDataList() {
			return dataList;
		}


		private String[] columnName = null;
		
		
		public String[] getColumnName() {
			return columnName;
		}
		
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
	    
	    public List<Serializable> getRingCountList(){
			List<Serializable> ringCountList = new ArrayList<Serializable>();
			
			for (int i = 0 ; i < dataList.size(); i++){
				List rowData = dataList.get(i);
				RingCount ringCount = (RingCount)rowData.get(rowData.size()-1);
				ringCount.setRx_ERSTP(0);
				ringCount.setRx_ETCN(0);
				ringCount.setRx_Ill(0);
				ringCount.setRx_Unk(0);
				ringCount.setTx_ERSTP(0);
				
				ringCountList.add(ringCount);
			}
			
			return ringCountList;
		}
	}
}
