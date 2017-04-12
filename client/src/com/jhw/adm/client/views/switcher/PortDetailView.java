package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.table.TableColumnExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ActionConstants;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.AlarmEvents;
import com.jhw.adm.client.model.AlarmMessage;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.SwitchPortCategory;
import com.jhw.adm.client.model.SwitcherPortConnectCategory;
import com.jhw.adm.client.model.SwitcherPortModeCategory;
import com.jhw.adm.client.model.SwitcherPortWorkCategory;
import com.jhw.adm.client.swing.JCloseButton;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.WarningType;

/**
 * 交换机端口详细信息，包括端口的基本信息，告警信息。
 */
@Component(PortDetailView.ID)
@Scope(Scopes.DESKTOP)
public class PortDetailView extends ViewPart {

	@PostConstruct
	protected void initialize() {
		setTitle("端口详细信息");
		setViewSize(640, 480);
		setLayout(new BorderLayout());
		buttonFactory = actionManager.getButtonFactory(this);
		JPanel infoPanel = new JPanel();
		createInfoPanel(infoPanel);
		JPanel alarmPanel = new JPanel();
		createAlarmPanel(alarmPanel);
		JPanel detailPanel = new JPanel();
		createDetailPanel(detailPanel);
		add(infoPanel, BorderLayout.NORTH);
//		add(alarmPanel, BorderLayout.CENTER);
//		add(detailPanel, BorderLayout.SOUTH);

		selectedPort = (SwitchPortEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchPortEntity.class);
		if (alarmModel.getLastAlarms() != null && alarmModel.getLastAlarms().size() > 0) {
			alarmArrival(alarmModel.getLastAlarms());
		}
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, SelectionChangedLinstener);
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_ARRIVAL, AlarmArrivalListener);
	}
	
	private void alarmArrival(List<AlarmMessage> alarms) {
		if (selectedPort == null) {
			return;
		}
		List<AlarmMessage> thisPortAlarms = new ArrayList<AlarmMessage>();
		
		for (AlarmMessage trapWarning : alarms) {
			String ipValue = selectedPort.getSwitchNode().getBaseConfig().getIpValue();
			if (ipValue.equals(trapWarning.getIpValue()) && 
				selectedPort.getPortNO() == trapWarning.getPortNo()) {
				thisPortAlarms.add(trapWarning);
			}
		}
		
		tableModel.setAlarms(thisPortAlarms);
		tableModel.fireTableDataChanged();
		if (thisPortAlarms.size() > 0) {
			table.setRowSelectionInterval(0, 0);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, SelectionChangedLinstener);
		alarmModel.removePropertyChangeListener(AlarmModel.ALARM_ARRIVAL, AlarmArrivalListener);
	}
	
	private void fillContents(SwitchPortEntity switchPortEntity) {
		if (switchPortEntity == null) {
			return;
		}
		int portNumber = switchPortEntity.getPortNO();
		boolean worked = switchPortEntity.isWorked();//状态
		boolean connected = switchPortEntity.isConnected();//连接状态
		int type = switchPortEntity.getType(); //类型
		String currentMode = switchPortEntity.getCurrentMode().toLowerCase();//当前模式
		String configMode =  switchPortEntity.getConfigMode().toLowerCase(); //配置模式
		boolean flowControl = switchPortEntity.isFlowControl(); //流控
		String abandonSetting = switchPortEntity.getAbandonSetting(); //丢弃
		
		String vendorname  = switchPortEntity.getVendorname();
		String vendorPN  = switchPortEntity.getVendorPN();
		String vendorrev  = switchPortEntity.getVendorrev();
		String vendorSN  = switchPortEntity.getVendorSN();
		String datecode  = switchPortEntity.getDatecode();
		String bRNominal  = switchPortEntity.getBRNominal();
		String wavelength  = switchPortEntity.getWavelength();
		String transMedia  = switchPortEntity.getTransMedia();
		String lengthSM  = switchPortEntity.getLengthSM();
		String temperature  = switchPortEntity.getTemperature();
		String voltage  = switchPortEntity.getVoltage();
		String txBias  = switchPortEntity.getTxBias();
		String txPower  = switchPortEntity.getTxPower();
		String rxPower  = switchPortEntity.getRxPower();
		String temperatureHighAlarm  = switchPortEntity.getTemperatureHighAlarm();
		String temperatureLowAlarm  = switchPortEntity.getTemperatureLowAlarm();
		String temperatureHighWarning  = switchPortEntity.getTemperatureHighWarning();
		String temperatureLowWarning  = switchPortEntity.getTemperatureLowWarning();
		String voltageHighAlarm  = switchPortEntity.getVoltageHighAlarm();
		String voltageLowAlarm  = switchPortEntity.getVoltageLowAlarm();
		String voltageHighWarning  = switchPortEntity.getVoltageHighWarning();
		String voltageLowWarning  = switchPortEntity.getVoltageLowWarning();
		String txBiasHighAlarm  = switchPortEntity.getTxBiasHighAlarm();
		String txBiasLowAlarm  = switchPortEntity.getTxBiasLowAlarm();
		String txBiasHighWarning  = switchPortEntity.getTxBiasHighWarning();
		String txBiasLowWarning  = switchPortEntity.getTxBiasLowWarning();
		String txPowerHighAlarm  = switchPortEntity.getTxPowerHighAlarm();
		String txPowerLowAlarm  = switchPortEntity.getTxPowerLowAlarm();
		String txPowerHighWarning  = switchPortEntity.getTxPowerHighWarning();
		String txPowerLowWarning  = switchPortEntity.getTxPowerLowWarning();
		String rxPowerHighAlarm  = switchPortEntity.getRxPowerHighAlarm();
		String rxPowerLowAlarm  = switchPortEntity.getRxPowerLowAlarm();
		String rxPowerHighWarning  = switchPortEntity.getRxPowerHighWarning();
		String rxPowerLowWarning  = switchPortEntity.getRxPowerLowWarning();
		
		portNumberField.setText(Integer.toString(portNumber)); //端口
		statusField.setText(switcherPortWorkCategory.get(worked).getTail());//状态
		connectionStatusField.setText(switcherPortConnectCategory.get(connected).getTail());//连接状态
		typeField.setText(switcherPortCategory.get(type).getKey());//类型		
		modeField.setText(switcherPortModeCategory.get(currentMode).getTail());//当前模式
		configModeField.setText(switcherPortModeCategory.get(configMode).getTail());//配置模式
		flowControlField.setText(switcherPortWorkCategory.get(flowControl).getTail());//流控
		discardField.setText(abandonSetting);//丢弃
		
		
		if(0 == switchPortEntity.getType()){
			vendornameField.setText(vendorname);
			vendorPNField.setText(vendorPN);
			vendorrevField.setText(vendorrev);
			vendorSNField.setText(vendorSN);
			datecodeField.setText(datecode);
			bRNominalField.setText(bRNominal);
			wavelengthField.setText(wavelength);
			transMediaField.setText(transMedia);
			lengthSMField.setText(lengthSM);
			temperatureField.setText(temperature);
			voltageField.setText(voltage);
			txBiasField.setText(txBias);
			txPowerField.setText(txPower);
			rxPowerField.setText(rxPower);
			temperatureHighAlarmField.setText(temperatureHighAlarm);
			temperatureLowAlarmField.setText(temperatureLowAlarm);
			temperatureHighWarningField.setText(temperatureHighWarning);
			temperatureLowWarningField.setText(temperatureLowWarning);
			voltageHighAlarmField.setText(voltageHighAlarm);
			voltageLowAlarmField.setText(voltageLowAlarm);
			voltageHighWarningField.setText(voltageHighWarning);
			voltageLowWarningField.setText(voltageLowWarning);
			txBiasHighAlarmField.setText(txBiasHighAlarm);
			txBiasLowAlarmField.setText(txBiasLowAlarm);
			txBiasHighWarningField.setText(txBiasHighWarning);
			txBiasLowWarningField.setText(txBiasLowWarning);
			txPowerHighAlarmField.setText(txPowerHighAlarm);
			txPowerLowAlarmField.setText(txPowerLowAlarm);
			txPowerHighWarningField.setText(txPowerHighWarning);
			txPowerLowWarningField.setText(txPowerLowWarning);
			rxPowerHighAlarmField.setText(rxPowerHighAlarm);
			rxPowerLowAlarmField.setText(rxPowerLowAlarm);
			rxPowerHighWarningField.setText(rxPowerHighWarning);
			rxPowerLowWarningField.setText(rxPowerLowWarning);
			
			vendornameField.setVisible(true);
			vendorPNField.setVisible(true);
			vendorrevField.setVisible(true);
			vendorSNField.setVisible(true);
			datecodeField.setVisible(true);
			bRNominalField.setVisible(true);
			wavelengthField.setVisible(true);
			transMediaField.setVisible(true);
			lengthSMField.setVisible(true);
			temperatureField.setVisible(true);
			voltageField.setVisible(true);
			txBiasField.setVisible(true);
			txPowerField.setVisible(true);
			rxPowerField.setVisible(true);
			temperatureHighAlarmField.setVisible(true);
			temperatureLowAlarmField.setVisible(true);
			temperatureHighWarningField.setVisible(true);
			temperatureLowWarningField.setVisible(true);
			voltageHighAlarmField.setVisible(true);
			voltageLowAlarmField.setVisible(true);
			voltageHighWarningField.setVisible(true);
			voltageLowWarningField.setVisible(true);
			txBiasHighAlarmField.setVisible(true);
			txBiasLowAlarmField.setVisible(true);
			txBiasHighWarningField.setVisible(true);
			txBiasLowWarningField.setVisible(true);
			txPowerHighAlarmField.setVisible(true);
			txPowerLowAlarmField.setVisible(true);
			txPowerHighWarningField.setVisible(true);
			txPowerLowWarningField.setVisible(true);
			rxPowerHighAlarmField.setVisible(true);
			rxPowerLowAlarmField.setVisible(true);
			rxPowerHighWarningField.setVisible(true);
			rxPowerLowWarningField.setVisible(true);
			
			vendornameLabel.setVisible(true);
			vendorPNLabel.setVisible(true);
			vendorrevLabel.setVisible(true);
			vendorSNLabel.setVisible(true);
			datecodeLabel.setVisible(true);
			bRNominalLabel.setVisible(true);
			wavelengthLabel.setVisible(true);
			transMediaLabel.setVisible(true);
			lengthSMLabel.setVisible(true);
			temperatureLabel.setVisible(true);
			voltageLabel.setVisible(true);
			txBiasLabel.setVisible(true);
			txPowerLabel.setVisible(true);
			rxPowerLabel.setVisible(true);
			temperatureHighAlarmLabel.setVisible(true);
			temperatureLowAlarmLabel.setVisible(true);
			temperatureHighWarningLabel.setVisible(true);
			temperatureLowWarningLabel.setVisible(true);
			voltageHighAlarmLabel.setVisible(true);
			voltageLowAlarmLabel.setVisible(true);
			voltageHighWarningLabel.setVisible(true);
			voltageLowWarningLabel.setVisible(true);
			txBiasHighAlarmLabel.setVisible(true);
			txBiasLowAlarmLabel.setVisible(true);
			txBiasHighWarningLabel.setVisible(true);
			txBiasLowWarningLabel.setVisible(true);
			txPowerHighAlarmLabel.setVisible(true);
			txPowerLowAlarmLabel.setVisible(true);
			txPowerHighWarningLabel.setVisible(true);
			txPowerLowWarningLabel.setVisible(true);
			rxPowerHighAlarmLabel.setVisible(true);
			rxPowerLowAlarmLabel.setVisible(true);
			rxPowerHighWarningLabel.setVisible(true);
			rxPowerLowWarningLabel.setVisible(true);

		}else{

			vendornameField.setVisible(false);
			vendorPNField.setVisible(false);
			vendorrevField.setVisible(false);
			vendorSNField.setVisible(false);
			datecodeField.setVisible(false);
			bRNominalField.setVisible(false);
			wavelengthField.setVisible(false);
			transMediaField.setVisible(false);
			lengthSMField.setVisible(false);
			temperatureField.setVisible(false);
			voltageField.setVisible(false);
			txBiasField.setVisible(false);
			txPowerField.setVisible(false);
			rxPowerField.setVisible(false);
			temperatureHighAlarmField.setVisible(false);
			temperatureLowAlarmField.setVisible(false);
			temperatureHighWarningField.setVisible(false);
			temperatureLowWarningField.setVisible(false);
			voltageHighAlarmField.setVisible(false);
			voltageLowAlarmField.setVisible(false);
			voltageHighWarningField.setVisible(false);
			voltageLowWarningField.setVisible(false);
			txBiasHighAlarmField.setVisible(false);
			txBiasLowAlarmField.setVisible(false);
			txBiasHighWarningField.setVisible(false);
			txBiasLowWarningField.setVisible(false);
			txPowerHighAlarmField.setVisible(false);
			txPowerLowAlarmField.setVisible(false);
			txPowerHighWarningField.setVisible(false);
			txPowerLowWarningField.setVisible(false);
			rxPowerHighAlarmField.setVisible(false);
			rxPowerLowAlarmField.setVisible(false);
			rxPowerHighWarningField.setVisible(false);
			rxPowerLowWarningField.setVisible(false);
			

			vendornameLabel.setVisible(false);
			vendorPNLabel.setVisible(false);
			vendorrevLabel.setVisible(false);
			vendorSNLabel.setVisible(false);
			datecodeLabel.setVisible(false);
			bRNominalLabel.setVisible(false);
			wavelengthLabel.setVisible(false);
			transMediaLabel.setVisible(false);
			lengthSMLabel.setVisible(false);
			temperatureLabel.setVisible(false);
			voltageLabel.setVisible(false);
			txBiasLabel.setVisible(false);
			txPowerLabel.setVisible(false);
			rxPowerLabel.setVisible(false);
			temperatureHighAlarmLabel.setVisible(false);
			temperatureLowAlarmLabel.setVisible(false);
			temperatureHighWarningLabel.setVisible(false);
			temperatureLowWarningLabel.setVisible(false);
			voltageHighAlarmLabel.setVisible(false);
			voltageLowAlarmLabel.setVisible(false);
			voltageHighWarningLabel.setVisible(false);
			voltageLowWarningLabel.setVisible(false);
			txBiasHighAlarmLabel.setVisible(false);
			txBiasLowAlarmLabel.setVisible(false);
			txBiasHighWarningLabel.setVisible(false);
			txBiasLowWarningLabel.setVisible(false);
			txPowerHighAlarmLabel.setVisible(false);
			txPowerLowAlarmLabel.setVisible(false);
			txPowerHighWarningLabel.setVisible(false);
			txPowerLowWarningLabel.setVisible(false);
			rxPowerHighAlarmLabel.setVisible(false);
			rxPowerLowAlarmLabel.setVisible(false);
			rxPowerHighWarningLabel.setVisible(false);
			rxPowerLowWarningLabel.setVisible(false);
		}
		
		
	}
	
	private void createDetailPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		contentArea = new JTextArea(3, 1);
		contentArea.setEditable(false);
		contentArea.setBorder(new JTextField().getBorder());
		contentArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(contentArea);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		contentArea.setCaretPosition(0);
		parent.add(scrollPane, BorderLayout.CENTER);
		
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		parent.add(toolPanel, BorderLayout.PAGE_END);
		toolPanel.add(buttonFactory.createButton("deleteAlarm"));
		toolPanel.add(buttonFactory.createButton("cleanAlarm"));
		closeButton = buttonFactory.createCloseButton();
		toolPanel.add(closeButton);
		setCloseButton(closeButton);
	}
	
	@ViewAction(text=ActionConstants.DELETE, icon=ButtonConstants.DELETE, desc="删除端口告警",role=Constants.MANAGERCODE)
	public void deleteAlarm() {
		if (selectedAlarm != null) {
			remoteServer.getService().deleteEntity(selectedAlarm.getWrapped());
		}
	}
	
	@ViewAction(text=ActionConstants.CLEAN, icon=ButtonConstants.CLEAN, desc="清空端口告警",role=Constants.MANAGERCODE)
	public void cleanAlarm() {		
		int count = tableModel.getRowCount();
		
		for (int index = 0; index < count; index++) {
			AlarmMessage alarm = tableModel.getValue(index);
			remoteServer.getService().deleteEntity(alarm.getWrapped());
		}
	}
	
	private void selectOneRow() {
		if (table.getSelectedRow() > - 1) {
			selectedAlarm = tableModel.getValue(table.getSelectedRow());
			contentArea.setText(selectedAlarm.getDescs());
		}
	}
	
	private void createAlarmPanel(JPanel parent) {
		parent.setLayout(new GridLayout(1, 1));
		parent.setBorder(BorderFactory.createTitledBorder("告警信息"));
		table = new JXTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(false);
		table.getTableHeader().setReorderingAllowed(false);
		tableModel = new AlarmTableModel();		
		table.setModel(tableModel);		
		table.setColumnModel(tableModel.getColumnModel());
		JPanel tabelPanel = new JPanel(new BorderLayout(1, 2));
		tabelPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tabelPanel.add(table, BorderLayout.CENTER);
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);
		parent.add(scrollTablePanel);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						selectOneRow();
					}
				});
	}
	
	private void createInfoPanel(JPanel parent) {
		
			parent.setBorder(BorderFactory.createTitledBorder("端口信息"));
			parent.setLayout(new MigLayout());
			
			parent.add(new JLabel("端口"), "gapright 30");
			portNumberField = new JTextField("1");
			portNumberField.setEditable(false);
			parent.add(portNumberField, "width 200px, left");
			
			parent.add(new JLabel("状态"), "gapleft 30, gapright 30");
			statusField = new JTextField("2010-03-01 17:35");
			statusField.setEditable(false);
			parent.add(statusField, "span, width 200px, left");
			
			parent.add(new JLabel("连接状态"), "gapright 30");
			connectionStatusField = new JTextField("1");
			connectionStatusField.setEditable(false);
			parent.add(connectionStatusField, "width 200px, left");

			parent.add(new JLabel("类型"), "gapleft 30, gapright 30");
			typeField = new JTextField("192.168.14.6"); 
			typeField.setEditable(false);
			parent.add(typeField, "span, width 200px, left");
			
			parent.add(new JLabel("当前模式"), "gapright 30");
			modeField = new JTextField("192.168.14.6"); 
			modeField.setEditable(false);
			parent.add(modeField, "width 200px, left");
			
			parent.add(new JLabel("配置模式"), "gapleft 30, gapright 30");
			configModeField = new JTextField("192.168.14.6"); 
			configModeField.setEditable(false);
			parent.add(configModeField, "span, width 200px, left");
			
			parent.add(new JLabel("流控"), "gapright 30");
			flowControlField = new JTextField("192.168.14.6"); 
			flowControlField.setEditable(false);
			parent.add(flowControlField, "width 200px, left");
			
			parent.add(new JLabel("丢弃"), "gapleft 30, gapright 30");
			discardField = new JTextField("192.168.14.6");
			discardField.setEditable(false);
			parent.add(discardField, "span, width 200px, left");
			
			parent.add(vendornameLabel, "gapright 30");
			vendornameField = new JTextField("192.168.14.6"); 
			vendornameField.setEditable(false);
			parent.add(vendornameField, " width 200px, left");
			
			parent.add(vendorPNLabel, "gapleft 30, gapright 30");
			vendorPNField = new JTextField("192.168.14.6"); 
			vendorPNField.setEditable(false);
			parent.add(vendorPNField, "span, width 200px, left");
			
			parent.add(vendorrevLabel, "gapright 30");
			vendorrevField = new JTextField("192.168.14.6");
			vendorrevField.setEditable(false);
			parent.add(vendorrevField, " width 200px, left");
			
			parent.add(vendorSNLabel, "gapleft 30, gapright 30");
			vendorSNField = new JTextField("192.168.14.6"); 
			vendorSNField.setEditable(false);
			parent.add(vendorSNField, "span, width 200px, left");
			
			parent.add(datecodeLabel, "gapright 30");
			datecodeField = new JTextField("192.168.14.6"); 
			datecodeField.setEditable(false);
			parent.add(datecodeField, " width 200px, left");
			
			parent.add(bRNominalLabel, "gapleft 30, gapright 30");
			bRNominalField = new JTextField("192.168.14.6"); 
			bRNominalField.setEditable(false);
			parent.add(bRNominalField, "span, width 200px, left");
			
			parent.add(wavelengthLabel, "gapright 30");
			wavelengthField = new JTextField("192.168.14.6"); 
			wavelengthField.setEditable(false);
			parent.add(wavelengthField, " width 200px, left");
			
			parent.add(transMediaLabel, "gapleft 30, gapright 30");
			transMediaField = new JTextField("192.168.14.6"); 
			transMediaField.setEditable(false);
			parent.add(transMediaField, "span, width 200px, left");
			
			parent.add(lengthSMLabel, "gapright 30");
			lengthSMField = new JTextField("192.168.14.6"); 
			lengthSMField.setEditable(false);
			parent.add(lengthSMField, " width 200px, left");
			
			parent.add(temperatureLabel, "gapleft 30, gapright 30");
			temperatureField = new JTextField("192.168.14.6"); 
			temperatureField.setEditable(false);
			parent.add(temperatureField, "span, width 200px, left");
			
			parent.add(voltageLabel, "gapright 30");
			voltageField = new JTextField("192.168.14.6"); 
			voltageField.setEditable(false);
			parent.add(voltageField, " width 200px, left");
			
			parent.add(txBiasLabel, "gapleft 30, gapright 30");
			txBiasField = new JTextField("192.168.14.6"); 
			txBiasField.setEditable(false);
			parent.add(txBiasField, "span, width 200px, left");
			
			parent.add(txPowerLabel, "gapright 30");
			txPowerField = new JTextField("192.168.14.6"); 
			txPowerField.setEditable(false);
			parent.add(txPowerField, " width 200px, left");
			
			parent.add(rxPowerLabel, "gapleft 30, gapright 30");
			rxPowerField = new JTextField("192.168.14.6"); 
			rxPowerField.setEditable(false);
			parent.add(rxPowerField, "span, width 200px, left");
			
			parent.add(temperatureHighAlarmLabel, "gapright 30");
			temperatureHighAlarmField = new JTextField("192.168.14.6"); 
			temperatureHighAlarmField.setEditable(false);
			parent.add(temperatureHighAlarmField, " width 200px, left");
			
			parent.add(temperatureLowAlarmLabel, "gapleft 30, gapright 30");
			temperatureLowAlarmField = new JTextField("192.168.14.6"); 
			temperatureLowAlarmField.setEditable(false);
			parent.add(temperatureLowAlarmField, "span, width 200px, left");
			
			parent.add(temperatureHighWarningLabel, "gapright 30");
			temperatureHighWarningField = new JTextField("192.168.14.6"); 
			temperatureHighWarningField.setEditable(false);
			parent.add(temperatureHighWarningField, " width 200px, left");
			
			parent.add(temperatureLowWarningLabel, "gapleft 30, gapright 30");
			temperatureLowWarningField = new JTextField("192.168.14.6"); 
			temperatureLowWarningField.setEditable(false);
			parent.add(temperatureLowWarningField, "span, width 200px, left");
			
			parent.add(voltageHighAlarmLabel, "gapright 30");
			voltageHighAlarmField = new JTextField("192.168.14.6"); 
			voltageHighAlarmField.setEditable(false);
			parent.add(voltageHighAlarmField, " width 200px, left");
			
			parent.add(voltageLowAlarmLabel, "gapleft 30, gapright 30");
			voltageLowAlarmField = new JTextField("192.168.14.6"); 
			voltageLowAlarmField.setEditable(false);
			parent.add(voltageLowAlarmField, "span, width 200px, left");
			
			parent.add(voltageHighWarningLabel, "gapright 30");
			voltageHighWarningField = new JTextField("192.168.14.6"); 
			voltageHighWarningField.setEditable(false);
			parent.add(voltageHighWarningField, " width 200px, left");
			
			parent.add(voltageLowWarningLabel, "gapleft 30, gapright 30");
			voltageLowWarningField = new JTextField("192.168.14.6"); 
			voltageLowWarningField.setEditable(false);
			parent.add(voltageLowWarningField, "span, width 200px, left");
			
			parent.add(txBiasHighAlarmLabel, "gapright 30");
			txBiasHighAlarmField = new JTextField("192.168.14.6"); 
			txBiasHighAlarmField.setEditable(false);
			parent.add(txBiasHighAlarmField, " width 200px, left");
			
			parent.add(txBiasLowAlarmLabel, "gapleft 30, gapright 30");
			txBiasLowAlarmField = new JTextField("192.168.14.6"); 
			txBiasLowAlarmField.setEditable(false);
			parent.add(txBiasLowAlarmField, "span, width 200px, left");
			
			parent.add(txBiasHighWarningLabel, "gapright 30");
			txBiasHighWarningField = new JTextField("192.168.14.6"); 
			txBiasHighWarningField.setEditable(false);
			parent.add(txBiasHighWarningField, " width 200px, left");
			
			parent.add(txBiasLowWarningLabel, "gapleft 30, gapright 30");
			txBiasLowWarningField = new JTextField("192.168.14.6"); 
			txBiasLowWarningField.setEditable(false);
			parent.add(txBiasLowWarningField, "span, width 200px, left");
			
			parent.add(txPowerHighAlarmLabel, "gapright 30");
			txPowerHighAlarmField = new JTextField("192.168.14.6"); 
			txPowerHighAlarmField.setEditable(false);
			parent.add(txPowerHighAlarmField, " width 200px, left");
			
			parent.add(txPowerLowAlarmLabel, "gapleft 30, gapright 30");
			txPowerLowAlarmField = new JTextField("192.168.14.6"); 
			txPowerLowAlarmField.setEditable(false);
			parent.add(txPowerLowAlarmField, "span, width 200px, left");
			
			parent.add(txPowerHighWarningLabel, "gapright 30");
			txPowerHighWarningField = new JTextField("192.168.14.6"); 
			txPowerHighWarningField.setEditable(false);
			parent.add(txPowerHighWarningField, " width 200px, left");
			
			parent.add(txPowerLowWarningLabel, "gapleft 30, gapright 30");
			txPowerLowWarningField = new JTextField("192.168.14.6"); 
			txPowerLowWarningField.setEditable(false);
			parent.add(txPowerLowWarningField, "span, width 200px, left");
			
			parent.add(rxPowerHighAlarmLabel, "gapright 30");
			rxPowerHighAlarmField = new JTextField("192.168.14.6"); 
			rxPowerHighAlarmField.setEditable(false);
			parent.add(rxPowerHighAlarmField, " width 200px, left");
			
			parent.add(rxPowerLowAlarmLabel, "gapleft 30, gapright 30");
			rxPowerLowAlarmField = new JTextField("192.168.14.6"); 
			rxPowerLowAlarmField.setEditable(false);
			parent.add(rxPowerLowAlarmField, "span, width 200px, left");
			
			parent.add(rxPowerHighWarningLabel, "gapright 30");
			rxPowerHighWarningField = new JTextField("192.168.14.6"); 
			rxPowerHighWarningField.setEditable(false);
			parent.add(rxPowerHighWarningField, " width 200px, left");
			
			parent.add(rxPowerLowWarningLabel, "gapleft 30, gapright 30");
			rxPowerLowWarningField = new JTextField("192.168.14.6"); 
			rxPowerLowWarningField.setEditable(false);
			parent.add(rxPowerLowWarningField, "span, width 200px, left");
			
		
	}
		
	private final PropertyChangeListener SelectionChangedLinstener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			selectedPort = 
				(SwitchPortEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchPortEntity.class);
			fillContents(selectedPort);
			if (alarmModel.getLastAlarms() != null && alarmModel.getLastAlarms().size() > 0) {
				alarmArrival(alarmModel.getLastAlarms());
			}
		}		
	};
	
	private final PropertyChangeListener AlarmArrivalListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() instanceof List<?>) {
				List<AlarmMessage> alarms = (List<AlarmMessage>)evt.getNewValue();
				alarmArrival(alarms);
			}
		}
	};

	private AlarmMessage selectedAlarm;
	private JCloseButton closeButton;
	private JTextArea contentArea;
	private JXTable table;
	private AlarmTableModel tableModel;
	private JTextField portNumberField;
	private JTextField statusField;
	private JTextField connectionStatusField;
	private JTextField typeField;
	private JTextField modeField;
	private JTextField configModeField;
	private JTextField flowControlField;
	private JTextField discardField;
	private JTextField vendornameField;
	private JTextField vendorPNField;
	private JTextField vendorrevField;
	private JTextField vendorSNField;
	private JTextField datecodeField;
	private JTextField bRNominalField;
	private JTextField wavelengthField;
	private JTextField transMediaField;
	private JTextField lengthSMField;
	private JTextField temperatureField;
	private JTextField voltageField;
	private JTextField txBiasField;
	private JTextField txPowerField;
	private JTextField rxPowerField;
	private JTextField temperatureHighAlarmField;
	private JTextField temperatureLowAlarmField;
	private JTextField temperatureHighWarningField;
	private JTextField temperatureLowWarningField;
	private JTextField voltageHighAlarmField;
	private JTextField voltageLowAlarmField;
	private JTextField voltageHighWarningField;
	private JTextField voltageLowWarningField;
	private JTextField txBiasHighAlarmField;
	private JTextField txBiasLowAlarmField;
	private JTextField txBiasHighWarningField;
	private JTextField txBiasLowWarningField;
	private JTextField txPowerHighAlarmField;
	private JTextField txPowerLowAlarmField;
	private JTextField txPowerHighWarningField;
	private JTextField txPowerLowWarningField;
	private JTextField rxPowerHighAlarmField;
	private JTextField rxPowerLowAlarmField;
	private JTextField rxPowerHighWarningField;
	private JTextField rxPowerLowWarningField;
	private JLabel vendornameLabel = new JLabel("Vendorname");
	private JLabel vendorPNLabel = new JLabel("VendorPN");
	private JLabel vendorrevLabel = new JLabel("Vendorrev");
	private JLabel vendorSNLabel = new JLabel("VendorSN");
	private JLabel datecodeLabel = new JLabel("Datecode");
	private JLabel bRNominalLabel = new JLabel("BRNominal");
	private JLabel wavelengthLabel = new JLabel("Wavelength");
	private JLabel transMediaLabel = new JLabel("TransMedia");
	private JLabel lengthSMLabel = new JLabel("LengthSM");
	private JLabel temperatureLabel = new JLabel("Temperature");
	private JLabel voltageLabel = new JLabel("Voltage");
	private JLabel txBiasLabel = new JLabel("TxBias");
	private JLabel txPowerLabel = new JLabel("TxPower");
	private JLabel rxPowerLabel = new JLabel("RxPower");
	private JLabel temperatureHighAlarmLabel = new JLabel("TemperatureHighAlarm");
	private JLabel temperatureLowAlarmLabel = new JLabel("TemperatureLowAlarm");
	private JLabel temperatureHighWarningLabel = new JLabel("TemperatureHighWarning");
	private JLabel temperatureLowWarningLabel = new JLabel("TemperatureLowWarning");
	private JLabel voltageHighAlarmLabel = new JLabel("VoltageHighAlarm");
	private JLabel voltageLowAlarmLabel = new JLabel("VoltageLowAlarm");
	private JLabel voltageHighWarningLabel = new JLabel("VoltageHighWarning");
	private JLabel voltageLowWarningLabel = new JLabel("VoltageLowWarning");
	private JLabel txBiasHighAlarmLabel = new JLabel("TxBiasHighAlarm");
	private JLabel txBiasLowAlarmLabel = new JLabel("TxBiasLowAlarm");
	private JLabel txBiasHighWarningLabel = new JLabel("TxBiasHighWarning");
	private JLabel txBiasLowWarningLabel = new JLabel("TxBiasLowWarning");
	private JLabel txPowerHighAlarmLabel = new JLabel("TxPowerHighAlarm");
	private JLabel txPowerLowAlarmLabel = new JLabel("TxPowerLowAlarm");
	private JLabel txPowerHighWarningLabel = new JLabel("TxPowerHighWarning");
	private JLabel txPowerLowWarningLabel = new JLabel("TxPowerLowWarning");
	private JLabel rxPowerHighAlarmLabel = new JLabel("RxPowerHighAlarm");
	private JLabel rxPowerLowAlarmLabel = new JLabel("RxPowerLowAlarm");
	private JLabel rxPowerHighWarningLabel = new JLabel("RxPowerHighWarning");
	private JLabel rxPowerLowWarningLabel = new JLabel("RxPowerLowWarning");

	
	private SwitchPortEntity selectedPort;
	
	private ButtonFactory buttonFactory;

	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name = AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name = LocalizationManager.ID)
	private LocalizationManager localizationManager;

	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DateFormatter.ID)
	private DateFormatter dataFormatter;
	
	@Resource(name=AlarmSeverity.ID)
	private AlarmSeverity alarmSeverity;
	
	@Resource(name=AlarmEvents.ID)
	private AlarmEvents alarmEvents;
	
	@Resource(name = SwitcherPortConnectCategory.ID)
	private SwitcherPortConnectCategory switcherPortConnectCategory;
	
	@Resource(name = SwitcherPortWorkCategory.ID)
	private SwitcherPortWorkCategory switcherPortWorkCategory;
	
	@Resource(name = SwitcherPortModeCategory.ID)
	private SwitcherPortModeCategory switcherPortModeCategory;
	
	@Resource(name = SwitchPortCategory.ID)
	private SwitchPortCategory switcherPortCategory;

	private static final Logger LOG = LoggerFactory
			.getLogger(PortDetailView.class);
	private static final long serialVersionUID = 1L;
	public static final String ID = "portDetailView";
	
	public class AlarmSeverityHighlighter extends AbstractHighlighter {
		@Override
		protected java.awt.Component doHighlight(
				java.awt.Component component, ComponentAdapter adapter) {
			AlarmMessage alarmMessage = tableModel.getValue(adapter.row);
			WarningType warningType = alarmModel.getWarningType(alarmMessage.getWarningType());
			component.setBackground(alarmSeverity.getColor(warningType.getWarningLevel()));
			return component;
		}
	
		private static final long serialVersionUID = -8795784237395864733L;
	}

	public class AlarmTableModel extends AbstractTableModel {
		
		public AlarmTableModel() {
			alarms = Collections.emptyList();

			int modelIndex = 0;
			columnModel = new DefaultTableColumnModel();
			TableColumnExt raisedTimeColumn = new TableColumnExt(modelIndex++, 160);
			raisedTimeColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_RAISED_TIME);
			raisedTimeColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_RAISED_TIME));
			columnModel.addColumn(raisedTimeColumn);

			TableColumnExt severityColumn = new TableColumnExt(modelIndex++, 60);
			severityColumn.setHighlighters(new AlarmSeverityHighlighter());
			severityColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_SEVERITY);
			severityColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_SEVERITY));		
			columnModel.addColumn(severityColumn);
			
			TableColumnExt contentColumn = new TableColumnExt(modelIndex++, 300);
			contentColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_CONTENT);
			contentColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_CONTENT));
			columnModel.addColumn(contentColumn);
			
			TableColumnExt eventColumn = new TableColumnExt(modelIndex++, 100);
			eventColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_EVENT);
			eventColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_EVENT));
			columnModel.addColumn(eventColumn);
		}

		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return alarms.size();
		}

		@Override
		public String getColumnName(int col) {
			return columnModel.getColumn(col).getHeaderValue().toString();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if (row < alarms.size()) {
				AlarmMessage alarmMessage = alarms.get(row);
				WarningType warningType = alarmModel.getWarningType(alarmMessage.getWarningType());
				switch (col) {
					case 0: value = dataFormatter.format(alarmMessage.getCreateDate()); break;
					case 1: value = alarmSeverity.get(warningType.getWarningLevel()).getKey(); break;
					case 2: value = alarmMessage.getDescs(); break;
					case 3: value = alarmEvents.get(warningType.getWarningType()).getKey(); break;
					default: break;
				}
			}		
			
			return value;
		}
		
		public TableColumnModel getColumnModel() {
			return columnModel;
		}
		
		public AlarmMessage getValue(int row) {
			AlarmMessage value = null;
			if (row < alarms.size()) {
				value = alarms.get(row);
			}
			
			return value;
		}
		
		public void setAlarms(List<AlarmMessage> alarms) {
			if (alarms == null) {
				return;
			}
			this.alarms = alarms;
		}
		
		private List<AlarmMessage> alarms;
		private final TableColumnModel columnModel;
		private static final long serialVersionUID = -3065768803494840568L;
	}
}