/**
 * Constants.java
 * Administrator
 * 2010-3-5
 * TODO
 * 
 */
package com.jhw.adm.server.entity.util;

/**
 * @author Administrator
 * 
 */
public class Constants {
    /**
     * //ͬ����������
     */
	public final static String MESSPARMTYPE="MESSPARMTYPE";
	public final static String MESSAGETYPE = "MESSAGETYPE";// ��Ϣ����
	public final static String MACVALUE = "MACVALUE";// ��Ϣ����Ŀ��
	public final static String AIMFEP = "AIMFEP";// Ŀ��ǰ�û�
	public final static String MESSAGEFROM = "MESSAGEFROM";// ��Ϣ��Դ:����������Ϣ�Ϳͻ��˷�����Ϣ��ǰ�û���ʱ��
	public final static String CLIENTIP = "CLIENTIP";// �ͻ����ڷ��ͺͽ�����Ϣ��ʱ�򣬸��ݸ������ж��Ƿ����Լ�����Ϣ
	public final static String MESSAGETO = "MESSAGETO";
	public final static String MESSAGERES = "MESSAGERES";// �û�ǰ�û�����Ϣ�ķ������ͣ���ʾ�����ɹ�����ʧ��
	public final static String PARAMID = "PARAMID";// ���������ý�������ʱ��ǰ�û����سɹ���ʧ�ܵ�ʱ�������Ϣ��
	public final static String PARAMCLASS = "PARAMCLASS";// ���óɹ�����ʧ�ܵĲ�������
	public final static String FEPCODE = "FEPCODE";// ǰ�û�����
	public final static String LOCALIP = "LOCALIP";// ����ʱ����ip��ַ��ip����豸ͬ����
	// =========================�¼�����=========================================================================
	public final static String TRAPTYPE = "TRAPTYPE";
	public final static int COLDSTART = 0;// ������
	public final static int WARMSTART = 1;// ������
	public final static int LINKDOWN = 2;// �˿ڶϿ�
	public final static int LINKUP = 3;// �˿�����
	public final static int AUTHENTICATIONFAILURE = 4;// ��֤ʧ��
	public final static int EGPNEIGHORLOSS = 5;  //EGP�ھӹ���
	public final static int ENTERPRISESPECIFIC = 6; 
	public final static int REMONTHING = 7;// �����˿�����������ֵʱ���͵�trap
	public final static int PINGOUT = 8;// ������Ping��ͨ
	public final static int PINGIN = 9;// ������Ping�ɹ�
	public final static int DATABASE_DISCONNECT = 10; //���ݿ�Ͽ�
	public final static int DATABASE_CONNECT = 11; //���ݿ�����
	public final static int FEP_DISCONNECT = 12;  //ǰ�û��Ͽ�
	public final static int FEP_CONNECT = 13;  //ǰ�û�����
	public final static int SYSLOG = 14;       //syslog
	public final static int OTHERWARNING = 15;    //�����澯
	// ==================================�¼�����=================================================
	public final static short URGENCY = 1;// ����
	public final static short SERIOUS = 2;// ����
	public final static short INFORM = 3;// ֪ͨ
	public final static short GENERAL = 4;// ��ͨ
	// =========================�˿ڲ���==========================================================

	public final static String octets = "octets";
	public final static String packets = "packets";
	public final static String bcast_pkts = "bcast_pkts";
	public final static String mcast_pkts = "mcast_pkts";
	public final static String crc_align = "crc_align";
	public final static String undersize = "undersize";
	public final static String oversize = "oversize";
	public final static String fragments = "fragments";
	public final static String jabbers = "jabbers";
	public final static String collisions = "collisions";
	public final static String pkts_64 = "pkts_64";
	public final static String pkts_65_127 = "pkts_65_127";
	public final static String pkts_128_255 = "pkts_128_255";
	public final static String pkts_256_511 = "pkts_256_511";
	public final static String pkts_512_1023 = "pkts_512_1023";
	public final static String pkts_1024_1518 = "pkts_1024_1518";
	public final static String ifInDiscards = "ifInDiscards";
	public final static String ifOutDiscards = "ifOutDiscards";
	public final static String txPackets = "txPackets";
	public final static String txBcastPkts = "txBcastPkts";
	public final static String txMcastPkts = "txMcastPkts";
	// ==============================ϵͳ���ѷ�ʽ=======================================================
	public final static String MESSAGE = "M";
	public final static String VOICE = "V";
	// ================================�澯��ʽ==================================================
	public final static String SMS = "S";
	public final static String EMAIL = "E";

	// ===============================�澯״̬=======================================================
	public final static int NEW = 0;
	public final static int FIXING = 1;
	public final static int CLOSE = 2;
	// ===============================�¼��ļ�¼��ʽ========��Խ�����===============================
	public final static String LOG = "LOG";
	public final static String TRAP = "TRAP";
	public final static String LOGANDTRAP = "LOGANDTRAP";
	// ===========================================================================================
	public final static int ADMINCODE = 1000;
	public final static int MANAGERCODE = 1001;
	public final static int USERCODE = 1002;
	// =================================�ز���==========================================================
	public final static String CARRIERCODE = "CARRIERCODE";
	public final static int CARRIER_CORE = 1;
	public final static int CARRIER_UE = 2;

	// �������������������������������������B�Q��ͣ�������������������������������������������������������
	public final static int LINE_FIBERO = 1001;// ����
	public final static int LINE_COPPER = 1002;// ����
	public final static int LINE_WIRE = 1003;// ����
	public final static int LINE_WIRELESS = 1004;// ����
	public final static int LINE_SERIAL = 1005;// ������
	// ============================����ͬ�����ͣ�Ӧ���ڽ��������ز���=============================================
	public final static String SYN_TYPE = "SYN_TYPE";
	public final static int SYN_DEV = 300;// ֻ�����豸��
	public final static int SYN_SERVER = 301;// ֻ���浽��������
	public final static int SYN_ALL = 303;// �ȱ��浽�豸�ˡ�Ҳ���浽��������
	// ===============================�����״̬=========================================================
	public final static byte L_ON = 1;
	public final static byte L_OFF = 0;
	// ================================�B�Q��B======================================================
	public final static int L_CONNECT = 1;
	public final static int L_UNCONNECT = -1;
	public final static int L_BLOCK = 0;
	// ================================�����������Ƿ�����================================================
	public final static String RESTART = "RESTART";// �����������Ƿ�������Ϣ
	// ================================��ʱping����======================================================
	public final static String PING_TIMES = "RING_TIMERS";
	public final static String PING_JIANGE = "PING_JIANGE";
	public final static String PING_WARNING = "PING_WARNING";

	public final static String RING_ID = "RINGID";// ��ID
	// ================================�豸����=========================================================
	public final static String DEVTYPE = "DEVTYPE";// �豸����
	public final static int DEV_SWITCHER2 = 800;// ���㽻����
	public final static int DEV_SWITCHER3 = 3000;// ���㽻����
	public final static int DEV_OLT = 10000;// OLT
	public final static int DEV_ONU = 10001;// ONU
	// ================================�˿�����=================================================
	public final static int FX = 0;// ���
	public final static int TX = 1;// ���
	public final static int PX = 3;// pon��
	// =======================================================================================
	public final static int GIGA_PORT = 0;// ǧ�׶˿�
	public final static int FAST_PORT = 1;// ���׶˿�
	// =======================================================================================
	public final static int CONNECTED = 1;
	public final static int DISCONNECTED = 0;

	public final static String SWITCHUSERS = "SWITCHUSERS";// �������û�
	public final static int SWITCHUSERADD = 0;// �������û�����
	public final static int SWITCHUSERDEL = 1;// �������û�ɾ��
	public final static int SWITCHUSERUPDATE = 2;// �������û�����

	public final static int CENTER = 1;// ����
	public final static int TERMINAL = 2;// �ն�

	public final static int SENDNOTE = 0;// ����
	public final static int SENDEMAIL = 1;// �ʼ�

	public final static int SINGLECHANNEL = 1;// ��ͨ���ز���
	public final static int DOUBLECHANNEL = 2;// ˫ͨ���ز���

	// ======================================================�ڵ�״̬
	// 1:������-1����ping��ͨ��0����Χ���豸����豸���ӵĶ˿ڶ��Ͽ���2:�и澯
	public static final int RESPONSELESS = -1;
	public static final int SHUTDOWN = 0;
	public static final int NORMAL = 1;
	public static final int HASWARNING = 2;
	/**
	 * ���ߴ��������澯״̬
	 */
	public static final int TRAFFIC = 2;

	// ===�·����
	/**
	 * // ���ܲ�
	 */
	public static final int ISSUEDADM = 0;
	/**
	 * // �豸��
	 */
	public static final int ISSUEDDEVICE = 1;
	
	// ================================�澯ȷ��״̬============================
	public static final int UNCONFIRM = 0;  //δȷ��
	public static final int CONFIRM = 1;    //��ȷ��
	// ================================�澯���============================
	public static final int equipment_Warning = 0;  	//�豸�澯
	public static final int baord_Warning = 1 ;     	//�忨�澯
	public static final int port_Warning = 2 ;  		//�˿ڸ澯
	public static final int facility_Warning = 3 ;  	//ͨ�Ÿ澯
	public static final int protocol_Warning = 4;   	//Э��澯
	public static final int security_Warning = 5;   	//��ȫ�澯
	public static final int performance_Warning = 6;   	//���ܸ澯
	public static final int NMS_Warning = 7;  	 		//���ܸ澯
	public static final int other_Warning = 8;   		//�����澯
	
	// ==========================�澯��ʷ�򵥲�ѯ����============================
	public static final String WEEKLY = "W";    //����
	public static final String MONTHLY = "M";   //����
	public static final String QUARTERLY = "Q"; //������
	public static final String YEARLY = "Y";    //����
}
