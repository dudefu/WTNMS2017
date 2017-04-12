package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.CLOSE;
import static com.jhw.adm.client.core.ActionConstants.SEND;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DateFormatter;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.swing.CommonTable;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SwitchUpgradeStrategy;
import com.jhw.adm.client.views.SpringUtilities;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(SwitcherSystemUpgradeView.ID)
@Scope(Scopes.DESKTOP)
public class SwitcherSystemUpgradeView extends ViewPart {

	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DateFormatter.ID)
	private DateFormatter format;
	
	private MessageSender messageSender;
	
	private JButton sendButton = null;
	private ButtonFactory  buttonFactory;
	
	private JTextField selectedFileField = null;
	private JTextField fileSizeField = null;
	private JTextField lastModifiedField = null;
	private String upgradeFileName = "";
	private File selectedFile = null;
	
	private static final String[] COLUMN_NAME = { "交换机IP","交换机类型","软件版本","版本时间"};
	private SwitchTableModel tableModel;
	private CommonTable table = new CommonTable();
	private JScrollPane scrollPnl = new JScrollPane();
	
	private JCheckBox restartChkBox ;
	
	@PostConstruct
	protected void initialize() {
		
		messageSender = remoteServer.getMessageSender();
		buttonFactory = actionManager.getButtonFactory(this);
		
		setTitle("交换机软件升级");
		setViewSize(600, 480);
		setLayout(new BorderLayout());
		
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		sendButton = buttonFactory.createButton(SEND);
		JButton closeButton = createCloseButton();
		toolPanel.add(sendButton);
		toolPanel.add(closeButton);
		this.setCloseButton(closeButton);
		add(toolPanel, BorderLayout.PAGE_END);
		
		JPanel content = new JPanel();
		createContents(content);		
		add(content, BorderLayout.CENTER);
		
		setValue();
	}
	
	private void createContents(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		//start theContainer
		JPanel theContainer = new JPanel(new GridLayout(1, 1));
		JPanel systemUpdateContainer = new JPanel(new BorderLayout(5, 5));
		theContainer.add(systemUpdateContainer);
		
		systemUpdateContainer.setBorder(BorderFactory.createTitledBorder("升级文件"));
		JButton fileChooserButton = new JButton("选择文件");
		selectedFileField = new JTextField();
		systemUpdateContainer.add(selectedFileField, BorderLayout.CENTER);
		systemUpdateContainer.add(fileChooserButton, BorderLayout.LINE_END);

		selectedFileField.setEditable(false);
		selectedFileField.setBackground(Color.WHITE);
		//end theContainer
		
		//start CenterPanel--tableList
		tableModel = new SwitchTableModel();
		tableModel.setColumnName(COLUMN_NAME);
		table.setModel(tableModel);
		scrollPnl.getViewport().add(table);
		scrollPnl.setBorder(BorderFactory.createTitledBorder("设备信息"));
		//end CenterPanel--tableList
		
		//start southPanel
		JPanel infoWrapper = new JPanel(new BorderLayout());
		JPanel infoContainerWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel infoContainer = new JPanel(new SpringLayout());
		infoContainerWrapper.add(infoContainer);
		infoWrapper.setBorder(BorderFactory.createTitledBorder("文件信息"));
		
		fileSizeField = new JTextField("", 30);
		lastModifiedField = new JTextField("", 30);

		fileSizeField.setEditable(false);
		lastModifiedField.setEditable(false);
		fileSizeField.setBackground(Color.WHITE);
		lastModifiedField.setBackground(Color.WHITE);
		
		infoContainer.add(new JLabel("大小"));
		infoContainer.add(fileSizeField);
		infoContainer.add(new JLabel("修改日期"));
		infoContainer.add(lastModifiedField);
		
		SpringUtilities.makeCompactGrid(infoContainer, 2, 2, 6, 6, 30, 6);
		infoWrapper.add(infoContainerWrapper, BorderLayout.PAGE_START);
		
		JPanel restartPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		restartChkBox = new JCheckBox("升级后是否重启");
		restartPnl.add(restartChkBox);
		restartChkBox.setSelected(true);
		restartChkBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(restartChkBox.isSelected()){
					restartChkBox.setSelected(true);
					isStart = true;
				}else{
					restartChkBox.setSelected(false);
					isStart = false;
				}
			}
			
		});
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(infoWrapper, BorderLayout.CENTER);
		southPanel.add(restartPnl, BorderLayout.SOUTH);
		//end southPanel
		
		parent.add(theContainer, BorderLayout.PAGE_START);
		parent.add(scrollPnl, BorderLayout.CENTER);
		parent.add(southPanel, BorderLayout.SOUTH);

		fileChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooserAction();
			}
		});
	}
	
	/**size 如果 小于1024 * 1024,以KB单位返回,反则以MB单位返回*/
	private String fileSize(long fileLength)
	{
		if(fileLength == 0)
		{
			return "";
		}
		DecimalFormat df = new DecimalFormat("###.##");
		   float f;
		   if (fileLength < 1024 * 1024) {
		    f = ((float) fileLength / (float) 1024);
		    return (df.format(new Float(f).doubleValue())+"KB");
		   } else {
		    f = ((float) fileLength / (float) (1024 * 1024));
		    return (df.format(new Float(f).doubleValue())+"MB");
		   }
	}
	private String fileLastModified(long fileLastModified)
	{
		Date d = new Date(fileLastModified);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(d);
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(){
		List<SwitchNodeEntity> list = new ArrayList<SwitchNodeEntity>();
		List<SwitchTopoNodeEntity> topoList = new ArrayList<SwitchTopoNodeEntity>();
//		Iterator iterator = equipmentModel.getDiagram().getNodes().iterator();
//		while(iterator.hasNext()){
//			Object object = iterator.next();
//			if (object instanceof SwitchTopoNodeEntity){
//				topoList.add((SwitchTopoNodeEntity)object);
//			}
//		}
		List<SwitchTopoNodeEntity> switchTopoNodeEntities = (List<SwitchTopoNodeEntity>) remoteServer
				.getService().findAll(SwitchTopoNodeEntity.class);
		for(SwitchTopoNodeEntity switchTopoNodeEntity : switchTopoNodeEntities){
			LOG.info(switchTopoNodeEntity.getIpValue());
			SwitchNodeEntity switchNodeEntity = remoteServer.getService()
					.getSwitchByIp(switchTopoNodeEntity.getIpValue());
			list.add(switchNodeEntity);
		}
		setTableValue(list);
	}

	private void setTableValue(List<SwitchNodeEntity> list){
		tableModel.setDataList(list);
		tableModel.fireTableDataChanged();
	}
	
	private void fileChooserAction()
	{
		final JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser.showOpenDialog(SwitcherSystemUpgradeView.this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			selectedFile = fileChooser.getSelectedFile();
			String filePath = selectedFile.getAbsolutePath();
			File file = new File(filePath);
			if(file.exists())
			{
				upgradeFileName = filePath;
				selectedFileField.setText(upgradeFileName);
				long fileSize = file.length();
				fileSizeField.setText(fileSize(fileSize));
				long fileLastModified = file.lastModified();
				lastModifiedField.setText(fileLastModified(fileLastModified));
			}else
			{
				JOptionPane.showMessageDialog(this, "文件不存在，请重新选择", "提示", JOptionPane.NO_OPTION);
				selectedFileField.setText("");
				fileSizeField.setText("");
				lastModifiedField.setText("");
				return;
			}
		}
	}
	
	private InputStream is = null;
	private byte[] byteBuffer;
	private boolean isStart = true;
	private String ipAddress = "";
	private ArrayList<SwitchNodeEntity> list ;
	@ViewAction(name=SEND,icon=ButtonConstants.SEND,desc="发送交换机升级文件",role=Constants.MANAGERCODE)
	public void send(){
		if(table.getSelectedColumnCount() < 1){
			JOptionPane.showMessageDialog(this, "请选择需要升级的设备", "提示", JOptionPane.NO_OPTION);
			return;
		}

		File file = new File(selectedFileField.getText().trim());
		if(!file.exists()){
			JOptionPane.showMessageDialog(this, "文件不存在，请重新选择", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "确定要升级", "提示", JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION){
			return;
		}
		
		list = tableModel.getSelectedRows(table.getSelectedRows());
		for(int i = 0; i < list.size() ;i++){
			if(i == 0){
				ipAddress = list.get(i).getBaseConfig().getIpValue();
			}else{
				ipAddress = ipAddress  + "," + list.get(i).getBaseConfig().getIpValue();				
			}
		}
		
//		if(!restartChkBox.isSelected()){
//			isStart = false;
//		}
		
		Task task = new UpgradeRequestTask(file, isStart, ipAddress,"192.168.1.207");
		showMessageDialog(task);
	}
	
	private class UpgradeRequestTask implements Task{

		private File file;
		private boolean isStart = false;
		private String ipAddress = "";
		private String localIp = "";
		public UpgradeRequestTask(File file, boolean isStart, String ipAddress,String localIp){
			this.file = file;
			this.isStart = isStart;
			this.ipAddress = ipAddress;
			this.localIp = localIp;
		}
		
		@Override
		public void run() {
			try {
				is = new FileInputStream(file);
				int available = is.available();
				byteBuffer = new byte[available];
				while(is.read(byteBuffer) != -1){}
				
				messageSender.send(new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						StreamMessage message = session.createStreamMessage();
						message.setIntProperty(Constants.MESSAGETYPE, MessageNoConstants.SWITCHERUPGRATE);
						message.writeBytes(byteBuffer);
//					message.setObjectProperty(Constants.PARAMCLASS, list);
						message.setBooleanProperty(Constants.RESTART, isStart);
						message.setStringProperty(Constants.MESSAGETO, ipAddress);
						message.setStringProperty(Constants.LOCALIP, localIp);
						message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
						message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
						return message;
					}
				});
			} catch (FileNotFoundException e) {
				strategy.showErrorMessage("升级出现异常");
				LOG.error("Send file error" , e);
			} catch (IOException e) {
				strategy.showErrorMessage("升级出现异常");
				LOG.error("Send file error" , e);
			}finally{
				if(null != is){
					try {
						is.close();
					} catch (IOException e) {
						LOG.error("Close FileInputStream error", e);
					}
				}
			}
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SwitchUpgradeStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SwitchUpgradeStrategy("升级", progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,"升级",true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task);
				}
			});
		}
	}
	
	private JButton createCloseButton() {
		JButton closeButton = new JButton(localizationManager.getString(CLOSE), 
				imageRegistry.getImageIcon(ButtonConstants.CLOSE));
		return closeButton;
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "switcherSystemUpgradeView";
	private static final Logger LOG = LoggerFactory.getLogger(SwitcherSystemUpgradeView.class);
	
	private class SwitchTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private String[] columnName = null;
		
		private List<SwitchNodeEntity> dataList = null;
		
		public SwitchTableModel(){
			super();
		}

		public void setColumnName(String[] columnName) {
			this.columnName = columnName;
		}

		public void setDataList(List<SwitchNodeEntity> dataList) {
			if (null == dataList){
				this.dataList = new ArrayList<SwitchNodeEntity>();
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

	
	    public Object getValueAt(int row, int col){
	    	Object value = null;
			
			if(row < dataList.size()){
				SwitchNodeEntity switchNodeEntity = dataList.get(row);
				switch(col)
				{
					case 0:
						if (null != switchNodeEntity){
							if(null != switchNodeEntity.getBaseConfig()){
								if(null == switchNodeEntity.getBaseConfig().getIpValue()){
									value = "";
								}else{
									value = switchNodeEntity.getBaseConfig().getIpValue();
								}
							}else{
								value = "";
							}
						}
						else{
							value = "";
						}
						
						break;
					case 1:
						if (null != switchNodeEntity){
							value = switchNodeEntity.getType();
						}
						else{
							value = "";
						}
						break;
					case 2:
						if(null != switchNodeEntity){
							if(null != switchNodeEntity.getBaseInfo()){
								if(null == switchNodeEntity.getBaseInfo().getCosVersion()){
									value = "";
								}else{
									value = switchNodeEntity.getBaseInfo().getCosVersion();
								}
							}else{
								value = "";
							}
						}
						else{
							value = "";
						}
						
						break;
					case 3:
						if (null != switchNodeEntity){
							if(null != switchNodeEntity.getBaseInfo()){
								if(null == switchNodeEntity.getBaseInfo().getCosVersionTime()){
									value = "";
								}else {
									value = switchNodeEntity.getBaseInfo().getCosVersionTime();
								}
							}else{
								value = "";
							}
						}
						else{
							value = "";
						}
						break;
					default:
						break;
				}
			}
			return value;
	    }
	    
	    @Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	    	if (aValue instanceof SwitchNodeEntity){
	    		dataList.set(rowIndex, (SwitchNodeEntity)aValue);
	    	}
	    }
	    
	    public ArrayList<SwitchNodeEntity> getSelectedRows(int[] selectedRows){
	    	ArrayList<SwitchNodeEntity> switchNodeEntitys = new ArrayList<SwitchNodeEntity>();
	    	for(int i = 0;i < selectedRows.length ; i++){
	    		switchNodeEntitys.add(this.dataList.get(selectedRows[i]));
	    	}
	    	return switchNodeEntitys;
	    }
	}
}