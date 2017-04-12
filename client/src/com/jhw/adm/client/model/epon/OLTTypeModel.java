package com.jhw.adm.client.model.epon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.server.entity.util.DeviceType;

@Component(OLTTypeModel.ID)
public class OLTTypeModel {
	public static final String ID = "OLTTypeModel";
	
	private List<StringInteger> list;
	public StringInteger EPON8506;
	public StringInteger EPON8000;
	public StringInteger IEL3010_HV;
	private Map<Integer, String> ImageMap;
	
	public final StringInteger Unknown = new StringInteger("·ÇÍø¹ÜÐÍ", -1);
	
	@PostConstruct
	protected void initialize() {
		EPON8000 = new StringInteger("IEL8000", DeviceType.EPON8000);
		EPON8506 = new StringInteger("IEL8506", DeviceType.EPON8506);
		IEL3010_HV = new StringInteger("IEL3010", DeviceType.IEL3010_HV);
		list  = new ArrayList<StringInteger>();
		list.add(EPON8000);
		list.add(EPON8506);
		list.add(IEL3010_HV);
		list.add(Unknown);
		
		ImageMap = new HashMap<Integer, String>();
		
		ImageMap.put(DeviceType.EPON8000, NetworkConstants.EPON8000);
		ImageMap.put(DeviceType.EPON8506, NetworkConstants.EPON8506);
		ImageMap.put(DeviceType.IEL3010_HV, NetworkConstants.IEL3010_HV);
	}
	
	public List<StringInteger> toList() {		
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case DeviceType.EPON8000: return EPON8000;
			case DeviceType.EPON8506: return EPON8506;
			case DeviceType.IEL3010_HV: return IEL3010_HV;
			default:return Unknown;
		}
	}
	
	public String getImageName(Integer number) {
		if (ImageMap.containsKey(number)) {
			return ImageMap.get(number);
		} else {
			return NetworkConstants.SWITCHER;
		}
	}
}
