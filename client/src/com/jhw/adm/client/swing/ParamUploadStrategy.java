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
 * 该strategy针对参数上载的操作
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
	private boolean isProcessoring = false;//True为已经接收到消息，正在处理中...
	private boolean isProcessorEnd = false;//True为消息处理完毕，结束整个流程...
	
	//当得到SINGLESYNCHONEFINISH的返回消息时，判断关闭按钮是否可以关闭。false:不能关闭    true:可以关闭
	private boolean isSingleClose = true; //True:单个设备；False：多个设备
	
	public void setInitializeStatus(){//初始态
		LOG.info("初始态");
		this.isProcessoring = false;
		this.isProcessorEnd = false;
	}
	public synchronized void setProcessoringStatus(){//处理态
		LOG.info("处理态");
		this.isProcessoring = true;
		this.isProcessorEnd = false;
	}
	private synchronized void setProcessorEndStatus(){//结束态
		LOG.info("结束态");
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
		setInitializeStatus();//设置初始态
		processorMessage();//添加消息监听
		
		//超时判断
		int timeOut = getTimeout();
		messageTimeOutThread = new MessageTimeOutThread(timeOut,this);
		messageTimeOutThread.start();
	}
	
	/**
	 * 单个设备参数上载消息完成处理器
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
								if (result.equals("S")) {//表明是成功了
									showSuccessMessage(message.getText());
								} else {//失败
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
	 * 多个设备参数上载消息完成处理器
	 * 适用于Vlan,GH-Ring,SNMP的上载操作
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
								if (result.equals("S")) {//表明是成功了
									showSuccessMessage(message.getText());
								} else {//失败
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
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
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
		this.showFailureMessage(this.operatorMessage + "超时");
	}
}
