package com.jhw.adm.client.views.carrier;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.carrier.CarrierPortSubnet;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.util.Constants;

@org.springframework.stereotype.Component(CarrierSubnetInfoView.ID)
@Scope(Scopes.DESKTOP)
public class CarrierSubnetInfoView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "carrierSubnetInfoView";
	private static final Logger LOG = LoggerFactory.getLogger(CarrierSubnetInfoView.class);
	
	private JPanel topPnl = new JPanel();
	private JLabel subnetLbl = new JLabel("编号");
	private JFormattedTextField subnetFld = null;
	private JLabel subnetNameLbl = new JLabel("名称");
	private JFormattedTextField subnetNameFld = null;
	private JButton addButton = null;
	private JButton delButton = null;

	private JPanel centerPnl = new JPanel(new BorderLayout(1, 2));
	private JTable subnetTable = new JTable();
	private SubnetTableModel subnetTableModel;
	
	private JPanel bottomPnl = new JPanel();
	private JButton saveBtn = null;
	
	private ButtonFactory buttonFactory;
	
	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;
	
	@PostConstruct
	protected void initialize() {
		this.setTitle("子网设置");
		buttonFactory = actionManager.getButtonFactory(this);
		
		NumberFormat integerFormat = NumberFormat.getNumberInstance();
		integerFormat.setMinimumFractionDigits(0);
		integerFormat.setParseIntegerOnly(true);
		integerFormat.setGroupingUsed(false);
		
		NumberFormat stringFormat = NumberFormat.getInstance();
		stringFormat.setMinimumFractionDigits(0);
		stringFormat.setParseIntegerOnly(true);
		stringFormat.setGroupingUsed(false);
		
		subnetFld = new JFormattedTextField();
		subnetFld.setColumns(10);	
		
		subnetNameFld = new JFormattedTextField();
		subnetNameFld.setColumns(10);	
		
		addButton = buttonFactory.createButton(APPEND);
		delButton = buttonFactory.createButton(DELETE);
		
		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(subnetNameLbl);
		topPnl.add(subnetNameFld);
		topPnl.add(subnetLbl);
		topPnl.add(subnetFld);
		topPnl.add(new JLabel("(1-255)"));
		topPnl.add(addButton);
		topPnl.add(delButton);
		
		subnetTableModel = new SubnetTableModel();
		subnetTable.setModel(subnetTableModel);
		subnetTable.getColumnModel().getColumn(0).setCellRenderer(new SubnetTableCellRenderer());
		centerPnl.add(subnetTable.getTableHeader(), BorderLayout.PAGE_START);
		centerPnl.add(subnetTable, BorderLayout.CENTER);
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(centerPnl, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);
		
		saveBtn = buttonFactory.createButton(SAVE);
		bottomPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		bottomPnl.add(saveBtn);
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(scrollTablePanel,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		this.setViewSize(480, 400);
		
		fillComponent();
	}
	private void fillComponent(){
		List<List> list = CarrierTools.readXml();
		if (list == null){
			return;
		}
		
//		//更新PLCPortSubnet中的值，便于在端口查询面板中实时显示
		CarrierPortSubnet.setList(list);
		
		subnetTableModel.setData(list);
		subnetTableModel.fireTableDataChanged();
	}
	
//	public List<List> getData(){
//		return subnetTableModel.getData();
//	}
	
	@ViewAction(icon = ButtonConstants.SAVE, desc="保存载波机子网信息",role=Constants.MANAGERCODE)
	public void save() {
		List<List> list = subnetTableModel.getData();
		CarrierTools.writeXml(list);
		JOptionPane.showMessageDialog(CarrierSubnetInfoView.this, "保存成功");
		
//		//更新CarrierPortSubnet中的值，便于在端口查询面板中实时显示
		CarrierPortSubnet.setList(list);
	}
	
	@ViewAction(icon = ButtonConstants.APPEND, desc="增加载波机子网信息",role=Constants.MANAGERCODE)
	public void append() {

		String name = subnetNameFld.getText();
		String code = subnetFld.getText().trim();
		List<List> listData = subnetTableModel.getData();
		
		if (null == name || "".equals(name)){
			return;
		}
		if (null == code || "".equals(code)){
			return;
		}
		if (!isNumeric(code)){
			JOptionPane.showMessageDialog(CarrierSubnetInfoView.this, "子网编号的范围为(1-255),请重新输入");
			return;
		}
		int iCode = Integer.parseInt(code);
		if(iCode> 255 || iCode < 1){
			JOptionPane.showMessageDialog(CarrierSubnetInfoView.this, "子网编号的范围为(1-255),请重新输入");
			return;
		}
		if (listData != null){
			for (int i = 0 ; i < listData.size(); i++){
				List rowList = listData.get(i);
				String codeStr = String.valueOf(rowList.get(1));
				if (code.equalsIgnoreCase(codeStr)){
					JOptionPane.showMessageDialog(CarrierSubnetInfoView.this, "已经有相同的子网编号,请重新输入");
					return;
				}
			}
		}
		
		List rowList = new ArrayList();
		rowList.add(0,name);
		rowList.add(1,code);
		subnetTableModel.addSubnet(rowList);
		subnetTableModel.fireTableDataChanged();
		subnetFld.setText("");
		subnetNameFld.setText("");
	
	}
	
	@ViewAction(icon = ButtonConstants.DELETE, desc="删除载波机子网信息",role=Constants.MANAGERCODE)
	public void delete() {
		if (subnetTable.getSelectedRow()<0){
			return;
		}
		subnetTableModel.removeSubnet(subnetTable.getSelectedRow());
		subnetTableModel.fireTableDataChanged();
	}
	
	
	//用正则表达式   
	private static boolean isNumeric(String str){   
	    Pattern pattern = Pattern.compile("[0-9]*");   
	    return pattern.matcher(str).matches();      
	} 
	
	class SubnetTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -3065768803494840568L;
		private final String[] COLUMNNAME = {"子网名称","子网编号"};
        private List<List> listOfSubnet;
        public SubnetTableModel(){
        	listOfSubnet = new ArrayList<List>();
        }
        
        public void addSubnet(List subnet) {
        	listOfSubnet.add(subnet);
        }
        
        public void removeSubnet(int index) {
        	listOfSubnet.remove(index);
        }

		public void setData(List<List> listOfSubnet) {
			this.listOfSubnet = listOfSubnet;
		}
		
		public List<List> getData() {
			return listOfSubnet;
		}

		public int getColumnCount() {
            return COLUMNNAME.length;
        }

        public int getRowCount() {
            return listOfSubnet == null ? 0 : listOfSubnet.size();
        }

        public String getColumnName(int col) {
            return COLUMNNAME[col];
        }

        public Object getValueAt(int row, int col) {
        	if (listOfSubnet == null) return null;
        	
        	Object value = listOfSubnet.get(row).get(col);
        	return value;
        }

        public boolean isCellEditable(int row, int col) {
        	return false;
        }
    }
	
	class SubnetTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
		public Component getTableCellRendererComponent(JTable table, Object value,
			    boolean isSelected, boolean hasFocus, 
			    int row, int column){
			setHorizontalAlignment(SwingConstants.CENTER);
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
}
