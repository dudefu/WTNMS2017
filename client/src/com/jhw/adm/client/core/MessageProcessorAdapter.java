package com.jhw.adm.client.core;

import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

public class MessageProcessorAdapter implements MessageProcessor {

	@Override
	public void process(ObjectMessage message) {
	}

	@Override
	public void process(TextMessage message) {
	}
}