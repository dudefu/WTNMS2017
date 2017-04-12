package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.AlarmTypeCategory;
import com.jhw.adm.server.entity.warning.WarningEntity;
import com.jhw.adm.server.entity.warning.WarningHistoryEntity;

@Component(WarningHistoryDetailView.ID)
@Scope(Scopes.DESKTOP)
public class WarningHistoryDetailView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "warningHistoryDetailView";
	
	private final JPanel detailPanel = new JPanel();
	private JXTable table = new JXTable();
	private WarningHistoryDetailTableModel model = new WarningHistoryDetailTableModel();
	private JTextArea contentArea = null;
	private JTextArea commentArea = null;
	
	private final JPanel buttonPanel = new JPanel();
	private JButton closeButton = new JButton();
	private ButtonFactory buttonFactory;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DateFormatter.ID)
	private DateFormatter dateFormatter;
	
	@Resource(name=AlarmSeverity.ID)
	private AlarmSeverity alarmSeverity;
	
	@Resource(name=AlarmTypeCategory.ID)
	private AlarmTypeCategory alarmTypeCategory;
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this);
		
		createDetailPanel(detailPanel);
		createButtonPanel(buttonPanel);
		
		this.setLayout(new BorderLayout());
		this.add(detailPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.PAGE_END);
		this.setViewSize(800, 600);
		this.setTitle("告警历史详细信息");
		
		initialTableData();
	}

	private void initialTableData() {
		WarningHistoryEntity warningHistoryEntity = alarmModel.getDetailHistoryAlarm();
		
		if(null == warningHistoryEntity){
			return;
		}
		List<WarningEntity> warningEntityList = remoteServer.getNmsService().queryWarningFromHistory(warningHistoryEntity);
		if(null != warningEntityList && warningEntityList.size() > 0){
			fireTableData(warningEntityList);
		}
	}
	
	private void fireTableData(final List<WarningEntity> warningEntityList) {
		if(SwingUtilities.isEventDispatchThread()){
			model.setAlarms(warningEntityList);
			model.fireTableDataChanged();
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireTableData(warningEntityList);
				}
			});
		}
	}

	private void selectOneRow() {
		if (table.getSelectedRow() > - 1) {
			WarningEntity warning = model.getValue(table.getSelectedRow());
			this.contentArea.setText(warning.getDescs());
			this.commentArea.setText(warning.getComment());
		}
	}

	private void createDetailPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		table = new JXTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(true);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						selectOneRow();
					}
				});

		model = new WarningHistoryDetailTableModel();
		table.setModel(model);
		table.setColumnModel(model.getColumnModel());
		table.getTableHeader().setReorderingAllowed(false);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createTitledBorder("查询结果"));
		scrollPane.getViewport().add(table, null);
		scrollPane.getHorizontalScrollBar().setFocusable(false);
		scrollPane.getVerticalScrollBar().setFocusable(false);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		
		JPanel detailPanel = new JPanel();
		createMessageDetail(detailPanel);
//		detailPanel.setBorder(BorderFactory.createTitledBorder("详细内容"));
		
		parent.add(scrollPane, BorderLayout.CENTER);
		parent.add(detailPanel, BorderLayout.PAGE_END);
	}
	
	private void createMessageDetail(JPanel parent) {
		GridLayout gridLayout = new GridLayout(1, 2);
		parent.setLayout(gridLayout);
		contentArea = new JTextArea(3, 1);
		contentArea.setEditable(false);
//		contentArea.setBorder(new JTextField().getBorder());
		contentArea.setLineWrap(true);		
		contentArea.setBorder(BorderFactory.createTitledBorder("内容"));
		JScrollPane contentScrollPane = new JScrollPane(contentArea);
		contentScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		contentScrollPane.getVerticalScrollBar().setUnitIncrement(30);
		contentArea.setCaretPosition(0);
		
		commentArea = new JTextArea(3, 1);
		commentArea.setEditable(false);
//		commentArea.setBorder(new JTextField().getBorder());
		commentArea.setLineWrap(true);		
		commentArea.setBorder(BorderFactory.createTitledBorder("描述"));		
		JScrollPane commentScrollPane = new JScrollPane(commentArea);
		commentScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		commentScrollPane.getVerticalScrollBar().setUnitIncrement(30);
		commentArea.setCaretPosition(0);

//		JSplitPane splitPane = new JSplitPane();
//		splitPane.setDividerSize(1);
//		splitPane.setDividerLocation(400);
//		splitPane.setLeftComponent(contentScrollPane);
//		splitPane.setRightComponent(commentScrollPane);
//		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		parent.add(contentArea);
		parent.add(commentArea);
	}

	private void createButtonPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		closeButton = buttonFactory.createCloseButton();
		parent.add(closeButton);
		this.setCloseButton(closeButton);
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
	
	private class WarningHistoryDetailTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private final TableColumnModelExt columnModel;
		private List<WarningEntity> warningList;
		
		public WarningHistoryDetailTableModel(){
			warningList = new ArrayList<WarningEntity>();
			
			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			
			TableColumnExt originColumn = new TableColumnExt(modelIndex++, 200);
			originColumn.setIdentifier("来源");
			originColumn.setTitle("来源");
			columnModel.addColumn(originColumn);
			
			TableColumnExt contentColumn = new TableColumnExt(modelIndex++, 250);
			contentColumn.setIdentifier("内容");
			contentColumn.setTitle("内容");
			columnModel.addColumn(contentColumn);
			
			TableColumnExt levelColumn = new TableColumnExt(modelIndex++, 60);
			levelColumn.setIdentifier("级别");
			levelColumn.setTitle("级别");
			columnModel.addColumn(levelColumn);

			TableColumnExt typeColumn = new TableColumnExt(modelIndex++, 60);
			typeColumn.setIdentifier("类型");
			typeColumn.setTitle("类型");
			columnModel.addColumn(typeColumn);
			
			TableColumnExt subnetColumn = new TableColumnExt(modelIndex++, 120);
			subnetColumn.setIdentifier("所属子网");
			subnetColumn.setTitle("所属子网");
			columnModel.addColumn(subnetColumn);

			TableColumnExt createTimeColumn = new TableColumnExt(modelIndex++, 200);
			createTimeColumn.setIdentifier("发生时间");
			createTimeColumn.setTitle("发生时间");
			columnModel.addColumn(createTimeColumn);

			TableColumnExt confirmTimeColumn = new TableColumnExt(modelIndex++, 200);
			confirmTimeColumn.setIdentifier("确认时间");
			confirmTimeColumn.setTitle("确认时间");
			columnModel.addColumn(confirmTimeColumn);

			TableColumnExt ConfirmUserColumn = new TableColumnExt(modelIndex++, 100);
			ConfirmUserColumn.setIdentifier("确认人员");
			ConfirmUserColumn.setTitle("确认人员");
			columnModel.addColumn(ConfirmUserColumn);

		}
		
		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return warningList.size();
		}
		
		@Override
		public String getColumnName(int col) {
			return columnModel.getColumnExt(col).getTitle();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if(warningList != null){
				if (row < warningList.size()) {
					WarningEntity warning = warningList.get(row);
					switch (col) {
					case 0:
						value = warning.getNodeName();
						break;
					case 1:
						value = warning.getDescs();
						break;
					case 2:
						value = alarmSeverity.get(warning.getWarningLevel()).getKey();
						break;
					case 3:
						value = alarmTypeCategory.get(warning.getWarningCategory());
						break;
					case 4:
						value = warning.getSubnetName();
						break;
					case 5:
						value = dateFormatter.format(warning.getCreateDate());
						break;
					case 6:
						value = dateFormatter.format(warning.getConfirmTime());
						break;
					case 7:
						value = warning.getConfirmUserName();
						break;
					default:
						break;
					}
				}
			}
			return value;
		}
		
		public TableColumnModel getColumnModel() {
			return columnModel;
		}

		public WarningEntity getValue(int row) {
			WarningEntity value = null;
			if (row < warningList.size()) {
				value = warningList.get(row);
			}

			return value;
		}

		public void setAlarms(List<WarningEntity> data) {
			if (data == null) {
				return;
			}
			this.warningList = data;
		}
	}
}
