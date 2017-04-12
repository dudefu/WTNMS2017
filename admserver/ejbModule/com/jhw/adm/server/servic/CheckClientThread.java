package com.jhw.adm.server.servic;

import java.util.Map;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import org.apache.log4j.Logger;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.interfaces.CheckClientThreadLocal;
import com.jhw.adm.server.util.CacheDatas;
@Stateless
@Local(CheckClientThreadLocal.class)
public class CheckClientThread implements CheckClientThreadLocal{

	RunAction runaction = null;
	private Logger logger = Logger.getLogger(CheckClientThread.class.getName());
	public void startClientTimer(long milliseconds) {
		if (runaction != null) {
			runaction.setLoop(false);
		}
		runaction = new RunAction();
		runaction.setLoop(true);
		runaction.setMilliseconds(milliseconds);
		runaction.start();
	}

	class RunAction extends Thread {

		public RunAction() {
			this.setName("��ʱ��Client��¼�û���");
		}

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
		public void run() {
			while (loop) {
				try {
					this.checkClientStatus();
				} catch (JMSException e) {
					logger.error("���Client��¼�û�",e);
				}
				try {
					Thread.sleep(milliseconds);
				} catch (InterruptedException e) {
					logger.error("���Client��¼�û�",e);
				}
			}
		}

		public void checkClientStatus() throws JMSException {
			logger.info("��ʱ�������Client��¼�û�");
			try {
				Map<String, UserEntity> userMap = CacheDatas.getInstance().getUserMap();
				Set<String> set = userMap.keySet();
				if (set != null && set.size() > 0) {
					for (String key : set) {
						UserEntity entity = userMap.get(key);
						if (entity != null) {
							long userTime = entity.getCurrentTime();
							if ((((System.currentTimeMillis()) - userTime) / 1000) >= 30) {
								CacheDatas.getInstance().removeUser(entity.getUserName());
								logger.info("��ʱ����û�"+entity.getUserName()+"");
							} 
						}

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
		

	}
}
