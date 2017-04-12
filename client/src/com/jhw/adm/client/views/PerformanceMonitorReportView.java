package com.jhw.adm.client.views;

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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
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
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.util.FileImportAndExport;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.RmonCount;

/**
 * 性能监控报表视图
 */ 
@Component(PerformanceMonitorReportView.ID)
@Scope(Scopes.DESKTOP)
public class PerformanceMonitorReportView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "performanceMonitorReportView";
	
	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name = DateFormatter.ID)
	private DateFormatter dateFormatter;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	private JXTable table;
	private RmonCountTableModel tableModel;
	
	private JXDatePicker fromPicker;
	private JXDatePicker toPicker;
	private JTextField equipmentField;
	private NumberField portFld;
	
	private final JComboBox pageCountBox = new JComboBox();
	
	private ButtonFactory buttonFactory; 
	private JButton queryBtn;
	private JButton exportBtn;
	
	private JLabel statusLabel;
	private static final String STATUS_PATTERN = "共%s页，每页最多显示16条性能监控数据";
	private static final int PAGE_SIZE = 16;
	
	@PostConstruct
	protected void initialize() {
		buttonFactory = actionManager.getButtonFactory(this); 
		setTitle("实时监控报表");
		setLayout(new BorderLayout());
		createContents(this);
	}
	
	private void createContents(JPanel parent){
		
		JPanel toolPanel = new JPanel(new GridLayout(2, 1));
		toolPanel.setBorder(BorderFactory.createTitledBorder("查询条件"));
		//**************************
		//查询面板
		JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 250, 0));
		
		queryPanel.add(new JLabel("开始时间"));
		fromPicker = new JXDatePicker();
		fromPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(fromPicker);

		queryPanel.add(new JLabel("到"));
		toPicker = new JXDatePicker();
		toPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(toPicker);
		
		queryPanel.add(new JLabel("监控设备"));
		equipmentField = new JTextField(20);
		queryPanel.add(equipmentField);

		queryPanel.add(new JLabel("监控端口"));
		portFld = new NumberField(2,0,1,100,true);
		queryPanel.add(portFld);
		
		exportBtn = buttonFactory.createButton(EXPORT);
		queryBtn = buttonFactory.createButton(QUERY);
		buttonPnl.add(exportBtn);
		buttonPnl.add(queryBtn);	
		
		toolPanel.add(queryPanel);
		toolPanel.add(buttonPnl);
		
		table = new JXTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(true);
		table.setHighlighters(HighlighterFactory.createAlternateStriping(3));
		tableModel = new RmonCountTableModel();
		table.setModel(tableModel);
		table.setColumnModel(tableModel.getColumnModel());
		table.getTableHeader().setReorderingAllowed(false);
		
		JPanel tabelPanel = new JPanel(new BorderLayout(1, 2));
		tabelPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tabelPanel.add(table, BorderLayout.CENTER);	
				
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.setBorder(BorderFactory.createTitledBorder("查询结果"));
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);
		
		//分页画面
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
						MonitorAction();
					}
				});
			}
		});
		
		parent.add(toolPanel,BorderLayout.NORTH);
		parent.add(scrollTablePanel, BorderLayout.CENTER);
		parent.add(paginationPanel,BorderLayout.SOUTH);		
	}
	
	public void setTableValue(List<RmonCount> list)
	{
		tableModel.setData(list);
		tableModel.fireTableDataChanged();
	}
	
	private void MonitorAction(){
		int page = pageCountBox.getSelectedIndex() + 1;
		
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		String equipmentIP = equipmentField.getText().trim();
		int portNumber = -1;
		if(null != portFld.getText() && !"".equals(portFld.getText())){
			portNumber = Integer.parseInt(portFld.getText());
		}
		
		List<RmonCount> rmonCountList = remoteServer.getNmsService()
				.queryRmonCount(startDate, endDate, equipmentIP, portNumber,
						PAGE_SIZE, page);
		
		if(rmonCountList != null){
			setTableValue(rmonCountList);
		}else{
			rmonCountList = new ArrayList<RmonCount>();
			setTableValue(rmonCountList);
		}
	}
	
	@ViewAction(name=QUERY,icon=ButtonConstants.QUERY,desc="查询性能检测数据",role=Constants.MANAGERCODE)
	public void query(){
//		setTableValue(null);
		
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		String equipmentIP = equipmentField.getText().trim();
		int portNumber = -1;
		if(null != portFld.getText() && !"".equals(portFld.getText())){
			portNumber = Integer.parseInt(portFld.getText());
		}
		
		long count = remoteServer.getNmsService().queryAllRmonCountNum(startDate, endDate, equipmentIP, portNumber);
//		if(count > 0){
			setPageBoxItem(count);
//		}
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=EXPORT,icon=ButtonConstants.EXPORT,desc="导出定时监控数据",role=Constants.MANAGERCODE)
	public void export(){
		
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		String equipmentIP = equipmentField.getText().trim();
		int portNumber = -1;
		if(null != portFld.getText() && !"".equals(portFld.getText())){
			portNumber = Integer.parseInt(portFld.getText());
		}
		
		List<RmonCount> rmonCountList = remoteServer.getNmsService()
				.queryRmonCount(startDate, endDate, equipmentIP,portNumber, 0, 0);
		
		String[] propertiesName = new String[5];
		propertiesName[0] = "时间";
		propertiesName[1] = "设备";
		propertiesName[2] = "端口";
		propertiesName[3] = "参数";
		propertiesName[4] = "数值";
		List<HashMap> list = new ArrayList<HashMap>();
		for(int i = 0;i < rmonCountList.size();i++){
			HashMap hashMap = new HashMap();
			hashMap.put(propertiesName[0], dateFormatter.format(rmonCountList.get(i).getSampleTime()));
			hashMap.put(propertiesName[1], rmonCountList.get(i).getIpValue());
			hashMap.put(propertiesName[2], String.valueOf(rmonCountList.get(i).getPortNo()));
			hashMap.put(propertiesName[3], rmonCountList.get(i).getParam());
			hashMap.put(propertiesName[4], String.valueOf(rmonCountList.get(i).getValue()));
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
	
	@ViewAction(name=FIRST, icon=ButtonConstants.FIRST,desc="显示性能检测数据第一页",role=Constants.MANAGERCODE)
	public void first(){
		pageCountBox.setSelectedIndex(0);
	}
	
	@ViewAction(name=PREVIOUS, icon=ButtonConstants.PREVIOUS,desc="显示性能检测数据上一页",role=Constants.MANAGERCODE)
	public void previous(){
		if (pageCountBox.getSelectedIndex() <=0){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() -1);
	}
	
	@ViewAction(name=NEXT, icon=ButtonConstants.NEXT,desc="显示性能检测数据下一页",role=Constants.MANAGERCODE)
	public void next(){
		if (pageCountBox.getSelectedIndex() >= pageCountBox.getItemCount()-1){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() +1);
	}
	
	@ViewAction(name=LAST, icon=ButtonConstants.LAST,desc="显示性能检测数据最后页",role=Constants.MANAGERCODE)
	public void last(){
		pageCountBox.setSelectedIndex(pageCountBox.getItemCount()-1);
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

	private class RmonCountTableModel extends AbstractTableModel {

		public RmonCountTableModel() {
			data = Collections.emptyList();

			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			TableColumnExt sampleTimeColumn = new TableColumnExt(modelIndex++, 120);
			sampleTimeColumn.setIdentifier("TIME");
			sampleTimeColumn.setTitle(localizationManager.getString("TIME"));
			columnModel.addColumn(sampleTimeColumn);

			TableColumnExt equipmentColumn = new TableColumnExt(modelIndex++, 200);
			equipmentColumn.setIdentifier("EQUIPMENT");
			equipmentColumn.setTitle(localizationManager.getString("EQUIPMENT"));
			columnModel.addColumn(equipmentColumn);
			
			TableColumnExt portColumn = new TableColumnExt(modelIndex++, 80);
			portColumn.setIdentifier("PORT");
			portColumn.setTitle(localizationManager.getString("PORT"));
			columnModel.addColumn(portColumn);

			TableColumnExt parameterColumn = new TableColumnExt(modelIndex++, 100);
			parameterColumn.setIdentifier("PARAM");
			parameterColumn.setTitle(localizationManager.getString("PARAM"));
			columnModel.addColumn(parameterColumn);

			TableColumnExt valueColumn = new TableColumnExt(modelIndex++, 100);
			valueColumn.setIdentifier("VALUE");
			valueColumn.setTitle(localizationManager.getString("VALUE"));
			columnModel.addColumn(valueColumn);
		}

		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public String getColumnName(int col) {
			return columnModel.getColumnExt(col).getTitle();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if (row < data.size()) {
				RmonCount rmonCount = data.get(row);

				switch (col) {
				case 0:
					value = dateFormatter.format(rmonCount.getSampleTime());
					break;
				case 1:
					value = rmonCount.getIpValue();
					break;
				case 2:
					value = rmonCount.getPortNo();
					break;
				case 3:
					value = rmonCount.getParam();
					break;
				case 4:
					value = rmonCount.getValue();
					break;
				default:
					break;
				}
			}

			return value;
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public TableColumnModel getColumnModel() {
			return columnModel;
		}

		public RmonCount getValue(int row) {
			RmonCount value = null;
			if (row < data.size()) {
				value = data.get(row);
			}

			return value;
		}

		public void setData(List<RmonCount> data) {
			if (data == null)
				return;
			this.data = data;
		}
		
		public List<RmonCount> getSelectedRows(int[] selectedRows)
		{
			List<RmonCount> rmonCountEntityLists = new ArrayList<RmonCount>();
			for(int i = 0;i < selectedRows.length;i++)
			{
				rmonCountEntityLists.add(this.data.get(selectedRows[i]));
			}
			return rmonCountEntityLists;
		}

		private List<RmonCount> data;
		private final TableColumnModelExt columnModel;
		private static final long serialVersionUID = -3065768803494840568L;
	}
}