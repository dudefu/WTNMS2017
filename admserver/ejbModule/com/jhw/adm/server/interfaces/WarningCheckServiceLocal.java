package com.jhw.adm.server.interfaces;
import javax.ejb.Local;

@Local
public interface WarningCheckServiceLocal {
	public void scheduleTimer(long milliseconds);
}
                                                  