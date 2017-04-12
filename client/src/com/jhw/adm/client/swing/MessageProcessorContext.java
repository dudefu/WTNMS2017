package com.jhw.adm.client.swing;

public class MessageProcessorContext {
	
	public MessageProcessorStrategy messageProcessorStrategy;
	
	public MessageProcessorContext(MessageProcessorStrategy messageProcessorStrategy){
		this.messageProcessorStrategy = messageProcessorStrategy;
	}

	public void messageProcessor(){
		this.messageProcessorStrategy.processorMessage();
	}
	
	public void removeProcessor(){
		this.messageProcessorStrategy.removeProcessor();
	}
	
}
