package com.jhw.adm.server.servic;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.interfaces.CheckFEPTimerLocal;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.NMSServiceLocal;
import com.jhw.adm.server.util.CacheDatas;

@Stateless
@Local(CheckFEPTimerLocal.class)
public class CheckFEPTimer implements CheckFEPTimerLocal{
	
	
	@PersistenceContext(unitName = "adm2server")
	private EntityManager manager;
	@Resource
	private SessionContext ctx;
    private Logger logger = Logger.getLogger(CheckFEPTimer.class.getName());
	@EJB
	private DataCacheLocal cacheLocal;
	
	@EJB
	private NMSServiceLocal nmsServiceLocal;
	private TimerService timerService;
	
	public TimerService getTimerService() {
		return timerService;
	}
	public void setTimerService(TimerService timerService) {
		this.timerService = timerService;
	}
	@Override
	public void scheduleTimer(long milliseconds) {
		try {
			if (ctx != null) {
				if(timerService==null){
				this.timerService =ctx.getTimerService();
				timerService.createTimer(new Date(new Date().getTime() + milliseconds),milliseconds, "定时查询Fep启动情况");
				}
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (EJBException e) {
			e.printStackTrace();
		}
	}
	@Timeout
	public void checkFEPWork(Timer timer){
		try {
				Map<Integer, Boolean> map = CacheDatas.getInstance().getCheckFep();
				if (map.get(1) == null||!map.get(1)) {
//					CacheDatas.getInstance().addCheckFep(1, true);
				}
				this.checkFepStatus();
		} catch (Exception e) {
			logger.error("定时查询Fep状态异常——" + e);
			e.printStackTrace();
		}
		if(timer!=null){
			timer.cancel();
		}
	}
	public  void checkFepStatus(){
		try {
			Map<String, FEPEntity> map = CacheDatas.getInstance().getFEPEntityMap();
			Set<String> set = map.keySet();
			if (set != null && set.size() > 0) {
				for (String key : set) {
					FEPEntity entity = map.get(key);
					if (entity != null) {
						long fepTiem = entity.getStatus().getCurrentTime();
						if ((((System.currentTimeMillis())  - fepTiem) / 1000) >= 50) 
						{   
							
							if(entity.getStatus().isStatus()){
								entity.getStatus().setStatus(false);
								this.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE,"server", entity);
								FEPEntity fepEntity =nmsServiceLocal.findFepEntity(entity.getCode());
								if(fepEntity.getStatus().isStatus()){
								fepEntity.getStatus().setStatus(false);
								manager.merge(fepEntity.getStatus());
								this.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE, "server",fepEntity);
							    }
							}
							logger.info("前置机超时没连接上！"+entity.getCode());
						}else{
							if(!entity.getStatus().isStatus()){
								entity.getStatus().setStatus(true);
								this.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE,"server", entity);
								FEPEntity fepEntity =nmsServiceLocal.findFepEntity(entity.getCode());
								if(!fepEntity.getStatus().isStatus()){
								fepEntity.getStatus().setStatus(true);
								manager.merge(fepEntity.getStatus());
								this.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE, "server",fepEntity);
								}
							}
							logger.info("前置在线！"+entity.getCode());
						}
						
					}

				}
			}
		} catch (Exception e) {
			logger.error("定时监测前置机状态  "+e);
			e.printStackTrace();
		}
	}
	public void sendHeartBeatObjMeg(int type, String from,Serializable message) throws JMSException {
		
		TopicPublisher publisher=null;
		TopicSession session=null;
		try {
			session=cacheLocal.getTopicSession();
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
