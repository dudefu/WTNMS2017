package com.jhw.adm.client.model.switcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.ports.QOSStormControl;

@Component(QOSStormViewModel.ID)
public class QOSStormViewModel extends ViewModel{
	public static final String ID = "qosStormViewModel";

	private List<QOSStormControl> qosStormControlList = null;
	@PostConstruct
	protected void initialize() {
		
	}
	
	public void setQosStormControlList(List<QOSStormControl> qosStormControlList) {
		if (null == qosStormControlList){
			qosStormControlList = new ArrayList<QOSStormControl>();
		}
		else{
			this.qosStormControlList = qosStormControlList;
		}
	}
	
	public QOSStormControl getQOSStormControl(int port){
		QOSStormControl qosStormControl = null;
		Iterator iterator = qosStormControlList.iterator();
		while(iterator.hasNext()){
			qosStormControl = (QOSStormControl)iterator.next();
			int portNo = qosStormControl.getPortNo();
			if (port == portNo){
				break;
			}
		}
		
		return qosStormControl;
	}
}
