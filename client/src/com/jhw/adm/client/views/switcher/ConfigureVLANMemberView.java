package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.MODIFY;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

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
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.ConfigureVlanMemberModel;
import com.jhw.adm.client.model.switcher.VlanMemberPortModel;
import com.jhw.adm.client.model.switcher.VlanMemberTableModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.nets.VlanPortConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/*
 * 单个配置
 */

@Component(ConfigureVLANMemberView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureVLANMemberView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "configureVLANMemberView";
		
	//**************
	//valn配置面板
	private JPanel vlanConfigPnl = new JPanel();
	
	private JPanel vlanCfgTopPnl = new JPanel();
	private JLabel vlanIdLbl = new JLabel();
//	private JTextField vlanIdFld = new JTextField();
	//Amend 2010.06.03
	private NumberField vlanIdFld = new NumberField(4,0,1,4094,true);
	private JLabel vlanNameLbl = new JLabel();
	private JTextField vlanNameFld = new JTextField();
	
	private JScrollPane vlanCfgScrlPnl = new JScrollPane();
	private JTable portTable = null;
	private String[] portColumnName = null;
	
	
	private JToolBar vlanCfgBottomPnl = new JToolBar();
	private JButton addBtn ;
	private JButton modifyBtn ;
	private JButton saveBtn;
	private JButton synBtn;
	private JButton delBtn;
	
	//VLAN详细信息面板
	private JScrollPane detailScrollPnl = new JScrollPane();
	private JTable valnTable = new JTable();
	
	//保存和关闭按钮面板
	private JPanel bottomPnl = new JPanel();
	private JButton closeBtn = null;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private VlanConfig vlanConfig = null;
	
	private ButtonFactory buttonFactory;
	
	private static final Logger LOG = LoggerFactory.getLogger(ConfigureVLANMemberView.class);
	private MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(ConfigureVlanMemberModel.ID)
	private ConfigureVlanMemberModel configureVlanMemberModel;
	
	@Autowired
	@Qualifier(VlanMemberPortModel.ID)
	private VlanMemberPortModel vlanPortModel;
	
	@Autowired
	@Qualifier(VlanMemberTableModel.ID)
	private VlanMemberTableModel vlanTableModel;

	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		this.setLayout(new BorderLayout());
		initConfigPnl();
		initDetailPnl();
		initBottomPnl();
		
		this.add(vlanConfigPnl,BorderLayout.NORTH);
		this.add(detailScrollPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		//得到资源文件
		setResource();
	}
	private void initConfigPnl(){
		vlanCfgTopPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel container = new JPanel(new GridBagLayout());
		container.add(vlanIdLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		container.add(vlanIdFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		container.add(new StarLabel(),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		container.add(vlanNameLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
		container.add(vlanNameFld,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,5,0),0,0));
		vlanCfgTopPnl.add(container);
		
		portColumnName=new String[]{"端口","Tag","Untag"};
		vlanPortModel.setColumnName(portColumnName);
		/*********************/
//		configureVlanMemberModel.setPortDataList(initSwitchPortTable());
		/*********************/
		portTable = new JTable(vlanPortModel){
			public void tableChanged(TableModelEvent e) {
				super.tableChanged(e);        
				repaint();      
			} 
		};
		portTable.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());  
		portTable.getColumnModel().getColumn(0).setCellEditor(new CheckBoxEditor(new JCheckBox()));  
		portTable.getColumnModel().getColumn(1).setCellRenderer(new RadioButtonRenderer());    
		portTable.getColumnModel().getColumn(1).setCellEditor(new RadioButtonEditor(new JCheckBox()));   
		portTable.getColumnModel().getColumn(2).setCellRenderer(new RadioButtonRenderer());    
		portTable.getColumnModel().getColumn(2).setCellEditor(new RadioButtonEditor(new JCheckBox()));  

		vlanCfgScrlPnl.getViewport().add(portTable);
		vlanCfgScrlPnl.setPreferredSize(new Dimension(vlanCfgScrlPnl.getPreferredSize().width,149));
		
//		vlanCfgBottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		addBtn = buttonFactory.createButton(APPEND);
		modifyBtn = buttonFactory.createButton(MODIFY);
		saveBtn = buttonFactory.createButton(SAVE);
		delBtn = buttonFactory.createButton(DELETE);
		synBtn = buttonFactory.createButton(UPLOAD);
		vlanCfgBottomPnl.add(addBtn);
		vlanCfgBottomPnl.add(modifyBtn);
		vlanCfgBottomPnl.add(saveBtn);
		vlanCfgBottomPnl.add(delBtn);
//		vlanCfgBottomPnl.add(synBtn);
		vlanCfgBottomPnl.setFloatable(false);
		
		JToolBar toolbar = new JToolBar();
		toolbar.add(synBtn);
		toolbar.setFloatable(false);
		
		JPanel middlePnl = new JPanel(new BorderLayout());
		JPanel toolBarPnl = new JPanel(new BorderLayout());
		//Amend 2010.06.13
		int preferredHeight = synBtn.getPreferredSize().height - 4;
		int preferredWidth = 10;
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(preferredHeight, preferredWidth));
		toolBarPnl.add(vlanCfgBottomPnl,BorderLayout.WEST);
		separator.setPreferredSize(new Dimension(preferredHeight, preferredWidth));
		toolBarPnl.add(toolbar,BorderLayout.CENTER);
		middlePnl.add(toolBarPnl,BorderLayout.NORTH);
		middlePnl.add(vlanCfgTopPnl,BorderLayout.CENTER);
		
		vlanConfigPnl.setLayout(new BorderLayout());
		vlanConfigPnl.add(middlePnl,BorderLayout.NORTH);
		vlanConfigPnl.add(vlanCfgScrlPnl,BorderLayout.CENTER);
//		vlanConfigPnl.add(vlanCfgBottomPnl,BorderLayout.SOUTH);
		vlanConfigPnl.setBorder(BorderFactory.createTitledBorder(localizationManager
				.getString(ResourceConstants.VLANVIEW_VLANOPTION)));
	}
	
	private void initDetailPnl(){
		valnTable.setModel(vlanTableModel);
		valnTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				setDetailTableAction();
			}
		});
		
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(vlanTableModel);
		valnTable.setRowSorter(sorter);
		sorter.toggleSortOrder(0);
		
		detailScrollPnl.getViewport().add(valnTable);
		detailScrollPnl.setBorder(BorderFactory.createTitledBorder(localizationManager
				.getString(ResourceConstants.VLANVIEW_VLANDETAIL)));
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(closeBtn);
		
		this.setCloseButton(closeBtn);
	}

	private void setResource(){
		vlanIdFld.setColumns(20);
		vlanNameFld.setColumns(20);
		vlanNameFld.setDocument(new TextFieldPlainDocument(vlanNameFld));
		
		vlanIdLbl.setText(localizationManager.getString(ResourceConstants.VLANVIEW_VLANID));
		vlanNameLbl.setText(localizationManager.getString(ResourceConstants.VLANVIEW_VLANNAME));
		
		
		this.setEnable(false);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	@SuppressWarnings("unchecked")
	private List<List> initSwitchPortTable(){
		if (null == switchNodeEntity){
			return null;
		}
		
		//端口总数
		int portSum = switchNodeEntity.getPorts().size();
		List<List> list = new ArrayList<List>();
		for (int i = 0 ; i < portSum; i++){
			JCheckBox portBox = new JCheckBox("端口"+(i+1));
			JRadioButton tagBtn = new JRadioButton("");
			JRadioButton untagBtn = new JRadioButton("");
			
			portBox.setHorizontalAlignment(JCheckBox.CENTER);
			tagBtn.setHorizontalAlignment(AbstractButton.CENTER);
			untagBtn.setHorizontalAlignment(AbstractButton.CENTER);
			
			List rowList = new ArrayList();
			rowList.add(portBox );
			rowList.add(tagBtn);
			rowList.add(untagBtn);
			list.add(i, rowList);
		}
		return list;	
	}
	
	/**
	 * 清空控件中的值
	 */
	private  void clear(){
		vlanIdFld.setText("");
		vlanNameFld.setText("");
		configureVlanMemberModel.getVlanPortModel().clearPortStatus();
	}
	
	/**
	 * 设置输入控件是否可选
	 * @param enable
	 */
	private void setEnable(boolean enable){
		vlanIdFld.setEditable(enable);
		vlanNameFld.setEditable(enable);
		portTable.setEnabled(enable);
		vlanIdFld.setBackground(Color.WHITE);
		vlanNameFld.setBackground(Color.WHITE);
	}
	
	/**
	 *从服务器查询设备的vlan信息
	 */
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		configureVlanMemberModel.setPortDataList(initSwitchPortTable());
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<VlanConfig> vlanConfigList = (List<VlanConfig>)remoteServer.getService().findAll(VlanConfig.class, where, parms);
		if (null == vlanConfigList || vlanConfigList.size() <1){
			configureVlanMemberModel.setVlanDataList(null);
			return;
		}
		vlanConfig = vlanConfigList.get(0);
		
		configureVlanMemberModel.setVlanDataList(vlanConfig);
	}
	
	/**
	 * 新增按钮事件
	 */
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND, desc="增加VLAN",role=Constants.MANAGERCODE)
	public void append(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		clear();
		setEnable(true);
	}
	
	/**
	 * 修改按钮事件
	 */
	@ViewAction(name=MODIFY, icon=ButtonConstants.MODIFY, desc="修改VLAN",role=Constants.MANAGERCODE)
	public void modify(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		if (valnTable.getSelectedRowCount()<1){
			JOptionPane.showMessageDialog(this, "请选择修改的VLAN","提示",JOptionPane.NO_OPTION);
			return;
		}
		setEnable(true);
	}
	
	/**
	 * 保存按钮事件
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存VLAN",role=Constants.MANAGERCODE)
	public void save(){
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return ;
		}
		
		String vlanID = vlanIdFld.getText().trim();
		
		//当modifyRow的值不变，还是-1时，说明新增，否则为修改
		int modifyRow = -1;
		
		//得到vlan详细列表中的所有vlanID
		int rowCount = configureVlanMemberModel.getVlanTableModel().getRowCount();
		for (int row = 0 ; row < rowCount; row++){
			String vlan = configureVlanMemberModel.getVlanTableModel().getValueAt(row, 0).toString();
			if (vlan.equals(vlanID)){
				modifyRow = row;
				break;
			}else{
				continue;
			}
		}

		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		messageOfSaveProcessorStrategy.showInitializeDialog("保存", this);
		//增加新的Vlan
		if (-1 == modifyRow){
			VlanEntity vlanEntity = new VlanEntity();
			vlanEntity.setVlanID(NumberUtils.toInt(vlanID));
			vlanEntity.setPortConfig(getVlanPortConfigList());
			try {
				if (null == vlanConfig){
					vlanConfig = new VlanConfig();
					Set<VlanEntity> vlanEntityList = new LinkedHashSet<VlanEntity>();
					vlanEntityList.add(vlanEntity);
					vlanConfig.setSwitchNode(switchNodeEntity);
					vlanConfig.setVlanEntitys(vlanEntityList);
					//先增加VlanConfig
					remoteServer.getAdmService().saveAndSetting(macValue, MessageNoConstants.VLANSET, vlanConfig,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
					
					//再增加VlanEntity
					queryData();
					vlanEntity.setVlanConfig(vlanConfig);
					remoteServer.getAdmService().saveAndSetting(macValue, MessageNoConstants.VLANNEW, vlanEntity,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
				}
				else{
					vlanEntity.setVlanConfig(vlanConfig);
					remoteServer.getAdmService().saveAndSetting(macValue, MessageNoConstants.VLANNEW, vlanEntity,
							clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
				}
			} catch (JMSException e) {
				messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
				LOG.error("ConfigureVLANMemberView.save() failure:{}", e);
			}
		}else { //修改已经存在的Vlan
			int modelRow = valnTable.convertRowIndexToModel(valnTable.getSelectedRow());
			VlanEntity vlanEntity = (VlanEntity)configureVlanMemberModel.getVlanTableModel().getDataList().get(modelRow).get(4);
			vlanEntity.setPortConfig(getVlanPortConfigList());
		
			try {
				vlanEntity.setVlanConfig(vlanConfig);
				List<Serializable> list = new ArrayList<Serializable>();
				list.add(vlanEntity);
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.VLANUPDATE, list, 
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			} catch (JMSException e) {
				messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
				LOG.error("ConfigureVLANMemberView.save() failure:{}", e);
			}
		}
		if (result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
		clear();
		setEnable(false);
	}
	/**
	 * 删除按钮事件
	 */
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="删除VLAN",role=Constants.MANAGERCODE)
	public void delete(){
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		int row = valnTable.getSelectedRow();
		if (row < 0){
			return;
		}
		int modelRow = valnTable.convertRowIndexToModel(valnTable.getSelectedRow());
		if (modelRow <0){
			return;
		}
		
		String vlanString = configureVlanMemberModel.getVlanTableModel().getValueAt(modelRow, 0).toString();
		int vlan = NumberUtils.toInt(vlanString);
		if (1 == vlan){
			JOptionPane.showMessageDialog(this, "Vlan ID为1不能被删除","提示",JOptionPane.NO_OPTION);
			return ;
		}
		
		VlanEntity vlanEntity = (VlanEntity)configureVlanMemberModel.getVlanTableModel().getDataList().get(modelRow).get(4);
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		messageOfSaveProcessorStrategy.showInitializeDialog("删除", this);
		try {
			vlanEntity.setVlanConfig(vlanConfig);
			remoteServer.getAdmService().deleteAndSetting(macValue, MessageNoConstants.VLANDELETE, vlanEntity, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,Constants.SYN_ALL);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("删除出现异常");
			LOG.error("ConfigureVLANMemberView.delete() failure:{}", e);
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="向上同步VLAN信息",role=Constants.MANAGERCODE)
	public void upSynchronize(){

	}
	
	private Set<VlanPortConfig> getVlanPortConfigList(){
		Vector vector = configureVlanMemberModel.getTagPort();
		String[] tags = vector.get(0).toString().split(",");
		String[] untags = vector.get(1).toString().split(",");
		
		Set<VlanPortConfig> vlanPortConfigList = new HashSet<VlanPortConfig>();
		for (int i = 0 ; i< tags.length ; i++){
			if (tags[i].equals("")){
				continue;
			}
			VlanPortConfig vlanPortConfig = new VlanPortConfig();
			vlanPortConfig.setPortNo(Integer.parseInt(tags[i]));
			vlanPortConfig.setPortTag('T');
			vlanPortConfigList.add(vlanPortConfig);
		}
		for (int j = 0 ; j < untags.length; j++){
			if (untags[j].equals("")){
				continue;
			}
			VlanPortConfig vlanPortConfig = new VlanPortConfig();
			vlanPortConfig.setPortNo(Integer.parseInt(untags[j]));
			vlanPortConfig.setPortTag('U');
			vlanPortConfigList.add(vlanPortConfig);
		}
		
		return vlanPortConfigList;
	}
	
	/**
	 * vlan表格中被选择行的事件响应
	 * 根据所选择的Vlan，在端口表格中相应显示Tag和untag端口
	 */
	private void setDetailTableAction(){
		int row = valnTable.getSelectedRow();
		if (row < 0)
		{
			return;
		}
		int modelRow = valnTable.convertRowIndexToModel(row);
		if (modelRow < 0){
			return;
		}
		
		String vlanId = "";
		String vlanName = "";
		if (null != configureVlanMemberModel.getVlanTableModel().getValueAt(modelRow, 0)){
			vlanId = configureVlanMemberModel.getVlanTableModel().getValueAt(modelRow, 0).toString();
		}
		if (null != configureVlanMemberModel.getVlanTableModel().getValueAt(modelRow, 1)){
			vlanName = configureVlanMemberModel.getVlanTableModel().getValueAt(modelRow, 1).toString();
		}
		vlanIdFld.setText(vlanId);
		vlanNameFld.setText(vlanName);
		configureVlanMemberModel.setSelectVlanToPort(modelRow);
		
		this.setEnable(false);
	}
	
	/**
	 * 设备浏览器的时间监听
	 * 当选择不同的设备时，根据switchNodeEntity,重新查询数据库
	 * @author Administrator
	 *
	 */
	private PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
			else{
				clear();
				switchNodeEntity = null;
				configureVlanMemberModel.setPortDataList(null);
				configureVlanMemberModel.setVlanDataList(null);
			}
		}
	};
	
	/**
	 * radioButton的渲染器
	 * @author Administrator
	 *
	 */
	class RadioButtonRenderer implements TableCellRenderer {
		public java.awt.Component getTableCellRendererComponent(JTable table, Object value
				, boolean isSelected, boolean hasFocus, int row, int column){
			if (value==null) 
				return null;    
			return (java.awt.Component)value; 
		}
	} 

	class RadioButtonEditor extends DefaultCellEditor implements ItemListener {
		private JRadioButton button;   
		public RadioButtonEditor(JCheckBox checkBox) {
			super(checkBox);  
		}   
		public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if (value==null) return null;   
			button = (JRadioButton)value;   
			button.addItemListener(this);   
			return (java.awt.Component)value;  
		}   
		public Object getCellEditorValue() {
			button.removeItemListener(this);    
			return button;  
		}   
		public void itemStateChanged(ItemEvent e) {
			super.fireEditingStopped();
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if (null != portTable){
						vlanPortModel.fireTableDataChanged();
					}
				}
			});
		}
	} 

	class CheckBoxRenderer implements TableCellRenderer {
		public java.awt.Component getTableCellRendererComponent(JTable table, Object value
				, boolean isSelected, boolean hasFocus, int row, int column) { 
			if (value==null) 
				return null;    
			return (java.awt.Component)value; 
		}
	} 

	class CheckBoxEditor extends    DefaultCellEditor implements ItemListener {
		private JCheckBox button;   
		public CheckBoxEditor(JCheckBox checkBox) {
			super(checkBox);  
		}   
		public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if (value==null) return null;   
			button = (JCheckBox)value;   
			
			button.addItemListener(this);   
			return (java.awt.Component)value;  
		}   
		public Object getCellEditorValue() {
			button.removeItemListener(this);    
			return button;  
		}   
		public void itemStateChanged(ItemEvent e) {
			super.fireEditingStopped();
			clearSelect(e.getSource());
		}
	} 
	
	private void clearSelect(Object object){
		if (object instanceof JCheckBox){
			if(!((JCheckBox)object).isSelected()){
				configureVlanMemberModel.clearSelectPortStatus(object);
			}
			else{
				//默认port选择为untag
				configureVlanMemberModel.setDefaultPortSelected(object);
			}
			portTable.revalidate();
		}
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	public boolean isValids()
	{
		boolean isValid = true;
		
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == vlanIdFld.getText().trim() || "".equals(vlanIdFld.getText().trim())){
			JOptionPane.showMessageDialog(this, "VLAN ID错误，范围为：1-4094","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if ((NumberUtils.toInt(vlanIdFld.getText().trim()) < 1) || (NumberUtils
						.toInt(vlanIdFld.getText().trim()) > 4094)){
			JOptionPane.showMessageDialog(this, "VLAN ID错误，范围为：1-4094","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		return isValid;
	}
}