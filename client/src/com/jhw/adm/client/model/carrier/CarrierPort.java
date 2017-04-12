package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

/**
 * 载波机端口
 */
@Component(CarrierPort.ID)
public class CarrierPort {
	
	@PostConstruct
	protected void initialize() {
		Console = new StringInteger("Console", 0);
		DEV1 = new StringInteger("DEV1", 1);
		DEV2 = new StringInteger("DEV2", 2);
		DEV3 = new StringInteger("DEV3", 3);
		DEV4 = new StringInteger("DEV4", 4);
		Acable1 = new StringInteger("Acable1", 5);
		Acable2 = new StringInteger("Acable2", 6);
		list  = new ArrayList<StringInteger>(7);
		list.add(Console);
		list.add(DEV1);
		list.add(DEV2);
		list.add(DEV3);
		list.add(DEV4);
		list.add(Acable1);
		list.add(Acable2);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 0: return Console;
			case 1: return DEV1;
			case 2: return DEV2;
			case 3: return DEV3;
			case 4: return DEV4;
			case 5: return Acable1;
			case 6: return Acable2;
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger Console;
	public StringInteger DEV1;
	public StringInteger DEV2;
	public StringInteger DEV3;
	public StringInteger DEV4;
	public StringInteger Acable1;
	public StringInteger Acable2;
	
	/**
	 * 默认端口，及与网管通讯的端口
	 * <br>
	 * 使用Console(0:配置口)
	 */
	public static final int DEFAULT_PORT = 0;
	
	/**
	 * 默认电力线连接端口
	 * <br>
	 * 使用Acable(5:)
	 */
	public static final int DEFAULT_POWER_PORT = 5;
	
	/**
	 * 默认串口线连接端口
	 * <br>
	 * 使用DEV1(1:)
	 */
	public static final int DEFAULT_SERIAL_PORT = 1;
	
	public static final String ID = "carrierPort";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}
