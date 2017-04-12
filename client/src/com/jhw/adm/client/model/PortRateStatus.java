package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component(PortRateStatus.ID)
public class PortRateStatus {
	public static final String ID = "portRateStatus";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("È«²¿", -2);
	
	private List<StringInteger> list;
	public StringInteger _10M;
	public StringInteger _100M;
	public StringInteger _1000M;
	public StringInteger AUTO;
	
	@PostConstruct
	protected void initialize() {
		_10M = new StringInteger("10M", 1);
		_100M = new StringInteger("100M", 2);
		_1000M = new StringInteger("1000M", 3);
		AUTO = new StringInteger("auto", 4);
		list  = new ArrayList<StringInteger>(4);
		list.add(_10M);
		list.add(_100M);
		list.add(_1000M);
		list.add(AUTO);
	}
	
	public List<StringInteger> toList() {		
		return Collections.unmodifiableList(list);
	}
	
	public List<StringInteger> toListIncludeAll() {
		List<StringInteger> all = new ArrayList<StringInteger>(list);
		all.add(0, ALL);
		return Collections.unmodifiableList(all);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 1: return _10M;
			case 2: return _100M;
			case 3: return _1000M;
			case 4: return AUTO;
			default: return Unknown;
		}
	}


}
