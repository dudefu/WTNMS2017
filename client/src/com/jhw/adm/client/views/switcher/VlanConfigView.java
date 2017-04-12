package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.BACK;
import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang.StringUtils;
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
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.SwitcherExplorerView;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.nets.VlanPortConfig;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.SwitchVlanPortInfo;
import com.jhw.adm.server.entity.util.VlanBean;

/*
 * 批量配置
 */

@Component(VlanConfigView.ID)
@Scope(Scopes.DESKTOP)
public class VlanConfigView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "vlanConfigView";
	private static final Logger LOG = LoggerFactory.getLogger(VlanConfigView.class);
	
	private final JPanel vlanPnl = new JPanel();
	private final JLabel vlanIDLbl = new JLabel();
	private final JTextField vlanIDFld = new JTextField();
	private final JLabel vlanNameLbl = new JLabel();
	private final JTextField vlanNameFld = new JTextField();
	
	private final JPanel mainPnl = new JPanel();
	
	private final JPanel portPnl = new JPanel();
	private final JPanel bottomPortPnl = new JPanel();
	
	private final JPanel optionPnl = new JPanel();
	private final JLabel tagLbl = new JLabel();
	private final JComboBox tagCombox = new JComboBox();

	
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JTable portTable = new JTable();
	private PortVlanTableModel portModel = null;
	
	
	private final JPanel directPnl = new JPanel();
	private final JButton toRightBtn = new JButton(">>");
	private final JButton toLeftBtn = new JButton("<<");
	
	private final JPanel configPnl = new JPanel();
	private final JTable configTable = new JTable();
	private PortVlanTableModel configModel = null;
	

	private final JPanel bottomPnl = new JPanel();
	
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
//	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton returnBtn;
	private JButton closeBtn ;
	
	private CardLayout cardLayout;
	private JPanel cardPnl;
	
	private VlanBean vlanBean;
	
	private VlanManagementView vlanMngView;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private final static String[] TAG = {"tag","untag"};
	private final static String[] CONFIG_COLUMNNAME = new String[]{"设备","端口","Tag/Untag"};
	private final static String[] PORT_COLUMNNAME = new String[]{"端口","端口类型"};
	private final static String[] VLAN_COLUMNNAME = new String[]{"VLAN ID","VLAN名称"};
	
	private ButtonFactory buttonFactory;
	private MessageSender messageSender;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=SwitcherExplorerView.ID)
	private ViewPart explorerView;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	/**
	 * 设备浏览器的时间监听
	 * 当选择不同的设备时，根据switchNodeEntity,重新查询数据库
	 * @author Administrator
	 *
	 */
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				setPortTable();
			}
			else{
				portModel.setDataList(null);
				portModel.fireTableDataChanged();
			}
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
			List<VlanBean> vlanBeanList = remoteServer.getNmsService().querySwitchVlanInfo();
			if (vlanBeanList != null && vlanBeanList.size() > 0){
				String selectVlanId = vlanIDFld.getText();
				for (VlanBean vlanBean : vlanBeanList){
					String vlanId = vlanBean.getVlanID();
					String status = dataStatus.get(vlanBean.getIssuedTag()).getKey();
					if (selectVlanId.equalsIgnoreCase(vlanId.trim())){
						statusFld.setText(status);
					}
				}
			}
			
			vlanMngView.refreshVlanTableView();
		}
	};
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
		@Override
		public void process(TextMessage message) {
			List<VlanBean> vlanBeanList = remoteServer.getNmsService().querySwitchVlanInfo();
			if (vlanBeanList != null && vlanBeanList.size() > 0){
				String selectVlanId = vlanIDFld.getText();
				for (VlanBean vlanBean : vlanBeanList){
					String vlanId = vlanBean.getVlanID();
					String status = dataStatus.get(vlanBean.getIssuedTag()).getKey();
					if (selectVlanId.equalsIgnoreCase(vlanId.trim())){
						statusFld.setText(status);
					}
				}
			}
			
			vlanMngView.refreshVlanTableView();
		}
	};
	
	@PostConstruct
	protected void initialize(){
		init();
	}

	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		initVlanPnl();
		initConfigPnl();
		initPortPnl();
		initBottomPnl();
		
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(portPnl,BorderLayout.WEST);
		panel.add(configPnl,BorderLayout.CENTER);
		
		mainPnl.setLayout(new BorderLayout());
		mainPnl.add(vlanPnl,BorderLayout.NORTH);
		mainPnl.add(panel,BorderLayout.CENTER);
		mainPnl.add(bottomPnl,BorderLayout.SOUTH);
		
		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				explorerView, mainPnl);
		splitPanel.setDividerLocation(210);
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(splitPanel,BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(container);
		
		setResource();
	}
	
	private void initVlanPnl(){
		vlanPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(vlanIDLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,5,0),0,0));
		panel.add(vlanIDFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		panel.add(vlanNameLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,5,0),0,0));
		panel.add(vlanNameFld,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		panel.add(statusLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,5,0),0,0));
		panel.add(statusFld,new GridBagConstraints(5,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
		vlanPnl.add(panel);
		
		vlanPnl.setBorder(BorderFactory.createTitledBorder("当前Vlan"));
	}
	
	private void initPortPnl(){
		initScrollPnl();
		initBottomPortPnl();
		portPnl.setLayout(new BorderLayout());
		portPnl.add(scrollPnl,BorderLayout.CENTER);
		portPnl.add(bottomPortPnl,BorderLayout.SOUTH);
		
		portPnl.setBorder(BorderFactory.createTitledBorder("端口信息"));
		portPnl.setPreferredSize(new Dimension(250,portPnl.getPreferredSize().height));
	}
	
	private void initBottomPortPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(tagLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,2),0,0));
		panel.add(tagCombox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,2),0,0));
		
		
		optionPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		optionPnl.add(panel);
		
		bottomPortPnl.setLayout(new BorderLayout());
		bottomPortPnl.add(optionPnl,BorderLayout.CENTER);	
	}
	
	private void initScrollPnl(){
		portModel = new PortVlanTableModel();
		portModel.setColumnName(PORT_COLUMNNAME);
		portTable.setModel(portModel);
		scrollPnl.getViewport().add(portTable);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
//		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		returnBtn = buttonFactory.createButton(BACK); 
		closeBtn = buttonFactory.createCloseButton();
//		newPanel.add(uploadBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(returnBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		bottomPnl.add(newPanel, BorderLayout.EAST);
	}
	
	private void initConfigPnl(){
		configModel = new PortVlanTableModel();
		configModel.setColumnName(CONFIG_COLUMNNAME);
		configTable.setModel(configModel);
		JScrollPane configScrollPnl= new JScrollPane();
		configScrollPnl.getViewport().add(configTable);
		configTable.getColumn("设备").setPreferredWidth(100);
		
//		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(configModel);
//		configTable.setRowSorter(sorter);
//		sorter.toggleSortOrder(0);
	
		
		directPnl.setLayout(new GridBagLayout());
		directPnl.add(toRightBtn,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,5),0,0));
		directPnl.add(toLeftBtn,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(50,5,0,5),0,0));
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(configScrollPnl,BorderLayout.CENTER);
		configPnl.setLayout(new BorderLayout());
		configPnl.add(panel,BorderLayout.CENTER);
		configPnl.add(directPnl,BorderLayout.WEST);
		panel.setBorder(BorderFactory.createTitledBorder("Vlan端口"));
	}
	
	private void setResource(){
		tagLbl.setText("标签");
		vlanIDLbl.setText("Vlan ID");
		vlanNameLbl.setText("Vlan名称");
		
		for (int i = 0 ; i < TAG.length; i++){
			tagCombox.addItem(TAG[i]);
		}
		tagCombox.setPreferredSize(new Dimension(80,tagCombox.getPreferredSize().height));
		
		vlanIDFld.setColumns(15);
		vlanNameFld.setColumns(15);

		vlanIDFld.setEditable(false);
		vlanIDFld.setBackground(Color.WHITE);
		vlanNameFld.setEditable(false);
		vlanNameFld.setBackground(Color.WHITE);
		
		statusLbl.setText("状态");
		statusFld.setColumns(15);
		statusFld.setBackground(Color.WHITE);
		statusFld.setEditable(false);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		
		toRightBtn.addActionListener(new ButtonActionListener());
		toLeftBtn.addActionListener(new ButtonActionListener());
		toRightBtn.setActionCommand("toRightBtn");
		toLeftBtn.setActionCommand("toLeftBtn");
	}
	
	/**
	 * 把端口信息加入到配置Vlan端口表格中
	 */
	@SuppressWarnings("unchecked")
	private void toRightAction(){
		if (portTable.getSelectedRowCount() < 1){
			return;
		}

		for (int i = 0 ; i < portTable.getSelectedRows().length; i++){
			int modelRow = portTable.convertRowIndexToModel(portTable.getSelectedRows()[i]);
			
			String ipValue = switchNodeEntity.getBaseConfig().getIpValue();
			String port = (String)portModel.getValueAt(modelRow, 0);
			char tagChar = reverseTag(tagCombox.getSelectedItem().toString());
			
			VlanPortConfig vlanPortConfig = new VlanPortConfig();
			vlanPortConfig.setIpVlue(ipValue);
			vlanPortConfig.setPortNo(NumberUtils.toInt(port));
			vlanPortConfig.setPortTag(tagChar);
			
			List rowList = new ArrayList();
			rowList.add(ipValue);
			rowList.add(port);
			rowList.add(tagCombox.getSelectedItem().toString());
			rowList.add(vlanPortConfig);
			
			if (configTable.getRowCount() < 1){
				configModel.insertRow(rowList);
				continue;
			}
			boolean isSame = false;
			int row = 0;
			char tag = 0;
			for (int j = 0 ; j < configTable.getRowCount(); j++){
				row = j;
				String ipAddr = String.valueOf(configModel.getValueAt(row, 0));
				String portValue = String.valueOf(configModel.getValueAt(row, 1));
				tag = reverseTag(String.valueOf(configModel.getValueAt(row, 2)));
				if (ipValue.equals(ipAddr) && port.equals(portValue)){
					isSame = true;
					break;
				}
				else{			
					isSame = false;
				}
			}
			
			if (isSame){
				if (tagChar == tag){
					continue;
				}
				else{
					configModel.updateRow(row, rowList);
					continue;
				}
			}
			else{
				configModel.insertRow(rowList);
			}
		}	
	}
	
	/**
	 * 移除配置Vlan端口表格
	 */
	private void toLeftAction(){
		if (configTable.getSelectedRowCount() < 1){
			return;
		}
		
		int[] selectRows = new int[configTable.getSelectedRowCount()];
		for(int i = 0 ; i < configTable.getSelectedRows().length; i++){
			int modelRow = configTable.convertRowIndexToModel(configTable.getSelectedRows()[i]);
			selectRows[i] = modelRow;
		}
		configModel.removeRow(selectRows);
	}
	
	/**
	 * 设备树的事件响应，通过选择的设备刷新端口信息表格
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void setPortTable(){
		switchNodeEntity =
			(SwitchNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			portModel.setDataList(null);
			portModel.fireTableDataChanged();
			return ;
		}
		
//		通过switchNodeEntity得到设备的端口和端口类型
//		List<List> dataList = new ArrayList<List>();
//		Set<SwitchPortEntity> portEntitySet = switchNodeEntity.getPorts();
//		Iterator iterator = portEntitySet.iterator();
//		while(iterator.hasNext()){
//			SwitchPortEntity switchPortEntity = (SwitchPortEntity)iterator.next();
//			String port = switchPortEntity.getPortNO()+ "";
//			String type = reverseTypeToString(switchPortEntity.getType());
//			
//			List rowList = new ArrayList();
//			rowList.add(port);
//			rowList.add(type);
//			dataList.add(rowList);
//		}
		
		//通过switchNodeEntity得到设备的端口和端口类型
		List<List> dataList = new ArrayList<List>();
		Set<SwitchPortEntity> portEntitySet = switchNodeEntity.getPorts();
		List<SwitchPortEntity> middleList = new ArrayList<SwitchPortEntity>(portEntitySet);
		int portCount = middleList.size();
		for (int i = 0; i< portCount; i++){
			int port = i+1;
			for (int j = 0; j < middleList.size() ; j++){
				SwitchPortEntity switchPortEntity = middleList.get(j);
				int portNo = switchPortEntity.getPortNO();
				String type = reverseTypeToString(switchPortEntity.getType());
				if (port == portNo){
					List rowList = new ArrayList();
					rowList.add(portNo + "");
					rowList.add(type);
					dataList.add(rowList);
					break;
				}
			}
		}

		portModel.setDataList(dataList);
		portModel.fireTableDataChanged();
	}
	
	/**
	 * 设置vlan端口表格的值
	 * @param selectVlanBean
	 * @param switchVlanPortInfoList
	 */
	@SuppressWarnings("unchecked")
	public void setValue(VlanBean selectVlanBean,List<SwitchVlanPortInfo> switchVlanPortInfoList){
		setPortTable();
		
		if (null == selectVlanBean){
			return;
		}
		vlanBean = selectVlanBean;
		String vlanID = selectVlanBean.getVlanID();
		String vlanName = selectVlanBean.getVlanName();
		String status = dataStatus.get(selectVlanBean.getIssuedTag()).getKey();
		this.vlanIDFld.setText(vlanID);
		this.vlanNameFld.setText(vlanName);
		this.statusFld.setText(status);
		
		if (null == switchVlanPortInfoList || switchVlanPortInfoList.size() < 1){
			configModel.setDataList(null);
			configModel.fireTableDataChanged();
			return;
		}
		
		List<List> dataList = new ArrayList<List>();
		for(SwitchVlanPortInfo switchVlanPortInfo : switchVlanPortInfoList){
			String ipValue = switchVlanPortInfo.getIpVlue();
			
			String[] tagPorts;
			if (null != switchVlanPortInfo.getTagPort()){
				tagPorts = switchVlanPortInfo.getTagPort().split(",");
				for(int i = 0 ; i < tagPorts.length; i++){
					if (StringUtils.isNumeric(tagPorts[i])){
						List rowList = new ArrayList();
						rowList.add(ipValue);
						rowList.add(tagPorts[i]);
						rowList.add("tag");
						VlanPortConfig vlanPortConfig = new VlanPortConfig();
						vlanPortConfig.setPortNo(NumberUtils.toInt(tagPorts[i]));
						vlanPortConfig.setIpVlue(ipValue);
						vlanPortConfig.setPortTag('T');
						rowList.add(vlanPortConfig);
						dataList.add(rowList);
					}
				}
			}
			
			String[] untagPorts;
			if (null != switchVlanPortInfo.getUntagPort()){
				untagPorts = switchVlanPortInfo.getUntagPort().split(",");
				String untagPort = sortByPort(untagPorts);//修改部分
				String[] Ports = untagPort.split(",");//
				for(int i = 0 ; i < Ports.length; i++){
					if (StringUtils.isNumeric(Ports[i])){
						List rowList = new ArrayList();
						rowList.add(ipValue);
						rowList.add(Ports[i]);
						rowList.add("untag");
						
						VlanPortConfig vlanPortConfig = new VlanPortConfig();
						vlanPortConfig.setPortNo(NumberUtils.toInt(Ports[i]));
						vlanPortConfig.setIpVlue(ipValue);
						vlanPortConfig.setPortTag('U');
						rowList.add(vlanPortConfig);
						dataList.add(rowList);
					}
				}
			}
		}
		
		configModel.setDataList(dataList);
		configModel.fireTableDataChanged();
	}
	
	/**
	 * 对端口进行排序
	 * @param strInfo
	 * @return
	 */
	private String sortByPort(String[] infoArray){
		
		StringBuffer buffer = new StringBuffer();
		if (infoArray == null || "".equals(infoArray)){
			return buffer.toString();
		}
		int[] iPort = new int[infoArray.length];
		for (int i = 0; i < infoArray.length; i++){
            try{
                int temp = Integer.parseInt(infoArray[i]);   
                iPort[i] = temp; 
            }catch(Exception e){
            }   
		}

		Arrays.sort(iPort);
		for(int j = 0 ; j < iPort.length; j++){
			buffer.append(iPort[j]).append(",");  
		}
		
		String portStrs = buffer.toString();
		if (!"".equals(portStrs)){
			portStrs = portStrs.substring(0,portStrs.length()-1);
		}
		
		return portStrs;
	}
	
	public void setCardLayout(CardLayout cardLayout,JPanel cardPnl,VlanManagementView vlanMngView){
		this.cardLayout = cardLayout;
		this.cardPnl = cardPnl;
		this.vlanMngView = vlanMngView;
	}
	
	/**
	 * 下载操作
	 */
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载端口Vlan配置",role=Constants.MANAGERCODE)
	public void download(){
		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return ;
		}
		
		List<VlanPortConfig> list = new ArrayList<VlanPortConfig>();
		for(int i = 0 ; i < configTable.getRowCount(); i++){
			VlanPortConfig vlanPortConfig = (VlanPortConfig)configModel.getValueAt(i, configModel.getColumnCount());
			list.add(vlanPortConfig);
		}
		
		Task task = new DownLoadRequestTask(list, result);
		showConfigureMessageDialog(task, "下载");
	}
	
	private class DownLoadRequestTask implements Task{

		private List<VlanPortConfig> list = null;
		private int result = 0;
		public DownLoadRequestTask(List<VlanPortConfig> list, int result){
			this.list = list;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				if(0 != list.size()){
					//先存到数据库
					remoteServer.getNmsService().saveVlan(vlanBean, list,clientModel.getLocalAddress()
							,301,clientModel.getCurrentUser().getUserName());
				}
				remoteServer.getNmsService().saveVlan(vlanBean, list,clientModel.getLocalAddress()
						,result,clientModel.getCurrentUser().getUserName());
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载端口Vlan异常");
				statusFld.setText(dataStatus.get(Constants.ISSUEDADM).getKey());
				vlanMngView.refreshVlanTableView();
				LOG.error("VlanConfigView.DownLoadRequestTask error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				statusFld.setText(dataStatus.get(Constants.ISSUEDADM).getKey());
				vlanMngView.refreshVlanTableView();
			}
		}
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private JProgressBarDialog dialog;
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
	
	/**
	 * 返回操作
	 */
	@ViewAction(name=BACK, icon=ButtonConstants.RETURN,desc="返回VLAN ID新增视图",role=Constants.MANAGERCODE)
	public void back(){
		if (null == cardLayout){
			return;
		}
		cardLayout.show(cardPnl, VlanManagementView.LIST_CARD);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				vlanMngView.refreshAddVlanPortView();
			}
		});
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
	
	private String reverseTypeToString(int type){
		String str ="";
		if (type == Constants.TX){
			str = "电口";
		}
		else if (type == Constants.FX){
			str = "光口";
		}
		else if(type == Constants.PX){
			str = "PON";
		}
		return str;
	}
	
	private char reverseTag(String tag){
		char tagChar ;
		if (tag.equals("tag")){
			tagChar = 'T';
		}
		else{
			tagChar = 'U';
		}
		
		return tagChar;
	}

	class ButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if (e.getActionCommand().equals("toRightBtn")){
				toRightAction();
			}
			else if (e.getActionCommand().equals("toLeftBtn")){
				toLeftAction();
			}
		}
	}

	@SuppressWarnings("unchecked")
	class PortVlanTableModel extends AbstractTableModel{

		private List<List> dataList = new ArrayList<List>();
		private String[] columnName = null;
		
		protected PortVlanTableModel() {
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

	    @Override
		public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }

	    @Override
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
	  
	    	Object object = rowList.get(columnIndex);
	    	
	    	return object;
	    }

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (null == dataList || dataList.size() <1){
	    		return ;
	    	}
	    	List rowList = null;

	    	rowList = dataList.get(rowIndex);

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
	    
	    public void removeRow(int rows[]){
			int size = rows.length;
			List listStr = new ArrayList();
			for(int j = 0 ; j < size; j++){
				String value = (String)this.getValueAt(rows[j], 0);
				value = value + "," + (String)this.getValueAt(rows[j], 1);
				listStr.add(value);
			}
			
			for(int i = 0 ; i < listStr.size(); i++){
				String value = (String)listStr.get(i);
				String[] values = value.split(",");
				String ip = values[0];
				String port = values[1];
				for (int k = this.dataList.size()-1 ; k >=0; k--){
					if (this.dataList.get(k).get(0).toString().equals(ip) 
							&& this.dataList.get(k).get(1).toString().equals(port)){
						this.dataList.remove(k);
						break;
					}
				}
			}
			this.fireTableDataChanged();
		}
	}
}
