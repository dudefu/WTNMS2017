package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.AlarmEvents;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.AlarmStatus;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.TrapSimpleWarning;

/** 
 * 告警详细信息视图 
 */
@Component(AlarmDetailView.ID)
@Scope(Scopes.DESKTOP)
public class AlarmDetailView extends ViewPart {

	@PostConstruct
	protected void initialize() {
		setViewSize(550, 320);
		setTitle("告警详细信息");
		actionMap = actionManager.getActionMap(this);
		createContents(this);
		fillContents();
		alarmModel.addPropertyChangeListener(AlarmModel.SELECTION_CHANGED, SelectionChangedLinstener);
	}

	protected void fillContents() {
//		trapWarning =
//			(TrapSimpleWarning)adapterManager.getAdapter(
//					alarmModel.getLastSelected(), TrapSimpleWarning.class);
//
//		if (trapWarning != null) {
//			
//			WarningType warningType = alarmModel.getWarningType(trapWarning.getWarningType());
//			
//			if (warningType != null) {
//				typeField.setText(alarmEvents.get(warningType.getWarningType()).getKey());
//				alarmSeverityField.setText(alarmSeverity.get(warningType.getWarningLevel()).getKey());
//			}
//			
//			codeField.setText(Long.toString(trapWarning.getId()));
//			rasiedTimeField.setText(dateFormatter.format(getRasiedTime(trapWarning)));
//			equipmentField.setText(trapWarning.getIpValue());
//			statusCombox.setSelectedItem(alarmStatus.get(trapWarning.getCurrentStatus()));
//			contentArea.setText(trapWarning.getDescs());
//		}
	}
	
	private Date getRasiedTime(TrapSimpleWarning trapWarning) {
		Date rasiedTime = null;
		
		if (trapWarning.getCurrentStatus() == alarmStatus.NEW.getValue()) {
			rasiedTime = trapWarning.getCreateDate();//trapWarning.getNewStatus().getBeginTime();
		}
		
		if (trapWarning.getCurrentStatus() == alarmStatus.FIXING.getValue()) {
			rasiedTime = trapWarning.getFixStatus().getBeginTime();
		}
		
		if (trapWarning.getCurrentStatus() == alarmStatus.CLOSE.getValue()) {
			rasiedTime = trapWarning.getCloseStatus().getBeginTime();
		}
		
		return rasiedTime;
	}

	protected void createContents(JPanel parent) {
		setLayout(new BorderLayout(0, 0));
		JPanel centerPanel = new JPanel();
		createCenter(centerPanel);
		add(centerPanel, BorderLayout.CENTER);
		JPanel pageStartPanel = new JPanel();
		createPageStart(pageStartPanel);
		add(pageStartPanel, BorderLayout.PAGE_START);
	}

	private void createPageStart(JPanel parent) {
		parent.setLayout(new GridLayout(1, 1));
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(new JButton(imageRegistry.getImageIcon(ButtonConstants.PREVIOUS)));
		toolBar.add(new JButton(imageRegistry.getImageIcon(ButtonConstants.NEXT)));
		JButton saveButton = new JButton();
		saveButton.setAction(actionMap.get(SAVE));
		toolBar.add(saveButton);
		parent.add(toolBar);
	}

	private void createCenter(JPanel parent) {
		parent.setBorder(BorderFactory.createTitledBorder("详细信息"));
		parent.setLayout(new MigLayout("", "[right]"));

		parent.add(new JLabel("编号"), "gapright 30");
		codeField = new JTextField();
		codeField.setEditable(false);
		parent.add(codeField, "span, width 200px, left");
		
		parent.add(new JLabel("时间"), "gapright 30");
		rasiedTimeField = new JTextField();
		rasiedTimeField.setEditable(false);
		parent.add(rasiedTimeField, "span, width 200px, left");
		
		parent.add(new JLabel("级别"), "gapright 30");
		alarmSeverityField = new JTextField();
		alarmSeverityField.setEditable(false);
		parent.add(alarmSeverityField, "span, width 200px, left");

		parent.add(new JSeparator(), "span, growx, wrap, gaptop 10");

		parent.add(new JLabel("设备"), "gapright 30");
		equipmentField = new JTextField(); 
		equipmentField.setEditable(false);
		parent.add(equipmentField, "span, width 200px, left");
		
		parent.add(new JLabel("状态"), "gapright 30");
		statusCombox = new JComboBox(new ListComboBoxModel(
				alarmStatus.toList()));
		parent.add(statusCombox, "span, width 200px, left");
		
		parent.add(new JLabel("类型"), "gapright 30");
		typeField = new JTextField();
		typeField.setEditable(false);
		parent.add(typeField, "span, width 200px, left");
		
		parent.add(new JSeparator(), "span, growx, wrap, gaptop 10");
		
		parent.add(new JLabel("内容"), "gapright 30");
		contentArea = new JTextArea(5, 1);
		contentArea.setEditable(false);
		contentArea.setBorder(new JTextField().getBorder());
		contentArea.setLineWrap(true);
		parent.add(contentArea, "span, width 300px, height 100px:100px:100px, left");

		commentArea = new JTextArea(5, 1);
		
		commentArea.setBorder(new JTextField().getBorder());
		commentArea.setLineWrap(true);
		parent.add(new JLabel("意见"), "gapright 30");
		parent.add(commentArea, "width 300px:300px:300px, height 100px:100px:100px, left");
	}
	
	@ViewAction(icon=ButtonConstants.SAVE,desc="保存告警详细信息",role=Constants.MANAGERCODE)
	public void save() {
		if (trapWarning != null) {
			int status = ((StringInteger)statusCombox.getSelectedItem()).getValue();
//			alarmModel.changeTrapStatus(trapWarning, status);
			remoteServer.getService().updateEntity(trapWarning);
//			alarmModel.fireStatusChanged();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		alarmModel.removePropertyChangeListener(AlarmModel.SELECTION_CHANGED, SelectionChangedLinstener);
	}
	
	private PropertyChangeListener SelectionChangedLinstener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (initialized) {
				fillContents();
			} else {
				initialized = true;
			}
		}		
	};
	
	private JTextField codeField;
	private JTextField rasiedTimeField;
	private JTextField alarmSeverityField;
	private JTextField equipmentField;
	private JComboBox statusCombox;
	private JTextField typeField;
	private JTextArea contentArea;
	private JTextArea commentArea;
	
	private boolean initialized;
	private TrapSimpleWarning trapWarning;
	private ActionMap actionMap;
	
	@Resource(name = AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name = AlarmSeverity.ID)
	private AlarmSeverity alarmSeverity;

	@Resource(name = AlarmEvents.ID)
	private AlarmEvents alarmEvents;

	@Resource(name = AlarmStatus.ID)
	private AlarmStatus alarmStatus;
	
	@Resource(name = DateFormatter.ID)
	private DateFormatter dateFormatter;
	
	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private static final long serialVersionUID = -7404359366467597532L;
	public static final String ID = "alarmDetailView";
}