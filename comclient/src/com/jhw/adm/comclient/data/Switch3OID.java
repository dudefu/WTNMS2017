package com.jhw.adm.comclient.data;

/**
 * 
 * @author xiongbo
 * 
 */
public interface Switch3OID {
	// For Vlan add,update and delete
	public final static String DOT1QVLANSTATICROWSTATUS = "1.3.6.1.2.1.17.7.1.4.3.1.5";
	// Vlan Parameters
	public final static String DOT1QVLANSTATICNAME = "1.3.6.1.2.1.17.7.1.4.3.1.1";
	public final static String DOT1QVLANSTATICEGRESSPORTS = "1.3.6.1.2.1.17.7.1.4.3.1.2";
	public final static String DOT1QVLANFORBIDDENEGRESSPORTS = "1.3.6.1.2.1.17.7.1.4.3.1.3";
	public final static String DOT1QVLANSTATICUNTAGGEDPORTS = "1.3.6.1.2.1.17.7.1.4.3.1.4";
	// Vlan Port Parameters
	public final static String DOT1QPVID = "1.3.6.1.2.1.17.7.1.4.5.1.1";
	public final static String DOT1QPORTACCEPTABLEFRAMETYPES = "1.3.6.1.2.1.17.7.1.4.5.1.2";
	public final static String DOT1QPORTINGRESSFILTERING = "1.3.6.1.2.1.17.7.1.4.5.1.3";
	public final static String DOT1QPORTGVRPSTATUS = "1.3.6.1.2.1.17.7.1.4.5.1.4";
	// STP Parameters
	public final static String DOT1DSTPPROTOCOLSPECIFICATION = "1.3.6.1.2.1.17.2.1.0";//
	public final static String DOT1DSTPPRIORITY = "1.3.6.1.2.1.17.2.2.0";//
	// public final static String DOT1DSTPTIMESINCETOPOLOGYCHANGE =
	// "1.3.6.1.2.1.17.2.3.0";
	// public final static String DOT1DSTPTOPCHANGES = "1.3.6.1.2.1.17.2.4.0";
	// public final static String DOT1DSTPDESIGNATEDROOT =
	// "1.3.6.1.2.1.17.2.5.0";
	// public final static String DOT1DSTPROOTCOST = "1.3.6.1.2.1.17.2.6.0";
	// public final static String DOT1DSTPROOTPORT = "1.3.6.1.2.1.17.2.7.0";
	// public final static String DOT1DSTPMAXAGE = "1.3.6.1.2.1.17.2.8.0";
	// public final static String DOT1DSTPHELLOTIME = "1.3.6.1.2.1.17.2.9.0";
	// public final static String DOT1DSTPHOLDTIME = "1.3.6.1.2.1.17.2.10.0";
	// public final static String DOT1DSTPFORWARDDELAY =
	// "1.3.6.1.2.1.17.2.11.0";
	public final static String DOT1DSTPBRIDGEMAXAGE = "1.3.6.1.2.1.17.2.12.0";//
	public final static String DOT1DSTPBRIDGEHELLOTIME = "1.3.6.1.2.1.17.2.13.0";//
	public final static String DOT1DSTPBRIDGEFORWARDDELAY = "1.3.6.1.2.1.17.2.14.0";//
	// STP Port Parameters
	public final static String DOT1DSTPPORT = "1.3.6.1.2.1.17.2.15.1.1";
	public final static String DOT1DSTPPORTENABLE = "1.3.6.1.2.1.17.2.15.1.4";
	public final static String DOT1DSTPPORTPRIORITY = "1.3.6.1.2.1.17.2.15.1.2";
	public final static String DOT1DSTPPORTPATHCOST = "1.3.6.1.2.1.17.2.15.1.5";
	// LLDP Parameters
	public final static String LLDPMESSAGETXINTERVAL = "1.3.6.1.4.1.16001.127.1.1.1.0";// 报文发送周期
	public final static String LLDPREINITDELAY = "1.3.6.1.4.1.16001.127.1.1.3.0";// Reinit
	// LLDP Port Parameters
	public final static String LLDPPORTCONFIGPORTNUM = "1.3.6.1.4.1.16001.127.1.1.6.1.1";
	public final static String LLDPPORTCONFIGADMINSTATUS = "1.3.6.1.4.1.16001.127.1.1.6.1.2";
	// TODO
	public final static String DOT1DSTPPORTDESIGNATEDCOST = "1.3.6.1.2.1.17.2.15.1.7";
	// BaseConfig Parameters
	public final static String SYSNAME = "1.3.6.1.2.1.1.5.0";
	public final static String SYSDESCR = "1.3.6.1.2.1.1.1.0";

	// ifTable Parameters
	public final static String IFDESCR = "1.3.6.1.2.1.2.2.1.2";
}
