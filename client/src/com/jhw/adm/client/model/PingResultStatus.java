package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.Constants;

/**
 * ping状态结果
 */
@Component(PingResultStatus.ID)
public class PingResultStatus {
	@PostConstruct
	protected void initialize() {
		pass = new StringInteger("通", Constants.PINGIN);
		block = new StringInteger("不通", Constants.PINGOUT);
		list  = new ArrayList<StringInteger>(3);
		list.add(pass);
		list.add(block);
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
			case Constants.PINGIN: return pass;
			case Constants.PINGOUT: return block;
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger pass;
	public StringInteger block;
	
	public static final String ID = "pingResultStatus";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
	public final StringInteger ALL = new StringInteger("全部", -2);
}
