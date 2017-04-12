package com.jhw.adm.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.MessageProducer;
import javax.jms.TopicPublisher;

import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.system.UserEntity;

public class CacheDatas {
	private static CacheDatas cd;
	private static Map<String, String> ipcodeMap = new HashMap<String, String>();// 前置机ip和前置編號
	private static Map<String, FEPEntity> codefepMap = new HashMap<String, FEPEntity>();// 前置机编号和前置机对象
	private static Map<String, MessageProducer> producers = new HashMap<String, MessageProducer>();
	private static Map<String, UserEntity> users = new HashMap<String, UserEntity>();
	private static Map<String, String> ipmacMap = new HashMap<String, String>();
	private static Map<Integer, Boolean> checkFep = new HashMap<Integer, Boolean>();// 检查定时器是否启动
	private static Map<Integer, TopicPublisher> publicer = new HashMap<Integer, TopicPublisher>();
	private static Map<String, UserEntity> userMap = new HashMap<String, UserEntity>();// 检查当前登录用户数

	private static boolean timeServiceStart = false;
	private boolean refreshing = false;
	private boolean warninging=false;
	private boolean synchorizing = false;
	private static boolean fepStatus = true;
	private static int TIMERTAG = 0;// 检查前置机状态
	public static int SWITCHCOUNT = 0;// 交换机限制数量
	public static int SWITCH3COUNT = 0;// 三层交换机限制数量

	public static int OLTCOUNT = 0;// OLT限制数量
	public static int ONUCOUNT = 0;// ONU限制数量
	public static int GPRSCOUNT = 0;// GPRS限制数量
	public static int PLCCOUNT = 0;// 载波机限制数量

	public static int getGPRSCOUNT() {
		return GPRSCOUNT;
	}

	public static void setGPRSCOUNT(int gPRSCOUNT) {
		GPRSCOUNT = gPRSCOUNT;
	}

	public static int getSWITCH3COUNT() {
		return SWITCH3COUNT;
	}

	public static void setSWITCH3COUNT(int sWITCH3COUNT) {
		SWITCH3COUNT = sWITCH3COUNT;
	}

	public static int getOLTCOUNT() {
		return OLTCOUNT;
	}

	public static void setOLTCOUNT(int oLTCOUNT) {
		OLTCOUNT = oLTCOUNT;
	}

	public static int getONUCOUNT() {
		return ONUCOUNT;
	}

	public static void setONUCOUNT(int oNUCOUNT) {
		ONUCOUNT = oNUCOUNT;
	}

	public static int getPLCCOUNT() {
		return PLCCOUNT;
	}

	public static void setPLCCOUNT(int pLCCOUNT) {
		PLCCOUNT = pLCCOUNT;
	}

	public static int getTIMERTAG() {
		return TIMERTAG;
	}

	public static void setTIMERTAG(int tIMERTAG) {
		TIMERTAG = tIMERTAG;
	}

	public static int getSWITCHCOUNT() {
		return SWITCHCOUNT;
	}

	public static void setSWITCHCOUNT(int sWITCHCOUNT) {
		SWITCHCOUNT = sWITCHCOUNT;
	}

	public synchronized static CacheDatas getInstance() {
		if (cd == null) {
			cd = new CacheDatas();
		}
		return cd;
	}
   
	public boolean isWarninging() {
		return warninging;
	}

	public void setWarninging(boolean warninging) {
		this.warninging = warninging;
	}

	public FEPEntity getFEPEntityByIp(String ipvalue) {
		FEPEntity fep = null;
		Set<String> set = codefepMap.keySet();
		if (set != null && set.size() > 0) {
			for (String key : set) {
				fep = codefepMap.get(key);
				String ip = fep.getIpValue();
				if (ip.equals(ipvalue)) {
					break;
				}
			}
		}
		return fep;
	}

	public static boolean ipBetweenSegment(String ipvalue, IPSegment seg) {
		String bip = seg.getBeginIp();
		String eip = seg.getEndIp();
		String[] ips = ipvalue.split("\\.");
		String[] bips = bip.split("\\.");
		String[] eips = eip.split("\\.");
		int bips4 = Integer.parseInt(bips[3]);
		int eips4 = Integer.parseInt(eips[3]);
		int bips3 = Integer.parseInt(bips[2]);
		int eips3 = Integer.parseInt(eips[2]);
		int bips2 = Integer.parseInt(bips[1]);
		int eips2 = Integer.parseInt(eips[1]);
		int bips1 = Integer.parseInt(bips[0]);
		int eips1 = Integer.parseInt(eips[0]);
		int ips1 = Integer.parseInt(ips[0]);
		int ips2 = Integer.parseInt(ips[1]);
		int ips3 = Integer.parseInt(ips[2]);
		int ips4 = Integer.parseInt(ips[3]);
		if (ips1 >= bips1 && ips1 <= eips1) {
			if (ips2 >= bips2 && ips2 <= eips2) {
				if (ips3 >= bips3 && ips3 <= eips3) {
					if (ips4 >= bips4 && ips4 <= eips4) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void addFepCodeIp(String ip, String code) {
		ipcodeMap.put(ip, code);
	}

	public synchronized String getFepCodeByIp(String ip) {
		return ipcodeMap.get(ip);
	}

	public void addFEPEntity(String code, FEPEntity entity) {
		codefepMap.put(code, entity);
	}

	public void addUser(UserEntity userEntity) {
		userMap.put(userEntity.getUserName(), userEntity);

	}

	public void removeUser(String userName) {
		userMap.remove(userName);
	}

	public UserEntity getUserEntity(String userName) {
		return userMap.get(userName);
	}

	public Map<String, UserEntity> getUserMap() {
		return userMap;
	}

	public static void setUserMap(Map<String, UserEntity> userMap) {
		CacheDatas.userMap = userMap;
	}

	public synchronized FEPEntity getFEPByCode(String code) {
		FEPEntity fep = codefepMap.get(code);
		return fep;
	}

	public synchronized void removeFEP(String code) {
		codefepMap.remove(code);
	}

	public Map<String, FEPEntity> getFEPEntityMap() {
		return codefepMap;
	}

	public MessageProducer getProducerByFEP(String fepCode) {
		return producers.get(fepCode);
	}

	public void addProducer(String fepCode, MessageProducer producer) {
		producers.put(fepCode, producer);
	}

	public TopicPublisher getTopicPublisher(Integer num) {

		return publicer.get(num);
	}

	public void addPublicer(Integer num, TopicPublisher topicPublisher) {

		publicer.put(num, topicPublisher);
	}

	public UserEntity getUser(String userName) {
		return users.get(userName);
	}

	public synchronized void addUser(String userName, UserEntity user) {
		users.put(userName, user);
	}

	public Map<String, UserEntity> getUsers() {
		return users;
	}

	public void emptyFep() {
		codefepMap.clear();
	}

	public void emptyUser() {
		users.clear();
	}

	public boolean isTimeServiceStart() {
		return timeServiceStart;
	}

	public void setTimeServiceStart(boolean timeServiceStart) {
		CacheDatas.timeServiceStart = timeServiceStart;
	}

	public static boolean isFepStatus() {
		return fepStatus;
	}

	public static void setFepStatus(boolean fepStatus) {
		CacheDatas.fepStatus = fepStatus;
	}

	public String getIpByMac(String macvalue) {
		return ipmacMap.get(macvalue);
	}

	public void addIpByMac(String macvalue, String ipvalue) {
		ipmacMap.put(macvalue, ipvalue);
	}

	public void updateIpMac(String macvalue, String ipvalue) {
		ipmacMap.remove(macvalue);
		ipmacMap.put(macvalue, ipvalue);
	}

	public boolean isRefreshing() {
		return refreshing;
	}

	public void setRefreshing(boolean refreshing) {
		this.refreshing = refreshing;
	}

	public boolean isSynchorizing() {
		return synchorizing;
	}

	public void setSynchorizing(boolean synchorizing) {
		this.synchorizing = synchorizing;
	}

	public Map<Integer, Boolean> getCheckFep() {
		return checkFep;
	}

	public void addCheckFep(Integer num, boolean flag) {
		checkFep.put(num, flag);
	}

	
}
