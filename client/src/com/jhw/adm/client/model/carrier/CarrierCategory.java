package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.server.entity.util.Constants;

/**
 * �ز�������
 */
@Component(CarrierCategory.ID)
public class CarrierCategory {
	
	@PostConstruct
	protected void initialize() {
		MASTER = new StringInteger("����", Constants.CENTER);
		SLAVE = new StringInteger("�ն�", Constants.TERMINAL);
		list  = new ArrayList<StringInteger>(2);
		list.add(MASTER);
		list.add(SLAVE);
	}
	
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 1: return MASTER;
			case 2: return SLAVE;
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger MASTER;
	public StringInteger SLAVE;
	public static final String ID = "carrierCategory";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("ȫ��", -2);
}