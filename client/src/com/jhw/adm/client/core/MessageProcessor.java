package com.jhw.adm.client.core;

import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

public interface MessageProcessor {
	void process(TextMessage message);
	void process(ObjectMessage message);
}