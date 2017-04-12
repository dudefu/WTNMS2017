package com.jhw.adm.server.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.log4j.Logger;
import org.jboss.beans.metadata.api.annotations.Create;

import com.jhw.adm.server.interfaces.ClientMessageFactoryLocal;

/**
 * 用于监听从客户端发送到服务器端的文本消息
 * 
 * @author 杨霄
 * 
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/CTSQueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ClientMessageListener implements MessageListener {
	
	
	@EJB
	private ClientMessageFactoryLocal cmfl;
	 private Logger logger = Logger.getLogger(ClientMessageListener.class.getName());

	@Override
	public void onMessage(Message message) {
		logger.info("接收到客户端消息!");
		try {
			cmfl.DealWithMessage(message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
