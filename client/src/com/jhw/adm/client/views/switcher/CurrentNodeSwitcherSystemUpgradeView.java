package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.CLOSE;
import static com.jhw.adm.client.core.ActionConstants.SEND;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

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
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SwitchUpgradeStrategy;
import com.jhw.adm.client.views.SpringUtilities;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(CurrentNodeSwitcherSystemUpgradeView.ID)
@Scope(Scopes.DESKTOP)
public class CurrentNodeSwitcherSystemUpgradeView extends ViewPart {

	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	private MessageSender messageSender;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private JButton sendButton = null;
	private ButtonFactory  buttonFactory;
	
	private JTextField selectedFileField = null;
	private JTextField fileSizeField = null;
	private JTextField lastModifiedField = null;
	private JLabel ipLabel = null;
	private String upgradeFileName = "";
	private File selectedFile = null;
	
	private JCheckBox restartChkBox ;
	
	@PostConstruct
	protected void initialize() {
		
		messageSender = remoteServer.getMessageSender();
		buttonFactory = actionManager.getButtonFactory(this);
		
		setTitle("交换机软件升级");
		setViewSize(450, 380);
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
		
//		switchTopoNodeEntity = (SwitchTopoNodeEntity) adapterManager
//				.getAdapter(equipmentModel.getLastSelected(),
//						SwitchTopoNodeEntity.class);
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if(null != switchNodeEntity){
			if(null != switchNodeEntity.getBaseConfig()){
				ipLabel.setText("IP:" + switchNodeEntity.getBaseConfig().getIpValue());
			}
		}
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchSystemUpgradeListener);
		
	}
	
	private void createContents(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		JPanel theContainer = new JPanel(new GridLayout(2, 1));
		
		JPanel equipmentPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		equipmentPanel.setBorder(BorderFactory.createTitledBorder("当前设备"));
		theContainer.add(equipmentPanel);
		
		JPanel systemUpdateContainer = new JPanel(new BorderLayout(5, 5));
		theContainer.add(systemUpdateContainer);
		
		ipLabel = new JLabel("");
		equipmentPanel.add(ipLabel);
		
		systemUpdateContainer.setBorder(BorderFactory.createTitledBorder("升级文件"));
		JButton fileChooserButton = new JButton("选择文件");
		selectedFileField = new JTextField();
		systemUpdateContainer.add(selectedFileField, BorderLayout.CENTER);
		systemUpdateContainer.add(fileChooserButton, BorderLayout.LINE_END);
		//Amend 2010.06.08
		selectedFileField.setEditable(false);
		selectedFileField.setBackground(Color.WHITE);
		
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
		
		parent.add(theContainer, BorderLayout.PAGE_START);
		parent.add(infoWrapper, BorderLayout.CENTER);
		parent.add(restartPnl, BorderLayout.SOUTH);

		fileChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooserAction();
			}
		});
	}
	
	/** size 如果 小于1024 * 1024,以KB单位返回,反则以MB单位返回 */
	private String fileSize(long fileLength) {
		if (fileLength == 0) {
			return "";
		}
		DecimalFormat df = new DecimalFormat("###.##");
		float f;
		if (fileLength < 1024 * 1024) {
			f = ((float) fileLength / (float) 1024);
			return (df.format(new Float(f).doubleValue()) + "KB");
		} else {
			f = ((float) fileLength / (float) (1024 * 1024));
			return (df.format(new Float(f).doubleValue()) + "MB");
		}
	}

	private String fileLastModified(long fileLastModified) {
		Date d = new Date(fileLastModified);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(d);
	}
	
	private void fileChooserAction() {
		final JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser
				.showOpenDialog(CurrentNodeSwitcherSystemUpgradeView.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			String filePath = selectedFile.getAbsolutePath();
			File file = new File(filePath);
			if (file.exists()) {
				upgradeFileName = filePath;
				selectedFileField.setText(upgradeFileName);
				long fileSize = file.length();
				fileSizeField.setText(fileSize(fileSize));
				long fileLastModified = file.lastModified();
				lastModifiedField.setText(fileLastModified(fileLastModified));
			} else {
				JOptionPane.showMessageDialog(this, "文件不存在，请重新选择", "提示",
						JOptionPane.NO_OPTION);
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
	@ViewAction(name=SEND,icon=ButtonConstants.SEND,desc="发送交换机升级文件",role=Constants.MANAGERCODE)
	public void send(){
		if(null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		File file = new File(selectedFileField.getText().trim());
		if(!file.exists()){
			JOptionPane.showMessageDialog(this, "文件不存在，请重新选择", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this, "确定要升级", "提示", JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION){
			return;
		}
		
		if(!restartChkBox.isSelected()){
			isStart = false;
		}
		
		Task task = new UpgradeRequestTask(file, isStart, switchNodeEntity.getBaseConfig().getIpValue());
		showMessageDialog(task);
	}
	
	private class UpgradeRequestTask implements Task{

		private File file;
		private boolean isStart = false;
		private String ipAddress = "";
		public UpgradeRequestTask(File file, boolean isStart, String ipAddress){
			this.file = file;
			this.isStart = isStart;
			this.ipAddress = ipAddress;
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
	
	private PropertyChangeListener switchSystemUpgradeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				switchNodeEntity = (SwitchNodeEntity) adapterManager
						.getAdapter(equipmentModel.getLastSelected(),
								SwitchNodeEntity.class);
				if(null == switchNodeEntity){
					return;
				}
				String ipAddress = switchNodeEntity.getBaseConfig().getIpValue();
				ipLabel.setText("IP:" + ipAddress);
			}
		}
	};
	
	private JButton createCloseButton() {
		JButton closeButton = new JButton(localizationManager.getString(CLOSE), 
				imageRegistry.getImageIcon(ButtonConstants.CLOSE));
		return closeButton;
	}
	
	@Override
	public void dispose(){
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchSystemUpgradeListener);
	}
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "currentNodeSwitcherSystemUpgradeView";
	private static final Logger LOG = LoggerFactory.getLogger(CurrentNodeSwitcherSystemUpgradeView.class);
}