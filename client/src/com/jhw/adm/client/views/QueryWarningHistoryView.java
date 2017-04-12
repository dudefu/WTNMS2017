package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.ADVANCEDQUERY;
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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.jhw.adm.client.model.AlarmEventCategory;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.swing.HyalineDialog;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.ui.ClientUI;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.FileImportAndExport;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.WarningHistoryBean;
import com.jhw.adm.server.entity.warning.WarningHistoryEntity;

@Component(QueryWarningHistoryView.ID)
@Scope(Scopes.DESKTOP)
public class QueryWarningHistoryView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "queryWarningHistoryView";
	private static final Logger LOG = LoggerFactory.getLogger(QueryWarningHistoryView.class);
	
	private final JPanel filterPanel = new JPanel();
	private final JComboBox confirmComboBox = new JComboBox();
	
	private final JPanel resultPanel = new JPanel(); 
	private JXTable table = null;
	private WarningHistoryTableModel model = null;

	private final JPanel buttonPanel = new JPanel();
	private ButtonFactory buttonFactory;
	private JLabel statusLabel = null;
	private static final int PAGE_SIZE = 50;
	private static final String STATUS_PATTERN = "��%sҳ��ÿҳ�����ʾ50���澯��Ϣ";
	private final JComboBox pageCountBox = new JComboBox();
	
	private HyalineDialog hyalineDialog;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DateFormatter.ID)
	private DateFormatter dateFormatter;
	
	@Resource(name=AlarmEventCategory.ID)
	private AlarmEventCategory alarmEventCategory;
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this);
		
		createFilterPanel(filterPanel);
		createQueryAndShowPanel(resultPanel);
		createButtonPanel(buttonPanel);
		
		this.setLayout(new BorderLayout());
		this.add(filterPanel, BorderLayout.PAGE_START);
		this.add(resultPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.PAGE_END);
		this.setTitle("�澯��ʷ����");
		hyalineDialog = new HyalineDialog(this);
	}
	
	private JButton queryButton = null;
	private JButton advancedQueryButton = null;
	private JButton exportButton = null;
	private void createFilterPanel(JPanel parent) {
		parent.setLayout(new GridLayout(1,2));
		parent.setBorder(BorderFactory.createTitledBorder("��ѯ����"));
		
		JPanel confirmedPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		confirmedPanel.add(new JLabel("���ȷ��ʱ��"));
		confirmedPanel.add(confirmComboBox);
		initializeComboBox();
		
		JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		queryButton = buttonFactory.createButton(QUERY);
		advancedQueryButton = buttonFactory.createButton(ADVANCEDQUERY);
		exportButton = buttonFactory.createButton(EXPORT);
		filterButtonPanel.add(queryButton);
		filterButtonPanel.add(advancedQueryButton);
		filterButtonPanel.add(exportButton);
		
		parent.add(confirmedPanel);
		parent.add(filterButtonPanel);
	}

	private static final String[] FILTERS = {"����", "����", "������", "����"}; 
	private void initializeComboBox() {
		for(String filter : FILTERS){
			confirmComboBox.addItem(filter);
		}
		confirmComboBox.setSelectedIndex(0);
		confirmComboBox.setEditable(false);
	}

	private void createQueryAndShowPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		table = new JXTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(true);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				if(row >= 0){
					if(e.getClickCount() >= 2){
						alarmModel.setDetailHistoryAlarm(model.getValue(row));
						String viewID = WarningHistoryDetailView.ID;
						String groupID = ConfigureGroupView.ID;
						LOG.info(String.format("Enter view (%s)", viewID));
						ClientUI.getDesktopWindow().showView(viewID, groupID);
					}
				}
			}
		});

		model = new WarningHistoryTableModel();
		table.setModel(model);
		table.setColumnModel(model.getColumnModel());
		table.getTableHeader().setReorderingAllowed(false);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createTitledBorder("��ѯ���"));
		scrollPane.getViewport().add(table, null);
		scrollPane.getHorizontalScrollBar().setFocusable(false);
		scrollPane.getVerticalScrollBar().setFocusable(false);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		
		parent.add(scrollPane, BorderLayout.CENTER);
	}

	private void createButtonPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		JPanel pageButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

		JButton firstBtn = buttonFactory.createButton(FIRST);
		JButton previousBtn = buttonFactory.createButton(PREVIOUS);
		JButton nextBtn = buttonFactory.createButton(NEXT);
		JButton lastBtn = buttonFactory.createButton(LAST); 

		pageButtonPanel.add(firstBtn);
		pageButtonPanel.add(previousBtn);
		pageButtonPanel.add(nextBtn);
		pageButtonPanel.add(lastBtn);
		pageButtonPanel.add(pageCountBox);
		pageCountBox.addItemListener(pageCountBoxListener);
		
		statusLabel = new JLabel();
		statusLabel.setText(String.format(STATUS_PATTERN, 0));

		parent.add(statusLabel, BorderLayout.CENTER);
		parent.add(pageButtonPanel, BorderLayout.LINE_END);
	}
	
	private final ItemListener pageCountBoxListener = new ItemListener() {
		@Override
		public void itemStateChanged(final ItemEvent e) {
//			if(ItemEvent.SELECTED == e.getStateChange()){
				warningHistoryAction();
//			}
		}
	};
	
	@ViewAction(name=FIRST, icon=ButtonConstants.FIRST,desc="��ʾ�澯��Ϣ��һҳ",role=Constants.MANAGERCODE)
	public void first(){
		pageCountBox.setSelectedIndex(0);
	}
	
	@ViewAction(name=PREVIOUS, icon=ButtonConstants.PREVIOUS,desc="��ʾ�澯��Ϣ��һҳ",role=Constants.MANAGERCODE)
	public void previous(){
		if (pageCountBox.getSelectedIndex() <=0){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() -1);
	}
	
	@ViewAction(name=NEXT, icon=ButtonConstants.NEXT,desc="��ʾ�澯��Ϣ��һҳ",role=Constants.MANAGERCODE)
	public void next(){
		if (pageCountBox.getSelectedIndex() >= pageCountBox.getItemCount()-1){
			return;
		}
		pageCountBox.setSelectedIndex(pageCountBox.getSelectedIndex() +1);
	}
	
	@ViewAction(name=LAST, icon=ButtonConstants.LAST,desc="��ʾ�澯��Ϣ���ҳ",role=Constants.MANAGERCODE)
	public void last(){
		pageCountBox.setSelectedIndex(pageCountBox.getItemCount()-1);
	}
	
	private void fireTableData(final List<WarningHistoryEntity> historyEntityList){
		if(SwingUtilities.isEventDispatchThread()){
			model.setAlarms(historyEntityList);
			model.fireTableDataChanged();
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireTableData(historyEntityList);
				}
			});
		}
	}
	
	private WarningHistoryBean bean;
	public WarningHistoryBean getBean() {
		return bean;
	}
	public void setBean(WarningHistoryBean bean) {
		this.bean = bean;
	}

	@ViewAction(name=QUERY, icon=ButtonConstants.QUERY, desc="��ѯ�澯��ʷ��Ϣ",role=Constants.MANAGERCODE)
	public void query() {
		String filter = getSimpleFilter();
		WarningHistoryBean warningHistoryBean = new WarningHistoryBean();
		warningHistoryBean.setSimpleConfirmTimeStr(filter);
		this.setBean(warningHistoryBean);
		
		long count = remoteServer.getNmsService().getAllWarningHistoryCount(warningHistoryBean);
		setPageBoxItem(count);
	}
	
	private String getSimpleFilter(){
		String selected = (String) confirmComboBox.getSelectedItem();
		String filter = "";
		if(selected.equals(FILTERS[0])){
			filter = Constants.WEEKLY;
		}else if(selected.equals(FILTERS[1])){
			filter = Constants.MONTHLY;
		}else if(selected.equals(FILTERS[2])){
			filter = Constants.QUARTERLY;
		}else if(selected.equals(FILTERS[3])){
			filter = Constants.YEARLY;
		}
		return filter;
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
	
	@ViewAction(name=ADVANCEDQUERY, icon=ButtonConstants.QUERY, desc="�߼���ѯ�澯��ʷ��Ϣ",role=Constants.MANAGERCODE)
	public void advancedQuery() {
		AdvancedDialog dialog = new AdvancedDialog(this);
		dialog.openDialog();
	}
	
	@SuppressWarnings("unchecked")
	@ViewAction(name=EXPORT, icon=ButtonConstants.EXPORT, desc="�����澯��ʷ��Ϣ",role=Constants.MANAGERCODE)
	public void export(){
		WarningHistoryBean warningHistoryBean = this.getBean();
		warningHistoryBean.setStartPage(0);
		warningHistoryBean.setMaxPageSize(0);

		List<WarningHistoryEntity> historyList = remoteServer.getNmsService().queryWarningHistory(warningHistoryBean);
	
		String[] propertiesName = new String[6];
		propertiesName[0] = "��Դ";
		propertiesName[1] = "�¼�";
		propertiesName[2] = "Ƶ��";
		propertiesName[3] = "�״η���ʱ��";
		propertiesName[4] = "�������ʱ��";
		propertiesName[5] = "���ȷ��ʱ��";
		List<HashMap> list = new ArrayList<HashMap>();
		for(int i = 0;i < historyList.size();i++){
			HashMap hashMap = new HashMap();
			hashMap.put(propertiesName[0], historyList.get(i).getNodeName());
			hashMap.put(propertiesName[1], alarmEventCategory.get(historyList.get(i).getWarningEvent()).getKey());
			hashMap.put(propertiesName[2], historyList.get(i).getWarningCount() + "");
			hashMap.put(propertiesName[3], dateFormatter.format(historyList.get(i).getFirstTime()));
			hashMap.put(propertiesName[4], dateFormatter.format(historyList.get(i).getLastTime()));
			hashMap.put(propertiesName[5], dateFormatter.format(historyList.get(i).getLastConfirmTime()));
			list.add(hashMap);
		}
		int result = FileImportAndExport.export(this, list, propertiesName);
		if(FileImportAndExport.SUCCESS_RESULT == result){
			JOptionPane.showMessageDialog(this, "�澯��ʷ��Ϣ�����ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		}else if(FileImportAndExport.CANCLE_RESULT == result){
			//if cancle, no message dialog
		}else if(FileImportAndExport.FAILURE_RESULT == result){
			JOptionPane.showMessageDialog(this, "�澯��ʷ��Ϣ����ʧ��", "��ʾ", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void warningHistoryAction() {		

		final Runnable queryAndSetTableDataThread = new Runnable() {
			@Override
			public void run() {
				queryAndSetTableData();
			}
		};
		hyalineDialog.run(queryAndSetTableDataThread);
	}
	
	protected void queryAndSetTableData() {
		int page = pageCountBox.getSelectedIndex() + 1;
		
		WarningHistoryBean warningHistoryBean = this.getBean();
		warningHistoryBean.setStartPage(page);
		warningHistoryBean.setMaxPageSize(PAGE_SIZE);
		
		List<WarningHistoryEntity> historyList = remoteServer.getNmsService().queryWarningHistory(warningHistoryBean);
		if(null == historyList){
			historyList = new ArrayList<WarningHistoryEntity>();
		}
		fireTableData(historyList);
	}

	@Override
	public void dispose() {
		super.dispose();
		hyalineDialog.dispose();
	}
	
	private class WarningHistoryTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private final TableColumnModelExt columnModel;
		private List<WarningHistoryEntity> warningHistoryList;
		
		public WarningHistoryTableModel(){
			warningHistoryList = new ArrayList<WarningHistoryEntity>();
			
			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			
			TableColumnExt originColumn = new TableColumnExt(modelIndex++, 160);
			originColumn.setIdentifier("��Դ");
			originColumn.setTitle("��Դ");
			columnModel.addColumn(originColumn);
			
			TableColumnExt eventColumn = new TableColumnExt(modelIndex++, 160);
			eventColumn.setIdentifier("�¼�");
			eventColumn.setTitle("�¼�");
			columnModel.addColumn(eventColumn);

			TableColumnExt frequencyColumn = new TableColumnExt(modelIndex++, 160);
			frequencyColumn.setIdentifier("Ƶ��");
			frequencyColumn.setTitle("Ƶ��");
			columnModel.addColumn(frequencyColumn);

			TableColumnExt createTimeColumn = new TableColumnExt(modelIndex++, 160);
			createTimeColumn.setIdentifier("�״η���ʱ��");
			createTimeColumn.setTitle("�״η���ʱ��");
			columnModel.addColumn(createTimeColumn);

			TableColumnExt lastTimeColumn = new TableColumnExt(modelIndex++, 160);
			lastTimeColumn.setIdentifier("�������ʱ��");
			lastTimeColumn.setTitle("�������ʱ��");
			columnModel.addColumn(lastTimeColumn);

			TableColumnExt lastConfirmTimeColumn = new TableColumnExt(modelIndex++, 160);
			lastConfirmTimeColumn.setIdentifier("���ȷ��ʱ��");
			lastConfirmTimeColumn.setTitle("���ȷ��ʱ��");
			columnModel.addColumn(lastConfirmTimeColumn);

		}
		
		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return warningHistoryList.size();
		}
		
		@Override
		public String getColumnName(int col) {
			return columnModel.getColumnExt(col).getTitle();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if(warningHistoryList != null){
				if (row < warningHistoryList.size()) {
					WarningHistoryEntity history = warningHistoryList.get(row);
					switch (col) {
					case 0:
						value = history.getNodeName();
						break;
					case 1:
						value = alarmEventCategory.get(history.getWarningEvent()).getKey();
						break;
					case 2:
						value = history.getWarningCount();
						break;
					case 3:
						value = dateFormatter.format(history.getFirstTime());
						break;
					case 4:
						value = dateFormatter.format(history.getLastTime());
						break;
					case 5:
						value = dateFormatter.format(history.getLastConfirmTime());
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

		public WarningHistoryEntity getValue(int row) {
			WarningHistoryEntity value = null;
			if (row < warningHistoryList.size()) {
				value = warningHistoryList.get(row);
			}

			return value;
		}

		public void setAlarms(List<WarningHistoryEntity> data) {
			if (data == null) {
				return;
			}
			this.warningHistoryList = data;
		}
	}
	
	private class AdvancedDialog extends JDialog{
		
		private static final long serialVersionUID = 1L;
		private final JPanel advancedFilterPanel = new JPanel();
		private final JPanel advancedButtonPanel = new JPanel();
		
		private final JTextField originField = new JTextField();
		private final JComboBox eventComboBox = new JComboBox();
		private final NumberField frequencyField = new NumberField();
		private final JXDatePicker createFromPicker = new JXDatePicker();
		private final JXDatePicker createToPicker = new JXDatePicker();
		private final JXDatePicker lastFromPicker = new JXDatePicker();
		private final JXDatePicker lastToPicker = new JXDatePicker();
		private final JXDatePicker lastConfirmFromPicker = new JXDatePicker();
		private final JXDatePicker lastConfirmToPicker = new JXDatePicker();

		public AdvancedDialog(ViewPart viewPart){
			super(ClientUtils.getRootFrame());
			this.setModal(true);
			this.setResizable(false);
			this.setIconImage(ClientUtils.getRootFrame().getIconImage());
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			this.setTitle("�߼���ѯ");
			this.setSize(500, 300);
			this.createCenterPanel(this);
			this.setLocationRelativeTo(viewPart);
		}
		
		private void createCenterPanel(JDialog parent) {
			parent.setLayout(new BorderLayout());
			
			createAdvancedFilterPanel(advancedFilterPanel);
			createAdvancedButtonPanel(advancedButtonPanel);
			
			parent.add(advancedFilterPanel, BorderLayout.CENTER);
			parent.add(advancedButtonPanel, BorderLayout.PAGE_END);
		}
		
		private void createAdvancedFilterPanel(JPanel parent) {
			parent.setLayout(new FlowLayout(FlowLayout.LEADING));
			
			originField.setColumns(20);
			createFromPicker.setFormats("yyyy-MM-dd");
			createToPicker.setFormats("yyyy-MM-dd");
			lastFromPicker.setFormats("yyyy-MM-dd");
			lastToPicker.setFormats("yyyy-MM-dd");
			lastConfirmFromPicker.setFormats("yyyy-MM-dd");
			lastConfirmToPicker.setFormats("yyyy-MM-dd");
			Dimension originDimension = originField.getPreferredSize();
			createFromPicker.setPreferredSize(originDimension);
			createToPicker.setPreferredSize(originDimension);
			lastFromPicker.setPreferredSize(originDimension);
			lastToPicker.setPreferredSize(originDimension);
			lastConfirmFromPicker.setPreferredSize(originDimension);
			lastConfirmToPicker.setPreferredSize(originDimension);
			
			JPanel panel = new JPanel(new GridBagLayout());
			panel.add(new JLabel("��Դ"),new GridBagConstraints(0,0,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
			panel.add(originField,new GridBagConstraints(1,0,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

			panel.add(new JLabel("�¼�"),new GridBagConstraints(0,1,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
			panel.add(eventComboBox,new GridBagConstraints(1,1,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

			panel.add(new JLabel("Ƶ��"),new GridBagConstraints(0,2,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
			panel.add(frequencyField,new GridBagConstraints(1,2,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

			panel.add(new JLabel("�״η���ʱ���"),new GridBagConstraints(0,3,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
			panel.add(createFromPicker,new GridBagConstraints(1,3,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
			panel.add(new JLabel("��"),new GridBagConstraints(2,3,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,15,10,10),0,0));
			panel.add(createToPicker,new GridBagConstraints(3,3,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

			panel.add(new JLabel("�������ʱ���"),new GridBagConstraints(0,4,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
			panel.add(lastFromPicker,new GridBagConstraints(1,4,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
			panel.add(new JLabel("��"),new GridBagConstraints(2,4,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,15,10,10),0,0));
			panel.add(lastToPicker,new GridBagConstraints(3,4,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
			
			panel.add(new JLabel("���ȷ��ʱ���"),new GridBagConstraints(0,5,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
			panel.add(lastConfirmFromPicker,new GridBagConstraints(1,5,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
			panel.add(new JLabel("��"),new GridBagConstraints(2,5,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,15,10,10),0,0));
			panel.add(lastConfirmToPicker,new GridBagConstraints(3,5,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));

			ListComboBoxModel linkCategoryBoxModel = new ListComboBoxModel(
					Collections.unmodifiableList(alarmEventCategory.toList()));
			eventComboBox.setModel(linkCategoryBoxModel);
			
			parent.add(panel);
		}

		private void createAdvancedButtonPanel(JPanel parent) {
			parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
			JButton okButton = new JButton(new QueryAction("��ѯ"));
			okButton.setIcon(imageRegistry.getImageIcon(ButtonConstants.QUERY));
			JButton closeButton = new JButton(new CloseAction("�ر�"));
			closeButton.setIcon(imageRegistry.getImageIcon(ButtonConstants.CLOSE));
			
			parent.add(okButton);
			parent.add(closeButton);
		}
		
		public void openDialog(){
			this.setVisible(true);
		}
		
		public void closeDialog(){
			super.dispose();
			this.setVisible(false);
		}
		
		private class QueryAction extends AbstractAction{

			private static final long serialVersionUID = 1L;
			public QueryAction(String name){
				super(name);
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String origin = "";
				if(!StringUtils.isBlank(originField.getText())){
					origin = originField.getText().trim();
				}
				int event = -1;
				if(eventComboBox.getSelectedItem() != null){
					event = ((StringInteger)eventComboBox.getSelectedItem()).getValue();
				}
				int frequency = -1;
				if(!StringUtils.isBlank(frequencyField.getText())){
					frequency = NumberUtils.toInt(frequencyField.getText().trim());
				}
				Date createFrom = createFromPicker.getDate();
				Date createTo = createToPicker.getDate();
				
				Date lastFrom = lastFromPicker.getDate();
				Date lastTo = lastToPicker.getDate();

				Date lastConfirmFrom = lastConfirmFromPicker.getDate();
				Date lastConfirmTo = lastConfirmToPicker.getDate();
				
				WarningHistoryBean bean = new WarningHistoryBean();
				bean.setWarningSouce(origin);
				bean.setWarningEvent(event);
				bean.setWarningCount(frequency);
				
				bean.setStartFirstTime(createFrom);
				bean.setEndFirstTime(createTo);
				
				bean.setStartLastTime(lastFrom);
				bean.setEndLastTime(lastTo);
				
				bean.setStartLastConfirmTime(lastConfirmFrom);
				bean.setEndLastConfirmTime(lastConfirmTo);
				setBean(bean);
				
				long count = remoteServer.getNmsService().getAllWarningHistoryCount(bean);
				setPageBoxItem(count);
				closeDialog();
			}
		}
		
		private class CloseAction extends AbstractAction{

			private static final long serialVersionUID = 1L;
			public CloseAction(String name){
				super(name);
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						advancedQueryButton.setEnabled(true);
						queryButton.setEnabled(true);
						closeDialog();
					}
				});
			}
			
		}
	}
}
