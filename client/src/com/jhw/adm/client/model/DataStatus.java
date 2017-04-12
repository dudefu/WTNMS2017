package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.Constants;

/**
 * 参数配置状态
 */
@Component(DataStatus.ID)
public class DataStatus {
	
	@PostConstruct
	protected void initialize() {
		ADM = new StringInteger("网管侧", Constants.ISSUEDADM);
		DEVICE = new StringInteger("设备侧", Constants.ISSUEDDEVICE);
		list  = new ArrayList<StringInteger>(2);
		list.add(ADM);
		list.add(DEVICE);
	}
	
	public List<StringInteger> toList() {		
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case Constants.ISSUEDADM: return ADM;
			case Constants.ISSUEDDEVICE: return DEVICE;
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger ADM;
	public StringInteger DEVICE;
	
	public static final String ID = "dataStatus";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}