package com.jhw.adm.server.test;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class ClientMessage {

	
	public static void main(String[] args) throws NamingException, JMSException{
		
		System.out.println("׼��������Ϣ");
		QueueConnection cnn=null;
		QueueSender sender= null;
		QueueSession session =null;
		
		InitialContext context =new InitialContext();
		Queue queue =(Queue) context.lookup("queue/kuaffejb3/sample");
		QueueConnectionFactory factory =(QueueConnectionFactory) context.lookup("ConnectionFactory");
		cnn=factory.createQueueConnection();
		session=cnn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		TextMessage msg =session.createTextMessage("�߸���Ŷ����Ŷ��ȥ����");
		sender=session.createSender(queue);
		sender.send(msg);
		System.out.println("��Ϣ�Ѿ�����");
	}
	
	
}
