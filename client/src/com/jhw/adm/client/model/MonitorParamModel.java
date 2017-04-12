package com.jhw.adm.client.model;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.system.TimeConfig;

/**
 * ����Ƶ�ʣ�������ʱ�����˷��ֳ�ʱʱ�䣬�豸ͬ����ʱʱ�䣬�������ó�ʱʱ��
 * @author Administrator
 * 
 */
@Component(MonitorParamModel.ID)
public class MonitorParamModel extends ViewModel {
	public static final String ID = "monitorParamModel";
	
	private TimeConfig timeConfig;
	
	@PostConstruct
	protected void initialize() {
		
	}
	
	public TimeConfig getTimeConfig() {
		return timeConfig;
	}

	public void setTimeConfig(List<TimeConfig> timeConfigList){
		if(null == timeConfigList || timeConfigList.size() < 1){
			this.timeConfig = new TimeConfig();
		}
		else{
			this.timeConfig = timeConfigList.get(0);
		}
	}
}
