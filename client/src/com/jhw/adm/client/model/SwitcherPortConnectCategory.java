package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Pair;

/**
 * 交换机端口模式
 */
@Component(SwitcherPortConnectCategory.ID)
public class SwitcherPortConnectCategory {
	
	@PostConstruct
	protected void initialize() {
		connected = new Pair<Boolean, String>(true, "连接");
		disconnected = new Pair<Boolean, String>(false, "断开");
		list  = new ArrayList<Pair<Boolean, String>>(2);
		list.add(connected);
		list.add(disconnected);
	}
	
	public List<Pair<Boolean, String>> toList() {		
		return Collections.unmodifiableList(list);
	}
	
	public Pair<Boolean, String> get(boolean value) {		
		return value ? connected : disconnected;
	}

	private List<Pair<Boolean, String>> list;
	public Pair<Boolean, String> connected;
	public Pair<Boolean, String> disconnected;
	
	public static final String ID = "switcherPortConnectCategory";
}