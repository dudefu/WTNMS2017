package com.jhw.adm.client.model.carrier;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.model.StringInteger;

@Component(CarrierPortSubnet.ID)
public class CarrierPortSubnet {
	public static final String ID = "carrierPortSubnet";
	private static List<StringInteger> dataList = new ArrayList<StringInteger>();
	public static void setList(List<List> lists){
		dataList.clear();
		for (int i = 0 ; i < lists.size(); i++){
			List rowList = lists.get(i);
			String name = rowList.get(0).toString();
			int code = Integer.parseInt(rowList.get(1).toString());
			StringInteger s = new StringInteger(code+ "-" + name, code);
			dataList.add(s);
		}
	}

	public static List<StringInteger> toList() {

		return dataList;
	}
	
	public static StringInteger get(int value) {
		for (int i = 0 ; i < dataList.size(); i++){
			StringInteger nameValue = dataList.get(i);
			if (nameValue.getValue()==value){
				return nameValue;
			}
		}
		StringInteger unknown = new StringInteger(String.valueOf(value), value);
		return unknown;
	}


}
