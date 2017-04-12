package com.jhw.adm.server.util;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ContextUtil {
	private static ContextUtil cu;
	private InitialContext ic = null;
	private QueueConnectionFactory queueFactory;
	private TopicConnectionFactory topicFactory;
	private QueueConnection queueConn;
	private TopicConnection topicConn;

	private ContextUtil() {
		try {
			ic = new InitialContext();
			queueFactory = (QueueConnectionFactory) ic
					.lookup("ConnectionFactory");
			topicFactory = (TopicConnectionFactory) ic
					.lookup("ConnectionFactory");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public static ContextUtil getInstance() {
		if (cu == null) {
			cu = new ContextUtil();
		}
		return cu;
	}

	public InitialContext getContext() {
		return ic;
	}

	public QueueConnection getQueueConnection() throws JMSException {
		if (queueConn == null) {
			queueConn = queueFactory.createQueueConnection();
		}
		return queueConn;
	}

	public TopicConnection getTopicConnection() throws JMSException {
		if (topicConn == null) {
			topicConn = topicFactory.createTopicConnection();
		}
		return topicConn;
	}

	public Queue getQueue(String jndiName) throws NamingException {
		Queue queue = (Queue) ic.lookup(jndiName);
		return queue;
	}
	
	public Topic getTopic(String jndiName) throws NamingException {
		Topic topic = (Topic) ic.lookup(jndiName);
		return topic;
	}

}
