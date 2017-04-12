package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component(PortRateCategory.ID)
public class PortRateCategory {
	
	@PostConstruct
	protected void initialize() {
		NoSpeedLimit = new StringInteger("未限速", 0);
		Kbps_64 = new StringInteger("64Kbps", 64);
		Kbps_128 = new StringInteger("128Kbps", 128);
		Kbps_256 = new StringInteger("256Kbps", 256);
		Kbps_512 = new StringInteger("512Kbps", 512);
		Mbps_1 = new StringInteger("1Mbps", 1);
		Mbps_2 = new StringInteger("2Mbps", 2);
		Mbps_5 = new StringInteger("5Mbps", 5);
		Mbps_10 = new StringInteger("10Mbps", 10);
		Mbps_20 = new StringInteger("20Mbps", 20);
		Mbps_50 = new StringInteger("50Mbps", 50);
		Mbps_100 = new StringInteger("100Mbps", 100);
		Mbps_200 = new StringInteger("200Mbps", 200);
		Mbps_400 = new StringInteger("400Mbps", 400);
		Mbps_800 = new StringInteger("800Mbps", 800);
		Mbps_1000 = new StringInteger("1000Mbps", 1000);

		
		list  = new ArrayList<StringInteger>(16);
		list.add(NoSpeedLimit);
		list.add(Kbps_64);
		list.add(Kbps_128);
		list.add(Kbps_256);
		list.add(Kbps_512);
		list.add(Mbps_1);
		list.add(Mbps_2);
		list.add(Mbps_5);
		list.add(Mbps_10);
		list.add(Mbps_20);
		list.add(Mbps_50);
		list.add(Mbps_100);
		list.add(Mbps_200);
		list.add(Mbps_400);
		list.add(Mbps_800);
		list.add(Mbps_1000);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 0: return NoSpeedLimit;
			case 64: return Kbps_64;
			case 128: return Kbps_128;
			case 256: return Kbps_256;
			case 512: return Kbps_512;
			case 1: return Mbps_1;
			case 2: return Mbps_2;
			case 5: return Mbps_5;
			case 10: return Mbps_10;
			case 20: return Mbps_20;
			case 50: return Mbps_50;
			case 100: return Mbps_100;
			case 200: return Mbps_200;
			case 400: return Mbps_400;
			case 800: return Mbps_800;
			case 1000: return Mbps_1000;
			
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger NoSpeedLimit;
	public StringInteger Kbps_64;
	public StringInteger Kbps_128;
	public StringInteger Kbps_256;
	public StringInteger Kbps_512;
	public StringInteger Mbps_1;
	public StringInteger Mbps_2;
	public StringInteger Mbps_5;
	public StringInteger Mbps_10;
	public StringInteger Mbps_20;
	public StringInteger Mbps_50;
	public StringInteger Mbps_100;
	public StringInteger Mbps_200;
	public StringInteger Mbps_400;
	public StringInteger Mbps_800;
	public StringInteger Mbps_1000;
	
	
	public static final String ID = "portRateCategory";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("全部", -2);
}
