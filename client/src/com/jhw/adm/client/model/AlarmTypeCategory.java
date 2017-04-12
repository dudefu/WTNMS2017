package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.Constants;

@Component(AlarmTypeCategory.ID)
public class AlarmTypeCategory {

	public static final String ID = "alarmTypeCategory";
	private List<StringInteger> list = null;
	private StringInteger equipmentWarning = null;
//	private StringInteger baordWarning = null;
	private StringInteger portWarning = null;
//	private StringInteger facilityWarning = null;
	private StringInteger protocolWarning = null;
//	private StringInteger securityWarning = null;
	private StringInteger performanceWarning = null;
	private StringInteger NMSWarning = null;
//	private StringInteger otherWarning = null;
	
	@PostConstruct
	protected void initialize(){
		equipmentWarning = new StringInteger("设备", Constants.equipment_Warning);
//		baordWarning = new StringInteger("板卡", Constants.baord_Warning);
		portWarning = new StringInteger("端口", Constants.port_Warning);
//		facilityWarning = new StringInteger("通信", Constants.facility_Warning);
		protocolWarning = new StringInteger("协议", Constants.protocol_Warning);
//		securityWarning = new StringInteger("安全", Constants.security_Warning);
		performanceWarning = new StringInteger("性能", Constants.performance_Warning);
		NMSWarning = new StringInteger("网管", Constants.NMS_Warning);
//		otherWarning = new StringInteger("其它", Constants.other_Warning);
		
		list = new ArrayList<StringInteger>();
		list.add(equipmentWarning);
//		list.add(baordWarning);
		list.add(portWarning);
//		list.add(facilityWarning);
		list.add(protocolWarning);
//		list.add(securityWarning);
		list.add(performanceWarning);
		list.add(NMSWarning);
//		list.add(otherWarning);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}

	public StringInteger get(int value) {
		switch (value) {
			case Constants.equipment_Warning: return equipmentWarning;
//			case Constants.baord_Warning: return baordWarning;
			case Constants.port_Warning: return portWarning;
//			case Constants.facility_Warning: return facilityWarning;
			case Constants.protocol_Warning: return protocolWarning;
//			case Constants.security_Warning: return securityWarning;
			case Constants.performance_Warning: return performanceWarning;
			case Constants.NMS_Warning: return NMSWarning;
//			case Constants.other_Warning: return otherWarning;
			default: return equipmentWarning;
		}
	}
}
