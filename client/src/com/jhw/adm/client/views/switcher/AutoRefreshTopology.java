package com.jhw.adm.client.views.switcher;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.views.RefreshTopologyView;
import com.jhw.adm.server.entity.message.TopoFoundFEPs;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 自动更新拓扑图
 * 
 * @author dudefu
 *
 */
public class AutoRefreshTopology extends Thread {

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;
	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;
	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	TopoFoundFEPs topoFoundFEPs;
	MessageSender messageSender;

	@Override
	public void run() {
		while (true) {
			System.out.println(666);
			if (null != RefreshTopologyView.topoFoundFEPs || null != RefreshTopologyView.messageSender) {
				topoFoundFEPs = RefreshTopologyView.topoFoundFEPs;
				messageSender = RefreshTopologyView.messageSender;
				messageSender.send(new MessageCreator() {
					public Message createMessage(Session session) throws JMSException {
						ObjectMessage message = session.createObjectMessage();
						message.setIntProperty(Constants.MESSAGETYPE, MessageNoConstants.TOPOSEARCH);
						Long diagramId = equipmentModel.getDiagram().getId();
						String username = clientModel.getCurrentUser().getUserName();
						topoFoundFEPs.setRefreshDiagramId(diagramId);
						message.setObject(topoFoundFEPs);
						message.setStringProperty(Constants.MESSAGEFROM, username);
						message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
						return message;
					}
				});
			}
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
