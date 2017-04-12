package com.jhw.adm.server.interfaces;
import javax.ejb.Local;

@Local
public interface TimerServiceLocal {
	public void scheduleTimer(long time);
}
                                                  