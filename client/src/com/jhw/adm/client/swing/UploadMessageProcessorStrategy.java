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
 * 设备参数上载处理策略
 */
public class UploadMessageProcessorStrategy implements MessageProcessorStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(UploadMessageProcessorStrategy.class);
	private MessageDisplayDialog messageDisplayDialog;
	private final RemoteServer remoteServer;
	private final MessageDispatcher messageDispatcher;
	public String operatorMessage = "";
	private boolean isProcessoring = false;//True为已经接收到消息，正在处理中...
	private boolean isProcessorEnd = false;//True为消息处理完毕，结束整个流程...
	
	//当得到SINGLESYNCHONEFINISH的返回消息时，判断关闭按钮是否可以关闭。false:不能关闭    true:可以关闭
	private boolean isSingleClose = true; 
	
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
	
	public UploadMessageProcessorStrategy() {
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
	}
	
	/**
	 * 设备参数上载消息完成处理器
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
									showSingleFinishMessage(message.getText());
								} else {//失败
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
	 * 设备参数上载消息完成处理器
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
									showSingleFinishMessage(message.getText());
								} else {//失败
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
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
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
		setInitializeStatus();//设置初始态
		this.isSingleClose = true;
		processorMessage();//添加消息监听
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				messageDisplayDialog.initializeDialog(viewPart);
				messageDisplayDialog.setVisible(true);
			}
		});
		
		//超时判断
		int timeOut = getTimeout();
		messageTimeOutThread = new MessageTimeOutThread(timeOut,this);
		messageTimeOutThread.start();
	}
	
	/**
	 * 当vlan,GH-ring,SNMP的上载时调用。
	 * @param operatorMessage
	 * @param viewPart
	 * @param isSingleClose
	 */
	public void showInitializeDialog(String operatorMessage,final ViewPart viewPart,boolean isSingleClose){
		this.operatorMessage = operatorMessage;
		messageDisplayDialog = new MessageDisplayDialog(this.operatorMessage);
		setInitializeStatus();//设置初始态
		this.isSingleClose = isSingleClose;
		processorMessage();//添加消息监听
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				messageDisplayDialog.initializeDialog(viewPart);
				messageDisplayDialog.setVisible(true);
			}
		});
		
		//超时判断
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
