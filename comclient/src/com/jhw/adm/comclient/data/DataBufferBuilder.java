package com.jhw.adm.comclient.data;

/**
 * 
 * @author xiongbo
 * 
 */
public class DataBufferBuilder {

	private final static int ADD = 1;
	private final static int UPDATE = 2;
	private final static int DELETE = 3;
	//
	private final static String COLON = ":";
	private final static String SEPARATOR = "-";
	//
	private final int SERIAL_TYPE_NAME = 1;
	private final int SERIAL_TYPE_BAUDRATE = 2;
	private final int SERIAL_TYPE_DATABITS = 3;
	private final int SERIAL_TYPE_MODE = 4;
	private final int SERIAL_TYPE_PARITY = 5;
	private final int SERIAL_TYPE_STOPBITS = 6;
	private final int SERIAL_TYPE_TCPCLIENT = 7;
	private final int SERIAL_TYPE_TCPSERVER = 8;
	private final int SERIAL_TYPE_UDPCLIENT = 9;
	private final int SERIAL_TYPE_UDPSERVER = 10;

	/**
	 * For batch operate ADD
	 * 
	 * @param vlanID
	 * @param port
	 * @return
	 */

	public Object createVlanId(int vlanID) {
		// byte[] buffer = new byte[10];
		// byte[] temp = tolh(DELETE);
		// System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		// temp = tolh(vlanID);
		// System.arraycopy(temp, 0, buffer, 4, temp.length);// vlanid
		Object obj = vlanID;
		return obj;
	}

	public Object vlanCreate(String[] str) {
		Object obj = null;
		StringBuilder strb = new StringBuilder();
		String result = null;
		for (int i = 0; i < str.length - 1; i++) {
			obj = strb.append(str[i] + ",");
			result = obj.toString();
			System.out.println(result);
		}
		obj = strb.append(str[str.length - 1]);
		result = obj.toString();
		return result;
	}

	/**
	 * @TODO
	 */
	// public String toHexString(String s) {
	// String str = "";
	// for (int i = 0; i < s.length(); i++) {
	// int ch = (int) s.charAt(i);
	// String s4 = Integer.toHexString(ch);
	// str = str + s4;
	// }
	// return str;
	// }
	/**
	 * For batch operate UPDATE
	 * 
	 * @param vlanID
	 * @param port
	 * @return
	 */
	public byte[] vlanUpdate(int vlanID, String portValue) {
		byte[] buffer = new byte[200];
		byte[] temp = tolh(UPDATE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		int portNum = portValue.length();
		temp = tolh(portNum);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// portnum
		temp = tolh(vlanID);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// vlanid

		temp = portValue.getBytes();
		System.arraycopy(temp, 0, buffer, 12, temp.length);

		// for (int i = 0; i < portNum; i++) {
		// temp = portValue[i].getBytes();
		// int position = 12 + 4 * i;
		// System.arraycopy(temp, 0, buffer, position, temp.length);
		// }

		return buffer;
	}

	/**
	 * For batch operate DELETE
	 * 
	 * @param vlanID
	 * @param port
	 * @return
	 */
	public Object vlanDelete(int vlanID) {
		// byte[] buffer = new byte[10];
		// byte[] temp = tolh(DELETE);
		// System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		// temp = tolh(vlanID);
		// System.arraycopy(temp, 0, buffer, 4, temp.length);// vlanid
		Object obj = vlanID;
		return obj;
	}

	/**
	 * For Port configuration batch operate UPDATE
	 * 
	 * @param portsId
	 * @param data
	 * @return
	 */
	public byte[] portConfig(int portsId, String portState, String portSpeed,
			String flowctl, String portsDiscard) {

		byte[] buffer = new byte[100];
		// Here ADD means config port
		byte[] temp = tolh(ADD);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		// byte[] temp = portsId.getBytes();
		temp = tolh(portsId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// portsId
		temp = portState.getBytes();
		System.arraycopy(temp, 0, buffer, 8, temp.length);// portState
		temp = portSpeed.getBytes();
		System.arraycopy(temp, 0, buffer, 24, temp.length);// portSpeed
		temp = flowctl.getBytes();
		System.arraycopy(temp, 0, buffer, 40, temp.length);// flowctl
		temp = portsDiscard.getBytes();
		System.arraycopy(temp, 0, buffer, 56, temp.length);// portsDiscard

		return buffer;
	}

	public byte[] vlanPortConfig(int portid, int pvid, int priority) {
		byte[] buffer = new byte[100];
		// Here UPDATE means vlanPortConfig
		// byte[] temp = tolh(UPDATE);
		// System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		byte[] temp = tolh(portid);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// portid
		temp = tolh(pvid);
		System.arraycopy(temp, 0, buffer, 31, temp.length);// pvid
		temp = tolh(priority);
		System.arraycopy(temp, 0, buffer, 62, temp.length);// priority
		return buffer;
	}

	public Object[] vlanPortConfigObj(int pvid, int priority) {
		Object[] obj = new Object[5];
		// Object[] obj1 = { portid };
		// Object[] obj2 = { pvid };
		// Object[] obj3 = { priority };
		// System.arraycopy(obj1, 0, obj, 0, 1);// portid
		// System.arraycopy(obj2, 0, obj, 26, 1);// pvid
		// System.arraycopy(obj3, 0, obj, 50, 1);// priority
		obj[0] = pvid;
		obj[1] = priority;
		return obj;
	}

	/**
	 * For STP Port configuration batch operate UPDATE
	 */
	public byte[] stpPortConfig(String stpPort, String pathCost,
			String priority, String mode, String edge, String p2p) {
		byte[] buffer = new byte[200];
		// byte[] temp = tolh(UPDATE);
		// System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		byte[] temp = stpPort.getBytes();
		System.arraycopy(temp, 0, buffer, 0, temp.length);// stpPort
		temp = pathCost.getBytes();
		System.arraycopy(temp, 0, buffer, 28, temp.length);// pathCost
		temp = priority.getBytes();
		System.arraycopy(temp, 0, buffer, 56, temp.length);// priority
		temp = mode.getBytes();
		System.arraycopy(temp, 0, buffer, 86, temp.length);// mode
		temp = edge.getBytes();
		System.arraycopy(temp, 0, buffer, 119, temp.length);// edge
		temp = p2p.getBytes();
		System.arraycopy(temp, 0, buffer, 152, temp.length);// p2p

		return buffer;
	}

	public byte[] agingTimeConfig(int ageTime) {
		byte[] buffer = new byte[15];
		// Here 'ADD' is config agetime
		byte[] temp = tolh(ADD);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(ageTime);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// age time
		return buffer;
	}

	public byte[] unicastCreate(String mac, int portID, int vlanID) {
		byte[] buffer = new byte[25];
		byte[] temp = tolh(UPDATE);
		// Here 'UPDATE' is Create unicast
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = macToByte(mac);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// mac
		temp = tolh(portID);
		System.arraycopy(temp, 0, buffer, 10, temp.length);// portID
		temp = tolh(vlanID);
		System.arraycopy(temp, 0, buffer, 14, temp.length);// vlanID

		return buffer;
	}

	public byte[] unicastDelete(String mac, int portID, int vlanID) {
		byte[] buffer = new byte[25];
		byte[] temp = tolh(DELETE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = macToByte(mac);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// mac
		// temp = tolh(portID);
		// System.arraycopy(temp, 0, buffer, 12, temp.length);// portID
		temp = tolh(vlanID);
		System.arraycopy(temp, 0, buffer, 16, temp.length);// vlanID

		return buffer;
	}

	public byte[] multicastCreate(String multiMac, byte[] multiPort, int vlanID) {
		byte[] buffer = new byte[50];
		byte[] temp = tolh(ADD);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = macToByte(multiMac);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// multiMac
		System.arraycopy(multiPort, 0, buffer, 12, multiPort.length);// multiPort
		temp = tolh(vlanID);
		System.arraycopy(temp, 0, buffer, 12 + multiPort.length, temp.length);// vlanID
		return buffer;
	}

	public byte[] multicastDelete(String multiMac, byte[] multiPort, int vlanID) {
		byte[] buffer = new byte[50];
		// Here 'UPDATE' is Delete unicast
		byte[] temp = tolh(UPDATE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = macToByte(multiMac);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// multiMac
		System.arraycopy(multiPort, 0, buffer, 12, multiPort.length);// multiPort
		temp = tolh(vlanID);
		System.arraycopy(temp, 0, buffer, 12 + multiPort.length, temp.length);// vlanID
		return buffer;
	}

	public byte[] mirrorConfig() {
		byte[] buffer = new byte[10];
		return buffer;
	}

	public byte[] dot1xPortConfig(int portId, String portState) {
		byte[] buffer = new byte[20];
		byte[] temp = tolh(portId);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// portId
		temp = stringToByte(portState);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// portState
		return buffer;
	}

	public byte[] igmpRouterConfig(byte[] portChoice, int state) {
		byte[] buffer = new byte[40];
		byte[] temp = tolh(ADD);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		// temp = charToByte(portChoice);
		System.arraycopy(portChoice, 0, buffer, 4, portChoice.length);// portChoice
		temp = tolh(state);
		System.arraycopy(temp, 0, buffer, 4 + portChoice.length, temp.length);// state
		return buffer;
	}

	public byte[] igmpQuerierConfig(int vlanid, int state) {
		byte[] buffer = new byte[20];
		byte[] temp = tolh(UPDATE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(vlanid);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// vlanid
		temp = tolh(state);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// state

		return buffer;
	}

	public byte[] igmpSnoopingConfig(int vlanid, int state) {
		byte[] buffer = new byte[20];
		byte[] temp = tolh(DELETE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(vlanid);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// vlanid
		temp = tolh(state);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// state

		return buffer;
	}

	/**
	 * Also Port speed limit
	 */
	public byte[] trafficControlConfig(int portId, int ingress, String rxRate,
			int egress, String txRate) {
		byte[] buffer = new byte[60];
		byte[] temp = tolh(portId);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// portId
		temp = tolh(egress);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// egress
		temp = tolh(ingress);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// ingress
		temp = stringToByte(rxRate);
		System.arraycopy(temp, 0, buffer, 12, temp.length);// rxRate
		temp = stringToByte(txRate);
		System.arraycopy(temp, 0, buffer, 28, temp.length);// txRate
		return buffer;
	}

	public byte[] stormControlConfig(int portid, String level, String type) {
		byte[] buffer = new byte[50];
		byte[] temp = tolh(portid);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// portId
		temp = stringToByte(level);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// level
		temp = stringToByte(type);
		System.arraycopy(temp, 0, buffer, 20, temp.length);// type

		return buffer;
	}

	public byte[] stormControl_type_80series(int selmcast, int selbcast,
			int seldlf) {
		byte[] buffer = new byte[10];
		byte[] temp = tolh(1);
		System.arraycopy(temp, 0, buffer, 0, temp.length);
		byte[] temp2 = new byte[1];
		temp2[0] = (byte) (selmcast & 0xff);
		System.arraycopy(temp2, 0, buffer, 4, temp2.length);// selmcast
		temp2[0] = (byte) (selbcast & 0xff);
		System.arraycopy(temp2, 0, buffer, 5, temp2.length);// selbcast
		temp2[0] = (byte) (seldlf & 0xff);
		System.arraycopy(temp2, 0, buffer, 6, temp2.length);// seldlf

		return buffer;
	}

	public byte[] stormControl_rate_80series(int ifindex, int rate) {
		byte[] buffer = new byte[20];
		byte[] temp = tolh(2);
		System.arraycopy(temp, 0, buffer, 0, temp.length);
		temp = tolh(ifindex);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// ifindex
		temp = tolh(rate);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// rate

		return buffer;
	}

	public byte[] qosPriorityCfg(int qosType, int value, int queue) {
		byte[] buffer = new byte[20];
		byte[] temp = tolh(qosType);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// qosType
		temp = tolh(value);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// value
		temp = tolh(queue);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// queue

		return buffer;
	}

	public byte[] ghringRing(int ringId, byte[] portMember, int ringType) {
		byte[] buffer = new byte[20];
		byte[] temp = tolh(1);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(ringType);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// ringType
		temp = tolh(ringId);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// ringId
		System.arraycopy(portMember, 0, buffer, 12, portMember.length);// portChoice
		return buffer;
	}

	public byte[] ghringType(int ringId, String sysType) {
		byte[] buffer = new byte[25];
		byte[] temp = tolh(2);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(ringId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// ringId
		temp = stringToByte(sysType);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// sysType
		return buffer;
	}

	public byte[] ghringPortRole(int portId, String role) {
		byte[] buffer = new byte[20];
		byte[] temp = tolh(3);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(portId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// portId
		temp = stringToByte(role);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// role
		return buffer;
	}

	public byte[] ghringDel(int ringId) {
		byte[] buffer = new byte[10];
		byte[] temp = tolh(4);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(ringId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// ringId
		return buffer;
	}

	public byte[] ghringLinkAdd(int linkId, int portId, int role) {
		byte[] buffer = new byte[35];
		byte[] temp = tolh(5);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(linkId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// linkId
		temp = tolh(portId);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// portId
		temp = tolh(role);
		System.arraycopy(temp, 0, buffer, 12, temp.length);// role
		return buffer;
	}

	public byte[] ghringLinkAdd(int linkId, int portId, String role) {
		byte[] buffer = new byte[35];
		byte[] temp = tolh(5);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(linkId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// linkId
		temp = tolh(portId);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// portId
		temp = stringToByte(role);
		System.arraycopy(temp, 0, buffer, 12, temp.length);// role
		return buffer;
	}

	public byte[] ghringLinkDel(int linkId, int portId, String role) {
		byte[] buffer = new byte[35];
		byte[] temp = tolh(6);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(linkId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// linkId
		temp = tolh(portId);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// portId
		temp = stringToByte(role);
		System.arraycopy(temp, 0, buffer, 12, temp.length);// role
		return buffer;
	}

	// public byte[] lacpKeyConfig(byte[] portChoice, int[] keyValue) {
	// byte[] buffer = new byte[60];
	// byte[] temp = tolh(ADD);
	// System.arraycopy(temp, 0, buffer, 0, temp.length);// type
	// System.arraycopy(portChoice, 0, buffer, 4, portChoice.length);//
	// portChoice
	// int len = 4 + portChoice.length;
	// for (int keyv : keyValue) {
	// temp = tolh(keyv);
	// System.arraycopy(temp, 0, buffer, len, temp.length);// keyValue
	// len = len + 4;
	// }
	//
	// return buffer;
	// }

	public byte[] lacpKeyConfig(byte[] portChoice, String[] keyValue) {
		byte[] buffer = new byte[120];
		byte[] temp = tolh(ADD);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		System.arraycopy(portChoice, 0, buffer, 4, portChoice.length);// portChoice
		int len = 4 + portChoice.length;
		for (String keyv : keyValue) {
			temp = stringToByte(keyv);
			System.arraycopy(temp, 0, buffer, len, temp.length);// keyValue
			len = len + 12;
		}
		return buffer;
	}

	public byte[] lacpKeyConfigs(int port, int key) {
		byte[] buffer = new byte[40];
		byte[] temp = tolh(ADD);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(port);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// port
		temp = tolh(key);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// keyValue

		return buffer;
	}

	/**
	 * 
	 * @param portChoice
	 * @param keyValue
	 *            0 disable 1 enable
	 * @return
	 */
	public byte[] lacpModeConfig(byte[] portChoice, int[] keyValue) {
		byte[] buffer = new byte[60];
		byte[] temp = tolh(UPDATE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		System.arraycopy(portChoice, 0, buffer, 4, portChoice.length);// portChoice
		int len = 4 + portChoice.length;
		for (int keyv : keyValue) {
			temp = tolh(keyv);
			System.arraycopy(temp, 0, buffer, len, temp.length);// keyValue
			len = len + 4;
		}
		return buffer;
	}

	/**
	 * 
	 * @param portChoice
	 * @param keyValue
	 *            0 disable 1 enable
	 * @return
	 */
	public byte[] lacpModeConfigs(int port, int keyValue) {
		byte[] buffer = new byte[20];
		byte[] temp = tolh(UPDATE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(port);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// port
		temp = tolh(keyValue);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// keyValue

		return buffer;
	}

	/**
	 * 
	 * @param portChoice
	 * @param keyValue
	 *            passive|active
	 * 
	 * @return
	 */
	public byte[] lacpRoleConfig(byte[] portChoice, String[] keyValue) {
		byte[] buffer = new byte[120];
		byte[] temp = tolh(DELETE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		System.arraycopy(portChoice, 0, buffer, 4, portChoice.length);// portChoice
		int len = 4 + portChoice.length;
		for (String keyv : keyValue) {
			temp = stringToByte(keyv);
			System.arraycopy(temp, 0, buffer, len, temp.length);// keyValue
			len = len + 12;
		}
		return buffer;
	}

	public byte[] lacpRoleConfigs(int port, String keyValue) {
		byte[] buffer = new byte[40];
		byte[] temp = tolh(DELETE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(port);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// portChoice
		temp = stringToByte(keyValue);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// keyValue

		return buffer;
	}

	public byte[] trunkConfig(int groupId, byte[] portChoice) {
		byte[] buffer = new byte[40];
		byte[] temp = tolh(ADD);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(groupId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// groupId
		System.arraycopy(portChoice, 0, buffer, 8, portChoice.length);// portChoice
		return buffer;
	}

	public byte[] trunkConfigDelete(int groupId, byte[] portChoice) {
		byte[] buffer = new byte[40];
		byte[] temp = tolh(UPDATE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(groupId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// groupId
		System.arraycopy(portChoice, 0, buffer, 8, portChoice.length);// portChoice
		return buffer;
	}

	public byte[] lldpConfig(byte[] portMember, byte[] stateMember) {
		byte[] buffer = new byte[100];
		System.arraycopy(portMember, 0, buffer, 0, portMember.length);// portMember
		System.arraycopy(stateMember, 0, buffer, portMember.length,
				stateMember.length);// stateMember

		return buffer;
	}

	public byte[] rmonStaticAdd(int ctrl_index, int portId) {
		byte[] buffer = new byte[20];
		byte[] temp = tolh(1);// Static
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(ctrl_index);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// ctrl_index
		temp = tolh(portId);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// portId

		return buffer;
	}

	public byte[] rmonAlarmAdd(int ctrl_index, int portId, int interval,
			String variable, String sample_type, int rising_threshold,
			int falling_threshold, int rising_event_index,
			int falling_event_index) {
		byte[] buffer = new byte[120];
		byte[] temp = tolh(2);// Alarm
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(ctrl_index);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// ctrl_index
		temp = tolh(portId);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// portId
		temp = tolh(interval);
		System.arraycopy(temp, 0, buffer, 12, temp.length);// interval
		temp = stringToByte(variable);
		System.arraycopy(temp, 0, buffer, 16, temp.length);// variable
		temp = stringToByte(sample_type);
		System.arraycopy(temp, 0, buffer, 80, temp.length);// sample_type
		temp = tolh(rising_threshold);
		System.arraycopy(temp, 0, buffer, 96, temp.length);// rising_threshold
		temp = tolh(falling_threshold);
		System.arraycopy(temp, 0, buffer, 100, temp.length);// falling_threshold
		temp = tolh(rising_event_index);
		System.arraycopy(temp, 0, buffer, 104, temp.length);// rising_event_index
		temp = tolh(falling_event_index);
		System.arraycopy(temp, 0, buffer, 108, temp.length);// falling_event_index

		return buffer;
	}

	public byte[] rmonEventAdd(int ctrl_index, String description,
			String eventType, String community) {
		byte[] buffer = new byte[80];
		byte[] temp = tolh(3);// Event
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(ctrl_index);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// ctrl_index
		temp = stringToByte(description);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// description
		temp = stringToByte(eventType);
		System.arraycopy(temp, 0, buffer, 40, temp.length);// eventType
		temp = stringToByte(community);
		System.arraycopy(temp, 0, buffer, 72, temp.length);// community

		return buffer;
	}

	public byte[] rmonStaticDelete(int ctrl_index) {
		byte[] buffer = new byte[15];
		byte[] temp = tolh(4);// delete
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(1);// static
		System.arraycopy(temp, 0, buffer, 4, temp.length);// static
		temp = tolh(ctrl_index);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// ctrl_index

		return buffer;
	}

	public byte[] rmonAlarmDelete(int ctrl_index) {
		byte[] buffer = new byte[15];
		byte[] temp = tolh(4);// delete
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(2);// Alarm
		System.arraycopy(temp, 0, buffer, 4, temp.length);// Alarm
		temp = tolh(ctrl_index);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// ctrl_index

		return buffer;
	}

	public byte[] rmonEventDelete(int ctrl_index) {
		byte[] buffer = new byte[15];
		byte[] temp = tolh(4);// delete
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(3);// Event
		System.arraycopy(temp, 0, buffer, 4, temp.length);// Event
		temp = tolh(ctrl_index);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// ctrl_index

		return buffer;
	}

	/**
	 * Only include data of string type Exclude tcpclient=7 and udpclient=9
	 */
	public byte[] serialConfig_string(int serialId, int type,
			String propertyValue) {
		byte[] buffer = new byte[50];
		byte[] temp = tolh(type);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(serialId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// serialId
		temp = stringToByte(propertyValue);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// propertyValue

		return buffer;
	}

	/**
	 * Only include data of int type
	 * 
	 * Exclude tcpclient=7 and udpclient=9
	 */
	public byte[] serialConfig_int(int serialId, int type, int propertyValue) {
		byte[] buffer = new byte[50];
		byte[] temp = tolh(type);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(serialId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// serialId
		temp = tolh(propertyValue);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// propertyValue

		return buffer;
	}

	/**
	 * Only include tcpclient=7 and udpclient=9
	 */
	public byte[] serialConfig_client(int serialId, int type, String ipAddr,
			int port) {
		byte[] buffer = new byte[50];
		byte[] temp = tolh(type);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// type
		temp = tolh(serialId);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// serialId
		// TODO
		// temp = ipAddr.getBytes();
		temp = stringToByte(ipAddr);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// ipAddr
		temp = tolh(port);
		System.arraycopy(temp, 0, buffer, 24, temp.length);// port

		return buffer;
	}

	public byte[] snmpHostConfig(String ip, String version, String community) {
		byte[] buffer = new byte[70];
		byte[] temp = tolh(1); // 1:表示增加
		System.arraycopy(temp, 0, buffer, 0, temp.length);//
		temp = stringToByte(ip);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// ip
		temp = stringToByte(version);
		System.arraycopy(temp, 0, buffer, 20, temp.length);// version
		temp = stringToByte(community);
		System.arraycopy(temp, 0, buffer, 28, temp.length);// community

		return buffer;
	}

	public byte[] snmpHostDel(String ip, String version, String community) {
		byte[] buffer = new byte[70];
		byte[] temp = tolh(2); // 2:表示删除
		System.arraycopy(temp, 0, buffer, 0, temp.length);//
		temp = stringToByte(ip);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// ip
		temp = stringToByte(version);
		System.arraycopy(temp, 0, buffer, 20, temp.length);// version
		temp = stringToByte(community);
		System.arraycopy(temp, 0, buffer, 28, temp.length);// community

		return buffer;
	}

	public byte[] upgradeOperate(int operateType, String ip, String fileName) {
		byte[] buffer = new byte[64];
		byte[] temp = tolh(operateType); //
		System.arraycopy(temp, 0, buffer, 0, temp.length);//
		temp = stringToByte(ip);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// ip
		temp = stringToByte(fileName);
		System.arraycopy(temp, 0, buffer, 20, temp.length);// version

		return buffer;
	}

	public byte[] switcherUserAdd(int level, String name, String pwd) {
		byte[] buffer = new byte[90];
		byte[] temp = tolh(ADD);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// operateType
		temp = tolh(level);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// level
		temp = stringToByte(name);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// name
		temp = stringToByte(pwd);
		System.arraycopy(temp, 0, buffer, 40, temp.length);// pwd

		return buffer;
	}

	public byte[] switcherUserDel(int level, String name, String pwd) {
		byte[] buffer = new byte[90];
		byte[] temp = tolh(DELETE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// operateType
		temp = tolh(level);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// level
		temp = stringToByte(name);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// name
		temp = stringToByte(pwd);
		System.arraycopy(temp, 0, buffer, 40, temp.length);// pwd

		return buffer;
	}

	public byte[] switcherUserModify(String name, String oldPwd, String newPwd) {
		byte[] buffer = new byte[120];
		byte[] temp = tolh(UPDATE);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// operateType
		temp = stringToByte(name);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// name
		temp = stringToByte(oldPwd);
		System.arraycopy(temp, 0, buffer, 36, temp.length);// oldPwd
		temp = stringToByte(newPwd);
		System.arraycopy(temp, 0, buffer, 68, temp.length);// newPwd

		return buffer;
	}

	public byte[] syslogServerConfig(int operate, int index, String serverAddr,
			int port) {
		byte[] buffer = new byte[35];
		byte[] temp = tolh(operate);// 1 add 2 del
		System.arraycopy(temp, 0, buffer, 0, temp.length);// operate
		temp = tolh(index);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// index
		temp = stringToByte(serverAddr);
		System.arraycopy(temp, 0, buffer, 8, temp.length);// serverAddr
		temp = tolh(port);
		System.arraycopy(temp, 0, buffer, 24, temp.length);// port
		temp = tolh(1);// 1 启用 0 不启用
		System.arraycopy(temp, 0, buffer, 28, temp.length);// state
		return buffer;
	}

	public byte[] saveConfig(int operate) {
		byte[] buffer = new byte[5];
		byte[] temp = tolh(operate);// 1 save 2 update system name 3 download
		// cfg 4 upload cfg

		System.arraycopy(temp, 0, buffer, 0, temp.length);// operate

		return buffer;
	}

	/**
	 * change to a lower byte int first, high byte in the end
	 */
	private byte[] tolh(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * change to a high byte int first, lower byte in the end
	 */
	// private byte[] tolh(int n) {
	// byte[] b = new byte[4];
	// b[0] = (byte) (n >> 24 & 0xff000000);
	// b[1] = (byte) (n >> 16 & 0xff0000);
	// b[2] = (byte) (n >> 8 & 0xff00);
	// b[3] = (byte) (n & 0xff);
	//
	// return b;
	// }
	// public byte[] tolh(int n) {
	// byte[] b = new byte[4];
	// b[3] = (byte) (n & 0xff);
	// b[2] = (byte) (n >> 8 & 0xff);
	// b[1] = (byte) (n >> 16 & 0xff);
	// b[0] = (byte) (n >> 24 & 0xff);
	// return b;
	// }

	private byte[] macToByte(String mac) {
		byte[] b = new byte[6];
		String[] macSegment = null;
		if (mac.indexOf(SEPARATOR) != -1) {
			macSegment = mac.split(SEPARATOR);
		} else if (mac.indexOf(COLON) != -1) {
			macSegment = mac.split(COLON);
		}
		for (int i = 0; i < 6; i++) {
			b[i] = (byte) Integer.parseInt(macSegment[i], 16);
		}
		return b;
	}

	private byte[] stringToByte(String str) {
		int strLeng = str.length();
		byte[] b = new byte[strLeng];
		for (int i = 0; i < strLeng; i++) {
			int ch = (int) str.charAt(i);
			b[i] = (byte) ch;
		}
		return b;
	}
}
