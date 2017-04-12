package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.SwitchSerialPort;

@Component(SwitchSerialPortAdapterFactory.ID)
public class SwitchSerialPortAdapterFactory implements AdapterFactory {

	public static final String ID = "switchSerialPortAdapterFactory";
	public static Class<?> ADAPTER_CLASS = SwitchSerialPort.class;
	
	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(SwitchSerialPort.class)) {
			if (adaptableObject instanceof SwitchSerialPort) {
				adapterObject = adaptableObject;
			}
		}
		return adapterObject;
	}
}
