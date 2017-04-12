package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

/**
 * 载波机端口奇偶校验
 */
@Component(CarrierPortParity.ID)
public class CarrierPortParity {
	@PostConstruct
	protected void initialize() {
		no_Parity = new StringInteger("不校验", 1);
		odd = new StringInteger("奇校验", 2);
		even = new StringInteger("偶校验", 3);
		list  = new ArrayList<StringInteger>(3);
		list.add(no_Parity);
		list.add(odd);
		list.add(even);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 1: return no_Parity;
			case 2: return odd;
			case 3: return even;
			default: return Unknown;
		}
	}
	
	private List<StringInteger> list;
	public StringInteger no_Parity;  //不校验
	public StringInteger odd;   //奇校验
	public StringInteger even;  //偶校验
	public static final String ID = "carrierPortParity";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}
