package com.jhw.adm.server.entity.util;
import java.io.Serializable;


public class PropertyValue<T> implements Serializable {

	public PropertyValue() {
		this(null, true);
	}
	
	public PropertyValue(boolean r) {
		this(null, r);
	}
	
	public PropertyValue(T v) {
		this(v, true);
	}
	
	public PropertyValue(T v, boolean r) {
		value = v;
		restriction = r;
	}
	/**
	 * 该属性的值
	 */
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	/**
	 * 该属性值是否需要限制
	 */
	public boolean isRestriction() {
		return restriction;
	}
	public void setRestriction(boolean restriction) {
		this.restriction = restriction;
	}
	
	private T value;
	private boolean restriction;
	
	private static final long serialVersionUID = -8077699174940778910L;
}