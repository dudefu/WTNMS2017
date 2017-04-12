package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.DeviceType;

@Component(VirtualImageCategory.ID)
public class VirtualImageCategory {

	public static final String ID = "virtualImageCategory";
	
	private List<StringInteger> list;
	private StringInteger HuaWei;
	private StringInteger DongTu;
	private StringInteger WiFi;
	
	@PostConstruct
	protected void initialize() {
		HuaWei = new StringInteger("华为",DeviceType.IETH802);
		DongTu = new StringInteger("东土",DeviceType.IETH8008);
		WiFi = new StringInteger("WIFI",DeviceType.IETH804);
		list  = new ArrayList<StringInteger>(3);
		list.add(HuaWei);
		list.add(DongTu);
		list.add(WiFi);
	}
	
	public List<StringInteger> toList(){
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger getValue(int value){
		switch (value) {
		case DeviceType.IETH802:
			return HuaWei;
		case DeviceType.IETH8008:
			return DongTu;
		case DeviceType.IETH804:
			return WiFi;
		default:
			return null;
		}
	}
}
