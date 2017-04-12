package com.jhw.adm.comclient.service;

import java.util.Map;
import java.util.Vector;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;

/**
 * 
 * @author xiongbo
 * 
 * 1.Device query
 * 
 * 2.Device config
 * 
 * 3.Vlan config
 * 
 */
public class SwitchService extends BaseHandler {
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;

	/**
	 * @TODO
	 */
	public Object getSwitchInfo(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		Vector<String> vbs = new Vector<String>(4);
		vbs.add(OID.SYSNAME);
		vbs.add(OID.SYSCONTACT);
		vbs.add(OID.SYSLOCATION);
		vbs.add(OID.SYSBOOTTIME);
		snmpV2.snmpGet(vbs);

		return null;
	}

	/**
	 * @TODO
	 */
	public boolean setSwitchInfo(String ip, Map<String, Object> vbs) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);

		snmpV2.snmpSet(vbs);

		return true;
	}

	public Object getIp(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		Vector<String> vbs = new Vector<String>(3);
		vbs.add(OID.IP);
		vbs.add(OID.NETMASK);
		vbs.add(OID.GATEWAY);
		snmpV2.snmpGet(vbs);

		return null;
	}

	public boolean setIp(String ip, Map<String, Object> vbs) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.snmpSet(vbs);

		return true;
	}

	public Object getVlans(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);

		// Port num
		PDU response = snmpV2.snmpGet(OID.PORT_NUM);
		int portNum = 0;
		if (response != null) {
			Vector<VariableBinding> recVBs = response.getVariableBindings();
			portNum = recVBs.elementAt(0).getVariable().toInt();
			System.out.println("portNum:" + portNum);
			recVBs.clear();
		}
		// Vlan num
		response = snmpV2.snmpGet(OID.VLANNUM);
		int vlanNum = 0;
		if (response != null) {
			Vector<VariableBinding> recVBs = response.getVariableBindings();
			vlanNum = recVBs.elementAt(0).getVariable().toInt();
			System.out.println("vlanNum:" + vlanNum);
			recVBs.clear();
		}
		if (vlanNum != 0 && portNum != 0) {
			Vector<String> vlanvbs = new Vector<String>();
			vlanvbs.add(OID.VLANID);
			for (int i = 1; i <= portNum; i++) {
				vlanvbs.add(OID.VLAN_PORT + "." + (i + 1));
			}
			// vlanvbs.add(OID.VLAN_PORT1);
			// vlanvbs.add(OID.VLAN_PORT2);
			// vlanvbs.add(OID.VLAN_PORT3);
			// vlanvbs.add(OID.VLAN_PORT4);
			// vlanvbs.add(OID.VLAN_PORT5);
			// vlanvbs.add(OID.VLAN_PORT6);
			// vlanvbs.add(OID.VLAN_PORT7);
			// vlanvbs.add(OID.VLAN_PORT8);
			response = snmpV2.snmpGetBulk(vlanvbs, 0, vlanNum);
		}

		return null;
	}

	// public Object addVlan(String ip, Vlan vlan) {
	// snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
	// byte[] buf = dataBufferBuilder.vlanCreate(vlan.getVlanID(), "");
	// PDU response = snmpV2.snmpSet(OID.VLAN_BATCH_OPERATE, buf);
	//
	// return null;
	// }
	//
	// public Object updateVlan(String ip, Vlan vlan) {
	// snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
	// byte[] buf = dataBufferBuilder.vlanUpdate(vlan.getVlanID(), "");
	// PDU response = snmpV2.snmpSet(OID.VLAN_BATCH_OPERATE, buf);
	//
	// return null;
	// }
	//
	// public Object deleteVlan(String ip, Vlan vlan) {
	// snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
	// byte[] buf = dataBufferBuilder.vlanDelete(vlan.getVlanID());
	// PDU response = snmpV2.snmpSet(OID.VLAN_BATCH_OPERATE, buf);
	//
	// return null;
	// }

	// Spring inject
	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

	public DataBufferBuilder getDataBufferBuilder() {
		return dataBufferBuilder;
	}

	public void setDataBufferBuilder(DataBufferBuilder dataBufferBuilder) {
		this.dataBufferBuilder = dataBufferBuilder;
	}

}
