package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.SEARCH;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.PLCMarking;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.model.switcher.SwitcherModelNumber;
import com.jhw.adm.client.swing.HyalineDialog;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(SearchDeviceView.ID)
@Scope(Scopes.DESKTOP)
public class SearchDeviceView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "searchDeviceView";
	private final JLabel nodeLbl = new JLabel();
	private final JTextField nodeFld = new JTextField();
	private final JLabel nameLbl = new JLabel();
	private final JTextField nameFld = new JTextField();
	private final JLabel categoryLbl = new JLabel();
	private final JComboBox categoryCombox = new JComboBox();
	private JButton searchBtn = null;
	
	private JScrollPane centerScrollPnl = null;
	private final JTable detailTable = new JTable();
	private DetailTableModel detailModel = null;
	
	private JButton closeBtn = null;
	
	private JPopupMenu popup = null;
	
	private ButtonFactory buttonFactory;
	private HyalineDialog hyalineDialog;
	
	private final static String[] COLUMN_NAME = {"设备标识","节点名称","设备型号","所属子网"};
	private final static String[] SWITCHER_COLUMN_NAME = {"设备IP","设备名称","设备型号","所在区域"}; 
	private final static String[] ONU_COLUMN_NAME = {"MAC地址","节点名称","设备型号","所在区域"}; 
	private final static String[] CARRIER_COLUMN_NAME = {"前置机编号","节点名称","设备型号","所在区域"}; 
	private final static String[] GPRS_COLUMN_NAME = {"身份识别码","节点名称","设备型号","所在区域"}; 
	
	private final static String SWITCHERLBL = "设备IP";
	private final static String ONULBL = "MAC地址";
	private final static String CARRIERLBL = "前置机编号";
	private final static String GPRSLBL = "身份识别码";
	private final static String VIRTUALLBL = "IP地址";
	
	private final static int ALL_CATEGORY = 0;
	private final static int SWITCHER2_CATEGORY = 1;
	private final static int SWITCHER3_CATEGORY = 2;
	private final static int EPON_CATEGORY = 3;
	private final static int ONU_CATEGORY = 4;
	private final static int CARRIER_CATEGORY = 5;
	private final static int GPRS_CATEGORY = 6;
	private final static int VIRTUAL_CATEGORYd = 7;
		
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=SwitcherModelNumber.ID)
	private SwitcherModelNumber switcherModelNumber;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=PLCMarking.ID)
	private PLCMarking plcMarking;
	
	@PostConstruct
	public void initialize(){
		setTitle("搜索设备");
		buttonFactory = actionManager.getButtonFactory(this); 
		
		init();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		hyalineDialog.dispose();
	}
	
	private void init(){
		searchBtn = buttonFactory.createButton(SEARCH);
		hyalineDialog = new HyalineDialog(this);
		nodeLbl.setText("设备标识");
//		nodeLbl.setPreferredSize(new Dimension(60,(int)nodeLbl.getPreferredSize().getHeight()));
		nodeFld.setColumns(15);
	
		nameLbl.setText("节点名称");
		nameFld.setColumns(15);

		categoryLbl.setText("设备类型");
//		categoryLbl.setPreferredSize(new Dimension(60,(int)categoryLbl.getPreferredSize().getHeight()));
		categoryCombox.setModel(new MyComboBoxModel());
		categoryCombox.setPreferredSize(new Dimension((int)nodeFld.getPreferredSize().getWidth()
				,(int)categoryCombox.getPreferredSize().getHeight()));
//		categoryCombox.addItemListener(new CategoryItemListener());
		
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(nodeLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		panel.add(nodeFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		panel.add(nameLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
		panel.add(nameFld,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		panel.add(categoryLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
		panel.add(categoryCombox,new GridBagConstraints(5,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));
		
		panel.add(searchBtn,new GridBagConstraints(6,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,5,0),0,0));
		topPnl.add(panel);
		
		detailModel = new DetailTableModel();
		detailModel.setColumnName(COLUMN_NAME);
		detailTable.setModel(detailModel);
		centerScrollPnl = new JScrollPane(detailTable); 
		centerScrollPnl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		centerScrollPnl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		JPanel centerPnl = new JPanel(new BorderLayout());
		centerPnl.add(centerScrollPnl,BorderLayout.CENTER);
		centerPnl.setBorder(BorderFactory.createTitledBorder("设备信息"));
		
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.add(closeBtn);
		
		initPopupMenu();
		detailTable.addMouseListener(new DetailPopupListener());

		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(centerPnl,BorderLayout.CENTER);
//		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(650, 520);
	}
	
	private void initPopupMenu(){
		popup = new JPopupMenu();
		JMenuItem  locationMenuItem = new JMenuItem ("定位");
		popup.add(locationMenuItem);
		
		Action locationAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NodeEntity selectedNode = (NodeEntity) detailModel.getValueAt(detailTable.getSelectedRow(), 4);
				NodeUtils.fixedPositionEquipment(selectedNode);
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
				detailTable.dispatchEvent(ne);
			}
		}
	}

	@ViewAction(name=SEARCH, icon=ButtonConstants.SEARCH, desc="搜索设备",role=Constants.MANAGERCODE)
	public void search(){
		Class<?> classObject = null;
		
		final StringInteger stringInteger = (StringInteger)categoryCombox.getSelectedItem();
		int category= stringInteger.getValue();
		switch(category){
			case ALL_CATEGORY:
				searchDevice(null);
				break;
			case SWITCHER2_CATEGORY:
				classObject = SwitchTopoNodeEntity.class;
				searchDevice(classObject);
				break;
			case SWITCHER3_CATEGORY:
				classObject = SwitchTopoNodeLevel3.class;
				searchDevice(classObject);
				break;
			case EPON_CATEGORY:
				classObject = EponTopoEntity.class;
				searchDevice(classObject);
				break;
			case ONU_CATEGORY:
				classObject = ONUTopoNodeEntity.class;
				searchDevice(classObject);
				break;
			case CARRIER_CATEGORY:
				classObject = CarrierTopNodeEntity.class;
				searchDevice(classObject);
				break;
			case GPRS_CATEGORY:
				classObject = GPRSTopoNodeEntity.class;
				searchDevice(classObject);
				break;
			case VIRTUAL_CATEGORYd:
				classObject = VirtualNodeEntity.class;
				searchDevice(classObject);
				break;
			default:
				break;
		}
	}
	
	private void searchDevice(final Class<?> classObject) {
		
		final String searchStr = nodeFld.getText().trim();
		final String searchName = nameFld.getText().trim();
	
		Runnable runnable = new Runnable() {
			public void run() {
				List<Object[]> list = remoteServer.getNmsService().queryDevice(
						searchStr, searchName,classObject,clientModel.getCurrentUser());
				fillDataList(list);
			}
		};
		
		hyalineDialog.run(runnable);		
	}
	
	@SuppressWarnings("unchecked")
	private void fillDataList(List<Object[]> list) {
		final List<List> dataList = new ArrayList<List>();
		if (list != null && list.size() > 0){
			for (int i = 0 ; i < list.size(); i++){
				Object[] object = list.get(i);
				String nodeTag = "";            //节点标识
				if (object[0] != null){
					nodeTag = String.valueOf(object[0]);   
				}
				
				String name = "";               //名称
				if (object[2] == null || "".equals(object[2])){
					name = String.valueOf(object[0]);//如果为空,把设备标识赋予name
				}
				else{
					name = String.valueOf(object[2]);     
				}
				
				String deviceType = "";         //型号
				if (object[1] != null && (object[1] instanceof String)){
					deviceType = String.valueOf(object[1]);
				}
				else if (object[1] != null && NumberUtils.isNumber(String.valueOf(object[1]))){
					int type = (Integer)object[1];
					if (isCarrier(type)){
						type = type -300; //服务端传过来的是301和302，分别代表单通道和双通道
						deviceType = plcMarking.get(type).getKey();
					}
					else{
						deviceType = (switcherModelNumber.get(type)).getKey(); 
					}
				}
				
				NodeEntity nodeEntity = (NodeEntity)object[3];   //NodeEntity
				String parentNode = getParentSubnet(nodeEntity.getParentNode());
				
				List rowList = new ArrayList();
				rowList.add(nodeTag);
				rowList.add(name);
				rowList.add(deviceType);
				rowList.add(parentNode);//所在区域,以","分隔
				rowList.add(nodeEntity);
				dataList.add(rowList);
			}
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {				
				detailModel.setDataList(dataList);
				detailModel.fireTableDataChanged();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private String getParentSubnet(String parentNode){
		String path = "";
		if (parentNode != null){
			String where = " where entity.guid='" + parentNode + "'";
			List<SubNetTopoNodeEntity> subEntities = (List<SubNetTopoNodeEntity>)remoteServer.getService()
									.findAll(SubNetTopoNodeEntity.class, where);
			if(subEntities != null && subEntities.size() > 0){
				SubNetTopoNodeEntity entity = subEntities.get(0);
				path = entity.getName();
			}
		}else{
			path = equipmentModel.getDiagramName();
		}
		
		return path;
	}
	
	private boolean isCarrier(int type){
		boolean bool = false;
		if (type == 301 || type == 302){
			bool = true;
		}
		
		return bool;
	}
	
	/**
	 * 查询二层交换机
	 * queryDevice(String params,Class<?> clazz)  查询数据库返回
	 * Object[0]：ipvalue  Object[1]：设备类型     Object[2]：设备名称
	 */
//	private void searchSwitch2(){
//		List<List> dataList = new ArrayList<List>();
//		
//		String searchStr = nodeFld.getText().trim();
//		List<Object[]> list = this.remoteServer.getNmsService().queryDevice(searchStr, "",SwitchTopoNodeEntity.class);
//		
//		if (list != null && list.size() > 0){
//			for (int i = 0 ; i < list.size(); i++){
//				Object[] object = list.get(i);
//				
//				String ipValue = String.valueOf(object[0]);
//				String name = String.valueOf(object[2]);
//				String deviceType = ((StringInteger)switcherModelNumber.get((Integer)object[1])).getKey();
//				String areas = getAreas(ipValue);
//				
//				List rowList = new ArrayList();
//				rowList.add(ipValue);
//				rowList.add(name);
//				rowList.add(deviceType);
//				rowList.add(areas);//所在区域,以","分隔
//				dataList.add(rowList);
//			}
//		}
//		
//		detailModel.setDataList(dataList);
//		detailModel.fireTableDataChanged();
//	}
	
	@SuppressWarnings("unchecked")
	class DetailTableModel extends AbstractTableModel{
		private String[] columnName = null;
		private List<List> dataList = null;
		
		public DetailTableModel(){
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
	    	if (rowIndex < 0 || columnIndex < 0){
	    		return null;
	    	}
	    	return dataList.get(rowIndex).get(columnIndex);
	    }
	    
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	dataList.get(rowIndex).set(columnIndex, aValue);
	    }
	}	

	class MyComboBoxModel extends AbstractListModel implements ComboBoxModel{
		private final StringInteger allCategory = new StringInteger("全部",ALL_CATEGORY);
		private final StringInteger switcher2Category = new StringInteger("二层交换机",SWITCHER2_CATEGORY);
		private final StringInteger switcher3Category = new StringInteger("三层交换机",SWITCHER3_CATEGORY);
		private final StringInteger eponCategory = new StringInteger("OLT",EPON_CATEGORY);
		private final StringInteger onuCategory = new StringInteger("ONU",ONU_CATEGORY);
		private final StringInteger virtualCategory = new StringInteger("虚拟网元",VIRTUAL_CATEGORYd);
		
		
		private final Object[] CATEGORY = {allCategory,switcher2Category,switcher3Category,eponCategory,onuCategory,virtualCategory};
		private StringInteger item = null ;
		
		public Object getSelectedItem(){
			if (this.item == null){
				this.item = allCategory;
			} 
		   return this.item;
		}
		public void setSelectedItem(Object arg){
		   this.item = (StringInteger)arg;
		} 
		public Object getElementAt(int ind){ //根据编号返回选项
		   return this.CATEGORY[ind];
		}
		public int getSize(){    //取得选项长度
		   return this.CATEGORY.length;
		}
	}
	
	class CategoryItemListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent e) {
			StringInteger stringInteger = (StringInteger)categoryCombox.getSelectedItem();
			int category= stringInteger.getValue();
			switch(category){
				case SWITCHER2_CATEGORY:
					nodeLbl.setText(SWITCHERLBL);
					break;
				case SWITCHER3_CATEGORY:
					nodeLbl.setText(SWITCHERLBL);
					break;
				case EPON_CATEGORY:
					nodeLbl.setText(SWITCHERLBL);
					break;
				case ONU_CATEGORY:
					nodeLbl.setText(ONULBL);
					break;
				case CARRIER_CATEGORY:
					nodeLbl.setText(CARRIERLBL);
					break;
				case GPRS_CATEGORY:
					nodeLbl.setText(GPRSLBL);
					break;
				case VIRTUAL_CATEGORYd:
					nodeLbl.setText(VIRTUALLBL);
					break;
				default:
					break;
			}
		}
	}

}
