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
	
	public MessageOfSwitchUserProcessorStrategy() {
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
		remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
//		this.view = view;
	}
	
	@SuppressWarnings("unchecked")
	private MessageProcessorAdapter messageUserUpdateProcessor = new MessageProcessorAdapter(){//交换机用户密码修改
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
	private MessageProcessorAdapter messageUserAddProcessor = new MessageProcessorAdapter(){//交换机增加用户
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
						messageDisplayDialog.showNormalMessage(operatorMessage + "设备侧和网管侧成功");
					}
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if(null != messageDisplayDialog){
						messageDisplayDialog.showNormalMessage(operatorMessage + "设备侧失败");
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
