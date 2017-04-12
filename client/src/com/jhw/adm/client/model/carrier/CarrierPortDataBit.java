package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

/**
 * 载波机端口数据位
 */
@Component(CarrierPortDataBit.ID)
public class CarrierPortDataBit {
	@PostConstruct
	protected void initialize() {
		data6 = new StringInteger("6位", 1);
		data7 = new StringInteger("7位", 2);
		data8 = new StringInteger("8位", 3);
		list  = new ArrayList<StringInteger>(3);
		list.add(data6);
		list.add(data7);
		list.add(data8);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 1: return data6;
			case 2: return data7;
			case 3: return data8;
			default: return Unknown;
		}
	}
	
	private List<StringInteger> list;
	public StringInteger data6;  //6位
	public StringInteger data7;   //7位
	public StringInteger data8;  //8位
	public static final String ID = "carrierPortDataBit";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}
