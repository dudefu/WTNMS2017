package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.switchs.SNMPGroup;
import com.jhw.adm.server.entity.switchs.SNMPHost;
import com.jhw.adm.server.entity.switchs.SNMPMass;
import com.jhw.adm.server.entity.switchs.SNMPUser;
import com.jhw.adm.server.entity.switchs.SNMPView;

/**
 * 
 * @author xiongbo
 * 
 */
public class SnmpHandler extends BaseHandler {
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;

	public boolean configSnmpHost(String ip, SNMPHost snmpHost) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			byte[] dataBuffer = dataBufferBuilder.snmpHostConfig(snmpHost
					.getHostIp(), snmpHost.getSnmpVersion().toLowerCase(),
					snmpHost.getMassName());
			// Object obj = snmpHost.getHostIp();
			// response = snmpV2.snmpSet(OID.SNMP_HOST_ADDR + "." + 4.0, obj);
			response = snmpV2.snmpSet(OID.SNMP_HOST_OPERAT, dataBuffer);

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

	public boolean deleteSnmpHost(String ip, SNMPHost snmpHost) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			byte[] dataBuffer = dataBufferBuilder.snmpHostDel(snmpHost
					.getHostIp(), snmpHost.getSnmpVersion(), snmpHost
					.getMassName());
			response = snmpV2.snmpSet(OID.SNMP_BATCH_OPERATE, dataBuffer);

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

	public String getSnmpEngineId(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.SNMP_ENGINEID);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			String snmpEngineId = null;
			if (responseVar != null) {
				snmpEngineId = responseVar.get(0).getVariable().toString();
			}
			return snmpEngineId;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public List<SNMPView> getSnmpViews(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		Vector<String> oids = null;
		try {
			response = snmpV2.snmpGet(OID.SNMP_VIEWNUM);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			int snmpViewNum = 0;
			List<SNMPView> snmpViews = null;
			if (responseVar != null) {
				snmpViews = new ArrayList<SNMPView>();
				snmpViewNum = responseVar.get(0).getVariable().toInt();
			}
			if (snmpViewNum == 0) {
				return null;
			}
			oids = new Vector<String>();
			oids.add(OID.SNMP_VIEW_NAME);
			oids.add(OID.SNMP_VIEW_SUBTREE);
			oids.add(OID.SNMP_VIEW_TYPE);
			snmpV2.setTimeout(3 * 1000);
			response = snmpV2.snmpGetBulk(oids, 0, snmpViewNum);
			responseVar = checkResponseVar(response);

			if (responseVar != null) {
				int vbsSize = responseVar.size();

				for (int i = 0; i < vbsSize; i++) {
					if (i % 3 == 0) {
						SNMPView snmpView = new SNMPView();
						snmpView.setViewName(responseVar.get(i + 0)
								.getVariable().toString().trim());
						snmpView.setOIDName(responseVar.get(i + 1)
								.getVariable().toString().trim());
						snmpView.setViewType(responseVar.get(i + 2)
								.getVariable().toString().trim());
						snmpViews.add(snmpView);
					}
				}

			}
			return snmpViews;
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

	public List<SNMPMass> getSnmpComms(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		Vector<String> oids = null;
		try {
			response = snmpV2.snmpGet(OID.SNMP_COMMNUM);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			int snmpCommNum = 0;
			List<SNMPMass> snmpComms = null;
			if (responseVar != null) {
				snmpComms = new ArrayList<SNMPMass>();
				snmpCommNum = responseVar.get(0).getVariable().toInt();
			}
			if (snmpCommNum == 0) {
				return null;
			}
			oids = new Vector<String>();
			oids.add(OID.SNMP_COMM_NAME);
			oids.add(OID.SNMP_ACCESS_VIEW_NAME);
			oids.add(OID.SNMP_ACCESS_LIMITS);
			snmpV2.setTimeout(3 * 1000);
			response = snmpV2.snmpGetBulk(oids, 0, snmpCommNum);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 3 == 0) {
						SNMPMass snmpMass = new SNMPMass();
						snmpMass.setMassName(responseVar.get(i + 0)
								.getVariable().toString().trim());
						// SNMPView snmpView = new SNMPView();
						// snmpView.setViewName(responseVar.get(i + 1)
						// .getVariable().toString().trim());
						snmpMass.setMassView(responseVar.get(i + 1)
								.getVariable().toString().trim());
						snmpMass.setMassRight(responseVar.get(i + 2)
								.getVariable().toString().trim());
						snmpComms.add(snmpMass);
					}
				}
			}
			return snmpComms;
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

	public List<SNMPGroup> getSnmpGroups(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		Vector<String> oids = null;
		try {
			response = snmpV2.snmpGet(OID.SNMP_GROUPNUM);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			int snmpGroupNum = 0;
			List<SNMPGroup> snmpGroups = null;
			if (responseVar != null) {
				snmpGroups = new ArrayList<SNMPGroup>();
				snmpGroupNum = responseVar.get(0).getVariable().toInt();
			}
			if (snmpGroupNum == 0) {
				return null;
			}
			oids = new Vector<String>();
			oids.add(OID.SNMP_GROUP_NAME);
			oids.add(OID.SNMP_SECURE_NAME);
			oids.add(OID.SNMP_SECURE_LEVEL);
			snmpV2.setTimeout(3 * 1000);
			response = snmpV2.snmpGetBulk(oids, 0, snmpGroupNum);
			responseVar = checkResponseVar(response);

			if (responseVar != null) {
				int vbsSize = responseVar.size();

				for (int i = 0; i < vbsSize; i++) {
					if (i % 3 == 0) {
						SNMPGroup snmpGroup = new SNMPGroup();
						snmpGroup.setGroupName(responseVar.get(i + 0)
								.getVariable().toString().trim());
						snmpGroup.setSecurityModel(responseVar.get(i + 1)
								.getVariable().toString().trim());

						int secureLevel = 0;
						String securityLevel = responseVar.get(i + 2)
								.getVariable().toString().trim();
						if ("NoAuthNoPriv".equalsIgnoreCase(securityLevel)) {
							secureLevel = 1;
						} else if ("AuthNoPriv".equalsIgnoreCase(securityLevel)) {
							secureLevel = 2;
						} else {
							secureLevel = 3;
						}
						snmpGroup.setSecurityLevel(secureLevel);

						snmpGroups.add(snmpGroup);
					}
				}
			}
			return snmpGroups;
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

	public List<SNMPUser> getSnmpUsers(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		Vector<String> oids = null;
		try {
			response = snmpV2.snmpGet(OID.SNMP_USERNUM);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			int snmpUserNum = 0;
			List<SNMPUser> snmpUsers = null;
			if (responseVar != null) {
				snmpUsers = new ArrayList<SNMPUser>();
				snmpUserNum = responseVar.get(0).getVariable().toInt();
			}
			if (snmpUserNum == 0) {
				return null;
			}
			oids = new Vector<String>();
			oids.add(OID.SNMP_USER_NAME);
			oids.add(OID.SNMP_ACCESS_GROUP);
			oids.add(OID.SNMP_VERSION);
			snmpV2.setTimeout(3 * 1000);
			response = snmpV2.snmpGetBulk(oids, 0, snmpUserNum);
			responseVar = checkResponseVar(response);

			if (responseVar != null) {
				int vbsSize = responseVar.size();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 3 == 0) {
						SNMPUser snmpUser = new SNMPUser();
						snmpUser.setUserName(responseVar.get(i + 0)
								.getVariable().toString().trim());
						// SNMPGroup snmpGroup = new SNMPGroup();
						// snmpGroup.setGroupName(responseVar.get(i + 1)
						// .getVariable().toString().trim());
						snmpUser.setSnmpGroup(responseVar.get(i + 1)
								.getVariable().toString().trim());
						// snmpUser.set
						snmpUser.setVersion(responseVar.get(i + 2)
								.getVariable().toString().trim());

						snmpUsers.add(snmpUser);
					}
				}
			}
			return snmpUsers;
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

	public List<SNMPHost> getSnmpHosts(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		Vector<String> oids = null;
		try {
			response = snmpV2.snmpGet(OID.SNMP_HOSTNUM);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			int snmpHostNum = 0;
			List<SNMPHost> snmpHosts = null;
			if (responseVar != null) {
				snmpHosts = new ArrayList<SNMPHost>();
				snmpHostNum = responseVar.get(0).getVariable().toInt();
			}
			if (snmpHostNum == 0) {
				return snmpHosts;
			}
			oids = new Vector<String>();
			oids.add(OID.SNMP_HOST_ADDR);
			oids.add(OID.SNMP_ACCESS_VERSION);
			oids.add(OID.SNMP_ACCESS_COMM);
			snmpV2.setTimeout(3 * 1000);
			response = snmpV2.snmpGetBulk(oids, 0, snmpHostNum);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 3 == 0) {
						SNMPHost snmpHost = new SNMPHost();
						System.out.println(responseVar.get(i + 0).getVariable()
								.toString().trim());
						snmpHost.setHostIp(responseVar.get(i + 0).getVariable()
								.toString().trim());
						System.out.println(responseVar.get(i + 1).getVariable()
								.toString().trim());
						snmpHost.setSnmpVersion(responseVar.get(i + 1)
								.getVariable().toString().trim());
						// SNMPMass snmpMass = new SNMPMass();
						// snmpMass.setMassName(responseVar.get(i + 2)
						// .getVariable().toString().trim());
						snmpHost.setMassName(responseVar.get(i + 2)
								.getVariable().toString().trim());
						if (!"0.0.0.0".equals(snmpHost.getHostIp())) {
							snmpHosts.add(snmpHost);
						}
					}
				}
			}
			return snmpHosts;
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

	public boolean snmpViewConfig(String ip, Map<String, Object> vbs) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.snmpSet(vbs);

		return true;
	}

	public boolean snmpPublicConfig(String ip, Map<String, Object> vbs) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.snmpSet(vbs);
		return true;
	}

	public boolean snmpGroupConfig(String ip, Map<String, Object> vbs) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.snmpSet(vbs);
		return true;
	}

	public boolean snmpUserConfig(String ip, Map<String, Object> vbs) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.snmpSet(vbs);
		return true;
	}

	public boolean snmpEngineIDConfig(String ip, Map<String, Object> vbs) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.snmpSet(vbs);

		return true;
	}

	public boolean configEngineID(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean createCluster(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean createGroup(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean createHost(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean createUser(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean createView(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteCluster() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteGroup() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteHost() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteUser() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteView() {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getCluster() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getHost() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getView() {
		// TODO Auto-generated method stub
		return null;
	}

	// Spring inject
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
