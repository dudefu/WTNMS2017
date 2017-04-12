package com.jhw.adm.comclient.data;

/**
 * 
 * @author xiongbo
 * 
 */
public interface OID {

	// ipAddrTable
	public static final String IPADENTADDR = "1.3.6.1.2.1.4.20.1.1";
	public static final String IPADENTIFINDEX = "1.3.6.1.2.1.4.20.1.2";
	public static final String IPADENTNETMASK = "1.3.6.1.2.1.4.20.1.3";
	// ipRouteTable
	public static final String IPROUTEDEST = "1.3.6.1.2.1.4.21.1.1";
	public static final String IPROUTEIFINDEX = "1.3.6.1.2.1.4.21.1.2";
	public static final String IPROUTENEXTHOP = "1.3.6.1.2.1.4.21.1.7";
	public static final String IPROUTETYPE = "1.3.6.1.2.1.4.21.1.8";

	// IP Information of Node
	public final static String IP = "1.3.6.1.4.1.44405.71.2.4.1.0";
	public final static String NETMASK = "1.3.6.1.4.1.44405.71.2.4.2.0";
	public final static String GATEWAY = "1.3.6.1.4.1.44405.71.2.4.3.0";
	public final static String DHCPSTATE = "1.3.6.1.4.1.44405.71.2.4.4.0";
	public final static String PRIDNS = "1.3.6.1.4.1.44405.71.2.4.5.0";
	public final static String SECDNS = "1.3.6.1.4.1.44405.71.2.4.6.0";
	// The end of ifPhysAddress
	// public final static String MAC = "1.3.6.1.2.1.2.2.1.6.22001";
	// public final static String MAC_NEW = "1.3.6.1.4.1.16001.1.8";

	// Device Base Information of Node
	public final static String SYSNAME = "1.3.6.1.4.1.44405.71.1.1.0";
	public final static String SYSCONTACT = "1.3.6.1.4.1.44405.71.1.2.0";
	public final static String SYSLOCATION = "1.3.6.1.4.1.44405.71.1.3.0";
	public final static String SYSBOOTTIME = "1.3.6.1.4.1.44405.71.1.4.0";
	public final static String SYSSOFTVERSION = "1.3.6.1.4.1.44405.71.1.6.0";
	public final static String SYSBOOTROMVERSION = "1.3.6.1.4.1.44405.71.1.7.0";
	public final static String SYSMACADDR = "1.3.6.1.4.1.44405.71.1.8.0";
	public final static String SYSVERSIONTIME = "1.3.6.1.4.1.44405.71.1.9.0";
	public final static String SYSTIME = "1.3.6.1.4.1.44405.71.1.10.0";
	public final static String CPUUsageRate = "1.3.6.1.4.1.44405.71.1.11.0";
	public final static String MemoryUsageRate = "1.3.6.1.4.1.44405.71.1.12.0";
	public final static String CURTemperature = "1.3.6.1.4.1.44405.71.1.13.0";

	// Vlan
	// Port num
	public final static String PORT_NUM = "1.3.6.1.4.1.44405.71.2.11.1.1.0";

	// vlan num,Same with Port num
	public final static String VLANNUM = "1.3.6.1.4.1.44405.71.2.20.1.0";
	public final static String VLANID = "1.3.6.1.4.1.44405.71.2.20.2.1.1";
	// For vlan batch operate
	public final static String VLAN_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.20.2";
	public final static String VLAN_CREATE = "1.3.6.1.4.1.44405.71.2.20.6.0";
	public final static String VLAN_CREATEportU = "1.3.6.1.4.1.44405.71.2.20.4.1.3";
	public final static String VLAN_CREATEportT = "1.3.6.1.4.1.44405.71.2.20.4.1.2";
	public final static String VLAN_DELETE = "1.3.6.1.4.1.44405.71.2.20.5.0";
	// For traverse the vlan_port,so VLAN_PORT1 to VLAN_PORT8 woudln't use for
	// the moment.The more vlan according to Port num
	public final static String VLAN_PORT = "1.3.6.1.4.1.44405.71.2.20.2.1";
	public final static String VLAN_PORT1 = "1.3.6.1.4.1.44405.71.2.20.2.1.2";
	public final static String VLAN_PORT2 = "1.3.6.1.4.1.44405.71.2.20.2.1.3";
	public final static String VLAN_PORT3 = "1.3.6.1.4.1.44405.71.2.20.2.1.4";
	public final static String VLAN_PORT4 = "1.3.6.1.4.1.44405.71.2.20.2.1.5";
	public final static String VLAN_PORT5 = "1.3.6.1.4.1.44405.71.2.20.2.1.6";
	public final static String VLAN_PORT6 = "1.3.6.1.4.1.44405.71.2.20.2.1.7";
	public final static String VLAN_PORT7 = "1.3.6.1.4.1.44405.71.2.20.2.1.8";
	public final static String VLAN_PORT8 = "1.3.6.1.4.1.44405.71.2.20.2.1.9";
	public final static String VLAN_PORT9 = "1.3.6.1.4.1.44405.71.2.20.2.1.10";
	public final static String VLAN_PORT10 = "1.3.6.1.4.1.44405.71.2.20.2.1.11";
	public final static String VLAN_PORT11 = "1.3.6.1.4.1.44405.71.2.20.2.1.12";
	public final static String VLAN_PORT12 = "1.3.6.1.4.1.44405.71.2.20.2.1.13";
	public final static String VLAN_PORT13 = "1.3.6.1.4.1.44405.71.2.20.2.1.14";
	public final static String VLAN_PORT14 = "1.3.6.1.4.1.44405.71.2.20.2.1.15";
	public final static String VLAN_PORT15 = "1.3.6.1.4.1.44405.71.2.20.2.1.16";
	public final static String VLAN_PORT16 = "1.3.6.1.4.1.44405.71.2.20.2.1.17";
	public final static String VLAN_PORT17 = "1.3.6.1.4.1.44405.71.2.20.2.1.18";
	public final static String VLAN_PORT18 = "1.3.6.1.4.1.44405.71.2.20.2.1.19";
	public final static String VLAN_PORT19 = "1.3.6.1.4.1.44405.71.2.20.2.1.20";
	public final static String VLAN_PORT20 = "1.3.6.1.4.1.44405.71.2.20.2.1.21";
	public final static String VLAN_PORT21 = "1.3.6.1.4.1.44405.71.2.20.2.1.22";
	public final static String VLAN_PORT22 = "1.3.6.1.4.1.44405.71.2.20.2.1.23";
	public final static String VLAN_PORT23 = "1.3.6.1.4.1.44405.71.2.20.2.1.24";
	public final static String VLAN_PORT24 = "1.3.6.1.4.1.44405.71.2.20.2.1.25";
	public final static String VLAN_PORT25 = "1.3.6.1.4.1.44405.71.2.20.2.1.26";
	public final static String VLAN_PORT26 = "1.3.6.1.4.1.44405.71.2.20.2.1.27";
	public final static String VLAN_PORT27 = "1.3.6.1.4.1.44405.71.2.20.2.1.28";
	public final static String VLAN_PORT28 = "1.3.6.1.4.1.44405.71.2.20.2.1.29";
	public final static String VLAN_PORTS_UPDATE = "1.3.6.1.4.1.44405.71.2.11.2.2.1";
	// Ports
	// For Port config batch operate
	public final static String PORTS_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.11.1.3";
	// Same with Port num
	public final static String PORTSINDEX = "1.3.6.1.4.1.44405.71.2.11.2.1";
	// For traverse
	public final static String PORTSID = "1.3.6.1.4.1.44405.71.2.11.1.2.1.1";
	public final static String PORTSTATE = "1.3.6.1.4.1.44405.71.2.11.1.2.1.2";
	public final static String PORTSPEED = "1.3.6.1.4.1.44405.71.2.11.1.2.1.3";
	public final static String PORTS_FLOWCTL = "1.3.6.1.4.1.44405.71.2.11.1.2.1.4";
	public final static String PORTS_LINK = "1.3.6.1.4.1.44405.71.2.11.1.2.1.5";
	public final static String PORTS_TYPE = "1.3.6.1.4.1.44405.71.2.11.1.2.1.6";
	public final static String PORTSDISCARD = "1.3.6.1.4.1.44405.71.2.11.2.2.1.4";

	public final static String Vendorname = "1.3.6.1.4.1.44405.71.2.11.4.2.1.1";
	public final static String VendorPN = "1.3.6.1.4.1.44405.71.2.11.4.2.1.2";
	public final static String Vendorrev = "1.3.6.1.4.1.44405.71.2.11.4.2.1.3";
	public final static String VendorSN = "1.3.6.1.4.1.44405.71.2.11.4.2.1.4";
	public final static String Datecode = "1.3.6.1.4.1.44405.71.2.11.4.2.1.5";
	public final static String BRNominal = "1.3.6.1.4.1.44405.71.2.11.4.2.1.6";
	public final static String Wavelength = "1.3.6.1.4.1.44405.71.2.11.4.2.1.7";
	public final static String TransMedia = "1.3.6.1.4.1.44405.71.2.11.4.2.1.8";
	public final static String LengthSM = "1.3.6.1.4.1.44405.71.2.11.4.2.1.9";
	public final static String Temperature = "1.3.6.1.4.1.44405.71.2.11.4.2.1.10";
	public final static String Voltage = "1.3.6.1.4.1.44405.71.2.11.4.2.1.11";
	public final static String TxBias = "1.3.6.1.4.1.44405.71.2.11.4.2.1.12";
	public final static String TxPower = "1.3.6.1.4.1.44405.71.2.11.4.2.1.13";
	public final static String RxPower = "1.3.6.1.4.1.44405.71.2.11.4.2.1.14";
	public final static String TemperatureHighAlarm = "1.3.6.1.4.1.44405.71.2.11.4.2.1.15";
	public final static String TemperatureLowAlarm = "1.3.6.1.4.1.44405.71.2.11.4.2.1.16";
	public final static String TemperatureHighWarning = "1.3.6.1.4.1.44405.71.2.11.4.2.1.17";
	public final static String TemperatureLowWarning = "1.3.6.1.4.1.44405.71.2.11.4.2.1.18";
	public final static String VoltageHighAlarm = "1.3.6.1.4.1.44405.71.2.11.4.2.1.19";
	public final static String VoltageLowAlarm = "1.3.6.1.4.1.44405.71.2.11.4.2.1.20";
	public final static String VoltageHighWarning = "1.3.6.1.4.1.44405.71.2.11.4.2.1.21";
	public final static String VoltageLowWarning = "1.3.6.1.4.1.44405.71.2.11.4.2.1.22";
	public final static String TxBiasHighAlarm = "1.3.6.1.4.1.44405.71.2.11.4.2.1.23";
	public final static String TxBiasLowAlarm = "1.3.6.1.4.1.44405.71.2.11.4.2.1.24";
	public final static String TxBiasHighWarning = "1.3.6.1.4.1.44405.71.2.11.4.2.1.25";
	public final static String TxBiasLowWarning = "1.3.6.1.4.1.44405.71.2.11.4.2.1.26";
	public final static String TxPowerHighAlarm = "1.3.6.1.4.1.44405.71.2.11.4.2.1.27";
	public final static String TxPowerLowAlarm = "1.3.6.1.4.1.44405.71.2.11.4.2.1.28";
	public final static String TxPowerHighWarning = "1.3.6.1.4.1.44405.71.2.11.4.2.1.29";
	public final static String TxPowerLowWarning = "1.3.6.1.4.1.44405.71.2.11.4.2.1.30";
	public final static String RxPowerHighAlarm = "1.3.6.1.4.1.44405.71.2.11.4.2.1.31";
	public final static String RxPowerLowAlarm = "1.3.6.1.4.1.44405.71.2.11.4.2.1.32";
	public final static String RxPowerHighWarning = "1.3.6.1.4.1.44405.71.2.11.4.2.1.33";
	public final static String RxPowerLowWarning = "1.3.6.1.4.1.44405.71.2.11.4.2.1.34";

	// Vlan ports
	public final static String PORTSINFOID = "1.3.6.1.4.1.44405.71.2.11.2.2.1.1";
	public final static String PORTSPVID = "1.3.6.1.4.1.44405.71.2.11.2.2.1.2";
	public final static String PORTSPRIORITY = "1.3.6.1.4.1.44405.71.2.11.2.2.1.3";

	// STP
	// For STP system configuration
	public final static String STPPRIORITY = "1.3.6.1.4.1.44405.71.2.16.1.2.0";
	public final static String STP_MAXAGE = "1.3.6.1.4.1.44405.71.2.16.1.3.0";
	public final static String STP_FORWARDDELAY = "1.3.6.1.4.1.44405.71.2.16.1.4.0";
	public final static String STP_PROTOCOLVERSION = "1.3.6.1.4.1.44405.71.2.16.1.5.0";
	// For STP Port configuration batch operate
	public final static String STP_PORT_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.16.1.7";
	// Same with Port num
	public final static String STPPORTNUM = "1.3.6.1.4.1.44405.71.2.16.1.6";
	// For traverse
	public final static String STPPORT = "1.3.6.1.4.1.44405.71.2.16.1.7.1.1";
	public final static String STP_MODE = "1.3.6.1.4.1.44405.71.2.16.1.7.1.2";
	public final static String STP_PATHCOST = "1.3.6.1.4.1.44405.71.2.16.1.7.1.3";
	// Different from STP_PRIORITY
	public final static String STP_PRIORITY = "1.3.6.1.4.1.44405.71.2.16.1.7.1.4";
	public final static String STP_EDGE = "1.3.6.1.4.1.44405.71.2.16.1.7.1.5";
	public final static String STP_P2P = "1.3.6.1.4.1.44405.71.2.16.1.7.1.6";

	// aging time
	public final static String AGINGTIME_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.8.3.0";

	// MAC
	public final static String UNICAST_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.8.7.0";
	public final static String UNICAST_BATCH_DELETE = "1.3.6.1.4.1.44405.71.2.8.5.0";
	public final static String UNICAST_MACNUM = "1.3.6.1.4.1.44405.71.2.8.1.0";
	// For traverse
	public final static String UNICAST_MACID = "1.3.6.1.4.1.44405.71.2.8.2.1.1";
	public final static String UNICAST_MACADDR = "1.3.6.1.4.1.44405.71.2.8.2.1.2";
	public final static String UNICAST_PORTIDX = "1.3.6.1.4.1.44405.71.2.8.2.1.3";
	public final static String UNICAST_VLANID = "1.3.6.1.4.1.44405.71.2.8.2.1.4";
	public final static String UNICAST_MACSTATE = "1.3.6.1.4.1.44405.71.2.8.2.1.5";
	// MULTICAST
	public final static String MULTICAST_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.10.6.0";
	public final static String MULTICAST_BATCH_DELETE = "1.3.6.1.4.1.44405.71.2.10.4.0";
	public final static String MULTIMACNUM = "1.3.6.1.4.1.44405.71.2.10.1.0";
	// For traverse
	public final static String MULTIMACID = "1.3.6.1.4.1.44405.71.2.10.2.1.1";
	public final static String MULTIMACADDR = "1.3.6.1.4.1.44405.71.2.10.2.1.2";
	public final static String MULTIPORTIDX = "1.3.6.1.4.1.44405.71.2.10.2.1.3";
	public final static String MULTIVLANID = "1.3.6.1.4.1.44405.71.2.10.2.1.4";

	// Mirror
	public final static String MIRRORDSTPORT = "1.3.6.1.4.1.44405.71.2.9.1.0";
	public final static String MIRROR_ISRCPORT = "1.3.6.1.4.1.44405.71.2.9.2.0";
	public final static String MIRROR_ESRCPORT = "1.3.6.1.4.1.44405.71.2.9.3.0";
	public final static String MIRROR_INGRESSMONITORODE = "1.3.6.1.4.1.44405.71.2.9.4.0";
	public final static String MIRROR_ENGRESSMONITORMODE = "1.3.6.1.4.1.44405.71.2.9.5.0";
	public final static String MIRROR_IDIV = "1.3.6.1.4.1.44405.71.2.9.6.0";
	public final static String MIRROR_EDIV = "1.3.6.1.4.1.44405.71.2.9.7.0";
	public final static String MIRROR_IMAC = "1.3.6.1.4.1.44405.71.2.9.8.0";
	public final static String MIRROR_EMAC = "1.3.6.1.4.1.44405.71.2.9.9.0";
	public final static String MIRRORSTART = "1.3.6.1.4.1.44405.71.2.9.10.0";

	// 802.1X
	public final static String DOT1X_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.1.1";
	public final static String DOT1XSTATE = "1.3.6.1.4.1.44405.71.2.1.1.1.0";
	public final static String DOT1X_RADIUSSERVER = "1.3.6.1.4.1.44405.71.2.1.1.2.0";
	public final static String DOT1X_REAUTHENTICATION = "1.3.6.1.4.1.44405.71.2.1.1.3.0";
	public final static String DOT1XPERIOD = "1.3.6.1.4.1.44405.71.2.1.1.4.0";
	public final static String DOT1XTIMEOUT = "1.3.6.1.4.1.44405.71.2.1.1.5.0";
	public final static String DOT1X_RADUDPPORT = "1.3.6.1.4.1.44405.71.2.1.1.6.0";
	public final static String DOT1XSECRET = "1.3.6.1.4.1.44405.71.2.1.1.8.0";
	public final static String DOT1XNUM = "1.3.6.1.4.1.44405.71.2.1.1.7.0";
	// For traverse
	public final static String DOT1XPORT = "1.3.6.1.4.1.44405.71.2.1.1.9.1.1";
	public final static String DOT1X_ADMINSTATE = "1.3.6.1.4.1.44405.71.2.1.1.9.1.2";
	public final static String DOT1X_PORTSTATE = "1.3.6.1.4.1.44405.71.2.1.1.9.1.3";

	// IGMP
	public final static String IGMP_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.3.1";
	public final static String IGMPMODE = "1.3.6.1.4.1.44405.71.2.3.1.1.0";
	public final static String IGMP_VIDNUM = "1.3.6.1.4.1.44405.71.2.3.1.2.0";
	// For traverse
	public final static String IGMP_VID = "1.3.6.1.4.1.44405.71.2.3.1.3.1.1";
	public final static String IGMPSTATE = "1.3.6.1.4.1.44405.71.2.3.1.3.1.2";
	public final static String IGMPQUERIER = "1.3.6.1.4.1.44405.71.2.3.1.3.1.3";
	// IGMP port state
	public final static String IGMP_RTIFID = "1.3.6.1.4.1.44405.71.2.3.3.2.1.1";
	public final static String IGMP_RTIFSTATE = "1.3.6.1.4.1.44405.71.2.3.3.2.1.2";

	// QOS system config
	public final static String QOSSTATE = "1.3.6.1.4.1.44405.71.2.12.1.0";
	public final static String QOSMODE = "1.3.6.1.4.1.44405.71.2.12.2.0";
	public final static String QOSQUEUENUMBER = "1.3.6.1.4.1.44405.71.2.12.3.0";
	public final static String QOSPRIORITYTRUST = "1.3.6.1.4.1.44405.71.2.12.4.0";
	// public final static String QOS_Q0WEIGHT = "1.3.6.1.4.1.16001.2.12.3.1";
	// public final static String QOS_Q1WEIGHT = "1.3.6.1.4.1.16001.2.12.3.2";
	// public final static String QOS_Q2WEIGHT = "1.3.6.1.4.1.16001.2.12.3.3";
	// public final static String QOS_Q3WEIGHT = "1.3.6.1.4.1.16001.2.12.3.4";
	public final static String QOS_Q0WEIGHT = "1.3.6.1.4.1.44405.71.2.12.5.1.0";
	public final static String QOS_Q1WEIGHT = "1.3.6.1.4.1.44405.71.2.12.5.2.0";
	public final static String QOS_Q2WEIGHT = "1.3.6.1.4.1.44405.71.2.12.5.3.0";
	public final static String QOS_Q3WEIGHT = "1.3.6.1.4.1.44405.71.2.12.5.4.0";

	// QOS priority config
	public final static String QOS_PRIORITY_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.12.6.2";
	//
	public final static String QOS_DOT1PRIORITYVALUE = "1.3.6.1.4.1.44405.71.2.12.6.2.1.1";
	public final static String QOS_DOTQUEUENUMBER = "1.3.6.1.4.1.44405.71.2.12.6.2.1.2";
	//
	public final static String QOS_TOSPRIORITYVALUE = "1.3.6.1.4.1.16001.2.12.6.4.1.1";
	public final static String QOS_TOSQUEUENUMBER = "1.3.6.1.4.1.16001.2.12.6.4.1.2";
	//
	public final static String QOS_DSCPPRIORITYVALUE = "1.3.6.1.4.1.44405.71.2.12.6.6.1.1";
	public final static String QOS_DSCPQUEUENUMBER = "1.3.6.1.4.1.44405.71.2.12.6.6.1.2";

	// QOS-trafficControl(Port speed limit)
	public final static String TRAFFICCONTROL_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.18.2";
	public final static String TFCPORTNUM = "1.3.6.1.4.1.44405.71.2.18.1.0";
	public final static String TFCPORT = "1.3.6.1.4.1.44405.71.2.18.2.1.1";
	public final static String TFC_EGRESS = "1.3.6.1.4.1.16001.2.18.2.1.2";
	public final static String TFC_TXRATE = "1.3.6.1.4.1.44405.71.2.18.2.1.2";
	public final static String TFC_INGRESS = "1.3.6.1.4.1.44405.2.18.2.1.4";
	public final static String TFC_RXRATE = "1.3.6.1.4.1.44405.71.2.18.2.1.5";

	// QOS-stormControl
	public final static String STORMCONTROL_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.15.2";
	public final static String STORMCONTROL_BATCH_OPERATE_80SERIES = "1.3.6.1.4.1.44405.71.2.15.2";
	public final static String STORMPORTNUM = "1.3.6.1.4.1.44405.71.2.15.1.0";
	public final static String STORMPORT = "1.3.6.1.4.1.44405.71.2.15.2.1.1";
	public final static String STORM_TYPE = "1.3.6.1.4.1.44405.71.2.15.2.1.2";
	public final static String STORM_LEVEL = "1.3.6.1.4.1.44405.71.2.15.2.1.3";
	// stormControl-80series
	public final static String STORM_TYPE_80SERIES = "1.3.6.1.4.1.16001.2.25.1.0";
	public final static String STORM_STORMIF_80SERIES = "1.3.6.1.4.1.16001.2.25.3.1.1";
	public final static String STORM_IFRATE_80SERIES = "1.3.6.1.4.1.16001.2.25.3.1.2";

	// GH_RING
	public final static String GHRING_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.2.4.0";
	public final static String GHRINGINFONUM = "1.3.6.1.4.1.44405.71.2.2.1.1.0";
	// For traverse
	public final static String GHRINGID = "1.3.6.1.4.1.44405.71.2.2.1.2.1.1";
	public final static String GHSWITCHTYPE = "1.3.6.1.4.1.44405.71.2.2.1.2.1.2";
	public final static String GHRINGENABLE = "1.3.6.1.4.1.44405.71.2.2.1.2.1.3";
	public final static String GHRINGSTATE = "1.3.6.1.4.1.44405.71.2.2.1.2.1.4";
	public final static String GHRINGAUTO = "1.3.6.1.4.1.44405.71.2.2.1.2.1.5";
	public final static String GHPORTMEMBERS = "1.3.6.1.4.1.44405.71.2.2.1.2.1.6";
	// GH_RING-ghRingPortinfo
	public final static String GHRINGPORTINFONUM = "1.3.6.1.4.1.44405.71.2.2.2.1.0";
	// For traverse
	public final static String GHRINGPORT = "1.3.6.1.4.1.44405.71.2.2.2.2.1.1";
	public final static String GHRINGPORTSTATE = "1.3.6.1.4.1.44405.71.2.2.2.2.1.2";
	public final static String GHRINGPORTRINGID = "1.3.6.1.4.1.44405.71.2.2.2.2.1.3";
	// Also ring mode
	public final static String GHRINGPORTRINGENABLE = "1.3.6.1.4.1.44405.71.2.2.2.2.1.4";
	public final static String GHRINGPORTRINGROLE = "1.3.6.1.4.1.44405.71.2.2.2.2.1.5";
	public final static String GHRINGFORSTATE = "1.3.6.1.4.1.44405.71.2.2.2.2.1.6";
	// Ring link bak
	public final static String GHRINGLINKID = "1.3.6.1.4.1.44405.71.2.2.3.1.0";
	public final static String GHRINGLINKPORT = "1.3.6.1.4.1.44405.71.2.2.3.2.0";
	public final static String GHRINGLINKROLE = "1.3.6.1.4.1.44405.71.2.2.3.3.0";

	// LACP
	public final static String LACP_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.5.1";
	public final static String LACPNUM = "1.3.6.1.4.1.44405.71.2.5.1.1.0";
	// For traverse
	public final static String LACPPORT = "1.3.6.1.4.1.44405.71.2.5.1.2.1.1";
	public final static String LACPSTATE = "1.3.6.1.4.1.44405.71.2.5.1.2.1.2";
	public final static String LACPKEY = "1.3.6.1.4.1.44405.71.2.5.1.2.1.3";
	public final static String LACPROLE = "1.3.6.1.4.1.44405.71.2.5.1.2.1.4";
	// lacp info
	public final static String LACPINFONUM = "1.3.6.1.4.1.44405.71.2.5.2.1.0";
	// For traverse
	public final static String LACPGROUP = "1.3.6.1.4.1.44405.71.2.5.2.2.1.1";
	public final static String LACP_PEERID = "1.3.6.1.4.1.44405.71.2.5.2.2.1.2";
	public final static String LACP_PEERKEY = "1.3.6.1.4.1.44405.71.2.5.2.2.1.3";
	public final static String LASTCHANGETIME = "1.3.6.1.4.1.44405.71.2.5.2.2.1.4";
	public final static String LACPPORTMEMBER = "1.3.6.1.4.1.44405.71.2.5.2.2.1.5";

	// Trunk(linkAggregation)
	public final static String TRUNK_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.6";
	public final static String LINKAGGREGATIONNUM = "1.3.6.1.4.1.44405.71.2.6.1.0";
	public final static String LINKAGGREGATIONID = "1.3.6.1.4.1.44405.71.2.6.2.1.1";
	public final static String LINKAGGREGATIONTYPE = "1.3.6.1.4.1.44405.71.2.6.2.1.2";
	public final static String LINKAGGREGATIONPORTS = "1.3.6.1.4.1.44405.71.2.6.2.1.3";

	// LLDP
	public final static String LLDP_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.7.1.6";
	// LLDP-paramater config
	public final static String LLDPINTERVAL = "1.3.6.1.4.1.44405.71.2.7.1.1.0";
	public final static String LLDPHOLD = "1.3.6.1.4.1.44405.71.2.7.1.2.0";
	public final static String LLDPTXDELAY = "1.3.6.1.4.1.44405.71.2.7.1.3.0";
	public final static String LLDPREINITDELAY = "1.3.6.1.4.1.44405.71.2.7.1.4.0";
	// LLDP-Port config
	public final static String LLDPNUM = "1.3.6.1.4.1.44405.71.2.7.1.5.0";
	// for traverse
	public final static String LLDPPORT = "1.3.6.1.4.1.44405.71.2.7.1.6.1.1";
	public final static String LLDPPORTSTATE = "1.3.6.1.4.1.44405.71.2.7.1.6.1.2";
	// LLDP Info Structure
	public final static String LLDPINFONUM = "1.3.6.1.4.1.44405.71.2.7.2.1.0";
	// for traverse
	public final static String LLDP_LOCALPORT = "1.3.6.1.4.1.44405.71.2.7.2.2.1.1";
	public final static String LLDP_CHASSISID = "1.3.6.1.4.1.44405.71.2.7.2.2.1.2";
	public final static String LLDPPORTID = "1.3.6.1.4.1.44405.71.2.7.2.2.1.3";
	public final static String LLDP_PORTDESCRIPTION = "1.3.6.1.4.1.44405.71.2.7.2.2.1.4";
	public final static String LLDP_SYSTEMNAME = "1.3.6.1.4.1.44405.71.2.7.2.2.1.5";
	public final static String LLDP_SYSTEMDESCRIPTION = "1.3.6.1.4.1.44405.71.2.7.2.2.1.6";
	public final static String LLDP_SYSTEMCAPABILITIES = "1.3.6.1.4.1.44405.71.2.7.2.2.1.7";
	public final static String LLDP_MANAGEMENTADDRESS = "1.3.6.1.4.1.44405.71.2.7.2.2.1.8";

	// SNTP
	public final static String SNTP = "1.3.6.1.4.1.44405.71.2.14.1.0";
	public final static String SNTP_PRISERVER = "1.3.6.1.4.1.44405.71.2.14.2.0";
	public final static String SNTP_SECSERVER = "1.3.6.1.4.1.44405.71.2.14.3.0";
	public final static String SNTP_POLLINTERVAL = "1.3.6.1.4.1.44405.71.2.14.4.0";
	public final static String SNTP_TIMEZONE = "1.3.6.1.4.1.44405.71.2.14.5.0";
	public final static String SNTP_TIME = "1.3.6.1.4.1.44405.71.2.14.6.0";

	// SNMP
	public final static String SNMPENGINEID = "1.3.6.1.6.3.10.2.1.1";
	public final static String SNMPCOMMUNITYSECURITYNAME = "1.3.6.1.6.3.18.1.1.1.3";

	// SERIAL
	public final static String SERIAL_BATCH_OPERATE = "1.3.6.1.4.1.16001.2.13.1.3";
	public final static String SERIALSNUM = "1.3.6.1.4.1.16001.2.13.1.1";
	// for traverse
	public final static String SERIALINDEX = "1.3.6.1.4.1.16001.2.13.1.2.1.1";
	public final static String SERIALNAME = "1.3.6.1.4.1.16001.2.13.1.2.1.2";
	public final static String SERIALBAUDRATE = "1.3.6.1.4.1.16001.2.13.1.2.1.3";
	public final static String SERIALDATABITS = "1.3.6.1.4.1.16001.2.13.1.2.1.4";
	public final static String SERIALPARITY = "1.3.6.1.4.1.16001.2.13.1.2.1.5";
	public final static String SERIALSTOPBITS = "1.3.6.1.4.1.16001.2.13.1.2.1.6";
	public final static String SERIALMODE = "1.3.6.1.4.1.16001.2.13.1.2.1.7";
	public final static String SERIAL_TCPCLIENT = "1.3.6.1.4.1.16001.2.13.1.2.1.8";
	public final static String SERIAL_UDPCLIENT = "1.3.6.1.4.1.16001.2.13.1.2.1.9";
	public final static String SERIAL_TCPSERVER = "1.3.6.1.4.1.16001.2.13.1.2.1.10";
	public final static String SERIAL_UDPSERVER = "1.3.6.1.4.1.16001.2.13.1.2.1.11";

	// rmon
	public final static String RMON_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.21.5.0";
	//
	public final static String RMONSTATICSNUM = "1.3.6.1.4.1.44405.71.2.21.1.1.0";
	//
	public final static String RMONSTATICSINDEX = "1.3.6.1.4.1.44405.71.2.21.1.2.1.1";
	public final static String RMONPORTINDEX = "1.3.6.1.4.1.44405.71.2.21.1.2.1.2";
	//
	public final static String RMON_STCTRLINDEX = "1.3.6.1.4.1.44405.71.2.21.1.3.1.1";
	public final static String RMON_STIFINDEX = "1.3.6.1.4.1.44405.71.2.21.1.3.1.2";
	public final static String RMON_STDROPEVENTS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.3";
	public final static String RMON_STOCTETS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.4";
	public final static String RMON_STPACKETS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.5";
	public final static String RMON_STBCASTPKTS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.6";
	public final static String RMON_STMCASTPKTS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.7";
	public final static String RMON_STCRCALIGN = "1.3.6.1.4.1.44405.71.2.21.1.3.1.8";
	public final static String RMON_STUNDERSIZE = "1.3.6.1.4.1.44405.71.2.21.1.3.1.9";
	public final static String RMON_STOVERSIZE = "1.3.6.1.4.1.44405.71.2.21.1.3.1.10";
	public final static String RMON_STFRAGMENTS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.11";
	public final static String RMON_STJABBERS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.12";
	public final static String RMON_STCOLLISIONS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.13";
	public final static String RMON_STPKTS64 = "1.3.6.1.4.1.44405.71.2.21.1.3.1.14";
	public final static String RMON_STPKTS65TO127 = "1.3.6.1.4.1.44405.71.2.21.1.3.1.15";
	public final static String RMON_STPKTS128TO255 = "1.3.6.1.4.1.44405.71.2.21.1.3.1.16";
	public final static String RMON_STPKTS256TO511 = "1.3.6.1.4.1.44405.71.2.21.1.3.1.17";
	public final static String RMON_STPKTS512TO1023 = "1.3.6.1.4.1.44405.71.2.21.1.3.1.18";
	public final static String RMON_STPKTS1024TO1518 = "1.3.6.1.4.1.44405.71.2.21.1.3.1.19";
	public final static String RMON_IFINDISCARDS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.20";
	public final static String RMON_IFOUTDISCARDS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.21";
	public final static String RMON_TXPACKETS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.22";
	public final static String RMON_TXBCASTPKTS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.23";
	public final static String RMON_TXMCASTPKTS = "1.3.6.1.4.1.44405.71.2.21.1.3.1.24";
	//
	public final static String RMONALARMNUM = "1.3.6.1.4.1.44405.71.2.21.3.1.0";
	//
	public final static String RMON_HISCTRLINDEX = "1.3.6.1.4.1.44405.71.2.21.3.2.1.1";
	public final static String RMON_HISIFINDEX = "1.3.6.1.4.1.44405.71.2.21.3.2.1.2";
	public final static String RMON_HISINTERVAL = "1.3.6.1.4.1.44405.71.2.21.3.2.1.3";
	public final static String RMON_HISVARIABLE = "1.3.6.1.4.1.44405.71.2.21.3.2.1.4";
	public final static String RMON_HISSAMPLETYPE = "1.3.6.1.4.1.44405.71.2.21.3.2.1.5";
	public final static String RMON_HISRISINGTHRESHOLD = "1.3.6.1.4.1.44405.71.2.21.3.2.1.6";
	public final static String RMON_HISFALLINGTHRESHOLD = "1.3.6.1.4.1.44405.71.2.21.3.2.1.7";
	public final static String RMON_HISRISINGEVENTINDEX = "1.3.6.1.4.1.44405.71.2.21.3.2.1.8";
	public final static String RMON_HISFALLINGEVENTINDEX = "1.3.6.1.4.1.44405.71.2.21.3.2.1.9";
	//
	public final static String RMONEVENTNUMBER = "1.3.6.1.4.1.44405.71.2.21.4.1.0";
	//
	public final static String RMON_EVENTCTRLINDEX = "1.3.6.1.4.1.44405.71.2.21.4.2.1.1";
	public final static String RMON_EVENTDESCRIPTION = "1.3.6.1.4.1.44405.71.2.21.4.2.1.2";
	public final static String RMON_EVENTTYPE = "1.3.6.1.4.1.44405.71.2.21.4.2.1.3";
	public final static String RMON_EVENTCOMMUNITY = "1.3.6.1.4.1.44405.71.2.21.4.2.1.4";

	// SNMP
	public final static String SNMP_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.22.7.0";
	public final static String SNMP_ENGINEID = "1.3.6.1.4.1.44405.71.2.22.1.1.0";
	// snmpView
	public final static String SNMP_VIEWNUM = "1.3.6.1.4.1.44405.71.2.22.2.1.0";
	public final static String SNMP_VIEW_NAME = "1.3.6.1.4.1.44405.71.2.22.2.2.1.1";
	public final static String SNMP_VIEW_SUBTREE = "1.3.6.1.4.1.44405.71.2.22.2.2.1.2";
	public final static String SNMP_VIEW_TYPE = "1.3.6.1.4.1.44405.71.2.22.2.2.1.3";
	// snmpComm
	public final static String SNMP_COMMNUM = "1.3.6.1.4.1.44405.71.2.22.+" + "3.1.0";
	public final static String SNMP_COMM_NAME = "1.3.6.1.4.1.44405.71.2.22.3.2.1.1";
	public final static String SNMP_ACCESS_VIEW_NAME = "1.3.6.1.4.1.44405.71.2.22.3.2.1.2";
	public final static String SNMP_ACCESS_LIMITS = "1.3.6.1.4.1.44405.71.2.22.3.2.1.3";
	// snmpGroup
	public final static String SNMP_GROUPNUM = "1.3.6.1.4.1.44405.71.2.22.4.1.0";
	public final static String SNMP_GROUP_NAME = "1.3.6.1.4.1.44405.71.2.22.4.2.1.1";
	public final static String SNMP_SECURE_NAME = "1.3.6.1.4.1.44405.71.2.22.4.2.1.2";
	public final static String SNMP_SECURE_LEVEL = "1.3.6.1.4.1.44405.71.2.22.4.2.1.3";
	// snmpUser
	public final static String SNMP_USERNUM = "1.3.6.1.4.1.44405.71.2.22.5.1.0";
	public final static String SNMP_USER_NAME = "1.3.6.1.4.1.44405.71.2.22.5.2.1.1";
	public final static String SNMP_ACCESS_GROUP = "1.3.6.1.4.1.44405.71.2.22.5.2.1.2";
	public final static String SNMP_VERSION = "1.3.6.1.4.1.44405.71.2.22.5.2.1.3";
	// snmpHost
	public final static String SNMP_HOST_OPERAT = "1.3.6.1.4.1.44405.71.2.22.7.0";
	public final static String SNMP_HOSTNUM = "1.3.6.1.4.1.44405.71.2.22.6.1.0";
	public final static String SNMP_HOST_ADDR = "1.3.6.1.4.1.44405.71.2.22.6.2.1.1";
	public final static String SNMP_ACCESS_VERSION = "1.3.6.1.4.1.44405.71.2.22.6.2.1.2";
	public final static String SNMP_ACCESS_COMM = "1.3.6.1.4.1.44405.71.2.22.6.2.1.3";

	// reboot
	public final static String SNMP_REBOOT = "1.3.6.1.4.1.44405.71.2.23.6.0";
	// upgrade
	public final static String SNMP_UPGRADE = "1.3.6.1.4.1.44405.71.2.23.2.0";
	// downloadcfg
	public final static String SNMP_DOWNLOADCFG = "1.3.6.1.4.1.44405.71.2.23.4.0";
	// uploadcfg
	public final static String SNMP_UPLOADCFG = "1.3.6.1.4.1.44405.71.2.23.5.0";
	// get update state
	public final static String SNMP_GET_UPDATE_STATE = "1.3.6.1.4.1.44405.71.2.23.7.0";

	// Switcher user
	public final static String SWITCHER_USER_BATCH_OPERATE = "1.3.6.1.4.1.44405.71.2.24.3.0";
	public final static String SWITCHER_USER_NUMBER = "1.3.6.1.4.1.44405.71.2.24.1.0";
	public final static String SWITCHER_USER_NAME = "1.3.6.1.4.1.44405.71.2.24.2.1.1";
	public final static String SWITCHER_USER_ROLE = "1.3.6.1.4.1.44405.71.2.24.2.1.2";
}
