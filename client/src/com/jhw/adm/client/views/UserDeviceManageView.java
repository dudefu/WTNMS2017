package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(UserDeviceManageView.ID)
@Scope(Scopes.DESKTOP)
public class UserDeviceManageView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "userDeviceManageView";
	private static final Logger LOG = LoggerFactory.getLogger(UserDeviceManageView.ID);
	
	private ButtonFactory buttonFactory;
	
	//中间设备面板
	private JPanel devicePnl = new JPanel();
	private final JTable deviceTable = new JTable();
	private DeviceTableModel deviceModel = null;
	//底部面板
	private final JPanel bottomPnl = new JPanel();
	private JButton closeBtn = null;
	
	private UserManagementView userView = null;
	
	
	private static final String[] DEVICE_COLUMN_NAME = {"设备标识","设备名称"};
	private final static String DELDEVICE = "delDevice";
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;	
	
	@PostConstruct
	public void initialize() {
		init();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		
		initDevicePnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(devicePnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
	}
	
	private void initDevicePnl(){
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton appendDeviceBtn = buttonFactory.createButton(APPEND);		
		JButton delDeviceBtn = buttonFactory.createButton(DELDEVICE);
		
		topPnl.add(appendDeviceBtn);
		topPnl.add(delDeviceBtn);
		
		//表格
		deviceModel = new DeviceTableModel();
		deviceModel.setColumnName(DEVICE_COLUMN_NAME);
		deviceTable.setModel(deviceModel);
		deviceModel.setDataList(null);
		JScrollPane switchScrollPnl = new JScrollPane();
		switchScrollPnl.getViewport().add(deviceTable);

		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.add(topPnl,BorderLayout.NORTH);
		
		JPanel userPnl = new JPanel(new BorderLayout());
		userPnl.add(panel1,BorderLayout.CENTER);
		
		JPanel middleCenterPnl = new JPanel(new BorderLayout());
		middleCenterPnl.add(userPnl,BorderLayout.NORTH);
		middleCenterPnl.add(switchScrollPnl,BorderLayout.CENTER);
		
		devicePnl = new JPanel(new BorderLayout());
		devicePnl.add(middleCenterPnl,BorderLayout.CENTER);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}
	
	public void setUserView(UserManagementView userView){
		this.userView = userView;
	}
	
	@SuppressWarnings("unchecked")
	private void showDeviceDialog(){
		List<NodeEntity> deviceList = new ArrayList<NodeEntity>();
		
		List<List> dataList = deviceModel.getDataList();
		if (dataList != null && dataList.size() > 0){
			for (List rowList : dataList){
				NodeEntity nodeEntity = (NodeEntity)rowList.get(2);//得到已有的设备信息
				deviceList.add(nodeEntity);
			}
		}
		
		SelectedDeviceDialog dialog = new SelectedDeviceDialog(deviceList,this,"设备列表");
		dialog.setVisible(true);
	}
	
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND, text=APPEND, desc="打开设备列表",role=Constants.ADMINCODE)
	public void addDevice() {
		showDeviceDialog();
	}
	
	@ViewAction(name=DELDEVICE, icon=ButtonConstants.DELETE, text=DELETE, desc="删除管理设备",role=Constants.ADMINCODE)
	public void delDevice() {
		if(deviceTable.getSelectedRowCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择要删除的设备","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		int[] rows = deviceTable.getSelectedRows();

		//把所选择的nodeEntity放入到nodeEntities中
		List<NodeEntity> nodeEntities = new ArrayList<NodeEntity>();
		for (int i = 0 ; i < rows.length; i++){
			List rowList = deviceModel.getSelectedRow(rows[i]);
			NodeEntity nodeEntity = (NodeEntity) rowList.get(2);
			nodeEntities.add(nodeEntity);
		}
		
		//遍历nodeEntities，把树节点得到的userEntity从nodeEntity中删除掉
		UserEntity selectedUserEntity = userView.getSelectedUserEntity();
		for(NodeEntity nodeEntity : nodeEntities){
			long selectID = selectedUserEntity.getId();
			
			Set<UserEntity> set = nodeEntity.getUsers();
			Object[] objects = set.toArray();
			for (int i = objects.length -1 ; i >= 0; i--){
				UserEntity entity = (UserEntity)objects[i];
				long id = entity.getId();
				if (selectID == id){
					set.remove(entity);
				}
			}
			nodeEntity.setUsers(set);
		}
		Task deleteTask = new DeleteTask(nodeEntities, selectedUserEntity);
		showMessageDialog(deleteTask, "删除");
	}
	
	private class DeleteTask implements Task{
		
		private final List<NodeEntity> nodeList;
		private final UserEntity selectUserEntity;
		public DeleteTask(List<NodeEntity> nodeList, UserEntity selectUserEntity){
			this.nodeList = nodeList;
			this.selectUserEntity = selectUserEntity;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().updateEntities(this.nodeList);
			}catch(Exception e){
				strategy.showErrorMessage("删除异常");
				refreshSwitcherTable(selectUserEntity);
				LOG.error("UserDeviceManageView.updateEntity() error", e);
			}
			strategy.showNormalMessage("删除成功");
			refreshSwitcherTable(selectUserEntity);
		}
	}
	
	/**
	 * @author Administrator
	 */
	class SelectedDeviceDialog extends JDialog{
		private static final long serialVersionUID = 1L;
		private List<NodeEntity> list = null; 
		private JTable table = null;
		private TableModel tableModel = null;
		private ViewPart viewPart = null; 
		
		/**
		 * @param list  区域中已有的用户信息列表
		 * @param type  是操作管理员还是交换机
		 * @param viewPart  本视图
		 */
		public SelectedDeviceDialog(List<NodeEntity> list,ViewPart viewPart,String title){
			super(ClientUtils.getRootFrame(),title);
			this.list = list;
			this.viewPart = viewPart;
			
			init();
		}
		
		private void init(){
			JPanel panel = new JPanel(new BorderLayout());
			
			JScrollPane scrollPnl = new JScrollPane();
			table = new JTable();

			tableModel = new DeviceTableModel();
			((DeviceTableModel)tableModel).setColumnName(DEVICE_COLUMN_NAME);

			table.setModel(tableModel);
			scrollPnl.getViewport().add(table);
			
			JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			JButton saveBtn = new JButton("保存", imageRegistry.getImageIcon(ButtonConstants.SAVE));
			JButton closeSelectBtn = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
			bottomPnl.add(saveBtn);
			bottomPnl.add(closeSelectBtn);
			
			panel.add(scrollPnl,BorderLayout.CENTER);
			panel.add(bottomPnl,BorderLayout.SOUTH);
			
			//设置值
			setValue();
			
			saveBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					saveAction();
				}
			});
			
			closeSelectBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
			
			this.getContentPane().add(panel);
			this.setSize(400, 300);
			this.setModal(true);
			this.setResizable(false);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setIconImage(ClientUtils.getRootFrame().getIconImage());
			this.setLocationRelativeTo(viewPart);
		}
		
		private void setValue(){
			//向数据库查询所有的设备
			List allDevices = queryAllDevice();
			((DeviceTableModel)tableModel).setDataList(allDevices);
			((DeviceTableModel)tableModel).fireTableDataChanged();
		}
		
		/**
		 * 保存
		 */
		private void saveAction(){
			UserEntity selectUserEntity = userView.getSelectedUserEntity();
			if (selectUserEntity == null){
				JOptionPane.showMessageDialog(this, "请选择要配置的用户","提示",JOptionPane.NO_OPTION);
				return;
			}
			
			int[] selectRows = table.getSelectedRows();
			int lenth = selectRows.length;
			if (lenth < 1){
				return;
			}
			
			List<NodeEntity> nodeList = new ArrayList<NodeEntity>(list); 

			for (int i = 0 ; i < lenth; i++){
				List rowList = ((DeviceTableModel)tableModel).getSelectedRow(selectRows[i]);
				NodeEntity nodeEntity = (NodeEntity)rowList.get(2);
				
				boolean isSame = false;
				for (NodeEntity entity : nodeList){
					if ((long)entity.getId() == (long)nodeEntity.getId()){
						isSame = true;
						break;
					}
				}
				if (!isSame){
					nodeList.add(nodeEntity);
				}
			}
			saveNodeEntity(nodeList);
			
			this.dispose();
		}
		
		private void close(){
			this.dispose();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveNodeEntity(List<NodeEntity> nodeList) {
		List<List> dataList = new ArrayList<List>();
		
		for (NodeEntity nodeEntity : nodeList){
			List rowList = new ArrayList();
			String deviceTag = "";   //设备标识
			String name = "";  //设备名称
			if(nodeEntity instanceof SwitchTopoNodeEntity){
				deviceTag = ((SwitchTopoNodeEntity)nodeEntity).getIpValue();
				name = nodeEntity.getName();
			}
			else if(nodeEntity instanceof SwitchTopoNodeLevel3){
				deviceTag = ((SwitchTopoNodeLevel3)nodeEntity).getIpValue();
				name = nodeEntity.getName();
			}
			else if(nodeEntity instanceof EponTopoEntity){
				deviceTag = ((EponTopoEntity)nodeEntity).getIpValue();
				name = nodeEntity.getName();		
			}
			else if(nodeEntity instanceof ONUTopoNodeEntity){
				deviceTag = ((ONUTopoNodeEntity)nodeEntity).getMacValue();
				name = nodeEntity.getName();	
			}
			else if(nodeEntity instanceof CarrierTopNodeEntity){
				deviceTag = ((CarrierTopNodeEntity)nodeEntity).getCarrierCode() + "";
				name = nodeEntity.getName();	
			}
			else if(nodeEntity instanceof GPRSTopoNodeEntity){
				deviceTag = ((GPRSTopoNodeEntity)nodeEntity).getUserId();
				name = nodeEntity.getName();	
			}
			else if(nodeEntity instanceof VirtualNodeEntity){
				deviceTag = ((VirtualNodeEntity)nodeEntity).getIpValue();
				name = nodeEntity.getName();	
			}
			else{
				continue;
			}
			
			rowList.add(0, deviceTag);//第一列为设备标识
			rowList.add(1, name);//第二列为设备名称
			rowList.add(2,nodeEntity);//第三列为NodeEntity
			dataList.add(rowList);
		}
		
		deviceModel.setDataList(dataList);
		
		//保存到数据库
		saveDevice();
	}
	
	/**
	 * 
	 */
	public void saveDevice(){
		UserEntity selectUserEntity = userView.getSelectedUserEntity();
		if (selectUserEntity == null){
			return;
		}
		
		List<NodeEntity> nodeList = new ArrayList<NodeEntity>();
		
		List<List> dataList = deviceModel.getDataList();
		for (List rowList : dataList){
			NodeEntity nodeEntity = (NodeEntity)rowList.get(2);//得到已有的设备信息
			boolean isSame = false;
			int index = -1;
			for (int i = 0 ; i < nodeEntity.getUsers().size(); i++){
				long selectId = selectUserEntity.getId();
				Object[] objects = nodeEntity.getUsers().toArray();
				long id = ((UserEntity)objects[i]).getId();
				if (selectId == id){
					index = i;
					isSame = true;
					break;
				}
			}
			if (!isSame){
				nodeEntity.getUsers().add(selectUserEntity);
			}
			
			nodeList.add(nodeEntity);
		}
		Task saveTask = new SaveRequestTask(nodeList, selectUserEntity);
		showMessageDialog(saveTask, "保存");
	}
	
	private class SaveRequestTask implements Task{

		private final List<NodeEntity> nodeList;
		private final UserEntity selectUserEntity;
		public SaveRequestTask(List<NodeEntity> nodeList, UserEntity selectUserEntity){
			this.nodeList = nodeList;
			this.selectUserEntity = selectUserEntity;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getService().updateEntities(nodeList);
			}catch(Exception e){
				strategy.showErrorMessage("保存异常");
				refreshSwitcherTable(selectUserEntity);
				LOG.error("UserDeviceManageView.updateEntity() error", e);
			}
			strategy.showNormalMessage("保存成功");
			refreshSwitcherTable(selectUserEntity);
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task,operation);
				}
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	public void refreshSwitcherTable(UserEntity selectUserEntity){
		List<List> dataList = new ArrayList<List>(); 
		if (selectUserEntity != null){
			
			//通过左边树选择的用户ID查询NodeEntity。
			List<NodeEntity> nodeEntities = this.remoteServer.getNmsService()
				.queryNodeEntityByUser(selectUserEntity.getId());

			if (nodeEntities != null && nodeEntities.size() > 0){
				Iterator iterator = nodeEntities.iterator();
				while (iterator.hasNext()){
					NodeEntity nodeEntity = (NodeEntity)iterator.next();
					if (nodeEntity instanceof SwitchTopoNodeEntity){
						List rowList = new ArrayList();
						rowList.add(((SwitchTopoNodeEntity)nodeEntity).getIpValue());
						rowList.add(((SwitchTopoNodeEntity)nodeEntity).getName());
						rowList.add(nodeEntity);
						dataList.add(rowList);
					}
					else if (nodeEntity instanceof SwitchTopoNodeLevel3){
						List rowList = new ArrayList();
						rowList.add(((SwitchTopoNodeLevel3)nodeEntity).getIpValue());
						rowList.add(((SwitchTopoNodeLevel3)nodeEntity).getName());
						rowList.add(nodeEntity);
						dataList.add(rowList);
					}
					else if (nodeEntity instanceof EponTopoEntity){
						List rowList = new ArrayList();
						rowList.add(((EponTopoEntity)nodeEntity).getIpValue());
						rowList.add(((EponTopoEntity)nodeEntity).getName());
						rowList.add(nodeEntity);
						dataList.add(rowList);
					}
					else if (nodeEntity instanceof ONUTopoNodeEntity){
						List rowList = new ArrayList();
						rowList.add(((ONUTopoNodeEntity)nodeEntity).getMacValue());
						rowList.add(((ONUTopoNodeEntity)nodeEntity).getName());
						rowList.add(nodeEntity);
						dataList.add(rowList);
					}
					else if (nodeEntity instanceof CarrierTopNodeEntity){
						List rowList = new ArrayList();
						rowList.add(((CarrierTopNodeEntity)nodeEntity).getCarrierCode() + "");
						rowList.add(((CarrierTopNodeEntity)nodeEntity).getName());
						rowList.add(nodeEntity);
						dataList.add(rowList);
					}
					else if (nodeEntity instanceof GPRSTopoNodeEntity){
						List rowList = new ArrayList();
						rowList.add(((GPRSTopoNodeEntity)nodeEntity).getUserId());
						rowList.add(((GPRSTopoNodeEntity)nodeEntity).getName());
						rowList.add(nodeEntity);
						dataList.add(rowList);
					}
					else if (nodeEntity instanceof VirtualNodeEntity){
						List rowList = new ArrayList();
						rowList.add(((VirtualNodeEntity)nodeEntity).getIpValue());
						rowList.add(((VirtualNodeEntity)nodeEntity).getName());
						rowList.add(nodeEntity);
						dataList.add(rowList);
					}
				}
			}
		}
		
		deviceModel.setDataList(dataList);
		deviceModel.fireTableDataChanged();
	}
	
	/**
	 * 查询所有的设备
	 */
	@SuppressWarnings("unchecked")
	public List queryAllDevice(){
		List<List> objects = new ArrayList<List>();
		
		//二层交换机
		List<SwitchTopoNodeEntity> switchTopoNodeEntities = (List<SwitchTopoNodeEntity>) remoteServer
			.getService().findAll(SwitchTopoNodeEntity.class);
		for(SwitchTopoNodeEntity switchTopoNodeEntity : switchTopoNodeEntities){
//			SwitchNodeEntity switchNodeEntity = remoteServer.getService()
//					.getSwitchByIp(switchTopoNodeEntity.getIpValue());
			
			List rowList = new ArrayList();
			rowList.add(switchTopoNodeEntity.getIpValue());
			rowList.add(switchTopoNodeEntity.getName());
			rowList.add(switchTopoNodeEntity);
			objects.add(rowList);
		}
		
		//三层交换机
		List<SwitchTopoNodeLevel3> layer3TopoEntitys = (List<SwitchTopoNodeLevel3>) remoteServer
				.getService().findAll(SwitchTopoNodeLevel3.class);
		for(SwitchTopoNodeLevel3 layer3TopoNode : layer3TopoEntitys){
//			SwitchLayer3 layer3 = remoteServer.getService().getSwitcher3ByIP(layer3TopoNode.getIpValue());
			
			List rowList = new ArrayList();
			rowList.add(layer3TopoNode.getIpValue());
			rowList.add(layer3TopoNode.getName());
			rowList.add(layer3TopoNode);
			objects.add(rowList);
		}
		
		//OLT
		List<EponTopoEntity> eponTopoEntities = (List<EponTopoEntity>) remoteServer
				.getService().findAll(EponTopoEntity.class);
		for(EponTopoEntity eponTopoEntity : eponTopoEntities){
//			OLTEntity oltEntity = remoteServer.getService()
//					.getOLTByIpValue(eponTopoEntity.getIpValue());
			
			List rowList = new ArrayList();
			rowList.add(eponTopoEntity.getIpValue());
			rowList.add(eponTopoEntity.getName());
			rowList.add(eponTopoEntity);
			objects.add(rowList);
		}
		
		List<ONUTopoNodeEntity> onuTopoNodeEntities = (List<ONUTopoNodeEntity>) remoteServer
				.getService().findAll(ONUTopoNodeEntity.class);
		for(ONUTopoNodeEntity onuTopoNodeEntity : onuTopoNodeEntities){
//			String macValue = onuTopoNodeEntity.getMacValue();
//			ONUEntity onuEntity = remoteServer.getService().getOLTONUEntity(macValue);	
			
			List rowList = new ArrayList();
			rowList.add(onuTopoNodeEntity.getMacValue());
			rowList.add(onuTopoNodeEntity.getName());
			rowList.add(onuTopoNodeEntity);
			objects.add(rowList);
		}
		
		
		List<CarrierTopNodeEntity> carrierTopNodeEntities = (List<CarrierTopNodeEntity>) remoteServer
			.getService().findAll(CarrierTopNodeEntity.class);
		for(CarrierTopNodeEntity carrierTopNodeEntity : carrierTopNodeEntities){
//			int carrierCode = carrierTopNodeEntity.getCarrierCode();
//			CarrierEntity carrierEntity = remoteServer.getService().getCarrierByCode(carrierCode);
			
			List rowList = new ArrayList();
			rowList.add(carrierTopNodeEntity.getCarrierCode() + "");
			rowList.add(carrierTopNodeEntity.getName());
			rowList.add(carrierTopNodeEntity);	
			objects.add(rowList);
		}
		
		List<GPRSTopoNodeEntity> gprsTopoNodeEntities = (List<GPRSTopoNodeEntity>) remoteServer
			.getService().findAll(GPRSTopoNodeEntity.class);
		for(GPRSTopoNodeEntity gprsTopoNodeEntity : gprsTopoNodeEntities){
//			String userID = gprsTopoNodeEntity.getUserId();
//			GPRSEntity gprsEntity = remoteServer.getService().getGPRSEntityByUserId(userID);
			
			List rowList = new ArrayList();
			rowList.add(gprsTopoNodeEntity.getUserId());
			rowList.add(gprsTopoNodeEntity.getName());
			rowList.add(gprsTopoNodeEntity);
			objects.add(rowList);
		}
		
		List<VirtualNodeEntity> virtualNodeEntities = (List<VirtualNodeEntity>) remoteServer
					.getService().findAll(VirtualNodeEntity.class);
		for(VirtualNodeEntity virtualNodeEntity : virtualNodeEntities){
			List rowList = new ArrayList();
			rowList.add(virtualNodeEntity.getIpValue());
			rowList.add(virtualNodeEntity.getName());
			rowList.add(virtualNodeEntity);
			objects.add(rowList);
		}	
		
		
		return objects;
	}
	
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}

	
	public DeviceTableModel getDeviceModel() {
		return deviceModel;
	}

	@SuppressWarnings("unchecked")
	class DeviceTableModel extends AbstractTableModel{
		private String[] columnName = null;
		private List<List> dataList = null;
		
		public DeviceTableModel(){
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
			if (null == dataList){
				this.dataList = new ArrayList<List>();
			}
			else{
				this.dataList = dataList;
			}
		}

		public int getRowCount(){
			if (null == dataList){
				return 0;
			}
			return dataList.size();
		}

	    public int getColumnCount(){
	    	if (null == columnName){
	    		return 0;
	    	}
	    	return columnName.length;
	    }

	    @Override
		public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }
	  
	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	    public Object getValueAt(int rowIndex, int columnIndex){
	    	if (rowIndex < 0 || columnIndex < 0){
	    		return null;
	    	}
	    	
	    	return dataList.get(rowIndex).get(columnIndex);
	    }
	    
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	dataList.get(rowIndex).set(columnIndex, aValue);
	    }
	    
	    public List getSelectedRow(int selectedRow){
			return this.dataList.get(selectedRow);
		}
	}
}
