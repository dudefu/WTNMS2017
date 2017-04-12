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
import com.jhw.adm.server.entity.level3.Switch3LLDPPortEntity;
import com.jhw.adm.server.entity.level3.Switch3LLDP_PEntity;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 
 * @author xiongbo
 * 
 */
public class LLDP3Service extends BaseService {
	private MessageSend messageSend;
	private AbstractSnmp snmpV2;
	private Vlan3Service vlan3Service;

	public Switch3LLDP_PEntity getLLDP(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(5 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(Switch3OID.LLDPMESSAGETXINTERVAL);
		oids.add(Switch3OID.LLDPREINITDELAY);
		Switch3LLDP_PEntity lldp = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oids);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				lldp = new Switch3LLDP_PEntity();
				int sendCycle = responseVar.elementAt(0).getVariable().toInt();
				int reinit = responseVar.elementAt(1).getVariable().toInt();
				lldp.setSendCycle(sendCycle);
				lldp.setReinit(reinit);
			}
			return lldp;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return lldp;
		} finally {
			if (response != null) {
				response.clear();
			}
			oids.clear();
		}
	}

	public Set<Switch3LLDPPortEntity> getLLDPPortList(String ip,
			String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(5 * 1000);
		String[] columnOIDs = { Switch3OID.LLDPPORTCONFIGPORTNUM,
				Switch3OID.LLDPPORTCONFIGADMINSTATUS };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Set<Switch3LLDPPortEntity> lldpPortList = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				lldpPortList = new HashSet<Switch3LLDPPortEntity>();

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
							Switch3LLDPPortEntity lldpPort = new Switch3LLDPPortEntity();
							int portIndex = variableBinding[i + 0]
									.getVariable().toInt();
							lldpPort.setPortIndex(portIndex);
							String portName = ifDescrMap.get(portIndex);
							lldpPort.setPortName(portName);
							int lLDPPacket = variableBinding[i + 1]
									.getVariable().toInt();
							lldpPort.setlLDPPacket(lLDPPacket);

							lldpPortList.add(lldpPort);
						}
					}
				}
			}
			return lldpPortList;

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return lldpPortList;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}

	}

	public void updateLLDP(String ip, String client, String clientIp,
			Message message) {
		List<Switch3LLDP_PEntity> lldps = getLLDPs(message);
		if (lldps != null) {
			Switch3LLDP_PEntity lldp = lldps.get(0);
			boolean result = updateLLDP(ip,
					Configuration.three_layer_community, lldp);
			ParmRep parmRep = handleLLDP(lldps, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3,
					parmRep, ip, client, clientIp);
		}
	}

	public void updateLLDPPort(String ip, String client, String clientIp,
			Message message) {
		List<Switch3LLDPPortEntity> lldpPorts = getLLDPPorts(message);
		if (lldpPorts != null) {
			boolean result = updateLLDPPort(ip,
					Configuration.three_layer_community, lldpPorts);
			ParmRep parmRep = handleLLDPPort(lldpPorts, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3,
					parmRep, ip, client, clientIp);
		}
	}

	private boolean updateLLDP(String ip, String community,
			Switch3LLDP_PEntity lldp) {
		if (lldp == null) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 5000);

		Map<String, Object> vbsMap = new HashMap<String, Object>();

		vbsMap.put(Switch3OID.LLDPMESSAGETXINTERVAL, lldp.getSendCycle());
		vbsMap.put(Switch3OID.LLDPREINITDELAY, lldp.getReinit());

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

	private boolean updateLLDPPort(String ip, String community,
			List<Switch3LLDPPortEntity> lldpPortList) {
		if (lldpPortList == null) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 5000);

		Map<String, Object> vbsMap = new HashMap<String, Object>();
		for (Switch3LLDPPortEntity lldpPort : lldpPortList) {
			vbsMap.put(Switch3OID.LLDPPORTCONFIGADMINSTATUS + "."
					+ lldpPort.getPortIndex(), lldpPort.getlLDPPacket());
			log.info(lldpPort.getPortIndex() + " " + lldpPort.getlLDPPacket());
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

	private ParmRep handleLLDP(List<Switch3LLDP_PEntity> lldps, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (Switch3LLDP_PEntity lldp : lldps) {
			parmIds.add(lldp.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(Switch3LLDP_PEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<Switch3LLDP_PEntity> getLLDPs(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<Switch3LLDP_PEntity> lldps = null;
		try {
			lldps = (List<Switch3LLDP_PEntity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return lldps;
	}

	private ParmRep handleLLDPPort(List<Switch3LLDPPortEntity> lldpPorts,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (Switch3LLDPPortEntity lldpPort : lldpPorts) {
			parmIds.add(lldpPort.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(Switch3LLDPPortEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<Switch3LLDPPortEntity> getLLDPPorts(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<Switch3LLDPPortEntity> lldpPorts = null;
		try {
			lldpPorts = (List<Switch3LLDPPortEntity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return lldpPorts;
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
