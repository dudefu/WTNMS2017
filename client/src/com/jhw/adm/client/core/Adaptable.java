package com.jhw.adm.client.core;

/**
 * ���������
 * <br>
 * �ο�������ģʽ
 */
public interface Adaptable {
	/**
	 * ȡ���������
	 * @param adapterClass �����������
	 * @return �������
	 */
	public Object getAdapter(Class<?> adapterClass);
}