package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

/**
 * �ز����˿���żУ��
 */
@Component(CarrierPortParity.ID)
public class CarrierPortParity {
	@PostConstruct
	protected void initialize() {
		no_Parity = new StringInteger("��У��", 1);
		odd = new StringInteger("��У��", 2);
		even = new StringInteger("żУ��", 3);
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
	public StringInteger no_Parity;  //��У��
	public StringInteger odd;   //��У��
	public StringInteger even;  //żУ��
	public static final String ID = "carrierPortParity";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}
