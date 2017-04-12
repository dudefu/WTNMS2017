package com.jhw.adm.server.interfaces;

import javax.ejb.Local;

@Local
public interface PingTimerThreadLocal {
	public void startPingTimer(int pinglv,boolean start);
	public void stopPingTimer(boolean stop);
}
