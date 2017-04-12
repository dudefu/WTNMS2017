package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.APPEND;
import static com.jhw.adm.client.core.ActionConstants.DELETE;
import static com.jhw.adm.client.core.ActionConstants.START;
import static com.jhw.adm.client.core.ActionConstants.STOP;
import static com.jhw.adm.client.core.ActionConstants.CLEAN;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.MessagePromptDialog;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PingResult;

@Component(PingToolRealtimeView.ID)
@Scope(Scopes.DESKTOP)
public class PingToolRealtimeView extends ViewPart {
	public static final String ID = "pingToolRealtimeView";
	
	private static final long serialVersionUID = 1L;
	
	private IpAddressField ipField = new IpAddressField();
	
//	private JButton addBtn ;//添加
	private JButton deleteBtn;//删除
	private JButton startBtn;//开始
	private JButton stopBtn;//停止
	
	//private JTable table = new JTable();
	private JTextArea textArea = new JTextArea();
	
	//private IpTableModel model = null;
	
	private ArrayList fepCodeList = new ArrayList();
	
	private String fepCode = "";
	
	private ButtonFactory buttonFactory;
	
	private static final Logger LOG = LoggerFactory.getLogger(PingToolRealtimeView.class);
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;	
	
	private MessageSender messageSender;	
	private MessageProcessorAdapter messageProcessor;
	private MessageProcessorAdapter messageFepOfflineProcessor;
	
	@Autowired
	@Qualifier(MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Autowired
	@Qualifier(DateFormatter.ID)
	private DateFormatter format;
	
	@PostConstruct
	protected void initialize() {
		messageSender = remoteServer.getMessageSender();
		buttonFactory = actionManager.getButtonFactory(this); 
		
		init();
		
		//异步消息的接收
		//receive();
		
		//从数据库中查询
		//queryData();
	}
	
	/**
	 * 视图初始化
	 */
	private void init(){
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
		JToolBar addingBar = new JToolBar();
		JToolBar toolBar = new JToolBar();
		addingBar.setFloatable(false);
		toolBar.setFloatable(false);

		addingBar.add(new JLabel("IP: "));
		addingBar.add(ipField);		
		
//		addBtn = buttonFactory.createButton(APPEND);
		startBtn = buttonFactory.createButton(START);
		stopBtn = buttonFactory.createButton(STOP);
		deleteBtn = buttonFactory.createButton(CLEAN);
//		addingBar.add(addBtn);
		toolBar.add(startBtn);
		toolBar.add(stopBtn);
		toolBar.add(deleteBtn);
		
		toolPanel.add(addingBar);
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(2, ipField.getPreferredSize().height));
		toolPanel.add(separator);
		toolPanel.add(toolBar);

//		String[] columnNames = { "IP",  "Ping时间", "结果信息" };
//		model = new IpTableModel();
//		model.setColumnName(columnNames);
//		table.setModel(model);
		
		textArea.setFont(new   Font( "宋体 ",Font.BOLD,14));
		//t1.setFont(new   Font( "标楷体 ",Font.BOLD,16));
		JPanel tabelPanel = new JPanel(new BorderLayout(1, 2));
		tabelPanel.add(textArea, BorderLayout.PAGE_START);
		tabelPanel.add(textArea, BorderLayout.CENTER);
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(textArea, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);

		setLayout(new BorderLayout());
		add(toolPanel, BorderLayout.PAGE_START);
		add(scrollTablePanel, BorderLayout.CENTER);
		setViewSize(640, 480);
		
		setResource();
	}
	
	/**
	 * 设置资源文件
	 */
	private void setResource(){
		setTitle("实时Ping工具");
		ipField.setColumns(25);
	}

	/**
	 * 从数据库中查询数据
	 */
	private void queryData(){
		List<PingResult> pingResultList = (List<PingResult>)remoteServer.getService().findAll(PingResult.class);
		if (null == pingResultList){
			return ;
		}
		
		setValue(pingResultList);
	}
	
	/**
	 * 把从数据中查询到的数据显示到视图中
	 * @param pingResultList
	 */
	@SuppressWarnings("unchecked")
	private void setValue(List<PingResult> pingResultList){
		List<List> dataList = new ArrayList<List>();
		for (int i = 0 ; i < pingResultList.size(); i++){
			PingResult pingResult = pingResultList.get(i);
			List rowList = new ArrayList();
			rowList.add(0,pingResult.getIpValue());
			
			String date = "";
			if (null != pingResult.getTime()){
				String pattern = "yyyy-MM-dd HH:mm:ss:SSS";
				date = format.format(pingResult.getTime(),pattern);
			}
			rowList.add(1,date);
			//rowList.add(2,reverseIntToString(pingResult.getStatus()));
			rowList.add(3,pingResult);
			
			dataList.add(rowList);
		}
		
		//model.setDataList(dataList);
		//model.fireTableDataChanged();
	}
	
//	@ViewAction(name=APPEND, icon=ButtonConstants.APPEND,desc="添加ping设备",role=Constants.MANAGERCODE)
//	public void append(){
//
//		if(!isValids()){
//			return;
//		}
//		
//		PingResult pingResult = new PingResult();
//		pingResult.setIpValue(ipField.getText());
//		pingResult.setStatus(9);
//		
//		pingResult.setTime(new Date());
//		
//		for (int i = 0 ; i < table.getRowCount(); i++){
//			String ipValue = String.valueOf(table.getValueAt(i, 0));
//			if (ipValue.equalsIgnoreCase(ipField.getText().trim())){
//				ipField.setIpAddress("");
//				return;
//			}
//		}
//		
//		Task task = new RequestTask(pingResult);
//		showMessageDialog(task, "添加");
//	}
	
//	private class RequestTask implements Task{
//		
//		private PingResult pingResult = null;
//		public RequestTask(PingResult pingResult){
//			this.pingResult = pingResult;
//		}
//		
//		@Override
//		public void run() {
//			try{
//				remoteServer.getService().saveEntity(pingResult);
//			}catch(Exception e){
//				strategy.showErrorMessage("添加ping设备异常");
//				queryData();
//				ipField.setIpAddress("");
//				LOG.error("PingToolRealtimeView.append() error", e);
//			}
//			strategy.showNormalMessage("添加ping设备成功");
//			queryData();
//			ipField.setIpAddress("");
//		}
//	}
	
	private Thread thread;
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task,operation);
				}
			});
		}
	}
	
	private boolean isValids(){
		boolean isValids = true;
		
		String ipAddress = ipField.getText().trim();
		
		if (StringUtils.isBlank(ipAddress)){
			JOptionPane.showMessageDialog(this, "IP地址不能为空，请重新输入","提示",JOptionPane.NO_OPTION);
			isValids = false;
			return isValids;
		}else if(ClientUtils.isIllegal(ipAddress)){
			JOptionPane.showMessageDialog(this, "IP地址非法，请重新输入","提示",JOptionPane.NO_OPTION);
			isValids = false;
			return isValids;
		}
		
//		if (table.getRowCount() >= 20){
//			JOptionPane.showMessageDialog(this, "最大支持20个IP地址的ping功能,现已经有20个IP地址了,请删除后再增加。","提示"
//					,JOptionPane.NO_OPTION);
//			isValids = false;
//			return isValids;
//		}
		
		return isValids;
	}
	
	@ViewAction(name=CLEAN, icon=ButtonConstants.CLEAN,desc="删除ping设备",role=Constants.MANAGERCODE)
	public void delete(){
//		int[] rows = table.getSelectedRows();
//		if (rows.length < 1){
//			JOptionPane.showMessageDialog(this, "请选择需要删除的记录", "提示", JOptionPane.NO_OPTION);
//			return;
//		}
//		
//		int result = JOptionPane.showConfirmDialog(this, "你确定要删除此记录吗？","提示",JOptionPane.OK_CANCEL_OPTION);
//		if (JOptionPane.OK_OPTION != result){
//			return;
//		}
//		
//		int[] modelRows = new int[rows.length];
//		for (int i = 0 ; i < rows.length; i++){
//			modelRows[i] = table.convertRowIndexToModel(rows[i]);
//		}
//		
//		List<PingResult> list = new ArrayList<PingResult>();
//		for (int k = 0 ; k < modelRows.length; k++){
//			PingResult pingResult = (PingResult)model.getValueAt(modelRows[k], table.getColumnCount());
//			list.add(pingResult);
//		}
//
//		Task task = new DeleteRequestTask(list);
//		showMessageDialog(task, "删除");
		textArea.setText("");
	}
//	
//	private class DeleteRequestTask implements Task{
//		
//		private List<PingResult> list = null;
//		public DeleteRequestTask(List<PingResult> list){
//			this.list = list;
//		}
//		
//		@Override
//		public void run() {
//			try{
//				remoteServer.getService().deleteEntities(list);
//			}catch(Exception e){
//				strategy.showErrorMessage("删除ping设备异常");
//				queryData();
//				LOG.error("PingToolRealtimeView.delete() error", e);
//			}
//			strategy.showNormalMessage("删除ping设备成功");
//			queryData();
//		}
//	}
	
	@ViewAction(name=START, icon=ButtonConstants.START, desc="开始Ping设备",role=Constants.MANAGERCODE)
	public void start(){
		final String ipAddress = ipField.getText().trim();
		if (StringUtils.isBlank(ipAddress)){
			JOptionPane.showMessageDialog(this, "请输入设备的IP地址","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		//setComponentEnabled(false);
		
		thread = new Thread(new Runnable() {
			public void run() {
				long t = 0l;
				BufferedReader in = null;
				Runtime r = Runtime.getRuntime();
				String osName = System.getProperty("os.name").trim();
				String pingCommand = "";
				boolean isReceive = false;// 判断是否接收到交换机重启成功

				while (true) {
					try {
						if (t > 60000 || isReceive) {
							break;
						}
						if (osName.startsWith("Windows")) {
							pingCommand = "ping " + ipAddress + " -t ";
						} else {
							pingCommand = "ping " + ipAddress + " -c " + 1 + " -w "
									+ 1000;
						}

						Process p = r.exec(pingCommand);
						if (p != null) {
							in = new BufferedReader(new InputStreamReader(p
									.getInputStream()));
							String line = null;
							while ((line = in.readLine()) != null) {
								System.out.println(line);
								TextAreaOutputStream textOut = new TextAreaOutputStream(textArea);
								PrintStream outStream = new PrintStream(textOut, true);
								System.setOut(outStream);
//								if (line.startsWith("Reply from")
//										|| line.startsWith("来自")) {
//									isReceive = true;
//									break;
//								}
							}
						}

						t = t + 1000;
						Thread.sleep(1000);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});

		thread.start();
//		messageSender.send(new MessageCreator() {
//			public Message createMessage(Session session) throws JMSException {
//				ObjectMessage message = session.createObjectMessage();
//				message.setIntProperty(Constants.MESSAGETYPE,
//						MessageNoConstants.PINGSTART);
//				message.setObject(getIPList());
//				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
//				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
//				return message;
//			}
//		});
		
	}
	
	
	/**
	 * 得到所选择的IP地址列表
	 * @return
	 */
//	private ArrayList getIPList(){
//		int[] rows = table.getSelectedRows();
//		int[] modelRows = new int[rows.length];
//		for (int i = 0 ; i < rows.length; i++){
//			modelRows[i] = table.convertRowIndexToModel(rows[i]);
//		}
//		
//		ArrayList<PingResult> list = new ArrayList<PingResult>();
//		for (int k = 0 ; k < table.getRowCount(); k++){
//			PingResult pingResult = (PingResult)model.getValueAt(k, 3);
//			list.add(pingResult);
//		}
//		
//		return list;
//	}
//	
	@ViewAction(name=STOP, icon=ButtonConstants.STOP,desc="停止ping设备",role=Constants.MANAGERCODE)
	public void stop(){
//		for (int i = 0 ; i < fepCodeList.size() ; i++){
//			fepCode = String.valueOf(fepCodeList.get(i));
//			if (null == fepCode || "".equals(fepCode) || "null".equals(fepCode)){
//				continue;
//			}
//			messageSender.send(new MessageCreator() {
//				public Message createMessage(Session session) throws JMSException {
//					TextMessage message = session.createTextMessage();
//					message.setIntProperty(Constants.MESSAGETYPE,
//							MessageNoConstants.PINGEND);
//					message.setStringProperty(Constants.AIMFEP, fepCode);
//	
//					message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
//					message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
//					message.setIntProperty(Constants.DEVTYPE, 0);
//					return message;
//				}
//			});
//		}
//		fepCodeList.clear();
//		setComponentEnabled(true);
		thread.stop();
	}
//	
//	/**
//	 * 发送异步消息后，从后台接受异步消息的结果
//	 */
//	private void receive(){
//		messageProcessor = new MessageProcessorAdapter() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public void process(ObjectMessage message) {
//				try {
//					Object object = message.getObject();
//					String fepCode = ((PingResult)object).getFepCode();
//					if(!fepCodeList.contains(fepCode)){
//						fepCodeList.add(fepCode);
//					}
//					updateStatus(object);
//				} catch (JMSException e) {
//					LOG.error("DataSynchronizationView.MessageProcessor.message.getText() error", e);
//				}
//			}
//		};
//		messageDispatcher.addProcessor(MessageNoConstants.PINGRES, messageProcessor);
//		
//		messageFepOfflineProcessor = new MessageProcessorAdapter() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public void process(TextMessage message) {
//				try {
//					final String str = message.getText();
//					SwingUtilities.invokeLater(new Runnable(){
//						public void run(){
//							openMessageDialog(str);
//						}
//					});
//				} catch (JMSException e) {
//					LOG.error("DataSynchronizationView.MessageProcessor.message.getText() error", e);
//				}
//			}
//		};
//		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepOfflineProcessor);
//	}
//	
//	/**
//	 * 显示结果对话框
//	 */
//	private void openMessageDialog(String operate){
//		MessagePromptDialog messageDlg = new MessagePromptDialog(this,operate,imageRegistry,remoteServer);
//		messageDlg.setMessage(operate);
//		messageDlg.setVisible(true);
//	}
//	
//	@Override
//	public void dispose() {
//		setComponentEnabled(true);
//		messageDispatcher.removeProcessor(MessageNoConstants.PINGRES, messageProcessor);
//		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOfflineProcessor);
//	}
//	
//	private void setComponentEnabled(boolean enabled){
//		addBtn.setEnabled(enabled);
//		deleteBtn.setEnabled(enabled);
//		startBtn.setEnabled(enabled);		
//	}
//	
//	/**
//	 * 根据接收到的异步消息，更新视图
//	 * @param object
//	 * @throws JMSException
//	 */
//	private void updateStatus(Object object) throws JMSException{
//		PingResult pingResult = (PingResult)object;
//		String ip = pingResult.getIpValue();
//		
//		int count = table.getRowCount();
//		
//		for(int i = 0 ; i < count; i++){
//			PingResult pResult = (PingResult)model.getValueAt(i, 3);
//			
//			//ip地址
//			String ipValue = pResult.getIpValue();
//			
//			//时间
//			String date = "";
//			if (null != pResult.getTime()){
//				String pattern = "yyyy-MM-dd HH:mm:ss:SSS";
//				date = format.format(pingResult.getTime(),pattern);
//			}
//			
//			//结果
//			String result = reverseIntToString(pResult.getStatus());
//			
//			if (ipValue.equals(ip)){
////				if (ipValue.equals(ip) && date.equals("")){
//				//更新表格
//				model.removeRow(i);
//				break;
//			}
//		}
//		
//		model.addRow(pingResult);
//		table.setRowSelectionInterval(0, 0);
//	}
//	
//	
//	private String reverseIntToString(int value){
//		String str = "";
//		switch(value){
//			case 0:
//				str = "";
//				break;
//			case Constants.PINGIN:
//				str = "成功";
//				break;
//			case Constants.PINGOUT:
//				str = "失败";
//				break;
//			case 3:
//				str = "不在此前置机管辖范围内";
//				break;
//		}
//		
//		return str;
//	}
//	
//	private int reverseStringToInt(String str){
//		int value = 0;
//		if ("".equals("")){
//			value = 0;
//		}
//		else if ("成功".equalsIgnoreCase(str)){
//			value = Constants.PINGIN;
//		}
//		else if ("失败".equalsIgnoreCase(str)){
//			value = Constants.PINGOUT;
//		}
//		else if ("不在此前置机管辖范围内".equals(str)){
//			value = 3;
//		}
//		return value;
//	}
//	
//	public JButton getCloseButton(){
//		return null;
//	}
//	
//	@SuppressWarnings("unchecked")
//	class IpTableModel extends AbstractTableModel{
//		private List<List> dataList = new ArrayList<List>();
//		
//		private String[] columnName = null;
//
//		public List<List> getDataList() {
//			return dataList;
//		}
//
//		public void setDataList(List<List> dataList) {
//			if (null == dataList){
//				dataList = new ArrayList<List>();
//			}
//			else{
//				this.dataList = dataList;
//			}
//		}
//
//		public String[] getColumnName() {
//			return columnName;
//		}
//
//		public void setColumnName(String[] columnName) {
//			this.columnName = columnName;
//		}
//
//		@Override
//		public int getColumnCount() {
//			return columnName.length;
//		}
//
//		@Override
//		public String getColumnName(int columnIndex) {
//			// TODO Auto-generated method stub
//			return columnName[columnIndex];
//		}
//
//		@Override
//		public int getRowCount() {
//			return dataList.size();
//		}
//
//		@Override
//		public Object getValueAt(int rowIndex, int columnIndex) {
//			if (dataList.size() <= rowIndex){
//				return null;
//			}
//			
//			if (null == dataList.get(rowIndex)){
//				return null;
//			}
//			return dataList.get(rowIndex).get(columnIndex);
//		}
//
//		@Override
//		public boolean isCellEditable(int rowIndex, int columnIndex) {
//			return false;
//		}
//
//
//		@Override
//		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//			if (null == dataList.get(rowIndex)){
//				return;
//			}
//			dataList.get(rowIndex).set(columnIndex, aValue);
//		}
//		
//		public void addRow(Object object){
//			PingResult pingResult = (PingResult)object;
//			List rowList = new ArrayList();
//			rowList.add(0,pingResult.getIpValue());
//			if (null == pingResult.getTime()){
//				rowList.add(1,"");
//			}
//			else{
//				String date = "";
//				String pattern = "yyyy-MM-dd HH:mm:ss:SSS";
//				date = format.format(pingResult.getTime(),pattern);
//				rowList.add(1,date);
//			}
//			
//			rowList.add(2,reverseIntToString(pingResult.getStatus()));
//			
//			rowList.add(3,pingResult);
//			
//			this.dataList.add(0,rowList);
//			this.fireTableDataChanged();
//		}
//		
//		public void removeRow(int index){
//			this.dataList.remove(index);
//			this.fireTableDataChanged();
//		}
//		
//		
//		public void updateRow(Object object){
//			PingResult pingResult = (PingResult)object;
//			String ip = pingResult.getIpValue();
//			
//			for (int i = 0 ; i < dataList.size(); i++){
//				List rowList = dataList.get(i);
//				String ipValue = String.valueOf(rowList.get(0));
//				if (ipValue.equals(ipValue)){
//					
//					String date = "";
//					if (null != pingResult.getTime()){
//						String pattern = "yyyy-MM-dd HH:mm:ss:SSS";
//						date = format.format(pingResult.getTime(),pattern);
//					}
//					rowList.add(1,date);
//					
//					rowList.add(2,reverseIntToString(pingResult.getStatus()));
//					dataList.set(i, rowList);
//					
//					return;
//				}
//			}
//		}
//		
//		public void moveRow(Object object,int row){
//			PingResult pingResult = (PingResult)object;
//			String ip = pingResult.getIpValue();
//			
//			for (int i = 0 ; i < dataList.size(); i++){
//				List rowList = dataList.get(i);
//				String ipValue = String.valueOf(rowList.get(0));
//				if (ipValue.equals(ipValue)){
//					int k = i; 
//					while(k > row){
//						dataList.set(k, dataList.get(k-1));
//						
//						k--;
//					}
//					
//					dataList.set(row, rowList);
//					
//					return;
//				}
//			}
//		}
//	}
//	
//	
//	
////	JFormattedTextField ipField = new JFormattedTextField(new IPAddressFormat());
//	// Reference:
//	// http://72.5.124.102/thread.jspa?threadID=571662&messageID=2830342
//	public class IPAddressFormat extends NumberFormat {
//	    
//	    private static final long LOCALHOST = 2130706433l;
//	    private static final int LOCALHOST_PARSE_POSITION = 9;
//	 
//	    /**
//	     * This method isn't used and returns null.
//	     * @param d
//	     * @param buffer
//	     * @param fp
//	     * @return
//	     * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
//	     */
//	    @Override
//		public StringBuffer format(double d, StringBuffer buffer, FieldPosition fp) {
//	        return null;
//	    }
//	 
//	    /**
//	     * Formats the long in the appropriate style for an IP address.
//	     * @param l
//	     * @param buffer
//	     * @param fp
//	     * @return
//	     * @see java.text.NumberFormat#format(long, java.lang.StringBuffer, java.text.FieldPosition)
//	     */
//	    @Override
//		public StringBuffer format(long l, StringBuffer buffer, FieldPosition fp) {
//	        StringBuffer buf = new StringBuffer();
//	        buf.append(new Long((l >> 24) & 0x000000ff).toString());
//	        buf.append(".");
//	        buf.append(new Long((l >> 16) & 0x000000ff).toString());
//	        buf.append(".");
//	        buf.append(new Long((l >> 8) & 0x000000ff).toString());
//	        buf.append(".");
//	        buf.append(new Long(l & 0x000000ff).toString());
//	        return buf;
//	    } //end format
//	 
//	    /**
//	     * Parses the text into a Long that represents the IP address.
//	     * @param text The text contained within the formatted text field.
//	     * @param pp The current ParsePosition.
//	     * @return A Long that represents an IP address. If the parsing fails the localhost IP is returned.
//	     * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
//	     */
//	    @Override
//		public Number parse(String text, ParsePosition pp) {
//	        pp.setIndex(text.length());
//	        int c = 24;
//	        long l = 0;
//	        StringTokenizer tokens = new StringTokenizer(text, ".");
//	        try {
//		        while (tokens.hasMoreTokens() && c >= 0) {
//		            int value = Integer.parseInt(tokens.nextToken());
//		            if (value > 255) {
//						value = 255;
//					} else if (value < 0) {
//						value = 0;
//					}
//		            l |= (value << c);
//		            c -= 8;
//		        }
//	        } catch (Exception e) {
//	            pp.setIndex(LOCALHOST_PARSE_POSITION);
//	            return new Long(LOCALHOST);
//	        }
//	        return new Long(l);
//	    }	 
//	}
}