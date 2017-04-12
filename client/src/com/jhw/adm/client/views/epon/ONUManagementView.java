package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.MessagePromptDialog;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(ONUManagementView.ID)
@Scope(Scopes.DESKTOP)
public class ONUManagementView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "ONUManagementView";

	private final JPanel topPnl = new JPanel();
	private final JPanel centerPnl = new JPanel();
	private final JPanel centerTopPnl = new JPanel();
	private final JLabel eponPortLbl = new JLabel();
	private final JCheckBox verifyChkBox = new JCheckBox();
	private final JComboBox eponPortCombox = new JComboBox();
	private final JLabel onuMacLbl = new JLabel();
	private final JTextField onuMacFld = new JTextField();
	
	private JButton addBtn ;
	private JButton delBtn;

	private final JScrollPane scrollPnl = new JScrollPane();
	private final JTable table = new JTable();
	private ONUTableModel model;
	
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn ;
	private JButton closeBtn;
	
	private ButtonFactory buttonFactory;
	private OLTEntity oltNodeEntity = null;
	
	private final static String[] COLUMN_NAME = {"�˿�","ONU MAC��ַ"};
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@PostConstruct
	protected void initialize(){
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initTopPnl();
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		this.setViewSize(500, 420);
		setResource();
	}
	
	private void initTopPnl(){
		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(verifyChkBox);
		topPnl.setBorder(BorderFactory.createTitledBorder("OLT��֤"));
	}
	
	private void initCenterPnl(){
		initCenterTopPnl();
		initScrollPnl();
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(centerTopPnl,BorderLayout.NORTH);
		centerPnl.add(scrollPnl,BorderLayout.CENTER);
		centerPnl.setBorder(BorderFactory.createTitledBorder("ONUע��"));
	}
	
	private void initCenterTopPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(eponPortLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		panel.add(eponPortCombox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		panel.add(onuMacLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(onuMacFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		JPanel optionPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		optionPnl.add(panel);
		
		JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		addBtn = buttonFactory.createButton(APPEND);
		delBtn = buttonFactory.createButton(DELETE);
		buttonPnl.add(addBtn);
		buttonPnl.add(delBtn);
		
		centerTopPnl.setLayout(new BorderLayout());
		centerTopPnl.add(optionPnl,BorderLayout.CENTER);
		centerTopPnl.add(buttonPnl,BorderLayout.SOUTH);
	}
	
	private void initScrollPnl(){
		model = new ONUTableModel();
		model.setColumnName(COLUMN_NAME);
		table.setModel(model);
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		scrollPnl.getViewport().add(table);
	}
	
	private void initBottomPnl(){
		closeBtn = buttonFactory.createCloseButton();
		saveBtn = buttonFactory.createButton(SAVE);
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
//		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}
	
	private void setResource(){
		this.setTitle("ONU����");
		verifyChkBox.setText("��֤");
		eponPortLbl.setText("�˿�");
		onuMacLbl.setText("ONU MAC");
		onuMacFld.setColumns(25);
		
		eponPortCombox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				comboxAction();
			}
		});
		
		//���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}
	
	private void queryData(){
		oltNodeEntity = (OLTEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), OLTEntity.class);
		
		if (null == oltNodeEntity){
			return ;
		}
		
		String where = " where entity.oltEntity=?";
		Object[] parms = {oltNodeEntity};
		
		List<OLTPort> oltPortList = (List<OLTPort>)remoteServer.getService().findAll(OLTPort.class,where,parms);
		
		if (null == oltPortList || oltPortList.size() < 1){
//			initDataBasePort();
			return;
		}
		setValue(oltPortList);
	}
	
	private void setValue(List<OLTPort> oltPortList){
		eponPortCombox.removeAllItems();
		for(OLTPort oltPort :oltPortList){
			if(oltPort.getPortType().equals("EPON")){
				OltPortObject objects = new OltPortObject(oltPort);
				eponPortCombox.addItem(objects);
			}
		}
	}
	/**
	 * ���������
	 */
	private void initDataBasePort(){
		List<OLTPort> list = new ArrayList<OLTPort>();
		for (int i = 0 ; i < 16; i++){
			OLTPort eponPort = new OLTPort();
			eponPort.setPortName("PON_3_[EPON 0/" + (i+1) + "]");
			eponPort.setProtNo(i+1);
			eponPort.setPortType("A");
			eponPort.setPortStatus(true);
			
			list.add(eponPort);
		}
		remoteServer.getService().saveEntities(list);
	}
	
	private void comboxAction(){
		if (null == eponPortCombox.getSelectedItem() || "".equals(eponPortCombox.getSelectedItem().toString())){
			return;
		}
		
		OLTPort oltPort = ((OltPortObject)eponPortCombox.getSelectedItem()).getOltPort();
		List<List> dataList = new ArrayList<List>();
//		Iterator iterator = oltPort.getOltonuEntities().iterator();
//		while(iterator.hasNext()){
//			OLTONUEntity oltOnuEntity = (ONUEntity)iterator.next();
//			List rowList = new ArrayList();
//			rowList.add(oltOnuEntity.getMacAddress1());
//			rowList.add(oltOnuEntity);
//			dataList.add(rowList);
//		}
	}
	
	private void setTableValue(List<List> dataList){
		model.setDataList(dataList);
		model.fireTableDataChanged();
	}
	
	/**
	 * ��������
	 */
	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND, desc="����ONU����",role=Constants.MANAGERCODE)
	public void append(){
		if(!isValids()){
			return;
		}
		
		String macAddress = onuMacFld.getText().trim();
		ONUEntity oltOnuEntity = new ONUEntity();
		oltOnuEntity.setMacValue(macAddress);
		
		OLTPort oltPort = ((OltPortObject)eponPortCombox.getSelectedItem()).getOltPort();
		
		remoteServer.getService().saveEntity(oltOnuEntity);
		
		final String operate = "����";
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				//����Ϣ��ʾ�Ի���
				openMessageDialog(operate);
			}
		});
	}
	
	/**
	 * ��������
	 */
	@ViewAction(name=DELETE, icon=ButtonConstants.DELETE, desc="ɾ��ONU����",role=Constants.MANAGERCODE)
	public void delete(){
		int result = JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		if (null == oltNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ��EPON�豸","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		int row = table.getSelectedRow();
		ONUEntity oltOnuEntity = (ONUEntity)model.getValueAt(row, 1);
		
		remoteServer.getService().deleteEntity(oltOnuEntity);
		
		final String operate = "ɾ��";
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				//����Ϣ��ʾ�Ի���
				openMessageDialog(operate);
			}
		});
	}
	
	/**
	 * �������
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="����ONU����",role=Constants.MANAGERCODE)
	public void save(){
		
	}
	
	/**
	 * ��ʾ����Ի���
	 */
	private void openMessageDialog(String operate){
		MessagePromptDialog messageDlg = new MessagePromptDialog(this,operate,imageRegistry,remoteServer);
		onuMacFld.setText("");
		queryData();
		messageDlg.setMessage("�������");
		messageDlg.setVisible(true);
	}
	
	private void clear(){
		onuMacFld.setText("");
		model.setDataList(null);
		model.fireTableDataChanged();
	}
	
	/**
	 * �ж�������Ƿ�Ϸ�
	 * @return
	 */
	public boolean isValids()
	{
		boolean isValid = true;
		if (null == oltNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ��EPON�豸","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String macAddr = onuMacFld.getText().trim();
		if(null == macAddr || "".equals(macAddr))
		{
			JOptionPane.showMessageDialog(this, "MAC��ַ����Ϊ�գ�������MAC��ַ","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String regex = "^([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})$";
		Pattern p=Pattern.compile(regex);
		Matcher m=p.matcher(macAddr);
		if (!m.matches()){
			JOptionPane.showMessageDialog(this, "MAC��ַ��������������","��ʾ",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		int result = PromptDialog.showPromptDialog(this, "��ѡ�񱣴�ķ�ʽ",imageRegistry);
		if (result == 0){
			isValid = false;
			return isValid;
		}
		return isValid;
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}
	
	/**
	 * �豸�������ʱ�����
	 * ��ѡ��ͬ���豸ʱ������eponNodeEntity,���²�ѯ���ݿ�
	 * @author Administrator
	 *
	 */
	private final PropertyChangeListener eponNodeChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof EponTopoEntity){
				queryData();
			}
		}
	};
	
	@SuppressWarnings("unchecked")
	private class ONUTableModel extends AbstractTableModel{
		private String[] columnName = null;
		
		private List<List> dataList = null;
		
		public ONUTableModel(){
			super();
		}
		
		public String[] getColumnName() {
			return columnName;
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public List<List> getDataList() {
			return dataList;
		}

		public void setDataList(List<List> dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<List>();
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

	    @Override
		public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }
	  
	    @Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }
	
	    public Object getValueAt(int rowIndex, int columnIndex){
	    	return dataList.get(rowIndex).get(columnIndex);
	    }
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	dataList.get(rowIndex).set(columnIndex, aValue);
	    }
	}
	
	private class OltPortObject {
		private final OLTPort oltPort ;
		public OltPortObject(OLTPort oltPort){
			this.oltPort = oltPort;
		}
		@Override
		public String toString(){
			return this.oltPort.getPortName();
		}
		public OLTPort getOltPort() {
			return this.oltPort;
		}
	}
}
