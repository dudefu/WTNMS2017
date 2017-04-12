package com.jhw.adm.client.core;

public class MessageConstant {
	public final static int TIMEOUT_TOPOREFRESH = 60000;//拓扑发现的超时时间为25秒;
	public final static int TIMEOUT_SYN = 60000;//同步时的超时时间为25秒;
	public final static int TIMEOUT = 15000;//下发时的超时时间为6秒;
	public final static String SUCCESS = "S"; //服务端返回的结果为成功
	public final static String FAILURE = "F"; //服务端返回的结果为失败
	public final static int TIMEOUT_UPGRADE = 5*60000;//升级时的超时时间为60秒
	public final static int TIMEOUT_SAVE = 60000;//保存参数时的超时时间为60秒
	
	public final static int SYN_SWITCH = 1;//同步交换机
	public final static int FINISH_TOPO = 2;//拓扑发现
	public final static int SYN_CARRIER = 3;//同步载波机
	public final static int UPGRADE_SWITCH = 4;//交换机升级
	public final static int UPGRADE_CARRIER = 5;//载波机升级
	public final static int SAVE_PARAM = 6;//参数保存
	
	//操作参数
	public static final String SAVE = "save";//保存参数
	public static final String TOPO_REFRESH = "topo_refresh";//拓扑刷新
	public static final String SYNCHRONIZE = "synchronize";//同步
	public static final String DELETE = "delete";//删除
}
