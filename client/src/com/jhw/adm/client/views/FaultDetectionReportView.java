package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.EXPORT;
import static com.jhw.adm.client.core.ActionConstants.FIRST;
import static com.jhw.adm.client.core.ActionConstants.LAST;
import static com.jhw.adm.client.core.ActionConstants.NEXT;
import static com.jhw.adm.client.core.ActionConstants.PREVIOUS;
import static com.jhw.adm.client.core.ActionConstants.QUERY;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
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

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.PingResultStatus;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.swing.MessagePromptDialog;
import com.jhw.adm.client.util.FileImportAndExport;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.TrapWarningBean;
import com.jhw.adm.server.entity.warning.FaultDetectionRecord;

@Component(FaultDetectionReportView.ID)
@Scope(Scopes.DESKTOP)
public class FaultDetectionReportView extends ViewPart {
	public static final String ID = "faultDetectionReportView";
	private static final long serialVersionUID = 1L;
	
	private JXTable table = null;
	private ReportTableModel tableModel = null;
	
	private JXDatePicker fromPicker;
	
	private JXDatePicker thruPicker;
	
	private JComboBox statusCombox;
	
	private JTextField equipmentField;
	
	private final JComboBox pageCountBox = new JComboBox();
	
	private ButtonFactory buttonFactory;
	
	private final static String[] COLUMN_NAME = {"故障时间", "设备", "故障内容", "状态"};
	
	private JLabel statusLabel;
	private static final String STATUS_PATTERN = "共%s页，每页最多显示16条故障探测记录";
	private final static int maxPageSize = 16;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name = PingResultStatus.ID)
	private PingResultStatus pingResultStatus;
	
	@Resource(name = DateFormatter.ID)
	private DateFormatter dataFormatter;
	
	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	protected void initialize() {
		init();
	}
	
	protected void init() {
		setTitle("故障探测报表");
		buttonFactory = actionManager.getButtonFactory(this); 
		setViewSize(800, 600);
		setLayout(new BorderLayout());

//		JPanel toolPanel = new JPanel(new GridLayout(2, 1));
		JPanel toolPanel = new JPanel(new BorderLayout());
		toolPanel.setBorder(BorderFactory.createTitledBorder("查询条件"));
		
		//**************************
		//查询面板
		JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		toolPanel.add(queryPanel,BorderLayout.CENTER);

		queryPanel.add(new JLabel("开始时间"));
		fromPicker = new JXDatePicker();
		fromPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(fromPicker);

		queryPanel.add(new JLabel("到"));
		thruPicker = new JXDatePicker();
		thruPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(thruPicker);
		
		equipmentField = new JTextField(20);
		queryPanel.add(new JLabel("设备"));
		queryPanel.add(equipmentField);
		
		statusCombox = new JComboBox(new ListComboBoxModel(
				pingResultStatus.toListIncludeAll()));
		statusCombox.setSelectedIndex(0);
		queryPanel.add(new JLabel("状态"));
		queryPanel.add(statusCombox);
		statusCombox.setPreferredSize(new Dimension(120, statusCombox.getPreferredSize().height));
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel middlePnl = new JPanel(new GridBagLayout());
		JButton queryBtn = buttonFactory.createButton(QUERY);
		JButton exportBtn = buttonFactory.createButton(EXPORT);
		JButton deleteBtn = buttonFactory.createButton(DELETE);
		middlePnl.add(queryBtn,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		middlePnl.add(exportBtn,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,0,0),0,0));
		middlePnl.add(deleteBtn,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,0,0),0,0));
		buttonPanel.add(middlePnl);
		toolPanel.add(buttonPanel,BorderLayout.SOUTH);
		
		tableModel = new ReportTableModel();
		tableModel.setColumnName(COLUMN_NAME);
		
		table = new JXTable();
		table.setModel(tableModel);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(true);
		table.setHighlighters(HighlighterFactory.createAlternateStriping(2));
		setTableWidth();
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
		JPanel pageButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		statusLabel = new JLabel();
		statusLabel.setText(String.format(STATUS_PATTERN, 0));
		
		JButton firstBtn = buttonFactory.createButton(FIRST);
		JButton previousBtn = buttonFactory.createButton(PREVIOUS);
		JButton nextBtn = buttonFactory.createButton(NEXT);
		JButton lastBtn = buttonFactory.createButton(LAST); 

		pageButtonPanel.add(firstBtn);
		pageButtonPanel.add(previousBtn);
		pageButtonPanel.add(nextBtn);
		pageButtonPanel.add(lastBtn);
		pageButtonPanel.add(pageCountBox);
		
		paginationPanel.add(statusLabel, BorderLayout.CENTER);
		paginationPanel.add(pageButtonPanel, BorderLayout.LINE_END);
		
		pageCountBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						warnInfoAction();
					}
				});
			}
		});

		add(toolPanel, BorderLayout.PAGE_START);
		add(scrollTablePanel, BorderLayout.CENTER);
		add(paginationPanel,BorderLayout.SOUTH);
	}
	
	private void setTableWidth(){
		table.getColumn(0).setPreferredWidth(50);
		table.getColumn(1).setPreferredWidth(50);
		table.getColumn(2).setPreferredWidth(300);
		table.getColumn(3).setPreferredWidth(20);
	}
	
	private void setPageBoxItem(long count){
		long page = 0; 
		if(count%maxPageSize ==0){
			page = count/maxPageSize;
		}
		else{
			page = count/maxPageSize + 1;
		}

		statusLabel.setText(String.format(STATUS_PATTERN, page));
		
		pageCountBox.removeAllItems();
		for(int i = 1 ; i <= page; i++){
			pageCountBox.addItem(i);
		}
		statusLabel.setText(String.format(STATUS_PATTERN, page));
	}
	
	@ViewAction(name=QUERY, icon=ButtonConstants.QUERY,desc="查询故障探测报表",role=Constants.MANAGERCODE)
	public void query(){
		Date startDate = fromPicker.getDate();
		Date endDate = thruPicker.getDate();
		if ((startDate != null) && (endDate != null)){
			long startTime = startDate.getTime();
			long endTime = endDate.getTime();
			if (startTime > endTime){
				JOptionPane.showMessageDialog(this, "开始时间大于结束时间，请重新输入","提示",JOptionPane.NO_OPTION);
				return;
			}
		}
		String ipValue = equipmentField.getText().trim();
		
		int status = ((StringInteger)statusCombox.getSelectedItem()).getValue();
		
		TrapWarningBean bean = new TrapWarningBean();
		bean.setStartDate(startDate);
		bean.setEndDate(endDate);
		bean.setIpValue(ipValue);
		bean.setStatus(status);
		bean.setUserName(clientModel.getCurrentUser().getUserName());
		
		
		long count = remoteServer.getNmsService().queryAllFaultDetection(bean);
//		if(count > 0){
			setPageBoxItem(count);
//		}
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=EXPORT, icon=ButtonConstants.EXPORT,desc="导出故障探测报表",role=Constants.MANAGERCODE)
	public void export(){
		Date startDate = fromPicker.getDate();
		Date endDate = thruPicker.getDate();
		String ipValue = equipmentField.getText().trim();
		
		TrapWarningBean bean = new TrapWarningBean();
		bean.setStartDate(startDate);
		bean.setEndDate(endDate);
		bean.setIpValue(ipValue);
		
		int status = ((StringInteger)statusCombox.getSelectedItem()).getValue();
		bean.setStatus(status);

		bean.setUserName(clientModel.getCurrentUser().getUserName());
		
		bean.setMaxPageSize(0);
		bean.setStartPage(0);
		
		List<FaultDetectionRecord> faultDetectionRecordList = remoteServer.getNmsService().findFaultDetection(bean);
		
		String[] propertiesName = new String[5];
		propertiesName[0] = "故障时间";
		propertiesName[1] = "设备";
		propertiesName[2] = "故障内容";
		propertiesName[3] = "状态";
		List<HashMap> list = new ArrayList<HashMap>();
	
		for(int i = 0;i < faultDetectionRecordList.size();i++){
			HashMap hashMap = new HashMap();
			hashMap.put(propertiesName[0], dataFormatter.format(faultDetectionRecordList.get(i).getCreateDate()));
			hashMap.put(propertiesName[1], faultDetectionRecordList.get(i).getDevice());
			hashMap.put(propertiesName[2], faultDetectionRecordList.get(i).getWarningDescs());
			hashMap.put(propertiesName[3], pingResultStatus.get(faultDetectionRecordList.get(i).getStatus()).getKey());
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
	
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE,desc="删除故障探测报表",role=Constants.MANAGERCODE)
	public void delete(){
		int rowCount = table.getSelectedRowCount();
		if (rowCount < 0){
			return;
		}

		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		
		List<FaultDetectionRecord> list = new ArrayList<FaultDetectionRecord>();
		for(int i = 0 ; i < rowCount; i++){
			FaultDetectionRecord faultDetectionRecord = tableModel.getSelectRowValue(table.getSelectedRows()[i]);
			list.add(faultDetectionRecord);
		}
		
		remoteServer.getService().deleteEntities(list);
		
		final String operate = "删除";
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				//打开消息提示对话框
				openMessageDialog(operate);
			}
		});
	}
	
	/**
	 * 显示结果对话框
	 */
	private void openMessageDialog(String operate){
		MessagePromptDialog messageDlg = new MessagePromptDialog(this,operate,imageRegistry,remoteServer);
		warnInfoAction();
		messageDlg.setMessage("操作完成");
		messageDlg.setVisible(true);
	}
	
	@ViewAction(name=FIRST, icon=ButtonConstants.FIRST,desc="显示故障探测报表第一页",role=Constants.MANAGERCODE)
	public void first(){
		pageCountBox.setSelectedIndex(0);
	}
	
	@ViewAction(name=PREVIOUS, icon=ButtonConstants.PREVIOUS,desc="显示故障探测报表上一页",role=Constants.MANAGERCODE)
	public void previous(){
		if (pageCountBox.getSelectedIndex() <=0){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() -1);
	}
	
	@ViewAction(name=NEXT, icon=ButtonConstants.NEXT,desc="显示故障探测报表下一页",role=Constants.MANAGERCODE)
	public void next(){
		if (pageCountBox.getSelectedIndex() >= pageCountBox.getItemCount()-1){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() +1);
	}
	
	@ViewAction(name=LAST, icon=ButtonConstants.LAST,desc="显示故障探测报表最后页",role=Constants.MANAGERCODE)
	public void last(){
		pageCountBox.setSelectedIndex(pageCountBox.getItemCount()-1);
	}
	
	/**
	 * pageCountBox的监听事件
	 */
	private void warnInfoAction(){
		int page = pageCountBox.getSelectedIndex()+1;
		
		Date startDate = fromPicker.getDate();
		Date endDate = thruPicker.getDate();
		String ipValue = equipmentField.getText().trim();
		
		int status = ((StringInteger)statusCombox.getSelectedItem()).getValue();
		
		TrapWarningBean bean = new TrapWarningBean();
		bean.setStartDate(startDate);
		bean.setEndDate(endDate);
		bean.setIpValue(ipValue);
		bean.setStatus(status);
		bean.setUserName(clientModel.getCurrentUser().getUserName());
		bean.setMaxPageSize(maxPageSize);
		bean.setStartPage(page);
		
		List<FaultDetectionRecord> list = remoteServer.getNmsService().findFaultDetection(bean);
		if(null != list){
			setTableValue(list);
		}else{
			setTableValue(new ArrayList<FaultDetectionRecord>());
		}
	}
	
	private void setTableValue(List<FaultDetectionRecord> list){
		tableModel.setDataList(list);
		tableModel.fireTableDataChanged();
	}
	
	/**
	 * @author Administrator
	 */
	class ReportTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private String[] columnName = null;
		
		private List<FaultDetectionRecord> dataList = null;
		
		public ReportTableModel(){
			super();
		}
		
		public String[] getColumnName() {
			return columnName;
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public List<FaultDetectionRecord> getDataList() {
			return dataList;
		}

		public void setDataList(List<FaultDetectionRecord> dataList) {
			if(null == dataList)return;
			this.dataList = dataList;
		}

		public int getRowCount(){
			if (null == dataList){
				return 0;
			}
			return dataList.size();
		}

	    
	    public int getColumnCount(){
	    	if (null == columnName){
	    		return 0;
	    	}
	    	return columnName.length;
	    }

	    @Override
		public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }

	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	
	    @Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if (row < dataList.size()) {
				FaultDetectionRecord fault = dataList.get(row);
				switch (col) {
				case 0:
					value = dataFormatter.format(fault.getCreateDate());
					break;
				case 1:
					value = fault.getDevice();
					break;
				case 2:
					value = fault.getWarningDescs();
					break;
				case 3:
					value = pingResultStatus.get(fault.getStatus()).getKey();
					break;
				default:
					break;
				}
			}

			return value;
		}
	    
	    public FaultDetectionRecord getSelectRowValue(int row) {
	    	FaultDetectionRecord value = null;
			if (row < dataList.size()) {
				value = dataList.get(row);
			}
			return value;
		}
	}
}