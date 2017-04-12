package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.CLEAN;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.EXPORT;
import static com.jhw.adm.client.core.ActionConstants.FIRST;
import static com.jhw.adm.client.core.ActionConstants.LAST;
import static com.jhw.adm.client.core.ActionConstants.NEXT;
import static com.jhw.adm.client.core.ActionConstants.PREVIOUS;
import static com.jhw.adm.client.core.ActionConstants.QUERY;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.util.FileImportAndExport;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.LogEntity;

@Component(QueryLogView.ID)
@Scope(Scopes.DESKTOP)
public class QueryLogView extends ViewPart {
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "queryLogView";
	
	//time
	private JXDatePicker fromPicker;
	private JXDatePicker toPicker;

	private JTextField userNameFld;
	private IpAddressField clientIPFld;
	
	private ButtonFactory buttonFactory;
	private JButton queryBtn;
	private JButton clearBtn;//清空，删除数据库中的全部数据
	private JButton deleteBtn;//删除，删除选中的记录
	private JButton exportBtn;//导出当前查询条件下所有的数据
	
	private JLabel statusLabel;
	private static final String STATUS_PATTERN = "共%s页，每页最多显示16条日志信息";
	
	private JTextArea contentArea;
	private JXTable table;
	private LogReportTableModel tableModel;
	private final JComboBox pageCountBox = new JComboBox();
	
	private static final int PAGE_SIZE = 16;
	
	@Resource(name=DateFormatter.ID)
	private DateFormatter dateFormatter;
	
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	public void initialize()
	{
		setTitle("日志信息管理");
		setLayout(new BorderLayout());
		createContents(this);
	}
	
	private void createContents(JPanel parent) {
		
		buttonFactory = actionManager.getButtonFactory(this);
		
		JPanel toolPanel = new JPanel(new GridLayout(2, 1));
		toolPanel.setBorder(BorderFactory.createTitledBorder("查询条件"));
		
		JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		queryPanel.add(new JLabel("开始时间"));
		fromPicker = new JXDatePicker();
		fromPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(fromPicker);
		
		queryPanel.add(new JLabel("到"));
		toPicker = new JXDatePicker();
		toPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(toPicker);
		
		queryPanel.add(new JLabel("操作员"));
		userNameFld = new JTextField("",20);
		userNameFld.setDocument(new TextFieldPlainDocument(userNameFld, 36));
		queryPanel.add(userNameFld);
		
		queryPanel.add(new JLabel("客户端IP"));
		clientIPFld = new IpAddressField();
		clientIPFld.setColumns(20);
		queryPanel.add(clientIPFld);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		queryBtn = buttonFactory.createButton(QUERY);
		clearBtn = buttonFactory.createButton(CLEAN);
		deleteBtn = buttonFactory.createButton(DELETE);
		exportBtn = buttonFactory.createButton(EXPORT);
		buttonPanel.add(queryBtn);
		buttonPanel.add(clearBtn);
		buttonPanel.add(deleteBtn);
		buttonPanel.add(exportBtn);
		
		toolPanel.add(queryPanel);
		toolPanel.add(buttonPanel);
		
		table = new JXTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
//		table.setColumnControlVisible(true);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectOneRow();
			}
		});
		
		tableModel = new LogReportTableModel();
		table.setModel(tableModel);
		table.setColumnModel(tableModel.getColumnMode());
		table.getTableHeader().setReorderingAllowed(false);
		
		JPanel tablePanel = new JPanel(new BorderLayout(1, 2));
		tablePanel.add(table.getTableHeader(),BorderLayout.PAGE_START);
		tablePanel.add(table,BorderLayout.CENTER);
		
		JPanel jPanel = new JPanel(new BorderLayout(1,2));
		jPanel.add(tablePanel,BorderLayout.CENTER);
		
		JPanel resultPanel = new JPanel(new BorderLayout());
		
		JScrollPane scroollTablePane = new JScrollPane();
		scroollTablePane.setBorder(BorderFactory.createTitledBorder("查询结果"));
		scroollTablePane.getViewport().add(table, null);
		scroollTablePane.getHorizontalScrollBar().setFocusable(false);
		scroollTablePane.getVerticalScrollBar().setFocusable(false);
		scroollTablePane.getHorizontalScrollBar().setUnitIncrement(30);
		scroollTablePane.getVerticalScrollBar().setUnitIncrement(30);
		
		JPanel detailPanel = new JPanel();
		createMessageDetail(detailPanel);
		detailPanel.setBorder(BorderFactory.createTitledBorder("详细内容"));
		
		resultPanel.add(scroollTablePane,BorderLayout.CENTER);
		resultPanel.add(detailPanel,BorderLayout.PAGE_END);
		
		JPanel paginationPanel = new JPanel(new BorderLayout());
		JPanel pageButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		statusLabel = new JLabel();
		statusLabel.setText(String.format(STATUS_PATTERN, 0));
		
		JButton firstBtn = buttonFactory.createButton(FIRST);
		JButton previosBtn = buttonFactory.createButton(PREVIOUS);
		JButton nextBtn = buttonFactory.createButton(NEXT);
		JButton lastBtn = buttonFactory.createButton(LAST);
		
		pageButtonPanel.add(firstBtn);
		pageButtonPanel.add(previosBtn);
		pageButtonPanel.add(nextBtn);
		pageButtonPanel.add(lastBtn);
		pageButtonPanel.add(pageCountBox);
		
		paginationPanel.add(statusLabel, BorderLayout.CENTER);
		paginationPanel.add(pageButtonPanel, BorderLayout.LINE_END);
		
		pageCountBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						logInfoAction();
					}
				});
			}
		});
		
		parent.add(toolPanel,BorderLayout.NORTH);
		parent.add(resultPanel,BorderLayout.CENTER);
		parent.add(paginationPanel,BorderLayout.SOUTH);
	}
	
	private void selectOneRow() {
		if (table.getSelectedRow() > - 1) {
			LogEntity log = tableModel.getValue(table.getSelectedRow());
			contentArea.setText(log.getContents());
		}
	}
	
	@ViewAction(name=QUERY,icon=ButtonConstants.QUERY,desc="查询操作日志信息",role=Constants.MANAGERCODE)
	public void query()
	{
//		//查询之前先清空table中的数据
//		setTableValue(null);
		
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		String userName = "";
		if(null != userNameFld.getText())
		{
			userName = userNameFld.getText().trim();
		}
		String ipAddress = "";
		if(null != clientIPFld.getIpAddress()){
			ipAddress = clientIPFld.getIpAddress();
		}
		
		long count = remoteServer.getNmsService().queryAllRowCount(startDate, endDate, userName, ipAddress);
//		if(count > 0){
			setPageBoxItem(count);
//		}
	}
	
	@ViewAction(name=CLEAN,icon=ButtonConstants.CLEAN,desc="清空操作日志信息",role=Constants.MANAGERCODE)
	public void clean()
	{
		int isClean = JOptionPane.showConfirmDialog(this, "是否要清空数据库中所有的日志记录", "提示", JOptionPane.OK_CANCEL_OPTION);
		if(isClean == 0)
		{
			remoteServer.getNmsService().clearLogEntity();
			query();
			tableModel.fireTableDataChanged();
		}
	}
	@ViewAction(name=DELETE,icon=ButtonConstants.DELETE,desc="删除操作日志信息",role=Constants.MANAGERCODE)
	public void delete(){
		int[] selectedRows = table.getSelectedRows();
		if(selectedRows.length == 0){
			JOptionPane.showMessageDialog(this, "请选择需要删除的记录", "提示", JOptionPane.NO_OPTION);
			return;
		}
		int isDelete = JOptionPane.showConfirmDialog(this, "你确定删除选中的日志记录吗?", "提示", JOptionPane.OK_CANCEL_OPTION);
		if(isDelete == JOptionPane.OK_OPTION){
			remoteServer.getService().deleteEntities(tableModel.getSelectedRows(selectedRows));
			query();
			tableModel.fireTableDataChanged();
		}
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=EXPORT,icon=ButtonConstants.EXPORT,desc="导出操作日志信息",role=Constants.MANAGERCODE)
	public void export(){
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		String userName = "";
		if(null != userNameFld.getText()){
			userName = userNameFld.getText().trim();
		}
		String ipAddress = "";
		if(null != clientIPFld.getIpAddress()){
			ipAddress = clientIPFld.getIpAddress();
		}
		
		List<LogEntity> logEntityList = remoteServer.getNmsService().queryLogEntity(startDate, endDate, userName, ipAddress, 0, 0);
		
		String[] propertiesName = new String[4];
		propertiesName[0] = "用户名称";
		propertiesName[1] = "内容";
		propertiesName[2] = "操作时间";
		propertiesName[3] = "IP地址";
		List<HashMap> list = new ArrayList<HashMap>();
		for(int i = 0;i < logEntityList.size();i++){
			HashMap hashMap = new HashMap();
			hashMap.put(propertiesName[0], logEntityList.get(i).getUserName());
			hashMap.put(propertiesName[1], logEntityList.get(i).getContents());
			hashMap.put(propertiesName[2], dateFormatter.format(logEntityList.get(i).getDoTime()));
			hashMap.put(propertiesName[3], logEntityList.get(i).getClientIp());
			list.add(hashMap);
		}
		int result = FileImportAndExport.export(this, list, propertiesName);
		if(FileImportAndExport.SUCCESS_RESULT == result){
			JOptionPane.showMessageDialog(this, "日志导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
		}else if(FileImportAndExport.CANCLE_RESULT == result){
			//if cancle, no message dialog
		}else if(FileImportAndExport.FAILURE_RESULT == result){
			JOptionPane.showMessageDialog(this, "日志导出失败", "提示", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void setTableValue(List<LogEntity> list){
		tableModel.setData(list);
		tableModel.fireTableDataChanged();
	}
	
	@ViewAction(name=FIRST, icon=ButtonConstants.FIRST,desc="显示告警信息第一页",role=Constants.MANAGERCODE)
	public void first(){
		pageCountBox.setSelectedIndex(0);
	}
	
	@ViewAction(name=PREVIOUS, icon=ButtonConstants.PREVIOUS,desc="显示告警信息上一页",role=Constants.MANAGERCODE)
	public void previous(){
		if (pageCountBox.getSelectedIndex() <=0){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() -1);
	}
	
	@ViewAction(name=NEXT, icon=ButtonConstants.NEXT,desc="显示告警信息下一页",role=Constants.MANAGERCODE)
	public void next(){
		if (pageCountBox.getSelectedIndex() >= pageCountBox.getItemCount()-1){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() +1);
	}
	
	@ViewAction(name=LAST, icon=ButtonConstants.LAST,desc="显示告警信息最后页",role=Constants.MANAGERCODE)
	public void last(){
		pageCountBox.setSelectedIndex(pageCountBox.getItemCount()-1);
	}
	
	private void logInfoAction(){
		int page = pageCountBox.getSelectedIndex() + 1;
		
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		String userName = "";
		if(null != userNameFld.getText()){
			userName = userNameFld.getText().trim();
		}
		String ipAddress = "";
		if(null != clientIPFld.getIpAddress()){
			ipAddress = clientIPFld.getIpAddress();
		}
		
		List<LogEntity> logEntityList = remoteServer.getNmsService().queryLogEntity(startDate, endDate, userName, ipAddress, PAGE_SIZE, page);

		if(logEntityList != null){
			setTableValue(logEntityList);
		}else{
			logEntityList = new ArrayList<LogEntity>();
			setTableValue(logEntityList);
		}
		contentArea.setText("");
	}
	
	private void setPageBoxItem(long count){
		long pageSize = PAGE_SIZE;
		long page = 0; 
		if(count%pageSize ==0){
			page = count/pageSize;
		}
		else{
			page = count/pageSize + 1;
		}
		
		statusLabel.setText(String.format(STATUS_PATTERN, page));
		
		pageCountBox.removeAllItems();
		for(int i = 1 ; i <= page; i++){
			pageCountBox.addItem(i);
		}
	}

	private void createMessageDetail(JPanel panel) {
		
		panel.setLayout(new BorderLayout());
		contentArea = new JTextArea(3, 1);
		contentArea.setEditable(false);
		contentArea.setBorder(new JTextField().getBorder());
		contentArea.setLineWrap(true);		
		JScrollPane scrollPane = new JScrollPane(contentArea);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		contentArea.setCaretPosition(0);
		panel.add(scrollPane, BorderLayout.CENTER);
		
	}

	public class LogReportTableModel extends AbstractTableModel{
		private static final long serialVersionUID = -3065768803494840568L;
		private List<LogEntity> logEntityList;
		private final TableColumnModelExt columnModel;
		
		public LogReportTableModel(){
			logEntityList = Collections.emptyList();
			
			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			TableColumnExt userNameColumn = new TableColumnExt(modelIndex++,100);
			userNameColumn.setIdentifier("LOG_USER_NAME");
			userNameColumn.setTitle(localizationManager.getString("LOG_USER_NAME"));
			columnModel.addColumn(userNameColumn);
			
			TableColumnExt contentsColumn = new TableColumnExt(modelIndex++,300);
			contentsColumn.setIdentifier("LOG_CONTENTS");
			contentsColumn.setTitle(localizationManager.getString("LOG_CONTENTS"));
			columnModel.addColumn(contentsColumn);
			
			TableColumnExt doTimeColumn = new TableColumnExt(modelIndex++,160);
			doTimeColumn.setIdentifier("LOG_DO_TIME");
			doTimeColumn.setTitle(localizationManager.getString("LOG_DO_TIME"));
			columnModel.addColumn(doTimeColumn);
			
			TableColumnExt clientIPColumn = new TableColumnExt(modelIndex++,100);
			clientIPColumn.setIdentifier("LOG_CLIENT_IP");
			clientIPColumn.setTitle(localizationManager.getString("LOG_CLIENT_IP"));
			columnModel.addColumn(clientIPColumn);
		}
		
		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return logEntityList.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			
			if(row < logEntityList.size())
			{
				LogEntity logEntity = logEntityList.get(row);
				switch(col)
				{
				case 0:
					value = logEntity.getUserName();
					break;
				case 1:
					value = logEntity.getContents();
					break;
				case 2:
					value = dateFormatter.format(logEntity.getDoTime());
					break;
				case 3:
					value = logEntity.getClientIp();
					break;
				default:
					break;
				}
			}
			return value;
		}
		
		@Override
		public String getColumnName(int col){
			return columnModel.getColumnExt(col).getTitle();
		}
		
		@Override
		public boolean isCellEditable(int row,int col){
			return false;
		}
		
		public TableColumnModel getColumnMode(){
			return columnModel;
		}
		
		public LogEntity getValue(int row){
			LogEntity value = null;
			if(row < logEntityList.size()){
				value = logEntityList.get(row);
			}
			return value;
		}
		
		public void setData(List<LogEntity> data){
			if(null == data) {
				return;
			}
			this.logEntityList = data;
		}
		
		public void clean() {
			logEntityList = new ArrayList<LogEntity>();
			fireTableDataChanged();
		}
		
		public List<LogEntity> getSelectedRows(int[] selectedRows){
			List<LogEntity> logEntityLists = new ArrayList<LogEntity>();
			for(int i = 0;i < selectedRows.length;i++){
				logEntityLists.add(this.logEntityList.get(selectedRows[i]));
			}
			return logEntityLists;
		}
	}
}
