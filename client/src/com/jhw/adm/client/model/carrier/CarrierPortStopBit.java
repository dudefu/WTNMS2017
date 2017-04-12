package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

/**
 * 载波机端口停止位
 */
@Component(CarrierPortStopBit.ID)
public class CarrierPortStopBit {
	@PostConstruct
	protected void initialize() {
		data1 = new StringInteger("1位", 1);
		data2 = new StringInteger("2位", 2);
		list  = new ArrayList<StringInteger>(3);
		list.add(data1);
		list.add(data2);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 1: return data1;
			case 2: return data2;
			default: return Unknown;
		}
	}
	
	private List<StringInteger> list;
	public StringInteger data1;  //1位
	public StringInteger data2;   //2位
	public static final String ID = "carrierPortStopBit";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}
