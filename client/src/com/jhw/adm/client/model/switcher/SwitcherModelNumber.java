package com.jhw.adm.client.model.switcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.model.SwitchPortCategory;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.util.DeviceType;
import com.jhw.adm.server.entity.util.SwitchSerialPort;

/**
 * 二层交换机型号
 */
@Component(SwitcherModelNumber.ID)
public class SwitcherModelNumber {
	
	@PostConstruct
	protected void initialize() {
		IETH802 = new StringInteger("IETH802", DeviceType.IETH802);
		IETH804_H = new StringInteger("IETH804-H", DeviceType.IETH804_H);
		IETH8008 = new StringInteger("IETH8008", DeviceType.IETH8008);
		IETH8008_U = new StringInteger("IETH8008-U", DeviceType.IETH8008_U);
		IETH9028 = new StringInteger("IETH9028", DeviceType.IETH9028);
		IETH9307 = new StringInteger("IETH9307", DeviceType.IETH9307);
		
		IETH3424 = new StringInteger("IETH3424", DeviceType.IETH3424);
		IEL3010_HV = new StringInteger("IEL3010_HV", DeviceType.IEL3010_HV);
		EPON8506 = new StringInteger("EPON8506", DeviceType.EPON8506);
		EPON8000 = new StringInteger("EPON8000", DeviceType.EPON8000);
		MOXA8000 = new StringInteger("MOXA8000", DeviceType.MOXA8000);
		KYLAND3016 = new StringInteger("KYLAND3016", DeviceType.KYLAND3016);
		
		list  = new ArrayList<StringInteger>(13);
		list.add(IETH802);
		list.add(IETH804_H);
		list.add(IETH8008);
		list.add(IETH8008_U);
		list.add(IETH9028);
		list.add(IETH9307);
		
		list.add(IETH3424);
		list.add(IEL3010_HV);
		list.add(EPON8506);
		list.add(EPON8000);
		list.add(MOXA8000);
		list.add(KYLAND3016);
		
		list.add(Unknown);
		
		ImageMap = new HashMap<Integer, String>();		
		
//		ImageMap.put("IETH2224", NetworkConstants.IETH2224);
//		ImageMap.put("IETH2424", NetworkConstants.IETH2424);
//		ImageMap.put("IETH2307", NetworkConstants.IETH2307);
//		ImageMap.put("IETH2900", NetworkConstants.IETH2900);		
//		ImageMap.put("IETH2026", NetworkConstants.IETH2026);
//		ImageMap.put("IETH2022", NetworkConstants.IETH2022);
//		ImageMap.put("IETH2018", NetworkConstants.IETH2018);
//		ImageMap.put("IETH2009", NetworkConstants.IETH2009);
//		
//		ImageMap.put("IETH8026", NetworkConstants.IETH8026);
//		ImageMap.put("IETH8022", NetworkConstants.IETH8022);
//		ImageMap.put("IETH8018", NetworkConstants.IETH8018);
		ImageMap.put(DeviceType.IETH8008, NetworkConstants.IETH8008);
		ImageMap.put(DeviceType.IETH8008_U, NetworkConstants.IETH8008U);
		
//		ImageMap.put("IETH9424", NetworkConstants.IETH9424);
//		ImageMap.put("IETH9224", NetworkConstants.IETH9224);
//		ImageMap.put("IETH9216", NetworkConstants.IETH9216);
		ImageMap.put(DeviceType.IETH9307, NetworkConstants.IETH9307);
//		ImageMap.put("IETH9900", NetworkConstants.IETH9900);
		
		ImageMap.put(DeviceType.IETH802, NetworkConstants.IETH802);
//		ImageMap.put("IETH804", NetworkConstants.IETH804);
		ImageMap.put(DeviceType.IETH804_H, NetworkConstants.IETH804);
		ImageMap.put(DeviceType.IETH9028, NetworkConstants.IETH9028);
		
//		ImageMap.put("IETH3424", NetworkConstants.IETH3424);
//		ImageMap.put("IETH4424", NetworkConstants.IETH4424);
	}
	
	public void createPort(SwitchNodeEntity switcher) {
		String category = switcher.getType();
		
		if (category.equals("IETH802")) {
			appendPort(switcher, 8);
		} else if (category.equals("IETH804-H")) {
			appendPort(switcher, 8);
		} else if (category.equals("IETH8008")) {
			append8008Port(switcher);
		} else if (category.equals("IETH8008-U")) {
			append8008UPort(switcher);
		}
	}
	
	public void createSerialPort(SwitchNodeEntity switcher) {
		String category = switcher.getType();
		
		if (category.equals("IETH802")) {
			appendSerialPort(switcher, 1);
		} else if (category.equals("IETH804-H")) {
			appendSerialPort(switcher, 4);
		}
	}
	
	/**
	 * 给交换机添加串口
	 * @param switcher 交换机
	 * @param count 串口数量
	 */
	private void appendSerialPort(SwitchNodeEntity switcher, int count) {
		Set<SwitchSerialPort> listOfPort = new HashSet<SwitchSerialPort>();
		for (int i = 1; i < count + 1; i++) {
			SwitchSerialPort serialPort = new SwitchSerialPort();
			serialPort.setPortNo(i);
			serialPort.setSwitchNode(switcher);
			listOfPort.add(serialPort);
		}
		switcher.setSerialPorts(listOfPort);
	}
	
	private void append8008Port(SwitchNodeEntity switcher) {
		Set<SwitchPortEntity> listOfPort = switcher.getPorts();
		
		for (int i = 1; i <= 8; i++) {
			SwitchPortEntity port = new SwitchPortEntity();
			port.setPortNO(i);
			port.setConnected(false);
			port.setWorked(false);
			port.setConfigMode("auto");
			port.setCurrentMode("auto");
			port.setFlowControl(false);
			port.setAbandonSetting("none");
			port.setSwitchNode(switcher);
			if (i == 3 || i == 4) {
				port.setType(SwitchPortCategory.FDDI_PORT);
			} else {
				port.setType(SwitchPortCategory.POWER_PORT);
			}
			listOfPort.add(port);
		}
		switcher.setPorts(listOfPort);
	}
	
	private void append8008UPort(SwitchNodeEntity switcher) {
		Set<SwitchPortEntity> listOfPort = switcher.getPorts();
		
		for (int i = 1; i <= 8; i++) {
			SwitchPortEntity port = new SwitchPortEntity();
			port.setPortNO(i);
			port.setConnected(false);
			port.setWorked(false);
			port.setConfigMode("auto");
			port.setCurrentMode("auto");
			port.setFlowControl(false);
			port.setAbandonSetting("none");
			port.setSwitchNode(switcher);
			if (i > 4) {
				port.setType(SwitchPortCategory.POWER_PORT);
			} else {
				port.setType(SwitchPortCategory.FDDI_PORT);
			}
			listOfPort.add(port);
		}
		switcher.setPorts(listOfPort);
	}
	
	private void appendPort(SwitchNodeEntity switcher, int count) {
		Set<SwitchPortEntity> listOfPort = switcher.getPorts();
		for (int i = 1; i <= count; i++) {
			SwitchPortEntity port = new SwitchPortEntity();
			port.setPortNO(i);
			port.setConnected(false);
			port.setWorked(false);
			port.setConfigMode("auto");
			port.setCurrentMode("auto");
			port.setFlowControl(false);
			port.setAbandonSetting("none");
			port.setSwitchNode(switcher);
			if (i < 5) {
				port.setType(SwitchPortCategory.FDDI_PORT);
			} else {
				port.setType(SwitchPortCategory.POWER_PORT);
			}
			listOfPort.add(port);
		}
		switcher.setPorts(listOfPort);
	}
	
	public List<StringInteger> toList() {		
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case DeviceType.IETH802: return IETH802;
			case DeviceType.IETH804_H: return IETH804_H;
			case DeviceType.IETH8008: return IETH8008;
			case DeviceType.IETH8008_U: return IETH8008_U;
			case DeviceType.IETH9028: return IETH9028;
			case DeviceType.IETH9307: return IETH9307;
			
			case DeviceType.IETH3424: return IETH3424;
			case DeviceType.IEL3010_HV: return IEL3010_HV;
			case DeviceType.EPON8506: return EPON8506;
			case DeviceType.EPON8000: return EPON8000;
			case DeviceType.MOXA8000: return MOXA8000;
			case DeviceType.KYLAND3016: return KYLAND3016;
			
			default: return Unknown;
		}
	}
	
	public String getImageName(Integer number) {
		if (ImageMap.containsKey(number)) {
			return ImageMap.get(number);
		} else {
			return NetworkConstants.SWITCHER;
		}
	}

	private Map<Integer, String> ImageMap;
	private List<StringInteger> list;
	public StringInteger IETH802;
	public StringInteger IETH804_H;
	public StringInteger IETH8008;
	public StringInteger IETH8008_U;
	public StringInteger IETH9028;
	public StringInteger IETH9307;
	
	public StringInteger IETH3424;   //三层
	public StringInteger IEL3010_HV; //OLT
	public StringInteger EPON8506;   //EPON
	public StringInteger EPON8000;   //epon8000
	public StringInteger MOXA8000;   //MOXA8000系列
	public StringInteger KYLAND3016; //KYLAND3016系列
	
	
	public static final String ID = "switcherModelNumber";
	public final StringInteger Unknown = new StringInteger("非网管型", -1);
}