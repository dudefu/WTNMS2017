package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.util.MACMutiCast;
import com.jhw.adm.server.entity.util.MACUniCast;

/**
 * 
 * @author xiongbo
 * 
 */

public class MacHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(MacHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private PortHandler portHandler;

	public boolean configAgingTime(String ip, MACUniCast mACUniCast) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			byte[] dataBuffer = dataBufferBuilder.agingTimeConfig(mACUniCast
					.getOldTime());
			response = snmpV2.snmpSet(OID.AGINGTIME_BATCH_OPERATE, mACUniCast
					.getOldTime());

			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public boolean createMulticast(String ip, MACMutiCast mACMutiCast) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		int portn = portHandler.getPortNum(ip);
		if (portn == 0) {
			return false;
		}
		PDU response = null;
		try {
			String mac = mACMutiCast.getMacAddress();
			if (!mac.trim().startsWith(MULTICAST_PREFIX_SEPARATE)
					&& !mac.trim().startsWith(MULTICAST_PREFIX_COLON)) {
				return false;
			}
			int vlanId = Integer.parseInt(mACMutiCast.getVlanID());
			byte[] portByte = new byte[portn];
			String[] portStr = mACMutiCast.getPorts().split(",");

			// for (String port : portStr) {
			// int portId = Integer.parseInt(port);
			// portByte[portId - 1] = 1;
			// }
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < portStr.length - 1; i++) {
				sb.append(portStr[i] + ",");
			}
			String port = sb.toString() + portStr[portStr.length - 1];
			byte[] dataBuffer = dataBufferBuilder.multicastCreate(mac,
					portByte, vlanId);
			response = snmpV2.snmpSet(OID.MULTICAST_BATCH_OPERATE, mac + "!"
					+ port + "!" + vlanId);

			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public boolean createUnicast(String ip, MACUniCast mACUniCast) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			String mac = mACUniCast.getMacAddress();
			int vlanId = Integer.parseInt(mACUniCast.getVlanID());
			int portId = mACUniCast.getPortNO();
			// byte[] dataBuffer = dataBufferBuilder.unicastCreate(mac, portId,
			// vlanId);
			// byte[] bufh = { 0, 0, 0, 2, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 4,
			// 0,
			// 0, 0, 1, 0, 0, 0, 0, 0 };
			// byte[] bufL = { 2, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 4, 0, 0, 0,
			// 1,
			// 0, 0, 0, 0, 0, 0, 0, 0 };
			boolean b = configAgingTime(ip, mACUniCast);
			response = snmpV2.snmpSet(OID.UNICAST_BATCH_OPERATE, mac + ","
					+ portId + "," + vlanId);

			return checkResponse(response) == b;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public List<MACMutiCast> getMulticast(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2000);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> vbs = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.MULTIMACNUM);
			int multimacNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			List<MACMutiCast> mutiCastList = null;
			if (responseVar != null) {
				mutiCastList = new ArrayList<MACMutiCast>();
				multimacNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (multimacNum == 0) {
				return mutiCastList;
			}
			snmpV2.setTimeout(6 * 1000);
			vbs = new Vector<String>();
			// vbs.add(OID.MULTIMACID);
			vbs.add(OID.MULTIMACADDR);
			vbs.add(OID.MULTIPORTIDX);
			vbs.add(OID.MULTIVLANID);
			response = snmpV2.snmpGetBulk(vbs, 0, multimacNum);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				int j = 1;
				for (int i = 0; i < vbsSize; i++) {
					// 3 is size of vbs
					if (i % 3 == 0) {
						MACMutiCast mACMutiCast = new MACMutiCast();
						// mACMutiCast.setMutiCode(Integer.parseInt(responseVar
						// .elementAt(i + 0).toString()));
						mACMutiCast.setSortNum(j++);
						mACMutiCast.setMacAddress(responseVar.elementAt(i + 0)
								.getVariable().toString());
						String ports = responseVar.elementAt(i + 1)
								.getVariable().toString();
						String[] portStr = ports.split(",");
						String port = "";
						for (String portss : portStr) {
							port += portss + ",";
						}
						port = port.substring(0, port.length() - 1);
						mACMutiCast.setPorts(port);
						mACMutiCast.setVlanID(responseVar.elementAt(i + 2)
								.getVariable().toString());
						// List<SwitchPortEntity> portList = new
						// ArrayList<SwitchPortEntity>();
						// for (String port : portStr) {
						// SwitchPortEntity switchPortEntity = new
						// SwitchPortEntity();
						// switchPortEntity.setPortNO(Integer.parseInt(port));
						// portList.add(switchPortEntity);
						// }
						// mACMutiCast.setPorts(portList);
						// VlanEntity vlanEntity = new VlanEntity();
						// vlanEntity.setVlanID(responseVar.elementAt(i + 3)
						// .toString());
						// mACMutiCast.setVlan(vlanEntity);

						mutiCastList.add(mACMutiCast);
					}
				}

				return mutiCastList;
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (vbs != null) {
				vbs.clear();
			}
		}
	}

	public List<MACUniCast> getUnicast(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.UNICAST_MACNUM);
			int unicastmacNum = 0;
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				unicastmacNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (unicastmacNum == 0) {
				return null;
			}
			snmpV2.setTimeout(12 * 1000);
			// When time out,may adjust the 'pagesize'
			int pagesize = 10;
			int mod = unicastmacNum % pagesize;
			int pages = unicastmacNum / pagesize;
			pages = mod == 0 ? pages : pages + 1;
			Vector<String> oids = new Vector<String>();
			List<MACUniCast> uniCastList = new ArrayList<MACUniCast>();
			int k = 1;
			for (int i = 0; i < pages; i++) {
				int step = i * pagesize;
				oids.add(OID.UNICAST_MACADDR + "." + step);
				oids.add(OID.UNICAST_PORTIDX + "." + step);
				oids.add(OID.UNICAST_VLANID + "." + step);
				oids.add(OID.UNICAST_MACSTATE + "." + step);
				if (unicastmacNum - (i * pagesize) > mod) {
					response = snmpV2.snmpGetBulk(oids, 0, pagesize);
				} else {
					response = snmpV2.snmpGetBulk(oids, 0, mod);
				}
				if (response != null
						&& SUCCESS.equalsIgnoreCase(response
								.getErrorStatusText())) {
					Vector<VariableBinding> responseVar = response
							.getVariableBindings();
					int vbsSize = responseVar.size();
					for (int j = 0; j < vbsSize; j++) {
						if (j % 4 == 0) {
							MACUniCast mACUniCast = new MACUniCast();
							mACUniCast.setSortNum(k++);
							mACUniCast.setMacAddress(responseVar.elementAt(
									j + 0).getVariable().toString());
							mACUniCast.setPortNO(responseVar.elementAt(j + 1)
									.getVariable().toInt());
							mACUniCast.setVlanID(responseVar.elementAt(j + 2)
									.getVariable().toString());
							String uniCastState = responseVar.elementAt(j + 3)
									.getVariable().toString();
							if ("Dynamic".equalsIgnoreCase(uniCastState)) {
								mACUniCast.setUnitCastType(0);
							} else {
								mACUniCast.setUnitCastType(1);
							}
							uniCastList.add(mACUniCast);
						}
					}
				}
				oids.clear();
			}
			return uniCastList;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}

	}

	public List<MACUniCast> getUnicast_batch(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setTimeout(6 * 1000);
		String[] columnOIDs = { OID.UNICAST_MACADDR, OID.UNICAST_PORTIDX,
				OID.UNICAST_VLANID, OID.UNICAST_MACSTATE };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		List<MACUniCast> uniCastList = null;
		try {
			uniCastList = new ArrayList<MACUniCast>();
			int j = 1;
			int start = -1;
			final int ROWCOUNT = 9;
			int count = ROWCOUNT;
			while (count >= ROWCOUNT) {
				start++;
				log.info("Start:" + start + " end:" + (start + ROWCOUNT));
				tableEventList = snmpV2.snmpTableDisplay(columnOIDs,
						start + "", (start + ROWCOUNT) + "");
				if (tableEventList != null) {
					int k = 1;
					for (TableEvent tableEvent : tableEventList) {
						VariableBinding[] variableBinding = tableEvent
								.getColumns();
						if (variableBinding == null) {
							continue;
						}
						int vbLength = variableBinding.length;
						for (int i = 0; i < vbLength; i++) {
							if (i % columnOIDLength == 0) {
								k++;
								MACUniCast mACUniCast = new MACUniCast();
								mACUniCast.setSortNum(j++);
								mACUniCast.setMacAddress(variableBinding[i + 0]
										.getVariable().toString());
								mACUniCast.setPortNO(variableBinding[i + 1]
										.getVariable().toInt());
								mACUniCast.setVlanID(variableBinding[i + 2]
										.getVariable().toString());
								String uniCastState = variableBinding[i + 3]
										.getVariable().toString();
								if ("Dynamic".equalsIgnoreCase(uniCastState)) {
									mACUniCast.setUnitCastType(0);
								} else {
									mACUniCast.setUnitCastType(1);
								}
								uniCastList.add(mACUniCast);
							}
						}
					}
					count = k;
					tableEventList.clear();
				}
				start += ROWCOUNT;
			}
			log.info("%%&&:" + uniCastList.size());
			return uniCastList;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}

		// try {
		// response = snmpV2.snmpGet(OID.UNICAST_MACNUM);
		// int unicastmacNum = 0;
		// if (response != null
		// && SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
		// Vector<VariableBinding> responseVar = response
		// .getVariableBindings();
		// unicastmacNum = responseVar.elementAt(0).getVariable().toInt();
		// }
		// if (unicastmacNum == 0) {
		// return null;
		// }
		// snmpV2.setTimeout(12 * 1000);
		// // When time out,may adjust the 'pagesize'
		// int pagesize = 10;
		// int mod = unicastmacNum % pagesize;
		// int pages = unicastmacNum / pagesize;
		// pages = mod == 0 ? pages : pages + 1;
		// Vector<String> oids = new Vector<String>();
		// List<MACUniCast> uniCastList = new ArrayList<MACUniCast>();
		// int k = 1;
		// for (int i = 0; i < pages; i++) {
		// int step = i * pagesize;
		// oids.add(OID.UNICAST_MACADDR + "." + step);
		// oids.add(OID.UNICAST_PORTIDX + "." + step);
		// oids.add(OID.UNICAST_VLANID + "." + step);
		// oids.add(OID.UNICAST_MACSTATE + "." + step);
		// if (unicastmacNum - (i * pagesize) > mod) {
		// response = snmpV2.snmpGetBulk(oids, 0, pagesize);
		// } else {
		// response = snmpV2.snmpGetBulk(oids, 0, mod);
		// }
		// if (response != null
		// && SUCCESS.equalsIgnoreCase(response
		// .getErrorStatusText())) {
		// Vector<VariableBinding> responseVar = response
		// .getVariableBindings();
		// int vbsSize = responseVar.size();
		// for (int j = 0; j < vbsSize; j++) {
		// if (j % 4 == 0) {
		// MACUniCast mACUniCast = new MACUniCast();
		// mACUniCast.setSortNum(k++);
		// mACUniCast.setMacAddress(responseVar.elementAt(
		// j + 0).getVariable().toString());
		// mACUniCast.setPortNO(responseVar.elementAt(j + 1)
		// .getVariable().toInt());
		// mACUniCast.setVlanID(responseVar.elementAt(j + 2)
		// .getVariable().toString());
		// String uniCastState = responseVar.elementAt(j + 3)
		// .getVariable().toString();
		// if ("Dynamic".equalsIgnoreCase(uniCastState)) {
		// mACUniCast.setUnitCastType(0);
		// } else {
		// mACUniCast.setUnitCastType(1);
		// }
		// uniCastList.add(mACUniCast);
		// }
		// }
		// }
		// oids.clear();
		// }
		// return uniCastList;
		// } catch (RuntimeException e) {
		// log.error(getTraceMessage(e));
		// return null;
		// } finally {
		// if (response != null) {
		// response.clear();
		// }
		// }

	}

	public boolean deleteUnicast(String ip, MACUniCast mACUniCast) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			String mac = mACUniCast.getMacAddress();
			int vlanId = Integer.parseInt(mACUniCast.getVlanID());
			// int portId = mACUniCast.getPort().getPortNO();
			byte[] dataBuffer = dataBufferBuilder.unicastDelete(mac, 0, vlanId);
			response = snmpV2.snmpSet(OID.UNICAST_BATCH_DELETE, mac + ","
					+ vlanId);

			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public boolean deleteMulticast(String ip, MACMutiCast mACMutiCast) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return false;
		}
		PDU response = null;
		try {
			String mac = mACMutiCast.getMacAddress();
			if (!mac.trim().startsWith(MULTICAST_PREFIX_SEPARATE)
					&& !mac.trim().startsWith(MULTICAST_PREFIX_COLON)) {
				return false;
			}
			int vlanId = Integer.parseInt(mACMutiCast.getVlanID());
			byte[] portByte = new byte[portNum];
			// for (SwitchPortEntity switchPortEntity : mACMutiCast.getPorts())
			// {
			// int portId = switchPortEntity.getPortNO();
			// portByte[portId] = 1;
			// }
			byte[] dataBuffer = dataBufferBuilder.multicastDelete(mac,
					portByte, vlanId);
			response = snmpV2.snmpSet(OID.MULTICAST_BATCH_DELETE, mac + "!"
					+ vlanId);

			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

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

	public PortHandler getPortHandler() {
		return portHandler;
	}

	public void setPortHandler(PortHandler portHandler) {
		this.portHandler = portHandler;
	}
}
