package com.jhw.adm.server.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.FEPMessageFactoryLocal;

/**
 * 用于监听前置机发送回来的消息
 * 
 * @author 杨霄
 * 
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/FTSQueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class FEPMessageListener implements MessageListener {
	private Logger logger = Logger.getLogger(FEPMessageListener.class.getName());
	@EJB
	private FEPMessageFactoryLocal fepmf;
	@EJB
	private DataCacheLocal datacache;

	@Override
	public void onMessage(Message message) {
		try {
			fepmf.DealWithMessage(message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@AroundInvoke
	public Object fepInterceptor(InvocationContext ic) throws Exception {
		Object[] objs = ic.getParameters();
		if (objs != null && objs.length != 0) {
			Message message = (Message) objs[0];
			String fepcode = message.getStringProperty(Constants.FEPCODE);
			if (fepcode != null) {
				FEPEntity fep = datacache.getFEPByCode(fepcode);
				if (fep != null) {
					boolean status = fep.getStatus().isStatus();
					if (status) {
						logger.info("前置登录在线验证成功!");
						return ic.proceed();
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		}
		return null;
	}

}
