package com.jhw.adm.server.entity.util;

public class MessageNoConstants {
	private static int i = 0;
    
	public final static int TOPOSEARCH = ++i;// 拓扑发现，用在客户端要求自动拓扑的消息发送类型中
	public final static int TOPOSEARCHONEFINSH=++i;//发现一个网络节点，返回给客户端消息
	
	public final static int SYNCHDEVICE = ++i;// 客户端要求同步交换机所有参数
	public final static int SYNCHFEP = ++i; // 客户端要求同步前置机下面所有设备的参数
	public final static int SYNCHREP = ++i;// 前置机返回的是一个设备的同步参数
	/**
	 * 前置返回完成所有设备的参数同步完成消息
	 */
	public final static int SYNCHFINISH = ++i;
	public final static int SYNCHONEFINISH = ++i;// 完成一个交换机参数同步时，由服务器端发送给客户端的消息
	
	public final static int TOPONODE = ++i;// 拓扑节点，用在前置返回发现的拓扑节点（交换机）的消息中
	public final static int TOPOFINISH = ++i;// 拓扑发现完成
//	public final static int TOPERROR = ++i;//当客户端要求拓扑的时候，如果前置机没有在线或者没有登录的时候，服务器端发送该消息给客户端
//	public final static int SYNCHORERROR = ++i;//当客户端要求参数同步时，如果前置机都不在线或者没有正常登录，服务器发送消息该客户端
	public final static int FEPOFFLINE = ++i;//前置机不在线，服务器端发送给客户端的消息
	// ==================================VLAN====================================================
	public final static int VLANGET = ++i;// 客户端要求获取vlan配置信息
	public final static int VLANSET = ++i;// 发送给前置要求配置vlan的消息
	public final static int VLANNEW = ++i;// 客户端要求新增一个vlan
	public final static int VLANUPDATE = ++i;// 客户端发送给前置机修改vlan的消息
	public final static int VLANDELETE = ++i;// 客户端发送给前置机删除vlan的消息给
	public final static int VLANPORTUPDATE = ++i;// 修改端口的vlan信息
	// =================================RING======================================================
	public final static int RINGNEW = ++i;// 新建一个JH_RING
	public final static int RINGDELETE = ++i;// 删除一个JH_RING
	public final static int RINGSGET = ++i;// 获取JH_RING列表
	public final static int RINGON = ++i;// 开启RING
	public final static int RINGOFF = ++i;// 关闭RING
	// -----------------链路备份----------------------------
	public final static int LINKBAKNEW = ++i;// 新建链路备份
	public final static int LINKBAKDELETE = ++i;;// 删除链路备份
	public final static int LINKBAKGET = ++i;// 获取链路备份
	public final static int PORTSRINGGET = ++i;// 基于RING端口信息统计
	public final static int RINGCOUNT = ++i;// RING统计
	// ==================================MAC================================================
	public final static int MACUNINEW = ++i;// 单播新增
	public final static int MACUNIGET = ++i;// 单播列表获取
	public final static int MACUNIDEL = ++i;// 单播删除
	public final static int MACMUTINEW = ++i;// 多播新增
	public final static int MACMUTIGET = ++i;// 多播列表获取
	public final static int MACMUTIDEL = ++i;// 多播删除
	// ==============================MIRROR=======================================================
	public final static int MIRRORNEW = ++i;// 端口镜像新增
	public final static int MIRRORUPDATE = ++i;// 端口镜像参数修改
	public final static int MIRRORDEL = ++i;// 端口镜像删除
	public final static int MIRRORGET = ++i;// 获取端口镜像配置
	// ====================================端口设置=================================================
	public final static int PORTSET = ++i;// 设置交换机端口参数
	public final static int PORTGET = ++i;// 获取所有端口参数
	public final static int SERIALCOFIG =++i;//设置串口参数

	// ==================================设备基本信息=============================================
	public final static int BASEINFOGET = ++i;// 获取设备基本信息
	public final static int BASECONFIGGET = ++i;// 获取设备基本配置信息
	public final static int BASECONFIGUPDATE = ++i;// 修改设备基本配置信息
	// =================================STP===================================================
	public final static int STPSYSCONFIGGET = ++i;// 获取stp系统配置
	public final static int STPSYSCONFIGSET = ++i;// 设置stp系统配置参数
	public final static int STPPORTCONFIGGET = ++i;// 获取stp端口配置
	public final static int STPPORTCONFIGSET = ++i;// 设置stp端口配置参数
	public final static int STPPORTSTATUGET = ++i;// stp端口状态获取
	public final static int STPCOUNTGET = ++i;// stp端口统计数据
	public final static int STPCOUNTCLEAR = ++i;// stp端口统计数据清零
	// ===================================SNTP====================================================
	public final static int SNTPGET = ++i;// sntp参数获取
	public final static int SNTPSET = ++i;// sntp参数设置
	// ================================心跳包命令=================================================
	public final static int FEPHEARTBEAT = ++i;// 前置机发送的心跳包，用于心跳类型的消息
	public final static int CLIENTHEARTBEAT = ++i;// 客户端返送的心跳包，用于心跳类型的消息
	// ================================消息超时=====================================================
	public final static int OUTTIMEEXCEPTION = ++i;// 前置机跟第一个交换机连接超时消息
	public final static int FEPSWITCHNETEXCEPTION = ++i;// 跟前置机直接相连的交换机之间的链路异常
	// ==================================告警=====================================================
	public final static int RTMESSAGE = ++i;// 下发实时监控消息的消息类型
	public final static int REMONTHING = ++i;// 配置rmon事件的消息类型
	public final static int TRAPMESSAGE = ++i;// trap事件发回的消息类型
	public final static int RMONCOUNT = ++i;// 进行实时监控的时候发回的实时性能数据包消息类型
	// ===================================性能监控===================================================
	public final static int RTMONITORSTART = ++i;// 实时监控开始请求
	public final static int RTMONITORSTOP = ++i;// 实时监控停止请求
	public final static int RTMONITORRES = ++i;// 实时监控前置机返回消息类型
	// ===================================实时ping==================================================
	public final static int PINGSTART = ++i;// ping开始
	public final static int PINGEND = ++i;// ping结束
	public final static int PINGRES = ++i;// ping前置机返回消息类型
	// ===================================端口告警配置==============================================
	public final static int PWARNINGCONFIG = ++i;// 端口告警配置
	public final static int PWARNINGCONFIGDELETE = ++i;// 端口告警配置参数删除
	public final static int PWARNINGCONFIGUPDATE = ++i;// 端口告警配置参数修改
	// =================================remon事件配置===============================================
	public final static int REMONTHINGNEW = ++i;// 新增加事件配置
	public final static int REMONTHINGDEL = ++i;// 删除事件配置
	// =================================实时监控端口配置===========================================
	public final static int REMONPORTNEW = ++i;// 新增加实时监控端口配置
	public final static int REMONPORTDEL = ++i;// 删除实时监控端口配置
	// ==============================设备参数操作返回消息类型=========================================
	public final static int PARMREP = ++i;// 设备参数操作返回消息类型
	// ==============================载波机操作消息类型=========================================
	public final static int CARRIERTEST = ++i; // 载波机测试//文本消息
	public final static int CARRIERTESTREP = ++i;// 载波机测试返回//"S","F"保存在文本中
	public final static int CARRIERRESTART = ++i; // 载波机重启//文本消息
	public final static int CARRIERONLINE = ++i; // 载波机上线//前置机主动发送，没有res

	public final static int CARRIERWAVEBANDCONFIG = ++i; // 载波机波段配置//对象消息，发送载波机实体
	public final static int CARRIERWAVEBANDCONFIGREP = ++i;// 载波机波段配置返回消息

	public final static int CARRIERWAVEBANDQUERY = ++i;// 波段查询消息类型//文本消息
	public final static int CARRIERWAVEBANDQUERYREP = ++i;// 波段查询返回消息\

	public final static int CARRIERROUTEQUERY = ++i; // 载波机路由查询
	public final static int CARRIERROUTEQUERYREP = ++i;// 路由查询返回消息

	public final static int CARRIERROUTECONFIG = ++i; // 单个载波机路由配置
	public final static int CARRIERROUTECONFIGS = ++i; // 批量同步所有载波机路由配置
	public final static int CARRIERROUTECONFIGREP = ++i; // 单个载波机路由配置返回消息
	public final static int CARRIERROUTEFINISH = ++i; // 批量同步路由结束

	public final static int CARRIERROUTECLEAR = ++i; // 载波机路由清空//文本消息
	public final static int CARRIERROUTECLEARREP = ++i;// 载波机路由清空返回消息

	public final static int CARRIERPORTQUERY = ++i; // 载波机端口查询
	public final static int CARRIERPORTQUERYREP = ++i;// 载波机端口查询返回消息类型
	public final static int CARRIERPORTCONFIG = ++i; // 载波机端口配置
	public final static int CARRIERPORTCONFIGREP = ++i; // 载波机端口配置返回消息

	public final static int CARRIERSYSTEMUPGRADE = ++i; // 载波机系统升级
	public final static int CARRIERSYSTEMUPGRADEREP = ++i; // 载波机系统升级返回消息

	public final static int CARRIERMONITORREP = ++i; // 前置机主动监测载波机状态发送回来的消息类型

	// ===================================GPRS=================================================================
	public final static int GPRSMESSAGE = ++i;// 用于gprs设备从设备端发送到客户端的信息
	public final static int SWITCHERUPGRATE = ++i;//交换机升级消息
	public final static int ONE_SWITCHER_UPGRADEREP=++i;//单个升级完成的返回消息
	public final static int ALL_SWITCHER_UPGRADEREP = ++i;//交换机升级完成
	public final static int SWITCHER_UPGRADEING=++i;//当前交换机正在升级，不能进行升级操作
	public final static int WARNING=++i;//服务器发送给客户端的告警信息
	public final static int LLDPCONFIG=++i;//lldp设置
	
	//=========================================================================================================
	public final static int REFRESHING=++i;//正在拓扑发现，提示客户端现在有用户正在进行拓扑发现，不能两个用户同时进行拓扑发现操作
	public final static int SYNCHORIZING=++i;//正在进行参数同步，提示客户端不能两个客户端同时进行参数同步
	
	public final static int SNMPHOSTADD=++i;//snmp主机配置
	public final static int SNMPHOSTDELETE = ++i;//删除snmp主机项
	//==========================================仿真灯===========================================================
	public final static int LIGHT_SIGNAL=++i;//发送获取交换机灯信号的消息
	public final static int LIGHT_SIGNAL_REP=++i;//前置机返回交换机信号灯状态的消息类型
	//==========================================前置机状态
	public final static int FEP_STATUSTYPE=++i; //前置机状态
	
	public final static int PING_TIMER=++i;//定时ping
	//==========================================================================================================
	
    public final static int EPON_INFO=++i;//返回epon消息

	public final static int ALL_VLAN=++i;//批量配置Vlan
	//==========================================================================================================
	
	public final static int ALL_RING=++i;//批量配置RING
	//==========================================================================================================
	
	public final static int IGMP_PORTSET=++i;//IGMP端口配置
	public final static int IGMP_VLANSET=++i;//IGMPVlan配置
	
	public final static int ALL_SNMPHOST=++i;//批量配置SNMPHOST
	//==========================================================================================================
	
	//====================================================QOS配置
	
	public final static int QOS_SYSCONFIG=++i;//系统配置
	public final static int QOS_PRIORITYCONFIG=++i;//QOS优先级配置
	public final static int TOS_PRIORITYCONFIG=++i;//TOS优先级配置
	public final static int DSCP_PRIORITYCONFIG=++i;//优先级配置
	public final static int QOS_LIMITPORT=++i;//端口限速
	public final static int QOS_STORMCONTROL=++i;//风暴控制
	
	
	//=============================================================
	public final static int LACPCONFIG=++i;//lacp配置
	
	//=============================================================
	
	public final static int AGGREGATEPORT_ADD=++i;//聚合端口增加
	public final static int AGGREGATEPORT_DEL=++i;//聚合端口删除
	
	//=============================================================================================================
	public final static int OLTVLANADD=++i;//olt vlan 添加
	public final static int OLTVLANDEL = ++i;//删除olt上的vlan配置
	public final static int DBACONFIG = ++i;//DBA配置
	public final static int LLIDCONFIG=++i;//llid新增
	public final static int OLTSTPCONFIG=++i;//OLT STP系统配置
	public final static int OLTMULTICASTCONFIG=++i;//OLT 组播配置
	

	//==========================================================================================================
	public final static int MONITORRING_TIMER=++i;//定时监控
	public final static int MONITORRING_DEL=++i;
	
	public final static int SWITCHRESTING=++i;//交换机重启中
	public final static int SWITCHRESTSUCCESS=++i;//交换机重启成功
	public final static int SWITCHRECEIVE=++i;//接受交换机信息
	public final static int SWITCHUPGRADEFAIL=++i;//交换机升级失败
	public final static int SWITCHRESTFAIL=++i;//交换机重启失败
	
	public final static int SWITCHUSERMANAGE=++i;//交换机用户管理
	
	
	public final static int SWITCH3STP=++i;//三层交换机STP配置
	public final static int SWITCH3VLAN=++i;//三层交换机Vlan配置
	public final static int SWITCH3VLANADD=++i;//三层交换机Vlan新增配置
	public final static int SWITCH3VLANUPDATE=++i;//三层交换机Vlan修改配置
	public final static int SWITCH3VLANDEL=++i;//三层交换机Vlan删除配置
	public final static int SWITCH3STPPORT=++i;//三层交换机STP端口配置
	public final static int SWITCH3SNMP=++i;//三层交换机SNMP配置
	public final static int SWITCH3LLDP_P=++i;//三层交换机LLDP协议配置
	public final static int SWITCH3LLDPPORT=++i;//三层交换机LLDP协议端口配置
	public final static int SWITCH3VLANPORT=++i;//三层交换机Vlan端口配置
	public final static int SWITCH3PORTFLOW=++i;//三层交换机端口流量配置
	public final static int SWITCH3RINGADD=++i;//三层交换机Ring添加配置
	public final static int SWITCH3RINGUPDATE=++i;//三层交换机Ring更新配置
	public final static int SWITCH3RINGDEL=++i;//三层交换机Ring删除配置
	
	
	
	
	
	public final static int CARRIERMARKINGCONFIG=++i;//（载波型号配置）
    public final static int CARRIERMARKINGREP=++i;//载波机型号配置返回
    
    public final static int SWITCHUSERUPDATE=++i;//
    public final static int SWITCHUSERADD=++i;//
    
    
    public final static int QOSPORTSPEEDREP=++i;//QOS端口速率配置
    
    public final static int WARNINGLINK=++i;//线告警
    public final static int WARNINGNODE=++i;//节点告警
	
    
    public final static int CHECKLLDP=++i;//LLDP诊断
    
    public final static int SYSLOGHOSTSAVE=++i;//syslog主机配置
    
    public final static int SYSLOGHOSTDELETE=++i;//syslog主机删除
    
    /**
     * 单个设备参数上载
     */
    public final static int SINGLESYNCHDEVICE=++i;
    
    /**
     * 单个交换机LLDP上载
     */
    public final static int SINGLESWITCHLLDP=++i;
    /**
     * 单个交换机Vlan上载
     */
    public final static int SINGLESWITCHVLAN=++i;
    /**
     * 单个交换机Vlan端口上载
     */
    public final static int SINGLESWITCHVLANPORT=++i;
    /**
     * 单个交换机IGMP端口上载
     */
    public final static int SINGLESWITCHIGMPPORT=++i;
    /**
     * 单个交换机IGMPVlanID上载
     */
    public final static int SINGLESWITCHIGMPVLANID=++i;
    
    /**
     * 单个交换机IP上载
     */
    public final static int SINGLESWITCHIP=++i;
    /**
     * 单个交换机单播上载
     */
    public final static int SINGLESWITCHUNICAST=++i;
    /**
     * 单个交换机多播上载
     */
    public final static int SINGLESWITCHMULTICAST=++i;
    /**
     * 单个交换机QOS端口限速上载
     */
    public final static int SINGLESWITCHQOSPORT=++i;
    /**
     * 单个交换机QOS风暴控制上载
     */
    public final static int SINGLESWITCHQOSSTORM=++i;
    /**
     * 单个交换机QOS系统配置上载
     */
    public final static int SINGLESWITCHQOSSYS=++i;
    /**
     * 单个交换机QOS802D1P优先级上载
     */
    public final static int SINGLESWITCHQOSPRIORITY802D1P=++i;
    /**
     * 单个交换机QOSDSCP优先级上载
     */
    public final static int SINGLESWITCHQOSPRIORITYDSCP=++i;
    /**
     * 单个交换机QOSTOS优先级上载
     */
    public final static int SINGLESWITCHQOSPRIORITYTOS=++i;
    /**
     * 单个交换机RING上载
     */
    public final static int SINGLESWITCHRING=++i;
   
    /**
     * 单个交换机SNMP群组上载
     */
    public final static int SINGLESWITCHSNMPGROUP=++i;
    /**
     * 单个交换机SNMP团体上载
     */
    public final static int SINGLESWITCHSNMPMASS=++i;
    /**
     * 单个交换机SNMP视图上载
     */
    public final static int SINGLESWITCHSNMPVIEW=++i;
    /**
     * 单个交换机SNMP主机上载
     */
    public final static int SINGLESWITCHSNMPHOST=++i;
    
    /**
     * 单个交换机STP端口上载
     */
    public final static int SINGLESWITCHSTPPORT=++i;
    /**
     * 单个交换机STP系统配置上载
     */
    public final static int SINGLESWITCHSTPSYS=++i;
    /**
     * 单个上载参数结束
     */
    public final static int SINGLESYNCHONEFINISH=++i;
    /**
     * 单个参数上载前置机返回消息
     */
    public final static int SINGLESYNCHREP=++i;
    
    /**
     * 单个参数交换机端口信息上载
     */
    public final static int SINGLESWITCHPORT=++i;
    /**
     * 单个参数交换机SNTP上载
     */
    public final static int SINGLESWITCHSNTP=++i;
    
    
    
    /**
     * 单个参数交换机串口上载
     */
    public final static int SINGLESWITCHSERIAL=++i;
    
    
    /**
     * 单个参数交换机SNMP用户上载
     */
    public final static int SINGLESWITCHSNMPUSER=++i;
    /**
     * 单个交换机用户上载
     */
    public final static int SINGLESWITCHUSERADM=++i;
    
   
    /**
     * 单个交换机链路备份上载
     */
    public final static int SINGLESWITCHLINKBACKUPS=++i;
    
    /**
     * 单个交换机LACP上载
     */
    public final static int SINGLESWITCHLACP=++i;
    
    /**
     * 单个交换机mirror上载
     */
    public final static int SINGLESWITCHMIRROR=++i;
    
    /**
     * 单个交换机TRUNK聚合端口上载
     */
    public final static int SINGLESWITCHTRUNKPORT=++i;
    
    
    /**
     * 单个交换机端口告警上载
     */
    public final static int SINGLESWITCHPORTWARN=++i;
    
     
    /**
     * 单个交换机事件组上载
     */
    public final static int SINGLESWITCHEVENTGROUP=++i;
    
    
    /**
     * 删除交换机用户
     */
    public final static int SWITCHUSERDEL=++i;
    
    
    /**
     * 交换机基本信息
     */
    public final static int SINGLESWITCHINFO=++i;
    /**
     * 交换机PortRemon
     */
    public final static int SINGLESWITCHPORTREMON=++i;
    
    /**
     * 单个syslog主机上载
     */
    public final static int SINGLESYSLOGHOST=++i;
    
    /**
     * 单个三层端口上载
     */
    public final static int SINGLESWITCHLAYER3PORT=++i;
    
    /**
     * 单个OLT端口上载
     */
    public final static int SINGLEOLTPORT=++i;
    
    /**
     * syslog告警信息
     */
    public final static int SYSLOGMESSAGE=++i;
    
}
