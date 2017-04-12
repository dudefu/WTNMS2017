package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.CONFIRM;
import static com.jhw.adm.client.core.ActionConstants.EXPORT;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.AlarmTypeCategory;
import com.jhw.adm.client.model.WarnByTypeTableModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.JUrgentAlarmButtonModel;
import com.jhw.adm.client.swing.WarningConfirmStrategy;
import com.jhw.adm.client.ui.ClientUI;
import com.jhw.adm.client.util.FileImportAndExport;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.WarningEntity;

@Component(UrgentAlarmConfirmView.ID)
@Scope(Scopes.DESKTOP)
public class UrgentAlarmConfirmView extends ViewPart {

	private static final long serialVersionUID = -1288837887099756518L;
	public static final String ID = "urgentAlarmConfirmView";
	private static final Logger LOG = LoggerFactory.getLogger(UrgentAlarmConfirmView.class);
	private static final int ZERO = 0;
	
	private JScrollPane scrollPane = new JScrollPane();
	private JXTable table = new JXTable();
	private WarnByTypeTableModel model = new WarnByTypeTableModel();
	private JTextArea contentArea;
	private JPanel resultPanel = new JPanel();
	private JPanel detailPanel = new JPanel();
	
	private ButtonFactory buttonFactory;
	private JPanel buttonPanel = new JPanel();
	private JButton confirmButton = null;
	private JButton exportButton = null;
	private JLabel messageLabel = null;
	private static final String MESSAGE = "紧急告警总数：%s,选中告警数：%s";
	private boolean isSelected = false;
	
	private List<WarningEntity> warningConfirmedList = new ArrayList<WarningEntity>();
	private JCheckBox realTimeUpdateCheckBox = new JCheckBox("实时更新");
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=JUrgentAlarmButtonModel.ID)
	private JUrgentAlarmButtonModel urgentModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name=DateFormatter.ID)
	private DateFormatter dateFormatter;
	
	@Resource(name=AlarmSeverity.ID)
	private AlarmSeverity alarmSeverity;
	
	@Resource(name=AlarmTypeCategory.ID)
	private AlarmTypeCategory alarmTypeCategory;
	
	private PropertyChangeListener urgentAlarmListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateUrgentAlarmTableData();
		}
	};
	
	private PropertyChangeListener urgentAlarmUpdateAfterConfirmListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setTableData();
		}
	};
	
	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this);
		urgentModel.addPropertyChangeListener(JUrgentAlarmButtonModel.UPDATE_URGENT_VIEW, urgentAlarmListener);
		urgentModel.addPropertyChangeListener(JUrgentAlarmButtonModel.NOTICE_VIEW_UPDATE_AFTER_CONFIRM_URGENT, urgentAlarmUpdateAfterConfirmListener);
		createResultPanel(resultPanel);
		createButtonPanel(buttonPanel);
		
		this.setLayout(new BorderLayout());
		this.add(resultPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setTitle("紧急告警列表");
		
		initializeTableData();
	}

	protected void updateUrgentAlarmTableData() {
		if(isSelected == true){
			setTableData();
		}else if(isSelected = false){
			//do something maybe
		}
	}

	private void setTableData() {
		if(SwingUtilities.isEventDispatchThread()){
			model.setWarningAlarms(urgentModel.getUrgentAlam());
			model.fireTableDataChanged();
			setMessageLabel(urgentModel.getUrgentAlam().size(), ZERO);
			this.contentArea.setText("");
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setTableData();
				}
			});
		}
	}

	private void createResultPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		createTablePanel(scrollPane);
		createMessageDetail(detailPanel);
		
		parent.add(scrollPane, BorderLayout.CENTER);
		parent.add(detailPanel, BorderLayout.SOUTH);
	}

	private void initializeTableData() {
		/*
		 * do something for update table's data which come from UrgentModel
		 * like data = urgentModel.getData();
		 */
		setTableData();
	}

	private void createButtonPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		JPanel messagePanel = new JPanel(new BorderLayout());
		
		messageLabel = new JLabel();
		setMessageLabel(ZERO, ZERO);
		
		realTimeUpdateCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					isSelected = true;
				}else if(e.getStateChange() == ItemEvent.DESELECTED){
					isSelected = false;
				}
			}
		});
		realTimeUpdateCheckBox.setSelected(true);
		
		messagePanel.add(messageLabel, BorderLayout.CENTER);
		messagePanel.add(realTimeUpdateCheckBox, BorderLayout.LINE_END);
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		confirmButton = buttonFactory.createButton(CONFIRM);
		exportButton = buttonFactory.createButton(EXPORT);
		panel.add(exportButton);
		panel.add(confirmButton);
		
		parent.add(messagePanel, BorderLayout.CENTER);
		parent.add(panel, BorderLayout.LINE_END);
	}
	
	private void createMessageDetail(JPanel parent) {
		parent.setLayout(new BorderLayout());
		parent.setBorder(BorderFactory.createTitledBorder("详细内容"));
		
		contentArea = new JTextArea(3, 1);
		contentArea.setEditable(false);
		contentArea.setBorder(new JTextField().getBorder());
		contentArea.setLineWrap(true);		
		JScrollPane scrollPane = new JScrollPane(contentArea);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		contentArea.setCaretPosition(0);
		parent.add(scrollPane, BorderLayout.CENTER);
	}
	
	private void setMessageLabel(final int totalMessage, final int selectedMessage){
		if(SwingUtilities.isEventDispatchThread()){
			messageLabel.setText(String.format(MESSAGE, totalMessage, selectedMessage));
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setMessageLabel(totalMessage,selectedMessage);
				}
			});
		}
	}
	
	private void selectOneRow() {
		if (table.getSelectedRow() > - 1) {
			WarningEntity warningEntity = model.getValue(table.getSelectedRow());
			contentArea.setText(warningEntity.getDescs());
		}
	}
	
	private void createTablePanel(JScrollPane parent) {
		
		parent.getHorizontalScrollBar().setFocusable(false);
		parent.getVerticalScrollBar().setFocusable(false);
		parent.getHorizontalScrollBar().setUnitIncrement(30);
		parent.getVerticalScrollBar().setUnitIncrement(30);
		
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
//		table.setColumnControlVisible(true);
		
		table.setModel(model);
		table.setColumnModel(model.getColumnModel());
		table.getTableHeader().setReorderingAllowed(false);
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				setMessageLabel(urgentModel.getUrgentAlam().size(), table.getSelectedRowCount());
				selectOneRow();
			}
		});
		
		initPopupMenu();
		table.addMouseListener(new DetailPopupListener());
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				if(row >= 0){
					if(e.getClickCount() >= 2){
						alarmModel.setSingleConfirmAlarm(model.getValue(row));
						String viewID = SingleWarningConfirmView.ID;
						String groupID = ConfigureGroupView.ID;
						LOG.info(String.format("Enter view (%s)", viewID));
						ClientUI.getDesktopWindow().showView(viewID, groupID);
					}
				}
			}
		});

		parent.getViewport().add(table);
	}
	
	private JPopupMenu popup = null;
	private void initPopupMenu(){
		popup = new JPopupMenu();
		JMenuItem  locationMenuItem = new JMenuItem ("定位");
		popup.add(locationMenuItem);
		
		Action locationAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row > -1){
					WarningEntity warningEntity = model
							.getValue(table.getSelectedRow());
					NodeEntity selectedNode = remoteServer.getService().findById(warningEntity.getNodeId(), NodeEntity.class);
					NodeUtils.fixedPositionEquipment(selectedNode);
				}
			}
		};
		locationMenuItem.addActionListener(locationAction);
	}

	class DetailPopupListener extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			processEvent(e);
		}
		
	    @Override
		public void mouseReleased(MouseEvent e) {
	    	processEvent(e);
	        if((e.getModifiers() & InputEvent.BUTTON3_MASK)!=0){
	            popup.show(e.getComponent(),e.getX(),e.getY());
	        }
	    }
	    
	    private void processEvent(MouseEvent e) {
			if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
				MouseEvent ne = new MouseEvent(e.getComponent(), e.getID(),
						e.getWhen(), MouseEvent.BUTTON1_MASK, e.getX(), e
								.getY(), e.getClickCount(), false);
				table.dispatchEvent(ne);
			}
		}
	}
	
	@ViewAction(name=CONFIRM, icon=ButtonConstants.SAVE, desc="确认紧急告警信息",role=Constants.MANAGERCODE)
	public void confirm(){
		int selected = table.getSelectedRowCount();
		if(selected <= 0){
			JOptionPane.showMessageDialog(this, "请选择需要确认的告警信息", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int backSelected = JOptionPane.showConfirmDialog(this, "你确定确认所选择的告警信息", "提示", JOptionPane.OK_CANCEL_OPTION);
		if(backSelected != JOptionPane.OK_OPTION){
			return;
		}
		
		warningConfirmedList = model.getSelectedList(table.getSelectedRows());
		
		Task task = new RequestTask(warningConfirmedList);
		showMessageDialog(task);
	}
	
	private class RequestTask implements Task{

		private List<WarningEntity> list = null;
		
		public RequestTask(List<WarningEntity> list){
			this.list = list;
		}
		
		@Override
		public void run() {
			confirmedWarning = new ArrayList<WarningEntity>();
			unConfirmedWarning = new ArrayList<WarningEntity>();
			alarmModel.updateSelectedWarningAttributes(list,unConfirmedWarning,confirmedWarning);
			try{
				remoteServer.getNmsService().confirmWarningInfo(unConfirmedWarning);
			}catch(Exception e){
				strategy.showErrorMessage("确认紧急告警异常");
				LOG.error("Error occur when confirming urgent warning(s)", e);
			}
			strategy.showNormalMessage(confirmedWarning.size(), unConfirmedWarning.size());
			alarmModel.confirmAlarm(model.getSelectedList(table.getSelectedRows()));
		}
	}
	
	private List<WarningEntity> confirmedWarning = null;
	private List<WarningEntity> unConfirmedWarning = null;
	private JProgressBarModel progressBarModel;
	private WarningConfirmStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new WarningConfirmStrategy("确认紧急告警", progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,"确认紧急告警",true);
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
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=EXPORT, icon=ButtonConstants.EXPORT, desc="导出紧急告警信息",role=Constants.MANAGERCODE)
	public void export(){
		List<WarningEntity> warninglist = urgentModel.getUrgentAlam();
		
		String[] propertiesName = new String[5];
		propertiesName[0] = "时间";
		propertiesName[1] = "来源";
		propertiesName[2] = "内容";
		propertiesName[3] = "级别";
		propertiesName[4] = "类型";
		List<HashMap> list = new ArrayList<HashMap>();
		for(WarningEntity warningEntity : warninglist){
			HashMap hashMap = new HashMap(); 
			hashMap.put(propertiesName[0], dateFormatter.format(warningEntity.getCreateDate()));
			hashMap.put(propertiesName[1], warningEntity.getNodeName());
			hashMap.put(propertiesName[2], warningEntity.getDescs());
			hashMap.put(propertiesName[3], alarmSeverity.get(warningEntity.getWarningLevel()).getKey());
			hashMap.put(propertiesName[4], alarmTypeCategory.get(warningEntity.getWarningCategory()).getKey());
			list.add(hashMap);
		}
		int result = FileImportAndExport.export(this, list, propertiesName);
		if(FileImportAndExport.SUCCESS_RESULT == result){
			JOptionPane.showMessageDialog(this, "紧急告警信息导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
		}else if(FileImportAndExport.CANCLE_RESULT == result){
			//if cancle, no message dialog
		}else if(FileImportAndExport.FAILURE_RESULT == result){
			JOptionPane.showMessageDialog(this, "紧急告警信息导出失败", "提示", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		urgentModel.removePropertyChangeListener(JUrgentAlarmButtonModel.UPDATE_URGENT_VIEW, urgentAlarmListener);
		urgentModel.removePropertyChangeListener(JUrgentAlarmButtonModel.NOTICE_VIEW_UPDATE_AFTER_CONFIRM_URGENT, urgentAlarmUpdateAfterConfirmListener);
	}
	
}
