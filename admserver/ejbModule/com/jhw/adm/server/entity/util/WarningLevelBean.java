package com.jhw.adm.server.entity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jhw.adm.server.entity.util.Constants;

/**
 * �澯����
 * 
 * URGENCY = 1;// ����
 * SERIOUS = 2;// ����
 * INFORM = 3; // ֪ͨ
 * GENERAL = 4;// ��ͨ
 * 
 * @author Administrator
 *
 */
public class WarningLevelBean {
	private static WarningLevelBean instance ;
	
	private Map coldStartMap;              	//������
	private Map warmStartMap;				//������
	private Map linkDownMap;				//�˿ڶϿ�
	private Map linkUpMap;					//�˿�����
	private Map authenticationFailureMap;   //��֤ʧ��
	private Map egpNeighorlossMap;          //EGP�ھӹ���
	private Map enterPriseSpecificMap;
	private Map remonThingMap;				//�����˿�����������ֵʱ���͵�trap
	private Map pingOutMap;                 //������Ping��ͨ
	private Map pingInMap;                  //������Ping�ɹ�
	private Map database_DisconnectMap;     //���ݿ�Ͽ�
	private Map database_ConnectMap;        //���ݿ�����
	private Map fep_DisconnectMap;          //ǰ�û��Ͽ�
	private Map fep_ConnectMap;          	//ǰ�û�����
	private Map syslogMap;                  //syslog
	private Map otherWarningMap;            //�����澯
	
	private List<Map> list = new ArrayList<Map>();
	
	private WarningLevelBean(){
		coldStartMap = new HashMap<Integer,Integer>();
		warmStartMap = new HashMap<Integer,Integer>();
		linkDownMap = new HashMap<Integer,Integer>();
		linkUpMap = new HashMap<Integer,Integer>();
		authenticationFailureMap = new HashMap<Integer,Integer>();
		egpNeighorlossMap = new HashMap<Integer,Integer>();
		enterPriseSpecificMap = new HashMap<Integer,Integer>();
		remonThingMap = new HashMap<Integer,Integer>();
		pingOutMap = new HashMap<Integer,Integer>();
		pingInMap = new HashMap<Integer,Integer>();
		database_DisconnectMap = new HashMap<Integer,Integer>();
		database_ConnectMap = new HashMap<Integer,Integer>();
		fep_DisconnectMap = new HashMap<Integer,Integer>();
		fep_ConnectMap = new HashMap<Integer,Integer>();
		syslogMap = new HashMap<Integer,Integer>();
		otherWarningMap = new HashMap<Integer,Integer>();
		
		coldStartMap.put(Constants.COLDSTART,Constants.GENERAL); //������::�澯����(��ͨ)
		warmStartMap.put(Constants.WARMSTART,Constants.GENERAL); //������::�澯����(��ͨ)
		linkDownMap.put(Constants.LINKDOWN,Constants.URGENCY); //�˿ڶϿ�::�澯����(����);
		linkUpMap.put(Constants.LINKUP,Constants.INFORM); //�˿�����::�澯����(֪ͨ);
		authenticationFailureMap.put(Constants.AUTHENTICATIONFAILURE,Constants.GENERAL); //��֤ʧ��::�澯����(��ͨ);
		egpNeighorlossMap.put(Constants.EGPNEIGHORLOSS,Constants.GENERAL); //EGP�ھӹ���::�澯����(��ͨ);
		enterPriseSpecificMap.put(Constants.ENTERPRISESPECIFIC,Constants.GENERAL); //::�澯����(��ͨ);
		remonThingMap.put(Constants.REMONTHING,Constants.SERIOUS); //����::�澯����(����);
		pingOutMap.put(Constants.PINGOUT,Constants.SERIOUS); //������Ping��ͨ::�澯����(����);
		pingInMap.put(Constants.PINGIN,Constants.INFORM); //������Ping�ɹ�::�澯����(֪ͨ);
		database_DisconnectMap.put(Constants.DATABASE_DISCONNECT,Constants.SERIOUS); //���ݿ�Ͽ�::�澯����(����);
		database_ConnectMap.put(Constants.DATABASE_CONNECT, Constants.GENERAL); //���ݿ�����::�澯����(��ͨ);
		fep_DisconnectMap.put(Constants.FEP_DISCONNECT,Constants.SERIOUS); //ǰ�û��Ͽ�::�澯����(����);
		fep_ConnectMap.put(Constants.FEP_CONNECT,Constants.GENERAL); //ǰ�û�����::�澯����(��ͨ);
		syslogMap.put(Constants.SYSLOG, Constants.INFORM);           //syslog::�澯����(֪ͨ)
		otherWarningMap.put(Constants.OTHERWARNING,Constants.GENERAL); //�����澯::�澯����(��ͨ);
		
		
		list.add(coldStartMap);
		list.add(warmStartMap);
		list.add(linkDownMap);
		list.add(linkUpMap);
		list.add(authenticationFailureMap);
		list.add(egpNeighorlossMap);
		list.add(enterPriseSpecificMap);
		list.add(remonThingMap);
		list.add(pingOutMap);
		list.add(pingInMap);
		list.add(database_DisconnectMap);
		list.add(database_ConnectMap);
		list.add(fep_DisconnectMap);
		list.add(fep_ConnectMap);
		list.add(syslogMap);
		list.add(otherWarningMap);
	}
	
	public static WarningLevelBean getInstance(){
		if (null == instance){
			instance = new WarningLevelBean();
		}
		
		return instance;
	}
	
	/**
	 * �õ��澯����
	 * @param warningEvent
	 * @return
	 */
	public Integer getWarningLevel(int warningEvent){
		Integer warningLevel = -1;
		for(Map map : list){
			Object object = map.get(warningEvent);
			if (object != null){
				int integer = (Short)object;
				warningLevel = (Integer)integer;
				break;
			}
		}
		
		return warningLevel;
	}
}
