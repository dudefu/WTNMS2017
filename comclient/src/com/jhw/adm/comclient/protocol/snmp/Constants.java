package com.jhw.adm.comclient.protocol.snmp;

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

	// // OID:LLDP Topology Discovery
	// public final static String LLDP_INFO_NUM = "1.3.6.1.4.1.16001.2.7.2.1";
	// public final static String LOCAL_PORT = "1.3.6.1.4.1.16001.2.7.2.2.1.1";
	// public final static String CHASSISID = "1.3.6.1.4.1.16001.2.7.2.2.1.2";
	// public final static String LLDP_PORTID = "1.3.6.1.4.1.16001.2.7.2.2.1.3";
	// public final static String PORT_DESCRIPTION =
	// "1.3.6.1.4.1.16001.2.7.2.2.1.4";
	// public final static String SYSTEM_NAME = "1.3.6.1.4.1.16001.2.7.2.2.1.5";
	// public final static String SYSTEM_DESCRIPTION =
	// "1.3.6.1.4.1.16001.2.7.2.2.1.6";
	// public final static String SYSTEM_CAPABILITIES =
	// "1.3.6.1.4.1.16001.2.7.2.2.1.7";
	// public final static String MANAGEMENT_ADDRESS =
	// "1.3.6.1.4.1.16001.2.7.2.2.1.8";
	//
	// // IP Information of Node
	// public final static String IP = "1.3.6.1.4.1.16001.2.4.1";
	// public final static String NETMASK = "1.3.6.1.4.1.16001.2.4.2";
	// public final static String GATEWAY = "1.3.6.1.4.1.16001.2.4.3";
	// // The end of ifPhysAddress
	// public final static String MAC = "1.3.6.1.2.1.2.2.1.6.22001";
	//
	// // Device Base Information of Node
	// public final static String SYSNAME = "1.3.6.1.4.1.16001.1.1";
	// public final static String SYSCONTACT = "1.3.6.1.4.1.16001.1.2";
	// public final static String SYSLOCATION = "1.3.6.1.4.1.16001.1.3";
	// public final static String SYSBOOTTIME = "1.3.6.1.4.1.16001.1.4";
	//
	// // For vlan
	// public final static String VLANNUM = "1.3.6.1.4.1.16001.2.20.1";
	// public final static String VLANID = "1.3.6.1.4.1.16001.2.20.2.1.1";
	// // For traverse the vlan_port,so VLAN_PORT1 to VLAN_PORT8 woudln't use
	// for
	// // the moment
	// public final static String VLAN_PORT = "1.3.6.1.4.1.16001.2.20.2.1";
	// public final static String VLAN_PORT1 = "1.3.6.1.4.1.16001.2.20.2.1.2";
	// public final static String VLAN_PORT2 = "1.3.6.1.4.1.16001.2.20.2.1.3";
	// public final static String VLAN_PORT3 = "1.3.6.1.4.1.16001.2.20.2.1.4";
	// public final static String VLAN_PORT4 = "1.3.6.1.4.1.16001.2.20.2.1.5";
	// public final static String VLAN_PORT5 = "1.3.6.1.4.1.16001.2.20.2.1.6";
	// public final static String VLAN_PORT6 = "1.3.6.1.4.1.16001.2.20.2.1.7";
	// public final static String VLAN_PORT7 = "1.3.6.1.4.1.16001.2.20.2.1.8";
	// public final static String VLAN_PORT8 = "1.3.6.1.4.1.16001.2.20.2.1.9";
	//
	// // For vlan batch operate
	// public final static String VLAN_BATCH_OPERATE =
	// "1.3.6.1.4.1.16001.2.20.3";

}
