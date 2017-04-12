package com.jhw.adm.server.test;

import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import com.jhw.adm.server.util.ContextUtil;

public class TopicMessageSend {
	public static void main(String[] args) throws Exception {
		TopicConnection cnn = ContextUtil.getInstance().getTopicConnection();
		Topic topic = ContextUtil.getInstance().getTopic("");
		TopicSession session = cnn.createTopicSession(false,
				TopicSession.AUTO_ACKNOWLEDGE);
		TopicPublisher publisher = session.createPublisher(topic);
		TextMessage msg = session.createTextMessage("Hello World");
		publisher.publish(msg);
		publisher.close();
	}

}
