package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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

import org.apache.commons.lang.StringUtils;
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
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PingResult;

/**
 * 单个设备PING对话框
 */
public class PingDialog extends JDialog {
	
	public PingDialog() {
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

		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		equipmentModel = ClientUtils.getSpringBean(EquipmentModel.ID);
		adapterManager = ClientUtils.getSpringBean(DesktopAdapterManager.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
		clientModel = ClientUtils.getSpringBean(ClientModel.ID);
		messageSender = remoteServer.getMessageSender();
	}
	
	public void run() {
		addMessageListener();		
		titleLabel.setText(getTargetAddress());		
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
			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.PINGSTART);
				message.setObject(getAddressList());
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				return message;
			}
		});
	}
	
	public ArrayList<PingResult> getAddressList() {
		ArrayList<PingResult> list = new ArrayList<PingResult>();
		
		PingResult pingResult = new PingResult();
		pingResult.setIpValue(getTargetAddress());
		pingResult.setStatus(0);
		
		list.add(pingResult);
		
		return list;
	}
	
	private String getTargetAddress() {
		String targetAddress = StringUtils.EMPTY;
		Object lastSelected = equipmentModel.getLastSelected();

		if (lastSelected instanceof SwitchTopoNodeEntity) {
			SwitchTopoNodeEntity selectedSwitcher = (SwitchTopoNodeEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchTopoNodeEntity.class);
			targetAddress = selectedSwitcher.getIpValue();
		} else if (lastSelected instanceof EponTopoEntity) {
			EponTopoEntity selectedOlt = (EponTopoEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), EponTopoEntity.class);
			targetAddress = selectedOlt.getIpValue();
		} else if (lastSelected instanceof SwitchTopoNodeLevel3) {
			SwitchTopoNodeLevel3 selectedLayer3 = (SwitchTopoNodeLevel3)lastSelected;
			targetAddress = selectedLayer3.getIpValue();
		}
		
		return targetAddress;
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
		messageDispatcher.addProcessor(MessageNoConstants.PINGRES, pingResultProcessor);
	}
	
	private void removeMessageListener() {
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, fepOfflineMessage);
		messageDispatcher.removeProcessor(MessageNoConstants.PINGRES, pingResultProcessor);
	}
	
	private void close() {
		removeMessageListener();
		PingDialog.this.dispose();
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
	
	private final MessageProcessorAdapter pingResultProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(ObjectMessage message) {
			try {
				Object messageObject = message.getObject();
				if (messageObject instanceof PingResult) {
					PingResult pingResult = (PingResult)messageObject;
					String address = pingResult.getIpValue();
					
					if (address.equals(getTargetAddress())) {
						showMessage(String.format("PING %s %s", address, getStatusText(pingResult.getStatus())));
					}
				}
			} catch (JMSException e) {
				LOG.error("PingDialogMessageProcessor.message.getText() error", e);
			}
		}
	};
	
	private String getStatusText(int status) {
		//0：还未开始ping；1：成功！；2：失败、超时;3:请求ping
		String text = "还未开始";
		switch (status) {
			case 1: text = "成功";break;
			case 2: text = "失败/超时";break;
			case 3: text = "请求";break;
		}
		
		return text;
	}
	
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
	
	private final JLabel titleLabel;
	private final JLabel messageLabel;
	private final ClientModel clientModel;
	private final AdapterManager adapterManager;
	private final EquipmentModel equipmentModel;
	private final MessageSender messageSender;
	private final RemoteServer remoteServer;
	private final MessageDispatcher messageDispatcher;
	private static final Logger LOG = LoggerFactory.getLogger(PingDialog.class);
	private static final long serialVersionUID = 1335811745157709166L;
}