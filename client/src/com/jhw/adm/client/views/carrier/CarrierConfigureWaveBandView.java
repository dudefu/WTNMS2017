package com.jhw.adm.client.views.carrier;

import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
import com.jhw.adm.client.model.carrier.WaveBand;
import com.jhw.adm.client.swing.MessageOfCarrierConfigProcessorStrategy;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(CarrierConfigureWaveBandView.ID)
@Scope(Scopes.DESKTOP)
public class CarrierConfigureWaveBandView extends ViewPart{
	
	@PostConstruct
	protected void initialize() {
		setTitle("载波机波段设置");
		setViewSize(580, 420);
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
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, selectionChangedListener);
	}

	private void createContents(JPanel parent) {
		parent.setLayout(new BorderLayout());
		parent.setBorder(BorderFactory.createTitledBorder("波段列表"));

		table = new JXTable();
		tableModel = new WaveInfoTableModel();
		table.setModel(tableModel);
		table.setSortable(false);
		table.setColumnModel(tableModel.getColumnModel());

		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);

		parent.add(scrollTablePanel, BorderLayout.CENTER);
	}

	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="向上同步载波机波段信息",role=Constants.MANAGERCODE)
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
						MessageNoConstants.CARRIERWAVEBANDQUERY);
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
		if (carrierEntity == null){
			tableModel.setData(null);
		}
		else{
			tableModel.setData(carrierEntity);
		}
		
		tableModel.fireTableDataChanged();
	}

	@ViewAction(icon = ButtonConstants.SAVE, desc="保存载波机波段信息",role=Constants.MANAGERCODE)
	public void save() {
		if (null == carrierEntity){
			JOptionPane.showMessageDialog(this, "请选择载波机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		timeOutEditor.stopCellEditing();
		waveBandEditor.stopCellEditing();
		
		carrierEntity = tableModel.getData();
		int timeOut1 = carrierEntity.getTimeout1();
		int timeOut2 = carrierEntity.getTimeout2();
//		if (timeOut1 < 100 || timeOut2 < 100){
		if (timeOut1 < 100){
			JOptionPane.showMessageDialog(this, "超时时长范围是100-20000毫秒，请重新输入","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		showMessageDialog();
		try {
			remoteServer.getAdmService().updateAndSettingCarrier(MessageNoConstants.CARRIERWAVEBANDCONFIG,
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
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, selectionChangedListener);
	}

	private final PropertyChangeListener selectionChangedListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			tableModel.setData(null);
			tableModel.fireTableDataChanged();
			carrierEntity = (CarrierEntity)adapterManager.getAdapter(evt.getNewValue(), CarrierEntity.class);
			if (carrierEntity != null) {
				fillContents(carrierEntity);
			}
		}
	};
	
	private JXTable table;
	private WaveInfoTableModel tableModel;
	private CarrierEntity carrierEntity;
	private ButtonFactory buttonFactory;	
	private MessageSender messageSender;
	
	private TimeOutEditor timeOutEditor;
	private WaveBandEditor waveBandEditor;
	
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
	
	@Resource(name = WaveBand.ID)
	private WaveBand waveBand;

	private static final Logger LOG = LoggerFactory.getLogger(CarrierConfigureWaveBandView.class);
	private final MessageOfCarrierConfigProcessorStrategy messageOfCarrierProcessorStrategy = new MessageOfCarrierConfigProcessorStrategy();
	private static final long serialVersionUID = 1L;
	public static final String ID = "carrierConfigureWaveBandView";

	public class WaveInfoTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -3065768803494840568L;
		private final TableColumnModelExt columnModel;
		private CarrierEntity carrierEntity;
		
		public WaveInfoTableModel() {
			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			TableColumnExt acableColumn = new TableColumnExt(modelIndex++, 60);
			acableColumn.setIdentifier("电力口");
			acableColumn.setTitle("电力口");
			columnModel.addColumn(acableColumn);

			TableColumnExt waveBandColumn = new TableColumnExt(modelIndex++, 60);
			waveBandEditor = new WaveBandEditor();
			waveBandColumn.setCellEditor(waveBandEditor);
			waveBandColumn.setIdentifier("波段值");
			waveBandColumn.setTitle("波段值");
			columnModel.addColumn(waveBandColumn);
			
			TableColumnExt timeOutColumn = new TableColumnExt(modelIndex++, 60);
			timeOutEditor = new TimeOutEditor();
			timeOutColumn.setCellEditor(timeOutEditor);
			timeOutColumn.setIdentifier("超时时长(100-20000毫秒)");
			timeOutColumn.setTitle("超时时长(100-20000毫秒)");
			columnModel.addColumn(timeOutColumn);
		}

		public TableColumnModel getColumnModel() {
			return columnModel;
		}

		public void setData(CarrierEntity carrierEntity) {
			this.carrierEntity = carrierEntity;
		}

		public CarrierEntity getData() {
			return carrierEntity;
		}

		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public String getColumnName(int col) {
			return columnModel.getColumnExt(col).getTitle();
		}

		public Object getValueAt(int row, int col) {
			if (carrierEntity == null){
				return null;
			}
			Object value = null;
			switch (col) {
			case 0:
				if (row == 0){
					value = "Acable1";
				}
				else{
					value = "Acable2";
				}
				break;
			case 1:
				if (row == 0){
					value = waveBand.get(carrierEntity.getWaveBand1());
				}
				else{
					value = waveBand.get(carrierEntity.getWaveBand2());
				}
				break;
			case 2:
				if (row == 0){
					value = carrierEntity.getTimeout1();
				}
				else{
					value = carrierEntity.getTimeout2();
				}
				break;
			default:
				value = StringUtils.EMPTY;
			}

			return value;
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			if (column == 1) {
				if (row == 0){
					carrierEntity.setWaveBand1((Integer) value);
				}
				else{
					carrierEntity.setWaveBand2((Integer) value);
				}
			}
			else if (column == 2){
				if (value == null || "".equals(value)){
					value = "0";
				}
				if (row == 0){
					carrierEntity.setTimeout1(Integer.parseInt(String.valueOf(value)));
				}
				else{
					carrierEntity.setTimeout2(Integer.parseInt(String.valueOf(value)));
				}
			}
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			if (col == 0){
				return false;
			}
			else{
				return true;
			}
		}
		
		@Override
		public int getRowCount() {
			int row = 0;
			if (carrierEntity != null){
//				if (carrierEntity.getMarking() == Constants.SINGLECHANNEL){
//					row = 1;
//				}
//				else if (carrierEntity.getMarking() == Constants.DOUBLECHANNEL){
//					row = 2;
//				}
				row = 1;  //无论是单通道还是双通道，都只是显示一个电力口
			}
			return row;
		}
	}
	
	public class WaveBandEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

		private static final long serialVersionUID = 5280053050827922481L;
		JComboBox waveBandBox;
		int iWaveBand;
		
		public WaveBandEditor() {
			waveBandBox = new JComboBox();
			ListComboBoxModel model = new ListComboBoxModel(waveBand.toList());
			waveBandBox.setModel(model);
			waveBandBox.addActionListener(WaveBandEditor.this);
		}
		
		@Override
		public Object getCellEditorValue() {
			return iWaveBand;
		}
		
		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table,
		                            Object value,
		                            boolean isSelected,
		                            int row,
		                            int column) {
			iWaveBand = ((StringInteger)value).getValue();
			tableModel.setValueAt(iWaveBand, row, column);
			
			return waveBandBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			iWaveBand = ((StringInteger)waveBandBox.getSelectedItem()).getValue();
		}
	}
	
	public class TimeOutEditor extends AbstractCellEditor implements TableCellEditor {

		private static final long serialVersionUID = 5280053050827922481L;
		NumberField timeOutFld;
		int iTimeOut = 0;
		
		public TimeOutEditor() {
			timeOutFld = new NumberField(5, 0, 1, 20000, true);
			
			timeOutFld.addKeyListener(new KeyAdapter(){
				@Override
				public void keyReleased(KeyEvent e) {
					iTimeOut = Integer.parseInt(timeOutFld.getText().trim());
				}
			});
		}
		
		@Override
		public Object getCellEditorValue() {
			return iTimeOut;
		}
		
		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table,
		                            Object value,
		                            boolean isSelected,
		                            int row,
		                            int column) {
			if(value != null && !"".equals(String.valueOf(value))){
				iTimeOut = Integer.parseInt(String.valueOf(value));
			}
			tableModel.setValueAt(iTimeOut, row, column);
			
			return timeOutFld;
		}
	}
}