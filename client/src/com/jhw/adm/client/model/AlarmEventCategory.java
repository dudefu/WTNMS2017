package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.Constants;

@Component(AlarmEventCategory.ID)
public class AlarmEventCategory {

	public static final String ID = "alarmEventCategory";
	private List<StringInteger> list = null;
	private StringInteger coldStartWarning = null;
	private StringInteger hotStartWarning = null;
	private StringInteger linkDownWarning = null;
	private StringInteger linkUpWarning = null;
	private StringInteger rmonWarning = null;
	private StringInteger pingOutWarning = null;
	private StringInteger pingInWarning = null;
//	private StringInteger datebaseDisconnectWarning = null;
//	private StringInteger datebaseConnectWarning = null;
	private StringInteger fepDisconnectWarning = null;
	private StringInteger fepConnectWarning = null;
	private StringInteger syslogWarning = null;
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	
	@PostConstruct
	protected void initialize(){
		coldStartWarning = new StringInteger("������", Constants.COLDSTART);
		hotStartWarning = new StringInteger("������", Constants.WARMSTART);
		linkDownWarning = new StringInteger("�˿ڶϿ�", Constants.LINKDOWN);
		linkUpWarning = new StringInteger("�˿�����", Constants.LINKUP);
		rmonWarning = new StringInteger("���ܸ澯", Constants.REMONTHING);
		pingOutWarning = new StringInteger("Ping��ͨ", Constants.PINGOUT);
		pingInWarning = new StringInteger("Ping�ɹ�", Constants.PINGIN);
//		datebaseDisconnectWarning = new StringInteger("���ݿ�Ͽ�", Constants.DATABASE_DISCONNECT);
//		datebaseConnectWarning = new StringInteger("���ݿ�����", Constants.DATABASE_CONNECT);
		fepDisconnectWarning = new StringInteger("ǰ�û��Ͽ�", Constants.FEP_DISCONNECT);
		fepConnectWarning = new StringInteger("ǰ�û�����", Constants.FEP_CONNECT);
		syslogWarning = new StringInteger("SYSLOG", Constants.SYSLOG);
		
		list = new ArrayList<StringInteger>();
		list.add(coldStartWarning);
		list.add(hotStartWarning);
		list.add(linkDownWarning);
		list.add(linkUpWarning);
		list.add(rmonWarning);
		list.add(pingOutWarning);
		list.add(pingInWarning);
//		list.add(datebaseDisconnectWarning);
//		list.add(datebaseConnectWarning);
		list.add(fepDisconnectWarning);
		list.add(fepConnectWarning);
		list.add(syslogWarning);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}

	public StringInteger get(int value) {
		switch (value) {
			case Constants.COLDSTART: return coldStartWarning;
			case Constants.WARMSTART: return hotStartWarning;
			case Constants.LINKDOWN: return linkDownWarning;
			case Constants.LINKUP: return linkUpWarning;
			case Constants.REMONTHING: return rmonWarning;
			case Constants.PINGOUT: return pingOutWarning;
			case Constants.PINGIN: return pingInWarning;
//			case Constants.DATABASE_DISCONNECT: return datebaseDisconnectWarning;
//			case Constants.DATABASE_CONNECT: return datebaseConnectWarning;
			case Constants.FEP_DISCONNECT: return fepDisconnectWarning;
			case Constants.FEP_CONNECT: return fepConnectWarning;
			case Constants.SYSLOG: return syslogWarning;
			default: return Unknown;
		}
	}
}
