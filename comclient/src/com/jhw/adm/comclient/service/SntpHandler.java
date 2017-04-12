package com.jhw.adm.comclient.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.switchs.SNTPConfigEntity;

/**
 * SNTP
 * 
 * @author xiongbo
 * 
 */
public class SntpHandler extends BaseHandler {
	// private static Logger log = Logger.getLogger(SntpHandler.class);
	private AbstractSnmp snmpV2;
	//
	private Pattern ptn = Pattern
			.compile("\\s*(\\d{2})\\s*(\\d{2})\\s*(\\d{4})\\s*(\\d{2}:\\d{2}:\\d{2})");

	public boolean configSntp(String ip, SNTPConfigEntity sntpConfigEntity) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(5 * 1000);
		Map<String, Object> oidMap = new HashMap<String, Object>();
		PDU response = null;
		try {
			if (sntpConfigEntity.isApplied()) {
				oidMap.put(OID.SNTP, enable);
			} else {
				oidMap.put(OID.SNTP, disable);
			}
			if (!isEmpty(sntpConfigEntity.getFirstServerIP())) {
				oidMap.put(OID.SNTP_PRISERVER, sntpConfigEntity
						.getFirstServerIP());
			}
			if (!isEmpty(sntpConfigEntity.getSecondServerIP())) {
				oidMap.put(OID.SNTP_SECSERVER, sntpConfigEntity
						.getSecondServerIP());
			}
			oidMap.put(OID.SNTP_POLLINTERVAL, sntpConfigEntity.getBtSeconds());

			int index = sntpConfigEntity.getTimeArea().lastIndexOf(":");
			String timeArea = sntpConfigEntity.getTimeArea()
					.substring(1, index);
			timeArea = timeArea.replaceAll("GMT", "");
			// oidMap.put(OID.SNTP_TIMEZONE, 1);
			oidMap.put(OID.SNTP_TIMEZONE, timeArea);

			String time = sntpConfigEntity.getCurrentTime();
			String format = "dd MM yyyy HH:mm:ss";
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date date = null;
			try {
				date = sdf.parse(time);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Calendar cd = Calendar.getInstance();
			cd.setTime(date);
			int year = cd.get(Calendar.YEAR);
			int month = cd.get(Calendar.MONTH) + 1;
			int day = cd.get(Calendar.DATE);
			int hour = cd.get(Calendar.HOUR);
			int minute = cd.get(Calendar.MINUTE);
			int second = cd.get(Calendar.SECOND);
			String currentTime = day + "-" + month + "-" + year + "," + hour
					+ ":" + minute + ":" + second;
			oidMap.put(OID.SNTP_TIME, currentTime);

			response = snmpV2.snmpSet(oidMap);
			return checkResponse(response);
		} catch (RuntimeException e) {
			// log.error(getTraceMessage(e));
			e.printStackTrace();
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public SNTPConfigEntity getSntp(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(OID.SNTP);
		oids.add(OID.SNTP_PRISERVER);
		oids.add(OID.SNTP_SECSERVER);
		oids.add(OID.SNTP_POLLINTERVAL);
		oids.add(OID.SNTP_TIMEZONE);
		oids.add(OID.SNTP_TIME);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oids);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				SNTPConfigEntity sntpConfigEntity = new SNTPConfigEntity();
				if (ENABLED.equalsIgnoreCase(responseVar.elementAt(0)
						.getVariable().toString().trim())) {
					sntpConfigEntity.setApplied(true);
				} else {
					sntpConfigEntity.setApplied(false);
				}
				if (!isEmpty(responseVar.elementAt(1).getVariable())) {
					sntpConfigEntity.setFirstServerIP(responseVar.elementAt(1)
							.getVariable().toString());
				}
				if (!isEmpty(responseVar.elementAt(2).getVariable())) {
					sntpConfigEntity.setSecondServerIP(responseVar.elementAt(2)
							.getVariable().toString());
				}
				sntpConfigEntity.setBtSeconds(responseVar.elementAt(3)
						.getVariable().toInt());

				String timeZone = responseVar.elementAt(4).getVariable()
						.toString().trim();
				getTimeZone(timeZone);
				sntpConfigEntity.setTimeArea(timeZone);
				String time = responseVar.elementAt(5).getVariable().toString();
				time = time.replace("\r", "").replace("\n", "");
				// Matcher matcher = ptn.matcher(time);
				// if (matcher.find()) {
				// SimpleDateFormat df = new SimpleDateFormat(
				// "yyyy-MM-dd HH:mm:ss");
				// Date date = df.parse(time);
				// Date date = df.parse(matcher.group(3) + "-"
				// + matcher.group(1) + "-" + matcher.group(2) + " "
				// + matcher.group(4));
				sntpConfigEntity.setCurrentTime(time);
				// }

				return sntpConfigEntity;
			}
			return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} catch (Exception e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			oids.clear();
		}
	}

	private void getTimeZone(String timeZone) {
		timeZone = timeZone.substring(1, timeZone.indexOf(":"));
		String timeZone_hour = timeZone.substring(timeZone.length() - 2);
		timeZone = timeZone.substring(0, timeZone.length() - 2);
		int hour = Integer.parseInt(timeZone_hour);
		if (hour == 0) {
			timeZone = "GMT";
		} else {
			timeZone = timeZone + hour;
		}
	}

	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

}
