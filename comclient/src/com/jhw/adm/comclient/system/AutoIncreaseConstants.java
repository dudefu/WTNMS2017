package com.jhw.adm.comclient.system;

/**
 * 
 * @author xiongbo
 * 
 */
public class AutoIncreaseConstants {
	private static int i = 0;
	public final static int LAYER2LLDP = ++i;// For layer2
	public final static int SYSTEM = ++i;// Maybe removed
	public final static int IFTABLE = ++i;// For all device
	public final static int IPADDRTABLE = ++i;// For layer3
	public final static int IPNETTOMEDIATABLE = ++i;// For layer3
	public final static int DOT1DSTPPORTTABLE = ++i;// For OLT
	public final static int LAYER3LLDP = ++i;// For layer3
}
