package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

/**
 * �ز����˿�ֹͣλ
 */
@Component(CarrierPortStopBit.ID)
public class CarrierPortStopBit {
	@PostConstruct
	protected void initialize() {
		data1 = new StringInteger("1λ", 1);
		data2 = new StringInteger("2λ", 2);
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
	public StringInteger data1;  //1λ
	public StringInteger data2;   //2λ
	public static final String ID = "carrierPortStopBit";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}
