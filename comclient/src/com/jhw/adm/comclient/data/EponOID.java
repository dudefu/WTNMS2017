package com.jhw.adm.comclient.data;

/**
 * 
 * @author xiongbo
 * 
 */
public interface EponOID {
	// bdEponOltSlot
	public final static String OLTSLOTINDEX = "1.3.6.1.4.1.3320.101.21.1.1.1";
	public final static String OLTSLOTHELLOINTERVAL = "1.3.6.1.4.1.3320.101.21.1.1.2";
	public final static String OLTSLOTDEADINTERVAL = "1.3.6.1.4.1.3320.101.21.1.1.3";
	public final static String OLTSLOTCHIPSREGISTEREDNUMBER = "1.3.6.1.4.1.3320.101.21.1.1.4";
	// For add,update and delete
	public final static String DOT1QVLANSTATICROWSTATUS = "1.3.6.1.2.1.17.7.1.4.3.1.5";
	// bdEponOltChipInfo
	public final static String OLTCHIPINDEX = "1.3.6.1.4.1.3320.101.2.1.1.1";
	public final static String OLTCHIPSLOTID = "1.3.6.1.4.1.3320.101.2.1.1.2";
	public final static String OLTCHIPMODULEID = "1.3.6.1.4.1.3320.101.2.1.1.3";
	public final static String OLTCHIPDEVICEID = "1.3.6.1.4.1.3320.101.2.1.1.4";
	public final static String OLTCHIPMACADDRESS = "1.3.6.1.4.1.3320.101.2.1.1.5";
	public final static String OLTCHIPSTATUS = "1.3.6.1.4.1.3320.101.2.1.1.6";
	// olt Encryp
	public final static String OLTENCRYPSTATUS = "1.3.6.1.4.1.3320.101.1.1";
	public final static String OLTENCRYPMODE = "1.3.6.1.4.1.3320.101.1.2";
	public final static String OLTREKEYINGTIME = "1.3.6.1.4.1.3320.101.1.3";
	// Multicast
	public final static String OLTDLFDROP = "1.3.6.1.4.1.3320.101.1.14";
	public final static String OLTV1PACKETS = "1.3.6.1.4.1.3320.101.1.15";
	public final static String OLTV2PACKETS = "1.3.6.1.4.1.3320.101.1.16";
	public final static String OLTV3PACKETS = "1.3.6.1.4.1.3320.101.1.17";
	public final static String OLTJOINSPACKETS = "1.3.6.1.4.1.3320.101.1.18";
	public final static String OLTLEAVESPACKETS = "1.3.6.1.4.1.3320.101.1.19";
	public final static String OLTGENERALQUERYPACKETS = "1.3.6.1.4.1.3320.101.1.20";
	public final static String OLTSPECIALQUERYPACKETS = "1.3.6.1.4.1.3320.101.1.21";
	public final static String OLTHOSTQUERYFREQ = "1.3.6.1.4.1.3320.101.1.6";// RouteAge
	public final static String OLTMCSTSTATUS = "1.3.6.1.4.1.3320.101.1.7";
	public final static String OLTMCSTMODE = "1.3.6.1.4.1.3320.101.1.8";
	public final static String OLTIGMPPROXYSTATUS = "1.3.6.1.4.1.3320.101.1.9";
	public final static String OLTIGMPQUERIERADDRESS = "1.3.6.1.4.1.3320.101.1.10";
	public final static String OLTQUERYMAXRESPONSETIME = "1.3.6.1.4.1.3320.101.1.11";
	public final static String OLTLASTMEMBERQUERYINTERVAL = "1.3.6.1.4.1.3320.101.1.12";
	public final static String OLTLASTMEMBERQUERYCOUNT = "1.3.6.1.4.1.3320.101.1.13";
	// Vlan
	public final static String DOT1QVLANSTATICNAME = "1.3.6.1.2.1.17.7.1.4.3.1.1";
	public final static String DOT1QVLANSTATICEGRESSPORTS = "1.3.6.1.2.1.17.7.1.4.3.1.2";
	public final static String DOT1QVLANFORBIDDENEGRESSPORTS = "1.3.6.1.2.1.17.7.1.4.3.1.3";
	public final static String DOT1QVLANSTATICUNTAGGEDPORTS = "1.3.6.1.2.1.17.7.1.4.3.1.4";
	// DBA
	public final static String OLTDBAMODE = "1.3.6.1.4.1.3320.101.1.22.0";
	public final static String OLTDBAALGORITHM = "1.3.6.1.4.1.3320.101.1.23.0";
	public final static String OLTIFDBAPARAMCYCLETIME = "1.3.6.1.4.1.3320.101.1.33.0";
	public final static String OLTIFDBAPARAMDISCFREQ = "1.3.6.1.4.1.3320.101.1.34.0";
	public final static String OLTIFDBAPARAMDISCTIME = "1.3.6.1.4.1.3320.101.1.35.0";
	// LLID Bandwidth
	public final static String LLIDIFINDEX = "1.3.6.1.4.1.3320.101.9.1.1.1";
	public final static String LLIDTOEPONPORTDIID = "1.3.6.1.4.1.3320.101.9.1.1.2";
	public final static String LLIDSEQUENCENO = "1.3.6.1.4.1.3320.101.9.1.1.3";
	public final static String LLIDIFPIR = "1.3.6.1.4.1.3320.101.9.1.1.8";
	public final static String LLIDIFCIR = "1.3.6.1.4.1.3320.101.9.1.1.9";
	public final static String LLIDIFFIR = "1.3.6.1.4.1.3320.101.9.1.1.10";
	// For update
	public final static String LLIDIFCONFIGROWSTATUS = "1.3.6.1.4.1.3320.101.9.1.1.11";

	// STP
	public final static String DOT1DSTPPROTOCOLSPECIFICATION = "1.3.6.1.2.1.17.2.1.0";
	public final static String DOT1DSTPPRIORITY = "1.3.6.1.2.1.17.2.2.0";
	public final static String DOT1DSTPTIMESINCETOPOLOGYCHANGE = "1.3.6.1.2.1.17.2.3.0";
	public final static String DOT1DSTPTOPCHANGES = "1.3.6.1.2.1.17.2.4.0";
	public final static String DOT1DSTPDESIGNATEDROOT = "1.3.6.1.2.1.17.2.5.0";
	public final static String DOT1DSTPROOTCOST = "1.3.6.1.2.1.17.2.6.0";
	public final static String DOT1DSTPROOTPORT = "1.3.6.1.2.1.17.2.7.0";
	public final static String DOT1DSTPMAXAGE = "1.3.6.1.2.1.17.2.8.0";
	public final static String DOT1DSTPHELLOTIME = "1.3.6.1.2.1.17.2.9.0";
	public final static String DOT1DSTPHOLDTIME = "1.3.6.1.2.1.17.2.10.0";
	public final static String DOT1DSTPFORWARDDELAY = "1.3.6.1.2.1.17.2.11.0";
	public final static String DOT1DSTPBRIDGEMAXAGE = "1.3.6.1.2.1.17.2.12.0";
	public final static String DOT1DSTPBRIDGEHELLOTIME = "1.3.6.1.2.1.17.2.13.0";
	public final static String DOT1DSTPBRIDGEFORWARDDELAY = "1.3.6.1.2.1.17.2.14.0";

	public final static String DOT1DSTPPORT = "1.3.6.1.2.1.17.2.15.1.1";
	public final static String DOT1DSTPPORTPRIORITY = "1.3.6.1.2.1.17.2.15.1.2";

}
