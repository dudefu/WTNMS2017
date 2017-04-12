package com.jhw.adm.comclient.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

/**
 * 
 * @author xiongbo
 * 
 */
public class BaseHandler {
	public final Logger log = Logger.getLogger(this.getClass().getName());

	public final String SUCCESS = "Success";

	public final String DISABLED = "Disabled";
	public final String ENABLED = "Enabled";
	public final String disable = "disable";
	public final String enable = "enable";
	public final String TRUE = "true";
	public final String FALSE = "false";
	public final String AUTO = "auto";
	// public final String MULTICAST_PREFIX_COLON = "01:00:5E";
	// public final String MULTICAST_PREFIX_SEPARATE = "01-00-5E";
	public final String MULTICAST_PREFIX_COLON = "01:";
	public final String MULTICAST_PREFIX_SEPARATE = "01-";

	/**
	 * 
	 * @param response
	 * @return
	 */
	public boolean checkResponse(PDU response) {
		if (response == null) {
			return false;
		}
		if (SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
			return true;
		}
		return false;
	}

	public Vector<VariableBinding> checkResponseVar(PDU response) {
		if (response == null) {
			return null;
		}
		if (SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
			Vector<VariableBinding> responseVar = response
					.getVariableBindings();
			return responseVar;
		}
		return null;
	}

	public boolean isEmpty(Object obj) {
		return null == obj || obj.toString().trim().length() == 0;
	}

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

}
