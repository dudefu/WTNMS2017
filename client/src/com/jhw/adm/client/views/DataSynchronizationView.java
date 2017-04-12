package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
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
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.jhw.adm.client.model.epon.OLTTypeModel;
import com.jhw.adm.client.swing.CommonTable;
import com.jhw.adm.client.swing.DataSynchronizeStrategy;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(DataSynchronizationView.ID)
@Scope(Scopes.DESKTOP)
public class DataSynchronizationView extends ViewPart {
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "dataSynchronizationView";

	private final JPanel centerPnl = new JPanel();
	private final JScrollPane scrollPnl = new JScrollPane();
	private final CommonTable table = new CommonTable();
	private DeviceTableModel tableModel;
	private final JCheckBox chkBox = new JCheckBox("全选(鼠标结合CTRL(或SHIFT)键支持多选)");
	private static final String[] COLUMN_NAME = { "设备IP","设备型号" };
	
	private final JPanel bottomPnl = new JPanel();
	private JButton closeBtn = null;
	private JButton synBtn = null;

	private MessageSender messageSender;	
	private ButtonFactory buttonFactory;

	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;

	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=OLTTypeModel.ID)
	private OLTTypeModel oltTypeModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	private static final Logger LOG = LoggerFactory.getLogger(DataSynchronizationView.class);
	
	@PostConstruct
	protected void initialize() {
		messageSender = remoteServer.getMessageSender();
		buttonFactory = actionManager.getButtonFactory(this); 
		init();
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}	

	private void init() {
		this.setTitle("数据上载");
		
		initCenterPnl();
		initBottomPnl();

		this.setLayout(new BorderLayout());
		this.add(centerPnl, BorderLayout.CENTER);
		this.add(bottomPnl, BorderLayout.SOUTH);
		this.setViewSize(480, 360);
		
		queryData();
	}

	private void initCenterPnl() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel.add(chkBox);

		tableModel = new DeviceTableModel();
		tableModel.setColumnName(COLUMN_NAME);
		table.setModel(tableModel);
		table.setSortable(false);
		scrollPnl.getViewport().add(table);

		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(panel, BorderLayout.SOUTH);
		centerPnl.add(scrollPnl, BorderLayout.CENTER);
		centerPnl.setBorder(BorderFactory.createTitledBorder("设备列表"));

		chkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					table.setRowSelectionInterval(0, table.getRowCount() - 1);
				} else if (table.getSelectedRows().length == table.getRowCount()) {
					table.clearSelection();
				}
			}
		});
		
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						boolean selected = table.getSelectedRows().length == table.getRowCount();
						chkBox.getModel().setSelected(selected);
						DataSynchronizationView.this.repaint();
					}
				}
			);
	}

	private void initBottomPnl() {
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		synBtn = buttonFactory.createButton(UPLOAD);
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(synBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
		
		setCompontEanble(true);
	}

	private void setCompontEanble(boolean enable) {
		synBtn.setEnabled(enable);
	}
	
	@SuppressWarnings("unchecked")
	private void queryData() {
		List<Serializable> nodeList = new ArrayList<Serializable>();
//		Iterator iterator = equipmentModel.getDiagram().getNodes().iterator();
//		while(iterator.hasNext()){
//			Object object = iterator.next();
//			if (object instanceof SwitchTopoNodeEntity){
//				nodeEntitieList.add((SwitchTopoNodeEntity)object);
//			}else if (object instanceof EponTopoEntity){
//				nodeEntitieList.add((EponTopoEntity)object);
//			}
//		}
		List<SwitchTopoNodeEntity> switchTopoNodeEntities = (List<SwitchTopoNodeEntity>) remoteServer
				.getService().findAll(SwitchTopoNodeEntity.class);
		for(SwitchTopoNodeEntity switchTopoNodeEntity : switchTopoNodeEntities){
			LOG.info("SwitchTopoNodeEntity : " + switchTopoNodeEntity.getIpValue());
			SwitchNodeEntity switchNodeEntity = remoteServer.getService()
					.getSwitchByIp(switchTopoNodeEntity.getIpValue());
			nodeList.add(switchNodeEntity);
		}
		List<EponTopoEntity> eponTopoEntities = (List<EponTopoEntity>) remoteServer
				.getService().findAll(EponTopoEntity.class);
		for(EponTopoEntity eponTopoEntity : eponTopoEntities){
			LOG.info("EponTopoEntity : " + eponTopoEntity.getIpValue());
			OLTEntity oltEntity = remoteServer.getService()
					.getOLTByIpValue(eponTopoEntity.getIpValue());
			nodeList.add(oltEntity);
		}
		List<SwitchTopoNodeLevel3> layer3TopoEntitys = (List<SwitchTopoNodeLevel3>) remoteServer
				.getService().findAll(SwitchTopoNodeLevel3.class);
		for(SwitchTopoNodeLevel3 layer3TopoNode : layer3TopoEntitys){
			LOG.info("SwitchTopoNodeLevel3 : " + layer3TopoNode.getIpValue());
			SwitchLayer3 layer3 = remoteServer.getService().getSwitcher3ByIP(layer3TopoNode.getIpValue());
			nodeList.add(layer3);
		}
//		for(NodeEntity nodeEntity : nodeEntitieList){
//			if (nodeEntity instanceof SwitchTopoNodeEntity){
//				LOG.info("SwitchTopoNodeEntity : " + ((SwitchTopoNodeEntity)nodeEntity).getIpValue());
//				SwitchNodeEntity switchNodeEntity = remoteServer.getService()
//						.getSwitchByIp(((SwitchTopoNodeEntity)nodeEntity).getIpValue());
//				nodeList.add(switchNodeEntity);
//			}else if (nodeEntity instanceof EponTopoEntity){
//				LOG.info("EponTopoEntity : " + ((EponTopoEntity)nodeEntity).getIpValue());
//				OLTEntity oltEntity = remoteServer.getService()
//						.getOLTByIpValue(((EponTopoEntity)nodeEntity).getIpValue());
//				nodeList.add(oltEntity);
//			}
//		}
		tableModel.setDataList(nodeList);
		tableModel.fireTableDataChanged();
	}

	@ViewAction(icon = ButtonConstants.SYNCHRONIZE,desc="批量上载设备数据",role=Constants.MANAGERCODE)
	public void upload() {
		final HashSet<SynchDevice> deviceList = getDeviceList();
		
		if (deviceList == null || deviceList.size() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要上载的设备","提示",JOptionPane.NO_OPTION);
			return;
		}
		String message = "如果设备侧和网管侧数据不一致，网管侧数据会删除。你确定吗？";
		int result = JOptionPane.showConfirmDialog(this, message, "提示", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (0 != result){
			return;
		}

		Task task = new RequestTask(deviceList);
		showMessageDialog(task);
	}
	
	private class RequestTask implements Task{

		private HashSet<SynchDevice> deviceList = null;
		public RequestTask(HashSet<SynchDevice> deviceList){
			this.deviceList = deviceList;
		}
		
		@Override
		public void run() {
			messageSender.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage message = session.createObjectMessage();
					message.setIntProperty(Constants.MESSAGETYPE,
							MessageNoConstants.SYNCHDEVICE);
					message.setObject(deviceList);
					message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
					message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());	
					return message;
				}
			});
		}
		
	}
	
	private JProgressBarModel progressBarModel;
	private DataSynchronizeStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new DataSynchronizeStrategy("上载数据", progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,"上载数据",true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task);
				}
			});
		}
	}
	
	private HashSet<SynchDevice> getDeviceList() {
		HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		
		int[] rows = table.getSelectedRows();
		if (chkBox.isSelected()){
			rows = new int[table.getRowCount()];
			for(int i = 0 ; i < table.getRowCount(); i++){
				rows[i] = i;
			}
		}
		for (int i = 0 ; i < rows.length; i++){
			SynchDevice synchDevice = new SynchDevice();
			String ip = StringUtils.EMPTY;
			int deviceType = 0;
			
			Serializable serializable = tableModel.getNodeEntity(rows[i]);
			if (serializable instanceof SwitchNodeEntity){
				SwitchNodeEntity switcher = (SwitchNodeEntity)serializable;
				if(null != switcher.getBaseConfig()){
					ip = switcher.getBaseConfig().getIpValue();
					LOG.info(String.format("二层交换机{%s}需要进行上载", ip));
				}
				deviceType = switcher.getDeviceModel();
			}else if(serializable instanceof OLTEntity){
				OLTEntity olt = (OLTEntity)serializable;
				ip = olt.getIpValue();
				LOG.info(String.format("OLT{%s}需要进行上载", ip));
				deviceType = olt.getDeviceModel();
			}else if(serializable instanceof SwitchLayer3){
				SwitchLayer3 switcherLayer3 = ((SwitchLayer3)serializable);
				ip = switcherLayer3.getIpValue();
				LOG.info(String.format("三层交换机{%s}需要进行上载", ip));
				deviceType = switcherLayer3.getDeviceModel();
			}
			synchDevice.setIpvalue(ip);
			synchDevice.setModelNumber(deviceType);
			synDeviceList.add(synchDevice);
		}
		
		return synDeviceList;
	}
	
	class DeviceTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;

		private String[] columnName = null;
		
		private List<Serializable> dataList = null;
		
		public DeviceTableModel(){
			super();
		}
		
		public String[] getColumnName() {
			return columnName;
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public List<Serializable> getDataList() {
			return dataList;
		}

		public void setDataList(List<Serializable> dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<Serializable>();
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
	
	    public Object getValueAt(int row, int col){
	    	Object value = null;
	    	if(row < dataList.size()){
	    		Serializable serializable = dataList.get(row);
	    		if (serializable instanceof SwitchNodeEntity){
					switch (col) {
					case 0:
						if (null != ((SwitchNodeEntity) serializable)
								.getBaseConfig()) {
							if (null == ((SwitchNodeEntity) serializable)
									.getBaseConfig().getIpValue()) {
								value = "";
							} else {
								value = ((SwitchNodeEntity) serializable)
										.getBaseConfig().getIpValue();
							}
						} else {
							value = "";
						}
						break;
					case 1:
						value = ((SwitchNodeEntity) serializable).getType();
						break;
					default:
						break;
					}
	    		}else if (serializable instanceof OLTEntity){
	    			switch (col) {
					case 0:
						if (null != ((OLTEntity) serializable).getIpValue()) {
							value = ((OLTEntity) serializable).getIpValue();
						} else {
							value = "";
						}
						break;
					case 1:
						if(0 != ((OLTEntity) serializable).getDeviceModel()){
							value = oltTypeModel.get(((OLTEntity) serializable).getDeviceModel()).getKey();
						}else {
							value = "";
						}
						break;
					default:
						break;
					}
		    	}else if(serializable instanceof SwitchLayer3){
		    		switch (col) {
					case 0:
						if(null != ((SwitchLayer3) serializable).getIpValue()){
							value = ((SwitchLayer3) serializable).getIpValue();
						}else {
							value = "";
						}
						break;
					case 1:
						if(null != ((SwitchLayer3) serializable).getName()){
							value = ((SwitchLayer3) serializable).getName();
						}else {
							value = "";
						}
						break;
					default:
						break;
					}
		    	}
	    	}
	    	return value;
	    }
	    
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (aValue instanceof SwitchTopoNodeEntity){
	    		dataList.set(rowIndex, (SwitchTopoNodeEntity)aValue);
	    	}
	    	else if (aValue instanceof EponTopoEntity){
	    		dataList.set(rowIndex, (EponTopoEntity)aValue);
	    	}
	    }
	    
	    public Serializable getNodeEntity(int row){   	
	    	return dataList.get(row);
	    }
	}
}