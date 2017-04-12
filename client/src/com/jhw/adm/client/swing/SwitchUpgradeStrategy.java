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
import com.jhw.adm.server.entity.util.MessageNoConstants;

public class SwitchUpgradeStrategy implements MessageProcessorStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(SwitchUpgradeStrategy.class);
	private static final int ONE = 1;
	private JProgressBarModel model;
	private RemoteServer remoteServer;
	private MessageDispatcher messageDispatcher;
	public String operatorMessage = "";
	private boolean isProcessoring = false;//True为已经接收到消息，正在处理中...
	private boolean isProcessorEnd = false;//True为消息处理完毕，结束整个流程...
	
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
	
	public SwitchUpgradeStrategy(String operatorMessage,JProgressBarModel model) {
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
		this.model = model;
		this.operatorMessage = operatorMessage;
		showInitializeDialog();
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
				LOG.error("SwitchUpgradeStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageProcessorAdapter messageSingleFinishProcessor = new MessageProcessorAdapter(){//单个升级完成   or 失败
		@Override
		public void process(TextMessage message) {
			LOG.info("Single over --- Message");
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						showSingleFinishMessage(message.getText());
					}
				}
			} catch (JMSException e) {
				LOG.error("SwitchUpgradeStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageProcessorAdapter messageAllFinishProcessor = new MessageProcessorAdapter(){//全部升级完成
		@Override
		public void process(TextMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						showAllFinishMessage(message.getText());
					}
				}
			} catch (JMSException e) {
				LOG.error("SwitchUpgradeStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageProcessorAdapter messageUpgradingProcessor = new MessageProcessorAdapter(){//正在升级,不允许同时升级
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
				LOG.error("SwitchUpgradeStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private MessageTimeOutThread messageTimeOutThread;
	private void showInitializeDialog(){
		setInitializeStatus();//设置初始态
		processorMessage();//添加消息监听
		
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
				
				model.setDetail(errorMessage + "|" + model.FAILURE);
				model.setProgress(ONE);
				model.setEnabled(true);
				model.setDetermine(new Boolean(false));				
			}
		});
	}
	
	public void showSingleFinishMessage(final String singleFinishMessage){
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
	
	public void showAllFinishMessage(final String AllFinishMessage){
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
	
	@SuppressWarnings("unchecked")
	private int getTimeout(){
		int time = 0;
		List<TimeConfig> list = null;
		try{
			list = (List<TimeConfig>)remoteServer.getService().findAll(TimeConfig.class);
		}catch(Exception e){
			list = null;
		}
		
		if (null == list || list.size() < 1){
			time = MessageConstant.TIMEOUT_UPGRADE;
		}else{
			time = list.get(0).getTuopoMaxTime() * 1000;
			if (0 == time){
				time = MessageConstant.TIMEOUT_UPGRADE;
			}
		}
		return time;
	}
	
	@Override
	public void processorMessage() {
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SWITCHER_UPGRADEING, messageUpgradingProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SWITCHUPGRADEFAIL, messageSingleFinishProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.ONE_SWITCHER_UPGRADEREP, messageSingleFinishProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.ALL_SWITCHER_UPGRADEREP, messageAllFinishProcessor);
	}

	@Override
	public void removeProcessor() {
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SWITCHER_UPGRADEING, messageUpgradingProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SWITCHUPGRADEFAIL, messageSingleFinishProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.ONE_SWITCHER_UPGRADEREP, messageSingleFinishProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.ALL_SWITCHER_UPGRADEREP, messageAllFinishProcessor);
	}
	@Override
	public void dealTimeOut() {
		this.showErrorMessage(this.operatorMessage + "超时");
	}
}
