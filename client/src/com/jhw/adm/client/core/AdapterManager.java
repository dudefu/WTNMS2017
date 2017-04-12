package com.jhw.adm.client.core;
/**
 * 适配对象管理器
 */
public interface AdapterManager {
	/**
	 * 取得适配对象
	 * @param adaptableObject 可适配对象
	 * @param adapterClass 适配对象类型
	 * @return 适配对象
	 */
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass);
	/**
	 * 注册适配对象
	 * @param adapterClass 适配对象类型
	 * @param factory 可以适配对象工厂
	 * @return true 注册成功，false 注册失败
	 */
	public boolean registerAdapters(Class<?> adapterClass, AdapterFactory factory);
}