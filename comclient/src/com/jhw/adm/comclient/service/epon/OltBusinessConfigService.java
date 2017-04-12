package com.jhw.adm.comclient.service.epon;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.service.BaseService;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.epon.OLTDBA;
import com.jhw.adm.server.entity.epon.OLTSTP;
import com.jhw.adm.server.entity.epon.OLTVlan;
import com.jhw.adm.server.entity.epon.ONULLID;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 
 * @author xiongbo
 * 
 */
public class OltBusinessConfigService extends BaseService {
	private MessageSend messageSend;
	private OltBusinessConfigHandler oltBusinessConfigHandler;

	public void createOLTVlan(String ip, String client, String clientIp,
			Message message) {
		List<OLTVlan> oltVlans = getOLTVlans(message);
		if (oltVlans != null) {
			OLTVlan oltVlan = oltVlans.get(0);
			boolean result = oltBusinessConfigHandler.createVlan(ip,
					Configuration.olt_community, oltVlan);
			ParmRep parmRep = handleOLTVlan(oltVlans, result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_OLT, parmRep,
					ip, client, clientIp);
		}
	}

	public void deleteOLTVlan(String ip, String client, String clientIp,
			Message message) {
		List<OLTVlan> oltVlans = getOLTVlans(message);
		if (oltVlans != null) {
			OLTVlan oltVlan = oltVlans.get(0);
			boolean result = oltBusinessConfigHandler.deleteVlan(ip,
					Configuration.olt_community, oltVlan);
			ParmRep parmRep = handleOLTVlan(oltVlans, result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_OLT, parmRep,
					ip, client, clientIp);
		}
	}

	public void configDBA(String ip, String client, String clientIp,
			Message message) {
		List<OLTDBA> oltDBAs = getOLTDBAs(message);
		if (oltDBAs != null) {
			OLTDBA oltDBA = oltDBAs.get(0);
			boolean result = oltBusinessConfigHandler.configDBA(ip,
					Configuration.olt_community, oltDBA);
			ParmRep parmRep = handleOLTDBA(oltDBAs, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_OLT, parmRep,
					ip, client, clientIp);
		}
	}

	public void configSTP(String ip, String client, String clientIp,
			Message message) {
		List<OLTSTP> oltSTPs = getOLTSTPs(message);
		if (oltSTPs != null) {
			OLTSTP oltSTP = oltSTPs.get(0);
			boolean result = oltBusinessConfigHandler.configSTP(ip,
					Configuration.olt_community, oltSTP);
			ParmRep parmRep = handleOLTSTP(oltSTPs, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_OLT, parmRep,
					ip, client, clientIp);
		}
	}

	public void configLLID(String ip, String client, String clientIp,
			Message message) {
		List<ONULLID> onuLLIDs = this.getONULLIDs(message);
		if (onuLLIDs != null) {
			ONULLID onuLLID = onuLLIDs.get(0);
			boolean result = oltBusinessConfigHandler.configLLID(ip,
					Configuration.olt_community, onuLLID);
			ParmRep parmRep = handleONULLID(onuLLIDs, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_OLT, parmRep,
					ip, client, clientIp);
		}
	}

	private ParmRep handleOLTVlan(List<OLTVlan> oltVlans, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (OLTVlan oltVlan : oltVlans) {
			parmIds.add(oltVlan.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(OLTVlan.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<OLTVlan> getOLTVlans(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<OLTVlan> oltVlans = null;
		try {
			oltVlans = (List<OLTVlan>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return oltVlans;
	}

	private List<OLTDBA> getOLTDBAs(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<OLTDBA> oltDBAs = null;
		try {
			oltDBAs = (List<OLTDBA>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return oltDBAs;
	}

	private ParmRep handleOLTDBA(List<OLTDBA> oltDBAs, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (OLTDBA oltDBA : oltDBAs) {
			parmIds.add(oltDBA.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(OLTDBA.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<OLTSTP> getOLTSTPs(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<OLTSTP> oltSTPs = null;
		try {
			oltSTPs = (List<OLTSTP>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return oltSTPs;
	}

	private ParmRep handleOLTSTP(List<OLTSTP> oltSTPs, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (OLTSTP oltSTP : oltSTPs) {
			parmIds.add(oltSTP.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(OLTSTP.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<ONULLID> getONULLIDs(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<ONULLID> onuLLIDs = null;
		try {
			onuLLIDs = (List<ONULLID>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return onuLLIDs;
	}

	private ParmRep handleONULLID(List<ONULLID> onuLLIDs, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (ONULLID onuLLID : onuLLIDs) {
			parmIds.add(onuLLID.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(ONULLID.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public OltBusinessConfigHandler getOltBusinessConfigHandler() {
		return oltBusinessConfigHandler;
	}

	public void setOltBusinessConfigHandler(
			OltBusinessConfigHandler oltBusinessConfigHandler) {
		this.oltBusinessConfigHandler = oltBusinessConfigHandler;
	}

}
