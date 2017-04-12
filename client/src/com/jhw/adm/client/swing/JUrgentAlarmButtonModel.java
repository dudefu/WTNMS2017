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
 * ���ڴ�������澯����ʱ��ť��text�Լ�toolTipText��Ϣ�ķ�װ�Լ��źŵƵ���˸
 * 
 * @author Administrator
 * 
 */
@Component(JUrgentAlarmButtonModel.ID)
public class JUrgentAlarmButtonModel extends JAlarmButtonModel {

	public static final String ID = "jurgentAlarmButtonModel";
	private static final String URGENT_TEXT = "����";
	private static boolean FLASH_STATUS = false;// ��˸״̬,true-������˸,false-δ��˸
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
		 * do something like Color change to Color.RED & text change to ����(1) &
		 * toolTipText change to ����(2) �Ѵ���(1) δ����(1) other warn messages also do
		 * it
		 */
	}

	/*
	 * ʣ��setToolTipTextδ�����Ĵ���
	 */
	public void setButtonText(int number) {
		// ����ʾ�ı�������ϣ��� text = "����(" + i + ")"
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
