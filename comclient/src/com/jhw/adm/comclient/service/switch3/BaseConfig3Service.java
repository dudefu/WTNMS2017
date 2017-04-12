package com.jhw.adm.comclient.service.switch3;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.snmp4j.PDU;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.Switch3OID;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.service.BaseService;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 
 * @author xiongbo
 * 
 */
public class BaseConfig3Service extends BaseService {
	private MessageSend messageSend;
	private AbstractSnmp snmpV2;

	public void updateBase(String ip, String client, String clientIp,
			Message message) {
		List<SwitchLayer3> bases = getBases(message);
		if (bases != null) {
			SwitchLayer3 base = bases.get(0);
			boolean result = updateBase(ip,
					Configuration.three_layer_community, base);
			ParmRep parmRep = handleBase(bases, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3,
					parmRep, ip, client, clientIp);
		}
	}

	private ParmRep handleBase(List<SwitchLayer3> bases, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (SwitchLayer3 base : bases) {
			parmIds.add(base.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(SwitchLayer3.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<SwitchLayer3> getBases(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<SwitchLayer3> vlans = null;
		try {
			vlans = (List<SwitchLayer3>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return vlans;
	}

	private boolean updateBase(String ip, String community, SwitchLayer3 base) {
		if (base.getName() == null) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		PDU response = null;
		try {
			response = snmpV2.snmpSet(Switch3OID.SYSNAME, base.getName());
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

}
