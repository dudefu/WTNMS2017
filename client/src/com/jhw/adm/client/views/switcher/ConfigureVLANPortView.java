package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

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
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.VlanPortModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.SwitcherExplorerView;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanPort;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(ConfigureVLANPortView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureVLANPortView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "configureVLANPortView";
	
	private JScrollPane scrollPnl = new JScrollPane();
	private JTable table = new JTable();
	
	private NumberField pvIDTextFld = new NumberField(4,0,1,4094,true);
	
	private JPanel bottomPnl = new JPanel();
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn = null;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private VlanConfig vlanConfig = null;
	
	private List<List> oldVlanPortList = new ArrayList<List>();
	
	private DefaultCellEditor cellEditor = null;
	
	private ButtonFactory buttonFactory;
	private MessageSender messageSender;
	
	private static final Logger LOG = LoggerFactory.getLogger(ConfigureVLANPortView.class);
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(VlanPortModel.ID)
	private VlanPortModel vlanPortModel;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=SwitcherExplorerView.ID)
	private ViewPart explorerView;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
		@Override
		public void process(TextMessage message) {
			queryData();
		}
	};
	
	@PostConstruct
	protected void initialize(){
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		this.setTitle("Vlan端口配置");
		
		
		initScrollPnl();
		initBottomPnl();
		
		JPanel mainPnl = new JPanel(new BorderLayout());
		mainPnl.add(scrollPnl,BorderLayout.CENTER);
		mainPnl.add(bottomPnl,BorderLayout.SOUTH);
		
		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				explorerView, mainPnl);
		splitPanel.setDividerLocation(210);
		this.setLayout(new BorderLayout());
		this.add(splitPanel,BorderLayout.CENTER);
		
		statusLbl.setText("状态");
		statusFld.setColumns(15);
		statusFld.setEditable(false);
		statusFld.setBackground(Color.WHITE);
		statusFld.setText("设备侧");
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);	
	}
	
	private void initScrollPnl(){
		table.setModel(vlanPortModel);
//		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(vlanPortModel);
//		table.setRowSorter(sorter);
//		sorter.toggleSortOrder(0);
		
		setTextFieldColumn(table,table.getColumnModel().getColumn(1));
		setComboxColumn(table, table.getColumnModel().getColumn(2));

		scrollPnl.getViewport().add(table);
	}
	
	private void initBottomPnl(){
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		statusPnl.add(statusFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		leftPnl.add(statusPnl);
		
		JPanel panel = new JPanel(new GridBagLayout());
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		closeBtn = buttonFactory.createCloseButton();
		panel.add(uploadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		panel.add(downloadBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		panel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPnl.add(panel);
		
		bottomPnl.setLayout(new BorderLayout());
		bottomPnl.add(leftPnl, BorderLayout.WEST);
		bottomPnl.add(rightPnl, BorderLayout.EAST);
	}
	
	public void setTextFieldColumn(JTable table,TableColumn sportColumn) {
//		JTextField textFld = new JTextField();
		cellEditor = new DefaultCellEditor(pvIDTextFld);
        sportColumn.setCellEditor(cellEditor);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        sportColumn.setCellRenderer(renderer);
    }
	
    public void setComboxColumn(JTable table,TableColumn sportColumn) {
        JComboBox comboBox = new JComboBox();
        for(int i = 0 ; i < 28; i++){
        	comboBox.addItem(i);
        }
        sportColumn.setCellEditor(new DefaultCellEditor(comboBox));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        sportColumn.setCellRenderer(renderer);
    }
    
    @SuppressWarnings("unchecked")
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
    	if (null == switchNodeEntity){
    		return;
    	}
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<VlanConfig> vlanConfigList = (List<VlanConfig>)remoteServer.getService().findAll(VlanConfig.class, where, parms);
		if (null == vlanConfigList || vlanConfigList.size() <1){
			vlanConfig = null;
			
	    	Set<VlanPort> vlanPortSet = new LinkedHashSet<VlanPort>();
			for (int i = 0 ; i < 28 ; i++){
				VlanPort vlanPort = new VlanPort();
				vlanPort.setPortNO(i+1);
				vlanPort.setPvid(1);
				vlanPort.setPriority(i);
				vlanPortSet.add(vlanPort);
			}
			
			//把set转化为List
			List<VlanPort> vlanPortList = new ArrayList<VlanPort>();
			Iterator iterator = vlanPortSet.iterator();
			while(iterator.hasNext()){
				VlanPort vlanPort = (VlanPort)iterator.next();
				vlanPortList.add(vlanPort);
			}
			
			statusFld.setText("");
			vlanPortModel.setVlanPortData(vlanPortList);
		}
		else{
			vlanConfig = vlanConfigList.get(0);
			Set<VlanPort> vlanPortSet = vlanConfig.getVlanPorts();
			
			//把set转化为List
			List<VlanPort> vlanPortList = new ArrayList<VlanPort>();
			String status = "";
			
			List<VlanPort> middleList = new ArrayList<VlanPort>(vlanPortSet);
			int portCount = middleList.size();
			for (int i = 0; i< portCount; i++){
				int port = i+1;
				for (int j = 0; j < middleList.size() ; j++){
					VlanPort vlanport = middleList.get(j);
					
					if ("".equals(status)){
						status = dataStatus.get(vlanport.getIssuedTag()).getKey();
					}
					
					int portNo = vlanport.getPortNO();
					if (port == portNo){
						vlanPortList.add(vlanport);
						break;
					}
				}
			}
			
			vlanPortModel.setVlanPortData(vlanPortList);
			statusFld.setText(status);
			
			//在内存中保存一份，便于在下发时做比较
			oldVlanPortList.clear();
			for (int  i = 0 ; i < vlanPortList.size(); i++){
				List rowList = new ArrayList();
				rowList.add(0,vlanPortList.get(i).getId());
				rowList.add(1,vlanPortList.get(i).getPvid());
				rowList.add(2,vlanPortList.get(i).getPriority());
				oldVlanPortList.add(rowList);
			}
		}
    }
    
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载VLAN端口配置",role=Constants.MANAGERCODE)
	public void download(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		cellEditor.stopCellEditing();

		List<VlanPort> vlanPortList = getPortValues();

		String macValue = switchNodeEntity.getBaseInfo().getMacValue();

		Task task = new DownLoadRequestTask(vlanPortList, macValue, result);
		showConfigureMessageDialog(task, "下载");
	}
	
	private class DownLoadRequestTask implements Task{

		private List<VlanPort> vlanPortList = null;
		private String macValue = "";
		private int result = 0;
		public DownLoadRequestTask(List<VlanPort> vlanPortList, String macValue, int result){
			this.vlanPortList = vlanPortList;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				if (null == vlanConfig){
					//list转化为set
					Set<VlanPort> vlanPortSet = new LinkedHashSet<VlanPort>();
					for (int i = 0 ; i < vlanPortList.size(); i++){
						vlanPortSet.add(vlanPortList.get(i));
					}
					
					vlanConfig = new VlanConfig();
					vlanConfig.setVlanPorts(vlanPortSet);
					vlanConfig.setSwitchNode(switchNodeEntity);
					vlanConfig.setIssuedTag(Constants.ISSUEDADM);
					remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.VLANSET, vlanConfig, 
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
				}
				else{
					List<Serializable> data = new ArrayList<Serializable>();
					for (int i = 0 ; i < vlanPortList.size() ; i++){
						vlanConfig.setIssuedTag(Constants.ISSUEDADM);
						vlanPortList.get(i).setIssuedTag(Constants.ISSUEDADM);
						vlanPortList.get(i).setVlanConfig(vlanConfig);
						data.add(vlanPortList.get(i));
					}
					remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.VLANPORTUPDATE, data,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
				}
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载VLAN端口异常");
				LOG.error("ConfigureVLANPortView.save() failure:{}",e);
				queryData();
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			paramConfigStrategy = new ParamConfigStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(paramConfigStrategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showConfigureMessageDialog(task, operation);
				}
			});
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载VLAN端口配置",role=Constants.MANAGERCODE)
	public void upload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		
		Task task = new UploadRequestTask(synDeviceList,MessageNoConstants.SINGLESWITCHVLANPORT);
		showUploadMessageDialog(task, "上载VLAN端口");
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	private void showUploadMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			uploadStrategy = new ParamUploadStrategy(operation, progressBarModel,true);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(uploadStrategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showUploadMessageDialog(task, operation);
				}
			});
		}
	}
	
	private List<VlanPort> getPortValues(){
		//得到现在视图中输入的值
		List<VlanPort> newVlanPortList = vlanPortModel.getVlanPortData();
		
		List<VlanPort> portList = new ArrayList<VlanPort>();
		for (int i = 0 ; i < newVlanPortList.size() ; i++){
			VlanPort newVlanPort = newVlanPortList.get(i);
			portList.add(newVlanPort);
		}
		return portList;
	}
	
	private List<VlanPort> getPortValue(){
		//得到现在视图中输入的值
		List<VlanPort> newVlanPortList = vlanPortModel.getVlanPortData();
		
		List<VlanPort> portList = new ArrayList<VlanPort>();
		for (int i = 0 ; i < newVlanPortList.size() ; i++){
			VlanPort newVlanPort = newVlanPortList.get(i);
			for (int k = 0 ; k < oldVlanPortList.size(); k++){
				int id = NumberUtils.toInt(oldVlanPortList.get(k).get(0).toString());
				int pvid = NumberUtils.toInt(oldVlanPortList.get(k).get(1).toString());
				int priority = NumberUtils.toInt(oldVlanPortList.get(k).get(2).toString());

				if (newVlanPort.getId() == id){
					if (newVlanPort.getPvid() != pvid ||newVlanPort.getPriority() != priority){
						portList.add(newVlanPort);
						break;
					}
				}
			}
		}
		return portList;
	}
    
	private PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				if (scrollPnl.getViewport().getComponentCount() < 1){
					scrollPnl.getViewport().add(table);
				}
				queryData();
			}
			else{
				scrollPnl.getViewport().removeAll();
				scrollPnl.getViewport().revalidate();
				scrollPnl.revalidate();
				scrollPnl.updateUI();
			}
		}
		
	};
	
	private void clear(){
		this.switchNodeEntity = null;
	}
    
    public JButton getCloseButton(){
		return this.closeBtn;
	}
    
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
}