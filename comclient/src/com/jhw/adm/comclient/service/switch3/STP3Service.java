package com.jhw.adm.comclient.service.switch3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.Switch3OID;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.service.BaseService;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.level3.Switch3STPEntity;
import com.jhw.adm.server.entity.level3.Switch3STPPortEntity;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 
 * @author xiongbo
 * 
 */
public class STP3Service extends BaseService {
	private MessageSend messageSend;
	private AbstractSnmp snmpV2;
	private Vlan3Service vlan3Service;

	public Switch3STPEntity getSTP(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(5 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(Switch3OID.DOT1DSTPPROTOCOLSPECIFICATION);
		oids.add(Switch3OID.DOT1DSTPPRIORITY);
		oids.add(Switch3OID.DOT1DSTPBRIDGEMAXAGE);
		oids.add(Switch3OID.DOT1DSTPBRIDGEHELLOTIME);
		oids.add(Switch3OID.DOT1DSTPBRIDGEFORWARDDELAY);
		Switch3STPEntity stp = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oids);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				stp = new Switch3STPEntity();
				int stpType = responseVar.elementAt(0).getVariable().toInt();
				int s_TreePriority = responseVar.elementAt(1).getVariable()
						.toInt();
				int maxAgeTime = responseVar.elementAt(2).getVariable().toInt();
				int helloTime = responseVar.elementAt(3).getVariable().toInt();
				int forwardDelTime = responseVar.elementAt(4).getVariable()
						.toInt();
				stp.setStpType(stpType);
				stp.setS_TreePriority(s_TreePriority);
				stp.setMaxAgeTime(maxAgeTime);
				stp.setHelloTime(helloTime);
				stp.setForwardDelTime(forwardDelTime);
			}
			return stp;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return stp;
		} finally {
			if (response != null) {
				response.clear();
			}
			oids.clear();
		}
	}

	public Set<Switch3STPPortEntity> getSTPPortList(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(5 * 1000);
		String[] columnOIDs = { Switch3OID.DOT1DSTPPORT,
				Switch3OID.DOT1DSTPPORTENABLE, Switch3OID.DOT1DSTPPORTPRIORITY,
				Switch3OID.DOT1DSTPPORTPATHCOST,
				Switch3OID.DOT1DSTPPORTDESIGNATEDCOST };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Set<Switch3STPPortEntity> stpPortList = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				stpPortList = new HashSet<Switch3STPPortEntity>();

				Map<Integer, String> ifDescrMap = vlan3Service.getIfDescr(ip,
						community);
				if (ifDescrMap == null) {
					return null;
				}

				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							Switch3STPPortEntity stpPort = new Switch3STPPortEntity();
							int portID = variableBinding[i + 0].getVariable()
									.toInt();
							String portName = ifDescrMap.get(portID);
							stpPort.setPortID(portID);
							stpPort.setPortName(portName);
							int status = variableBinding[i + 1].getVariable()
									.toInt();
							stpPort.setStatus(status);
							int priorityLevel = variableBinding[i + 2]
									.getVariable().toInt();
							stpPort.setPriorityLevel(priorityLevel);
							int pathCost = variableBinding[i + 3].getVariable()
									.toInt();
							stpPort.setPathCost(pathCost);
							// TODO
							int edgePort = variableBinding[i + 4].getVariable()
									.toInt();
							stpPort.setEdgePort(edgePort);
							stpPortList.add(stpPort);
						}
					}
				}
			}
			return stpPortList;

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return stpPortList;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}

	}

	public void updateSTP(String ip, String client, String clientIp,
			Message message) {
		List<Switch3STPEntity> stps = getSTPs(message);
		if (stps != null) {
			Switch3STPEntity stp = stps.get(0);
			boolean result = updateSTP(ip, Configuration.three_layer_community,
					stp);
			ParmRep parmRep = handleSTP(stps, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3,
					parmRep, ip, client, clientIp);
		}
	}

	public void updateSTPPort(String ip, String client, String clientIp,
			Message message) {
		List<Switch3STPPortEntity> stpPorts = getSTPPorts(message);
		if (stpPorts != null) {
			boolean result = updateSTPPort(ip,
					Configuration.three_layer_community, stpPorts);
			ParmRep parmRep = handleSTPPort(stpPorts, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3,
					parmRep, ip, client, clientIp);
		}
	}

	private boolean updateSTP(String ip, String community, Switch3STPEntity stp) {
		if (stp == null) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(5 * 1000);

		Map<String, Object> vbsMap = new HashMap<String, Object>();
		// vbsMap.put(Switch3OID.DOT1DSTPPROTOCOLSPECIFICATION,
		// stp.getStpType());
		vbsMap.put(Switch3OID.DOT1DSTPPRIORITY, stp.getS_TreePriority());
		vbsMap.put(Switch3OID.DOT1DSTPBRIDGEMAXAGE, stp.getMaxAgeTime());
		vbsMap.put(Switch3OID.DOT1DSTPBRIDGEHELLOTIME, stp.getHelloTime());
		vbsMap.put(Switch3OID.DOT1DSTPBRIDGEFORWARDDELAY, stp
				.getForwardDelTime());
		PDU response = null;
		try {
			response = snmpV2.snmpSet(vbsMap);
			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbsMap.clear();
		}
	}

	private boolean updateSTPPort(String ip, String community,
			List<Switch3STPPortEntity> stpPortList) {
		if (stpPortList == null) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 5000);

		Map<String, Object> vbsMap = new HashMap<String, Object>();
		for (Switch3STPPortEntity stpPort : stpPortList) {
			vbsMap.put(Switch3OID.DOT1DSTPPORTENABLE + "."
					+ stpPort.getPortID(), stpPort.getStatus());
			vbsMap.put(Switch3OID.DOT1DSTPPORTPRIORITY + "."
					+ stpPort.getPortID(), stpPort.getPriorityLevel());
			vbsMap.put(Switch3OID.DOT1DSTPPORTPATHCOST + "."
					+ stpPort.getPortID(), stpPort.getPathCost());

			// vbsMap.put(Switch3OID.DOT1DSTPPORTDESIGNATEDCOST + "."
			// + stpPort.getPortID(), stpPort.getPriorityLevel());

			log.info(stpPort.getPortID() + " " + stpPort.getStatus() + " "
					+ stpPort.getPriorityLevel() + " " + stpPort.getPathCost()
					+ " " + stpPort.getEdgePort());
		}

		PDU response = null;
		try {
			response = snmpV2.snmpSet(vbsMap);
			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbsMap.clear();
		}
	}

	private ParmRep handleSTP(List<Switch3STPEntity> stps, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (Switch3STPEntity stp : stps) {
			parmIds.add(stp.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(Switch3STPEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<Switch3STPEntity> getSTPs(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<Switch3STPEntity> stps = null;
		try {
			stps = (List<Switch3STPEntity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return stps;
	}

	private ParmRep handleSTPPort(List<Switch3STPPortEntity> stpPorts,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (Switch3STPPortEntity stpPort : stpPorts) {
			parmIds.add(stpPort.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(Switch3STPPortEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<Switch3STPPortEntity> getSTPPorts(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<Switch3STPPortEntity> stpPorts = null;
		try {
			stpPorts = (List<Switch3STPPortEntity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return stpPorts;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

	public Vlan3Service getVlan3Service() {
		return vlan3Service;
	}

	public void setVlan3Service(Vlan3Service vlan3Service) {
		this.vlan3Service = vlan3Service;
	}

}
