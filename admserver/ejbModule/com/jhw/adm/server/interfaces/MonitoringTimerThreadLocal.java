/**
 * 
 */
package com.jhw.adm.server.interfaces;

import javax.ejb.Local;

/**
 * @author �����
 * @ʱ�� 2010-8-9
 */
@Local
public interface MonitoringTimerThreadLocal {
	
	public void startMonitoringTimer(int pinglv,boolean start);

}
