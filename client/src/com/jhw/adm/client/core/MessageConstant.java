package com.jhw.adm.client.core;

public class MessageConstant {
	public final static int TIMEOUT_TOPOREFRESH = 60000;//���˷��ֵĳ�ʱʱ��Ϊ25��;
	public final static int TIMEOUT_SYN = 60000;//ͬ��ʱ�ĳ�ʱʱ��Ϊ25��;
	public final static int TIMEOUT = 15000;//�·�ʱ�ĳ�ʱʱ��Ϊ6��;
	public final static String SUCCESS = "S"; //����˷��صĽ��Ϊ�ɹ�
	public final static String FAILURE = "F"; //����˷��صĽ��Ϊʧ��
	public final static int TIMEOUT_UPGRADE = 5*60000;//����ʱ�ĳ�ʱʱ��Ϊ60��
	public final static int TIMEOUT_SAVE = 60000;//�������ʱ�ĳ�ʱʱ��Ϊ60��
	
	public final static int SYN_SWITCH = 1;//ͬ��������
	public final static int FINISH_TOPO = 2;//���˷���
	public final static int SYN_CARRIER = 3;//ͬ���ز���
	public final static int UPGRADE_SWITCH = 4;//����������
	public final static int UPGRADE_CARRIER = 5;//�ز�������
	public final static int SAVE_PARAM = 6;//��������
	
	//��������
	public static final String SAVE = "save";//�������
	public static final String TOPO_REFRESH = "topo_refresh";//����ˢ��
	public static final String SYNCHRONIZE = "synchronize";//ͬ��
	public static final String DELETE = "delete";//ɾ��
}
