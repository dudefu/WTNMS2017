package com.jhw.adm.client.core;

import javax.jms.Destination;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * ��Ϣ��������ʹ��Spring.JMSTemplate
 */
public interface MessageSender {

	public void send(final MessageCreator messageCreator);

	public void setDestination(Destination destination);

	public Destination getDestination();

	public void setTemplate(JmsTemplate template);
}