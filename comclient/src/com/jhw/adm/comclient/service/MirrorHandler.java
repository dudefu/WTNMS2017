package com.jhw.adm.comclient.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.switchs.MirrorEntity;

/**
 * 端口镜像
 * 
 * @author xiongbo
 * 
 */
public class MirrorHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(MirrorHandler.class);
	private AbstractSnmp snmpV2;

	public boolean configMirror(String ip, MirrorEntity mirrorEntity) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(4 * 1000);
		Map<String, Object> vbsMap1 = new HashMap<String, Object>();
		Map<String, Object> vbsMap2 = new HashMap<String, Object>();
		Map<String, Object> vbsMap3 = new HashMap<String, Object>();
		Map<String, Object> vbsMap4 = new HashMap<String, Object>();
		Map<String, Object> vbsMap5 = new HashMap<String, Object>();
		Map<String, Object> vbsMap6 = new HashMap<String, Object>();
		Map<String, Object> vbsMap7 = new HashMap<String, Object>();
		Map<String, Object> vbsMap8 = new HashMap<String, Object>();
		Map<String, Object> vbsMap9 = new HashMap<String, Object>();
		Map<String, Object> vbsMap10 = new HashMap<String, Object>();
		PDU response1 = null;
		PDU response2 = null;
		PDU response3 = null;
		PDU response4 = null;
		PDU response5 = null;
		PDU response6 = null;
		PDU response7 = null;
		PDU response8 = null;
		PDU response9 = null;
		PDU response10 = null;
		try {
			if (mirrorEntity.isApplied()) {
				vbsMap1.put(OID.MIRRORSTART, ENABLED);
			} else {
				vbsMap1.put(OID.MIRRORSTART, DISABLED);
			}
			response1 = snmpV2.snmpSet(vbsMap1);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// 镜像端口
			vbsMap2.put(OID.MIRRORDSTPORT, mirrorEntity.getPortNo());
			response2 = snmpV2.snmpSet(vbsMap2);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// 出入口比率
			vbsMap3.put(OID.MIRROR_IDIV, mirrorEntity.getInbit());
			response3 = snmpV2.snmpSet(vbsMap3);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			vbsMap4.put(OID.MIRROR_EDIV, mirrorEntity.getOutbit());
			response4 = snmpV2.snmpSet(vbsMap4);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// // 被镜像端口
			boolean result5 = true;
			// if (mirrorEntity.getInports() != null
			// && mirrorEntity.getInports().trim().length() != 0) {
			if (mirrorEntity.getInports() != null) {
				String inports = mirrorEntity.getInports();
				if (inports.trim().length() == 0) {
					inports = "NULL";
				}
				vbsMap5.put(OID.MIRROR_ISRCPORT, inports);
				response5 = snmpV2.snmpSet(vbsMap5);
				result5 = checkResponse(response5);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			boolean result6 = true;
			// if (mirrorEntity.getOutports() != null
			// && mirrorEntity.getOutports().trim().length() != 0) {
			if (mirrorEntity.getOutports() != null) {
				String outports = mirrorEntity.getOutports();
				if (outports.trim().length() == 0) {
					outports = "NULL";
				}
				vbsMap6.put(OID.MIRROR_ESRCPORT, outports);
				response6 = snmpV2.snmpSet(vbsMap6);
				result6 = checkResponse(response6);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// mac监控模式
			vbsMap7.put(OID.MIRROR_IMAC, mirrorEntity.getInscanMac());
			response7 = snmpV2.snmpSet(vbsMap7);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			vbsMap8.put(OID.MIRROR_EMAC, mirrorEntity.getOutscanMac());
			response8 = snmpV2.snmpSet(vbsMap8);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			vbsMap9.put(OID.MIRROR_INGRESSMONITORODE, mirrorEntity
					.getScanInMode());
			response9 = snmpV2.snmpSet(vbsMap9);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			vbsMap10.put(OID.MIRROR_ENGRESSMONITORMODE, mirrorEntity
					.getScanOutMode());
			response10 = snmpV2.snmpSet(vbsMap10);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return (checkResponse(response1) && checkResponse(response2)
					&& checkResponse(response3) && checkResponse(response4)
					&& result5 && result6 && checkResponse(response7)
					&& checkResponse(response8) && checkResponse(response9) && checkResponse(response10));

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response1 != null) {
				response1.clear();
			}
			if (response2 != null) {
				response2.clear();
			}
			if (response3 != null) {
				response3.clear();
			}
			if (response4 != null) {
				response4.clear();
			}
			if (response5 != null) {
				response5.clear();
			}
			if (response6 != null) {
				response6.clear();
			}
			if (response7 != null) {
				response7.clear();
			}
			if (response8 != null) {
				response8.clear();
			}
			if (response9 != null) {
				response9.clear();
			}
			if (response10 != null) {
				response10.clear();
			}
			vbsMap1.clear();
			vbsMap2.clear();
			vbsMap3.clear();
			vbsMap4.clear();
			vbsMap5.clear();
			vbsMap6.clear();
			vbsMap7.clear();
			vbsMap8.clear();
			vbsMap9.clear();
			vbsMap10.clear();
		}
	}

	public MirrorEntity getMirrorConfig(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		Vector<String> vbs = new Vector<String>();
		vbs.add(OID.MIRRORDSTPORT);
		vbs.add(OID.MIRROR_ISRCPORT);
		vbs.add(OID.MIRROR_ESRCPORT);
		vbs.add(OID.MIRROR_INGRESSMONITORODE);
		vbs.add(OID.MIRROR_ENGRESSMONITORMODE);
		vbs.add(OID.MIRROR_IDIV);
		vbs.add(OID.MIRROR_EDIV);
		vbs.add(OID.MIRROR_IMAC);
		vbs.add(OID.MIRROR_EMAC);
		vbs.add(OID.MIRRORSTART);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(vbs);
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				MirrorEntity mirrorEntity = new MirrorEntity();
				// if (!isEmpty(responseVar.elementAt(0))) {
				// SwitchPortEntity switchPortEntity = new SwitchPortEntity();
				// switchPortEntity.setPortNO(Integer.parseInt(responseVar
				// .elementAt(0).toString()));
				// mirrorEntity.setCurrentPort(switchPortEntity);
				// }

				if (!isEmpty(responseVar.elementAt(0).getVariable())) {
					mirrorEntity.setPortNo(responseVar.elementAt(0)
							.getVariable().toInt());
				}
				if (!isEmpty(responseVar.elementAt(1).getVariable())) {
					// List<SwitchPortEntity> inPortList = new
					// ArrayList<SwitchPortEntity>();
					String[] inPortStr = responseVar.elementAt(1).getVariable()
							.toString().trim().split(" ");
					String inports = "";
					for (String inport : inPortStr) {
						// SwitchPortEntity switchPort = new SwitchPortEntity();
						// switchPort.setPortNO(Integer.parseInt(inport));
						// inPortList.add(switchPort);
						inports += inport + ",";
					}
					mirrorEntity.setInports(inports);
				}
				if (!isEmpty(responseVar.elementAt(2).getVariable())) {
					// List<SwitchPortEntity> exitPortList = new
					// ArrayList<SwitchPortEntity>();
					String[] exitPortStr = responseVar.elementAt(2)
							.getVariable().toString().trim().split(" ");
					String exitports = "";
					for (String exitport : exitPortStr) {
						// SwitchPortEntity switchPort = new SwitchPortEntity();
						// switchPort.setPortNO(Integer.parseInt(exitport));
						// exitPortList.add(switchPort);
						exitports += exitport + ",";
					}
					mirrorEntity.setOutports(exitports);
				}
				// if (!isEmpty(responseVar.elementAt(3))) {
				mirrorEntity.setScanInMode(responseVar.elementAt(3)
						.getVariable().toString());
				// }
				// if (!isEmpty(responseVar.elementAt(4))) {
				mirrorEntity.setScanOutMode(responseVar.elementAt(4)
						.getVariable().toString());
				// }
				mirrorEntity.setInbit(Integer.parseInt(responseVar.elementAt(5)
						.getVariable().toString()));
				mirrorEntity.setOutbit(Integer.parseInt(responseVar
						.elementAt(6).getVariable().toString()));
				if (!isEmpty(responseVar.elementAt(7).getVariable())) {
					mirrorEntity.setInscanMac(responseVar.elementAt(7)
							.getVariable().toString());
				}
				if (!isEmpty(responseVar.elementAt(8).getVariable())) {
					mirrorEntity.setOutscanMac(responseVar.elementAt(8)
							.getVariable().toString());
				}

				if (ENABLED.equalsIgnoreCase(responseVar.elementAt(9)
						.getVariable().toString().trim())) {
					mirrorEntity.setApplied(true);
				} else {
					mirrorEntity.setApplied(false);
				}

				return mirrorEntity;
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

	private void sleepTime(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Spring inject
	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}
}
