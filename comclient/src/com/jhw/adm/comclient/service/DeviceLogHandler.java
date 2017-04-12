package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.switchs.SysLogHostEntity;
import com.jhw.adm.server.entity.switchs.SysLogHostToDevEntity;

/**
 * 
 * @author xiongbo
 * 
 */
public class DeviceLogHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(DeviceLogHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;

	private final String INDEX = "1.3.6.1.4.1.44405.71.2.17.2.1.1";
	private final String STATE = "1.3.6.1.4.1.44405.71.2.17.2.1.2";
	private final String ADDRESS = "1.3.6.1.4.1.44405.71.2.17.2.1.3";
	private final String PORT = "1.3.6.1.4.1.44405.71.2.17.2.1.4";
	//
	private final String CONFIG_OPERATION = "1.3.6.1.4.1.44405.71.2.17.3";
	private final int ADD = 1;
	private final int DEL = 2;

	public List<SysLogHostToDevEntity> getSyslogConfig(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		String[] columnOIDs = { INDEX, STATE, ADDRESS, PORT };
		List<TableEvent> tableEventList = null;
		List<SysLogHostToDevEntity> syslogConfigSet = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				syslogConfigSet = new ArrayList<SysLogHostToDevEntity>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int indx = variableBinding[0].getVariable().toInt();
					String stat = variableBinding[1].getVariable().toString();
					String logAddress = variableBinding[2].getVariable()
							.toString();
					int logPort = variableBinding[3].getVariable().toInt();
					if ("enable".equalsIgnoreCase(stat.trim())) {
						SysLogHostToDevEntity sysLogHostToDevEntity = new SysLogHostToDevEntity();
						sysLogHostToDevEntity.setIpValue(ip);
						sysLogHostToDevEntity.setHostID(indx);
						// if ("enable".equalsIgnoreCase(stat)) {
						sysLogHostToDevEntity.setHostMode(1);
						// }

						SysLogHostEntity sysLogHostEntity = new SysLogHostEntity();
						sysLogHostEntity.setHostIp(logAddress);
						sysLogHostEntity.setHostPort(logPort);

						sysLogHostToDevEntity
								.setSysLogHostEntity(sysLogHostEntity);
						syslogConfigSet.add(sysLogHostToDevEntity);
					}
				}
			}
			return syslogConfigSet;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	/**
	 * If have same hostid,it will be cover the hostid
	 */
	public boolean createSyslogServer(String ip,
			SysLogHostToDevEntity sysLogHostToDevEntity) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			byte[] buf = dataBufferBuilder.syslogServerConfig(ADD,
					sysLogHostToDevEntity.getHostID(), sysLogHostToDevEntity
							.getSysLogHostEntity().getHostIp(),
					sysLogHostToDevEntity.getSysLogHostEntity().getHostPort());
			response = snmpV2.snmpSet(CONFIG_OPERATION, buf);
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

	public boolean deleteSyslogServer(String ip,
			SysLogHostToDevEntity sysLogHostToDevEntity) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			byte[] buf = dataBufferBuilder.syslogServerConfig(DEL,
					sysLogHostToDevEntity.getHostID(), sysLogHostToDevEntity
							.getSysLogHostEntity().getHostIp(),
					sysLogHostToDevEntity.getSysLogHostEntity().getHostPort());
			response = snmpV2.snmpSet(CONFIG_OPERATION, buf);
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

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

	public void setDataBufferBuilder(DataBufferBuilder dataBufferBuilder) {
		this.dataBufferBuilder = dataBufferBuilder;
	}

}
