/**
 * 
 */
package com.jhw.adm.server.servic;


import java.util.List;

import javax.ejb.Remote;

import com.jhw.adm.server.entity.warning.FaultDetection;
import com.jhw.adm.server.entity.warning.TimerMonitoring;

/**
 * @author ×ó¾üÓÂ
 * @Ê±¼ä 2010-7-30
 */
@Remote
public interface PingTimerRemote {
	public void updateFaultDetection(FaultDetection faultDetection) throws InterruptedException;
	public void updateMonitoring(List<TimerMonitoring> list);
	public void deleteMonitoring(List<TimerMonitoring> list);

}
