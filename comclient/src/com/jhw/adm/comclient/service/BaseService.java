package com.jhw.adm.comclient.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.server.entity.message.SynchDevice;

/**
 * 
 * @author xiongbo
 * 
 */
public class BaseService {
	public final Logger log = Logger.getLogger(this.getClass().getName());
	//
	public final String SUCCESS = "S";
	public final String FAIL = "F";
	//
	public final String INSERT = "I";
	public final String UPDATE = "U";
	public final String DELETE = "D";

	public final static int PING_SUCCESS = 1;
	public final static int PING_BLOCK = 0;

	public String getTrace(Exception e) {
		StackTraceElement[] ste = e.getStackTrace();
		StringBuffer sb = new StringBuffer();
		sb.append(e + "\n");
		for (int i = 0; i < ste.length; i++) {
			sb.append(ste[i].toString() + "\n");
		}
		return sb.toString();
	}

	public String getTraceMessage(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		e.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}

	public Vector<VariableBinding> checkResponseVar(PDU response) {
		if (response == null) {
			return null;
		}
		if ("Success".equalsIgnoreCase(response.getErrorStatusText())) {
			Vector<VariableBinding> responseVar = response
					.getVariableBindings();
			return responseVar;
		}
		return null;
	}

	/**
	 * 
	 * @param response
	 * @return
	 */
	public boolean checkResponse(PDU response) {
		if (response == null) {
			return false;
		}
		if ("Success".equalsIgnoreCase(response.getErrorStatusText())) {
			return true;
		}
		return false;
	}

	protected Set<SynchDevice> getSingleSynchSet(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		Set<SynchDevice> synchDeviceSet = null;
		try {
			synchDeviceSet = (Set<SynchDevice>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return synchDeviceSet;
	}

	protected void saveConfig(final String ip, final SystemHandler systemHandler) {
		if (ip == null || systemHandler == null) {
			log.error("ip/SystemHandler NULL, when save config!");
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				log.info("Save device config Result£º"
						+ systemHandler.saveConfig(ip));
			}
		});
	}
}
