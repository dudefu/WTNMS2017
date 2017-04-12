package com.jhw.adm.server.test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.util.ContextUtil;

public class QueueMessageReceiver {
	InitialContext ctx;
	QueueSender sender = null;
	QueueSession session = null;
	QueueConnection cnn = null;
	Queue queue = null;

	public void initContext() {
		try {
			ctx = new InitialContext();
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
		try {
			cnn = ContextUtil.getInstance().getQueueConnection();
			queue = ContextUtil.getInstance().getQueue("queue/shenzhen");
			session = cnn.createQueueSession(false,
					QueueSession.AUTO_ACKNOWLEDGE);
			sender = session.createSender(queue);

		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException je) {
			je.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		QueueMessageReceiver re = new QueueMessageReceiver();
		re.initContext();
		Session session = null;

		QueueConnection cnn = ContextUtil.getInstance().getQueueConnection();
		Queue queue = ContextUtil.getInstance().getQueue("queue/shenzhen");

		session = cnn.createQueueSession(false,// 不需要事务
				QueueSession.AUTO_ACKNOWLEDGE);// 自动接收消息
		MessageConsumer receiver = session.createConsumer(queue);

		cnn.start();
		while (true) {
			Message message = receiver.receive();
			if (message instanceof TextMessage) {
				TextMessage tm = (TextMessage) message;
				int mt = tm.getIntProperty(Constants.MESSAGETYPE);
				String from = tm.getStringProperty(Constants.MESSAGEFROM);
				String ipvalue = tm.getStringProperty(Constants.MESSAGETO);
				if (message != null && mt == MessageNoConstants.TOPOSEARCH) {
					SwitchNodeEntity snt = Tools.buildSwitch1();
					re
							.sendback(from, ipvalue,
									MessageNoConstants.TOPONODE, snt);
				}
			} else if (message instanceof ObjectMessage) {
				ObjectMessage om = (ObjectMessage) message;
				int type = om.getIntProperty(Constants.MESSAGETYPE);
				if (type == MessageNoConstants.VLANSET) {
					String from = om.getStringProperty(Constants.MESSAGEFROM);
					String to = om.getStringProperty(Constants.MESSAGETO);
					Object obj = om.getObject();
					System.out.println(type + "   " + from + "    " + to
							+ "   ");
				}

			}
			Thread.sleep(1000);
		}
	}

	public void sendback(String to, String ipvalue, int mt,
			SwitchNodeEntity mess) throws Exception {
		ObjectMessage om = session.createObjectMessage(mess);
		om.setStringProperty(Constants.MESSAGETO, to);
		om.setStringProperty(Constants.MESSAGEFROM, ipvalue);
		om.setIntProperty(Constants.MESSAGETYPE, mt);
		sender.send(om);
	}

}