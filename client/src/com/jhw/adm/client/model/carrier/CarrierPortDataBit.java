package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

/**
 * �ز����˿�����λ
 */
@Component(CarrierPortDataBit.ID)
public class CarrierPortDataBit {
	@PostConstruct
	protected void initialize() {
		data6 = new StringInteger("6λ", 1);
		data7 = new StringInteger("7λ", 2);
		data8 = new StringInteger("8λ", 3);
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
	public StringInteger data6;  //6λ
	public StringInteger data7;   //7λ
	public StringInteger data8;  //8λ
	public static final String ID = "carrierPortDataBit";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}
