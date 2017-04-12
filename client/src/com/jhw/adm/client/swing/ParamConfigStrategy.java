package com.jhw.adm.client.swing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
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
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 
 *  该strategy针对只与交换机基本参数配置的操作
 * @author Administrator
 *
 */
public class ParamConfigStrategy implements MessageProcessorStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(ParamConfigStrategy.class);
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
	
	public ParamConfigStrategy(String operatorMessage,JProgressBarModel model) {
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
		this.model = model;
		this.operatorMessage = operatorMessage;
		showInitializeDialog();
	}
	
	private MessageProcessorAdapter messageParamRepProcessor = new MessageProcessorAdapter(){//设备参数操作返回消息
		@Override
		public void process(ObjectMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						ParmRep object = (ParmRep) message.getObject();
						boolean result = object.isSuccess();
						showNormalMessage(result,Constants.SYN_ALL);
					}
				}
			} catch (JMSException e) {
				LOG.error("ParamConfigStrategy.setMessage() error:{}", e);
			}
		}
	};
	
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
				LOG.error("ParamConfigStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	@SuppressWarnings("unchecked")
	private MessageProcessorAdapter messageUserProcessor = new MessageProcessorAdapter(){//交换机用户
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
						showNormalMessage(result, Constants.SYN_ALL);
					}
				}
			} catch (JMSException e) {
				LOG.error("ParamConfigStrategy.setMessage() error:{}", e);
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
	
	public void showNormalMessage(boolean isSuccess,final int result){
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
					if(Constants.SYN_ALL == result){
						model.setDetail(operatorMessage + "设备侧和网管侧成功|" + model.NORMAL);
					}else if(Constants.SYN_SERVER == result){
						model.setDetail(operatorMessage + "网管侧成功|" + model.NORMAL);
					}
					model.setProgress(ONE);
					model.setEnabled(true);
					model.setDetermine(new Boolean(false));
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					
					if(Constants.SYN_ALL == result){
						model.setDetail(operatorMessage + "设备侧失败|" + model.FAILURE);
					}else if(Constants.SYN_SERVER == result){
						model.setDetail(operatorMessage + "网管侧失败|" + model.FAILURE);
					}
					model.setProgress(ONE);
					model.setEnabled(true);
					model.setDetermine(new Boolean(false));
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
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageParamRepProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SWITCHUSERUPDATE, messageUserProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.SWITCHUSERADD, messageUserProcessor);
	}

	@Override
	public void removeProcessor() {
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageParamRepProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SWITCHUSERUPDATE, messageUserProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.SWITCHUSERADD, messageUserProcessor);
	}
	@Override
	public void dealTimeOut() {
		this.showErrorMessage(this.operatorMessage + "超时");
	}
}
