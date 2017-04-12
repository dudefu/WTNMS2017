package com.jhw.adm.client.swing;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.WarningEntity;


/**
 * 
 * 严重告警
 * @author Administrator
 *
 */
@Component(JSeriousAlarmButtonModel.ID)
public class JSeriousAlarmButtonModel extends JAlarmButtonModel {

	public static final String ID = "jseriousAlarmButtonModel";
	private static final String SERIOUS_TEXT = "严重";
	private static boolean FLASH_STATUS = false;//闪烁状态,true-正在闪烁,false-未闪烁
	private static final int FLASH_SLEEP_TIME = 500;
	private int unconfirmSeriousAlarmNumbers = 0;
	public static final String UPDATE_SERIOUS_VIEW = "UPDATE_SERIOUS_VIEW";
	private List<WarningEntity> warningEntityList = new ArrayList<WarningEntity>();
	public static final String NOTICE_VIEW_UPDATE_AFTER_CONFIRM_SERIOUS = "NOTICE_VIEW_UPDATE_AFTER_CONFIRM_SERIOUS";
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	protected void initialize(){
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_RECEIVED, seriousAlarmListener);
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_CONFIRM, updateAfterConfirmListener);
		initialData();
		if(unconfirmSeriousAlarmNumbers > 0){
			this.startFlash();
		}
	}

	@SuppressWarnings("unchecked")
	private void initialData() {
		warningEntityList = null;
		Object[] parms = {new Integer(Constants.SERIOUS), new Integer(0)};
		String where = " where entity.warningLevel=? and currentStatus=?";
		warningEntityList = (List<WarningEntity>) remoteServer.getService().findAll(WarningEntity.class,where, parms);
		warningEntityList = NodeUtils.filterWarningEntity(warningEntityList);
		if(null != warningEntityList){
			unconfirmSeriousAlarmNumbers = warningEntityList.size();
		}else{
			unconfirmSeriousAlarmNumbers = 0;
		}
	}
	
	private PropertyChangeListener seriousAlarmListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Object alarmObject = evt.getNewValue();
			if(alarmObject instanceof WarningEntity){
				WarningEntity alarm = (WarningEntity) evt.getNewValue();
				int level = alarm.getWarningLevel();
				if(Constants.SERIOUS == level){
					updateSeriousAlarmInfo(alarm);
				}
			}
		}
	}; 
	
	private PropertyChangeListener updateAfterConfirmListener = new PropertyChangeListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			List<WarningEntity> newValue = (List<WarningEntity>) evt.getNewValue();
			confirmSeriousWarning(newValue);
		}
	};
	
	protected void confirmSeriousWarning(List<WarningEntity> newValue) {
//		for(WarningEntity warningEntity : newValue){
//			if(warningEntity.getWarningLevel() == Constants.SERIOUS){
//				this.unconfirmSeriousAlarmNumbers -= 1;
//				this.warningEntitiyList.remove(warningEntity);
//			}
//		}
		initialData();
		setButtonText(this.unconfirmSeriousAlarmNumbers);
		if(this.unconfirmSeriousAlarmNumbers == 0){
			if(this.isFlash()){
				this.stopFlash();
			}
		}
		firePropertyChange(NOTICE_VIEW_UPDATE_AFTER_CONFIRM_SERIOUS, null, "UPDATE");
	}
	
	protected void updateSeriousAlarmInfo(WarningEntity alarm) {
		unconfirmSeriousAlarmNumbers += 1;
		setButtonText(unconfirmSeriousAlarmNumbers);
		if(!this.isFlash()){
			this.startFlash();
		}
		int oldValue = this.warningEntityList.size();
		this.warningEntityList.add(0, alarm); 
		int newValue = this.warningEntityList.size();
		firePropertyChange(UPDATE_SERIOUS_VIEW, oldValue, newValue);
	}

	public List<WarningEntity> getSeriousAlam(){
		return this.warningEntityList;
	}
	
	@Override
	public void setDefaultInfo(){
		setButtonText(unconfirmSeriousAlarmNumbers);
	}
	
	public void setWarnComeingInfo(){
		/*
		 * do something
		 * like Color change to Color.RED
		 * & text change to  紧急(1)
		 * & toolTipText change to 总数(2) 已处理(1) 未处理(1)
		 * other warn messages also do it
		 */
	}
	
	/*
	 * 剩余setToolTipText未完美的处理
	 */
	public void setButtonText(int number){
		//对显示文本进行组合，如 text = "紧急(" + i + ")"
		String text = SERIOUS_TEXT + LEFT_PARENTHESIS + number + RIGHT_PARENTHESIS;
		super.setText(text);
		//another toolTipText
	}

	
	private AlarmFlashThread alarmFlashThread = null;
	@Override
	public void startFlash(){
		alarmFlashThread = new AlarmFlashThread(this,FLASH_SLEEP_TIME);
		alarmFlashThread.start();
		FLASH_STATUS = true;
	}
	
	@Override
	public void stopFlash(){
		if(alarmFlashThread.isAlive()){
			alarmFlashThread.stopThread();
			FLASH_STATUS = false;
			super.setColor(null);
		}
	}
	
	@Override
	public boolean isFlash(){
		return FLASH_STATUS;
	}
	
	@Override
	public Color getDrawColor(){
		return Color.ORANGE;
	}
	
}
