package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.ports.PC8021x;
import com.jhw.adm.server.entity.ports.SC8021x;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 802.1x
 * 
 * @author xiongbo
 * 
 */
public class Dot1xService extends BaseService {
	private static Logger log = Logger.getLogger(Dot1xService.class);
	private MessageSend messageSend;
	private Dot1xHandler dot1xHandler;

	public void dot1xSysConfig(String ip, String client, Message message) {

	}

	public void dot1xPortConfig(String ip, String client, Message message) {

	}

	public void getDot1xSysConfig(String ip, String client, Message message) {

	}

	public void getDot1xPortConfig(String ip, String client, Message message) {

	}

	private List<PC8021x> getPC8021xs(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<PC8021x> pc8021xs = null;
		try {
			pc8021xs = (List<PC8021x>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return pc8021xs;
	}

	private SC8021x getSC8021x(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		SC8021x sc8021x = null;
		try {
			sc8021x = (SC8021x) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return sc8021x;
	}

	private ParmRep handlePC8021x(List<PC8021x> pc8021xs, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (PC8021x pc8021x : pc8021xs) {
			parmIds.add(pc8021x.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(PC8021x.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleSC8021x(List<SC8021x> sc8021xs, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (SC8021x sc8021x : sc8021xs) {
			parmIds.add(sc8021x.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(SC8021x.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public Dot1xHandler getDot1xHandler() {
		return dot1xHandler;
	}

	public void setDot1xHandler(Dot1xHandler dot1xHandler) {
		this.dot1xHandler = dot1xHandler;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

}
