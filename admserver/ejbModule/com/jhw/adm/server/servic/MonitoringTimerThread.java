/**
 * 
 */
package com.jhw.adm.server.servic;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import org.apache.log4j.Logger;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.RTMonitorConfig;
import com.jhw.adm.server.entity.warning.TimerMonitoring;
import com.jhw.adm.server.interfaces.CommonServiceBeanLocal;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.MonitoringTimerThreadLocal;
import com.jhw.adm.server.interfaces.NMSServiceLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;

/**
 * @author 左军勇
 * @时间 2010-8-9
 */
@Stateless
@Local(MonitoringTimerThreadLocal.class)
public class MonitoringTimerThread implements MonitoringTimerThreadLocal{
	private Logger logger = Logger.getLogger(MonitoringTimerThread.class.getName());
	RunAction runaction = null;
	@EJB
	private NMSServiceLocal nmsServiceLocal;
	@EJB
	private CommonServiceBeanLocal commonServiceBeanLocal;
	@EJB
	private SMTCServiceLocal smtcServiceLocal;
	
	@EJB
	private DataCacheLocal dataCache;
	
	public void startMonitoringTimer(int pinglv,boolean start){
		if (runaction != null){
			runaction.setLoop(false);
		}
		if(start){
		runaction = new RunAction();
		runaction.setLoop(true);
		runaction.setPinglv(pinglv);
		runaction.start();
		}
	}
	class RunAction extends Thread{
		public boolean loop =true;
		int pinglv;
		public void setLoop(boolean bool){
			this.loop = bool;
		}
		public int getPinglv() {
			return pinglv;
		}
		public void setPinglv(int pinglv) {
			this.pinglv = pinglv;
		}
		public void run(){
			while(loop){
				this.setName("TimerMonitoring---");
				List<TimerMonitoring> results = nmsServiceLocal.queryTimerMonitoring();
				logger.info("定时监控******");
				try {
					if (results != null && results.size() > 0) {
						this.timerMonitoring(MessageNoConstants.MONITORRING_TIMER,"server", "", results);
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
				try {
					if(pinglv>0){
					Thread.sleep(pinglv*1000*60);
					}
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
		public void timerMonitoring(int mt, String from, String clientIp,
				List<TimerMonitoring>  messages) throws JMSException {
			Date startDate = null;
			Date endDate = null;
			for (TimerMonitoring pr : messages) {
				startDate = pr.getStartDate();
				endDate = pr.getEndDate();
				RTMonitorConfig count =new RTMonitorConfig();
				String[] str ={pr.getParam()};
				count.setParms(str);
				count.setIpValue(pr.getIpValue());
				count.setPortNo(pr.getPortNo());
				count.setBeginTime(startDate.getTime());
				count.setEndTime(endDate.getTime());
				count.setId(pr.getId());
				String ipValue = pr.getIpValue();
				String fepCode = dataCache.getFepCodeByIP(ipValue);
				if (fepCode == null) {
					logger.info("定时监控时该设备不在前置机管辖范围内!");
					smtcServiceLocal.sendMessage(MessageNoConstants.FEPOFFLINE,
							"server", from, clientIp, "该设备不在前置机管理范围内");
					continue;
				}
			Date nowDate = new Date();
			if (nowDate.before(endDate) && nowDate.after(startDate)) {
				    
				if(!pr.isSendTag()){
					MessageProducer producer = dataCache.getProducerByCode(fepCode);
					ObjectMessage message = dataCache.getSession().createObjectMessage(count);
					message.setIntProperty(Constants.MESSAGETYPE, mt);
					message.setStringProperty(Constants.MESSAGEFROM, from);
					message.setStringProperty(Constants.CLIENTIP, clientIp);
					producer.send(message);
					pr.setSendTag(true);
					commonServiceBeanLocal.updateEntity(pr);
					logger.info("定时监控消息发送成功");
				}else{
					logger.info("定时监控 "+pr.getIpValue()+"已发送监控信息！");
				}
				
			}
			}	
		}
	}
}
