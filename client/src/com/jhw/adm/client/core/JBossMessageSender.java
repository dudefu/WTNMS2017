package com.jhw.adm.client.core;

import javax.jms.Destination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * 消息发送器，使用JBossJmsTemplate
 * @see JBossJmsTemplate
 */
public class JBossMessageSender implements MessageSender {

	public void send(final MessageCreator messageCreator) {
		try {
			template.send(messageCreator);
		} catch (JmsException ex) {
			LOG.error("send message error");
			throw ex;
		}
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}
	
	private JmsTemplate template;
	private Destination destination;
	private static final Logger LOG = LoggerFactory.getLogger(JBossMessageSender.class);
	public static final String ID = "jbossMessageSender";
}