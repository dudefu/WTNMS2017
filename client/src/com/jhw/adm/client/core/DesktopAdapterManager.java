package com.jhw.adm.client.core;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 客户端桌面适配对象管理器
 */
@Component(DesktopAdapterManager.ID)
public class DesktopAdapterManager implements AdapterManager {
	public DesktopAdapterManager() {
		factoryMap = new HashMap<Class<?>, AdapterFactory>();
	}
	
	@PostConstruct
	public void initialize() {
		registerAdapters(TrapWarningEntityAdapterFactory.ADAPTER_CLASS, TrapWarningEntityAdapterFactory.ID);
		registerAdapters(TrapSimpleWarningAdapterFactory.ADAPTER_CLASS, TrapSimpleWarningAdapterFactory.ID);
		registerAdapters(SwitchEntityAdapterFactory.ADAPTER_CLASS, SwitchEntityAdapterFactory.ID);
		registerAdapters(SwitchNodeEntityAdapterFactory.ADAPTER_CLASS, SwitchNodeEntityAdapterFactory.ID);
		registerAdapters(SwitchPortEntityAdapterFactory.ADAPTER_CLASS, SwitchPortEntityAdapterFactory.ID);
		registerAdapters(SwitchSerialPortAdapterFactory.ADAPTER_CLASS, SwitchSerialPortAdapterFactory.ID);
		registerAdapters(LinkEntityAdapterFactory.ADAPTER_CLASS, LinkEntityAdapterFactory.ID);
		registerAdapters(CarrierEntityAdapterFactory.ADAPTER_CLASS, CarrierEntityAdapterFactory.ID);
		registerAdapters(CarrierNodeAdapterFactory.ADAPTER_CLASS, CarrierNodeAdapterFactory.ID);
		registerAdapters(GPRSNodeAdapterFactory.ADAPTER_CLASS, GPRSNodeAdapterFactory.ID);
		
		registerAdapters(OltNodeAdapterFactory.ADAPTER_CLASS, OltNodeAdapterFactory.ID);
		registerAdapters(OLTPortEntityAdapterFactory.ADAPTER_CLASS, OLTPortEntityAdapterFactory.ID);
		registerAdapters(OnuNodeAdapterFactory.ADAPTER_CLASS, OnuNodeAdapterFactory.ID);
		registerAdapters(OpticalSplitterAdapterFactory.ADAPTER_CLASS, OpticalSplitterAdapterFactory.ID);
		
		registerAdapters(OltEntityAdapterFactory.ADAPTER_CLASS, OltEntityAdapterFactory.ID);
		registerAdapters(OnuEntityAdapterFactory.ADAPTER_CLASS, OnuEntityAdapterFactory.ID);
		registerAdapters(EponSplitterAdapterFactory.ADAPTER_CLASS, EponSplitterAdapterFactory.ID);
		
		registerAdapters(FrontEndNodeAdapterFactory.ADAPTER_CLASS, FrontEndNodeAdapterFactory.ID);
		
		registerAdapters(Layer3SwitcherTopoAdapterFactory.ADAPTER_CLASS, Layer3SwitcherTopoAdapterFactory.ID);
		registerAdapters(Layer3SwitcherAdapterFactory.ADAPTER_CLASS, Layer3SwitcherAdapterFactory.ID);
		
		registerAdapters(SubnetTopoNodeAdapterFactory.ADAPTER_CLASS, SubnetTopoNodeAdapterFactory.ID);
		
		registerAdapters(VirtualNodeEntityAdapter.ADAPTER_CLASS, VirtualNodeEntityAdapter.ID);
		registerAdapters(CommentTopoNodeEntityAdapter.ADAPTER_CLASS,CommentTopoNodeEntityAdapter.ID);
	}

	public boolean registerAdapters(Class<?> adapterClass, String factoryId) {
		if (applicationContext.containsBean(factoryId)) {
			AdapterFactory factory = (AdapterFactory)applicationContext.getBean(factoryId);
		return registerAdapters(adapterClass, factory);
		} else {
			return false;
		}
	}

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (factoryMap.containsKey(adapterClass)) {
			AdapterFactory factory = factoryMap.get(adapterClass);
			adapterObject = factory.getAdapter(adaptableObject, adapterClass);
		}
		return adapterObject;
	}

	@Override
	public boolean registerAdapters(Class<?> adapterClass, AdapterFactory factory) {
		factoryMap.put(adapterClass, factory);
		return true;
	}

	@Resource
	private ApplicationContext applicationContext;
	
	private final Map<Class<?>, AdapterFactory> factoryMap;
	public static final String ID = "desktopAdapterManager";
}