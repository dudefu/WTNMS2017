package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

/**
 * 载波机端口波特率
 */
@Component(CarrierPortBaudRate.ID)
public class CarrierPortBaudRate {
	
	@PostConstruct
	protected void initialize() {
		R1200 = new StringInteger("1200", 1);
		R2400 = new StringInteger("2400", 2);
		R4800 = new StringInteger("4800", 3);
		R9600 = new StringInteger("9600", 4);
		R19200 = new StringInteger("19200", 5);
		R38400 = new StringInteger("38400", 6);
		R57600 = new StringInteger("57600", 7);
		R115200 = new StringInteger("115200", 8);
		list  = new ArrayList<StringInteger>(8);
		list.add(R1200);
		list.add(R2400);
		list.add(R4800);
		list.add(R9600);
		list.add(R19200);
		list.add(R38400);
		list.add(R57600);
		list.add(R115200);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 1: return R1200;
			case 2: return R2400;
			case 3: return R4800;
			case 4: return R9600;
			case 5: return R19200;
			case 6: return R38400;
			case 7: return R57600;
			case 8: return R115200;
			default: return Unknown;
		}
	}
	
	private List<StringInteger> list;
	public StringInteger R1200;
	public StringInteger R2400;
	public StringInteger R4800;
	public StringInteger R9600;
	public StringInteger R19200;
	public StringInteger R38400;
	public StringInteger R57600;
	public StringInteger R115200;
	public static final String ID = "carrierPortBaudRate";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}