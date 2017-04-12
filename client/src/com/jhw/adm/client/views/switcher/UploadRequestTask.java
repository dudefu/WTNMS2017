package com.jhw.adm.client.views.switcher;

import java.util.HashSet;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

public class UploadRequestTask implements Task {

	private RemoteServer remoteServer;
	private MessageSender messageSender;
	private ClientModel clientModel;
	private HashSet<SynchDevice> synDeviceList = null;
	private int operationMessage = -1;
	public UploadRequestTask(HashSet<SynchDevice> synDeviceList, int message){
		this.synDeviceList = synDeviceList;
		this.operationMessage = message;
		
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
		clientModel = ClientUtils.getSpringBean(ClientModel.ID);
		messageSender = remoteServer.getMessageSender();
	}
	
	@Override
	public void run() {
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.SINGLESYNCHDEVICE);
				message.setObject(synDeviceList);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				message.setIntProperty(Constants.MESSPARMTYPE, operationMessage);
				return message;
			}
		});	
	}
}
