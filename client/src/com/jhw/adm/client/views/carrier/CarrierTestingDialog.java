package com.jhw.adm.client.views.carrier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.MessageCreator;

import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ApplicationConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.ImagePanel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 载波机测试对话框
 */
public class CarrierTestingDialog extends JDialog {
	
	public CarrierTestingDialog() {
		super(ClientUtils.getRootFrame());
		ImageRegistry imageRegistry = ClientUtils.getSpringBean(ImageRegistry.class, ImageRegistry.ID);
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		titleLabel = new JLabel();
		topPnl.add(titleLabel);
		topPnl.setOpaque(false);
				
		messageLabel = new JLabel();
		messageLabel.setText("正在请求...");
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		JPanel centerPnl = new JPanel();

		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(messageLabel, BorderLayout.CENTER);
		
		JButton button = new JButton("关闭");
		JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.setOpaque(false);
		bottomPnl.add(button);
		
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		Image image = imageRegistry.getImage(ApplicationConstants.MESSAGE_PROMPT);
		ImagePanel mainPnl = new ImagePanel(image);
		mainPnl.setLayout(new BorderLayout());
		mainPnl.add(topPnl,BorderLayout.NORTH);
		mainPnl.add(centerPnl,BorderLayout.CENTER);
		mainPnl.add(bottomPnl,BorderLayout.SOUTH);
		mainPnl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		mainPnl.setOpaque(false);
		centerPnl.setOpaque(false);
		
		this.getContentPane().add(mainPnl);
		this.setModal(true);
		this.setUndecorated(true);
		this.setSize(300, 100);
		
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.class, MessageDispatcher.ID);
		equipmentModel = ClientUtils.getSpringBean(EquipmentModel.class, EquipmentModel.ID);
		adapterManager = ClientUtils.getSpringBean(AdapterManager.class, DesktopAdapterManager.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.class, RemoteServer.ID);
		clientModel = ClientUtils.getSpringBean(ClientModel.class, ClientModel.ID);
		messageSender = remoteServer.getMessageSender();
		selectedCarrier = 
			(CarrierEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), CarrierEntity.class);
	}
	
	public void run() {
		addMessageListener();
		titleLabel.setText(String.format("载波机[%s]", selectedCarrier.getCarrierCode()));		
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				open();
			}
		});
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				sendMessage();
			}
		});
	}
	
	private void sendMessage() {
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.CARRIERTEST);
				message.setObject(selectedCarrier);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				return message;
			}
		});
	}
	
	private void showMessage(final String text) {
		if (SwingUtilities.isEventDispatchThread()) {
			messageLabel.setText(text);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessage(text);
				}
			});
		}
	}
	
	private void addMessageListener() {
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, fepOfflineMessage);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERMONITORREP, carrierMessageProcessor);
	}
	
	private void removeMessageListener() {
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, fepOfflineMessage);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERMONITORREP, carrierMessageProcessor);
	}
	
	private void close() {
		removeMessageListener();
		CarrierTestingDialog.this.dispose();
	}
	
	private final MessageProcessorAdapter fepOfflineMessage = new MessageProcessorAdapter() {
		@Override
		public void process(TextMessage message) {
			try {
				showMessage(message.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	};
	
	private final MessageProcessorAdapter carrierMessageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(final TextMessage message) {
			try {
				String carrierCode = message.getStringProperty(Constants.MESSAGEFROM);
				String result = message.getStringProperty(Constants.MESSAGERES);
				boolean status = "S".equals(result);
				if (status) {
					showMessage(String.format("载波机[%s]正常", carrierCode));
				} else {
					showMessage(String.format("载波机[%s]掉线", carrierCode));
				}
			} catch (JMSException e) {
				LOG.error("message.getStringProperty() error", e);
			}
		}
	};
	
	public void open() {
		if (SwingUtilities.isEventDispatchThread()) {
			setVisible(true);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					open();
				}
			});
		}
	}

	private final CarrierEntity selectedCarrier;
	private final JLabel titleLabel;
	private final JLabel messageLabel;
	private final ClientModel clientModel;
	private final AdapterManager adapterManager;
	private final EquipmentModel equipmentModel;
	private final MessageSender messageSender;
	private final RemoteServer remoteServer;
	private final MessageDispatcher messageDispatcher;
	private static final Logger LOG = LoggerFactory.getLogger(CarrierTestingDialog.class);
	private static final long serialVersionUID = 1335811745157709166L;
}