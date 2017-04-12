package com.jhw.adm.comclient.data;

import com.jhw.adm.comclient.system.Configuration;

/**
 * 
 * @author xiongbo
 * 
 */
public interface Constants {

	public final static int SNMP_PORT = 161;
	public final static int SNMP_TRAP_PORT = Configuration.trap_port;

	public final static String COMMUNITY_PUBLIC = "public";
	public final static String COMMUNITY_PRIVATE = "private";

	public final static String BEAN_MESSAGESEND = "messageSend";
	public final static String BEAN_MESSAGERECEIVE = "messageReceive";

	public final static int VLAN_GET = 1;
	public final static int VLAN_UPDATE = 2;
	public final static int VLAN_ADD = 3;
	public final static int VLAN_DELETE = 4;

	public final static String QUARTZ_SERVICE_TYPE = "serviceType";
	public final static int QUARTZ_PERFORMANCE = 1;
	public final static int QUARTZ_PING = 2;
	public final static String QUARTZ_PERFORMANCE_IP = "ip";
	public final static String QUARTZ_PERFORMANCE_PORTNO = "portNo";
	public final static String QUARTZ_PERFORMANCE_OIDS = "oids";
	public final static String QUARTZ_PERFORMANCE_MAC = "mac";
	public final static String QUARTZ_PERFORMANCE_CLIENT = "client";
	public final static String QUARTZ_PERFORMANCE_CLIENTIP = "clientIp";

	public final static String QUARTZ_PING_IPVLAUES = "ipvlaues";
	public final static String QUARTZ_PING__PINGLV = "pinglv";
	public final static String QUARTZ_PING_PINGTIMES = "pingtimes";
	public final static String QUARTZ_PING_JIANGE = "jiange";
	public final static String QUARTZ_PING_CLIENT = "client";
	public final static String QUARTZ_PING_CLIENTIP = "clientIp";

	public final static int LOADTYPE_IMG = 1; // 表示IMG文件
	public final static int LOADEXEC_DOWN = 2; // 从文件服务器下载到交换机，使用于IMG文件和配置文件

	// 升级
	public static final int READ = 1;
	public static final int WRITE = 2;

	public static final int REBOOT = 1;
	public static final int UPGRADE = 2;
	public static final int DOWNLOADCFG = 3;
	public static final int UPLOADCFG = 4;
	public static final int UPDATE_STATE = 5;

	public static final int FAILED = 0; // 升级失败
	public static final int OK = 1; // 升级成功
	public static final int DOWNLOADING = 2; // 正在升级
	public static final int COMMAND_ERROR = 3;// 升级命令错误
}
