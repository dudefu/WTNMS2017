package com.jhw.adm.server.interfaces;

import javax.ejb.Local;
import javax.jms.JMSException;
import javax.jms.Message;

@Local
public interface ClientMessageFactoryLocal {
	public void DealWithMessage(Message message) throws JMSException;
}
