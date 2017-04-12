/**
 * 
 */
package com.jhw.adm.server.interfaces;

import javax.ejb.Local;

/**
 * @author 左军勇
 * @时间 2010-8-24
 */
@Local
public interface CheckFEPTimerLocal{
	public void scheduleTimer(long milliseconds);//开启检测前置机状态
}
