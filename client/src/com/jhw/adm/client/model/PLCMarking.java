package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.Constants;

@Component(PLCMarking.ID)
public class PLCMarking {
	@PostConstruct
	protected void initialize() {
		singleChannel = new StringInteger("单通道载波机", Constants.SINGLECHANNEL);
		doubleChannel = new StringInteger("双通道载波机", Constants.DOUBLECHANNEL);
		list  = new ArrayList<StringInteger>(3);
		list.add(singleChannel);
		list.add(doubleChannel);
	}
	
	public List<StringInteger> toList() {		
		return Collections.unmodifiableList(list);
	}
	
	public List<StringInteger> toListIncludeAll() {
		List<StringInteger> all = new ArrayList<StringInteger>(list);
		all.add(0, ALL);
		return Collections.unmodifiableList(all);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case Constants.SINGLECHANNEL: return singleChannel;
			case Constants.DOUBLECHANNEL: return doubleChannel;
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger singleChannel;
	public StringInteger doubleChannel;
	
	public static final String ID = "plcMarking";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("全部", -2);
}