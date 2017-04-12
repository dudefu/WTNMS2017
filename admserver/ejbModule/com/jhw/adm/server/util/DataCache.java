package com.jhw.adm.server.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;
import org.jboss.ejb3.annotation.RemoteBinding;

import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.servic.DataCacheServiceRemote;

@Stateless(mappedName = "dataCache")
@LocalBinding(jndiBinding = "local/DataCacheLocal")
@RemoteBinding(jndiBinding = "remote/DataCacheRemote")
public class DataCache implements DataCacheLocal, DataCacheServiceRemote {

	private static Session session;
	private Logger logger = Logger.getLogger(DataCache.class.getName());
	private static TopicConnection cn = null;
	private static TopicSession topicSession = null;
	@Resource(mappedName = "topic/STCTopic")
	private Topic stcTopic;

	TopicPublisher publisher = null;

	@PersistenceContext(unitName = "adm2server")
	private EntityManager manager;

	@Resource(mappedName = "ClusteredXAConnectionFactory")
	private ConnectionFactory connf;

	@Resource(mappedName = "ClusteredXAConnectionFactory")
	private TopicConnectionFactory connfTop;

	public void cacheFep(String code, String pwssword) {
		FEPEntity fep = null;
		String jpql = "select entity from " + FEPEntity.class.getName()
				+ " as entity where entity.code=? and entity.loginPassword=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, code);
		query.setParameter(2, pwssword);
		List datas = query.getResultList();
		if (datas != null && datas.size() > 0) {
			fep = (FEPEntity) datas.get(0);
			CacheDatas.getInstance().addFEPEntity(code, fep);
		}
	}

	public void cacheAllFeps() {
		String jpql = "select entity from " + FEPEntity.class.getName()
				+ " as entity";
		Query query = manager.createQuery(jpql);
		List<FEPEntity> datas = query.getResultList();
		if (datas != null && datas.size() != 0) {
			for (FEPEntity fep : datas) {
				FEPEntity cfep = CacheDatas.getInstance().getFEPByCode(
						fep.getCode());
				if (cfep == null) {
					CacheDatas.getInstance().addFEPEntity(fep.getCode(), fep);
				} else {
					cfep.getStatus().setStatus(fep.getStatus().isStatus());
					CacheDatas.getInstance().addFEPEntity(fep.getCode(), cfep);
				}
			}
		}
	}

	private void initUsers() {
		String jpql = "select entity from " + UserEntity.class.getName()
				+ " as entity";
		Query query = manager.createQuery(jpql);
		List<UserEntity> users = query.getResultList();
		CacheDatas.getInstance().emptyUser();
		for (UserEntity user : users) {
			CacheDatas.getInstance().addUser(user.getUserName(), user);
		}
	}

	/**
	 * 通过需要配置的设备的ip地址，获取管理该设备的前置机编号
	 * 
	 * @param ipvalue
	 * @return
	 */
	public String getFepCodeByIP(String ipvalue) {
		CacheDatas cds = CacheDatas.getInstance();
		String fepcode = cds.getFepCodeByIp(ipvalue);
		if (fepcode == null) {
			// initfepdatas();
			Map<String, FEPEntity> fepMap = cds.getFEPEntityMap();
			Set<String> keys = fepMap.keySet();
			l0: for (String key : keys) {
				FEPEntity tfep = fepMap.get(key);
				if (tfep != null) {
					List<IPSegment> segments = tfep.getSegment();
					for (IPSegment seg : segments) {
						boolean ok = CacheDatas.ipBetweenSegment(ipvalue, seg);
						if (ok) {
							CacheDatas.getInstance().addFepCodeIp(ipvalue,
									tfep.getCode());
							fepcode = tfep.getCode();
							break l0;
						}
					}
				}
			}
		}
		return fepcode;
	}

	/**
	 * 通过前置机编号获取消息发送器
	 */
	public MessageProducer getProducerByCode(String code) throws JMSException {
		if (code == null) {
			return null;
		}
		MessageProducer producer = CacheDatas.getInstance().getProducerByFEP(
				code);
		if (producer == null) {
			if (session == null) {
				session = connf.createConnection().createSession(false,
						Session.AUTO_ACKNOWLEDGE);

			}
			Queue queue = session.createQueue(code);
			producer = session.createProducer(queue);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			CacheDatas.getInstance().addProducer(code, producer);
		}
		return producer;
	}

	public TopicPublisher getTopicPublisher() throws JMSException {

		TopicPublisher publisher = CacheDatas.getInstance()
				.getTopicPublisher(1);
		if (publisher == null) {
			if (cn == null) {
				cn = connfTop.createTopicConnection();
			}
			if (topicSession == null) {
				topicSession = cn.createTopicSession(false,
						TopicSession.AUTO_ACKNOWLEDGE);
			}
			publisher = topicSession.createPublisher(stcTopic);
			CacheDatas.getInstance().addPublicer(1, publisher);
		}
		return publisher;
	}

	/**
	 * 获取session
	 */
	public Session getSession() throws JMSException {
		if (session == null) {
			session = connf.createConnection().createSession(true,
					Session.AUTO_ACKNOWLEDGE);
		}
		return session;
	}

	public TopicConnection getTopicConnection() throws JMSException {
		if (cn == null) {
			cn = connfTop.createTopicConnection();
		}
		return cn;
	}

	public TopicSession getTopicSession() throws JMSException {
		if (topicSession == null) {
			cn = getTopicConnection();
			topicSession = cn.createTopicSession(false,
					TopicSession.AUTO_ACKNOWLEDGE);
		}
		return topicSession;
	}

	public UserEntity getUser(String userName) {
		String jpql = "select entity from " + UserEntity.class.getName()+ " as entity where 1=1 ";
		if(userName!=null&&!userName.equals("")){
			jpql +=" and entity.userName=:userName";
		}
		Query query = manager.createQuery(jpql);
		if(userName!=null&&!userName.equals("")){
		query.setParameter("userName", userName);
		}
		List<UserEntity> users = query.getResultList();
		if(users!=null&&users.size()>0){
		return users.get(0);
		}
		return null;
	}

	public FEPEntity getFEPByCode(String code) {
		FEPEntity fep = CacheDatas.getInstance().getFEPByCode(code);
		return fep;
	}

	public void addFEPEntity(String code, FEPEntity fep) {
		CacheDatas.getInstance().addFEPEntity(code, fep);
	}

	public Map<String, Set> splitIps(Set<SynchDevice> sds) {
		Map<String, Set> datas = new HashMap<String, Set>();
		if (sds != null) {
			for (SynchDevice sd : sds) {
				if (sd.getIpvalue() != null && !sd.getIpvalue().equals("")) {
					String fepCode = getFepCodeByIP(sd.getIpvalue());
					
					if (fepCode == null) {
						continue;
					}
					
					FEPEntity fep = this.getFEPByCode(fepCode);
					if (fep == null) {
						continue;
					}
					boolean ok = fep.getStatus().isStatus();
					if (!ok) {
						continue;
					} 
					Set<SynchDevice> bsds = datas.get(fepCode);
					if (bsds != null) {
						bsds.add(sd);
						datas.put(fepCode, bsds);
					} else {
						bsds = new HashSet<SynchDevice>();
						bsds.add(sd);
						datas.put(fepCode, bsds);
					}
				} else {
					logger.error("同步时设备IP为空");
				}
			}
		}
		return datas;
	}

	public Map<String, String> splitIps(String[] ipss) {
		Map<String, String> datas = new HashMap<String, String>();
		if (ipss != null) {
			for (String ip : ipss) {
				String fepCode = getFepCodeByIP(ip);
				if (fepCode == null) {
					continue;
				}
				String ips = datas.get(fepCode);
				if (ips != null) {
					ips = ips + ";" + ip;
					datas.put(fepCode, ips);
				} else {
					ips = ip;
					datas.put(fepCode, ips);
				}
			}
		}
		return datas;
	}

	public void resettingTopo() {
		CacheDatas.getInstance().setRefreshing(false);
	}

	public void resettingSyn() {
		CacheDatas.getInstance().setSynchorizing(false);
	}

	private String queryIpByMac(String macvalue) {
		String jpql = "select entity.baseConfig.ipValue from "
				+ SwitchNodeEntity.class.getName()
				+ " as entity where entity.baseInfo.macValue=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, macvalue);
		List<?> datas = query.getResultList();
		if (datas != null && datas.size() != 0) {
			return datas.get(0).toString();
		}
		return null;
	}

	public String getIpByMac(String macvalue) {
		
		String ipvlaue = "";
		if (ipvlaue == null || ipvlaue.equals("")) {
			ipvlaue = queryIpByMac(macvalue);
		}
		return ipvlaue;
	}

	public Map<String, FEPEntity> getFepMap() {
		return CacheDatas.getInstance().getFEPEntityMap();
	}

	public void removeFEP(String code) {
		CacheDatas.getInstance().removeFEP(code);
	}

	/**
	 * 通过设备的ip判断前置机是否在线
	 * @param ip
	 * @return
	 */
	@Override
	public boolean isFEPOnline(String ip){
		boolean bool = true;
//		String ip = getIpByMac(macvalue);
		String code = getFepCodeByIP(ip);
		if (code != null) {
			FEPEntity fep = getFEPByCode(code);
			if (fep != null) {
				boolean ok = fep.getStatus().isStatus();
				if (!ok) {
					logger.info("前置机" + code + "不在线，请检查！");
				}
				return ok;
			}
			else{
				bool = false;
			}
		}
		else{
			bool = false;
		}
		return bool;
	
//		FEPEntity fepEntity = dataCache.getFEPByCode(fepCode);
//		if(fepEntity==null){
//			smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
//					"server", from, clientIp, "该设备所属前置机不在线");
//			return;
//		}
//		if (fepEntity.getStatus() != null&& !fepEntity.getStatus().isStatus()) {
//				smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
//						"server", from, clientIp, "该设备所属前置机不在线");
//				return;
//		}
	}

	public boolean canContinueIP(String ipValue) {
		String code = getFepCodeByIP(ipValue);
		if (code != null) {
			FEPEntity fep = getFEPByCode(code);
			if (fep != null) {
				boolean ok = fep.getStatus().isStatus();
				if (!ok) {
					logger.info("前置机" + code + "不在线，请检查！");
				}
				return ok;
			}
		}
		return false;
	}
}
