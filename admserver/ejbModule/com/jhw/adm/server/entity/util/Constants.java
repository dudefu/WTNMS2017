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
     * //同步参数类型
     */
	public final static String MESSPARMTYPE="MESSPARMTYPE";
	public final static String MESSAGETYPE = "MESSAGETYPE";// 消息类型
	public final static String MACVALUE = "MACVALUE";// 消息发送目标
	public final static String AIMFEP = "AIMFEP";// 目标前置机
	public final static String MESSAGEFROM = "MESSAGEFROM";// 消息来源:用于心跳消息和客户端发送消息给前置机的时候
	public final static String CLIENTIP = "CLIENTIP";// 客户单在发送和接收消息的时候，根据该属性判断是否是自己的消息
	public final static String MESSAGETO = "MESSAGETO";
	public final static String MESSAGERES = "MESSAGERES";// 用户前置机对消息的返回类型，表示操作成功或者失败
	public final static String PARAMID = "PARAMID";// 用于在配置交换机的时候，前置机返回成功，失败的时候带回消息。
	public final static String PARAMCLASS = "PARAMCLASS";// 设置成功或者失败的参数类型
	public final static String FEPCODE = "FEPCODE";// 前置机编码
	public final static String LOCALIP = "LOCALIP";// 升级时本地ip地址，ip需跟设备同网段
	// =========================事件类型=========================================================================
	public final static String TRAPTYPE = "TRAPTYPE";
	public final static int COLDSTART = 0;// 冷启动
	public final static int WARMSTART = 1;// 热启动
	public final static int LINKDOWN = 2;// 端口断开
	public final static int LINKUP = 3;// 端口连接
	public final static int AUTHENTICATIONFAILURE = 4;// 认证失败
	public final static int EGPNEIGHORLOSS = 5;  //EGP邻居故障
	public final static int ENTERPRISESPECIFIC = 6; 
	public final static int REMONTHING = 7;// 超过端口流量上下限值时候发送的trap
	public final static int PINGOUT = 8;// 交换机Ping不通
	public final static int PINGIN = 9;// 交换机Ping成功
	public final static int DATABASE_DISCONNECT = 10; //数据库断开
	public final static int DATABASE_CONNECT = 11; //数据库连接
	public final static int FEP_DISCONNECT = 12;  //前置机断开
	public final static int FEP_CONNECT = 13;  //前置机连接
	public final static int SYSLOG = 14;       //syslog
	public final static int OTHERWARNING = 15;    //其它告警
	// ==================================事件级别=================================================
	public final static short URGENCY = 1;// 紧急
	public final static short SERIOUS = 2;// 严重
	public final static short INFORM = 3;// 通知
	public final static short GENERAL = 4;// 普通
	// =========================端口参数==========================================================

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
	// ==============================系统提醒方式=======================================================
	public final static String MESSAGE = "M";
	public final static String VOICE = "V";
	// ================================告警方式==================================================
	public final static String SMS = "S";
	public final static String EMAIL = "E";

	// ===============================告警状态=======================================================
	public final static int NEW = 0;
	public final static int FIXING = 1;
	public final static int CLOSE = 2;
	// ===============================事件的记录方式========针对交换机===============================
	public final static String LOG = "LOG";
	public final static String TRAP = "TRAP";
	public final static String LOGANDTRAP = "LOGANDTRAP";
	// ===========================================================================================
	public final static int ADMINCODE = 1000;
	public final static int MANAGERCODE = 1001;
	public final static int USERCODE = 1002;
	// =================================载波机==========================================================
	public final static String CARRIERCODE = "CARRIERCODE";
	public final static int CARRIER_CORE = 1;
	public final static int CARRIER_UE = 2;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝連綫類型＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public final static int LINE_FIBERO = 1001;// 光纤
	public final static int LINE_COPPER = 1002;// 网线
	public final static int LINE_WIRE = 1003;// 电线
	public final static int LINE_WIRELESS = 1004;// 无线
	public final static int LINE_SERIAL = 1005;// 串口线
	// ============================数据同步类型：应用于交换机和载波机=============================================
	public final static String SYN_TYPE = "SYN_TYPE";
	public final static int SYN_DEV = 300;// 只操作设备端
	public final static int SYN_SERVER = 301;// 只保存到服务器端
	public final static int SYN_ALL = 303;// 既保存到设备端、也保存到服务器端
	// ===============================仿真灯状态=========================================================
	public final static byte L_ON = 1;
	public final static byte L_OFF = 0;
	// ================================連綫狀態======================================================
	public final static int L_CONNECT = 1;
	public final static int L_UNCONNECT = -1;
	public final static int L_BLOCK = 0;
	// ================================交换机升级是否重启================================================
	public final static String RESTART = "RESTART";// 交换机升级是否重启消息
	// ================================定时ping次数======================================================
	public final static String PING_TIMES = "RING_TIMERS";
	public final static String PING_JIANGE = "PING_JIANGE";
	public final static String PING_WARNING = "PING_WARNING";

	public final static String RING_ID = "RINGID";// 环ID
	// ================================设备类型=========================================================
	public final static String DEVTYPE = "DEVTYPE";// 设备类型
	public final static int DEV_SWITCHER2 = 800;// 二层交换机
	public final static int DEV_SWITCHER3 = 3000;// 三层交换机
	public final static int DEV_OLT = 10000;// OLT
	public final static int DEV_ONU = 10001;// ONU
	// ================================端口类型=================================================
	public final static int FX = 0;// 光口
	public final static int TX = 1;// 电口
	public final static int PX = 3;// pon口
	// =======================================================================================
	public final static int GIGA_PORT = 0;// 千兆端口
	public final static int FAST_PORT = 1;// 百兆端口
	// =======================================================================================
	public final static int CONNECTED = 1;
	public final static int DISCONNECTED = 0;

	public final static String SWITCHUSERS = "SWITCHUSERS";// 交换机用户
	public final static int SWITCHUSERADD = 0;// 交换机用户新增
	public final static int SWITCHUSERDEL = 1;// 交换机用户删除
	public final static int SWITCHUSERUPDATE = 2;// 交换机用户更新

	public final static int CENTER = 1;// 中心
	public final static int TERMINAL = 2;// 终端

	public final static int SENDNOTE = 0;// 短信
	public final static int SENDEMAIL = 1;// 邮件

	public final static int SINGLECHANNEL = 1;// 单通道载波机
	public final static int DOUBLECHANNEL = 2;// 双通道载波机

	// ======================================================节点状态
	// 1:正常；-1：即ping不通；0：周围的设备与该设备连接的端口都断开。2:有告警
	public static final int RESPONSELESS = -1;
	public static final int SHUTDOWN = 0;
	public static final int NORMAL = 1;
	public static final int HASWARNING = 2;
	/**
	 * 连线处于流量告警状态
	 */
	public static final int TRAFFIC = 2;

	// ===下发情况
	/**
	 * // 网管侧
	 */
	public static final int ISSUEDADM = 0;
	/**
	 * // 设备侧
	 */
	public static final int ISSUEDDEVICE = 1;
	
	// ================================告警确认状态============================
	public static final int UNCONFIRM = 0;  //未确认
	public static final int CONFIRM = 1;    //已确认
	// ================================告警类别============================
	public static final int equipment_Warning = 0;  	//设备告警
	public static final int baord_Warning = 1 ;     	//板卡告警
	public static final int port_Warning = 2 ;  		//端口告警
	public static final int facility_Warning = 3 ;  	//通信告警
	public static final int protocol_Warning = 4;   	//协议告警
	public static final int security_Warning = 5;   	//安全告警
	public static final int performance_Warning = 6;   	//性能告警
	public static final int NMS_Warning = 7;  	 		//网管告警
	public static final int other_Warning = 8;   		//其它告警
	
	// ==========================告警历史简单查询条件============================
	public static final String WEEKLY = "W";    //本周
	public static final String MONTHLY = "M";   //本月
	public static final String QUARTERLY = "Q"; //本季度
	public static final String YEARLY = "Y";    //本年
}
