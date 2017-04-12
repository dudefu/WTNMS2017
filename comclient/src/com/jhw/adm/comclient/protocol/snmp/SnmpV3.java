package com.jhw.adm.comclient.protocol.snmp;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.snmp4j.PDU;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

public class SnmpV3 extends AbstractSnmp {
	private Request request;

	// public SnmpV3(String ip,int port,OctetString contextEngineID
	// ,OctetString contextName,OctetString securityName
	// ,OID privProtocol,OID authProtocol
	// ,OctetString privPassphrase,OctetString authPassphrase)
	// {
	// request=new Request();
	// request.setVersion(SnmpConstants.version3);
	// request.setAddress(new UdpAddress(ip+"/"+port));
	// if(contextEngineID!=null)
	// {
	// request.setContextEngineID(contextEngineID);
	// }
	// if(contextName!=null)
	// {
	// request.setContextName(contextName);
	// }
	// if(securityName!=null)
	// {
	// request.setSecurityName(securityName);
	// }
	// if(privPassphrase!=null)
	// {
	// request.setPrivPassphrase(privPassphrase);
	// }
	// if(authPassphrase!=null)
	// {
	// request.setAuthPassphrase(authPassphrase);
	// }
	// if(privProtocol!=null)
	// {
	// request.setPrivProtocol(privProtocol);
	// }
	// if(authProtocol!=null)
	// {
	// request.setAuthProtocol(authProtocol);
	// }
	// }
	//	
	// public SnmpV3(String ip,int port,OctetString contextEngineID
	// ,OID privProtocol,OID authProtocol
	// ,OctetString privPassphrase,OctetString authPassphrase)
	// {
	// request=new Request();
	// request.setVersion(SnmpConstants.version3);
	// request.setAddress(new UdpAddress(ip+"/"+port));
	// if(contextEngineID!=null)
	// {
	// request.setContextEngineID(contextEngineID);
	// }
	// if(privPassphrase!=null)
	// {
	// request.setPrivPassphrase(privPassphrase);
	// }
	// if(authPassphrase!=null)
	// {
	// request.setAuthPassphrase(authPassphrase);
	// }
	// if(privProtocol!=null)
	// {
	// request.setPrivProtocol(privProtocol);
	// }
	// if(authProtocol!=null)
	// {
	// request.setAuthProtocol(authProtocol);
	// }
	// }
	//	
	// public SnmpV3(String ip,OctetString contextEngineID
	// ,OID privProtocol,OID authProtocol
	// ,OctetString privPassphrase,OctetString authPassphrase)
	// {
	// request=new Request();
	// request.setVersion(SnmpConstants.version3);
	// request.setAddress(new UdpAddress(ip+"/"+Constants.SNMP_PORT));
	// if(contextEngineID!=null)
	// {
	// request.setContextEngineID(contextEngineID);
	// }
	// if(privPassphrase!=null)
	// {
	// request.setPrivPassphrase(privPassphrase);
	// }
	// if(authPassphrase!=null)
	// {
	// request.setAuthPassphrase(authPassphrase);
	// }
	// if(privProtocol!=null)
	// {
	// request.setPrivProtocol(privProtocol);
	// }
	// if(authProtocol!=null)
	// {
	// request.setAuthProtocol(authProtocol);
	// }
	// }

	public SnmpV3() {
		request = new Request();
		request.setVersion(SnmpConstants.version3);
	}

	public void instanceV3() throws SnmpException {
		request = new Request();
		request.setVersion(SnmpConstants.version3);
	}

	public void instanceV3(String ip, int port, OctetString contextEngineID,
			OctetString contextName, OctetString securityName,
			OID privProtocol, OID authProtocol, OctetString privPassphrase,
			OctetString authPassphrase) throws SnmpException {
		request = new Request();
		request.setVersion(SnmpConstants.version3);
		request.setAddress(new UdpAddress(ip + "/" + port));
		if (contextEngineID != null) {
			request.setContextEngineID(contextEngineID);
		}
		if (contextName != null) {
			request.setContextName(contextName);
		}
		if (securityName != null) {
			request.setSecurityName(securityName);
		}
		if (privPassphrase != null) {
			request.setPrivPassphrase(privPassphrase);
		}
		if (authPassphrase != null) {
			request.setAuthPassphrase(authPassphrase);
		}
		if (privProtocol != null) {
			request.setPrivProtocol(privProtocol);
		}
		if (authProtocol != null) {
			request.setAuthProtocol(authProtocol);
		}
	}

	public void instanceV3(String ip, int port, OctetString contextEngineID,
			OID privProtocol, OID authProtocol, OctetString privPassphrase,
			OctetString authPassphrase) throws SnmpException {
		request = new Request();
		request.setVersion(SnmpConstants.version3);
		request.setAddress(new UdpAddress(ip + "/" + port));
		if (contextEngineID != null) {
			request.setContextEngineID(contextEngineID);
		}
		if (privPassphrase != null) {
			request.setPrivPassphrase(privPassphrase);
		}
		if (authPassphrase != null) {
			request.setAuthPassphrase(authPassphrase);
		}
		if (privProtocol != null) {
			request.setPrivProtocol(privProtocol);
		}
		if (authProtocol != null) {
			request.setAuthProtocol(authProtocol);
		}
	}

	public void instanceV3(String ip, OctetString contextEngineID,
			OID privProtocol, OID authProtocol, OctetString privPassphrase,
			OctetString authPassphrase) throws SnmpException {
		request = new Request();
		request.setVersion(SnmpConstants.version3);
		request.setAddress(new UdpAddress(ip + "/" + Constants.SNMP_PORT));
		if (contextEngineID != null) {
			request.setContextEngineID(contextEngineID);
		}
		if (privPassphrase != null) {
			request.setPrivPassphrase(privPassphrase);
		}
		if (authPassphrase != null) {
			request.setAuthPassphrase(authPassphrase);
		}
		if (privProtocol != null) {
			request.setPrivProtocol(privProtocol);
		}
		if (authProtocol != null) {
			request.setAuthProtocol(authProtocol);
		}
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
		// vbs.clear();

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
		// vbs.clear();

		return response;
	}

	public PDU snmpGetBulk(Vector<String> vbs, int nonRepeaters,
			int maxRepetitions) throws SnmpException {
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
		// vbs.clear();

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
		// vbs.clear();

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
			vbs.add(new VariableBinding(new OID(oid), new OctetString(obj
					.toString())));
		} else if (obj instanceof Integer) {
			vbs.add(new VariableBinding(new OID(oid), new Integer32(Integer
					.parseInt(obj.toString()))));
		}
		// vbs.add(new VariableBinding(new OID(oid),value));
		request.setVbs(vbs);

		PDU response = request.send();

		vbs.clear();

		return response;
	}

	public PDU snmpSet(String oid, Object[] obj, int portNo)
			throws SnmpException {
		request.setPduType(PDU.SET);
		if (oid == null || obj == null) {
			throw new SnmpException("VBS or AbstractVariable is NULL");
		}
		Vector<VariableBinding> vbs = new Vector<VariableBinding>();

		for (int i = 0; i < obj.length; i++) {
			if (obj[i] instanceof String) {
				vbs.add(new VariableBinding(new OID(oid), new OctetString(obj
						.toString())));
			} else if (obj[i] instanceof Integer) {
				vbs.add(new VariableBinding(new OID(oid), new Integer32(Integer
						.parseInt(obj.toString()))));
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
				vbs.add(new VariableBinding(new OID(oid), new OctetString(entry
						.getValue().toString())));
			} else if (entry.getValue() instanceof Integer) {
				vbs.add(new VariableBinding(new OID(oid), new Integer32(Integer
						.parseInt(entry.getValue().toString()))));
			}
			// AbstractVariable variable=(AbstractVariable)entry.getValue();
			// vbs.add(new VariableBinding(new OID(oid),variable));
		}
		request.setVbs(vbs);

		PDU response = request.send();

		vbs.clear();
		// oids.clear();

		return response;
	}

	public PDU snmpSet(LinkedHashMap<String, Object> oids, String rowStatus)
			throws SnmpException {
		return null;
	}

	public Address getAddress() {
		return null;
	}

	public void setAddress(String ip, int port) {
		request.setAddress(new UdpAddress(ip + "/" + port));
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

	public OctetString getContextEngineID() {
		return null;
	}

	public void setContextEngineID(String contextEngineID) {
		request.setContextEngineID(new OctetString(contextEngineID));
	}

	public OctetString getContextName() {
		return null;
	}

	public void setContextName(String contextName) {
		request.setContextName(new OctetString(contextName));
	}

	public OctetString getSecurityName() {
		return null;
	}

	public void setSecurityName(String securityName) {
		request.setSecurityName(new OctetString(securityName));
	}

	public OID getPrivProtocol() {
		return null;
	}

	public void setPrivProtocol(OID privProtocol) {
		request.setPrivProtocol(privProtocol);
	}

	public OID getAuthProtocol() {
		return null;
	}

	public void setAuthProtocol(OID authProtocol) {
		request.setAuthProtocol(authProtocol);
	}

	public OctetString getPrivPassphrase() {
		return null;
	}

	public void setPrivPassphrase(String privPassphrase) {
		request.setPrivPassphrase(new OctetString(privPassphrase));
	}

	public OctetString getAuthPassphrase() {
		return null;
	}

	public void setAuthPassphrase(String authPassphrase) {
		request.setAuthPassphrase(new OctetString(authPassphrase));
	}

	public static void main(String[] args) {

	}

	@Override
	public PDU snmpTableAddRow(Map<String, Object> oids,
			String rowStatusColumnOID, String rowIndexOID) throws SnmpException {
		return null;
	}

	@Override
	public PDU snmpTableDeleteRow(String rowStatusColumnOID, String rowIndexOID)
			throws SnmpException {
		return null;
	}

	@Override
	public List<TableEvent> snmpTableDisplay(String[] columnOIDs,
			String lowerBoundIndex, String upperBoundIndex)
			throws SnmpException {
		return null;
	}
}
