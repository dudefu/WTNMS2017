package com.jhw.adm.client.swing;


/*
 * ���߳�ֻʵ����˸
 */
public class AlarmFlashThread extends Thread {

	private boolean loop = false;
	private JAlarmButtonModel model;
	private int flashSleepTime;
	
	public AlarmFlashThread(JAlarmButtonModel model, int flashSleepTime){
		this.loop = true;
		this.model = model;
		this.flashSleepTime = flashSleepTime;
	}
	
	@Override
	public void run() {
		while(this.loop){
			try {
				flash();
				Thread.sleep(flashSleepTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	public void stopThread(){
		if(this.isAlive()){
			this.loop = false;
		}
	}
	
	/*
	 * ͨ�����ƹ̶��������ɫ(model.getDrawColor())�Ͱ�ɫ(URGENT_DEFAULT_COLOR)���໥����
	 * ���ﵽ�ù̶�������˸��Ч��
	 */
	private boolean turnOn;
	private void flash() {
		turnOn = !turnOn;
		if (turnOn) {
			model.setColor(model.getDrawColor());
		} else {
			model.setColor(null);
		}
	}
	
}
