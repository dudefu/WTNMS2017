package com.jhw.adm.client.core;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.JmsUtils;
import org.springframework.util.Assert;

import com.jhw.adm.server.entity.util.Constants;

/**
 * JBoss消息模版
 * @see JmsTemplate
 */
public class JBossJmsTemplate extends JmsTemplate {

	@Override
	protected void doSend(Session session, Destination destination, MessageCreator messageCreator)
			throws JMSException {

		Assert.notNull(messageCreator, "MessageCreator must not be null");
		MessageProducer producer = createProducer(session, destination);
		try {
			Message message = messageCreator.createMessage(session);
			
			int messageNo = message.getIntProperty(Constants.MESSAGETYPE);
			LOG.info(String.format("发送消息%s[%s]", getDefaultDestination(), messageNo));
			doSend(producer, message);
			// Check commit - avoid commit call within a JTA transaction.
			if (session.getTransacted() && isSessionLocallyTransacted(session)) {
				// Transacted session created by this template -> commit.
				JmsUtils.commitIfNecessary(session);
			}
		}
		finally {
			JmsUtils.closeMessageProducer(producer);
		}
	}
	
	@Override
	public <T> T execute(SessionCallback<T> action, boolean startConnection) throws JmsException {
		// TODO: using connection pool
		return super.execute(action, startConnection);
//		Assert.notNull(action, "Callback object must not be null");
//		Connection conToClose = null;
//		Session sessionToClose = null;
//		try {
//			Session sessionToUse = getSharedSession();
//			if (sessionToUse == null) {
//				conToClose = createConnection();
//				sessionToClose = createSession(conToClose);
//				if (startConnection) {
//					conToClose.start();
//				}
//				sessionToUse = sessionToClose;
//			}
//			if (logger.isDebugEnabled()) {
//				logger.debug("Executing callback on JMS Session: " + sessionToUse);
//			}
//			return action.doInJms(sessionToUse);
//		}
//		catch (JMSException ex) {
//			throw convertJmsAccessException(ex);
//		}
//		finally {
//			JmsUtils.closeSession(sessionToClose);
//			ConnectionFactoryUtils.releaseConnection(conToClose, getConnectionFactory(), startConnection);
//		}
	}
	
	private Session getSharedSession() throws JMSException {
		if (sharedSession == null) {
			sharedSession = new ThreadLocal<Session>();
			Connection sharedConnection = getSharedConnection();
			sharedSession.set(createSession(sharedConnection));
		}		

		return sharedSession.get();
	}
	
	private Connection getSharedConnection() throws JMSException {
		if (sharedConnection == null) {
			sharedConnection = new ThreadLocal<Connection>();
			sharedConnection.set(createConnection());
		}
		
		return sharedConnection.get();
	}
	
	private static ThreadLocal<Session> sharedSession;
	private static ThreadLocal<Connection> sharedConnection;
	
	private static final Logger LOG = LoggerFactory.getLogger(JBossJmsTemplate.class);
}