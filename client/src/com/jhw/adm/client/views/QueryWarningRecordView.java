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
import com.jhw.adm.client.util.FileImportAndExport;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.WarningRecord;

@Component(QueryWarningRecordView.ID)
@Scope(Scopes.DESKTOP)
public class QueryWarningRecordView extends ViewPart {
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "queryWarningRecordView";
		
	//time
	private JXDatePicker fromPicker;
	private JXDatePicker toPicker;

	private JTextField accepterFld;
//	private IpAddressField systemIPFld;
	private JTextField systemIPFld;
	
	private ButtonFactory buttonFactory;
	private JButton queryBtn;
	private JButton clearBtn;//��գ�ɾ�����ݿ��е�ȫ������
	private JButton deleteBtn;//ɾ����ɾ��ѡ�еļ�¼
	private JButton exportBtn;//������ǰ��ѯ���������е�����.
	
	private JLabel statusLabel;
	private static final String STATUS_PATTERN = "��%sҳ��ÿҳ�����ʾ16���澯���Ѽ�¼";
	
	private JTextArea contentArea;
	private JXTable table;
	private WarningRecordTableModel tableModel;
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
		setTitle("�澯���Ѽ�¼����");
		setLayout(new BorderLayout());
		createContents(this);
	}
	
	private void createContents(JPanel parent) {
		
		buttonFactory = actionManager.getButtonFactory(this);
		
		JPanel toolPanel = new JPanel(new GridLayout(2, 1));
		toolPanel.setBorder(BorderFactory.createTitledBorder("��ѯ����"));
		
		JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		queryPanel.add(new JLabel("��ʼʱ��"));
		fromPicker = new JXDatePicker();
		fromPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(fromPicker);
		
		queryPanel.add(new JLabel("��"));
		toPicker = new JXDatePicker();
		toPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(toPicker);
		
		queryPanel.add(new JLabel("������"));
		accepterFld = new JTextField("",20);
		queryPanel.add(accepterFld);
		
		queryPanel.add(new JLabel("�豸IP"));
		systemIPFld = new JTextField(20);
		queryPanel.add(systemIPFld);
		
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
		table.setColumnControlVisible(true);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectOneRow();
			}
		});
		
		tableModel = new WarningRecordTableModel();
		table.setModel(tableModel);
		table.setColumnModel(tableModel.getColumnMode());
		table.getTableHeader().setReorderingAllowed(false);
		setTableWidth();
		
		JPanel tablePanel = new JPanel(new BorderLayout(1, 2));
		tablePanel.add(table.getTableHeader(),BorderLayout.PAGE_START);
		tablePanel.add(table,BorderLayout.CENTER);
		
		JPanel jPanel = new JPanel(new BorderLayout(1,2));
		jPanel.add(tablePanel,BorderLayout.CENTER);
		
		JPanel resultPanel = new JPanel(new BorderLayout());
		
		JScrollPane scroollTablePane = new JScrollPane();
		scroollTablePane.setBorder(BorderFactory.createTitledBorder("��ѯ���"));
		scroollTablePane.getViewport().add(table, null);
		scroollTablePane.getHorizontalScrollBar().setFocusable(false);
		scroollTablePane.getVerticalScrollBar().setFocusable(false);
		scroollTablePane.getHorizontalScrollBar().setUnitIncrement(30);
		scroollTablePane.getVerticalScrollBar().setUnitIncrement(30);
		
		JPanel detailPanel = new JPanel();
		createMessageDetail(detailPanel);
		detailPanel.setBorder(BorderFactory.createTitledBorder("��ϸ����"));
		
		resultPanel.add(scroollTablePane,BorderLayout.CENTER);
		resultPanel.add(detailPanel,BorderLayout.PAGE_END);
		
		JPanel paginationPanel = new JPanel(new BorderLayout());
		JPanel pageButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
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
						warningRecordAction();
					}
				});
			}
		});
		
		parent.add(toolPanel,BorderLayout.NORTH);
		parent.add(resultPanel,BorderLayout.CENTER);
		parent.add(paginationPanel,BorderLayout.SOUTH);
	}
	
	private void setTableWidth(){
		table.getColumn(0).setPreferredWidth(60);
		table.getColumn(1).setPreferredWidth(340);
		table.getColumn(2).setPreferredWidth(90);
		table.getColumn(3).setPreferredWidth(20);
		table.getColumn(4).setPreferredWidth(10);
		table.getColumn(5).setPreferredWidth(10);
	}
	
	private void selectOneRow() {
		if (table.getSelectedRow() > - 1) {
			WarningRecord warningRecord = tableModel.getValue(table.getSelectedRow());
			contentArea.setText(warningRecord.getMessage());
		}
	}
	
	@ViewAction(name=QUERY,icon=ButtonConstants.QUERY,desc="��ѯ�澯���Ѽ�¼��Ϣ",role=Constants.MANAGERCODE)
	public void query()
	{
//		//��ѯ֮ǰ�����table�е�����
//		setTableValue(null);
		
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		String accepter = "";
		if(null != accepterFld.getText()){
			accepter = accepterFld.getText().trim();
		}
		String ipAddress = systemIPFld.getText().trim();
		
		long count = remoteServer.getNmsService().queryAllWarningCount(startDate, endDate, accepter, ipAddress);
//		if(count > 0){
			setPageBoxItem(count);
//		}
	}
	
	@ViewAction(name=CLEAN,icon=ButtonConstants.CLEAN,desc="��ո澯���Ѽ�¼��Ϣ",role=Constants.MANAGERCODE)
	public void clean()
	{
		int isClean = JOptionPane.showConfirmDialog(this, "�Ƿ�Ҫ������ݿ������еĸ澯���Ѽ�¼", "��ʾ", JOptionPane.OK_CANCEL_OPTION);
		if(isClean == 0)
		{
			remoteServer.getNmsService().clearWarningRecode();
			query();
			tableModel.fireTableDataChanged();
		}
	}
	@ViewAction(name=DELETE,icon=ButtonConstants.DELETE,desc="ɾ���澯���Ѽ�¼��Ϣ",role=Constants.MANAGERCODE)
	public void delete()
	{
		int[] selectedRows = table.getSelectedRows();
		if(selectedRows.length == 0)
		{
			JOptionPane.showMessageDialog(this, "��ѡ����Ҫɾ���ļ�¼", "��ʾ", JOptionPane.NO_OPTION);
			return;
		}
		int isDelete = JOptionPane.showConfirmDialog(this, "�Ƿ�ɾ��ѡ�еĸ澯���Ѽ�¼", "��ʾ", JOptionPane.OK_CANCEL_OPTION);
		if(isDelete == 0)
		{
			remoteServer.getService().deleteEntities(tableModel.getSelectedRows(selectedRows));
			query();
			tableModel.fireTableDataChanged();
		}
	}
	@SuppressWarnings("unchecked")
	@ViewAction(name=EXPORT,icon=ButtonConstants.EXPORT,desc="�����澯���Ѽ�¼��Ϣ",role=Constants.MANAGERCODE)
	public void export(){
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		String accepter = "";
		if(null != accepterFld.getText())
		{
			accepter = accepterFld.getText().trim();
		}
		String ipAddress = systemIPFld.getText().trim();
		
		List<WarningRecord> warningRecordList = remoteServer.getNmsService().queryWarningRecord(startDate, endDate, accepter, ipAddress, 0, 0);

		if(warningRecordList == null)return;
		
		String[] propertiesName = new String[5];
		propertiesName[0] = "�豸IP";
		propertiesName[1] = "��Ϣ����";
		propertiesName[2] = "����ʱ��";
		propertiesName[3] = "������";
		propertiesName[4] = "���ͷ�ʽ";
		List<HashMap> list = new ArrayList<HashMap>();
		for(int i = 0;i < warningRecordList.size();i++){
			HashMap hashMap = new HashMap();
			hashMap.put(propertiesName[0], warningRecordList.get(i).getIpValue());
			hashMap.put(propertiesName[1], warningRecordList.get(i).getMessage());
			hashMap.put(propertiesName[2], dateFormatter.format(
					warningRecordList.get(i).getTime()));
			if(warningRecordList.get(i).getPersonInfo() != null){
				hashMap.put(propertiesName[3], warningRecordList.get(i).getPersonInfo().getName());
			}else{
				hashMap.put(propertiesName[3], "");
			}

			String warningType = "";
			List<UserEntity> userEntityList = (List<UserEntity>) remoteServer
					.getService().findAll(UserEntity.class);
			if(userEntityList.size() > 0){
				for(UserEntity userEntity : userEntityList){
					if(userEntity.getPersonInfo() != null){
						if (userEntity.getPersonInfo().getId().equals(warningRecordList.get(i)
								.getPersonInfo().getId())) {
							if(userEntity.getWarningStyle() != null){
								if(userEntity.getWarningStyle().toUpperCase().equals("S")){
									warningType = "����";
								}else if(userEntity.getWarningStyle().toUpperCase().equals("S,E")){
									warningType = "���ţ������ʼ�";
								}else if(userEntity.getWarningStyle().toUpperCase().equals("E")){
									warningType = "�����ʼ�";
								}
							}else{
								warningType = "";
							}
							break;
						}
					}
				}
			}else{
				warningType = "";
			}
			hashMap.put(propertiesName[4], warningType);
			list.add(hashMap);
		}
		int result = FileImportAndExport.export(this, list, propertiesName);
		if(FileImportAndExport.SUCCESS_RESULT == result){
			JOptionPane.showMessageDialog(this, "��־�����ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		}else if(FileImportAndExport.CANCLE_RESULT == result){
			//if cancle, no message dialog
		}else if(FileImportAndExport.FAILURE_RESULT == result){
			JOptionPane.showMessageDialog(this, "��־����ʧ��", "��ʾ", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void setTableValue(List<WarningRecord> list){
		tableModel.setData(list);
		tableModel.fireTableDataChanged();
	}
	
	@ViewAction(name=FIRST, icon=ButtonConstants.FIRST,desc="��ʾ�澯���Ѽ�¼��һҳ",role=Constants.MANAGERCODE)
	public void first(){
		pageCountBox.setSelectedIndex(0);
	}
	
	@ViewAction(name=PREVIOUS, icon=ButtonConstants.PREVIOUS,desc="��ʾ�澯���Ѽ�¼��һҳ",role=Constants.MANAGERCODE)
	public void previous(){
		if (pageCountBox.getSelectedIndex() <=0){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() -1);
	}
	
	@ViewAction(name=NEXT, icon=ButtonConstants.NEXT,desc="��ʾ�澯���Ѽ�¼��һҳ",role=Constants.MANAGERCODE)
	public void next(){
		if (pageCountBox.getSelectedIndex() >= pageCountBox.getItemCount()-1){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() +1);
	}
	
	@ViewAction(name=LAST, icon=ButtonConstants.LAST,desc="��ʾ�澯���Ѽ�¼���ҳ",role=Constants.MANAGERCODE)
	public void last(){
		pageCountBox.setSelectedIndex(pageCountBox.getItemCount()-1);
	}
	
	private void warningRecordAction()
	{
		int page = pageCountBox.getSelectedIndex() + 1;
		
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		String accepter = "";
		if(null != accepterFld.getText())
		{
			accepter = accepterFld.getText().trim();
		}
		String ipAddress = systemIPFld.getText().trim();
		
		List<WarningRecord> warningRecordList = remoteServer.getNmsService().queryWarningRecord(startDate, endDate, accepter, ipAddress, PAGE_SIZE, page);
		if(null != warningRecordList){
			setTableValue(warningRecordList);
		}else{
			setTableValue(new ArrayList<WarningRecord>());
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
		
		pageCountBox.removeAllItems();
		for(int i = 1 ; i <= page; i++){
			pageCountBox.addItem(i);
		}
		statusLabel.setText(String.format(STATUS_PATTERN, page));
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

	public class WarningRecordTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = -3065768803494840568L;
		private List<WarningRecord> warningRecordEntityList;
		private final TableColumnModelExt columnModel;
		
		public WarningRecordTableModel()
		{
			warningRecordEntityList = Collections.emptyList();
			
			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			
			TableColumnExt systemIPColumn = new TableColumnExt(modelIndex++,100);
			systemIPColumn.setIdentifier("Warning_record_system_ip");
			systemIPColumn.setTitle(localizationManager.getString("Warning_record_system_ip"));
			columnModel.addColumn(systemIPColumn);
			
			TableColumnExt contentsColumn = new TableColumnExt(modelIndex++,300);
			contentsColumn.setIdentifier("Warning_record_message");
			contentsColumn.setTitle(localizationManager.getString("Warning_record_message"));
			columnModel.addColumn(contentsColumn);
			
			TableColumnExt doTimeColumn = new TableColumnExt(modelIndex++,160);
			doTimeColumn.setIdentifier("Warning_record_do_time");
			doTimeColumn.setTitle(localizationManager.getString("Warning_record_do_time"));
			columnModel.addColumn(doTimeColumn);
			
			TableColumnExt accepterColumn = new TableColumnExt(modelIndex++, 100);
			accepterColumn.setIdentifier("Warning_record_accepter");
			accepterColumn.setTitle(localizationManager.getString("Warning_record_accepter"));
			columnModel.addColumn(accepterColumn);
			
			TableColumnExt warningTypeColumn = new TableColumnExt(modelIndex++,100);
			warningTypeColumn.setIdentifier("Warning_record_warningStyle");
			warningTypeColumn.setTitle(localizationManager.getString("Warning_record_warningStyle"));
			columnModel.addColumn(warningTypeColumn);
			
			TableColumnExt statusColumn = new TableColumnExt(modelIndex++,100);
			statusColumn.setIdentifier("Warning_record_warningStyle");
			statusColumn.setTitle("״̬");
			columnModel.addColumn(statusColumn);
		}
		
		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return warningRecordEntityList.size();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			
			if(row < warningRecordEntityList.size())
			{
				WarningRecord warningRecordEntity = warningRecordEntityList.get(row);
				switch(col)
				{
				case 0:
					value = warningRecordEntity.getIpValue();
					break;
				case 1:
					value = warningRecordEntity.getMessage();
					break;
				case 2:
					value = dateFormatter.format(warningRecordEntity.getTime());
					break;
				case 3:
					if(warningRecordEntity.getPersonInfo() != null){
						value = warningRecordEntity.getPersonInfo().getName();						
					}else{
						value = "";
					}
					break;
				case 4:
					value = warningRecordEntity.getSendType() == Constants.SENDNOTE ? "����" : "�ʼ�";
					break;
				case 5:
					value = warningRecordEntity.isSendTag() ? "�ɹ�" : "ʧ��";
					break;
				default:
					break;
				}
			}
			return value;
		}
		
		@Override
		public String getColumnName(int col)
		{
			return columnModel.getColumnExt(col).getTitle();
		}
		
		@Override
		public boolean isCellEditable(int row,int col)
		{
			return false;
		}
		
		public TableColumnModel getColumnMode()
		{
			return columnModel;
		}
		
		public WarningRecord getValue(int row)
		{
			WarningRecord value = null;
			if(row < warningRecordEntityList.size())
			{
				value = warningRecordEntityList.get(row);
			}
			return value;
		}
		
		public void setData(List<WarningRecord> data)
		{
			if(null == data)return;
			this.warningRecordEntityList = data;
		}
		
		public List<WarningRecord> getSelectedRows(int[] selectedRows)
		{
			List<WarningRecord> warnigRecordLists = new ArrayList<WarningRecord>();
			for(int i = 0;i < selectedRows.length;i++)
			{
				warnigRecordLists.add(this.warningRecordEntityList.get(selectedRows[i]));
			}
			return warnigRecordLists;
		}
	}
}
