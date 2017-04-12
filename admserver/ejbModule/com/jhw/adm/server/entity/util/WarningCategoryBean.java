package com.jhw.adm.server.entity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarningCategoryBean {
	private static WarningCategoryBean instance ;
	
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
	
	private WarningCategoryBean(){
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
		
		coldStartMap.put(Constants.COLDSTART,Constants.equipment_Warning); //������::�澯���(�豸�澯)
		warmStartMap.put(Constants.WARMSTART,Constants.equipment_Warning); //������::�澯���(�豸�澯)
		linkDownMap.put(Constants.LINKDOWN,Constants.port_Warning); //�˿ڶϿ�::�澯���(�˿ڸ澯)
		linkUpMap.put(Constants.LINKUP,Constants.port_Warning); //�˿�����::�澯���(�˿ڸ澯)
		authenticationFailureMap.put(Constants.AUTHENTICATIONFAILURE,Constants.facility_Warning); //��֤ʧ��::�澯���(ͨ�Ÿ澯)
		egpNeighorlossMap.put(Constants.EGPNEIGHORLOSS,Constants.protocol_Warning); //EGP�ھӹ���::�澯���(Э��澯)
		enterPriseSpecificMap.put(Constants.ENTERPRISESPECIFIC,Constants.protocol_Warning); //::�澯���(Э��澯)
		remonThingMap.put(Constants.REMONTHING,Constants.equipment_Warning); //����::�澯���(���ܸ澯)
		pingOutMap.put(Constants.PINGOUT,Constants.equipment_Warning); //������Ping��ͨ::�澯���(�豸�澯)
		pingInMap.put(Constants.PINGIN,Constants.equipment_Warning); //������Ping�ɹ�::�澯���(�豸�澯)
		database_DisconnectMap.put(Constants.DATABASE_DISCONNECT,Constants.NMS_Warning); //���ݿ�Ͽ�::�澯���(���ܸ澯)
		database_ConnectMap.put(Constants.DATABASE_CONNECT, Constants.NMS_Warning); //���ݿ�����::�澯���(���ܸ澯)
		fep_DisconnectMap.put(Constants.FEP_DISCONNECT,Constants.NMS_Warning); //ǰ�û��Ͽ�::�澯���(���ܸ澯)
		fep_ConnectMap.put(Constants.FEP_CONNECT,Constants.NMS_Warning); //ǰ�û�����::�澯���(���ܸ澯)
		syslogMap.put(Constants.SYSLOG, Constants.protocol_Warning);     //syslog::�澯���(Э��澯)
		otherWarningMap.put(Constants.OTHERWARNING,Constants.other_Warning); //�����澯::�澯���(�����澯)
		
		
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
	
	public static WarningCategoryBean getInstance(){
		if (null == instance){
			instance = new WarningCategoryBean();
		}
		
		return instance;
	}
	
	/**
	 * �õ��澯���
	 * @param warningEvent
	 * @return
	 */
	public Integer getWarningCategory(int warningEvent){
		Integer warningCategory = -1;
		for(Map map : list){
			Object object = map.get(warningEvent);
			if (object != null){
				warningCategory = (Integer)object;
				break;
			}
		}
		
		return warningCategory;
	}
}
