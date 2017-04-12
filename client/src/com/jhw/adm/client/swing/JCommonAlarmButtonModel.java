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
 * 普通告警
 * @author Administrator
 *
 */
@Component(JCommonAlarmButtonModel.ID)
public class JCommonAlarmButtonModel extends JAlarmButtonModel {

	public static final String ID = "jcommonAlarmButtonModel";
	private static final String COMMON_TEXT = "普通";
	private static boolean FLASH_STATUS = false;//闪烁状态,true-正在闪烁,false-未闪烁
	private static final int FLASH_SLEEP_TIME = 1500;
	public static final String UPDATE_COMMON_VIEW = "UPDATE_COMMON_VIEW";
	private int unconfirmCommonAlarmNumbers = 0;
	private List<WarningEntity> warningEntityList = new ArrayList<WarningEntity>();
	public static final String NOTICE_VIEW_UPDATE_AFTER_CONFIRM_COMMON = "NOTICE_VIEW_UPDATE_AFTER_CONFIRM_COMMON";
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	protected void initialize(){
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_RECEIVED, commonAlarmListener);
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_CONFIRM, updateAfterConfirmListener);
		initialData();
		if(unconfirmCommonAlarmNumbers > 0){
			this.startFlash();
		}
	}

	@SuppressWarnings("unchecked")
	private void initialData() {
		warningEntityList = null;
		Object[] parms = {new Integer(Constants.GENERAL), new Integer(0)};
		String where = " where entity.warningLevel=? and currentStatus=?";
		warningEntityList = (List<WarningEntity>) remoteServer.getService().findAll(WarningEntity.class, where, parms);
		warningEntityList = NodeUtils.filterWarningEntity(warningEntityList);
		if(null != warningEntityList){
			unconfirmCommonAlarmNumbers = warningEntityList.size();
		}else {
			unconfirmCommonAlarmNumbers = 0;
		}
	}
	
	private PropertyChangeListener commonAlarmListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Object alarmObject = evt.getNewValue();
			if(alarmObject instanceof WarningEntity){
				WarningEntity alarm = (WarningEntity) evt.getNewValue();
				int level = alarm.getWarningLevel();
				if(Constants.GENERAL == level){
					updateCommonAlarmInfo(alarm);
				}
			}
		}
	}; 
	
	private PropertyChangeListener updateAfterConfirmListener = new PropertyChangeListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			List<WarningEntity> newValue = (List<WarningEntity>) evt.getNewValue();
			confirmCommonWarning(newValue);
		}
	};
	
	protected void confirmCommonWarning(List<WarningEntity> newValue) {
//		for(WarningEntity warningEntity : newValue){
//			if(warningEntity.getWarningLevel() == Constants.GENERAL){
//				this.unconfirmCommonAlarmNumbers -= 1;
//				this.warningEntitiyList.remove(warningEntity);
//			}
//		}
		initialData();
		setButtonText(this.unconfirmCommonAlarmNumbers);
		if(this.unconfirmCommonAlarmNumbers == 0){
			if(this.isFlash()){
				this.stopFlash();
			}
		}
		firePropertyChange(NOTICE_VIEW_UPDATE_AFTER_CONFIRM_COMMON, null, "UPDATE");
	}
	
	protected void updateCommonAlarmInfo(WarningEntity alarm) {
		unconfirmCommonAlarmNumbers += 1;
		setButtonText(unconfirmCommonAlarmNumbers);
		if(!this.isFlash()){
			this.startFlash();
		}
		int oldValue = this.warningEntityList.size();
		this.warningEntityList.add(0, alarm); 
		int newValue = this.warningEntityList.size();
		firePropertyChange(UPDATE_COMMON_VIEW, oldValue, newValue);
	}
	
	public List<WarningEntity> getCommonAlam(){
		return this.warningEntityList;
	}
	
	@Override
	public void setDefaultInfo(){
		setButtonText(unconfirmCommonAlarmNumbers);
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
		String text = COMMON_TEXT + LEFT_PARENTHESIS + number + RIGHT_PARENTHESIS;
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
		return Color.CYAN;
	}
	
}
