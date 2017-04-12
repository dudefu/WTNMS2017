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
@Component(SwitcherPortWorkCategory.ID)
public class SwitcherPortWorkCategory {
	
	@PostConstruct
	protected void initialize() {
		enable = new Pair<Boolean, String>(true, "启用");
		disable = new Pair<Boolean, String>(false, "禁用");
		list  = new ArrayList<Pair<Boolean, String>>(2);
		list.add(enable);
		list.add(disable);
	}
	
	public List<Pair<Boolean, String>> toList() {		
		return Collections.unmodifiableList(list);
	}
	
	public Pair<Boolean, String> get(boolean value) {		
		return value ? enable : disable;
	}

	private List<Pair<Boolean, String>> list;
	public Pair<Boolean, String> enable;
	public Pair<Boolean, String> disable;
	
	public static final String ID = "switcherPortWorkCategory";
}