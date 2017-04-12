package com.jhw.adm.client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.WarningEntity;
import com.jhw.adm.server.entity.warning.WarningHistoryEntity;
import com.jhw.adm.server.entity.warning.WarningType;

/**
 * 告警模型，包含最新的告警信息，选中的告警信息
 */
@Component(AlarmModel.ID)
public class AlarmModel {
	
	@PostConstruct
	protected void initialize() {
		propertySupport = new PropertyChangeSupport(this);
		executorService = Executors.newSingleThreadExecutor();
		lastAlarms = new ArrayList<AlarmMessage>();
		confirmAlarms = new ArrayList<WarningEntity>();
		messageDispatcher.addProcessor(MessageNoConstants.WARNING, alarmMessageProcessor);
	}
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        for (PropertyChangeListener l : propertySupport.getPropertyChangeListeners(propertyName)) {
            if (l == listener) {
                propertySupport.removePropertyChangeListener(propertyName, l);
                break;
            }
        }
    }

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
    	Runnable notify = new Runnable() {
			@Override
			public void run() {
				propertySupport.firePropertyChange(propertyName, oldValue, newValue);
			}
		};
		executorService.execute(notify);
    }
	
	/**
	 * 返回Trap告警类型列表
	 */
	public List<WarningType> getWarningTypes() {
		return warningTypes;
	}
	
	/**
	 * 返回Trap告警类型
	 */
	public WarningType getWarningType(int type) {
		WarningType warningType = null;

		if (warningTypes != null) {
			for (WarningType wt : warningTypes) {
				if (wt.getWarningType() == type) {
					warningType = wt;
					break;
				}
			}
		}
		
		return warningType;
	}
	
	public void updateSelectedWarningAttributes(List<WarningEntity> warningConfirmedList, List<WarningEntity> unConfirmedWarning, List<WarningEntity> confirmedWarning) {
		for(WarningEntity warningEntity : warningConfirmedList){
			String comment = warningEntity.getComment();
			warningEntity = remoteServer.getService().findById(warningEntity.getId(), WarningEntity.class); 
			if(warningEntity.getCurrentStatus() == Constants.UNCONFIRM){
				warningEntity.setCurrentStatus(Constants.CONFIRM);
				warningEntity.setConfirmTime(new Date());
				warningEntity.setConfirmUserName(clientModel.getCurrentUser().getUserName());
				warningEntity.setComment(comment);
				unConfirmedWarning.add(warningEntity);
			}else if(warningEntity.getCurrentStatus() == Constants.CONFIRM){
				confirmedWarning.add(warningEntity);
			}
		}
	}

	public void setWarningTypes(List<WarningType> warningTypes) {
		this.warningTypes = warningTypes;
	}

	public List<AlarmMessage> getLastAlarms() {
		return lastAlarms;
	}
	
	public WarningEntity getSingleConfirmAlarm() {
		return singleConfirmAlarm;
	}
	public void setSingleConfirmAlarm(WarningEntity singleConfirmAlarm) {
		this.singleConfirmAlarm = singleConfirmAlarm;
	}

	public WarningHistoryEntity getDetailHistoryAlarm() {
		return detailHistoryAlarm;
	}
	public void setDetailHistoryAlarm(WarningHistoryEntity detailHistoryAlarm) {
		this.detailHistoryAlarm = detailHistoryAlarm;
	}

	public void confirmAlarm(List<WarningEntity> confirmedAlarm){
		this.confirmAlarms = confirmedAlarm;
		firePropertyChange(ALARM_CONFIRM, null, this.confirmAlarms);
	}
	
	public void addAlarm(AlarmMessage alarm) {
		lastAlarms.add(alarm);
		firePropertyChange(ALARM_ARRIVAL, null, lastAlarms);
	}
	
	private void alarmArrival(final WarningEntity alarmObject) {
		AlarmMessage alarmMessage = AlarmMessage.wrap(alarmObject);
		firePropertyChange(ALARM_RECEIVED, null, alarmObject);
		addAlarm(alarmMessage);
	}
	
	private final MessageProcessorAdapter alarmMessageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(ObjectMessage message) {
			try {
				//前置机的判断尚未完成
				WarningEntity messageObject = (WarningEntity) message.getObject();
				LOG.info(String.format("告警类型:[%s],告警级别:[%s],来源:[%s],设备类型:[%s]",
						alarmTypeCategory.get(messageObject.getWarningCategory()).getKey(),
						alarmSeverity.get(messageObject.getWarningLevel()).getKey(), messageObject.getIpValue(), 
						new Integer(messageObject.getDeviceType())));
				int event = messageObject.getWarningEvent();
				if((Constants.FEP_CONNECT == event) || (Constants.FEP_DISCONNECT == event)){
					alarmArrival(messageObject);
				}else{
					String warningFrom = messageObject.getIpValue();
					if (messageObject.getDeviceType() == Constants.DEV_ONU){
						warningFrom = messageObject.getWarnOnuMac();
					}
					
					if (NodeUtils.filterWarningInfoByUser(warningFrom, 
							messageObject.getDeviceType(),
							messageObject.getWarningLevel())) {
						alarmArrival(messageObject);
					}
				}
			} catch (JMSException e) {
				LOG.error("message.getObject() error", e);
			}
		}
	};
	
	@Resource(name = MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=AlarmTypeCategory.ID)
	private AlarmTypeCategory alarmTypeCategory;
	
	@Resource(name=AlarmSeverity.ID)
	private AlarmSeverity alarmSeverity;

	private List<WarningType> warningTypes;
	private List<AlarmMessage> lastAlarms;
	private List<WarningEntity> confirmAlarms;
	private WarningEntity singleConfirmAlarm;
	private WarningHistoryEntity detailHistoryAlarm;

	private ExecutorService executorService;
    private PropertyChangeSupport propertySupport;

	private static final Logger LOG = LoggerFactory.getLogger(AlarmModel.class);
	public static final String ALARM_CONFIRM = "ALARM_CONFIRM";
	public static final String STATUS_CHANGED = "STATUS_CHANGED";
	public static final String ALARM_ARRIVAL = "ALARM_ARRIVAL";
	public static final String ALARM_RECEIVED = "ALARM_RECEIVED";
	public static final String SELECTION_CHANGED = "SELECTION_CHANGED";
	public static final String ID = "alarmModel";
}