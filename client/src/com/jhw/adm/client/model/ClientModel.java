package com.jhw.adm.client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageListenerContainer;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.ThreadUtils;
import com.jhw.adm.server.entity.system.TimeConfig;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.License;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.servic.CommonServiceBeanRemote;
import com.jhw.adm.server.servic.NMSServiceRemote;

/**
 * 客户端模型，包含当前用户，服务器，顶层窗口，设备模型。
 */
@Component(ClientModel.ID)
public class ClientModel {
	
	@PostConstruct
	protected void initialize() {
		connected = true;
		propertySupport = new PropertyChangeSupport(this);
		executorService = Executors.newCachedThreadPool(ThreadUtils.createThreadFactory(ClientModel.class.getSimpleName()));
		messageDispatcher.addProcessor(MessageNoConstants.CLIENTHEARTBEAT, heartbreakMessageProcessor);
	}

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        for (PropertyChangeListener l : propertySupport.getPropertyChangeListeners(propertyName)) {
            if (l == listener) {
                propertySupport.removePropertyChangeListener(propertyName, l);
                break;
            }
        }
    }

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
    	Runnable notify = new Runnable() {
			@Override
			public void run() {
		        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
			}
		};
		executorService.execute(notify);
    }
    
	/**
	 * 启动心跳检测
	 */
	public void startHeartbreak() {
    	secondInterval = 10;
    	delay = TimeUnit.SECONDS.toMillis(1);
    	if (timeConfig != null) {
    		if (timeConfig.getHeartbeatMaxTime() > 0) {
    			secondInterval = timeConfig.getHeartbeatMaxTime();
    			delay = TimeUnit.SECONDS.toMillis(timeConfig.getHearbeatdelayMaxTime());
    		}
    	}
    	stopwatch = new Stopwatch();
		executorService.execute(stopwatch);
	}
	
	private void sendHeartbreak() {
		MessageCreator messageCreator = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage("心跳检测");
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.CLIENTHEARTBEAT);
				message.setStringProperty(Constants.MESSAGEFROM, getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, getLocalAddress());
				return message;
			}
		};
		try {
			heartbeatMessageSender.send(messageCreator);
		} catch (UncategorizedJmsException e) {
			onDisconnect();
			LOG.error("MessageSender.send error, may be connection timed out", e);
		}
	}
	
	private void onTimeout() {
		// 服务器没有响应，并且之前没有超时
		if (responseless && !timeout) {
			LOG.info("server responseless.");
			timeout = true;
			firePropertyChange(TIMEOUNT, false, true);
			// 如果30秒还是超时，则断开重新连接。
			Thread checkConnection = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(TimeUnit.MINUTES.toMillis(1));
						if (timeout) {
							onDisconnect();
						}
					} catch (InterruptedException e) {
					}
				}
			};
			checkConnection.start();
		}
	}
		
	private void onDisconnect() {
		if (connected) {
			LOG.info("server disconnect.");
			connected = false;
			firePropertyChange(DISCONNECT, false, true);
		}
	}
	
	private void onConnected() {
		if (connected == false) {
			connected = true;
			LOG.info("server connected.");
			firePropertyChange(CONNECTED, false, true);
		} else if (timeout) {
			LOG.info("server response.");
			timeout = false;
			firePropertyChange(CONNECTED, false, true);
		}
	}
	
	private final MessageProcessorAdapter heartbreakMessageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(TextMessage message) {
			responseless = false;
			onConnected();
		}
	};

	private class Stopwatch extends Thread {
		
		public Stopwatch() {
			setName("ClientModel.Stopwatch");
			running = true;
		}
		
		@Override
		public void run() {
			try {
				while (running) {
					loop();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOG.info("Stopwatch stop");
			}
		}
		
		private void loop() throws InterruptedException {
			if (connected) {
				responseless = true;
				sendHeartbreak();
				while (second < max) {
					second++;
					Thread.sleep(unit);
				}
				second = 0;
				if (responseless) {
					Thread.sleep(delay);
					if (responseless) {
						onTimeout();
					}
				}
			} else {
				Thread.sleep(unit);
				tryConnect();
			}
		}
		
		private final int unit = 1000;
		private int second;
		private final int max = secondInterval;
		private final boolean running;
	}
	
	private void tryConnect() {
		LOG.info("try connect...");
		
		try {
			getCommonService().findById(0L, TimeConfig.class);
			MessageListenerContainer messageListenerContainer = 
				ClientUtils.getSpringBean(MessageListenerContainer.class, MessageListenerContainer.ID);
			messageListenerContainer.start();
			onConnected();
		} catch (Exception ex) {
			connected = false;
			responseless = true;
			LOG.error("try connect fail....", ex);
		}
	}
	
	/**
	 * @see EquipmentModel
	 */
	public EquipmentModel getEquipmentModel() {
		return equipmentModel;
	}

	/**
	 * 客户端登陆用户
	 */
	public UserEntity getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserEntity currentUser) {
		this.currentUser = currentUser;
	}
	
	/**
	 * 客户端本机地址
	 */
	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	/**
	 * 心跳包发送器
	 */
	public MessageSender getHeartbeatMessageSender() {
		return heartbeatMessageSender;
	}

	public void setHeartbeatMessageSender(MessageSender heartbeatMessageSender) {
		this.heartbeatMessageSender = heartbeatMessageSender;
	}

	/**
	 * @see NMSServiceRemote
	 */
	public NMSServiceRemote getNmsService() {
		return nmsService;
	}

	public void setNmsService(NMSServiceRemote nmsService) {
		this.nmsService = nmsService;
	}

	/**
	 * @see CommonServiceBeanRemote
	 */
	public CommonServiceBeanRemote getCommonService() {
		return commonService;
	}

	public void setCommonService(CommonServiceBeanRemote commonService) {
		this.commonService = commonService;
	}

	/**
	 * 登录服务器信息
	 */
	public ServerInfo getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(ServerInfo serverConfig) {
		this.serverConfig = serverConfig;
	}

	/**
	 * 客户端配置信息
	 */
	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	public void setClientConfig(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}
	
	public void setTimeConfig(TimeConfig config) {
		timeConfig = config;
	}
	
	/**
	 * license信息
	 */
	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	private TimeConfig timeConfig;
	private Stopwatch stopwatch;
	private int secondInterval;
	private long delay;
	private boolean timeout;
	private boolean responseless;
	private boolean connected;
	
	private ServerInfo serverConfig;
	private ClientConfig clientConfig;
	private ExecutorService executorService;
	private NMSServiceRemote nmsService;
	private CommonServiceBeanRemote commonService;
	private MessageSender heartbeatMessageSender;
	private PropertyChangeSupport propertySupport;
	private String localAddress;
	private UserEntity currentUser;
	private License license;
	private static final Logger LOG = LoggerFactory.getLogger(ClientModel.class);
	public static final String TIMEOUNT = "TIMEOUNT";
	public static final String DISCONNECT = "DISCONNECT";
	public static final String CONNECTED = "CONNECTED";
	public static final String ID = "clientModel";
}