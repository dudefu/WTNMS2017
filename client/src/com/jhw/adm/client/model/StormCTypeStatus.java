package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component(StormCTypeStatus.ID)
public class StormCTypeStatus {
	public static final String ID = "stormCTypeStatus";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("全部", -2);
	
	private List<StringInteger> list;
	public StringInteger BROADCASE;
	public StringInteger MULTICASE;
	public StringInteger UNKNOWCASE;
	
	@PostConstruct
	protected void initialize() {
		BROADCASE = new StringInteger("广播", 1);
		MULTICASE = new StringInteger("多播", 2);
		UNKNOWCASE = new StringInteger("未知单播", 3);
		list  = new ArrayList<StringInteger>(3);
		list.add(BROADCASE);
		list.add(MULTICASE);
		list.add(UNKNOWCASE);
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
			case 1: return BROADCASE;
			case 2: return MULTICASE;
			case 3: return UNKNOWCASE;
			default: return Unknown;
		}
	}




}
