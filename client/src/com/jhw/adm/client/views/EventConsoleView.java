package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.DISABLED;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.table.TableColumnExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Cleanable;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.core.WarningAudioPlayer;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.AlarmTypeCategory;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.resources.ResourceManager;
import com.jhw.adm.client.resources.sound.SoundConstants;
import com.jhw.adm.client.ui.ClientUI;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.WarningEntity;

/**
 * 事件通知台，显示最新的告警信息
 */
@Component(EventConsoleView.ID)
@Scope(Scopes.DESKTOP)
public class EventConsoleView extends ViewPart implements Cleanable {

	@PostConstruct
	protected void initialize() {
//		String Url = resourceManager.getURL(SoundConstants.WARNING_SOUND).toString();
//		DEFAULT_WARNING_SOUND_URL = file.getAbsolutePath();
		DEFAULT_WARNING_SOUND_URL = resourceManager.getURL(SoundConstants.WARNING_SOUND);
//		System.err.println(DEFAULT_WARNING_SOUND_URL);
		setTitle(localizationManager.getString(ResourceConstants.EVENT_CONSOLE_TITLE));
		setLayout(new BorderLayout());

		warningTable = new JXTable();
		warningTable.setAutoCreateColumnsFromModel(false);
		warningTable.setEditable(false);
		warningTable.setSortable(false);
		warningTable.getTableHeader().setReorderingAllowed(false);
		warningTableModel = new WarningTableModel();
		warningTable.setModel(warningTableModel);
		warningTable.setColumnModel(warningTableModel.getColumnModel());
//		warningTable.setColumnControlVisible(true);
//		warningTable.setColumnControl(new RowControlButton(this));

		JScrollPane warningScrollTablePanel = new JScrollPane();
		warningScrollTablePanel.getViewport().add(warningTable, null);
		warningScrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		warningScrollTablePanel.getVerticalScrollBar().setFocusable(false);
		warningScrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		warningScrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);
		
		initPopupMenu();
		warningTable.addMouseListener(new DetailPopupListener());
		
		warningTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = warningTable.rowAtPoint(e.getPoint());
				if(row >= 0){
					if(e.getClickCount() >= 2){
						alarmModel.setSingleConfirmAlarm(warningTableModel.getValue(row));
						String viewID = SingleWarningConfirmView.ID;
						String groupID = ConfigureGroupView.ID;
						LOG.info(String.format("Enter view (%s)", viewID));
						ClientUI.getDesktopWindow().showView(viewID, groupID);
					}
				}
			}
		});

		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
		statusLabel = new JLabel();
		setStatusLabel(String.format(STATUS_PATTERN, 0));
		warningAudioCheckBox = new JCheckBox("声音告警", true);
		warningAudioCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				stop();
			}
		});
		statusPanel.add(statusLabel, BorderLayout.CENTER);
		statusPanel.add(warningAudioCheckBox, BorderLayout.LINE_END);
		add(warningScrollTablePanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.PAGE_END);
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_RECEIVED, alarmReceivedListener);
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_CONFIRM, alarmConfirmedListener);
		
		initialData();
	}

	private void initPopupMenu(){
		popup = new JPopupMenu();
		JMenuItem  locationMenuItem = new JMenuItem ("定位");
		popup.add(locationMenuItem);
		
		Action locationAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = warningTable.getSelectedRow();
				if(row > -1){
					WarningEntity warningEntity = warningTableModel
							.getValue(warningTable.getSelectedRow());
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
				warningTable.dispatchEvent(ne);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initialData() {
		
		String where = " where entity.currentStatus=" + Constants.UNCONFIRM + " order by entity.createDate desc";
		List<WarningEntity> list = (List<WarningEntity>) remoteServer.getService().findAll(WarningEntity.class, where);
		list = NodeUtils.filterWarningEntity(list);
		if(null != list){
			this.warningEventNumber = list.size();
		}else{
			this.warningEventNumber = 0;
		}
//		if(this.warningEventNumber == 0) {
//			fireTableDataChanged(list);
//			setStatusLabel(String.format(STATUS_PATTERN,warningEventNumber));
//			return;
//		}
		
		LOG.info(String.format("初始化告警数量:%s", this.warningEventNumber));
		fireTableDataChanged(list);
		setStatusLabel(String.format(STATUS_PATTERN,warningEventNumber));
	}
	
	private void fireTableDataChanged(List<WarningEntity> list){
		warningTableModel.setWarningAlarms(list);
		warningTableModel.fireTableDataChanged();
	}
	
	@Override
	public void clean() {
		warningTableModel.clean();
		warningEventNumber = 0;
		setStatusLabel(String.format(STATUS_PATTERN,warningEventNumber));
	}

	@Override
	public void dispose() {
		super.dispose();
		alarmModel.removePropertyChangeListener(AlarmModel.ALARM_RECEIVED, alarmReceivedListener);
		alarmModel.removePropertyChangeListener(AlarmModel.ALARM_CONFIRM, alarmConfirmedListener);
	}
	
	private final PropertyChangeListener alarmConfirmedListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			initialData();
		}
	};

	private long warningEventNumber = 0;
	private final PropertyChangeListener alarmReceivedListener = new PropertyChangeListener() {
		@SuppressWarnings("static-access")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() != null) {
				warningEventNumber += 1;
				WarningEntity warning = (WarningEntity) evt.getNewValue();
				warningTableModel.addWarningAlarms(warning); 
				warningTableModel.fireTableDataChanged();
				setStatusLabel(String.format(STATUS_PATTERN,warningEventNumber));
				LOG.info(String.format(STATUS_PATTERN,warningEventNumber));
				if (startOrEndPlay == 0){// 禁止状态下禁止播放告警声音
					if (!warningAudioPlayer.playStatus) {
						warningAudioPlayer.playStatus = true;
						if(clientModel.getClientConfig().isDefaultWarningAudio()){
							playUrl = DEFAULT_WARNING_SOUND_URL;
						}else {
							String filePath = clientModel.getClientConfig().getWavFileName();
							File file = new File(filePath);
							if(file.exists()){
								try {
									playUrl = file.toURI().toURL();
								} catch (MalformedURLException e) {
									JOptionPane.showMessageDialog(ClientUtils.getRootFrame(), "播放出错，文件不存在", "提示",JOptionPane.NO_OPTION);
									warningAudioPlayer.playStatus = false;
									LOG.error("EventConsole.alarmRecieve error", e);
									return;
								}
							}else{
								JOptionPane.showMessageDialog(ClientUtils.getRootFrame(), "播放出错，文件不存在", "提示",JOptionPane.NO_OPTION);
								warningAudioPlayer.playStatus = false;
								return;
							}
						}
						play(playUrl, clientModel.getClientConfig().getRePeatNum());
					}
				}
			}
		}
	};
	
	private void setStatusLabel(final String message){
		if(SwingUtilities.isEventDispatchThread()) {
			statusLabel.setText(message);
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setStatusLabel(message);
				}
			});
		}
	}
	
	// Amend 2010.06.10
	// int playStatus = 0;//0--正在播放,1--停止播放
	int startOrEndPlay = 0;// 0--启用状态(start)，1--禁止状态(end),初始为启用状态

	@SuppressWarnings("static-access")
	@ViewAction(name = DISABLED, icon = ButtonConstants.STOP, desc = "控制是否启用声音告警", role = Constants.MANAGERCODE)
	public void stop() {
		if (!warningAudioCheckBox.isSelected()){// 由启用转为禁止，原本是启用状态,顺便停止播放
			warningAudioPlayer.stop();
			startOrEndPlay = 1;
		} else if (warningAudioCheckBox.isSelected()){// 由禁止转为启用，原本是禁止状态
			startOrEndPlay = 0;
		}
		// playStatus = 0;
		warningAudioPlayer.playStatus = false;
	}

	// 用于播放告警声音
	private void play(URL fileURL, int playNum) {
//		File file = new File(wavFileName);
//		if (file.exists()) {
//			try {
			warningAudioPlayer.play(playNum, fileURL, false);
//			} catch (MalformedURLException e) {
//				JOptionPane.showMessageDialog(this, "播放出错，文件不存在", "提示",JOptionPane.NO_OPTION);
//				warningAudioPlayer.playStatus = false;
//				return;
//			}
//		} else {
//			JOptionPane.showMessageDialog(this, "播放出错，文件不存在", "提示",JOptionPane.NO_OPTION);
//			warningAudioPlayer.playStatus = false;
//			return;
//		}
	}

	@Resource(name = LocalizationManager.ID)
	private LocalizationManager localizationManager;

	@Resource(name = DateFormatter.ID)
	private DateFormatter dataFormatter;

	@Resource(name = AlarmSeverity.ID)
	private AlarmSeverity alarmSeverity;

	@Resource(name = AlarmModel.ID)
	private AlarmModel alarmModel;

	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;

	@Resource(name = WarningAudioPlayer.ID)
	private WarningAudioPlayer warningAudioPlayer;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=AlarmTypeCategory.ID)
	private AlarmTypeCategory alarmTypeCategory;
	
	@Resource(name=ResourceManager.ID)
	private ResourceManager resourceManager;
	
	private JLabel statusLabel;
	
	private JPopupMenu popup = null;
	private static URL DEFAULT_WARNING_SOUND_URL = null;
	private URL playUrl = null;
	private JXTable warningTable;
	private WarningTableModel warningTableModel;
	private JCheckBox warningAudioCheckBox = null;
	private static final String STATUS_PATTERN = "告警共计%s条";
	private static final long serialVersionUID = -695974871802510930L;
	private static final Logger LOG = LoggerFactory.getLogger(EventConsoleView.class);
	public static final String ID = "eventConsoleView";

	private class WarningAlarmSeverityHighlighter extends AbstractHighlighter {
		@Override
		protected java.awt.Component doHighlight(java.awt.Component component,
				ComponentAdapter adapter) {
			WarningEntity warningEntity = warningTableModel.getValue(adapter.row);
			component.setBackground(alarmSeverity.getColor(warningEntity.getWarningLevel()));
			return component;
		}

		private static final long serialVersionUID = -8795784237395864733L;
	}

	private class WarningTableModel extends AbstractTableModel {

		public WarningTableModel() {
			warningAlarms = new ArrayList<WarningEntity>();

			int modelIndex = 0;
			warningColumnModel = new DefaultTableColumnModel();
			
			//时间
			TableColumnExt raisedTimeColumn = new TableColumnExt(modelIndex++,170);
			raisedTimeColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_RAISED_TIME);
			raisedTimeColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_RAISED_TIME));
			warningColumnModel.addColumn(raisedTimeColumn);
			//设备
			TableColumnExt equipmentColumn = new TableColumnExt(modelIndex++,120);
			equipmentColumn.setHighlighters(new WarningAlarmSeverityHighlighter());
			equipmentColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_EQUIPMENT);
			equipmentColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_EQUIPMENT));
			warningColumnModel.addColumn(equipmentColumn);
			//内容
			TableColumnExt contentColumn = new TableColumnExt(modelIndex++, 330);
			contentColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_CONTENT);
			contentColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_CONTENT));
			warningColumnModel.addColumn(contentColumn);
			//级别
//			TableColumnExt severityColumn = new TableColumnExt(modelIndex++, 60);
//			severityColumn.setHighlighters(new WarningAlarmSeverityHighlighter());
//			severityColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_SEVERITY);
//			severityColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_SEVERITY));
//			warningColumnModel.addColumn(severityColumn);
			//类型
			TableColumnExt equipmentTypeColumn = new TableColumnExt(modelIndex++, 50);
			equipmentTypeColumn.setIdentifier(ResourceConstants.ALARM_COLUMN_EQUIPMENT_TYPE);
			equipmentTypeColumn.setHeaderValue(localizationManager.getString(ResourceConstants.ALARM_COLUMN_EQUIPMENT_TYPE));
			warningColumnModel.addColumn(equipmentTypeColumn);
		}
		
		public void clean() {
			warningAlarms.clear();
			fireTableDataChanged();
		}

		@Override
		public int getColumnCount() {
			return warningColumnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return warningAlarms.size();
		}

		@Override
		public String getColumnName(int col) {
			return warningColumnModel.getColumn(col).getHeaderValue()
					.toString();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if (row < warningAlarms.size()) {
				WarningEntity alarm = warningAlarms.get(row);
				switch (col) {
				case 0://时间
					value = dataFormatter.format(alarm.getCreateDate());
					break;
				case 1://来源
					value = alarm.getNodeName();
					break;
				case 2://内容
					value = alarm.getDescs();
					break;
//				case 3://级别
//					value = alarmSeverity.get(alarm.getWarningLevel()).getKey();
//					break;
				case 3://类型
					value = alarmTypeCategory.get(alarm.getWarningCategory()).getKey();
					break;
				default:
					break;
				}
			}
			return value;
		}

		public TableColumnModel getColumnModel() {
			return warningColumnModel;
		}

		public WarningEntity getValue(int row) {
			WarningEntity value = null;
			if (row < warningAlarms.size()) {
				value = warningAlarms.get(row);
			}
			return value;
		}

		public void setWarningAlarms(List<WarningEntity> alarms) {
			if (alarms == null) {
				return;
			}
			this.warningAlarms = alarms;
		}

		public void addWarningAlarms(WarningEntity warning) {
			if (null != warning){// 将最新的添加至最前端
				this.warningAlarms.add(0, warning);
			}
			this.setWarningAlarms(warningAlarms);
		}
		
		public void confirmWarningAlarms(List<WarningEntity> warningEntityList){
			if(null == warningEntityList || warningEntityList.size() < 0){
				return;
			}
			for(WarningEntity warningEntity : warningEntityList){
				this.warningAlarms.remove(warningEntity);
			}
		}
		
		public List<WarningEntity> getWarningAlarms() {
			if(null == warningAlarms || warningAlarms.size() < 0){
				return new ArrayList<WarningEntity>();
			}
			return this.warningAlarms;
		}
		
		@Override
		public void fireTableDataChanged(){
			if(SwingUtilities.isEventDispatchThread()){
				super.fireTableDataChanged();
			}else {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fireTableDataChanged();
					}
				});
			}
		}

		private List<WarningEntity> warningAlarms;
		private final TableColumnModel warningColumnModel;
		private static final long serialVersionUID = -3065768803494840568L;
	}
}