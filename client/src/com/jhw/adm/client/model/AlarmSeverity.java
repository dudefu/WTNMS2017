package com.jhw.adm.client.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.Constants;

/**
 * 告警级别
 */
@Component(AlarmSeverity.ID)
public class AlarmSeverity {
	
	@PostConstruct
	protected void initialize() {
		URGENCY = new StringInteger("紧急", (int)Constants.URGENCY);
		SERIOUS = new StringInteger("严重", (int)Constants.SERIOUS);
		INFORM = new StringInteger("通知", (int)Constants.INFORM);
		GENERAL = new StringInteger("普通", (int)Constants.GENERAL);
		list  = new ArrayList<StringInteger>(4);
		list.add(URGENCY);
		list.add(SERIOUS);
		list.add(INFORM);
		list.add(GENERAL);
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
			case (int)Constants.URGENCY: return URGENCY;
			case (int)Constants.SERIOUS: return SERIOUS;
			case (int)Constants.INFORM: return INFORM;
			case (int)Constants.GENERAL: return GENERAL;
			default: return Unknown;
		}
	}
	
	public Color getColor(int value) {
		switch (value) {
			case (int)Constants.URGENCY: return Color.RED;
			case (int)Constants.SERIOUS: return Color.ORANGE;
			case (int)Constants.INFORM: return Color.YELLOW;
			case (int)Constants.GENERAL: return Color.CYAN;
			default: return Color.GRAY;
		}
	}

	private List<StringInteger> list;
	public StringInteger URGENCY;
	public StringInteger SERIOUS;
	public StringInteger INFORM;
	public StringInteger GENERAL;
	
	public static final String ID = "alarmSeverity";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("全部", -2);
}