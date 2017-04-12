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

/**
 * �豸�������ش������
 */
public class UploadMessageProcessorStrategy implements MessageProcessorStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(UploadMessageProcessorStrategy.class);
	private MessageDisplayDialog messageDisplayDialog;
	private final RemoteServer remoteServer;
	private final MessageDispatcher messageDispatcher;
	public String operatorMessage = "";
	private boolean isProcessoring = false;//TrueΪ�Ѿ����յ���Ϣ�����ڴ�����...
	private boolean isProcessorEnd = false;//TrueΪ��Ϣ������ϣ�������������...
	
	//���õ�SINGLESYNCHONEFINISH�ķ�����Ϣʱ���жϹرհ�ť�Ƿ���Թرա�false:���ܹر�    true:���Թر�
	private boolean isSingleClose = true; 
	
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
	
	public UploadMessageProcessorStrategy() {
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
	}
	
	/**
	 * �豸����������Ϣ��ɴ�����
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
									showSingleFinishMessage(message.getText());
								} else {//ʧ��
									showSingleFailMessage(message.getText());
								}
							}
							else{
								showSingleFinishMessage(message.getText());			
							}
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("UploadMessageProcessorStrategy.setMessage() error:{}", e);
			}
		}
	};
	
	/**
	 * �豸����������Ϣ��ɴ�����
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
									showSingleFinishMessage(message.getText());
								} else {//ʧ��
									showSingleFailMessage(message.getText());
								}
							}
							else{
								showSingleFinishMessage(message.getText());
							}
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("UploadMessageProcessorStrategy.setMessage() error:{}", e);
			}
		}
	};
	
	public void showSingleFinishMessage(final String singleFinishMessage){
		LOG.info(singleFinishMessage);
		if(isProcessorEnd){
			return;
		}
		setProcessorEndStatus();
		this.isSingleClose = true;
		if(null != messageTimeOutThread){
			messageTimeOutThread.stopThread();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(null != messageDisplayDialog){
					messageDisplayDialog.showNormalMessage(singleFinishMessage);
				}
			}
		});
	}
	
	public void showSingleFailMessage(final String singleFinishMessage){
		LOG.info(singleFinishMessage);
		if(isProcessorEnd){
			return;
		}
		setProcessorEndStatus();
		this.isSingleClose = true;
		if(null != messageTimeOutThread){
			messageTimeOutThread.stopThread();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(null != messageDisplayDialog){
					messageDisplayDialog.showErrorMessage(singleFinishMessage);
				}
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
						showErrorMessage(message.getText());
					}
				}
			} catch (JMSException e) {
				LOG.error("UploadMessageProcessorStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageTimeOutThread messageTimeOutThread;
	public void showInitializeDialog(String operatorMessage,final ViewPart viewPart){
		this.operatorMessage = operatorMessage;
		messageDisplayDialog = new MessageDisplayDialog(this.operatorMessage);
		setInitializeStatus();//���ó�ʼ̬
		this.isSingleClose = true;
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
	
	/**
	 * ��vlan,GH-ring,SNMP������ʱ���á�
	 * @param operatorMessage
	 * @param viewPart
	 * @param isSingleClose
	 */
	public void showInitializeDialog(String operatorMessage,final ViewPart viewPart,boolean isSingleClose){
		this.operatorMessage = operatorMessage;
		messageDisplayDialog = new MessageDisplayDialog(this.operatorMessage);
		setInitializeStatus();//���ó�ʼ̬
		this.isSingleClose = isSingleClose;
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
		// TODO Auto-generated method stub
		
	}
}
