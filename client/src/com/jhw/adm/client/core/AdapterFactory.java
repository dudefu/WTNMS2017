package com.jhw.adm.client.core;
/**
 * ������󹤳�
 */
public interface AdapterFactory {
	/**
	 * ȡ���������
	 * @param adaptableObject ���������
	 * @param adapterClass �����������
	 * @return �������
	 */
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass);
}