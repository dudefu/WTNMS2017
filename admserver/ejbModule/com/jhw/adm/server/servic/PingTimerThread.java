package com.jhw.adm.server.servic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.FaultDetection;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.NMSServiceLocal;
import com.jhw.adm.server.interfaces.PingTimerThreadLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;

@Stateless
@Local(PingTimerThreadLocal.class)
public class PingTimerThread implements PingTimerThreadLocal{
	private Logger logger = Logger.getLogger(PingTimerThread.class.getName());
	RunAction runaction = null;
	@EJB
	NMSServiceLocal nmsServiceLocal;
	@EJB
	private SMTCServiceLocal smtcServiceLocal;
	
	@EJB
	private DataCacheLocal dataCache;
	public void stopPingTimer(boolean stop){
		if (runaction != null){
			runaction.setLoop(false);
		}
	}
	public void startPingTimer(int pinglv,boolean start){
		if (runaction != null){
			runaction.setLoop(false);
		}
		runaction = new RunAction();
		runaction.setLoop(true);
		runaction.setPinglv(pinglv);
		runaction.start();
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
				List<FaultDetection> pingResults = nmsServiceLocal.queryPingResult();
				logger.info("定时Ping");
				try {
					if (pingResults != null && pingResults.size() > 0) {
						this.timerFaultDetection(MessageNoConstants.PING_TIMER,"server", "", pingResults);
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
		public void timerFaultDetection(int mt, String from, String clientIp,
				List<FaultDetection> messages) throws JMSException {
			Date startDate = null;
			Date endDate = null;
			int timers = 0;
			int jiange =0;
			boolean warning=false;
			for (FaultDetection pr : messages) {
				String ipValues = pr.getIpvlaues();
				startDate = pr.getBeginTime();
				endDate = pr.getEndTime();
				timers = pr.getPingtimes();
				jiange =pr.getJiange();
				warning = pr.isWarning();
				if(ipValues!=null&&!ipValues.equals("")){
				String fepCode = dataCache.getFepCodeByIP(ipValues);
				if (fepCode == null) {
					logger.info("定时Ping时该设备不在前置机管辖范围内!");
					smtcServiceLocal.sendMessage(MessageNoConstants.FEPOFFLINE,
							"server", from, clientIp, "该设备不在前置机管理范围内");
					continue;
				}
				Date nowDate = new Date();
				ArrayList<String> list =new ArrayList<String>();
				list.add(ipValues);
				if (nowDate.before(endDate) && nowDate.after(startDate)) {
						MessageProducer producer = dataCache.getProducerByCode(fepCode);
						ObjectMessage message = dataCache.getSession().createObjectMessage(list);
						message.setIntProperty(Constants.MESSAGETYPE, mt);
						message.setIntProperty(Constants.PING_TIMES, timers);
						message.setIntProperty(Constants.PING_JIANGE, jiange);
						message.setStringProperty(Constants.MESSAGEFROM, from);
						message.setStringProperty(Constants.CLIENTIP, clientIp);
						message.setBooleanProperty(Constants.PING_WARNING, warning);
						producer.send(message);

				}
					
				
			}
			}
		}
	}
	
}
