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
import com.jhw.adm.server.entity.switchs.SwitchUser;

public class SwitcherUserHandle extends BaseHandler {
	private static Logger log = Logger.getLogger(SwitcherUserHandle.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;

	public List<SwitchUser> getSwitcherUser(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		Vector<String> oids = null;
		try {
			response = snmpV2.snmpGet(OID.SWITCHER_USER_NUMBER);
			int userNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			List<SwitchUser> switchUsers = null;
			if (responseVar != null) {
				switchUsers = new ArrayList<SwitchUser>();
				userNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (userNum == 0) {
				return switchUsers;
			}

			oids = new Vector<String>();
			oids.add(OID.SWITCHER_USER_NAME);
			oids.add(OID.SWITCHER_USER_ROLE);
			snmpV2.setTimeout(3 * 1000);

			response = snmpV2.snmpGetBulk(oids, 0, userNum);
			responseVar = checkResponseVar(response);

			if (responseVar != null) {
				int vbsSize = responseVar.size();

				for (int i = 0; i < vbsSize; i++) {
					if (i % 2 == 0) {
						SwitchUser switchUser = new SwitchUser();
						switchUser.setUserName(responseVar.get(i + 0)
								.getVariable().toString().trim());
						switchUser.setRole(responseVar.get(i + 1).getVariable()
								.toString().trim());
						switchUsers.add(switchUser);
					}
				}
			}
			return switchUsers;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (oids != null) {
				oids.clear();
			}
			if (response != null) {
				response.clear();
			}
		}
	}

	public synchronized boolean addUser(SwitchUser switchUser) {
		PDU response = null;
		try {
			String ipValue = switchUser.getSwitchNode().getBaseConfig()
					.getIpValue();
			snmpV2.setAddress(ipValue, Constants.SNMP_PORT);
			snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
			snmpV2.setTimeout(2 * 1000);

			String name = switchUser.getUserName();
			String pwd = switchUser.getNewPwd();

			int level = 0;
			String roleStr = switchUser.getRole();
			if ("administrators".equalsIgnoreCase(roleStr)) {
				level = 0;
			} else if ("users".equalsIgnoreCase(roleStr)) {
				level = 1;
			}
			byte[] dataBuffer = dataBufferBuilder.switcherUserAdd(level, name,
					pwd);
			response = snmpV2.snmpSet(OID.SWITCHER_USER_BATCH_OPERATE,
					dataBuffer);
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

	public synchronized boolean deleteUser(SwitchUser switchUser) {
		PDU response = null;
		try {
			String ipValue = switchUser.getSwitchNode().getBaseConfig()
					.getIpValue();
			snmpV2.setAddress(ipValue, Constants.SNMP_PORT);
			snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
			snmpV2.setTimeout(3 * 1000);

			String name = switchUser.getUserName();
			String pwd = "1";

			int level = 0;
			String roleStr = switchUser.getRole();
			if ("administrators".equalsIgnoreCase(roleStr)) {
				level = 0;
			} else if ("users".equals(roleStr)) {
				level = 1;
			}
			byte[] dataBuffer = dataBufferBuilder.switcherUserDel(level, name,
					pwd);
			response = snmpV2.snmpSet(OID.SWITCHER_USER_BATCH_OPERATE,
					dataBuffer);
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

	public synchronized boolean modifyUserPwd(SwitchUser switchUser) {
		PDU response = null;
		try {
			String ipValue = switchUser.getSwitchNode().getBaseConfig()
					.getIpValue();
			snmpV2.setAddress(ipValue, Constants.SNMP_PORT);
			snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
			snmpV2.setTimeout(2 * 1000);

			String name = switchUser.getUserName();
			String oldPwd = switchUser.getPassword();
			String newPwd = switchUser.getNewPwd();

			byte[] dataBuffer = dataBufferBuilder.switcherUserModify(name,
					oldPwd, newPwd);
			response = snmpV2.snmpSet(OID.SWITCHER_USER_BATCH_OPERATE,
					dataBuffer);
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
