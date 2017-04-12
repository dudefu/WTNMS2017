package com.jhw.adm.client.core;
/**
 * 适配对象工厂
 */
public interface AdapterFactory {
	/**
	 * 取得适配对象
	 * @param adaptableObject 可适配对象
	 * @param adapterClass 适配对象类型
	 * @return 适配对象
	 */
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass);
}