package com.jhw.adm.client.model.switcher;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.ViewModel;
import com.jhw.adm.server.entity.ports.QOSPriority;
import com.jhw.adm.server.entity.switchs.Priority802D1P;
import com.jhw.adm.server.entity.switchs.PriorityDSCP;
import com.jhw.adm.server.entity.switchs.PriorityTOS;

@Component(QOSPriorityViewModel.ID)
public class QOSPriorityViewModel extends ViewModel{
	public static final String ID = "qosPriorityViewModel";
	
	private QOSPriority qosPriority = null;
	private List<Priority802D1P> prioEOTList;
	private List<PriorityDSCP> prioDSCPList;
	private List<PriorityTOS> prioTOSList;
	
	@PostConstruct
	protected void initialize() {
		
	}

	public void setQOSPriority (QOSPriority qosPriority){
		this.qosPriority = qosPriority;
		if (null == qosPriority){
			return;
		}
		
		this.prioTOSList = qosPriority.getPriorityTOSs();
		this.prioEOTList = qosPriority.getPriorityEOTs();
		this.prioDSCPList = qosPriority.getPriorityDSCPs();
	}
	
	public QOSPriority getQosPriority() {
		return qosPriority;
	}

	public Priority802D1P getPrioEOT(int priority) {
		Priority802D1P priority802D1P = null;
		for(int i = 0 ; i < prioEOTList.size(); i++){
			priority802D1P = prioEOTList.get(i);
			int prio = priority802D1P.getPriorityLevel();
			if (prio == priority){
				break;
			}
		}
		
		return priority802D1P;
	}
	
	public PriorityTOS getPrioTOS(int priority) {
		PriorityTOS priorityTOS = null;
		for(int i = 0 ; i < prioTOSList.size(); i++){
			priorityTOS = prioTOSList.get(i);
			int prio = priorityTOS.getPriorityLevel();
			if (prio == priority){
				break;
			}
		}
		
		return priorityTOS;
	}

	public PriorityDSCP getPrioDSCP(int priority) {
		PriorityDSCP priorityDSCP = null;
		for(int i = 0 ; i < prioDSCPList.size(); i++){
			priorityDSCP = prioDSCPList.get(i);
			int dscp = priorityDSCP.getDscp();
			if (dscp == priority){
				break;
			}
		}
		
		return priorityDSCP;
	}
}
