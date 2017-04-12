package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * 载波机类型
 */
@Component(LinkCategory.ID)
public class LinkCategory {
	
	@PostConstruct
	protected void initialize() {
		Ethernet = new StringInteger("网线", ETHERNET);
		PowerLine = new StringInteger("电力线", POWER_LINE);
		Wireless = new StringInteger("Wireless", WIRELESS);
		SerialLine = new StringInteger("串口线", SERIAL_LINE);
		Fddi = new StringInteger("光纤", FDDI);
		list  = new ArrayList<StringInteger>(5);
		list.add(Ethernet);
		list.add(PowerLine);
		list.add(Wireless);
		list.add(SerialLine);
		list.add(Fddi);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case ETHERNET: return Ethernet;
			case POWER_LINE: return PowerLine;
			case WIRELESS: return Wireless;
			case SERIAL_LINE: return SerialLine;
			case FDDI: return Fddi;
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger Ethernet;
	public StringInteger PowerLine;
	public StringInteger Wireless;
	public StringInteger SerialLine;
	public StringInteger Fddi;
	
	public static final int ETHERNET = 1;
	public static final int POWER_LINE = 2;
	public static final int WIRELESS = 3;
	public static final int SERIAL_LINE = 4;
	public static final int FDDI = 5;
	
	public static final String ID = "linkCategory";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("全部", -2);
}