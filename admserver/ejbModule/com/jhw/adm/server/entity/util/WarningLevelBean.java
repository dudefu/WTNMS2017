package com.jhw.adm.server.entity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jhw.adm.server.entity.util.Constants;

/**
 * 告警级别
 * 
 * URGENCY = 1;// 紧急
 * SERIOUS = 2;// 严重
 * INFORM = 3; // 通知
 * GENERAL = 4;// 普通
 * 
 * @author Administrator
 *
 */
public class WarningLevelBean {
	private static WarningLevelBean instance ;
	
	private Map coldStartMap;              	//冷启动
	private Map warmStartMap;				//热启动
	private Map linkDownMap;				//端口断开
	private Map linkUpMap;					//端口连接
	private Map authenticationFailureMap;   //认证失败
	private Map egpNeighorlossMap;          //EGP邻居故障
	private Map enterPriseSpecificMap;
	private Map remonThingMap;				//超过端口流量上下限值时候发送的trap
	private Map pingOutMap;                 //交换机Ping不通
	private Map pingInMap;                  //交换机Ping成功
	private Map database_DisconnectMap;     //数据库断开
	private Map database_ConnectMap;        //数据库连接
	private Map fep_DisconnectMap;          //前置机断开
	private Map fep_ConnectMap;          	//前置机连接
	private Map syslogMap;                  //syslog
	private Map otherWarningMap;            //其它告警
	
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
		
		coldStartMap.put(Constants.COLDSTART,Constants.GENERAL); //冷启动::告警级别(普通)
		warmStartMap.put(Constants.WARMSTART,Constants.GENERAL); //热启动::告警级别(普通)
		linkDownMap.put(Constants.LINKDOWN,Constants.URGENCY); //端口断开::告警级别(紧急);
		linkUpMap.put(Constants.LINKUP,Constants.INFORM); //端口连接::告警级别(通知);
		authenticationFailureMap.put(Constants.AUTHENTICATIONFAILURE,Constants.GENERAL); //认证失败::告警级别(普通);
		egpNeighorlossMap.put(Constants.EGPNEIGHORLOSS,Constants.GENERAL); //EGP邻居故障::告警级别(普通);
		enterPriseSpecificMap.put(Constants.ENTERPRISESPECIFIC,Constants.GENERAL); //::告警级别(普通);
		remonThingMap.put(Constants.REMONTHING,Constants.SERIOUS); //流量::告警级别(严重);
		pingOutMap.put(Constants.PINGOUT,Constants.SERIOUS); //交换机Ping不通::告警级别(严重);
		pingInMap.put(Constants.PINGIN,Constants.INFORM); //交换机Ping成功::告警级别(通知);
		database_DisconnectMap.put(Constants.DATABASE_DISCONNECT,Constants.SERIOUS); //数据库断开::告警级别(严重);
		database_ConnectMap.put(Constants.DATABASE_CONNECT, Constants.GENERAL); //数据库连接::告警级别(普通);
		fep_DisconnectMap.put(Constants.FEP_DISCONNECT,Constants.SERIOUS); //前置机断开::告警级别(严重);
		fep_ConnectMap.put(Constants.FEP_CONNECT,Constants.GENERAL); //前置机连接::告警级别(普通);
		syslogMap.put(Constants.SYSLOG, Constants.INFORM);           //syslog::告警级别(通知)
		otherWarningMap.put(Constants.OTHERWARNING,Constants.GENERAL); //其它告警::告警级别(普通);
		
		
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
	 * 得到告警级别
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
