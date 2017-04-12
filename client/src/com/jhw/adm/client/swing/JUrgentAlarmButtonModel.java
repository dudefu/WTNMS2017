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
 * 用于处理紧急告警来临时按钮的text以及toolTipText信息的封装以及信号灯的闪烁
 * 
 * @author Administrator
 * 
 */
@Component(JUrgentAlarmButtonModel.ID)
public class JUrgentAlarmButtonModel extends JAlarmButtonModel {

	public static final String ID = "jurgentAlarmButtonModel";
	private static final String URGENT_TEXT = "紧急";
	private static boolean FLASH_STATUS = false;// 闪烁状态,true-正在闪烁,false-未闪烁
	private static final int FLASH_SLEEP_TIME = 250;
	public static final String UPDATE_URGENT_VIEW = "UPDATE_URGENT_VIEW";
	private int unconfirmUrgentAlarmNumbers = 0;
	private List<WarningEntity> warningEntityList = new ArrayList<WarningEntity>();
	public static final String NOTICE_VIEW_UPDATE_AFTER_CONFIRM_URGENT = "NOTICE_VIEW_UPDATE_AFTER_CONFIRM_URGENT";

	@Resource(name = AlarmModel.ID)
	private AlarmModel alarmModel;

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;

	@PostConstruct
	protected void initialize() {
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_RECEIVED,
				urgentAlarmListener);
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_CONFIRM,
				updateAfterConfirmListener);
		initialData();
		if (unconfirmUrgentAlarmNumbers > 0) {
			this.startFlash();
		}
	}

	@SuppressWarnings("unchecked")
	private void initialData() {
		warningEntityList = null;
		Object[] parms = { new Integer(Constants.URGENCY), new Integer(0) };
		String where = " where entity.warningLevel=? and currentStatus=?";
		warningEntityList = (List<WarningEntity>) remoteServer.getService()
				.findAll(WarningEntity.class, where, parms);
		warningEntityList = NodeUtils.filterWarningEntity(warningEntityList);
		if (null != warningEntityList) {
			unconfirmUrgentAlarmNumbers = warningEntityList.size();
		} else {
			unconfirmUrgentAlarmNumbers = 0;
		}
	}

	private PropertyChangeListener urgentAlarmListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Object alarmObject = evt.getNewValue();
			if (alarmObject instanceof WarningEntity) {
				WarningEntity alarm = (WarningEntity) evt.getNewValue();
				int level = alarm.getWarningLevel();
				if (Constants.URGENCY == level) {
					updateUrgentAlarmInfo(alarm);
				}
			}
		}
	};

	private PropertyChangeListener updateAfterConfirmListener = new PropertyChangeListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			List<WarningEntity> newValue = (List<WarningEntity>) evt
					.getNewValue();
			confirmUrgentWarning(newValue);
		}
	};

	protected void updateUrgentAlarmInfo(WarningEntity alarm) {
		unconfirmUrgentAlarmNumbers += 1;
		setButtonText(unconfirmUrgentAlarmNumbers);
		if (!this.isFlash()) {
			this.startFlash();
		}
		int oldValue = this.warningEntityList.size();
		this.warningEntityList.add(0, alarm);
		int newValue = this.warningEntityList.size();
		firePropertyChange(UPDATE_URGENT_VIEW, oldValue, newValue);
	}

	private void confirmUrgentWarning(List<WarningEntity> newValue) {
		// for(WarningEntity warningEntity : newValue){
		// if(warningEntity.getWarningLevel() == Constants.URGENCY){
		// this.unconfirmUrgentAlarmNumbers -= 1;
		// this.warningEntitiyList.remove(warningEntity);
		// }
		// }
		initialData();
		setButtonText(this.unconfirmUrgentAlarmNumbers);
		if (this.unconfirmUrgentAlarmNumbers == 0) {
			if (this.isFlash()) {
				this.stopFlash();
			}
		}
		firePropertyChange(NOTICE_VIEW_UPDATE_AFTER_CONFIRM_URGENT, null,
				"UPDATE");// just for notice
	}

	public List<WarningEntity> getUrgentAlam() {
		return this.warningEntityList;
	}

	@Override
	public void setDefaultInfo() {
		setButtonText(unconfirmUrgentAlarmNumbers);
	}

	public void setWarnComeingInfo() {
		/*
		 * do something like Color change to Color.RED & text change to 紧急(1) &
		 * toolTipText change to 总数(2) 已处理(1) 未处理(1) other warn messages also do
		 * it
		 */
	}

	/*
	 * 剩余setToolTipText未完美的处理
	 */
	public void setButtonText(int number) {
		// 对显示文本进行组合，如 text = "紧急(" + i + ")"
		String text = URGENT_TEXT + LEFT_PARENTHESIS + number
				+ RIGHT_PARENTHESIS;
		super.setText(text);
		// another toolTipText
	}

	private AlarmFlashThread alarmFlashThread = null;

	@Override
	public void startFlash() {
		alarmFlashThread = new AlarmFlashThread(this, FLASH_SLEEP_TIME);
		alarmFlashThread.start();
		FLASH_STATUS = true;
	}

	@Override
	public void stopFlash() {
		if (alarmFlashThread.isAlive()) {
			alarmFlashThread.stopThread();
			FLASH_STATUS = false;
			super.setColor(null);
		}
	}

	@Override
	public boolean isFlash() {
		return FLASH_STATUS;
	}

	@Override
	public Color getDrawColor() {
		return Color.RED;
	}

}
