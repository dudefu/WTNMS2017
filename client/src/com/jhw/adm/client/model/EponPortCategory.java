package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component(EponPortCategory.ID)
public class EponPortCategory {
	
	@PostConstruct
	protected void initialize() {
		POWER = new StringInteger("Íø¿Ú", G_PORT);
		FDDI = new StringInteger("¹â¿Ú", FDDI_PORT);
		list  = new ArrayList<StringInteger>(2);
		list.add(POWER);
		list.add(FDDI);
	}
	
	public List<StringInteger> toList() {		
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 0: return POWER;
			case 1: return FDDI;
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger POWER;
	public StringInteger FDDI;
	
	public static final int G_PORT = 0;
	public static final int FDDI_PORT = 1;
	public static final String ID = "eponPortCategory";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}
