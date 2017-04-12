package com.jhw.adm.server.util;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

public class ClientMessageListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {

		} else if (message instanceof ObjectMessage) {

		}
	}

}
