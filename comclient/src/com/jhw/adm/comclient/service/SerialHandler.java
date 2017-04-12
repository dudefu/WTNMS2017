package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.util.SwitchSerialPort;

/**
 * ´®¿Ú
 * 
 * @author xiongbo
 * 
 */
public class SerialHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(SerialHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	//
	private final String[] serialMode = { "tcpserver", "tcpclient",
			"udpserver", "udpclient" };

	public List<SwitchSerialPort> getSerialConfig(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oids = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.SERIALSNUM);
			int serialsNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			List<SwitchSerialPort> switchSerials = null;
			if (responseVar != null) {
				switchSerials = new ArrayList<SwitchSerialPort>();
				serialsNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (serialsNum == 0) {
				return switchSerials;
			}
			oids = new Vector<String>();
			oids.add(OID.SERIALINDEX);
			oids.add(OID.SERIALNAME);
			oids.add(OID.SERIALBAUDRATE);
			oids.add(OID.SERIALDATABITS);
			oids.add(OID.SERIALPARITY);
			oids.add(OID.SERIALSTOPBITS);
			oids.add(OID.SERIALMODE);
			oids.add(OID.SERIAL_TCPCLIENT);
			oids.add(OID.SERIAL_UDPCLIENT);
			oids.add(OID.SERIAL_TCPSERVER);
			oids.add(OID.SERIAL_UDPSERVER);
			response = snmpV2.snmpGetBulk(oids, 0, serialsNum);
			responseVar = checkResponseVar(response);
			// if (responseVar != null) {
			// int vbsSize = responseVar.size();
			// for (int i = 0; i < vbsSize; i++) {
			// // 11 is size of oid
			// if (i % 11 == 0) {
			// SwitchSerialPort switchSerialPort = new SwitchSerialPort();
			// switchSerialPort.setPortNo(responseVar.elementAt(i + 0)
			// .getVariable().toInt());
			// Object portObj = responseVar.elementAt(i + 1)
			// .getVariable();
			// switchSerialPort.setPortName(portObj != null ? portObj
			// .toString().trim() : null);
			// switchSerialPort.setBaudRate((float) responseVar
			// .elementAt(i + 2).getVariable().toInt());
			// switchSerialPort.setDatabit(responseVar
			// .elementAt(i + 3).getVariable().toInt());
			// // TODO
			// switchSerialPort.setCheckbit(responseVar.elementAt(
			// i + 4).getVariable().toInt());
			//
			// switchSerialPort.setStopbit(responseVar
			// .elementAt(i + 5).getVariable().toInt());
			// String serialMode = responseVar.elementAt(i + 6)
			// .getVariable().toString();
			// if ("tcp server".equalsIgnoreCase(serialMode.trim())) {
			// switchSerialPort.setSerialMode(0);
			// } else if ("tcp client".equalsIgnoreCase(serialMode
			// .trim())) {
			// switchSerialPort.setSerialMode(1);
			// } else if ("udp server".equalsIgnoreCase(serialMode
			// .trim())) {
			// switchSerialPort.setSerialMode(2);
			// } else {
			// switchSerialPort.setSerialMode(3);
			// }
			// // tcpclient
			// String tcpclient = responseVar.elementAt(i + 7)
			// .getVariable().toString().trim();
			// String[] tcpclients = tcpclient.split(" ");
			// switchSerialPort.setTcpclientRemoteIP(tcpclients[0]);
			// switchSerialPort.setTcpclientRemotePort(tcpclients[1]);
			//
			// // udpclient
			// String udpclient = responseVar.elementAt(i + 8)
			// .getVariable().toString().trim();
			// String[] udpclients = udpclient.split(" ");
			// switchSerialPort.setUdpclientRemoteIp(udpclients[0]);
			// switchSerialPort.setUdpclientRemotePort(udpclients[1]);
			// // tcpserver
			// switchSerialPort.setTcpserverLocalPort(responseVar
			// .elementAt(i + 9).getVariable().toString()
			// .trim());
			// // udpserver
			// switchSerialPort.setUdpserverLocalPort(responseVar
			// .elementAt(i + 10).getVariable().toString()
			// .trim());
			//
			// switchSerials.add(switchSerialPort);
			// }
			// }
			// return switchSerials;
			// }
			return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (oids != null) {
				oids.clear();
			}
		}
	}

	public boolean configSerial(String ip, SwitchSerialPort switchSerialPort) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			int port = switchSerialPort.getPortNo();

			byte[] buf = dataBufferBuilder.serialConfig_string(port, 1,
					switchSerialPort.getPortName());
			response = snmpV2.snmpSet(OID.SERIAL_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}

			buf = dataBufferBuilder.serialConfig_int(port, 2,
					(int) switchSerialPort.getBaudRate());
			response = snmpV2.snmpSet(OID.SERIAL_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}

			buf = dataBufferBuilder.serialConfig_int(port, 3, switchSerialPort
					.getDatabit());
			response = snmpV2.snmpSet(OID.SERIAL_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}

			buf = dataBufferBuilder.serialConfig_string(port, 4,
					serialMode[switchSerialPort.getSerialMode()]);
			response = snmpV2.snmpSet(OID.SERIAL_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}

			buf = dataBufferBuilder.serialConfig_int(port, 5, switchSerialPort
					.getCheckbit());
			response = snmpV2.snmpSet(OID.SERIAL_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}

			buf = dataBufferBuilder.serialConfig_int(port, 6, switchSerialPort
					.getStopbit());
			response = snmpV2.snmpSet(OID.SERIAL_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}

			buf = dataBufferBuilder
					.serialConfig_client(port, 7, switchSerialPort
							.getTcpclientRemoteIP(),
							Integer.parseInt(switchSerialPort
									.getTcpclientRemotePort()));
			response = snmpV2.snmpSet(OID.SERIAL_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}

			buf = dataBufferBuilder.serialConfig_int(port, 8, Integer
					.parseInt(switchSerialPort.getTcpserverLocalPort()));
			response = snmpV2.snmpSet(OID.SERIAL_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}

			buf = dataBufferBuilder
					.serialConfig_client(port, 9, switchSerialPort
							.getUdpclientRemoteIp(),
							Integer.parseInt(switchSerialPort
									.getUdpclientRemotePort()));
			response = snmpV2.snmpSet(OID.SERIAL_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}

			buf = dataBufferBuilder.serialConfig_int(port, 10, Integer
					.parseInt(switchSerialPort.getUdpserverLocalPort()));
			response = snmpV2.snmpSet(OID.SERIAL_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}
			return true;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

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

}
