package com.jhw.adm.client.views.carrier;

import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.model.carrier.CarrierCategory;
import com.jhw.adm.client.model.carrier.CarrierPort;
import com.jhw.adm.client.model.carrier.CarrierPortBaudRate;
import com.jhw.adm.client.model.carrier.CarrierPortCategory;
import com.jhw.adm.client.model.carrier.CarrierPortDataBit;
import com.jhw.adm.client.model.carrier.CarrierPortParity;
import com.jhw.adm.client.model.carrier.CarrierPortStopBit;
import com.jhw.adm.client.model.carrier.CarrierPortSubnet;
import com.jhw.adm.client.swing.MessageOfCarrierConfigProcessorStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierPortEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 载波机端口设置视图
 */
@Component(CarrierConfigurePortView.ID)
@Scope(Scopes.DESKTOP)
public class CarrierConfigurePortView extends ViewPart {
	
	@PostConstruct
	protected void initialize() {
		setTitle("载波机端口设置");
		setViewSize(640, 480);
		setLayout(new BorderLayout());
		messageSender = remoteServer.getMessageSender();
		buttonFactory = actionManager.getButtonFactory(this);

		JPanel toolPanel = new JPanel(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());

		JButton synBtn = buttonFactory.createButton(UPLOAD);
		newPanel.add(synBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(buttonFactory.createSaveButton(),new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		JButton closeButton = buttonFactory.createCloseButton(); 
		newPanel.add(closeButton,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		setCloseButton(closeButton);
		toolPanel.add(newPanel, BorderLayout.EAST);

		JPanel content = new JPanel();
		createContents(content);

		add(content, BorderLayout.CENTER);
		add(toolPanel, BorderLayout.PAGE_END);
		carrierEntity = (CarrierEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), CarrierEntity.class);

		fillContents(carrierEntity);
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, SelectionChangedListener);
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				List<List> list = CarrierTools.readXml();
				CarrierPortSubnet.setList(list);
			}
		});
	}

	private void createContents(JPanel parent) {
		parent.setLayout(new BorderLayout());
		parent.setBorder(BorderFactory.createTitledBorder("端口列表"));

		table = new JXTable();
		tableModel = new PortInfoTableModel();
		table.setModel(tableModel);
		table.setSortable(false);
		table.setColumnModel(tableModel.getColumnModel());

		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);

		JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		portCountField = new JTextField(10);
		portCountField.setEditable(false);
		infoPanel.add(new JLabel("端口数量"));
		infoPanel.add(portCountField);

		parent.add(scrollTablePanel, BorderLayout.CENTER);
		parent.add(infoPanel, BorderLayout.PAGE_END);
	}
	
	private void showAlert(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="向上同步载波机端口设置信息",role=Constants.MANAGERCODE)
	public void upSynchronize() {
		if (carrierEntity == null) {
			JOptionPane.showMessageDialog(this, "请选择载波机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		List<CarrierEntity> listOfCarrier = remoteServer.getService().queryCarrierEntity();
		if (listOfCarrier == null) {
			JOptionPane.showMessageDialog(this, "请先设置一台中心载波机","提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (listOfCarrier.size() > 1) {
			JOptionPane.showMessageDialog(this, "中心载波机只能有一台","提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		showQueryMessageDialog();
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.CARRIERPORTQUERY);
				message.setObject(carrierEntity);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				return message;
			}
		});
	}
	
	private void showQueryMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("向上同步", this);
	}
	
	public void fillContents(CarrierEntity carrierEntity) {
		List<CarrierPortEntity> listOfPortInfo = Collections.emptyList();
		
		int portCount = 0;
		
		if (carrierEntity == null) {
			//
		} else {
//			if (listOfPortInfo.size() > 0){
				listOfPortInfo = sortPort(carrierEntity.getPorts());
				int marking = carrierEntity.getMarking();
				if (marking == Constants.SINGLECHANNEL){
					portCount = listOfPortInfo.size() - 1;
				}
				else{
					portCount = listOfPortInfo.size();
				}
//			}
		}
		
		portCountField.setText(Integer.toString(portCount));
		tableModel.setData(listOfPortInfo);
		tableModel.fireTableDataChanged();
	}
	
	private List<CarrierPortEntity> sortPort(Set<CarrierPortEntity> setOfPort) {
		if (setOfPort == null) {
			return Collections.emptyList();
		}
		
		CarrierPortEntity[] portData = new CarrierPortEntity[setOfPort.size()];
		int index = 0;
		for (CarrierPortEntity portEntity : setOfPort) {
			portData[index++] = portEntity;
		}
		
		selectionSort(portData);
		List<CarrierPortEntity> result = new ArrayList<CarrierPortEntity>(portData.length);
		for (int i = 0; i < portData.length; i++) {
			result.add(portData[i]);
		}
		
		return result;
	}
	
	private void selectionSort(CarrierPortEntity[] data) {
		for (int out = 0; out < data.length; out++) {
			int min = out;
			for (int in = out + 1; in < data.length; in++) {
				if (data[in].getPortCode() < data[min].getPortCode()) {
					min = in;
				}
			}
			
			if (min != out) {
				CarrierPortEntity temp = data[out];
				data[out] = data[min];
				data[min] = temp;
			}
		}
	}

	@ViewAction(icon = ButtonConstants.SAVE, desc="保存载波机端口设置信息",role=Constants.MANAGERCODE)
	public void save() {
		if (null == carrierEntity){
			JOptionPane.showMessageDialog(this, "请选择载波机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		boolean isSingleConfigPort = hasSingleConfigPort(tableModel.getData());
		if (!isSingleConfigPort){
			showAlert("载波设备最多只允许设置一个配置口，请先把不用的端口设置为备用端口");
			return;
		}
		
		fillCarrierPorts();
		
		showMessageDialog();
		try {
			remoteServer.getAdmService().updateAndSettingCarrier(MessageNoConstants.CARRIERPORTCONFIG,
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), carrierEntity, result);
		} catch (JMSException e) {
			messageOfCarrierProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("updateAndSettingCarrier error", e);
		}
		
		if (result == Constants.SYN_SERVER){
			messageOfCarrierProcessorStrategy.showNormalMessage("S");
		}
	}
	
	private void showMessageDialog(){
		messageOfCarrierProcessorStrategy.showInitializeDialog("保存", this);
	}
	
	private void fillCarrierPorts() {
		if (carrierEntity != null) {
			Set<CarrierPortEntity> setOfPort = new HashSet<CarrierPortEntity>();
			
			for (CarrierPortEntity port : tableModel.getData()) {
				setOfPort.add(port);
			}
			carrierEntity.setPorts(setOfPort);
		}
	}
	
	/**
	 * 判断是否只有一个配置口
	 * @param portInfoList
	 * @return
	 */
	private boolean hasSingleConfigPort(List<CarrierPortEntity> portInfoList){
		int count = 0;
		for(CarrierPortEntity portInfo : portInfoList){
			int configPort = carrierPortCategory.Config.getValue(); //配置口
			int type = portInfo.getPortType();
			if (configPort == type){
				count++;
			}
		}
		
		if (count > 1){
			return false;
		}
		return true;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, SelectionChangedListener);
	}

	private final PropertyChangeListener SelectionChangedListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			carrierEntity = (CarrierEntity)adapterManager.getAdapter(evt.getNewValue(), CarrierEntity.class);
			if (carrierEntity != null) {
				fillContents(carrierEntity);
			}
		}
	};
	
	private JXTable table;
	private PortInfoTableModel tableModel;
	private JTextField portCountField;
	private CarrierEntity carrierEntity;
	private ButtonFactory buttonFactory;	
	private MessageSender messageSender;
	
	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name = DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	@Resource(name = CarrierCategory.ID)
	private CarrierCategory carrierCategory;

	@Resource(name = CarrierPort.ID)
	private CarrierPort carrierPort;

	@Resource(name = CarrierPortBaudRate.ID)
	private CarrierPortBaudRate carrierPortBaudRate;

	@Resource(name = CarrierPortCategory.ID)
	private CarrierPortCategory carrierPortCategory;
	
	@Resource(name = CarrierPortParity.ID)
	private CarrierPortParity carrierPortParity;
	
	
	@Resource(name = CarrierPortDataBit.ID)
	private CarrierPortDataBit carrierPortDataBit;
	
	@Resource(name = CarrierPortStopBit.ID)
	private CarrierPortStopBit carrierPortStopBit;
	
	@Resource(name = CarrierPortSubnet.ID)
	private CarrierPortSubnet carrierPortSubnet;
	

	private static final Logger LOG = LoggerFactory.getLogger(CarrierConfigurePortView.class);
	private final MessageOfCarrierConfigProcessorStrategy messageOfCarrierProcessorStrategy = new MessageOfCarrierConfigProcessorStrategy();
	private static final long serialVersionUID = 1L;
	public static final String ID = "carrierConfigurePortView";

	public class PortInfoTableModel extends AbstractTableModel {

		public PortInfoTableModel() {
			listOfPortInfo = Collections.emptyList();

			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			TableColumnExt numberColumn = new TableColumnExt(modelIndex++, 60);
			numberColumn.setIdentifier("端口号");
			numberColumn.setTitle("端口号");
			columnModel.addColumn(numberColumn);

			TableColumnExt categoryColumn = new TableColumnExt(modelIndex++, 60);
			categoryColumn.setIdentifier("类型");
			categoryColumn.setTitle("类型");

			PortCategoryEditor portCategoryEditor = new PortCategoryEditor();
			portCategoryEditor.setData(listOfPortInfo);
			categoryColumn.setCellEditor(portCategoryEditor);
			columnModel.addColumn(categoryColumn);

			TableColumnExt baudRateColumn = new TableColumnExt(modelIndex++, 60);
			PortBaudRateEditor baunRateEditor = new PortBaudRateEditor();
			baudRateColumn.setCellEditor(baunRateEditor);
			baudRateColumn.setIdentifier("波特率");
			baudRateColumn.setTitle("波特率");
			columnModel.addColumn(baudRateColumn);
			
			TableColumnExt parityColumn = new TableColumnExt(modelIndex++, 60);
			PortParityEditor parityEditor = new PortParityEditor();
			parityColumn.setCellEditor(parityEditor);
			parityColumn.setIdentifier("校验位");
			parityColumn.setTitle("校验位");
			columnModel.addColumn(parityColumn);
			
			TableColumnExt dataBitColumn = new TableColumnExt(modelIndex++, 60);
			PortDataBitEditor dataBitEditor = new PortDataBitEditor();
			dataBitColumn.setCellEditor(dataBitEditor);
			dataBitColumn.setIdentifier("数据位");
			dataBitColumn.setTitle("数据位");
			columnModel.addColumn(dataBitColumn);
			
			
			TableColumnExt stopBitColumn = new TableColumnExt(modelIndex++, 60);
			PortStopBitEditor stopBitEditor = new PortStopBitEditor();
			stopBitColumn.setCellEditor(stopBitEditor);
			stopBitColumn.setIdentifier("停止位");
			stopBitColumn.setTitle("停止位");
			columnModel.addColumn(stopBitColumn);
			
			TableColumnExt subnetColumn = new TableColumnExt(modelIndex++, 60);
			PortSubnetEditor subnetEditor = new PortSubnetEditor();
			subnetColumn.setCellEditor(subnetEditor);
			subnetColumn.setIdentifier("子网编号");
			subnetColumn.setTitle("子网编号");
			columnModel.addColumn(subnetColumn);
		}

		public TableColumnModel getColumnModel() {
			return columnModel;
		}

		public void addPort(CarrierPortEntity portInfo) {
			listOfPortInfo.add(portInfo);
		}

		public void removePortInfo(int index) {
			listOfPortInfo.remove(index);
		}

		public void setData(List<CarrierPortEntity> listOfPortInfo) {
			this.listOfPortInfo = listOfPortInfo;
		}

		public CarrierPortEntity getPortInfo(int index) {
			return listOfPortInfo.get(index);
		}

		public List<CarrierPortEntity> getData() {
			return listOfPortInfo;
		}

		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			if (listOfPortInfo == null || listOfPortInfo.size() < 1){
				return 0;
			}
			int marking = carrierEntity.getMarking();
			if (marking == Constants.SINGLECHANNEL){
				return listOfPortInfo.size() - 1;
			}
			else{
				return listOfPortInfo.size();
			}
		}

		@Override
		public String getColumnName(int col) {
			return columnModel.getColumnExt(col).getTitle();
		}

		public Object getValueAt(int row, int col) {
			if (listOfPortInfo == null)
				return null;
			CarrierPortEntity portInfo = listOfPortInfo.get(row);
			Object value = null;
			int marking = carrierEntity.getMarking();
			if (row == 6 && marking == Constants.SINGLECHANNEL){//单通道载波机没有第7行电力口2的数据
        		return null;
        	}
			
			switch (col) {
			case 0:
				if(row == 5 && marking == Constants.SINGLECHANNEL){//单通道载波机第6行的"Acable1"改为"Acable"
        			value = "Acable";
        		}
        		else{
        			value = carrierPort.get(portInfo.getPortCode()).getKey();
        		}
				break;
			case 1:
				value = carrierPortCategory.get(portInfo.getPortType());
				break;
			case 2:
				value = carrierPortBaudRate.get(portInfo.getBaudRate());
				break;
			case 3:
				value = carrierPortParity.get(portInfo.getVerify());
				break;
			case 4:
				value = carrierPortDataBit.get(portInfo.getDataBit());
				break;
			case 5:
				value = carrierPortStopBit.get(portInfo.getStopBit());
				break;
			case 6:
				if ((row == 5 || row == 6)){
        			value = "";
        		}
        		else{
        			value = carrierPortSubnet.get(portInfo.getSubnetCode()); 
        		}
				break;	
			default:
				value = StringUtils.EMPTY;
			}

			return value;
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			CarrierPortEntity portInfo = listOfPortInfo.get(row);
			if (column == 1) {
				int selectedPortCategory = (Integer) value;
				portInfo.setPortType(selectedPortCategory);
			}
			if (column == 2) {
				portInfo.setBaudRate((Integer) value);
			}
			if (column == 3) {
				portInfo.setVerify((Integer) value);
			}
			if (column == 4) {
				portInfo.setDataBit((Integer) value);
			}
			if (column == 5){
				portInfo.setStopBit((Integer) value);
			}
			if (column == 6){
				if ((row == 5 || row == 6)){
        			portInfo.setSubnetCode(0);
        		}
        		else{
        			portInfo.setSubnetCode((Integer) value);
        		}
			}
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			CarrierPortEntity portInfo = listOfPortInfo.get(row);
			// 类型，波特率可以修改
			// 电力口，配置口不可以修改
			 if (col == 1 || col == 2 
					 || col == 3 || col == 4 || col == 5 || col == 6) {
				 if (portInfo.getPortType() == carrierPortCategory.Power.getValue()){
//						 || portInfo.getPortType() == carrierPortCategory.Config.getValue()) {
					 return false;
				 } else {
					 return true;
				 }
			 } else {
				 return false;
			 }
		}

		private List<CarrierPortEntity> listOfPortInfo;
		private final TableColumnModelExt columnModel;
		private static final long serialVersionUID = -3065768803494840568L;
	}
	
	public class PortCategoryEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        
        public boolean isMaster() {
        	return carrierEntity.getType() == carrierCategory.MASTER.getValue();
		}

		public void setData(List<CarrierPortEntity> listOfPortInfo) {
			this.listOfPortInfo = listOfPortInfo;
		}
		
		public PortCategoryEditor() {
			portCategoryBox = new JComboBox();
			List<StringInteger> listOfCategory = new ArrayList<StringInteger>();
			listOfCategory.add(carrierPortCategory.Up);
			listOfCategory.add(carrierPortCategory.Config);
			listOfCategory.add(carrierPortCategory.Transport);
			listOfCategory.add(carrierPortCategory.Local);
			listOfCategory.add(carrierPortCategory.Standby);
			
			ListComboBoxModel model = new ListComboBoxModel(listOfCategory);
			portCategoryBox.setModel(model);
			portCategoryBox.addActionListener(PortCategoryEditor.this);
		}
		
		@Override
		public Object getCellEditorValue() {
			return selectedCategory;
		}

		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			selectedCategory = ((StringInteger)value).getValue();
			tableModel.setValueAt(selectedCategory, row, column);
			
			return portCategoryBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int category = ((StringInteger)portCategoryBox.getSelectedItem()).getValue();

//    		if (category == carrierPortCategory.Up.getValue() && hasUpPort() && isMaster()) {
//    			showAlert("中心载波机只允许设置一个上联数据端口");
//    		} else if (category == carrierPortCategory.Local.getValue() && isMaster()) {
//    			showAlert("中心载波机不允许设下接设备端口");
//    		}  else if (category == carrierPortCategory.Up.getValue() && !isMaster()) {
//    			showAlert("终端载波机不允许设上联数据端口");
//    		} else {
        		selectedCategory = category;
//    		}
		}
        
		// 是否存在上联端口
        private boolean hasUpPort() {
        	boolean result = false;
        	for (CarrierPortEntity pi : listOfPortInfo) {
        		if (pi.getPortType() == carrierPortCategory.Up.getValue()) {
        			result = true;
        			break;
        		}
        	}
        	return result;
        }
        
		private static final long serialVersionUID = 3520964663527539313L;
		JComboBox portCategoryBox;
		int selectedCategory;

        private List<CarrierPortEntity> listOfPortInfo;
	}
	
	public class PortBaudRateEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

		private static final long serialVersionUID = 5280053050827922478L;
		JComboBox portBaudRateBox;
		int selectedBaudRate;
		
		public PortBaudRateEditor() {
			portBaudRateBox = new JComboBox();
			ListComboBoxModel model = new ListComboBoxModel(carrierPortBaudRate.toList());
			portBaudRateBox.setModel(model);
			portBaudRateBox.addActionListener(PortBaudRateEditor.this);
		}
		
		@Override
		public Object getCellEditorValue() {
			return selectedBaudRate;
		}
		
		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table,
		                            Object value,
		                            boolean isSelected,
		                            int row,
		                            int column) {
			selectedBaudRate = ((StringInteger)value).getValue();
			tableModel.setValueAt(selectedBaudRate, row, column);
			
			return portBaudRateBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			selectedBaudRate = ((StringInteger)portBaudRateBox.getSelectedItem()).getValue();
		}
	}
	
	public class PortParityEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

		private static final long serialVersionUID = 5280053050827922479L;
		JComboBox portParityBox;
		int selectedParity;
		
		public PortParityEditor() {
			portParityBox = new JComboBox();
			ListComboBoxModel model = new ListComboBoxModel(carrierPortParity.toList());
			portParityBox.setModel(model);
			portParityBox.addActionListener(PortParityEditor.this);
		}
		
		@Override
		public Object getCellEditorValue() {
			return selectedParity;
		}
		
		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table,
		                            Object value,
		                            boolean isSelected,
		                            int row,
		                            int column) {
			selectedParity = ((StringInteger)value).getValue();
			tableModel.setValueAt(selectedParity, row, column);
			
			return portParityBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			selectedParity = ((StringInteger)portParityBox.getSelectedItem()).getValue();
		}
	}
	
	public class PortDataBitEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

		private static final long serialVersionUID = 5280053050827922480L;
		JComboBox portDataBitBox;
		int selectedDataBit;
		
		public PortDataBitEditor() {
			portDataBitBox = new JComboBox();
			ListComboBoxModel model = new ListComboBoxModel(carrierPortDataBit.toList());
			portDataBitBox.setModel(model);
			portDataBitBox.addActionListener(PortDataBitEditor.this);
		}
		
		@Override
		public Object getCellEditorValue() {
			return selectedDataBit;
		}
		
		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table,
		                            Object value,
		                            boolean isSelected,
		                            int row,
		                            int column) {
			selectedDataBit = ((StringInteger)value).getValue();
			tableModel.setValueAt(selectedDataBit, row, column);
			
			return portDataBitBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			selectedDataBit = ((StringInteger)portDataBitBox.getSelectedItem()).getValue();
		}
	}
	
	public class PortStopBitEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

		private static final long serialVersionUID = 5280053050827922481L;
		JComboBox portStopBitBox;
		int selectedStopBit;
		
		public PortStopBitEditor() {
			portStopBitBox = new JComboBox();
			ListComboBoxModel model = new ListComboBoxModel(carrierPortStopBit.toList());
			portStopBitBox.setModel(model);
			portStopBitBox.addActionListener(PortStopBitEditor.this);
		}
		
		@Override
		public Object getCellEditorValue() {
			return selectedStopBit;
		}
		
		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table,
		                            Object value,
		                            boolean isSelected,
		                            int row,
		                            int column) {
			selectedStopBit = ((StringInteger)value).getValue();
			tableModel.setValueAt(selectedStopBit, row, column);
			
			return portStopBitBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			selectedStopBit = ((StringInteger)portStopBitBox.getSelectedItem()).getValue();
		}
	}
	
	public class PortSubnetEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
		private static final long serialVersionUID = 5280053050827922481L;
		JComboBox portSubnetCombox;
		int selectedSubnet;
		ListComboBoxModel model;
		
		public PortSubnetEditor() {
			portSubnetCombox = new JComboBox();
			model = new ListComboBoxModel(carrierPortSubnet.toList());
			portSubnetCombox.setModel(model);
			portSubnetCombox.addActionListener(PortSubnetEditor.this);
			
			java.awt.Component button = portSubnetCombox.getComponent(0);
			button.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent arg0) {
					model = new ListComboBoxModel(carrierPortSubnet.toList());
					portSubnetCombox.setModel(model);
				}
			});
		}
		
		@Override
		public Object getCellEditorValue() {
			return selectedSubnet;
		}
		
		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table,
		                            Object value,
		                            boolean isSelected,
		                            int row,
		                            int column) {
			if ("".equals(value)){
				selectedSubnet = 0;
			}
			else{
				selectedSubnet = ((StringInteger)value).getValue();
			}
			
			tableModel.setValueAt(selectedSubnet, row, column);
			return portSubnetCombox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			selectedSubnet = ((StringInteger)portSubnetCombox.getSelectedItem()).getValue();
		}
	}
}