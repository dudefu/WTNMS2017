package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

/**
 * ÔØ²¨»ú²¨¶Î
 */
@Component(WaveBand.ID)
public class WaveBand {

	
	@PostConstruct
	protected void initialize() {
		A = new StringInteger("A²¨¶Î", 0);
		C = new StringInteger("C²¨¶Î", 1);
		list  = new ArrayList<StringInteger>(6);
		list.add(A);
		list.add(C);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case 0: return A;
			case 1: return C;
			default: return Unknown;
		}
	}
	
	private List<StringInteger> list;
	
	public StringInteger A;
	public StringInteger C;
	public static final String ID = "waveBand";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}
