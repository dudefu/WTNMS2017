package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

/**
 * �ز����˿�����
 */
@Component(CarrierPortCategory.ID)
public class CarrierPortCategory {
	
	@PostConstruct
	protected void initialize() {
		Power = new StringInteger("������", 0);
		Up = new StringInteger("�������ݶ˿�", 1);
		Config = new StringInteger("���ÿ�", 2);
		Transport = new StringInteger("ת����", 3);
		Local = new StringInteger("�½��豸�˿�", 4);
		Standby = new StringInteger("���ö˿�", 5);
		list  = new ArrayList<StringInteger>(6);
		list.add(Power);
		list.add(Up);
		list.add(Config);
		list.add(Transport);
		list.add(Local);
		list.add(Standby);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 0: return Power;
			case 1: return Up;
			case 2: return Config;
			case 3: return Transport;
			case 4: return Local;
			case 5: return Standby;
			default: return Unknown;
		}
	}
	
	private List<StringInteger> list;
	public StringInteger Power;
	public StringInteger Up;
	public StringInteger Config;
	public StringInteger Transport;
	public StringInteger Local;
	public StringInteger Standby;
	public static final String ID = "carrierPortCategory";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}