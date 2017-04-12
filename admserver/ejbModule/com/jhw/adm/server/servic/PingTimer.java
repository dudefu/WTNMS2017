package com.jhw.adm.server.servic;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.RemoteBinding;

import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.FaultDetection;
import com.jhw.adm.server.entity.warning.RTMonitorConfig;
import com.jhw.adm.server.entity.warning.TimerMonitoring;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.MonitoringTimerThreadLocal;
import com.jhw.adm.server.interfaces.PingTimerLocal;
import com.jhw.adm.server.interfaces.PingTimerThreadLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;

@Stateless
@Local(PingTimerLocal.class)
@RemoteBinding(jndiBinding="remote/PingTimerRemote")
public class PingTimer implements PingTimerLocal,PingTimerRemote{
	private Logger logger = Logger.getLogger(PingTimer.class.getName());
	@PersistenceContext(unitName = "adm2server")
	private EntityManager manager;
	@EJB
	private PingTimerThreadLocal timerThreadLocal;
	@EJB
	private SMTCServiceLocal smtcServiceLocal;
	@EJB
	private MonitoringTimerThreadLocal monitoringTimerThreadLocal;
	@EJB
	private DataCacheLocal dataCache;
	
	public void updateFaultDetection(FaultDetection faultDetection) throws InterruptedException{
		  try {
			  manager.merge(faultDetection);
			  timerThreadLocal.startPingTimer(1,faultDetection.isWarning());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateMonitoring(List<TimerMonitoring> list){
		try {
			for(TimerMonitoring monitoring :list){
				manager.merge(monitoring);
			}
			monitoringTimerThreadLocal.startMonitoringTimer(1, true);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		
		
	}
	
	public void deleteMonitoring(List<TimerMonitoring> list){
		
		
		
		try {
			for(TimerMonitoring monitoring :list){
				monitoring =manager.find(TimerMonitoring.class, monitoring.getId());
				manager.remove(monitoring);
			}
			this.timerMonitoring(MessageNoConstants.MONITORRING_DEL, "server", "", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void timerMonitoring(int mt, String from, String clientIp,
			List<TimerMonitoring>  messages) throws JMSException {
		
		for (TimerMonitoring pr : messages) {
			RTMonitorConfig count = new RTMonitorConfig();
			String[] str = { pr.getParam() };
			count.setParms(str);
			count.setIpValue(pr.getIpValue());
			count.setPortNo(pr.getPortNo());
			count.setId(pr.getId());
			String ipValue = pr.getIpValue();
			String fepCode = dataCache.getFepCodeByIP(ipValue);
			if (fepCode == null) {
				logger.info("定时监控时该设备不在前置机管辖范围内!");
				smtcServiceLocal.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "该设备不在前置机管理范围内");
				continue;
			}

			MessageProducer producer = dataCache.getProducerByCode(fepCode);
			ObjectMessage message = dataCache.getSession().createObjectMessage(
					count);
			message.setIntProperty(Constants.MESSAGETYPE, mt);
			message.setStringProperty(Constants.MESSAGEFROM, from);
			message.setStringProperty(Constants.CLIENTIP, clientIp);
			producer.send(message);

		}
	}
}
