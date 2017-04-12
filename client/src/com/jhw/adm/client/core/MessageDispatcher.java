package com.jhw.adm.client.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.ThreadUtils;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * JMS消息分发器
 */
public class MessageDispatcher implements MessageListener {
	
	public MessageDispatcher() {
		executorService = Executors.newCachedThreadPool(ThreadUtils.createThreadFactory(MessageDispatcher.class.getSimpleName()));
		listOfProcessor = new CopyOnWriteArrayList<MessageProcessor>();
		mapOfProcessor = new ConcurrentHashMap<Integer, List<MessageProcessor>>();
		messageNoMap = new HashMap<Integer, String>();
		
		for (Field field : MessageNoConstants.class.getDeclaredFields()) {
			try {
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
					LOG.debug(String.format("Field: %s, %s", field.getName(), field.getInt(field)));
					messageNoMap.put(field.getInt(field), field.getName());
				}
			} catch (IllegalArgumentException e) {
				LOG.error("getDeclaredFields error", e);
			} catch (IllegalAccessException e) {
				LOG.error("getDeclaredFields error", e);
			}
		}
	}
	
	/**
	 * 用消息编号注册消息处理器，该处理器只处理这个编号的消息
	 */
	public void addProcessor(Integer messageNo, MessageProcessor processor) {
		if (mapOfProcessor.containsKey(messageNo)) {
			List<MessageProcessor> listOfNoProcessor = mapOfProcessor.get(messageNo);
			listOfNoProcessor.add(processor);
		} else {
			List<MessageProcessor> listOfNumberedProcessor = new CopyOnWriteArrayList<MessageProcessor>();
			listOfNumberedProcessor.add(processor);
			mapOfProcessor.put(messageNo, listOfNumberedProcessor);
		}
	}
	
	public void removeProcessor(Integer messageNo, MessageProcessor processor) {
		if (mapOfProcessor.containsKey(messageNo)) {
			List<MessageProcessor> listOfProcessor = mapOfProcessor.get(messageNo);
			listOfProcessor.remove(processor);
		}
	}
	
	public void addProcessor(MessageProcessor processor) {
		listOfProcessor.add(processor);
	}

	public void removeProcessor(MessageProcessor processor) {
		listOfProcessor.remove(processor);
	}

	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage)message;
			notifyTextMessage(textMessage);
		}
		if (message instanceof ObjectMessage) {
			ObjectMessage objectMessage = (ObjectMessage)message;
			notifyObjectMessage(objectMessage);
		}
	}
	
	protected void notifyTextMessage(final TextMessage textMessage) {
		int messageNo = 0;
		try {
			messageNo = textMessage.getIntProperty(Constants.MESSAGETYPE);
			String address = ClientUtils.getLocalAddress();
			String to = textMessage.getStringProperty(Constants.MESSAGETO);
			String clientAddress = textMessage.getStringProperty(Constants.CLIENTIP);
			String messageName = messageNoMap.get(messageNo);
			LOG.debug(String.format("收到消息%s[%s:%s][%s:%s]", textMessage.getJMSDestination(), messageNo, messageName, to, clientAddress));
			
			if (excludeAll(messageNo)) {
				LOG.debug("excludeAll");
			} else if (excludeAddress(messageNo) &&
					clientModel.getCurrentUser().getUserName().equals(to)) {
			} else if (address.equals(clientAddress) && 
					clientModel.getCurrentUser().getUserName().equals(to)) {
			} else {
				return;
			}
		} catch (JMSException e) {
			LOG.error("getStringProperty error", e);
		}
		
		for (MessageProcessor processor : listOfProcessor) {
			processor.process(textMessage);
		}
		
		if (mapOfProcessor.containsKey(messageNo)) {
			final List<MessageProcessor> listOfNumberedProcessor = mapOfProcessor.get(messageNo);
			for (final MessageProcessor numberedProcessor : listOfNumberedProcessor) {
				Runnable notify = new Runnable() {
					@Override
					public void run() {
						numberedProcessor.process(textMessage);
					}
				};
				executorService.execute(notify);
			}
		}
	}
	
	// 是否排除验证用户名，IP？临时增加处理载波机消息
	private boolean excludeAll(int messageNo) {
		List<Integer> excludes = new ArrayList<Integer>();
		excludes.add(MessageNoConstants.CARRIERTESTREP);
		excludes.add(MessageNoConstants.CARRIERMONITORREP);
		excludes.add(MessageNoConstants.CARRIERWAVEBANDCONFIGREP);
		excludes.add(MessageNoConstants.CARRIERWAVEBANDQUERYREP);
		excludes.add(MessageNoConstants.CARRIERROUTEQUERYREP);
		excludes.add(MessageNoConstants.CARRIERROUTECONFIGREP);
		excludes.add(MessageNoConstants.CARRIERMARKINGREP);
		excludes.add(MessageNoConstants.CARRIERROUTEFINISH);
		excludes.add(MessageNoConstants.CARRIERROUTECLEARREP);
		excludes.add(MessageNoConstants.CARRIERPORTQUERYREP);
		excludes.add(MessageNoConstants.CARRIERPORTCONFIGREP);
		excludes.add(MessageNoConstants.CARRIERSYSTEMUPGRADEREP);
		excludes.add(MessageNoConstants.GPRSMESSAGE);
		excludes.add(MessageNoConstants.PARMREP);
		excludes.add(MessageNoConstants.FEP_STATUSTYPE);
		excludes.add(MessageNoConstants.PINGRES);
		excludes.add(MessageNoConstants.WARNING);
		excludes.add(MessageNoConstants.WARNINGLINK);
		excludes.add(MessageNoConstants.WARNINGNODE);
		excludes.add(MessageNoConstants.SWITCHUSERUPDATE);
		excludes.add(MessageNoConstants.SWITCHUSERADD);
		
		return excludes.contains(messageNo);
	}
	
	// 是否排除验证用户名，IP？临时增加处理载波机消息
	private boolean excludeAddress(int messageNo) {
		List<Integer> excludes = new ArrayList<Integer>();
		
		return excludes.contains(messageNo);
	}
	
	protected void notifyObjectMessage(final ObjectMessage objectMessage) {
		int messageNo = 0;
		try {
			messageNo = objectMessage.getIntProperty(Constants.MESSAGETYPE);
			String address = ClientUtils.getLocalAddress();
			String to = objectMessage.getStringProperty(Constants.MESSAGETO);
			String clientAddress = objectMessage.getStringProperty(Constants.CLIENTIP);
			String messageName = messageNoMap.get(messageNo);
			LOG.debug(String.format("收到消息%s[%s:%s][%s:%s]", objectMessage.getJMSDestination(), messageNo, messageName, to, clientAddress));
			if (excludeAll(messageNo)) {
				//
			} else if (excludeAddress(messageNo) &&
					clientModel.getCurrentUser().getUserName().equals(to)) {
				//
			} else if (address.equals(clientAddress) && 
					clientModel.getCurrentUser().getUserName().equals(to)) {
			} else {
				return;
			}
		} catch (JMSException e) {
			LOG.error("getStringProperty error", e);
		}
		
		for (MessageProcessor processor : listOfProcessor) {
			processor.process(objectMessage);
		}
		
		if (mapOfProcessor.containsKey(messageNo)) {
			final List<MessageProcessor> listOfNumberedProcessor = mapOfProcessor.get(messageNo);
			for (final MessageProcessor numberedProcessor : listOfNumberedProcessor) {
				Runnable notify = new Runnable() {
					@Override
					public void run() {
						numberedProcessor.process(objectMessage);
					}
				};
				executorService.execute(notify);
			}
		}
	}
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	private final ExecutorService executorService;
	private final List<MessageProcessor> listOfProcessor;
	private final Map<Integer, List<MessageProcessor>> mapOfProcessor;
	private final Map<Integer, String> messageNoMap;
	private static final Logger LOG = LoggerFactory.getLogger(MessageDispatcher.class);
	public static final String ID = "messageDispatcher";
}