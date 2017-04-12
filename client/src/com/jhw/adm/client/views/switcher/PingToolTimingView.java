package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.PingResult;

@Component(PingToolTimingView.ID)
@Scope(Scopes.DESKTOP)
public class PingToolTimingView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "pingToolTimingView";
	
	private JPanel centerPnl = new JPanel();
	
	private JPanel northPnl = new JPanel();
	private JScrollPane leftNorthPnl = new JScrollPane();
	private JTable leftNorthTable = new JTable();
	private SwitchTableModel leftNorthModel;
	
	private JPanel centerNorthPnl = new JPanel();
	private JButton addBtn = new JButton(">>"); 
	private JButton delBtn = new JButton("<<");
	
	private JScrollPane rightNorthPnl = new JScrollPane();
	private JTable rightNorthTable = new JTable();
	private IpTableModel rightNorthModel;
	
	private JPanel southPnl = new JPanel();
	private JLabel intervalLbl = new JLabel();
	private NumberField intervalFld = new NumberField();
	
	private JLabel timesLbl = new JLabel();
	private NumberField timesFld = new NumberField();
	
	private JPanel bottomPnl = new JPanel();
	private JButton saveBtn ;
	private JButton closeBtn;
	
	private ButtonFactory buttonFactory;
	
	private static final String[] COLUMN_NAME = { "交换机IP" };
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initCenterPnl(){
		initNorthPnl();
		initSouthPnl();
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(northPnl,BorderLayout.NORTH);
		centerPnl.add(southPnl,BorderLayout.CENTER);
	}
	
	private void initNorthPnl(){
		northPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(leftNorthPnl,new GridBagConstraints(0,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,0,0),0,0));
		panel.add(centerNorthPnl,new GridBagConstraints(1,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,20,0,0),0,0));
		panel.add(rightNorthPnl,new GridBagConstraints(2,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,20,0,0),0,0));
		northPnl.add(panel);
		
		leftNorthModel = new SwitchTableModel();
		rightNorthModel = new IpTableModel();
		leftNorthModel.setColumnName(COLUMN_NAME);
		rightNorthModel.setColumnName(COLUMN_NAME);
		
		
		leftNorthTable.setModel(leftNorthModel);
		rightNorthTable.setModel(rightNorthModel);
		leftNorthTable.getTableHeader().setPreferredSize(new Dimension(leftNorthTable.getTableHeader().getPreferredSize().width,0));
		rightNorthTable.getTableHeader().setPreferredSize(new Dimension(rightNorthTable.getTableHeader().getPreferredSize().width,0));
		
		leftNorthTable.setShowGrid(false);
		rightNorthTable.setShowGrid(false);

		leftNorthPnl.getViewport().setBackground(Color.WHITE);
		rightNorthPnl.getViewport().setBackground(Color.WHITE);
		
		//对scrollPane进行布局
		leftNorthPnl.getViewport().setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel leftPnl = new JPanel(new GridBagLayout());
		leftPnl.setBackground(Color.WHITE);
		leftPnl.add(leftNorthTable,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		leftNorthPnl.getViewport().add(leftPnl);

		rightNorthPnl.getViewport().setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel rightPnl = new JPanel(new GridBagLayout());
		rightPnl.setBackground(Color.WHITE);
		rightPnl.add(rightNorthTable,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		rightNorthPnl.getViewport().add(rightPnl);
		//
		
		centerNorthPnl.setLayout(new GridBagLayout());
		centerNorthPnl.add(addBtn,new GridBagConstraints(0,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		centerNorthPnl.add(delBtn,new GridBagConstraints(0,1,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(40,0,0,0),0,0));
	}
	
	private void initSouthPnl(){
		southPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(intervalLbl,new GridBagConstraints(0,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,7,0,0),0,0));
		panel.add(intervalFld,new GridBagConstraints(1,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,20,0,0),0,0));
		panel.add(timesLbl,new GridBagConstraints(2,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,45,0,0),0,0));
		panel.add(timesFld,new GridBagConstraints(3,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,20,0,0),0,0));
		southPnl.add(panel);
	}
	
	private void initBottomPnl(){
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}
	
	private void setResource(){
		centerPnl.setBorder(BorderFactory.createTitledBorder("Ping设备定时配置"));
		intervalLbl.setText("间隔时间");
		timesLbl.setText("次数");
		
		intervalFld.setColumns(15);
		timesFld.setColumns(15);
		
		leftNorthPnl.setBackground(Color.WHITE);
		rightNorthPnl.setBackground(Color.WHITE);
		
		leftNorthPnl.setPreferredSize(new Dimension(250,280));
		rightNorthPnl.setPreferredSize(new Dimension(250,280));
		
		addBtn.setActionCommand("addBtn");
		delBtn.setActionCommand("delBtn");
		ButtonActionListener actionListener = new ButtonActionListener();
		addBtn.addActionListener(actionListener);
		delBtn.addActionListener(actionListener);
	}
	
	class ButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if (e.getActionCommand().equals("addBtn")){
				add();
			}
			else if (e.getActionCommand().equals("delBtn")){
				del();
			}
		}
	}
	
	private void add(){
		rightNorthModel.addRow(leftNorthTable.getSelectedRows());
		
		//不用的代码
//		if (isNew){
//			pingToolEntity = new PingToolEntitys();
//			List<PingResultStatus> pingResultList = new ArrayList<PingResultStatus>();
//			pingToolEntity.setIpList(pingResultList);
//		}
//		
//		int interval = 0;
//		if (!(intervalFld.getText() == null) && !"".equals(intervalFld.getText().trim())){
//			interval = NumberUtils.toInt(intervalFld.getText().trim());
//		}
//		int times = 0;
//		if (!(timesFld.getText() == null) && !"".equals(timesFld.getText().trim())){
//			times = NumberUtils.toInt(timesFld.getText().trim());
//		}
//
//		int rows[] = leftNorthTable.getSelectedRows();
//		for(int i = 0 ; i < rows.length; i++){
//			boolean isSame = false;
//			String ipValue = (String)leftNorthModel.getValueAt(rows[i], 0);
//			PingResultStatus pingResult = new PingResultStatus();
//			pingResult.setIpValue(ipValue);
//			List rowList = new ArrayList();
//			rowList.add(ipValue);
//			rowList.add(pingResult);
//			for(int k = 0 ;k < rightNorthModel.getDataList().size(); k++){
//				String ipAddr = (String)rightNorthModel.getDataList().get(k).get(0);
//				if (ipAddr.equals(ipValue)){
//					isSame = true;
//					break;
//				}
//			}
//			
//			if (!isSame){
//				rightNorthModel.getDataList().add(rowList);
//			
//				pingToolEntity.getIpList().add(pingResult);
//			}
//		}
//		rightNorthModel.fireTableDataChanged();
	}
	
	private void del(){
		rightNorthModel.removeRow(rightNorthTable.getSelectedRows());
	}
	
	private void queryData(){
		setLeftTable();
		setRightTable();
	}
	
	private void setLeftTable(){
		List<SwitchTopoNodeEntity> list = new ArrayList<SwitchTopoNodeEntity>();
		Iterator iterator = equipmentModel.getDiagram().getNodes().iterator();
		while(iterator.hasNext()){
			Object object = iterator.next();
			if (object instanceof SwitchTopoNodeEntity){
				list.add((SwitchTopoNodeEntity)object);
			}
		}
		leftNorthModel.setDataList(list);
		leftNorthModel.fireTableDataChanged();
	}
	
	private void setRightTable(){
		//为了编译过，暂时注释掉
//		List<PingToolEntitys> list;
//		try{
//			list = (List<PingToolEntitys>)remoteServer.getService().findAll(PingToolEntitys.class);
//		}catch(Exception  e){
//			list = null;
//		}
//		if (null == list || list.size() < 1){
//			isNew = true;
//			return;
//		}
//		isNew = false;
//		
//		pingToolEntity = list.get(0);
		
		
//		String interval = pingToolEntity.getInterval()+"";
//		String times = pingToolEntity.getTimes() + "";
//		
//		intervalFld.setText(interval);
//		timesFld.setText(times);
//		
//		List<PingResultStatus> resultList = pingToolEntity.getIpList();
//		if (null == resultList || resultList.size() < 1){
//			return;
//		}
//		
//		List<List> dataList = new ArrayList<List>();
//		for (int i = 0 ; i < resultList.size(); i++){
//			String ip = resultList.get(i).getIpValue();
//			PingResultStatus result = resultList.get(i);
//			List rowList = new ArrayList();
//			rowList.add(ip);
//			rowList.add(result);
//			
//			dataList.add(rowList);
//		}
//		
//		rightNorthModel.setDataList(dataList);
//		rightNorthModel.fireTableDataChanged();
	}
	
	private void setValue(){
		
	}
	
	/**
	 * 保存操作
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存用户",role=Constants.MANAGERCODE)
	public void save(){
		
	}
	
	public JButton getCloseButton(){
		return null;
	}
	
	/**
	 * 左边表格的tableModel
	 * @author Administrator
	 *
	 */
	class SwitchTableModel extends AbstractTableModel{
		private String[] columnName = null;
		
		private List<SwitchTopoNodeEntity> dataList = null;
		
		public SwitchTableModel(){
			super();
		}
		
		public String[] getColumnName() {
			return columnName;
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public List<SwitchTopoNodeEntity> getDataList() {
			return dataList;
		}

		public void setDataList(List<SwitchTopoNodeEntity> dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<SwitchTopoNodeEntity>();
			}
			else{
				this.dataList = dataList;
			}
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

	    public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
//	    	return null;
	    }

	  
	    public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	
	    public Object getValueAt(int rowIndex, int columnIndex){
	    	if(0 == columnIndex){
	    		return dataList.get(rowIndex).getNodeEntity().getBaseConfig().getIpValue();
	    	}
	    	else{
	    		return dataList.get(rowIndex).getNodeEntity().getType();
	    	}
	    	
	    }
	    
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (aValue instanceof SwitchTopoNodeEntity){
	    		dataList.set(rowIndex, (SwitchTopoNodeEntity)aValue);
	    	}
	    }
	    
	    public SwitchTopoNodeEntity getNodeEntity(int row){   	
	    	return dataList.get(row);
	    }
	}
	
	/**
	 * 右边表格的tableModel
	 * @author Administrator
	 */
	@SuppressWarnings("unchecked")
	class IpTableModel extends AbstractTableModel{
		private List<List> dataList = new ArrayList<List>();
		
		private String[] columnName = null;

		public List<List> getDataList() {
			return dataList;
		}

		public void setDataList(List<List> dataList) {
			if (null == dataList){
				dataList = new ArrayList<List>();
			}
			else{
				this.dataList = dataList;
			}
		}

		public String[] getColumnName() {
			return columnName;
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		@Override
		public int getColumnCount() {
			return columnName.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			// TODO Auto-generated method stub
			return columnName[columnIndex];
		}

		@Override
		public int getRowCount() {
			return dataList.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (dataList.size() <= rowIndex){
				return null;
			}
			
			if (null == dataList.get(rowIndex)){
				return null;
			}
			return dataList.get(rowIndex).get(columnIndex);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}


		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (null == dataList.get(rowIndex)){
				return;
			}
			dataList.get(rowIndex).set(columnIndex, aValue);
		}
		
		public void addRow(int rows[]){
			for(int i = 0 ; i < rows.length; i++){
				boolean isSame = false;
				String ipValue = (String)leftNorthModel.getValueAt(rows[i], 0);
				PingResult pingResult = new PingResult();
				pingResult.setIpValue(ipValue);
				List rowList = new ArrayList();
				rowList.add(ipValue);
				rowList.add(pingResult);
				for(int k = 0 ;k < this.getDataList().size(); k++){
					String ipAddr = (String)this.getDataList().get(k).get(0);
					if (ipAddr.equals(ipValue)){
						isSame = true;
						break;
					}
				}
				
				if (!isSame){
					this.getDataList().add(rowList);
				}
			}
			this.fireTableDataChanged();
		}
		
		public void removeRow(int rows[]){
			int size = rows.length;
			List listStr = new ArrayList();
			for(int j = 0 ; j < size; j++){
				String ipValue = (String)this.getValueAt(rows[j], 0);
				listStr.add(ipValue);
			}
			for(int i = 0 ; i < listStr.size(); i++){
				String ipValue = (String)listStr.get(i);
				for (int k = this.dataList.size()-1 ; k >=0; k--){
					if (this.dataList.get(k).get(0).toString().equals(ipValue)){
						this.dataList.remove(k);
						break;
					}
				}
			}
			this.fireTableDataChanged();
		}
	}
}
