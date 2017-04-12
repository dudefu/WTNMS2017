package com.jhw.adm.client.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.warning.WarningEntity;

/**
 * 
 * 用于处理紧急告警来临时按钮的text以及toolTipText信息的封装以及信号灯的闪烁
 * @author Administrator
 *
 */
@Component(EquipmentAlarmModel.ID)
public class EquipmentAlarmModel extends ViewModel {

	public static final String ID = "equipmentAlarmModel";
	public static final String UPDATE_EMULATION_VIEW = "UPDATE_EMULATION_VIEW";
	private List<WarningEntity> warningEntitiyList = new ArrayList<WarningEntity>();
	public static final String NOTICE_VIEW_UPDATE_AFTER_CONFIRM_ALARM = "NOTICE_VIEW_UPDATE_AFTER_CONFIRM_ALARM";
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	protected void initialize(){
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_RECEIVED, equipmentAlarmListener);
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_CONFIRM, updateAfterConfirmListener);
		initialData();
	}

	private String originIP = "";
	@SuppressWarnings("unchecked")
	private void initialData() {
		Object object = equipmentModel.getLastSelected();
		if(object instanceof SwitchTopoNodeEntity){
			SwitchTopoNodeEntity selectedSwitcher = (SwitchTopoNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchTopoNodeEntity.class);
			if(null == selectedSwitcher){
				return;
			}
			originIP = selectedSwitcher.getIpValue();
		}else if(object instanceof EponTopoEntity){
			EponTopoEntity eponTopoEntity = (EponTopoEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), EponTopoEntity.class);
			if(null == eponTopoEntity){
				return;
			}
			originIP = eponTopoEntity.getIpValue();
		}
		warningEntitiyList = null;
		Object[] parms = {new Integer(0),originIP};
		String where = " where entity.currentStatus=? and entity.ipValue=?";
		warningEntitiyList = (List<WarningEntity>) remoteServer.getService().findAll(WarningEntity.class,where,parms);
	}
	
	private PropertyChangeListener equipmentAlarmListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Object alarmObject = evt.getNewValue();
			if(alarmObject instanceof WarningEntity){
				WarningEntity alarm = (WarningEntity) evt.getNewValue();
				if(originIP.equals(alarm.getIpValue())) {
					updateEquipmentAlarmInfo(alarm);
				}
			}
		}
	}; 
	
	private PropertyChangeListener updateAfterConfirmListener = new PropertyChangeListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			List<WarningEntity> newValue = (List<WarningEntity>) evt.getNewValue();
			confirmEquipmentWarning(newValue);
		}
	};
	
	protected void updateEquipmentAlarmInfo(WarningEntity alarm) {
		int oldValue = this.warningEntitiyList.size();
		this.warningEntitiyList.add(0, alarm); 
		int newValue = this.warningEntitiyList.size();
		firePropertyChange(UPDATE_EMULATION_VIEW, oldValue, newValue);
	}
	
	private void confirmEquipmentWarning(List<WarningEntity> newValue) {
		initialData();
		firePropertyChange(NOTICE_VIEW_UPDATE_AFTER_CONFIRM_ALARM, null, "UPDATE");
	}

	public List<WarningEntity> getEquipmentAlam(){
		return this.warningEntitiyList;
	}
	public String getOriginIP(){
		return originIP;
	}
}
