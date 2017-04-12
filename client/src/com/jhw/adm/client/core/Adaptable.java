package com.jhw.adm.client.core;

/**
 * 可适配对象
 * <br>
 * 参考适配器模式
 */
public interface Adaptable {
	/**
	 * 取得适配对象
	 * @param adapterClass 适配对象类型
	 * @return 适配对象
	 */
	public Object getAdapter(Class<?> adapterClass);
}