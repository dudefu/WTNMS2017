package com.jhw.adm.client.views.switchlayer3;

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
import com.jhw.adm.client.core.RemoteServer;
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
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.level3.SwitchPortLevel3;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(Layer3PortInformationView.ID)
@Scope(Scopes.DESKTOP)
public class Layer3PortInformationView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "layer3PortInformationView";
	private static final Logger LOG = LoggerFactory.getLogger(Layer3PortInformationView.class);
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private JScrollPane portInformationPanel = new JScrollPane();
	private JXTable table = new JXTable();
	private SwitchPortLevel3TableModel model = new SwitchPortLevel3TableModel();
	
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
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
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
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//ǰ�û�������
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
		this.setTitle("���㽻�����˿���Ϣ");
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
					LOG.error("Layer3PortInformationView.initializeTableData() error", e);
				}
			}
		};
		executorService.execute(queryDataThread);
	}
	
	private SwitchTopoNodeLevel3 switchTopoNodeLevel3 = null;
	private void queryData(){
		List<SwitchPortLevel3> switchPortLevel3List = new ArrayList<SwitchPortLevel3>();
		switchTopoNodeLevel3 = (SwitchTopoNodeLevel3) adapterManager
				.getAdapter(equipmentModel.getLastSelected(),SwitchTopoNodeLevel3.class);
		if(null == switchTopoNodeLevel3){
			fireTableDataChange(switchPortLevel3List);
			setStatus(switchPortLevel3List);
			return;
		}
		switchTopoNodeLevel3 = NodeUtils.getNodeEntity(switchTopoNodeLevel3);
		SwitchLayer3 switchLayer3 = switchTopoNodeLevel3.getSwitchLayer3();
		for(SwitchPortLevel3 switchPortLevel3 : switchLayer3.getPorts()){
			switchPortLevel3List.add(switchPortLevel3);
		}
		fireTableDataChange(switchPortLevel3List);
		setStatus(switchPortLevel3List);
	}
	
	private void setStatus(final List<SwitchPortLevel3> switchPortLevel3List) {
		if(SwingUtilities.isEventDispatchThread()){
			if(switchPortLevel3List.size() == 0){
				statusField.setText("");
			}else{
				int status = switchPortLevel3List.get(0).getIssuedTag();
				statusField.setText(dataStatus.get(status).getKey());
			}
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setStatus(switchPortLevel3List);
				}
			});
		}
	}

	private void fireTableDataChange(final List<SwitchPortLevel3> switchPortLevel3List){
		if(SwingUtilities.isEventDispatchThread()){
			model.setData(switchPortLevel3List);
			model.fireTableDataChanged();
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					fireTableDataChange(switchPortLevel3List);
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
		statusPanel.add(new JLabel("״̬"));
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

	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="�������㽻�����˿���Ϣ",role=Constants.MANAGERCODE)
	public void upload(){
		
		if(null == switchTopoNodeLevel3){
			JOptionPane.showMessageDialog(this, "��ѡ�����㽻����","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		SwitchLayer3 switchLayer3 = switchTopoNodeLevel3.getSwitchLayer3();
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchLayer3.getIpValue());
		synchDevice.setModelNumber(switchLayer3.getDeviceModel());
		synDeviceList.add(synchDevice);

		Task task = new UploadRequestTask(synDeviceList,MessageNoConstants.SINGLESWITCHLAYER3PORT);
		showUploadMessageDialog(task, "���ض˿���Ϣ");
	}
	
	private JProgressBarModel progressBarModel;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	private void showUploadMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			uploadStrategy = new ParamUploadStrategy(operation, progressBarModel,true);
			dialog = new JProgressBarDialog("��ʾ",0,1,this,operation,true);
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
	
	private class SwitchPortLevel3TableModel extends AbstractTableModel{
		private static final long serialVersionUID = -3065768803494840568L;
		private List<SwitchPortLevel3> switchPortLevel3List;
		private final TableColumnModelExt columnModel;
		private static final String GIGA_PORT = "ǧ�׿�";
		private static final String FAST_PORT = "���׿�";
		private static final int ONE = 1;
		private static final int TWO = 2;
		private static final String CONNECT = "����";
		private static final String DISCONNECT = "�Ͽ�";
		private static final String DUPLEX = "ȫ˫��";
		private static final String HALFDUPLEX = "��˫��";
		private static final String OPEN = "��";
		private static final String CLOSE = "�ر�";
		
		public SwitchPortLevel3TableModel() {
			switchPortLevel3List = new ArrayList<SwitchPortLevel3>();

			int modelIndex = 0;
			columnModel = new DefaultTableColumnModelExt();
			
			TableColumnExt portNameColumn = new TableColumnExt(modelIndex++, 120);
			portNameColumn.setIdentifier("�˿�����");
			portNameColumn.setTitle("�˿�����");
			columnModel.addColumn(portNameColumn);

			TableColumnExt portIPColumn = new TableColumnExt(modelIndex++,160);
			portIPColumn.setIdentifier("�˿�IP");
			portIPColumn.setTitle("�˿�IP");
			columnModel.addColumn(portIPColumn);
			
			TableColumnExt macColumn = new TableColumnExt(modelIndex++, 200);
			macColumn.setIdentifier("MAC��ַ");
			macColumn.setTitle("MAC��ַ");
			columnModel.addColumn(macColumn);
			
			TableColumnExt enableColumn = new TableColumnExt(modelIndex++, 80);
			enableColumn.setIdentifier("ʹ��");
			enableColumn.setTitle("ʹ��");
			columnModel.addColumn(enableColumn);
			
			TableColumnExt connectStatusColumn = new TableColumnExt(modelIndex++, 120);
			connectStatusColumn.setIdentifier("����״̬ ");
			connectStatusColumn.setTitle("����״̬ ");
			columnModel.addColumn(connectStatusColumn);
			
			TableColumnExt rateColumn = new TableColumnExt(modelIndex++, 80);
			rateColumn.setIdentifier("����");
			rateColumn.setTitle("����");
			columnModel.addColumn(rateColumn);
			
			TableColumnExt duplexColumn = new TableColumnExt(modelIndex++, 80);
			duplexColumn.setIdentifier("˫��ģʽ");
			duplexColumn.setTitle("˫��ģʽ");
			columnModel.addColumn(duplexColumn);
			
			TableColumnExt flowControlColumn = new TableColumnExt(modelIndex++, 80);
			flowControlColumn.setIdentifier("����");
			flowControlColumn.setTitle("����");
			columnModel.addColumn(flowControlColumn);
		}
		
		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return switchPortLevel3List.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			
			if (switchPortLevel3List.size() > 0) {
				SwitchPortLevel3 switchPortLevel3 = switchPortLevel3List.get(row);
				switch (col) {
				case 0://�˿�����
					value = switchPortLevel3.getPortName();
					break;
				case 1://�˿�IP
					value = switchPortLevel3.getIpValue();
					break;
				case 2://MAC��ַ
					value = switchPortLevel3.getMacValue();
					break;
				case 3://ʹ��
					value = switchPortLevel3.getEnableStr();
					break;
				case 4://����״̬
					if(switchPortLevel3.getConnect() == ONE){
						value = CONNECT;
					}else if(switchPortLevel3.getConnect() == TWO){
						value = DISCONNECT;
					}
					break;
				case 5://����
					value = switchPortLevel3.getRate();
					break;
				case 6://˫��ģʽ
					if(switchPortLevel3.getDuplex() == ONE){
						value = DUPLEX;
					}else if(switchPortLevel3.getDuplex() == TWO){
						value = HALFDUPLEX;
					}
					break;
				case 7://����
					if(switchPortLevel3.getFlowControl() == ONE){
						value = OPEN;
					}else if(switchPortLevel3.getFlowControl() == TWO){
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

//		public SwitchPortLevel3 getValue(int row) {
//			SwitchPortLevel3 value = null;
//			if (row < switchPortLevel3List.size()) {
//				value = switchPortLevel3List.get(row);
//			}
//			return value;
//		}

		public void setData(List<SwitchPortLevel3> data) {
			if (null == data) {
				return;
			}
			this.switchPortLevel3List = data;
		}
	}
}
