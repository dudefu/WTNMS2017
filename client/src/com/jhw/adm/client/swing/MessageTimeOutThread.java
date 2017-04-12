package com.jhw.adm.client.swing;


public class MessageTimeOutThread extends Thread {
	
	private static final int INTERVAL_TIME = 500;
	private static final int SLEEP_TIME = 500;
	private boolean hasTimeOut = true; 
	private int timeOut = 0;
	private int loopTime = 0;
	private final MessageProcessorStrategy messageProcessorStrategy;
	
	public MessageTimeOutThread(int timeOut,
			MessageProcessorStrategy messageProcessorStrategy) {
		this.timeOut = timeOut;
		this.messageProcessorStrategy = messageProcessorStrategy;
		hasTimeOut = true;
	}
	
	public void stopThread(){
		if(this.isAlive()){
			this.hasTimeOut = false;
		}
	}
	
	public void reStartTimer(){
		this.loopTime = 0;
	}
	
	@Override
	public void run() {
		while(hasTimeOut){
			if((loopTime < this.timeOut)){//ÉÐÎ´³¬Ê±
				loopTime += INTERVAL_TIME; 
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}else{
				messageProcessorStrategy.dealTimeOut();
			}
		}
	}

}
