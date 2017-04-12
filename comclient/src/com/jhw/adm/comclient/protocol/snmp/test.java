package com.jhw.adm.comclient.protocol.snmp;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

/**
 * 
 * @author xiongbo
 * 
 * 
 *         The Class handle Get,GetNext,GetBulk and Set operation The Class is
 *         Base Communication.
 * 
 */

public class test implements PDUFactory {

	public final Logger log = Logger.getLogger(this.getClass().getName());
	private Target target;
	private Address address;
	private OID authProtocol;
	private OID privProtocol;
	private OctetString privPassphrase;
	private OctetString authPassphrase;
	private OctetString community = new OctetString("public");

	private OctetString contextEngineID;
	private OctetString contextName = new OctetString();
	private OctetString securityName = new OctetString();
	private OctetString localEngineID = new OctetString(MPv3
			.createLocalEngineID());
	private PDUv1 v1TrapPDU = new PDUv1();
	private int version = SnmpConstants.version1;
	private int engineBootCount = 0;
	private int retries = 2;
	private int timeout = 300;
	private int pduType = PDU.GET;
	private int maxRepetitions = 10;
	private int nonRepeaters = 0;
	private int maxSizeResponsePDU = 65535;

	private Vector vbs;

	private VariableBinding[] variableBindings;
	private OID rowStatusColumnOID;
	private OID rowIndexOID;
	private OID[] columnOIDs;
	private OID lowerBoundIndex;
	private OID upperBoundIndex;

	private final String SUCCESS = "Success";

	public PDU send() {

		PDU response = null;
		Snmp snmp = null;
		try {
			snmp = createSnmpSession();
			this.target = createTarget();
			target.setVersion(version);
			target.setAddress(address);
			target.setRetries(retries);
			target.setTimeout(timeout);
			target.setMaxSizeRequestPDU(maxSizeResponsePDU);
			snmp.listen();

			PDU request = createPDU(target);
			if (request.getType() == PDU.GETBULK) {
				request.setMaxRepetitions(maxRepetitions);
				request.setNonRepeaters(nonRepeaters);
			}
			for (int i = 0; i < vbs.size(); i++) {
				request.add((VariableBinding) vbs.get(i));
			}

			ResponseEvent responseEvent = snmp.send(request, target);
			if (responseEvent != null) {
				response = responseEvent.getResponse();
				if (response == null) {
					log.error(address + " Snmp Time out");
				} else {
					if (!SUCCESS
							.equalsIgnoreCase(response.getErrorStatusText())) {
						log.info("SnmpResult£º" + response.getErrorStatusText()
								+ " " + response.getErrorIndex() + " "
								+ response.getErrorStatus());

						// int type = responseEvent.getRequest().getType();
						// if (type == PDU.GETBULK || type == PDU.GET
						// || type == PDU.GETNEXT) {
						// Vector<VariableBinding> variableBindings = response
						// .getVariableBindings();
						// if (variableBindings != null) {
						// int vbSize = variableBindings.size();
						// for (int i = 0; i < vbSize; i++) {
						// VariableBinding recVB = variableBindings
						// .elementAt(i);
						// // log.info("VariableBinding:" +
						// // recVB.getOid()
						// // + ":" + recVB.getVariable().toString());
						// }
						// }
						// }
					}
				}
			}

		} catch (IOException e) {
			log.error(e);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	public PDU tableAddRow() {
		PDU response = null;
		Snmp snmp = null;
		try {
			snmp = createSnmpSession();
			this.target = createTarget();
			target.setVersion(version);
			target.setAddress(address);
			target.setRetries(retries);
			target.setTimeout(timeout);
			snmp.listen();

			TableUtils tableUtils = new TableUtils(snmp, this);
			ResponseEvent responseEvent = tableUtils.createRow(target,
					rowStatusColumnOID, rowIndexOID, variableBindings);
			if (responseEvent != null) {
				response = responseEvent.getResponse();
				if (response == null) {
					log.error(address + " Snmp Time out");
				} else {
					// log.info("SnmpResult£º" + response.getErrorStatusText());
					if (!SUCCESS
							.equalsIgnoreCase(response.getErrorStatusText())) {
						log.info("SnmpResult£º" + response.getErrorStatusText()
								+ " " + response.getErrorIndex() + " "
								+ response.getErrorStatus());
					}
				}
			}

		} catch (IOException e) {
			log.error(e);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return response;
	}

	public PDU tableDeleteRow() {

		PDU response = null;
		Snmp snmp = null;
		try {
			snmp = createSnmpSession();
			this.target = createTarget();
			target.setVersion(version);
			target.setAddress(address);
			target.setRetries(retries);
			target.setTimeout(timeout);

			snmp.listen();

			TableUtils tableUtils = new TableUtils(snmp, this);
			ResponseEvent responseEvent = tableUtils.destroyRow(target,
					rowStatusColumnOID, rowIndexOID);
			if (responseEvent != null) {
				response = responseEvent.getResponse();
				if (response == null) {
					log.error(address + " Snmp Time out");
				} else {
					// log.info("SnmpResult£º" + response.getErrorStatusText());
					if (!SUCCESS
							.equalsIgnoreCase(response.getErrorStatusText())) {
						log.info("SnmpResult£º" + response.getErrorStatusText()
								+ " " + response.getErrorIndex() + " "
								+ response.getErrorStatus());
					}
				}
			}

		} catch (IOException e) {
			log.error(e);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return response;
	}

	public List<TableEvent> getTable() {

		Snmp snmp = null;
		List<TableEvent> list = null;
		try {
			snmp = createSnmpSession();
			this.target = createTarget();
			target.setVersion(version);
			target.setAddress(address);
			target.setRetries(retries);
			target.setTimeout(timeout);

			snmp.listen();

			TableUtils tableUtils = new TableUtils(snmp, this);
			list = tableUtils.getTable(target, columnOIDs, lowerBoundIndex,
					upperBoundIndex);
			if (list == null) {
				log.error(address + " Snmp Time out");
			}
			// else {
			// boolean flag = true;
			// for (TableEvent tableEvent : list) {
			// if (flag) {
			// log.info("SnmpResult£º" + tableEvent.getErrorMessage());
			// flag = false;
			// }
			// VariableBinding[] variableBinding = tableEvent.getColumns();
			// if (variableBinding == null) {
			// continue;
			// }
			// for (VariableBinding vb : variableBinding) {
			// // log.info("VariableBinding£º" + vb.getOid() + ":"
			// // + vb.getVariable());
			// }
			// }
			// }
		} catch (IOException e) {
			log.error(e);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}

	private void checkOptions() {
		if ((pduType == PDU.V1TRAP) && (version != SnmpConstants.version1)) {
			throw new IllegalArgumentException(
					"V1TRAP PDU type is only available for SNMP version 1");
		}
	}

	private void addUsmUser(Snmp snmp) {
		snmp.getUSM().addUser(
				securityName,
				new UsmUser(securityName, authProtocol, authPassphrase,
						privProtocol, privPassphrase));
	}

	private Snmp createSnmpSession() throws IOException {

		AbstractTransportMapping transport = null;
		if (address instanceof TcpAddress) {
			transport = new DefaultTcpTransportMapping();
		} else {
			transport = new DefaultUdpTransportMapping();
		}

		Snmp snmp = new Snmp(transport);
		((MPv3) snmp.getMessageProcessingModel(MPv3.ID))
				.setLocalEngineID(localEngineID.getValue());

		if (version == SnmpConstants.version3) {
			USM usm = new USM(SecurityProtocols.getInstance(), localEngineID,
					engineBootCount);
			SecurityModels.getInstance().addSecurityModel(usm);
			addUsmUser(snmp);
		}
		return snmp;
	}

	private Target createTarget() {
		if (version == SnmpConstants.version3) {
			UserTarget target = new UserTarget();
			if (authPassphrase != null) {
				if (privPassphrase != null) {
					target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
				} else {
					target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
				}
			} else {
				target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
			}
			target.setSecurityName(securityName);
			return target;
		} else {
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(community);
			return target;
		}
	}

	public PDU createPDU(Target target) {
		PDU request;
		if (target.getVersion() == SnmpConstants.version3) {
			request = new ScopedPDU();
			ScopedPDU scopedPDU = (ScopedPDU) request;
			if (contextEngineID != null) {
				scopedPDU.setContextEngineID(contextEngineID);
			}
			if (contextName != null) {
				scopedPDU.setContextName(contextName);
			}
		} else {
			if (pduType == PDU.V1TRAP) {
				request = v1TrapPDU;
			} else {
				request = new PDU();
			}
		}
		request.setType(pduType);
		return request;
	}

	public int getPduType() {
		return pduType;
	}

	public int getVersion() {
		return version;
	}

	public Vector<VariableBinding> getVbs() {
		return vbs;
	}

	public int getTimeout() {
		return timeout;
	}

	public Target getTarget() {
		return target;
	}

	public OctetString getSecurityName() {
		return securityName;
	}

	public int getRetries() {
		return retries;
	}

	public OID getPrivProtocol() {
		return privProtocol;
	}

	public OctetString getPrivPassphrase() {
		return privPassphrase;
	}

	public int getNonRepeaters() {
		return nonRepeaters;
	}

	public int getMaxRepetitions() {
		return maxRepetitions;
	}

	public OctetString getContextName() {
		return contextName;
	}

	public OctetString getContextEngineID() {
		return contextEngineID;
	}

	public OctetString getCommunity() {
		return community;
	}

	public OID getAuthProtocol() {
		return authProtocol;
	}

	public OctetString getAuthPassphrase() {
		return authPassphrase;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setVbs(Vector vbs) {
		this.vbs = vbs;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public void setSecurityName(OctetString securityName) {
		this.securityName = securityName;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public void setPrivProtocol(OID privProtocol) {
		this.privProtocol = privProtocol;
	}

	public void setPrivPassphrase(OctetString privPassphrase) {
		this.privPassphrase = privPassphrase;
	}

	public void setPduType(int pduType) {
		this.pduType = pduType;
	}

	public void setNonRepeaters(int nonRepeaters) {
		this.nonRepeaters = nonRepeaters;
	}

	public void setMaxRepetitions(int maxRepetitions) {
		this.maxRepetitions = maxRepetitions;
	}

	public void setContextName(OctetString contextName) {
		this.contextName = contextName;
	}

	public void setContextEngineID(OctetString contextEngineID) {
		this.contextEngineID = contextEngineID;
	}

	public void setCommunity(OctetString community) {
		this.community = community;
	}

	public void setAuthProtocol(OID authProtocol) {
		this.authProtocol = authProtocol;
	}

	public void setAuthPassphrase(OctetString authPassphrase) {
		this.authPassphrase = authPassphrase;
	}

	public VariableBinding[] getVariableBindings() {
		return variableBindings;
	}

	public void setVariableBindings(VariableBinding[] variableBindings) {
		this.variableBindings = variableBindings;
	}

	public OID getRowStatusColumnOID() {
		return rowStatusColumnOID;
	}

	public void setRowStatusColumnOID(OID rowStatusColumnOID) {
		this.rowStatusColumnOID = rowStatusColumnOID;
	}

	public OID getRowIndexOID() {
		return rowIndexOID;
	}

	public void setRowIndexOID(OID rowIndexOID) {
		this.rowIndexOID = rowIndexOID;
	}

	public OID[] getColumnOIDs() {
		return columnOIDs;
	}

	public void setColumnOIDs(OID[] columnOIDs) {
		this.columnOIDs = columnOIDs;
	}

	public OID getLowerBoundIndex() {
		return lowerBoundIndex;
	}

	public void setLowerBoundIndex(OID lowerBoundIndex) {
		this.lowerBoundIndex = lowerBoundIndex;
	}

	public OID getUpperBoundIndex() {
		return upperBoundIndex;
	}

	public void setUpperBoundIndex(OID upperBoundIndex) {
		this.upperBoundIndex = upperBoundIndex;
	}

	public static void main(String[] args) {

		Request snmpRequest = new Request();
		snmpRequest.setVersion(SnmpConstants.version1);

		snmpRequest.setNonRepeaters(2);
		snmpRequest.setMaxRepetitions(13);

		snmpRequest.setAddress(new UdpAddress("192.168.1.100/161"));
		snmpRequest.setPduType(PDU.GET);
		Vector<VariableBinding> vbs = new Vector<VariableBinding>();
		// vbs.add(new VariableBinding(new OID(".1.3.6.1.2.1.1.4.0"),new
		// OctetString("xiongbo")));
		vbs.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0")));
		// vbs.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1")));
		//
		// vbs.add(new VariableBinding(new OID("1.3.6.1.2.1.2.2.1.1")));
		//
		// vbs.add(new VariableBinding(new OID("1.3.6.1.2.1.2.2.1.2")));
		//
		// vbs.add(new VariableBinding(new OID("1.3.6.1.2.1.2.2.1.3")));
		//
		// vbs.add(new VariableBinding(new
		// OID("1.3.6.1.4.1.44405.1.1.1.1.3.0")));
		//
		// vbs.add(new VariableBinding(new
		// OID("1.3.6.1.4.1.44405.1.1.1.1.4.0")));

		// vbs.add(new VariableBinding(new
		// OID("1.3.6.1.4.1.44405.1.1.1.1.5.0")));
		snmpRequest.setVbs(vbs);
		PDU response = null;

		response = snmpRequest.send();

		// for (int i=0; i<response.size(); i++) {
		// VariableBinding vb = response.get(i);
		// System.out.println(vb.toString());
		// }

		if (response != null) {
			Vector<VariableBinding> recVBs = response.getVariableBindings();
			for (int i = 0; i < recVBs.size(); i++) {
				VariableBinding recVB = recVBs.elementAt(i);
				System.out.println("Reviced:" + recVB.getOid() + " : "
						+ recVB.getVariable());
			}
		}

	}
}
