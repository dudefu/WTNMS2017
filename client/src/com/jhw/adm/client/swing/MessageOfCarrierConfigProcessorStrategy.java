package com.jhw.adm.client.swing;

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
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.client.views.carrier.CarrierConfigurePortView;
import com.jhw.adm.client.views.carrier.CarrierConfigureRouteView;
import com.jhw.adm.client.views.carrier.CarrierConfigureWaveBandView;
import com.jhw.adm.client.views.carrier.CarrierInfoView;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.system.TimeConfig;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

public class MessageOfCarrierConfigProcessorStrategy implements MessageProcessorStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(MessageOfCarrierConfigProcessorStrategy.class);
	private MessageDisplayDialog messageDisplayDialog;
	private final RemoteServer remoteServer;
	private final MessageDispatcher messageDispatcher;
	private ViewPart viewPart;
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
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
		@Override
		public void process(TextMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						setLabelIcon("F");
						showErrorMessage(message.getText());
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfSaveProcessorStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private final MessageProcessorAdapter messageCarrierOnlineProcessor = new MessageProcessorAdapter(){//载波机状态
		@Override
		public void process(TextMessage message) {
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						String result = message.getStringProperty(Constants.MESSAGERES);
						setLabelIcon(result);
						showNormalMessage(result);
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfSaveProcessorStrategy.setStopMessage() error:{}", e);
			}
		}
	};
	
	private final MessageProcessorAdapter messageConfigureProcessor = new MessageProcessorAdapter(){//参数配置
		@Override
		public void process(TextMessage textMessage){
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						String result = textMessage.getStringProperty(Constants.MESSAGERES);
						showNormalMessage(result);
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfCarrierProcessorStrategy.process() Configure failure{}:", e);
			}
		}
	};
	
	private final MessageProcessorAdapter messageConfigMarkingProcessor = new MessageProcessorAdapter(){//向上同步
		@Override
		public void process(ObjectMessage objectMessage){
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						Object messageObject = objectMessage.getObject();
						if (messageObject instanceof CarrierEntity) {
							CarrierEntity carrier = (CarrierEntity)messageObject;
							fillContents(carrier);
							showNormalMessage("S");
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfCarrierProcessorStrategy.process() Port-Query failure{}:", e);
			}
		}
	};
	
	private final MessageProcessorAdapter messageRouteQueryProcessor = new MessageProcessorAdapter(){//路由向上同步
		@Override
		public void process(ObjectMessage objectMessage){
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						Object messageObject = objectMessage.getObject();
						if (messageObject instanceof CarrierEntity) {
							CarrierEntity carrier = (CarrierEntity)messageObject;
							fillContents(carrier);
							showNormalMessage("S");
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfCarrierProcessorStrategy.process() Port-Query failure{}:", e);
			}
		}
	};
	
	private final MessageProcessorAdapter messagePortQueryProcessor = new MessageProcessorAdapter(){//端口向上同步
		@Override
		public void process(ObjectMessage objectMessage){
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						Object messageObject = objectMessage.getObject();
						if (messageObject instanceof CarrierEntity) {
							CarrierEntity carrier = (CarrierEntity)messageObject;
							fillContents(carrier);
							showNormalMessage("S");
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfCarrierProcessorStrategy.process() Port-Query failure{}:", e);
			}
		}
	};
	
	private final MessageProcessorAdapter messageWaveBoundProcessor = new MessageProcessorAdapter(){//波段配置
		@Override
		public void process(TextMessage textMessage){
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						String result = textMessage.getStringProperty(Constants.MESSAGERES);
						showNormalMessage(result);
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfCarrierProcessorStrategy.process() WaveBound-Save failure{}:", e);
			}
		}
		@Override
		public void process(ObjectMessage objectMessage){
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						Object messageObject = objectMessage.getObject();
						if (messageObject instanceof CarrierEntity) {
							CarrierEntity carrier = (CarrierEntity)messageObject;
							fillContents(carrier);
							showNormalMessage("S");
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfCarrierProcessorStrategy.process() WaveBound-Query failure{}:", e);
			}
		}
	};
	
	private final MessageProcessorAdapter messageRouteSynchronizeProcessor = new MessageProcessorAdapter(){//载波机同步
		@Override
		public void process(TextMessage textMessage){
			try {
				if(!isProcessorEnd){
					if(!isProcessoring){
						setProcessoringStatus();
						String message = textMessage.getText(); 
					}
				}
			} catch (JMSException e) {
				LOG.error("MessageOfCarrierProcessorStrategy.process() WaveBound-Save failure{}:", e);
			}
		}
	};
	
//	private MessageProcessorAdapter messageCleanRouteProcessor = new MessageProcessorAdapter(){//清空路由
//		@Override
//		public void process(TextMessage textMessage){
//			try {
//				if(!isProcessorEnd){
//					if(!isProcessoring){
//						setProcessoringStatus();
//						String result = textMessage.getStringProperty(Constants.MESSAGERES);
//						showNormalMessage(result);
//					}
//				}
//			} catch (JMSException e) {
//				LOG.error("MessageOfCarrierProcessorStrategy.process() Route-Append failure{}:", e);
//			}
//		}
//	};
	
	public void fillContents(CarrierEntity entity){
		if(viewPart instanceof CarrierConfigurePortView){
			((CarrierConfigurePortView)viewPart).fillContents(entity);
		}else if(viewPart instanceof CarrierConfigureRouteView){
			((CarrierConfigureRouteView)viewPart).updateCarrierEntity(entity);
		}
		else if(viewPart instanceof CarrierConfigureWaveBandView){
			((CarrierConfigureWaveBandView)viewPart).fillContents(entity);
		}
		else if(viewPart instanceof CarrierInfoView){
//			((CarrierInfoView)viewPart).fillContents(entity);
		}
	}
	
	public void setLabelIcon(String result){
		if(viewPart instanceof CarrierInfoView){
			((CarrierInfoView)viewPart).setStatusIcon(result);
		}
	}
	
	public MessageOfCarrierConfigProcessorStrategy(){
		remoteServer = ClientUtils.getSpringBean(RemoteServer.class, RemoteServer.ID);
		messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.class, MessageDispatcher.ID);
	}
	
	private MessageTimeOutThread messageTimeOutThread;
	public void showInitializeDialog(String operatorMessage,final ViewPart viewPart){
		this.viewPart = viewPart;
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
	
	public void showNormalMessage(String result){
		if(isProcessorEnd){
			return;
		}
		setProcessorEndStatus();
		if(null != messageTimeOutThread){
			messageTimeOutThread.stopThread();			
		}
		
		if("S".equals(result)){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if(null != messageDisplayDialog){
						messageDisplayDialog.showNormalMessage(operatorMessage + "成功");
					}
				}
			});
		}else if("F".equals(result)){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if(null != messageDisplayDialog){
						messageDisplayDialog.showNormalMessage(operatorMessage + "失败");
					}
				}
			});
		}
	}
	
	public void showErrorMessage(final String errorMessage){
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
		}catch(Exception e){
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
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERPORTCONFIGREP, messageConfigureProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERPORTQUERYREP, messagePortQueryProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERMONITORREP, messageCarrierOnlineProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERROUTECONFIGREP, messageConfigureProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERROUTEQUERYREP, messageRouteQueryProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERROUTECLEARREP, messageConfigureProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERWAVEBANDQUERYREP, messageWaveBoundProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERWAVEBANDCONFIGREP, messageWaveBoundProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERROUTECONFIGS, messageRouteSynchronizeProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERMARKINGREP, messageConfigMarkingProcessor);
	}

	@Override
	public void removeProcessor() {
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERPORTCONFIGREP, messageConfigureProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERPORTQUERYREP, messagePortQueryProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERMONITORREP, messageCarrierOnlineProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERROUTECONFIGREP, messageConfigureProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERROUTEQUERYREP, messageRouteQueryProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERROUTECLEARREP, messageConfigureProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERWAVEBANDQUERYREP, messageWaveBoundProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERWAVEBANDCONFIGREP, messageWaveBoundProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERROUTECONFIGS, messageRouteSynchronizeProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.CARRIERMARKINGREP, messageConfigMarkingProcessor);
	}
	@Override
	public void dealTimeOut() {
		// TODO Auto-generated method stub
		
	}
}
