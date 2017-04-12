package com.jhw.adm.client.model.switcher;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.ports.QOSSpeedConfig;

@Component(QOSRateLimitViewModel.ID)
public class QOSRateLimitViewModel extends ViewModel{
	public static final String ID = "qosRateLimitViewModel";
	
	private List<QOSSpeedConfig> dataList = new ArrayList<QOSSpeedConfig>();
	
	@PostConstruct
	protected void initialize() {
		
	}
	
	public List<QOSSpeedConfig> getDataList() {
		return dataList;
	}

	public void setDataList(List<QOSSpeedConfig> dataList) {
		this.dataList = dataList;
	}

	public QOSSpeedConfig getValueAt(int port){
		if (null == dataList){
			return null;
		}
		QOSSpeedConfig qosSpeedConfig = null;
		for(int i = 0 ; i < dataList.size(); i++ ){
			qosSpeedConfig = dataList.get(i);
			int portNo = qosSpeedConfig.getPortNo();
			if (portNo == port){
				break;
			}
		}
		
		return qosSpeedConfig;
		
		
	}
}
