package com.jhw.adm.comclient.protocol.snmp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.snmp4j.PDU;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.util.TableEvent;

/**
 * 
 * 
 * @author xiongbo
 * 
 */
public abstract class AbstractSnmp {
	// v2
	public void instanceV2() throws SnmpException {
	}

	public void instanceV2(String ip, int port, String community)
			throws SnmpException {
	}

	public void instanceV2(String ip, int port) throws SnmpException {
	}

	public void instanceV2(String ip) throws SnmpException {
	}

	// v3
	public void instanceV3() throws SnmpException {
	}

	public void instanceV3(String ip, int port, OctetString contextEngineID,
			OctetString contextName, OctetString securityName,
			OID privProtocol, OID authProtocol, OctetString privPassphrase,
			OctetString authPassphrase) throws SnmpException {
	}

	public void instanceV3(String ip, int port, OctetString contextEngineID,
			OID privProtocol, OID authProtocol, OctetString privPassphrase,
			OctetString authPassphrase) throws SnmpException {
	}

	public void instanceV3(String ip, OctetString contextEngineID,
			OID privProtocol, OID authProtocol, OctetString privPassphrase,
			OctetString authPassphrase) throws SnmpException {
	}

	// v2 and v3 operate
	public abstract PDU snmpGet(String oid) throws SnmpException;

	public abstract PDU snmpGet(Vector<String> vbs) throws SnmpException;

	public abstract PDU snmpGetNext(Vector<String> vbs) throws SnmpException;

	public abstract PDU snmpGetBulk(Vector<String> vbs, int nonRepeaters,
			int maxRepetitions) throws SnmpException;

	public abstract PDU snmpGetBulk(Vector<String> vbs) throws SnmpException;

	public abstract PDU snmpSet(Map<String, Object> oids) throws SnmpException;

	/**
	 * Special For LLID
	 */
	public abstract PDU snmpSet(LinkedHashMap<String, Object> oids,
			String rowStatus) throws SnmpException;

	public abstract PDU snmpSet(String oid, Object obj) throws SnmpException;

	public abstract PDU snmpSet(String oid, Object[] obj, int portNo)
			throws SnmpException;

	/**
	 * For table add new row
	 */
	public abstract PDU snmpTableAddRow(Map<String, Object> oids,
			String rowStatusColumnOID, String rowIndexOID) throws SnmpException;

	/**
	 * For table delete row
	 */
	public abstract PDU snmpTableDeleteRow(String rowStatusColumnOID,
			String rowIndexOID) throws SnmpException;

	/**
	 * For display table
	 */
	public abstract List<TableEvent> snmpTableDisplay(String[] columnOIDs,
			String lowerBoundIndex, String upperBoundIndex)
			throws SnmpException;

	/**
	 * Batch operation.include add,update,delete
	 * 
	 * @throws SnmpException
	 */
	public abstract PDU snmpSet(String oid, byte[] buf) throws SnmpException;

	// v2
	public abstract Address getAddress();

	public abstract void setAddress(String ip, int port);

	public OctetString getCommunity() {
		return null;
	}

	public void setCommunity(String community) {
	}

	public abstract int getRetries();

	public abstract void setRetries(int retries);

	public abstract int getTimeout();

	public abstract void setTimeout(int timeout);

	// v3
	public OctetString getContextEngineID() {
		return null;
	}

	public void setContextEngineID(String contextEngineID) {

	}

	public OctetString getContextName() {
		return null;
	}

	public void setContextName(String contextName) {

	}

	public OctetString getSecurityName() {
		return null;
	}

	public void setSecurityName(String securityName) {

	}

	public OID getPrivProtocol() {
		return null;
	}

	public void setPrivProtocol(OID privProtocol) {

	}

	public OID getAuthProtocol() {
		return null;
	}

	public void setAuthProtocol(OID authProtocol) {

	}

	public OctetString getPrivPassphrase() {
		return null;
	}

	public void setPrivPassphrase(String privPassphrase) {

	}

	public OctetString getAuthPassphrase() {
		return null;
	}

	public void setAuthPassphrase(String authPassphrase) {

	}

}
