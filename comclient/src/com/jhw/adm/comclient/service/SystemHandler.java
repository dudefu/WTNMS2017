package com.jhw.adm.comclient.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;

public class SystemHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(SystemHandler.class);
	private AbstractSnmp snmpV2;
	//
	private Map<String, String> macMap = new HashMap<String, String>();

	private final int SAVE_CONFIG = 1;
	private final String SAVE_OPERATION = "1.3.6.1.4.1.16001.2.23.4";
	private DataBufferBuilder dataBufferBuilder;

	/**
	 * Config ip,netmask,gateway and so on
	 */
	public boolean configIp(String ip, SwitchBaseConfig switchBaseConfig) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Map<String, Object> vbsMap = new HashMap<String, Object>();
		PDU response = null;
		try {
			vbsMap.put(OID.IP, switchBaseConfig.getIpValue());
			vbsMap.put(OID.NETMASK, switchBaseConfig.getMaskValue());
			vbsMap.put(OID.GATEWAY, switchBaseConfig.getNetGate());
			response = snmpV2.snmpSet(vbsMap);
			vbsMap.clear();
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

	public boolean configSysInfo(String ip, SwitchBaseInfo switchBaseInfo) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Map<String, Object> vbsMap = new HashMap<String, Object>();
		PDU response = null;
		try {
			vbsMap.put(OID.SYSCONTACT, switchBaseInfo.getContacts());
			vbsMap.put(OID.SYSNAME, switchBaseInfo.getDeviceName());
			vbsMap.put(OID.SYSLOCATION, switchBaseInfo.getAddress());
			response = snmpV2.snmpSet(vbsMap);
			vbsMap.clear();
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

	public SwitchBaseConfig getIp(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> vbs = new Vector<String>();
		vbs.add(OID.IP);
		vbs.add(OID.NETMASK);
		vbs.add(OID.GATEWAY);
		vbs.add(OID.DHCPSTATE);
		vbs.add(OID.PRIDNS);
		vbs.add(OID.SECDNS);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(vbs);
			if (response != null && SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response.getVariableBindings();
				SwitchBaseConfig switchBaseConfig = new SwitchBaseConfig();
				switchBaseConfig.setIpValue(responseVar.elementAt(0).getVariable().toString());
				switchBaseConfig.setMaskValue(responseVar.elementAt(1).getVariable().toString());
				switchBaseConfig.setNetGate(responseVar.elementAt(2).getVariable().toString());
				if (ENABLED.equalsIgnoreCase(responseVar.elementAt(3).getVariable().toString().trim())) {
					switchBaseConfig.setDhcpAyylied(true);
				} else {
					switchBaseConfig.setDhcpAyylied(false);
				}
				switchBaseConfig.setFirstDNS(responseVar.elementAt(4).getVariable().toString().trim());
				switchBaseConfig.setSecondDNS(responseVar.elementAt(5).getVariable().toString().trim());

				return switchBaseConfig;
			}
			return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbs.clear();
		}

	}

	public SwitchBaseInfo getSysInfo(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> vbs = new Vector<String>();
		vbs.add(OID.SYSCONTACT);
		vbs.add(OID.SYSNAME);
		vbs.add(OID.SYSLOCATION);
		vbs.add(OID.SYSBOOTTIME);
		vbs.add(OID.SYSSOFTVERSION);
		vbs.add(OID.SYSBOOTROMVERSION);
		vbs.add(OID.SYSMACADDR);
		vbs.add(OID.SYSVERSIONTIME);
		vbs.add(OID.SYSTIME);
		vbs.add(OID.CPUUsageRate);
		vbs.add(OID.MemoryUsageRate);
		vbs.add(OID.CURTemperature);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(vbs);
			// if (response != null
			// && SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
			// Vector<VariableBinding> responseVar = response
			// .getVariableBindings();
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				SwitchBaseInfo switchBaseInfo = new SwitchBaseInfo();
				switchBaseInfo.setContacts(responseVar.elementAt(0).getVariable().toString());
				if ("noSuchObject".equalsIgnoreCase(switchBaseInfo.getContacts())) {
					return null;
				}
				switchBaseInfo.setDeviceName(responseVar.elementAt(1).getVariable().toString());
				log.info("***系统类型: " + switchBaseInfo.getDeviceName());
				switchBaseInfo.setAddress(responseVar.elementAt(2).getVariable().toString());

				String bootTime = responseVar.elementAt(3).getVariable().toString();
				bootTime = bootTime.replace("\n", "").replace("\r", "");
				switchBaseInfo.setStartupTime(bootTime);

				switchBaseInfo.setCosVersion(responseVar.elementAt(4).getVariable().toString());
				switchBaseInfo.setBootromVersion(responseVar.elementAt(5).getVariable().toString());
				switchBaseInfo.setMacValue(responseVar.elementAt(6).getVariable().toString());
				// For Common invoke
				macMap.put(ip, switchBaseInfo.getMacValue());

				//
				String versionTime = responseVar.elementAt(7).getVariable().toString();
				switchBaseInfo.setCosVersionTime(versionTime);

				//
				String sysTime = responseVar.elementAt(8).getVariable().toString();
				sysTime = sysTime.replace("\n", "").replace("\r", "");
				switchBaseInfo.setCurrentTime(sysTime);

				switchBaseInfo.setCPUUsageRate(responseVar.elementAt(9).getVariable().toString());
				switchBaseInfo.setMemoryUsageRate(responseVar.elementAt(10).getVariable().toString());
				switchBaseInfo.setTemperature(responseVar.elementAt(11).getVariable().toString());

				return switchBaseInfo;
			}

			// else {
			return null;
			// }
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbs.clear();
		}

	}

	public boolean saveConfig(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			byte[] buf = dataBufferBuilder.saveConfig(SAVE_CONFIG);
			response = snmpV2.snmpSet(SAVE_OPERATION, buf);
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

	/**
	 * Common invoke
	 * 
	 * @param ip
	 * @return
	 */
	public String getMAC(String ip) {
		String mac = macMap.get(ip);
		if (mac != null) {
			return mac;
		} else {
			log.info("Query MAC");
			snmpV2.setAddress(ip, Constants.SNMP_PORT);
			snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
			snmpV2.setTimeout(2 * 1000);
			PDU response = null;
			try {
				response = snmpV2.snmpGet(OID.SYSMACADDR);
				if (response != null && SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
					Vector<VariableBinding> responseVar = response.getVariableBindings();
					String macAddr = responseVar.elementAt(0).getVariable().toString();

					macMap.put(ip, macAddr);

					return macAddr;
				} // else {
				return null;
				// }
			} catch (RuntimeException e) {
				log.error(getTraceMessage(e));
				return null;
			} finally {
				if (response != null) {
					response.clear();
				}
			}
		}
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	private Date strToDate(String str) {

		// SimpleDateFormat format = new
		// SimpleDateFormat("dd MM yyyy HH:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			date = new Date();
			log.error(getTraceMessage(e));
		}
		return date;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	private Date strToDate(String str, Locale US) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", US);
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Common invoke
	 * 
	 * @param ip
	 * @param mac
	 */
	public void putMAC(String ip, String mac) {
		macMap.put(ip, mac);
	}

	private void clear() {
		macMap.clear();
		macMap = null;
	}

	/**
	 * system upgrade
	 */
	public boolean sysUpgrade() {
		// TODO Auto-generated method stub
		return false;
	}

	// Spring inject
	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

	public void setDataBufferBuilder(DataBufferBuilder dataBufferBuilder) {
		this.dataBufferBuilder = dataBufferBuilder;
	}

}
