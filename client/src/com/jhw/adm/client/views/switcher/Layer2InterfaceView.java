package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.EXPORT;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.util.Constants;

@Component(Layer2InterfaceView.ID)
@Scope(Scopes.DESKTOP)
public class Layer2InterfaceView extends ViewPart implements MessageReceiveInter{
	public static final String ID = "layer2InterfaceView";
	
	private JScrollPane scrollPnl = new JScrollPane();
	private JTable table = new JTable();
	private Layer2TableModel model = null;
	
	private JPanel bottomPnl = new JPanel();
	private JButton synBtn ;
	private JButton exportBtn;
	private JButton closeBtn;

	private ButtonFactory buttonFactory;
	
	private final static String[] COLUMN_NAME= {"索引","描述","类型","MTU","速率","MAC地址","管理状态"
		,"操作状态","更改时间","接收字节数","接收单点包","接收多点广播包","接收丢弃包","接收错误包"
		,"接收未知协议包","发送字节数","发送单点广播包","发送多点广播包","发送丢弃包","发送错误包","输出队列长度"};
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;

	@PostConstruct
	protected void initialize(){
		init();
		
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initCenterPnl();
		initBottomPnl();
		
		setResource();
		
		this.setLayout(new BorderLayout());
		this.add(scrollPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(640, 560);
	}
	
	private void initCenterPnl(){
		model = new Layer2TableModel();
		model.setColumnName(COLUMN_NAME);
		table.setModel(model);
		for (int i = 0 ; i < COLUMN_NAME.length; i++){
			TableColumn colum = table.getColumn(COLUMN_NAME[i]);
			colum.setPreferredWidth(90);
		}
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		scrollPnl.getViewport().add(table,BorderLayout.CENTER);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		closeBtn = buttonFactory.createCloseButton();
		exportBtn = buttonFactory.createButton(EXPORT);
		synBtn = buttonFactory.createButton(UPLOAD);
		newPanel.add(synBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(exportBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		bottomPnl.add(newPanel,BorderLayout.EAST);
	}
	
	private void setResource(){
		
	}
	
	private void queryData(){
		
	}
	
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="向上同步二层接口表",role=Constants.MANAGERCODE)
	public void upSynchronize(){
		
	}
	
	@ViewAction(name=EXPORT, icon=ButtonConstants.SYNCHRONIZE, desc="导出二层接口表",role=Constants.MANAGERCODE)
	public void export(){
		
	}
	

	public void receive(Object object){
		
	}
	
	
	
	class Layer2TableModel extends AbstractTableModel{
		private String[] columnName = null;
		
		private List<List> dataList = null;
		
		public Layer2TableModel(){
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

	    public String getColumnName(int columnIndex){
	    	return columnName[columnIndex];
	    }


//	    public Class<?> getColumnClass(int columnIndex){}

	  
	    public boolean isCellEditable(int rowIndex, int columnIndex){
	    	return false;
	    }

	
	    public Object getValueAt(int rowIndex, int columnIndex){
	    	return dataList.get(rowIndex).get(columnIndex);
	    }
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	dataList.get(rowIndex).set(columnIndex, aValue);
	    }
	}
 
}
