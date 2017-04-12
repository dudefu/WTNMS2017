package com.jhw.adm.server.mdb;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.interfaces.CheckFEPThreadLocal;
import com.jhw.adm.server.interfaces.MonitoringTimerThreadLocal;
import com.jhw.adm.server.interfaces.NMSServiceLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;
import com.jhw.adm.server.interfaces.SMTFEPServiceLocal;
import com.jhw.adm.server.util.CacheDatas;
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/HeartbeatFEP"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class HeartbeatFEP implements MessageListener{
	 private Logger logger = Logger.getLogger(HeartbeatFEP.class.getName());
	@EJB
	private SMTFEPServiceLocal smtfepService;
	@EJB
	private SMTCServiceLocal smtcService;
	@EJB
	private CheckFEPThreadLocal checkFEPThreadLocal;
	@EJB
	private MonitoringTimerThreadLocal monitoringTimerThreadLocal;
	@PersistenceContext(unitName = "adm2server")
	private EntityManager manager;
	@EJB
	private NMSServiceLocal nmsServiceLocal;
	@Override
	public void onMessage(Message message) {
		if(message instanceof TextMessage){
			TextMessage tm = (TextMessage) message;
			try {
				if(CacheDatas.getTIMERTAG()==0){
					checkFEPThreadLocal.startFEPTimer((long)50000);
					monitoringTimerThreadLocal.startMonitoringTimer(1, true);
					CacheDatas.setTIMERTAG(1);
				}
				if(tm!=null){
				String code = tm.getStringProperty(Constants.MESSAGEFROM).toString();
				logger.info("心跳检查前置机代码:"+code+"");
				int type = tm.getIntProperty(Constants.MESSAGETYPE);
				if (type == MessageNoConstants.FEPHEARTBEAT) {
					if(code!=null&&!code.equals("")){
							FEPEntity fep=CacheDatas.getInstance().getFEPByCode(code);
							if (fep != null) {
								fep.getStatus().setCurrentTime(System.currentTimeMillis());
								if(!fep.getStatus().isStatus()){
									fep.getStatus().setStatus(true);
									smtcService.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE,"server", fep);
								}
								FEPEntity fepEntity =nmsServiceLocal.findFepEntity(fep.getCode());
								if(fepEntity!=null){
								if(!fepEntity.getStatus().isStatus()){
									fepEntity.getStatus().setStatus(true);
									manager.merge(fepEntity);
									smtcService.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE,"server", fepEntity);
								}
								smtfepService.sendMsgToFep(code, type,"server", "");
								}
							} else {
								fep =nmsServiceLocal.findFepEntity(code);
								if(fep!=null){
								if(!fep.getStatus().isStatus()){
								fep.getStatus().setStatus(true);
								fep.getStatus().setCurrentTime(System.currentTimeMillis());
								manager.merge(fep);
								smtcService.sendHeartBeatObjMeg(MessageNoConstants.FEP_STATUSTYPE,"server", fep);
								}
								smtfepService.sendMsgToFep(code, type,"server", "");	
								CacheDatas.getInstance().addFEPEntity(code, fep);
								}else{
									logger.error("编号" + code + "前置机未找到！");
								}
								
							}
					}else{
						logger.error("前置机心跳 Code为空！");
					}
				} 
				}
			} catch (JMSException e) {
				logger.error("接受前置消息：",e);
			}
		}
		
	}

}
