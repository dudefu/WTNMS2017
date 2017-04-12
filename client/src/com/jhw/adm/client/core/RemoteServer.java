package com.jhw.adm.client.core;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.Destination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.server.servic.AdmServiceBeanRemote;
import com.jhw.adm.server.servic.CommonServiceBeanRemote;
import com.jhw.adm.server.servic.DataCacheServiceRemote;
import com.jhw.adm.server.servic.NMSServiceRemote;
import com.jhw.adm.server.servic.PingTimerRemote;

/**
 * 远程服务器，封装RMI,JMS连接和操作
 * */
@Component(RemoteServer.ID)
public class RemoteServer {

	@PostConstruct
	protected void initialize() {
		commonService = (CommonServiceBeanRemote) lazyCommonObjectLocator
				.getTarget();
		if (commonService == null) {
			LOG.error("initialize error: CommonServiceBeanRemote is null");
		}
		amdService = (AdmServiceBeanRemote) lazyAdmObjectLocator.getTarget();
		if (amdService == null) {
			LOG.error("initialize error: AdmServiceBeanRemote is null");
		}
		nmsService = (NMSServiceRemote) lazyNmsObjectLocator.getTarget();
		if (nmsService == null) {
			LOG.error("initialize error: NMSServiceRemote is null");
		}
		dataCacheService = (DataCacheServiceRemote) lazyDataCacheObjectLocator.getTarget();
		if (dataCacheService == null) {
			LOG.error("initialize error: DataCacheServiceRemote is null");
		}
		
		pingTimerService = (PingTimerRemote)lazyPingTimerObjectLocator.getTarget();
		if (pingTimerService == null){
			LOG.error("initialize error: PingTimerRemote is null");
		}
		
		try {
			messageSender = (MessageSender)applicationContext.getBean(JMS_OBJECT);
		} catch (BeanCreationException ex) {
			messageSender = new MessageSender() {
				@Override
				public Destination getDestination() {
					LOG.error("MessageSender.getDestination()");
					return null;
				}

				@Override
				public void send(MessageCreator messageCreator) {
					LOG.error("MessageSender.send()");
				}

				@Override
				public void setDestination(Destination destination) {
					LOG.error("MessageSender.setDestination()");
				}

				@Override
				public void setTemplate(JmsTemplate template) {
					LOG.error("MessageSender.setTemplate()");
				}
			};
		}
	}

	public void connect() {
		try {
			commonService = (CommonServiceBeanRemote) lazyCommonObjectLocator
					.start();
			amdService = (AdmServiceBeanRemote) lazyAdmObjectLocator.start();
			nmsService = (NMSServiceRemote) lazyNmsObjectLocator.start();
			dataCacheService = (DataCacheServiceRemote) lazyDataCacheObjectLocator.start();
			pingTimerService = (PingTimerRemote)lazyPingTimerObjectLocator.start();
			
		} catch (JndiLookupException e) {
			LOG.error("lazyJndiObjectLocator.start() error", e);
		}
		if (commonService == null) {
			LOG.error("connect error: CommonServiceBeanRemote is null");
		}
		if (amdService == null) {
			LOG.error("connect error: AdmServiceBeanRemote is null");
		}
		if (nmsService == null) {
			LOG.error("connect error: NMSServiceRemote is null");
		}if (dataCacheService == null) {
			LOG.error("initialize error: DataCacheServiceRemote is null");
		}
		if (pingTimerService == null) {
			LOG.error("initialize error: PingTimerRemote is null");
		}
	}

	/**
	 * @see NMSServiceRemote
	 */
	public NMSServiceRemote getNmsService() {
		return nmsService;
	}

	/**
	 * @see CommonServiceBeanRemote
	 */
	public CommonServiceBeanRemote getService() {
		return commonService;
	}

	/**
	 * @see AdmServiceBeanRemote
	 */
	public AdmServiceBeanRemote getAdmService() {
		return amdService;
	}
	
	public DataCacheServiceRemote getDataCacheService() {
		return dataCacheService;
	}

	public PingTimerRemote getPingTimerService() {
		return pingTimerService;
	}

	/**
	 * @see MessageSender
	 */
	public MessageSender getMessageSender() {
		return messageSender;
	}

	@Resource(name=COMMON_OBJECT)
	private LazyJndiObjectLocator lazyCommonObjectLocator;
	
	@Resource(name=ADM_OBJECT)
	private LazyJndiObjectLocator lazyAdmObjectLocator;
	
	@Resource(name=NMS_OBJECT)
	private LazyJndiObjectLocator lazyNmsObjectLocator;
	
	@Resource(name=DATA_CACHE_OBJECT)
	private LazyJndiObjectLocator lazyDataCacheObjectLocator;
	
	@Resource(name=PING_TIMER_OBJECT)
	private LazyJndiObjectLocator lazyPingTimerObjectLocator;
	
	@Resource
	private ApplicationContext applicationContext;
	
	private MessageSender messageSender;
	private CommonServiceBeanRemote commonService;
	private AdmServiceBeanRemote amdService;
	private NMSServiceRemote nmsService;	
	private DataCacheServiceRemote dataCacheService;
	private PingTimerRemote pingTimerService;

	private static final Logger LOG = LoggerFactory
			.getLogger(RemoteServer.class);

	public static final String ID = "remoteServer";
	public static final String COMMON_OBJECT = "lazyCommonObjectLocator";
	public static final String ADM_OBJECT = "lazyAdmObjectLocator";
	public static final String LOGIN_OBJECT = "lazyLoginObjectLocator";
	public static final String NMS_OBJECT = "lazyNmsObjectLocator";
	public static final String DATA_CACHE_OBJECT = "lazyDataCacheObjectLocator";
	public static final String PING_TIMER_OBJECT = "lazyPingTimerObjectLocator";
	public static final String JMS_OBJECT = "messageSender";
}