/**
 * 
 */
package com.jhw.adm.server.interfaces;

import javax.ejb.Local;

/**
 * @author �����
 * @ʱ�� 2010-8-24
 */
@Local
public interface CheckFEPTimerLocal{
	public void scheduleTimer(long milliseconds);//�������ǰ�û�״̬
}
