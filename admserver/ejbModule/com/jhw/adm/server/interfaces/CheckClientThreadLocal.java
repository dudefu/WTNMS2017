package com.jhw.adm.server.interfaces;

import javax.ejb.Local;

@Local
public interface CheckClientThreadLocal {
	public void startClientTimer(long milliseconds);
}
