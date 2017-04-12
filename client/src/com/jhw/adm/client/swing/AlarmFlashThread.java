package com.jhw.adm.client.swing;


/*
 * 该线程只实现闪烁
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
	 * 通过绘制固定区域的亮色(model.getDrawColor())和暗色(URGENT_DEFAULT_COLOR)的相互交替
	 * 来达到该固定区域闪烁的效果
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
