package com.jhw.adm.client.util;

import org.apache.commons.lang.StringUtils;

public final class InternetUtils {

	/**
	 * 把IP字符串转换成为Long<br>
	 * 例：<code>"100.4.5.6" => 1677985030</code>
	 * @param ip IP地址字符串形式
	 * @return IP地址Long形式
	 */
	public static long toLong(String ip) {
		long result = 0L;
		
		String[] split = StringUtils.split(ip, "\\.");
		
		if (split != null && split.length == 4) {
			long[] longArray = new long[split.length];
			
			for (int i = 0; i < split.length; i++) {
				longArray[i] = Long.parseLong(split[i]);
			}
	
			result = 
				((longArray[0] & 0xFF) << 24) + 
				((longArray[1] & 0xFF) << 16) + 
				((longArray[2] & 0xFF) << 8) + 
				((longArray[3] & 0xFF));
		}
		
		return result;
	}
	
	/**
	 * 比较两个IP的大小<br>
	 * 例：<code>"100.4.5.6" > "100.4.5.1" == true</code>
	 * @param ipa 第一个IP地址
	 * @param ipb 第二个IP地址
	 * @return true 第一个IP地址大于第二个IP地址;<br>
	 *          false 第一个IP地址小于或者等于第二个IP地址
	 */
	public static boolean greaterThan(String ipa, String ipb) {
		boolean result = false;
		
		long ip1 = toLong(ipa);
		long ip2 = toLong(ipb);
		result = ip1 > ip2;
		
		return result;
	}
}