package com.jhw.adm.client.views.carrier;

import static com.jhw.adm.client.core.ActionConstants.SEND;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import java.text.NumberFormat;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.MessagePromptDialog;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.views.SpringUtilities;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(CarrierSystemUpgradeView.ID)
@Scope(Scopes.DESKTOP)
public class CarrierSystemUpgradeView extends ViewPart {
	public static final String ID = "carrierSystemUpgradeView";
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(CarrierSystemUpgradeView.class); 
	
	private File selectedFile = null;
		
	private final JTextField selectedFileField = new JTextField();
	private final JTextField fileSizeField = new JTextField("", 10);
	private final JTextField fileCRCField = new JTextField("", 10);
	private final JTextField packetCountField = new JTextField("", 10);
	
	private JFormattedTextField versionField;
	private NumberField intervalField;
	
	private JButton sendBtn = null;
	private JButton closeBtn = null;
	
	private CarrierEntity carrierEntity = null;
	private CarrierTopNodeEntity carrierTopNodeEntity;
	
	public static final int PACKET_LEN = 100;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	private MessageSender messageSender;
	private MessageProcessorAdapter messageProcessor;
	private MessagePromptDialog messagePromptDialog;
	
	@PostConstruct
	protected void initialize() {
		
		messageSender = remoteServer.getMessageSender();
		
		setTitle("载波机系统升级");
		setViewSize(450, 400);
		setLayout(new BorderLayout());
		
		JPanel content = new JPanel();
		createContents(content);		
		add(content, BorderLayout.CENTER);
		
		carrierEntity =(CarrierEntity)adapterManager.getAdapter(
				equipmentModel.getLastSelected(), CarrierEntity.class);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, carrierChangeListener);
		
		messageProcessor = new MessageProcessorAdapter() {
			@Override
			public void process(TextMessage message) {
				try {
					messagePromptDialog.setMessage(message.getText());
				} catch (JMSException e) {
					LOG.error("Initialize error", e);
				}
			}
		};
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERSYSTEMUPGRADEREP, messageProcessor);
	}
	
	private void createContents(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		JPanel systemUpdateContainer = new JPanel(new BorderLayout(5, 5));
		systemUpdateContainer.setBorder(BorderFactory.createTitledBorder("升级文件"));
		JButton fileChooserButton = new JButton("选择文件");
		selectedFileField.setEditable(false);
		selectedFileField.setBackground(Color.WHITE);
		systemUpdateContainer.add(selectedFileField, BorderLayout.CENTER);
		systemUpdateContainer.add(fileChooserButton, BorderLayout.LINE_END);
		
		JPanel infoWrapper = new JPanel(new BorderLayout());
		JPanel infoContainerWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel infoContainer = new JPanel(new GridBagLayout());
		infoContainerWrapper.add(infoContainer);
		infoWrapper.setBorder(BorderFactory.createTitledBorder("文件信息"));

		fileSizeField.setEnabled(false);
		fileCRCField.setEnabled(false);
		packetCountField.setEnabled(false);
		
		
		infoContainer.add(new JLabel("大小"),new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,20,0),0,0));
		infoContainer.add(fileSizeField,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,20,0),0,0));
		infoContainer.add(new JLabel("CRC"),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,20,0),0,0));
		infoContainer.add(fileCRCField,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,20,0),0,0));
		infoContainer.add(new JLabel("包数"),new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,20,20,0),0,0));
		infoContainer.add(packetCountField,new GridBagConstraints(5,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,20,0),0,0));

		
		NumberFormat integerFormat = NumberFormat.getNumberInstance();
		integerFormat.setMinimumFractionDigits(0);
		integerFormat.setParseIntegerOnly(true);
		integerFormat.setGroupingUsed(false);
		
		versionField = new JFormattedTextField(integerFormat);
		versionField.setColumns(4);
		versionField.setDocument(new TextFieldPlainDocument(versionField));
		
		intervalField = new NumberField();
		intervalField.setColumns(4);
		
		infoContainer.add(new JLabel("版本"),new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,15,0),0,0));
		infoContainer.add(versionField,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,15,0),0,0));
		infoContainer.add(new JLabel("发送间隔"),new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,20,15,0),0,0));
		infoContainer.add(intervalField,new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,15,0),0,0));
		
		
		SpringUtilities.makeCompactGrid(infoContainer, 1, 11, 6, 6, 6, 6);

		infoWrapper.add(infoContainerWrapper, BorderLayout.PAGE_START);
		
		JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		ButtonFactory buttonFactory = actionManager.getButtonFactory(this); 
		sendBtn = buttonFactory.createButton(SEND);
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
		bottomPnl.add(sendBtn);
		bottomPnl.add(closeBtn);
		
		parent.add(systemUpdateContainer, BorderLayout.PAGE_START);
		parent.add(infoWrapper, BorderLayout.CENTER);
		parent.add(bottomPnl, BorderLayout.SOUTH);

		fileChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooserAction();
			}
		});
	}
	
	private void fileChooserAction(){
		final JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser.showOpenDialog(CarrierSystemUpgradeView.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			//被选择的文件
			selectedFile = fileChooser.getSelectedFile();
			//文件路径
			String path = selectedFile.getAbsolutePath();

			File file = new File(path);
			if(file.exists())
			{
				selectedFileField.setText(path);
				analyse(path);
			}else
			{
				JOptionPane.showMessageDialog(this, "文件不存在，请重新选择", "提示", JOptionPane.NO_OPTION);
				selectedFileField.setText("");
				return;
			}
		}
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
	private void analyse(String fileName) {
		//得到文件的CRC校验码
		InputStream inputStream = null;
		CRC32 crc32 = new CRC32();
		try {
			CheckedInputStream checkedInputStream = new CheckedInputStream(new FileInputStream(fileName), crc32);
			while (checkedInputStream.read() != -1) {}
			checkedInputStream.close();
			long fileCRC32 = crc32.getValue();
			fileCRCField.setText(Long.toHexString(fileCRC32).toUpperCase());
		} catch (FileNotFoundException e) {
			LOG.error("Analyse file error", e);
		} catch (IOException e) {
			LOG.error("Analyse file error", e);
		}

		//设置文件大小和包数
		try {
			inputStream = new FileInputStream(fileName);
			int available = inputStream.available();
//			fileSizeField.setText(Integer.toString(available));
			fileSizeField.setText(fileSize(selectedFile.length()));
			fileBuffer = new byte[available];
			while (inputStream.read(fileBuffer) != -1) {}
			int packetCount = available / PACKET_LEN;
			if ((available % PACKET_LEN) > 0) {
				packetCount += 1;
			}
			packetCountField.setText(Integer.toString(packetCount));
			
		} catch (FileNotFoundException e) {
			LOG.error("Analyse file error", e);
		} catch (IOException e) {
			LOG.error("Analyse file error", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				LOG.error("Close FileInputStream error", e);
			}
		}
	}
	private byte[] fileBuffer;
	@ViewAction(name=SEND, icon=ButtonConstants.SEND, desc="发送载波机升级文件",role=Constants.MANAGERCODE)
	public void send(){
		String message = "";
		if (null == carrierEntity){
			message = "请选择要升级的载波机";
			JOptionPane.showMessageDialog(this, message,"提示",JOptionPane.NO_OPTION);
			return;
		}
		
		String path = selectedFileField.getText();
		selectedFile = new File(path);
		if(!this.selectedFile.exists()){
			message = "请选择升级文件";
			JOptionPane.showMessageDialog(this, message,"提示",JOptionPane.NO_OPTION);
			return;
		}
		analyse(path);
		
		messageSender.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				StreamMessage message = session.createStreamMessage();
				message.setIntProperty(Constants.MESSAGETYPE, MessageNoConstants.CARRIERSYSTEMUPGRADE);
//				message.setIntProperty(Constants, Integer.parseInt(intervalField.getText()));
//				message.setStringProperty(Constants, versionField.getText());
				message.writeBytes(fileBuffer);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				return message;
			}
		});
		
	}
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, carrierChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERSYSTEMUPGRADEREP, messageProcessor);
	}
	
	private final PropertyChangeListener carrierChangeListener = new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof CarrierTopNodeEntity){
				carrierEntity =(CarrierEntity)adapterManager.getAdapter(
						equipmentModel.getLastSelected(), CarrierEntity.class);
			}
			else{
				carrierEntity = null;
			}
		}
	};
}