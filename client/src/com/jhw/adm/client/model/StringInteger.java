package com.jhw.adm.client.model;

/**
 * 字符串和整型绑定
 */
public class StringInteger {
	public StringInteger(String k, Integer v) {
		key = k;
		value = v;
	}
	public String getKey() {
		return key;
	}

//	public void setKey(String key) {
//		this.key = key;
//	}

	public Integer getValue() {
		return value;
	}

//	public void setValue(Integer value) {
//		this.value = value;
//	}
	
	private String key;
	private Integer value;
	
	@Override
	public String toString() {
		return key.toString();
	}
	
	@Override
	public int hashCode() {
		return value == null ? super.hashCode() : value.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		
		if (obj instanceof StringInteger) {
			StringInteger kv = (StringInteger)obj;
			
			if (kv.getValue() == null && getValue() == null) {
				return true;
			} else if (kv.getValue() == null || getValue() == null) {
				return false;
			} else {
				return kv.getValue().equals(getValue());
			}
		} else {
			return false;
		}
	}
}