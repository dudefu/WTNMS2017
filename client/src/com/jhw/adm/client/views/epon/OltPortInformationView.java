package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.TextMessage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

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
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.client.views.switcher.UploadRequestTask;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(OltPortInformationView.ID)
@Scope(Scopes.DESKTOP)
public class OltPortInformationView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "oltPortInformationView";
	private static final Logger LOG = LoggerFactory.getLogger(OltPortInformationView.class);
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private JScrollPane portInformationPanel = new JScrollPane();
	private JXTable table = new JXTable();
	private OltPortTableModel model = new OltPortTableModel();
	
	private JPanel buttonPanel = new JPanel();
	private JButton uploadButton = null;
	private JButton closeButton = null;
	private ButtonFactory buttonFactory;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private PropertyChangeListener portListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			initializeTableData();
		}
	};
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("messageUploadProcessor error", e);
			}
			initializeTableData();
		}
	};
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("messageUploadProcessor error", e);
			}
			initializeTableData();
		}
	};
	
	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this);
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		createPortInfoPanel(portInformationPanel);
		createButtonPanel(buttonPanel);
		
		this.setLayout(new BorderLayout());
		this.add(portInformationPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.PAGE_END);
		this.setTitle("OLT端口信息");
		this.setViewSize(800, 700);
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, portListener);
		initializeTableData();
	}

	private void initializeTableData() {
		final Runnable queryDataThread = new Runnable() {
			@Override
			public void run() {
				try{
					queryData();
				}catch(Exception e){
					LOG.error("OltPortInformationView.initializeTableData() error", e);
				}
			}
		};
		executorService.execute(queryDataThread);
	}
	
	private EponTopoEntity eponTopoEntity = null;
	private void queryData(){
		List<OLTPort> oltPortList = new ArrayList<OLTPort>();
		eponTopoEntity = (EponTopoEntity) adapterManager
				.getAdapter(equipmentModel.getLastSelected(),EponTopoEntity.class);
		if(null == eponTopoEntity){
			fireTableDataChange(oltPortList);
			setStatus(oltPortList);
			return;
		}
		eponTopoEntity = NodeUtils.getNodeEntity(eponTopoEntity);
		OLTEntity oltEntity = eponTopoEntity.getOltEntity();
		for(OLTPort oltPort  : oltEntity.getPorts()){
			oltPortList.add(oltPort);
		}
		fireTableDataChange(oltPortList);
		setStatus(oltPortList);
	}
	
	private void setStatus(final List<OLTPort> oltPortList) {
		if(SwingUtilities.isEventDispatchThread()){
			if(oltPortList.size() == 0){
				statusField.setText("");
			}else{
				int status = oltPortList.get(0).getIssuedTag();
				statusField.setText(dataStatus.get(status).getKey());
			}
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setStatus(oltPortList);
				}
			});
		}
	}

	private void fireTableDataChange(final List<OLTPort> oltPortList){
		if(SwingUtilities.isEventDispatchThread()){
			model.setData(oltPortList);
			model.fireTableDataChanged();
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					fireTableDataChange(oltPortList);
				}
			});
		}
	}

	private JTextField statusField = new JTextField();
	
	private void createButtonPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		statusField.setColumns(15);
		statusField.setEditable(false);
		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		statusPanel.add(new JLabel("状态"));
		statusPanel.add(statusField);
		
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		uploadButton = buttonFactory.createButton(UPLOAD);
		closeButton = buttonFactory.createCloseButton();
		this.setCloseButton(closeButton);
		controlPanel.add(uploadButton);
		controlPanel.add(closeButton);
		
		parent.add(statusPanel,BorderLayout.WEST);
		parent.add(controlPanel,BorderLayout.EAST);
	}

	private void createPortInfoPanel(JScrollPane parent) {
		
		parent.getHorizontalScrollBar().setFocusable(false);
		parent.getVerticalScrollBar().setFocusable(false);
		parent.getHorizontalScrollBar().setUnitIncrement(30);
		parent.getVerticalScrollBar().setUnitIncrement(30);
		
		table.setAutoCreateColumnsFromModel(false);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(true);
		
		table.setModel(model);
		table.setColumnModel(model.getColumnModel());
		table.getTableHeader().setReorderingAllowed(false);
		
		parent.getViewport().add(table);
	}

	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载OLT端口信息",role=Constants.MANAGERCODE)
	public void upload(){
		
		if(null == eponTopoEntity){
			JOptionPane.showMessageDialog(this, "请选择OLT","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		OLTEntity oltEntity = eponTopoEntity.getOltEntity();
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(oltEntity.getIpValue());
		synchDevice.setModelNumber(oltEntity.getDeviceModel());
		synDeviceList.add(synchDevice);

		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLEOLTPORT);
		showUploadMessageDialog(task, "上载OLT端口信息");
	}
	
	private JProgressBarModel progressBarModel;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	private void showUploadMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			uploadStrategy = new ParamUploadStrategy(operation, progressBarModel,true);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(uploadStrategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showUploadMessageDialog(task, operation);
				}
			});
		}
	}
	
	@Override
	public void dispose(){
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, portListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
	
	private class OltPortTableModel extends AbstractTableModel{
		private static final long serialVersionUID = -3065768803494840568L;
		private List<OLTPort> oltPortList;
		private final TableColumnModelExt columnModel;
		private static final int ONE = 1;
		private static final int TWO = 2;
		private static final String CONNECT = "连接";
		private static final String DISCONNECT = "断开";
		private static final String DUPLEX = "全双工";
		private static final String HALFDUPLEX = "半双工";
		private static final String OPEN = "打开";
		private static final String CLOSE = "关闭";
		
		public OltPortTableModel() {
			oltPortList = new ArrayList<OLTPort>();

			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			
			TableColumnExt portNameColumn = new TableColumnExt(modelIndex++, 120);
			portNameColumn.setIdentifier("端口名称");
			portNameColumn.setTitle("端口名称");
			columnModel.addColumn(portNameColumn);

			TableColumnExt macColumn = new TableColumnExt(modelIndex++, 200);
			macColumn.setIdentifier("MAC地址");
			macColumn.setTitle("MAC地址");
			columnModel.addColumn(macColumn);
			
			TableColumnExt enableColumn = new TableColumnExt(modelIndex++, 80);
			enableColumn.setIdentifier("使能");
			enableColumn.setTitle("使能");
			columnModel.addColumn(enableColumn);
			
			TableColumnExt connectStatusColumn = new TableColumnExt(modelIndex++, 120);
			connectStatusColumn.setIdentifier("连接状态 ");
			connectStatusColumn.setTitle("连接状态 ");
			columnModel.addColumn(connectStatusColumn);
			
			TableColumnExt rateColumn = new TableColumnExt(modelIndex++, 80);
			rateColumn.setIdentifier("速率");
			rateColumn.setTitle("速率");
			columnModel.addColumn(rateColumn);
			
			TableColumnExt duplexColumn = new TableColumnExt(modelIndex++, 80);
			duplexColumn.setIdentifier("双工模式");
			duplexColumn.setTitle("双工模式");
			columnModel.addColumn(duplexColumn);
			
			TableColumnExt flowControlColumn = new TableColumnExt(modelIndex++, 80);
			flowControlColumn.setIdentifier("流控");
			flowControlColumn.setTitle("流控");
			columnModel.addColumn(flowControlColumn);
		}
		
		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return oltPortList.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			
			if (oltPortList.size() > 0) {
				OLTPort oltPort = oltPortList.get(row);
				switch (col) {
				case 0://端口名称
					value = oltPort.getPortName();
					break;
				case 1://MAC地址
					value = oltPort.getMacValue();
					break;
				case 2://使能
					value = oltPort.getEnableStr();
					break;
				case 3://连接状态
					if(oltPort.getConnect() == ONE){
						value = CONNECT;
					}else if(oltPort.getConnect() == TWO){
						value = DISCONNECT;
					}
					break;
				case 4://速率
					value = oltPort.getRate();
					break;
				case 5://双工模式
					if(oltPort.getDuplex() == ONE){
						value = DUPLEX;
					}else if(oltPort.getDuplex() == TWO){
						value = HALFDUPLEX;
					}
					break;
				case 6://流控
					if(oltPort.getFlowControl() == ONE){
						value = OPEN;
					}else if(oltPort.getFlowControl() == TWO){
						value = CLOSE;
					}
					break;
				default:
					break;
				}
			}
			return value;
		}
		
		@Override
		public String getColumnName(int col) {
			return columnModel.getColumnExt(col).getTitle();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public TableColumnModel getColumnModel() {
			return columnModel;
		}

		public void setData(List<OLTPort> data) {
			if (null == data) {
				return;
			}
			this.oltPortList = data;
		}
	}
}
