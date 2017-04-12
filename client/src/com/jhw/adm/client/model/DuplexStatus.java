package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component(DuplexStatus.ID)
public class DuplexStatus {
	public static final String ID = "duplexStatus";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("È«²¿", -2);
	
	private List<StringInteger> list;
	public StringInteger FULL;
	public StringInteger HALF;
	public StringInteger AUTO;
	
	@PostConstruct
	protected void initialize() {
		FULL = new StringInteger("full", 1);
		HALF = new StringInteger("half", 2);
		AUTO = new StringInteger("auto", 3);
		list  = new ArrayList<StringInteger>(3);
		list.add(FULL);
		list.add(HALF);
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
			case 1: return FULL;
			case 2: return HALF;
			case 3: return AUTO;
			default: return Unknown;
		}
	}
}
