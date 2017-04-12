package com.jhw.adm.client.swing;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import com.jhw.adm.server.entity.system.TimeConfig;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

public class TopologyRefreshStrategy implements MessageProcessorStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(TopologyRefreshStrategy.class);
	private static final int ONE = 1;
	private JProgressBarModel model;
	private MessageTimeOutThread messageTimeOutThread;
	public String operatorMessage = "";
	private int fepNo = 1;
	private RemoteServer remoteServer;
	private MessageDispatcher messageDispatcher;
	private boolean isProcessoring = false;//TrueΪ�Ѿ����յ���Ϣ�����ڴ�����...
	private boolean isProcessorEnd = false;//TrueΪ��Ϣ������ϣ�������������...
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
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
	
	public TopologyRefreshStrategy(String operatorMessage,JProgressBarModel model,int fepNo){
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
		this.model = model;
		this.fepNo = fepNo;
		this.operatorMessage = operatorMessage;
		showInitializeDialog();
	}
	
	private void showInitializeDialog(){
		setInitializeStatus();//���ó�ʼ̬
		processorMessage();//������Ϣ����
		
		//��ʱ�ж�
		int timeOut = getTimeout();
		messageTimeOutThread = new MessageTimeOutThread(timeOut,this);
		messageTimeOutThread.start();
	}
	
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
				LOG.error("TopologyRefreshStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageProcessorAdapter messageAllFinishProcessor = new MessageProcessorAdapter(){//ȫ���������
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
				LOG.error("TopologyRefreshStrategy.setStopMessage() error:{}", e);
			}
		}
	};

	private MessageProcessorAdapter messageSingleFinishProcessor = new MessageProcessorAdapter(){//�����������
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
						}else{
							showSingleFinishMessage(message.getText());
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("TopologyRefreshStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
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
				LOG.error("TopologyRefreshStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
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
				model.setDetail(AllFinishMessage + "|" + model.NORMAL);
				model.setProgress(ONE);
				model.setEnabled(true);
				model.setDetermine(new Boolean(false));
			}
		});
	}
	
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
				model.setDetail(singleFinishMessage + "|" + model.FAILURE);
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
				model.setDetail(singleFinishMessage + "|" + model.NORMAL);
			}
		});
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
				model.setDetail(errorMessage + "|" + model.FAILURE);
				model.setProgress(ONE);
				model.setEnabled(true);
				model.setDetermine(new Boolean(false));				
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
			time = MessageConstant.TIMEOUT_TOPOREFRESH;
		}else{
			time = list.get(0).getTuopoMaxTime() * 1000;
			if (0 == time){
				time = MessageConstant.TIMEOUT_TOPOREFRESH;
			}
		}
		return time;
	}
	
	@Override
	public void processorMessage() {
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.TOPOSEARCHONEFINSH, messageSingleFinishProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.TOPOFINISH, messageAllFinishProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.REFRESHING, messageRefreshProcessor);
	}

	@Override
	public void removeProcessor() {
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.TOPOSEARCHONEFINSH, messageSingleFinishProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.TOPOFINISH, messageAllFinishProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.REFRESHING, messageRefreshProcessor);
	}
	@Override
	public void dealTimeOut() {
		final Runnable setMessageThread = new Runnable() {
			
			@Override
			public void run() {
				showErrorMessage(operatorMessage + "��ʱ");
			}
		};
		final Runnable sendMessageToServerThread = new Runnable() {
			
			@Override
			public void run() {
				remoteServer.getDataCacheService().resettingTopo();
			}
		};
		executorService.execute(setMessageThread);
		executorService.execute(sendMessageToServerThread);
	}

}