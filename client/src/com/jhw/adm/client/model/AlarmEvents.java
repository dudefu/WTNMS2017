package com.jhw.adm.client.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.Constants;

/**
 * 告警事件
 */
@Component(AlarmEvents.ID)
public class AlarmEvents {
	
	@PostConstruct
	protected void initialize() {
		COLDSTART = new StringInteger("冷启动", Constants.COLDSTART);
		WARMSTART = new StringInteger("热启动", Constants.WARMSTART);
		LINKDOWN = new StringInteger("端口断开", Constants.LINKDOWN);
		LINKUP = new StringInteger("端口连接", Constants.LINKUP);
		AUTHENTICATIONFAILURE = new StringInteger("认证失败", Constants.AUTHENTICATIONFAILURE);
		EGPNEIGHORLOSS = new StringInteger("EGPNEIGHORLOSS", Constants.EGPNEIGHORLOSS);
		ENTERPRISESPECIFIC = new StringInteger("ENTERPRISESPECIFIC", Constants.ENTERPRISESPECIFIC);
		REMONTHING = new StringInteger("流量异常", Constants.REMONTHING);
		list  = new ArrayList<StringInteger>(2);
		list.add(COLDSTART);
		list.add(WARMSTART);
		list.add(LINKDOWN);
		list.add(LINKUP);
		list.add(AUTHENTICATIONFAILURE);
		list.add(EGPNEIGHORLOSS);
		list.add(ENTERPRISESPECIFIC);
		list.add(REMONTHING);
	}
	
	public List<StringInteger> toList() {		
		return Collections.unmodifiableList(list);
	}
	
	public List<StringInteger> toListIncludeAll() {
		List<StringInteger> all = new ArrayList<StringInteger>(list);
		all.add(0, ALL);
		return Collections.unmodifiableList(all);
	}
	
	public Color getColor(int value) {
		switch (value) {
			case Constants.COLDSTART: return Color.RED;
			case Constants.WARMSTART: return Color.RED;
			case Constants.LINKDOWN: return Color.RED;
			case Constants.LINKUP: return Color.RED;
			case Constants.AUTHENTICATIONFAILURE: return Color.RED;
			case Constants.EGPNEIGHORLOSS: return Color.RED;
			case Constants.ENTERPRISESPECIFIC: return Color.RED;
			case Constants.REMONTHING: return Color.RED;
			default: return Color.GRAY;
		}
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case Constants.COLDSTART: return COLDSTART;
			case Constants.WARMSTART: return WARMSTART;
			case Constants.LINKDOWN: return LINKDOWN;
			case Constants.LINKUP: return LINKUP;
			case Constants.AUTHENTICATIONFAILURE: return AUTHENTICATIONFAILURE;
			case Constants.EGPNEIGHORLOSS: return EGPNEIGHORLOSS;
			case Constants.ENTERPRISESPECIFIC: return ENTERPRISESPECIFIC;
			case Constants.REMONTHING: return REMONTHING;
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger COLDSTART;
	public StringInteger WARMSTART;
	public StringInteger LINKDOWN;
	public StringInteger LINKUP;
	public StringInteger AUTHENTICATIONFAILURE;
	public StringInteger EGPNEIGHORLOSS;
	public StringInteger ENTERPRISESPECIFIC;
	public StringInteger REMONTHING;
	
	public static final String ID = "alarmEvents";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("全部", -2);
}