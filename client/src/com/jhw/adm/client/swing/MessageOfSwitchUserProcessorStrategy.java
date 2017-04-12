package com.jhw.adm.client.swing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.MessageConstant;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.system.TimeConfig;
import com.jhw.adm.server.entity.util.MessageNoConstants;

public class MessageOfSwitchUserProcessorStrategy  implements MessageProcessorStrategy{
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageOfSwitchUserProcessorStrategy.class);
	private MessageDisplayDialog messageDisplayDialog;
	private RemoteServer remoteServer;
	private MessageDispatcher messageDispatcher;
//	private SwitcherUserManageView view ;
	public String operatorMessage = "";
	private boolean isProcessoring = false;//TrueΪ�Ѿ����յ���Ϣ�����ڴ�����...
	private boolean isProcessorEnd = false;//TrueΪ��Ϣ������ϣ�������������...
	
	public void setInitializeStatus(){//��ʼ̬
		LOG.info("��ʼ̬");
		this.isProcessoring = false;
		this.isProcessorEnd = false;
	}
	public synchronized void setProcessoringStatus(){//����̬
		LOG.info("����̬");
		this.isProcessoring = true;
		this.isProcessorEnd = false;
	}
	private synchronized void setProcessorEndStatus(){//����̬
		LOG.info("����̬");
		this.isProcessoring = false;
		this.isProcessorEnd = true;
		removeProcessor();
	}
	
	public MessageOfSwitchUserProcessorStrategy() {
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
//		this.view = view;
	}
	
	@SuppressWarnings("unchecked")
	private MessageProcessorAdapter messageUserUpdateProcessor = new MessageProcessorAdapter(){//�������û������޸�
		@Override
		public void process(ObjectMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						boolean result = false;
						HashMap hashMap = null;
						if (message != null && message.getObject() != null){
							hashMap = (HashMap) message.getObject();
							Iterator iterator = hashMap.values().iterator();
							while(iterator.hasNext()){
								boolean bool = (Boolean)iterator.next();
								result = (result || bool);
							}
						}
						showNormalMessage(result);
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfSwitchUserProcessorStrategy.setMessage() error:{}", e);
			}
		}
	};
	
	@SuppressWarnings("unchecked")
	private MessageProcessorAdapter messageUserAddProcessor = new MessageProcessorAdapter(){//�����������û�
		@Override
		public void process(ObjectMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						boolean result = false;
						HashMap hashMap = null;
						if (message != null && message.getObject() != null){
							hashMap = (HashMap) message.getObject();
							Iterator iterator = hashMap.values().iterator();
							while(iterator.hasNext()){
								boolean bool = (Boolean)iterator.next();
								result = (result || bool);
							}
						}
						showNormalMessage(result);
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfSwitchUserProcessorStrategy.setMessage() error:{}", e);
			}
		}
	};
	
	private MessageTimeOutThread messageTimeOutThread;
	public void showInitializeDialog(String operatorMessage,final ViewPart viewPart){
		this.operatorMessage = operatorMessage;
		messageDisplayDialog = new MessageDisplayDialog(this.operatorMessage);
		setInitializeStatus();//���ó�ʼ̬
		processorMessage();//�����Ϣ����
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				messageDisplayDialog.initializeDialog(viewPart);
				messageDisplayDialog.setVisible(true);
			}
		});
		
		//��ʱ�ж�
		int timeOut = getTimeout();
		messageTimeOutThread = new MessageTimeOutThread(timeOut,this);
		messageTimeOutThread.start();
	}
	
	public void showNormalMessage(boolean isSuccess){
		if(isProcessorEnd){
			return;
		}
		setProcessorEndStatus();
		if(null != messageTimeOutThread){
			messageTimeOutThread.stopThread();			
		}
		if (isSuccess) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if(null != messageDisplayDialog){
						messageDisplayDialog.showNormalMessage(operatorMessage + "�豸������ܲ�ɹ�");
					}
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if(null != messageDisplayDialog){
						messageDisplayDialog.showNormalMessage(operatorMessage + "�豸��ʧ��");
					}
				}
			});
		}
	}
	
	public void showErrorMessage(final String errorMessage) {
		if(isProcessorEnd){
			return;
		}
		setProcessorEndStatus();
		if(null != messageTimeOutThread){
			messageTimeOutThread.stopThread();			
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(null != messageDisplayDialog){
					messageDisplayDialog.showErrorMessage(errorMessage);
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private int getTimeout(){
		int time = 0;
		List<TimeConfig> list = null;
		try{
			list = (List<TimeConfig>)remoteServer.getService().findAll(TimeConfig.class);
		}
		catch(Exception e){
			list = null;
		}
		
		if (null == list || list.size() < 1){
			time = MessageConstant.TIMEOUT;
		}else{
			time = list.get(0).getParmConfigMaxTime() * 1000;
			if (0 == time){
				time = MessageConstant.TIMEOUT;
			}
		}
		return time;
	}
	
	@Override
	public void processorMessage() {
		messageDispatcher.addProcessor(MessageNoConstants.SWITCHUSERUPDATE, messageUserUpdateProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SWITCHUSERADD, messageUserAddProcessor);
	}

	@Override
	public void removeProcessor() {
		messageDispatcher.removeProcessor(MessageNoConstants.SWITCHUSERUPDATE, messageUserUpdateProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SWITCHUSERADD, messageUserAddProcessor);
	}
	@Override
	public void dealTimeOut() {
		// TODO Auto-generated method stub
		
	}


}
