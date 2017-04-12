package com.jhw.adm.comclient.service.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.data.Switch3OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.service.BaseHandler;
import com.jhw.adm.comclient.system.AutoIncreaseConstants;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.comclient.system.IDiagnose;
import com.jhw.adm.comclient.system.OIDConfig;
import com.jhw.adm.comclient.ui.DiagnoseView;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;

/**
 * 
 * 
 * @author xiongbo
 * 
 */
public class TopologyHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(TopologyService.class);
	private AbstractSnmp snmpV2;
	// Really is 1.3.6.1.2.1.4.1
	private final String before_ipForwarding = "1.3.6.1.2.1.4";
	// Really is 1.3.6.1.2.1.2.1
	private final String before_ifNumber = "1.3.6.1.2.1.2";
	// Really is 1.3.6.1.2.1.1.7
	private String before_sysServices = "1.3.6.1.2.1.1.6";
	private final String sysServices = "1.3.6.1.2.1.1.7";
	// Really is 1.3.6.1.2.1.1.5
	private String before_sysName = "1.3.6.1.2.1.1.4";
	private final String check_sysName = "1.3.6.1.2.1.1.5";
	//
	private String ipNetToMediaPhysAddress = "1.3.6.1.2.1.4.22.1.2";
	private String ipNetToMediaNetAddress = "1.3.6.1.2.1.4.22.1.3";
	private String ipNetToMediaType = "1.3.6.1.2.1.4.22.1.4";
	// check weather end.
	private final String check_ipNetToMediaType = "1.3.6.1.2.1.4.22.1.4";
	//
	private String dot1dTpFdbAddress = "1.3.6.1.2.1.17.4.3.1.1";
	private String dot1dTpFdbPort = "1.3.6.1.2.1.17.4.3.1.2";
	// check weather end.
	private final String check_dot1dTpFdbStatus = "1.3.6.1.2.1.17.4.3.1.3";
	//
	private String ipRouteDest = "1.3.6.1.2.1.4.21.1.1";
	private String ipRouteNextHop = "1.3.6.1.2.1.4.21.1.7";
	private String ipRouteType = "1.3.6.1.2.1.4.21.1.8";
	private String ipRouteMask = "1.3.6.1.2.1.4.21.1.11";
	// check weather end
	private final String check_ipRouteMetric5 = "1.3.6.1.2.1.4.21.1.12";
	//
	private String ipAdEntAddr = "1.3.6.1.2.1.4.20.1.1";
	private String ipAdEntNetMask = "1.3.6.1.2.1.4.20.1.3";
	// check weather end
	private final String check_ipAdEntBcastAddr = "1.3.6.1.2.1.4.20.1.4";
	//
	private final String OSPFROUTERID = "1.3.6.1.2.1.14.1.1.0";

	// For Layer3 LLDP
	// ******OLD
	// private final String LLDPLOCMANADDRIFID =
	// "1.3.6.1.4.1.3320.127.1.3.8.1.5";
	// private final String LLDPLOCMANADDR = "1.3.6.1.4.1.3320.127.1.3.8.1.2";
	//
	// private final String LLDPREMLOCALPORTNUM =
	// "1.3.6.1.4.1.3320.127.1.4.1.1.2";
	//
	// private final String LLDPREMMANADDR = "1.3.6.1.4.1.3320.127.1.4.2.1.2";
	// private final String LLDPREMPORTID = "1.3.6.1.4.1.3320.127.1.4.1.1.7";
	// private final String LLDPLOCPORTID = "1.3.6.1.4.1.3320.127.1.3.7.1.3.";

	// ******NEW
	private final String LLDPLOCMANADDRIFID = "1.3.6.1.4.1.16001.127.1.3.8.1.5";
	private final String LLDPLOCMANADDR = "1.3.6.1.4.1.16001.127.1.3.8.1.2";

	private final String LLDPREMLOCALPORTNUM = "1.3.6.1.4.1.16001.127.1.4.1.1.2";

	private final String LLDPREMMANADDR = "1.3.6.1.4.1.16001.127.1.4.2.1.2";
	private final String LLDPREMPORTID = "1.3.6.1.4.1.16001.127.1.4.1.1.7";
	//
	private final String LLDPLOCPORTID = "1.3.6.1.4.1.16001.127.1.3.7.1.3.";
	//
	private final String DOT1QTPFDBADDRESS = "1.3.6.1.2.1.17.7.1.2.2.1.1";
	private final String DOT1QTPFDBPORT = "1.3.6.1.2.1.17.7.1.2.2.1.2";

	// For system info
	private final String sysName = "1.3.6.1.4.1.44405.71.1.1.0";
	private final String sysContact = "1.3.6.1.4.1.44405.71.1.2.0";
	private final String sysLocation = "1.3.6.1.4.1.44405.71.1.3.0";
	private final String sysBootTime = "1.3.6.1.4.1.44405.71.1.4.0";
	private final String sysSoftVersion = "1.3.6.1.4.1.44405.71.1.6.0";
	private final String sysBootRomVersion = "1.3.6.1.4.1.44405.71.1.7.0";
	private final String sysMacAddr = "1.3.6.1.4.1.44405.71.1.8.0";
	private final String sysVersionTime = "1.3.6.1.4.1.44405.71.1.9.0";
	private final String sysTime = "1.3.6.1.4.1.44405.71.1.10.0";
	private final String sysCPUUsageRate = "1.3.6.1.4.1.44405.71.1.11.0";
	private final String sysMemoryUsageRate = "1.3.6.1.4.1.44405.71.1.12.0";
	private final String sysTemperature = "1.3.6.1.4.1.44405.71.1.13.0";

	//

	private DiagnoseView diagnoseView;

	/**
	 * @deprecated check weather JHW
	 * 
	 * @param ip
	 * @param community
	 * @return
	 */
	public String getSysName(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 1000);// 2
		Vector<String> oids = new Vector<String>();
		oids.add(before_sysName);
		PDU response = null;
		String systemName = null;
		try {
			while (true) {
				response = snmpV2.snmpGetNext(oids);
				Vector<VariableBinding> responseVar = checkResponseVar(response);
				if (responseVar != null) {
					systemName = responseVar.elementAt(0).getVariable().toString().trim();
					String check = responseVar.elementAt(0).getOid().toString().trim();
					if (check.startsWith(check_sysName)) {
						break;
					}
					oids.clear();
					oids.add(responseVar.elementAt(0).getOid().toString().trim());
				} else {
					break;
				}
			}
			return systemName;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	/**
	 * @deprecated
	 * @param ip
	 * @param community
	 * @return
	 */
	public List<AddressEntry> getAddrTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 1000);// 2
		Vector<String> oids = new Vector<String>();
		oids.add(ipAdEntAddr);
		oids.add(ipAdEntNetMask);
		List<AddressEntry> addressList = new ArrayList<AddressEntry>();
		PDU response = null;
		try {
			while (true) {
				response = snmpV2.snmpGetNext(oids);
				Vector<VariableBinding> responseVar = checkResponseVar(response);
				if (responseVar != null) {
					String ipAdEntAddr2 = responseVar.elementAt(0).getVariable().toString().trim();
					String ipAdEntNetMask2 = responseVar.elementAt(1).getVariable().toString().trim();

					String check = responseVar.elementAt(1).getOid().toString().trim();
					if (check.startsWith(check_ipAdEntBcastAddr)) {
						break;
					}
					if (!"127.0.0.1".equals(ipAdEntAddr2) && !"127.0.0.0".equals(ipAdEntAddr2)) {
						AddressEntry addressEntry = new AddressEntry();
						addressEntry.setIpAdEntAddr(ipAdEntAddr2);
						addressEntry.setIpAdEntNetMask(ipAdEntNetMask2);
						addressList.add(addressEntry);
					}
					oids.clear();
					oids.add(responseVar.elementAt(0).getOid().toString().trim());
					oids.add(responseVar.elementAt(1).getOid().toString().trim());
				} else {
					break;
				}
			}
			if (addressList.size() == 0) {
				return null;
			}
			return addressList;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	/**
	 * Tabel Form
	 * 
	 * @param ip
	 * @param community
	 * @return
	 */
	public String getAddrTableWithTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		String[] columnOIDs = { ipAdEntAddr };

		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		String ips = "";
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							String ipAddr = variableBinding[i + 0].getVariable().toString();
							if (!"127.0.0.1".equals(ipAddr) && !"127.0.0.0".equals(ipAddr)) {
								ips += ipAddr + ",";
							}
						}
					}

				}
				// For Diagnose
				final List<TableEvent> tableEventListDiagnose = tableEventList;
				final String ipsDiagnose = ips;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						IDiagnose iDiagnose = diagnoseView.getDiagnoseReference(AutoIncreaseConstants.IPADDRTABLE);
						if (iDiagnose != null) {
							StringBuilder ssb = new StringBuilder();
							StringBuilder tsb = new StringBuilder();
							for (TableEvent tableEvent : tableEventListDiagnose) {
								VariableBinding[] variableBinding = tableEvent.getColumns();
								if (variableBinding == null) {
									continue;
								}
								ssb.append(variableBinding[0].getVariable())
										.append(System.getProperty("line.separator"));
							}
							ssb.append("******").append(System.getProperty("line.separator"));
							tableEventListDiagnose.clear();
							if (ipsDiagnose != null) {
								String[] temps = ipsDiagnose.split(",");
								for (String temp : temps) {
									tsb.append("ipAdEntAddr£º").append(temp)
											.append(System.getProperty("line.separator"));
								}
								tsb.append("******").append(System.getProperty("line.separator"));
							}
							iDiagnose.receiveInfo(ssb.toString(), tsb.toString());
						}
					}
				});
				//
			}
			if (ips.length() == 0) {
				return null;
			}
			return ips;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {

		}
	}

	public Map<Integer, String> getAddrTableWithTable_all(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		String[] columnOIDs = { "1.3.6.1.2.1.4.20.1.1", "1.3.6.1.2.1.4.20.1.2" };
		List<TableEvent> tableEventList = null;
		Map<Integer, String> addrTableMap = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				addrTableMap = new HashMap<Integer, String>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int ipAdEntIfIndex = variableBinding[1].getVariable().toInt();
					String ipAdEntAddr = variableBinding[0].getVariable().toString();
					addrTableMap.put(ipAdEntIfIndex, ipAdEntAddr);
				}
			}
			return addrTableMap;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	/**
	 * @deprecated
	 * @param ip
	 * @param community
	 * @return
	 */
	public List<RouteEntry> getRouteTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 1000);// 2
		Vector<String> oids = new Vector<String>();
		oids.add(ipRouteDest);
		oids.add(ipRouteNextHop);
		oids.add(ipRouteType);
		oids.add(ipRouteMask);
		// Map<String, List<RouteEntry>> ipRouteMap = new HashMap<String,
		// List<RouteEntry>>();
		// String mainIp = null;
		List<RouteEntry> routes = new ArrayList<RouteEntry>();
		PDU response = null;
		try {
			while (true) {
				response = snmpV2.snmpGetNext(oids);
				Vector<VariableBinding> responseVar = checkResponseVar(response);
				if (responseVar != null) {
					String ipRouteDest2 = responseVar.elementAt(0).getVariable().toString().trim();
					String ipRouteNextHop2 = responseVar.elementAt(1).getVariable().toString().trim();
					int ipRouteType2 = responseVar.elementAt(2).getVariable().toInt();

					String check = responseVar.elementAt(3).getOid().toString().trim();
					if (check.startsWith(check_ipRouteMetric5)) {
						break;
					}
					if (!"0.0.0.0".equals(ipRouteNextHop2) && !"127.0.0.1".equals(ipRouteNextHop2)
							&& !"127.0.0.0".equals(ipRouteNextHop2) && ipRouteType2 != 2) {
						RouteEntry routeEntry = new RouteEntry();
						routeEntry.setIpRouteDest(ipRouteDest2);
						routeEntry.setIpRouteNextHop(ipRouteNextHop2);
						// if (ip.equals(ipRouteNextHop)) {
						// mainIp = ipRouteDest;
						// }
						routeEntry.setIpRouteType(ipRouteType2);
						routes.add(routeEntry);
					}

					oids.clear();
					oids.add(responseVar.elementAt(0).getOid().toString().trim());
					oids.add(responseVar.elementAt(1).getOid().toString().trim());
					oids.add(responseVar.elementAt(2).getOid().toString().trim());
					oids.add(responseVar.elementAt(3).getOid().toString().trim());
				} else {
					break;
				}
			}
			if (routes.size() == 0) {
				return null;
			}
			return routes;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	/**
	 * @deprecated
	 * @param ip
	 * @param community
	 * @return
	 */
	public Map<String, Integer> getRouteTableWithTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);

		String[] columnOIDs = { ipRouteNextHop, ipRouteType, ipRouteMask };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;

		Map<String, Integer> routes = null;
		try {

			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				routes = new HashMap<String, Integer>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							String nextHop = variableBinding[i + 0].getVariable().toString();
							int routeType = variableBinding[i + 1].getVariable().toInt();
							String routeMask = variableBinding[i + 2].getVariable().toString();
							if (!"0.0.0.0".equals(nextHop) && !"127.0.0.1".equals(nextHop)
									&& !"127.0.0.0".equals(nextHop) && routeType != 2) {
								if (Configuration.layer3_loopback == 1) {
									String nextHop_oid = variableBinding[i + 0].getOid().toString();
									nextHop_oid = nextHop_oid.replace(ipRouteNextHop + ".", "");
									nextHop_oid = nextHop_oid.replace("." + routeMask + ".", "");
									nextHop_oid = nextHop_oid.replace(nextHop, "");
									if (!nextHop_oid.endsWith(".0")) {

										routes.put(nextHop_oid, 0);
									}
									// TODO
									else {
										routes.put(nextHop, 0);
									}
								} else {
									routes.put(nextHop, 0);
								}
							}
						}
					}
				}
			}

			return routes;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	/**
	 * @deprecated
	 * @param ip
	 * @param community
	 * @return
	 */
	public Map<String, String> getIpNetToMediaTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 1000);// 2
		Vector<String> oids = new Vector<String>();
		oids.add(ipNetToMediaPhysAddress);
		oids.add(ipNetToMediaNetAddress);
		Map<String, String> ipNetToMediaMap = new HashMap<String, String>();
		PDU response = null;
		try {
			while (true) {
				response = snmpV2.snmpGetNext(oids);
				Vector<VariableBinding> responseVar = checkResponseVar(response);
				if (responseVar != null) {
					String physAddress = responseVar.elementAt(0).getVariable().toString().trim();
					String netAddress = responseVar.elementAt(1).getVariable().toString().trim();

					String check = responseVar.elementAt(1).getOid().toString().trim();
					if (check.startsWith(check_ipNetToMediaType)) {
						break;
					}
					if (!"127.0.0.1".equals(netAddress) && !"127.0.0.0".equals(netAddress)) {
						ipNetToMediaMap.put(netAddress, physAddress);
					}
					oids.clear();
					oids.add(responseVar.elementAt(0).getOid().toString().trim());
					oids.add(responseVar.elementAt(1).getOid().toString().trim());
				} else {
					break;
				}
			}
			if (ipNetToMediaMap.size() == 0) {
				return null;
			}
			return ipNetToMediaMap;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	/**
	 * @deprecated
	 * @param ip
	 * @param community
	 * @return
	 */
	public Map<String, String> getIpNetToMediaTableWithTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		String[] columnOIDs = { ipNetToMediaPhysAddress, ipNetToMediaNetAddress };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Map<String, String> ipNetToMediaMap = null;
		try {

			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				ipNetToMediaMap = new HashMap<String, String>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							String physAddress = variableBinding[i + 0].getVariable().toString();
							String netAddress = variableBinding[i + 1].getVariable().toString();
							if (!"127.0.0.1".equals(netAddress) && !"127.0.0.0".equals(netAddress)) {
								ipNetToMediaMap.put(physAddress, netAddress);
							}
						}
					}
				}
			}
			return ipNetToMediaMap;

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	public Map<String, String> getIpNetToMediaTableWithTableFilter(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(3 * 1000);
		String[] columnOIDs = { ipNetToMediaPhysAddress, ipNetToMediaNetAddress, ipNetToMediaType };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Map<String, String> ipNetToMediaMap = null;
		try {

			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				ipNetToMediaMap = new HashMap<String, String>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							String physAddress = variableBinding[i + 0].getVariable().toString();
							String netAddress = variableBinding[i + 1].getVariable().toString();
							int type = variableBinding[i + 2].getVariable().toInt();
							if (!"127.0.0.1".equals(netAddress) && !"127.0.0.0".equals(netAddress)
									&& !"192.168.0.1".equals(netAddress)
							// && type == 3
							) {
								ipNetToMediaMap.put(physAddress, netAddress);
							}
						}
					}
				}
				// For Diagnose
				final List<TableEvent> tableEventListDiagnose = tableEventList;
				final Map<String, String> ipNetToMediaMapDiagnose = ipNetToMediaMap;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						IDiagnose iDiagnose = diagnoseView
								.getDiagnoseReference(AutoIncreaseConstants.IPNETTOMEDIATABLE);
						if (iDiagnose != null) {
							StringBuilder ssb = new StringBuilder();
							StringBuilder tsb = new StringBuilder();
							for (TableEvent tableEvent : tableEventListDiagnose) {
								VariableBinding[] variableBinding = tableEvent.getColumns();
								if (variableBinding == null) {
									continue;
								}
								ssb.append(variableBinding[0]).append(System.getProperty("line.separator"))
										.append(variableBinding[1]).append(System.getProperty("line.separator"))
										.append(variableBinding[2]).append(System.getProperty("line.separator"));
							}
							ssb.append("******");
							tableEventListDiagnose.clear();
							for (String physAddress : ipNetToMediaMapDiagnose.keySet()) {
								tsb.append("ipNetToMediaPhysAddress£º").append(physAddress)
										.append(System.getProperty("line.separator")).append("ipNetToMediaNetAddress£º")
										.append(ipNetToMediaMapDiagnose.get(physAddress))
										.append(System.getProperty("line.separator"));
							}
							tsb.append("******").append(System.getProperty("line.separator"));

							iDiagnose.receiveInfo(ssb.toString(), tsb.toString());
						}
					}
				});
				//
			}
			return ipNetToMediaMap;

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	public Map<String, Integer> getDot1qTpFdbTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(5 * 1000);
		String[] columnOIDs = { DOT1QTPFDBADDRESS, DOT1QTPFDBPORT };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Map<String, Integer> dot1qTpFdbTableMap = null;
		try {

			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				dot1qTpFdbTableMap = new HashMap<String, Integer>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							String physAddress = variableBinding[i + 0].getVariable().toString();
							int portIndex = variableBinding[i + 1].getVariable().toInt();
							// if (!"127.0.0.1".equals(netAddress)
							// && !"127.0.0.0".equals(netAddress)) {
							// physAddressList.add(physAddress);
							dot1qTpFdbTableMap.put(physAddress, portIndex);
							// }
						}
					}
				}
			}
			return dot1qTpFdbTableMap;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	/**
	 * @deprecated
	 * @param ip
	 * @param community
	 * @param mac
	 * @return
	 */
	public String getPortFromDot1dTpFdb(String ip, String community, String mac) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);// 3
		PDU response = null;
		try {
			mac = getStandardMac(mac);
			response = snmpV2.snmpGet(dot1dTpFdbPort + mac);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			String port = null;
			if (responseVar != null) {
				port = responseVar.elementAt(0).getVariable().toString().trim();
			}
			return port;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}

	}

	/**
	 * @deprecated
	 * @param ip
	 * @param mac
	 * @return
	 */
	public int getPortByUnicast(String ip, String mac) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.UNICAST_MACNUM);
			int unicastmacNum = 0;
			if (response != null && SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response.getVariableBindings();
				unicastmacNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (unicastmacNum == 0) {
				return 0;
			}
			snmpV2.setTimeout(12 * 1000);
			// When time out,may adjust the 'pagesize'
			int pagesize = 10;
			int mod = unicastmacNum % pagesize;
			int pages = unicastmacNum / pagesize;
			pages = mod == 0 ? pages : pages + 1;
			Vector<String> oids = new Vector<String>();
			// List<MACUniCast> uniCastList = new ArrayList<MACUniCast>();
			for (int i = 0; i < pages; i++) {
				int step = i * pagesize;
				oids.add(OID.UNICAST_MACADDR + "." + step);
				oids.add(OID.UNICAST_PORTIDX + "." + step);
				// oids.add(OID.UNICAST_VLANID + "." + step);
				// oids.add(OID.UNICAST_MACSTATE + "." + step);
				if (unicastmacNum - (i * pagesize) > mod) {
					response = snmpV2.snmpGetBulk(oids, 0, pagesize);
				} else {
					response = snmpV2.snmpGetBulk(oids, 0, mod);
				}
				if (response != null && SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
					Vector<VariableBinding> responseVar = response.getVariableBindings();
					int vbsSize = responseVar.size();
					for (int j = 0; j < vbsSize; j++) {
						if (j % 2 == 0) {
							// MACUniCast mACUniCast = new MACUniCast();

							String macAddr = responseVar.elementAt(j + 0).getVariable().toString().trim();
							int port = responseVar.elementAt(j + 1).getVariable().toInt();
							if (mac.equalsIgnoreCase(macAddr)) {
								return port;
							}

							// mACUniCast.setMacAddress(responseVar.elementAt(
							// j + 0).getVariable().toString());
							// mACUniCast.setPortNO(responseVar.elementAt(j + 1)
							// .getVariable().toInt());
							// mACUniCast.setVlanID(responseVar.elementAt(j + 2)
							// .getVariable().toString());
							// String uniCastState = responseVar.elementAt(j +
							// 3)
							// .getVariable().toString();
							// if ("Dynamic".equalsIgnoreCase(uniCastState)) {
							// mACUniCast.setUnitCastType(0);
							// } else {
							// mACUniCast.setUnitCastType(1);
							// }
							// uniCastList.add(mACUniCast);
						}
					}
				}
				oids.clear();
			}
			return 0;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return 0;
		} finally {
			if (response != null) {
				response.clear();
			}
		}

	}

	/**
	 * Get Loopback address
	 * 
	 * @param ip
	 * @param community
	 * @return
	 */
	public String getOspfRouterId(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);

		PDU response = null;
		try {
			response = snmpV2.snmpGet(OSPFROUTERID);
			Vector<VariableBinding> responseVar = checkResponseVar(response);

			String ospfRouterId = null;
			if (responseVar != null) {

				ospfRouterId = responseVar.elementAt(0).getVariable().toString();

			}
			return ospfRouterId;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}

		}
	}

	/**
	 * For Layer3 LLDP
	 * 
	 * @param ip
	 * @param community
	 * @return
	 */
	public Map<Integer, String> getLLDPLocManAddrTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		// String[] columnOIDs = { LLDPLOCMANADDRIFID, LLDPLOCMANADDR };
		String[] columnOIDs = { OIDConfig.getOIDList().getLLDPLOCMANADDRIFID(),
				OIDConfig.getOIDList().getLLDPLOCMANADDR() };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Map<Integer, String> lldpLocManAddrTable = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				lldpLocManAddrTable = new HashMap<Integer, String>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							int lldpLocManAddrIfId = variableBinding[i + 0].getVariable().toInt();
							String lldpLocManAddr = variableBinding[i + 1].getVariable().toString();
							if (!"0.0.0.0".equals(lldpLocManAddr)) {
								// TODO
								if (lldpLocManAddrTable.containsValue(lldpLocManAddr)) {
									int keyInt = 0;
									for (int key : lldpLocManAddrTable.keySet()) {
										String value = lldpLocManAddrTable.get(key);
										if (value.equals(lldpLocManAddr)) {
											keyInt = key;
										}
									}
									lldpLocManAddrTable.remove(keyInt);
								}
								lldpLocManAddrTable.put(lldpLocManAddrIfId, lldpLocManAddr);
							}
						}
					}
				}
			}
			return lldpLocManAddrTable;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	/**
	 * For Layer3 LLDP
	 * 
	 * @param ip
	 * @param community
	 * @return
	 */
	public String getLLDPLocPortDesc(String ip, String community, int lldpLocManAddrIfId) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);

		PDU response = null;
		try {
			// response = snmpV2
			// .snmpGet(LLDPLOCPORTID + lldpLocManAddrIfId + ".0");
			response = snmpV2.snmpGet(OIDConfig.getOIDList().getLLDPLOCPORTID() + lldpLocManAddrIfId + ".0");
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			String lldpLocPortDesc = null;
			if (responseVar != null) {
				lldpLocPortDesc = responseVar.elementAt(0).getVariable().toString();
			}
			return lldpLocPortDesc;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	/**
	 * Query local port ip for local port num
	 * 
	 * @param ip
	 * @param community
	 * @param lldpRemLocalPortNum
	 * @return
	 */
	public String getLLDPLocPortAddr(String ip, String community, int lldpRemLocalPortNum) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);

		PDU response = null;
		try {
			// response = snmpV2.snmpGet(LLDPLOCMANADDR + "."
			// + lldpRemLocalPortNum + ".0");
			response = snmpV2.snmpGet(OIDConfig.getOIDList().getLLDPLOCMANADDR() + "." + lldpRemLocalPortNum + ".0");
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			String lldpLocPortAddr = null;
			if (responseVar != null) {
				lldpLocPortAddr = responseVar.elementAt(0).getVariable().toString();
			}
			return lldpLocPortAddr;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	/**
	 * For Layer3 LLDP
	 * 
	 * @param ip
	 * @param community
	 * @return
	 */
	public Map<String, String> getLLDPRemoteTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		// String[] columnOIDs = { LLDPREMMANADDR, LLDPREMLOCALPORTNUM,
		// LLDPREMPORTID };
		String[] columnOIDs = { OIDConfig.getOIDList().getLLDPREMMANADDR(),
				OIDConfig.getOIDList().getLLDPREMLOCALPORTNUM(), OIDConfig.getOIDList().getLLDPREMPORTID() };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Map<String, String> lldpRemoteTable = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				lldpRemoteTable = new HashMap<String, String>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							String lldpRemManAddr = variableBinding[i + 0].getVariable().toString();
							String lldpRemLocalPortNum = variableBinding[i + 1].getVariable().toString();
							String lldpRemPortId = variableBinding[i + 2].getVariable().toString();

							if (lldpRemoteTable.get(lldpRemManAddr) == null)
								lldpRemoteTable.put(lldpRemManAddr, lldpRemLocalPortNum + "," + lldpRemPortId);
						}
					}
				}
			}
			return lldpRemoteTable;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	public Map<String, String> getLayer3LLDPTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		// String[] columnOIDs = { LLDPREMMANADDR, LLDPREMLOCALPORTNUM,
		// LLDPREMPORTID };
		String[] columnOIDs = { OIDConfig.getOIDList().getLLDPREMMANADDR(),
				OIDConfig.getOIDList().getLLDPREMLOCALPORTNUM(), OIDConfig.getOIDList().getLLDPREMPORTID() };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Map<String, String> lldpRemoteTable = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				lldpRemoteTable = new HashMap<String, String>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							//
							if (variableBinding[i + 0] == null) {
								break;
							}
							Object obj = variableBinding[i + 0].getVariable();
							if (obj == null) {
								break;
							}
							//
							String lldpRemManAddr = variableBinding[i + 0].getVariable().toString();
							String lldpRemLocalPortNum = variableBinding[i + 1].getVariable().toString();
							String lldpRemPortId = variableBinding[i + 2].getVariable().toString();

							if (lldpRemoteTable.get(lldpRemManAddr) == null)
								lldpRemoteTable.put(lldpRemManAddr, lldpRemLocalPortNum + "," + lldpRemPortId);
						}
					}
				}
				// For Diagnose
				final List<TableEvent> tableEventListDiagnose = tableEventList;
				final Map<String, String> lldpRemoteTableDiagnose = lldpRemoteTable;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						IDiagnose iDiagnose = diagnoseView.getDiagnoseReference(AutoIncreaseConstants.LAYER3LLDP);
						if (iDiagnose != null) {
							StringBuilder ssb = new StringBuilder();
							StringBuilder tsb = new StringBuilder();
							for (TableEvent tableEvent : tableEventListDiagnose) {
								VariableBinding[] variableBinding = tableEvent.getColumns();
								if (variableBinding == null) {
									continue;
								}
								ssb.append(variableBinding[0]).append(System.getProperty("line.separator"))
										.append(variableBinding[1]).append(System.getProperty("line.separator"))
										.append(variableBinding[2]).append(System.getProperty("line.separator"));
							}
							ssb.append("******").append(System.getProperty("line.separator"));
							tableEventListDiagnose.clear();
							for (String lldpRemManAddr : lldpRemoteTableDiagnose.keySet()) {
								tsb.append("lldpRemManAddr£º").append(lldpRemManAddr)
										.append(System.getProperty("line.separator")).append("lldpRemoteAddr£º")
										.append(lldpRemoteTableDiagnose.get(lldpRemManAddr))
										.append(System.getProperty("line.separator"));
							}
							tsb.append("******").append(System.getProperty("line.separator"));
							iDiagnose.receiveInfo(ssb.toString(), tsb.toString());
						}
					}
				});
				//
			}
			return lldpRemoteTable;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
		}
	}

	public SwitchLayer3 get3LayerBase(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(Switch3OID.SYSDESCR);
		SwitchLayer3 base = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oids);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				base = new SwitchLayer3();
				String descr = responseVar.elementAt(0).getVariable().toString();
				String[] desc = descr.split("\n");
				if (desc != null) {
					String[] temp = desc[1].split(" ");
					if (temp != null) {
						base.setName(temp[0]);
					}
				}
			}
			return base;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return base;
		} finally {
			if (response != null) {
				response.clear();
			}
			oids.clear();
		}
	}

	public SystemInfoEntity getSystemInfo(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(sysName);
		oids.add(sysContact);
		oids.add(sysLocation);
		oids.add(sysBootTime);
		oids.add(sysSoftVersion);
		oids.add(sysBootRomVersion);
		oids.add(sysMacAddr);
		oids.add(sysVersionTime);
		oids.add(sysTime);
		oids.add(sysCPUUsageRate);
		oids.add(sysMemoryUsageRate);
		oids.add(sysTemperature);

		PDU response = null;
		try {
			response = snmpV2.snmpGet(oids);
			final Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar == null) {
				return null;
			}
			Object sNameObj = responseVar.elementAt(0).getVariable();
			Object sContactObj = responseVar.elementAt(1).getVariable();
			Object sLocationObj = responseVar.elementAt(2).getVariable();
			Object ssysBootTimeObj = responseVar.elementAt(3).getVariable();
			Object ssysSoftVersionObj = responseVar.elementAt(4).getVariable();
			Object ssysBootRomVersionObj = responseVar.elementAt(5).getVariable();
			Object ssysMacAddrObj = responseVar.elementAt(6).getVariable();
			Object ssysVersionTimeObj = responseVar.elementAt(7).getVariable();
			Object ssysTimeObj = responseVar.elementAt(8).getVariable();
			Object sysCPUUsageRateObj = responseVar.elementAt(9).getVariable();
			Object sysMemoryUsageRateObj = responseVar.elementAt(10).getVariable();
			Object sysTemperatureObj = responseVar.elementAt(11).getVariable();

			final SystemInfoEntity systemInfoEntity = new SystemInfoEntity();

			if (sNameObj != null) {
				systemInfoEntity.setSysName(sNameObj.toString());
			}
			if (sContactObj != null) {
				systemInfoEntity.setSysContact(sContactObj.toString());
			}
			if (sLocationObj != null) {
				systemInfoEntity.setSysLocation(sLocationObj.toString());
			}
			if (ssysBootTimeObj != null) {
				systemInfoEntity.setSysBootTime(ssysBootTimeObj.toString());
			}
			if (ssysSoftVersionObj != null) {
				systemInfoEntity.setSysSoftVersion(ssysSoftVersionObj.toString());
			}
			if (ssysBootRomVersionObj != null) {
				systemInfoEntity.setSysBootRomVersion(ssysBootRomVersionObj.toString());
			}
			if (ssysMacAddrObj != null) {
				systemInfoEntity.setSysMacAddr(ssysMacAddrObj.toString());
			}
			if (ssysVersionTimeObj != null) {
				systemInfoEntity.setSysVersionTime(ssysVersionTimeObj.toString());
			}
			if (ssysTimeObj != null) {
				systemInfoEntity.setSysTime(ssysTimeObj.toString());
			}
			if (sysCPUUsageRateObj != null) {
				systemInfoEntity.setSysCPUUsageRate(sysCPUUsageRateObj.toString());
			}
			if (sysMemoryUsageRateObj != null) {
				systemInfoEntity.setSysMemoryUsageRate(sysMemoryUsageRateObj.toString());
			}
			if (sysTemperatureObj != null) {
				systemInfoEntity.setSysTemperature(sysTemperatureObj.toString());
			}

			// For Diagnose
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					IDiagnose iDiagnose = diagnoseView.getDiagnoseReference(AutoIncreaseConstants.SYSTEM);
					if (iDiagnose != null) {
						StringBuilder ssb = new StringBuilder();
						StringBuilder tsb = new StringBuilder();
						ssb.append(responseVar.elementAt(0)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(1)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(2)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(3)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(4)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(5)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(6)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(7)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(8)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(9)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(10)).append(System.getProperty("line.separator"))
								.append(responseVar.elementAt(11)).append(System.getProperty("line.separator"))
								.append("******").append(System.getProperty("line.separator"));
						// }
						responseVar.clear();
						tsb.append("SysName£º").append(systemInfoEntity.getSysName())
								.append(System.getProperty("line.separator")).append("SysContact£º")
								.append(systemInfoEntity.getSysContact()).append(System.getProperty("line.separator"))
								.append("SysLocation£º").append(systemInfoEntity.getSysLocation())
								.append(System.getProperty("line.separator")).append("SysBootTime£º")
								.append(systemInfoEntity.getSysBootTime()).append(System.getProperty("line.separator"))
								.append("SysSoftVersion£º").append(systemInfoEntity.getSysSoftVersion())
								.append(System.getProperty("line.separator")).append("SysBootRomVersion£º")
								.append(systemInfoEntity.getSysBootRomVersion())
								.append(System.getProperty("line.separator")).append("SysMacAddr£º")
								.append(systemInfoEntity.getSysMacAddr()).append(System.getProperty("line.separator"))
								.append("SysVersionTime£º").append(systemInfoEntity.getSysVersionTime())
								.append(System.getProperty("line.separator")).append("SysTime£º")
								.append(systemInfoEntity.getSysTime()).append(System.getProperty("line.separator"))
								.append("SysCPUUsageRate£º").append(systemInfoEntity.getSysCPUUsageRate())
								.append(System.getProperty("line.separator")).append("SysMemoryUsageRate£º")
								.append(systemInfoEntity.getSysMemoryUsageRate())
								.append(System.getProperty("line.separator")).append("SysTemperature£º")
								.append(systemInfoEntity.getSysTemperature())
								.append(System.getProperty("line.separator")).append("******")
								.append(System.getProperty("line.separator"));
						iDiagnose.receiveInfo(ssb.toString(), tsb.toString());
					}
				}
			});
			//

			return systemInfoEntity;

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			// if (response != null) {
			// response.clear();
			// }
			oids.clear();
		}
	}

	public Set<LLDPInofEntity> getMOXALLDPTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		String[] columnOIDs = { "1.3.6.1.4.1.8691.2.12.1.7.2.2.1.1.5", "1.3.6.1.4.1.8691.2.12.1.7.2.2.1.1.6",
				"1.3.6.1.4.1.8691.2.12.1.7.2.2.1.1.7" };
		// int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Set<LLDPInofEntity> lldpTable = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				lldpTable = new HashSet<LLDPInofEntity>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int localPort = variableBinding[0].getVariable().toInt();
					String remoteIpAddress = variableBinding[1].getVariable().toString();
					int remotePort = variableBinding[2].getVariable().toInt();

					if ((!"0.0.0.0".equals(remoteIpAddress)) && (localPort != 80 || localPort != 23)) {
						LLDPInofEntity lldpInofEntity = new LLDPInofEntity();
						lldpInofEntity.setLocalDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2);
						lldpInofEntity.setLocalIP(ip);
						lldpInofEntity.setLocalPortNo(localPort);
						// lldpInofEntity.setLocalPortType();
						lldpInofEntity.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2);
						lldpInofEntity.setRemoteIP(remoteIpAddress);
						lldpInofEntity.setRemotePortNo(remotePort);
						// lldpInofEntity.setRemotePortType();
						lldpTable.add(lldpInofEntity);
					}
				}
			}
			return lldpTable;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	public Set<LLDPInofEntity> getKYLANDLLDPTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		String[] columnOIDs = { "1.3.6.1.4.1.8691.2.12.1.7.2.2.1.1.5", "1.3.6.1.4.1.8691.2.12.1.7.2.2.1.1.6",
				"1.3.6.1.4.1.8691.2.12.1.7.2.2.1.1.7" };
		// int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Set<LLDPInofEntity> lldpTable = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				lldpTable = new HashSet<LLDPInofEntity>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int localPort = variableBinding[0].getVariable().toInt();
					String remoteIpAddress = variableBinding[1].getVariable().toString();
					int remotePort = variableBinding[2].getVariable().toInt();

					if ((!"0.0.0.0".equals(remoteIpAddress)) && (localPort != 80 || localPort != 23)) {
						LLDPInofEntity lldpInofEntity = new LLDPInofEntity();
						lldpInofEntity.setLocalDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2);
						lldpInofEntity.setLocalIP(ip);
						lldpInofEntity.setLocalPortNo(localPort);
						// lldpInofEntity.setLocalPortType();
						lldpInofEntity.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2);
						lldpInofEntity.setRemoteIP(remoteIpAddress);
						lldpInofEntity.setRemotePortNo(remotePort);
						// lldpInofEntity.setRemotePortType();
						lldpTable.add(lldpInofEntity);
					}
				}
			}
			return lldpTable;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	public SwitchBaseConfig getMOXABaseConfig(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add("1.3.6.1.4.1.44405.1.1.1.1.1.0");// modelName
		oids.add("1.3.6.1.4.1.44405.1.1.1.2.1.0");// macAddress
		oids.add("1.3.6.1.4.1.44405.1.1.1.2.1.0");// autoIPConfig(dhcpAyylied)
		oids.add("1.3.6.1.4.1.44405.1.1.1.2.2.0");// subMask
		oids.add("1.3.6.1.4.1.44405.1.1.1.2.3.0");// gateway
		oids.add("1.3.6.1.4.1.44405.1.1.1.2.1.0");// dnsServer1IPAddr
		oids.add("1.3.6.1.4.1.44405.1.1.1.2.1.0");// dnsServer2IPAddr
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oids);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar == null) {
				return null;
			}
			String modelName = responseVar.elementAt(0).getVariable().toString();
			String macAddress = responseVar.elementAt(1).getVariable().toString();
			int dhcpAyylied = responseVar.elementAt(2).getVariable().toInt();
			String subMask = responseVar.elementAt(3).getVariable().toString();
			String gateway = responseVar.elementAt(4).getVariable().toString();
			String dnsServer1IPAddr = responseVar.elementAt(5).getVariable().toString();
			String dnsServer2IPAddr = responseVar.elementAt(6).getVariable().toString();

			SwitchBaseConfig switchBaseConfig = new SwitchBaseConfig();
			switchBaseConfig.setDescs(modelName);
			if (dhcpAyylied == 1) {
				switchBaseConfig.setDhcpAyylied(true);
			}
			switchBaseConfig.setFirstDNS(dnsServer1IPAddr);
			switchBaseConfig.setIpValue(ip);
			switchBaseConfig.setMaskValue(subMask);
			switchBaseConfig.setNetGate(gateway);
			switchBaseConfig.setSecondDNS(dnsServer2IPAddr);
			// Use to transfer macAddress,then must be NULL
			switchBaseConfig.setUserNames(macAddress);

			return switchBaseConfig;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			oids.clear();
		}
	}

	public Object getSingleNode(String ip, String community, String oid) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oid);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			Object obj = null;
			if (responseVar != null) {
				obj = responseVar.elementAt(0).getVariable();
			}
			return obj;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	private String getStandardMac(String mac) {
		String[] macs = mac.trim().split(":");
		mac = "";
		for (String m : macs) {
			mac += "." + Integer.parseInt(m, 16);
		}
		return mac;
	}

	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

	public void setDiagnoseView(DiagnoseView diagnoseView) {
		this.diagnoseView = diagnoseView;
	}

}
