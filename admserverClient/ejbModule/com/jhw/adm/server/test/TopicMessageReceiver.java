package com.jhw.adm.server.test;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.util.ContextUtil;

public class TopicMessageReceiver {
	public static void main(String[] args) throws Exception {
		TopicConnection cnn = ContextUtil.getInstance().getTopicConnection();
		Topic topic = ContextUtil.getInstance().getTopic("topic/STCTopic");
		TopicSession session = cnn.createTopicSession(false,
				TopicSession.AUTO_ACKNOWLEDGE);
		TopicSubscriber subscriber = session.createSubscriber(topic);
		// ClientMessageListener cml = new ClientMessageListener();
		// subscriber.setMessageListener(cml);
		cnn.start();
		while (true) {
			Message me = subscriber.receive();
			if (me instanceof TextMessage) {
				TextMessage tm = (TextMessage) me;
				int mt = tm.getIntProperty(Constants.MESSAGETYPE);
				if (mt == MessageNoConstants.TOPOSEARCH) {
					String mmmm = tm.getText();
					System.out.println(mmmm);
				}else if(mt == MessageNoConstants.TOPOFINISH){
					String mmmm = tm.getText();
					System.out.println(mmmm);
				}
			}
			Thread.sleep(10);
		}

	}

}
