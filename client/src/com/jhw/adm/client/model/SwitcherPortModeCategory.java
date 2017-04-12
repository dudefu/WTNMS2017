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
@Component(SwitcherPortModeCategory.ID)
public class SwitcherPortModeCategory {
	
	@PostConstruct
	protected void initialize() {
		auto = new Pair<String, String>("auto", "����Ӧ");
		fdx100 = new Pair<String, String>("100fdx", "100Mȫ˫��");
		hdx100 = new Pair<String, String>("100hdx", "100M��˫��");
		fdx10 = new Pair<String, String>("10fdx", "10Mȫ˫��");
		hdx10 = new Pair<String, String>("10hdx", "10M��˫��");
		list  = new ArrayList<Pair<String, String>>(5);
		list.add(auto);
		list.add(fdx100);
		list.add(hdx100);
		list.add(fdx10);
		list.add(hdx10);
	}
	
	public List<Pair<String, String>> toList() {		
		return Collections.unmodifiableList(list);
	}
	
	public Pair<String, String> get(String value) {
		Pair<String, String> result = Unknown;
		for (Pair<String, String> pair : list) {
			if (pair.getHead().equals(value)) {
				result = pair;
				break;
			}
		}
		
		return result;
	}

	private List<Pair<String, String>> list;
	public Pair<String, String> auto;
	public Pair<String, String> fdx100;
	public Pair<String, String> hdx100;
	public Pair<String, String> fdx10;
	public Pair<String, String> hdx10;
	
	public static final String ID = "switcherPortModeCategory";
	public final Pair<String, String> Unknown = new Pair<String, String>("Unknow", "Unknow");
}