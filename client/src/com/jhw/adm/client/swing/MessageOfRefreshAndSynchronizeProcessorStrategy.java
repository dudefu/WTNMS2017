package com.jhw.adm.client.swing;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;
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
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

public class MessageOfRefreshAndSynchronizeProcessorStrategy implements MessageProcessorStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageOfRefreshAndSynchronizeProcessorStrategy.class);
	private MessageDisplayDialog messageDisplayDialog;
	private RemoteServer remoteServer;
	private MessageDispatcher messageDispatcher;
	public String operatorMessage = "";
	public boolean isRefresh = true;
	private boolean isProcessoring = false;//TrueΪ�Ѿ����յ���Ϣ�����ڴ�����...
	private boolean isProcessorEnd = false;//TrueΪ��Ϣ������ϣ�������������...
	private int fepNo = 1;
	
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
	
	public MessageOfRefreshAndSynchronizeProcessorStrategy() {
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
	}
	
	private MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//ǰ�û�������
		@Override
		public void process(TextMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						showErrorMessage(message.getText());
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfRefreshAndSynchronizeProcessorStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageProcessorAdapter messageSingleFinishProcessor = new MessageProcessorAdapter(){//����ͬ��(ˢ��)���
		@Override
		public void process(TextMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						String result = message.getStringProperty(Constants.MESSAGERES);
						if (null != result){
							if (result.equals("S")){//�����ǳɹ���
								showSingleFinishMessage(message.getText());
							}
							else{//ʧ��
								showSingleFailMessage(message.getText());
							}
						}
						else{
							showSingleFinishMessage(message.getText());
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfRefreshAndSynchronizeProcessorStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageProcessorAdapter messageAllFinishProcessor = new MessageProcessorAdapter(){//ȫ��ͬ��(ˢ��)���
		@Override
		public void process(TextMessage message) {
			fepNo -= 1;
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						if(fepNo == 0){
							setProcessoringStatus();
							showAllFinishMessage(message.getText());
						}else {
							String fepCode = message.getStringProperty(Constants.MESSAGEFROM);
							showSingleFinishMessage("ǰ�û�" + fepCode + message.getText());
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfRefreshAndSynchronizeProcessorStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageProcessorAdapter messageSynchronizeProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						showErrorMessage(message.getText());
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfRefreshAndSynchronizeProcessorStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageProcessorAdapter messageRefreshProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						showErrorMessage(message.getText());
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfRefreshAndSynchronizeProcessorStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageTimeOutThread messageTimeOutThread;
	public void showInitializeDialog(String operatorMessage,final ViewPart viewPart,boolean isRefresh,int fepNo){
		this.isRefresh = isRefresh;
		this.operatorMessage = operatorMessage;
		this.fepNo = fepNo;
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
		if(isRefresh){//���˷���
			messageTimeOutThread = new MessageTimeOutThread(timeOut,this);
		}else{//ͬ��
			messageTimeOutThread = new MessageTimeOutThread(timeOut,this);			
		}
		messageTimeOutThread.start();
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
	
	public void showSingleFinishMessage(final String singleFinishMessage){
		LOG.info(singleFinishMessage);
		if(isProcessorEnd){
			return;
		}
		
		if(null != messageTimeOutThread){
			messageTimeOutThread.reStartTimer();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(null != messageDisplayDialog){
					messageDisplayDialog.showSingleFinishMessage(singleFinishMessage);
				}
			}
		});
	}
	
	//modifier:wuzhongwei  Date:2010.12.10
	public void showSingleFailMessage(final String singleFinishMessage){
		LOG.info(singleFinishMessage);
		if(isProcessorEnd){
			return;
		}
		
		if(null != messageTimeOutThread){
			messageTimeOutThread.reStartTimer();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(null != messageDisplayDialog){
					messageDisplayDialog.showSingleFailMessage(singleFinishMessage);
				}
			}
		});
	}
	
	public void showAllFinishMessage(final String AllFinishMessage){
		LOG.info(AllFinishMessage);
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
					messageDisplayDialog.showNormalMessage(AllFinishMessage);
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
			if(isRefresh){
				time = MessageConstant.TIMEOUT_TOPOREFRESH;
			}else{
				time = MessageConstant.TIMEOUT_SYN;				
			}
		}else{
			if(isRefresh){
				time = list.get(0).getTuopoMaxTime() * 1000;
				if (0 == time){
					time = MessageConstant.TIMEOUT_TOPOREFRESH;
				}
			}else{
				time = list.get(0).getSynchoizeMaxTime() * 1000;
				if (0 == time){
					time = MessageConstant.TIMEOUT_SYN;
				}
			}
		}
		return time;
	}
	
	@Override
	public void processorMessage() {
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SYNCHONEFINISH, messageSingleFinishProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SYNCHFINISH, messageAllFinishProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.TOPOSEARCHONEFINSH, messageSingleFinishProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.TOPOFINISH, messageAllFinishProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SYNCHORIZING, messageSynchronizeProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.REFRESHING, messageRefreshProcessor);
	}

	@Override
	public void removeProcessor() {
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SYNCHONEFINISH, messageSingleFinishProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SYNCHFINISH, messageAllFinishProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.TOPOSEARCHONEFINSH, messageSingleFinishProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.TOPOFINISH, messageAllFinishProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SYNCHORIZING, messageSynchronizeProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.REFRESHING, messageRefreshProcessor);
	}
	@Override
	public void dealTimeOut() {
		// TODO Auto-generated method stub
		
	}
}
