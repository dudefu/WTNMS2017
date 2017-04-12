package com.jhw.adm.comclient.protocol.snmp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.DataBufferBuilder;

/**
 * 
 * @author xiongbo
 * 
 */

public class SnmpV2 extends AbstractSnmp {
	public final Logger log = Logger.getLogger(this.getClass().getName());

	private Request request;

	public SnmpV2() {
		request = new Request();
		request.setVersion(SnmpConstants.version2c);
	}

	public void instanceV2() throws SnmpException {
		request = new Request();
		request.setVersion(SnmpConstants.version2c);
	}

	public void instanceV2(String ip, int port, String community) throws SnmpException {
		request = new Request();
		request.setVersion(SnmpConstants.version2c);
		request.setAddress(new UdpAddress(ip + "/" + port));
		if (community != null && !"".equalsIgnoreCase(community.trim())) {
			request.setCommunity(new OctetString(community));
		}
	}

	public void instanceV2(String ip, int port) throws SnmpException {
		request = new Request();
		request.setVersion(SnmpConstants.version2c);
		request.setAddress(new UdpAddress(ip + "/" + port));
	}

	public void instanceV2(String ip) throws SnmpException {
		request = new Request();
		request.setVersion(SnmpConstants.version2c);
		request.setAddress(new UdpAddress(ip + "/" + Constants.SNMP_PORT));
	}

	public PDU snmpGet(String oid) throws SnmpException {
		request.setPduType(PDU.GET);
		if (oid == null) {
			throw new SnmpException("VBS is NULL");
		}
		Vector<VariableBinding> vbs = new Vector<VariableBinding>();
		vbs.add(new VariableBinding(new OID(oid)));
		request.setVbs(vbs);

		PDU response = request.send();

		vbs.clear();

		return response;
	}

	public PDU snmpGet(Vector<String> vbs) throws SnmpException {
		request.setPduType(PDU.GET);
		if (vbs == null) {
			throw new SnmpException("VBS is NULL");
		}
		Vector<VariableBinding> vbs2 = new Vector<VariableBinding>();
		for (String oid : vbs) {
			vbs2.add(new VariableBinding(new OID(oid)));
		}
		request.setVbs(vbs2);

		PDU response = request.send();

		vbs2.clear();

		return response;
	}

	public PDU snmpGetNext(Vector<String> vbs) throws SnmpException {
		request.setPduType(PDU.GETNEXT);
		if (vbs == null) {
			throw new SnmpException("VBS is NULL");
		}
		Vector<VariableBinding> vbs2 = new Vector<VariableBinding>();
		for (String oid : vbs) {
			vbs2.add(new VariableBinding(new OID(oid)));
		}
		request.setVbs(vbs2);

		PDU response = request.send();

		vbs2.clear();

		return response;
	}

	public PDU snmpGetBulk(Vector<String> vbs, int nonRepeaters, int maxRepetitions) throws SnmpException {
		request.setPduType(PDU.GETBULK);
		if (vbs == null) {
			throw new SnmpException("VBS is NULL");
		}
		Vector<VariableBinding> vbs2 = new Vector<VariableBinding>();
		for (String oid : vbs) {
			vbs2.add(new VariableBinding(new OID(oid)));
		}
		request.setVbs(vbs2);
		if (nonRepeaters != 0) {
			request.setNonRepeaters(nonRepeaters);
		}
		if (maxRepetitions != 0) {
			request.setMaxRepetitions(maxRepetitions);
		}

		PDU response = request.send();

		vbs2.clear();

		return response;
	}

	public PDU snmpGetBulk(Vector<String> vbs) throws SnmpException {
		request.setPduType(PDU.GETBULK);
		if (vbs == null) {
			throw new SnmpException("VBS is NULL");
		}
		Vector<VariableBinding> vbs2 = new Vector<VariableBinding>();
		for (String oid : vbs) {
			vbs2.add(new VariableBinding(new OID(oid)));
		}
		request.setVbs(vbs2);

		PDU response = request.send();

		vbs2.clear();

		return response;
	}

	/**
	 * Batch operation.Include add,update,delete
	 * 
	 * @throws SnmpException
	 */
	public PDU snmpSet(String oid, byte[] buf) throws SnmpException {
		request.setPduType(PDU.SET);
		if (oid == null || buf == null) {
			throw new SnmpException("VBS or Buf is NULL");
		}
		Vector<VariableBinding> vbs = new Vector<VariableBinding>();
		vbs.add(new VariableBinding(new OID(oid), new OctetString(buf)));
		request.setVbs(vbs);

		PDU response = request.send();

		vbs.clear();

		return response;
	}

	public PDU snmpSet(String oid, Object obj) throws SnmpException {
		request.setPduType(PDU.SET);
		if (oid == null || obj == null) {
			throw new SnmpException("VBS or AbstractVariable is NULL");
		}
		Vector<VariableBinding> vbs = new Vector<VariableBinding>();

		if (obj instanceof String) {
			vbs.add(new VariableBinding(new OID(oid), new OctetString(obj.toString())));
		} else if (obj instanceof Integer) {
			vbs.add(new VariableBinding(new OID(oid), new Integer32(Integer.parseInt(obj.toString()))));
		}

		request.setVbs(vbs);

		PDU response = request.send();

		vbs.clear();

		return response;
	}

	public PDU snmpSet(String oid, Object[] obj, int portNo) throws SnmpException {
		request.setPduType(PDU.SET);
		if (oid == null || obj == null) {
			throw new SnmpException("VBS or AbstractVariable is NULL");
		}
		Vector<VariableBinding> vbs = new Vector<VariableBinding>();
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] instanceof String) {
				vbs.add(new VariableBinding(new OID(oid + "." + (i + 2) + "." + portNo),
						new OctetString(obj[i].toString())));
			} else if (obj[i] instanceof Integer) {
				vbs.add(new VariableBinding(new OID(oid + "." + (i + 2) + "." + portNo),
						new Integer32(Integer.parseInt(obj[i].toString()))));
			}
		}

		request.setVbs(vbs);

		PDU response = request.send();

		vbs.clear();

		return response;
	}

	public PDU snmpSet(Map<String, Object> oids) throws SnmpException {
		request.setPduType(PDU.SET);
		if (oids == null) {
			throw new SnmpException("VBS is NULL");
		}
		Vector<VariableBinding> vbs = new Vector<VariableBinding>();
		Set set = oids.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String oid = entry.getKey().toString();
			if (entry.getValue() instanceof String) {
				vbs.add(new VariableBinding(new OID(oid), new OctetString(entry.getValue().toString())));
			} else if (entry.getValue() instanceof Integer) {
				vbs.add(new VariableBinding(new OID(oid),
						new Integer32(Integer.parseInt(entry.getValue().toString()))));
			}

		}
		request.setVbs(vbs);

		PDU response = request.send();

		vbs.clear();

		return response;
	}

	/**
	 * Special For LLID
	 */
	public PDU snmpSet(LinkedHashMap<String, Object> oids, String rowStatus) throws SnmpException {
		request.setPduType(PDU.SET);
		if (oids == null) {
			throw new SnmpException("VBS is NULL");
		}
		Vector<VariableBinding> vbs = new Vector<VariableBinding>();
		Set set = oids.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String oid = entry.getKey().toString();
			if (entry.getValue() instanceof String) {
				vbs.add(new VariableBinding(new OID(oid), new OctetString(entry.getValue().toString())));
			} else if (entry.getValue() instanceof Integer) {
				vbs.add(new VariableBinding(new OID(oid),
						new Integer32(Integer.parseInt(entry.getValue().toString()))));
			}

		}

		vbs.add(0, new VariableBinding(new OID(rowStatus), new Integer32(2)));
		vbs.add(vbs.size(), new VariableBinding(new OID(rowStatus), new Integer32(1)));

		request.setVbs(vbs);

		PDU response = request.send();

		vbs.clear();

		return response;
	}

	/**
	 * Table add new row
	 */
	public PDU snmpTableAddRow(Map<String, Object> oids, String rowStatusColumnOID, String rowIndexOID)
			throws SnmpException {
		request.setPduType(PDU.SET);
		if (oids == null) {
			log.error("variableBindings is NULL");
			return null;
		}
		VariableBinding[] variableBindings = new VariableBinding[oids.size()];
		Set set = oids.entrySet();
		Iterator it = set.iterator();
		int i = 0;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String oid = entry.getKey().toString();
			if (entry.getValue() instanceof String) {
				variableBindings[i] = new VariableBinding(new OID(oid), new OctetString(entry.getValue().toString()));
			} else if (entry.getValue() instanceof Integer) {
				variableBindings[i] = new VariableBinding(new OID(oid),
						new Integer32(Integer.parseInt(entry.getValue().toString())));
			}
			i++;
		}
		request.setVariableBindings(variableBindings);
		request.setRowStatusColumnOID(new OID(rowStatusColumnOID));
		request.setRowIndexOID(new OID(rowIndexOID));

		PDU response = request.tableAddRow();

		return response;
	}

	/**
	 * Table delete row
	 */
	public PDU snmpTableDeleteRow(String rowStatusColumnOID, String rowIndexOID) throws SnmpException {
		request.setPduType(PDU.SET);

		request.setRowStatusColumnOID(new OID(rowStatusColumnOID));
		request.setRowIndexOID(new OID(rowIndexOID));

		PDU response = request.tableDeleteRow();

		return response;
	}

	public List<TableEvent> snmpTableDisplay(String[] columnOIDs, String lowerBoundIndex, String upperBoundIndex)
			throws SnmpException {
		// request.setPduType(PDU.GET);

		// request.setRowStatusColumnOID(new OID(rowStatusColumnOID));
		// request.setRowIndexOID(new OID(rowIndexOID));
		OID[] oids = new OID[columnOIDs.length];
		for (int i = 0; i < columnOIDs.length; i++) {
			oids[i] = new OID(columnOIDs[i]);
		}
		request.setColumnOIDs(oids);
		if (lowerBoundIndex != null) {
			request.setLowerBoundIndex(new OID(lowerBoundIndex));
		}
		if (upperBoundIndex != null) {
			request.setUpperBoundIndex(new OID(upperBoundIndex));
		}

		List<TableEvent> list = request.getTable();

		return list;
	}

	public Address getAddress() {
		return null;
	}

	public void setAddress(String ip, int port) {
		request.setAddress(new UdpAddress(ip + "/" + port));
	}

	public OctetString getCommunity() {
		return null;
	}

	public void setCommunity(String community) {
		request.setCommunity(new OctetString(community));
	}

	public int getRetries() {
		return 0;
	}

	public void setRetries(int retries) {
		request.setRetries(retries);
	}

	public int getTimeout() {
		return 0;
	}

	public void setTimeout(int timeout) {
		request.setTimeout(timeout);
	}

	public static void main(String[] args) {

		// snmp.instanceV2("192.168.13.148", Constants.SNMP_PORT, "private");

		// String portValue = "UTTTTTTT";
		// byte[] temp = new DataBufferBuilder().vlanCreate(500, portValue);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.20.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// Vector<String> vbs = new Vector<String>();
		// vbs.add("1.3.6.1.4.1.16001.2.20.3");
		// // snmp.snmpGetBulk(vbs, 0, 2);
		// // snmp.snmpGet("1.3.6.1.4.1.16001.2.20.3");

		// String portValue = "UTTTTTTT";
		// byte[] temp = new DataBufferBuilder().vlanUpdate(2, portValue);
		//
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.20.3", temp);
		//
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().portConfig("7", "disable",
		// "10_half", "enable", "untag");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.11.1.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// System.out.println(response.getErrorStatusText());
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }
		//
		// String a = "100fdx".substring(0, "100fdx".length() - 3);
		// System.out.println(a);

		// byte[] temp = new DataBufferBuilder().vlanPortConfig(2, 1, 1);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.11.1.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// System.out.println(response.getErrorStatusText());
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().stpPortConfig("1", "124",
		// "176",
		// "disable", "disable", "true");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.16.1.8", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// PDU response = snmp.snmpGet("1.3.6.1.4.1.16001.2.4.1");
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().agingTimeConfig(301);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.8.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().unicastCreate(
		// "00-23-AE-AE-CB-4E", 5, 1);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.8.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().unicastDelete(
		// "00-23-AE-AA-2A-4E", 0, 1);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.8.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] port = new byte[8];
		// for (int i = 0; i < 8; i++) {
		// port[i] = 1;
		// }
		// // port[0] = 1;
		// // port[5] = 0;
		// byte[] temp = new DataBufferBuilder().multicastCreate(
		// "01-00-5e-AA-2A-4C", port, 1);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.10.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] port = new byte[8];
		// byte[] temp = new DataBufferBuilder().multicastDelete(
		// "01-00-5E-AA-2A-4E", port, 0);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.10.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// Vector<String> vbs = new Vector<String>();
		// vbs.add("1.3.6.1.4.1.3320.101.9.1.1.9");
		// PDU response = snmp.snmpGetBulk(vbs, 0, 2);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder()
		// .dot1xPortConfig(7, "unauthorized");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.1.5", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// System.out.println("axswedcfrvbgt".substring(5, 6));
		// }
		// }

		// byte[] t = new byte[8];
		// t[0] = 1;
		// t[1] = 0;
		// t[2] = 0;
		// t[3] = 0;
		// t[4] = 0;
		// t[5] = 1;
		// t[6] = 1;
		// t[7] = 0;
		// byte[] temp = new DataBufferBuilder().igmpRouterConfig(t, 0);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.3.5", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().igmpSnoopingConfig(1, 1);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.3.5", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().igmpQuerierConfig(1, 0);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.3.5", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] t = new byte[8];
		// t[0] = 1;
		// t[1] = 0;
		// t[2] = 0;
		// t[3] = 0;
		// t[4] = 0;
		// t[5] = 0;
		// t[6] = 0;
		// t[7] = 0;
		// int[] k = new int[8];
		// k[0] = 5;
		// k[1] = 5;
		// k[2] = 5;
		// k[3] = 5;
		// k[4] = 5;
		// k[5] = 5;
		// k[6] = 5;
		// k[7] = 5;
		//
		// byte[] temp = new DataBufferBuilder().lacpKeyConfig(t, k);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.5.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] t = new byte[8];
		// t[0] = 1;
		// t[1] = 1;
		// t[2] = 0;
		// t[3] = 0;
		// t[4] = 0;
		// t[5] = 0;
		// t[6] = 0;
		// t[7] = 0;
		// int[] k = new int[8];
		// k[0] = 1;
		// k[1] = 1;
		// k[2] = 1;
		// k[3] = 1;
		// k[4] = 1;
		// k[5] = 1;
		// k[6] = 1;
		// k[7] = 1;
		//
		// byte[] temp = new DataBufferBuilder().lacpModeConfig(t, k);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.5.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] t = new byte[8];
		// t[0] = 1;
		// t[1] = 1;
		// t[2] = 0;
		// t[3] = 0;
		// t[4] = 0;
		// t[5] = 0;
		// t[6] = 0;
		// t[7] = 0;
		// String[] k = new String[8];
		// k[0] = "passive";
		// k[1] = "passive";
		// k[2] = "passive";
		// k[3] = "passive";
		// k[4] = "passive";
		// k[5] = "passive";
		// k[6] = "passive";
		// k[7] = "passive";
		//
		// byte[] temp = new DataBufferBuilder().lacpRoleConfig(t, k);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.5.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().trafficControlConfig(1, 1,
		// "100k", 0, "100k");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.18.3 ", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().stormControlConfig(1, "20%",
		// "dlf");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.15.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] t = new byte[8];
		// t[0] = 1;
		// t[1] = 1;
		// t[2] = 0;
		// t[3] = 0;
		// t[4] = 0;
		// t[5] = 0;
		// t[6] = 0;
		// t[7] = 0;
		// byte[] temp = new DataBufferBuilder().trunkConfig(1, t);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.6.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] t = new byte[8];
		// t[0] = 1;
		// t[1] = 1;
		// t[2] = 0;
		// t[3] = 0;
		// t[4] = 0;
		// t[5] = 0;
		// t[6] = 0;
		// t[7] = 0;
		// byte[] temp = new DataBufferBuilder().trunkConfigDelete(1, t);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.6.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] t = new byte[8];
		// t[0] = 1;
		// t[1] = 1;
		// t[2] = 0;
		// t[3] = 0;
		// t[4] = 0;
		// t[5] = 0;
		// t[6] = 0;
		// t[7] = 0;
		// byte[] stateMember = new byte[8];
		// stateMember[0] = 1;
		// stateMember[1] = 2;
		// byte[] temp = new DataBufferBuilder().lldpConfig(t, stateMember);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.7.1.6", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] portMember = new byte[2];
		// portMember[0] = 1;
		// portMember[1] = 2;
		// byte[] temp = new DataBufferBuilder().ghringRing(1, portMember);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.2.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().ghringType(40, "assistant");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.2.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// temp = new DataBufferBuilder().ghringPortRole(1, "edgeport");
		// response = snmp.snmpSet("1.3.6.1.4.1.16001.2.2.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }
		//
		// temp = new DataBufferBuilder().ghringPortRole(2, "none");
		// response = snmp.snmpSet("1.3.6.1.4.1.16001.2.2.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().ghringDel(1);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.2.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().ghringLinkAdd(1, 1,
		// "mainlink");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.2.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().ghringLinkDel(1, 1, "");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.2.4", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().qosPriorityCfg(1, 1, 3);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.12.7", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().rmonStaticCfg(8, 8);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.21.5", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().rmonAlarmAdd(1, 8, 10,
		// "1.3.6.1.4.1.16001.3.1.0.3", "delta", 3000, 1000, 1, 1);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.21.5", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().rmonEventAdd(2, "xiongbo",
		// "trap", "public");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.21.5", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().rmonStaticDelete(3);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.21.5", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().rmonEventDelete(6);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.21.5", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().serialConfig_string(1, 4,
		// "udpclient");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.13.1.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().serialConfig_int(1, 4, 3);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.13.1.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new DataBufferBuilder().serialConfig_client(1, 9,
		// "192.168.13.148", 35);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.13.1.3", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// byte[] temp = new
		// DataBufferBuilder().snmpHostConfig("192.168.13.185",
		// "v1", "public");
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.22.7", temp);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// ***
		// Map<String, Object> oids = new HashMap<String, Object>();
		// oids.put("1.3.6.1.2.1.17.7.1.4.3.1.1", "Vlan1");
		// // oids.put("1.3.6.1.2.1.17.7.1.4.3.1.2", "30:00");
		// PDU response = snmp.snmpSet(oids, "1.3.6.1.2.1.17.7.1.4.3.1.5", "2");
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }
		// ***

		// PDU response = snmp.snmpSet("1.3.6.1.2.1.17.7.1.4.3.1.5", "2");
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }
		// ***

		// String[] columnOIDs = { "1.3.6.1.2.1.17.2.15.1.1",
		// "1.3.6.1.2.1.17.2.15.1.3", "1.3.6.1.2.1.17.2.15.1.6",
		// "1.3.6.1.2.1.17.2.15.1.8", "1.3.6.1.2.1.17.2.15.1.9" };
		// String[] columnOIDs = { "1.3.6.1.2.1.17.4.3.1.1",
		// "1.3.6.1.2.1.17.4.3.1.2", "1.3.6.1.2.1.17.4.3.1.3" };

		// String[] columnOIDs = { "1.3.6.1.2.1.17.1.4.1.1",
		// "1.3.6.1.2.1.17.1.4.1.2" };

		// getSingleNode();
		getBulk();
		// getTable();

		// /snmpSet();
		// getNext();
		// setByte();

		// ***

		// Map<String, Object> variableBindings = new HashMap<String, Object>();
		// variableBindings.put("1.3.6.1.4.1.3320.101.9.1.1.9.7", 1001);
		// PDU response = snmp.snmpSet(variableBindings);
		// if (response != null) {
		// Vector<VariableBinding> recVBs = response.getVariableBindings();
		// for (int i = 0; i < recVBs.size(); i++) {
		// VariableBinding recVB = recVBs.elementAt(i);
		// System.out.println("" + response.getErrorStatusText()
		// + " Reviced:" + recVB.getOid() + " : "
		// + recVB.getVariable());
		// }
		// }

		// getSingleNode();

		// asynchronousSnmp();
		// snmpSet2();
	}

	private static void setByte() {
		SnmpV2 snmp = new SnmpV2();
		snmp.instanceV2("192.168.1.100", Constants.SNMP_PORT, "private");

		// byte[] temp = new DataBufferBuilder().syslogServerConfig(2, 1,
		// "192.168.65.185", 514);
		// PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.17.3", temp);

		byte[] temp = new DataBufferBuilder().saveConfig(1);
		PDU response = snmp.snmpSet("1.3.6.1.4.1.16001.2.23.4", temp);

		if (response != null) {
			Vector<VariableBinding> recVBs = response.getVariableBindings();
			for (int i = 0; i < recVBs.size(); i++) {
				VariableBinding recVB = recVBs.elementAt(i);
				System.out.println("Reviced:" + response.getErrorStatusText() + " : " + recVB.getOid() + " : "
						+ recVB.getVariable());
			}
		}
	}

	private static void getSingleNode() {
		SnmpV2 snmp = new SnmpV2();
		snmp.instanceV2("192.168.1.100", Constants.SNMP_PORT, "public");
		snmp.setTimeout(1000 * 8);

		Vector<String> oids = new Vector<String>();
		oids.add("1.3.6.1.2.1.1.1");
		oids.add("1.3.6.1.2.1.1.2");

		PDU response = snmp.snmpGet(oids);
		if (response != null) {
			Vector<VariableBinding> recVBs = response.getVariableBindings();
			System.out.println(response.getErrorStatusText());
			for (int i = 0; i < recVBs.size(); i++) {
				VariableBinding recVB = recVBs.elementAt(i);
				System.out.println("Reviced:" + recVB.getOid() + " : " + recVB.getVariable().toString());
				// String descr = recVB.getVariable().toString();
				// System.out.println(descr.indexOf("\n"));
				// String[] desc = descr.split("\n");
				// if (desc != null) {
				// String[] temp = desc[1].split(" ");
				// System.out.println(temp[0]);
				// // if (temp != null) {
				// // base.setName(temp[0]);
				// // }
				// }
			}
		}
	}

	private static void getBulk() {
		SnmpV2 snmp = new SnmpV2();
		snmp.instanceV2("192.168.1.100", Constants.SNMP_PORT, "public");
		snmp.setTimeout(1000 * 8);

		Vector<String> oids = new Vector<String>();
		oids.add("1.3.6.1.2.1.1.1.0");
		// oids.add("1.3.6.1.2.1.1.2.0");
		// oids.add("1.3.6.1.2.1.1.3.0");

		PDU response = snmp.snmpGetBulk(oids, 0, 1);
		if (response != null) {
			Vector<VariableBinding> recVBs = response.getVariableBindings();
			System.out.println(response.getErrorStatusText());
			for (int i = 0; i < recVBs.size(); i++) {
				VariableBinding recVB = recVBs.elementAt(i);
				System.out.println("Reviced:" + recVB.getOid() + " : " + recVB.getVariable().toString());
				// String descr = recVB.getVariable().toString();
				// System.out.println(descr.indexOf("\n"));
				// String[] desc = descr.split("\n");
				// if (desc != null) {
				// String[] temp = desc[1].split(" ");
				// System.out.println(temp[0]);
				// }
			}
		}
	}

	private static void getNext() {
		SnmpV2 snmp = new SnmpV2();
		snmp.instanceV2("192.168.1.100", Constants.SNMP_PORT, "public");
		snmp.setTimeout(1000 * 8);

		Vector<String> oids = new Vector<String>();
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.1");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.2");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.3");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.4");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.4");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.4");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.4");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.4");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.4");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.4");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.4");
		// oids.add("1.3.6.1.4.1.16001.2.20.2.1.0");
		for (int j = 0; j < 8; j++) {
			oids.clear();
			for (int i = 1; i <= 9; i++) {
				oids.add("1.3.6.1.4.1.16001.2.20.2.1." + i + "." + j);
			}

			// PDU response = snmp.snmpGetBulk(oids, 0, 9);
			PDU response = snmp.snmpGetNext(oids);
			if (response != null) {
				Vector<VariableBinding> recVBs = response.getVariableBindings();
				System.out.println(response.getErrorStatusText());
				for (int i = 0; i < recVBs.size(); i++) {
					VariableBinding recVB = recVBs.elementAt(i);
					System.out.println("Reviced:" + recVB.getOid() + " : " + recVB.getVariable().toString());
					// String descr = recVB.getVariable().toString();
					// System.out.println(descr.indexOf("\n"));
					// String[] desc = descr.split("\n");
					// if (desc != null) {
					// String[] temp = desc[1].split(" ");
					// System.out.println(temp[0]);
					// }
				}
			}
		}
	}

	private static void getTable() {
		SnmpV2 snmp = new SnmpV2();
		snmp.instanceV2("192.168.1.100", Constants.SNMP_PORT, "public");
		snmp.setTimeout(1000 * 8);

		// String[] columnOIDs = { "1.3.6.1.4.1.16001.1.33424.0.127.1.3.8.1.2",
		// "1.3.6.1.4.1.16001.1.33424.0.127.1.3.8.1.3",
		// "1.3.6.1.4.1.16001.1.33424.0.127.1.3.8.1.5" };

		// String[] columnOIDs = { "1.3.6.1.4.1.16001.1.33424.0.127.1.4.2.1.2",
		// "1.3.6.1.4.1.16001.1.33424.0.127.1.4.1.1.2",
		// "1.3.6.1.4.1.16001.1.33424.0.127.1.4.1.1.7" };

		// String[] columnOIDs = { "1.3.6.1.2.1.17.7.1.4.3.1.1",
		// "1.3.6.1.2.1.17.7.1.4.3.1.2", "1.3.6.1.2.1.17.7.1.4.3.1.3" };

		// String[] columnOIDs = { "1.3.6.1.4.1.16001.1.33424.0.127.1.3.7.1.2",
		// "1.3.6.1.4.1.16001.1.33424.0.127.1.3.7.1.3",
		// "1.3.6.1.4.1.16001.1.33424.0.127.1.3.7.1.4" };

		// String[] columnOIDs = { "1.3.6.1.4.1.16001.127.1.4.1.1.2",
		// "1.3.6.1.4.1.16001.127.1.4.1.1.7",
		// "1.3.6.1.4.1.16001.127.1.4.2.1.2" };

		String[] columnOIDs = { "1.3.6.1.2.1.2.2.1.3", "1.3.6.1.2.1.2.2.1.2", "1.3.6.1.2.1.2.2.1.7",
				"1.3.6.1.2.1.2.2.1.8", "1.3.6.1.2.1.2.2.1.6", "1.3.6.1.2.1.31.1.1.1.15"
				// ,
				// "1.3.6.1.2.1.17.7.1.2.2.1.2", "1.3.6.1.2.1.17.7.1.2.2.1.3"
				// "1.3.6.1.4.1.3320.127.1.4.2.1.2",
				// "1.3.6.1.4.1.3320.127.1.4.2.1.2",
				// "1.3.6.1.4.1.16001.2.7.2.2.1.5"
				// ,
				// "1.3.6.1.4.1.16001.2.7.2.2.1.6"
				// ,
				// "1.3.6.1.4.1.16001.2.7.2.2.1.7",
				// "1.3.6.1.4.1.16001.2.7.2.2.1.8"
		};

		// String[] columnOIDs = { "1.3.6.1.4.1.16001.1.33424.0.127.1.3.7.1.1",
		// "1.3.6.1.4.1.16001.1.33424.0.127.1.3.7.1.3",
		// "1.3.6.1.4.1.16001.1.33424.0.127.1.3.7.1.4" };

		List<TableEvent> list = snmp.snmpTableDisplay(columnOIDs, null, null);
		if (list != null) {
			for (TableEvent tableEvent : list) {
				for (VariableBinding variableBinding : tableEvent.getColumns()) {
					System.out.print(variableBinding.getOid() + " : " + variableBinding.getVariable()// +
																										// "
																										// :
																										// "
					// + tableEvent.getReportPDU() + ":"
					// + tableEvent.getErrorMessage()
					);
					System.out.println();
					// + tableEvent.getReportPDU().getErrorStatus()
					// + " : "
					// +
					// tableEvent.getReportPDU().getErrorStatusText());
					// }
					// VariableBinding[] variableBinding =
					// tableEvent.getColumns();
					// for (int i = 0; i < variableBinding.length; i++) {
					// if (i % columnOIDs.length == 0) {
					// System.out.print(variableBinding[0].getVariable());
					// System.out.print(" * ");
					// System.out.print(variableBinding[1].getVariable());
					// System.out.print(" * ");
					// System.out.print(variableBinding[2].getVariable());
					// System.out.print(" * ");
					// System.out.print(variableBinding[3].getVariable());
					// System.out.print(" * ");
					// System.out.print(variableBinding[4].getVariable());
					// System.out.print(" * ");
					// System.out.print(variableBinding[5].getVariable());
					// System.out.print(" * ");
					// System.out.print(variableBinding[6].getVariable());
					// System.out.print(" * ");
					// System.out.print(variableBinding[7].getVariable());
					// System.out.print(" * ");
					// System.out.print(variableBinding[i +
					// 3].getVariable());
					// System.out.print(" * ");
					// System.out.print(variableBinding[i +
					// 4].getVariable());

					// System.out.print(" * ");
					// System.out.print(variableBinding[i +
					// 2].getVariable());

					// System.out.print(" ");
					// System.out.print(variableBinding[i +
					// 3].getVariable());
					// System.out.print(" ");
					// System.out.print(variableBinding[i +
					// 4].getVariable());

					// System.out.print(" ");
					// System.out.print(variableBinding[i +
					// 5].getVariable());
				}
				// }
				System.out.println();
			}
		}
	}

	private static void snmpSet() {

		Snmp snmp = null;
		try {
			snmp = new Snmp(new DefaultUdpTransportMapping());
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString("public"));
			target.setAddress(new UdpAddress("192.168.1.100/161"));
			target.setTimeout(1000);
			target.setVersion(SnmpConstants.version2c);
			// creating PDU
			PDU pdu = new PDU();

			pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 4, 1, 16001, 2, 9, 2 }),
					new OctetString("1 3 7")));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 2, 2, 0 }), new Integer32(4096)));
			//
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 2, 12, 0 }), new Integer32(20)));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 2, 13, 0 }), new Integer32(7)));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 2, 14, 0 }), new Integer32(18)));

			// OctetString

			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 7, 1, 4, 5, 1, 3, 1 }), new Integer32(1)));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 7, 1, 4, 5, 1, 4, 1 }), new Integer32(1)));

			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 7, 1, 4, 5, 1, 1, 5 }), new Integer32(1)));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 7, 1, 4, 5, 1, 1, 6 }), new Integer32(1)));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 7, 1, 4, 5, 1, 1, 7 }), new Integer32(3)));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 7, 1, 4, 5, 1, 1, 8 }), new Integer32(1)));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 7, 1, 4, 5, 1, 1, 9 }), new Integer32(1)));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 7, 1, 4, 5, 1, 1, 10 }), new Integer32(1)));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 7, 1, 4, 5, 1, 1, 11 }), new Integer32(1)));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 17, 7, 1, 4, 5, 1, 1, 12 }), new Integer32(3)));

			pdu.setType(PDU.SET);
			snmp.listen();
			// sending request
			ResponseListener listener = new ResponseListener() {
				public void onResponse(ResponseEvent event) {
					((Snmp) event.getSource()).cancel(event.getRequest(), this);
					System.out.println("Received:" + event.getPeerAddress() + " " + event.getResponse());
				}
			};

			snmp.sendPDU(pdu, target, null, listener);

			System.out.println("Send Complete");

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			snmp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void snmpSet2() {

		Snmp snmp = null;
		try {
			snmp = new Snmp(new DefaultUdpTransportMapping());
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString("public"));
			target.setAddress(new UdpAddress("192.168.1.100/161"));
			target.setTimeout(1000);
			target.setVersion(SnmpConstants.version2c);
			// creating PDU
			PDU pdu = new PDU();
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 4, 1,
			// 3320, 101, 1, 22, 0 }), new Integer32(1)));

			pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 4, 1, 3320, 101, 9, 1, 1, 11, 7 }),
					new Integer32(2)));
			pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 4, 1, 3320, 101, 9, 1, 1, 8, 7 }),
					new Integer32(100000)));
			pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 4, 1, 3320, 101, 9, 1, 1, 9, 7 }),
					new Integer32(1000)));
			pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 4, 1, 3320, 101, 9, 1, 1, 11, 7 }),
					new Integer32(1)));

			pdu.setType(PDU.SET);
			snmp.listen();
			// sending request
			ResponseListener listener = new ResponseListener() {
				public void onResponse(ResponseEvent event) {
					((Snmp) event.getSource()).cancel(event.getRequest(), this);
					System.out.println("Received:" + event.getPeerAddress() + " " + event.getResponse());
				}
			};

			snmp.sendPDU(pdu, target, null, listener);

			System.out.println("Send Complete");

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			snmp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void asynchronousSnmp() {
		Snmp snmp = null;
		final List<ResponseEvent> list = new ArrayList<ResponseEvent>();
		try {
			snmp = new Snmp(new DefaultUdpTransportMapping());
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString("public"));
			target.setTimeout(2000);
			target.setVersion(SnmpConstants.version2c);
			// creating PDU
			// PDU pdu = new PDU();
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 1, 2, 0 })));
			// pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1,
			// 4, 1, 0 })));
			// pdu.setType(PDU.GET);
			snmp.listen();
			// sending request
			ResponseListener listener = new ResponseListener() {
				public void onResponse(ResponseEvent event) {
					((Snmp) event.getSource()).cancel(event.getRequest(), this);
					System.out.println("Received:" + event.getPeerAddress() + " " + event.getResponse());
					if (event.getPeerAddress() != null) {
						list.add(event);
					}
				}
			};

			for (int i = 1; i < 255; i++) {
				target.setAddress(new UdpAddress("192.168.1." + i + "/161"));
				// creating PDU
				PDU pdu = new PDU();
				pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1, 1, 2, 0 })));
				pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1, 4, 1, 0 })));
				pdu.setType(PDU.GET);
				snmp.sendPDU(pdu, target, null, listener);
			}
			System.out.println("Send Complete");

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			snmp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(list.size());
	}

	public static String convertHexToBinary3(String hexString) {
		long l = Long.parseLong(hexString, 16);
		String binaryString = Long.toBinaryString(l);
		int shouldBinaryLen = hexString.length() * 4;
		StringBuffer addZero = new StringBuffer();
		int addZeroNum = shouldBinaryLen - binaryString.length();
		for (int i = 1; i <= addZeroNum; i++) {
			addZero.append("0");
		}
		return addZero.toString() + binaryString;

	}

	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	private static String getStandardMac(String mac) {
		String[] macs = mac.trim().split(" ");
		mac = "";
		for (String m : macs) {
			mac += "." + Integer.parseInt(m, 16);
		}
		return mac;
	}

	private static String test() {
		String a = null;

		try {
			a.length();
		} catch (RuntimeException e) {
			System.out.println(getTrace(e));
			System.out.println(getTrace2(e));
		}
		return null;
	}

	public static String getTrace(Exception e) {
		StackTraceElement[] ste = e.getStackTrace();
		StringBuffer sb = new StringBuffer();
		sb.append(e + "\n");
		for (int i = 0; i < ste.length; i++) {
			sb.append(ste[i].toString() + "\n");
		}
		return sb.toString();
	}

	public static String getTrace2(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		e.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}
}
