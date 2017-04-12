package com.jhw.adm.client.core;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ÏûÏ¢¼àÌýÆ÷µÄÈÝÆ÷
 */
public class MessageListenerContainer implements ExceptionListener {

	/**
	 * Æô¶¯ÈÝÆ÷£¬¿ªÊ¼¼àÌý
	 */
	public void start() {
		Connection connection;
		try {
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(messageListener);
			connection.setExceptionListener(this);
			connection.start();
		} catch (JMSException e) {
			LOG.error("MessageListenerContainer.start error");
			throw new ApplicationException("MessageListenerContainer.start error", e);
		}
	}

	@Override
	public void onException(JMSException exception) {
		LOG.error("JMSConnection.onException");
	}
	
	/**
	 * Í£Ö¹ÈÝÆ÷
	 */
	public void stop() {
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
				LOG.error("MessageListenerContainer.stop error");
				throw new ApplicationException("MessageListenerContainer.stop error", e);
			}
		}
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	public Destination getDestination() {
		return destination;
	}
	public void setDestination(Destination destination) {
		this.destination = destination;
	}
	public MessageListener getMessageListener() {
		return messageListener;
	}
	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	private Connection connection;
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private MessageListener messageListener;
	private static final Logger LOG = LoggerFactory.getLogger(MessageListenerContainer.class);
	
	public static final String ID = "listenerContainer";
}