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
import com.jhw.adm.server.entity.system.TimeConfig;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 
 * ��strategy��Բ������صĲ���
 * @author Administrator
 *
 */
public class ParamUploadStrategy implements MessageProcessorStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(ParamUploadStrategy.class);
	private static final int ONE = 1;
	private final RemoteServer remoteServer;
	private final MessageDispatcher messageDispatcher;
	private String operatorMessage = "";
	private JProgressBarModel model;
	private boolean isProcessoring = false;//TrueΪ�Ѿ����յ���Ϣ�����ڴ�����...
	private boolean isProcessorEnd = false;//TrueΪ��Ϣ������ϣ�������������...
	
	//���õ�SINGLESYNCHONEFINISH�ķ�����Ϣʱ���жϹرհ�ť�Ƿ���Թرա�false:���ܹر�    true:���Թر�
	private boolean isSingleClose = true; //True:�����豸��False������豸
	
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
	
	public ParamUploadStrategy(String operatorMessage,JProgressBarModel model,boolean isSingleClose) {
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
		this.model = model;
		this.isSingleClose = isSingleClose;
		this.operatorMessage = operatorMessage;
		showInitializeDialog();
	}
	
	private MessageTimeOutThread messageTimeOutThread;
	public void showInitializeDialog(){
		setInitializeStatus();//���ó�ʼ̬
		processorMessage();//�����Ϣ����
		
		//��ʱ�ж�
		int timeOut = getTimeout();
		messageTimeOutThread = new MessageTimeOutThread(timeOut,this);
		messageTimeOutThread.start();
	}
	
	/**
	 * �����豸����������Ϣ��ɴ�����
	 */
	private final MessageProcessorAdapter messageParamRepProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				if (isSingleClose){
					if(!isProcessorEnd){
						if(!isProcessoring){
							String result = message.getStringProperty(Constants.MESSAGERES);
							if (null != result) {
								if (result.equals("S")) {//�����ǳɹ���
									showSuccessMessage(message.getText());
								} else {//ʧ��
									showFailureMessage(message.getText());
								}
							}else{
								showSuccessMessage(message.getText());			
							}
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("ParamUploadStrategy.setMessage() error:{}", e);
			}
		}
	};
	
	/**
	 * ����豸����������Ϣ��ɴ�����
	 * ������Vlan,GH-Ring,SNMP�����ز���
	 */
	private final MessageProcessorAdapter messageAllParamRepProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				if (!isSingleClose){
					if(!isProcessorEnd){
						if(!isProcessoring){
							String result = message.getStringProperty(Constants.MESSAGERES);
							if (null != result) {
								if (result.equals("S")) {//�����ǳɹ���
									showSuccessMessage(message.getText());
								} else {//ʧ��
									showFailureMessage(message.getText());
								}
							}else{
								showSuccessMessage(message.getText());
							}
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("ParamUploadStrategy.setMessage() error:{}", e);
			}
		}
	};
	
	public void showSuccessMessage(final String successMessage){
		LOG.info(successMessage);
		if(isProcessorEnd){
			return;
		}
		setProcessorEndStatus();
		this.isSingleClose = true;
		if(null != messageTimeOutThread){
			messageTimeOutThread.stopThread();
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			LOG.error("ParamUploadStrategy.time out error");
			Thread.currentThread().interrupt();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				model.setDetail(successMessage + "|" + model.NORMAL);
				model.setProgress(ONE);
				model.setEnabled(true);
				model.setDetermine(new Boolean(false));
			}
		});
	}
	
	public void showFailureMessage(final String failureMessage){
		LOG.info(failureMessage);
		if(isProcessorEnd){
			return;
		}
		setProcessorEndStatus();
		this.isSingleClose = true;
		if(null != messageTimeOutThread){
			messageTimeOutThread.stopThread();
		}

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			LOG.error("ParamUploadStrategy.time out error");
			Thread.currentThread().interrupt();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				model.setDetail(failureMessage + "|" + model.FAILURE);
				model.setProgress(ONE);
				model.setEnabled(true);
				model.setDetermine(new Boolean(false));
			}
		});
	}
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//ǰ�û�������
		@Override
		public void process(TextMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						showFailureMessage(message.getText());
					}
				}
			} catch (JMSException e) {
				LOG.error("ParamUploadStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	
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
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageParamRepProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SYNCHFINISH, messageAllParamRepProcessor);
	}

	@Override
	public void removeProcessor() {
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageParamRepProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SYNCHFINISH, messageAllParamRepProcessor);
	}
	
	@Override
	public void dealTimeOut() {
		this.showFailureMessage(this.operatorMessage + "��ʱ");
	}
}
