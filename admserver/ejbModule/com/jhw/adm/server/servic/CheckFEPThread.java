/**
 * 
 */
package com.jhw.adm.server.servic;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.apache.log4j.Logger;

import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.interfaces.CheckFEPThreadLocal;
import com.jhw.adm.server.interfaces.CommonServiceBeanLocal;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.FepWarningLocal;
import com.jhw.adm.server.interfaces.NMSServiceLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;
import com.jhw.adm.server.util.CacheDatas;
import com.jhw.adm.server.util.Tools;

/**
 * @author 左军勇
 * @时间 2010-7-30
 */
@Stateless
@Local(CheckFEPThreadLocal.class)
public class CheckFEPThread implements CheckFEPThreadLocal {
	RunAction runaction = null;
	private Logger logger = Logger.getLogger(CheckFEPThread.class.getName());
	@EJB
	private DataCacheLocal cacheLocal;
	@EJB
	private NMSServiceLocal nmsServiceLocal;
	@EJB
	private CommonServiceBeanLocal commonServiceBeanLocal;
	
	@EJB
	private SMTCServiceLocal smtcService;
	
	@EJB
	private FepWarningLocal fepWarningLocal;

	public void startFEPTimer(long milliseconds) {
		if (runaction != null) {
			runaction.setLoop(false);
		}
		runaction = new RunAction(commonServiceBeanLocal);
		runaction.setLoop(true);
		runaction.setMilliseconds(milliseconds);
		runaction.start();
	}

	class RunAction extends Thread {

		public RunAction(CommonServiceBeanLocal commonServiceBeanLocal) {
			this.commonServiceBeanLocal = commonServiceBeanLocal;
			this.setName("定时检查前置状态");
		}

		private CommonServiceBeanLocal commonServiceBeanLocal;
		public boolean loop = true;
		long milliseconds;

		public long getMilliseconds() {
			return milliseconds;
		}

		public void setMilliseconds(long milliseconds) {
			this.milliseconds = milliseconds;
		}

		public void setLoop(boolean loop) {
			this.loop = loop;
		}

		public CommonServiceBeanLocal getCommonServiceBeanLocal() {
			return commonServiceBeanLocal;
		}

		public void setCommonServiceBeanLocal(
				CommonServiceBeanLocal commonServiceBeanLocal) {
			this.commonServiceBeanLocal = commonServiceBeanLocal;
		}

		public void run() {
			while (loop) {
				try {
					this.checkFepStatus();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(milliseconds);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		public void checkFepStatus() throws JMSException {
			logger.info("定时启动检查前置状态");
			try {
				Map<String, FEPEntity> map = CacheDatas.getInstance().getFEPEntityMap();
				Set<String> set = map.keySet();
				if (set != null && set.size() > 0) {
					for (String key : set) {
						FEPEntity entity = map.get(key);
						if (entity != null) {
							long fepTiem = entity.getStatus().getCurrentTime();
							if ((((System.currentTimeMillis()) - fepTiem) / 1000) >= 50) {
								if (entity.getStatus().isStatus()) {
									entity.getStatus().setStatus(false);
									FEPEntity fepEntity = nmsServiceLocal.findFepEntity(entity.getCode());
									if (fepEntity.getStatus().isStatus()) {
										fepEntity.getStatus().setStatus(false);
										commonServiceBeanLocal.updateEntity(fepEntity.getStatus());
										this.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE,"server", fepEntity);
									}
								}
								map.remove(entity.getCode());
								logger.info("前置机超时没连上！" + entity.getCode()+ "IP:" + entity.getIpValue());
								this.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE,"server", entity);
								if(Tools.isFepOnline){
									Tools.isFepOnline = false;
									
									//向客户端发送消息
									fepWarningLocal.sendFepMessageToClient(entity,Tools.ABORTING_EXIT);
								}

							} else {
								if (!entity.getStatus().isStatus()) {
									entity.getStatus().setStatus(true);
									FEPEntity fepEntity = nmsServiceLocal.findFepEntity(entity.getCode());
									if (!fepEntity.getStatus().isStatus()) {
										fepEntity.getStatus().setStatus(true);
										commonServiceBeanLocal.updateEntity(fepEntity.getStatus());
										this.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE,"server", fepEntity);
									}
								}
								logger.info("前置机在线！" + entity.getCode() + "IP:"+ entity.getIpValue());
								this.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE,"server", entity);
								
								if(!Tools.isFepOnline){
									Tools.isFepOnline = true;
									
									//向客户端发送消息
									fepWarningLocal.sendFepMessageToClient(entity,Tools.LOGOUT_SUCCEED);
								}
							}
						}
					}
				}
				else{  //前置机没有上线
					if(Tools.isFepOnline){
						Tools.isFepOnline = false;
						
						//向客户端发送消息
						fepWarningLocal.sendFepMessageToClient(null,Tools.ABORTING_EXIT);
						logger.info("*******************fepEntity is null****************************");
					}
					
				}
			} catch (Exception e) {
				logger.error("checkFepStatus error", e);
			} finally {
				try {
					finalize();
				} catch (Throwable e) {
				}
			}
		}

		public void sendHeartBeatObjMeg(int type, String from,
				Serializable message) throws JMSException {
			TopicPublisher publisher = null;
			TopicSession session = null;
			try {
				session = cacheLocal.getTopicSession();
				ObjectMessage objMessage = session.createObjectMessage(message);
				objMessage.setIntProperty(Constants.MESSAGETYPE, type);
				objMessage.setStringProperty(Constants.MESSAGEFROM, from);
				publisher = cacheLocal.getTopicPublisher();
				publisher.publish(objMessage);
				objMessage.clearBody();
				objMessage.clearProperties();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
