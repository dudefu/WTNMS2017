package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Pair;

/**
 * �������˿�ģʽ
 */
@Component(SwitcherPortWorkCategory.ID)
public class SwitcherPortWorkCategory {
	
	@PostConstruct
	protected void initialize() {
		enable = new Pair<Boolean, String>(true, "����");
		disable = new Pair<Boolean, String>(false, "����");
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