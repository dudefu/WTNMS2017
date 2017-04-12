package com.jhw.adm.server.entity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarningEventDescription {
	private static WarningEventDescription instance ;
	private Map<Integer, String> coldStartMap;              	//冷启动
	private Map<Integer, String> warmStartMap;				        //热启动
	private Map<Integer, String> linkDownMap;				        //端口断开
	private Map<Integer, String> linkUpMap;					        //端口连接
	private Map<Integer, String> remonThingMap;				//超过端口流量上下限值时候发送的trap
	private Map<Integer, String> pingOutMap;                 //交换机Ping不通
	private Map<Integer, String> pingInMap;                  //交换机Ping成功
	private Map<Integer, String> database_DisconnectMap;     //数据库断开
	private Map<Integer, String> database_ConnectMap;        //数据库连接
	private Map<Integer, String> fep_DisconnectMap;          //前置机断开
	private Map<Integer, String> fep_ConnectMap;          	//前置机连接
	private Map<Integer, String> syslogMap;                  //syslog
	
	private List<Map> list = new ArrayList<Map>();
	
	private WarningEventDescription(){
		coldStartMap = new HashMap<Integer,String>();
		warmStartMap = new HashMap<Integer,String>();
		linkDownMap = new HashMap<Integer,String>();
		linkUpMap = new HashMap<Integer,String>();
		remonThingMap = new HashMap<Integer,String>();
		pingOutMap = new HashMap<Integer,String>();
		pingInMap = new HashMap<Integer,String>();
		database_DisconnectMap = new HashMap<Integer,String>();
		database_ConnectMap = new HashMap<Integer,String>();
		fep_DisconnectMap = new HashMap<Integer,String>();
		fep_ConnectMap = new HashMap<Integer,String>();
		syslogMap = new HashMap<Integer,String>();
		
		coldStartMap.put(Constants.COLDSTART, "%s");
		warmStartMap.put(Constants.WARMSTART, "%s");
		linkDownMap.put(Constants.LINKDOWN, "端口(%s)断开");  //端口断开
		linkUpMap.put(Constants.LINKUP, "端口(%s)连接");      //端口连接
		remonThingMap.put(Constants.REMONTHING, "温度告警(%s)");
		pingOutMap.put(Constants.PINGOUT, "设备Ping不通");
		pingInMap.put(Constants.PINGIN, "设备Ping成功");
		database_DisconnectMap.put(Constants.DATABASE_DISCONNECT, "数据库断开");
		database_ConnectMap.put(Constants.DATABASE_CONNECT, "数据库连接");
		fep_DisconnectMap.put(Constants.FEP_DISCONNECT, "前置机断开%s");
		fep_ConnectMap.put(Constants.FEP_CONNECT, "前置机连接%s");
		syslogMap.put(Constants.SYSLOG, "syslog");
		
		list.add(coldStartMap);
		list.add(warmStartMap);
		list.add(linkDownMap);
		list.add(linkUpMap);
		list.add(remonThingMap);
		list.add(pingOutMap);
		list.add(pingInMap);
		list.add(database_DisconnectMap);
		list.add(database_ConnectMap);
		list.add(fep_DisconnectMap);
		list.add(fep_ConnectMap);
		list.add(syslogMap);
	}
	
	public static WarningEventDescription getInstance(){
		if (null == instance){
			instance = new WarningEventDescription();
		}
		return instance;
	}
	
	/**
	 * 得到告警描述
	 * @param warningEvent
	 * @return
	 */
	public String getWarningEventDescription(int warningEvent){
		String warningEventDescription = "";
		for(Map map : list){
			Object object = map.get(warningEvent);
			if (object != null){
				warningEventDescription = String.valueOf(object);
				break;
			}
		}
		
		return warningEventDescription;
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 * 
	 * 新增将十六字节转换为字符串：toStrHex(String str) 、 toStringHex(String s)
	 */
	public String toStrHex(String str){
		String[] s = str.split(":");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length - 4; i++) {
			sb.append(s[i]);
		}
		String str1 = toStringHex(sb.toString()) + "\u2103 !";
		return str1;
	}
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
						i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}
	
	
}
