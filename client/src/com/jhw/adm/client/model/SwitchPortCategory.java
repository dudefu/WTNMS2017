package com.jhw.adm.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.util.Constants;

/**
 * 交换机端口类型
 */
@Component(SwitchPortCategory.ID)
public class SwitchPortCategory {
	
	@PostConstruct
	protected void initialize() {
		POWER = new StringInteger("电口", POWER_PORT);
		FDDI = new StringInteger("光口", FDDI_PORT);
		PON = new StringInteger("PON", PON_PORT);
		list  = new ArrayList<StringInteger>();
		list.add(POWER);
		list.add(FDDI);
		list.add(PON);
	}
	
	public List<StringInteger> toList() {
		return Collections.unmodifiableList(list);
	}
	
	public StringInteger get(int value) {
		switch (value) {
			case POWER_PORT: return POWER;
			case FDDI_PORT: return FDDI;
			case PON_PORT: return PON;
			default: return Unknown;
		}
	}

	private List<StringInteger> list;
	public StringInteger POWER;
	public StringInteger FDDI;
	public StringInteger PON;
	
	public static final int POWER_PORT = Constants.TX;
	public static final int FDDI_PORT = Constants.FX;
	public static final int PON_PORT = Constants.PX;
	public static final String ID = "switchPortCategory";
	public final StringInteger Unknown = new StringInteger("Unknow", -1);
}