package com.jhw.adm.comclient.jms;

import java.io.Serializable;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.service.BaseService;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.servic.CommonServiceBeanRemote;
import com.jhw.adm.server.servic.LoginServiceRemote;

/**
 * JMS消息队列初始化
 * 
 * @author xiongbo
 * 
 */
public class MessageSend extends BaseService {
	private static Logger log = Logger.getLogger(MessageSend.class);
	// Send
	private InitialContext initialContext;
	private QueueSender sender;
	private QueueSender heartbeatSender;
	private QueueSession session;
	private QueueConnection conn;

	private CommonServiceBeanRemote commservice;
	private LoginServiceRemote loginService;

	// Receive
	private MessageConsumer receiver;
	//
	private boolean loop = true;

	private boolean isRunning = false;
	private boolean whileSwitch = true;
	private Receive receive;
	//
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jnp.interfaces.NamingContextFactory");
		env.put(Context.PROVIDER_URL, Configuration.jndi_ipPort);
		env
				.put(Context.URL_PKG_PREFIXES,
						"org.jboss.naming:org.jnp.interfaces");

		boolean connected = false;
		while (!connected && whileSwitch) {
			try {
				connected = true;
				//
				initialContext = new InitialContext(env);
				// Send
				QueueConnectionFactory queueFactory = (QueueConnectionFactory) initialContext
						.lookup("ConnectionFactory");
				conn = queueFactory.createQueueConnection();

				Queue ftsQueue = (Queue) initialContext
						.lookup("queue/FTSQueue");
				Queue heartbeatQueue = (Queue) initialContext
						.lookup("queue/HeartbeatFEP");
				session = conn.createQueueSession(false,
						QueueSession.AUTO_ACKNOWLEDGE);
				sender = session.createSender(ftsQueue);
				heartbeatSender = session.createSender(heartbeatQueue);

				commservice = (CommonServiceBeanRemote) initialContext
						.lookup("remote/CommonServiceBean");
				loginService = (LoginServiceRemote) initialContext
						.lookup("remote/LoginService");

				// Receive
				Queue stfQueue = (Queue) initialContext.lookup("queue/"
						+ Configuration.fepCode);
				receiver = session.createConsumer(stfQueue);

				//
				conn.start();

			} catch (Exception e) {
				log.warn("Reconnecting Server..." + getTraceMessage(e));
				connected = false;
				close();
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		env.clear();
		log.info("*Connect Success*");

		// Relogin to Server
		if (isRunning) {
			FEPEntity fepEntity = getLoginService().loginFEP(
					Configuration.fepCode, Configuration.fepPassword);

			if (fepEntity == null) {
				closeAll();
				JOptionPane.showMessageDialog(null, "提示：系统已初始化数据，需要重启前置机",
						"提示", JOptionPane.NO_OPTION);
				System.exit(0);
			}

		}

		if (receive != null) {
			log.info(receive.isAlive() + " " + receive.interrupted());
			if (receive.isAlive() || !receive.interrupted()) {
				receive.interrupt();
				receive.stop();
				log.warn("3&&&Receive Thread alive");
			}
		}

		loop = true;
		receive = new Receive();
		receive.start();

	}

	private class Receive extends Thread {
		public Receive() {
			log.info("Init Receive Constructor");
		}

		public void run() {
			while (loop) {
				Message message = null;
				try {
					message = receiver.receive();
				} catch (JMSException e) {
					if (!isRunning) {
						log.error("Run(): " + getTraceMessage(e));
						reConnect();
					}

					// TODO
					return;
				}
				if (message != null) {
					log.info("Receive a message.");
					messageQueueHandler.addQueue(message);
				}
			}

		}
	}

	public void closeAll() {
		whileSwitch = false;
		loop = false;
		if (receive != null) {
			log.info(receive.isAlive() + " " + receive.interrupted());
			if (receive.isAlive() || !receive.interrupted()) {
				receive.interrupt();
				receive.stop();
				log.warn("2&&&Receive Thread alive");
			}
		}
		close();

	}

	private void close() {
		try {
			if (!isRunning) {
				if (sender != null) {
					sender.close();
					sender = null;
				}
				if (heartbeatSender != null) {
					heartbeatSender.close();
					heartbeatSender = null;
				}
				if (receiver != null) {
					receiver.close();
					receiver = null;
				}
				if (session != null) {
					session.close();
					session = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
				if (initialContext != null) {
					initialContext.close();
					initialContext = null;
				}
			} else {
				if (sender != null) {
					sender.close();
					sender = null;
				}
				if (heartbeatSender != null) {
					heartbeatSender.close();
					heartbeatSender = null;
				}
				if (receiver != null) {
					receiver.close();
					receiver = null;
				}
				if (session != null) {
					session.close();
					session = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
				if (initialContext != null) {
					initialContext.close();
					initialContext = null;
				}
			}
		} catch (Exception e) {
			log.error("When release RES:" + e);
		}

	}

	/**
	 * 
	 * @return
	 */
	public CommonServiceBeanRemote getServiceBeanRemote() {
		return commservice;
	}

	public void sendTextMessageForHeartbeat(String message, int msgType,
			String from, String to) {
		try {
			TextMessage tm = session.createTextMessage();
			tm.setIntProperty(Constants.MESSAGETYPE, msgType);
			tm.setStringProperty(Constants.MESSAGEFROM, from);
			tm.setStringProperty(Constants.MESSAGETO, to);
			tm.setStringProperty(Constants.FEPCODE, Configuration.fepCode);
			tm.setText(message);
			heartbeatSender.send(tm);
		} catch (Exception e) {
			log.error("SendTextMessageForHeartbeat(): " + getTraceMessage(e));
			if (!isRunning) {
				reConnect();
			}
		}

	}

	private void reConnect() {
		isRunning = true;
		loop = false;
		if (receive != null) {
			log.info(receive.isAlive() + " " + receive.interrupted());
			if (receive.isAlive() || !receive.interrupted()) {
				receive.interrupt();
				receive.stop();
				log.warn("1&&&Receive Thread alive");
			}
		}
		close();
		init();
		isRunning = false;
	}

	/**
	 * 
	 * @param message
	 * @param msgType
	 * @param from
	 *            (mac)
	 * @param to
	 */
	public void sendTextMessage(String message, int msgType, String from,
			String to) {
		try {
			TextMessage tm = session.createTextMessage();
			tm.setIntProperty(Constants.MESSAGETYPE, msgType);
			tm.setStringProperty(Constants.MESSAGEFROM, from);
			tm.setStringProperty(Constants.MESSAGETO, to);
			tm.setStringProperty(Constants.FEPCODE, Configuration.fepCode);
			tm.setText(message);
			sender.send(tm);

		} catch (Exception e) {
			log.error(getTraceMessage(e));
		}

	}

	/**
	 * 
	 * @param message
	 * @param res
	 * @param msgType
	 * @param from
	 *            (mac)
	 * @param to
	 */
	public void sendTextMessageRes(String message, String res, int msgType,
			String from, String to, String clientIp) {
		try {
			TextMessage tm = session.createTextMessage();
			tm.setIntProperty(Constants.MESSAGETYPE, msgType);
			tm.setStringProperty(Constants.MESSAGEFROM, from);
			tm.setStringProperty(Constants.MESSAGETO, to);
			tm.setStringProperty(Constants.MESSAGERES, res);
			tm.setStringProperty(Constants.CLIENTIP, clientIp);
			tm.setStringProperty(Constants.FEPCODE, Configuration.fepCode);
			tm.setText(message);
			sender.send(tm);
		} catch (Exception e) {
			log.error(getTraceMessage(e));
		}

	}

	/**
	 * 
	 * @param objectMessage
	 * @param msgType
	 * @param from
	 *            (mac)
	 * @param to
	 */
	public void sendObjectMessage(int deviceType, Serializable objectMessage,
			int msgType, String from, String to) {
		try {
			ObjectMessage om = session.createObjectMessage(objectMessage);
			om.setIntProperty(Constants.MESSAGETYPE, msgType);
			om.setStringProperty(Constants.MESSAGEFROM, from);
			om.setStringProperty(Constants.MESSAGETO, to);
			om.setStringProperty(Constants.FEPCODE, Configuration.fepCode);
			om.setIntProperty(Constants.DEVTYPE, deviceType);
			sender.send(om);
		} catch (Exception e) {
			log.error(getTraceMessage(e));
		}

	}

	/**
	 * 
	 * @param objectMessage
	 * @param res
	 * @param msgType
	 * @param from
	 *            (mac)
	 * @param to
	 */
	public void sendObjectMessageRes(int deviceType,
			Serializable objectMessage, String res, int msgType, String from,
			String to, String clientIp) {
		try {
			ObjectMessage om = session.createObjectMessage(objectMessage);
			om.setIntProperty(Constants.MESSAGETYPE, msgType);
			om.setStringProperty(Constants.MESSAGERES, res);
			om.setStringProperty(Constants.MESSAGEFROM, from);
			om.setStringProperty(Constants.MESSAGETO, to);
			om.setStringProperty(Constants.CLIENTIP, clientIp);
			om.setStringProperty(Constants.FEPCODE, Configuration.fepCode);
			om.setIntProperty(Constants.DEVTYPE, deviceType);
			sender.send(om);
		} catch (Exception e) {
			log.error(getTraceMessage(e));
		}

	}

	/**
	 * Equipment operation to return a message type parameter
	 * 
	 * @param objectMessage
	 *            is Class ParmRep
	 * 
	 * @param from
	 * @param to
	 * @param clientIp
	 */
	public void sendObjectMessageRes(int deviceType,
			Serializable objectMessage, String from, String to, String clientIp) {
		log.info("Send ObjectMessage");
		try {
			ObjectMessage om = session.createObjectMessage(objectMessage);
			om
					.setIntProperty(Constants.MESSAGETYPE,
							MessageNoConstants.PARMREP);

			om.setStringProperty(Constants.MESSAGEFROM, from);
			om.setStringProperty(Constants.MESSAGETO, to);
			om.setStringProperty(Constants.CLIENTIP, clientIp);
			om.setStringProperty(Constants.FEPCODE, Configuration.fepCode);
			om.setIntProperty(Constants.DEVTYPE, deviceType);
			sender.send(om);
		} catch (Exception e) {
			log.error(getTraceMessage(e));
		}

	}

	public LoginServiceRemote getLoginService() {
		return loginService;
	}

	public MessageQueueHandler getMessageQueueHandler() {
		return messageQueueHandler;
	}

	public void setMessageQueueHandler(MessageQueueHandler messageQueueHandler) {
		this.messageQueueHandler = messageQueueHandler;
	}

}
