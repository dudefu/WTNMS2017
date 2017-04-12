package com.jhw.adm.client.views.switchlayer3;

import static com.jhw.adm.client.core.ActionConstants.DELETE;
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.math.NumberUtils;
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
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.level3.Switch3VlanEnity;
import com.jhw.adm.server.entity.level3.Switch3VlanPortEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(VlanAddLayer3View.ID)
@Scope(Scopes.DESKTOP)
public class VlanAddLayer3View extends ViewPart{

	private static final long serialVersionUID = 1L;
	public static final String ID = "vlanAddLayer3View";
	
	private JPanel centerPnl = new JPanel();
	private JPanel centerTopPnl = new JPanel();
	
	private JPanel optionPnl = new JPanel();
	private JLabel vlanIdLbl = new JLabel();
	private NumberField vlanIdFld = new NumberField(4,0,1,4094,true);
	private JLabel vlanNameLbl = new JLabel();
	private JTextField vlanNameFld = new JTextField();
	private JLabel vlanNameRangeLbl = new JLabel();
	private JPanel buttonPnl = new JPanel();
	private JButton saveBtn;
	private JButton deleteBtn ;
	
	private JScrollPane scrollPnl = new JScrollPane();
	private JTable vlanTable = new JTable();
	
	private VlanTableModel vlanModel = null;
	
	private JPanel bottomPnl = new JPanel();
	private JButton closeBtn ;
	
	private final static String[] VLAN_COLUMNNAME = new String[]{"VLAN ID","VLAN名称"};
	
	private SwitchLayer3 switchLayer3 = null;

	private ButtonFactory buttonFactory;
	
	private static final Logger LOG = LoggerFactory.getLogger(VlanAddLayer3View.class);
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	private MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	
	@PostConstruct
	protected void initialize(){
		init();
		queryData();
	}

	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initCenterPnl(){
		initCenterTopPnl();
		initScrollPnl();
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(centerTopPnl,BorderLayout.NORTH);
		centerPnl.add(scrollPnl,BorderLayout.CENTER);
	}
	
	private void initCenterTopPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(vlanIdLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,20,0),0,0));
		panel.add(vlanIdFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,20,20,0),0,0));
		panel.add(new StarLabel("(1-4094)"),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,3,20,0),0,0));

		panel.add(vlanNameLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
		panel.add(vlanNameFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,0,0),0,0));
		panel.add(vlanNameRangeLbl,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,3,0,0),0,0));
		optionPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		optionPnl.add(panel);
		
		saveBtn = buttonFactory.createButton(SAVE);
		deleteBtn = buttonFactory.createButton(DELETE);
		buttonPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPnl.add(saveBtn);
		buttonPnl.add(deleteBtn);
		
		centerTopPnl.setLayout(new BorderLayout());
		centerTopPnl.add(optionPnl,BorderLayout.CENTER);
		centerTopPnl.add(buttonPnl,BorderLayout.SOUTH);	
	}
	
	private void initScrollPnl(){
		vlanModel = new VlanTableModel();
		vlanModel.setColumnName(VLAN_COLUMNNAME);
		vlanTable.setModel(vlanModel);
		scrollPnl.getViewport().add(vlanTable);
		vlanTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}
	
	private void setResource(){
		vlanIdLbl.setText("VLAN ID");
		vlanNameLbl.setText("VLAN名称");
		
		vlanNameFld.setColumns(20);
		
		vlanNameFld.setDocument(new TextFieldPlainDocument(vlanNameFld, 36));
		
		vlanNameRangeLbl.setText("(1-36个字符)");
		vlanNameRangeLbl.setForeground(Color.RED);
		
		vlanTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				vlanTableAction();
			}
		});
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	/**
	 * vlan表格的选择事件监听
	 */
	@SuppressWarnings("unchecked")
	public void vlanTableAction(){
		int selectRow = vlanTable.getSelectedRow();
		if (selectRow < 0){
			return;
		}
		
		Switch3VlanEnity vlanEnity = (Switch3VlanEnity)vlanModel
					.getValueAt(selectRow, vlanModel.getColumnCount()+1);
		int vlanID = vlanEnity.getVlanID();
		String vlanName = vlanEnity.getVlanName();
		vlanIdFld.setText(vlanID + "");
		vlanNameFld.setText(vlanName);
		//当为缺省VLAN时
		if(vlanID == 1){
			vlanIdFld.setEnabled(false);
			vlanNameFld.setEnabled(false);
			saveBtn.setEnabled(false);
			deleteBtn.setEnabled(false) ;
		}
		else{
			vlanIdFld.setEnabled(true);
			vlanNameFld.setEnabled(true);
			saveBtn.setEnabled(true);
			deleteBtn.setEnabled(true) ;
		}
	}
	
	public void queryData(){
		if (null == switchLayer3){
			vlanIdFld.setText("");
			vlanNameFld.setText("");
			
			vlanModel.setDataList(null);
			vlanModel.fireTableDataChanged();
			return ;
		}
		
		String where = " where entity.ipValue=?";
		Object[] parms = {switchLayer3.getIpValue()};
		List<Switch3VlanEnity> vlanEnityList = (List<Switch3VlanEnity>)remoteServer
				.getService().findAll(Switch3VlanEnity.class, where, parms);
		
		setValue(vlanEnityList);
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(List<Switch3VlanEnity> vlanEnityList){
		List<List> dataList = new ArrayList<List>();
		if (null != vlanEnityList){
			for (Switch3VlanEnity vlanEnity : vlanEnityList){
				List rowList = new ArrayList();
				int vlanId = vlanEnity.getVlanID();
				String vlanName = vlanEnity.getVlanName();
				
				String vlanPorts = "";
				Set<Switch3VlanPortEntity> vlanPortSet = vlanEnity.getVlanPortEntities();

				if (vlanPortSet != null){
					int[] nums = new int[vlanPortSet.size()];
					Iterator iterator = vlanPortSet.iterator();
					
					int temp = 0;//临时变量，用于冒泡交换        
					int count = 0;
					while(iterator.hasNext()){
						Switch3VlanPortEntity switch3VlanPortEntity = (Switch3VlanPortEntity)iterator.next();
						int portID = switch3VlanPortEntity.getPortID();
						nums[count] = portID;
						count++;
					}
					
					for (int i = 0; i < nums.length-1; i++) {
						for (int j = 0; j < nums.length - i - 1; j++) {
							if (nums[j] > nums[j + 1]) {
								temp = nums[j];
								nums[j] = nums[j + 1];
								nums[j + 1] = temp;

						    }
						}
					}
					
					for(int k = 0 ; k < nums.length; k++){
						vlanPorts = vlanPorts + nums[k] + ",";
					}
				}
				if (!"".equals(vlanPorts)){
					vlanPorts = vlanPorts.substring(0,vlanPorts.length()-1);
				}
				
				rowList.add(vlanId+"");
				rowList.add(vlanName);
				rowList.add(vlanPorts);
				rowList.add(vlanEnity);
				
				dataList.add(rowList);
			}
		}
		
		vlanModel.setDataList(dataList);
		vlanModel.fireTableDataChanged();
		vlanTable.updateUI();
	}
	
	/**
	 * 判断是增加还是修改Vlan
	 * @return
	 */
	public boolean isAddOperate(){
		boolean isAdd = true;
		List<List> dataList = vlanModel.getDataList();
		if (null != dataList){
			for (int i = 0; i < dataList.size(); i++){
				List rowList = dataList.get(i);
				String vlanStr = String.valueOf(rowList.get(0));
				if (NumberUtils.toInt(vlanStr) == NumberUtils.toInt(vlanIdFld.getText().trim())){
					isAdd = false; //修改
					break;
				}
			}
		}
		return isAdd;
	}
	
	/**
	 * 保存操作
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存三层交换机VLAN ID",role=Constants.MANAGERCODE)
	public void save(){
		if (null == switchLayer3){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		if(null == vlanIdFld.getText().trim() || "".equals(vlanIdFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "VLAN ID错误，范围为：1-4094","提示",JOptionPane.NO_OPTION);
			return ;
		}
		else if ((NumberUtils.toInt(vlanIdFld.getText().trim()) < 1) || (NumberUtils
						.toInt(vlanIdFld.getText().trim()) > 4094)){
			JOptionPane.showMessageDialog(this, "VLAN ID错误，范围为：1-4094","提示",JOptionPane.NO_OPTION);
			return ;
		}
		else if ((NumberUtils.toInt(vlanIdFld.getText().trim()) == 1)){
			JOptionPane.showMessageDialog(this, "VLAN ID 1为默认ID，请重新输入","提示",JOptionPane.NO_OPTION);
			return ;
		}
		
		if(vlanNameFld.getText().trim().length() < 1 || vlanNameFld.getText().trim().length() > 36){
			JOptionPane.showMessageDialog(this, "VLAN名称错误，范围为：1-36个字符","提示",JOptionPane.NO_OPTION);
			return ;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		String ipValue = switchLayer3.getIpValue();
		int vlanID = NumberUtils.toInt(vlanIdFld.getText().trim());
		String vlanName = vlanNameFld.getText().trim();
		
		boolean isAdd = isAddOperate();
		if (isAdd){//增加Vlan
			Switch3VlanEnity vlanEntity = new Switch3VlanEnity();
			vlanEntity.setVlanID(vlanID);
			vlanEntity.setVlanName(vlanName);
			vlanEntity.setIpValue(ipValue);
			
			List<Serializable> vlanEntityList = new ArrayList<Serializable>();
			vlanEntityList.add(vlanEntity);
			
			showMessageDialog();
			try {
				remoteServer.getAdmService().saveAndSettingByIP(ipValue, MessageNoConstants.SWITCH3VLANADD, vlanEntityList, 
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER3,result);
			} catch (JMSException e) {
				messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
				LOG.error("VlanAddLayer3View's save() is failure:{}", e);
			}
		}
		else{//修改Vlan
			Switch3VlanEnity vlanEntity = null;
			
			List<List> dataList = vlanModel.getDataList();
			if (null != dataList){
				for (int i = 0; i < dataList.size(); i++){
					List rowList = dataList.get(i);
					String vlanStr = String.valueOf(rowList.get(0));
					if (NumberUtils.toInt(vlanStr) == NumberUtils.toInt(vlanIdFld.getText().trim())){
						vlanEntity = (Switch3VlanEnity)rowList.get(3);
						break;
					}
				}
			}
			
			vlanEntity.setVlanName(vlanName);
			List<Serializable> vlanEntityList = new ArrayList<Serializable>();
			vlanEntityList.add(vlanEntity);
			
			showMessageDialog();
			try {
				remoteServer.getAdmService().updateAndSetSwitch3Vlan(ipValue, MessageNoConstants.SWITCH3VLANUPDATE, vlanEntityList, 
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER3,result);
			} catch (JMSException e) {
				messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
				LOG.error("VlanAddLayer3View's save() is failure:{}", e);
			}
		}
		
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				queryData();
			}
		});
	}
	
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("保存",this);
	}
	
	/**
	 * 删除操作
	 */
	@SuppressWarnings("unchecked")
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="删除三层交换机VLAN ID",role=Constants.MANAGERCODE)
	public void delete(){
		if (vlanTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要删除的VLAN ID","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		String vlanID = (String)vlanTable.getValueAt(vlanTable.getSelectedRow(), 0);
		if ("1".equals(vlanID)){
			JOptionPane.showMessageDialog(this, "VLAN 1 不能被删除，请重新选择","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		List<List> dataList = vlanModel.getDataList();

		Switch3VlanEnity vlanEntity = (Switch3VlanEnity)dataList.get(vlanTable.getSelectedRow()).get(3);
		if((vlanEntity.getVlanPortEntities() != null) 
				&& (vlanEntity.getVlanPortEntities().size()>0)){
			JOptionPane.showMessageDialog(this, "请先删除此VLAN下绑定的端口","提示",JOptionPane.NO_OPTION);
			return;
		}
		List<Serializable> vlanEntityList = new ArrayList<Serializable>();
		vlanEntityList.add(vlanEntity);
		
		String ipValue = switchLayer3.getIpValue();
		showDeleteMessageDialog();
		try {
			remoteServer.getAdmService().delteAndSetSwitch3Vlan(ipValue, MessageNoConstants.SWITCH3VLANDEL, vlanEntityList,
					clientModel.getCurrentUser().getUserName(), clientModel.getLocalAddress(),Constants.DEV_SWITCHER3,Constants.SYN_ALL);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("删除出现异常");
			LOG.error("VlanAddLayer3View delete() error:{}", e);
		}
		queryData();
	}
	
	public void showDeleteMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("删除",this);
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeLevel3){
				switchLayer3 = (SwitchLayer3) adapterManager
					.getAdapter(equipmentModel.getLastSelected(), SwitchLayer3.class);
			}
			else{
				switchLayer3 = null; 
			}
			queryData();
		}
	};
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	class VlanTableModel extends AbstractTableModel{
		private List<List> dataList = new ArrayList<List>();
		private String[] columnName = null;
		
		@PostConstruct
		protected void initialize() {
			
		}
		
		public void setDataList(List dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<List>();
			}
			else{
				this.dataList= dataList;
			}
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public int getRowCount(){
			if (null == dataList){
				return 0;
			}
			return dataList.size();
		}

	    public int getColumnCount(){
	    	return columnName.length;
	    }

	    public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }

	    public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	    public Object getValueAt(int rowIndex, int columnIndex){
	    	if (null == dataList || dataList.size() <1){
	    		return null;
	    	}
	    	List rowList = null;
	    	try{
	    		rowList = dataList.get(rowIndex);
	    	}
	    	catch(Exception ee){
	    	}
	    	
	    	if (null == rowList){
	    		return null;
	    	}
	    	Object object = rowList.get(columnIndex);
	    	
	    	return object;
	    }

	    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (null == dataList || dataList.size() <1){
	    		return ;
	    	}
	    	List rowList = null;

	    	rowList = dataList.get(rowIndex);
	    	
	    	if (null == rowList){
	    		return;
	    	}

	    	rowList.set(columnIndex, aValue);
	    }

	    
	    public List<List> getDataList(){
	    	return dataList;
	    }
	    
	    public void insertRow(List rowData){
	    	dataList.add(rowData);
	    	this.fireTableDataChanged();
	    }
	    
	    public void updateRow(int index,List rowData){
	    	dataList.set(index, rowData);
	    	this.fireTableDataChanged();
	    }
	    
	    public void deleteRow(List rowData){
	    	dataList.remove(rowData);
	    	this.fireTableDataChanged();
	    }
	}
}
