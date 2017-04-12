package com.jhw.adm.server.entity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarningCategoryBean {
	private static WarningCategoryBean instance ;
	
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
		
		coldStartMap.put(Constants.COLDSTART,Constants.equipment_Warning); //冷启动::告警类别(设备告警)
		warmStartMap.put(Constants.WARMSTART,Constants.equipment_Warning); //热启动::告警类别(设备告警)
		linkDownMap.put(Constants.LINKDOWN,Constants.port_Warning); //端口断开::告警类别(端口告警)
		linkUpMap.put(Constants.LINKUP,Constants.port_Warning); //端口连接::告警类别(端口告警)
		authenticationFailureMap.put(Constants.AUTHENTICATIONFAILURE,Constants.facility_Warning); //认证失败::告警类别(通信告警)
		egpNeighorlossMap.put(Constants.EGPNEIGHORLOSS,Constants.protocol_Warning); //EGP邻居故障::告警类别(协议告警)
		enterPriseSpecificMap.put(Constants.ENTERPRISESPECIFIC,Constants.protocol_Warning); //::告警类别(协议告警)
		remonThingMap.put(Constants.REMONTHING,Constants.equipment_Warning); //流量::告警类别(性能告警)
		pingOutMap.put(Constants.PINGOUT,Constants.equipment_Warning); //交换机Ping不通::告警类别(设备告警)
		pingInMap.put(Constants.PINGIN,Constants.equipment_Warning); //交换机Ping成功::告警类别(设备告警)
		database_DisconnectMap.put(Constants.DATABASE_DISCONNECT,Constants.NMS_Warning); //数据库断开::告警类别(网管告警)
		database_ConnectMap.put(Constants.DATABASE_CONNECT, Constants.NMS_Warning); //数据库连接::告警类别(网管告警)
		fep_DisconnectMap.put(Constants.FEP_DISCONNECT,Constants.NMS_Warning); //前置机断开::告警类别(网管告警)
		fep_ConnectMap.put(Constants.FEP_CONNECT,Constants.NMS_Warning); //前置机连接::告警类别(网管告警)
		syslogMap.put(Constants.SYSLOG, Constants.protocol_Warning);     //syslog::告警类别(协议告警)
		otherWarningMap.put(Constants.OTHERWARNING,Constants.other_Warning); //其它告警::告警类别(其它告警)
		
		
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
	 * 得到告警类别
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
