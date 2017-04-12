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
	private boolean isProcessoring = false;//True为已经接收到消息，正在处理中...
	private boolean isProcessorEnd = false;//True为消息处理完毕，结束整个流程...
	private int fepNo = 1;
	
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
	
	public MessageOfRefreshAndSynchronizeProcessorStrategy() {
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
	}
	
	private MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
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
	
	private MessageProcessorAdapter messageSingleFinishProcessor = new MessageProcessorAdapter(){//单个同步(刷新)完成
		@Override
		public void process(TextMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						String result = message.getStringProperty(Constants.MESSAGERES);
						if (null != result){
							if (result.equals("S")){//表明是成功了
								showSingleFinishMessage(message.getText());
							}
							else{//失败
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
	
	private MessageProcessorAdapter messageAllFinishProcessor = new MessageProcessorAdapter(){//全部同步(刷新)完成
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
							showSingleFinishMessage("前置机" + fepCode + message.getText());
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
		setInitializeStatus();//设置初始态
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
		if(isRefresh){//拓扑发现
			messageTimeOutThread = new MessageTimeOutThread(timeOut,this);
		}else{//同步
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
