package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ActionConstants;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.client.views.switcher.UploadRequestTask;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.PortRemon;

/**
 * 实时监控端口配置
 */
@Component(RealtimePerformConfigView.ID)
@Scope(Scopes.DESKTOP)
public class RealtimePerformConfigView extends ViewPart {
	
	@PostConstruct
	protected void initialize() {
		setTitle("端口配置");
		setLayout(new BorderLayout());
		buttonFactory = actionManager.getButtonFactory(this);
		messageSender = remoteServer.getMessageSender();
		JPanel configurePanel = new JPanel(new BorderLayout());
		createConfigurePanel(configurePanel);
		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				switcherExplorerView, configurePanel);
		splitPanel.setDividerLocation(switcherExplorerView.getViewWidth());		
		add(splitPanel, BorderLayout.CENTER);
		
		selectedSwitch =(SwitchNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		fillContents();
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, SelectionChangedListener);
		
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
	}
	
	private void fillContents() {
		if (selectedSwitch == null) {
			equipmentField.setText(StringUtils.EMPTY);
			return;
		} else {
			equipmentField.setText(selectedSwitch.getBaseConfig().getIpValue());
			portCount = selectedSwitch.getPorts().size();
			findAllPortRemon();
		}
	}
	
	private void createConfigurePanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		JPanel addingPanel = new JPanel();
		JPanel listPanel = new JPanel();
		JPanel toolPanel = new JPanel();
		createAddingPanel(addingPanel);
		createListPanel(listPanel);
		createToolPanel(toolPanel);
		parent.add(addingPanel, BorderLayout.PAGE_START);
		parent.add(listPanel, BorderLayout.CENTER);
		parent.add(toolPanel, BorderLayout.PAGE_END);
	}
	
	private void createToolPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		JButton uploadBtn = buttonFactory.createButton(UPLOAD);
		JButton downloadBtn = buttonFactory.createButton(DOWNLOAD);
		parent.add(uploadBtn);
		parent.add(downloadBtn);
	}
	
	private void createAddingPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
		parent.setBorder(BorderFactory.createTitledBorder("选项"));
		
		parent.add(new JLabel("设备"));
		equipmentField = new JTextField(15);
		equipmentField.setEditable(false);
		parent.add(equipmentField);
		
		parent.add(new JLabel("端口"));
		portField = new NumberField();
		portField.setColumns(10);
		parent.add(portField);
		
		StarLabel startLabel = new StarLabel();
		parent.add(startLabel);
		
		parent.add(new JLabel("编号"));
		codeField = new NumberField(5, 0, 0, 65535, true);
		codeField.setColumns(10);
		parent.add(codeField);
		
		StarLabel codeStartLabel = new StarLabel();
		parent.add(codeStartLabel);
		
		parent.add(buttonFactory.createButton(ActionConstants.SAVE));
		parent.add(buttonFactory.createButton(ActionConstants.DELETE));
	}
	
	private boolean isValids(){
		boolean bool = true;
		if (null == selectedSwitch){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}

		if (null == portField.getText() || "".equals(portField.getText().trim())){
			JOptionPane.showMessageDialog(this, "端口号错误，范围是：1-" + portCount,"提示",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}
		int port = NumberUtils.toInt(portField.getText());
		if (port > portCount || port < 1){
			JOptionPane.showMessageDialog(this, "端口号错误，范围是：1-" + portCount,"提示",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}
		
		if (null == codeField.getText() || "".equals(codeField.getText().trim())){
			JOptionPane.showMessageDialog(this, "条目编号错误，范围是：1-65535","提示",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}
		
		int code = NumberUtils.toInt(codeField.getText());
		if (code > 65535 || code < 1){
			JOptionPane.showMessageDialog(this, "条目编号错误，范围是：1-65535","提示",JOptionPane.NO_OPTION);
			bool = false;
			return bool;
		}
		
		int rows = table.getRowCount();
		for (int i = 0 ; i < rows ; i++){
			int value = Integer.parseInt(String.valueOf(table.getModel().getValueAt(i, 0)));
			if (port == value){
				JOptionPane.showMessageDialog(this, "列表中已经有相同的端口，请重新输入","提示",JOptionPane.NO_OPTION);
				bool = false;
				return bool;
			}
		}
		
		for(int j = 0; j < rows; j++){
			int value = Integer.parseInt(String.valueOf(table.getModel().getValueAt(j, 1)));
			if (code == value){
				JOptionPane.showMessageDialog(this, "列表中已经有相同的条目编号，请重新输入","提示",JOptionPane.NO_OPTION);
				bool = false;
				return bool;
			}
		}
		
		return bool;
	}
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载RMON端口配置",role=Constants.MANAGERCODE)
	public void download() {
		
		if (table.getSelectedRow() < 0) {
			JOptionPane.showMessageDialog(this, "请选择需要下载的数据", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		PortRemon portRemon = tableModel.getValue(table.getSelectedRow());
		
		if (portRemon.getIssuedTag() == Constants.ISSUEDDEVICE) {
			JOptionPane.showMessageDialog(this, "RMON端口配置已经存在，请重新选择", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		String macValue = selectedSwitch.getBaseInfo().getMacValue();
		portRemon.setSwitchNode(selectedSwitch);
		
		Task task = new DownLoadRequestTask(portRemon, macValue, Constants.SYN_ALL);
		showConfigureMessageDialog(task, "下载");
	}
	
	private class DownLoadRequestTask implements Task{

		private PortRemon portRemon = null;
		private int result = 0;
		private String macValue = "";
		public DownLoadRequestTask(PortRemon portRemon, String macValue, int result){
			this.portRemon = portRemon;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.REMONPORTNEW, portRemon,
						clientModel.getCurrentUser().getUserName(), clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载RMON端口配置异常");
				portField.setText("");
				codeField.setText("");
				findAllPortRemon();
				LOG.error("RealtimePerformConfigView.DownLoadRequestTask error", e);
			}
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载RMON端口配置",role=Constants.MANAGERCODE)
	public void upload() {
		if (null == selectedSwitch) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(selectedSwitch.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(selectedSwitch.getDeviceModel());
		synDeviceList.add(synchDevice);
		
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHPORTREMON);
		showUploadMessageDialog(task, "上载RMON端口配置");
	}
	
	@ViewAction(icon=ButtonConstants.SAVE,desc="保存实时性能配置",role=Constants.MANAGERCODE)
	public void save() {
		if(!isValids()) {
			return;
		}
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return ;
		}
		
		String macValue = selectedSwitch.getBaseInfo().getMacValue();
		PortRemon portRemon = new PortRemon();
		portRemon.setSwitchNode(selectedSwitch);
		portRemon.setCode(Integer.parseInt(codeField.getText()));
		portRemon.setPortNo(Integer.parseInt(portField.getText()));
		
		Task task = new SaveRequestTask(portRemon, macValue, result);
		showConfigureMessageDialog(task, "保存");
	}
	
	private class SaveRequestTask implements Task{

		private PortRemon portRemon = null;
		private int result = 0;
		private String macValue = "";
		public SaveRequestTask(PortRemon portRemon, String macValue, int result){
			this.portRemon = portRemon;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getAdmService().saveAndSetting(macValue, MessageNoConstants.REMONPORTNEW, portRemon,
						clientModel.getCurrentUser().getUserName(), clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("保存实时性能配置异常");
				portField.setText("");
				codeField.setText("");
				findAllPortRemon();
				LOG.error("RealtimePerformConfigView.SaveRequestTask error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				portField.setText("");
				codeField.setText("");
				findAllPortRemon();
			}
		}
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			paramConfigStrategy = new ParamConfigStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(paramConfigStrategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showConfigureMessageDialog(task, operation);
				}
			});
		}
	}
	
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

	
	@ViewAction(icon=ButtonConstants.DELETE,desc="删除实时性能配置",role=Constants.MANAGERCODE)
	public void delete() {
		int result = JOptionPane.showConfirmDialog(this, "你确定要删除吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if (JOptionPane.OK_OPTION != result){
			return;
		}
		if (null == selectedSwitch){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
				
		if (table.getSelectedRow() < 0) {
			JOptionPane.showMessageDialog(this, "请选择需要删除的数据", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		String macValue = selectedSwitch.getBaseInfo().getMacValue();		
		PortRemon portRemon = tableModel.getValue(table.getSelectedRow());
		
		int syn = portRemon.getIssuedTag() == Constants.ISSUEDDEVICE ? Constants.SYN_ALL : Constants.SYN_SERVER;
		
		Task task = new DeleteRequestTask(portRemon, macValue, syn);
		showConfigureMessageDialog(task, "删除");
	}
	
	private class DeleteRequestTask implements Task{

		private PortRemon portRemon = null;
		private int result = 0;
		private String macValue = "";
		public DeleteRequestTask(PortRemon portRemon, String macValue, int result){
			this.portRemon = portRemon;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getAdmService().deleteAndSetting(macValue, MessageNoConstants.REMONPORTDEL, portRemon, 
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2, result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("删除实时性能配置异常");
				findAllPortRemon();
				LOG.error("RealtimePerformConfigView.DeleteRequestTask error", e);
			}
			if (portRemon.getIssuedTag() == Constants.ISSUEDADM) {
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				findAllPortRemon();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void findAllPortRemon() {
		String where = " where entity.switchNode=?";
		Object[] parms = {selectedSwitch};
		List<PortRemon> listOfPortRemon = 
			(List<PortRemon>)remoteServer.getService().findAll(PortRemon.class, where, parms);
		tableModel.setData(listOfPortRemon);
		tableModel.fireTableDataChanged();
	}
	
	private void createListPanel(JPanel parent) {
		parent.setBorder(BorderFactory.createTitledBorder("列表"));
		parent.setLayout(new GridLayout(1, 1));
		table = new JXTable();
		table.setAutoCreateColumnsFromModel(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setEditable(false);
		table.setSortable(false);
		table.setColumnControlVisible(false);
		table.getTableHeader().setReorderingAllowed(false);
		tableModel = new PortRmonTableModel();
		table.setModel(tableModel);
		table.setColumnModel(tableModel.getColumnModel());
		JPanel tabelPanel = new JPanel(new BorderLayout(1, 2));
		tabelPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tabelPanel.add(table, BorderLayout.CENTER);
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);
		parent.add(scrollTablePanel);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, SelectionChangedListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
	}
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("Upload error", e);
			}
			findAllPortRemon();
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("Upload error", e);
			}
			findAllPortRemon();
		}
	};
	
	private ButtonFactory buttonFactory;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;	

	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=SwitcherExplorerView.ID)
	private SwitcherExplorerView switcherExplorerView;

	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;

	private MessageSender messageSender;

	private int portCount;
	private NumberField portField;
	private NumberField codeField;
	private JTextField equipmentField;
    private JXTable table;
	private PortRmonTableModel tableModel;
	private SwitchNodeEntity selectedSwitch;
	private static final Logger LOG = LoggerFactory.getLogger(RealtimePerformConfigView.class);
	private static final long serialVersionUID = 5228344893940641965L;
	public static final String ID = "realtimePerformConfigView";
	
	private final PropertyChangeListener SelectionChangedListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				selectedSwitch =(SwitchNodeEntity)adapterManager.getAdapter(
							equipmentModel.getLastSelected(), SwitchNodeEntity.class);
				fillContents();
			}
			else{
				tableModel.setData(null);
				tableModel.fireTableDataChanged();
			}
		}
	};
	
	public class PortRmonTableModel extends AbstractTableModel {

		public PortRmonTableModel() {
			data = Collections.emptyList();
			int modelIndex = 0;
			columnModel = new DefaultTableColumnModel();

			TableColumnExt portColumn = new TableColumnExt(modelIndex++, 80);
			portColumn.setIdentifier("端口");
			portColumn.setHeaderValue("端口");
			columnModel.addColumn(portColumn);
			
			TableColumnExt codeColumn = new TableColumnExt(modelIndex++, 120);
			codeColumn.setIdentifier("编号");
			codeColumn.setHeaderValue("编号");
			columnModel.addColumn(codeColumn);
			
			TableColumnExt statusColumn = new TableColumnExt(modelIndex++, 80);
			statusColumn.setIdentifier("状态");
			statusColumn.setHeaderValue("状态");
			columnModel.addColumn(statusColumn);
		}

		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public int getRowCount() {
			if (null != data){
				return data.size();
			}
			else{
				return 0;
			}
		}

		@Override
		public String getColumnName(int col) {
			return columnModel.getColumn(col).getHeaderValue().toString();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value = null;
			if (row < data.size()) {
				PortRemon server = data.get(row);
				switch (col) {
				case 0:
					value = server.getPortNo();
					break;
				case 1:
					value = server.getCode();
					break;
				case 2:
					value = dataStatus.get(server.getIssuedTag()).getKey();
					break;
				default:
					break;
				}
			}

			return value;
		}

		public TableColumnModel getColumnModel() {
			return columnModel;
		}

		public PortRemon getValue(int row) {
			PortRemon value = null;
			if (row < data.size()) {
				value = data.get(row);
			}

			return value;
		}

		public List<PortRemon> getData() {
			return data;
		}

		public void setData(List<PortRemon> data) {
			this.data = data;
		}
		
		public List<PortRemon> getSelectedData(int[] selectedRows){
			List<PortRemon> portRemonLists = new ArrayList<PortRemon>();
			for(int i = 0;i < selectedRows.length;i++){
				portRemonLists.add(this.getValue(selectedRows[i]));
			}
			return portRemonLists;
		}

		private List<PortRemon> data;
		private final TableColumnModel columnModel;
		private static final long serialVersionUID = -3065768803494840568L;
	}
}