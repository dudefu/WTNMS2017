package com.jhw.adm.client.swing;

public interface MessageProcessorStrategy {
	
	void processorMessage();
	void removeProcessor();
	void dealTimeOut();
}
