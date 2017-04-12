package com.jhw.adm.comclient.service.topology;

/**
 * 
 * @author xiongbo
 * 
 */

public class DiscoveryUtil {
	/**
	 * 计算一个IP地址的下一个IP。
	 * 
	 * @param ip
	 *            IP地址
	 * @return 计算的下一个IP
	 */
	public static String getNextIP(String ip) {
		String nextip;
		int[] iparray = new int[4];

		ipToint(ip, iparray);
		if (++iparray[3] == 256) {
			iparray[3] = 0;
			if (++iparray[2] == 256) {
				iparray[2] = 0;
				if (++iparray[1] == 256) {
					iparray[1] = 0;
					if (++iparray[0] == 256) {
						iparray[0] = 0;
					}
				}
			}
		}

		return intToip(iparray);
	}

	/**
	 * 由整数转换为IP地址
	 * 
	 * @param iparray
	 *            长度为4的整数数组
	 * @return IP地址
	 */
	public static String intToip(int[] iparray) {
		String ip = "";

		for (int i = 0; i < 4; i++) {
			ip += iparray[i] + "";
			if (i != 3) {
				ip += ".";
			}
		}

		return ip;
	}

	/**
	 * 由IP地址转换为整数数组
	 * 
	 * @param ip
	 *            IP地址
	 * @param iparray
	 *            长度为4的整数数组
	 */
	public static void ipToint(String ip, int[] iparray) {
		int pos;
		int nextpos;
		pos = ip.indexOf(".");
		iparray[0] = Integer.parseInt(ip.substring(0, pos));
		nextpos = ip.indexOf(".", pos + 1);
		iparray[1] = Integer.parseInt(ip.substring(pos + 1, nextpos));
		pos = nextpos;
		nextpos = ip.indexOf(".", pos + 1);
		iparray[2] = Integer.parseInt(ip.substring(pos + 1, nextpos));
		iparray[3] = Integer.parseInt(ip.substring(nextpos + 1, ip.length()));
	}

	/**
	 * 由IP地址和子网掩码计算子网地址。
	 * 
	 * @param ip
	 *            IP地址
	 * @param mask
	 *            子网掩码
	 * @return 子网地址
	 */
	public static String getSubnetIP(String ip, String mask) {
		int[] arrayip = new int[4];
		int[] arraymask = new int[4];
		int[] arraytemp = new int[4];

		ipToint(ip, arrayip);
		ipToint(mask, arraymask);

		arraytemp[0] = arrayip[0] & arraymask[0];
		arraytemp[1] = arrayip[1] & arraymask[1];
		arraytemp[2] = arrayip[2] & arraymask[2];
		arraytemp[3] = arrayip[3] & arraymask[3];

		return intToip(arraytemp);
	}

	/**
	 * 比较IP地址大小
	 * 
	 * @param ip1
	 * @param ip2
	 * @return
	 */
	public static int compareIP(String ip1, String ip2) {
		int[] arrayip1 = new int[4];
		int[] arrayip2 = new int[4];

		ipToint(ip1, arrayip1);
		ipToint(ip2, arrayip2);
		int k = 0;
		for (int i = 0; i < 4; i++) {
			if (arrayip1[i] > arrayip2[i])
				return 1;
			if (arrayip1[i] == arrayip2[i])
				k++;
		}
		if (k == 4)
			return 0;

		return -1;
	}

	/**
	 * 判断两个IP地址是否处在同一子网
	 * 
	 * @param ip1
	 * @param ip2
	 * @param mask
	 * @return
	 */
	public static boolean isSameSubnet(String ip1, String ip2, String mask) {
		if (getSubnetIP(ip1, mask).equals(getSubnetIP(ip2, mask)))
			return true;
		return false;
	}

	/**
	 * 计算IP地址的整数值
	 * 
	 * @param ipAddress
	 * @return
	 */
	public static int getIPaddressValue(String ipAddress) {
		int IPVal = 0;
		int[] arrayip1 = new int[4];
		ipToint(ipAddress, arrayip1);
		for (int i = 0; i < 4; i++) {
			IPVal = arrayip1[i] * (int) Math.pow(256, 3 - i) + IPVal;
		}
		return IPVal;
	}

	/**
	 * 计算一个子网内的最大IP地址
	 * 
	 * @param subAddr
	 *            String IP地址
	 * @param subMask
	 *            String 子网掩码
	 * @return String
	 */
	public static String getMaxIP(String subAddr, String subMask) {
		String newIp = subAddr.replace('.', ';');
		String newMask = subMask.replace('.', ';');
		String[] ipString = newIp.split(";");
		String[] maskString = newMask.split(";");
		String result = "";

		int[] ipVal = new int[4];
		int[] maskVal = new int[4];
		int[] maxIp = new int[4];

		for (int i = 0; i < 4; i++) {
			ipVal[i] = Integer.parseInt(ipString[i]);
			maskVal[i] = Integer.parseInt(maskString[i]);
			maxIp[i] = (ipVal[i] & maskVal[i]) | (0x000000FF ^ maskVal[i]);
		}
		maxIp[3]--;
		result = maxIp[0] + "." + maxIp[1] + "." + maxIp[2] + "." + maxIp[3];

		return result;
	}

	/**
	 * 计算一个子网内的最小IP地址
	 * 
	 * @param subAddr
	 *            String IP地址
	 * @param subMask
	 *            String 子网掩码
	 * @return String
	 */
	public static String getMinIP(String subAddr, String subMask) {
		String newIp = subAddr.replace('.', ';');
		String newMask = subMask.replace('.', ';');
		String[] ipString = newIp.split(";");
		String[] maskString = newMask.split(";");
		String result = "";

		int[] ipVal = new int[4];
		int[] maskVal = new int[4];
		int[] maxIp = new int[4];

		for (int i = 0; i < 4; i++) {
			ipVal[i] = Integer.parseInt(ipString[i]);
			maskVal[i] = Integer.parseInt(maskString[i]);
			maxIp[i] = ipVal[i] & maskVal[i];
		}
		maxIp[3]++;
		result = maxIp[0] + "." + maxIp[1] + "." + maxIp[2] + "." + maxIp[3];

		return result;
	}

	/**
	 * 计算一个子网内的最大IP地址
	 * 
	 * @param subaddr
	 *            子网地址
	 * @param submask
	 *            子网掩码
	 * @return IP地址
	 */
	public static String getMaxIP1(String subaddr, String submask) {
		String newsubip = subaddr.replace('.', ';');
		String newsubmask = submask.replace('.', ';');

		String[] ipstring = newsubip.split(";");
		String[] maskstring = newsubmask.split(";");
		int ipstring1 = Integer.parseInt(ipstring[0]);
		int ipstring2 = Integer.parseInt(ipstring[1]);
		int ipstring3 = Integer.parseInt(ipstring[2]);
		int ipstring4 = Integer.parseInt(ipstring[3]);
		int maskstring1 = Integer.parseInt(maskstring[0]);
		int maskstring2 = Integer.parseInt(maskstring[1]);
		int maskstring3 = Integer.parseInt(maskstring[2]);
		int maskstring4 = Integer.parseInt(maskstring[3]);

		String result = "";
		if (maskstring2 != 255) {
			int inttemp[] = new int[8];// ip地址的二进制表示
			int masktemp[] = new int[8];// 掩码的二进制表示
			inttemp = getBitValue(ipstring2);// ip地址的二进制表示
			masktemp = getBitValue(maskstring2);// 掩码的二进制表示
			for (int i = 0; i <= 7; i++) {
				if (masktemp[i] == 0) {
					inttemp[i] = 1;
				}
			}
			int inendipstring = getIntValue(inttemp);
			String strendipstring = ipstring[0] + "." + inendipstring + "."
					+ "255.254";
			result = strendipstring;
		} else // maskstring2==255
		{
			if (maskstring3 != 255) {
				int inttemp[] = new int[8];
				int masktemp[] = new int[8];

				inttemp = getBitValue(ipstring3);
				masktemp = getBitValue(maskstring3);
				for (int i = 0; i <= 7; i++) {
					if (masktemp[i] == 0) {
						inttemp[i] = 1;
					}
				}
				int inendipstring = getIntValue(inttemp);
				String strendipstring = ipstring[0] + "." + ipstring[1] + "."
						+ inendipstring + ".254";
				result = strendipstring;
			} else // maskstring3==255
			{
				if (maskstring4 != 255) {
					int inttemp[] = new int[8];
					int masktemp[] = new int[8];

					inttemp = getBitValue(ipstring4);
					masktemp = getBitValue(maskstring4);
					for (int i = 0; i <= 7; i++) {
						if (masktemp[i] == 0) {
							inttemp[i] = 1;
						}
					}
					int inendipstring = getIntValue(inttemp) - 1;
					String strendipstring = ipstring[0] + "." + ipstring[1]
							+ "." + ipstring[2] + "." + inendipstring;
					result = strendipstring;
				}
			}
		}
		return result;
	}

	/**
	 * 由IP地址整数数值转换为四位数组
	 * 
	 * @param maskstring
	 * @return
	 */
	public static int[] getBitValue(int maskstring) {
		int newarray[] = new int[8];
		int temp = maskstring;
		for (int i = 0; i <= 7; i++) {
			newarray[i] = temp % 2;
			temp = temp / 2;
		}
		return newarray;
	}

	/**
	 * 由四位数组转换为IP整数值。
	 * 
	 * @param inttemp
	 * @return
	 */
	public static int getIntValue(int[] inttemp) {
		int j = 0;
		for (int i = 0; i <= 7; i++) {
			j = j + (int) Math.pow(2.0, i * 1.0) * inttemp[i];
		}
		return j;
	}

	public static void main(String args[]) {
		// System.out.println("MaxIp = " +
		// DiscoveryUtil.getMaxIP("192.168.88.100", "255.255.255.192"));
		// System.out.println("MinIp = " +
		// DiscoveryUtil.getMinIP("192.168.88.100", "255.255.255.192"));
	}

}
