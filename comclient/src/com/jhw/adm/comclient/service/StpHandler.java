package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.switchs.STPPortConfig;
import com.jhw.adm.server.entity.switchs.STPSysConfig;

public class StpHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(StpHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private PortHandler portHandler;

	public boolean configPort(String ip, List<STPPortConfig> stpList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		boolean result = false;
		int count = 1;
		try {
			for (STPPortConfig portRSTP : stpList) {
				Map<String, Object> oids = new HashMap<String, Object>();
				// TODO
				// String portid = portRSTP.getPortNo() + "";
				String lujingkaixiao = portRSTP.getLujingkaixiao() + "";
				int prelevel = portRSTP.getPrelevel();
				String stpMode = "";
				if (portRSTP.isStpModed()) {
					stpMode = "enable";
				} else {
					stpMode = "disable";
				}
				String edgePort = "";
				if (portRSTP.isEdgePorted()) {
					edgePort = "enable";
				} else {
					edgePort = "disable";
				}
				// In mib,p2p value:auto Enabled Disabled
				// When package set:auto true false
				String p2pPort = "";
				if (portRSTP.getP2pPort() == 0) {
					p2pPort = AUTO;
				} else if (portRSTP.getP2pPort() == 1) {
					p2pPort = TRUE;
				} else {
					p2pPort = FALSE;
				}
				// byte[] dataBuffer = dataBufferBuilder.stpPortConfig(portid,
				// lujingkaixiao, prelevel, stpMode, edgePort, p2pPort);
				// response = snmpV2.snmpSet(OID.STP_PORT_BATCH_OPERATE,
				// dataBuffer);
				oids.put(OID.STP_MODE + "." + count, stpMode);
				oids.put(OID.STP_PATHCOST + "." + count, lujingkaixiao);
				oids.put(OID.STP_PRIORITY + "." + count, prelevel);
				oids.put(OID.STP_EDGE + "." + count, edgePort);
				oids.put(OID.STP_P2P + "." + count, p2pPort);

				response = snmpV2.snmpSet(oids);
				result = checkResponse(response);

				if (!checkResponse(response)) {
					return false;
				}
				count++;
			}
			return result;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			// stpList.clear();
		}
	}

	public boolean configStp(String ip, STPSysConfig switchRSTP) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		Map<String, Object> oids = new HashMap<String, Object>();
		PDU response = null;
		try {
			oids.put(OID.STP_FORWARDDELAY, switchRSTP.getTransferDelay());
			oids.put(OID.STPPRIORITY, (int) switchRSTP.getPre());
			oids.put(OID.STP_PROTOCOLVERSION, switchRSTP.getProtocolVersion());
			oids.put(OID.STP_MAXAGE, switchRSTP.getMaxOldTime());

			// 设置到交换机时由于传入的值是一个个设置的，
			// 而最大老化时间和转发延时的值之间又有关联，
			// 会导致现在下发的值和交换机上已经存在的值的关系不满足
			// 条件"2*(转发延时-1)必须大于或等于最大的老化时间",导致失败。
			// 所有针对stp系统配置进行了两次下发。并且以第二次下发的结果为准
			boolean result = false;
			int count = 0;
			while (count < 2) {
				response = snmpV2.snmpSet(oids);
				result = checkResponse(response);
				count++;
			}

			return result;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			oids.clear();
		}
	}

	public List<STPPortConfig> getPort(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		int portn = portHandler.getPortNum(ip);
		if (portn == 0) {
			return null;
		}
		snmpV2.setTimeout(3 * 1000);
		Vector<String> vbs = new Vector<String>();
		vbs.add(OID.STPPORT);
		vbs.add(OID.STP_MODE);
		vbs.add(OID.STP_PATHCOST);
		vbs.add(OID.STP_PRIORITY);
		vbs.add(OID.STP_EDGE);
		vbs.add(OID.STP_P2P);
		PDU response = null;
		try {
			response = snmpV2.snmpGetBulk(vbs, 0, portn);
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				int vbsSize = responseVar.size();
				List<STPPortConfig> portStpList = new ArrayList<STPPortConfig>();
				for (int i = 0; i < vbsSize; i++)
					// 6 is size of vbs
					if (i % 6 == 0) {
						STPPortConfig portRSTP = new STPPortConfig();
						// SwitchPortEntity switchPortEntity = new
						// SwitchPortEntity();
						// switchPortEntity.setPortNO(Integer.parseInt(responseVar
						// .elementAt(i + 0).toString()));
						portRSTP.setPortNo(responseVar.elementAt(i + 0)
								.getVariable().toInt());
						String stpMode = responseVar.elementAt(i + 1)
								.getVariable().toString().trim();
						if (ENABLED.equalsIgnoreCase(stpMode)) {
							portRSTP.setStpModed(true);
						} else {
							portRSTP.setStpModed(false);
						}
						if (AUTO.equalsIgnoreCase(responseVar.elementAt(i + 2)
								.getVariable().toString().trim())) {
							portRSTP.setLujingkaixiao(0);
						} else {
							// Change type wrong,if use 'toInt()'
							portRSTP.setLujingkaixiao(Integer
									.parseInt(responseVar.elementAt(i + 2)
											.getVariable().toString().trim()));
						}
						portRSTP.setPrelevel(responseVar.elementAt(i + 3)
								.getVariable().toInt());
						String edge = responseVar.elementAt(i + 4)
								.getVariable().toString().trim();
						if (ENABLED.equalsIgnoreCase(edge)) {
							portRSTP.setEdgePorted(true);
						} else {
							portRSTP.setEdgePorted(false);
						}
						// 0:自动；1：是；2：否
						// In mib,p2p value:auto Enabled Disabled
						// When package set:auto true false
						String p2p = responseVar.elementAt(i + 5).getVariable()
								.toString().trim();
						if (AUTO.equalsIgnoreCase(p2p)) {
							portRSTP.setP2pPort(0);
						} else if (ENABLED.equalsIgnoreCase(p2p)) {
							portRSTP.setP2pPort(1);
						} else {
							portRSTP.setP2pPort(2);
						}

						portStpList.add(portRSTP);
					}

				return portStpList;
			}// else {
			return null;
			// }
		} catch (RuntimeException e) {// TODO
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbs.clear();
		}

	}

	public STPSysConfig getStp(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> vbs = new Vector<String>();
		vbs.add(OID.STPPRIORITY);
		vbs.add(OID.STP_MAXAGE);
		vbs.add(OID.STP_FORWARDDELAY);
		vbs.add(OID.STP_PROTOCOLVERSION);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(vbs);
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				STPSysConfig switchRSTP = new STPSysConfig();
				switchRSTP.setPre(responseVar.elementAt(0).getVariable()
						.toLong());
				switchRSTP.setMaxOldTime(responseVar.elementAt(1).getVariable()
						.toInt());
				switchRSTP.setTransferDelay(responseVar.elementAt(2)
						.getVariable().toInt());
				switchRSTP.setProtocolVersion(responseVar.elementAt(3)
						.getVariable().toString());

				return switchRSTP;
			}
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbs.clear();
		}
		return null;
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
