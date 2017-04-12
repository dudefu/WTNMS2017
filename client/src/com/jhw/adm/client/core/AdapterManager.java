package com.jhw.adm.client.core;
/**
 * ������������
 */
public interface AdapterManager {
	/**
	 * ȡ���������
	 * @param adaptableObject ���������
	 * @param adapterClass �����������
	 * @return �������
	 */
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass);
	/**
	 * ע���������
	 * @param adapterClass �����������
	 * @param factory ����������󹤳�
	 * @return true ע��ɹ���false ע��ʧ��
	 */
	public boolean registerAdapters(Class<?> adapterClass, AdapterFactory factory);
}