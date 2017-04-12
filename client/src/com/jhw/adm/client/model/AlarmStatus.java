package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.Constants;

/**
 * 告警状态
 */
@Component(AlarmStatus.ID)
public class AlarmStatus {
	
	@PostConstruct
	protected void initialize() {
		NEW = new StringInteger("未处理", Constants.NEW);
		FIXING = new StringInteger("处理中", Constants.FIXING);
		CLOSE = new StringInteger("已处理", Constants.CLOSE);
		list  = new ArrayList<StringInteger>(3);
		list.add(NEW);
		list.add(FIXING);
		list.add(CLOSE);
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
			case Constants.NEW: return NEW;
			case Constants.FIXING: return FIXING;
			case Constants.CLOSE: return CLOSE;
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger NEW;
	public StringInteger FIXING;
	public StringInteger CLOSE;
	
	public static final String ID = "alarmStatus";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("全部", -2);
}